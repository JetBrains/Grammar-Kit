// ---- Fixes.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedTypes
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class Fixes(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: SyntaxElementType, builder_: SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(GeneratedTypes.RECURSIVE_EXTEND_A, GeneratedTypes.RECURSIVE_EXTEND_B),
      create_token_set_(GeneratedTypes.A_EXPR, GeneratedTypes.B_EXPR, GeneratedTypes.EXPR, GeneratedTypes.LEFT_EXPR,
        GeneratedTypes.SOME_EXPR),
    )

    /* ********************************************************** */
    // &<Foo  predicate> <Foo (ﾉ´･ω･)ﾉ ﾐ ┸━┸ inner>
    internal fun Foo(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "Foo")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = Foo_0(builder_, level_ + 1)
      result_ = result_ && Foo__ﾉ__ω__ﾉ_ﾐ_____inner(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, Fixes::Foo__recovery)
      return result_
    }

    // &<Foo  predicate>
    private fun Foo_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "Foo_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = Foo__predicate(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    internal fun Foo__predicate(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    internal fun Foo__recovery(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    fun Foo__ﾉ__ω__ﾉ_ﾐ_____inner(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedTypes.FOO__ﾉ__Ω__ﾉ_ﾐ_____INNER, true)
      return true
    }

    /* ********************************************************** */
    // orRestriction
    fun a_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "a_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.A_EXPR, "<a expr>")
      result_ = orRestriction(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // singleRestriction ( "&&" singleRestriction ) *
    internal fun andRestriction(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "andRestriction")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = singleRestriction(builder_, level_ + 1)
      result_ = result_ && andRestriction_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // ( "&&" singleRestriction ) *
    private fun andRestriction_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "andRestriction_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!andRestriction_1_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "andRestriction_1", pos_)) break
      }
      return true
    }

    // "&&" singleRestriction
    private fun andRestriction_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "andRestriction_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "&&")
      result_ = result_ && singleRestriction(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // andRestriction
    fun b_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "b_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.B_EXPR, "<b expr>")
      result_ = andRestriction(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // expr A erl_tail
    fun erl_list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "erl_list")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.ERL_LIST, "<erl list>")
      result_ = expr(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedTypes.A)
      pinned_ = result_ // pin = 2
      result_ = result_ && erl_tail(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // zome | A zome | '&&' expr some erl_tail
    internal fun erl_tail(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "erl_tail")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = zome(builder_, level_ + 1)
      if (!result_) result_ = erl_tail_1(builder_, level_ + 1)
      if (!result_) result_ = erl_tail_2(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // A zome
    private fun erl_tail_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "erl_tail_1")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      pinned_ = result_ // pin = 1
      result_ = result_ && zome(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // '&&' expr some erl_tail
    private fun erl_tail_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "erl_tail_2")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, "&&")
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, expr(builder_, level_ + 1))
      result_ = pinned_ && report_error_(builder_, some(builder_, level_ + 1)) && result_
      result_ = pinned_ && erl_tail(builder_, level_ + 1) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // (A erl_tail_bad)*
    internal fun erl_tail_bad(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "erl_tail_bad")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!erl_tail_bad_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "erl_tail_bad", pos_)) break
      }
      return true
    }

    // A erl_tail_bad
    private fun erl_tail_bad_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "erl_tail_bad_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      result_ = result_ && erl_tail_bad(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // a_expr | b_expr
    fun expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.EXPR, "<expr>")
      result_ = a_expr(builder_, level_ + 1)
      if (!result_) result_ = b_expr(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A (some A | A some A)
    fun `import_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`import_$`")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.A)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.IMPORT, null)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      pinned_ = result_ // pin = 1
      result_ = result_ && import_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // some A | A some A
    private fun import_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "import_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = import_1_0(builder_, level_ + 1)
      if (!result_) result_ = import_1_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // some A
    private fun import_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "import_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = some(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedTypes.A)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // A some A
    private fun import_1_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "import_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      result_ = result_ && some(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedTypes.A)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // expr
    fun left_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "left_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.LEFT_EXPR, "<left expr>")
      result_ = expr(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    internal fun `meta2_$`(p: Parser, q: Parser): Parser {
      return Parser { builder_, level_ -> meta2(builder_, level_ + 1, p, q) }
    }

    // <<p>> <<q>>
    internal fun meta2(builder_: SyntaxTreeBuilder, level_: Int, p: Parser, q: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "meta2")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = p.parse(builder_, level_)
      result_ = result_ && q.parse(builder_, level_)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // "1" (("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>)
    internal fun nested_meta_pin(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "nested_meta_pin")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, "1")
      pinned_ = result_ // pin = 1
      result_ = result_ && nested_meta_pin_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // ("2") <<meta2 <<meta2 ("3" "a") !"b">> <<meta2 ("4" "a") ("5" "a")>>>>
    private fun nested_meta_pin_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "nested_meta_pin_1")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = nested_meta_pin_1_0(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && meta2(builder_, level_ + 1, nested_meta_pin_1_1_0_parser_, nested_meta_pin_1_1_1_parser_)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // ("2")
    private fun nested_meta_pin_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "2")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // "3" "a"
    private fun nested_meta_pin_1_1_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_0_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, "3")
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, "a")
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // !"b"
    private fun nested_meta_pin_1_1_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_0_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !consumeToken(builder_, "b")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // "4" "a"
    private fun nested_meta_pin_1_1_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_1_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, "4")
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, "a")
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // "5" "a"
    private fun nested_meta_pin_1_1_1_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "nested_meta_pin_1_1_1_1")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, "5")
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, "a")
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // A | private_named | private_unnamed
    fun not_optimized_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_optimized_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.NOT_OPTIMIZED_CHOICE, "<not optimized choice>")
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = private_named(builder_, level_ + 1)
      if (!result_) result_ = private_unnamed(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | private_named | private_unnamed
    internal fun optimized_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "optimized_choice")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = private_named(builder_, level_ + 1)
      if (!result_) result_ = private_unnamed(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // token-two | '#'
    internal fun optimized_choice2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "optimized_choice2")) return false
      if (!nextTokenIs(builder_, "", GeneratedTypes.TOKEN_THREE, GeneratedTypes.TOKEN_TWO)) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedTypes.TOKEN_TWO)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.TOKEN_THREE)
      return result_
    }

    /* ********************************************************** */
    // "foo" | "bar"
    internal fun optimized_choice3(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "optimized_choice3")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, "foo")
      if (!result_) result_ = consumeToken(builder_, "bar")
      return result_
    }

    /* ********************************************************** */
    // andRestriction ( "||" andRestriction ) *
    internal fun orRestriction(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "orRestriction")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = andRestriction(builder_, level_ + 1)
      result_ = result_ && orRestriction_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // ( "||" andRestriction ) *
    private fun orRestriction_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "orRestriction_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!orRestriction_1_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "orRestriction_1", pos_)) break
      }
      return true
    }

    // "||" andRestriction
    private fun orRestriction_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "orRestriction_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "||")
      result_ = result_ && andRestriction(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // A | &B A
    internal fun pinned_report(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_report")) return false
      if (!nextTokenIs(builder_, "", GeneratedTypes.A, GeneratedTypes.B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = pinned_report_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &B A
    private fun pinned_report_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_report_1")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = pinned_report_1_0(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, GeneratedTypes.A)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // &B
    private fun pinned_report_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_report_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = consumeToken(builder_, GeneratedTypes.B)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | &<<aux>> A
    internal fun pinned_report_ext(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_report_ext")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = pinned_report_ext_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &<<aux>> A
    private fun pinned_report_ext_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_report_ext_1")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = pinned_report_ext_1_0(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, GeneratedTypes.A)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // &<<aux>>
    private fun pinned_report_ext_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_report_ext_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = aux(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // "foo"
    internal fun private_named(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "private_named")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, null, "<named>")
      result_ = consumeToken(builder_, "foo")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // "foo"
    internal fun private_unnamed(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return consumeToken(builder_, "foo")
    }

    /* ********************************************************** */
    // some [recursive]
    internal fun recursive(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "recursive")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = some(builder_, level_ + 1)
      result_ = result_ && recursive_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // [recursive]
    private fun recursive_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "recursive_1")) return false
      recursive(builder_, level_ + 1)
      return true
    }

    /* ********************************************************** */
    fun recursive_extendA(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedTypes.RECURSIVE_EXTEND_A, true)
      return true
    }

    /* ********************************************************** */
    fun recursive_extendB(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedTypes.RECURSIVE_EXTEND_B, true)
      return true
    }

    /* ********************************************************** */
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // <<p>> (',' <<p>>)*
    internal fun sequence(builder_: SyntaxTreeBuilder, level_: Int, p: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "sequence")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = p.parse(builder_, level_)
      pinned_ = result_ // pin = 1
      result_ = result_ && sequence_1(builder_, level_ + 1, p)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // (',' <<p>>)*
    private fun sequence_1(builder_: SyntaxTreeBuilder, level_: Int, p: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "sequence_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!sequence_1_0(builder_, level_ + 1, p)) break
        if (!empty_element_parsed_guard_(builder_, "sequence_1", pos_)) break
      }
      return true
    }

    // ',' <<p>>
    private fun sequence_1_0(builder_: SyntaxTreeBuilder, level_: Int, p: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "sequence_1_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, ",")
      pinned_ = result_ // pin = 1
      result_ = result_ && p.parse(builder_, level_)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // A expr | '(' orRestriction ')'
    internal fun singleRestriction(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "singleRestriction")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = singleRestriction_0(builder_, level_ + 1)
      if (!result_) result_ = singleRestriction_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // A expr
    private fun singleRestriction_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "singleRestriction_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      result_ = result_ && expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // '(' orRestriction ')'
    private fun singleRestriction_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "singleRestriction_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "(")
      result_ = result_ && orRestriction(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ")")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // A
    fun some(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      exit_section_(builder_, marker_, GeneratedTypes.SOME, result_)
      return result_
    }

    /* ********************************************************** */
    // expr left_expr *
    fun some_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.SOME_EXPR, "<some expr>")
      result_ = expr(builder_, level_ + 1)
      result_ = result_ && some_expr_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // left_expr *
    private fun some_expr_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!left_expr(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "some_expr_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // <<sequence some>>
    fun some_seq(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_seq")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = sequence(builder_, level_ + 1, Fixes::some)
      exit_section_(builder_, marker_, GeneratedTypes.SOME_SEQ, result_)
      return result_
    }

    /* ********************************************************** */
    // expr two_usages_left | expr two_usages_left
    internal fun two_usages(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two_usages")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = two_usages_0(builder_, level_ + 1)
      if (!result_) result_ = two_usages_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // expr two_usages_left
    private fun two_usages_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two_usages_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = expr(builder_, level_ + 1)
      result_ = result_ && two_usages_left(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // expr two_usages_left
    private fun two_usages_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two_usages_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = expr(builder_, level_ + 1)
      result_ = result_ && two_usages_left(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    fun two_usages_left(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.TWO_USAGES_LEFT, null)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // recursive
    fun with_recursive(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "with_recursive")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = recursive(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.WITH_RECURSIVE, result_)
      return result_
    }

    /* ********************************************************** */
    // token-one | token-two
    fun zome(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "zome")) return false
      if (!nextTokenIs(builder_, "<zome>", GeneratedTypes.TOKEN_ONE, GeneratedTypes.TOKEN_TWO)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.ZOME, "<zome>")
      result_ = consumeToken(builder_, GeneratedTypes.TOKEN_ONE)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.TOKEN_TWO)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    private val nested_meta_pin_1_1_0_parser_: Parser = `meta2_$`(Fixes::nested_meta_pin_1_1_0_0, Fixes::nested_meta_pin_1_1_0_1)
    private val nested_meta_pin_1_1_1_parser_: Parser = `meta2_$`(Fixes::nested_meta_pin_1_1_1_0, Fixes::nested_meta_pin_1_1_1_1)
  }
}