/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.tree.ParseKt;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.platform.syntax.util.language.SyntaxElementLanguageProvider;
import fleet.org.jetbrains.kotlin.kmp.lexer.KotlinLexer;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import fleet.org.jetbrains.kotlin.kmp.parser.KotlinParser;
import fleet.org.jetbrains.kotlin.kmp.parser.KotlinParserKt;
import kotlin.Unit;
import kotlin.sequences.SequencesKt;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.jetbrains.annotations.NotNull;

/**
 * Parses a Kotlin source string with the {@code syntax-api} + {@code kotlin-syntax} libraries and
 * returns the root {@link SyntaxNode} of the parsed tree. File-level caching is handled by
 * {@link SyntaxTreeCache}; this class is a stateless parser shim.
 * <p>
 * Unlike Java, {@code kotlin-syntax} does not expose a {@code LanguageSyntaxDefinition} — we drive
 * the lower-level {@link ParseKt#parse} overload directly with the lexer factory + parser lambda
 * and pass an empty {@link SyntaxElementLanguageProvider}.
 */
@SuppressWarnings("UnstableApiUsage")
final class KotlinSyntaxTreeManager {
  private static final SyntaxElementLanguageProvider NO_LANG = type -> SequencesKt.emptySequence();

  private KotlinSyntaxTreeManager() { }

  static @NotNull SyntaxNode parseText(@NotNull String text) {
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
}
