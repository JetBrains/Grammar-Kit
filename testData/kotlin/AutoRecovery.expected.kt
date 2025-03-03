// ---- GeneratedParser.kt -----------------
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
      return `file_$`(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // list (';' list ) *
    internal fun `file_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`file_$`")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.PAREN1)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = list(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && file_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // (';' list ) *
    private fun file_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "file_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!file_1_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "file_1", pos_)) break
      }
      return true
    }

    // ';' list
    private fun file_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "file_1_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, GeneratedTypes.SEMI)
      pinned_ = result_ // pin = 1
      result_ = result_ && list(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // number
    fun item(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "item")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.ITEM, "<item>")
      result_ = consumeToken(builder_, GeneratedTypes.NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, item_auto_recover_)
      return result_
    }

    /* ********************************************************** */
    // "(" [!")" item (',' item) * ] ")"
    fun list(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.PAREN1)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.LIST, null)
      result_ = consumeToken(builder_, GeneratedTypes.PAREN1)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, list_1(builder_, level_ + 1))
      result_ = pinned_ && consumeToken(builder_, GeneratedTypes.PAREN2) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // [!")" item (',' item) * ]
    private fun list_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_1")) return false
      list_1_0(builder_, level_ + 1)
      return true
    }

    // !")" item (',' item) *
    private fun list_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_1_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = list_1_0_0(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, item(builder_, level_ + 1))
      result_ = pinned_ && list_1_0_2(builder_, level_ + 1) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // !")"
    private fun list_1_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_1_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !consumeToken(builder_, GeneratedTypes.PAREN2)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' item) *
    private fun list_1_0_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_1_0_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!list_1_0_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "list_1_0_2", pos_)) break
      }
      return true
    }

    // ',' item
    private fun list_1_0_2_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "list_1_0_2_0")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, GeneratedTypes.COMMA)
      pinned_ = result_ // pin = 1
      result_ = result_ && item(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    internal val item_auto_recover_: Parser = Parser { builder_, level_ -> !nextTokenIsFast(builder_, GeneratedTypes.PAREN2, GeneratedTypes.COMMA, GeneratedTypes.SEMI) }
  }
}