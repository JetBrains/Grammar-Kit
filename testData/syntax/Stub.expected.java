// ---- test/FooParser.java -----------------
//header.txt
package test;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static test.FooSyntaxTypes.*;
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

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class FooParser {

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

    runtime_.init(parse_, EXTENDS_SETS_);
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

  public static final SyntaxElementTypeSet[] EXTENDS_SETS_ = new SyntaxElementTypeSet[] {
    create_token_set_(INTERFACE_TYPE, STRUCT_TYPE, TYPE),
  };

  /* ********************************************************** */
  // 'aa' element5
  public static boolean element1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ELEMENT_1, "<element 1>");
    result_ = consumeToken(runtime_, "aa");
    result_ = result_ && element5(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'bb' element4*
  public static boolean element2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ELEMENT_2, "<element 2>");
    result_ = consumeToken(runtime_, "bb");
    result_ = result_ && element2_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // element4*
  private static boolean element2_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element2_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!element4(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "element2_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'bb' element4
  public static boolean element3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ELEMENT_3, "<element 3>");
    result_ = consumeToken(runtime_, "bb");
    result_ = result_ && element4(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'bb' | element2
  public static boolean element4(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element4")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ELEMENT_4, "<element 4>");
    result_ = consumeToken(runtime_, "bb");
    if (!result_) result_ = element2(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'cc'
  public static boolean element5(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "element5")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ELEMENT_5, "<element 5>");
    result_ = consumeToken(runtime_, "cc");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'interface'
  public static boolean interface_type(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "interface_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, INTERFACE_TYPE, "<interface type>");
    result_ = consumeToken(runtime_, "interface");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // element1 | element2 | element3 | element4 | element5 | type
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root")) return false;
    boolean result_;
    result_ = element1(runtime_, level_ + 1);
    if (!result_) result_ = element2(runtime_, level_ + 1);
    if (!result_) result_ = element3(runtime_, level_ + 1);
    if (!result_) result_ = element4(runtime_, level_ + 1);
    if (!result_) result_ = element5(runtime_, level_ + 1);
    if (!result_) result_ = type(runtime_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // 'struct'
  public static boolean struct_type(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "struct_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, STRUCT_TYPE, "<struct type>");
    result_ = consumeToken(runtime_, "struct");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // interface_type | struct_type
  public static boolean type(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, TYPE, "<type>");
    result_ = interface_type(runtime_, level_ + 1);
    if (!result_) result_ = struct_type(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

}