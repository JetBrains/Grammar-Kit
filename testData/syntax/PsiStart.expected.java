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
    boolean result_;
    if (root_ == ELEMENT) {
      result_ = element(runtime_, level_ + 1);
    }
    else if (root_ == ENTRY) {
      result_ = entry(runtime_, level_ + 1);
    }
    else if (root_ == LIST) {
      result_ = list(runtime_, level_ + 1);
    }
    else if (root_ == MAP) {
      result_ = map(runtime_, level_ + 1);
    }
    else {
      result_ = grammar(runtime_, level_ + 1);
    }
    return result_;
  }

  /* ********************************************************** */
  // 'id'
  public static boolean element(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ELEMENT, "<element>");
    result_ = consumeToken(runtime_, "id");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'name' '->' element
  public static boolean entry(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "entry")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ENTRY, "<entry>");
    result_ = consumeToken(runtime_, "name");
    result_ = result_ && consumeToken(runtime_, "->");
    result_ = result_ && element(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // list | map
  static boolean grammar(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "grammar")) return false;
    boolean result_;
    result_ = list(runtime_, level_ + 1);
    if (!result_) result_ = map(runtime_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // '(' element (',' element) * ')'
  public static boolean list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, LIST, "<list>");
    result_ = consumeToken(runtime_, "(");
    result_ = result_ && element(runtime_, level_ + 1);
    result_ = result_ && list_2(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, ")");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // (',' element) *
  private static boolean list_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!list_2_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "list_2", pos_)) break;
    }
    return true;
  }

  // ',' element
  private static boolean list_2_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ",");
    result_ = result_ && element(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '(' entry (',' entry) * ')'
  public static boolean map(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "map")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, MAP, "<map>");
    result_ = consumeToken(runtime_, "(");
    result_ = result_ && entry(runtime_, level_ + 1);
    result_ = result_ && map_2(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, ")");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // (',' entry) *
  private static boolean map_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "map_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!map_2_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "map_2", pos_)) break;
    }
    return true;
  }

  // ',' entry
  private static boolean map_2_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "map_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ",");
    result_ = result_ && entry(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

}