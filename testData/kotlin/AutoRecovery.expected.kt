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
    return file__(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // list (';' list ) *
  internal fun file__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "file__")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.PAREN1)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = list(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && file_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // (';' list ) *
  private fun file_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "file_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!file_1_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("file_1", pos_)) break
    }
    return true
  }

  // ';' list
  private fun file_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "file_1_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.SEMI)
    pinned_ = result_ // pin = 1
    result_ = result_ && list(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // number
  fun item(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "item")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ITEM, "<item>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.NUMBER)
    runtime_.exit_section_(level_, marker_, result_, false, item_auto_recover_)
    return result_
  }

  /* ********************************************************** */
  // "(" [!")" item (',' item) * ] ")"
  fun list(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.PAREN1)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.LIST, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.PAREN1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(list_1(runtime_, level_ + 1))
    result_ = pinned_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.PAREN2) && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // [!")" item (',' item) * ]
  private fun list_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list_1")) return false
    list_1_0(runtime_, level_ + 1)
    return true
  }

  // !")" item (',' item) *
  private fun list_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list_1_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = list_1_0_0(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(item(runtime_, level_ + 1))
    result_ = pinned_ && list_1_0_2(runtime_, level_ + 1) && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // !")"
  private fun list_1_0_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list_1_0_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !runtime_.consumeToken(GeneratedSyntaxElementTypes.PAREN2)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // (',' item) *
  private fun list_1_0_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list_1_0_2")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!list_1_0_2_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("list_1_0_2", pos_)) break
    }
    return true
  }

  // ',' item
  private fun list_1_0_2_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list_1_0_2_0")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.COMMA)
    pinned_ = result_ // pin = 1
    result_ = result_ && item(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  internal val item_auto_recover_: Parser = { runtime_, level_ -> !runtime_.nextTokenIsFast(GeneratedSyntaxElementTypes.PAREN2, GeneratedSyntaxElementTypes.COMMA, GeneratedSyntaxElementTypes.SEMI) }
}