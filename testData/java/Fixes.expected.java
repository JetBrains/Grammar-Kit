// ---- Fixes.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Fixes implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(RECURSIVE_EXTEND_A, RECURSIVE_EXTEND_B),
    create_token_set_(A_EXPR, B_EXPR, EXPR, LEFT_EXPR,
      SOME_EXPR),
  };

  /* ********************************************************** */
  // &<Foo  predicate> <Foo (ﾉ´･ω･)ﾉ ﾐ ┸━┸ inner>
  static boolean Foo(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Foo")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = Foo_0(builder_, level_ + 1);
    result_ = result_ && Foo__ﾉ__ω__ﾉ_ﾐ_____inner(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, Fixes::Foo__recovery);
    return result_;
  }

  // &<Foo  predicate>
  private static boolean Foo_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Foo_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = Foo__predicate(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static boolean Foo__predicate(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  static boolean Foo__recovery(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  public static boolean Foo__ﾉ__ω__ﾉ_ﾐ_____inner(PsiBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER, true);
    return true;
  }

  /* ********************************************************** */
  // orRestriction
  public static boolean a_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, A_EXPR, "<a expr>");
    result_ = orRestriction(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // singleRestriction ( "&&" singleRestriction ) *
  static boolean andRestriction(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = singleRestriction(builder_, level_ + 1);
    result_ = result_ && andRestriction_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( "&&" singleRestriction ) *
  private static boolean andRestriction_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andRestriction_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!andRestriction_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "andRestriction_1", pos_)) break;
    }
    return true;
  }

  // "&&" singleRestriction
  private static boolean andRestriction_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "andRestriction_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "&&");
    result_ = result_ && singleRestriction(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // andRestriction
  public static boolean b_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, B_EXPR, "<b expr>");
    result_ = andRestriction(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // expr A erl_tail
  public static boolean erl_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "erl_list")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ERL_LIST, "<erl list>");
    result_ = expr(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, A);
    pinned_ = result_; // pin = 2
    result_ = result_ && erl_tail(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // zome | A zome | '&&' expr some erl_tail
  static boolean erl_tail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "erl_tail")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = zome(builder_, level_ + 1);
    if (!result_) result_ = erl_tail_1(builder_, level_ + 1);
    if (!result_) result_ = erl_tail_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // A zome
  private static boolean erl_tail_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "erl_tail_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, A);
    pinned_ = result_; // pin = 1
    result_ = result_ && zome(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // '&&' expr some erl_tail
  private static boolean erl_tail_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "erl_tail_2")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, "&&");
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, expr(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, some(builder_, level_ + 1)) && result_;
    result_ = pinned_ && erl_tail(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // (A erl_tail_bad)*
  static boolean erl_tail_bad(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "erl_tail_bad")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!erl_tail_bad_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "erl_tail_bad", pos_)) break;
    }
    return true;
  }

  // A erl_tail_bad
  private static boolean erl_tail_bad_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "erl_tail_bad_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    result_ = result_ && erl_tail_bad(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // a_expr | b_expr
  public static boolean expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, EXPR, "<expr>");
    result_ = a_expr(builder_, level_ + 1);
    if (!result_) result_ = b_expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A (some A | A some A)
  public static boolean import_$(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_$")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, IMPORT, null);
    result_ = consumeToken(builder_, A);
    pinned_ = result_; // pin = 1
    result_ = result_ && import_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // some A | A some A
  private static boolean import_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = import_1_0(builder_, level_ + 1);
    if (!result_) result_ = import_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // some A
  private static boolean import_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = some(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, A);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // A some A
  private static boolean import_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "import_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    result_ = result_ && some(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, A);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr
  public static boolean left_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "left_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, LEFT_EXPR, "<left expr>");
    result_ = expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static Parser meta2_$(Parser p, Parser q) {
    return (builder_, level_) -> meta2(builder_, level_ + 1, p, q);
  }

  // <<p>> <<q>>
  static boolean meta2(PsiBuilder builder_, int level_, Parser p, Parser q) {
    if (!recursion_guard_(builder_, level_, "meta2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = p.parse(builder_, level_);
    result_ = result_ && q.parse(builder_, level_);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // "1" (("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>)
  static boolean nested_meta_pin(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nested_meta_pin")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, "1");
    pinned_ = result_; // pin = 1
    result_ = result_ && nested_meta_pin_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>
  private static boolean nested_meta_pin_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nested_meta_pin_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = nested_meta_pin_1_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && meta2(builder_, level_ + 1, nested_meta_pin_1_1_0_parser_, nested_meta_pin_1_1_1_parser_);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ("2")
  private static boolean nested_meta_pin_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "2");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // "3" "a"
  private static boolean nested_meta_pin_1_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_0_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, "3");
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, "a");
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // !"b"
  private static boolean nested_meta_pin_1_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, "b");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // "4" "a"
  private static boolean nested_meta_pin_1_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, "4");
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, "a");
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // "5" "a"
  private static boolean nested_meta_pin_1_1_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_1_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, "5");
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, "a");
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A | private_named | private_unnamed
  public static boolean not_optimized_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_optimized_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NOT_OPTIMIZED_CHOICE, "<not optimized choice>");
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = private_named(builder_, level_ + 1);
    if (!result_) result_ = private_unnamed(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | private_named | private_unnamed
  static boolean optimized_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "optimized_choice")) return false;
    boolean result_;
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = private_named(builder_, level_ + 1);
    if (!result_) result_ = private_unnamed(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // token-two | '#'
  static boolean optimized_choice2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "optimized_choice2")) return false;
    if (!nextTokenIs(builder_, "", TOKEN_THREE, TOKEN_TWO)) return false;
    boolean result_;
    result_ = consumeToken(builder_, TOKEN_TWO);
    if (!result_) result_ = consumeToken(builder_, TOKEN_THREE);
    return result_;
  }

  /* ********************************************************** */
  // "foo" | "bar"
  static boolean optimized_choice3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "optimized_choice3")) return false;
    boolean result_;
    result_ = consumeToken(builder_, "foo");
    if (!result_) result_ = consumeToken(builder_, "bar");
    return result_;
  }

  /* ********************************************************** */
  // andRestriction ( "||" andRestriction ) *
  static boolean orRestriction(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = andRestriction(builder_, level_ + 1);
    result_ = result_ && orRestriction_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( "||" andRestriction ) *
  private static boolean orRestriction_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orRestriction_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!orRestriction_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "orRestriction_1", pos_)) break;
    }
    return true;
  }

  // "||" andRestriction
  private static boolean orRestriction_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "orRestriction_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "||");
    result_ = result_ && andRestriction(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // A | &B A
  static boolean pinned_report(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_report")) return false;
    if (!nextTokenIs(builder_, "", A, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = pinned_report_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &B A
  private static boolean pinned_report_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_report_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = pinned_report_1_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, A);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // &B
  private static boolean pinned_report_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_report_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, B);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // A | &<<aux>> A
  static boolean pinned_report_ext(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_report_ext")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = pinned_report_ext_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &<<aux>> A
  private static boolean pinned_report_ext_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_report_ext_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = pinned_report_ext_1_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, A);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // &<<aux>>
  private static boolean pinned_report_ext_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_report_ext_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = aux(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // "foo"
  static boolean private_named(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "private_named")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null, "<named>");
    result_ = consumeToken(builder_, "foo");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // "foo"
  static boolean private_unnamed(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, "foo");
  }

  /* ********************************************************** */
  // some [recursive]
  static boolean recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = some(builder_, level_ + 1);
    result_ = result_ && recursive_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [recursive]
  private static boolean recursive_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive_1")) return false;
    recursive(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  public static boolean recursive_extendA(PsiBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, RECURSIVE_EXTEND_A, true);
    return true;
  }

  /* ********************************************************** */
  public static boolean recursive_extendB(PsiBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, RECURSIVE_EXTEND_B, true);
    return true;
  }

  /* ********************************************************** */
  static boolean root(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // <<p>> (',' <<p>>)*
  static boolean sequence(PsiBuilder builder_, int level_, Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = p.parse(builder_, level_);
    pinned_ = result_; // pin = 1
    result_ = result_ && sequence_1(builder_, level_ + 1, p);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' <<p>>)*
  private static boolean sequence_1(PsiBuilder builder_, int level_, Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!sequence_1_0(builder_, level_ + 1, p)) break;
      if (!empty_element_parsed_guard_(builder_, "sequence_1", pos_)) break;
    }
    return true;
  }

  // ',' <<p>>
  private static boolean sequence_1_0(PsiBuilder builder_, int level_, Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && p.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A expr | '(' orRestriction ')'
  static boolean singleRestriction(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "singleRestriction")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = singleRestriction_0(builder_, level_ + 1);
    if (!result_) result_ = singleRestriction_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // A expr
  private static boolean singleRestriction_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "singleRestriction_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    result_ = result_ && expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '(' orRestriction ')'
  private static boolean singleRestriction_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "singleRestriction_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "(");
    result_ = result_ && orRestriction(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ")");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // A
  public static boolean some(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    exit_section_(builder_, marker_, SOME, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr left_expr *
  public static boolean some_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, SOME_EXPR, "<some expr>");
    result_ = expr(builder_, level_ + 1);
    result_ = result_ && some_expr_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // left_expr *
  private static boolean some_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!left_expr(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "some_expr_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // <<sequence some>>
  public static boolean some_seq(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_seq")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = sequence(builder_, level_ + 1, Fixes::some);
    exit_section_(builder_, marker_, SOME_SEQ, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr two_usages_left | expr two_usages_left
  static boolean two_usages(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "two_usages")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = two_usages_0(builder_, level_ + 1);
    if (!result_) result_ = two_usages_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // expr two_usages_left
  private static boolean two_usages_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "two_usages_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expr(builder_, level_ + 1);
    result_ = result_ && two_usages_left(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // expr two_usages_left
  private static boolean two_usages_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "two_usages_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = expr(builder_, level_ + 1);
    result_ = result_ && two_usages_left(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  public static boolean two_usages_left(PsiBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, TWO_USAGES_LEFT, null);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // recursive
  public static boolean with_recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "with_recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = recursive(builder_, level_ + 1);
    exit_section_(builder_, marker_, WITH_RECURSIVE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token-one | token-two
  public static boolean zome(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "zome")) return false;
    if (!nextTokenIs(builder_, "<zome>", TOKEN_ONE, TOKEN_TWO)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ZOME, "<zome>");
    result_ = consumeToken(builder_, TOKEN_ONE);
    if (!result_) result_ = consumeToken(builder_, TOKEN_TWO);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  private static final Parser nested_meta_pin_1_1_0_parser_ = meta2_$(Fixes::nested_meta_pin_1_1_0_0, Fixes::nested_meta_pin_1_1_0_1);
  private static final Parser nested_meta_pin_1_1_1_parser_ = meta2_$(Fixes::nested_meta_pin_1_1_1_0, Fixes::nested_meta_pin_1_1_1_1);
}