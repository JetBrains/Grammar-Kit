// ---- org/intellij/grammar/expression/ExpressionParser.kt -----------------
//header.txt
package org.intellij.grammar.expression

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object ExpressionParser {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, EXTENDS_SETS_)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    var result_: Boolean
    if (root_ == ExpressionSyntaxTypes.EXPR) {
      result_ = expr(runtime_, level_ + 1, -1)
    }
    else {
      result_ = root(runtime_, level_ + 1)
    }
    return result_
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(ExpressionSyntaxTypes.ASSIGN_EXPR, ExpressionSyntaxTypes.BETWEEN_EXPR, ExpressionSyntaxTypes.CALL_EXPR, ExpressionSyntaxTypes.CONDITIONAL_EXPR,
      ExpressionSyntaxTypes.DIV_EXPR, ExpressionSyntaxTypes.ELVIS_EXPR, ExpressionSyntaxTypes.EXPR, ExpressionSyntaxTypes.EXP_EXPR,
      ExpressionSyntaxTypes.FACTORIAL_EXPR, ExpressionSyntaxTypes.IS_NOT_EXPR, ExpressionSyntaxTypes.LITERAL_EXPR, ExpressionSyntaxTypes.MINUS_EXPR,
      ExpressionSyntaxTypes.MUL_EXPR, ExpressionSyntaxTypes.PAREN_EXPR, ExpressionSyntaxTypes.PLUS_EXPR, ExpressionSyntaxTypes.REF_EXPR,
      ExpressionSyntaxTypes.SPECIAL_EXPR, ExpressionSyntaxTypes.UNARY_MIN_EXPR, ExpressionSyntaxTypes.UNARY_NOT_EXPR, ExpressionSyntaxTypes.UNARY_PLUS_EXPR,
      ExpressionSyntaxTypes.XOR_EXPR),
  )

  /* ********************************************************** */
  // '(' [ !')' expr  (',' expr) * ] ')'
  fun arg_list(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "arg_list")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, ExpressionSyntaxTypes.ARG_LIST, "<arg list>")
    result_ = runtime_.consumeToken("(")
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(arg_list_1(runtime_, level_ + 1))
    result_ = pinned_ && runtime_.consumeToken(")") && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // [ !')' expr  (',' expr) * ]
  private fun arg_list_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "arg_list_1")) return false
    arg_list_1_0(runtime_, level_ + 1)
    return true
  }

  // !')' expr  (',' expr) *
  private fun arg_list_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "arg_list_1_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = arg_list_1_0_0(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(expr(runtime_, level_ + 1, -1))
    result_ = pinned_ && arg_list_1_0_2(runtime_, level_ + 1) && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // !')'
  private fun arg_list_1_0_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "arg_list_1_0_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !runtime_.consumeToken(")")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // (',' expr) *
  private fun arg_list_1_0_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "arg_list_1_0_2")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!arg_list_1_0_2_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("arg_list_1_0_2", pos_)) break
    }
    return true
  }

  // ',' expr
  private fun arg_list_1_0_2_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "arg_list_1_0_2_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken(",")
    pinned_ = result_ // pin = 1
    result_ = result_ && expr(runtime_, level_ + 1, -1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // expr ';'?
  internal fun element(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = expr(runtime_, level_ + 1, -1)
    result_ = result_ && element_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, ExpressionParser::element_recover)
    return result_
  }

  // ';'?
  private fun element_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element_1")) return false
    runtime_.consumeToken(";")
    return true
  }

  /* ********************************************************** */
  // !('(' | '+' | '-' | '!' | 'multiply' | id | number)
  internal fun element_recover(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element_recover")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !element_recover_0(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // '(' | '+' | '-' | '!' | 'multiply' | id | number
  private fun element_recover_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element_recover_0")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken("(")
    if (!result_) result_ = runtime_.consumeToken("+")
    if (!result_) result_ = runtime_.consumeToken("-")
    if (!result_) result_ = runtime_.consumeToken("!")
    if (!result_) result_ = runtime_.consumeToken("multiply")
    if (!result_) result_ = runtime_.consumeToken(ExpressionSyntaxTypes.ID)
    if (!result_) result_ = runtime_.consumeToken(ExpressionSyntaxTypes.NUMBER)
    return result_
  }

  /* ********************************************************** */
  // id
  fun identifier(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "identifier")) return false
    if (!runtime_.nextTokenIs(ExpressionSyntaxTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(ExpressionSyntaxTypes.ID)
    runtime_.exit_section_(marker_, ExpressionSyntaxTypes.IDENTIFIER, result_)
    return result_
  }

  /* ********************************************************** */
  // 'multiply' '(' simple_ref_expr ',' mul_expr ')'
  fun meta_special_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "meta_special_expr")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, ExpressionSyntaxTypes.SPECIAL_EXPR, "<meta special expr>")
    result_ = runtime_.consumeToken("multiply")
    result_ = result_ && runtime_.consumeToken("(")
    pinned_ = result_ // pin = 2
    result_ = result_ && runtime_.report_error_(simple_ref_expr(runtime_, level_ + 1))
    result_ = pinned_ && runtime_.report_error_(runtime_.consumeToken(",")) && result_
    result_ = pinned_ && runtime_.report_error_(expr(runtime_, level_ + 1, 3)) && result_
    result_ = pinned_ && runtime_.consumeToken(")") && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // element *
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!element(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("root", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY(assign_expr)
  // 1: BINARY(elvis_expr) BINARY(conditional_expr)
  // 2: BINARY(plus_expr) BINARY(minus_expr)
  // 3: BINARY(xor_expr) BINARY(between_expr) BINARY(is_not_expr)
  // 4: BINARY(mul_expr) BINARY(div_expr)
  // 5: PREFIX(unary_plus_expr) PREFIX(unary_min_expr) PREFIX(unary_not_expr)
  // 6: N_ARY(exp_expr)
  // 7: POSTFIX(factorial_expr)
  // 8: POSTFIX(call_expr)
  // 9: POSTFIX(qualification_expr)
  // 10: ATOM(special_expr) ATOM(simple_ref_expr) ATOM(literal_expr) PREFIX(paren_expr)
  fun expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int, priority_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "expr")) return false
    runtime_.addVariant("<expr>")
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, "<expr>")
    result_ = unary_plus_expr(runtime_, level_ + 1)
    if (!result_) result_ = unary_min_expr(runtime_, level_ + 1)
    if (!result_) result_ = unary_not_expr(runtime_, level_ + 1)
    if (!result_) result_ = SyntaxGeneratedParserRuntime.meta_special_expr(runtime_, level_ + 1)
    if (!result_) result_ = simple_ref_expr(runtime_, level_ + 1)
    if (!result_) result_ = literal_expr(runtime_, level_ + 1)
    if (!result_) result_ = paren_expr(runtime_, level_ + 1)
    pinned_ = result_
    result_ = result_ && expr_0(runtime_, level_ + 1, priority_)
    runtime_.exit_section_(level_, marker_, null, result_, pinned_, null)
    return result_ || pinned_
  }

  fun expr_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int, priority_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "expr_0")) return false
    var result_ = true
    while (true) {
      val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, null)
      if (priority_ < 0 && runtime_.consumeTokenSmart("=")) {
        result_ = expr(runtime_, level_, -1)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.ASSIGN_EXPR, result_, true, null)
      }
      else if (priority_ < 1 && runtime_.consumeTokenSmart("?")) {
        result_ = runtime_.report_error_(expr(runtime_, level_, 1))
        result_ = elvis_expr_1(runtime_, level_ + 1) && result_
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.ELVIS_EXPR, result_, true, null)
      }
      else if (priority_ < 1 && conditional_expr_0(runtime_, level_ + 1)) {
        result_ = expr(runtime_, level_, 1)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.CONDITIONAL_EXPR, result_, true, null)
      }
      else if (priority_ < 2 && runtime_.consumeTokenSmart("+")) {
        result_ = expr(runtime_, level_, 2)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.PLUS_EXPR, result_, true, null)
      }
      else if (priority_ < 2 && runtime_.consumeTokenSmart("-")) {
        result_ = expr(runtime_, level_, 2)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.MINUS_EXPR, result_, true, null)
      }
      else if (priority_ < 3 && runtime_.consumeTokenSmart("^")) {
        result_ = expr(runtime_, level_, 3)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.XOR_EXPR, result_, true, null)
      }
      else if (priority_ < 3 && runtime_.consumeTokenSmart(ExpressionSyntaxTypes.BETWEEN)) {
        result_ = runtime_.report_error_(expr(runtime_, level_, 1))
        result_ = between_expr_1(runtime_, level_ + 1) && result_
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.BETWEEN_EXPR, result_, true, null)
      }
      else if (priority_ < 3 && runtime_.parseTokensSmart(0, ExpressionSyntaxTypes.IS, ExpressionSyntaxTypes.NOT)) {
        result_ = expr(runtime_, level_, 3)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.IS_NOT_EXPR, result_, true, null)
      }
      else if (priority_ < 4 && runtime_.consumeTokenSmart("*")) {
        result_ = expr(runtime_, level_, 4)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.MUL_EXPR, result_, true, null)
      }
      else if (priority_ < 4 && runtime_.consumeTokenSmart("/")) {
        result_ = expr(runtime_, level_, 4)
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.DIV_EXPR, result_, true, null)
      }
      else if (priority_ < 7 && runtime_.consumeTokenSmart("!")) {
        result_ = true
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.FACTORIAL_EXPR, result_, true, null)
      }
      else if (priority_ < 6 && runtime_.consumeTokenSmart("**")) {
        while (true) {
          result_ = runtime_.report_error_(expr(runtime_, level_, 6))
          if (!runtime_.consumeTokenSmart("**")) break
        }
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.EXP_EXPR, result_, true, null)
      }
      else if (priority_ < 8 && runtime_.leftMarkerIs(ExpressionSyntaxTypes.REF_EXPR) && arg_list(runtime_, level_ + 1)) {
        result_ = true
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.CALL_EXPR, result_, true, null)
      }
      else if (priority_ < 9 && qualification_expr_0(runtime_, level_ + 1)) {
        result_ = true
        runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.REF_EXPR, result_, true, null)
      }
      else {
        runtime_.exit_section_(level_, marker_, null, false, false, null)
        break
      }
    }
    return result_
  }

  // ':' expr
  private fun elvis_expr_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "elvis_expr_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(":")
    result_ = result_ && expr(runtime_, level_ + 1, -1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // '<' | '>' | '<=' | '>=' | '==' | '!='
  private fun conditional_expr_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "conditional_expr_0")) return false
    var result_: Boolean
    result_ = runtime_.consumeTokenSmart("<")
    if (!result_) result_ = runtime_.consumeTokenSmart(">")
    if (!result_) result_ = runtime_.consumeTokenSmart("<=")
    if (!result_) result_ = runtime_.consumeTokenSmart(">=")
    if (!result_) result_ = runtime_.consumeTokenSmart("==")
    if (!result_) result_ = runtime_.consumeTokenSmart("!=")
    return result_
  }

  fun unary_plus_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "unary_plus_expr")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, null)
    result_ = runtime_.consumeTokenSmart("+")
    pinned_ = result_
    result_ = pinned_ && expr(runtime_, level_, 5)
    runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.UNARY_PLUS_EXPR, result_, pinned_, null)
    return result_ || pinned_
  }

  fun unary_min_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "unary_min_expr")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, null)
    result_ = runtime_.consumeTokenSmart("-")
    pinned_ = result_
    result_ = pinned_ && expr(runtime_, level_, 5)
    runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.UNARY_MIN_EXPR, result_, pinned_, null)
    return result_ || pinned_
  }

  // AND add_group
  private fun between_expr_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "between_expr_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(ExpressionSyntaxTypes.AND)
    result_ = result_ && expr(runtime_, level_ + 1, 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  fun unary_not_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "unary_not_expr")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, null)
    result_ = runtime_.consumeTokenSmart("!")
    pinned_ = result_
    result_ = pinned_ && expr(runtime_, level_, 5)
    runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.UNARY_NOT_EXPR, result_, pinned_, null)
    return result_ || pinned_
  }

  // '.' identifier
  private fun qualification_expr_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "qualification_expr_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokenSmart(".")
    result_ = result_ && identifier(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // identifier
  fun simple_ref_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "simple_ref_expr")) return false
    if (!runtime_.nextTokenIsSmart(ExpressionSyntaxTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = identifier(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, ExpressionSyntaxTypes.REF_EXPR, result_)
    return result_
  }

  // number
  fun literal_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "literal_expr")) return false
    if (!runtime_.nextTokenIsSmart(ExpressionSyntaxTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokenSmart(ExpressionSyntaxTypes.NUMBER)
    runtime_.exit_section_(marker_, ExpressionSyntaxTypes.LITERAL_EXPR, result_)
    return result_
  }

  fun paren_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "paren_expr")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, null)
    result_ = runtime_.consumeTokenSmart("(")
    pinned_ = result_
    result_ = pinned_ && expr(runtime_, level_, -1)
    result_ = pinned_ && runtime_.report_error_(runtime_.consumeToken(")")) && result_
    runtime_.exit_section_(level_, marker_, ExpressionSyntaxTypes.PAREN_EXPR, result_, pinned_, null)
    return result_ || pinned_
  }

}