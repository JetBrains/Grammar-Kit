// ---- Small.java -----------------
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
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Small {

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
    return parseRoot(runtime_, level_ + 1, Small::statement);
  }

  /* ********************************************************** */
  // ()
  public static boolean empty(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, EMPTY, true);
    return true;
  }

  /* ********************************************************** */
  // []
  public static boolean empty10(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_INNER_, EMPTY_10, null);
    exit_section_(runtime_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // {}
  public static boolean empty2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, EMPTY_2, true);
    return true;
  }

  /* ********************************************************** */
  // []
  public static boolean empty3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, EMPTY_3, true);
    return true;
  }

  /* ********************************************************** */
  // ()
  static boolean empty4(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // []
  static boolean empty5(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // &()
  static boolean empty6(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "empty6")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = empty6_0(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // ()
  private static boolean empty6_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // !()
  static boolean empty7(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "empty7")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !empty7_0(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // ()
  private static boolean empty7_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // [({})]
  static boolean empty8(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // []
  public static boolean empty9(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, EMPTY_9, null);
    exit_section_(runtime_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // [({token})]
  static boolean not_empty1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "not_empty1")) return false;
    not_empty1_0(runtime_, level_ + 1);
    return true;
  }

  // {token}
  private static boolean not_empty1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "not_empty1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, TOKEN);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [({token someString})]
  static boolean not_empty2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "not_empty2")) return false;
    not_empty2_0(runtime_, level_ + 1);
    return true;
  }

  // token someString
  private static boolean not_empty2_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "not_empty2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, TOKEN);
    result_ = result_ && someString(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // ( token )
  public static boolean otherRule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "otherRule")) return false;
    if (!nextTokenIs(runtime_, TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, TOKEN);
    exit_section_(runtime_, marker_, OTHER_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token
  static boolean privateRule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return consumeToken(runtime_, TOKEN);
  }

  /* ********************************************************** */
  // 'token'
  static boolean privateString(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return consumeToken(runtime_, "token");
  }

  /* ********************************************************** */
  // token
  public static boolean someRule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "someRule")) return false;
    if (!nextTokenIs(runtime_, TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, TOKEN);
    exit_section_(runtime_, marker_, SOME_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token?
  public static boolean someRule2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "someRule2")) return false;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, SOME_RULE_2, "<some rule 2>");
    consumeToken(runtime_, TOKEN);
    exit_section_(runtime_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // 'token'
  public static boolean someString(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "someString")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, SOME_STRING, "<some string>");
    result_ = consumeToken(runtime_, "token");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // token | someRule | someString
  public static boolean statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, STATEMENT, "<statement>");
    result_ = consumeToken(runtime_, TOKEN);
    if (!result_) result_ = someRule(runtime_, level_ + 1);
    if (!result_) result_ = someString(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '=' "=" '==' "=="
  static boolean tokenRule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "tokenRule")) return false;
    if (!nextTokenIs(runtime_, OP_EQ)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokens(runtime_, 0, OP_EQ, OP_EQ);
    result_ = result_ && consumeToken(runtime_, "==");
    result_ = result_ && consumeToken(runtime_, "==");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

}