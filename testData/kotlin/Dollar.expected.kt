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
      return parseRoot(builder_, level_ + 1, GeneratedParser::`class_$`)
    }

    /* ********************************************************** */
    // token?
    fun `abstract_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`abstract_$`")) return false
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.ABSTRACT, "<abstract>")
      consumeToken(builder_, GeneratedTypes.TOKEN)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // token | interface | record
    fun `class_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`class_$`")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.CLASS, "<class>")
      result_ = consumeToken(builder_, GeneratedTypes.TOKEN)
      if (!result_) result_ = `interface_$`(builder_, level_ + 1)
      if (!result_) result_ = `record_$`(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // token
    fun `interface_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`interface_$`")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.TOKEN)
      exit_section_(builder_, marker_, GeneratedTypes.INTERFACE, result_)
      return result_
    }

    /* ********************************************************** */
    // 'token'
    fun `record_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`record_$`")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.RECORD, "<record>")
      result_ = consumeToken(builder_, "token")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}