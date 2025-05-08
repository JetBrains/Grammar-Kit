// ---- org/intellij/grammar/expression/ExpressionParser.kt -----------------
//header.txt
package org.intellij.grammar.expression

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class ExpressionParser(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: SyntaxElementType, builder_: SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      var result_: Boolean
      if (root_ == ExpressionSyntaxTypes.EXPR) {
        result_ = expr(builder_, level_ + 1, -1)
      }
      else {
        result_ = root(builder_, level_ + 1)
      }
      return result_
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(ExpressionSyntaxTypes.ASSIGN_EXPR, ExpressionSyntaxTypes.BETWEEN_EXPR, ExpressionSyntaxTypes.CALL_EXPR, ExpressionSyntaxTypes.CONDITIONAL_EXPR,
        ExpressionSyntaxTypes.DIV_EXPR, ExpressionSyntaxTypes.ELVIS_EXPR, ExpressionSyntaxTypes.EXPR, ExpressionSyntaxTypes.EXP_EXPR,
        ExpressionSyntaxTypes.FACTORIAL_EXPR, ExpressionSyntaxTypes.IS_NOT_EXPR, ExpressionSyntaxTypes.LITERAL_EXPR, ExpressionSyntaxTypes.MINUS_EXPR,
        ExpressionSyntaxTypes.MUL_EXPR, ExpressionSyntaxTypes.PAREN_EXPR, ExpressionSyntaxTypes.PLUS_EXPR, ExpressionSyntaxTypes.REF_EXPR,
        ExpressionSyntaxTypes.SPECIAL_EXPR, ExpressionSyntaxTypes.UNARY_MIN_EXPR, ExpressionSyntaxTypes.UNARY_NOT_EXPR, ExpressionSyntaxTypes.UNARY_PLUS_EXPR,
        ExpressionSyntaxTypes.XOR_EXPR),
    )

    /* ********************************************************** */
    // '(' [ !')' expr  (',' expr) * ] ')'
    fun arg_list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "arg_list")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, ExpressionSyntaxTypes.ARG_LIST, "<arg list>")
      result_ = consumeToken(builder_, "(")
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, arg_list_1(builder_, level_ + 1))
      result_ = pinned_ && consumeToken(builder_, ")") && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // [ !')' expr  (',' expr) * ]
    private fun arg_list_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "arg_list_1")) return false
      arg_list_1_0(builder_, level_ + 1)
      return true
    }

    // !')' expr  (',' expr) *
    private fun arg_list_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "arg_list_1_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = arg_list_1_0_0(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, expr(builder_, level_ + 1, -1))
      result_ = pinned_ && arg_list_1_0_2(builder_, level_ + 1) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // !')'
    private fun arg_list_1_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "arg_list_1_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !consumeToken(builder_, ")")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' expr) *
    private fun arg_list_1_0_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "arg_list_1_0_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!arg_list_1_0_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "arg_list_1_0_2", pos_)) break
      }
      return true
    }

    // ',' expr
    private fun arg_list_1_0_2_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "arg_list_1_0_2_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, ",")
      pinned_ = result_ // pin = 1
      result_ = result_ && expr(builder_, level_ + 1, -1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // expr ';'?
    internal fun element(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = expr(builder_, level_ + 1, -1)
      result_ = result_ && element_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, ExpressionParser::element_recover)
      return result_
    }

    // ';'?
    private fun element_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element_1")) return false
      consumeToken(builder_, ";")
      return true
    }

    /* ********************************************************** */
    // !('(' | '+' | '-' | '!' | 'multiply' | id | number)
    internal fun element_recover(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element_recover")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !element_recover_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // '(' | '+' | '-' | '!' | 'multiply' | id | number
    private fun element_recover_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element_recover_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, "(")
      if (!result_) result_ = consumeToken(builder_, "+")
      if (!result_) result_ = consumeToken(builder_, "-")
      if (!result_) result_ = consumeToken(builder_, "!")
      if (!result_) result_ = consumeToken(builder_, "multiply")
      if (!result_) result_ = consumeToken(builder_, ExpressionSyntaxTypes.ID)
      if (!result_) result_ = consumeToken(builder_, ExpressionSyntaxTypes.NUMBER)
      return result_
    }

    /* ********************************************************** */
    // id
    fun identifier(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "identifier")) return false
      if (!nextTokenIs(builder_, ExpressionSyntaxTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ExpressionSyntaxTypes.ID)
      exit_section_(builder_, marker_, ExpressionSyntaxTypes.IDENTIFIER, result_)
      return result_
    }

    /* ********************************************************** */
    // 'multiply' '(' simple_ref_expr ',' mul_expr ')'
    fun meta_special_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_special_expr")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, ExpressionSyntaxTypes.SPECIAL_EXPR, "<meta special expr>")
      result_ = consumeToken(builder_, "multiply")
      result_ = result_ && consumeToken(builder_, "(")
      pinned_ = result_ // pin = 2
      result_ = result_ && report_error_(builder_, simple_ref_expr(builder_, level_ + 1))
      result_ = pinned_ && report_error_(builder_, consumeToken(builder_, ",")) && result_
      result_ = pinned_ && report_error_(builder_, expr(builder_, level_ + 1, 3)) && result_
      result_ = pinned_ && consumeToken(builder_, ")") && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // element *
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!element(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "root", pos_)) break
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
    fun expr(builder_: SyntaxTreeBuilder, level_: Int, priority_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr")) return false
      addVariant(builder_, "<expr>")
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, "<expr>")
      result_ = unary_plus_expr(builder_, level_ + 1)
      if (!result_) result_ = unary_min_expr(builder_, level_ + 1)
      if (!result_) result_ = unary_not_expr(builder_, level_ + 1)
      if (!result_) result_ = meta_special_expr(builder_, level_ + 1)
      if (!result_) result_ = simple_ref_expr(builder_, level_ + 1)
      if (!result_) result_ = literal_expr(builder_, level_ + 1)
      if (!result_) result_ = paren_expr(builder_, level_ + 1)
      pinned_ = result_
      result_ = result_ && expr_0(builder_, level_ + 1, priority_)
      exit_section_(builder_, level_, marker_, null, result_, pinned_, null)
      return result_ || pinned_
    }

    fun expr_0(SyntaxTreeBuilder: builder_, level_: Int, priority_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr_0")) return false
      var result_ = true
      while (true) {
        val marker_: Marker = enter_section_(builder_, level_, _LEFT_, null)
        if (priority_ < 0 && consumeTokenSmart(builder_, "=")) {
          result_ = expr(builder_, level_, -1)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.ASSIGN_EXPR, result_, true, null)
        }
        else if (priority_ < 1 && consumeTokenSmart(builder_, "?")) {
          result_ = report_error_(builder_, expr(builder_, level_, 1))
          result_ = elvis_expr_1(builder_, level_ + 1) && result_
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.ELVIS_EXPR, result_, true, null)
        }
        else if (priority_ < 1 && conditional_expr_0(builder_, level_ + 1)) {
          result_ = expr(builder_, level_, 1)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.CONDITIONAL_EXPR, result_, true, null)
        }
        else if (priority_ < 2 && consumeTokenSmart(builder_, "+")) {
          result_ = expr(builder_, level_, 2)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.PLUS_EXPR, result_, true, null)
        }
        else if (priority_ < 2 && consumeTokenSmart(builder_, "-")) {
          result_ = expr(builder_, level_, 2)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.MINUS_EXPR, result_, true, null)
        }
        else if (priority_ < 3 && consumeTokenSmart(builder_, "^")) {
          result_ = expr(builder_, level_, 3)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.XOR_EXPR, result_, true, null)
        }
        else if (priority_ < 3 && consumeTokenSmart(builder_, ExpressionSyntaxTypes.BETWEEN)) {
          result_ = report_error_(builder_, expr(builder_, level_, 1))
          result_ = between_expr_1(builder_, level_ + 1) && result_
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.BETWEEN_EXPR, result_, true, null)
        }
        else if (priority_ < 3 && parseTokensSmart(builder_, 0, ExpressionSyntaxTypes.IS, ExpressionSyntaxTypes.NOT)) {
          result_ = expr(builder_, level_, 3)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.IS_NOT_EXPR, result_, true, null)
        }
        else if (priority_ < 4 && consumeTokenSmart(builder_, "*")) {
          result_ = expr(builder_, level_, 4)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.MUL_EXPR, result_, true, null)
        }
        else if (priority_ < 4 && consumeTokenSmart(builder_, "/")) {
          result_ = expr(builder_, level_, 4)
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.DIV_EXPR, result_, true, null)
        }
        else if (priority_ < 7 && consumeTokenSmart(builder_, "!")) {
          result_ = true
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.FACTORIAL_EXPR, result_, true, null)
        }
        else if (priority_ < 6 && consumeTokenSmart(builder_, "**")) {
          while (true) {
            result_ = report_error_(builder_, expr(builder_, level_, 6))
            if (!consumeTokenSmart(builder_, "**")) break
          }
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.EXP_EXPR, result_, true, null)
        }
        else if (priority_ < 8 && leftMarkerIs(builder_, ExpressionSyntaxTypes.REF_EXPR) && arg_list(builder_, level_ + 1)) {
          result_ = true
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.CALL_EXPR, result_, true, null)
        }
        else if (priority_ < 9 && qualification_expr_0(builder_, level_ + 1)) {
          result_ = true
          exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.REF_EXPR, result_, true, null)
        }
        else {
          exit_section_(builder_, level_, marker_, null, false, false, null)
          break
        }
      }
      return result_
    }

    // ':' expr
    private fun elvis_expr_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "elvis_expr_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ":")
      result_ = result_ && expr(builder_, level_ + 1, -1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // '<' | '>' | '<=' | '>=' | '==' | '!='
    private fun conditional_expr_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "conditional_expr_0")) return false
      var result_: Boolean
      result_ = consumeTokenSmart(builder_, "<")
      if (!result_) result_ = consumeTokenSmart(builder_, ">")
      if (!result_) result_ = consumeTokenSmart(builder_, "<=")
      if (!result_) result_ = consumeTokenSmart(builder_, ">=")
      if (!result_) result_ = consumeTokenSmart(builder_, "==")
      if (!result_) result_ = consumeTokenSmart(builder_, "!=")
      return result_
    }

    fun unary_plus_expr(SyntaxTreeBuilder: builder_, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "unary_plus_expr")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, null)
      result_ = consumeTokenSmart(builder_, "+")
      pinned_ = result_
      result_ = pinned_ && expr(builder_, level_, 5)
      exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.UNARY_PLUS_EXPR, result_, pinned_, null)
      return result_ || pinned_
    }

    fun unary_min_expr(SyntaxTreeBuilder: builder_, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "unary_min_expr")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, null)
      result_ = consumeTokenSmart(builder_, "-")
      pinned_ = result_
      result_ = pinned_ && expr(builder_, level_, 5)
      exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.UNARY_MIN_EXPR, result_, pinned_, null)
      return result_ || pinned_
    }

    // AND add_group
    private fun between_expr_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "between_expr_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ExpressionSyntaxTypes.AND)
      result_ = result_ && expr(builder_, level_ + 1, 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    fun unary_not_expr(SyntaxTreeBuilder: builder_, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "unary_not_expr")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, null)
      result_ = consumeTokenSmart(builder_, "!")
      pinned_ = result_
      result_ = pinned_ && expr(builder_, level_, 5)
      exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.UNARY_NOT_EXPR, result_, pinned_, null)
      return result_ || pinned_
    }

    // '.' identifier
    private fun qualification_expr_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "qualification_expr_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokenSmart(builder_, ".")
      result_ = result_ && identifier(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // identifier
    fun simple_ref_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "simple_ref_expr")) return false
      if (!nextTokenIsSmart(builder_, ExpressionSyntaxTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = identifier(builder_, level_ + 1)
      exit_section_(builder_, marker_, ExpressionSyntaxTypes.REF_EXPR, result_)
      return result_
    }

    // number
    fun literal_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "literal_expr")) return false
      if (!nextTokenIsSmart(builder_, ExpressionSyntaxTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokenSmart(builder_, ExpressionSyntaxTypes.NUMBER)
      exit_section_(builder_, marker_, ExpressionSyntaxTypes.LITERAL_EXPR, result_)
      return result_
    }

    fun paren_expr(SyntaxTreeBuilder: builder_, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "paren_expr")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, null)
      result_ = consumeTokenSmart(builder_, "(")
      pinned_ = result_
      result_ = pinned_ && expr(builder_, level_, -1)
      result_ = pinned_ && report_error_(builder_, consumeToken(builder_, ")")) && result_
      exit_section_(builder_, level_, marker_, ExpressionSyntaxTypes.PAREN_EXPR, result_, pinned_, null)
      return result_ || pinned_
    }

  }
}