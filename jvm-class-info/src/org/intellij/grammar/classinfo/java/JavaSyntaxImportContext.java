/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.element.JavaSyntaxElementType;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.AbstractImportContext;
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
    extractImports(fileRoot, singleImports, wildcards);
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
