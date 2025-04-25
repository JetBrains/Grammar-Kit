// ---- org/intellij/grammar/parser/GrammarParser.java -----------------
// license.txt
package org.intellij.grammar.parser;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static org.intellij.grammar.BnfSyntaxTypes.*;
import static org.intellij.grammar.parser.GrammarParserUtil.*;
import static com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntimeKt.*;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NONE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._COLLAPSE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_INNER_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._AND_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NOT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._UPPER_;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.parser.ProductionResult;
import com.intellij.platform.syntax.SyntaxElementTypeSet;
import static com.intellij.platform.syntax.parser.ProductionResultKt.prepareProduction;
import kotlin.jvm.functions.Function2;
import kotlin.Unit;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GrammarParser {

  public ProductionResult parse(SyntaxElementType type, SyntaxGeneratedParserRuntime runtime) {
    parseLight(type, runtime);
    return prepareProduction(runtime.getSyntaxBuilder());
  }

  public void parseLight(SyntaxElementType type, SyntaxGeneratedParserRuntime runtime) {
    boolean result;
    Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit> parse = new Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit>(){
      @Override
      public Unit invoke(SyntaxElementType type, SyntaxGeneratedParserRuntime runtime) {
        parseLight(type, runtime);
        return Unit.INSTANCE;
      }
    };

    runtime.init(parse, EXTENDS_SETS_);
    Marker marker = enter_section_(runtime, 0, _COLLAPSE_, null);
    result = parse_root_(type, runtime);
    exit_section_(runtime, 0, marker, type, result, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType type, SyntaxGeneratedParserRuntime runtime) {
    return parse_root_(type, runtime, 0);
  }

  static boolean parse_root_(SyntaxElementType type, SyntaxGeneratedParserRuntime runtime, int level) {
    return parseGrammar(runtime, level + 1, GrammarParser::grammar_element);
  }

  public static final SyntaxElementTypeSet[] EXTENDS_SETS_ = new SyntaxElementTypeSet[] {
    create_token_set_(BNF_CHOICE, BNF_EXPRESSION, BNF_EXTERNAL_EXPRESSION, BNF_LITERAL_EXPRESSION,
      BNF_PAREN_EXPRESSION, BNF_PAREN_OPT_EXPRESSION, BNF_PREDICATE, BNF_QUANTIFIED,
      BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE, BNF_STRING_LITERAL_EXPRESSION, BNF_VALUE_LIST),
  };

  /* ********************************************************** */
  // !attr_start_simple expression
  static boolean alt_choice_element(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "alt_choice_element")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = alt_choice_element_0(runtime, level + 1);
    result = result && expression(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !attr_start_simple
  private static boolean alt_choice_element_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "alt_choice_element_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !attr_start_simple(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // attr_start attr_value ';'?
  public static boolean attr(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_ATTR, "<attr>");
    result = attr_start(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, attr_value(runtime, level + 1));
    result = pinned && attr_2(runtime, level + 1) && result;
    exit_section_(runtime, level, marker, result, pinned, GrammarParser::attr_recover);
    return result || pinned;
  }

  // ';'?
  private static boolean attr_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_2")) return false;
    consumeToken(runtime, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // '(' string_literal_expression ')'
  public static boolean attr_pattern(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_pattern")) return false;
    if (!nextTokenIs(runtime, BNF_LEFT_PAREN)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_ATTR_PATTERN, null);
    result = consumeToken(runtime, BNF_LEFT_PAREN);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, string_literal_expression(runtime, level + 1));
    result = pinned && consumeToken(runtime, BNF_RIGHT_PAREN) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // !('}' | attr_start)
  static boolean attr_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !attr_recover_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '}' | attr_start
  private static boolean attr_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_recover_0")) return false;
    boolean result;
    result = consumeToken(runtime, BNF_RIGHT_BRACE);
    if (!result) result = attr_start(runtime, level + 1);
    return result;
  }

  /* ********************************************************** */
  // id (attr_pattern '=' | '=')
  static boolean attr_start(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_start")) return false;
    if (!nextTokenIs(runtime, BNF_ID)) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, BNF_ID);
    result = result && attr_start_1(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // attr_pattern '=' | '='
  private static boolean attr_start_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_start_1")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = attr_start_1_0(runtime, level + 1);
    if (!result) result = consumeToken(runtime, BNF_OP_EQ);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // attr_pattern '='
  private static boolean attr_start_1_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_start_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = attr_pattern(runtime, level + 1);
    pinned = result; // pin = attr_pattern
    result = result && consumeToken(runtime, BNF_OP_EQ);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // id attr_pattern? '='
  static boolean attr_start_simple(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_start_simple")) return false;
    if (!nextTokenIs(runtime, BNF_ID)) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, BNF_ID);
    result = result && attr_start_simple_1(runtime, level + 1);
    result = result && consumeToken(runtime, BNF_OP_EQ);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // attr_pattern?
  private static boolean attr_start_simple_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_start_simple_1")) return false;
    attr_pattern(runtime, level + 1);
    return true;
  }

  /* ********************************************************** */
  // attr_value_inner !'='
  static boolean attr_value(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_value")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = attr_value_inner(runtime, level + 1);
    result = result && attr_value_1(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !'='
  private static boolean attr_value_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_value_1")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !consumeToken(runtime, BNF_OP_EQ);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // reference_or_token
  //   | literal_expression
  //   | value_list
  static boolean attr_value_inner(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attr_value_inner")) return false;
    boolean result;
    result = reference_or_token(runtime, level + 1);
    if (!result) result = literal_expression(runtime, level + 1);
    if (!result) result = value_list(runtime, level + 1);
    return result;
  }

  /* ********************************************************** */
  // '{' attr * '}'
  public static boolean attrs(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attrs")) return false;
    if (!nextTokenIs(runtime, BNF_LEFT_BRACE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_ATTRS, null);
    result = consumeToken(runtime, BNF_LEFT_BRACE);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, attrs_1(runtime, level + 1));
    result = pinned && consumeToken(runtime, BNF_RIGHT_BRACE) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // attr *
  private static boolean attrs_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "attrs_1")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!attr(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "attrs_1", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ( '|' sequence ) +
  public static boolean choice(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "choice")) return false;
    if (!nextTokenIs(runtime, BNF_OP_OR)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _LEFT_, BNF_CHOICE, null);
    result = choice_0(runtime, level + 1);
    while (result) {
      int pos = current_position_(runtime);
      if (!choice_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "choice", pos)) break;
    }
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '|' sequence
  private static boolean choice_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "choice_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, BNF_OP_OR);
    pinned = result; // pin = 1
    result = result && sequence(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // sequence choice?
  public static boolean expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "expression")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _COLLAPSE_, BNF_EXPRESSION, "<expression>");
    result = sequence(runtime, level + 1);
    result = result && expression_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // choice?
  private static boolean expression_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "expression_1")) return false;
    choice(runtime, level + 1);
    return true;
  }

  /* ********************************************************** */
  // '<<' reference_or_token option * '>>'
  public static boolean external_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "external_expression")) return false;
    if (!nextTokenIs(runtime, BNF_EXTERNAL_START)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_EXTERNAL_EXPRESSION, null);
    result = consumeToken(runtime, BNF_EXTERNAL_START);
    result = result && reference_or_token(runtime, level + 1);
    pinned = result; // pin = 2
    result = result && report_error_(runtime, external_expression_2(runtime, level + 1));
    result = pinned && consumeToken(runtime, BNF_EXTERNAL_END) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // option *
  private static boolean external_expression_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "external_expression_2")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!option(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "external_expression_2", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> (attrs | rule)
  static boolean grammar_element(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "grammar_element")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = grammar_element_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && grammar_element_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, GrammarParser::grammar_element_recover);
    return result || pinned;
  }

  // !<<eof>>
  private static boolean grammar_element_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "grammar_element_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !eof(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // attrs | rule
  private static boolean grammar_element_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "grammar_element_1")) return false;
    boolean result;
    result = attrs(runtime, level + 1);
    if (!result) result = rule(runtime, level + 1);
    return result;
  }

  /* ********************************************************** */
  // !('{'|rule_start)
  static boolean grammar_element_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "grammar_element_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !grammar_element_recover_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '{'|rule_start
  private static boolean grammar_element_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "grammar_element_recover_0")) return false;
    boolean result;
    result = consumeToken(runtime, BNF_LEFT_BRACE);
    if (!result) result = rule_start(runtime, level + 1);
    return result;
  }

  /* ********************************************************** */
  // (id list_entry_tail? | string_literal_expression) ';'?
  public static boolean list_entry(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_LIST_ENTRY, "<list entry>");
    result = list_entry_0(runtime, level + 1);
    result = result && list_entry_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, GrammarParser::list_entry_recover);
    return result;
  }

  // id list_entry_tail? | string_literal_expression
  private static boolean list_entry_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = list_entry_0_0(runtime, level + 1);
    if (!result) result = string_literal_expression(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // id list_entry_tail?
  private static boolean list_entry_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, BNF_ID);
    result = result && list_entry_0_0_1(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // list_entry_tail?
  private static boolean list_entry_0_0_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry_0_0_1")) return false;
    list_entry_tail(runtime, level + 1);
    return true;
  }

  // ';'?
  private static boolean list_entry_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry_1")) return false;
    consumeToken(runtime, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // !(']' | '}' | id | string)
  static boolean list_entry_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !list_entry_recover_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // ']' | '}' | id | string
  private static boolean list_entry_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry_recover_0")) return false;
    boolean result;
    result = consumeToken(runtime, BNF_RIGHT_BRACKET);
    if (!result) result = consumeToken(runtime, BNF_RIGHT_BRACE);
    if (!result) result = consumeToken(runtime, BNF_ID);
    if (!result) result = consumeToken(runtime, BNF_STRING);
    return result;
  }

  /* ********************************************************** */
  // '=' string_literal_expression
  static boolean list_entry_tail(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "list_entry_tail")) return false;
    if (!nextTokenIs(runtime, BNF_OP_EQ)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, BNF_OP_EQ);
    pinned = result; // pin = 1
    result = result && string_literal_expression(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // string_literal_expression | number
  public static boolean literal_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "literal_expression")) return false;
    if (!nextTokenIs(runtime, "<literal expression>", BNF_NUMBER, BNF_STRING)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _COLLAPSE_, BNF_LITERAL_EXPRESSION, "<literal expression>");
    result = string_literal_expression(runtime, level + 1);
    if (!result) result = consumeToken(runtime, BNF_NUMBER);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // 'private' | 'external' | 'meta'
  //   | 'inner' | 'left' | 'upper' | 'fake'
  public static boolean modifier(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "modifier")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_MODIFIER, "<modifier>");
    result = consumeToken(runtime, "private");
    if (!result) result = consumeToken(runtime, "external");
    if (!result) result = consumeToken(runtime, "meta");
    if (!result) result = consumeToken(runtime, "inner");
    if (!result) result = consumeToken(runtime, "left");
    if (!result) result = consumeToken(runtime, "upper");
    if (!result) result = consumeToken(runtime, "fake");
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // predicate | paren_opt_expression | simple quantified?
  static boolean option(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = predicate(runtime, level + 1);
    if (!result) result = paren_opt_expression(runtime, level + 1);
    if (!result) result = option_2(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // simple quantified?
  private static boolean option_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_2")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = simple(runtime, level + 1);
    result = result && option_2_1(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // quantified?
  private static boolean option_2_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_2_1")) return false;
    quantified(runtime, level + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' expression ')' | '{' alt_choice_element '}'
  public static boolean paren_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "paren_expression")) return false;
    if (!nextTokenIs(runtime, "<paren expression>", BNF_LEFT_BRACE, BNF_LEFT_PAREN)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_PAREN_EXPRESSION, "<paren expression>");
    result = paren_expression_0(runtime, level + 1);
    if (!result) result = paren_expression_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '(' expression ')'
  private static boolean paren_expression_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "paren_expression_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, BNF_LEFT_PAREN);
    result = result && expression(runtime, level + 1);
    pinned = result; // pin = 2
    result = result && consumeToken(runtime, BNF_RIGHT_PAREN);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // '{' alt_choice_element '}'
  private static boolean paren_expression_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "paren_expression_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, BNF_LEFT_BRACE);
    result = result && alt_choice_element(runtime, level + 1);
    pinned = result; // pin = 2
    result = result && consumeToken(runtime, BNF_RIGHT_BRACE);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '[' expression ']'
  public static boolean paren_opt_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "paren_opt_expression")) return false;
    if (!nextTokenIs(runtime, BNF_LEFT_BRACKET)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_PAREN_OPT_EXPRESSION, null);
    result = consumeToken(runtime, BNF_LEFT_BRACKET);
    result = result && expression(runtime, level + 1);
    pinned = result; // pin = 2
    result = result && consumeToken(runtime, BNF_RIGHT_BRACKET);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // predicate_sign simple
  public static boolean predicate(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "predicate")) return false;
    if (!nextTokenIs(runtime, "<predicate>", BNF_OP_AND, BNF_OP_NOT)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_PREDICATE, "<predicate>");
    result = predicate_sign(runtime, level + 1);
    result = result && simple(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '&' | '!'
  public static boolean predicate_sign(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "predicate_sign")) return false;
    if (!nextTokenIs(runtime, "<predicate sign>", BNF_OP_AND, BNF_OP_NOT)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_PREDICATE_SIGN, "<predicate sign>");
    result = consumeToken(runtime, BNF_OP_AND);
    if (!result) result = consumeToken(runtime, BNF_OP_NOT);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // quantifier
  public static boolean quantified(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "quantified")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _LEFT_, BNF_QUANTIFIED, "<quantified>");
    result = quantifier(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '?' | '+' | '*'
  public static boolean quantifier(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "quantifier")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_QUANTIFIER, "<quantifier>");
    result = consumeToken(runtime, BNF_OP_OPT);
    if (!result) result = consumeToken(runtime, BNF_OP_ONEMORE);
    if (!result) result = consumeToken(runtime, BNF_OP_ZEROMORE);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // id
  public static boolean reference_or_token(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "reference_or_token")) return false;
    if (!nextTokenIs(runtime, BNF_ID)) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, BNF_ID);
    exit_section_(runtime, marker, BNF_REFERENCE_OR_TOKEN, result);
    return result;
  }

  /* ********************************************************** */
  // rule_start expression attrs? ';'?
  public static boolean rule(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_RULE, "<rule>");
    result = rule_start(runtime, level + 1);
    result = result && expression(runtime, level + 1);
    pinned = result; // pin = 2
    result = result && report_error_(runtime, rule_2(runtime, level + 1));
    result = pinned && rule_3(runtime, level + 1) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // attrs?
  private static boolean rule_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_2")) return false;
    attrs(runtime, level + 1);
    return true;
  }

  // ';'?
  private static boolean rule_3(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_3")) return false;
    consumeToken(runtime, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // modifier* id '::='
  static boolean rule_start(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_start")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = rule_start_0(runtime, level + 1);
    result = result && consumeTokens(runtime, 0, BNF_ID, BNF_OP_IS);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // modifier*
  private static boolean rule_start_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_start_0")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!modifier(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "rule_start_0", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // option *
  public static boolean sequence(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence")) return false;
    Marker marker = enter_section_(runtime, level, _COLLAPSE_, BNF_SEQUENCE, "<sequence>");
    while (true) {
      int pos = current_position_(runtime);
      if (!option(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "sequence", pos)) break;
    }
    exit_section_(runtime, level, marker, true, false, GrammarParser::sequence_recover);
    return true;
  }

  /* ********************************************************** */
  // !(';'|'|'|'('|')'|'['|']'|'{'|'}') grammar_element_recover
  static boolean sequence_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = sequence_recover_0(runtime, level + 1);
    result = result && grammar_element_recover(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !(';'|'|'|'('|')'|'['|']'|'{'|'}')
  private static boolean sequence_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !sequence_recover_0_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // ';'|'|'|'('|')'|'['|']'|'{'|'}'
  private static boolean sequence_recover_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence_recover_0_0")) return false;
    boolean result;
    result = consumeToken(runtime, BNF_SEMICOLON);
    if (!result) result = consumeToken(runtime, BNF_OP_OR);
    if (!result) result = consumeToken(runtime, BNF_LEFT_PAREN);
    if (!result) result = consumeToken(runtime, BNF_RIGHT_PAREN);
    if (!result) result = consumeToken(runtime, BNF_LEFT_BRACKET);
    if (!result) result = consumeToken(runtime, BNF_RIGHT_BRACKET);
    if (!result) result = consumeToken(runtime, BNF_LEFT_BRACE);
    if (!result) result = consumeToken(runtime, BNF_RIGHT_BRACE);
    return result;
  }

  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token
  //   | literal_expression
  //   | external_expression
  //   | paren_expression
  static boolean simple(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "simple")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = simple_0(runtime, level + 1);
    if (!result) result = literal_expression(runtime, level + 1);
    if (!result) result = external_expression(runtime, level + 1);
    if (!result) result = paren_expression(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !(modifier* id '::=' ) reference_or_token
  private static boolean simple_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "simple_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = simple_0_0(runtime, level + 1);
    result = result && reference_or_token(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !(modifier* id '::=' )
  private static boolean simple_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "simple_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !simple_0_0_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // modifier* id '::='
  private static boolean simple_0_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "simple_0_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = simple_0_0_0_0(runtime, level + 1);
    result = result && consumeTokens(runtime, 0, BNF_ID, BNF_OP_IS);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // modifier*
  private static boolean simple_0_0_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "simple_0_0_0_0")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!modifier(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "simple_0_0_0_0", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // string
  public static boolean string_literal_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "string_literal_expression")) return false;
    if (!nextTokenIs(runtime, BNF_STRING)) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, BNF_STRING);
    exit_section_(runtime, marker, BNF_STRING_LITERAL_EXPRESSION, result);
    return result;
  }

  /* ********************************************************** */
  // '[' list_entry * ']'
  public static boolean value_list(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "value_list")) return false;
    if (!nextTokenIs(runtime, BNF_LEFT_BRACKET)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, BNF_VALUE_LIST, null);
    result = consumeToken(runtime, BNF_LEFT_BRACKET);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, value_list_1(runtime, level + 1));
    result = pinned && consumeToken(runtime, BNF_RIGHT_BRACKET) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // list_entry *
  private static boolean value_list_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "value_list_1")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!list_entry(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "value_list_1", pos)) break;
    }
    return true;
  }

}