// ---- ExpressionParser.java -----------------
//header.txt
package org.intellij.grammar.expression;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExpressionParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("org.intellij.grammar.expression.ExpressionParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    if (root_ == ARG_LIST) {
      result_ = arg_list(builder_, level_ + 1);
    }
    else if (root_ == ASSIGN_EXPR) {
      result_ = expr(builder_, level_ + 1, -1);
    }
    else if (root_ == CALL_EXPR) {
      result_ = expr(builder_, level_ + 1, 6);
    }
    else if (root_ == CONDITIONAL_EXPR) {
      result_ = expr(builder_, level_ + 1, 0);
    }
    else if (root_ == DIV_EXPR) {
      result_ = expr(builder_, level_ + 1, 2);
    }
    else if (root_ == EXP_EXPR) {
      result_ = expr(builder_, level_ + 1, 4);
    }
    else if (root_ == EXPR) {
      result_ = expr(builder_, level_ + 1, -1);
    }
    else if (root_ == FACTORIAL_EXPR) {
      result_ = expr(builder_, level_ + 1, 5);
    }
    else if (root_ == IDENTIFIER) {
      result_ = identifier(builder_, level_ + 1);
    }
    else if (root_ == LITERAL_EXPR) {
      result_ = literal_expr(builder_, level_ + 1);
    }
    else if (root_ == MINUS_EXPR) {
      result_ = expr(builder_, level_ + 1, 1);
    }
    else if (root_ == MUL_EXPR) {
      result_ = expr(builder_, level_ + 1, 2);
    }
    else if (root_ == PAREN_EXPR) {
      result_ = paren_expr(builder_, level_ + 1);
    }
    else if (root_ == PLUS_EXPR) {
      result_ = expr(builder_, level_ + 1, 1);
    }
    else if (root_ == REF_EXPR) {
      result_ = expr(builder_, level_ + 1, 7);
    }
    else if (root_ == SPECIAL_EXPR) {
      result_ = special_expr(builder_, level_ + 1);
    }
    else if (root_ == UNARY_MIN_EXPR) {
      result_ = unary_min_expr(builder_, level_ + 1);
    }
    else if (root_ == UNARY_PLUS_EXPR) {
      result_ = unary_plus_expr(builder_, level_ + 1);
    }
    else {
      Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
      result_ = parse_root_(root_, builder_, level_);
      exit_section_(builder_, level_, marker_, root_, result_, true, TOKEN_ADVANCER);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return root(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ASSIGN_EXPR, CALL_EXPR, CONDITIONAL_EXPR, DIV_EXPR,
      EXPR, EXP_EXPR, FACTORIAL_EXPR, LITERAL_EXPR,
      MINUS_EXPR, MUL_EXPR, PAREN_EXPR, PLUS_EXPR,
      REF_EXPR, SPECIAL_EXPR, UNARY_MIN_EXPR, UNARY_PLUS_EXPR),
  };

  /* ********************************************************** */
  // '(' [ expr  (',' expr) * ] ')'
  public static boolean arg_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<arg list>");
    result_ = consumeToken(builder_, "(");
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, arg_list_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, ")") && result_;
    exit_section_(builder_, level_, marker_, ARG_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // [ expr  (',' expr) * ]
  private static boolean arg_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1")) return false;
    arg_list_1_0(builder_, level_ + 1);
    return true;
  }

  // expr  (',' expr) *
  private static boolean arg_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = expr(builder_, level_ + 1, -1);
    pinned_ = result_; // pin = 1
    result_ = result_ && arg_list_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' expr) *
  private static boolean arg_list_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1_0_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!arg_list_1_0_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "arg_list_1_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ',' expr
  private static boolean arg_list_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1_0_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // expr
  static boolean element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, null, result_, false, element_recover_parser_);
    return result_;
  }

  /* ********************************************************** */
  // element ';'
  static boolean element_and_separator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element_and_separator")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = element(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, ";");
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // !';'
  static boolean element_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element_recover")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !consumeToken(builder_, ";");
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, IDENTIFIER, result_);
    return result_;
  }

  /* ********************************************************** */
  // element_and_separator *
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!element_and_separator(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "root");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY(assign_expr)
  // 1: BINARY(conditional_expr)
  // 2: BINARY(plus_expr) BINARY(minus_expr)
  // 3: BINARY(mul_expr) BINARY(div_expr)
  // 4: PREFIX(unary_plus_expr) PREFIX(unary_min_expr)
  // 5: N_ARY(exp_expr)
  // 6: POSTFIX(factorial_expr)
  // 7: POSTFIX(call_expr)
  // 8: POSTFIX(ref_expr)
  // 9: ATOM(special_expr) ATOM(simple_ref_expr) ATOM(literal_expr) PREFIX(paren_expr)
  public static boolean expr(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    addVariant(builder_, "<expr>");
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expr>");
    result_ = unary_plus_expr(builder_, level_ + 1);
    if (!result_) result_ = unary_min_expr(builder_, level_ + 1);
    if (!result_) result_ = special_expr(builder_, level_ + 1);
    if (!result_) result_ = simple_ref_expr(builder_, level_ + 1);
    if (!result_) result_ = literal_expr(builder_, level_ + 1);
    if (!result_) result_ = paren_expr(builder_, level_ + 1);
    pinned_ = result_;
    result_ = result_ && expr_0(builder_, level_ + 1, priority_);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  public static boolean expr_0(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr_0")) return false;
    boolean result_ = true;
    while (true) {
      Marker left_marker_ = (Marker) builder_.getLatestDoneMarker();
      if (!invalid_left_marker_guard_(builder_, left_marker_, "expr_0")) return false;
      Marker marker_ = builder_.mark();
      if (priority_ < 0 && consumeTokenFast(builder_, "=")) {
        result_ = report_error_(builder_, expr(builder_, level_, -1));
        marker_.drop();
        left_marker_.precede().done(ASSIGN_EXPR);
      }
      else if (priority_ < 1 && consumeTokenFast(builder_, "?")) {
        result_ = report_error_(builder_, expr(builder_, level_, 1));
        result_ = conditional_expr_1(builder_, level_ + 1) && result_;
        marker_.drop();
        left_marker_.precede().done(CONDITIONAL_EXPR);
      }
      else if (priority_ < 2 && consumeTokenFast(builder_, "+")) {
        result_ = report_error_(builder_, expr(builder_, level_, 2));
        marker_.drop();
        left_marker_.precede().done(PLUS_EXPR);
      }
      else if (priority_ < 2 && consumeTokenFast(builder_, "-")) {
        result_ = report_error_(builder_, expr(builder_, level_, 2));
        marker_.drop();
        left_marker_.precede().done(MINUS_EXPR);
      }
      else if (priority_ < 3 && consumeTokenFast(builder_, "*")) {
        result_ = report_error_(builder_, expr(builder_, level_, 3));
        marker_.drop();
        left_marker_.precede().done(MUL_EXPR);
      }
      else if (priority_ < 3 && consumeTokenFast(builder_, "/")) {
        result_ = report_error_(builder_, expr(builder_, level_, 3));
        marker_.drop();
        left_marker_.precede().done(DIV_EXPR);
      }
      else if (priority_ < 5 && consumeTokenFast(builder_, "^")) {
        while (true) {
          result_ = report_error_(builder_, expr(builder_, level_, 5));
          if (!consumeTokenFast(builder_, "^")) break;
        }
        marker_.drop();
        left_marker_.precede().done(EXP_EXPR);
      }
      else if (priority_ < 6 && consumeTokenFast(builder_, "!")) {
        result_ = true;
        marker_.drop();
        left_marker_.precede().done(FACTORIAL_EXPR);
      }
      else if (priority_ < 7 && ((LighterASTNode)left_marker_).getTokenType() == REF_EXPR && arg_list(builder_, level_ + 1)) {
        result_ = true;
        marker_.drop();
        left_marker_.precede().done(CALL_EXPR);
      }
      else if (priority_ < 8 && ref_expr_0(builder_, level_ + 1)) {
        result_ = true;
        marker_.drop();
        left_marker_.precede().done(REF_EXPR);
      }
      else {
        exit_section_(builder_, marker_, null, false);
        break;
      }
    }
    return result_;
  }

  // ':' expr
  private static boolean conditional_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conditional_expr_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenFast(builder_, ":");
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  public static boolean unary_plus_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_plus_expr")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeTokenFast(builder_, "+");
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, 4) && result_;
    if (result_ || pinned_) {
      marker_.done(UNARY_PLUS_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  public static boolean unary_min_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_min_expr")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeTokenFast(builder_, "-");
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, 4) && result_;
    if (result_ || pinned_) {
      marker_.done(UNARY_MIN_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // '.' identifier
  private static boolean ref_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ref_expr_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenFast(builder_, ".");
    result_ = result_ && identifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // 'multiply' '(' simple_ref_expr ',' mul_expr ')'
  public static boolean special_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "special_expr")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<special expr>");
    result_ = consumeTokenFast(builder_, "multiply");
    result_ = result_ && consumeToken(builder_, "(");
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, simple_ref_expr(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, ",")) && result_;
    result_ = pinned_ && report_error_(builder_, expr(builder_, level_ + 1, 2)) && result_;
    result_ = pinned_ && consumeToken(builder_, ")") && result_;
    exit_section_(builder_, level_, marker_, SPECIAL_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // identifier
  public static boolean simple_ref_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_ref_expr")) return false;
    if (!nextTokenIsFast(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = identifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, REF_EXPR, result_);
    return result_;
  }

  // number
  public static boolean literal_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expr")) return false;
    if (!nextTokenIsFast(builder_, NUMBER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenFast(builder_, NUMBER);
    exit_section_(builder_, marker_, LITERAL_EXPR, result_);
    return result_;
  }

  public static boolean paren_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expr")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeTokenFast(builder_, "(");
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, -1) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, ")")) && result_;
    if (result_ || pinned_) {
      marker_.done(PAREN_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  final static Parser element_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return element_recover(builder_, level_ + 1);
    }
  };
}