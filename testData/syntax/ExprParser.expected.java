// ---- org/intellij/grammar/expression/ExpressionParser.java -----------------
//header.txt
package org.intellij.grammar.expression;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import static com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntimeKt.*;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NONE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._COLLAPSE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_INNER_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._AND_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NOT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._UPPER_;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.parser.ProductionResult;
import com.intellij.platform.syntax.SyntaxElementTypeSet;
import static com.intellij.platform.syntax.parser.ProductionResultKt.prepareProduction;
import kotlin.jvm.functions.Function2;
import kotlin.Unit;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExpressionParser {

  public ProductionResult parse(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    parseLight(root_, runtime_);
    return prepareProduction(runtime_.getSyntaxBuilder());
  }

  public void parseLight(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    boolean result_;
    Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit> parse_ = new Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit>(){
      @Override
      public Unit invoke(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
        parseLight(root_, runtime_);
        return Unit.INSTANCE;
      }
    };

    runtime_.init(parse_, EXTENDS_SETS_);
    Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_, int level_) {
    boolean result_;
    if (root_ == EXPR) {
      result_ = expr(runtime_, level_ + 1, -1);
    }
    else {
      result_ = root(runtime_, level_ + 1);
    }
    return result_;
  }

  public static final SyntaxElementTypeSet[] EXTENDS_SETS_ = new SyntaxElementTypeSet[] {
    create_token_set_(ASSIGN_EXPR, BETWEEN_EXPR, CALL_EXPR, CONDITIONAL_EXPR,
      DIV_EXPR, ELVIS_EXPR, EXPR, EXP_EXPR,
      FACTORIAL_EXPR, IS_NOT_EXPR, LITERAL_EXPR, MINUS_EXPR,
      MUL_EXPR, PAREN_EXPR, PLUS_EXPR, REF_EXPR,
      SPECIAL_EXPR, UNARY_MIN_EXPR, UNARY_NOT_EXPR, UNARY_PLUS_EXPR,
      XOR_EXPR),
  };

  /* ********************************************************** */
  // '(' [ !')' expr  (',' expr) * ] ')'
  public static boolean arg_list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "arg_list")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ARG_LIST, "<arg list>");
    result_ = consumeToken(runtime_, "(");
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, arg_list_1(runtime_, level_ + 1));
    result_ = pinned_ && consumeToken(runtime_, ")") && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // [ !')' expr  (',' expr) * ]
  private static boolean arg_list_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "arg_list_1")) return false;
    arg_list_1_0(runtime_, level_ + 1);
    return true;
  }

  // !')' expr  (',' expr) *
  private static boolean arg_list_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "arg_list_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = arg_list_1_0_0(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, expr(runtime_, level_ + 1, -1));
    result_ = pinned_ && arg_list_1_0_2(runtime_, level_ + 1) && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // !')'
  private static boolean arg_list_1_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "arg_list_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !consumeToken(runtime_, ")");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // (',' expr) *
  private static boolean arg_list_1_0_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "arg_list_1_0_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!arg_list_1_0_2_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "arg_list_1_0_2", pos_)) break;
    }
    return true;
  }

  // ',' expr
  private static boolean arg_list_1_0_2_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "arg_list_1_0_2_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && expr(runtime_, level_ + 1, -1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // expr ';'?
  static boolean element(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = expr(runtime_, level_ + 1, -1);
    result_ = result_ && element_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, ExpressionParser::element_recover);
    return result_;
  }

  // ';'?
  private static boolean element_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element_1")) return false;
    consumeToken(runtime_, ";");
    return true;
  }

  /* ********************************************************** */
  // !('(' | '+' | '-' | '!' | 'multiply' | id | number)
  static boolean element_recover(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element_recover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !element_recover_0(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // '(' | '+' | '-' | '!' | 'multiply' | id | number
  private static boolean element_recover_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element_recover_0")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, "(");
    if (!result_) result_ = consumeToken(runtime_, "+");
    if (!result_) result_ = consumeToken(runtime_, "-");
    if (!result_) result_ = consumeToken(runtime_, "!");
    if (!result_) result_ = consumeToken(runtime_, "multiply");
    if (!result_) result_ = consumeToken(runtime_, ID);
    if (!result_) result_ = consumeToken(runtime_, NUMBER);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "identifier")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, marker_, IDENTIFIER, result_);
    return result_;
  }

  /* ********************************************************** */
  // 'multiply' '(' simple_ref_expr ',' mul_expr ')'
  public static boolean meta_special_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "meta_special_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, SPECIAL_EXPR, "<meta special expr>");
    result_ = consumeToken(runtime_, "multiply");
    result_ = result_ && consumeToken(runtime_, "(");
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(runtime_, simple_ref_expr(runtime_, level_ + 1));
    result_ = pinned_ && report_error_(runtime_, consumeToken(runtime_, ",")) && result_;
    result_ = pinned_ && report_error_(runtime_, expr(runtime_, level_ + 1, 3)) && result_;
    result_ = pinned_ && consumeToken(runtime_, ")") && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // element *
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!element(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "root", pos_)) break;
    }
    return true;
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
  public static boolean expr(SyntaxGeneratedParserRuntime runtime_, int level_, int priority_) {
    if (!recursion_guard_(runtime_, level_, "expr")) return false;
    addVariant(runtime_, "<expr>");
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, "<expr>");
    result_ = unary_plus_expr(runtime_, level_ + 1);
    if (!result_) result_ = unary_min_expr(runtime_, level_ + 1);
    if (!result_) result_ = unary_not_expr(runtime_, level_ + 1);
    if (!result_) result_ = meta_special_expr(runtime_, level_ + 1);
    if (!result_) result_ = simple_ref_expr(runtime_, level_ + 1);
    if (!result_) result_ = literal_expr(runtime_, level_ + 1);
    if (!result_) result_ = paren_expr(runtime_, level_ + 1);
    pinned_ = result_;
    result_ = result_ && expr_0(runtime_, level_ + 1, priority_);
    exit_section_(runtime_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  public static boolean expr_0(SyntaxGeneratedParserRuntime runtime_, int level_, int priority_) {
    if (!recursion_guard_(runtime_, level_, "expr_0")) return false;
    boolean result_ = true;
    while (true) {
      Marker marker_ = enter_section_(runtime_, level_, _LEFT_, null);
      if (priority_ < 0 && consumeTokenSmart(runtime_, "=")) {
        result_ = expr(runtime_, level_, -1);
        exit_section_(runtime_, level_, marker_, ASSIGN_EXPR, result_, true, null);
      }
      else if (priority_ < 1 && consumeTokenSmart(runtime_, "?")) {
        result_ = report_error_(runtime_, expr(runtime_, level_, 1));
        result_ = elvis_expr_1(runtime_, level_ + 1) && result_;
        exit_section_(runtime_, level_, marker_, ELVIS_EXPR, result_, true, null);
      }
      else if (priority_ < 1 && conditional_expr_0(runtime_, level_ + 1)) {
        result_ = expr(runtime_, level_, 1);
        exit_section_(runtime_, level_, marker_, CONDITIONAL_EXPR, result_, true, null);
      }
      else if (priority_ < 2 && consumeTokenSmart(runtime_, "+")) {
        result_ = expr(runtime_, level_, 2);
        exit_section_(runtime_, level_, marker_, PLUS_EXPR, result_, true, null);
      }
      else if (priority_ < 2 && consumeTokenSmart(runtime_, "-")) {
        result_ = expr(runtime_, level_, 2);
        exit_section_(runtime_, level_, marker_, MINUS_EXPR, result_, true, null);
      }
      else if (priority_ < 3 && consumeTokenSmart(runtime_, "^")) {
        result_ = expr(runtime_, level_, 3);
        exit_section_(runtime_, level_, marker_, XOR_EXPR, result_, true, null);
      }
      else if (priority_ < 3 && consumeTokenSmart(runtime_, BETWEEN)) {
        result_ = report_error_(runtime_, expr(runtime_, level_, 1));
        result_ = between_expr_1(runtime_, level_ + 1) && result_;
        exit_section_(runtime_, level_, marker_, BETWEEN_EXPR, result_, true, null);
      }
      else if (priority_ < 3 && parseTokensSmart(runtime_, 0, IS, NOT)) {
        result_ = expr(runtime_, level_, 3);
        exit_section_(runtime_, level_, marker_, IS_NOT_EXPR, result_, true, null);
      }
      else if (priority_ < 4 && consumeTokenSmart(runtime_, "*")) {
        result_ = expr(runtime_, level_, 4);
        exit_section_(runtime_, level_, marker_, MUL_EXPR, result_, true, null);
      }
      else if (priority_ < 4 && consumeTokenSmart(runtime_, "/")) {
        result_ = expr(runtime_, level_, 4);
        exit_section_(runtime_, level_, marker_, DIV_EXPR, result_, true, null);
      }
      else if (priority_ < 7 && consumeTokenSmart(runtime_, "!")) {
        result_ = true;
        exit_section_(runtime_, level_, marker_, FACTORIAL_EXPR, result_, true, null);
      }
      else if (priority_ < 6 && consumeTokenSmart(runtime_, "**")) {
        while (true) {
          result_ = report_error_(runtime_, expr(runtime_, level_, 6));
          if (!consumeTokenSmart(runtime_, "**")) break;
        }
        exit_section_(runtime_, level_, marker_, EXP_EXPR, result_, true, null);
      }
      else if (priority_ < 8 && leftMarkerIs(runtime_, REF_EXPR) && arg_list(runtime_, level_ + 1)) {
        result_ = true;
        exit_section_(runtime_, level_, marker_, CALL_EXPR, result_, true, null);
      }
      else if (priority_ < 9 && qualification_expr_0(runtime_, level_ + 1)) {
        result_ = true;
        exit_section_(runtime_, level_, marker_, REF_EXPR, result_, true, null);
      }
      else {
        exit_section_(runtime_, level_, marker_, null, false, false, null);
        break;
      }
    }
    return result_;
  }

  // ':' expr
  private static boolean elvis_expr_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "elvis_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ":");
    result_ = result_ && expr(runtime_, level_ + 1, -1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // '<' | '>' | '<=' | '>=' | '==' | '!='
  private static boolean conditional_expr_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "conditional_expr_0")) return false;
    boolean result_;
    result_ = consumeTokenSmart(runtime_, "<");
    if (!result_) result_ = consumeTokenSmart(runtime_, ">");
    if (!result_) result_ = consumeTokenSmart(runtime_, "<=");
    if (!result_) result_ = consumeTokenSmart(runtime_, ">=");
    if (!result_) result_ = consumeTokenSmart(runtime_, "==");
    if (!result_) result_ = consumeTokenSmart(runtime_, "!=");
    return result_;
  }

  public static boolean unary_plus_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "unary_plus_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, null);
    result_ = consumeTokenSmart(runtime_, "+");
    pinned_ = result_;
    result_ = pinned_ && expr(runtime_, level_, 5);
    exit_section_(runtime_, level_, marker_, UNARY_PLUS_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  public static boolean unary_min_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "unary_min_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, null);
    result_ = consumeTokenSmart(runtime_, "-");
    pinned_ = result_;
    result_ = pinned_ && expr(runtime_, level_, 5);
    exit_section_(runtime_, level_, marker_, UNARY_MIN_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // AND add_group
  private static boolean between_expr_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "between_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, AND);
    result_ = result_ && expr(runtime_, level_ + 1, 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  public static boolean unary_not_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "unary_not_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, null);
    result_ = consumeTokenSmart(runtime_, "!");
    pinned_ = result_;
    result_ = pinned_ && expr(runtime_, level_, 5);
    exit_section_(runtime_, level_, marker_, UNARY_NOT_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // '.' identifier
  private static boolean qualification_expr_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "qualification_expr_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokenSmart(runtime_, ".");
    result_ = result_ && identifier(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // identifier
  public static boolean simple_ref_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "simple_ref_expr")) return false;
    if (!nextTokenIsSmart(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = identifier(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REF_EXPR, result_);
    return result_;
  }

  // number
  public static boolean literal_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "literal_expr")) return false;
    if (!nextTokenIsSmart(runtime_, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokenSmart(runtime_, NUMBER);
    exit_section_(runtime_, marker_, LITERAL_EXPR, result_);
    return result_;
  }

  public static boolean paren_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "paren_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, null);
    result_ = consumeTokenSmart(runtime_, "(");
    pinned_ = result_;
    result_ = pinned_ && expr(runtime_, level_, -1);
    result_ = pinned_ && report_error_(runtime_, consumeToken(runtime_, ")")) && result_;
    exit_section_(runtime_, level_, marker_, PAREN_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

}