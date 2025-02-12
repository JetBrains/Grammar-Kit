// ---- GrammarParser.kt -----------------
// license.txt
package org.intellij.grammar.parser

import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import org.intellij.grammar.psi.BnfTypes.*
import org.intellij.grammar.parser.GrammarParserUtil.*
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiParser
import com.intellij.lang.LightPsiParser

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class GrammarParser: PsiParser, LightPsiParser {

  override fun parse(type: IElementType, builder: PsiBuilder): ASTNode {
    parseLight(type, builder)
    return builder.getTreeBuilt()
  }

  override fun parseLight(type: IElementType, builder: PsiBuilder) {
    var result: Boolean
    val builder = adapt_builder_(type, builder, this, EXTENDS_SETS_)
    val marker: Marker = enter_section_(builder, 0, _COLLAPSE_, null)
    result = parse_root_(type, builder)
    exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION)
  }

  protected fun parse_root_(type: IElementType, builder: PsiBuilder): Boolean {
    return parse_root_(type, builder, 0)
  }

  companion object {
    internal fun parse_root_(type: IElementType, builder: PsiBuilder, level: Int): Boolean {
      return parseGrammar(builder, level + 1, GrammarParser::grammar_element)
    }

    val EXTENDS_SETS_: Array<TokenSet> = arrayOf(
      create_token_set_(BNF_CHOICE, BNF_EXPRESSION, BNF_EXTERNAL_EXPRESSION, BNF_LITERAL_EXPRESSION,
        BNF_PAREN_EXPRESSION, BNF_PAREN_OPT_EXPRESSION, BNF_PREDICATE, BNF_QUANTIFIED,
        BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE, BNF_STRING_LITERAL_EXPRESSION, BNF_VALUE_LIST),
    )

    /* ********************************************************** */
    // !attr_start_simple expression
    internal fun alt_choice_element(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "alt_choice_element")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = alt_choice_element_0(builder, level + 1)
      result = result && expression(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // !attr_start_simple
    private fun alt_choice_element_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "alt_choice_element_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !attr_start_simple(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // attr_start attr_value ';'?
    fun attr(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr")) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_ATTR, "<attr>")
      result = attr_start(builder, level + 1)
      pinned = result // pin = 1
      result = result && report_error_(builder, attr_value(builder, level + 1))
      result = pinned && attr_2(builder, level + 1) && result
      exit_section_(builder, level, marker, result, pinned, GrammarParser::attr_recover)
      return result || pinned
    }

    // ';'?
    private fun attr_2(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_2")) return false
      consumeToken(builder, BNF_SEMICOLON)
      return true
    }

    /* ********************************************************** */
    // '(' string_literal_expression ')'
    fun attr_pattern(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_pattern")) return false
      if (!nextTokenIs(builder, BNF_LEFT_PAREN)) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_ATTR_PATTERN, null)
      result = consumeToken(builder, BNF_LEFT_PAREN)
      pinned = result // pin = 1
      result = result && report_error_(builder, string_literal_expression(builder, level + 1))
      result = pinned && consumeToken(builder, BNF_RIGHT_PAREN) && result
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    /* ********************************************************** */
    // !('}' | attr_start)
    internal fun attr_recover(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_recover")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !attr_recover_0(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // '}' | attr_start
    private fun attr_recover_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_recover_0")) return false
      var result: Boolean
      result = consumeToken(builder, BNF_RIGHT_BRACE)
      if (!result) result = attr_start(builder, level + 1)
      return result
    }

    /* ********************************************************** */
    // id (attr_pattern '=' | '=')
    internal fun attr_start(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_start")) return false
      if (!nextTokenIs(builder, BNF_ID)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = consumeToken(builder, BNF_ID)
      result = result && attr_start_1(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // attr_pattern '=' | '='
    private fun attr_start_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_start_1")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = attr_start_1_0(builder, level + 1)
      if (!result) result = consumeToken(builder, BNF_OP_EQ)
      exit_section_(builder, marker, null, result)
      return result
    }

    // attr_pattern '='
    private fun attr_start_1_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_start_1_0")) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_)
      result = attr_pattern(builder, level + 1)
      pinned = result // pin = attr_pattern
      result = result && consumeToken(builder, BNF_OP_EQ)
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    /* ********************************************************** */
    // id attr_pattern? '='
    internal fun attr_start_simple(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_start_simple")) return false
      if (!nextTokenIs(builder, BNF_ID)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = consumeToken(builder, BNF_ID)
      result = result && attr_start_simple_1(builder, level + 1)
      result = result && consumeToken(builder, BNF_OP_EQ)
      exit_section_(builder, marker, null, result)
      return result
    }

    // attr_pattern?
    private fun attr_start_simple_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_start_simple_1")) return false
      attr_pattern(builder, level + 1)
      return true
    }

    /* ********************************************************** */
    // attr_value_inner !'='
    internal fun attr_value(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_value")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = attr_value_inner(builder, level + 1)
      result = result && attr_value_1(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // !'='
    private fun attr_value_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_value_1")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !consumeToken(builder, BNF_OP_EQ)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // reference_or_token
    //   | literal_expression
    //   | value_list
    internal fun attr_value_inner(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attr_value_inner")) return false
      var result: Boolean
      result = reference_or_token(builder, level + 1)
      if (!result) result = literal_expression(builder, level + 1)
      if (!result) result = value_list(builder, level + 1)
      return result
    }

    /* ********************************************************** */
    // '{' attr * '}'
    fun attrs(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attrs")) return false
      if (!nextTokenIs(builder, BNF_LEFT_BRACE)) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_ATTRS, null)
      result = consumeToken(builder, BNF_LEFT_BRACE)
      pinned = result // pin = 1
      result = result && report_error_(builder, attrs_1(builder, level + 1))
      result = pinned && consumeToken(builder, BNF_RIGHT_BRACE) && result
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    // attr *
    private fun attrs_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "attrs_1")) return false
      while (true) {
        val pos: Int = current_position_(builder)
        if (!attr(builder, level + 1)) break
        if (!empty_element_parsed_guard_(builder, "attrs_1", pos)) break
      }
      return true
    }

    /* ********************************************************** */
    // ( '|' sequence ) +
    fun choice(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "choice")) return false
      if (!nextTokenIs(builder, BNF_OP_OR)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _LEFT_, BNF_CHOICE, null)
      result = choice_0(builder, level + 1)
      while (result) {
        val pos: Int = current_position_(builder)
        if (!choice_0(builder, level + 1)) break
        if (!empty_element_parsed_guard_(builder, "choice", pos)) break
      }
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // '|' sequence
    private fun choice_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "choice_0")) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_)
      result = consumeToken(builder, BNF_OP_OR)
      pinned = result // pin = 1
      result = result && sequence(builder, level + 1)
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    /* ********************************************************** */
    // sequence choice?
    fun expression(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "expression")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _COLLAPSE_, BNF_EXPRESSION, "<expression>")
      result = sequence(builder, level + 1)
      result = result && expression_1(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // choice?
    private fun expression_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "expression_1")) return false
      choice(builder, level + 1)
      return true
    }

    /* ********************************************************** */
    // '<<' reference_or_token option * '>>'
    fun external_expression(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "external_expression")) return false
      if (!nextTokenIs(builder, BNF_EXTERNAL_START)) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_EXTERNAL_EXPRESSION, null)
      result = consumeToken(builder, BNF_EXTERNAL_START)
      result = result && reference_or_token(builder, level + 1)
      pinned = result // pin = 2
      result = result && report_error_(builder, external_expression_2(builder, level + 1))
      result = pinned && consumeToken(builder, BNF_EXTERNAL_END) && result
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    // option *
    private fun external_expression_2(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "external_expression_2")) return false
      while (true) {
        val pos: Int = current_position_(builder)
        if (!option(builder, level + 1)) break
        if (!empty_element_parsed_guard_(builder, "external_expression_2", pos)) break
      }
      return true
    }

    /* ********************************************************** */
    // !<<eof>> (attrs | rule)
    internal fun grammar_element(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "grammar_element")) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_)
      result = grammar_element_0(builder, level + 1)
      pinned = result // pin = 1
      result = result && grammar_element_1(builder, level + 1)
      exit_section_(builder, level, marker, result, pinned, GrammarParser::grammar_element_recover)
      return result || pinned
    }

    // !<<eof>>
    private fun grammar_element_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "grammar_element_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !eof(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // attrs | rule
    private fun grammar_element_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "grammar_element_1")) return false
      var result: Boolean
      result = attrs(builder, level + 1)
      if (!result) result = rule(builder, level + 1)
      return result
    }

    /* ********************************************************** */
    // !('{'|rule_start)
    internal fun grammar_element_recover(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "grammar_element_recover")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !grammar_element_recover_0(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // '{'|rule_start
    private fun grammar_element_recover_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "grammar_element_recover_0")) return false
      var result: Boolean
      result = consumeToken(builder, BNF_LEFT_BRACE)
      if (!result) result = rule_start(builder, level + 1)
      return result
    }

    /* ********************************************************** */
    // (id list_entry_tail? | string_literal_expression) ';'?
    fun list_entry(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_LIST_ENTRY, "<list entry>")
      result = list_entry_0(builder, level + 1)
      result = result && list_entry_1(builder, level + 1)
      exit_section_(builder, level, marker, result, false, GrammarParser::list_entry_recover)
      return result
    }

    // id list_entry_tail? | string_literal_expression
    private fun list_entry_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = list_entry_0_0(builder, level + 1)
      if (!result) result = string_literal_expression(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // id list_entry_tail?
    private fun list_entry_0_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry_0_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = consumeToken(builder, BNF_ID)
      result = result && list_entry_0_0_1(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // list_entry_tail?
    private fun list_entry_0_0_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry_0_0_1")) return false
      list_entry_tail(builder, level + 1)
      return true
    }

    // ';'?
    private fun list_entry_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry_1")) return false
      consumeToken(builder, BNF_SEMICOLON)
      return true
    }

    /* ********************************************************** */
    // !(']' | '}' | id | string)
    internal fun list_entry_recover(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry_recover")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !list_entry_recover_0(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // ']' | '}' | id | string
    private fun list_entry_recover_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry_recover_0")) return false
      var result: Boolean
      result = consumeToken(builder, BNF_RIGHT_BRACKET)
      if (!result) result = consumeToken(builder, BNF_RIGHT_BRACE)
      if (!result) result = consumeToken(builder, BNF_ID)
      if (!result) result = consumeToken(builder, BNF_STRING)
      return result
    }

    /* ********************************************************** */
    // '=' string_literal_expression
    internal fun list_entry_tail(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "list_entry_tail")) return false
      if (!nextTokenIs(builder, BNF_OP_EQ)) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_)
      result = consumeToken(builder, BNF_OP_EQ)
      pinned = result // pin = 1
      result = result && string_literal_expression(builder, level + 1)
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    /* ********************************************************** */
    // string_literal_expression | number
    fun literal_expression(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "literal_expression")) return false
      if (!nextTokenIs(builder, "<literal expression>", BNF_NUMBER, BNF_STRING)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _COLLAPSE_, BNF_LITERAL_EXPRESSION, "<literal expression>")
      result = string_literal_expression(builder, level + 1)
      if (!result) result = consumeToken(builder, BNF_NUMBER)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // 'private' | 'external' | 'meta'
    //   | 'inner' | 'left' | 'upper' | 'fake'
    fun modifier(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "modifier")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_MODIFIER, "<modifier>")
      result = consumeToken(builder, "private")
      if (!result) result = consumeToken(builder, "external")
      if (!result) result = consumeToken(builder, "meta")
      if (!result) result = consumeToken(builder, "inner")
      if (!result) result = consumeToken(builder, "left")
      if (!result) result = consumeToken(builder, "upper")
      if (!result) result = consumeToken(builder, "fake")
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // predicate | paren_opt_expression | simple quantified?
    internal fun option(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "option")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = predicate(builder, level + 1)
      if (!result) result = paren_opt_expression(builder, level + 1)
      if (!result) result = option_2(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // simple quantified?
    private fun option_2(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "option_2")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = simple(builder, level + 1)
      result = result && option_2_1(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // quantified?
    private fun option_2_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "option_2_1")) return false
      quantified(builder, level + 1)
      return true
    }

    /* ********************************************************** */
    // '(' expression ')' | '{' alt_choice_element '}'
    fun paren_expression(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "paren_expression")) return false
      if (!nextTokenIs(builder, "<paren expression>", BNF_LEFT_BRACE, BNF_LEFT_PAREN)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_PAREN_EXPRESSION, "<paren expression>")
      result = paren_expression_0(builder, level + 1)
      if (!result) result = paren_expression_1(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // '(' expression ')'
    private fun paren_expression_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "paren_expression_0")) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_)
      result = consumeToken(builder, BNF_LEFT_PAREN)
      result = result && expression(builder, level + 1)
      pinned = result // pin = 2
      result = result && consumeToken(builder, BNF_RIGHT_PAREN)
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    // '{' alt_choice_element '}'
    private fun paren_expression_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "paren_expression_1")) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_)
      result = consumeToken(builder, BNF_LEFT_BRACE)
      result = result && alt_choice_element(builder, level + 1)
      pinned = result // pin = 2
      result = result && consumeToken(builder, BNF_RIGHT_BRACE)
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    /* ********************************************************** */
    // '[' expression ']'
    fun paren_opt_expression(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "paren_opt_expression")) return false
      if (!nextTokenIs(builder, BNF_LEFT_BRACKET)) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_PAREN_OPT_EXPRESSION, null)
      result = consumeToken(builder, BNF_LEFT_BRACKET)
      result = result && expression(builder, level + 1)
      pinned = result // pin = 2
      result = result && consumeToken(builder, BNF_RIGHT_BRACKET)
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    /* ********************************************************** */
    // predicate_sign simple
    fun predicate(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "predicate")) return false
      if (!nextTokenIs(builder, "<predicate>", BNF_OP_AND, BNF_OP_NOT)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_PREDICATE, "<predicate>")
      result = predicate_sign(builder, level + 1)
      result = result && simple(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // '&' | '!'
    fun predicate_sign(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "predicate_sign")) return false
      if (!nextTokenIs(builder, "<predicate sign>", BNF_OP_AND, BNF_OP_NOT)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_PREDICATE_SIGN, "<predicate sign>")
      result = consumeToken(builder, BNF_OP_AND)
      if (!result) result = consumeToken(builder, BNF_OP_NOT)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // quantifier
    fun quantified(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "quantified")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _LEFT_, BNF_QUANTIFIED, "<quantified>")
      result = quantifier(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // '?' | '+' | '*'
    fun quantifier(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "quantifier")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_QUANTIFIER, "<quantifier>")
      result = consumeToken(builder, BNF_OP_OPT)
      if (!result) result = consumeToken(builder, BNF_OP_ONEMORE)
      if (!result) result = consumeToken(builder, BNF_OP_ZEROMORE)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    /* ********************************************************** */
    // id
    fun reference_or_token(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "reference_or_token")) return false
      if (!nextTokenIs(builder, BNF_ID)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = consumeToken(builder, BNF_ID)
      exit_section_(builder, marker, BNF_REFERENCE_OR_TOKEN, result)
      return result
    }

    /* ********************************************************** */
    // rule_start expression attrs? ';'?
    fun rule(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "rule")) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_RULE, "<rule>")
      result = rule_start(builder, level + 1)
      result = result && expression(builder, level + 1)
      pinned = result // pin = 2
      result = result && report_error_(builder, rule_2(builder, level + 1))
      result = pinned && rule_3(builder, level + 1) && result
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    // attrs?
    private fun rule_2(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "rule_2")) return false
      attrs(builder, level + 1)
      return true
    }

    // ';'?
    private fun rule_3(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "rule_3")) return false
      consumeToken(builder, BNF_SEMICOLON)
      return true
    }

    /* ********************************************************** */
    // modifier* id '::='
    internal fun rule_start(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "rule_start")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = rule_start_0(builder, level + 1)
      result = result && consumeTokens(builder, 0, BNF_ID, BNF_OP_IS)
      exit_section_(builder, marker, null, result)
      return result
    }

    // modifier*
    private fun rule_start_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "rule_start_0")) return false
      while (true) {
        val pos: Int = current_position_(builder)
        if (!modifier(builder, level + 1)) break
        if (!empty_element_parsed_guard_(builder, "rule_start_0", pos)) break
      }
      return true
    }

    /* ********************************************************** */
    // option *
    fun sequence(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "sequence")) return false
      val marker: Marker = enter_section_(builder, level, _COLLAPSE_, BNF_SEQUENCE, "<sequence>")
      while (true) {
        val pos: Int = current_position_(builder)
        if (!option(builder, level + 1)) break
        if (!empty_element_parsed_guard_(builder, "sequence", pos)) break
      }
      exit_section_(builder, level, marker, true, false, GrammarParser::sequence_recover)
      return true
    }

    /* ********************************************************** */
    // !(';'|'|'|'('|')'|'['|']'|'{'|'}') grammar_element_recover
    internal fun sequence_recover(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "sequence_recover")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = sequence_recover_0(builder, level + 1)
      result = result && grammar_element_recover(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // !(';'|'|'|'('|')'|'['|']'|'{'|'}')
    private fun sequence_recover_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "sequence_recover_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !sequence_recover_0_0(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // ';'|'|'|'('|')'|'['|']'|'{'|'}'
    private fun sequence_recover_0_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "sequence_recover_0_0")) return false
      var result: Boolean
      result = consumeToken(builder, BNF_SEMICOLON)
      if (!result) result = consumeToken(builder, BNF_OP_OR)
      if (!result) result = consumeToken(builder, BNF_LEFT_PAREN)
      if (!result) result = consumeToken(builder, BNF_RIGHT_PAREN)
      if (!result) result = consumeToken(builder, BNF_LEFT_BRACKET)
      if (!result) result = consumeToken(builder, BNF_RIGHT_BRACKET)
      if (!result) result = consumeToken(builder, BNF_LEFT_BRACE)
      if (!result) result = consumeToken(builder, BNF_RIGHT_BRACE)
      return result
    }

    /* ********************************************************** */
    // !(modifier* id '::=' ) reference_or_token
    //   | literal_expression
    //   | external_expression
    //   | paren_expression
    internal fun simple(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "simple")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = simple_0(builder, level + 1)
      if (!result) result = literal_expression(builder, level + 1)
      if (!result) result = external_expression(builder, level + 1)
      if (!result) result = paren_expression(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // !(modifier* id '::=' ) reference_or_token
    private fun simple_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "simple_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = simple_0_0(builder, level + 1)
      result = result && reference_or_token(builder, level + 1)
      exit_section_(builder, marker, null, result)
      return result
    }

    // !(modifier* id '::=' )
    private fun simple_0_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "simple_0_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder, level, _NOT_)
      result = !simple_0_0_0(builder, level + 1)
      exit_section_(builder, level, marker, result, false, null)
      return result
    }

    // modifier* id '::='
    private fun simple_0_0_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "simple_0_0_0")) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = simple_0_0_0_0(builder, level + 1)
      result = result && consumeTokens(builder, 0, BNF_ID, BNF_OP_IS)
      exit_section_(builder, marker, null, result)
      return result
    }

    // modifier*
    private fun simple_0_0_0_0(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "simple_0_0_0_0")) return false
      while (true) {
        val pos: Int = current_position_(builder)
        if (!modifier(builder, level + 1)) break
        if (!empty_element_parsed_guard_(builder, "simple_0_0_0_0", pos)) break
      }
      return true
    }

    /* ********************************************************** */
    // string
    fun string_literal_expression(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "string_literal_expression")) return false
      if (!nextTokenIs(builder, BNF_STRING)) return false
      var result: Boolean
      val marker: Marker = enter_section_(builder)
      result = consumeToken(builder, BNF_STRING)
      exit_section_(builder, marker, BNF_STRING_LITERAL_EXPRESSION, result)
      return result
    }

    /* ********************************************************** */
    // '[' list_entry * ']'
    fun value_list(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "value_list")) return false
      if (!nextTokenIs(builder, BNF_LEFT_BRACKET)) return false
      var result: Boolean
      var pinned: Boolean
      val marker: Marker = enter_section_(builder, level, _NONE_, BNF_VALUE_LIST, null)
      result = consumeToken(builder, BNF_LEFT_BRACKET)
      pinned = result // pin = 1
      result = result && report_error_(builder, value_list_1(builder, level + 1))
      result = pinned && consumeToken(builder, BNF_RIGHT_BRACKET) && result
      exit_section_(builder, level, marker, result, pinned, null)
      return result || pinned
    }

    // list_entry *
    private fun value_list_1(builder: PsiBuilder, level: Int): Boolean {
      if (!recursion_guard_(builder, level, "value_list_1")) return false
      while (true) {
        val pos: Int = current_position_(builder)
        if (!list_entry(builder, level + 1)) break
        if (!empty_element_parsed_guard_(builder, "value_list_1", pos)) break
      }
      return true
    }

  }
}