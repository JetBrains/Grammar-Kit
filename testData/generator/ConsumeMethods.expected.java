// ---- ConsumeMethods.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ConsumeMethods implements PsiParser, LightPsiParser {

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
  // &token_fast token_regular
  public static boolean fast_predicate_vs_regular(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_predicate_vs_regular")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = fast_predicate_vs_regular_0(builder_, level_ + 1);
    result_ = result_ && token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_PREDICATE_VS_REGULAR, result_);
    return result_;
  }

  // &token_fast
  private static boolean fast_predicate_vs_regular_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_predicate_vs_regular_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_fast token_smart
  public static boolean fast_predicate_vs_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_predicate_vs_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = fast_predicate_vs_smart_0(builder_, level_ + 1);
    result_ = result_ && token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_PREDICATE_VS_SMART, result_);
    return result_;
  }

  // &token_fast
  private static boolean fast_predicate_vs_smart_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_predicate_vs_smart_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // FAST_TOKEN 3 5
  public static boolean fast_rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_rule")) return false;
    if (!nextTokenIsFast(builder_, FAST_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenFast(builder_, FAST_TOKEN);
    result_ = result_ && consumeToken(builder_, "3");
    result_ = result_ && consumeToken(builder_, "5");
    exit_section_(builder_, marker_, FAST_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_regular
  public static boolean fast_vs_regular(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_vs_regular")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_fast(builder_, level_ + 1);
    if (!result_) result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_VS_REGULAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_regular
  public static boolean fast_vs_regular_in_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_vs_regular_in_fast")) return false;
    if (!nextTokenIsFast(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_fast(builder_, level_ + 1);
    if (!result_) result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_VS_REGULAR_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_regular
  public static boolean fast_vs_regular_in_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_vs_regular_in_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_fast(builder_, level_ + 1);
    if (!result_) result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_VS_REGULAR_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_smart
  public static boolean fast_vs_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_vs_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_fast(builder_, level_ + 1);
    if (!result_) result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_VS_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_smart
  public static boolean fast_vs_smart_in_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_vs_smart_in_fast")) return false;
    if (!nextTokenIsFast(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_fast(builder_, level_ + 1);
    if (!result_) result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_VS_SMART_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_smart
  public static boolean fast_vs_smart_in_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fast_vs_smart_in_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_fast(builder_, level_ + 1);
    if (!result_) result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, FAST_VS_SMART_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  public static boolean parent_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parent_fast")) return false;
    if (!nextTokenIsFast(builder_, FAST_TOKEN, REGULAR_TOKEN, SMART_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARENT_FAST, "<parent fast>");
    result_ = regular_rule(builder_, level_ + 1);
    if (!result_) result_ = smart_rule(builder_, level_ + 1);
    if (!result_) result_ = fast_rule(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  public static boolean parent_regular(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parent_regular")) return false;
    if (!nextTokenIsFast(builder_, FAST_TOKEN) &&
        !nextTokenIsSmart(builder_, SMART_TOKEN) &&
        !nextTokenIs(builder_, "<parent regular>", REGULAR_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARENT_REGULAR, "<parent regular>");
    result_ = regular_rule(builder_, level_ + 1);
    if (!result_) result_ = smart_rule(builder_, level_ + 1);
    if (!result_) result_ = fast_rule(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  public static boolean parent_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parent_smart")) return false;
    if (!nextTokenIsFast(builder_, FAST_TOKEN) &&
        !nextTokenIsSmart(builder_, REGULAR_TOKEN, SMART_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARENT_SMART, "<parent smart>");
    result_ = regular_rule(builder_, level_ + 1);
    if (!result_) result_ = smart_rule(builder_, level_ + 1);
    if (!result_) result_ = fast_rule(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_regular token_fast
  public static boolean regular_predicate_vs_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_predicate_vs_fast")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = regular_predicate_vs_fast_0(builder_, level_ + 1);
    result_ = result_ && token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_PREDICATE_VS_FAST, result_);
    return result_;
  }

  // &token_regular
  private static boolean regular_predicate_vs_fast_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_predicate_vs_fast_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_regular token_smart
  public static boolean regular_predicate_vs_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_predicate_vs_smart")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = regular_predicate_vs_smart_0(builder_, level_ + 1);
    result_ = result_ && token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_PREDICATE_VS_SMART, result_);
    return result_;
  }

  // &token_regular
  private static boolean regular_predicate_vs_smart_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_predicate_vs_smart_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // REGULAR_TOKEN 1 2 3
  public static boolean regular_rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_rule")) return false;
    if (!nextTokenIs(builder_, REGULAR_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, REGULAR_TOKEN);
    result_ = result_ && consumeToken(builder_, "1");
    result_ = result_ && consumeToken(builder_, "2");
    result_ = result_ && consumeToken(builder_, "3");
    exit_section_(builder_, marker_, REGULAR_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_fast
  public static boolean regular_vs_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_vs_fast")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_regular(builder_, level_ + 1);
    if (!result_) result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_VS_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_fast
  public static boolean regular_vs_fast_in_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_vs_fast_in_fast")) return false;
    if (!nextTokenIsFast(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_regular(builder_, level_ + 1);
    if (!result_) result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_VS_FAST_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_fast
  public static boolean regular_vs_fast_in_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_vs_fast_in_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_regular(builder_, level_ + 1);
    if (!result_) result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_VS_FAST_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_smart
  public static boolean regular_vs_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_vs_smart")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_regular(builder_, level_ + 1);
    if (!result_) result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_VS_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_smart
  public static boolean regular_vs_smart_in_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_vs_smart_in_fast")) return false;
    if (!nextTokenIsFast(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_regular(builder_, level_ + 1);
    if (!result_) result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_VS_SMART_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_smart
  public static boolean regular_vs_smart_in_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "regular_vs_smart_in_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_regular(builder_, level_ + 1);
    if (!result_) result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, marker_, REGULAR_VS_SMART_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  static boolean root(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // &token_smart token_fast
  public static boolean smart_predicate_vs_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_predicate_vs_fast")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = smart_predicate_vs_fast_0(builder_, level_ + 1);
    result_ = result_ && token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_PREDICATE_VS_FAST, result_);
    return result_;
  }

  // &token_smart
  private static boolean smart_predicate_vs_fast_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_predicate_vs_fast_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_smart token_regular
  public static boolean smart_predicate_vs_regular(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_predicate_vs_regular")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = smart_predicate_vs_regular_0(builder_, level_ + 1);
    result_ = result_ && token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_PREDICATE_VS_REGULAR, result_);
    return result_;
  }

  // &token_smart
  private static boolean smart_predicate_vs_regular_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_predicate_vs_regular_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = token_smart(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // SMART_TOKEN 2 4
  public static boolean smart_rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_rule")) return false;
    if (!nextTokenIsSmart(builder_, SMART_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, SMART_TOKEN);
    result_ = result_ && consumeToken(builder_, "2");
    result_ = result_ && consumeToken(builder_, "4");
    exit_section_(builder_, marker_, SMART_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_fast
  public static boolean smart_vs_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_vs_fast")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_smart(builder_, level_ + 1);
    if (!result_) result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_VS_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_fast
  public static boolean smart_vs_fast_in_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_vs_fast_in_fast")) return false;
    if (!nextTokenIsFast(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_smart(builder_, level_ + 1);
    if (!result_) result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_VS_FAST_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_fast
  public static boolean smart_vs_fast_in_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_vs_fast_in_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_smart(builder_, level_ + 1);
    if (!result_) result_ = token_fast(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_VS_FAST_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_regular
  public static boolean smart_vs_regular(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_vs_regular")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_smart(builder_, level_ + 1);
    if (!result_) result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_VS_REGULAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_regular
  public static boolean smart_vs_regular_in_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_vs_regular_in_fast")) return false;
    if (!nextTokenIsFast(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_smart(builder_, level_ + 1);
    if (!result_) result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_VS_REGULAR_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_regular
  public static boolean smart_vs_regular_in_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "smart_vs_regular_in_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = token_smart(builder_, level_ + 1);
    if (!result_) result_ = token_regular(builder_, level_ + 1);
    exit_section_(builder_, marker_, SMART_VS_REGULAR_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // SAME_TOKEN 5 6
  public static boolean token_fast(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_fast")) return false;
    if (!nextTokenIsFast(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenFast(builder_, SAME_TOKEN);
    result_ = result_ && consumeToken(builder_, "5");
    result_ = result_ && consumeToken(builder_, "6");
    exit_section_(builder_, marker_, TOKEN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // SAME_TOKEN 3 4
  public static boolean token_regular(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_regular")) return false;
    if (!nextTokenIs(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, SAME_TOKEN);
    result_ = result_ && consumeToken(builder_, "3");
    result_ = result_ && consumeToken(builder_, "4");
    exit_section_(builder_, marker_, TOKEN_REGULAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // SAME_TOKEN 4 5
  public static boolean token_smart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_smart")) return false;
    if (!nextTokenIsSmart(builder_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, SAME_TOKEN);
    result_ = result_ && consumeToken(builder_, "4");
    result_ = result_ && consumeToken(builder_, "5");
    exit_section_(builder_, marker_, TOKEN_SMART, result_);
    return result_;
  }

}