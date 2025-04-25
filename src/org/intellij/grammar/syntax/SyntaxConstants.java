package org.intellij.grammar.syntax;

public interface SyntaxConstants {

  String SYNTAX_ELEMENT_TYPE = "com.intellij.platform.syntax.SyntaxElementType";
  String RUNTIME_CLASS = "com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime";
  String RUNTIME_PARSER_INTERFACE = "SyntaxGeneratedParserRuntime.Parser";
  String RUNTIME_STATIC_METHOD_HOLDER = "com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntimeKt";
  String SYNTAX_BUILDER_CLASS = "com.intellij.platform.syntax.parser.SyntaxTreeBuilder";
  String TOKEN_SET_CLASS = "com.intellij.platform.syntax.SyntaxElementTypeSet";
  String TOKEN_SET_FILE = "com.intellij.platform.syntax.SyntaxElementTypeSetKt";
  String SYNTAX_ELEMENT_TYPE_CONVERTER = "com.intellij.platform.syntax.psi.ElementTypeConverter";
  String SYNTAX_ELEMENT_TYPE_CONVERTER_FACTORY = "com.intellij.platform.syntax.psi.ElementTypeConverterFactory";
  String SYNTAX_ELEMENT_TYPE_CONVERTER_FILE ="com.intellij.platform.syntax.psi.ElementTypeConverterKt";
  String PRODUCTION_RESULT = "com.intellij.platform.syntax.parser.ProductionResult";
  String PRODUCTION_RESULT_FILE = "com.intellij.platform.syntax.parser.ProductionResultKt";
  
  String[] MODIFIERS = {
    "com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NONE_",
    "com.intellij.platform.syntax.util.runtime.Modifiers.Companion._COLLAPSE_",
    "com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_",
    "com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_INNER_",
    "com.intellij.platform.syntax.util.runtime.Modifiers.Companion._AND_",
    "com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NOT_",
    "com.intellij.platform.syntax.util.runtime.Modifiers.Companion._UPPER_",
  };

  String KOTLIN_FUNCTION2_CLASS = "kotlin.jvm.functions.Function2";
  String KOTLIN_UNIT_CLASS = "kotlin.Unit";
  String KOTLIN_PAIR_CLASS = "kotlin.Pair";
}