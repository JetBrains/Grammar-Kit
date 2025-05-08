// ---- generated/GeneratedParser.kt -----------------
// This is a generated file. Not intended for manual editing.
package generated

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class GeneratedParser(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

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
      var result_: Boolean
      if (root_ == GeneratedSyntaxElementTypes.ELEMENT) {
        result_ = element(builder_, level_ + 1)
      }
      else if (root_ == GeneratedSyntaxElementTypes.ENTRY) {
        result_ = entry(builder_, level_ + 1)
      }
      else if (root_ == GeneratedSyntaxElementTypes.LIST) {
        result_ = list(builder_, level_ + 1)
      }
      else if (root_ == GeneratedSyntaxElementTypes.MAP) {
        result_ = map(builder_, level_ + 1)
      }
      else {
        result_ = grammar(builder_, level_ + 1)
      }
      return result_
    }

    /* ********************************************************** */
    // 'id'
    fun element(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.ELEMENT, "<element>")
      result_ = consumeToken(builder_, "id")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // 'name' '->' element
    fun entry(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "entry")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.ENTRY, "<entry>")
      result_ = consumeToken(builder_, "name")
      result_ = result_ && consumeToken(builder_, "->")
      result_ = result_ && element(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // list | map
    internal fun grammar(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "grammar")) return false
      var result_: Boolean
      result_ = list(builder_, level_ + 1)
      if (!result_) result_ = map(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // '(' element (',' element) * ')'
    fun list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.LIST, "<list>")
      result_ = consumeToken(builder_, "(")
      result_ = result_ && element(builder_, level_ + 1)
      result_ = result_ && list_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ")")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' element) *
    private fun list_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!list_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "list_2", pos_)) break
      }
      return true
    }

    // ',' element
    private fun list_2_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_2_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ",")
      result_ = result_ && element(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '(' entry (',' entry) * ')'
    fun map(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "map")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.MAP, "<map>")
      result_ = consumeToken(builder_, "(")
      result_ = result_ && entry(builder_, level_ + 1)
      result_ = result_ && map_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ")")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' entry) *
    private fun map_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "map_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!map_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "map_2", pos_)) break
      }
      return true
    }

    // ',' entry
    private fun map_2_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "map_2_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ",")
      result_ = result_ && entry(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

  }
}