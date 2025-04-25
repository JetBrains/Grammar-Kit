// ---- generated/TokenChoice.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static generated.GeneratedSyntaxElementTypes.TokenSets.*;
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
public class TokenChoice {

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
  // D | A | B
  public static boolean another_three_tokens(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "another_three_tokens")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ANOTHER_THREE_TOKENS, "<another three tokens>");
    result_ = consumeToken(runtime_, ANOTHER_THREE_TOKENS_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | F
  public static boolean fast_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "fast_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, FAST_CHOICE, "<fast choice>");
    result_ = consumeTokenFast(runtime_, FAST_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C | D | E
  public static boolean five_tokens_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "five_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, FIVE_TOKENS_CHOICE, "<five tokens choice>");
    result_ = consumeToken(runtime_, FIVE_TOKENS_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C | D
  public static boolean four_tokens_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "four_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, FOUR_TOKENS_CHOICE, "<four tokens choice>");
    result_ = consumeToken(runtime_, FOUR_TOKENS_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C
  public static boolean parenthesized_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "parenthesized_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, PARENTHESIZED_CHOICE, "<parenthesized choice>");
    result_ = consumeToken(runtime_, PARENTHESIZED_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // P2 | P3 | P0 | P1
  static boolean private_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "private_choice")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, PRIVATE_CHOICE_TOKENS);
    return result_;
  }

  /* ********************************************************** */
  // D | C | A | B | B | A | C
  public static boolean repeating_tokens_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "repeating_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, REPEATING_TOKENS_CHOICE, "<repeating tokens choice>");
    result_ = consumeToken(runtime_, REPEATING_TOKENS_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // A | B | S
  public static boolean smart_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "smart_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, SMART_CHOICE, "<smart choice>");
    result_ = consumeTokenSmart(runtime_, SMART_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C | D | E | F | G | H | I | J
  public static boolean ten_tokens_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "ten_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, TEN_TOKENS_CHOICE, "<ten tokens choice>");
    result_ = consumeToken(runtime_, TEN_TOKENS_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | C
  public static boolean three_tokens_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "three_tokens_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, THREE_TOKENS_CHOICE, "<three tokens choice>");
    result_ = consumeToken(runtime_, THREE_TOKENS_CHOICE_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // B | A | C
  public static boolean three_tokens_in_another_order(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "three_tokens_in_another_order")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, THREE_TOKENS_IN_ANOTHER_ORDER, "<three tokens in another order>");
    result_ = consumeToken(runtime_, THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

}
// ---- generated/TokenChoice2.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

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
import static generated.TokenChoice.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class TokenChoice2 {

  /* ********************************************************** */
  public static boolean some(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, SOME, true);
    return true;
  }

}