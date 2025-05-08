// ---- test/FooParser.kt -----------------
//header.txt
package test

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class FooParser(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: SyntaxElementType, builder_: SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(FooSyntaxTypes.INTERFACE_TYPE, FooSyntaxTypes.STRUCT_TYPE, FooSyntaxTypes.TYPE),
    )

    /* ********************************************************** */
    // 'aa' element5
    fun element1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, FooSyntaxTypes.ELEMENT_1, "<element 1>")
      result_ = consumeToken(builder_, "aa")
      result_ = result_ && element5(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // 'bb' element4*
    fun element2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element2")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, FooSyntaxTypes.ELEMENT_2, "<element 2>")
      result_ = consumeToken(builder_, "bb")
      result_ = result_ && element2_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // element4*
    private fun element2_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element2_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!element4(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "element2_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // 'bb' element4
    fun element3(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element3")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, FooSyntaxTypes.ELEMENT_3, "<element 3>")
      result_ = consumeToken(builder_, "bb")
      result_ = result_ && element4(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // 'bb' | element2
    fun element4(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element4")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, FooSyntaxTypes.ELEMENT_4, "<element 4>")
      result_ = consumeToken(builder_, "bb")
      if (!result_) result_ = element2(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // 'cc'
    fun element5(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "element5")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, FooSyntaxTypes.ELEMENT_5, "<element 5>")
      result_ = consumeToken(builder_, "cc")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // 'interface'
    fun interface_type(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "interface_type")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, FooSyntaxTypes.INTERFACE_TYPE, "<interface type>")
      result_ = consumeToken(builder_, "interface")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // element1 | element2 | element3 | element4 | element5 | type
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root")) return false
      var result_: Boolean
      result_ = element1(builder_, level_ + 1)
      if (!result_) result_ = element2(builder_, level_ + 1)
      if (!result_) result_ = element3(builder_, level_ + 1)
      if (!result_) result_ = element4(builder_, level_ + 1)
      if (!result_) result_ = element5(builder_, level_ + 1)
      if (!result_) result_ = type(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // 'struct'
    fun struct_type(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "struct_type")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, FooSyntaxTypes.STRUCT_TYPE, "<struct type>")
      result_ = consumeToken(builder_, "struct")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // interface_type | struct_type
    fun type(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "type")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, FooSyntaxTypes.TYPE, "<type>")
      result_ = interface_type(builder_, level_ + 1)
      if (!result_) result_ = struct_type(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}