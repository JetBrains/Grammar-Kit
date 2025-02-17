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
      var result_: Boolean
      if (root_ == ELEMENT) {
        result_ = element(builder_, level_ + 1)
      }
      else if (root_ == ENTRY) {
        result_ = entry(builder_, level_ + 1)
      }
      else if (root_ == LIST) {
        result_ = list(builder_, level_ + 1)
      }
      else if (root_ == MAP) {
        result_ = map(builder_, level_ + 1)
      }
      else {
        result_ = grammar(builder_, level_ + 1)
      }
      return result_
    }

    /* ********************************************************** */
    // 'id'
    fun element(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, ELEMENT, "<element>")
      result_ = consumeToken(builder_, "id")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // 'name' '->' element
    fun entry(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "entry")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, ENTRY, "<entry>")
      result_ = consumeToken(builder_, "name")
      result_ = result_ && consumeToken(builder_, "->")
      result_ = result_ && element(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // list | map
    internal fun grammar(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "grammar")) return false
      var result_: Boolean
      result_ = list(builder_, level_ + 1)
      if (!result_) result_ = map(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // '(' element (',' element) * ')'
    fun list(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, LIST, "<list>")
      result_ = consumeToken(builder_, "(")
      result_ = result_ && element(builder_, level_ + 1)
      result_ = result_ && list_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ")")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' element) *
    private fun list_2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!list_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "list_2", pos_)) break
      }
      return true
    }

    // ',' element
    private fun list_2_0(builder_: PsiBuilder, level_: Int): Boolean {
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
    fun map(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "map")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, MAP, "<map>")
      result_ = consumeToken(builder_, "(")
      result_ = result_ && entry(builder_, level_ + 1)
      result_ = result_ && map_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ")")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' entry) *
    private fun map_2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "map_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!map_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "map_2", pos_)) break
      }
      return true
    }

    // ',' entry
    private fun map_2_0(builder_: PsiBuilder, level_: Int): Boolean {
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