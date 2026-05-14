/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.java.syntax.kotlin.KotlinSyntaxNodes.buildDottedText;
import static org.intellij.grammar.java.syntax.kotlin.KotlinSyntaxNodes.firstChildOfType;

/**
 * File-level name-resolution scope: package + single-type imports (with optional alias).
 * <p>
 * Resolution rules (best-effort, no classpath model):
 * <ul>
 *   <li>Aliased imports ({@code import foo.Bar as Baz}) bind the alias to {@code foo.Bar}.</li>
 *   <li>Wildcard imports are skipped.</li>
 *   <li>Unqualified types resolve through the import map → a fixed allow-list of common Kotlin
 *       built-in / {@code kotlin.collections} names → a {@code java.lang} fallback → same-package.</li>
 *   <li>Qualified references are left as-is.</li>
 * </ul>
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxImportContext {

  private static final Map<String, String> KOTLIN_AUTO_IMPORTS = Map.ofEntries(
    Map.entry("Any", "kotlin.Any"),
    Map.entry("Nothing", "kotlin.Nothing"),
    Map.entry("Unit", "kotlin.Unit"),
    Map.entry("String", "kotlin.String"),
    Map.entry("Int", "kotlin.Int"),
    Map.entry("Long", "kotlin.Long"),
    Map.entry("Short", "kotlin.Short"),
    Map.entry("Byte", "kotlin.Byte"),
    Map.entry("Char", "kotlin.Char"),
    Map.entry("Boolean", "kotlin.Boolean"),
    Map.entry("Float", "kotlin.Float"),
    Map.entry("Double", "kotlin.Double"),
    Map.entry("Number", "kotlin.Number"),
    Map.entry("Throwable", "kotlin.Throwable"),
    Map.entry("Exception", "kotlin.Exception"),
    Map.entry("RuntimeException", "kotlin.RuntimeException"),
    Map.entry("Error", "kotlin.Error"),
    Map.entry("Array", "kotlin.Array"),
    Map.entry("IntArray", "kotlin.IntArray"),
    Map.entry("LongArray", "kotlin.LongArray"),
    Map.entry("BooleanArray", "kotlin.BooleanArray"),
    Map.entry("CharArray", "kotlin.CharArray"),
    Map.entry("DoubleArray", "kotlin.DoubleArray"),
    Map.entry("FloatArray", "kotlin.FloatArray"),
    Map.entry("ByteArray", "kotlin.ByteArray"),
    Map.entry("ShortArray", "kotlin.ShortArray"),
    Map.entry("Comparable", "kotlin.Comparable"),
    Map.entry("Enum", "kotlin.Enum"),
    Map.entry("Annotation", "kotlin.Annotation"),
    Map.entry("Pair", "kotlin.Pair"),
    Map.entry("Triple", "kotlin.Triple"),
    Map.entry("List", "kotlin.collections.List"),
    Map.entry("MutableList", "kotlin.collections.MutableList"),
    Map.entry("Set", "kotlin.collections.Set"),
    Map.entry("MutableSet", "kotlin.collections.MutableSet"),
    Map.entry("Map", "kotlin.collections.Map"),
    Map.entry("MutableMap", "kotlin.collections.MutableMap"),
    Map.entry("Collection", "kotlin.collections.Collection"),
    Map.entry("MutableCollection", "kotlin.collections.MutableCollection"),
    Map.entry("Iterable", "kotlin.collections.Iterable"),
    Map.entry("MutableIterable", "kotlin.collections.MutableIterable"),
    Map.entry("Iterator", "kotlin.collections.Iterator"),
    Map.entry("MutableIterator", "kotlin.collections.MutableIterator"),
    Map.entry("Sequence", "kotlin.sequences.Sequence"),
    Map.entry("CharSequence", "kotlin.CharSequence"),
    Map.entry("Cloneable", "kotlin.Cloneable")
  );

  private static final Set<String> JAVA_LANG_FALLBACK = Set.of(
    "Object", "Class", "Math", "System", "Thread", "Runnable", "StringBuilder", "StringBuffer",
    "Void", "Deprecated", "Override", "SuppressWarnings"
  );

  private final String packageName;
  private final Map<String, String> imports;

  static @NotNull KotlinSyntaxImportContext extractFrom(@NotNull SyntaxNode fileRoot) {
    return new KotlinSyntaxImportContext(extractPackageName(fileRoot), extractImports(fileRoot));
  }

  private KotlinSyntaxImportContext(@NotNull String packageName, @NotNull Map<String, String> imports) {
    this.packageName = packageName;
    this.imports = imports;
  }

  @NotNull String packageName() {
    return packageName;
  }

  @NotNull String resolveSimpleName(@NotNull String simple) {
    String byImport = imports.get(simple);
    if (byImport != null) return byImport;
    String byAutoImport = KOTLIN_AUTO_IMPORTS.get(simple);
    if (byAutoImport != null) return byAutoImport;
    if (JAVA_LANG_FALLBACK.contains(simple)) return "java.lang." + simple;
    if (!packageName.isEmpty()) return packageName + "." + simple;
    return simple;
  }

  private static @NotNull String extractPackageName(@NotNull SyntaxNode fileRoot) {
    SyntaxNode pkg = firstChildOfType(fileRoot, KtNodeTypes.INSTANCE.getPACKAGE_DIRECTIVE());
    if (pkg == null) return "";
    StringBuilder sb = new StringBuilder();
    for (SyntaxNode c = pkg.firstChild(); c != null; c = c.nextSibling()) {
      SyntaxElementType t = c.getType();
      if (t == KtTokens.INSTANCE.getPACKAGE_KEYWORD()) continue;
      if (t == KtNodeTypes.INSTANCE.getDOT_QUALIFIED_EXPRESSION() ||
          t == KtNodeTypes.INSTANCE.getREFERENCE_EXPRESSION()) {
        if (!sb.isEmpty()) sb.append('.');
        sb.append(buildDottedText(c));
      }
    }
    return sb.toString();
  }

  private static @NotNull Map<String, String> extractImports(@NotNull SyntaxNode fileRoot) {
    SyntaxNode importList = firstChildOfType(fileRoot, KtNodeTypes.INSTANCE.getIMPORT_LIST());
    if (importList == null) return Map.of();
    Map<String, String> imports = new HashMap<>();
    for (SyntaxNode imp = importList.firstChild(); imp != null; imp = imp.nextSibling()) {
      if (imp.getType() != KtNodeTypes.INSTANCE.getIMPORT_DIRECTIVE()) continue;
      if (firstChildOfType(imp, KtTokens.INSTANCE.getMUL()) != null) continue; // wildcard
      String dotted = importedFqn(imp);
      if (dotted == null) continue;
      SyntaxNode aliasNode = firstChildOfType(imp, KtNodeTypes.INSTANCE.getIMPORT_ALIAS());
      String name = aliasNode == null ? tailSegment(dotted) : aliasIdentifier(aliasNode);
      if (name != null) imports.put(name, dotted);
    }
    return imports;
  }

  private static @org.jetbrains.annotations.Nullable String importedFqn(@NotNull SyntaxNode importDirective) {
    for (SyntaxNode c = importDirective.firstChild(); c != null; c = c.nextSibling()) {
      SyntaxElementType t = c.getType();
      if (t == KtNodeTypes.INSTANCE.getDOT_QUALIFIED_EXPRESSION() ||
          t == KtNodeTypes.INSTANCE.getREFERENCE_EXPRESSION()) {
        return buildDottedText(c);
      }
    }
    return null;
  }

  private static @org.jetbrains.annotations.Nullable String aliasIdentifier(@NotNull SyntaxNode aliasNode) {
    SyntaxNode id = firstChildOfType(aliasNode, KtTokens.INSTANCE.getIDENTIFIER());
    return id == null ? null : id.getText().toString();
  }

  private static @NotNull String tailSegment(@NotNull String dotted) {
    int lastDot = dotted.lastIndexOf('.');
    return lastDot < 0 ? dotted : dotted.substring(lastDot + 1);
  }
}
