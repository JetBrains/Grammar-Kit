// ---- org/intellij/grammar/parser/GrammarParser.kt -----------------
// license.txt
package org.intellij.grammar.parser

import org.intellij.grammar.BnfSyntaxTypes
import org.intellij.grammar.syntax.grammar.GrammarParserUtil
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object GrammarParser {

  fun parse(type: SyntaxElementType, runtime: SyntaxGeneratedParserRuntime) {
    var result: Boolean
    runtime.init(::parse, EXTENDS_SETS_)
    val marker: Marker = runtime.enter_section_(0, Modifiers._COLLAPSE_, null)
    result = parse_root_(type, runtime, 0)
    runtime.exit_section_(0, marker, type, result, true, TRUE_CONDITION)
  }

  internal fun parse_root_(type: SyntaxElementType, runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    return GrammarParserUtil.parseGrammar(runtime, level + 1, GrammarParser::grammar_element)
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(BnfSyntaxTypes.BNF_CHOICE, BnfSyntaxTypes.BNF_EXPRESSION, BnfSyntaxTypes.BNF_EXTERNAL_EXPRESSION, BnfSyntaxTypes.BNF_LITERAL_EXPRESSION,
      BnfSyntaxTypes.BNF_PAREN_EXPRESSION, BnfSyntaxTypes.BNF_PAREN_OPT_EXPRESSION, BnfSyntaxTypes.BNF_PREDICATE, BnfSyntaxTypes.BNF_QUANTIFIED,
      BnfSyntaxTypes.BNF_REFERENCE_OR_TOKEN, BnfSyntaxTypes.BNF_SEQUENCE, BnfSyntaxTypes.BNF_STRING_LITERAL_EXPRESSION, BnfSyntaxTypes.BNF_VALUE_LIST),
  )

  /* ********************************************************** */
  // !attr_start_simple expression
  internal fun alt_choice_element(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "alt_choice_element")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = alt_choice_element_0(runtime, level + 1)
    result = result && expression(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !attr_start_simple
  private fun alt_choice_element_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "alt_choice_element_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !attr_start_simple(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // attr_start attr_value ';'?
  fun attr(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_ATTR, "<attr>")
    result = attr_start(runtime, level + 1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(attr_value(runtime, level + 1))
    result = pinned && attr_2(runtime, level + 1) && result
    runtime.exit_section_(level, marker, result, pinned, GrammarParser::attr_recover)
    return result || pinned
  }

  // ';'?
  private fun attr_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_2")) return false
    runtime.consumeToken(BnfSyntaxTypes.BNF_SEMICOLON)
    return true
  }

  /* ********************************************************** */
  // '(' string_literal_expression ')'
  fun attr_pattern(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_pattern")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_LEFT_PAREN)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_ATTR_PATTERN, null)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_PAREN)
    pinned = result // pin = 1
    result = result && runtime.report_error_(string_literal_expression(runtime, level + 1))
    result = pinned && runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_PAREN) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // !('}' | attr_start)
  internal fun attr_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !attr_recover_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '}' | attr_start
  private fun attr_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_recover_0")) return false
    var result: Boolean
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACE)
    if (!result) result = attr_start(runtime, level + 1)
    return result
  }

  /* ********************************************************** */
  // id (attr_pattern '=' | '=')
  internal fun attr_start(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_start")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_ID)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_ID)
    result = result && attr_start_1(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // attr_pattern '=' | '='
  private fun attr_start_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_start_1")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = attr_start_1_0(runtime, level + 1)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_EQ)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // attr_pattern '='
  private fun attr_start_1_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_start_1_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = attr_pattern(runtime, level + 1)
    pinned = result // pin = attr_pattern
    result = result && runtime.consumeToken(BnfSyntaxTypes.BNF_OP_EQ)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // id attr_pattern? '='
  internal fun attr_start_simple(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_start_simple")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_ID)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_ID)
    result = result && attr_start_simple_1(runtime, level + 1)
    result = result && runtime.consumeToken(BnfSyntaxTypes.BNF_OP_EQ)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // attr_pattern?
  private fun attr_start_simple_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_start_simple_1")) return false
    attr_pattern(runtime, level + 1)
    return true
  }

  /* ********************************************************** */
  // attr_value_inner !'='
  internal fun attr_value(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_value")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = attr_value_inner(runtime, level + 1)
    result = result && attr_value_1(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !'='
  private fun attr_value_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_value_1")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !runtime.consumeToken(BnfSyntaxTypes.BNF_OP_EQ)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // reference_or_token
  //   | literal_expression
  //   | value_list
  internal fun attr_value_inner(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attr_value_inner")) return false
    var result: Boolean
    result = reference_or_token(runtime, level + 1)
    if (!result) result = literal_expression(runtime, level + 1)
    if (!result) result = value_list(runtime, level + 1)
    return result
  }

  /* ********************************************************** */
  // '{' attr * '}'
  fun attrs(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attrs")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_LEFT_BRACE)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_ATTRS, null)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_BRACE)
    pinned = result // pin = 1
    result = result && runtime.report_error_(attrs_1(runtime, level + 1))
    result = pinned && runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACE) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // attr *
  private fun attrs_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "attrs_1")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!attr(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("attrs_1", pos)) break
    }
    return true
  }

  /* ********************************************************** */
  // ( '|' sequence ) +
  fun choice(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "choice")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_OP_OR)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._LEFT_, BnfSyntaxTypes.BNF_CHOICE, null)
    result = choice_0(runtime, level + 1)
    while (result) {
      val pos: Int = runtime.current_position_()
      if (!choice_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("choice", pos)) break
    }
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '|' sequence
  private fun choice_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "choice_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_OR)
    pinned = result // pin = 1
    result = result && sequence(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // sequence choice?
  fun expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "expression")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._COLLAPSE_, BnfSyntaxTypes.BNF_EXPRESSION, "<expression>")
    result = sequence(runtime, level + 1)
    result = result && expression_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // choice?
  private fun expression_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "expression_1")) return false
    choice(runtime, level + 1)
    return true
  }

  /* ********************************************************** */
  // '<<' reference_or_token option * '>>'
  fun external_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "external_expression")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_EXTERNAL_START)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_EXTERNAL_EXPRESSION, null)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_EXTERNAL_START)
    result = result && reference_or_token(runtime, level + 1)
    pinned = result // pin = 2
    result = result && runtime.report_error_(external_expression_2(runtime, level + 1))
    result = pinned && runtime.consumeToken(BnfSyntaxTypes.BNF_EXTERNAL_END) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // option *
  private fun external_expression_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "external_expression_2")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!option(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("external_expression_2", pos)) break
    }
    return true
  }

  /* ********************************************************** */
  // !<<eof>> (attrs | rule)
  internal fun grammar_element(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "grammar_element")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = grammar_element_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && grammar_element_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, GrammarParser::grammar_element_recover)
    return result || pinned
  }

  // !<<eof>>
  private fun grammar_element_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "grammar_element_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !runtime.eof(level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // attrs | rule
  private fun grammar_element_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "grammar_element_1")) return false
    var result: Boolean
    result = attrs(runtime, level + 1)
    if (!result) result = rule(runtime, level + 1)
    return result
  }

  /* ********************************************************** */
  // !('{'|rule_start)
  internal fun grammar_element_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "grammar_element_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !grammar_element_recover_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '{'|rule_start
  private fun grammar_element_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "grammar_element_recover_0")) return false
    var result: Boolean
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_BRACE)
    if (!result) result = rule_start(runtime, level + 1)
    return result
  }

  /* ********************************************************** */
  // (id list_entry_tail? | string_literal_expression) ';'?
  fun list_entry(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_LIST_ENTRY, "<list entry>")
    result = list_entry_0(runtime, level + 1)
    result = result && list_entry_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, GrammarParser::list_entry_recover)
    return result
  }

  // id list_entry_tail? | string_literal_expression
  private fun list_entry_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = list_entry_0_0(runtime, level + 1)
    if (!result) result = string_literal_expression(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // id list_entry_tail?
  private fun list_entry_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_ID)
    result = result && list_entry_0_0_1(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // list_entry_tail?
  private fun list_entry_0_0_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry_0_0_1")) return false
    list_entry_tail(runtime, level + 1)
    return true
  }

  // ';'?
  private fun list_entry_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry_1")) return false
    runtime.consumeToken(BnfSyntaxTypes.BNF_SEMICOLON)
    return true
  }

  /* ********************************************************** */
  // !(']' | '}' | id | string)
  internal fun list_entry_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !list_entry_recover_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // ']' | '}' | id | string
  private fun list_entry_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry_recover_0")) return false
    var result: Boolean
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACKET)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACE)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_ID)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_STRING)
    return result
  }

  /* ********************************************************** */
  // '=' string_literal_expression
  internal fun list_entry_tail(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "list_entry_tail")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_OP_EQ)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_EQ)
    pinned = result // pin = 1
    result = result && string_literal_expression(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // string_literal_expression | number
  fun literal_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "literal_expression")) return false
    if (!runtime.nextTokenIs("<literal expression>", BnfSyntaxTypes.BNF_NUMBER, BnfSyntaxTypes.BNF_STRING)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._COLLAPSE_, BnfSyntaxTypes.BNF_LITERAL_EXPRESSION, "<literal expression>")
    result = string_literal_expression(runtime, level + 1)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_NUMBER)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // 'private' | 'external' | 'meta'
  //   | 'inner' | 'left' | 'upper' | 'fake'
  fun modifier(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "modifier")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_MODIFIER, "<modifier>")
    result = runtime.consumeToken("private")
    if (!result) result = runtime.consumeToken("external")
    if (!result) result = runtime.consumeToken("meta")
    if (!result) result = runtime.consumeToken("inner")
    if (!result) result = runtime.consumeToken("left")
    if (!result) result = runtime.consumeToken("upper")
    if (!result) result = runtime.consumeToken("fake")
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // predicate | paren_opt_expression | simple quantified?
  internal fun option(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = predicate(runtime, level + 1)
    if (!result) result = paren_opt_expression(runtime, level + 1)
    if (!result) result = option_2(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // simple quantified?
  private fun option_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_2")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = simple(runtime, level + 1)
    result = result && option_2_1(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // quantified?
  private fun option_2_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_2_1")) return false
    quantified(runtime, level + 1)
    return true
  }

  /* ********************************************************** */
  // '(' expression ')' | '{' alt_choice_element '}'
  fun paren_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "paren_expression")) return false
    if (!runtime.nextTokenIs("<paren expression>", BnfSyntaxTypes.BNF_LEFT_BRACE, BnfSyntaxTypes.BNF_LEFT_PAREN)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_PAREN_EXPRESSION, "<paren expression>")
    result = paren_expression_0(runtime, level + 1)
    if (!result) result = paren_expression_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '(' expression ')'
  private fun paren_expression_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "paren_expression_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_PAREN)
    result = result && expression(runtime, level + 1)
    pinned = result // pin = 2
    result = result && runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_PAREN)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // '{' alt_choice_element '}'
  private fun paren_expression_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "paren_expression_1")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_BRACE)
    result = result && alt_choice_element(runtime, level + 1)
    pinned = result // pin = 2
    result = result && runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACE)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '[' expression ']'
  fun paren_opt_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "paren_opt_expression")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_LEFT_BRACKET)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_PAREN_OPT_EXPRESSION, null)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_BRACKET)
    result = result && expression(runtime, level + 1)
    pinned = result // pin = 2
    result = result && runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACKET)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // predicate_sign simple
  fun predicate(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "predicate")) return false
    if (!runtime.nextTokenIs("<predicate>", BnfSyntaxTypes.BNF_OP_AND, BnfSyntaxTypes.BNF_OP_NOT)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_PREDICATE, "<predicate>")
    result = predicate_sign(runtime, level + 1)
    result = result && simple(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '&' | '!'
  fun predicate_sign(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "predicate_sign")) return false
    if (!runtime.nextTokenIs("<predicate sign>", BnfSyntaxTypes.BNF_OP_AND, BnfSyntaxTypes.BNF_OP_NOT)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_PREDICATE_SIGN, "<predicate sign>")
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_AND)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_NOT)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // quantifier
  fun quantified(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "quantified")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._LEFT_, BnfSyntaxTypes.BNF_QUANTIFIED, "<quantified>")
    result = quantifier(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '?' | '+' | '*'
  fun quantifier(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "quantifier")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_QUANTIFIER, "<quantifier>")
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_OPT)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_ONEMORE)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_ZEROMORE)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // id
  fun reference_or_token(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "reference_or_token")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_ID)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_ID)
    runtime.exit_section_(marker, BnfSyntaxTypes.BNF_REFERENCE_OR_TOKEN, result)
    return result
  }

  /* ********************************************************** */
  // rule_start expression attrs? ';'?
  fun rule(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_RULE, "<rule>")
    result = rule_start(runtime, level + 1)
    result = result && expression(runtime, level + 1)
    pinned = result // pin = 2
    result = result && runtime.report_error_(rule_2(runtime, level + 1))
    result = pinned && rule_3(runtime, level + 1) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // attrs?
  private fun rule_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_2")) return false
    attrs(runtime, level + 1)
    return true
  }

  // ';'?
  private fun rule_3(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_3")) return false
    runtime.consumeToken(BnfSyntaxTypes.BNF_SEMICOLON)
    return true
  }

  /* ********************************************************** */
  // modifier* id '::='
  internal fun rule_start(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_start")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = rule_start_0(runtime, level + 1)
    result = result && runtime.consumeTokens(0, BnfSyntaxTypes.BNF_ID, BnfSyntaxTypes.BNF_OP_IS)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // modifier*
  private fun rule_start_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_start_0")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!modifier(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("rule_start_0", pos)) break
    }
    return true
  }

  /* ********************************************************** */
  // option *
  fun sequence(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence")) return false
    val marker: Marker = runtime.enter_section_(level, Modifiers._COLLAPSE_, BnfSyntaxTypes.BNF_SEQUENCE, "<sequence>")
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!option(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("sequence", pos)) break
    }
    runtime.exit_section_(level, marker, true, false, GrammarParser::sequence_recover)
    return true
  }

  /* ********************************************************** */
  // !(';'|'|'|'('|')'|'['|']'|'{'|'}') grammar_element_recover
  internal fun sequence_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = sequence_recover_0(runtime, level + 1)
    result = result && grammar_element_recover(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !(';'|'|'|'('|')'|'['|']'|'{'|'}')
  private fun sequence_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence_recover_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !sequence_recover_0_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // ';'|'|'|'('|')'|'['|']'|'{'|'}'
  private fun sequence_recover_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence_recover_0_0")) return false
    var result: Boolean
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_SEMICOLON)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_OP_OR)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_PAREN)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_PAREN)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_BRACKET)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACKET)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_BRACE)
    if (!result) result = runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACE)
    return result
  }

  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token
  //   | literal_expression
  //   | external_expression
  //   | paren_expression
  internal fun simple(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "simple")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = simple_0(runtime, level + 1)
    if (!result) result = literal_expression(runtime, level + 1)
    if (!result) result = external_expression(runtime, level + 1)
    if (!result) result = paren_expression(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !(modifier* id '::=' ) reference_or_token
  private fun simple_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "simple_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = simple_0_0(runtime, level + 1)
    result = result && reference_or_token(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !(modifier* id '::=' )
  private fun simple_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "simple_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !simple_0_0_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // modifier* id '::='
  private fun simple_0_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "simple_0_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = simple_0_0_0_0(runtime, level + 1)
    result = result && runtime.consumeTokens(0, BnfSyntaxTypes.BNF_ID, BnfSyntaxTypes.BNF_OP_IS)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // modifier*
  private fun simple_0_0_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "simple_0_0_0_0")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!modifier(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("simple_0_0_0_0", pos)) break
    }
    return true
  }

  /* ********************************************************** */
  // string
  fun string_literal_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "string_literal_expression")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_STRING)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_STRING)
    runtime.exit_section_(marker, BnfSyntaxTypes.BNF_STRING_LITERAL_EXPRESSION, result)
    return result
  }

  /* ********************************************************** */
  // '[' list_entry * ']'
  fun value_list(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "value_list")) return false
    if (!runtime.nextTokenIs(BnfSyntaxTypes.BNF_LEFT_BRACKET)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, BnfSyntaxTypes.BNF_VALUE_LIST, null)
    result = runtime.consumeToken(BnfSyntaxTypes.BNF_LEFT_BRACKET)
    pinned = result // pin = 1
    result = result && runtime.report_error_(value_list_1(runtime, level + 1))
    result = pinned && runtime.consumeToken(BnfSyntaxTypes.BNF_RIGHT_BRACKET) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // list_entry *
  private fun value_list_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "value_list_1")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!list_entry(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("value_list_1", pos)) break
    }
    return true
  }

}