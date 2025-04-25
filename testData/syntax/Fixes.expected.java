// ---- Fixes.java -----------------
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
public class Fixes {

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
    create_token_set_(RECURSIVE_EXTEND_A, RECURSIVE_EXTEND_B),
    create_token_set_(A_EXPR, B_EXPR, EXPR, LEFT_EXPR,
      SOME_EXPR),
  };

  /* ********************************************************** */
  // &<Foo  predicate> <Foo (ﾉ´･ω･)ﾉ ﾐ ┸━┸ inner>
  static boolean Foo(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "Foo")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = Foo_0(runtime_, level_ + 1);
    result_ = result_ && Foo__ﾉ__ω__ﾉ_ﾐ_____inner(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, Fixes::Foo__recovery);
    return result_;
  }

  // &<Foo  predicate>
  private static boolean Foo_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "Foo_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = Foo__predicate(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static boolean Foo__predicate(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  static boolean Foo__recovery(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  public static boolean Foo__ﾉ__ω__ﾉ_ﾐ_____inner(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER, true);
    return true;
  }

  /* ********************************************************** */
  // orRestriction
  public static boolean a_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "a_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, A_EXPR, "<a expr>");
    result_ = orRestriction(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // singleRestriction ( "&&" singleRestriction ) *
  static boolean andRestriction(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "andRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = singleRestriction(runtime_, level_ + 1);
    result_ = result_ && andRestriction_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // ( "&&" singleRestriction ) *
  private static boolean andRestriction_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "andRestriction_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!andRestriction_1_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "andRestriction_1", pos_)) break;
    }
    return true;
  }

  // "&&" singleRestriction
  private static boolean andRestriction_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "andRestriction_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "&&");
    result_ = result_ && singleRestriction(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // andRestriction
  public static boolean b_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "b_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, B_EXPR, "<b expr>");
    result_ = andRestriction(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // expr A erl_tail
  public static boolean erl_list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "erl_list")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ERL_LIST, "<erl list>");
    result_ = expr(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, A);
    pinned_ = result_; // pin = 2
    result_ = result_ && erl_tail(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // zome | A zome | '&&' expr some erl_tail
  static boolean erl_tail(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "erl_tail")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = zome(runtime_, level_ + 1);
    if (!result_) result_ = erl_tail_1(runtime_, level_ + 1);
    if (!result_) result_ = erl_tail_2(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // A zome
  private static boolean erl_tail_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "erl_tail_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, A);
    pinned_ = result_; // pin = 1
    result_ = result_ && zome(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // '&&' expr some erl_tail
  private static boolean erl_tail_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "erl_tail_2")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, "&&");
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, expr(runtime_, level_ + 1));
    result_ = pinned_ && report_error_(runtime_, some(runtime_, level_ + 1)) && result_;
    result_ = pinned_ && erl_tail(runtime_, level_ + 1) && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // (A erl_tail_bad)*
  static boolean erl_tail_bad(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "erl_tail_bad")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!erl_tail_bad_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "erl_tail_bad", pos_)) break;
    }
    return true;
  }

  // A erl_tail_bad
  private static boolean erl_tail_bad_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "erl_tail_bad_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    result_ = result_ && erl_tail_bad(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // a_expr | b_expr
  public static boolean expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, EXPR, "<expr>");
    result_ = a_expr(runtime_, level_ + 1);
    if (!result_) result_ = b_expr(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A (some A | A some A)
  public static boolean import_$(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "import_$")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, IMPORT, null);
    result_ = consumeToken(runtime_, A);
    pinned_ = result_; // pin = 1
    result_ = result_ && import_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // some A | A some A
  private static boolean import_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "import_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = import_1_0(runtime_, level_ + 1);
    if (!result_) result_ = import_1_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // some A
  private static boolean import_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "import_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = some(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, A);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // A some A
  private static boolean import_1_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "import_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    result_ = result_ && some(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, A);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr
  public static boolean left_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "left_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, LEFT_EXPR, "<left expr>");
    result_ = expr(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static SyntaxGeneratedParserRuntime.Parser meta2_$(SyntaxGeneratedParserRuntime.Parser p, SyntaxGeneratedParserRuntime.Parser q) {
    return (runtime_, level_) -> meta2(runtime_, level_ + 1, p, q);
  }

  // <<p>> <<q>>
  static boolean meta2(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser p, SyntaxGeneratedParserRuntime.Parser q) {
    if (!recursion_guard_(runtime_, level_, "meta2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = p.parse(runtime_, level_);
    result_ = result_ && q.parse(runtime_, level_);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // "1" (("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>)
  static boolean nested_meta_pin(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "nested_meta_pin")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, "1");
    pinned_ = result_; // pin = 1
    result_ = result_ && nested_meta_pin_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>
  private static boolean nested_meta_pin_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "nested_meta_pin_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = nested_meta_pin_1_0(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && meta2(runtime_, level_ + 1, nested_meta_pin_1_1_0_parser_, nested_meta_pin_1_1_1_parser_);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ("2")
  private static boolean nested_meta_pin_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "nested_meta_pin_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "2");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // "3" "a"
  private static boolean nested_meta_pin_1_1_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "nested_meta_pin_1_1_0_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, "3");
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(runtime_, "a");
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // !"b"
  private static boolean nested_meta_pin_1_1_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "nested_meta_pin_1_1_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !consumeToken(runtime_, "b");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // "4" "a"
  private static boolean nested_meta_pin_1_1_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "nested_meta_pin_1_1_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, "4");
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(runtime_, "a");
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // "5" "a"
  private static boolean nested_meta_pin_1_1_1_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "nested_meta_pin_1_1_1_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, "5");
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(runtime_, "a");
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A | private_named | private_unnamed
  public static boolean not_optimized_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "not_optimized_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, NOT_OPTIMIZED_CHOICE, "<not optimized choice>");
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = private_named(runtime_, level_ + 1);
    if (!result_) result_ = private_unnamed(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | private_named | private_unnamed
  static boolean optimized_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "optimized_choice")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = private_named(runtime_, level_ + 1);
    if (!result_) result_ = private_unnamed(runtime_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // token-two | '#'
  static boolean optimized_choice2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "optimized_choice2")) return false;
    if (!nextTokenIs(runtime_, "", TOKEN_THREE, TOKEN_TWO)) return false;
    boolean result_;
    result_ = consumeToken(runtime_, TOKEN_TWO);
    if (!result_) result_ = consumeToken(runtime_, TOKEN_THREE);
    return result_;
  }

  /* ********************************************************** */
  // "foo" | "bar"
  static boolean optimized_choice3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "optimized_choice3")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, "foo");
    if (!result_) result_ = consumeToken(runtime_, "bar");
    return result_;
  }

  /* ********************************************************** */
  // andRestriction ( "||" andRestriction ) *
  static boolean orRestriction(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "orRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = andRestriction(runtime_, level_ + 1);
    result_ = result_ && orRestriction_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // ( "||" andRestriction ) *
  private static boolean orRestriction_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "orRestriction_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!orRestriction_1_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "orRestriction_1", pos_)) break;
    }
    return true;
  }

  // "||" andRestriction
  private static boolean orRestriction_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "orRestriction_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "||");
    result_ = result_ && andRestriction(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // A | &B A
  static boolean pinned_report(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_report")) return false;
    if (!nextTokenIs(runtime_, "", A, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = pinned_report_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // &B A
  private static boolean pinned_report_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_report_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = pinned_report_1_0(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(runtime_, A);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // &B
  private static boolean pinned_report_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_report_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = consumeToken(runtime_, B);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | &<<aux>> A
  static boolean pinned_report_ext(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_report_ext")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = pinned_report_ext_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // &<<aux>> A
  private static boolean pinned_report_ext_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_report_ext_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = pinned_report_ext_1_0(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(runtime_, A);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // &<<aux>>
  private static boolean pinned_report_ext_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_report_ext_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = aux(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // "foo"
  static boolean private_named(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "private_named")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, null, "<named>");
    result_ = consumeToken(runtime_, "foo");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // "foo"
  static boolean private_unnamed(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return consumeToken(runtime_, "foo");
  }

  /* ********************************************************** */
  // some [recursive]
  static boolean recursive(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "recursive")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = some(runtime_, level_ + 1);
    result_ = result_ && recursive_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // [recursive]
  private static boolean recursive_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "recursive_1")) return false;
    recursive(runtime_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  public static boolean recursive_extendA(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, RECURSIVE_EXTEND_A, true);
    return true;
  }

  /* ********************************************************** */
  public static boolean recursive_extendB(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, RECURSIVE_EXTEND_B, true);
    return true;
  }

  /* ********************************************************** */
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // <<p>> (',' <<p>>)*
  static boolean sequence(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser p) {
    if (!recursion_guard_(runtime_, level_, "sequence")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = p.parse(runtime_, level_);
    pinned_ = result_; // pin = 1
    result_ = result_ && sequence_1(runtime_, level_ + 1, p);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' <<p>>)*
  private static boolean sequence_1(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser p) {
    if (!recursion_guard_(runtime_, level_, "sequence_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!sequence_1_0(runtime_, level_ + 1, p)) break;
      if (!empty_element_parsed_guard_(runtime_, "sequence_1", pos_)) break;
    }
    return true;
  }

  // ',' <<p>>
  private static boolean sequence_1_0(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser p) {
    if (!recursion_guard_(runtime_, level_, "sequence_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && p.parse(runtime_, level_);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A expr | '(' orRestriction ')'
  static boolean singleRestriction(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "singleRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = singleRestriction_0(runtime_, level_ + 1);
    if (!result_) result_ = singleRestriction_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // A expr
  private static boolean singleRestriction_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "singleRestriction_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    result_ = result_ && expr(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // '(' orRestriction ')'
  private static boolean singleRestriction_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "singleRestriction_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "(");
    result_ = result_ && orRestriction(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, ")");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // A
  public static boolean some(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    exit_section_(runtime_, marker_, SOME, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr left_expr *
  public static boolean some_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, SOME_EXPR, "<some expr>");
    result_ = expr(runtime_, level_ + 1);
    result_ = result_ && some_expr_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // left_expr *
  private static boolean some_expr_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!left_expr(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "some_expr_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // <<sequence some>>
  public static boolean some_seq(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_seq")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = sequence(runtime_, level_ + 1, Fixes::some);
    exit_section_(runtime_, marker_, SOME_SEQ, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr two_usages_left | expr two_usages_left
  static boolean two_usages(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "two_usages")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = two_usages_0(runtime_, level_ + 1);
    if (!result_) result_ = two_usages_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // expr two_usages_left
  private static boolean two_usages_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "two_usages_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = expr(runtime_, level_ + 1);
    result_ = result_ && two_usages_left(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // expr two_usages_left
  private static boolean two_usages_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "two_usages_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = expr(runtime_, level_ + 1);
    result_ = result_ && two_usages_left(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  public static boolean two_usages_left(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, TWO_USAGES_LEFT, null);
    exit_section_(runtime_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // recursive
  public static boolean with_recursive(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "with_recursive")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = recursive(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, WITH_RECURSIVE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token-one | token-two
  public static boolean zome(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "zome")) return false;
    if (!nextTokenIs(runtime_, "<zome>", TOKEN_ONE, TOKEN_TWO)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ZOME, "<zome>");
    result_ = consumeToken(runtime_, TOKEN_ONE);
    if (!result_) result_ = consumeToken(runtime_, TOKEN_TWO);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  private static final SyntaxGeneratedParserRuntime.Parser nested_meta_pin_1_1_0_parser_ = meta2_$(Fixes::nested_meta_pin_1_1_0_0, Fixes::nested_meta_pin_1_1_0_1);
  private static final SyntaxGeneratedParserRuntime.Parser nested_meta_pin_1_1_1_parser_ = meta2_$(Fixes::nested_meta_pin_1_1_1_0, Fixes::nested_meta_pin_1_1_1_1);
}