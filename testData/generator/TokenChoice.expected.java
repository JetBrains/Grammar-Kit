// ---- TokenChoice.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static generated.GeneratedTypes.TokenSets.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class TokenChoice implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ instanceof IFileElementType) {
      result_ = parse_root_(root_, builder_, 0);
    }
    else {
      result_ = false;
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // D | A | B
  public static boolean another_three_tokens(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "another_three_tokens")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ANOTHER_THREE_TOKENS, "<another three tokens>");
    result_ = consumeToken(builder_, ANOTHER_THREE_TOKENS_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | F
  public static boolean fast_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FAST_CHOICE, "<fast choice>");
    result_ = consumeTokenFast(builder_, FAST_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C | D | E
  public static boolean five_tokens_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "five_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FIVE_TOKENS_CHOICE, "<five tokens choice>");
    result_ = consumeToken(builder_, FIVE_TOKENS_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C | D
  public static boolean four_tokens_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "four_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOUR_TOKENS_CHOICE, "<four tokens choice>");
    result_ = consumeToken(builder_, FOUR_TOKENS_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C
  public static boolean parenthesized_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parenthesized_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARENTHESIZED_CHOICE, "<parenthesized choice>");
    result_ = consumeToken(builder_, PARENTHESIZED_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // P2 | P3 | P0 | P1
  static boolean private_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "private_choice")) return false;
    boolean result_;
    result_ = consumeToken(builder_, PRIVATE_CHOICE_TOKENS);
    return result_;
  }

  /* ********************************************************** */
  // D | C | A | B | B | A | C
  public static boolean repeating_tokens_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "repeating_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, REPEATING_TOKENS_CHOICE, "<repeating tokens choice>");
    result_ = consumeToken(builder_, REPEATING_TOKENS_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static boolean root(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // A | B | S
  public static boolean smart_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, SMART_CHOICE, "<smart choice>");
    result_ = consumeTokenSmart(builder_, SMART_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C | D | E | F | G | H | I | J
  public static boolean ten_tokens_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ten_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TEN_TOKENS_CHOICE, "<ten tokens choice>");
    result_ = consumeToken(builder_, TEN_TOKENS_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C
  public static boolean three_tokens_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "three_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, THREE_TOKENS_CHOICE, "<three tokens choice>");
    result_ = consumeToken(builder_, THREE_TOKENS_CHOICE_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // B | A | C
  public static boolean three_tokens_in_another_order(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "three_tokens_in_another_order")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, THREE_TOKENS_IN_ANOTHER_ORDER, "<three tokens in another order>");
    result_ = consumeToken(builder_, THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

}
// ---- TokenChoice2.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static generated.TokenChoice.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class TokenChoice2 {

  /* ********************************************************** */
  public static boolean some(PsiBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, SOME, true);
    return true;
  }

}