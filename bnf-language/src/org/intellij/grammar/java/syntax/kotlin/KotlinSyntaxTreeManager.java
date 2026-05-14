/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import com.intellij.platform.syntax.tree.ParseKt;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.platform.syntax.util.language.SyntaxElementLanguageProvider;
import com.intellij.util.containers.ContainerUtil;
import fleet.org.jetbrains.kotlin.kmp.lexer.KotlinLexer;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KotlinParser;
import fleet.org.jetbrains.kotlin.kmp.parser.KotlinParserKt;
import kotlin.Unit;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Parses a Kotlin source file with the {@code syntax-api} + {@code kotlin-syntax} libraries and
 * returns the root {@link SyntaxNode} of the parsed tree. Mtime-keyed soft-value cache mirrors the
 * Java side ({@link org.intellij.grammar.java.syntax.JavaSyntaxTreeManager}).
 * <p>
 * Unlike Java, {@code kotlin-syntax} does not expose a {@code LanguageSyntaxDefinition} — we drive
 * the lower-level {@link ParseKt#parse} overload directly with the lexer factory + parser lambda
 * and pass an empty {@link SyntaxElementLanguageProvider} (no per-node language metadata is needed
 * here, the tree walker only cares about element types).
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxTreeManager {
  private static final SyntaxElementLanguageProvider NO_LANG = type -> SequencesKt.emptySequence();

  private final Map<Path, CacheEntry> cache = ContainerUtil.createSoftValueMap();

  @Nullable SyntaxNode parseFile(@NotNull Path path) {
    long mtime;
    try {
      mtime = Files.getLastModifiedTime(path).toMillis();
    }
    catch (IOException e) {
      return null;
    }
    CacheEntry entry = cache.get(path);
    if (entry != null && entry.mtime == mtime) return entry.root;
    String text;
    try {
      text = Files.readString(path);
    }
    catch (IOException e) {
      return null;
    }
    SyntaxNode root = parseText(text);
    cache.put(path, new CacheEntry(mtime, root));
    return root;
  }

  static @NotNull SyntaxNode parseText(@NotNull CharSequence text) {
    return ParseKt.parse(
      text,
      KotlinLexer::new,
      builder -> {
        new KotlinParser(false, false).parse(builder);
        return Unit.INSTANCE;
      },
      KtTokens.INSTANCE.getWHITESPACES(),
      KtTokens.INSTANCE.getCOMMENTS(),
      NO_LANG,
      null,
      null,
      ParseKt.defaultTokenizationPolicy(null),
      KotlinParserKt.getKotlinBindingPolicy()
    );
  }

  private record CacheEntry(long mtime, SyntaxNode root) { }
}
