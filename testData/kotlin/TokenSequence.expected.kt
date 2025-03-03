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
      return foo(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // a
    fun bar(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "bar")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      exit_section_(builder_, marker_, GeneratedTypes.BAR, result_)
      return result_
    }

    /* ********************************************************** */
    // a 
    //   | b c d
    //   | c d e
    //   | bar d e f
    internal fun foo(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "foo")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.A)
      if (!result_) result_ = parseTokens(builder_, 2, GeneratedTypes.B, GeneratedTypes.C, GeneratedTypes.D)
      if (!result_) result_ = parseTokens(builder_, 2, GeneratedTypes.C, GeneratedTypes.D, GeneratedTypes.E)
      if (!result_) result_ = foo_3(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // bar d e f
    private fun foo_3(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "foo_3")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = bar(builder_, level_ + 1)
      result_ = result_ && consumeTokens(builder_, 1, GeneratedTypes.D, GeneratedTypes.E, GeneratedTypes.F)
      pinned_ = result_ // pin = 2
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

  }
}