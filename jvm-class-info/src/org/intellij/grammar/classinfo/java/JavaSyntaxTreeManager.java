/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.java.syntax.JavaSyntaxDefinition;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.SyntaxLanguage;
import com.intellij.platform.syntax.tree.ParseKt;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.platform.syntax.util.language.SyntaxElementLanguageProvider;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.jetbrains.annotations.NotNull;

/**
 * Parses a Java source string with the {@code syntax-api} + {@code java-syntax} libraries and
 * returns the root {@link SyntaxNode} of the parsed tree. File-level caching is handled by
 * {@link SyntaxTreeCache}; this class is a stateless parser shim
 * exposing {@link #parseText} as a method reference for cache callers.
 */
@SuppressWarnings("UnstableApiUsage")
public final class JavaSyntaxTreeManager {

  private JavaSyntaxTreeManager() { }

  /**
   * Parses an in-memory source string.
   */
  public static @NotNull SyntaxNode parseText(@NotNull String text) {
    return ParseKt.parse(text, JavaSyntaxDefinition.INSTANCE, JavaLang.INSTANCE, null, null);
  }

  private static final class JavaLang implements SyntaxElementLanguageProvider {
    public static final JavaLang INSTANCE = new JavaLang();

    @Override
    public @NotNull Sequence<SyntaxLanguage> getLanguages(@NotNull SyntaxElementType type) {
      return SequencesKt.sequenceOf(JavaSyntaxDefinition.INSTANCE.getLanguage());
    }
  }
}
