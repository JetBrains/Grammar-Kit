// ---- TokenChoice.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class TokenChoice(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

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
    // D | A | B
    fun another_three_tokens(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "another_three_tokens")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.ANOTHER_THREE_TOKENS, "<another three tokens>")
      result_ = consumeToken(builder_, GeneratedTypes.ANOTHER_THREE_TOKENS_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | F
    fun fast_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "fast_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.FAST_CHOICE, "<fast choice>")
      result_ = consumeTokenFast(builder_, GeneratedTypes.FAST_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | C | D | E
    fun five_tokens_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "five_tokens_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.FIVE_TOKENS_CHOICE, "<five tokens choice>")
      result_ = consumeToken(builder_, GeneratedTypes.FIVE_TOKENS_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | C | D
    fun four_tokens_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "four_tokens_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.FOUR_TOKENS_CHOICE, "<four tokens choice>")
      result_ = consumeToken(builder_, GeneratedTypes.FOUR_TOKENS_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | C
    fun parenthesized_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "parenthesized_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.PARENTHESIZED_CHOICE, "<parenthesized choice>")
      result_ = consumeToken(builder_, GeneratedTypes.PARENTHESIZED_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // P2 | P3 | P0 | P1
    internal fun private_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "private_choice")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedTypes.PRIVATE_CHOICE_TOKENS)
      return result_
    }

    /* ********************************************************** */
    // D | C | A | B | B | A | C
    fun repeating_tokens_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "repeating_tokens_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.REPEATING_TOKENS_CHOICE, "<repeating tokens choice>")
      result_ = consumeToken(builder_, GeneratedTypes.REPEATING_TOKENS_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // A | B | S
    fun smart_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "smart_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.SMART_CHOICE, "<smart choice>")
      result_ = consumeTokenSmart(builder_, GeneratedTypes.SMART_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | C | D | E | F | G | H | I | J
    fun ten_tokens_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "ten_tokens_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.TEN_TOKENS_CHOICE, "<ten tokens choice>")
      result_ = consumeToken(builder_, GeneratedTypes.TEN_TOKENS_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | C
    fun three_tokens_choice(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "three_tokens_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.THREE_TOKENS_CHOICE, "<three tokens choice>")
      result_ = consumeToken(builder_, GeneratedTypes.THREE_TOKENS_CHOICE_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // B | A | C
    fun three_tokens_in_another_order(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "three_tokens_in_another_order")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.THREE_TOKENS_IN_ANOTHER_ORDER, "<three tokens in another order>")
      result_ = consumeToken(builder_, GeneratedTypes.THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}
// ---- TokenChoice2.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class TokenChoice2 {

  companion object {
    /* ********************************************************** */
    fun some(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedTypes.SOME, true)
      return true
    }

  }
}