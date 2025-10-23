// ---- org/intellij/grammar/expression/ExpressionSyntaxTypes.kt -----------------
//header.txt
package org.intellij.grammar.expression

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import org.intellij.grammar.expression.ExpressionParserDefinition

object ExpressionSyntaxTypes {
  val ARG_LIST = ExpressionParserDefinition.createSyntaxType("ARG_LIST"))
  val ASSIGN_EXPR = ExpressionParserDefinition.createSyntaxType("ASSIGN_EXPR"))
  val BETWEEN_EXPR = ExpressionParserDefinition.createSyntaxType("BETWEEN_EXPR"))
  val CALL_EXPR = ExpressionParserDefinition.createSyntaxType("CALL_EXPR"))
  val CONDITIONAL_EXPR = ExpressionParserDefinition.createSyntaxType("CONDITIONAL_EXPR"))
  val DIV_EXPR = ExpressionParserDefinition.createSyntaxType("DIV_EXPR"))
  val ELVIS_EXPR = ExpressionParserDefinition.createSyntaxType("ELVIS_EXPR"))
  val EXPR = ExpressionParserDefinition.createSyntaxType("EXPR"))
  val EXP_EXPR = ExpressionParserDefinition.createSyntaxType("EXP_EXPR"))
  val FACTORIAL_EXPR = ExpressionParserDefinition.createSyntaxType("FACTORIAL_EXPR"))
  val IDENTIFIER = ExpressionParserDefinition.createSyntaxType("IDENTIFIER"))
  val IS_NOT_EXPR = ExpressionParserDefinition.createSyntaxType("IS_NOT_EXPR"))
  val LITERAL_EXPR = ExpressionParserDefinition.createSyntaxType("LITERAL_EXPR"))
  val MINUS_EXPR = ExpressionParserDefinition.createSyntaxType("MINUS_EXPR"))
  val MUL_EXPR = ExpressionParserDefinition.createSyntaxType("MUL_EXPR"))
  val PAREN_EXPR = ExpressionParserDefinition.createSyntaxType("PAREN_EXPR"))
  val PLUS_EXPR = ExpressionParserDefinition.createSyntaxType("PLUS_EXPR"))
  val REF_EXPR = ExpressionParserDefinition.createSyntaxType("REF_EXPR"))
  val SPECIAL_EXPR = ExpressionParserDefinition.createSyntaxType("SPECIAL_EXPR"))
  val UNARY_MIN_EXPR = ExpressionParserDefinition.createSyntaxType("UNARY_MIN_EXPR"))
  val UNARY_NOT_EXPR = ExpressionParserDefinition.createSyntaxType("UNARY_NOT_EXPR"))
  val UNARY_PLUS_EXPR = ExpressionParserDefinition.createSyntaxType("UNARY_PLUS_EXPR"))
  val XOR_EXPR = ExpressionParserDefinition.createSyntaxType("XOR_EXPR"))

  val AND = SyntaxElementType("AND")
  val BETWEEN = SyntaxElementType("BETWEEN")
  val COMMENT = SyntaxElementType("comment")
  val ID = SyntaxElementType("id")
  val IS = SyntaxElementType("IS")
  val NOT = SyntaxElementType("NOT")
  val NUMBER = SyntaxElementType("number")
  val STRING = SyntaxElementType("string")
  val SYNTAX = SyntaxElementType("syntax")
}