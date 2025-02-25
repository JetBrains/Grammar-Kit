// ---- ExpressionTypes.kt -----------------
//header.txt
package org.intellij.grammar.expression

import com.intellij.platform.syntax.SyntaxElementType

object ExpressionTypes {
  val ARG_LIST = SyntaxElementType("ARG_LIST")
  val ASSIGN_EXPR = SyntaxElementType("ASSIGN_EXPR")
  val BETWEEN_EXPR = SyntaxElementType("BETWEEN_EXPR")
  val CALL_EXPR = SyntaxElementType("CALL_EXPR")
  val CONDITIONAL_EXPR = SyntaxElementType("CONDITIONAL_EXPR")
  val DIV_EXPR = SyntaxElementType("DIV_EXPR")
  val ELVIS_EXPR = SyntaxElementType("ELVIS_EXPR")
  val EXPR = SyntaxElementType("EXPR")
  val EXP_EXPR = SyntaxElementType("EXP_EXPR")
  val FACTORIAL_EXPR = SyntaxElementType("FACTORIAL_EXPR")
  val IDENTIFIER = SyntaxElementType("IDENTIFIER")
  val IS_NOT_EXPR = SyntaxElementType("IS_NOT_EXPR")
  val LITERAL_EXPR = SyntaxElementType("LITERAL_EXPR")
  val MINUS_EXPR = SyntaxElementType("MINUS_EXPR")
  val MUL_EXPR = SyntaxElementType("MUL_EXPR")
  val PAREN_EXPR = SyntaxElementType("PAREN_EXPR")
  val PLUS_EXPR = SyntaxElementType("PLUS_EXPR")
  val REF_EXPR = SyntaxElementType("REF_EXPR")
  val SPECIAL_EXPR = SyntaxElementType("SPECIAL_EXPR")
  val UNARY_MIN_EXPR = SyntaxElementType("UNARY_MIN_EXPR")
  val UNARY_NOT_EXPR = SyntaxElementType("UNARY_NOT_EXPR")
  val UNARY_PLUS_EXPR = SyntaxElementType("UNARY_PLUS_EXPR")
  val XOR_EXPR = SyntaxElementType("XOR_EXPR")

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