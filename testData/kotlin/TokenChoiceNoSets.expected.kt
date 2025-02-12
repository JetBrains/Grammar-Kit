// ---- GeneratedParser.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import generated.GeneratedTypes.*
import com.intellij.lang.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiParser
import com.intellij.lang.LightPsiParser

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class GeneratedParser: PsiParser, LightPsiParser {

  override fun parse(root_: IElementType, builder_: PsiBuilder): ASTNode {
    parseLight(root_, builder_)
    return builder_.getTreeBuilt()
  }

  override fun parseLight(root_: IElementType, builder_: PsiBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, null)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: IElementType, builder_: PsiBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: IElementType, builder_: PsiBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // (A | B | C) D
    fun inner_choice(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "inner_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, INNER_CHOICE, "<inner choice>")
      result_ = inner_choice_0(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, D)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // A | B | C
    private fun inner_choice_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "inner_choice_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, A)
      if (!result_) result_ = consumeToken(builder_, B)
      if (!result_) result_ = consumeToken(builder_, C)
      return result_
    }

    /* ********************************************************** */
    // A | (B) | C
    fun inner_parenthesized_choice(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "inner_parenthesized_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, INNER_PARENTHESIZED_CHOICE, "<inner parenthesized choice>")
      result_ = consumeToken(builder_, A)
      if (!result_) result_ = consumeToken(builder_, B)
      if (!result_) result_ = consumeToken(builder_, C)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    internal fun root(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // A | B | 'c'
    fun text_token_choice(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "text_token_choice")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, TEXT_TOKEN_CHOICE, "<text token choice>")
      result_ = consumeToken(builder_, A)
      if (!result_) result_ = consumeToken(builder_, B)
      if (!result_) result_ = consumeToken(builder_, "c")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B
    fun two_tokens_choice(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two_tokens_choice")) return false
      if (!nextTokenIs(builder_, "<two tokens choice>", A, B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, TWO_TOKENS_CHOICE, "<two tokens choice>")
      result_ = consumeToken(builder_, A)
      if (!result_) result_ = consumeToken(builder_, B)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // A | B | B | A
    fun two_tokens_repeating_choice(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "two_tokens_repeating_choice")) return false
      if (!nextTokenIs(builder_, "<two tokens repeating choice>", A, B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, TWO_TOKENS_REPEATING_CHOICE, "<two tokens repeating choice>")
      result_ = consumeToken(builder_, A)
      if (!result_) result_ = consumeToken(builder_, B)
      if (!result_) result_ = consumeToken(builder_, B)
      if (!result_) result_ = consumeToken(builder_, A)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}