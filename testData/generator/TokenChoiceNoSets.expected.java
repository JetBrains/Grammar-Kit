// ---- GeneratedParser.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

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
public class GeneratedParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // (A | B | C) D
  public static boolean inner_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inner_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, INNER_CHOICE, "<inner choice>");
    result_ = inner_choice_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, D);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // A | B | C
  private static boolean inner_choice_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inner_choice_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = consumeToken(builder_, B);
    if (!result_) result_ = consumeToken(builder_, C);
    return result_;
  }

  /* ********************************************************** */
  // A | (B) | C
  public static boolean inner_parenthesized_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "inner_parenthesized_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, INNER_PARENTHESIZED_CHOICE, "<inner parenthesized choice>");
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = consumeToken(builder_, B);
    if (!result_) result_ = consumeToken(builder_, C);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static boolean root(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // A | B | 'c'
  public static boolean text_token_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "text_token_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TEXT_TOKEN_CHOICE, "<text token choice>");
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = consumeToken(builder_, B);
    if (!result_) result_ = consumeToken(builder_, "c");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B
  public static boolean two_tokens_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "two_tokens_choice")) return false;
    if (!nextTokenIs(builder_, "<two tokens choice>", A, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TWO_TOKENS_CHOICE, "<two tokens choice>");
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = consumeToken(builder_, B);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | B | A
  public static boolean two_tokens_repeating_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "two_tokens_repeating_choice")) return false;
    if (!nextTokenIs(builder_, "<two tokens repeating choice>", A, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TWO_TOKENS_REPEATING_CHOICE, "<two tokens repeating choice>");
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = consumeToken(builder_, B);
    if (!result_) result_ = consumeToken(builder_, B);
    if (!result_) result_ = consumeToken(builder_, A);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

}