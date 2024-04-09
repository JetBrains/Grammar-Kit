// ---- JsonParser.java -----------------
// This is a generated file. Not intended for manual editing.
package fleet.com.intellij.json;

import fleet.com.intellij.lang.PsiBuilder;
import fleet.com.intellij.lang.PsiBuilder.Marker;
import static fleet.com.intellij.json.JsonElementTypes.*;
import static fleet.com.intellij.json.psi.JsonParserUtil.*;
import fleet.com.intellij.psi.tree.IElementType;
import fleet.com.intellij.lang.ASTNode;
import fleet.com.intellij.psi.tree.TokenSet;
import fleet.com.intellij.lang.PsiParser;
import fleet.com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class JsonParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    throw new IllegalStateException("Use parseLight instead");
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return json(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ARRAY, BOOLEAN_LITERAL, LITERAL, NULL_LITERAL,
      NUMBER_LITERAL, OBJECT, REFERENCE_EXPRESSION, STRING_LITERAL,
      VALUE),
  };

  /* ********************************************************** */
  // '[' array_element* ']'
  public static boolean array(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "array")) return false;
    if (!nextTokenIs(builder_, L_BRACKET)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ARRAY, null);
    result_ = consumeToken(builder_, L_BRACKET);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, array_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, R_BRACKET) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // array_element*
  private static boolean array_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "array_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!array_element(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "array_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // value (','|&']')
  static boolean array_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "array_element")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = value(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && array_element_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, JsonParser::not_bracket_or_next_value);
    return result_ || pinned_;
  }

  // ','|&']'
  private static boolean array_element_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "array_element_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    if (!result_) result_ = array_element_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &']'
  private static boolean array_element_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "array_element_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, R_BRACKET);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean boolean_literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "boolean_literal")) return false;
    if (!nextTokenIs(builder_, "<boolean literal>", FALSE, TRUE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BOOLEAN_LITERAL, "<boolean literal>");
    result_ = consumeToken(builder_, TRUE);
    if (!result_) result_ = consumeToken(builder_, FALSE);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // value*
  static boolean json(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "json")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!value(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "json", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // string_literal | number_literal | boolean_literal | null_literal
  public static boolean literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, LITERAL, "<literal>");
    result_ = string_literal(builder_, level_ + 1);
    if (!result_) result_ = number_literal(builder_, level_ + 1);
    if (!result_) result_ = boolean_literal(builder_, level_ + 1);
    if (!result_) result_ = null_literal(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // !('}'|value)
  static boolean not_brace_or_next_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_brace_or_next_value")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !not_brace_or_next_value_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '}'|value
  private static boolean not_brace_or_next_value_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_brace_or_next_value_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, R_CURLY);
    if (!result_) result_ = value(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // !(']'|value)
  static boolean not_bracket_or_next_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_bracket_or_next_value")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !not_bracket_or_next_value_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ']'|value
  private static boolean not_bracket_or_next_value_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_bracket_or_next_value_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, R_BRACKET);
    if (!result_) result_ = value(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // NULL
  public static boolean null_literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "null_literal")) return false;
    if (!nextTokenIs(builder_, NULL)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NULL);
    exit_section_(builder_, marker_, NULL_LITERAL, result_);
    return result_;
  }

  /* ********************************************************** */
  // NUMBER
  public static boolean number_literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "number_literal")) return false;
    if (!nextTokenIs(builder_, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, marker_, NUMBER_LITERAL, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' object_element* '}'
  public static boolean object(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "object")) return false;
    if (!nextTokenIs(builder_, L_CURLY)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, OBJECT, null);
    result_ = consumeToken(builder_, L_CURLY);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, object_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, R_CURLY) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // object_element*
  private static boolean object_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "object_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!object_element(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "object_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // property (','|&'}')
  static boolean object_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "object_element")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = property(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && object_element_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, JsonParser::not_brace_or_next_value);
    return result_ || pinned_;
  }

  // ','|&'}'
  private static boolean object_element_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "object_element_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COMMA);
    if (!result_) result_ = object_element_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &'}'
  private static boolean object_element_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "object_element_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = consumeToken(builder_, R_CURLY);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // property_name (':' property_value)
  public static boolean property(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "property")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PROPERTY, "<property>");
    result_ = property_name(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && property_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ':' property_value
  private static boolean property_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "property_1")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, COLON);
    pinned_ = result_; // pin = 1
    result_ = result_ && property_value(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // literal | reference_expression
  static boolean property_name(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "property_name")) return false;
    boolean result_;
    result_ = literal(builder_, level_ + 1);
    if (!result_) result_ = reference_expression(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // value
  static boolean property_value(PsiBuilder builder_, int level_) {
    return value(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean reference_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "reference_expression")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    exit_section_(builder_, marker_, REFERENCE_EXPRESSION, result_);
    return result_;
  }

  /* ********************************************************** */
  // SINGLE_QUOTED_STRING | DOUBLE_QUOTED_STRING
  public static boolean string_literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string_literal")) return false;
    if (!nextTokenIs(builder_, "<string literal>", DOUBLE_QUOTED_STRING, SINGLE_QUOTED_STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRING_LITERAL, "<string literal>");
    result_ = consumeToken(builder_, SINGLE_QUOTED_STRING);
    if (!result_) result_ = consumeToken(builder_, DOUBLE_QUOTED_STRING);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // object | array | literal | reference_expression
  public static boolean value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "value")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, VALUE, "<value>");
    result_ = object(builder_, level_ + 1);
    if (!result_) result_ = array(builder_, level_ + 1);
    if (!result_) result_ = literal(builder_, level_ + 1);
    if (!result_) result_ = reference_expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

}