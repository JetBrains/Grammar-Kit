/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

public final class KotlinBnfConstants {
  public static final @NotNull String KT_SUPPRESS_ANNO = "@kotlin.Suppress";
  public static final @NotNull String KT_ELEMENT_TYPE_CLASS = "com.intellij.platform.syntax.SyntaxElementType";
  public static final @NotNull String KT_BUILDER_CLASS = "com.intellij.platform.syntax.parser.SyntaxTreeBuilder";
  public static final @NotNull String KT_SET_CLASS = "kotlin.collections.Set";
  public static final @NotNull String KT_SET_OF_FUNCTION = "kotlin.collections.setOf";
  public static final @NotNull String KT_ARRAY_CLASS = "kotlin.Array";
  public static final @NotNull String KT_ARRAY_OF_FUNCTION = "kotlin.arrayOf";
  public static final @NotNull String KT_PARSER_RUNTIME_CLASS = "com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase";

  private KotlinBnfConstants() {
  }
}
