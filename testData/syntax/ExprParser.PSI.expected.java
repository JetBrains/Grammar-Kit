// ---- ExpressionTypes.java -----------------
//header.txt
package org.intellij.grammar.expression;

import com.intellij.platform.syntax.SyntaxElementType;

public interface ExpressionTypes {

  SyntaxElementType ARG_LIST = ExpressionParserDefinition.createType("ARG_LIST");
  SyntaxElementType ASSIGN_EXPR = ExpressionParserDefinition.createType("ASSIGN_EXPR");
  SyntaxElementType BETWEEN_EXPR = ExpressionParserDefinition.createType("BETWEEN_EXPR");
  SyntaxElementType CALL_EXPR = ExpressionParserDefinition.createType("CALL_EXPR");
  SyntaxElementType CONDITIONAL_EXPR = ExpressionParserDefinition.createType("CONDITIONAL_EXPR");
  SyntaxElementType DIV_EXPR = ExpressionParserDefinition.createType("DIV_EXPR");
  SyntaxElementType ELVIS_EXPR = ExpressionParserDefinition.createType("ELVIS_EXPR");
  SyntaxElementType EXPR = ExpressionParserDefinition.createType("EXPR");
  SyntaxElementType EXP_EXPR = ExpressionParserDefinition.createType("EXP_EXPR");
  SyntaxElementType FACTORIAL_EXPR = ExpressionParserDefinition.createType("FACTORIAL_EXPR");
  SyntaxElementType IDENTIFIER = ExpressionParserDefinition.createType("IDENTIFIER");
  SyntaxElementType IS_NOT_EXPR = ExpressionParserDefinition.createType("IS_NOT_EXPR");
  SyntaxElementType LITERAL_EXPR = ExpressionParserDefinition.createType("LITERAL_EXPR");
  SyntaxElementType MINUS_EXPR = ExpressionParserDefinition.createType("MINUS_EXPR");
  SyntaxElementType MUL_EXPR = ExpressionParserDefinition.createType("MUL_EXPR");
  SyntaxElementType PAREN_EXPR = ExpressionParserDefinition.createType("PAREN_EXPR");
  SyntaxElementType PLUS_EXPR = ExpressionParserDefinition.createType("PLUS_EXPR");
  SyntaxElementType REF_EXPR = ExpressionParserDefinition.createType("REF_EXPR");
  SyntaxElementType SPECIAL_EXPR = ExpressionParserDefinition.createType("SPECIAL_EXPR");
  SyntaxElementType UNARY_MIN_EXPR = ExpressionParserDefinition.createType("UNARY_MIN_EXPR");
  SyntaxElementType UNARY_NOT_EXPR = ExpressionParserDefinition.createType("UNARY_NOT_EXPR");
  SyntaxElementType UNARY_PLUS_EXPR = ExpressionParserDefinition.createType("UNARY_PLUS_EXPR");
  SyntaxElementType XOR_EXPR = ExpressionParserDefinition.createType("XOR_EXPR");

  SyntaxElementType AND = ExpressionParserDefinition.createTokenType("AND");
  SyntaxElementType BETWEEN = ExpressionParserDefinition.createTokenType("BETWEEN");
  SyntaxElementType COMMENT = ExpressionParserDefinition.createTokenType("comment");
  SyntaxElementType ID = ExpressionParserDefinition.createTokenType("id");
  SyntaxElementType IS = ExpressionParserDefinition.createTokenType("IS");
  SyntaxElementType NOT = ExpressionParserDefinition.createTokenType("NOT");
  SyntaxElementType NUMBER = ExpressionParserDefinition.createTokenType("number");
  SyntaxElementType STRING = ExpressionParserDefinition.createTokenType("string");
  SyntaxElementType SYNTAX = ExpressionParserDefinition.createTokenType("syntax");
}