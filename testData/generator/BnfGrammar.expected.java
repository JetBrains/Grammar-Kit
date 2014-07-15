// ---- GrammarParser.java -----------------
license.txt
package org.intellij.grammar.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.intellij.grammar.psi.BnfTypes.*;
import static org.intellij.grammar.parser.GrammarParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GrammarParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("org.intellij.grammar.parser.GrammarParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == BNF_ATTR) {
      result_ = attr(builder_, 0);
    }
    else if (root_ == BNF_ATTR_PATTERN) {
      result_ = attr_pattern(builder_, 0);
    }
    else if (root_ == BNF_ATTRS) {
      result_ = attrs(builder_, 0);
    }
    else if (root_ == BNF_CHOICE) {
      result_ = choice(builder_, 0);
    }
    else if (root_ == BNF_EXPRESSION) {
      result_ = expression(builder_, 0);
    }
    else if (root_ == BNF_EXTERNAL_EXPRESSION) {
      result_ = external_expression(builder_, 0);
    }
    else if (root_ == BNF_LIST_ENTRY) {
      result_ = list_entry(builder_, 0);
    }
    else if (root_ == BNF_LITERAL_EXPRESSION) {
      result_ = literal_expression(builder_, 0);
    }
    else if (root_ == BNF_MODIFIER) {
      result_ = modifier(builder_, 0);
    }
    else if (root_ == BNF_PAREN_EXPRESSION) {
      result_ = paren_expression(builder_, 0);
    }
    else if (root_ == BNF_PAREN_OPT_EXPRESSION) {
      result_ = paren_opt_expression(builder_, 0);
    }
    else if (root_ == BNF_PREDICATE) {
      result_ = predicate(builder_, 0);
    }
    else if (root_ == BNF_PREDICATE_SIGN) {
      result_ = predicate_sign(builder_, 0);
    }
    else if (root_ == BNF_QUANTIFIED) {
      result_ = quantified(builder_, 0);
    }
    else if (root_ == BNF_QUANTIFIER) {
      result_ = quantifier(builder_, 0);
    }
    else if (root_ == BNF_REFERENCE_OR_TOKEN) {
      result_ = reference_or_token(builder_, 0);
    }
    else if (root_ == BNF_RULE) {
      result_ = rule(builder_, 0);
    }
    else if (root_ == BNF_SEQUENCE) {
      result_ = sequence(builder_, 0);
    }
    else if (root_ == BNF_STRING_LITERAL_EXPRESSION) {
      result_ = string_literal_expression(builder_, 0);
    }
    else if (root_ == BNF_VALUE_LIST) {
      result_ = value_list(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return parseGrammar(builder_, level_ + 1, grammar_element_parser_);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(BNF_CHOICE, BNF_EXPRESSION, BNF_EXTERNAL_EXPRESSION, BNF_LITERAL_EXPRESSION,
      BNF_PAREN_EXPRESSION, BNF_PAREN_OPT_EXPRESSION, BNF_PREDICATE, BNF_QUANTIFIED,
      BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE, BNF_STRING_LITERAL_EXPRESSION, BNF_VALUE_LIST),
    create_token_set_(BNF_LITERAL_EXPRESSION, BNF_STRING_LITERAL_EXPRESSION),
  };

  /* ********************************************************** */
  // !attr_start_simple expression
  static boolean alt_choice_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alt_choice_element")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = alt_choice_element_0(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !attr_start_simple
  private static boolean alt_choice_element_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alt_choice_element_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !attr_start_simple(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // attr_start attr_value ';'?
  public static boolean attr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr")) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<attr>");
    result_ = attr_start(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, attr_value(builder_, level_ + 1));
    result_ = pinned_ && attr_2(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, BNF_ATTR, result_, pinned_, attr_recover_parser_);
    return result_ || pinned_;
  }

  // ';'?
  private static boolean attr_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_2")) return false;
    consumeToken(builder_, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // '(' string_literal_expression ')'
  public static boolean attr_pattern(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_pattern")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_PAREN)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, string_literal_expression(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_RIGHT_PAREN) && result_;
    exit_section_(builder_, level_, marker_, BNF_ATTR_PATTERN, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // !('}' | attr_start)
  static boolean attr_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !attr_recover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // '}' | attr_start
  private static boolean attr_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
    if (!result_) result_ = attr_start(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // id (attr_pattern '=' | '=')
  static boolean attr_start(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start")) return false;
    if (!nextTokenIs(builder_, BNF_ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_ID);
    result_ = result_ && attr_start_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attr_pattern '=' | '='
  private static boolean attr_start_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = attr_start_1_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attr_pattern '='
  private static boolean attr_start_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_1_0")) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = attr_pattern(builder_, level_ + 1);
    pinned_ = result_; // pin = attr_pattern
    result_ = result_ && consumeToken(builder_, BNF_OP_EQ);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // id attr_pattern? '='
  static boolean attr_start_simple(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_simple")) return false;
    if (!nextTokenIs(builder_, BNF_ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_ID);
    result_ = result_ && attr_start_simple_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_OP_EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // attr_pattern?
  private static boolean attr_start_simple_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_simple_1")) return false;
    attr_pattern(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (reference_or_token | literal_expression | value_list) !'='
  static boolean attr_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = attr_value_0(builder_, level_ + 1);
    result_ = result_ && attr_value_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // reference_or_token | literal_expression | value_list
  private static boolean attr_value_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = reference_or_token(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
    if (!result_) result_ = value_list(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !'='
  private static boolean attr_value_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !consumeToken(builder_, BNF_OP_EQ);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '{' attr * '}'
  public static boolean attrs(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrs")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACE)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, attrs_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_RIGHT_BRACE) && result_;
    exit_section_(builder_, level_, marker_, BNF_ATTRS, result_, pinned_, null);
    return result_ || pinned_;
  }

  // attr *
  private static boolean attrs_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrs_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!attr(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "attrs_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // ( '|' sequence ) +
  public static boolean choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice")) return false;
    if (!nextTokenIs(builder_, BNF_OP_OR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, null);
    result_ = choice_0(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!choice_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "choice", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, level_, marker_, BNF_CHOICE, result_, false, null);
    return result_;
  }

  // '|' sequence
  private static boolean choice_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0")) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_OP_OR);
    pinned_ = result_; // pin = 1
    result_ = result_ && sequence(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // sequence choice?
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<expression>");
    result_ = sequence(builder_, level_ + 1);
    result_ = result_ && expression_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BNF_EXPRESSION, result_, false, null);
    return result_;
  }

  // choice?
  private static boolean expression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression_1")) return false;
    choice(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // '<<' reference_or_token option * '>>'
  public static boolean external_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "external_expression")) return false;
    if (!nextTokenIs(builder_, BNF_EXTERNAL_START)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_EXTERNAL_START);
    result_ = result_ && reference_or_token(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, external_expression_2(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_EXTERNAL_END) && result_;
    exit_section_(builder_, level_, marker_, BNF_EXTERNAL_EXPRESSION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // option *
  private static boolean external_expression_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "external_expression_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!option(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "external_expression_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> (attrs | rule)
  static boolean grammar_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element")) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = grammar_element_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && grammar_element_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, grammar_element_recover_parser_);
    return result_ || pinned_;
  }

  // !<<eof>>
  private static boolean grammar_element_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !eof(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // attrs | rule
  private static boolean grammar_element_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = attrs(builder_, level_ + 1);
    if (!result_) result_ = rule(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // !('{'|rule_start)
  static boolean grammar_element_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_recover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !grammar_element_recover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // '{'|rule_start
  private static boolean grammar_element_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_recover_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    if (!result_) result_ = rule_start(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (id list_entry_tail? | string_literal_expression) ';'?
  public static boolean list_entry(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<list entry>");
    result_ = list_entry_0(builder_, level_ + 1);
    result_ = result_ && list_entry_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BNF_LIST_ENTRY, result_, false, list_entry_recover_parser_);
    return result_;
  }

  // id list_entry_tail? | string_literal_expression
  private static boolean list_entry_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = list_entry_0_0(builder_, level_ + 1);
    if (!result_) result_ = string_literal_expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // id list_entry_tail?
  private static boolean list_entry_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_ID);
    result_ = result_ && list_entry_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // list_entry_tail?
  private static boolean list_entry_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_0_0_1")) return false;
    list_entry_tail(builder_, level_ + 1);
    return true;
  }

  // ';'?
  private static boolean list_entry_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_1")) return false;
    consumeToken(builder_, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // !(']' | '}' | id | string)
  static boolean list_entry_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_recover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !list_entry_recover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // ']' | '}' | id | string
  private static boolean list_entry_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_recover_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_RIGHT_BRACKET);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
    if (!result_) result_ = consumeToken(builder_, BNF_ID);
    if (!result_) result_ = consumeToken(builder_, BNF_STRING);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '=' string_literal_expression
  static boolean list_entry_tail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_tail")) return false;
    if (!nextTokenIs(builder_, BNF_OP_EQ)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_OP_EQ);
    pinned_ = result_; // pin = 1
    result_ = result_ && string_literal_expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // string_literal_expression | number
  public static boolean literal_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expression")) return false;
    if (!nextTokenIs(builder_, "<literal expression>", BNF_NUMBER, BNF_STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<literal expression>");
    result_ = string_literal_expression(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, BNF_NUMBER);
    exit_section_(builder_, level_, marker_, BNF_LITERAL_EXPRESSION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'private' | 'external' | 'meta' | 'inner' | 'left' | 'fake'
  public static boolean modifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "modifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<modifier>");
    result_ = consumeToken(builder_, "private");
    if (!result_) result_ = consumeToken(builder_, "external");
    if (!result_) result_ = consumeToken(builder_, "meta");
    if (!result_) result_ = consumeToken(builder_, "inner");
    if (!result_) result_ = consumeToken(builder_, "left");
    if (!result_) result_ = consumeToken(builder_, "fake");
    exit_section_(builder_, level_, marker_, BNF_MODIFIER, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // predicate | paren_opt_expression | simple quantified?
  static boolean option(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = predicate(builder_, level_ + 1);
    if (!result_) result_ = paren_opt_expression(builder_, level_ + 1);
    if (!result_) result_ = option_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // simple quantified?
  private static boolean option_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = simple(builder_, level_ + 1);
    result_ = result_ && option_2_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // quantified?
  private static boolean option_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_2_1")) return false;
    quantified(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' expression ')' | '{' alt_choice_element '}'
  public static boolean paren_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression")) return false;
    if (!nextTokenIs(builder_, "<paren expression>", BNF_LEFT_PAREN, BNF_LEFT_BRACE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<paren expression>");
    result_ = paren_expression_0(builder_, level_ + 1);
    if (!result_) result_ = paren_expression_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BNF_PAREN_EXPRESSION, result_, false, null);
    return result_;
  }

  // '(' expression ')'
  private static boolean paren_expression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression_0")) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    result_ = result_ && expression(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // '{' alt_choice_element '}'
  private static boolean paren_expression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression_1")) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    result_ = result_ && alt_choice_element(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '[' expression ']'
  public static boolean paren_opt_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_opt_expression")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACKET)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    result_ = result_ && expression(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACKET);
    exit_section_(builder_, level_, marker_, BNF_PAREN_OPT_EXPRESSION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // predicate_sign simple
  public static boolean predicate(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate")) return false;
    if (!nextTokenIs(builder_, "<predicate>", BNF_OP_NOT, BNF_OP_AND)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<predicate>");
    result_ = predicate_sign(builder_, level_ + 1);
    result_ = result_ && simple(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BNF_PREDICATE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '&' | '!'
  public static boolean predicate_sign(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate_sign")) return false;
    if (!nextTokenIs(builder_, "<predicate sign>", BNF_OP_NOT, BNF_OP_AND)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<predicate sign>");
    result_ = consumeToken(builder_, BNF_OP_AND);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_NOT);
    exit_section_(builder_, level_, marker_, BNF_PREDICATE_SIGN, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // quantifier
  public static boolean quantified(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, "<quantified>");
    result_ = quantifier(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BNF_QUANTIFIED, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '?' | '+' | '*'
  public static boolean quantifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<quantifier>");
    result_ = consumeToken(builder_, BNF_OP_OPT);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ONEMORE);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ZEROMORE);
    exit_section_(builder_, level_, marker_, BNF_QUANTIFIER, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean reference_or_token(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "reference_or_token")) return false;
    if (!nextTokenIs(builder_, BNF_ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_ID);
    exit_section_(builder_, marker_, BNF_REFERENCE_OR_TOKEN, result_);
    return result_;
  }

  /* ********************************************************** */
  // rule_start expression attrs? ';'?
  public static boolean rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule")) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<rule>");
    result_ = rule_start(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, rule_2(builder_, level_ + 1));
    result_ = pinned_ && rule_3(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, BNF_RULE, result_, pinned_, null);
    return result_ || pinned_;
  }

  // attrs?
  private static boolean rule_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_2")) return false;
    attrs(builder_, level_ + 1);
    return true;
  }

  // ';'?
  private static boolean rule_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_3")) return false;
    consumeToken(builder_, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // modifier* id '::='
  static boolean rule_start(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_start")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = rule_start_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_ID);
    result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // modifier*
  private static boolean rule_start_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_start_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!modifier(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "rule_start_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // option *
  public static boolean sequence(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<sequence>");
    int pos_ = current_position_(builder_);
    while (true) {
      if (!option(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "sequence", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, level_, marker_, BNF_SEQUENCE, true, false, sequence_recover_parser_);
    return true;
  }

  /* ********************************************************** */
  // !(';'|'|'|'('|')'|'['|']'|'{'|'}') grammar_element_recover
  static boolean sequence_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = sequence_recover_0(builder_, level_ + 1);
    result_ = result_ && grammar_element_recover(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !(';'|'|'|'('|')'|'['|']'|'{'|'}')
  private static boolean sequence_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !sequence_recover_0_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // ';'|'|'|'('|')'|'['|']'|'{'|'}'
  private static boolean sequence_recover_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_SEMICOLON);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_OR);
    if (!result_) result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_PAREN);
    if (!result_) result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACKET);
    if (!result_) result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token | literal_expression | external_expression | paren_expression
  static boolean simple(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = simple_0(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
    if (!result_) result_ = external_expression(builder_, level_ + 1);
    if (!result_) result_ = paren_expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !(modifier* id '::=' ) reference_or_token
  private static boolean simple_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = simple_0_0(builder_, level_ + 1);
    result_ = result_ && reference_or_token(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !(modifier* id '::=' )
  private static boolean simple_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !simple_0_0_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // modifier* id '::='
  private static boolean simple_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = simple_0_0_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_ID);
    result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // modifier*
  private static boolean simple_0_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!modifier(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "simple_0_0_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // string
  public static boolean string_literal_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string_literal_expression")) return false;
    if (!nextTokenIs(builder_, BNF_STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_STRING);
    exit_section_(builder_, marker_, BNF_STRING_LITERAL_EXPRESSION, result_);
    return result_;
  }

  /* ********************************************************** */
  // '[' list_entry * ']'
  public static boolean value_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "value_list")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACKET)) return false;
    boolean result_;
    boolean pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, value_list_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_RIGHT_BRACKET) && result_;
    exit_section_(builder_, level_, marker_, BNF_VALUE_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // list_entry *
  private static boolean value_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "value_list_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!list_entry(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "value_list_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  final static Parser attr_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return attr_recover(builder_, level_ + 1);
    }
  };
  final static Parser grammar_element_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return grammar_element(builder_, level_ + 1);
    }
  };
  final static Parser grammar_element_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return grammar_element_recover(builder_, level_ + 1);
    }
  };
  final static Parser list_entry_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return list_entry_recover(builder_, level_ + 1);
    }
  };
  final static Parser sequence_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return sequence_recover(builder_, level_ + 1);
    }
  };
}