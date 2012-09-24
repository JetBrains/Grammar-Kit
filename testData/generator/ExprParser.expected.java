// ---- ExpressionParser.java -----------------
//header.txt
package org.intellij.grammar.expression;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExpressionParser implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("org.intellij.grammar.expression.ExpressionParser");

  @NotNull
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
    if (root_ == ARG_LIST) {
      result_ = arg_list(builder_, level_ + 1);
    }
    else if (root_ == ASSIGN_EXPR) {
      result_ = expr(builder_, level_ + 1, 0);
    }
    else if (root_ == CALL_EXPR) {
      result_ = expr(builder_, level_ + 1, 7);
    }
    else if (root_ == CONDITIONAL_EXPR) {
      result_ = expr(builder_, level_ + 1, 1);
    }
    else if (root_ == DIV_EXPR) {
      result_ = expr(builder_, level_ + 1, 3);
    }
    else if (root_ == EXP_EXPR) {
      result_ = expr(builder_, level_ + 1, 5);
    }
    else if (root_ == EXPR) {
      result_ = expr(builder_, level_ + 1, -1);
    }
    else if (root_ == FACTORIAL_EXPR) {
      result_ = expr(builder_, level_ + 1, 6);
    }
    else if (root_ == IDENTIFIER) {
      result_ = identifier(builder_, level_ + 1);
    }
    else if (root_ == LITERAL_EXPR) {
      result_ = literal_expr(builder_, level_ + 1);
    }
    else if (root_ == MINUS_EXPR) {
      result_ = expr(builder_, level_ + 1, 2);
    }
    else if (root_ == MUL_EXPR) {
      result_ = expr(builder_, level_ + 1, 3);
    }
    else if (root_ == PAREN_EXPR) {
      result_ = paren_expr(builder_, level_ + 1);
    }
    else if (root_ == PLUS_EXPR) {
      result_ = expr(builder_, level_ + 1, 2);
    }
    else if (root_ == REF_EXPR) {
      result_ = expr(builder_, level_ + 1, 8);
    }
    else if (root_ == UNARY_MIN_EXPR) {
      result_ = unary_min_expr(builder_, level_ + 1);
    }
    else if (root_ == UNARY_PLUS_EXPR) {
      result_ = unary_plus_expr(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      result_ = parse_root_(root_, builder_, level_);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return root(builder_, level_ + 1);
  }

  private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    TokenSet.create(ASSIGN_EXPR, CALL_EXPR, CONDITIONAL_EXPR, DIV_EXPR,
      EXPR, EXP_EXPR, FACTORIAL_EXPR, LITERAL_EXPR,
      MINUS_EXPR, MUL_EXPR, PAREN_EXPR, PLUS_EXPR,
      REF_EXPR, UNARY_MIN_EXPR, UNARY_PLUS_EXPR),
  };
  public static boolean type_extends_(IElementType child_, IElementType parent_) {
    for (TokenSet set : EXTENDS_SETS_) {
      if (set.contains(child_) && set.contains(parent_)) return true;
    }
    return false;
  }

  /* ********************************************************** */
  // '(' [ expr  (',' expr) * ] ')'
  public static boolean arg_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<arg list>");
    result_ = consumeToken(builder_, "(");
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, arg_list_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, ")") && result_;
    if (result_ || pinned_) {
      marker_.done(ARG_LIST);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
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
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = expr(builder_, level_ + 1, -1);
    pinned_ = result_; // pin = 1
    result_ = result_ && arg_list_1_0_1(builder_, level_ + 1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
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

  // (',' expr)
  private static boolean arg_list_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1_0_1_0")) return false;
    return arg_list_1_0_1_0_0(builder_, level_ + 1);
  }

  // ',' expr
  private static boolean arg_list_1_0_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "arg_list_1_0_1_0_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && expr(builder_, level_ + 1, -1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // expr
  static boolean element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
    result_ = expr(builder_, level_ + 1, -1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_RECOVER_, element_recover_parser_);
    return result_;
  }

  /* ********************************************************** */
  // !';'
  static boolean element_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element_recover")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !consumeToken(builder_, ";");
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ID);
    if (result_) {
      marker_.done(IDENTIFIER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // 'mul only' mul_expr
  static boolean only_mul_test(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "only_mul_test")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "mul only");
    result_ = result_ && expr(builder_, level_ + 1, 3);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // 'simple ref only' simple_ref_expr
  static boolean only_simple_ref_test(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "only_simple_ref_test")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "simple ref only");
    result_ = result_ && simple_ref_expr(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // (element ';') *
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!root_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "root");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (element ';')
  private static boolean root_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_0")) return false;
    return root_0_0(builder_, level_ + 1);
  }

  // element ';'
  private static boolean root_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = element(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ";");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY('=')
  // 1: BINARY('?' tail: ':' expr)
  // 2: BINARY('+') BINARY('-')
  // 3: BINARY('*') BINARY('/')
  // 4: UNARY('+') UNARY('-')
  // 5: N_ARY('^')
  // 6: UNARY_POSTFIX('!')
  // 7: UNARY_POSTFIX(arg_list)
  // 8: UNARY_POSTFIX('.' simple_ref_expr)
  // 9: ATOM(identifier) ATOM(number) UNARY('(' tail: ')')
  public static boolean expr(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    Marker marker_ = builder_.mark();
    boolean result_ = false;
    boolean pinned_ = false;
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<expr>");
    result_ = unary_plus_expr(builder_, level_ + 1);
    if (!result_) result_ = unary_min_expr(builder_, level_ + 1);
    if (!result_) result_ = simple_ref_expr(builder_, level_ + 1);
    if (!result_) result_ = literal_expr(builder_, level_ + 1);
    if (!result_) result_ = paren_expr(builder_, level_ + 1);
    pinned_ = result_;
    result_ = result_ && expr_0(builder_, level_ + 1, priority_);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  public static boolean expr_0(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr_0")) return false;
    boolean result_ = true;
    while (true) {
      Marker left_marker_ = (Marker) builder_.getLatestDoneMarker();
      if (!invalid_left_marker_guard_(builder_, left_marker_, "expr_0")) return false;
      Marker marker_ = builder_.mark();
      if (priority_ < 0 && consumeToken(builder_, "=")) {
        result_ = report_error_(builder_, expr(builder_, level_, -1));
        marker_.drop();
        left_marker_.precede().done(ASSIGN_EXPR);
      }
      else if (priority_ < 1 && consumeToken(builder_, "?")) {
        result_ = report_error_(builder_, expr(builder_, level_, 1));
        result_ = conditional_expr_1(builder_, level_ + 1) && result_;
        marker_.drop();
        left_marker_.precede().done(CONDITIONAL_EXPR);
      }
      else if (priority_ < 2 && consumeToken(builder_, "+")) {
        result_ = report_error_(builder_, expr(builder_, level_, 2));
        marker_.drop();
        left_marker_.precede().done(PLUS_EXPR);
      }
      else if (priority_ < 2 && consumeToken(builder_, "-")) {
        result_ = report_error_(builder_, expr(builder_, level_, 2));
        marker_.drop();
        left_marker_.precede().done(MINUS_EXPR);
      }
      else if (priority_ < 3 && consumeToken(builder_, "*")) {
        result_ = report_error_(builder_, expr(builder_, level_, 3));
        marker_.drop();
        left_marker_.precede().done(MUL_EXPR);
      }
      else if (priority_ < 3 && consumeToken(builder_, "/")) {
        result_ = report_error_(builder_, expr(builder_, level_, 3));
        marker_.drop();
        left_marker_.precede().done(DIV_EXPR);
      }
      else if (priority_ < 5 && consumeToken(builder_, "^")) {
        while (true) {
          result_ = report_error_(builder_, expr(builder_, level_, 5));
          if (!consumeToken(builder_, "^")) break;
        }
        marker_.drop();
        left_marker_.precede().done(EXP_EXPR);
      }
      else if (priority_ < 6 && consumeToken(builder_, "!")) {
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
        marker_.rollbackTo();
        break;
      }
    }
    return result_;
  }

  // ':' expr
  private static boolean conditional_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conditional_expr_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ":");
    result_ = result_ && expr(builder_, level_ + 1, -1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  public static boolean unary_plus_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_plus_expr")) return false;
    Marker marker_ = builder_.mark();
    boolean result_ = false;
    boolean pinned_ = false;
    result_ = consumeToken(builder_, "+");
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, 4) && result_;
    if (result_ || pinned_) {
      marker_.done(UNARY_PLUS_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_ || pinned_;
  }

  public static boolean unary_min_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_min_expr")) return false;
    Marker marker_ = builder_.mark();
    boolean result_ = false;
    boolean pinned_ = false;
    result_ = consumeToken(builder_, "-");
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, 4) && result_;
    if (result_ || pinned_) {
      marker_.done(UNARY_MIN_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_ || pinned_;
  }

  // '.' simple_ref_expr
  private static boolean ref_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ref_expr_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ".");
    result_ = result_ && simple_ref_expr(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // identifier
  public static boolean simple_ref_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_ref_expr")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = identifier(builder_, level_ + 1);
    if (result_) {
      marker_.done(REF_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // number
  public static boolean literal_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expr")) return false;
    if (!nextTokenIs(builder_, NUMBER)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, NUMBER);
    if (result_) {
      marker_.done(LITERAL_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  public static boolean paren_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expr")) return false;
    Marker marker_ = builder_.mark();
    boolean result_ = false;
    boolean pinned_ = false;
    result_ = consumeToken(builder_, "(");
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, -1) && result_;
    result_ = pinned_ && report_error_(builder_, consumeToken(builder_, ")")) && result_;
    if (result_ || pinned_) {
      marker_.done(PAREN_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_ || pinned_;
  }

  final static Parser element_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return element_recover(builder_, level_ + 1);
    }
  };
}