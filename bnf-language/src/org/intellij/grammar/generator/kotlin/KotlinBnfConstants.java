/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.kotlin;

import org.jetbrains.annotations.NotNull;

public interface KotlinBnfConstants {
  @NotNull String KT_SUPPRESS_ANNO = "@kotlin.Suppress";

  @NotNull String KT_ELEMENT_TYPE_CLASS = "com.intellij.platform.syntax.SyntaxElementType";
  @NotNull String KT_BUILDER_CLASS = "com.intellij.platform.syntax.parser.SyntaxTreeBuilder";

  @NotNull String KT_PAIR_CLASS = "kotlin.Pair";
  @NotNull String KT_ARRAY_CLASS = "kotlin.Array";
  @NotNull String KT_ARRAY_OF_FUNCTION = "kotlin.arrayOf";

  @NotNull String KT_RUNTIME_PACKAGE = "com.intellij.platform.syntax.util.runtime";
  @NotNull String KT_PARSER_RUNTIME_CLASS = "com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime";

  @NotNull String KT_SYNTAX_ELEMENT_TYPE_SET_CLASS = "com.intellij.platform.syntax.SyntaxElementTypeSet";
  @NotNull String KT_SYNTAX_ELEMENT_TYPE_SET_OF_FUNCTION = "com.intellij.platform.syntax.syntaxElementTypeSetOf";
  
  @NotNull String KT_ELEMENT_TYPE_CONVERTER_CLASS = "com.intellij.platform.syntax.psi.ElementTypeConverter";
  @NotNull String KT_ELEMENT_TYPE_CONVERTER_FACTORY_CLASS = "com.intellij.platform.syntax.psi.ElementTypeConverterFactory";
  @NotNull String KT_ELEMENT_TYPE_CONVERTER_FILE = "com.intellij.platform.syntax.psi.ElementTypeConverterKt";

  @NotNull String KT_MODIFIERS_CLASS = "com.intellij.platform.syntax.util.runtime.Modifiers";
}
