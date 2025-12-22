// ---- org/intellij/jflex/parser/JFlexParser.kt -----------------
// license.txt
package org.intellij.jflex.parser

import org.intellij.jflex.JFlexTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object JFlexParser {

  fun parse(type: SyntaxElementType, runtime: SyntaxGeneratedParserRuntime) {
    var result: Boolean
    runtime.init(::parse, EXTENDS_SETS_)
    val marker: Marker = runtime.enter_section_(0, Modifiers._COLLAPSE_, null)
    result = parse_root_(type, runtime, 0)
    runtime.exit_section_(0, marker, type, result, true, TRUE_CONDITION)
  }

  internal fun parse_root_(type: SyntaxElementType, runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    return flex_file(runtime, level + 1)
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(JFlexTypes.FLEX_DECLARATIONS_SECTION, JFlexTypes.FLEX_LEXICAL_RULES_SECTION, JFlexTypes.FLEX_USER_CODE_SECTION),
    create_token_set_(JFlexTypes.FLEX_CHAR_RANGE, JFlexTypes.FLEX_CHOICE_EXPRESSION, JFlexTypes.FLEX_CLASS_EXPRESSION, JFlexTypes.FLEX_EXPRESSION,
      JFlexTypes.FLEX_LITERAL_EXPRESSION, JFlexTypes.FLEX_MACRO_REF_EXPRESSION, JFlexTypes.FLEX_NOT_EXPRESSION, JFlexTypes.FLEX_PAREN_EXPRESSION,
      JFlexTypes.FLEX_PREDEFINED_CLASS_EXPRESSION, JFlexTypes.FLEX_QUANTIFIER_EXPRESSION, JFlexTypes.FLEX_SEQUENCE_EXPRESSION),
  )

  /* ********************************************************** */
  // '{' raw? '}' | '|'
  fun action(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "action")) return false
    if (!runtime.nextTokenIs("<action>", JFlexTypes.FLEX_BAR, JFlexTypes.FLEX_BRACE1)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_JAVA_CODE, "<action>")
    result = action_0(runtime, level + 1)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_BAR)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '{' raw? '}'
  private fun action_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "action_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_BRACE1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(action_0_1(runtime, level + 1))
    result = pinned && runtime.consumeToken(JFlexTypes.FLEX_BRACE2) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // raw?
  private fun action_0_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "action_0_1")) return false
    runtime.consumeToken(JFlexTypes.FLEX_RAW)
    return true
  }

  /* ********************************************************** */
  // string | char_class | char_or_esc
  internal fun char_class_atom(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "char_class_atom")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, null, "<char>")
    result = runtime.consumeToken(JFlexTypes.FLEX_STRING)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_CHAR_CLASS)
    if (!result) result = char_or_esc(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // char_range | class_expression | macro_ref_expression | char_class_atom
  internal fun char_class_item(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "char_class_item")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, null, "<char>")
    result = char_range(runtime, level + 1)
    if (!result) result = class_expression(runtime, level + 1)
    if (!result) result = macro_ref_expression(runtime, level + 1)
    if (!result) result = char_class_atom(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '&&' | '||' | '~~' | '--'
  internal fun char_class_op(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "char_class_op")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, null, "<char>")
    result = runtime.consumeToken(JFlexTypes.FLEX_AMPAMP)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_BARBAR)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_TILDETILDE)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_DASHDASH)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // char | char_esc
  internal fun char_or_esc(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "char_or_esc")) return false
    if (!runtime.nextTokenIs("", JFlexTypes.FLEX_CHAR, JFlexTypes.FLEX_CHAR_ESC)) return false
    var result: Boolean
    result = runtime.consumeToken(JFlexTypes.FLEX_CHAR)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_CHAR_ESC)
    return result
  }

  /* ********************************************************** */
  // char_or_esc '-' char_or_esc
  fun char_range(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "char_range")) return false
    if (!runtime.nextTokenIs("<char>", JFlexTypes.FLEX_CHAR, JFlexTypes.FLEX_CHAR_ESC)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_CHAR_RANGE, "<char>")
    result = char_or_esc(runtime, level + 1)
    result = result && runtime.consumeToken(JFlexTypes.FLEX_DASH)
    result = result && char_or_esc(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // macro_definition | state_declaration | option
  internal fun declaration(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declaration")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = macro_definition(runtime, level + 1)
    if (!result) result = state_declaration(runtime, level + 1)
    if (!result) result = option(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, JFlexParser::declaration_recover)
    return result
  }

  /* ********************************************************** */
  // !(<<is_percent>> | id '=') section_recover
  internal fun declaration_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declaration_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = declaration_recover_0(runtime, level + 1)
    result = result && section_recover(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !(<<is_percent>> | id '=')
  private fun declaration_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declaration_recover_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !declaration_recover_0_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // <<is_percent>> | id '='
  private fun declaration_recover_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declaration_recover_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = JFlexParserUtil.is_percent(runtime, level + 1)
    if (!result) result = runtime.parseTokens(0, JFlexTypes.FLEX_ID, JFlexTypes.FLEX_EQ)
    runtime.exit_section_(marker, null, result)
    return result
  }

  /* ********************************************************** */
  // [] declaration (!(<<eof>> | '%%') declaration) *
  fun declarations_section(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declarations_section")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_DECLARATIONS_SECTION, "<declarations section>")
    result = declarations_section_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(declaration(runtime, level + 1))
    result = pinned && declarations_section_2(runtime, level + 1) && result
    runtime.exit_section_(level, marker, result, pinned, JFlexParser::section_recover)
    return result || pinned
  }

  // []
  private fun declarations_section_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    return true
  }

  // (!(<<eof>> | '%%') declaration) *
  private fun declarations_section_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declarations_section_2")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!declarations_section_2_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("declarations_section_2", pos)) break
    }
    return true
  }

  // !(<<eof>> | '%%') declaration
  private fun declarations_section_2_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declarations_section_2_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = declarations_section_2_0_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && declaration(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // !(<<eof>> | '%%')
  private fun declarations_section_2_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declarations_section_2_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !declarations_section_2_0_0_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // <<eof>> | '%%'
  private fun declarations_section_2_0_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "declarations_section_2_0_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.eof(level + 1)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_TWO_PERCS)
    runtime.exit_section_(marker, null, result)
    return result
  }

  /* ********************************************************** */
  // []
  //   user_code_section
  //   section_div
  //   declarations_section
  //   section_div
  //   lexical_rules_section
  internal fun flex_file(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "flex_file")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = flex_file_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(user_code_section(runtime, level + 1))
    result = pinned && runtime.report_error_(section_div(runtime, level + 1)) && result
    result = pinned && runtime.report_error_(declarations_section(runtime, level + 1)) && result
    result = pinned && runtime.report_error_(section_div(runtime, level + 1)) && result
    result = pinned && lexical_rules_section(runtime, level + 1) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // []
  private fun flex_file_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // raw?
  fun java_code(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_code")) return false
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_JAVA_CODE, "<java code>")
    runtime.consumeToken(JFlexTypes.FLEX_RAW)
    runtime.exit_section_(level, marker, true, false, null)
    return true
  }

  /* ********************************************************** */
  // id ( '.' id ) *
  fun java_name(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_name")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_ID)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_JAVA_NAME, null)
    result = runtime.consumeToken(JFlexTypes.FLEX_ID)
    pinned = result // pin = 1
    result = result && java_name_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // ( '.' id ) *
  private fun java_name_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_name_1")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!java_name_1_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("java_name_1", pos)) break
    }
    return true
  }

  // '.' id
  private fun java_name_1_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_name_1_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeTokens(1, JFlexTypes.FLEX_DOT, JFlexTypes.FLEX_ID)
    pinned = result // pin = 1
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // java_name java_type_parameters?
  fun java_type(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_JAVA_TYPE, "<java type>")
    result = java_name(runtime, level + 1)
    pinned = result // pin = 1
    result = result && java_type_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, JFlexParser::type_recover)
    return result || pinned
  }

  // java_type_parameters?
  private fun java_type_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_1")) return false
    java_type_parameters(runtime, level + 1)
    return true
  }

  /* ********************************************************** */
  // [java_type (',' java_type) *]
  internal fun java_type_list(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_list")) return false
    java_type_list_0(runtime, level + 1)
    return true
  }

  // java_type (',' java_type) *
  private fun java_type_list_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_list_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = java_type(runtime, level + 1)
    pinned = result // pin = 1
    result = result && java_type_list_0_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // (',' java_type) *
  private fun java_type_list_0_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_list_0_1")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!java_type_list_0_1_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("java_type_list_0_1", pos)) break
    }
    return true
  }

  // ',' java_type
  private fun java_type_list_0_1_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_list_0_1_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_COMMA)
    pinned = result // pin = 1
    result = result && java_type(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '<' !'>' java_type (',' java_type) * '>'
  fun java_type_parameters(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_parameters")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_ANGLE1)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_JAVA_TYPE_PARAMETERS, null)
    result = runtime.consumeToken(JFlexTypes.FLEX_ANGLE1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_type_parameters_1(runtime, level + 1))
    result = pinned && runtime.report_error_(java_type(runtime, level + 1)) && result
    result = pinned && runtime.report_error_(java_type_parameters_3(runtime, level + 1)) && result
    result = pinned && runtime.consumeToken(JFlexTypes.FLEX_ANGLE2) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // !'>'
  private fun java_type_parameters_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_parameters_1")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !runtime.consumeToken(JFlexTypes.FLEX_ANGLE2)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // (',' java_type) *
  private fun java_type_parameters_3(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_parameters_3")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!java_type_parameters_3_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("java_type_parameters_3", pos)) break
    }
    return true
  }

  // ',' java_type
  private fun java_type_parameters_3_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "java_type_parameters_3_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_COMMA)
    pinned = result // pin = 1
    result = result && java_type(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // [] rule_group_item +
  fun lexical_rules_section(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "lexical_rules_section")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_LEXICAL_RULES_SECTION, "<lexical rules section>")
    result = lexical_rules_section_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && lexical_rules_section_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, JFlexParser::section_recover)
    return result || pinned
  }

  // []
  private fun lexical_rules_section_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    return true
  }

  // rule_group_item +
  private fun lexical_rules_section_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "lexical_rules_section_1")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = rule_group_item(runtime, level + 1)
    while (result) {
      val pos: Int = runtime.current_position_()
      if (!rule_group_item(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("lexical_rules_section_1", pos)) break
    }
    runtime.exit_section_(marker, null, result)
    return result
  }

  /* ********************************************************** */
  // new_line <<p>> new_line
  internal fun line(runtime: SyntaxGeneratedParserRuntime, level: Int, aP: Parser): Boolean {
    if (!runtime.recursion_guard_(level, "line")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = new_line(runtime, level + 1)
    result = result && aP.parse(runtime, level)
    pinned = result // pin = 2
    result = result && new_line(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '$' | '/' expression
  fun look_ahead(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "look_ahead")) return false
    if (!runtime.nextTokenIs("<look ahead>", JFlexTypes.FLEX_DOLLAR, JFlexTypes.FLEX_FSLASH)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_LOOK_AHEAD, "<look ahead>")
    result = runtime.consumeToken(JFlexTypes.FLEX_DOLLAR)
    if (!result) result = look_ahead_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '/' expression
  private fun look_ahead_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "look_ahead_1")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(JFlexTypes.FLEX_FSLASH)
    result = result && expression(runtime, level + 1, -1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  /* ********************************************************** */
  // new_line id '=' expression
  fun macro_definition(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "macro_definition")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_MACRO_DEFINITION, "<macro definition>")
    result = new_line(runtime, level + 1)
    result = result && runtime.consumeTokens(2, JFlexTypes.FLEX_ID, JFlexTypes.FLEX_EQ)
    pinned = result // pin = 3
    result = result && expression(runtime, level + 1, -1)
    runtime.exit_section_(level, marker, result, pinned, JFlexParser::macro_definition_recover)
    return result || pinned
  }

  /* ********************************************************** */
  // !(new_line id '=' | '%%' | <<is_percent>>)
  internal fun macro_definition_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "macro_definition_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !macro_definition_recover_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // new_line id '=' | '%%' | <<is_percent>>
  private fun macro_definition_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "macro_definition_recover_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = macro_definition_recover_0_0(runtime, level + 1)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_TWO_PERCS)
    if (!result) result = JFlexParserUtil.is_percent(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // new_line id '='
  private fun macro_definition_recover_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "macro_definition_recover_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = new_line(runtime, level + 1)
    result = result && runtime.consumeTokens(0, JFlexTypes.FLEX_ID, JFlexTypes.FLEX_EQ)
    runtime.exit_section_(marker, null, result)
    return result
  }

  /* ********************************************************** */
  // id
  fun macro_reference(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "macro_reference")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_ID)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(JFlexTypes.FLEX_ID)
    runtime.exit_section_(marker, JFlexTypes.FLEX_MACRO_REFERENCE, result)
    return result
  }

  /* ********************************************************** */
  // &<<is_new_line>>
  internal fun new_line(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "new_line")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._AND_)
    result = JFlexParserUtil.is_new_line(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // option_class
  //   | option_implements
  //   | option_extends
  //   | option_public
  //   | option_final
  //   | option_abstract
  //   | option_api_private
  //   | option_user_code
  //   | option_init
  //   | option_init_throw
  //   | option_ctor_arg
  //   | option_scan_error
  //   | option_buffer_size
  //   | option_include
  //   | option_function
  //   | option_integer
  //   | option_intwrap
  //   | option_type
  //   | option_yylexthrow
  //   | option_eof_val
  //   | option_eof
  //   | option_eof_throw
  //   | option_eof_close
  //   | option_debug
  //   | option_standalone
  //   | option_cup
  //   | option_cup_sym
  //   | option_cup_debug
  //   | option_byacc
  //   | option_switch
  //   | option_table
  //   | option_7bit
  //   | option_16bit
  //   | option_full
  //   | option_unicode
  //   | option_ignore_case
  //   | option_count_char
  //   | option_count_line
  //   | option_count_column
  //   | option_obsolete
  fun option(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._COLLAPSE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = option_class(runtime, level + 1)
    if (!result) result = option_implements(runtime, level + 1)
    if (!result) result = option_extends(runtime, level + 1)
    if (!result) result = option_public(runtime, level + 1)
    if (!result) result = option_final(runtime, level + 1)
    if (!result) result = option_abstract(runtime, level + 1)
    if (!result) result = option_api_private(runtime, level + 1)
    if (!result) result = option_user_code(runtime, level + 1)
    if (!result) result = option_init(runtime, level + 1)
    if (!result) result = option_init_throw(runtime, level + 1)
    if (!result) result = option_ctor_arg(runtime, level + 1)
    if (!result) result = option_scan_error(runtime, level + 1)
    if (!result) result = option_buffer_size(runtime, level + 1)
    if (!result) result = option_include(runtime, level + 1)
    if (!result) result = option_function(runtime, level + 1)
    if (!result) result = option_integer(runtime, level + 1)
    if (!result) result = option_intwrap(runtime, level + 1)
    if (!result) result = option_type(runtime, level + 1)
    if (!result) result = option_yylexthrow(runtime, level + 1)
    if (!result) result = option_eof_val(runtime, level + 1)
    if (!result) result = option_eof(runtime, level + 1)
    if (!result) result = option_eof_throw(runtime, level + 1)
    if (!result) result = option_eof_close(runtime, level + 1)
    if (!result) result = option_debug(runtime, level + 1)
    if (!result) result = option_standalone(runtime, level + 1)
    if (!result) result = option_cup(runtime, level + 1)
    if (!result) result = option_cup_sym(runtime, level + 1)
    if (!result) result = option_cup_debug(runtime, level + 1)
    if (!result) result = option_byacc(runtime, level + 1)
    if (!result) result = option_switch(runtime, level + 1)
    if (!result) result = option_table(runtime, level + 1)
    if (!result) result = option_7bit(runtime, level + 1)
    if (!result) result = option_16bit(runtime, level + 1)
    if (!result) result = option_full(runtime, level + 1)
    if (!result) result = option_unicode(runtime, level + 1)
    if (!result) result = option_ignore_case(runtime, level + 1)
    if (!result) result = option_count_char(runtime, level + 1)
    if (!result) result = option_count_line(runtime, level + 1)
    if (!result) result = option_count_column(runtime, level + 1)
    if (!result) result = option_obsolete(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%16bit'
  fun option_16bit(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_16bit")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT16BIT)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT16BIT)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%7bit'
  fun option_7bit(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_7bit")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_7BIT)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_7BIT)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%abstract'
  fun option_abstract(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_abstract")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_ABSTRACT)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_ABSTRACT)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%apiprivate'
  fun option_api_private(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_api_private")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_APIPRIVATE)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_APIPRIVATE)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%buffer' number
  fun option_buffer_size(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_buffer_size")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_BUFFER)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeTokens(1, JFlexTypes.FLEX_OPT_BUFFER, JFlexTypes.FLEX_NUMBER)
    pinned = result // pin = 1
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%byacc'
  fun option_byacc(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_byacc")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken("%byacc")
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%class' java_type
  fun option_class(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_class")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_CLASS)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_CLASS)
    pinned = result // pin = 1
    result = result && java_type(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%char'
  fun option_count_char(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_count_char")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_CHAR)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_CHAR)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%column'
  fun option_count_column(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_count_column")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_COLUMN)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_COLUMN)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%line'
  fun option_count_line(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_count_line")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_LINE)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_LINE)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%ctorarg' java_type id
  fun option_ctor_arg(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_ctor_arg")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_CTORARG)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_CTORARG)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_type(runtime, level + 1))
    result = pinned && runtime.consumeToken(JFlexTypes.FLEX_ID) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%cup'
  fun option_cup(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_cup")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_CUP)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_CUP)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%cupdebug'
  fun option_cup_debug(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_cup_debug")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_CUPDEBUG)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_CUPDEBUG)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%cupsym' java_type
  fun option_cup_sym(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_cup_sym")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_CUPSYM)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_CUPSYM)
    pinned = result // pin = 1
    result = result && java_type(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%debug'
  fun option_debug(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_debug")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_DEBUG)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_DEBUG)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // <<line '%eof{'>> java_code <<line '%eof}'>>
  fun option_eof(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_eof")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = line(runtime, level + 1, opt_eof1_parser_)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_code(runtime, level + 1))
    result = pinned && line(runtime, level + 1, opt_eof2_parser_) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%eofclose' ['false']
  fun option_eof_close(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_eof_close")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_EOFCLOSE)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_EOFCLOSE)
    pinned = result // pin = 1
    result = result && option_eof_close_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // ['false']
  private fun option_eof_close_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_eof_close_1")) return false
    runtime.consumeToken("false")
    return true
  }

  /* ********************************************************** */
  // '%eofthrow' java_type_list | <<line '%eofthrow{'>> java_type_list <<line '%eofthrow}'>>
  fun option_eof_throw(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_eof_throw")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = option_eof_throw_0(runtime, level + 1)
    if (!result) result = option_eof_throw_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '%eofthrow' java_type_list
  private fun option_eof_throw_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_eof_throw_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_EOFTHROW)
    pinned = result // pin = 1
    result = result && java_type_list(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // <<line '%eofthrow{'>> java_type_list <<line '%eofthrow}'>>
  private fun option_eof_throw_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_eof_throw_1")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = line(runtime, level + 1, opt_eofthrow1_parser_)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_type_list(runtime, level + 1))
    result = pinned && line(runtime, level + 1, opt_eofthrow2_parser_) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // <<line '%eofval{'>> java_code <<line '%eofval}'>>
  fun option_eof_val(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_eof_val")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = line(runtime, level + 1, opt_eofval1_parser_)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_code(runtime, level + 1))
    result = pinned && line(runtime, level + 1, opt_eofval2_parser_) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%extends' java_type
  fun option_extends(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_extends")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_EXTENDS)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_EXTENDS)
    pinned = result // pin = 1
    result = result && java_type(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%final'
  fun option_final(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_final")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_FINAL)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_FINAL)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%full' | '%8bit'
  fun option_full(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_full")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_8BIT, JFlexTypes.FLEX_OPT_FULL)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_FULL)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_OPT_8BIT)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%function' id
  fun option_function(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_function")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_FUNCTION)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeTokens(1, JFlexTypes.FLEX_OPT_FUNCTION, JFlexTypes.FLEX_ID)
    pinned = result // pin = 1
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%caseless' | '%ignorecase'
  fun option_ignore_case(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_ignore_case")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_CASELESS, JFlexTypes.FLEX_OPT_IGNORECASE)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_CASELESS)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_OPT_IGNORECASE)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%implements' java_type_list
  fun option_implements(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_implements")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_IMPLEMENTS)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_IMPLEMENTS)
    pinned = result // pin = 1
    result = result && java_type_list(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%include' user_value
  fun option_include(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_include")) return false
    if (!runtime.nextTokenIs("<include>", JFlexTypes.FLEX_OPT_INCLUDE)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<include>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_INCLUDE)
    pinned = result // pin = 1
    result = result && user_value(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // <<line '%init{'>> java_code <<line '%init}'>>
  fun option_init(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_init")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = line(runtime, level + 1, opt_init1_parser_)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_code(runtime, level + 1))
    result = pinned && line(runtime, level + 1, opt_init2_parser_) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%initthrow' java_type_list | <<line '%initthrow{'>> java_type_list <<line '%initthrow}'>>
  fun option_init_throw(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_init_throw")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = option_init_throw_0(runtime, level + 1)
    if (!result) result = option_init_throw_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '%initthrow' java_type_list
  private fun option_init_throw_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_init_throw_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_INITTHROW)
    pinned = result // pin = 1
    result = result && java_type_list(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // <<line '%initthrow{'>> java_type_list <<line '%initthrow}'>>
  private fun option_init_throw_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_init_throw_1")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = line(runtime, level + 1, opt_initthrow1_parser_)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_type_list(runtime, level + 1))
    result = pinned && line(runtime, level + 1, opt_initthrow2_parser_) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%integer' | '%int'
  fun option_integer(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_integer")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_INT, JFlexTypes.FLEX_OPT_INTEGER)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_INTEGER)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_OPT_INT)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%intwrap'
  fun option_intwrap(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_intwrap")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_INTWRAP)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_INTWRAP)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%notunix' | '%yyeof'
  fun option_obsolete(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_obsolete")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_NOTUNIX, JFlexTypes.FLEX_OPT_YYEOF)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_NOTUNIX)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_OPT_YYEOF)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%public'
  fun option_public(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_public")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_PUBLIC)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_PUBLIC)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%scanerror' java_type
  fun option_scan_error(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_scan_error")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_SCANERROR)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_SCANERROR)
    pinned = result // pin = 1
    result = result && java_type(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%standalone'
  fun option_standalone(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_standalone")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_STANDALONE)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_STANDALONE)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%switch'
  fun option_switch(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_switch")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken("%switch")
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%table'
  fun option_table(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_table")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken("%table")
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // '%type' java_type
  fun option_type(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_type")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_TYPE)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_TYPE)
    pinned = result // pin = 1
    result = result && java_type(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%unicode' [number | version]
  fun option_unicode(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_unicode")) return false
    if (!runtime.nextTokenIs("<option>", JFlexTypes.FLEX_OPT_UNICODE)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_UNICODE)
    pinned = result // pin = 1
    result = result && option_unicode_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // [number | version]
  private fun option_unicode_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_unicode_1")) return false
    option_unicode_1_0(runtime, level + 1)
    return true
  }

  // number | version
  private fun option_unicode_1_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_unicode_1_0")) return false
    var result: Boolean
    result = runtime.consumeToken(JFlexTypes.FLEX_NUMBER)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_VERSION)
    return result
  }

  /* ********************************************************** */
  // <<line '%{'>> java_code <<line '%}'>>
  fun option_user_code(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_user_code")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = line(runtime, level + 1, opt_code1_parser_)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_code(runtime, level + 1))
    result = pinned && line(runtime, level + 1, opt_code2_parser_) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // '%yylexthrow' java_type_list | <<line '%yylexthrow{'>> java_type_list <<line '%yylexthrow}'>>
  fun option_yylexthrow(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_yylexthrow")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_OPTION, "<option>")
    result = option_yylexthrow_0(runtime, level + 1)
    if (!result) result = option_yylexthrow_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '%yylexthrow' java_type_list
  private fun option_yylexthrow_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_yylexthrow_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_YYLEXTHROW)
    pinned = result // pin = 1
    result = result && java_type_list(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // <<line '%yylexthrow{'>> java_type_list <<line '%yylexthrow}'>>
  private fun option_yylexthrow_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "option_yylexthrow_1")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = line(runtime, level + 1, opt_yylexthrow1_parser_)
    pinned = result // pin = 1
    result = result && runtime.report_error_(java_type_list(runtime, level + 1))
    result = pinned && line(runtime, level + 1, opt_yylexthrow2_parser_) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // state_list (rule_group | rule_tail ) | rule_tail
  fun rule(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_RULE, "<rule>")
    result = rule_0(runtime, level + 1)
    if (!result) result = rule_tail(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // state_list (rule_group | rule_tail )
  private fun rule_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = state_list(runtime, level + 1)
    pinned = result // pin = 1
    result = result && rule_0_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // rule_group | rule_tail
  private fun rule_0_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_0_1")) return false
    var result: Boolean
    result = rule_group(runtime, level + 1)
    if (!result) result = rule_tail(runtime, level + 1)
    return result
  }

  /* ********************************************************** */
  // !('{' id '}') '{' rule_group_item + '}'
  internal fun rule_group(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_group")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_BRACE1)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = rule_group_0(runtime, level + 1)
    result = result && runtime.consumeToken(JFlexTypes.FLEX_BRACE1)
    pinned = result // pin = 2
    result = result && runtime.report_error_(rule_group_2(runtime, level + 1))
    result = pinned && runtime.consumeToken(JFlexTypes.FLEX_BRACE2) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // !('{' id '}')
  private fun rule_group_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_group_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !rule_group_0_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '{' id '}'
  private fun rule_group_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_group_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeTokens(0, JFlexTypes.FLEX_BRACE1, JFlexTypes.FLEX_ID, JFlexTypes.FLEX_BRACE2)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // rule_group_item +
  private fun rule_group_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_group_2")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = rule_group_item(runtime, level + 1)
    while (result) {
      val pos: Int = runtime.current_position_()
      if (!rule_group_item(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("rule_group_2", pos)) break
    }
    runtime.exit_section_(marker, null, result)
    return result
  }

  /* ********************************************************** */
  // option_include | rule
  internal fun rule_group_item(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_group_item")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = option_include(runtime, level + 1)
    if (!result) result = rule(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, JFlexParser::rule_recover)
    return result
  }

  /* ********************************************************** */
  // !('}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group)
  internal fun rule_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !rule_recover_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group
  private fun rule_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_recover_0")) return false
    var result: Boolean
    result = runtime.consumeToken(JFlexTypes.FLEX_BRACE2)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_DOT)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_ANGLE1)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_EOF)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_HAT)
    if (!result) result = new_line(runtime, level + 1)
    if (!result) result = expression(runtime, level + 1, 4)
    return result
  }

  /* ********************************************************** */
  // rule_tail1 | rule_tail2
  internal fun rule_tail(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_tail")) return false
    var result: Boolean
    result = rule_tail1(runtime, level + 1)
    if (!result) result = rule_tail2(runtime, level + 1)
    return result
  }

  /* ********************************************************** */
  // '<<EOF>>' action
  internal fun rule_tail1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_tail1")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_EOF)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_EOF)
    pinned = result // pin = 1
    result = result && action(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // ['^'] expression look_ahead? action
  internal fun rule_tail2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_tail2")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = rule_tail2_0(runtime, level + 1)
    result = result && expression(runtime, level + 1, -1)
    pinned = result // pin = 2
    result = result && runtime.report_error_(rule_tail2_2(runtime, level + 1))
    result = pinned && action(runtime, level + 1) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // ['^']
  private fun rule_tail2_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_tail2_0")) return false
    runtime.consumeToken(JFlexTypes.FLEX_HAT)
    return true
  }

  // look_ahead?
  private fun rule_tail2_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "rule_tail2_2")) return false
    look_ahead(runtime, level + 1)
    return true
  }

  /* ********************************************************** */
  // [] new_line '%%' new_line
  internal fun section_div(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "section_div")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = section_div_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(new_line(runtime, level + 1))
    result = pinned && runtime.report_error_(runtime.consumeToken(JFlexTypes.FLEX_TWO_PERCS)) && result
    result = pinned && new_line(runtime, level + 1) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // []
  private fun section_div_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // !'%%'
  internal fun section_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "section_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !runtime.consumeToken(JFlexTypes.FLEX_TWO_PERCS)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // &('!' | '(' | '.' | '[' | '~'
  //   | char_or_esc | char_class | number | string
  //   | '{' id | !new_line id )
  internal fun sequence_op(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence_op")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._AND_, null, "<expression>")
    result = sequence_op_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '!' | '(' | '.' | '[' | '~'
  //   | char_or_esc | char_class | number | string
  //   | '{' id | !new_line id
  private fun sequence_op_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence_op_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(JFlexTypes.FLEX_BANG)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_PAREN1)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_DOT)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_BRACK1)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_TILDE)
    if (!result) result = char_or_esc(runtime, level + 1)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_CHAR_CLASS)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_NUMBER)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_STRING)
    if (!result) result = runtime.parseTokens(0, JFlexTypes.FLEX_BRACE1, JFlexTypes.FLEX_ID)
    if (!result) result = sequence_op_0_10(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !new_line id
  private fun sequence_op_0_10(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence_op_0_10")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = sequence_op_0_10_0(runtime, level + 1)
    result = result && runtime.consumeToken(JFlexTypes.FLEX_ID)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !new_line
  private fun sequence_op_0_10_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "sequence_op_0_10_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !new_line(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // ('%state' | '%xstate') state_definition ((','? !(id '=')) state_definition) * ','?
  fun state_declaration(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration")) return false
    if (!runtime.nextTokenIs("<state declaration>", JFlexTypes.FLEX_OPT_STATE, JFlexTypes.FLEX_OPT_XSTATE)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_STATE_DECLARATION, "<state declaration>")
    result = state_declaration_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(state_definition(runtime, level + 1))
    result = pinned && runtime.report_error_(state_declaration_2(runtime, level + 1)) && result
    result = pinned && state_declaration_3(runtime, level + 1) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // '%state' | '%xstate'
  private fun state_declaration_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_0")) return false
    var result: Boolean
    result = runtime.consumeToken(JFlexTypes.FLEX_OPT_STATE)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_OPT_XSTATE)
    return result
  }

  // ((','? !(id '=')) state_definition) *
  private fun state_declaration_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_2")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!state_declaration_2_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("state_declaration_2", pos)) break
    }
    return true
  }

  // (','? !(id '=')) state_definition
  private fun state_declaration_2_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_2_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = state_declaration_2_0_0(runtime, level + 1)
    result = result && state_definition(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // ','? !(id '=')
  private fun state_declaration_2_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_2_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = state_declaration_2_0_0_0(runtime, level + 1)
    result = result && state_declaration_2_0_0_1(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // ','?
  private fun state_declaration_2_0_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_2_0_0_0")) return false
    runtime.consumeToken(JFlexTypes.FLEX_COMMA)
    return true
  }

  // !(id '=')
  private fun state_declaration_2_0_0_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_2_0_0_1")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !state_declaration_2_0_0_1_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // id '='
  private fun state_declaration_2_0_0_1_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_2_0_0_1_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeTokens(0, JFlexTypes.FLEX_ID, JFlexTypes.FLEX_EQ)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // ','?
  private fun state_declaration_3(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_declaration_3")) return false
    runtime.consumeToken(JFlexTypes.FLEX_COMMA)
    return true
  }

  /* ********************************************************** */
  // id
  fun state_definition(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_definition")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_ID)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(JFlexTypes.FLEX_ID)
    runtime.exit_section_(marker, JFlexTypes.FLEX_STATE_DEFINITION, result)
    return result
  }

  /* ********************************************************** */
  // '<' state_reference (',' state_reference) * '>'
  fun state_list(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_list")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_ANGLE1)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_STATE_LIST, null)
    result = runtime.consumeToken(JFlexTypes.FLEX_ANGLE1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(state_reference(runtime, level + 1))
    result = pinned && runtime.report_error_(state_list_2(runtime, level + 1)) && result
    result = pinned && runtime.consumeToken(JFlexTypes.FLEX_ANGLE2) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // (',' state_reference) *
  private fun state_list_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_list_2")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!state_list_2_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("state_list_2", pos)) break
    }
    return true
  }

  // ',' state_reference
  private fun state_list_2_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_list_2_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = runtime.consumeToken(JFlexTypes.FLEX_COMMA)
    pinned = result // pin = 1
    result = result && state_reference(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  /* ********************************************************** */
  // id
  fun state_reference(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "state_reference")) return false
    if (!runtime.nextTokenIs(JFlexTypes.FLEX_ID)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeToken(JFlexTypes.FLEX_ID)
    runtime.exit_section_(marker, JFlexTypes.FLEX_STATE_REFERENCE, result)
    return result
  }

  /* ********************************************************** */
  // !(',' | '>') declaration_recover
  internal fun type_recover(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "type_recover")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = type_recover_0(runtime, level + 1)
    result = result && declaration_recover(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // !(',' | '>')
  private fun type_recover_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "type_recover_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !type_recover_0_0(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // ',' | '>'
  private fun type_recover_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "type_recover_0_0")) return false
    var result: Boolean
    result = runtime.consumeToken(JFlexTypes.FLEX_COMMA)
    if (!result) result = runtime.consumeToken(JFlexTypes.FLEX_ANGLE2)
    return result
  }

  /* ********************************************************** */
  // [] java_code
  fun user_code_section(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "user_code_section")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_USER_CODE_SECTION, "<user code section>")
    result = user_code_section_0(runtime, level + 1)
    pinned = result // pin = 1
    result = result && java_code(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, JFlexParser::section_recover)
    return result || pinned
  }

  // []
  private fun user_code_section_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // <<anything2 !new_line>>
  fun user_value(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "user_value")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_USER_VALUE, "<user value>")
    result = JFlexParserUtil.anything2(runtime, level + 1, JFlexParser::user_value_0_0)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // !new_line
  private fun user_value_0_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "user_value_0_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NOT_)
    result = !new_line(runtime, level + 1)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: N_ARY(choice_expression)
  // 1: N_ARY(sequence_expression)
  // 2: ATOM(paren_expression)
  // 3: PREFIX(not_expression)
  // 4: POSTFIX(quantifier_expression)
  // 5: ATOM(class_expression) ATOM(predefined_class_expression) ATOM(macro_ref_expression) ATOM(literal_expression)
  fun expression(runtime: SyntaxGeneratedParserRuntime, level: Int, priority: Int): Boolean {
    if (!runtime.recursion_guard_(level, "expression")) return false
    runtime.addVariant("<expression>")
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, "<expression>")
    result = paren_expression(runtime, level + 1)
    if (!result) result = not_expression(runtime, level + 1)
    if (!result) result = class_expression(runtime, level + 1)
    if (!result) result = predefined_class_expression(runtime, level + 1)
    if (!result) result = macro_ref_expression(runtime, level + 1)
    if (!result) result = literal_expression(runtime, level + 1)
    pinned = result
    result = result && expression_0(runtime, level + 1, priority)
    runtime.exit_section_(level, marker, null, result, pinned, null)
    return result || pinned
  }

  fun expression_0(runtime: SyntaxGeneratedParserRuntime, level: Int, priority: Int): Boolean {
    if (!runtime.recursion_guard_(level, "expression_0")) return false
    var result = true
    while (true) {
      val marker: Marker = runtime.enter_section_(level, Modifiers._LEFT_, null)
      if (priority < 0 && runtime.consumeTokenSmart(JFlexTypes.FLEX_BAR)) {
        while (true) {
          result = runtime.report_error_(expression(runtime, level, 0))
          if (!runtime.consumeTokenSmart(JFlexTypes.FLEX_BAR)) break
        }
        runtime.exit_section_(level, marker, JFlexTypes.FLEX_CHOICE_EXPRESSION, result, true, null)
      }
      else if (priority < 1 && sequence_op(runtime, level + 1)) {
        val pos: Int = runtime.current_position_()
        while (true) {
          result = runtime.report_error_(expression(runtime, level, 1))
          if (!sequence_op(runtime, level + 1)) break
          if (!runtime.empty_element_parsed_guard_("sequence_expression", pos)) break
          pos = runtime.current_position_()
        }
        runtime.exit_section_(level, marker, JFlexTypes.FLEX_SEQUENCE_EXPRESSION, result, true, null)
      }
      else if (priority < 4 && quantifier_expression_0(runtime, level + 1)) {
        result = true
        runtime.exit_section_(level, marker, JFlexTypes.FLEX_QUANTIFIER_EXPRESSION, result, true, null)
      }
      else {
        runtime.exit_section_(level, marker, null, false, false, null)
        break
      }
    }
    return result
  }

  // '(' expression ')'
  fun paren_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "paren_expression")) return false
    if (!runtime.nextTokenIsSmart(JFlexTypes.FLEX_PAREN1)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_PAREN_EXPRESSION, "<expression>")
    result = runtime.consumeTokenSmart(JFlexTypes.FLEX_PAREN1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(expression(runtime, level + 1, -1))
    result = pinned && runtime.consumeToken(JFlexTypes.FLEX_PAREN2) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  fun not_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "not_expression")) return false
    if (!runtime.nextTokenIsSmart(JFlexTypes.FLEX_BANG, JFlexTypes.FLEX_TILDE)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, null)
    result = not_expression_0(runtime, level + 1)
    pinned = result
    result = pinned && expression(runtime, level, 3)
    runtime.exit_section_(level, marker, JFlexTypes.FLEX_NOT_EXPRESSION, result, pinned, null)
    return result || pinned
  }

  // '!'|'~'
  private fun not_expression_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "not_expression_0")) return false
    var result: Boolean
    result = runtime.consumeTokenSmart(JFlexTypes.FLEX_BANG)
    if (!result) result = runtime.consumeTokenSmart(JFlexTypes.FLEX_TILDE)
    return result
  }

  // '*' | '+' | '?' | '{' number [ ',' number] '}'
  private fun quantifier_expression_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "quantifier_expression_0")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeTokenSmart(JFlexTypes.FLEX_STAR)
    if (!result) result = runtime.consumeTokenSmart(JFlexTypes.FLEX_PLUS)
    if (!result) result = runtime.consumeTokenSmart(JFlexTypes.FLEX_QUESTION)
    if (!result) result = quantifier_expression_0_3(runtime, level + 1)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // '{' number [ ',' number] '}'
  private fun quantifier_expression_0_3(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "quantifier_expression_0_3")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_()
    result = runtime.consumeTokensSmart(0, JFlexTypes.FLEX_BRACE1, JFlexTypes.FLEX_NUMBER)
    result = result && quantifier_expression_0_3_2(runtime, level + 1)
    result = result && runtime.consumeToken(JFlexTypes.FLEX_BRACE2)
    runtime.exit_section_(marker, null, result)
    return result
  }

  // [ ',' number]
  private fun quantifier_expression_0_3_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "quantifier_expression_0_3_2")) return false
    runtime.parseTokensSmart(0, JFlexTypes.FLEX_COMMA, JFlexTypes.FLEX_NUMBER)
    return true
  }

  // '[' '^'? (char_class_item (char_class_op char_class_item)* )* ']'
  fun class_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "class_expression")) return false
    if (!runtime.nextTokenIsSmart(JFlexTypes.FLEX_BRACK1)) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_CLASS_EXPRESSION, "<expression>")
    result = runtime.consumeTokenSmart(JFlexTypes.FLEX_BRACK1)
    pinned = result // pin = 1
    result = result && runtime.report_error_(class_expression_1(runtime, level + 1))
    result = pinned && runtime.report_error_(class_expression_2(runtime, level + 1)) && result
    result = pinned && runtime.consumeToken(JFlexTypes.FLEX_BRACK2) && result
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // '^'?
  private fun class_expression_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "class_expression_1")) return false
    runtime.consumeTokenSmart(JFlexTypes.FLEX_HAT)
    return true
  }

  // (char_class_item (char_class_op char_class_item)* )*
  private fun class_expression_2(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "class_expression_2")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!class_expression_2_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("class_expression_2", pos)) break
    }
    return true
  }

  // char_class_item (char_class_op char_class_item)*
  private fun class_expression_2_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "class_expression_2_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = char_class_item(runtime, level + 1)
    pinned = result // pin = 1
    result = result && class_expression_2_0_1(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // (char_class_op char_class_item)*
  private fun class_expression_2_0_1(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "class_expression_2_0_1")) return false
    while (true) {
      val pos: Int = runtime.current_position_()
      if (!class_expression_2_0_1_0(runtime, level + 1)) break
      if (!runtime.empty_element_parsed_guard_("class_expression_2_0_1", pos)) break
    }
    return true
  }

  // char_class_op char_class_item
  private fun class_expression_2_0_1_0(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "class_expression_2_0_1_0")) return false
    var result: Boolean
    var pinned: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_)
    result = char_class_op(runtime, level + 1)
    pinned = result // pin = 1
    result = result && char_class_item(runtime, level + 1)
    runtime.exit_section_(level, marker, result, pinned, null)
    return result || pinned
  }

  // char_class | '.'
  fun predefined_class_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "predefined_class_expression")) return false
    if (!runtime.nextTokenIsSmart(JFlexTypes.FLEX_CHAR_CLASS, JFlexTypes.FLEX_DOT)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_PREDEFINED_CLASS_EXPRESSION, "<expression>")
    result = runtime.consumeTokenSmart(JFlexTypes.FLEX_CHAR_CLASS)
    if (!result) result = runtime.consumeTokenSmart(JFlexTypes.FLEX_DOT)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // '{' macro_reference '}'
  fun macro_ref_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "macro_ref_expression")) return false
    if (!runtime.nextTokenIsSmart(JFlexTypes.FLEX_BRACE1)) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_MACRO_REF_EXPRESSION, "<expression>")
    result = runtime.consumeTokenSmart(JFlexTypes.FLEX_BRACE1)
    result = result && macro_reference(runtime, level + 1)
    result = result && runtime.consumeToken(JFlexTypes.FLEX_BRACE2)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  // string | id | char_or_esc | number
  fun literal_expression(runtime: SyntaxGeneratedParserRuntime, level: Int): Boolean {
    if (!runtime.recursion_guard_(level, "literal_expression")) return false
    var result: Boolean
    val marker: Marker = runtime.enter_section_(level, Modifiers._NONE_, JFlexTypes.FLEX_LITERAL_EXPRESSION, "<expression>")
    result = runtime.consumeTokenSmart(JFlexTypes.FLEX_STRING)
    if (!result) result = runtime.consumeTokenSmart(JFlexTypes.FLEX_ID)
    if (!result) result = char_or_esc(runtime, level + 1)
    if (!result) result = runtime.consumeTokenSmart(JFlexTypes.FLEX_NUMBER)
    runtime.exit_section_(level, marker, result, false, null)
    return result
  }

  internal val opt_code1_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_CODE1) }
  internal val opt_code2_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_CODE2) }
  internal val opt_eof1_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_EOF1) }
  internal val opt_eof2_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_EOF2) }
  internal val opt_eofthrow1_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_EOFTHROW1) }
  internal val opt_eofthrow2_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_EOFTHROW2) }
  internal val opt_eofval1_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_EOFVAL1) }
  internal val opt_eofval2_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_EOFVAL2) }
  internal val opt_init1_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_INIT1) }
  internal val opt_init2_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_INIT2) }
  internal val opt_initthrow1_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_INITTHROW1) }
  internal val opt_initthrow2_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_INITTHROW2) }
  internal val opt_yylexthrow1_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_YYLEXTHROW1) }
  internal val opt_yylexthrow2_parser_: Parser = { runtime, level -> runtime.consumeToken(JFlexTypes.FLEX_OPT_YYLEXTHROW2) }
}