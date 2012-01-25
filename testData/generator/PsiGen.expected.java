//header.txt
package ;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.ParserTypes.*;
import static PsiGenUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGen implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("PsiGen");

  @NotNull
  public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    if (root_ == A_EXPR) {
      result_ = a_expr(builder_, level_ + 1);
    }
    else if (root_ == B_EXPR) {
      result_ = b_expr(builder_, level_ + 1);
    }
    else if (root_ == EXPR) {
      result_ = expr(builder_, level_ + 1);
    }
    else if (root_ == GRAMMAR_ELEMENT) {
      result_ = grammar_element(builder_, level_ + 1);
    }
    else if (root_ == ID_EXPR) {
      result_ = id_expr(builder_, level_ + 1);
    }
    else if (root_ == MUL_EXPR) {
      result_ = mul_expr(builder_, level_ + 1);
    }
    else if (root_ == PLUS_EXPR) {
      result_ = plus_expr(builder_, level_ + 1);
    }
    else if (root_ == ROOT_B) {
      result_ = root_b(builder_, level_ + 1);
    }
    else if (root_ == ROOT_C) {
      result_ = root_c(builder_, level_ + 1);
    }
    else if (root_ == ROOT_D) {
      result_ = root_d(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      result_ = root(builder_, level_ + 1);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    TokenSet.create(A_EXPR, B_EXPR, EXPR, ID_EXPR,
      MUL_EXPR, PLUS_EXPR),
    TokenSet.create(ROOT_B, ROOT_C, ROOT_D, ROOT),
  };
  public static boolean type_extends_(IElementType child_, IElementType parent_) {
    for (TokenSet set : EXTENDS_SETS_) {
      if (set.contains(child_) && set.contains(parent_)) return true;
    }
    return false;
  }

  /* ********************************************************** */
  // b_expr plus_expr *
  public static boolean a_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = b_expr(builder_, level_ + 1);
    result_ = result_ && a_expr_1(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), A_EXPR)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(A_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // plus_expr *
  private static boolean a_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!plus_expr(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "a_expr_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // id_expr mul_expr *
  public static boolean b_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = id_expr(builder_, level_ + 1);
    result_ = result_ && b_expr_1(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), B_EXPR)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(B_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // mul_expr *
  private static boolean b_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!mul_expr(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "b_expr_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // <<p>> +
  public static boolean blockOf(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "blockOf")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = p.parse(builder_, level_);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!p.parse(builder_, level_)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "blockOf");
        break;
      }
      offset_ = next_offset_;
    }
    if (result_) {
      marker_.done(BLOCKOF);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // a_expr
  public static boolean expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = a_expr(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), EXPR)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // expr
  public static boolean grammar_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = expr(builder_, level_ + 1);
    if (result_) {
      marker_.done(GRAMMAR_ELEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean id_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "id_expr")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ID);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), ID_EXPR)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(ID_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<p>> +
  static boolean listOf(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "listOf")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = p.parse(builder_, level_);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!p.parse(builder_, level_)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "listOf");
        break;
      }
      offset_ = next_offset_;
    }
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '*' expr
  public static boolean mul_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mul_expr")) return false;
    boolean result_ = false;
    final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "mul_expr")) return false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "*");
    result_ = result_ && expr(builder_, level_ + 1);
    if (result_) {
      marker_.drop();
      left_marker_.precede().done(MUL_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // '+' expr
  public static boolean plus_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "plus_expr")) return false;
    boolean result_ = false;
    final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "plus_expr")) return false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "+");
    result_ = result_ && expr(builder_, level_ + 1);
    if (result_) {
      marker_.drop();
      left_marker_.precede().done(PLUS_EXPR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // root_a | root_b | root_c | root_d
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = parseGrammar(builder_, level_ + 1, grammar_element_parser_);
    if (!result_) result_ = root_b(builder_, level_ + 1);
    if (!result_) result_ = root_c(builder_, level_ + 1);
    if (!result_) result_ = root_d(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<parseGrammar grammar_element>>
  public static boolean root_b(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_b")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = parseGrammar(builder_, level_ + 1, grammar_element_parser_);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), ROOT_B)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(ROOT_B);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<blockOf grammar_element>>
  public static boolean root_c(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_c")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = blockOf(builder_, level_ + 1, grammar_element_parser_);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), ROOT_C)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(ROOT_C);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<listOf grammar_element>>
  public static boolean root_d(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_d")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = listOf(builder_, level_ + 1, grammar_element_parser_);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), ROOT_D)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(ROOT_D);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  final static Parser grammar_element_parser_ = new Parser() {
      public boolean parse(PsiBuilder builder_, int level_) {
        return grammar_element(builder_, level_ + 1);
      }
    };
}