/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

record GrammarPattern(
  @NotNull File grammarDir,
  @NotNull Pattern grammarPattern,
  @NotNull String wildCard
) {

  @NotNull List<File> collectGrammarFiles() {
    File[] files = grammarDir.listFiles();
    if (files == null) return List.of();

    return Stream.of(files)
      .filter(f -> !f.isDirectory() && grammarPattern.matcher(f.getName()).matches())
      .toList();
  }

  boolean isValid() {
    return grammarDir.exists() && grammarDir.isDirectory();
  }

  static @NotNull GrammarPattern of(@NotNull String grammar) {
    int idx = grammar.lastIndexOf(File.separator);
    File grammarDir = new File(idx >= 0 ? grammar.substring(0, idx) : ".");
    String wildCard = idx >= 0 ? grammar.substring(idx + 1) : grammar;
    Pattern grammarPattern = Pattern.compile(convertToJavaPattern(wildCard));
    return new GrammarPattern(grammarDir, grammarPattern, wildCard);
  }

  private static @NotNull String convertToJavaPattern(@NotNull String wildcardPattern) {
    wildcardPattern = StringUtil.replace(wildcardPattern, ".", "\\.");
    wildcardPattern = StringUtil.replace(wildcardPattern, "*?", ".+");
    wildcardPattern = StringUtil.replace(wildcardPattern, "?*", ".+");
    wildcardPattern = StringUtil.replace(wildcardPattern, "*", ".*");
    wildcardPattern = StringUtil.replace(wildcardPattern, "?", ".");
    return wildcardPattern;
  }
}
