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
    if (root_ == ASSIGN_EXPR) {
      result_ = expr(builder_, level_ + 1, 0);
    }
    else if (root_ == CUSTOM_EXPR) {
      result_ = custom_expr(builder_, level_ + 1);
    }
    else if (root_ == DIV_EXPR) {
      result_ = expr(builder_, level_ + 1, 3);
    }
    else if (root_ == ELVIS_EXPR) {
      result_ = expr(builder_, level_ + 1, 1);
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
      result_ = expr(builder_, level_ + 1, 7);
    }
    else if (root_ == MINUS_EXPR) {
      result_ = expr(builder_, level_ + 1, 2);
    }
    else if (root_ == MUL_EXPR) {
      result_ = expr(builder_, level_ + 1, 3);
    }
    else if (root_ == OBJECT_EXPR) {
      result_ = expr(builder_, level_ + 1, 7);
    }
    else if (root_ == PAREN_EXPR) {
      result_ = expr(builder_, level_ + 1, 7);
    }
    else if (root_ == PLUS_EXPR) {
      result_ = expr(builder_, level_ + 1, 2);
    }
    else if (root_ == REF_EXPR) {
      result_ = expr(builder_, level_ + 1, 7);
    }
    else if (root_ == UNARY_MIN_EXPR) {
      result_ = expr(builder_, level_ + 1, 4);
    }
    else if (root_ == UNARY_PLUS_EXPR) {
      result_ = expr(builder_, level_ + 1, 4);
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
    TokenSet.create(ASSIGN_EXPR, CUSTOM_EXPR, DIV_EXPR, ELVIS_EXPR,
      EXPR, EXP_EXPR, FACTORIAL_EXPR, LITERAL_EXPR,
      MINUS_EXPR, MUL_EXPR, OBJECT_EXPR, PAREN_EXPR,
      PLUS_EXPR, REF_EXPR, UNARY_MIN_EXPR, UNARY_PLUS_EXPR),
  };
  public static boolean type_extends_(IElementType child_, IElementType parent_) {
    for (TokenSet set : EXTENDS_SETS_) {
      if (set.contains(child_) && set.contains(parent_)) return true;
    }
    return false;
  }

  /* ********************************************************** */
  // 'OBJECT' '(' ref_expr ')'
  public static boolean custom_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "custom_expr")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<custom expr>");
    result_ = consumeToken(builder_, "OBJECT");
    result_ = result_ && consumeToken(builder_, "(");
    result_ = result_ && expr(builder_, level_ + 1, 7);
    result_ = result_ && consumeToken(builder_, ")");
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), CUSTOM_EXPR)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(CUSTOM_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
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
  // expr *
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!expr(builder_, level_ + 1, -1)) break;
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
  // 0: BINARY('=')
  // 1: BINARY('?' tail: ':' expr)
  // 2: BINARY('+') BINARY('-')
  // 3: BINARY('*') BINARY('/')
  // 4: UNARY('+') UNARY('-')
  // 5: N_ARY('^')
  // 6: UNARY_POSTFIX('!')
  // 7: UNARY('OBJECT' '(' tail: ')') ATOM(identifier) ATOM(number) UNARY('(' tail: ')')
  public static boolean expr(PsiBuilder builder_, int level_, int priority_) {
    Marker marker_ = builder_.mark();
    boolean result_ = false;
    boolean pinned_ = false;
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<expr>");
    if (consumeToken(builder_, "+")) {
      pinned_ = true;
      result_ = expr(builder_, level_, 4);
      marker_.done(UNARY_PLUS_EXPR);
    }
    else if (consumeToken(builder_, "-")) {
      pinned_ = true;
      result_ = expr(builder_, level_, 4);
      marker_.done(UNARY_MIN_EXPR);
    }
    else if (object_expr_0(builder_, level_ + 1)) {
      pinned_ = true;
      result_ = expr(builder_, level_, 7);
      result_ = report_error_(builder_, consumeToken(builder_, ")")) && result_;
      marker_.done(OBJECT_EXPR);
    }
    else if (identifier(builder_, level_ + 1)) {
      pinned_ = true;
      result_ = true;
      marker_.done(REF_EXPR);
    }
    else if (consumeToken(builder_, NUMBER)) {
      pinned_ = true;
      result_ = true;
      marker_.done(LITERAL_EXPR);
    }
    else if (consumeToken(builder_, "(")) {
      pinned_ = true;
      result_ = expr(builder_, level_, -1);
      result_ = report_error_(builder_, consumeToken(builder_, ")")) && result_;
      marker_.done(PAREN_EXPR);
    }
    result_ = pinned_ && expr_0(builder_, level_, priority_) && result_;
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  public static boolean expr_0(PsiBuilder builder_, int level_, int priority_) {
    boolean result_ = true;
    while (true) {
      Marker left_marker_ = (Marker) builder_.getLatestDoneMarker();
      if (!invalid_left_marker_guard_(builder_, left_marker_, "expr_0")) return false;
      Marker marker_ = builder_.mark();
      if (priority_ < 0 && consumeToken(builder_, "=")) {
        result_ = report_error_(builder_, expr(builder_, level_, -1));
        left_marker_.precede().done(ASSIGN_EXPR);
      }
      else if (priority_ < 1 && consumeToken(builder_, "?")) {
        result_ = report_error_(builder_, expr(builder_, level_, 1));
        result_ = elvis_expr_1(builder_, level_ + 1) && result_;
        left_marker_.precede().done(ELVIS_EXPR);
      }
      else if (priority_ < 2 && consumeToken(builder_, "+")) {
        result_ = report_error_(builder_, expr(builder_, level_, 2));
        left_marker_.precede().done(PLUS_EXPR);
      }
      else if (priority_ < 2 && consumeToken(builder_, "-")) {
        result_ = report_error_(builder_, expr(builder_, level_, 2));
        left_marker_.precede().done(MINUS_EXPR);
      }
      else if (priority_ < 3 && consumeToken(builder_, "*")) {
        result_ = report_error_(builder_, expr(builder_, level_, 3));
        left_marker_.precede().done(MUL_EXPR);
      }
      else if (priority_ < 3 && consumeToken(builder_, "/")) {
        result_ = report_error_(builder_, expr(builder_, level_, 3));
        left_marker_.precede().done(DIV_EXPR);
      }
      else if (priority_ < 5 && consumeToken(builder_, "^")) {
        while (true) {
          result_ = report_error_(builder_, expr(builder_, level_, 5));
          if (!consumeToken(builder_, "^")) break;
        }
        left_marker_.precede().done(EXP_EXPR);
      }
      else if (priority_ < 6 && consumeToken(builder_, "!")) {
        result_ = true;
        left_marker_.precede().done(FACTORIAL_EXPR);
      }
      else {
        marker_.rollbackTo();
        break;
      }
      marker_.drop();
    }
    return result_;
  }

  // ':' expr
  private static boolean elvis_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "elvis_expr_1")) return false;
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

  // 'OBJECT' '('
  private static boolean object_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "object_expr_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "OBJECT");
    result_ = result_ && consumeToken(builder_, "(");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

}