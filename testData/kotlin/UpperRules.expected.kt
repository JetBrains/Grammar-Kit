// ---- GeneratedParser.kt -----------------
//header.txt
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

class GeneratedParser: PsiParser, LightPsiParser {

  override fun parse(root_: IElementType, builder_: PsiBuilder): ASTNode {
    parseLight(root_, builder_)
    return builder_.getTreeBuilt()
  }

  override fun parseLight(root_: IElementType, builder_: PsiBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: IElementType, builder_: PsiBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: IElementType, builder_: PsiBuilder, level_: Int): Boolean {
      return file(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<TokenSet> = arrayOf(
      create_token_set_(ABC, ABC_ONE, ABC_THREE, ABC_TWO),
    )

    /* ********************************************************** */
    // abc_three? (abc_one | abc_two | abc_three)
    fun abc(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, ABC, "<abc>")
      result_ = abc_0(builder_, level_ + 1)
      result_ = result_ && abc_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // abc_three?
    private fun abc_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_0")) return false
      abc_three(builder_, level_ + 1)
      return true
    }

    // abc_one | abc_two | abc_three
    private fun abc_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_1")) return false
      var result_: Boolean
      result_ = abc_one(builder_, level_ + 1)
      if (!result_) result_ = abc_two(builder_, level_ + 1)
      if (!result_) result_ = abc_three(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // just_b X
    fun abc_one(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_one")) return false
      if (!nextTokenIs(builder_, B)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _UPPER_, ABC_ONE, null)
      result_ = just_b(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, X)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // X
    fun abc_three(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_three")) return false
      if (!nextTokenIs(builder_, X)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, X)
      exit_section_(builder_, marker_, ABC_THREE, result_)
      return result_
    }

    /* ********************************************************** */
    // C
    fun abc_two(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_two")) return false
      if (!nextTokenIs(builder_, C)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _UPPER_, ABC_TWO, null)
      result_ = consumeToken(builder_, C)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // abc | pinned_seq | plain_seq
    internal fun content(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "content")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = abc(builder_, level_ + 1)
      if (!result_) result_ = pinned_seq(builder_, level_ + 1)
      if (!result_) result_ = plain_seq(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, GeneratedParser::content_recover)
      return result_
    }

    /* ********************************************************** */
    // !';'
    internal fun content_recover(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "content_recover")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !consumeToken(builder_, ";")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // (content ';') *
    internal fun file(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "file")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!file_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "file", pos_)) break
      }
      return true
    }

    // content ';'
    private fun file_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "file_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = content(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, ";")
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // B
    fun just_b(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "just_b")) return false
      if (!nextTokenIs(builder_, B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, B)
      exit_section_(builder_, marker_, JUST_B, result_)
      return result_
    }

    /* ********************************************************** */
    // prefix (abc_one | abc_two)
    fun pinned_seq(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_seq")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, PINNED_SEQ, null)
      result_ = prefix(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && pinned_seq_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // abc_one | abc_two
    private fun pinned_seq_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_seq_1")) return false
      var result_: Boolean
      result_ = abc_one(builder_, level_ + 1)
      if (!result_) result_ = abc_two(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // prefix abc_one abc_two
    fun plain_seq(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "plain_seq")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = prefix(builder_, level_ + 1)
      result_ = result_ && abc_one(builder_, level_ + 1)
      result_ = result_ && abc_two(builder_, level_ + 1)
      exit_section_(builder_, marker_, PLAIN_SEQ, result_)
      return result_
    }

    /* ********************************************************** */
    // A
    fun prefix(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "prefix")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, A)
      exit_section_(builder_, marker_, PREFIX, result_)
      return result_
    }

  }
}