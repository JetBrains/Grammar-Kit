// ---- GeneratedParser.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class GeneratedParser {

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
    // (A | B | C) D
    fun inner_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "inner_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.INNER_CHOICE, "<inner choice>")
      result_ = inner_choice_0(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedTypes.D)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // A | B | C
    private fun inner_choice_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "inner_choice_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.B)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.C)
      return result_
    }

    /* ********************************************************** */
    // A | (B) | C
    fun inner_parenthesized_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "inner_parenthesized_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.INNER_PARENTHESIZED_CHOICE, "<inner parenthesized choice>")
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.B)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.C)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // A | B | 'c'
    fun text_token_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "text_token_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.TEXT_TOKEN_CHOICE, "<text token choice>")
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.B)
      if (!result_) result_ = consumeToken(builder_, "c")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B
    fun two_tokens_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two_tokens_choice")) return false
      if (!nextTokenIs(builder_, "<two tokens choice>", GeneratedTypes.A, GeneratedTypes.B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.TWO_TOKENS_CHOICE, "<two tokens choice>")
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.B)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | B | A
    fun two_tokens_repeating_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two_tokens_repeating_choice")) return false
      if (!nextTokenIs(builder_, "<two tokens repeating choice>", GeneratedTypes.A, GeneratedTypes.B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.TWO_TOKENS_REPEATING_CHOICE, "<two tokens repeating choice>")
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.B)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.B)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.A)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}