// ---- ConsumeMethods.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
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
public class ConsumeMethods {

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

    runtime_.init(parse_, null);
    Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_, int level_) {
    return root(runtime_, level_ + 1);
  }

  /* ********************************************************** */
  // &token_fast token_regular
  public static boolean fast_predicate_vs_regular(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_predicate_vs_regular")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = fast_predicate_vs_regular_0(runtime_, level_ + 1);
    result_ = result_ && token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_PREDICATE_VS_REGULAR, result_);
    return result_;
  }

  // &token_fast
  private static boolean fast_predicate_vs_regular_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_predicate_vs_regular_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_fast token_smart
  public static boolean fast_predicate_vs_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_predicate_vs_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = fast_predicate_vs_smart_0(runtime_, level_ + 1);
    result_ = result_ && token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_PREDICATE_VS_SMART, result_);
    return result_;
  }

  // &token_fast
  private static boolean fast_predicate_vs_smart_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_predicate_vs_smart_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // FAST_TOKEN 3 5
  public static boolean fast_rule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_rule")) return false;
    if (!nextTokenIsFast(runtime_, FAST_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokenFast(runtime_, FAST_TOKEN);
    result_ = result_ && consumeToken(runtime_, "3");
    result_ = result_ && consumeToken(runtime_, "5");
    exit_section_(runtime_, marker_, FAST_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_regular
  public static boolean fast_vs_regular(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_vs_regular")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_fast(runtime_, level_ + 1);
    if (!result_) result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_VS_REGULAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_regular
  public static boolean fast_vs_regular_in_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_vs_regular_in_fast")) return false;
    if (!nextTokenIsFast(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_fast(runtime_, level_ + 1);
    if (!result_) result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_VS_REGULAR_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_regular
  public static boolean fast_vs_regular_in_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_vs_regular_in_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_fast(runtime_, level_ + 1);
    if (!result_) result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_VS_REGULAR_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_smart
  public static boolean fast_vs_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_vs_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_fast(runtime_, level_ + 1);
    if (!result_) result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_VS_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_smart
  public static boolean fast_vs_smart_in_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_vs_smart_in_fast")) return false;
    if (!nextTokenIsFast(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_fast(runtime_, level_ + 1);
    if (!result_) result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_VS_SMART_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_fast | token_smart
  public static boolean fast_vs_smart_in_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_vs_smart_in_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_fast(runtime_, level_ + 1);
    if (!result_) result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, FAST_VS_SMART_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  public static boolean parent_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "parent_fast")) return false;
    if (!nextTokenIsFast(runtime_, FAST_TOKEN, REGULAR_TOKEN, SMART_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, PARENT_FAST, "<parent fast>");
    result_ = regular_rule(runtime_, level_ + 1);
    if (!result_) result_ = smart_rule(runtime_, level_ + 1);
    if (!result_) result_ = fast_rule(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  public static boolean parent_regular(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "parent_regular")) return false;
    if (!nextTokenIsFast(runtime_, FAST_TOKEN) &&
        !nextTokenIsSmart(runtime_, SMART_TOKEN) &&
        !nextTokenIs(runtime_, "<parent regular>", REGULAR_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, PARENT_REGULAR, "<parent regular>");
    result_ = regular_rule(runtime_, level_ + 1);
    if (!result_) result_ = smart_rule(runtime_, level_ + 1);
    if (!result_) result_ = fast_rule(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  public static boolean parent_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "parent_smart")) return false;
    if (!nextTokenIsFast(runtime_, FAST_TOKEN) &&
        !nextTokenIsSmart(runtime_, REGULAR_TOKEN, SMART_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, PARENT_SMART, "<parent smart>");
    result_ = regular_rule(runtime_, level_ + 1);
    if (!result_) result_ = smart_rule(runtime_, level_ + 1);
    if (!result_) result_ = fast_rule(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_regular token_fast
  public static boolean regular_predicate_vs_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_predicate_vs_fast")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = regular_predicate_vs_fast_0(runtime_, level_ + 1);
    result_ = result_ && token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_PREDICATE_VS_FAST, result_);
    return result_;
  }

  // &token_regular
  private static boolean regular_predicate_vs_fast_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_predicate_vs_fast_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_regular token_smart
  public static boolean regular_predicate_vs_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_predicate_vs_smart")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = regular_predicate_vs_smart_0(runtime_, level_ + 1);
    result_ = result_ && token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_PREDICATE_VS_SMART, result_);
    return result_;
  }

  // &token_regular
  private static boolean regular_predicate_vs_smart_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_predicate_vs_smart_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // REGULAR_TOKEN 1 2 3
  public static boolean regular_rule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_rule")) return false;
    if (!nextTokenIs(runtime_, REGULAR_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, REGULAR_TOKEN);
    result_ = result_ && consumeToken(runtime_, "1");
    result_ = result_ && consumeToken(runtime_, "2");
    result_ = result_ && consumeToken(runtime_, "3");
    exit_section_(runtime_, marker_, REGULAR_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_fast
  public static boolean regular_vs_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_vs_fast")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_regular(runtime_, level_ + 1);
    if (!result_) result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_VS_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_fast
  public static boolean regular_vs_fast_in_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_vs_fast_in_fast")) return false;
    if (!nextTokenIsFast(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_regular(runtime_, level_ + 1);
    if (!result_) result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_VS_FAST_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_fast
  public static boolean regular_vs_fast_in_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_vs_fast_in_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_regular(runtime_, level_ + 1);
    if (!result_) result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_VS_FAST_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_smart
  public static boolean regular_vs_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_vs_smart")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_regular(runtime_, level_ + 1);
    if (!result_) result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_VS_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_smart
  public static boolean regular_vs_smart_in_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_vs_smart_in_fast")) return false;
    if (!nextTokenIsFast(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_regular(runtime_, level_ + 1);
    if (!result_) result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_VS_SMART_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_regular | token_smart
  public static boolean regular_vs_smart_in_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "regular_vs_smart_in_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_regular(runtime_, level_ + 1);
    if (!result_) result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REGULAR_VS_SMART_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // &token_smart token_fast
  public static boolean smart_predicate_vs_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_predicate_vs_fast")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = smart_predicate_vs_fast_0(runtime_, level_ + 1);
    result_ = result_ && token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_PREDICATE_VS_FAST, result_);
    return result_;
  }

  // &token_smart
  private static boolean smart_predicate_vs_fast_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_predicate_vs_fast_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &token_smart token_regular
  public static boolean smart_predicate_vs_regular(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_predicate_vs_regular")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = smart_predicate_vs_regular_0(runtime_, level_ + 1);
    result_ = result_ && token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_PREDICATE_VS_REGULAR, result_);
    return result_;
  }

  // &token_smart
  private static boolean smart_predicate_vs_regular_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_predicate_vs_regular_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = token_smart(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // SMART_TOKEN 2 4
  public static boolean smart_rule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_rule")) return false;
    if (!nextTokenIsSmart(runtime_, SMART_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokenSmart(runtime_, SMART_TOKEN);
    result_ = result_ && consumeToken(runtime_, "2");
    result_ = result_ && consumeToken(runtime_, "4");
    exit_section_(runtime_, marker_, SMART_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_fast
  public static boolean smart_vs_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_vs_fast")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_smart(runtime_, level_ + 1);
    if (!result_) result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_VS_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_fast
  public static boolean smart_vs_fast_in_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_vs_fast_in_fast")) return false;
    if (!nextTokenIsFast(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_smart(runtime_, level_ + 1);
    if (!result_) result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_VS_FAST_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_fast
  public static boolean smart_vs_fast_in_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_vs_fast_in_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_smart(runtime_, level_ + 1);
    if (!result_) result_ = token_fast(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_VS_FAST_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_regular
  public static boolean smart_vs_regular(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_vs_regular")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_smart(runtime_, level_ + 1);
    if (!result_) result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_VS_REGULAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_regular
  public static boolean smart_vs_regular_in_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_vs_regular_in_fast")) return false;
    if (!nextTokenIsFast(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_smart(runtime_, level_ + 1);
    if (!result_) result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_VS_REGULAR_IN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // token_smart | token_regular
  public static boolean smart_vs_regular_in_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_vs_regular_in_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_smart(runtime_, level_ + 1);
    if (!result_) result_ = token_regular(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SMART_VS_REGULAR_IN_SMART, result_);
    return result_;
  }

  /* ********************************************************** */
  // SAME_TOKEN 5 6
  public static boolean token_fast(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_fast")) return false;
    if (!nextTokenIsFast(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokenFast(runtime_, SAME_TOKEN);
    result_ = result_ && consumeToken(runtime_, "5");
    result_ = result_ && consumeToken(runtime_, "6");
    exit_section_(runtime_, marker_, TOKEN_FAST, result_);
    return result_;
  }

  /* ********************************************************** */
  // SAME_TOKEN 3 4
  public static boolean token_regular(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_regular")) return false;
    if (!nextTokenIs(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, SAME_TOKEN);
    result_ = result_ && consumeToken(runtime_, "3");
    result_ = result_ && consumeToken(runtime_, "4");
    exit_section_(runtime_, marker_, TOKEN_REGULAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // SAME_TOKEN 4 5
  public static boolean token_smart(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_smart")) return false;
    if (!nextTokenIsSmart(runtime_, SAME_TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokenSmart(runtime_, SAME_TOKEN);
    result_ = result_ && consumeToken(runtime_, "4");
    result_ = result_ && consumeToken(runtime_, "5");
    exit_section_(runtime_, marker_, TOKEN_SMART, result_);
    return result_;
  }

}