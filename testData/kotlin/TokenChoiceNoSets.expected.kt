// ---- generated/GeneratedParser.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object GeneratedParser {

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
  // (A | B | C) D
  fun inner_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "inner_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.INNER_CHOICE, "<inner choice>")
    result_ = inner_choice_0(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.D)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // A | B | C
  private fun inner_choice_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "inner_choice_0")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.C)
    return result_
  }

  /* ********************************************************** */
  // A | (B) | C
  fun inner_parenthesized_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "inner_parenthesized_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.INNER_PARENTHESIZED_CHOICE, "<inner parenthesized choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.C)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // A | B | 'c'
  fun text_token_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "text_token_choice")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.TEXT_TOKEN_CHOICE, "<text token choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    if (!result_) result_ = runtime_.consumeToken("c")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B
  fun two_tokens_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "two_tokens_choice")) return false
    if (!runtime_.nextTokenIs("<two tokens choice>", GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.TWO_TOKENS_CHOICE, "<two tokens choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // A | B | B | A
  fun two_tokens_repeating_choice(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "two_tokens_repeating_choice")) return false
    if (!runtime_.nextTokenIs("<two tokens repeating choice>", GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.TWO_TOKENS_REPEATING_CHOICE, "<two tokens repeating choice>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

}