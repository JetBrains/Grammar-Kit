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
    return file(runtime_, level_ + 1);
  }

  /* ********************************************************** */
  // list (';' list ) *
  static boolean file(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "file")) return false;
    if (!nextTokenIs(runtime_, PAREN1)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = list(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && file_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (';' list ) *
  private static boolean file_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "file_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!file_1_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "file_1", pos_)) break;
    }
    return true;
  }

  // ';' list
  private static boolean file_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "file_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, SEMI);
    pinned_ = result_; // pin = 1
    result_ = result_ && list(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // number
  public static boolean item(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "item")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ITEM, "<item>");
    result_ = consumeToken(runtime_, NUMBER);
    exit_section_(runtime_, level_, marker_, result_, false, item_auto_recover_);
    return result_;
  }

  /* ********************************************************** */
  // "(" [!")" item (',' item) * ] ")"
  public static boolean list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list")) return false;
    if (!nextTokenIs(runtime_, PAREN1)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, LIST, null);
    result_ = consumeToken(runtime_, PAREN1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, list_1(runtime_, level_ + 1));
    result_ = pinned_ && consumeToken(runtime_, PAREN2) && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // [!")" item (',' item) * ]
  private static boolean list_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list_1")) return false;
    list_1_0(runtime_, level_ + 1);
    return true;
  }

  // !")" item (',' item) *
  private static boolean list_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = list_1_0_0(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, item(runtime_, level_ + 1));
    result_ = pinned_ && list_1_0_2(runtime_, level_ + 1) && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // !")"
  private static boolean list_1_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !consumeToken(runtime_, PAREN2);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // (',' item) *
  private static boolean list_1_0_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list_1_0_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!list_1_0_2_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "list_1_0_2", pos_)) break;
    }
    return true;
  }

  // ',' item
  private static boolean list_1_0_2_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "list_1_0_2_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && item(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  static final SyntaxGeneratedParserRuntime.Parser item_auto_recover_ = (runtime_, level_) -> !nextTokenIsFast(runtime_, PAREN2, COMMA, SEMI);
}