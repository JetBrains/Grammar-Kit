// ---- BindersAndHooks.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
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
import static com.intellij.lang.WhitespacesBinders.*;
import static com.sample.MyHooks.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class BindersAndHooks {

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
  // A item B
  public static boolean both_binders(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "both_binders")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, BOTH_BINDERS, null);
    result_ = consumeToken(runtime_, A);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, item(runtime_, level_ + 1));
    result_ = pinned_ && consumeToken(runtime_, B) && result_;
    register_hook_(runtime_, WS_BINDERS, GREEDY_LEFT_BINDER, GREEDY_RIGHT_BINDER);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A
  public static boolean got_hook(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "got_hook")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    register_hook_(runtime_, MY_HOOK, "my", "hook", "param", "array");
    exit_section_(runtime_, marker_, GOT_HOOK, result_);
    return result_;
  }

  /* ********************************************************** */
  static boolean item(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // A B
  public static boolean left_binder(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "left_binder")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokens(runtime_, 0, A, B);
    register_hook_(runtime_, LEFT_BINDER, GREEDY_LEFT_BINDER);
    exit_section_(runtime_, marker_, LEFT_BINDER, result_);
    return result_;
  }

  /* ********************************************************** */
  // item
  public static boolean right_binder(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "right_binder")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, RIGHT_BINDER, "<right binder>");
    result_ = item(runtime_, level_ + 1);
    register_hook_(runtime_, RIGHT_BINDER, GREEDY_RIGHT_BINDER);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // left_binder right_binder both_binders
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = left_binder(runtime_, level_ + 1);
    result_ = result_ && right_binder(runtime_, level_ + 1);
    result_ = result_ && both_binders(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

}