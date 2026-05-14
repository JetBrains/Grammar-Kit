/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.buildDottedText;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.firstChildOfType;

/**
 * File-level name-resolution scope: the source file's package, single-type imports, and
 * wildcard-imported packages.
 * <p>
 * Resolution is best-effort: explicit imports and a {@code java.lang} allow-list resolve textually
 * without I/O; wildcard imports are validated by probing the supplied {@link SymbolResolver}
 * (cross-language: a {@code .java} file's wildcard import can resolve to a Kotlin class); same-package
 * is used as a last-resort textual fallback. Qualified references are left untouched.
 */
@SuppressWarnings("UnstableApiUsage")
final class JavaSyntaxImportContext {

  private static final Set<String> JAVA_LANG_TYPES = Set.of(
    "Object", "String", "CharSequence", "Class", "Throwable", "Exception", "RuntimeException",
    "Error", "Number", "Integer", "Long", "Short", "Byte", "Boolean", "Character", "Float", "Double",
    "Void", "Iterable", "Comparable", "Cloneable", "Math", "System", "Thread", "Runnable",
    "StringBuilder", "StringBuffer", "Enum", "Record",
    // common java.lang annotations
    "Deprecated", "Override", "SuppressWarnings", "SafeVarargs", "FunctionalInterface"
  );

  private final String packageName;
  private final Map<String, String> imports;
  private final List<String> wildcardImports;
  private final SymbolResolver resolver;

  static @NotNull JavaSyntaxImportContext extractFrom(@NotNull SyntaxNode fileRoot, @NotNull SymbolResolver resolver) {
    Map<String, String> singleImports = new HashMap<>();
    List<String> wildcards = new ArrayList<>();
    extractImports(fileRoot, singleImports, wildcards);
    return new JavaSyntaxImportContext(extractPackageName(fileRoot), singleImports, wildcards, resolver);
  }

  private JavaSyntaxImportContext(@NotNull String packageName,
                                  @NotNull Map<String, String> imports,
                                  @NotNull List<String> wildcardImports,
                                  @NotNull SymbolResolver resolver) {
    this.packageName = packageName;
    this.imports = imports;
    this.wildcardImports = wildcardImports;
    this.resolver = resolver;
  }

  @NotNull String packageName() {
    return packageName;
  }

  @NotNull String resolveSimpleName(@NotNull String simple) {
    String byImport = imports.get(simple);
    if (byImport != null) {
      return byImport;
    }
    if (JAVA_LANG_TYPES.contains(simple)) {
      return "java.lang." + simple;
    }
    // Probe wildcard imports through the resolver — this is the cross-language hop: a Java file
    // with `import com.foo.*;` will find a `Foo.kt` declared in `com.foo` via the Kotlin provider.
    for (String pkg : wildcardImports) {
      Fqn candidate = Fqn.of(pkg).child(simple);
      if (resolver.findClass(candidate) != null) return candidate.value();
    }
    if (!packageName.isEmpty()) {
      return Fqn.of(packageName).child(simple).value();
    }
    // todo resolve of ij-platform PSI classes is not supported yet
    return simple;
  }

  private static @NotNull String extractPackageName(@NotNull SyntaxNode fileRoot) {
    SyntaxNode pkg = firstChildOfType(fileRoot, JavaSyntaxElementType.PACKAGE_STATEMENT);
    if (pkg == null) return "";
    SyntaxNode ref = firstChildOfType(pkg, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
    return ref == null ? "" : buildDottedText(ref);
  }

  private static void extractImports(@NotNull SyntaxNode fileRoot,
                                     @NotNull Map<String, String> single,
                                     @NotNull List<String> wildcards) {
    SyntaxNode importList = firstChildOfType(fileRoot, JavaSyntaxElementType.IMPORT_LIST);
    if (importList == null) return;

    for (SyntaxNode imp = importList.firstChild(); imp != null; imp = imp.nextSibling()) {
      SyntaxElementType t = imp.getType();
      if (t != JavaSyntaxElementType.IMPORT_STATEMENT && t != JavaSyntaxElementType.IMPORT_STATIC_STATEMENT) continue;
      SyntaxNode ref = firstChildOfType(imp, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
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
      single.put(simple, dotted);
    }
  }
}
