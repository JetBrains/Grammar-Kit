// ---- JsonParser.kt -----------------
// This is a generated file. Not intended for manual editing.
package com.intellij.json

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class JsonParser(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

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
      return json(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(JsonElementTypes.ARRAY, JsonElementTypes.BOOLEAN_LITERAL, JsonElementTypes.LITERAL, JsonElementTypes.NULL_LITERAL,
        JsonElementTypes.NUMBER_LITERAL, JsonElementTypes.OBJECT, JsonElementTypes.REFERENCE_EXPRESSION, JsonElementTypes.STRING_LITERAL,
        JsonElementTypes.VALUE),
    )

    /* ********************************************************** */
    // '[' array_element* ']'
    fun array(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array")) return false
      if (!nextTokenIs(builder_, JsonElementTypes.L_BRACKET)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, JsonElementTypes.ARRAY, null)
      result_ = consumeToken(builder_, JsonElementTypes.L_BRACKET)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, array_1(builder_, level_ + 1))
      result_ = pinned_ && consumeToken(builder_, JsonElementTypes.R_BRACKET) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // array_element*
    private fun array_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!array_element(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "array_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // value (','|&']')
    internal fun array_element(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array_element")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = `value_$`(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && array_element_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, JsonParser::not_bracket_or_next_value)
      return result_ || pinned_
    }

    // ','|&']'
    private fun array_element_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array_element_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, JsonElementTypes.COMMA)
      if (!result_) result_ = array_element_1_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &']'
    private fun array_element_1_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array_element_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = consumeToken(builder_, JsonElementTypes.R_BRACKET)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // TRUE | FALSE
    fun boolean_literal(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "boolean_literal")) return false
      if (!nextTokenIs(builder_, "<boolean literal>", JsonElementTypes.FALSE, JsonElementTypes.TRUE)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, JsonElementTypes.BOOLEAN_LITERAL, "<boolean literal>")
      result_ = consumeToken(builder_, JsonElementTypes.TRUE)
      if (!result_) result_ = consumeToken(builder_, JsonElementTypes.FALSE)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // value*
    internal fun json(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "json")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!`value_$`(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "json", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // string_literal | number_literal | boolean_literal | null_literal
    fun literal(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "literal")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, JsonElementTypes.LITERAL, "<literal>")
      result_ = string_literal(builder_, level_ + 1)
      if (!result_) result_ = number_literal(builder_, level_ + 1)
      if (!result_) result_ = boolean_literal(builder_, level_ + 1)
      if (!result_) result_ = null_literal(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // !('}'|value)
    internal fun not_brace_or_next_value(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_brace_or_next_value")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !not_brace_or_next_value_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // '}'|value
    private fun not_brace_or_next_value_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_brace_or_next_value_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, JsonElementTypes.R_CURLY)
      if (!result_) result_ = `value_$`(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // !(']'|value)
    internal fun not_bracket_or_next_value(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_bracket_or_next_value")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !not_bracket_or_next_value_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ']'|value
    private fun not_bracket_or_next_value_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_bracket_or_next_value_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, JsonElementTypes.R_BRACKET)
      if (!result_) result_ = `value_$`(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // NULL
    fun null_literal(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "null_literal")) return false
      if (!nextTokenIs(builder_, JsonElementTypes.NULL)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, JsonElementTypes.NULL)
      exit_section_(builder_, marker_, JsonElementTypes.NULL_LITERAL, result_)
      return result_
    }

    /* ********************************************************** */
    // NUMBER
    fun number_literal(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "number_literal")) return false
      if (!nextTokenIs(builder_, JsonElementTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, JsonElementTypes.NUMBER)
      exit_section_(builder_, marker_, JsonElementTypes.NUMBER_LITERAL, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' object_element* '}'
    fun `object_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`object_$`")) return false
      if (!nextTokenIs(builder_, JsonElementTypes.L_CURLY)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, JsonElementTypes.OBJECT, null)
      result_ = consumeToken(builder_, JsonElementTypes.L_CURLY)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, object_1(builder_, level_ + 1))
      result_ = pinned_ && consumeToken(builder_, JsonElementTypes.R_CURLY) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // object_element*
    private fun object_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "object_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!object_element(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "object_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // property (','|&'}')
    internal fun object_element(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "object_element")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = `property_$`(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && object_element_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, JsonParser::not_brace_or_next_value)
      return result_ || pinned_
    }

    // ','|&'}'
    private fun object_element_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "object_element_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, JsonElementTypes.COMMA)
      if (!result_) result_ = object_element_1_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &'}'
    private fun object_element_1_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "object_element_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = consumeToken(builder_, JsonElementTypes.R_CURLY)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // property_name (':' property_value)
    fun `property_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`property_$`")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, JsonElementTypes.PROPERTY, "<property>")
      result_ = property_name(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && property_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // ':' property_value
    private fun property_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "property_1")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, JsonElementTypes.COLON)
      pinned_ = result_ // pin = 1
      result_ = result_ && property_value(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // literal | reference_expression
    internal fun property_name(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "property_name")) return false
      var result_: Boolean
      result_ = literal(builder_, level_ + 1)
      if (!result_) result_ = reference_expression(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // value
    internal fun property_value(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return `value_$`(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // IDENTIFIER
    fun reference_expression(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "reference_expression")) return false
      if (!nextTokenIs(builder_, JsonElementTypes.IDENTIFIER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, JsonElementTypes.IDENTIFIER)
      exit_section_(builder_, marker_, JsonElementTypes.REFERENCE_EXPRESSION, result_)
      return result_
    }

    /* ********************************************************** */
    // SINGLE_QUOTED_STRING | DOUBLE_QUOTED_STRING
    fun string_literal(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "string_literal")) return false
      if (!nextTokenIs(builder_, "<string literal>", JsonElementTypes.DOUBLE_QUOTED_STRING, JsonElementTypes.SINGLE_QUOTED_STRING)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, JsonElementTypes.STRING_LITERAL, "<string literal>")
      result_ = consumeToken(builder_, JsonElementTypes.SINGLE_QUOTED_STRING)
      if (!result_) result_ = consumeToken(builder_, JsonElementTypes.DOUBLE_QUOTED_STRING)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // object | array | literal | reference_expression
    fun `value_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`value_$`")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, JsonElementTypes.VALUE, "<value>")
      result_ = `object_$`(builder_, level_ + 1)
      if (!result_) result_ = array(builder_, level_ + 1)
      if (!result_) result_ = literal(builder_, level_ + 1)
      if (!result_) result_ = reference_expression(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}