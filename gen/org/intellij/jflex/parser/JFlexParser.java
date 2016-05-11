/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.jflex.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.intellij.jflex.psi.JFlexTypes.*;
import static org.intellij.jflex.parser.JFlexParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class JFlexParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType type, PsiBuilder builder) {
    parseLight(type, builder);
    return builder.getTreeBuilt();
  }

  public void parseLight(IElementType type, PsiBuilder builder) {
    boolean result;
    builder = adapt_builder_(type, builder, this, EXTENDS_SETS_);
    Marker marker = enter_section_(builder, 0, _COLLAPSE_, null);
    if (type == FLEX_DECLARATIONS_SECTION) {
      result = declarations_section(builder, 0);
    }
    else if (type == FLEX_EXPRESSION) {
      result = expression(builder, 0, -1);
    }
    else if (type == FLEX_JAVA_CODE) {
      result = java_code(builder, 0);
    }
    else if (type == FLEX_JAVA_TYPE) {
      result = java_type(builder, 0);
    }
    else if (type == FLEX_LEXICAL_RULES_SECTION) {
      result = lexical_rules_section(builder, 0);
    }
    else if (type == FLEX_LOOK_AHEAD) {
      result = look_ahead(builder, 0);
    }
    else if (type == FLEX_MACRO_DEFINITION) {
      result = macro_definition(builder, 0);
    }
    else if (type == FLEX_MACRO_REFERENCE) {
      result = macro_reference(builder, 0);
    }
    else if (type == FLEX_OPTION) {
      result = option(builder, 0);
    }
    else if (type == FLEX_RULE) {
      result = rule(builder, 0);
    }
    else if (type == FLEX_STATE_DECLARATION) {
      result = state_declaration(builder, 0);
    }
    else if (type == FLEX_STATE_DEFINITION) {
      result = state_definition(builder, 0);
    }
    else if (type == FLEX_STATE_REFERENCE) {
      result = state_reference(builder, 0);
    }
    else if (type == FLEX_USER_CODE_SECTION) {
      result = user_code_section(builder, 0);
    }
    else if (type == FLEX_USER_VALUE) {
      result = user_value(builder, 0);
    }
    else {
      result = parse_root_(type, builder, 0);
    }
    exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType type, PsiBuilder builder, int level) {
    return flex_file(builder, level + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(FLEX_CHOICE_EXPRESSION, FLEX_CLASS_EXPRESSION, FLEX_EXPRESSION, FLEX_LITERAL_EXPRESSION,
      FLEX_MACRO_REF_EXPRESSION, FLEX_NOT_EXPRESSION, FLEX_PAREN_EXPRESSION, FLEX_PREDEFINED_CLASS_EXPRESSION,
      FLEX_QUANTIFIER_EXPRESSION, FLEX_SEQUENCE_EXPRESSION),
  };

  /* ********************************************************** */
  // '{' java_class_statements '}' | '|'
  public static boolean action(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "action")) return false;
    if (!nextTokenIs(builder, "<action>", FLEX_BRACE1, FLEX_OR)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_JAVA_CODE, "<action>");
    result = action_0(builder, level + 1);
    if (!result) result = consumeToken(builder, FLEX_OR);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '{' java_class_statements '}'
  private static boolean action_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "action_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_BRACE1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_class_statements(builder, level + 1));
    result = pinned && consumeToken(builder, FLEX_BRACE2) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // escaped_char | char | number | '-' | '=' | '<' | '>' | ','
  //   | ']'
  static boolean allowed_chars(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "allowed_chars")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ESCAPED_CHAR);
    if (!result) result = consumeToken(builder, FLEX_CHAR);
    if (!result) result = consumeToken(builder, FLEX_NUMBER);
    if (!result) result = consumeToken(builder, FLEX_DASH);
    if (!result) result = consumeToken(builder, FLEX_EQ);
    if (!result) result = consumeToken(builder, FLEX_ANGLE1);
    if (!result) result = consumeToken(builder, FLEX_ANGLE2);
    if (!result) result = consumeToken(builder, FLEX_COMMA);
    if (!result) result = consumeToken(builder, FLEX_BRACK2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // char | escaped_char
  static boolean class_char(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_char")) return false;
    if (!nextTokenIs(builder, "", FLEX_CHAR, FLEX_ESCAPED_CHAR)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_CHAR);
    if (!result) result = consumeToken(builder, FLEX_ESCAPED_CHAR);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // macro_definition | state_declaration | option
  static boolean declaration(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declaration")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = macro_definition(builder, level + 1);
    if (!result) result = state_declaration(builder, level + 1);
    if (!result) result = option(builder, level + 1);
    exit_section_(builder, level, marker, result, false, declaration_recover_parser_);
    return result;
  }

  /* ********************************************************** */
  // !(<<is_percent>> | id '=') section_recover
  static boolean declaration_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declaration_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = declaration_recover_0(builder, level + 1);
    result = result && section_recover(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !(<<is_percent>> | id '=')
  private static boolean declaration_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declaration_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !declaration_recover_0_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // <<is_percent>> | id '='
  private static boolean declaration_recover_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declaration_recover_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = is_percent(builder, level + 1);
    if (!result) result = declaration_recover_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // id '='
  private static boolean declaration_recover_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declaration_recover_0_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ID);
    result = result && consumeToken(builder, FLEX_EQ);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // [] (declaration) *
  public static boolean declarations_section(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_DECLARATIONS_SECTION, "<declarations section>");
    result = declarations_section_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && declarations_section_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, section_recover_parser_);
    return result || pinned;
  }

  // []
  private static boolean declarations_section_0(PsiBuilder builder, int level) {
    return true;
  }

  // (declaration) *
  private static boolean declarations_section_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section_1")) return false;
    int pos = current_position_(builder);
    while (true) {
      if (!declarations_section_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "declarations_section_1", pos)) break;
      pos = current_position_(builder);
    }
    return true;
  }

  // (declaration)
  private static boolean declarations_section_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = declaration(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // []
  //   user_code_section
  //   section_div
  //   declarations_section
  //   section_div
  //   lexical_rules_section
  static boolean flex_file(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "flex_file")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = flex_file_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, user_code_section(builder, level + 1));
    result = pinned && report_error_(builder, section_div(builder, level + 1)) && result;
    result = pinned && report_error_(builder, declarations_section(builder, level + 1)) && result;
    result = pinned && report_error_(builder, section_div(builder, level + 1)) && result;
    result = pinned && lexical_rules_section(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // []
  private static boolean flex_file_0(PsiBuilder builder, int level) {
    return true;
  }

  /* ********************************************************** */
  // <<anything !('}' | '<' id '>')>>
  static boolean java_class_statements(PsiBuilder builder, int level) {
    return anything(builder, level + 1, java_class_statements_0_0_parser_);
  }

  // !('}' | '<' id '>')
  private static boolean java_class_statements_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_class_statements_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !java_class_statements_0_0_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '}' | '<' id '>'
  private static boolean java_class_statements_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_class_statements_0_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_BRACE2);
    if (!result) result = java_class_statements_0_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '<' id '>'
  private static boolean java_class_statements_0_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_class_statements_0_0_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ANGLE1);
    result = result && consumeToken(builder, FLEX_ID);
    result = result && consumeToken(builder, FLEX_ANGLE2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // <<anything2 (&java | !<<is_percent>>)>>
  public static boolean java_code(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_code")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, FLEX_JAVA_CODE, "<java code>");
    result = anything2(builder, level + 1, java_code_0_0_parser_);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // &java | !<<is_percent>>
  private static boolean java_code_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_code_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = java_code_0_0_0(builder, level + 1);
    if (!result) result = java_code_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // &java
  private static boolean java_code_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_code_0_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_);
    result = consumeToken(builder, FLEX_JAVA);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // !<<is_percent>>
  private static boolean java_code_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_code_0_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !is_percent(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // id ( safe_dot id ) *
  public static boolean java_type(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_type")) return false;
    if (!nextTokenIs(builder, FLEX_ID)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_JAVA_TYPE, null);
    result = consumeToken(builder, FLEX_ID);
    pinned = result; // pin = 1
    result = result && java_type_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // ( safe_dot id ) *
  private static boolean java_type_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_type_1")) return false;
    int pos = current_position_(builder);
    while (true) {
      if (!java_type_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "java_type_1", pos)) break;
      pos = current_position_(builder);
    }
    return true;
  }

  // safe_dot id
  private static boolean java_type_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_type_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = safe_dot(builder, level + 1);
    pinned = result; // pin = 1
    result = result && consumeToken(builder, FLEX_ID);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // [java_type (',' java_type) *]
  static boolean java_type_list(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_type_list")) return false;
    Marker marker = enter_section_(builder, level, _NONE_);
    java_type_list_0(builder, level + 1);
    exit_section_(builder, level, marker, true, false, declaration_recover_parser_);
    return true;
  }

  // java_type (',' java_type) *
  private static boolean java_type_list_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_type_list_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = java_type(builder, level + 1);
    pinned = result; // pin = 1
    result = result && java_type_list_0_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (',' java_type) *
  private static boolean java_type_list_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_type_list_0_1")) return false;
    int pos = current_position_(builder);
    while (true) {
      if (!java_type_list_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "java_type_list_0_1", pos)) break;
      pos = current_position_(builder);
    }
    return true;
  }

  // ',' java_type
  private static boolean java_type_list_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_type_list_0_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_COMMA);
    pinned = result; // pin = 1
    result = result && java_type(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // [] rule_group_item +
  public static boolean lexical_rules_section(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "lexical_rules_section")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_LEXICAL_RULES_SECTION, "<lexical rules section>");
    result = lexical_rules_section_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && lexical_rules_section_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, section_recover_parser_);
    return result || pinned;
  }

  // []
  private static boolean lexical_rules_section_0(PsiBuilder builder, int level) {
    return true;
  }

  // rule_group_item +
  private static boolean lexical_rules_section_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "lexical_rules_section_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = rule_group_item(builder, level + 1);
    int pos = current_position_(builder);
    while (result) {
      if (!rule_group_item(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "lexical_rules_section_1", pos)) break;
      pos = current_position_(builder);
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // new_line <<p>> new_line
  static boolean line(PsiBuilder builder, int level, final Parser aP) {
    if (!recursion_guard_(builder, level, "line")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = new_line(builder, level + 1);
    result = result && aP.parse(builder, level);
    pinned = result; // pin = 2
    result = result && new_line(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '$' | '/' expression
  public static boolean look_ahead(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "look_ahead")) return false;
    if (!nextTokenIs(builder, "<look ahead>", FLEX_DOLLAR, FLEX_SLASH2)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_LOOK_AHEAD, "<look ahead>");
    result = consumeToken(builder, FLEX_DOLLAR);
    if (!result) result = look_ahead_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '/' expression
  private static boolean look_ahead_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "look_ahead_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_SLASH2);
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // new_line id '=' expression
  public static boolean macro_definition(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_definition")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_MACRO_DEFINITION, "<macro definition>");
    result = new_line(builder, level + 1);
    result = result && consumeToken(builder, FLEX_ID);
    result = result && consumeToken(builder, FLEX_EQ);
    pinned = result; // pin = 3
    result = result && expression(builder, level + 1, -1);
    exit_section_(builder, level, marker, result, pinned, macro_definition_recover_parser_);
    return result || pinned;
  }

  /* ********************************************************** */
  // !(new_line id '=' | '%%' | <<is_percent>>)
  static boolean macro_definition_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_definition_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !macro_definition_recover_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // new_line id '=' | '%%' | <<is_percent>>
  private static boolean macro_definition_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_definition_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = macro_definition_recover_0_0(builder, level + 1);
    if (!result) result = consumeToken(builder, FLEX_TWO_PERCS);
    if (!result) result = is_percent(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // new_line id '='
  private static boolean macro_definition_recover_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_definition_recover_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = new_line(builder, level + 1);
    result = result && consumeToken(builder, FLEX_ID);
    result = result && consumeToken(builder, FLEX_EQ);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // id
  public static boolean macro_reference(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_reference")) return false;
    if (!nextTokenIs(builder, FLEX_ID)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ID);
    exit_section_(builder, marker, FLEX_MACRO_REFERENCE, result);
    return result;
  }

  /* ********************************************************** */
  // &<<is_new_line>>
  static boolean new_line(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "new_line")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_);
    result = is_new_line(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
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
  //   | option_pack
  //   | option_7bit
  //   | option_full
  //   | option_unicode
  //   | option_ignore_case
  //   | option_count_char
  //   | option_count_line
  //   | option_count_column
  //   | option_obsolete
  public static boolean option(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _COLLAPSE_, FLEX_OPTION, "<option>");
    result = option_class(builder, level + 1);
    if (!result) result = option_implements(builder, level + 1);
    if (!result) result = option_extends(builder, level + 1);
    if (!result) result = option_public(builder, level + 1);
    if (!result) result = option_final(builder, level + 1);
    if (!result) result = option_abstract(builder, level + 1);
    if (!result) result = option_api_private(builder, level + 1);
    if (!result) result = option_user_code(builder, level + 1);
    if (!result) result = option_init(builder, level + 1);
    if (!result) result = option_init_throw(builder, level + 1);
    if (!result) result = option_ctor_arg(builder, level + 1);
    if (!result) result = option_scan_error(builder, level + 1);
    if (!result) result = option_buffer_size(builder, level + 1);
    if (!result) result = option_include(builder, level + 1);
    if (!result) result = option_function(builder, level + 1);
    if (!result) result = option_integer(builder, level + 1);
    if (!result) result = option_intwrap(builder, level + 1);
    if (!result) result = option_type(builder, level + 1);
    if (!result) result = option_yylexthrow(builder, level + 1);
    if (!result) result = option_eof_val(builder, level + 1);
    if (!result) result = option_eof(builder, level + 1);
    if (!result) result = option_eof_throw(builder, level + 1);
    if (!result) result = option_eof_close(builder, level + 1);
    if (!result) result = option_debug(builder, level + 1);
    if (!result) result = option_standalone(builder, level + 1);
    if (!result) result = option_cup(builder, level + 1);
    if (!result) result = option_cup_sym(builder, level + 1);
    if (!result) result = option_cup_debug(builder, level + 1);
    if (!result) result = option_byacc(builder, level + 1);
    if (!result) result = option_switch(builder, level + 1);
    if (!result) result = option_table(builder, level + 1);
    if (!result) result = option_pack(builder, level + 1);
    if (!result) result = option_7bit(builder, level + 1);
    if (!result) result = option_full(builder, level + 1);
    if (!result) result = option_unicode(builder, level + 1);
    if (!result) result = option_ignore_case(builder, level + 1);
    if (!result) result = option_count_char(builder, level + 1);
    if (!result) result = option_count_line(builder, level + 1);
    if (!result) result = option_count_column(builder, level + 1);
    if (!result) result = option_obsolete(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%7bit'
  public static boolean option_7bit(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_7bit")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_7BIT)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_7BIT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%abstract'
  public static boolean option_abstract(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_abstract")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_ABSTRACT)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_ABSTRACT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%apiprivate'
  public static boolean option_api_private(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_api_private")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_APIPRIVATE)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_APIPRIVATE);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%buffer' number
  public static boolean option_buffer_size(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_buffer_size")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_BUFFER)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_BUFFER);
    pinned = result; // pin = 1
    result = result && consumeToken(builder, FLEX_NUMBER);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%byacc'
  public static boolean option_byacc(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_byacc")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_BYACC)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_BYACC);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%class' java_type
  public static boolean option_class(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_class")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_CLASS)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_CLASS);
    pinned = result; // pin = 1
    result = result && java_type(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%char'
  public static boolean option_count_char(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_count_char")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_CHAR)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_CHAR);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%column'
  public static boolean option_count_column(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_count_column")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_COLUMN)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_COLUMN);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%line'
  public static boolean option_count_line(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_count_line")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_LINE)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_LINE);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%ctorarg' java_type id
  public static boolean option_ctor_arg(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_ctor_arg")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_CTORARG)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_CTORARG);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_type(builder, level + 1));
    result = pinned && consumeToken(builder, FLEX_ID) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%cup'
  public static boolean option_cup(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_cup")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_CUP)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_CUP);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%cupdebug'
  public static boolean option_cup_debug(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_cup_debug")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_CUPDEBUG)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_CUPDEBUG);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%cupsym' java_type
  public static boolean option_cup_sym(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_cup_sym")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_CUPSYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_CUPSYM);
    pinned = result; // pin = 1
    result = result && java_type(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%debug'
  public static boolean option_debug(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_debug")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_DEBUG)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_DEBUG);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // <<line '%eof{'>> java_code <<line '%eof}'>>
  public static boolean option_eof(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_eof")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_EOF1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(builder, level + 1, opt_eof1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_code(builder, level + 1));
    result = pinned && line(builder, level + 1, opt_eof2_parser_) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%eofclose' ['false']
  public static boolean option_eof_close(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_eof_close")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_EOFCLOSE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_EOFCLOSE);
    pinned = result; // pin = 1
    result = result && option_eof_close_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // ['false']
  private static boolean option_eof_close_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_eof_close_1")) return false;
    consumeToken(builder, "false");
    return true;
  }

  /* ********************************************************** */
  // '%eofthrow' java_type_list | <<line '%eofthrow{'>> java_type_list <<line '%eofthrow}'>>
  public static boolean option_eof_throw(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_eof_throw")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_EOFTHROW, FLEX_OPT_EOFTHROW1)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = option_eof_throw_0(builder, level + 1);
    if (!result) result = option_eof_throw_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '%eofthrow' java_type_list
  private static boolean option_eof_throw_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_eof_throw_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_OPT_EOFTHROW);
    pinned = result; // pin = 1
    result = result && java_type_list(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // <<line '%eofthrow{'>> java_type_list <<line '%eofthrow}'>>
  private static boolean option_eof_throw_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_eof_throw_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = line(builder, level + 1, opt_eofthrow1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_type_list(builder, level + 1));
    result = pinned && line(builder, level + 1, opt_eofthrow2_parser_) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // <<line '%eofval{'>> java_code <<line '%eofval}'>>
  public static boolean option_eof_val(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_eof_val")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_EOFVAL1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(builder, level + 1, opt_eofval1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_code(builder, level + 1));
    result = pinned && line(builder, level + 1, opt_eofval2_parser_) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%extends' java_type
  public static boolean option_extends(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_extends")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_EXTENDS)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_EXTENDS);
    pinned = result; // pin = 1
    result = result && java_type(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%final'
  public static boolean option_final(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_final")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_FINAL)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_FINAL);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%full' | '%8bit'
  public static boolean option_full(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_full")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_8BIT, FLEX_OPT_FULL)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_FULL);
    if (!result) result = consumeToken(builder, FLEX_OPT_8BIT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%function' id
  public static boolean option_function(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_function")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_FUNCTION)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_FUNCTION);
    pinned = result; // pin = 1
    result = result && consumeToken(builder, FLEX_ID);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%caseless' | '%ignorecase'
  public static boolean option_ignore_case(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_ignore_case")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_CASELESS, FLEX_OPT_IGNORECASE)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_CASELESS);
    if (!result) result = consumeToken(builder, FLEX_OPT_IGNORECASE);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%implements' java_type_list
  public static boolean option_implements(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_implements")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_IMPLEMENTS)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_IMPLEMENTS);
    pinned = result; // pin = 1
    result = result && java_type_list(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%include' user_value
  public static boolean option_include(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_include")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_INCLUDE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_INCLUDE);
    pinned = result; // pin = 1
    result = result && user_value(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // <<line '%init{'>> java_code <<line '%init}'>>
  public static boolean option_init(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_init")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_INIT1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(builder, level + 1, opt_init1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_code(builder, level + 1));
    result = pinned && line(builder, level + 1, opt_init2_parser_) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%initthrow' java_type_list | <<line '%initthrow{'>> java_type_list <<line '%initthrow}'>>
  public static boolean option_init_throw(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_init_throw")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_INITTHROW, FLEX_OPT_INITTHROW1)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = option_init_throw_0(builder, level + 1);
    if (!result) result = option_init_throw_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '%initthrow' java_type_list
  private static boolean option_init_throw_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_init_throw_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_OPT_INITTHROW);
    pinned = result; // pin = 1
    result = result && java_type_list(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // <<line '%initthrow{'>> java_type_list <<line '%initthrow}'>>
  private static boolean option_init_throw_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_init_throw_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = line(builder, level + 1, opt_initthrow1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_type_list(builder, level + 1));
    result = pinned && line(builder, level + 1, opt_initthrow2_parser_) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%integer' | '%int'
  public static boolean option_integer(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_integer")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_INT, FLEX_OPT_INTEGER)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_INTEGER);
    if (!result) result = consumeToken(builder, FLEX_OPT_INT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%intwrap'
  public static boolean option_intwrap(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_intwrap")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_INTWRAP)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_INTWRAP);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%notunix' | '%yyeof'
  public static boolean option_obsolete(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_obsolete")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_NOTUNIX, FLEX_OPT_YYEOF)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_NOTUNIX);
    if (!result) result = consumeToken(builder, FLEX_OPT_YYEOF);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%pack'
  public static boolean option_pack(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_pack")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_PACK)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_PACK);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%public'
  public static boolean option_public(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_public")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_PUBLIC)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_PUBLIC);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%scanerror' java_type
  public static boolean option_scan_error(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_scan_error")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_SCANERROR)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_SCANERROR);
    pinned = result; // pin = 1
    result = result && java_type(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%standalone'
  public static boolean option_standalone(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_standalone")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_STANDALONE)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_STANDALONE);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%switch'
  public static boolean option_switch(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_switch")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_SWITCH)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_SWITCH);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%table'
  public static boolean option_table(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_table")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_TABLE)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_TABLE);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%type' java_type
  public static boolean option_type(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_type")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_TYPE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_TYPE);
    pinned = result; // pin = 1
    result = result && java_type(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%unicode' | '%16bit'
  public static boolean option_unicode(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_unicode")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT16BIT, FLEX_OPT_UNICODE)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_UNICODE);
    if (!result) result = consumeToken(builder, FLEX_OPT16BIT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // <<line '%{'>> java_code <<line '%}'>>
  public static boolean option_user_code(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_user_code")) return false;
    if (!nextTokenIs(builder, FLEX_OPT_CODE1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(builder, level + 1, opt_code1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_code(builder, level + 1));
    result = pinned && line(builder, level + 1, opt_code2_parser_) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%yylexthrow' java_type_list | <<line '%yylexthrow{'>> java_type_list <<line '%yylexthrow}'>>
  public static boolean option_yylexthrow(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_yylexthrow")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_YYLEXTHROW, FLEX_OPT_YYLEXTHROW1)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = option_yylexthrow_0(builder, level + 1);
    if (!result) result = option_yylexthrow_1(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '%yylexthrow' java_type_list
  private static boolean option_yylexthrow_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_yylexthrow_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_OPT_YYLEXTHROW);
    pinned = result; // pin = 1
    result = result && java_type_list(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // <<line '%yylexthrow{'>> java_type_list <<line '%yylexthrow}'>>
  private static boolean option_yylexthrow_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_yylexthrow_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = line(builder, level + 1, opt_yylexthrow1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(builder, java_type_list(builder, level + 1));
    result = pinned && line(builder, level + 1, opt_yylexthrow2_parser_) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '[:jletter:]'
  //   | '[:jletterdigit:]'
  //   | '[:letter:]'
  //   | '[:digit:]'
  //   | '[:uppercase:]'
  //   | '[:lowercase:]'
  static boolean predefined_class(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "predefined_class")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, null, "<char class>");
    result = consumeToken(builder, FLEX_CLASS_JL);
    if (!result) result = consumeToken(builder, FLEX_CLASS_JLD);
    if (!result) result = consumeToken(builder, FLEX_CLASS_L);
    if (!result) result = consumeToken(builder, FLEX_CLASS_D);
    if (!result) result = consumeToken(builder, FLEX_CLASS_LU);
    if (!result) result = consumeToken(builder, FLEX_CLASS_LL);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // state_list (rule_group | rule_tail ) | rule_tail
  public static boolean rule(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_RULE, "<rule>");
    result = rule_0(builder, level + 1);
    if (!result) result = rule_tail(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // state_list (rule_group | rule_tail )
  private static boolean rule_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = state_list(builder, level + 1);
    pinned = result; // pin = 1
    result = result && rule_0_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // rule_group | rule_tail
  private static boolean rule_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = rule_group(builder, level + 1);
    if (!result) result = rule_tail(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // !('{' id '}') '{' rule_group_item + '}'
  static boolean rule_group(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_group")) return false;
    if (!nextTokenIs(builder, FLEX_BRACE1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = rule_group_0(builder, level + 1);
    result = result && consumeToken(builder, FLEX_BRACE1);
    pinned = result; // pin = 2
    result = result && report_error_(builder, rule_group_2(builder, level + 1));
    result = pinned && consumeToken(builder, FLEX_BRACE2) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // !('{' id '}')
  private static boolean rule_group_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_group_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !rule_group_0_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '{' id '}'
  private static boolean rule_group_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_group_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_BRACE1);
    result = result && consumeToken(builder, FLEX_ID);
    result = result && consumeToken(builder, FLEX_BRACE2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // rule_group_item +
  private static boolean rule_group_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_group_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = rule_group_item(builder, level + 1);
    int pos = current_position_(builder);
    while (result) {
      if (!rule_group_item(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "rule_group_2", pos)) break;
      pos = current_position_(builder);
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // option_include | rule
  static boolean rule_group_item(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_group_item")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = option_include(builder, level + 1);
    if (!result) result = rule(builder, level + 1);
    exit_section_(builder, level, marker, result, false, rule_recover_parser_);
    return result;
  }

  /* ********************************************************** */
  // !('}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group)
  static boolean rule_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !rule_recover_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group
  private static boolean rule_recover_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_BRACE2);
    if (!result) result = consumeToken(builder, FLEX_DOT);
    if (!result) result = consumeToken(builder, FLEX_ANGLE1);
    if (!result) result = consumeToken(builder, FLEX_EOF);
    if (!result) result = consumeToken(builder, FLEX_ROOF);
    if (!result) result = new_line(builder, level + 1);
    if (!result) result = expression(builder, level + 1, -2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // rule_tail1 | rule_tail2
  static boolean rule_tail(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_tail")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = rule_tail1(builder, level + 1);
    if (!result) result = rule_tail2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // '<<EOF>>' action
  static boolean rule_tail1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_tail1")) return false;
    if (!nextTokenIs(builder, FLEX_EOF)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_EOF);
    pinned = result; // pin = 1
    result = result && action(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // ['^'] expression look_ahead? action
  static boolean rule_tail2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_tail2")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = rule_tail2_0(builder, level + 1);
    result = result && expression(builder, level + 1, -1);
    pinned = result; // pin = 2
    result = result && report_error_(builder, rule_tail2_2(builder, level + 1));
    result = pinned && action(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // ['^']
  private static boolean rule_tail2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_tail2_0")) return false;
    consumeToken(builder, FLEX_ROOF);
    return true;
  }

  // look_ahead?
  private static boolean rule_tail2_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_tail2_2")) return false;
    look_ahead(builder, level + 1);
    return true;
  }

  /* ********************************************************** */
  // '.' !'*'
  static boolean safe_dot(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "safe_dot")) return false;
    if (!nextTokenIs(builder, FLEX_DOT)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_DOT);
    result = result && safe_dot_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !'*'
  private static boolean safe_dot_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "safe_dot_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !consumeToken(builder, FLEX_STAR);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // <<line '%%'>>
  static boolean section_div(PsiBuilder builder, int level) {
    return line(builder, level + 1, two_percs_parser_);
  }

  /* ********************************************************** */
  // !'%%'
  static boolean section_recover(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "section_recover")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !consumeToken(builder, FLEX_TWO_PERCS);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // &( '(' | '!' | '~' | '[' | ']'
  //   | string | predefined_class | '.' | '{' id '}'
  //   | !new_line (id | allowed_chars) )
  public static boolean sequence_op(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_, null, "<expression>");
    result = sequence_op_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '(' | '!' | '~' | '[' | ']'
  //   | string | predefined_class | '.' | '{' id '}'
  //   | !new_line (id | allowed_chars)
  private static boolean sequence_op_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_PAREN1);
    if (!result) result = consumeToken(builder, FLEX_NOT);
    if (!result) result = consumeToken(builder, FLEX_NOT2);
    if (!result) result = consumeToken(builder, FLEX_BRACK1);
    if (!result) result = consumeToken(builder, FLEX_BRACK2);
    if (!result) result = consumeToken(builder, FLEX_STRING);
    if (!result) result = predefined_class(builder, level + 1);
    if (!result) result = consumeToken(builder, FLEX_DOT);
    if (!result) result = sequence_op_0_8(builder, level + 1);
    if (!result) result = sequence_op_0_9(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '{' id '}'
  private static boolean sequence_op_0_8(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0_8")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_BRACE1);
    result = result && consumeToken(builder, FLEX_ID);
    result = result && consumeToken(builder, FLEX_BRACE2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !new_line (id | allowed_chars)
  private static boolean sequence_op_0_9(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0_9")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = sequence_op_0_9_0(builder, level + 1);
    result = result && sequence_op_0_9_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !new_line
  private static boolean sequence_op_0_9_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0_9_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !new_line(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // id | allowed_chars
  private static boolean sequence_op_0_9_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0_9_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ID);
    if (!result) result = allowed_chars(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // ('%state' | '%s' | '%xstate' | '%x') state_definition ((','? !(id '=')) state_definition) * ','?
  public static boolean state_declaration(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_STATE_DECLARATION, "<state declaration>");
    result = state_declaration_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, state_definition(builder, level + 1));
    result = pinned && report_error_(builder, state_declaration_2(builder, level + 1)) && result;
    result = pinned && state_declaration_3(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // '%state' | '%s' | '%xstate' | '%x'
  private static boolean state_declaration_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_OPT_STATE);
    if (!result) result = consumeToken(builder, FLEX_OPT_S);
    if (!result) result = consumeToken(builder, FLEX_OPT_XSTATE);
    if (!result) result = consumeToken(builder, FLEX_OPT_X);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ((','? !(id '=')) state_definition) *
  private static boolean state_declaration_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_2")) return false;
    int pos = current_position_(builder);
    while (true) {
      if (!state_declaration_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "state_declaration_2", pos)) break;
      pos = current_position_(builder);
    }
    return true;
  }

  // (','? !(id '=')) state_definition
  private static boolean state_declaration_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = state_declaration_2_0_0(builder, level + 1);
    result = result && state_definition(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ','? !(id '=')
  private static boolean state_declaration_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_2_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = state_declaration_2_0_0_0(builder, level + 1);
    result = result && state_declaration_2_0_0_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ','?
  private static boolean state_declaration_2_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_2_0_0_0")) return false;
    consumeToken(builder, FLEX_COMMA);
    return true;
  }

  // !(id '=')
  private static boolean state_declaration_2_0_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_2_0_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !state_declaration_2_0_0_1_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // id '='
  private static boolean state_declaration_2_0_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_2_0_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ID);
    result = result && consumeToken(builder, FLEX_EQ);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ','?
  private static boolean state_declaration_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_3")) return false;
    consumeToken(builder, FLEX_COMMA);
    return true;
  }

  /* ********************************************************** */
  // id
  public static boolean state_definition(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_definition")) return false;
    if (!nextTokenIs(builder, FLEX_ID)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ID);
    exit_section_(builder, marker, FLEX_STATE_DEFINITION, result);
    return result;
  }

  /* ********************************************************** */
  // '<' state_reference (',' state_reference) * '>'
  static boolean state_list(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_list")) return false;
    if (!nextTokenIs(builder, FLEX_ANGLE1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_ANGLE1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, state_reference(builder, level + 1));
    result = pinned && report_error_(builder, state_list_2(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, FLEX_ANGLE2) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (',' state_reference) *
  private static boolean state_list_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_list_2")) return false;
    int pos = current_position_(builder);
    while (true) {
      if (!state_list_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "state_list_2", pos)) break;
      pos = current_position_(builder);
    }
    return true;
  }

  // ',' state_reference
  private static boolean state_list_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_list_2_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_COMMA);
    pinned = result; // pin = 1
    result = result && state_reference(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // id
  public static boolean state_reference(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_reference")) return false;
    if (!nextTokenIs(builder, FLEX_ID)) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_ID);
    exit_section_(builder, marker, FLEX_STATE_REFERENCE, result);
    return result;
  }

  /* ********************************************************** */
  // [] java_code
  public static boolean user_code_section(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "user_code_section")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_USER_CODE_SECTION, "<user code section>");
    result = user_code_section_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && java_code(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, section_recover_parser_);
    return result || pinned;
  }

  // []
  private static boolean user_code_section_0(PsiBuilder builder, int level) {
    return true;
  }

  /* ********************************************************** */
  // <<anything2 !new_line>>
  public static boolean user_value(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "user_value")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_USER_VALUE, "<user value>");
    result = anything2(builder, level + 1, user_value_0_0_parser_);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // !new_line
  private static boolean user_value_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "user_value_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !new_line(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
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
  public static boolean expression(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "expression")) return false;
    addVariant(builder, "<expression>");
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, "<expression>");
    result = paren_expression(builder, level + 1);
    if (!result) result = not_expression(builder, level + 1);
    if (!result) result = class_expression(builder, level + 1);
    if (!result) result = predefined_class_expression(builder, level + 1);
    if (!result) result = macro_ref_expression(builder, level + 1);
    if (!result) result = literal_expression(builder, level + 1);
    pinned = result;
    result = result && expression_0(builder, level + 1, priority);
    exit_section_(builder, level, marker, null, result, pinned, null);
    return result || pinned;
  }

  public static boolean expression_0(PsiBuilder builder, int level, int priority) {
    if (!recursion_guard_(builder, level, "expression_0")) return false;
    boolean result = true;
    while (true) {
      Marker marker = enter_section_(builder, level, _LEFT_, null);
      if (priority < 0 && consumeTokenSmart(builder, FLEX_OR)) {
        while (true) {
          result = report_error_(builder, expression(builder, level, 0));
          if (!consumeTokenSmart(builder, FLEX_OR)) break;
        }
        exit_section_(builder, level, marker, FLEX_CHOICE_EXPRESSION, result, true, null);
      }
      else if (priority < 1 && sequence_op(builder, level + 1)) {
        while (true) {
          result = report_error_(builder, expression(builder, level, 1));
          if (!sequence_op(builder, level + 1)) break;
        }
        exit_section_(builder, level, marker, FLEX_SEQUENCE_EXPRESSION, result, true, null);
      }
      else if (priority < 4 && quantifier_expression_0(builder, level + 1)) {
        result = true;
        exit_section_(builder, level, marker, FLEX_QUANTIFIER_EXPRESSION, result, true, null);
      }
      else {
        exit_section_(builder, level, marker, null, false, false, null);
        break;
      }
    }
    return result;
  }

  // '(' expression ')'
  public static boolean paren_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "paren_expression")) return false;
    if (!nextTokenIsSmart(builder, FLEX_PAREN1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_PAREN_EXPRESSION, "<expression>");
    result = consumeTokenSmart(builder, FLEX_PAREN1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, expression(builder, level + 1, -1));
    result = pinned && consumeToken(builder, FLEX_PAREN2) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  public static boolean not_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "not_expression")) return false;
    if (!nextTokenIsSmart(builder, FLEX_NOT, FLEX_NOT2)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, null);
    result = not_expression_0(builder, level + 1);
    pinned = result;
    result = pinned && expression(builder, level, 3);
    exit_section_(builder, level, marker, FLEX_NOT_EXPRESSION, result, pinned, null);
    return result || pinned;
  }

  // '!'|'~'
  private static boolean not_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "not_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FLEX_NOT);
    if (!result) result = consumeTokenSmart(builder, FLEX_NOT2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '*' | '+' | '?' | '{' number [ ',' number] '}'
  private static boolean quantifier_expression_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quantifier_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FLEX_STAR);
    if (!result) result = consumeTokenSmart(builder, FLEX_PLUS);
    if (!result) result = consumeTokenSmart(builder, FLEX_QUESTION);
    if (!result) result = quantifier_expression_0_3(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '{' number [ ',' number] '}'
  private static boolean quantifier_expression_0_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quantifier_expression_0_3")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FLEX_BRACE1);
    result = result && consumeToken(builder, FLEX_NUMBER);
    result = result && quantifier_expression_0_3_2(builder, level + 1);
    result = result && consumeToken(builder, FLEX_BRACE2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // [ ',' number]
  private static boolean quantifier_expression_0_3_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quantifier_expression_0_3_2")) return false;
    quantifier_expression_0_3_2_0(builder, level + 1);
    return true;
  }

  // ',' number
  private static boolean quantifier_expression_0_3_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quantifier_expression_0_3_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FLEX_COMMA);
    result = result && consumeToken(builder, FLEX_NUMBER);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '[' '^'? '-'? (string | predefined_class | class_char [ '-' class_char]) * '-'? ']'
  public static boolean class_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression")) return false;
    if (!nextTokenIsSmart(builder, FLEX_BRACK1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_CLASS_EXPRESSION, "<expression>");
    result = consumeTokenSmart(builder, FLEX_BRACK1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, class_expression_1(builder, level + 1));
    result = pinned && report_error_(builder, class_expression_2(builder, level + 1)) && result;
    result = pinned && report_error_(builder, class_expression_3(builder, level + 1)) && result;
    result = pinned && report_error_(builder, class_expression_4(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, FLEX_BRACK2) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // '^'?
  private static boolean class_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_1")) return false;
    consumeTokenSmart(builder, FLEX_ROOF);
    return true;
  }

  // '-'?
  private static boolean class_expression_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_2")) return false;
    consumeTokenSmart(builder, FLEX_DASH);
    return true;
  }

  // (string | predefined_class | class_char [ '-' class_char]) *
  private static boolean class_expression_3(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_3")) return false;
    int pos = current_position_(builder);
    while (true) {
      if (!class_expression_3_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "class_expression_3", pos)) break;
      pos = current_position_(builder);
    }
    return true;
  }

  // string | predefined_class | class_char [ '-' class_char]
  private static boolean class_expression_3_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_3_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FLEX_STRING);
    if (!result) result = predefined_class(builder, level + 1);
    if (!result) result = class_expression_3_0_2(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // class_char [ '-' class_char]
  private static boolean class_expression_3_0_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_3_0_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = class_char(builder, level + 1);
    result = result && class_expression_3_0_2_1(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // [ '-' class_char]
  private static boolean class_expression_3_0_2_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_3_0_2_1")) return false;
    class_expression_3_0_2_1_0(builder, level + 1);
    return true;
  }

  // '-' class_char
  private static boolean class_expression_3_0_2_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_3_0_2_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeTokenSmart(builder, FLEX_DASH);
    result = result && class_char(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // '-'?
  private static boolean class_expression_4(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_4")) return false;
    consumeTokenSmart(builder, FLEX_DASH);
    return true;
  }

  // predefined_class | '.'
  public static boolean predefined_class_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "predefined_class_expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_PREDEFINED_CLASS_EXPRESSION, "<expression>");
    result = predefined_class(builder, level + 1);
    if (!result) result = consumeTokenSmart(builder, FLEX_DOT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '{' macro_reference '}'
  public static boolean macro_ref_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "macro_ref_expression")) return false;
    if (!nextTokenIsSmart(builder, FLEX_BRACE1)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_MACRO_REF_EXPRESSION, "<expression>");
    result = consumeTokenSmart(builder, FLEX_BRACE1);
    result = result && macro_reference(builder, level + 1);
    result = result && consumeToken(builder, FLEX_BRACE2);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // string | id | allowed_chars
  public static boolean literal_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "literal_expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_LITERAL_EXPRESSION, "<expression>");
    result = consumeTokenSmart(builder, FLEX_STRING);
    if (!result) result = consumeTokenSmart(builder, FLEX_ID);
    if (!result) result = allowed_chars(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  final static Parser declaration_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return declaration_recover(builder, level + 1);
    }
  };
  final static Parser java_class_statements_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return java_class_statements_0_0(builder, level + 1);
    }
  };
  final static Parser java_code_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return java_code_0_0(builder, level + 1);
    }
  };
  final static Parser macro_definition_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return macro_definition_recover(builder, level + 1);
    }
  };
  final static Parser opt_code1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_CODE1);
    }
  };
  final static Parser opt_code2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_CODE2);
    }
  };
  final static Parser opt_eof1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOF1);
    }
  };
  final static Parser opt_eof2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOF2);
    }
  };
  final static Parser opt_eofthrow1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFTHROW1);
    }
  };
  final static Parser opt_eofthrow2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFTHROW2);
    }
  };
  final static Parser opt_eofval1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFVAL1);
    }
  };
  final static Parser opt_eofval2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFVAL2);
    }
  };
  final static Parser opt_init1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INIT1);
    }
  };
  final static Parser opt_init2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INIT2);
    }
  };
  final static Parser opt_initthrow1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INITTHROW1);
    }
  };
  final static Parser opt_initthrow2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INITTHROW2);
    }
  };
  final static Parser opt_yylexthrow1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_YYLEXTHROW1);
    }
  };
  final static Parser opt_yylexthrow2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_YYLEXTHROW2);
    }
  };
  final static Parser rule_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return rule_recover(builder, level + 1);
    }
  };
  final static Parser section_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return section_recover(builder, level + 1);
    }
  };
  final static Parser two_percs_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_TWO_PERCS);
    }
  };
  final static Parser user_value_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return user_value_0_0(builder, level + 1);
    }
  };
}
