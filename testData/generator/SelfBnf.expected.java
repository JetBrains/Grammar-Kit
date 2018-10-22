// ---- GrammarParser.java -----------------
// license.txt
package org.intellij.grammar.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.intellij.grammar.psi.BnfTypes.*;
import static org.intellij.grammar.parser.GrammarParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GrammarParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType type, PsiBuilder builder) {
    parseLight(type, builder);
    return builder.getTreeBuilt();
  }

  public void parseLight(IElementType type, PsiBuilder builder) {
    boolean result;
    builder = adapt_builder_(type, builder, this, EXTENDS_SETS_);
    Marker marker = enter_section_(builder, 0, _COLLAPSE_, null);
    if (type == BNF_ATTR) {
      result = attr(builder, 0);
    }
    else if (type == BNF_ATTR_PATTERN) {
      result = attr_pattern(builder, 0);
    }
    else if (type == BNF_ATTRS) {
      result = attrs(builder, 0);
    }
    else if (type == BNF_CHOICE) {
      result = choice(builder, 0);
    }
    else if (type == BNF_EXPRESSION) {
      result = expression(builder, 0);
    }
    else if (type == BNF_EXTERNAL_EXPRESSION) {
      result = external_expression(builder, 0);
    }
    else if (type == BNF_LIST_ENTRY) {
      result = list_entry(builder, 0);
    }
    else if (type == BNF_LITERAL_EXPRESSION) {
      result = literal_expression(builder, 0);
    }
    else if (type == BNF_MODIFIER) {
      result = modifier(builder, 0);
    }
    else if (type == BNF_PAREN_EXPRESSION) {
      result = paren_expression(builder, 0);
    }
    else if (type == BNF_PAREN_OPT_EXPRESSION) {
      result = paren_opt_expression(builder, 0);
    }
    else if (type == BNF_PREDICATE) {
      result = predicate(builder, 0);
    }
    else if (type == BNF_PREDICATE_SIGN) {
      result = predicate_sign(builder, 0);
    }
    else if (type == BNF_QUANTIFIED) {
      result = quantified(builder, 0);
    }
    else if (type == BNF_QUANTIFIER) {
      result = quantifier(builder, 0);
    }
    else if (type == BNF_REFERENCE_OR_TOKEN) {
      result = reference_or_token(builder, 0);
    }
    else if (type == BNF_RULE) {
      result = rule(builder, 0);
    }
    else if (type == BNF_SEQUENCE) {
      result = sequence(builder, 0);
    }
    else if (type == BNF_STRING_LITERAL_EXPRESSION) {
      result = string_literal_expression(builder, 0);
    }
    else if (type == BNF_VALUE_LIST) {
      result = value_list(builder, 0);
    }
    else {
      result = parse_root_(type, builder, 0);
    }
    exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType type, PsiBuilder builder, int level) {
    return parseGrammar(builder, level + 1, grammar_element_parser_);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(BNF_CHOICE, BNF_EXPRESSION, BNF_EXTERNAL_EXPRESSION, BNF_LITERAL_EXPRESSION,
      BNF_PAREN_EXPRESSION, BNF_PAREN_OPT_EXPRESSION, BNF_PREDICATE, BNF_QUANTIFIED,
      BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE, BNF_STRING_LITERAL_EXPRESSION, BNF_VALUE_LIST),
  };

  /* ********************************************************** */
  // !attr_start_simple expression
  static boolean alt_choice_element(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "alt_choice_element")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = alt_choice_element_0(builder, level + 1);
    result = result && expression(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !attr_start_simple
  private static boolean alt_choice_element_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "alt_choice_element_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !attr_start_simple(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // attr_start attr_value ';'?
  public static boolean attr(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_ATTR, "<attr>");
    result = attr_start(builder, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, attr_value(builder, level + 1));
    result = pinned && attr_2(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, attr_recover_parser_);
    return result || pinned;
  }

  // ';'?
  private static boolean attr_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_2")) return false;
    consumeToken(builder, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // '(' string_literal_expression ')'
  public static boolean attr_pattern(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_pattern")) return false;
    if (!nextTokenIs(builder, BNF_LEFT_PAREN)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_ATTR_PATTERN, null);
    result = consumeToken(builder, BNF_LEFT_PAREN);
    pinned = result; // pin = 1
    result = result && report_error_(builder, string_literal_expression(builder, level + 1));
    result = pinned && consumeToken(builder, BNF_RIGHT_PAREN) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // !('}' | attr_start)
  static boolean attr_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !attr_recover_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '}' | attr_start
  private static boolean attr_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_RIGHT_BRACE);
    if (!result) result = attr_start(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // id (attr_pattern '=' | '=')
  static boolean attr_start(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_start")) return false;
    if (!nextTokenIs(builder, BNF_ID)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_ID);
    result = result && attr_start_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // attr_pattern '=' | '='
  private static boolean attr_start_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_start_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = attr_start_1_0(builder, level + 1);
    if (!result) result = consumeToken(builder, BNF_OP_EQ);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // attr_pattern '='
  private static boolean attr_start_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_start_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = attr_pattern(builder, level + 1);
    pinned = result; // pin = attr_pattern
    result = result && consumeToken(builder, BNF_OP_EQ);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // id attr_pattern? '='
  static boolean attr_start_simple(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_start_simple")) return false;
    if (!nextTokenIs(builder, BNF_ID)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_ID);
    result = result && attr_start_simple_1(builder, level + 1);
    result = result && consumeToken(builder, BNF_OP_EQ);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // attr_pattern?
  private static boolean attr_start_simple_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_start_simple_1")) return false;
    attr_pattern(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // attr_value_inner !'='
  static boolean attr_value(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_value")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = attr_value_inner(builder, level + 1);
    result = result && attr_value_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !'='
  private static boolean attr_value_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_value_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !consumeToken(builder, BNF_OP_EQ);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // reference_or_token
  //   | literal_expression
  //   | value_list
  static boolean attr_value_inner(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attr_value_inner")) return false;
    boolean result;
    result = reference_or_token(builder, level + 1);
    if (!result) result = literal_expression(builder, level + 1);
    if (!result) result = value_list(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // '{' attr * '}'
  public static boolean attrs(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attrs")) return false;
    if (!nextTokenIs(builder, BNF_LEFT_BRACE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_ATTRS, null);
    result = consumeToken(builder, BNF_LEFT_BRACE);
    pinned = result; // pin = 1
    result = result && report_error_(builder, attrs_1(builder, level + 1));
    result = pinned && consumeToken(builder, BNF_RIGHT_BRACE) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // attr *
  private static boolean attrs_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "attrs_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!attr(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "attrs_1", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ( '|' sequence ) +
  public static boolean choice(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "choice")) return false;
    if (!nextTokenIs(builder, BNF_OP_OR)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _LEFT_, BNF_CHOICE, null);
    result = choice_0(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!choice_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "choice", pos)) break;
    }
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '|' sequence
  private static boolean choice_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "choice_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, BNF_OP_OR);
    pinned = result; // pin = 1
    result = result && sequence(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // sequence choice?
  public static boolean expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, BNF_EXPRESSION, "<expression>");
    result = sequence(builder, level + 1);
    result = result && expression_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // choice?
  private static boolean expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "expression_1")) return false;
    choice(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // '<<' reference_or_token option * '>>'
  public static boolean external_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "external_expression")) return false;
    if (!nextTokenIs(builder, BNF_EXTERNAL_START)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_EXTERNAL_EXPRESSION, null);
    result = consumeToken(builder, BNF_EXTERNAL_START);
    result = result && reference_or_token(builder, level + 1);
    pinned = result; // pin = 2
    result = result && report_error_(builder, external_expression_2(builder, level + 1));
    result = pinned && consumeToken(builder, BNF_EXTERNAL_END) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // option *
  private static boolean external_expression_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "external_expression_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!option(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "external_expression_2", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> (attrs | rule)
  static boolean grammar_element(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "grammar_element")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = grammar_element_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && grammar_element_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, grammar_element_recover_parser_);
    return result || pinned;
  }

  // !<<eof>>
  private static boolean grammar_element_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "grammar_element_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !eof(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // attrs | rule
  private static boolean grammar_element_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "grammar_element_1")) return false;
    boolean result;
    result = attrs(builder, level + 1);
    if (!result) result = rule(builder, level + 1);
    return result;
  }

  /* ********************************************************** */
  // !('{'|rule_start)
  static boolean grammar_element_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "grammar_element_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !grammar_element_recover_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '{'|rule_start
  private static boolean grammar_element_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "grammar_element_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_LEFT_BRACE);
    if (!result) result = rule_start(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // (id list_entry_tail? | string_literal_expression) ';'?
  public static boolean list_entry(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_LIST_ENTRY, "<list entry>");
    result = list_entry_0(builder, level + 1);
    result = result && list_entry_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, list_entry_recover_parser_);
    return result;
  }

  // id list_entry_tail? | string_literal_expression
  private static boolean list_entry_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = list_entry_0_0(builder, level + 1);
    if (!result) result = string_literal_expression(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // id list_entry_tail?
  private static boolean list_entry_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_ID);
    result = result && list_entry_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // list_entry_tail?
  private static boolean list_entry_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry_0_0_1")) return false;
    list_entry_tail(builder, level + 1);
    return true;
  }

  // ';'?
  private static boolean list_entry_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry_1")) return false;
    consumeToken(builder, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // !(']' | '}' | id | string)
  static boolean list_entry_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !list_entry_recover_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ']' | '}' | id | string
  private static boolean list_entry_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_RIGHT_BRACKET);
    if (!result) result = consumeToken(builder, BNF_RIGHT_BRACE);
    if (!result) result = consumeToken(builder, BNF_ID);
    if (!result) result = consumeToken(builder, BNF_STRING);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // '=' string_literal_expression
  static boolean list_entry_tail(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "list_entry_tail")) return false;
    if (!nextTokenIs(builder, BNF_OP_EQ)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, BNF_OP_EQ);
    pinned = result; // pin = 1
    result = result && string_literal_expression(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // string_literal_expression | number
  public static boolean literal_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "literal_expression")) return false;
    if (!nextTokenIs(builder, "<literal expression>", BNF_NUMBER, BNF_STRING)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, BNF_LITERAL_EXPRESSION, "<literal expression>");
    result = string_literal_expression(builder, level + 1);
    if (!result) result = consumeToken(builder, BNF_NUMBER);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // 'private' | 'external' | 'meta'
  //   | 'inner' | 'left' | 'upper' | 'fake'
  public static boolean modifier(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "modifier")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_MODIFIER, "<modifier>");
    result = consumeToken(builder, "private");
    if (!result) result = consumeToken(builder, "external");
    if (!result) result = consumeToken(builder, "meta");
    if (!result) result = consumeToken(builder, "inner");
    if (!result) result = consumeToken(builder, "left");
    if (!result) result = consumeToken(builder, "upper");
    if (!result) result = consumeToken(builder, "fake");
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // predicate | paren_opt_expression | simple quantified?
  static boolean option(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = predicate(builder, level + 1);
    if (!result) result = paren_opt_expression(builder, level + 1);
    if (!result) result = option_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // simple quantified?
  private static boolean option_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = simple(builder, level + 1);
    result = result && option_2_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // quantified?
  private static boolean option_2_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_2_1")) return false;
    quantified(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' expression ')' | '{' alt_choice_element '}'
  public static boolean paren_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "paren_expression")) return false;
    if (!nextTokenIs(builder, "<paren expression>", BNF_LEFT_BRACE, BNF_LEFT_PAREN)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_PAREN_EXPRESSION, "<paren expression>");
    result = paren_expression_0(builder, level + 1);
    if (!result) result = paren_expression_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '(' expression ')'
  private static boolean paren_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "paren_expression_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, BNF_LEFT_PAREN);
    result = result && expression(builder, level + 1);
    pinned = result; // pin = 2
    result = result && consumeToken(builder, BNF_RIGHT_PAREN);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // '{' alt_choice_element '}'
  private static boolean paren_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "paren_expression_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, BNF_LEFT_BRACE);
    result = result && alt_choice_element(builder, level + 1);
    pinned = result; // pin = 2
    result = result && consumeToken(builder, BNF_RIGHT_BRACE);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '[' expression ']'
  public static boolean paren_opt_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "paren_opt_expression")) return false;
    if (!nextTokenIs(builder, BNF_LEFT_BRACKET)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_PAREN_OPT_EXPRESSION, null);
    result = consumeToken(builder, BNF_LEFT_BRACKET);
    result = result && expression(builder, level + 1);
    pinned = result; // pin = 2
    result = result && consumeToken(builder, BNF_RIGHT_BRACKET);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // predicate_sign simple
  public static boolean predicate(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "predicate")) return false;
    if (!nextTokenIs(builder, "<predicate>", BNF_OP_AND, BNF_OP_NOT)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_PREDICATE, "<predicate>");
    result = predicate_sign(builder, level + 1);
    result = result && simple(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '&' | '!'
  public static boolean predicate_sign(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "predicate_sign")) return false;
    if (!nextTokenIs(builder, "<predicate sign>", BNF_OP_AND, BNF_OP_NOT)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_PREDICATE_SIGN, "<predicate sign>");
    result = consumeToken(builder, BNF_OP_AND);
    if (!result) result = consumeToken(builder, BNF_OP_NOT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // quantifier
  public static boolean quantified(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quantified")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _LEFT_, BNF_QUANTIFIED, "<quantified>");
    result = quantifier(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '?' | '+' | '*'
  public static boolean quantifier(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quantifier")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_QUANTIFIER, "<quantifier>");
    result = consumeToken(builder, BNF_OP_OPT);
    if (!result) result = consumeToken(builder, BNF_OP_ONEMORE);
    if (!result) result = consumeToken(builder, BNF_OP_ZEROMORE);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // id
  public static boolean reference_or_token(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "reference_or_token")) return false;
    if (!nextTokenIs(builder, BNF_ID)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_ID);
    exit_section_(builder, marker, BNF_REFERENCE_OR_TOKEN, result);
    return result;
  }

  /* ********************************************************** */
  // rule_start expression attrs? ';'?
  public static boolean rule(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_RULE, "<rule>");
    result = rule_start(builder, level + 1);
    result = result && expression(builder, level + 1);
    pinned = result; // pin = 2
    result = result && report_error_(builder, rule_2(builder, level + 1));
    result = pinned && rule_3(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // attrs?
  private static boolean rule_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_2")) return false;
    attrs(builder, level + 1);
    return true;
  }

  // ';'?
  private static boolean rule_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_3")) return false;
    consumeToken(builder, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // modifier* id '::='
  static boolean rule_start(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_start")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = rule_start_0(builder, level + 1);
    result = result && consumeTokens(builder, 0, BNF_ID, BNF_OP_IS);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // modifier*
  private static boolean rule_start_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_start_0")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!modifier(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "rule_start_0", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // option *
  public static boolean sequence(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence")) return false;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, BNF_SEQUENCE, "<sequence>");
    while (true) {
      int pos = current_position_(builder);
      if (!option(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "sequence", pos)) break;
    }
    exit_section_(builder, level, marker, true, false, sequence_recover_parser_);
    return true;
  }

  /* ********************************************************** */
  // !(';'|'|'|'('|')'|'['|']'|'{'|'}') grammar_element_recover
  static boolean sequence_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = sequence_recover_0(builder, level + 1);
    result = result && grammar_element_recover(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !(';'|'|'|'('|')'|'['|']'|'{'|'}')
  private static boolean sequence_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !sequence_recover_0_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // ';'|'|'|'('|')'|'['|']'|'{'|'}'
  private static boolean sequence_recover_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_recover_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_SEMICOLON);
    if (!result) result = consumeToken(builder, BNF_OP_OR);
    if (!result) result = consumeToken(builder, BNF_LEFT_PAREN);
    if (!result) result = consumeToken(builder, BNF_RIGHT_PAREN);
    if (!result) result = consumeToken(builder, BNF_LEFT_BRACKET);
    if (!result) result = consumeToken(builder, BNF_RIGHT_BRACKET);
    if (!result) result = consumeToken(builder, BNF_LEFT_BRACE);
    if (!result) result = consumeToken(builder, BNF_RIGHT_BRACE);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token
  //   | literal_expression
  //   | external_expression
  //   | paren_expression
  static boolean simple(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "simple")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = simple_0(builder, level + 1);
    if (!result) result = literal_expression(builder, level + 1);
    if (!result) result = external_expression(builder, level + 1);
    if (!result) result = paren_expression(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !(modifier* id '::=' ) reference_or_token
  private static boolean simple_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "simple_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = simple_0_0(builder, level + 1);
    result = result && reference_or_token(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !(modifier* id '::=' )
  private static boolean simple_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "simple_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !simple_0_0_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // modifier* id '::='
  private static boolean simple_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "simple_0_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = simple_0_0_0_0(builder, level + 1);
    result = result && consumeTokens(builder, 0, BNF_ID, BNF_OP_IS);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // modifier*
  private static boolean simple_0_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "simple_0_0_0_0")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!modifier(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "simple_0_0_0_0", pos)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // string
  public static boolean string_literal_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "string_literal_expression")) return false;
    if (!nextTokenIs(builder, BNF_STRING)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, BNF_STRING);
    exit_section_(builder, marker, BNF_STRING_LITERAL_EXPRESSION, result);
    return result;
  }

  /* ********************************************************** */
  // '[' list_entry * ']'
  public static boolean value_list(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "value_list")) return false;
    if (!nextTokenIs(builder, BNF_LEFT_BRACKET)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, BNF_VALUE_LIST, null);
    result = consumeToken(builder, BNF_LEFT_BRACKET);
    pinned = result; // pin = 1
    result = result && report_error_(builder, value_list_1(builder, level + 1));
    result = pinned && consumeToken(builder, BNF_RIGHT_BRACKET) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // list_entry *
  private static boolean value_list_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "value_list_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!list_entry(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "value_list_1", pos)) break;
    }
    return true;
  }

  static final Parser attr_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return attr_recover(builder, level + 1);
    }
  };
  static final Parser grammar_element_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return grammar_element(builder, level + 1);
    }
  };
  static final Parser grammar_element_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return grammar_element_recover(builder, level + 1);
    }
  };
  static final Parser list_entry_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return list_entry_recover(builder, level + 1);
    }
  };
  static final Parser sequence_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return sequence_recover(builder, level + 1);
    }
  };
}