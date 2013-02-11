package org.intellij.grammar.expression;

import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.psi.BnfTypes;

public interface ExpressionTypes {

  IElementType ARG_LIST = ExpressionParserDefinition.createType("ARG_LIST");
  IElementType ASSIGN_EXPR = ExpressionParserDefinition.createType("ASSIGN_EXPR");
  IElementType DIV_EXPR = ExpressionParserDefinition.createType("DIV_EXPR");
  IElementType CALL_EXPR = ExpressionParserDefinition.createType("CALL_EXPR");
  IElementType CONDITIONAL_EXPR = ExpressionParserDefinition.createType("CONDITIONAL_EXPR");
  IElementType EXPR = ExpressionParserDefinition.createType("EXPR");
  IElementType EXP_EXPR = ExpressionParserDefinition.createType("EXP_EXPR");
  IElementType FACTORIAL_EXPR = ExpressionParserDefinition.createType("FACTORIAL_EXPR");
  IElementType IDENTIFIER = ExpressionParserDefinition.createType("IDENTIFIER");
  IElementType LITERAL_EXPR = ExpressionParserDefinition.createType("LITERAL_EXPR");
  IElementType MINUS_EXPR = ExpressionParserDefinition.createType("MINUS_EXPR");
  IElementType MUL_EXPR = ExpressionParserDefinition.createType("MUL_EXPR");
  IElementType PAREN_EXPR = ExpressionParserDefinition.createType("PAREN_EXPR");
  IElementType PLUS_EXPR = ExpressionParserDefinition.createType("PLUS_EXPR");
  IElementType REF_EXPR = ExpressionParserDefinition.createType("REF_EXPR");
  IElementType SPECIAL_EXPR = ExpressionParserDefinition.createType("SPECIAL_EXPR");
  IElementType UNARY_MIN_EXPR = ExpressionParserDefinition.createType("UNARY_MIN_EXPR");
  IElementType UNARY_PLUS_EXPR = ExpressionParserDefinition.createType("UNARY_PLUS_EXPR");

  IElementType ID = BnfTypes.BNF_ID;
  IElementType NUMBER = BnfTypes.BNF_NUMBER;
}
