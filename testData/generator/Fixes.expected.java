// ---- Fixes.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Fixes implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_, 0);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(A_EXPR, B_EXPR, EXPR, LEFT_EXPR,
      SOME_EXPR),
  };

  /* ********************************************************** */
  // orRestriction
  public static boolean a_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<a expr>");
    result_ = orRestriction(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, A_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // singleRestriction ( "&&" singleRestriction ) *
  static boolean andRestriction(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = singleRestriction(builder_, level_ + 1);
    result_ = result_ && andRestriction_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( "&&" singleRestriction ) *
  private static boolean andRestriction_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andRestriction_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!andRestriction_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "andRestriction_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // "&&" singleRestriction
  private static boolean andRestriction_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andRestriction_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "&&");
    result_ = result_ && singleRestriction(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // andRestriction
  public static boolean b_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<b expr>");
    result_ = andRestriction(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, B_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // a_expr | b_expr
  public static boolean expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<expr>");
    result_ = a_expr(builder_, level_ + 1);
    if (!result_) result_ = b_expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // expr
  public static boolean left_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "left_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, "<left expr>");
    result_ = expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, LEFT_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // andRestriction ( "||" andRestriction ) *
  static boolean orRestriction(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = andRestriction(builder_, level_ + 1);
    result_ = result_ && orRestriction_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( "||" andRestriction ) *
  private static boolean orRestriction_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orRestriction_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!orRestriction_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "orRestriction_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // "||" andRestriction
  private static boolean orRestriction_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orRestriction_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "||");
    result_ = result_ && andRestriction(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // some [recursive]
  static boolean recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = some(builder_, level_ + 1);
    result_ = result_ && recursive_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [recursive]
  private static boolean recursive_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive_1")) return false;
    recursive(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  static boolean root(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // <<p>> (',' <<p>>)*
  static boolean sequence(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = p.parse(builder_, level_);
    pinned_ = result_; // pin = 1
    result_ = result_ && sequence_1(builder_, level_ + 1, p);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' <<p>>)*
  private static boolean sequence_1(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!sequence_1_0(builder_, level_ + 1, p)) break;
      if (!empty_element_parsed_guard_(builder_, "sequence_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' <<p>>
  private static boolean sequence_1_0(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && p.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A expr | '(' orRestriction ')'
  static boolean singleRestriction(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "singleRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = singleRestriction_0(builder_, level_ + 1);
    if (!result_) result_ = singleRestriction_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // A expr
  private static boolean singleRestriction_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "singleRestriction_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    result_ = result_ && expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '(' orRestriction ')'
  private static boolean singleRestriction_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "singleRestriction_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "(");
    result_ = result_ && orRestriction(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ")");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // A
  public static boolean some(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    exit_section_(builder_, marker_, SOME, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr left_expr *
  public static boolean some_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<some expr>");
    result_ = expr(builder_, level_ + 1);
    result_ = result_ && some_expr_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, SOME_EXPR, result_, false, null);
    return result_;
  }

  // left_expr *
  private static boolean some_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!left_expr(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "some_expr_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // <<sequence some>>
  public static boolean some_seq(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_seq")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = sequence(builder_, level_ + 1, some_parser_);
    exit_section_(builder_, marker_, SOME_SEQ, result_);
    return result_;
  }

  /* ********************************************************** */
  // recursive
  public static boolean with_recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "with_recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = recursive(builder_, level_ + 1);
    exit_section_(builder_, marker_, WITH_RECURSIVE, result_);
    return result_;
  }

  final static Parser some_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return some(builder_, level_ + 1);
    }
  };
}