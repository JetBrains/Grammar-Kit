// ---- test/FooParser.kt -----------------
//header.txt
package test

import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object FooParser {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, EXTENDS_SETS_)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(GeneratedSyntaxElementTypes.INTERFACE_TYPE, GeneratedSyntaxElementTypes.STRUCT_TYPE, GeneratedSyntaxElementTypes.TYPE),
  )

  /* ********************************************************** */
  // 'aa' element5
  fun element1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ELEMENT_1, "<element 1>")
    result_ = runtime_.consumeToken("aa")
    result_ = result_ && element5(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // 'bb' element4*
  fun element2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element2")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ELEMENT_2, "<element 2>")
    result_ = runtime_.consumeToken("bb")
    result_ = result_ && element2_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // element4*
  private fun element2_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element2_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!element4(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("element2_1", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // 'bb' element4
  fun element3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element3")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ELEMENT_3, "<element 3>")
    result_ = runtime_.consumeToken("bb")
    result_ = result_ && element4(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // 'bb' | element2
  fun element4(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element4")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ELEMENT_4, "<element 4>")
    result_ = runtime_.consumeToken("bb")
    if (!result_) result_ = element2(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // 'cc'
  fun element5(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "element5")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ELEMENT_5, "<element 5>")
    result_ = runtime_.consumeToken("cc")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // 'interface'
  fun interface_type(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "interface_type")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.INTERFACE_TYPE, "<interface type>")
    result_ = runtime_.consumeToken("interface")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // element1 | element2 | element3 | element4 | element5 | type
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root")) return false
    var result_: Boolean
    result_ = element1(runtime_, level_ + 1)
    if (!result_) result_ = element2(runtime_, level_ + 1)
    if (!result_) result_ = element3(runtime_, level_ + 1)
    if (!result_) result_ = element4(runtime_, level_ + 1)
    if (!result_) result_ = element5(runtime_, level_ + 1)
    if (!result_) result_ = type(runtime_, level_ + 1)
    return result_
  }

  /* ********************************************************** */
  // 'struct'
  fun struct_type(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "struct_type")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.STRUCT_TYPE, "<struct type>")
    result_ = runtime_.consumeToken("struct")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // interface_type | struct_type
  fun type(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "type")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.TYPE, "<type>")
    result_ = interface_type(runtime_, level_ + 1)
    if (!result_) result_ = struct_type(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

}
