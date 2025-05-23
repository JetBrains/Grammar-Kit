// ---- ConsumeMethods.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object ConsumeMethods {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, null)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // &token_fast token_regular
  fun fast_predicate_vs_regular(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_predicate_vs_regular")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = fast_predicate_vs_regular_0(runtime_, level_ + 1)
    result_ = result_ && token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_PREDICATE_VS_REGULAR, result_)
    return result_
  }

  // &token_fast
  private fun fast_predicate_vs_regular_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_predicate_vs_regular_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // &token_fast token_smart
  fun fast_predicate_vs_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_predicate_vs_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = fast_predicate_vs_smart_0(runtime_, level_ + 1)
    result_ = result_ && token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_PREDICATE_VS_SMART, result_)
    return result_
  }

  // &token_fast
  private fun fast_predicate_vs_smart_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_predicate_vs_smart_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // FAST_TOKEN 3 5
  fun fast_rule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_rule")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.FAST_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokenFast(GeneratedSyntaxElementTypes.FAST_TOKEN)
    result_ = result_ && runtime_.consumeToken("3")
    result_ = result_ && runtime_.consumeToken("5")
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_RULE, result_)
    return result_
  }

  /* ********************************************************** */
  // token_fast | token_regular
  fun fast_vs_regular(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_vs_regular")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_fast(runtime_, level_ + 1)
    if (!result_) result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_VS_REGULAR, result_)
    return result_
  }

  /* ********************************************************** */
  // token_fast | token_regular
  fun fast_vs_regular_in_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_vs_regular_in_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_fast(runtime_, level_ + 1)
    if (!result_) result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_VS_REGULAR_IN_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_fast | token_regular
  fun fast_vs_regular_in_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_vs_regular_in_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_fast(runtime_, level_ + 1)
    if (!result_) result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_VS_REGULAR_IN_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  // token_fast | token_smart
  fun fast_vs_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_vs_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_fast(runtime_, level_ + 1)
    if (!result_) result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_VS_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  // token_fast | token_smart
  fun fast_vs_smart_in_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_vs_smart_in_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_fast(runtime_, level_ + 1)
    if (!result_) result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_VS_SMART_IN_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_fast | token_smart
  fun fast_vs_smart_in_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_vs_smart_in_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_fast(runtime_, level_ + 1)
    if (!result_) result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.FAST_VS_SMART_IN_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  fun parent_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "parent_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.FAST_TOKEN, GeneratedSyntaxElementTypes.REGULAR_TOKEN, GeneratedSyntaxElementTypes.SMART_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.PARENT_FAST, "<parent fast>")
    result_ = regular_rule(runtime_, level_ + 1)
    if (!result_) result_ = smart_rule(runtime_, level_ + 1)
    if (!result_) result_ = fast_rule(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  fun parent_regular(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "parent_regular")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.FAST_TOKEN) &&
        !runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SMART_TOKEN) &&
        !runtime_.nextTokenIs("<parent regular>", GeneratedSyntaxElementTypes.REGULAR_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.PARENT_REGULAR, "<parent regular>")
    result_ = regular_rule(runtime_, level_ + 1)
    if (!result_) result_ = smart_rule(runtime_, level_ + 1)
    if (!result_) result_ = fast_rule(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // regular_rule | smart_rule | fast_rule
  fun parent_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "parent_smart")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.FAST_TOKEN) &&
        !runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.REGULAR_TOKEN, GeneratedSyntaxElementTypes.SMART_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.PARENT_SMART, "<parent smart>")
    result_ = regular_rule(runtime_, level_ + 1)
    if (!result_) result_ = smart_rule(runtime_, level_ + 1)
    if (!result_) result_ = fast_rule(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // &token_regular token_fast
  fun regular_predicate_vs_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_predicate_vs_fast")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = regular_predicate_vs_fast_0(runtime_, level_ + 1)
    result_ = result_ && token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_PREDICATE_VS_FAST, result_)
    return result_
  }

  // &token_regular
  private fun regular_predicate_vs_fast_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_predicate_vs_fast_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // &token_regular token_smart
  fun regular_predicate_vs_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_predicate_vs_smart")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = regular_predicate_vs_smart_0(runtime_, level_ + 1)
    result_ = result_ && token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_PREDICATE_VS_SMART, result_)
    return result_
  }

  // &token_regular
  private fun regular_predicate_vs_smart_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_predicate_vs_smart_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // REGULAR_TOKEN 1 2 3
  fun regular_rule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_rule")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.REGULAR_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.REGULAR_TOKEN)
    result_ = result_ && runtime_.consumeToken("1")
    result_ = result_ && runtime_.consumeToken("2")
    result_ = result_ && runtime_.consumeToken("3")
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_RULE, result_)
    return result_
  }

  /* ********************************************************** */
  // token_regular | token_fast
  fun regular_vs_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_vs_fast")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_regular(runtime_, level_ + 1)
    if (!result_) result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_VS_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_regular | token_fast
  fun regular_vs_fast_in_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_vs_fast_in_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_regular(runtime_, level_ + 1)
    if (!result_) result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_VS_FAST_IN_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_regular | token_fast
  fun regular_vs_fast_in_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_vs_fast_in_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_regular(runtime_, level_ + 1)
    if (!result_) result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_VS_FAST_IN_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  // token_regular | token_smart
  fun regular_vs_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_vs_smart")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_regular(runtime_, level_ + 1)
    if (!result_) result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_VS_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  // token_regular | token_smart
  fun regular_vs_smart_in_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_vs_smart_in_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_regular(runtime_, level_ + 1)
    if (!result_) result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_VS_SMART_IN_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_regular | token_smart
  fun regular_vs_smart_in_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "regular_vs_smart_in_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_regular(runtime_, level_ + 1)
    if (!result_) result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REGULAR_VS_SMART_IN_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // &token_smart token_fast
  fun smart_predicate_vs_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_predicate_vs_fast")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = smart_predicate_vs_fast_0(runtime_, level_ + 1)
    result_ = result_ && token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_PREDICATE_VS_FAST, result_)
    return result_
  }

  // &token_smart
  private fun smart_predicate_vs_fast_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_predicate_vs_fast_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // &token_smart token_regular
  fun smart_predicate_vs_regular(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_predicate_vs_regular")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = smart_predicate_vs_regular_0(runtime_, level_ + 1)
    result_ = result_ && token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_PREDICATE_VS_REGULAR, result_)
    return result_
  }

  // &token_smart
  private fun smart_predicate_vs_regular_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_predicate_vs_regular_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = token_smart(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // SMART_TOKEN 2 4
  fun smart_rule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_rule")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SMART_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokenSmart(GeneratedSyntaxElementTypes.SMART_TOKEN)
    result_ = result_ && runtime_.consumeToken("2")
    result_ = result_ && runtime_.consumeToken("4")
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_RULE, result_)
    return result_
  }

  /* ********************************************************** */
  // token_smart | token_fast
  fun smart_vs_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_vs_fast")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_smart(runtime_, level_ + 1)
    if (!result_) result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_VS_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_smart | token_fast
  fun smart_vs_fast_in_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_vs_fast_in_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_smart(runtime_, level_ + 1)
    if (!result_) result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_VS_FAST_IN_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_smart | token_fast
  fun smart_vs_fast_in_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_vs_fast_in_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_smart(runtime_, level_ + 1)
    if (!result_) result_ = token_fast(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_VS_FAST_IN_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  // token_smart | token_regular
  fun smart_vs_regular(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_vs_regular")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_smart(runtime_, level_ + 1)
    if (!result_) result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_VS_REGULAR, result_)
    return result_
  }

  /* ********************************************************** */
  // token_smart | token_regular
  fun smart_vs_regular_in_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_vs_regular_in_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_smart(runtime_, level_ + 1)
    if (!result_) result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_VS_REGULAR_IN_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // token_smart | token_regular
  fun smart_vs_regular_in_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_vs_regular_in_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_smart(runtime_, level_ + 1)
    if (!result_) result_ = token_regular(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SMART_VS_REGULAR_IN_SMART, result_)
    return result_
  }

  /* ********************************************************** */
  // SAME_TOKEN 5 6
  fun token_fast(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_fast")) return false
    if (!runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokenFast(GeneratedSyntaxElementTypes.SAME_TOKEN)
    result_ = result_ && runtime_.consumeToken("5")
    result_ = result_ && runtime_.consumeToken("6")
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.TOKEN_FAST, result_)
    return result_
  }

  /* ********************************************************** */
  // SAME_TOKEN 3 4
  fun token_regular(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_regular")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.SAME_TOKEN)
    result_ = result_ && runtime_.consumeToken("3")
    result_ = result_ && runtime_.consumeToken("4")
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.TOKEN_REGULAR, result_)
    return result_
  }

  /* ********************************************************** */
  // SAME_TOKEN 4 5
  fun token_smart(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_smart")) return false
    if (!runtime_.nextTokenIsSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokenSmart(GeneratedSyntaxElementTypes.SAME_TOKEN)
    result_ = result_ && runtime_.consumeToken("4")
    result_ = result_ && runtime_.consumeToken("5")
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.TOKEN_SMART, result_)
    return result_
  }

}