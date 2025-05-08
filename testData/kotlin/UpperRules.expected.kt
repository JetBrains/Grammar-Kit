// ---- generated/GeneratedParser.kt -----------------
//header.txt
package generated

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class GeneratedParser(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

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
      return `file_$`(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(GeneratedSyntaxElementTypes.ABC, GeneratedSyntaxElementTypes.ABC_ONE, GeneratedSyntaxElementTypes.ABC_THREE, GeneratedSyntaxElementTypes.ABC_TWO),
    )

    /* ********************************************************** */
    // abc_three? (abc_one | abc_two | abc_three)
    fun abc(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.ABC, "<abc>")
      result_ = abc_0(builder_, level_ + 1)
      result_ = result_ && abc_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // abc_three?
    private fun abc_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_0")) return false
      abc_three(builder_, level_ + 1)
      return true
    }

    // abc_one | abc_two | abc_three
    private fun abc_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_1")) return false
      var result_: Boolean
      result_ = abc_one(builder_, level_ + 1)
      if (!result_) result_ = abc_two(builder_, level_ + 1)
      if (!result_) result_ = abc_three(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // just_b X
    fun abc_one(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_one")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.B)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _UPPER_, GeneratedSyntaxElementTypes.ABC_ONE, null)
      result_ = just_b(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, GeneratedSyntaxElementTypes.X)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // X
    fun abc_three(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_three")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.X)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.X)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.ABC_THREE, result_)
      return result_
    }

    /* ********************************************************** */
    // C
    fun abc_two(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "abc_two")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.C)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _UPPER_, GeneratedSyntaxElementTypes.ABC_TWO, null)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.C)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // abc | pinned_seq | plain_seq
    internal fun content(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
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
    internal fun content_recover(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "content_recover")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !consumeToken(builder_, ";")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // (content ';') *
    internal fun `file_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`file_$`")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!file_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "`file_$`", pos_)) break
      }
      return true
    }

    // content ';'
    private fun file_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
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
    fun just_b(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "just_b")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.B)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.JUST_B, result_)
      return result_
    }

    /* ********************************************************** */
    // prefix (abc_one | abc_two)
    fun pinned_seq(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_seq")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.A)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.PINNED_SEQ, null)
      result_ = prefix(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && pinned_seq_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // abc_one | abc_two
    private fun pinned_seq_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_seq_1")) return false
      var result_: Boolean
      result_ = abc_one(builder_, level_ + 1)
      if (!result_) result_ = abc_two(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // prefix abc_one abc_two
    fun plain_seq(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "plain_seq")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = prefix(builder_, level_ + 1)
      result_ = result_ && abc_one(builder_, level_ + 1)
      result_ = result_ && abc_two(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.PLAIN_SEQ, result_)
      return result_
    }

    /* ********************************************************** */
    // A
    fun prefix(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "prefix")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.A)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.PREFIX, result_)
      return result_
    }

  }
}