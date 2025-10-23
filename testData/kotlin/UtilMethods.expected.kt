// ---- test/FooParser.kt -----------------
//header.txt
package test

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object FooParser {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, null)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // 'aa'
  fun element1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, FooSyntaxTypes.ELEMENT_1, "<element 1>")
    result_ = runtime_.consumeToken("aa")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // element1
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return element1(runtime_, level_ + 1)
  }

}