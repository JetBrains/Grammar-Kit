// ---- generated/GeneratedParser.kt -----------------
//header.txt
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
    runtime_.init(::parse, EXTENDS_SETS_)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return `file_$`(runtime_, level_ + 1)
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(GeneratedSyntaxElementTypes.ABC, GeneratedSyntaxElementTypes.ABC_ONE, GeneratedSyntaxElementTypes.ABC_THREE, GeneratedSyntaxElementTypes.ABC_TWO),
  )

  /* ********************************************************** */
  // abc_three? (abc_one | abc_two | abc_three)
  fun abc(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "abc")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.ABC, "<abc>")
    result_ = abc_0(runtime_, level_ + 1)
    result_ = result_ && abc_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // abc_three?
  private fun abc_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "abc_0")) return false
    abc_three(runtime_, level_ + 1)
    return true
  }

  // abc_one | abc_two | abc_three
  private fun abc_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "abc_1")) return false
    var result_: Boolean
    result_ = abc_one(runtime_, level_ + 1)
    if (!result_) result_ = abc_two(runtime_, level_ + 1)
    if (!result_) result_ = abc_three(runtime_, level_ + 1)
    return result_
  }

  /* ********************************************************** */
  // just_b X
  fun abc_one(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "abc_one")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.B)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._UPPER_, GeneratedSyntaxElementTypes.ABC_ONE, null)
    result_ = just_b(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.X)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // X
  fun abc_three(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "abc_three")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.X)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.X)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.ABC_THREE, result_)
    return result_
  }

  /* ********************************************************** */
  // C
  fun abc_two(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "abc_two")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.C)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._UPPER_, GeneratedSyntaxElementTypes.ABC_TWO, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.C)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // abc | pinned_seq | plain_seq
  internal fun content(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "content")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = abc(runtime_, level_ + 1)
    if (!result_) result_ = pinned_seq(runtime_, level_ + 1)
    if (!result_) result_ = plain_seq(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, GeneratedParser::content_recover)
    return result_
  }

  /* ********************************************************** */
  // !';'
  internal fun content_recover(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "content_recover")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !runtime_.consumeToken(";")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // (content ';') *
  internal fun `file_$`(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "`file_$`")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!file_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("`file_$`", pos_)) break
    }
    return true
  }

  // content ';'
  private fun file_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "file_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = content(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken(";")
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // B
  fun just_b(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "just_b")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.B)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.JUST_B, result_)
    return result_
  }

  /* ********************************************************** */
  // prefix (abc_one | abc_two)
  fun pinned_seq(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_seq")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.PINNED_SEQ, null)
    result_ = prefix(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && pinned_seq_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // abc_one | abc_two
  private fun pinned_seq_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_seq_1")) return false
    var result_: Boolean
    result_ = abc_one(runtime_, level_ + 1)
    if (!result_) result_ = abc_two(runtime_, level_ + 1)
    return result_
  }

  /* ********************************************************** */
  // prefix abc_one abc_two
  fun plain_seq(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "plain_seq")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = prefix(runtime_, level_ + 1)
    result_ = result_ && abc_one(runtime_, level_ + 1)
    result_ = result_ && abc_two(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.PLAIN_SEQ, result_)
    return result_
  }

  /* ********************************************************** */
  // A
  fun prefix(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "prefix")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.PREFIX, result_)
    return result_
  }

}