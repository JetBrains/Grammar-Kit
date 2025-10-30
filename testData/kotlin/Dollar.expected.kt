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
    return parseRoot(runtime_, level_ + 1, GeneratedParser::class__)
  }

  /* ********************************************************** */
  // token?
  fun abstract__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "abstract__")) return false
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ABSTRACT, "<abstract>")
    runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    runtime_.exit_section_(level_, marker_, true, false, null)
    return true
  }

  /* ********************************************************** */
  // token | interface | record
  fun class__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "class__")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.CLASS, "<class>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    if (!result_) result_ = interface__(runtime_, level_ + 1)
    if (!result_) result_ = record__(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // token
  fun interface__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "interface__")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.INTERFACE, result_)
    return result_
  }

  /* ********************************************************** */
  // 'token'
  fun record__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "record__")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.RECORD, "<record>")
    result_ = runtime_.consumeToken("token")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

}