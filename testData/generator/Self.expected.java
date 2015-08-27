// ---- Self.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.intellij.grammar.psi.BnfTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Self implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == BNF_ATTR) {
      result_ = attr(builder_, 0);
    }
    else if (root_ == BNF_ATTR_PATTERN) {
      result_ = attr_pattern(builder_, 0);
    }
    else if (root_ == BNF_ATTR_VALUE) {
      result_ = attr_value(builder_, 0);
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
    else if (root_ == BNF_LITERAL_EXPRESSION) {
      result_ = literal_expression(builder_, 0);
    }
    else if (root_ == BNF_MODIFIER) {
      result_ = modifier(builder_, 0);
    }
    else if (root_ == BNF_PAREN_EXPRESSION) {
      result_ = paren_expression(builder_, 0);
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
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return grammar(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(BNF_LITERAL_EXPRESSION, BNF_STRING_LITERAL_EXPRESSION),
    create_token_set_(BNF_CHOICE, BNF_EXPRESSION, BNF_LITERAL_EXPRESSION, BNF_PAREN_EXPRESSION,
      BNF_PREDICATE, BNF_QUANTIFIED, BNF_REFERENCE_OR_TOKEN, BNF_SEQUENCE,
      BNF_STRING_LITERAL_EXPRESSION),
  };

  /* ********************************************************** */
  // id attr_pattern? '=' attr_value ';'?
  public static boolean attr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BNF_ATTR, null);
    result_ = consumeToken(builder_, BNF_ID);
    pinned_ = result_; // pin = 1
    result_ = result_ && attr_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_OP_EQ);
    result_ = result_ && attr_value(builder_, level_ + 1);
    result_ = result_ && attr_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, attr_recover_until_parser_);
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
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    result_ = result_ && consumeToken(builder_, BNF_STRING);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    exit_section_(builder_, marker_, BNF_ATTR_PATTERN, result_);
    return result_;
  }

  /* ********************************************************** */
  // !'}'
  static boolean attr_recover_until(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_recover_until")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, BNF_RIGHT_BRACE);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // (reference_or_token | literal_expression) !'='
  public static boolean attr_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = attr_value_0(builder_, level_ + 1);
    result_ = result_ && attr_value_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, BNF_ATTR_VALUE, result_);
    return result_;
  }

  // reference_or_token | literal_expression
  private static boolean attr_value_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = reference_or_token(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !'='
  private static boolean attr_value_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attr_value_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, BNF_OP_EQ);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '{' attr* '}'
  public static boolean attrs(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "attrs")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_BRACE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BNF_ATTRS, null);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    pinned_ = result_; // pin = 1
    result_ = result_ && attrs_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // attr*
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
  // '{' sequence ('|' sequence)* '}' | sequence choice_tail*
  public static boolean choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, BNF_CHOICE, null);
    result_ = choice_0(builder_, level_ + 1);
    if (!result_) result_ = choice_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '{' sequence ('|' sequence)* '}'
  private static boolean choice_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_LEFT_BRACE);
    result_ = result_ && sequence(builder_, level_ + 1);
    result_ = result_ && choice_0_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ('|' sequence)*
  private static boolean choice_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!choice_0_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "choice_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // '|' sequence
  private static boolean choice_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_0_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_OP_OR);
    result_ = result_ && sequence(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // sequence choice_tail*
  private static boolean choice_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = sequence(builder_, level_ + 1);
    result_ = result_ && choice_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // choice_tail*
  private static boolean choice_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_1_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!choice_tail(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "choice_1_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  /* ********************************************************** */
  // '|' sequence
  static boolean choice_tail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_tail")) return false;
    if (!nextTokenIs(builder_, BNF_OP_OR)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, BNF_OP_OR);
    pinned_ = result_; // pin = 1
    result_ = result_ && sequence(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // choice?
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, BNF_EXPRESSION, null);
    choice(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // (attrs | rule) *
  static boolean grammar(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!grammar_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "grammar", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // attrs | rule
  private static boolean grammar_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = attrs(builder_, level_ + 1);
    if (!result_) result_ = rule(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // string_literal_expression | number
  public static boolean literal_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expression")) return false;
    if (!nextTokenIs(builder_, "", BNF_NUMBER, BNF_STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, BNF_LITERAL_EXPRESSION, null);
    result_ = string_literal_expression(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, BNF_NUMBER);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'private' | 'external' | 'wrapped'
  public static boolean modifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "modifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "private");
    if (!result_) result_ = consumeToken(builder_, "external");
    if (!result_) result_ = consumeToken(builder_, "wrapped");
    exit_section_(builder_, marker_, BNF_MODIFIER, result_);
    return result_;
  }

  /* ********************************************************** */
  // quantified | predicate
  static boolean option(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = quantified(builder_, level_ + 1);
    if (!result_) result_ = predicate(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '(' expression ')'
  public static boolean paren_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression")) return false;
    if (!nextTokenIs(builder_, BNF_LEFT_PAREN)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BNF_PAREN_EXPRESSION, null);
    result_ = consumeToken(builder_, BNF_LEFT_PAREN);
    pinned_ = result_; // pin = 1
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_PAREN);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // predicate_sign  simple
  public static boolean predicate(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate")) return false;
    if (!nextTokenIs(builder_, "", BNF_OP_NOT, BNF_OP_AND)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = predicate_sign(builder_, level_ + 1);
    result_ = result_ && simple(builder_, level_ + 1);
    exit_section_(builder_, marker_, BNF_PREDICATE, result_);
    return result_;
  }

  /* ********************************************************** */
  // '&' | '!'
  public static boolean predicate_sign(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predicate_sign")) return false;
    if (!nextTokenIs(builder_, "", BNF_OP_NOT, BNF_OP_AND)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_OP_AND);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_NOT);
    exit_section_(builder_, marker_, BNF_PREDICATE_SIGN, result_);
    return result_;
  }

  /* ********************************************************** */
  // '[' expression ']' | simple quantifier?
  public static boolean quantified(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, BNF_QUANTIFIED, null);
    result_ = quantified_0(builder_, level_ + 1);
    if (!result_) result_ = quantified_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '[' expression ']'
  private static boolean quantified_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_LEFT_BRACKET);
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_RIGHT_BRACKET);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // simple quantifier?
  private static boolean quantified_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantified_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = simple(builder_, level_ + 1);
    result_ = result_ && quantified_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
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
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BNF_OP_OPT);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ONEMORE);
    if (!result_) result_ = consumeToken(builder_, BNF_OP_ZEROMORE);
    exit_section_(builder_, marker_, BNF_QUANTIFIER, result_);
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
  // modifier* id '::=' expression attrs? ';'?
  public static boolean rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BNF_RULE, null);
    result_ = rule_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, BNF_ID);
    result_ = result_ && consumeToken(builder_, BNF_OP_IS);
    pinned_ = result_; // pin = 3
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = result_ && rule_4(builder_, level_ + 1);
    result_ = result_ && rule_5(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, rule_recover_until_parser_);
    return result_ || pinned_;
  }

  // modifier*
  private static boolean rule_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!modifier(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "rule_0", pos_)) break;
      pos_ = current_position_(builder_);
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
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, BNF_LEFT_BRACE);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // option +
  public static boolean sequence(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, BNF_SEQUENCE, null);
    result_ = option(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!option(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "sequence", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // !(modifier* id '::=' ) reference_or_token | literal_expression | paren_expression
  static boolean simple(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "simple")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = simple_0(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
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
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !simple_0_0_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
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