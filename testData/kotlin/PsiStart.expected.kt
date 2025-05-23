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
    var result_: Boolean
    if (root_ == GeneratedSyntaxElementTypes.ELEMENT) {
      result_ = element(runtime_, level_ + 1)
    }
    else if (root_ == GeneratedSyntaxElementTypes.ENTRY) {
      result_ = entry(runtime_, level_ + 1)
    }
    else if (root_ == GeneratedSyntaxElementTypes.LIST) {
      result_ = list(runtime_, level_ + 1)
    }
    else if (root_ == GeneratedSyntaxElementTypes.MAP) {
      result_ = map(runtime_, level_ + 1)
    }
    else {
      result_ = grammar(runtime_, level_ + 1)
    }
    return result_
  }

  /* ********************************************************** */
  // 'id'
  fun element(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ELEMENT, "<element>")
    result_ = runtime_.consumeToken("id")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // 'name' '->' element
  fun entry(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "entry")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ENTRY, "<entry>")
    result_ = runtime_.consumeToken("name")
    result_ = result_ && runtime_.consumeToken("->")
    result_ = result_ && element(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // list | map
  internal fun grammar(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "grammar")) return false
    var result_: Boolean
    result_ = list(runtime_, level_ + 1)
    if (!result_) result_ = map(runtime_, level_ + 1)
    return result_
  }

  /* ********************************************************** */
  // '(' element (',' element) * ')'
  fun list(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.LIST, "<list>")
    result_ = runtime_.consumeToken("(")
    result_ = result_ && element(runtime_, level_ + 1)
    result_ = result_ && list_2(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(")")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // (',' element) *
  private fun list_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list_2")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!list_2_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("list_2", pos_)) break
    }
    return true
  }

  // ',' element
  private fun list_2_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "list_2_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(",")
    result_ = result_ && element(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // '(' entry (',' entry) * ')'
  fun map(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "map")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.MAP, "<map>")
    result_ = runtime_.consumeToken("(")
    result_ = result_ && entry(runtime_, level_ + 1)
    result_ = result_ && map_2(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(")")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // (',' entry) *
  private fun map_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "map_2")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!map_2_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("map_2", pos_)) break
    }
    return true
  }

  // ',' entry
  private fun map_2_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "map_2_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(",")
    result_ = result_ && entry(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

}