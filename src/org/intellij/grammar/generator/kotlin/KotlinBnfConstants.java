/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.kotlin;

import org.jetbrains.annotations.NotNull;

public interface KotlinBnfConstants {
  @NotNull String KT_SUPPRESS_ANNO = "@kotlin.Suppress";

  @NotNull String KT_ELEMENT_TYPE_CLASS = "com.intellij.platform.syntax.SyntaxElementType";
  @NotNull String KT_BUILDER_CLASS = "com.intellij.platform.syntax.parser.SyntaxTreeBuilder";

  @NotNull String KT_SET_CLASS = "kotlin.collections.Set";
  @NotNull String KT_SET_OF_FUNCTION = "kotlin.collections.setOf";
  @NotNull String KT_ARRAY_CLASS = "kotlin.Array";
  @NotNull String KT_ARRAY_OF_FUNCTION = "kotlin.arrayOf";

  @NotNull String KT_PARSER_RUNTIME_CLASS = "com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase";
}
