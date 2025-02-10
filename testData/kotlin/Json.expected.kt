// ---- JsonParser.kt -----------------
// This is a generated file. Not intended for manual editing.
package com.intellij.json

import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import com.intellij.json.JsonElementTypes.*
import com.intellij.json.psi.JsonParserUtil.*
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiParser
import com.intellij.lang.LightPsiParser

class JsonParser: PsiParser, LightPsiParser {

  override fun parse(root_: IElementType, builder_: PsiBuilder): ASTNode {
    parseLight(root_, builder_)
    return builder_.getTreeBuilt()
  }

  override fun parseLight(root_: IElementType, builder_: PsiBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: IElementType, builder_: PsiBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: IElementType, builder_: PsiBuilder, level_: Int): Boolean {
      return json(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<TokenSet> = arrayOf(
      create_token_set_(ARRAY, BOOLEAN_LITERAL, LITERAL, NULL_LITERAL,
        NUMBER_LITERAL, OBJECT, REFERENCE_EXPRESSION, STRING_LITERAL,
        VALUE),
    )

    /* ********************************************************** */
    // '[' array_element* ']'
    fun array(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array")) return false
      if (!nextTokenIs(builder_, L_BRACKET)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, ARRAY, null)
      result_ = consumeToken(builder_, L_BRACKET)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, array_1(builder_, level_ + 1))
      result_ = pinned_ && consumeToken(builder_, R_BRACKET) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // array_element*
    private fun array_1(builder_: PsiBuilder, level_: Int): Boolean {
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
    internal fun array_element(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array_element")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = `value`(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && array_element_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, JsonParser::not_bracket_or_next_value)
      return result_ || pinned_
    }

    // ','|&']'
    private fun array_element_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array_element_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, COMMA)
      if (!result_) result_ = array_element_1_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &']'
    private fun array_element_1_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "array_element_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = consumeToken(builder_, R_BRACKET)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // TRUE | FALSE
    fun boolean_literal(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "boolean_literal")) return false
      if (!nextTokenIs(builder_, "<boolean literal>", FALSE, TRUE)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, BOOLEAN_LITERAL, "<boolean literal>")
      result_ = consumeToken(builder_, TRUE)
      if (!result_) result_ = consumeToken(builder_, FALSE)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // value*
    internal fun json(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "json")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!`value`(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "json", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // string_literal | number_literal | boolean_literal | null_literal
    fun literal(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "literal")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, LITERAL, "<literal>")
      result_ = string_literal(builder_, level_ + 1)
      if (!result_) result_ = number_literal(builder_, level_ + 1)
      if (!result_) result_ = boolean_literal(builder_, level_ + 1)
      if (!result_) result_ = null_literal(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // !('}'|value)
    internal fun not_brace_or_next_value(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_brace_or_next_value")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !not_brace_or_next_value_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // '}'|value
    private fun not_brace_or_next_value_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_brace_or_next_value_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, R_CURLY)
      if (!result_) result_ = `value`(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // !(']'|value)
    internal fun not_bracket_or_next_value(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_bracket_or_next_value")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !not_bracket_or_next_value_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ']'|value
    private fun not_bracket_or_next_value_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_bracket_or_next_value_0")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, R_BRACKET)
      if (!result_) result_ = `value`(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // NULL
    fun null_literal(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "null_literal")) return false
      if (!nextTokenIs(builder_, NULL)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, NULL)
      exit_section_(builder_, marker_, NULL_LITERAL, result_)
      return result_
    }

    /* ********************************************************** */
    // NUMBER
    fun number_literal(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "number_literal")) return false
      if (!nextTokenIs(builder_, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, NUMBER)
      exit_section_(builder_, marker_, NUMBER_LITERAL, result_)
      return result_
    }

    /* ********************************************************** */
    // '{' object_element* '}'
    fun `object`(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`object`")) return false
      if (!nextTokenIs(builder_, L_CURLY)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, OBJECT, null)
      result_ = consumeToken(builder_, L_CURLY)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, object_1(builder_, level_ + 1))
      result_ = pinned_ && consumeToken(builder_, R_CURLY) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // object_element*
    private fun object_1(builder_: PsiBuilder, level_: Int): Boolean {
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
    internal fun object_element(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "object_element")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = `property`(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && object_element_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, JsonParser::not_brace_or_next_value)
      return result_ || pinned_
    }

    // ','|&'}'
    private fun object_element_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "object_element_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, COMMA)
      if (!result_) result_ = object_element_1_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &'}'
    private fun object_element_1_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "object_element_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = consumeToken(builder_, R_CURLY)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // property_name (':' property_value)
    fun `property`(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`property`")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, PROPERTY, "<property>")
      result_ = property_name(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && property_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // ':' property_value
    private fun property_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "property_1")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeToken(builder_, COLON)
      pinned_ = result_ // pin = 1
      result_ = result_ && property_value(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // literal | reference_expression
    internal fun property_name(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "property_name")) return false
      var result_: Boolean
      result_ = literal(builder_, level_ + 1)
      if (!result_) result_ = reference_expression(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // value
    internal fun property_value(builder_: PsiBuilder, level_: Int): Boolean {
      return `value`(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // IDENTIFIER
    fun reference_expression(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "reference_expression")) return false
      if (!nextTokenIs(builder_, IDENTIFIER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, IDENTIFIER)
      exit_section_(builder_, marker_, REFERENCE_EXPRESSION, result_)
      return result_
    }

    /* ********************************************************** */
    // SINGLE_QUOTED_STRING | DOUBLE_QUOTED_STRING
    fun string_literal(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "string_literal")) return false
      if (!nextTokenIs(builder_, "<string literal>", DOUBLE_QUOTED_STRING, SINGLE_QUOTED_STRING)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, STRING_LITERAL, "<string literal>")
      result_ = consumeToken(builder_, SINGLE_QUOTED_STRING)
      if (!result_) result_ = consumeToken(builder_, DOUBLE_QUOTED_STRING)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // object | array | literal | reference_expression
    fun `value`(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`value`")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, VALUE, "<value>")
      result_ = `object`(builder_, level_ + 1)
      if (!result_) result_ = array(builder_, level_ + 1)
      if (!result_) result_ = literal(builder_, level_ + 1)
      if (!result_) result_ = reference_expression(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}