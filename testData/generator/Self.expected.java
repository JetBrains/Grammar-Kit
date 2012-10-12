// ---- Self.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static org.intellij.grammar.psi.BnfTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Self implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("Self");

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
    else if (root_ == BNF_ATTR_VALUE) {
      result_ = attr_value(builder_, level_ + 1);
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
    else if (root_ == BNF_LITERAL_EXPRESSION) {
      result_ = literal_expression(builder_, level_ + 1);
    }
    else if (root_ == BNF_MODIFIER) {
      result_ = modifier(builder_, level_ + 1);
    }
    else if (root_ == BNF_PAREN_EXPRESSION) {
      result_ = paren_expression(builder_, level_ + 1);
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
    else {
      Marker marker_ = builder_.mark();
      result_ = parse_root_(root_, builder_, level_);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return grammar(builder_, level_ + 1);
  }

  private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    TokenSet.create(BNF_CHOICE, BNF_EXPRESSION, BNF_LITERAL_EXPRESSION, BNF_PAREN_EXPRESSION,
      BNF_PREDICATE, BNF_QUANTIFIED, BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE,
      BNF_STRING_LITERAL_EXPRESSION),
    TokenSet.create(BNF_LITERAL_EXPRESSION, BNF_STRING_LITERAL_EXPRESSION),
  };
  public static boolean type_extends_(IElementType child_, IElementType parent_) {
    for (TokenSet set : EXTENDS_SETS_) {
      if (set.contains(child_) && set.contains(parent_)) return true;
    }
    return false;
  }

  /* ********************************************************** */
  // id attr_pattern? '=' attr_value ';'?
  public static boolean attr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
    result_ = consumeToken(builder_, BNF_ID);
    pinned_ = result_; // pin = 1
    result_ = result_ && attr_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_OP_EQ);
    result_ = result_ && attr_value(builder_, level_ + 1);
    result_ = result_ && attr_4(builder_, level_ + 1);
    if (result_ || pinned_) {
      marker_.done(BNF_ATTR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_RECOVER_, attr_recover_until_parser_);
    return result_ || pinned_;
  }

  // attr_pattern?
  private static boolean attr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_1")) return false;
    attr_pattern(builder_, level_ + 1);
    return true;
  }

  // ';'?
  private static boolean attr_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_4")) return false;
    consumeToken(builder_, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // '(' string ')'
  public static boolean attr_pattern(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_pattern")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_PAREN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    result_ = result_ && consumeToken(builder_, BNF_STRING);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    if (result_) {
      marker_.done(BNF_ATTR_PATTERN);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // !'}'
  static boolean attr_recover_until(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_until")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !consumeToken(builder_, BNF_RIGHT_BRACE);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  /* ********************************************************** */
  // (reference_or_token | literal_expression) !'='
  public static boolean attr_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = attr_value_0(builder_, level_ + 1);
    result_ = result_ && attr_value_1(builder_, level_ + 1);
    if (result_) {
      marker_.done(BNF_ATTR_VALUE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (reference_or_token | literal_expression)
  private static boolean attr_value_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_0")) return false;
    return attr_value_0_0(builder_, level_ + 1);
  }

  // reference_or_token | literal_expression
  private static boolean attr_value_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = reference_or_token(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
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
  // '{' attr* '}'
  public static boolean attrs(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrs")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACE)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    pinned_ = result_; // pin = 1
    result_ = result_ && attrs_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    if (result_ || pinned_) {
      marker_.done(BNF_ATTRS);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // attr*
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
  // '{' sequence ('|' sequence)* '}' | sequence choice_tail*
  public static boolean choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    result_ = choice_0(builder_, level_ + 1);
    if (!result_) result_ = choice_1(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_CHOICE)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(BNF_CHOICE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // '{' sequence ('|' sequence)* '}'
  private static boolean choice_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    result_ = result_ && sequence(builder_, level_ + 1);
    result_ = result_ && choice_0_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // ('|' sequence)*
  private static boolean choice_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!choice_0_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "choice_0_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ('|' sequence)
  private static boolean choice_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0_2_0")) return false;
    return choice_0_2_0_0(builder_, level_ + 1);
  }

  // '|' sequence
  private static boolean choice_0_2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0_2_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_OP_OR);
    result_ = result_ && sequence(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // sequence choice_tail*
  private static boolean choice_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = sequence(builder_, level_ + 1);
    result_ = result_ && choice_1_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // choice_tail*
  private static boolean choice_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_1_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!choice_tail(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "choice_1_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // '|' sequence
  static boolean choice_tail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_tail")) return false;
    if (!nextTokenIs(builder_, BNF_OP_OR)) return false;
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
  // choice?
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    choice(builder_, level_ + 1);
    LighterASTNode last_ = builder_.getLatestDoneMarker();
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_EXPRESSION)) {
      marker_.drop();
    }
    else {
      marker_.done(BNF_EXPRESSION);
    }
    return true;
  }

  /* ********************************************************** */
  // (attrs | rule) *
  static boolean grammar(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!grammar_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "grammar");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (attrs | rule)
  private static boolean grammar_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_0")) return false;
    return grammar_0_0(builder_, level_ + 1);
  }

  // attrs | rule
  private static boolean grammar_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_0_0")) return false;
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
  // string_literal_expression | number
  public static boolean literal_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expression")) return false;
    if (!nextTokenIs(builder_, BNF_NUMBER) && !nextTokenIs(builder_, BNF_STRING)) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
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
    return result_;
  }

  /* ********************************************************** */
  // 'private' | 'external' | 'wrapped'
  public static boolean modifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "modifier")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "private");
    if (!result_) result_ = consumeToken(builder_, "external");
    if (!result_) result_ = consumeToken(builder_, "wrapped");
    if (result_) {
      marker_.done(BNF_MODIFIER);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // quantified | predicate
  static boolean option(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = quantified(builder_, level_ + 1);
    if (!result_) result_ = predicate(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '(' expression ')'
  public static boolean paren_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_PAREN)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    pinned_ = result_; // pin = 1
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    if (result_ || pinned_) {
      marker_.done(BNF_PAREN_EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // predicate_sign  simple
  public static boolean predicate(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate")) return false;
    if (!nextTokenIs(builder_, BNF_OP_NOT) && !nextTokenIs(builder_, BNF_OP_AND)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = predicate_sign(builder_, level_ + 1);
    result_ = result_ && simple(builder_, level_ + 1);
    if (result_) {
      marker_.done(BNF_PREDICATE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // ('&' | '!')
  public static boolean predicate_sign(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate_sign")) return false;
    return predicate_sign_0(builder_, level_ + 1);
  }

  // '&' | '!'
  private static boolean predicate_sign_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate_sign_0")) return false;
    if (!nextTokenIs(builder_, BNF_OP_NOT) && !nextTokenIs(builder_, BNF_OP_AND)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_OP_AND);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_NOT);
    if (result_) {
      marker_.done(BNF_PREDICATE_SIGN);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // '[' expression ']' | simple quantifier?
  public static boolean quantified(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    result_ = quantified_0(builder_, level_ + 1);
    if (!result_) result_ = quantified_1(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_QUANTIFIED)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(BNF_QUANTIFIED);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // '[' expression ']'
  private static boolean quantified_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACKET);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // simple quantifier?
  private static boolean quantified_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = simple(builder_, level_ + 1);
    result_ = result_ && quantified_1_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // quantifier?
  private static boolean quantified_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified_1_1")) return false;
    quantifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // '?' | '+' | '*'
  public static boolean quantifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, BNF_OP_OPT);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ONEMORE);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ZEROMORE);
    if (result_) {
      marker_.done(BNF_QUANTIFIER);
    }
    else {
      marker_.rollbackTo();
    }
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
  // modifier* id '::=' expression attrs? ';'?
  public static boolean rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
    result_ = rule_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_ID);
    result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    pinned_ = result_; // pin = 3
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && rule_4(builder_, level_ + 1);
    result_ = result_ && rule_5(builder_, level_ + 1);
    if (result_ || pinned_) {
      marker_.done(BNF_RULE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_RECOVER_, rule_recover_until_parser_);
    return result_ || pinned_;
  }

  // modifier*
  private static boolean rule_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_0")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!modifier(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "rule_0");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // attrs?
  private static boolean rule_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_4")) return false;
    attrs(builder_, level_ + 1);
    return true;
  }

  // ';'?
  private static boolean rule_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_5")) return false;
    consumeToken(builder_, BNF_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // !'{'
  static boolean rule_recover_until(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_recover_until")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_NOT_, null);
    result_ = !consumeToken(builder_, BNF_LEFT_BRACE);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_NOT_, null);
    return result_;
  }

  /* ********************************************************** */
  // option +
  public static boolean sequence(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    result_ = option(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!option(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "sequence");
        break;
      }
      offset_ = next_offset_;
    }
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), BNF_SEQUENCE)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(BNF_SEQUENCE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token | literal_expression | paren_expression
  static boolean simple(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = simple_0(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
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

  // (modifier* id '::=' )
  private static boolean simple_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0")) return false;
    return simple_0_0_0_0(builder_, level_ + 1);
  }

  // modifier* id '::='
  private static boolean simple_0_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = simple_0_0_0_0_0(builder_, level_ + 1);
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
  private static boolean simple_0_0_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple_0_0_0_0_0")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!modifier(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "simple_0_0_0_0_0");
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

  final static Parser attr_recover_until_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return attr_recover_until(builder_, level_ + 1);
    }
  };
  final static Parser rule_recover_until_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return rule_recover_until(builder_, level_ + 1);
    }
  };
}