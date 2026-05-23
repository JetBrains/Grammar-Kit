/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.AbstractImportContext;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.SyntaxTreeUtil.firstChildOfType;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.buildDottedText;

/**
 * Java file-level resolution scope. Built-in resolution maps the small set of always-importable
 * {@code java.lang} types; everything else falls back to the shared pipeline.
 */
@SuppressWarnings("UnstableApiUsage")
final class JavaSyntaxImportContext extends AbstractImportContext {

  private static final Set<String> JAVA_LANG_TYPES = Set.of(
    "Object", "String", "CharSequence", "Class", "Throwable", "Exception", "RuntimeException",
    "Error", "Number", "Integer", "Long", "Short", "Byte", "Boolean", "Character", "Float", "Double",
    "Void", "Iterable", "Comparable", "Cloneable", "Math", "System", "Thread", "Runnable",
    "StringBuilder", "StringBuffer", "Enum", "Record",
    // common java.lang annotations
    "Deprecated", "Override", "SuppressWarnings", "SafeVarargs", "FunctionalInterface"
  );

  static @NotNull JavaSyntaxImportContext extractFrom(@NotNull SyntaxNode fileRoot, @NotNull SymbolResolver resolver) {
    Map<String, String> singleImports = new HashMap<>();
    List<String> wildcards = new ArrayList<>();
    extractImports(fileRoot, singleImports, wildcards, resolver);
    return new JavaSyntaxImportContext(extractPackageName(fileRoot), singleImports, wildcards, resolver);
  }

  private JavaSyntaxImportContext(@NotNull String packageName,
                                  @NotNull Map<String, String> imports,
                                  @NotNull List<String> wildcardImports,
                                  @NotNull SymbolResolver resolver) {
    super(packageName, imports, wildcardImports, resolver);
  }

  @Override
  protected @Nullable String resolveBuiltin(@NotNull String simple) {
    return JAVA_LANG_TYPES.contains(simple) ? "java.lang." + simple : null;
  }

  private static @NotNull String extractPackageName(@NotNull SyntaxNode fileRoot) {
    SyntaxNode pkg = firstChildOfType(fileRoot, JavaSyntaxElementType.PACKAGE_STATEMENT);
    if (pkg == null) return "";
    SyntaxNode ref = firstChildOfType(pkg, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
    return ref == null ? "" : buildDottedText(ref);
  }

  private static void extractImports(@NotNull SyntaxNode fileRoot,
                                     @NotNull Map<String, String> single,
                                     @NotNull List<String> wildcards,
                                     @NotNull SymbolResolver resolver) {
    SyntaxNode importList = firstChildOfType(fileRoot, JavaSyntaxElementType.IMPORT_LIST);
    if (importList == null) return;

    for (SyntaxNode imp = importList.firstChild(); imp != null; imp = imp.nextSibling()) {
      SyntaxElementType t = imp.getType();
      if (t != JavaSyntaxElementType.IMPORT_STATEMENT && t != JavaSyntaxElementType.IMPORT_STATIC_STATEMENT) continue;
      // Non-wildcard static imports wrap their outer reference as IMPORT_STATIC_REFERENCE (see
      // ReferenceParser.parseJavaCodeReference); wildcard and regular imports use JAVA_CODE_REFERENCE.
      SyntaxNode ref = firstChildOfType(imp, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
      if (ref == null) ref = firstChildOfType(imp, JavaSyntaxElementType.IMPORT_STATIC_REFERENCE);
      if (ref == null) continue;
      String dotted = buildDottedText(ref);
      if (firstChildOfType(imp, JavaSyntaxTokenType.ASTERISK) != null) {
        // Wildcard: dotted is the package (or enclosing class for static-on-demand); probe lazily
        // through the resolver when resolving a simple name.
        wildcards.add(dotted);
        continue;
      }
      int lastDot = dotted.lastIndexOf('.');
      String simple = lastDot < 0 ? dotted : dotted.substring(lastDot + 1);
      // For a static import of a nested type, the enclosing class named in the import may be a
      // subclass that *inherits* the nested type rather than declaring it. JLS 7.5.1 says a single-
      // type-import must use the canonical name (the class that actually declares it), so walk the
      // enclosing class's supertype chain to find the canonical declaration. Example: `import
      // static com.goide.psi.impl.GoLightType.IconFlags` — IconFlags is declared on Iconable, a
      // supertype of GoLightType (via LightElement), so the canonical FQN is
      // `com.intellij.openapi.util.Iconable.IconFlags`. Falls back to the as-written form when the
      // resolver can't follow the chain.
      if (t == JavaSyntaxElementType.IMPORT_STATIC_STATEMENT && lastDot >= 0) {
        String canonical = findDeclaringClass(Fqn.of(dotted.substring(0, lastDot)), simple, resolver);
        if (canonical != null) dotted = canonical;
      }
      single.put(simple, dotted);
    }
  }

  /**
   * Walks {@code enclosing}'s supertype chain (superclass + interfaces, transitively) for a nested
   * type named {@code simple}. Returns the first hit's canonical FQN, or {@code null} when nothing
   * is reachable through the resolver. Cycle-safe via {@code visited}.
   */
  private static @Nullable String findDeclaringClass(@NotNull Fqn enclosing,
                                                     @NotNull String simple,
                                                     @NotNull SymbolResolver resolver) {
    return walkForNestedType(enclosing, simple, resolver, new HashSet<>());
  }

  private static @Nullable String walkForNestedType(@NotNull Fqn classFqn,
                                                    @NotNull String simple,
                                                    @NotNull SymbolResolver resolver,
                                                    @NotNull Set<Fqn> visited) {
    if (!visited.add(classFqn)) return null;
    Fqn candidate = classFqn.child(simple);
    if (resolver.findClass(candidate) != null) return candidate.value();
    ClassSymbol enclosingSymbol = resolver.findClass(classFqn);
    if (enclosingSymbol == null) return null;
    if (enclosingSymbol.superClass() != null) {
      String r = walkForNestedType(enclosingSymbol.superClass(), simple, resolver, visited);
      if (r != null) return r;
    }
    for (Fqn iface : enclosingSymbol.interfaces()) {
      String r = walkForNestedType(iface, simple, resolver, visited);
      if (r != null) return r;
    }
    return null;
  }
}
