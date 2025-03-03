// ---- ConsumeMethods.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedTypes
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class ConsumeMethods(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: SyntaxElementType, builder_: SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, null)
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

    /* ********************************************************** */
    // &token_fast token_regular
    fun fast_predicate_vs_regular(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_predicate_vs_regular")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = fast_predicate_vs_regular_0(builder_, level_ + 1)
      result_ = result_ && token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_PREDICATE_VS_REGULAR, result_)
      return result_
    }

    // &token_fast
    private fun fast_predicate_vs_regular_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_predicate_vs_regular_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // &token_fast token_smart
    fun fast_predicate_vs_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_predicate_vs_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = fast_predicate_vs_smart_0(builder_, level_ + 1)
      result_ = result_ && token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_PREDICATE_VS_SMART, result_)
      return result_
    }

    // &token_fast
    private fun fast_predicate_vs_smart_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_predicate_vs_smart_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // FAST_TOKEN 3 5
    fun fast_rule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_rule")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.FAST_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokenFast(builder_, GeneratedTypes.FAST_TOKEN)
      result_ = result_ && consumeToken(builder_, "3")
      result_ = result_ && consumeToken(builder_, "5")
      exit_section_(builder_, marker_, GeneratedTypes.FAST_RULE, result_)
      return result_
    }

    /* ********************************************************** */
    // token_fast | token_regular
    fun fast_vs_regular(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_vs_regular")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_fast(builder_, level_ + 1)
      if (!result_) result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_VS_REGULAR, result_)
      return result_
    }

    /* ********************************************************** */
    // token_fast | token_regular
    fun fast_vs_regular_in_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_vs_regular_in_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_fast(builder_, level_ + 1)
      if (!result_) result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_VS_REGULAR_IN_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_fast | token_regular
    fun fast_vs_regular_in_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_vs_regular_in_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_fast(builder_, level_ + 1)
      if (!result_) result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_VS_REGULAR_IN_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    // token_fast | token_smart
    fun fast_vs_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_vs_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_fast(builder_, level_ + 1)
      if (!result_) result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_VS_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    // token_fast | token_smart
    fun fast_vs_smart_in_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_vs_smart_in_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_fast(builder_, level_ + 1)
      if (!result_) result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_VS_SMART_IN_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_fast | token_smart
    fun fast_vs_smart_in_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_vs_smart_in_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_fast(builder_, level_ + 1)
      if (!result_) result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.FAST_VS_SMART_IN_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    // regular_rule | smart_rule | fast_rule
    fun parent_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "parent_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.FAST_TOKEN, GeneratedTypes.REGULAR_TOKEN, GeneratedTypes.SMART_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.PARENT_FAST, "<parent fast>")
      result_ = regular_rule(builder_, level_ + 1)
      if (!result_) result_ = smart_rule(builder_, level_ + 1)
      if (!result_) result_ = fast_rule(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // regular_rule | smart_rule | fast_rule
    fun parent_regular(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "parent_regular")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.FAST_TOKEN) &&
          !nextTokenIsSmart(builder_, GeneratedTypes.SMART_TOKEN) &&
          !nextTokenIs(builder_, "<parent regular>", GeneratedTypes.REGULAR_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.PARENT_REGULAR, "<parent regular>")
      result_ = regular_rule(builder_, level_ + 1)
      if (!result_) result_ = smart_rule(builder_, level_ + 1)
      if (!result_) result_ = fast_rule(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // regular_rule | smart_rule | fast_rule
    fun parent_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "parent_smart")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.FAST_TOKEN) &&
          !nextTokenIsSmart(builder_, GeneratedTypes.REGULAR_TOKEN, GeneratedTypes.SMART_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.PARENT_SMART, "<parent smart>")
      result_ = regular_rule(builder_, level_ + 1)
      if (!result_) result_ = smart_rule(builder_, level_ + 1)
      if (!result_) result_ = fast_rule(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // &token_regular token_fast
    fun regular_predicate_vs_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_predicate_vs_fast")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = regular_predicate_vs_fast_0(builder_, level_ + 1)
      result_ = result_ && token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_PREDICATE_VS_FAST, result_)
      return result_
    }

    // &token_regular
    private fun regular_predicate_vs_fast_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_predicate_vs_fast_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // &token_regular token_smart
    fun regular_predicate_vs_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_predicate_vs_smart")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = regular_predicate_vs_smart_0(builder_, level_ + 1)
      result_ = result_ && token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_PREDICATE_VS_SMART, result_)
      return result_
    }

    // &token_regular
    private fun regular_predicate_vs_smart_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_predicate_vs_smart_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // REGULAR_TOKEN 1 2 3
    fun regular_rule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_rule")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.REGULAR_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.REGULAR_TOKEN)
      result_ = result_ && consumeToken(builder_, "1")
      result_ = result_ && consumeToken(builder_, "2")
      result_ = result_ && consumeToken(builder_, "3")
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_RULE, result_)
      return result_
    }

    /* ********************************************************** */
    // token_regular | token_fast
    fun regular_vs_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_vs_fast")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_regular(builder_, level_ + 1)
      if (!result_) result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_VS_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_regular | token_fast
    fun regular_vs_fast_in_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_vs_fast_in_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_regular(builder_, level_ + 1)
      if (!result_) result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_VS_FAST_IN_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_regular | token_fast
    fun regular_vs_fast_in_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_vs_fast_in_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_regular(builder_, level_ + 1)
      if (!result_) result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_VS_FAST_IN_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    // token_regular | token_smart
    fun regular_vs_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_vs_smart")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_regular(builder_, level_ + 1)
      if (!result_) result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_VS_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    // token_regular | token_smart
    fun regular_vs_smart_in_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_vs_smart_in_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_regular(builder_, level_ + 1)
      if (!result_) result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_VS_SMART_IN_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_regular | token_smart
    fun regular_vs_smart_in_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "regular_vs_smart_in_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_regular(builder_, level_ + 1)
      if (!result_) result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REGULAR_VS_SMART_IN_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // &token_smart token_fast
    fun smart_predicate_vs_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_predicate_vs_fast")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = smart_predicate_vs_fast_0(builder_, level_ + 1)
      result_ = result_ && token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_PREDICATE_VS_FAST, result_)
      return result_
    }

    // &token_smart
    private fun smart_predicate_vs_fast_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_predicate_vs_fast_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // &token_smart token_regular
    fun smart_predicate_vs_regular(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_predicate_vs_regular")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = smart_predicate_vs_regular_0(builder_, level_ + 1)
      result_ = result_ && token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_PREDICATE_VS_REGULAR, result_)
      return result_
    }

    // &token_smart
    private fun smart_predicate_vs_regular_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_predicate_vs_regular_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = token_smart(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // SMART_TOKEN 2 4
    fun smart_rule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_rule")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SMART_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokenSmart(builder_, GeneratedTypes.SMART_TOKEN)
      result_ = result_ && consumeToken(builder_, "2")
      result_ = result_ && consumeToken(builder_, "4")
      exit_section_(builder_, marker_, GeneratedTypes.SMART_RULE, result_)
      return result_
    }

    /* ********************************************************** */
    // token_smart | token_fast
    fun smart_vs_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_vs_fast")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_smart(builder_, level_ + 1)
      if (!result_) result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_VS_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_smart | token_fast
    fun smart_vs_fast_in_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_vs_fast_in_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_smart(builder_, level_ + 1)
      if (!result_) result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_VS_FAST_IN_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_smart | token_fast
    fun smart_vs_fast_in_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_vs_fast_in_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_smart(builder_, level_ + 1)
      if (!result_) result_ = token_fast(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_VS_FAST_IN_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    // token_smart | token_regular
    fun smart_vs_regular(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_vs_regular")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_smart(builder_, level_ + 1)
      if (!result_) result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_VS_REGULAR, result_)
      return result_
    }

    /* ********************************************************** */
    // token_smart | token_regular
    fun smart_vs_regular_in_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_vs_regular_in_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_smart(builder_, level_ + 1)
      if (!result_) result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_VS_REGULAR_IN_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // token_smart | token_regular
    fun smart_vs_regular_in_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_vs_regular_in_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_smart(builder_, level_ + 1)
      if (!result_) result_ = token_regular(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SMART_VS_REGULAR_IN_SMART, result_)
      return result_
    }

    /* ********************************************************** */
    // SAME_TOKEN 5 6
    fun token_fast(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_fast")) return false
      if (!nextTokenIsFast(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokenFast(builder_, GeneratedTypes.SAME_TOKEN)
      result_ = result_ && consumeToken(builder_, "5")
      result_ = result_ && consumeToken(builder_, "6")
      exit_section_(builder_, marker_, GeneratedTypes.TOKEN_FAST, result_)
      return result_
    }

    /* ********************************************************** */
    // SAME_TOKEN 3 4
    fun token_regular(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_regular")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.SAME_TOKEN)
      result_ = result_ && consumeToken(builder_, "3")
      result_ = result_ && consumeToken(builder_, "4")
      exit_section_(builder_, marker_, GeneratedTypes.TOKEN_REGULAR, result_)
      return result_
    }

    /* ********************************************************** */
    // SAME_TOKEN 4 5
    fun token_smart(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_smart")) return false
      if (!nextTokenIsSmart(builder_, GeneratedTypes.SAME_TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokenSmart(builder_, GeneratedTypes.SAME_TOKEN)
      result_ = result_ && consumeToken(builder_, "4")
      result_ = result_ && consumeToken(builder_, "5")
      exit_section_(builder_, marker_, GeneratedTypes.TOKEN_SMART, result_)
      return result_
    }

  }
}