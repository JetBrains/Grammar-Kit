// ---- ExternalRules.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class ExternalRules(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

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
      var result_: Boolean
      if (root_ == GeneratedSyntaxElementTypes.EXTRA_ROOT) {
        result_ = ExternalRules2.extra_root(builder_, level_ + 1)
      }
      else {
        result_ = root(builder_, level_ + 1)
      }
      return result_
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(GeneratedSyntaxElementTypes.COLLAPSE_ONE, GeneratedSyntaxElementTypes.COLLAPSE_TWO),
    )

    /* ********************************************************** */
    // <<uniqueListOf one>>
    fun collapse_one(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "collapse_one")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.COLLAPSE_ONE, "<collapse one>")
      result_ = uniqueListOf(builder_, level_ + 1, ExternalRules::one)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    fun collapse_two(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.COLLAPSE_TWO, true)
      return true
    }

    /* ********************************************************** */
    internal fun `comma_list_$`(param: Parser): Parser {
      return Parser { builder_, level_ -> comma_list(builder_, level_ + 1, param) }
    }

    // <<param>> (',' <<param>>) *
    fun comma_list(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_list")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = param.parse(builder_, level_)
      result_ = result_ && comma_list_1(builder_, level_ + 1, param)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.COMMA_LIST, result_)
      return result_
    }

    // (',' <<param>>) *
    private fun comma_list_1(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_list_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!comma_list_1_0(builder_, level_ + 1, param)) break
        if (!empty_element_parsed_guard_(builder_, "comma_list_1", pos_)) break
      }
      return true
    }

    // ',' <<param>>
    private fun comma_list_1_0(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_list_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.COMMA)
      result_ = result_ && param.parse(builder_, level_)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    internal fun `comma_list_pinned_$`(head: Parser, param: Parser): Parser {
      return Parser { builder_, level_ -> comma_list_pinned(builder_, level_ + 1, head, param) }
    }

    // <<head>> <<param>> (<<comma_list_tail <<param>>>>) *
    fun comma_list_pinned(builder_: SyntaxTreeBuilder, level_: Int, head: Parser, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_list_pinned")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = head.parse(builder_, level_)
      result_ = result_ && param.parse(builder_, level_)
      result_ = result_ && comma_list_pinned_2(builder_, level_ + 1, param)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.COMMA_LIST_PINNED, result_)
      return result_
    }

    // (<<comma_list_tail <<param>>>>) *
    private fun comma_list_pinned_2(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_list_pinned_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!comma_list_pinned_2_0(builder_, level_ + 1, param)) break
        if (!empty_element_parsed_guard_(builder_, "comma_list_pinned_2", pos_)) break
      }
      return true
    }

    // <<comma_list_tail <<param>>>>
    private fun comma_list_pinned_2_0(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      return comma_list_tail(builder_, level_ + 1, param)
    }

    /* ********************************************************** */
    // ',' <<param>>
    fun comma_list_tail(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_list_tail")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.COMMA)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.COMMA_LIST_TAIL, null)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.COMMA)
      pinned_ = result_ // pin = 1
      result_ = result_ && param.parse(builder_, level_)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // '(' <<param>> (',' <<param>>) * ')'
    internal fun comma_paren_list(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_paren_list")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.PAREN1)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.PAREN1)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, param.parse(builder_, level_))
      result_ = pinned_ && report_error_(builder_, comma_paren_list_2(builder_, level_ + 1, param)) && result_
      result_ = pinned_ && consumeToken(builder_, GeneratedSyntaxElementTypes.PAREN2) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // (',' <<param>>) *
    private fun comma_paren_list_2(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_paren_list_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!comma_paren_list_2_0(builder_, level_ + 1, param)) break
        if (!empty_element_parsed_guard_(builder_, "comma_paren_list_2", pos_)) break
      }
      return true
    }

    // ',' <<param>>
    private fun comma_paren_list_2_0(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "comma_paren_list_2_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.COMMA)
      result_ = result_ && param.parse(builder_, level_)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // empty_external
    internal fun empty_external_usage(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return null(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // <<>>
    internal fun empty_external_usage2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // !(',' | ';' | ')')
    internal fun item_recover(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "item_recover")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !item_recover_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ',' | ';' | ')'
    private fun item_recover_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "item_recover_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.COMMA)
      if (!result_) result_ = consumeToken(builder_, ";")
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.PAREN2)
      return result_
    }

    /* ********************************************************** */
    // <<head>> <<comma_list <<param>>>> (<<comma_list_tail <<comma_list <<param>>>>>>) *
    fun list_of_lists(builder_: SyntaxTreeBuilder, level_: Int, head: Parser, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "list_of_lists")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = head.parse(builder_, level_)
      result_ = result_ && comma_list(builder_, level_ + 1, param)
      result_ = result_ && list_of_lists_2(builder_, level_ + 1, param)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.LIST_OF_LISTS, result_)
      return result_
    }

    // (<<comma_list_tail <<comma_list <<param>>>>>>) *
    private fun list_of_lists_2(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "list_of_lists_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!list_of_lists_2_0(builder_, level_ + 1, param)) break
        if (!empty_element_parsed_guard_(builder_, "list_of_lists_2", pos_)) break
      }
      return true
    }

    // <<comma_list_tail <<comma_list <<param>>>>>>
    private fun list_of_lists_2_0(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      return comma_list_tail(builder_, level_ + 1, `comma_list_$`(param))
    }

    /* ********************************************************** */
    internal fun `main_class_meta_$`(p: Parser): Parser {
      return Parser { builder_, level_ -> main_class_meta(builder_, level_ + 1, p) }
    }

    // <<p>>
    internal fun main_class_meta(builder_: SyntaxTreeBuilder, level_: Int, p: Parser): Boolean {
      return p.parse(builder_, level_)
    }

    /* ********************************************************** */
    // <<listOf "1+2" '1+2' <<param>>>>
    internal fun meta_mixed(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      return listOf(builder_, level_ + 1, "1+2", 1+2, param)
    }

    /* ********************************************************** */
    // <<meta_mixed <<comma_list one>>>>
    internal fun meta_mixed_list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return meta_mixed(builder_, level_ + 1, meta_mixed_list_0_0_parser_)
    }

    /* ********************************************************** */
    // <<meta_mixed (<<comma_list one>>)>>
    internal fun meta_mixed_list_paren(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return meta_mixed(builder_, level_ + 1, ExternalRules::meta_mixed_list_paren_0_0)
    }

    // <<comma_list one>>
    private fun meta_mixed_list_paren_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return comma_list(builder_, level_ + 1, ExternalRules::one)
    }

    /* ********************************************************** */
    // <<meta_mixed statement>>
    internal fun meta_mixed_simple(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return meta_mixed(builder_, level_ + 1, ExternalRules::statement)
    }

    /* ********************************************************** */
    // <<comma_list <<comma_list <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
    fun meta_multi_level(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_multi_level")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = comma_list(builder_, level_ + 1, `comma_list_$`(`comma_list_$`(`comma_list_$`(`comma_list_$`(param)))))
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.META_MULTI_LEVEL, result_)
      return result_
    }

    /* ********************************************************** */
    // <<comma_list <<comma_list <<comma_list some>>>>>>
    internal fun meta_multi_level_no_closure(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return comma_list(builder_, level_ + 1, meta_multi_level_no_closure_0_0_parser_)
    }

    /* ********************************************************** */
    // <<comma_list <<comma_list_pinned <<head>> <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
    fun meta_multi_level_pinned(builder_: SyntaxTreeBuilder, level_: Int, head: Parser, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = comma_list(builder_, level_ + 1, `comma_list_pinned_$`(head, `comma_list_$`(`comma_list_$`(`comma_list_$`(param)))))
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.META_MULTI_LEVEL_PINNED, result_)
      return result_
    }

    /* ********************************************************** */
    // <<comma_list <<comma_list_pinned <<head>> (<<comma_list <<comma_list <<comma_list <<param>>>>>>>>)>>>>
    fun meta_multi_level_pinned_paren(builder_: SyntaxTreeBuilder, level_: Int, head: Parser, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned_paren")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = comma_list(builder_, level_ + 1, `comma_list_pinned_$`(head, `meta_multi_level_pinned_paren_0_0_1_$`(param)))
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.META_MULTI_LEVEL_PINNED_PAREN, result_)
      return result_
    }

    private fun `meta_multi_level_pinned_paren_0_0_1_$`(param: Parser): Parser {
      return Parser { builder_, level_ -> meta_multi_level_pinned_paren_0_0_1(builder_, level_ + 1, param) }
    }

    // <<comma_list <<comma_list <<comma_list <<param>>>>>>>>
    private fun meta_multi_level_pinned_paren_0_0_1(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      return comma_list(builder_, level_ + 1, `comma_list_$`(`comma_list_$`(param)))
    }

    /* ********************************************************** */
    // <<comma_list_pinned one (one | two)>>
    internal fun meta_seq(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return comma_list_pinned(builder_, level_ + 1, ExternalRules::one, ExternalRules::meta_seq_0_1)
    }

    // one | two
    private fun meta_seq_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_seq_0_1")) return false
      var result_: Boolean
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // <<list_of_lists one (one | two)>>
    internal fun meta_seq_of_lists(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return list_of_lists(builder_, level_ + 1, ExternalRules::one, ExternalRules::meta_seq_of_lists_0_1)
    }

    // one | two
    private fun meta_seq_of_lists_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_0_1")) return false
      var result_: Boolean
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // (<<list_of_lists one (one | two)>>)?
    internal fun meta_seq_of_lists_opt(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt")) return false
      meta_seq_of_lists_opt_0(builder_, level_ + 1)
      return true
    }

    // <<list_of_lists one (one | two)>>
    private fun meta_seq_of_lists_opt_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return list_of_lists(builder_, level_ + 1, ExternalRules::one, ExternalRules::meta_seq_of_lists_opt_0_0_1)
    }

    // one | two
    private fun meta_seq_of_lists_opt_0_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt_0_0_1")) return false
      var result_: Boolean
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // <<comma_list one>>
    internal fun meta_simple(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return comma_list(builder_, level_ + 1, ExternalRules::one)
    }

    /* ********************************************************** */
    // <<comma_list (<<param>> | some)>>
    fun meta_with_in_place(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_with_in_place")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = comma_list(builder_, level_ + 1, `meta_with_in_place_0_0_$`(param))
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.META_WITH_IN_PLACE, result_)
      return result_
    }

    private fun `meta_with_in_place_0_0_$`(param: Parser): Parser {
      return Parser { builder_, level_ -> meta_with_in_place_0_0(builder_, level_ + 1, param) }
    }

    // <<param>> | some
    private fun meta_with_in_place_0_0(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "meta_with_in_place_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = param.parse(builder_, level_)
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.SOME)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // <<meta_multi_level one>>
    internal fun multi_level(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return meta_multi_level(builder_, level_ + 1, ExternalRules::one)
    }

    /* ********************************************************** */
    // <<two_params_meta <<nested1>> <<two_params_meta <<nested2>> <<nested3>>>>>>
    internal fun nested_meta(builder_: SyntaxTreeBuilder, level_: Int, nested1: Parser, nested2: Parser, nested3: Parser): Boolean {
      return two_params_meta(builder_, level_ + 1, nested1, `two_params_meta_$`(nested2, nested3))
    }

    /* ********************************************************** */
    // <<two_params_meta (<<two_params_meta '%' <<c>>>>) perc_re>>
    internal fun nested_mixed(builder_: SyntaxTreeBuilder, level_: Int, c: Parser): Boolean {
      return two_params_meta(builder_, level_ + 1, `nested_mixed_0_0_$`(c), PERC_RE_parser_)
    }

    private fun `nested_mixed_0_0_$`(c: Parser): Parser {
      return Parser { builder_, level_ -> nested_mixed_0_0(builder_, level_ + 1, c) }
    }

    // <<two_params_meta '%' <<c>>>>
    private fun nested_mixed_0_0(builder_: SyntaxTreeBuilder, level_: Int, c: Parser): Boolean {
      return two_params_meta(builder_, level_ + 1, perc_parser_, c)
    }

    /* ********************************************************** */
    // 'one'
    fun one(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "one")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.ONE, "<one>")
      result_ = consumeToken(builder_, "one")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // '{' <<uniqueListOf (one | two | 10 | some)>> '}'
    internal fun param_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "{")
      result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRules::param_choice_1_0)
      result_ = result_ && consumeToken(builder_, "}")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // one | two | 10 | some
    private fun param_choice_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_choice_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      if (!result_) result_ = consumeToken(builder_, "10")
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.SOME)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' <<uniqueListOf {one | two | 10 | some}>> '}'
    internal fun param_choice_alt(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_choice_alt")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "{")
      result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRules::param_choice_alt_1_0)
      result_ = result_ && consumeToken(builder_, "}")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // one | two | 10 | some
    private fun param_choice_alt_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_choice_alt_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      if (!result_) result_ = consumeToken(builder_, "10")
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.SOME)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' <<uniqueListOf [one | two | 10 | some]>> '}'
    internal fun param_opt(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_opt")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "{")
      result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRules::param_opt_1_0)
      result_ = result_ && consumeToken(builder_, "}")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // [one | two | 10 | some]
    private fun param_opt_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_opt_1_0")) return false
      param_opt_1_0_0(builder_, level_ + 1)
      return true
    }

    // one | two | 10 | some
    private fun param_opt_1_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_opt_1_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      if (!result_) result_ = consumeToken(builder_, "10")
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.SOME)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' <<uniqueListOf "1+1" '1+1' one two 10 some>> '}'
    internal fun param_seq(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "{")
      result_ = result_ && uniqueListOf(builder_, level_ + 1, "1+1", 1+1, ExternalRules::one, ExternalRules::two, 10, SOME_parser_)
      result_ = result_ && consumeToken(builder_, "}")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' <<uniqueListOf {one | two} [10 | some]>> '}'
    internal fun param_seq_alt(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq_alt")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "{")
      result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRules::param_seq_alt_1_0, ExternalRules::param_seq_alt_1_1)
      result_ = result_ && consumeToken(builder_, "}")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // one | two
    private fun param_seq_alt_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq_alt_1_0")) return false
      var result_: Boolean
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      return result_
    }

    // [10 | some]
    private fun param_seq_alt_1_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq_alt_1_1")) return false
      param_seq_alt_1_1_0(builder_, level_ + 1)
      return true
    }

    // 10 | some
    private fun param_seq_alt_1_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq_alt_1_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "10")
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.SOME)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' <<unique_list_of one two>> '}'
    internal fun param_seq_alt_ext(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq_alt_ext")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "{")
      result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRules::one, ExternalRules::two)
      result_ = result_ && consumeToken(builder_, "}")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' <<unique_list_of_params one !two>> '}'
    internal fun param_seq_alt_params_ext(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq_alt_params_ext")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "{")
      result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRules::one, "1+1", ExternalRules::param_seq_alt_params_ext_1_1, 1+1)
      result_ = result_ && consumeToken(builder_, "}")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // !two
    private fun param_seq_alt_params_ext_1_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "param_seq_alt_params_ext_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !two(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // <<listOf '%'>>
    internal fun perc_list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return listOf(builder_, level_ + 1, perc_parser_)
    }

    /* ********************************************************** */
    // <<listOf perc_re>>
    internal fun perc_re_list1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return listOf(builder_, level_ + 1, PERC_RE_parser_)
    }

    /* ********************************************************** */
    // <<listOf (perc_re)>>
    internal fun perc_re_list2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return listOf(builder_, level_ + 1, perc_re_list2_0_0_parser_)
    }

    /* ********************************************************** */
    // <<comma_paren_list (ref | '(' one ')')>>
    fun public_paren_list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "public_paren_list")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.PAREN1)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = comma_paren_list(builder_, level_ + 1, ExternalRules::public_paren_list_0_0)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.PUBLIC_PAREN_LIST, result_)
      return result_
    }

    // ref | '(' one ')'
    private fun public_paren_list_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "public_paren_list_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = parseRef(builder_, level_ + 1)
      if (!result_) result_ = public_paren_list_0_0_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // '(' one ')'
    private fun public_paren_list_0_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "public_paren_list_0_0_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.PAREN1)
      result_ = result_ && one(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedSyntaxElementTypes.PAREN2)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    fun public_paren_list2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.PUBLIC_PAREN_LIST, true)
      return true
    }

    /* ********************************************************** */
    // <<param>>
    internal fun recoverable_item(builder_: SyntaxTreeBuilder, level_: Int, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "recoverable_item")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = param.parse(builder_, level_)
      exit_section_(builder_, level_, marker_, result_, false, ExternalRules::item_recover)
      return result_
    }

    /* ********************************************************** */
    // <<param>>
    internal fun recoverable_item2(builder_: SyntaxTreeBuilder, level_: Int, param: Parser, recover_arg: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "recoverable_item2")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = param.parse(builder_, level_)
      exit_section_(builder_, level_, marker_, result_, false, recover_arg)
      return result_
    }

    /* ********************************************************** */
    // <<recover_arg>> <<param>>
    internal fun recoverable_item3(builder_: SyntaxTreeBuilder, level_: Int, recover_arg: Parser, param: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "recoverable_item3")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = recover_arg.parse(builder_, level_)
      pinned_ = result_ // pin = 1
      result_ = result_ && param.parse(builder_, level_)
      exit_section_(builder_, level_, marker_, result_, pinned_, recover_arg)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // <<listOf statement>>
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return listOf(builder_, level_ + 1, ExternalRules::statement)
    }

    /* ********************************************************** */
    // <<comma_list <<second_class_meta some>>>>
    internal fun second_class_meta_usage_from_main(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return comma_list(builder_, level_ + 1, second_class_meta_usage_from_main_0_0_parser_)
    }

    /* ********************************************************** */
    // one | two
    fun statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
      result_ = one(builder_, level_ + 1)
      if (!result_) result_ = two(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // 'two'
    fun two(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.TWO, "<two>")
      result_ = consumeToken(builder_, "two")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    internal fun `two_params_meta_$`(a: Parser, b: Parser): Parser {
      return Parser { builder_, level_ -> two_params_meta(builder_, level_ + 1, a, b) }
    }

    // <<a>> <<b>>
    fun two_params_meta(builder_: SyntaxTreeBuilder, level_: Int, a: Parser, b: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "two_params_meta")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = a.parse(builder_, level_)
      result_ = result_ && b.parse(builder_, level_)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.TWO_PARAMS_META, result_)
      return result_
    }

    internal val PERC_RE_parser_: Parser = Parser { builder_, level_ -> consumeToken(builder_, GeneratedSyntaxElementTypes.PERC_RE) }
    internal val SOME_parser_: Parser = Parser { builder_, level_ -> consumeToken(builder_, GeneratedSyntaxElementTypes.SOME) }
    internal val perc_parser_: Parser = Parser { builder_, level_ -> consumeToken(builder_, GeneratedSyntaxElementTypes.PERC) }
    internal val perc_re_list2_0_0_parser_: Parser = PERC_RE_parser_

    private val meta_mixed_list_0_0_parser_: Parser = `comma_list_$`(ExternalRules::one)
    private val meta_multi_level_no_closure_0_0_0_parser_: Parser = `comma_list_$`(SOME_parser_)
    private val meta_multi_level_no_closure_0_0_parser_: Parser = `comma_list_$`(meta_multi_level_no_closure_0_0_0_parser_)
    private val second_class_meta_usage_from_main_0_0_parser_: Parser = ExternalRules2.`second_class_meta_$`(SOME_parser_)
  }
}
// ---- ExternalRules2.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedSyntaxElementTypes

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class ExternalRules2 {

  companion object {
    /* ********************************************************** */
    fun extra_root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.EXTRA_ROOT, true)
      return true
    }

    /* ********************************************************** */
    // <<comma_list <<main_class_meta some>>>>
    internal fun main_class_meta_usage_from_second(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return ExternalRules.comma_list(builder_, level_ + 1, main_class_meta_usage_from_second_0_0_parser_)
    }

    /* ********************************************************** */
    // <<listOf one>>
    internal fun one_list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return listOf(builder_, level_ + 1, ExternalRules::one)
    }

    /* ********************************************************** */
    // <<listOf (one)>>
    internal fun one_list_par(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return listOf(builder_, level_ + 1, ExternalRules2::one_list_par_0_0)
    }

    // (one)
    private fun one_list_par_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "one_list_par_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = one(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    internal fun `second_class_meta_$`(bmp: Parser): Parser {
      return Parser { builder_, level_ -> second_class_meta(builder_, level_ + 1, bmp) }
    }

    // <<bmp>>
    internal fun second_class_meta(builder_: SyntaxTreeBuilder, level_: Int, bmp: Parser): Boolean {
      return bmp.parse(builder_, level_)
    }

    /* ********************************************************** */
    // <<comma_list <<third_class_meta some>>>>
    internal fun third_class_meta_usage_from_second(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return ExternalRules.comma_list(builder_, level_ + 1, third_class_meta_usage_from_second_0_0_parser_)
    }

    private val main_class_meta_usage_from_second_0_0_parser_: Parser = ExternalRules.`main_class_meta_$`(ExternalRules.SOME_parser_)
    private val third_class_meta_usage_from_second_0_0_parser_: Parser = ExternalRules3.`third_class_meta_$`(ExternalRules.SOME_parser_)
  }
}
// ---- ExternalRules3.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedSyntaxElementTypes

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class ExternalRules3 {

  companion object {
    /* ********************************************************** */
    // <<comma_list <<second_class_meta some>>>>
    internal fun second_class_meta_usage_from_third(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return ExternalRules.comma_list(builder_, level_ + 1, second_class_meta_usage_from_third_0_0_parser_)
    }

    /* ********************************************************** */
    internal fun `third_class_meta_$`(fmp: Parser): Parser {
      return Parser { builder_, level_ -> third_class_meta(builder_, level_ + 1, fmp) }
    }

    // <<fmp>>
    internal fun third_class_meta(builder_: SyntaxTreeBuilder, level_: Int, fmp: Parser): Boolean {
      return fmp.parse(builder_, level_)
    }

    private val second_class_meta_usage_from_third_0_0_parser_: Parser = ExternalRules2.`second_class_meta_$`(ExternalRules.SOME_parser_)
  }
}