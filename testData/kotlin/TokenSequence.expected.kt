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
    return foo(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // a
  fun bar(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "bar")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.BAR, result_)
    return result_
  }

  /* ********************************************************** */
  // a 
  //   | b c d
  //   | c d e
  //   | bar d e f
  internal fun foo(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "foo")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = runtime_.parseTokens(2, GeneratedSyntaxElementTypes.B, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D)
    if (!result_) result_ = runtime_.parseTokens(2, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E)
    if (!result_) result_ = foo_3(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // bar d e f
  private fun foo_3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "foo_3")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = bar(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeTokens(1, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E, GeneratedSyntaxElementTypes.F)
    pinned_ = result_ // pin = 2
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

}