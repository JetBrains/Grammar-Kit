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
      return foo(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // a
    fun bar(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "bar")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, A)
      exit_section_(builder_, marker_, BAR, result_)
      return result_
    }

    /* ********************************************************** */
    // a 
    //   | b c d
    //   | c d e
    //   | bar d e f
    internal fun foo(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "foo")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, A)
      if (!result_) result_ = parseTokens(builder_, 2, B, C, D)
      if (!result_) result_ = parseTokens(builder_, 2, C, D, E)
      if (!result_) result_ = foo_3(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // bar d e f
    private fun foo_3(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "foo_3")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = bar(builder_, level_ + 1)
      result_ = result_ && consumeTokens(builder_, 1, D, E, F)
      pinned_ = result_ // pin = 2
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

  }
}