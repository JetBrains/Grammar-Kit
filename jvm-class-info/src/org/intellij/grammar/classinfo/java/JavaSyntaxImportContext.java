/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.buildDottedText;
import static org.intellij.grammar.classinfo.java.JavaSyntaxNodes.firstChildOfType;

/**
 * File-level name-resolution scope: the source file's package + its single-type imports.
 * <p>
 * Resolution is best-effort: wildcard imports are ignored, qualified references are left untouched,
 * and unqualified types fall back to a fixed {@code java.lang} allow-list and then the same-package
 * guess.
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

  static @NotNull JavaSyntaxImportContext extractFrom(@NotNull SyntaxNode fileRoot) {
    return new JavaSyntaxImportContext(extractPackageName(fileRoot), extractImports(fileRoot));
  }

  private JavaSyntaxImportContext(@NotNull String packageName, @NotNull Map<String, String> imports) {
    this.packageName = packageName;
    this.imports = imports;
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
    if (!packageName.isEmpty()) {
      return packageName + "." + simple;
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

  private static @NotNull Map<String, String> extractImports(@NotNull SyntaxNode fileRoot) {
    SyntaxNode importList = firstChildOfType(fileRoot, JavaSyntaxElementType.IMPORT_LIST);
    if (importList == null) return Map.of();

    var imports = new HashMap<String, String>();
    for (SyntaxNode imp = importList.firstChild(); imp != null; imp = imp.nextSibling()) {
      SyntaxElementType t = imp.getType();
      if (t != JavaSyntaxElementType.IMPORT_STATEMENT && t != JavaSyntaxElementType.IMPORT_STATIC_STATEMENT) continue;
      SyntaxNode ref = firstChildOfType(imp, JavaSyntaxElementType.JAVA_CODE_REFERENCE);
      if (ref == null) continue;
      // Wildcard imports require a classpath model to resolve reliably — ignore them.
      if (firstChildOfType(imp, JavaSyntaxTokenType.ASTERISK) != null) continue;
      String dotted = buildDottedText(ref);
      int lastDot = dotted.lastIndexOf('.');
      String simple = lastDot < 0 ? dotted : dotted.substring(lastDot + 1);
      imports.put(simple, dotted);
    }
    return imports;
  }
}
