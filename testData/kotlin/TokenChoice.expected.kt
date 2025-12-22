// ---- generated/TokenChoice.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object TokenChoice {

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
  // D | A | B
  fun another_three_tokens(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "another_three_tokens")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ANOTHER_THREE_TOKENS, "<another three tokens>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ANOTHER_THREE_TOKENS_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B | F
  fun fast_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "fast_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.FAST_CHOICE, "<fast choice>")
    result_ = runtime_.consumeTokenFast(GeneratedSyntaxElementTypes.FAST_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B | C | D | E
  fun five_tokens_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "five_tokens_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.FIVE_TOKENS_CHOICE, "<five tokens choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.FIVE_TOKENS_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B | C | D
  fun four_tokens_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "four_tokens_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.FOUR_TOKENS_CHOICE, "<four tokens choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.FOUR_TOKENS_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B | C
  fun parenthesized_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "parenthesized_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.PARENTHESIZED_CHOICE, "<parenthesized choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.PARENTHESIZED_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // P2 | P3 | P0 | P1
  internal fun private_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "private_choice")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.PRIVATE_CHOICE_TOKENS)
    return result_
  }

  /* ********************************************************** */
  // D | C | A | B | B | A | C
  fun repeating_tokens_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "repeating_tokens_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.REPEATING_TOKENS_CHOICE, "<repeating tokens choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.REPEATING_TOKENS_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // A | B | S
  fun smart_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "smart_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.SMART_CHOICE, "<smart choice>")
    result_ = runtime_.consumeTokenSmart(GeneratedSyntaxElementTypes.SMART_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B | C | D | E | F | G | H | I | J
  fun ten_tokens_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "ten_tokens_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.TEN_TOKENS_CHOICE, "<ten tokens choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TEN_TOKENS_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B | C
  fun three_tokens_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "three_tokens_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.THREE_TOKENS_CHOICE, "<three tokens choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.THREE_TOKENS_CHOICE_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // B | A | C
  fun three_tokens_in_another_order(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "three_tokens_in_another_order")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.THREE_TOKENS_IN_ANOTHER_ORDER, "<three tokens in another order>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.THREE_TOKENS_IN_ANOTHER_ORDER_TOKENS)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

}
// ---- generated/TokenChoice2.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object TokenChoice2 {

  /* ********************************************************** */
  fun some(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_()
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SOME, true)
    return true
  }

}