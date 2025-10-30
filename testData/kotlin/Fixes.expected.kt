// ---- Fixes.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object Fixes {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, EXTENDS_SETS_)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(GeneratedSyntaxElementTypes.RECURSIVE_EXTEND_A, GeneratedSyntaxElementTypes.RECURSIVE_EXTEND_B),
    create_token_set_(GeneratedSyntaxElementTypes.A_EXPR, GeneratedSyntaxElementTypes.B_EXPR, GeneratedSyntaxElementTypes.EXPR, GeneratedSyntaxElementTypes.LEFT_EXPR,
      GeneratedSyntaxElementTypes.SOME_EXPR),
  )

  /* ********************************************************** */
  // &<Foo  predicate> <Foo (ﾉ´･ω･)ﾉ ﾐ ┸━┸ inner>
  internal fun Foo(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "Foo")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = Foo_0(runtime_, level_ + 1)
    result_ = result_ && Foo__ﾉ__ω__ﾉ_ﾐ_____inner(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, Fixes::Foo__recovery)
    return result_
  }

  // &<Foo  predicate>
  private fun Foo_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "Foo_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = Foo__predicate(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  internal fun Foo__predicate(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  internal fun Foo__recovery(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  fun Foo__ﾉ__ω__ﾉ_ﾐ_____inner(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_()
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER, true)
    return true
  }

  /* ********************************************************** */
  // orRestriction
  fun a_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "a_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.A_EXPR, "<a expr>")
    result_ = orRestriction(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // singleRestriction ( "&&" singleRestriction ) *
  internal fun andRestriction(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "andRestriction")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = singleRestriction(runtime_, level_ + 1)
    result_ = result_ && andRestriction_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // ( "&&" singleRestriction ) *
  private fun andRestriction_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "andRestriction_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!andRestriction_1_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("andRestriction_1", pos_)) break
    }
    return true
  }

  // "&&" singleRestriction
  private fun andRestriction_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "andRestriction_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken("&&")
    result_ = result_ && singleRestriction(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // andRestriction
  fun b_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "b_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.B_EXPR, "<b expr>")
    result_ = andRestriction(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // expr A erl_tail
  fun erl_list(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "erl_list")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ERL_LIST, "<erl list>")
    result_ = expr(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    pinned_ = result_ // pin = 2
    result_ = result_ && erl_tail(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // zome | A zome | '&&' expr some erl_tail
  internal fun erl_tail(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "erl_tail")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = zome(runtime_, level_ + 1)
    if (!result_) result_ = erl_tail_1(runtime_, level_ + 1)
    if (!result_) result_ = erl_tail_2(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // A zome
  private fun erl_tail_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "erl_tail_1")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    pinned_ = result_ // pin = 1
    result_ = result_ && zome(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // '&&' expr some erl_tail
  private fun erl_tail_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "erl_tail_2")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken("&&")
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(expr(runtime_, level_ + 1))
    result_ = pinned_ && runtime_.report_error_(some(runtime_, level_ + 1)) && result_
    result_ = pinned_ && erl_tail(runtime_, level_ + 1) && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // (A erl_tail_bad)*
  internal fun erl_tail_bad(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "erl_tail_bad")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!erl_tail_bad_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("erl_tail_bad", pos_)) break
    }
    return true
  }

  // A erl_tail_bad
  private fun erl_tail_bad_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "erl_tail_bad_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    result_ = result_ && erl_tail_bad(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // a_expr | b_expr
  fun expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.EXPR, "<expr>")
    result_ = a_expr(runtime_, level_ + 1)
    if (!result_) result_ = b_expr(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A (some A | A some A)
  fun import__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "import__")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.IMPORT, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    pinned_ = result_ // pin = 1
    result_ = result_ && import_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // some A | A some A
  private fun import_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "import_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = import_1_0(runtime_, level_ + 1)
    if (!result_) result_ = import_1_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // some A
  private fun import_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "import_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = some(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // A some A
  private fun import_1_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "import_1_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    result_ = result_ && some(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // expr
  fun left_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "left_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.LEFT_EXPR, "<left expr>")
    result_ = expr(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  internal fun meta2__(p: Parser, q: Parser): Parser {
    return { runtime_, level_ -> meta2(runtime_, level_ + 1, p, q) }
  }

  // <<p>> <<q>>
  internal fun meta2(runtime_: SyntaxGeneratedParserRuntime, level_: Int, p: Parser, q: Parser): Boolean {
    if (!runtime_.recursion_guard_(level_, "meta2")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = p.parse(runtime_, level_)
    result_ = result_ && q.parse(runtime_, level_)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // "1" (("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>)
  internal fun nested_meta_pin(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "nested_meta_pin")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken("1")
    pinned_ = result_ // pin = 1
    result_ = result_ && nested_meta_pin_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // ("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>
  private fun nested_meta_pin_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "nested_meta_pin_1")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = nested_meta_pin_1_0(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && meta2(runtime_, level_ + 1, nested_meta_pin_1_1_0_parser_, nested_meta_pin_1_1_1_parser_)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // ("2")
  private fun nested_meta_pin_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "nested_meta_pin_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken("2")
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // "3" "a"
  private fun nested_meta_pin_1_1_0_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "nested_meta_pin_1_1_0_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken("3")
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken("a")
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // !"b"
  private fun nested_meta_pin_1_1_0_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "nested_meta_pin_1_1_0_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !runtime_.consumeToken("b")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // "4" "a"
  private fun nested_meta_pin_1_1_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "nested_meta_pin_1_1_1_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken("4")
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken("a")
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // "5" "a"
  private fun nested_meta_pin_1_1_1_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "nested_meta_pin_1_1_1_1")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken("5")
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken("a")
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // A | private_named | private_unnamed
  fun not_optimized_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "not_optimized_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.NOT_OPTIMIZED_CHOICE, "<not optimized choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = private_named(runtime_, level_ + 1)
    if (!result_) result_ = private_unnamed(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | private_named | private_unnamed
  internal fun optimized_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "optimized_choice")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = private_named(runtime_, level_ + 1)
    if (!result_) result_ = private_unnamed(runtime_, level_ + 1)
    return result_
  }

  /* ********************************************************** */
  // token-two | '#'
  internal fun optimized_choice2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "optimized_choice2")) return false
    if (!runtime_.nextTokenIs("", GeneratedSyntaxElementTypes.TOKEN_THREE, GeneratedSyntaxElementTypes.TOKEN_TWO)) return false
    var result_: Boolean
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN_TWO)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN_THREE)
    return result_
  }

  /* ********************************************************** */
  // "foo" | "bar"
  internal fun optimized_choice3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "optimized_choice3")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken("foo")
    if (!result_) result_ = runtime_.consumeToken("bar")
    return result_
  }

  /* ********************************************************** */
  // andRestriction ( "||" andRestriction ) *
  internal fun orRestriction(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "orRestriction")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = andRestriction(runtime_, level_ + 1)
    result_ = result_ && orRestriction_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // ( "||" andRestriction ) *
  private fun orRestriction_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "orRestriction_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!orRestriction_1_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("orRestriction_1", pos_)) break
    }
    return true
  }

  // "||" andRestriction
  private fun orRestriction_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "orRestriction_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken("||")
    result_ = result_ && andRestriction(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // A | &B A
  internal fun pinned_report(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_report")) return false
    if (!runtime_.nextTokenIs("", GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = pinned_report_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // &B A
  private fun pinned_report_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_report_1")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = pinned_report_1_0(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // &B
  private fun pinned_report_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_report_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | &<<aux>> A
  internal fun pinned_report_ext(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_report_ext")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = pinned_report_ext_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // &<<aux>> A
  private fun pinned_report_ext_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_report_ext_1")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = pinned_report_ext_1_0(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // &<<aux>>
  private fun pinned_report_ext_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_report_ext_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = aux(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // "foo"
  internal fun private_named(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "private_named")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, null, "<named>")
    result_ = runtime_.consumeToken("foo")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // "foo"
  internal fun private_unnamed(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return runtime_.consumeToken("foo")
  }

  /* ********************************************************** */
  // some [recursive]
  internal fun recursive(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "recursive")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = some(runtime_, level_ + 1)
    result_ = result_ && recursive_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // [recursive]
  private fun recursive_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "recursive_1")) return false
    recursive(runtime_, level_ + 1)
    return true
  }

  /* ********************************************************** */
  fun recursive_extendA(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_()
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.RECURSIVE_EXTEND_A, true)
    return true
  }

  /* ********************************************************** */
  fun recursive_extendB(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_()
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.RECURSIVE_EXTEND_B, true)
    return true
  }

  /* ********************************************************** */
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // <<p>> (',' <<p>>)*
  internal fun sequence(runtime_: SyntaxGeneratedParserRuntime, level_: Int, p: Parser): Boolean {
    if (!runtime_.recursion_guard_(level_, "sequence")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = p.parse(runtime_, level_)
    pinned_ = result_ // pin = 1
    result_ = result_ && sequence_1(runtime_, level_ + 1, p)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // (',' <<p>>)*
  private fun sequence_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int, p: Parser): Boolean {
    if (!runtime_.recursion_guard_(level_, "sequence_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!sequence_1_0(runtime_, level_ + 1, p)) break
      if (!runtime_.empty_element_parsed_guard_("sequence_1", pos_)) break
    }
    return true
  }

  // ',' <<p>>
  private fun sequence_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int, p: Parser): Boolean {
    if (!runtime_.recursion_guard_(level_, "sequence_1_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken(",")
    pinned_ = result_ // pin = 1
    result_ = result_ && p.parse(runtime_, level_)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // A expr | '(' orRestriction ')'
  internal fun singleRestriction(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "singleRestriction")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = singleRestriction_0(runtime_, level_ + 1)
    if (!result_) result_ = singleRestriction_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // A expr
  private fun singleRestriction_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "singleRestriction_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    result_ = result_ && expr(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // '(' orRestriction ')'
  private fun singleRestriction_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "singleRestriction_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken("(")
    result_ = result_ && orRestriction(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(")")
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // A
  fun some(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SOME, result_)
    return result_
  }

  /* ********************************************************** */
  // expr left_expr *
  fun some_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.SOME_EXPR, "<some expr>")
    result_ = expr(runtime_, level_ + 1)
    result_ = result_ && some_expr_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // left_expr *
  private fun some_expr_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!left_expr(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("some_expr_1", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // <<sequence some>>
  fun some_seq(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_seq")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = sequence(runtime_, level_ + 1, Fixes::some)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SOME_SEQ, result_)
    return result_
  }

  /* ********************************************************** */
  // expr two_usages_left | expr two_usages_left
  internal fun two_usages(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "two_usages")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = two_usages_0(runtime_, level_ + 1)
    if (!result_) result_ = two_usages_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // expr two_usages_left
  private fun two_usages_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "two_usages_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = expr(runtime_, level_ + 1)
    result_ = result_ && two_usages_left(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // expr two_usages_left
  private fun two_usages_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "two_usages_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = expr(runtime_, level_ + 1)
    result_ = result_ && two_usages_left(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  fun two_usages_left(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.TWO_USAGES_LEFT, null)
    runtime_.exit_section_(level_, marker_, true, false, null)
    return true
  }

  /* ********************************************************** */
  // recursive
  fun with_recursive(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "with_recursive")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = recursive(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.WITH_RECURSIVE, result_)
    return result_
  }

  /* ********************************************************** */
  // token-one | token-two
  fun zome(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "zome")) return false
    if (!runtime_.nextTokenIs("<zome>", GeneratedSyntaxElementTypes.TOKEN_ONE, GeneratedSyntaxElementTypes.TOKEN_TWO)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ZOME, "<zome>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN_ONE)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN_TWO)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  private val nested_meta_pin_1_1_0_parser_: Parser = meta2__(Fixes::nested_meta_pin_1_1_0_0, Fixes::nested_meta_pin_1_1_0_1)
  private val nested_meta_pin_1_1_1_parser_: Parser = meta2__(Fixes::nested_meta_pin_1_1_1_0, Fixes::nested_meta_pin_1_1_1_1)
}