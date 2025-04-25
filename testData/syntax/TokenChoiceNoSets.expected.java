// ---- generated/GeneratedParser.java -----------------
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
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.parser.ProductionResult;
import com.intellij.platform.syntax.SyntaxElementTypeSet;
import static com.intellij.platform.syntax.parser.ProductionResultKt.prepareProduction;
import kotlin.jvm.functions.Function2;
import kotlin.Unit;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GeneratedParser {

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
  // (A | B | C) D
  public static boolean inner_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "inner_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, INNER_CHOICE, "<inner choice>");
    result_ = inner_choice_0(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, D);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // A | B | C
  private static boolean inner_choice_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "inner_choice_0")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = consumeToken(runtime_, B);
    if (!result_) result_ = consumeToken(runtime_, C);
    return result_;
  }

  /* ********************************************************** */
  // A | (B) | C
  public static boolean inner_parenthesized_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "inner_parenthesized_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, INNER_PARENTHESIZED_CHOICE, "<inner parenthesized choice>");
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = consumeToken(runtime_, B);
    if (!result_) result_ = consumeToken(runtime_, C);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // A | B | 'c'
  public static boolean text_token_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "text_token_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, TEXT_TOKEN_CHOICE, "<text token choice>");
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = consumeToken(runtime_, B);
    if (!result_) result_ = consumeToken(runtime_, "c");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B
  public static boolean two_tokens_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "two_tokens_choice")) return false;
    if (!nextTokenIs(runtime_, "<two tokens choice>", A, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, TWO_TOKENS_CHOICE, "<two tokens choice>");
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = consumeToken(runtime_, B);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | B | B | A
  public static boolean two_tokens_repeating_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "two_tokens_repeating_choice")) return false;
    if (!nextTokenIs(runtime_, "<two tokens repeating choice>", A, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, TWO_TOKENS_REPEATING_CHOICE, "<two tokens repeating choice>");
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = consumeToken(runtime_, B);
    if (!result_) result_ = consumeToken(runtime_, B);
    if (!result_) result_ = consumeToken(runtime_, A);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

}