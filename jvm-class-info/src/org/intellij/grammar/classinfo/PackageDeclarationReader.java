/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.java.syntax.JavaSyntaxDefinition;
import com.intellij.java.syntax.element.JavaSyntaxTokenType;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.SyntaxElementTypeSet;
import com.intellij.platform.syntax.lexer.Lexer;
import fleet.org.jetbrains.kotlin.kmp.lexer.KotlinLexer;
import fleet.org.jetbrains.kotlin.kmp.lexer.KtTokens;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads only the {@code package} declaration of a Java or Kotlin source file by running the
 * language's lexer over the file's bytes and following the token stream until the package directive
 * is consumed. This is intentionally cheaper than {@code JavaSyntaxTreeManager#parseText} /
 * {@code KotlinSyntaxTreeManager#parseText} — no AST is built — and is used by the syntax-class
 * providers to learn a file's declared package without paying for a full parse, so the package-index
 * fallback (see {@code KotlinSyntaxClassSymbolProvider} / {@code JavaSyntaxClassSymbolProvider}) can
 * decide whether to ingest the file.
 *
 * <p>An empty string is returned for files in the default package, files where no {@code package}
 * directive is found within the lexer's bounded scan, and files that fail to read.
 */
@SuppressWarnings("UnstableApiUsage")
public final class PackageDeclarationReader {

  // Most files declare package within the first kilobyte; cap the scan to avoid lexing entire
  // multi-megabyte files when the directive is missing or in the default package.
  private static final int SCAN_BYTE_BUDGET = 8 * 1024;

  private PackageDeclarationReader() { }

  public static @NotNull String readJavaPackage(@NotNull Path file) {
    CharSequence text = readPrefix(file);
    if (text.isEmpty()) return "";
    Lexer lexer = JavaSyntaxDefinition.INSTANCE.createLexer();
    return readPackageFrom(lexer, text,
                           JavaSyntaxTokenType.PACKAGE_KEYWORD,
                           JavaSyntaxTokenType.IDENTIFIER,
                           JavaSyntaxTokenType.DOT,
                           JavaSyntaxDefinition.INSTANCE.getWhitespaces(),
                           JavaSyntaxDefinition.INSTANCE.getComments());
  }

  public static @NotNull String readKotlinPackage(@NotNull Path file) {
    CharSequence text = readPrefix(file);
    if (text.isEmpty()) return "";
    Lexer lexer = new KotlinLexer();
    return readPackageFrom(lexer, text,
                           KtTokens.INSTANCE.getPACKAGE_KEYWORD(),
                           KtTokens.INSTANCE.getIDENTIFIER(),
                           KtTokens.INSTANCE.getDOT(),
                           KtTokens.INSTANCE.getWHITESPACES(),
                           KtTokens.INSTANCE.getCOMMENTS());
  }

  private enum State { SEEK_KEYWORD, EXPECT_IDENT, EXPECT_DOT }

  private static @NotNull String readPackageFrom(@NotNull Lexer lexer,
                                                 @NotNull CharSequence text,
                                                 @NotNull SyntaxElementType packageKw,
                                                 @NotNull SyntaxElementType identifier,
                                                 @NotNull SyntaxElementType dot,
                                                 @NotNull SyntaxElementTypeSet whitespaces,
                                                 @NotNull SyntaxElementTypeSet comments) {
    lexer.start(text, 0, text.length(), 0);

    State state = State.SEEK_KEYWORD;
    StringBuilder result = new StringBuilder();

    outer:
    while (lexer.getTokenType() != null) {
      SyntaxElementType t = lexer.getTokenType();
      if (whitespaces.contains(t) || comments.contains(t)) {
        lexer.advance();
        continue;
      }
      switch (state) {
        case SEEK_KEYWORD -> {
          // Any non-trivia token is fair game until `package` shows up — file-level annotations
          // (e.g. Kotlin @file:JvmName) can precede the directive.
          if (t == packageKw) {
            state = State.EXPECT_IDENT;
          }
        }
        case EXPECT_IDENT -> {
          if (t != identifier) break outer;
          appendIdentifier(result, lexer.getTokenText());
          state = State.EXPECT_DOT;
        }
        case EXPECT_DOT -> {
          if (t != dot) break outer;
          result.append('.');
          state = State.EXPECT_IDENT;
        }
      }
      lexer.advance();
    }

    // Drop a dangling trailing dot from a malformed `package com.` — return what we have minus it.
    while (!result.isEmpty() && result.charAt(result.length() - 1) == '.') {
      result.setLength(result.length() - 1);
    }
    return result.toString();
  }

  private static void appendIdentifier(@NotNull StringBuilder out, @NotNull String text) {
    // Kotlin allows backtick-escaped identifiers: `package` \`org\`.foo. Strip the backticks so the
    // assembled FQN matches what the AST-based extractor produces.
    if (text.length() >= 2 && text.charAt(0) == '`' && text.charAt(text.length() - 1) == '`') {
      out.append(text, 1, text.length() - 1);
    }
    else {
      out.append(text);
    }
  }

  private static @NotNull CharSequence readPrefix(@NotNull Path file) {
    try {
      long size = Files.size(file);
      if (size <= SCAN_BYTE_BUDGET) {
        return Files.readString(file, StandardCharsets.UTF_8);
      }
      byte[] buffer = new byte[SCAN_BYTE_BUDGET];
      try (var in = Files.newInputStream(file)) {
        int read = in.readNBytes(buffer, 0, SCAN_BYTE_BUDGET);
        return new String(buffer, 0, read, StandardCharsets.UTF_8);
      }
    }
    catch (IOException e) {
      return "";
    }
  }
}
