// ---- LeftAssociative.java -----------------
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
public class LeftAssociative {

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
    return from(runtime_, level_ + 1);
  }

  /* ********************************************************** */
  // AS? id
  public static boolean alias_definition(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "alias_definition")) return false;
    if (!nextTokenIs(runtime_, "<alias definition>", AS, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, ALIAS_DEFINITION, "<alias definition>");
    result_ = alias_definition_0(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, ID);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // AS?
  private static boolean alias_definition_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "alias_definition_0")) return false;
    consumeToken(runtime_, AS);
    return true;
  }

  /* ********************************************************** */
  // AS? id
  public static boolean alias_definition2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "alias_definition2")) return false;
    if (!nextTokenIs(runtime_, "<alias definition 2>", AS, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, ALIAS_DEFINITION_2, "<alias definition 2>");
    result_ = alias_definition2_0(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, ID);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // AS?
  private static boolean alias_definition2_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "alias_definition2_0")) return false;
    consumeToken(runtime_, AS);
    return true;
  }

  /* ********************************************************** */
  // reference alias_definition? alias_definition2? leech? leech2?
  static boolean from(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "from")) return false;
    if (!nextTokenIs(runtime_, REFERENCE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, REFERENCE);
    result_ = result_ && from_1(runtime_, level_ + 1);
    result_ = result_ && from_2(runtime_, level_ + 1);
    result_ = result_ && from_3(runtime_, level_ + 1);
    result_ = result_ && from_4(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // alias_definition?
  private static boolean from_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "from_1")) return false;
    alias_definition(runtime_, level_ + 1);
    return true;
  }

  // alias_definition2?
  private static boolean from_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "from_2")) return false;
    alias_definition2(runtime_, level_ + 1);
    return true;
  }

  // leech?
  private static boolean from_3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "from_3")) return false;
    leech(runtime_, level_ + 1);
    return true;
  }

  // leech2?
  private static boolean from_4(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "from_4")) return false;
    leech2(runtime_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // id
  public static boolean leech(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "leech")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_INNER_, LEECH, null);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  static boolean leech2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "leech2")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_INNER_);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id?
  static boolean leech3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "leech3")) return false;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_INNER_);
    consumeToken(runtime_, ID);
    exit_section_(runtime_, level_, marker_, true, false, null);
    return true;
  }

}