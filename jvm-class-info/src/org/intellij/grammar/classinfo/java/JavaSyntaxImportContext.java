/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.AbstractImportContext;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
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

  /**
   * Commonly-unqualified {@code java.lang.*} types — Java auto-imports the entire {@code java.lang}
   * package by JLS, so any name that resolves to {@code java.lang.X} can be written bare. This list
   * captures the names worth catching before falling through to the resolver. Intentionally broader
   * than {@code KotlinSyntaxImportContext.JAVA_LANG_FALLBACK}, which is policy-driven (Kotlin
   * auto-imports {@code kotlin.*} instead and only falls back to java.lang for a curated subset).
   */
  private static final Set<String> JAVA_LANG_TYPES = Set.of(
    "Object", "String", "CharSequence", "Class", "Throwable", "Exception", "RuntimeException",
    "Error", "Number", "Integer", "Long", "Short", "Byte", "Boolean", "Character", "Float", "Double",
    "Void", "Iterable", "Comparable", "Cloneable", "Math", "System", "Thread", "Runnable",
    "StringBuilder", "StringBuffer", "Enum", "Record",
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
      // For a (static or regular) import of a nested type, the enclosing class named in the import
      // may be a subclass that *inherits* the nested type rather than declaring it. JLS 7.5.1
      // requires single-type-imports — both forms — to name the canonical declaration, so walk the
      // enclosing class's supertype chain. Example: `import static com.goide.psi.impl.GoLightType
      // .IconFlags` — IconFlags is declared on Iconable, a supertype of GoLightType (via
      // LightElement), so the canonical FQN is `com.intellij.openapi.util.Iconable.IconFlags`.
      // Falls back to the as-written form when the resolver can't follow the chain.
      if (lastDot >= 0) {
        String canonical = NestedTypeResolver.findDeclaringClass(
          Fqn.of(dotted.substring(0, lastDot)), simple, resolver);
        if (canonical != null) dotted = canonical;
      }
      single.put(simple, dotted);
    }
  }
}
