// ---- GrammarParser.java -----------------
header.txt
package org.intellij.grammar.parser;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
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

  public static Logger LOG_ = Logger.getInstance("org.intellij.grammar.parser.GrammarParser");

  @NotNull
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
    if (root_ == BNF_ATTR) {
      result_ = attr(builder_, level_ + 1);
    }
    else if (root_ == BNF_ATTR_PATTERN) {
      result_ = attr_pattern(builder_, level_ + 1);
    }
    else if (root_ == BNF_ATTRS) {
      result_ = attrs(builder_, level_ + 1);
    }
    else if (root_ == BNF_CHOICE) {
      result_ = choice(builder_, level_ + 1);
    }
    else if (root_ == BNF_EXPRESSION) {
      result_ = expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_EXTERNAL_EXPRESSION) {
      result_ = external_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_LIST_ENTRY) {
      result_ = list_entry(builder_, level_ + 1);
    }
    else if (root_ == BNF_LITERAL_EXPRESSION) {
      result_ = literal_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_MODIFIER) {
      result_ = modifier(builder_, level_ + 1);
    }
    else if (root_ == BNF_PAREN_EXPRESSION) {
      result_ = paren_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_PAREN_OPT_EXPRESSION) {
      result_ = paren_opt_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_PREDICATE) {
      result_ = predicate(builder_, level_ + 1);
    }
    else if (root_ == BNF_PREDICATE_SIGN) {
      result_ = predicate_sign(builder_, level_ + 1);
    }
    else if (root_ == BNF_QUANTIFIED) {
      result_ = quantified(builder_, level_ + 1);
    }
    else if (root_ == BNF_QUANTIFIER) {
      result_ = quantifier(builder_, level_ + 1);
    }
    else if (root_ == BNF_REFERENCE_OR_TOKEN) {
      result_ = reference_or_token(builder_, level_ + 1);
    }
    else if (root_ == BNF_RULE) {
      result_ = rule(builder_, level_ + 1);
    }
    else if (root_ == BNF_SEQUENCE) {
      result_ = sequence(builder_, level_ + 1);
    }
    else if (root_ == BNF_STRING_LITERAL_EXPRESSION) {
      result_ = string_literal_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_VALUE_LIST) {
      result_ = value_list(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
      result_ = parse_root_(root_, builder_, level_);
      exitErrorRecordingSection(builder_, level_, result_, true, _SECTION_RECOVER_, TOKEN_ADVANCER);
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return parseGrammar(builder_, level_ + 1, grammar_element_parser_);
  }

  private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(BNF_CHOICE, BNF_EXPRESSION, BNF_EXTERNAL_EXPRESSION, BNF_LITERAL_EXPRESSION,
      BNF_PAREN_EXPRESSION, BNF_PAREN_OPT_EXPRESSION, BNF_PREDICATE, BNF_QUANTIFIED,
      BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE, BNF_STRING_LITERAL_EXPRESSION, BNF_VALUE_LIST),
    create_token_set_(BNF_LITERAL_EXPRESSION, BNF_STRING_LITERAL_EXPRESSION),
  };

  public static boolean type_extends_(IElementType child_, IElementType parent_) {
    return type_extends_impl_(EXTENDS_SETS_, child_, parent_);
  }

  /* ********************************************************** */
  // !attr_start_simple expression
  static boolean alt_choice_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alt_choice_element")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = alt_choice_element_0(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // !attr_start_simple
  private static boolean alt_choice_element_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alt_choice_element_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !attr_start_simple(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  /* ********************************************************** */
  // attr_start attr_value ';'?
  public static boolean attr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, "<attr>");
    result_ = attr_start(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, attr_value(builder_, level_ + 1));
    result_ = pinned_ && attr_2(builder_, level_ + 1) && result_;
    if (result_ || pinned_) {
      marker_.done(BNF_ATTR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_RECOVER_, attr_recover_until_parser_);
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
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, string_literal_expression(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_RIGHT_PAREN) && result_;
    if (result_ || pinned_) {
      marker_.done(BNF_ATTR_PATTERN);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // !('}' | attr_start)
  static boolean attr_recover_until(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_until")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !attr_recover_until_0(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  // '}' | attr_start
  private static boolean attr_recover_until_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_until_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
    if (!result_) result_ = attr_start(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // id (attr_pattern '=' | '=')
  static boolean attr_start(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start")) return false;
    if (!nextTokenIs(builder_, BNF_ID)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_ID);
    result_ = result_ && attr_start_1(builder_, level_ + 1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // attr_pattern '=' | '='
  private static boolean attr_start_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = attr_start_1_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_EQ);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // attr_pattern '='
  private static boolean attr_start_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = attr_pattern(builder_, level_ + 1);
    pinned_ = result_; // pin = attr_pattern
    result_ = result_ && consumeToken(builder_, BNF_OP_EQ);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // id attr_pattern? '='
  static boolean attr_start_simple(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_start_simple")) return false;
    if (!nextTokenIs(builder_, BNF_ID)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_ID);
    result_ = result_ && attr_start_simple_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_OP_EQ);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
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
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = attr_value_0(builder_, level_ + 1);
    result_ = result_ && attr_value_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // reference_or_token | literal_expression | value_list
  private static boolean attr_value_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = reference_or_token(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
    if (!result_) result_ = value_list(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // !'='
  private static boolean attr_value_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !consumeToken(builder_, BNF_OP_EQ);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  /* ********************************************************** */
  // '{' attr * '}'
  public static boolean attrs(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrs")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACE)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, attrs_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_RIGHT_BRACE) && result_;
    if (result_ || pinned_) {
      marker_.done(BNF_ATTRS);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // attr *
  private static boolean attrs_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrs_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!attr(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "attrs_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // ( '|' sequence ) +
  public static boolean choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice")) return false;
    if (!nextTokenIs(builder_, BNF_OP_OR)) return false;
    boolean result_ = false;
    Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "choice")) return false;
    Marker marker_ = builder_.mark();
    result_ = choice_0(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!choice_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "choice");
        break;
      }
      offset_ = next_offset_;
    }
    if (result_) {
      marker_.drop();
      left_marker_.precede().done(BNF_CHOICE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // '|' sequence
  private static boolean choice_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_OP_OR);
    pinned_ = result_; // pin = 1
    result_ = result_ && sequence(builder_, level_ + 1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // sequence choice?
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<expression>");
    result_ = sequence(builder_, level_ + 1);
    result_ = result_ && expression_1(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_EXPRESSION)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(BNF_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
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
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_EXTERNAL_START);
    result_ = result_ && reference_or_token(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, external_expression_2(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_EXTERNAL_END) && result_;
    if (result_ || pinned_) {
      marker_.done(BNF_EXTERNAL_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // option *
  private static boolean external_expression_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "external_expression_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!option(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "external_expression_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> (attrs | rule)
  static boolean grammar_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
    result_ = grammar_element_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && grammar_element_1(builder_, level_ + 1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_RECOVER_, grammar_element_recover_parser_);
    return result_ || pinned_;
  }

  // !<<eof>>
  private static boolean grammar_element_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !eof(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  // attrs | rule
  private static boolean grammar_element_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = attrs(builder_, level_ + 1);
    if (!result_) result_ = rule(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // !('{'|rule_start)
  static boolean grammar_element_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_recover")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !grammar_element_recover_0(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  // '{'|rule_start
  private static boolean grammar_element_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element_recover_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    if (!result_) result_ = rule_start(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // (id list_entry_tail? | string_literal_expression) ';'?
  public static boolean list_entry(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, "<list entry>");
    result_ = list_entry_0(builder_, level_ + 1);
    result_ = result_ && list_entry_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(BNF_LIST_ENTRY);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_RECOVER_, list_entry_recover_until_parser_);
    return result_;
  }

  // id list_entry_tail? | string_literal_expression
  private static boolean list_entry_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = list_entry_0_0(builder_, level_ + 1);
    if (!result_) result_ = string_literal_expression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // id list_entry_tail?
  private static boolean list_entry_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_ID);
    result_ = result_ && list_entry_0_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
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
  static boolean list_entry_recover_until(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_recover_until")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !list_entry_recover_until_0(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  // ']' | '}' | id | string
  private static boolean list_entry_recover_until_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_recover_until_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_RIGHT_BRACKET);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
    if (!result_) result_ = consumeToken(builder_, BNF_ID);
    if (!result_) result_ = consumeToken(builder_, BNF_STRING);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '=' string_literal_expression
  static boolean list_entry_tail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_entry_tail")) return false;
    if (!nextTokenIs(builder_, BNF_OP_EQ)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_OP_EQ);
    pinned_ = result_; // pin = 1
    result_ = result_ && string_literal_expression(builder_, level_ + 1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // string_literal_expression | number
  public static boolean literal_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expression")) return false;
    if (!nextTokenIs(builder_, BNF_NUMBER) && !nextTokenIs(builder_, BNF_STRING)
        && replaceVariants(builder_, 2, "<literal expression>")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<literal expression>");
    result_ = string_literal_expression(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, BNF_NUMBER);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_LITERAL_EXPRESSION)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(BNF_LITERAL_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // 'private' | 'external' | 'meta' | 'inner' | 'left' | 'fake'
  public static boolean modifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "modifier")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<modifier>");
    result_ = consumeToken(builder_, "private");
    if (!result_) result_ = consumeToken(builder_, "external");
    if (!result_) result_ = consumeToken(builder_, "meta");
    if (!result_) result_ = consumeToken(builder_, "inner");
    if (!result_) result_ = consumeToken(builder_, "left");
    if (!result_) result_ = consumeToken(builder_, "fake");
    if (result_) {
      marker_.done(BNF_MODIFIER);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // predicate | paren_opt_expression | simple quantified?
  static boolean option(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = predicate(builder_, level_ + 1);
    if (!result_) result_ = paren_opt_expression(builder_, level_ + 1);
    if (!result_) result_ = option_2(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // simple quantified?
  private static boolean option_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = simple(builder_, level_ + 1);
    result_ = result_ && option_2_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
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
    if (!nextTokenIs(builder_, BNF_LEFT_PAREN) && !nextTokenIs(builder_, BNF_LEFT_BRACE)
        && replaceVariants(builder_, 2, "<paren expression>")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<paren expression>");
    result_ = paren_expression_0(builder_, level_ + 1);
    if (!result_) result_ = paren_expression_1(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_PAREN_EXPRESSION)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(BNF_PAREN_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  // '(' expression ')'
  private static boolean paren_expression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    result_ = result_ && expression(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // '{' alt_choice_element '}'
  private static boolean paren_expression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression_1")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    result_ = result_ && alt_choice_element(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '[' expression ']'
  public static boolean paren_opt_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_opt_expression")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACKET)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    result_ = result_ && expression(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACKET);
    if (result_ || pinned_) {
      marker_.done(BNF_PAREN_OPT_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // predicate_sign simple
  public static boolean predicate(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate")) return false;
    if (!nextTokenIs(builder_, BNF_OP_NOT) && !nextTokenIs(builder_, BNF_OP_AND)
        && replaceVariants(builder_, 2, "<predicate>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<predicate>");
    result_ = predicate_sign(builder_, level_ + 1);
    result_ = result_ && simple(builder_, level_ + 1);
    if (result_) {
      marker_.done(BNF_PREDICATE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // '&' | '!'
  public static boolean predicate_sign(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate_sign")) return false;
    if (!nextTokenIs(builder_, BNF_OP_NOT) && !nextTokenIs(builder_, BNF_OP_AND)
        && replaceVariants(builder_, 2, "<predicate sign>")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<predicate sign>");
    result_ = consumeToken(builder_, BNF_OP_AND);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_NOT);
    if (result_) {
      marker_.done(BNF_PREDICATE_SIGN);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // quantifier
  public static boolean quantified(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified")) return false;
    boolean result_ = false;
    Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "quantified")) return false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<quantified>");
    result_ = quantifier(builder_, level_ + 1);
    if (result_) {
      marker_.drop();
      left_marker_.precede().done(BNF_QUANTIFIED);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // '?' | '+' | '*'
  public static boolean quantifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<quantifier>");
    result_ = consumeToken(builder_, BNF_OP_OPT);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ONEMORE);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ZEROMORE);
    if (result_) {
      marker_.done(BNF_QUANTIFIER);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean reference_or_token(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "reference_or_token")) return false;
    if (!nextTokenIs(builder_, BNF_ID)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_ID);
    if (result_) {
      marker_.done(BNF_REFERENCE_OR_TOKEN);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // rule_start expression attrs? ';'?
  public static boolean rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<rule>");
    result_ = rule_start(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, rule_2(builder_, level_ + 1));
    result_ = pinned_ && rule_3(builder_, level_ + 1) && result_;
    if (result_ || pinned_) {
      marker_.done(BNF_RULE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
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
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = rule_start_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_ID);
    result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // modifier*
  private static boolean rule_start_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_start_0")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!modifier(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "rule_start_0");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // option *
  public static boolean sequence(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, "<sequence>");
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!option(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "sequence");
        break;
      }
      offset_ = next_offset_;
    }
    LighterASTNode last_ = builder_.getLatestDoneMarker();
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_SEQUENCE)) {
      marker_.drop();
    }
    else {
      marker_.done(BNF_SEQUENCE);
    }
    exitErrorRecordingSection(builder_, level_, true, false, _SECTION_RECOVER_, sequence_recover_parser_);
    return true;
  }

  /* ********************************************************** */
  // !(';'|'|'|'('|')'|'['|']'|'{'|'}') grammar_element_recover
  static boolean sequence_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = sequence_recover_0(builder_, level_ + 1);
    result_ = result_ && grammar_element_recover(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // !(';'|'|'|'('|')'|'['|']'|'{'|'}')
  private static boolean sequence_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !sequence_recover_0_0(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  // ';'|'|'|'('|')'|'['|']'|'{'|'}'
  private static boolean sequence_recover_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_recover_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_SEMICOLON);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_OR);
    if (!result_) result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_PAREN);
    if (!result_) result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACKET);
    if (!result_) result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    if (!result_) result_ = consumeToken(builder_, BNF_RIGHT_BRACE);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token | literal_expression | external_expression | paren_expression
  static boolean simple(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = simple_0(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
    if (!result_) result_ = external_expression(builder_, level_ + 1);
    if (!result_) result_ = paren_expression(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // !(modifier* id '::=' ) reference_or_token
  private static boolean simple_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = simple_0_0(builder_, level_ + 1);
    result_ = result_ && reference_or_token(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // !(modifier* id '::=' )
  private static boolean simple_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !simple_0_0_0(builder_, level_ + 1);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  // modifier* id '::='
  private static boolean simple_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = simple_0_0_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_ID);
    result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // modifier*
  private static boolean simple_0_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0_0")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!modifier(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "simple_0_0_0_0");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // string
  public static boolean string_literal_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string_literal_expression")) return false;
    if (!nextTokenIs(builder_, BNF_STRING)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_STRING);
    if (result_) {
      marker_.done(BNF_STRING_LITERAL_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // '[' list_entry * ']'
  public static boolean value_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "value_list")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACKET)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, value_list_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, BNF_RIGHT_BRACKET) && result_;
    if (result_ || pinned_) {
      marker_.done(BNF_VALUE_LIST);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // list_entry *
  private static boolean value_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "value_list_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!list_entry(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "value_list_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  final static Parser attr_recover_until_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return attr_recover_until(builder_, level_ + 1);
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
  final static Parser list_entry_recover_until_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return list_entry_recover_until(builder_, level_ + 1);
    }
  };
  final static Parser sequence_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return sequence_recover(builder_, level_ + 1);
    }
  };
}