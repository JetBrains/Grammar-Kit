/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.tree.SyntaxNode;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KtNodeTypes;
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
import static org.intellij.grammar.classinfo.kotlin.KotlinSyntaxNodes.buildDottedText;

/**
 * Kotlin file-level resolution scope. Built-in resolution covers Kotlin standard names (mapped to
 * their {@code kotlin.*} FQNs) and a small {@code java.lang} fallback for names that are not part
 * of Kotlin's auto-imports but are still resolvable without an explicit import.
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxImportContext extends AbstractImportContext {

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
    Map.entry("Cloneable", "kotlin.Cloneable"),
    // kotlin.jvm.* — auto-available in Kotlin and emitted on the JVM with these FQNs by kotlinc.
    Map.entry("JvmStatic", "kotlin.jvm.JvmStatic"),
    Map.entry("JvmField", "kotlin.jvm.JvmField"),
    Map.entry("JvmName", "kotlin.jvm.JvmName"),
    Map.entry("JvmOverloads", "kotlin.jvm.JvmOverloads"),
    Map.entry("JvmMultifileClass", "kotlin.jvm.JvmMultifileClass"),
    Map.entry("JvmSynthetic", "kotlin.jvm.JvmSynthetic"),
    Map.entry("JvmSuppressWildcards", "kotlin.jvm.JvmSuppressWildcards"),
    Map.entry("JvmWildcard", "kotlin.jvm.JvmWildcard"),
    Map.entry("JvmDefault", "kotlin.jvm.JvmDefault"),
    Map.entry("JvmDefaultWithoutCompatibility", "kotlin.jvm.JvmDefaultWithoutCompatibility"),
    Map.entry("Throws", "kotlin.jvm.Throws"),
    Map.entry("Volatile", "kotlin.jvm.Volatile"),
    Map.entry("Transient", "kotlin.jvm.Transient"),
    Map.entry("Strictfp", "kotlin.jvm.Strictfp"),
    Map.entry("Synchronized", "kotlin.jvm.Synchronized")
  );

  private static final Set<String> JAVA_LANG_FALLBACK = Set.of(
    "Object", "Class", "Math", "System", "Thread", "Runnable", "StringBuilder", "StringBuffer",
    "Void", "Deprecated", "Override", "SuppressWarnings",
    // Common JDK exception / throwable types that Kotlin stdlib typealiases to java.lang.* — at
    // source level they're written unqualified, at the JVM ASM produces the java.lang.* FQN.
    "IllegalStateException", "IllegalArgumentException", "NullPointerException",
    "ClassCastException", "UnsupportedOperationException", "IndexOutOfBoundsException",
    "ArrayIndexOutOfBoundsException", "NoSuchElementException", "ConcurrentModificationException",
    "ArithmeticException", "NumberFormatException", "AssertionError",
    "Integer", "Character"
  );

  static @NotNull KotlinSyntaxImportContext extractFrom(@NotNull SyntaxNode fileRoot, @NotNull SymbolResolver resolver) {
    Map<String, String> singleImports = new HashMap<>();
    List<String> wildcards = new ArrayList<>();
    extractImports(fileRoot, singleImports, wildcards);
    return new KotlinSyntaxImportContext(extractPackageName(fileRoot), singleImports, wildcards, resolver);
  }

  private KotlinSyntaxImportContext(@NotNull String packageName,
                                    @NotNull Map<String, String> imports,
                                    @NotNull List<String> wildcardImports,
                                    @NotNull SymbolResolver resolver) {
    super(packageName, imports, wildcardImports, resolver);
  }

  @Override
  protected @Nullable String resolveBuiltin(@NotNull String simple) {
    String byAutoImport = KOTLIN_AUTO_IMPORTS.get(simple);
    if (byAutoImport != null) return byAutoImport;
    if (JAVA_LANG_FALLBACK.contains(simple)) return "java.lang." + simple;
    return null;
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

  private static void extractImports(@NotNull SyntaxNode fileRoot,
                                     @NotNull Map<String, String> single,
                                     @NotNull List<String> wildcards) {
    SyntaxNode importList = firstChildOfType(fileRoot, KtNodeTypes.INSTANCE.getIMPORT_LIST());
    if (importList == null) return;
    for (SyntaxNode imp = importList.firstChild(); imp != null; imp = imp.nextSibling()) {
      if (imp.getType() != KtNodeTypes.INSTANCE.getIMPORT_DIRECTIVE()) continue;
      String dotted = importedFqn(imp);
      if (dotted == null) continue;
      if (firstChildOfType(imp, KtTokens.INSTANCE.getMUL()) != null) {
        // Wildcard: probe lazily through the resolver when resolving a simple name.
        wildcards.add(dotted);
        continue;
      }
      SyntaxNode aliasNode = firstChildOfType(imp, KtNodeTypes.INSTANCE.getIMPORT_ALIAS());
      String name = aliasNode == null ? tailSegment(dotted) : aliasIdentifier(aliasNode);
      if (name != null) single.put(name, dotted);
    }
  }

  private static @Nullable String importedFqn(@NotNull SyntaxNode importDirective) {
    for (SyntaxNode c = importDirective.firstChild(); c != null; c = c.nextSibling()) {
      SyntaxElementType t = c.getType();
      if (t == KtNodeTypes.INSTANCE.getDOT_QUALIFIED_EXPRESSION() ||
          t == KtNodeTypes.INSTANCE.getREFERENCE_EXPRESSION()) {
        return buildDottedText(c);
      }
    }
    return null;
  }

  private static @Nullable String aliasIdentifier(@NotNull SyntaxNode aliasNode) {
    SyntaxNode id = firstChildOfType(aliasNode, KtTokens.INSTANCE.getIDENTIFIER());
    return id == null ? null : id.getText().toString();
  }

  private static @NotNull String tailSegment(@NotNull String dotted) {
    int lastDot = dotted.lastIndexOf('.');
    return lastDot < 0 ? dotted : dotted.substring(lastDot + 1);
  }
}
