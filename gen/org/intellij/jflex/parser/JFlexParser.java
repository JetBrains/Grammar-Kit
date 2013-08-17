/*
 * Copyright 2011-2011 Gregory Shrago
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
import com.intellij.openapi.diagnostic.Logger;
import static org.intellij.jflex.psi.JFlexTypes.*;
import static org.intellij.jflex.parser.JFlexParserUtil.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class JFlexParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("org.intellij.jflex.parser.JFlexParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    if (root_ == FLEX_CHOICE_EXPRESSION) {
      result_ = expression(builder_, level_ + 1, -1);
    }
    else if (root_ == FLEX_CLASS_EXPRESSION) {
      result_ = class_expression(builder_, level_ + 1);
    }
    else if (root_ == FLEX_DECLARATIONS_SECTION) {
      result_ = declarations_section(builder_, level_ + 1);
    }
    else if (root_ == FLEX_EXPRESSION) {
      result_ = expression(builder_, level_ + 1, -1);
    }
    else if (root_ == FLEX_JAVA_CODE) {
      result_ = java_code(builder_, level_ + 1);
    }
    else if (root_ == FLEX_JAVA_FQN) {
      result_ = java_fqn(builder_, level_ + 1);
    }
    else if (root_ == FLEX_LEXICAL_RULES_SECTION) {
      result_ = lexical_rules_section(builder_, level_ + 1);
    }
    else if (root_ == FLEX_LITERAL_EXPRESSION) {
      result_ = literal_expression(builder_, level_ + 1);
    }
    else if (root_ == FLEX_LOOK_AHEAD) {
      result_ = look_ahead(builder_, level_ + 1);
    }
    else if (root_ == FLEX_MACRO_DEFINITION) {
      result_ = macro_definition(builder_, level_ + 1);
    }
    else if (root_ == FLEX_MACRO_REF_EXPRESSION) {
      result_ = macro_ref_expression(builder_, level_ + 1);
    }
    else if (root_ == FLEX_MACRO_REFERENCE) {
      result_ = macro_reference(builder_, level_ + 1);
    }
    else if (root_ == FLEX_NOT_EXPRESSION) {
      result_ = not_expression(builder_, level_ + 1);
    }
    else if (root_ == FLEX_OPTION) {
      result_ = option(builder_, level_ + 1);
    }
    else if (root_ == FLEX_PAREN_EXPRESSION) {
      result_ = paren_expression(builder_, level_ + 1);
    }
    else if (root_ == FLEX_PREDEFINED_CLASS_EXPRESSION) {
      result_ = predefined_class_expression(builder_, level_ + 1);
    }
    else if (root_ == FLEX_QUANTIFIER_EXPRESSION) {
      result_ = expression(builder_, level_ + 1, 3);
    }
    else if (root_ == FLEX_RULE) {
      result_ = rule(builder_, level_ + 1);
    }
    else if (root_ == FLEX_SEQUENCE_EXPRESSION) {
      result_ = expression(builder_, level_ + 1, 0);
    }
    else if (root_ == FLEX_SEQUENCE_OP) {
      result_ = sequence_op(builder_, level_ + 1);
    }
    else if (root_ == FLEX_STATE_DECLARATION) {
      result_ = state_declaration(builder_, level_ + 1);
    }
    else if (root_ == FLEX_STATE_DEFINITION) {
      result_ = state_definition(builder_, level_ + 1);
    }
    else if (root_ == FLEX_STATE_REFERENCE) {
      result_ = state_reference(builder_, level_ + 1);
    }
    else if (root_ == FLEX_USER_CODE_SECTION) {
      result_ = user_code_section(builder_, level_ + 1);
    }
    else {
      Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
      result_ = parse_root_(root_, builder_, level_);
      exit_section_(builder_, level_, marker_, root_, result_, true, TOKEN_ADVANCER);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return flex_file(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(FLEX_CHOICE_EXPRESSION, FLEX_CLASS_EXPRESSION, FLEX_EXPRESSION, FLEX_LITERAL_EXPRESSION,
      FLEX_MACRO_REF_EXPRESSION, FLEX_NOT_EXPRESSION, FLEX_PAREN_EXPRESSION, FLEX_PREDEFINED_CLASS_EXPRESSION,
      FLEX_QUANTIFIER_EXPRESSION, FLEX_SEQUENCE_EXPRESSION),
    create_token_set_(FLEX_OPTION),
  };

  /* ********************************************************** */
  // '{' java_class_statements '}' | '|'
  public static boolean action(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "action")) return false;
    if (!nextTokenIs(builder_, FLEX_BRACE1) && !nextTokenIs(builder_, FLEX_OR)
        && replaceVariants(builder_, 2, "<action>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<action>");
    result_ = action_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, FLEX_OR);
    exit_section_(builder_, level_, marker_, FLEX_JAVA_CODE, result_, false, null);
    return result_;
  }

  // '{' java_class_statements '}'
  private static boolean action_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "action_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_BRACE1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_class_statements(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, FLEX_BRACE2) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // escaped_char | char | number | '-' | '=' | '<' | '>'
  //   | ']'
  static boolean allowed_chars(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "allowed_chars")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_ESCAPED_CHAR);
    if (!result_) result_ = consumeToken(builder_, FLEX_CHAR);
    if (!result_) result_ = consumeToken(builder_, FLEX_NUMBER);
    if (!result_) result_ = consumeToken(builder_, FLEX_DASH);
    if (!result_) result_ = consumeToken(builder_, FLEX_EQ);
    if (!result_) result_ = consumeToken(builder_, FLEX_ANGLE1);
    if (!result_) result_ = consumeToken(builder_, FLEX_ANGLE2);
    if (!result_) result_ = consumeToken(builder_, FLEX_BRACK2);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // char | escaped_char
  static boolean class_char(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_char")) return false;
    if (!nextTokenIs(builder_, FLEX_CHAR) && !nextTokenIs(builder_, FLEX_ESCAPED_CHAR)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_CHAR);
    if (!result_) result_ = consumeToken(builder_, FLEX_ESCAPED_CHAR);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // macro_definition | state_declaration | option
  static boolean declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declaration")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = macro_definition(builder_, level_ + 1);
    if (!result_) result_ = state_declaration(builder_, level_ + 1);
    if (!result_) result_ = option(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, declaration_recover_parser_);
    return result_;
  }

  /* ********************************************************** */
  // !(<<is_percent>> | id '=') section_recover
  static boolean declaration_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declaration_recover")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = declaration_recover_0(builder_, level_ + 1);
    result_ = result_ && section_recover(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !(<<is_percent>> | id '=')
  private static boolean declaration_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declaration_recover_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !declaration_recover_0_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // <<is_percent>> | id '='
  private static boolean declaration_recover_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declaration_recover_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = is_percent(builder_, level_ + 1);
    if (!result_) result_ = declaration_recover_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // id '='
  private static boolean declaration_recover_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declaration_recover_0_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_ID);
    result_ = result_ && consumeToken(builder_, FLEX_EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [] (declaration) *
  public static boolean declarations_section(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarations_section")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<declarations section>");
    result_ = declarations_section_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && declarations_section_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_DECLARATIONS_SECTION, result_, pinned_, section_recover_parser_);
    return result_ || pinned_;
  }

  // []
  private static boolean declarations_section_0(PsiBuilder builder_, int level_) {
    return true;
  }

  // (declaration) *
  private static boolean declarations_section_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarations_section_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!declarations_section_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "declarations_section_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (declaration)
  private static boolean declarations_section_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "declarations_section_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = declaration(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // []
  //   user_code_section
  //   section_div
  //   declarations_section
  //   section_div
  //   lexical_rules_section
  static boolean flex_file(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "flex_file")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = flex_file_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, user_code_section(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, section_div(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, declarations_section(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, section_div(builder_, level_ + 1)) && result_;
    result_ = pinned_ && lexical_rules_section(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // []
  private static boolean flex_file_0(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // <<anything !('}' | '<' id '>')>>
  static boolean java_class_statements(PsiBuilder builder_, int level_) {
    return anything(builder_, level_ + 1, java_class_statements_0_0_parser_);
  }

  // !('}' | '<' id '>')
  private static boolean java_class_statements_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_class_statements_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !java_class_statements_0_0_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // '}' | '<' id '>'
  private static boolean java_class_statements_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_class_statements_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_BRACE2);
    if (!result_) result_ = java_class_statements_0_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '<' id '>'
  private static boolean java_class_statements_0_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_class_statements_0_0_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_ANGLE1);
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    result_ = result_ && consumeToken(builder_, FLEX_ANGLE2);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<anything2 (&java | !<<is_percent>>)>>
  public static boolean java_code(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_code")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<java code>");
    result_ = anything2(builder_, level_ + 1, java_code_0_0_parser_);
    exit_section_(builder_, level_, marker_, FLEX_JAVA_CODE, result_, false, null);
    return result_;
  }

  // &java | !<<is_percent>>
  private static boolean java_code_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_code_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = java_code_0_0_0(builder_, level_ + 1);
    if (!result_) result_ = java_code_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &java
  private static boolean java_code_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_code_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _AND_, null);
    result_ = consumeToken(builder_, FLEX_JAVA);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // !<<is_percent>>
  private static boolean java_code_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_code_0_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !is_percent(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id ( safe_dot id ) *
  public static boolean java_fqn(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_fqn")) return false;
    if (!nextTokenIs(builder_, FLEX_ID)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_ID);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_JAVA_FQN, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( safe_dot id ) *
  private static boolean java_fqn_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_fqn_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!java_fqn_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "java_fqn_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // safe_dot id
  private static boolean java_fqn_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_fqn_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = safe_dot(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // [java_fqn (',' java_fqn) *]
  static boolean java_fqn_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_fqn_list")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    java_fqn_list_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, true, false, declaration_recover_parser_);
    return true;
  }

  // java_fqn (',' java_fqn) *
  private static boolean java_fqn_list_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_fqn_list_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = java_fqn(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn_list_0_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' java_fqn) *
  private static boolean java_fqn_list_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_fqn_list_0_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!java_fqn_list_0_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "java_fqn_list_0_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ',' java_fqn
  private static boolean java_fqn_list_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "java_fqn_list_0_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // [] rule +
  public static boolean lexical_rules_section(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lexical_rules_section")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<lexical rules section>");
    result_ = lexical_rules_section_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && lexical_rules_section_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_LEXICAL_RULES_SECTION, result_, pinned_, section_recover_parser_);
    return result_ || pinned_;
  }

  // []
  private static boolean lexical_rules_section_0(PsiBuilder builder_, int level_) {
    return true;
  }

  // rule +
  private static boolean lexical_rules_section_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "lexical_rules_section_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = rule(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!rule(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "lexical_rules_section_1");
        break;
      }
      offset_ = next_offset_;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // new_line <<p>> new_line
  static boolean line(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "line")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = new_line(builder_, level_ + 1);
    result_ = result_ && p.parse(builder_, level_);
    pinned_ = result_; // pin = 2
    result_ = result_ && new_line(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '$' | '/' expression
  public static boolean look_ahead(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "look_ahead")) return false;
    if (!nextTokenIs(builder_, FLEX_DOLLAR) && !nextTokenIs(builder_, FLEX_SLASH2)
        && replaceVariants(builder_, 2, "<look ahead>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<look ahead>");
    result_ = consumeToken(builder_, FLEX_DOLLAR);
    if (!result_) result_ = look_ahead_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_LOOK_AHEAD, result_, false, null);
    return result_;
  }

  // '/' expression
  private static boolean look_ahead_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "look_ahead_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_SLASH2);
    result_ = result_ && expression(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // new_line id '=' expression
  public static boolean macro_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_definition")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<macro definition>");
    result_ = new_line(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    result_ = result_ && consumeToken(builder_, FLEX_EQ);
    pinned_ = result_; // pin = 3
    result_ = result_ && expression(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, FLEX_MACRO_DEFINITION, result_, pinned_, macro_definition_recover_parser_);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // !(new_line id '=' | '%%' | <<is_percent>>)
  static boolean macro_definition_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_definition_recover")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !macro_definition_recover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // new_line id '=' | '%%' | <<is_percent>>
  private static boolean macro_definition_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_definition_recover_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = macro_definition_recover_0_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC2);
    if (!result_) result_ = is_percent(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // new_line id '='
  private static boolean macro_definition_recover_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_definition_recover_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = new_line(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    result_ = result_ && consumeToken(builder_, FLEX_EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean macro_reference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_reference")) return false;
    if (!nextTokenIs(builder_, FLEX_ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_ID);
    exit_section_(builder_, marker_, FLEX_MACRO_REFERENCE, result_);
    return result_;
  }

  /* ********************************************************** */
  // &<<is_new_line>>
  static boolean new_line(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "new_line")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _AND_, null);
    result_ = is_new_line(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
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
  public static boolean option(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = option_class(builder_, level_ + 1);
    if (!result_) result_ = option_implements(builder_, level_ + 1);
    if (!result_) result_ = option_extends(builder_, level_ + 1);
    if (!result_) result_ = option_public(builder_, level_ + 1);
    if (!result_) result_ = option_final(builder_, level_ + 1);
    if (!result_) result_ = option_abstract(builder_, level_ + 1);
    if (!result_) result_ = option_api_private(builder_, level_ + 1);
    if (!result_) result_ = option_user_code(builder_, level_ + 1);
    if (!result_) result_ = option_init(builder_, level_ + 1);
    if (!result_) result_ = option_init_throw(builder_, level_ + 1);
    if (!result_) result_ = option_ctor_arg(builder_, level_ + 1);
    if (!result_) result_ = option_scan_error(builder_, level_ + 1);
    if (!result_) result_ = option_buffer_size(builder_, level_ + 1);
    if (!result_) result_ = option_include(builder_, level_ + 1);
    if (!result_) result_ = option_function(builder_, level_ + 1);
    if (!result_) result_ = option_integer(builder_, level_ + 1);
    if (!result_) result_ = option_intwrap(builder_, level_ + 1);
    if (!result_) result_ = option_type(builder_, level_ + 1);
    if (!result_) result_ = option_yylexthrow(builder_, level_ + 1);
    if (!result_) result_ = option_eof_val(builder_, level_ + 1);
    if (!result_) result_ = option_eof(builder_, level_ + 1);
    if (!result_) result_ = option_eof_throw(builder_, level_ + 1);
    if (!result_) result_ = option_eof_close(builder_, level_ + 1);
    if (!result_) result_ = option_debug(builder_, level_ + 1);
    if (!result_) result_ = option_standalone(builder_, level_ + 1);
    if (!result_) result_ = option_cup(builder_, level_ + 1);
    if (!result_) result_ = option_cup_sym(builder_, level_ + 1);
    if (!result_) result_ = option_cup_debug(builder_, level_ + 1);
    if (!result_) result_ = option_byacc(builder_, level_ + 1);
    if (!result_) result_ = option_switch(builder_, level_ + 1);
    if (!result_) result_ = option_table(builder_, level_ + 1);
    if (!result_) result_ = option_pack(builder_, level_ + 1);
    if (!result_) result_ = option_7bit(builder_, level_ + 1);
    if (!result_) result_ = option_full(builder_, level_ + 1);
    if (!result_) result_ = option_unicode(builder_, level_ + 1);
    if (!result_) result_ = option_ignore_case(builder_, level_ + 1);
    if (!result_) result_ = option_count_char(builder_, level_ + 1);
    if (!result_) result_ = option_count_line(builder_, level_ + 1);
    if (!result_) result_ = option_count_column(builder_, level_ + 1);
    if (!result_) result_ = option_obsolete(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '%7bit'
  public static boolean option_7bit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_7bit")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_45)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_45);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%abstract'
  public static boolean option_abstract(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_abstract")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_6)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_6);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%apiprivate'
  public static boolean option_api_private(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_api_private")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_7)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_7);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%buffer' number
  public static boolean option_buffer_size(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_buffer_size")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_17)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_17);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, FLEX_NUMBER);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%byacc'
  public static boolean option_byacc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_byacc")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_41)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_41);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%class' java_fqn
  public static boolean option_class(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_class")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_1)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_1);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%char'
  public static boolean option_count_char(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_count_char")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_52)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_52);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%column'
  public static boolean option_count_column(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_count_column")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_54)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_54);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%line'
  public static boolean option_count_line(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_count_line")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_53)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_53);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%ctorarg' java_fqn id
  public static boolean option_ctor_arg(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_ctor_arg")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_15)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_15);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_fqn(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, FLEX_ID) && result_;
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%cup'
  public static boolean option_cup(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_cup")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_38)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_38);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%cupdebug'
  public static boolean option_cup_debug(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_cup_debug")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_40)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_40);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%cupsym' java_fqn
  public static boolean option_cup_sym(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_cup_sym")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_39)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_39);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%debug'
  public static boolean option_debug(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_debug")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_36)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_36);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // <<line '%eof{'>> java_code <<line '%eof}'>>
  public static boolean option_eof(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_eof")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_29)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = line(builder_, level_ + 1, perc_29_parser_);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_code(builder_, level_ + 1));
    result_ = pinned_ && line(builder_, level_ + 1, perc_30_parser_) && result_;
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%eofclose' ['false']
  public static boolean option_eof_close(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_eof_close")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_34)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_34);
    pinned_ = result_; // pin = 1
    result_ = result_ && option_eof_close_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ['false']
  private static boolean option_eof_close_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_eof_close_1")) return false;
    consumeToken(builder_, "false");
    return true;
  }

  /* ********************************************************** */
  // '%eofthrow' java_fqn_list | <<line '%eofthrow{'>> java_fqn_list <<line '%eofthrow}'>>
  public static boolean option_eof_throw(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_eof_throw")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_31) && !nextTokenIs(builder_, FLEX_PERC_32)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = option_eof_throw_0(builder_, level_ + 1);
    if (!result_) result_ = option_eof_throw_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  // '%eofthrow' java_fqn_list
  private static boolean option_eof_throw_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_eof_throw_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_31);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn_list(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // <<line '%eofthrow{'>> java_fqn_list <<line '%eofthrow}'>>
  private static boolean option_eof_throw_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_eof_throw_1")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = line(builder_, level_ + 1, perc_32_parser_);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_fqn_list(builder_, level_ + 1));
    result_ = pinned_ && line(builder_, level_ + 1, perc_33_parser_) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // <<line '%eofval{'>> java_code <<line '%eofval}'>>
  public static boolean option_eof_val(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_eof_val")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_27)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = line(builder_, level_ + 1, perc_27_parser_);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_code(builder_, level_ + 1));
    result_ = pinned_ && line(builder_, level_ + 1, perc_28_parser_) && result_;
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%extends' java_fqn
  public static boolean option_extends(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_extends")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_3)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_3);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%final'
  public static boolean option_final(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_final")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_5)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_5);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%full' | '%8bit'
  public static boolean option_full(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_full")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_47) && !nextTokenIs(builder_, FLEX_PERC_46)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = consumeToken(builder_, FLEX_PERC_46);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_47);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '%function' id
  public static boolean option_function(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_function")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_19)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_19);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%caseless' | '%ignorecase'
  public static boolean option_ignore_case(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_ignore_case")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_50) && !nextTokenIs(builder_, FLEX_PERC_51)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = consumeToken(builder_, FLEX_PERC_50);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_51);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '%implements' java_fqn_list
  public static boolean option_implements(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_implements")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_2)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_2);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn_list(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%include' string
  public static boolean option_include(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_include")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_18)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_18);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, FLEX_STRING);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // <<line '%init{'>> java_code <<line '%init}'>>
  public static boolean option_init(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_init")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_10)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = line(builder_, level_ + 1, perc_10_parser_);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_code(builder_, level_ + 1));
    result_ = pinned_ && line(builder_, level_ + 1, perc_11_parser_) && result_;
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%initthrow' java_fqn_list | <<line '%initthrow{'>> java_fqn_list <<line '%initthrow}'>>
  public static boolean option_init_throw(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_init_throw")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_12) && !nextTokenIs(builder_, FLEX_PERC_13)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = option_init_throw_0(builder_, level_ + 1);
    if (!result_) result_ = option_init_throw_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  // '%initthrow' java_fqn_list
  private static boolean option_init_throw_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_init_throw_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_12);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn_list(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // <<line '%initthrow{'>> java_fqn_list <<line '%initthrow}'>>
  private static boolean option_init_throw_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_init_throw_1")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = line(builder_, level_ + 1, perc_13_parser_);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_fqn_list(builder_, level_ + 1));
    result_ = pinned_ && line(builder_, level_ + 1, perc_14_parser_) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%integer' | '%int'
  public static boolean option_integer(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_integer")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_21) && !nextTokenIs(builder_, FLEX_PERC_20)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = consumeToken(builder_, FLEX_PERC_20);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_21);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '%intwrap'
  public static boolean option_intwrap(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_intwrap")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_22)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_22);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%notunix' | '%yyeof'
  public static boolean option_obsolete(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_obsolete")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_55) && !nextTokenIs(builder_, FLEX_PERC_56)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = consumeToken(builder_, FLEX_PERC_55);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_56);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '%pack'
  public static boolean option_pack(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_pack")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_44)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_44);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%public'
  public static boolean option_public(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_public")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_4)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_4);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%scanerror' java_fqn
  public static boolean option_scan_error(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_scan_error")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_16)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_16);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%standalone'
  public static boolean option_standalone(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_standalone")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_37)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_37);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%switch'
  public static boolean option_switch(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_switch")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_42)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_42);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%table'
  public static boolean option_table(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_table")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_43)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_43);
    pinned_ = result_; // pin = 1
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%type' java_fqn
  public static boolean option_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_type")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_23)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_23);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%unicode' | '%16bit'
  public static boolean option_unicode(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_unicode")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_49) && !nextTokenIs(builder_, FLEX_PERC_48)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = consumeToken(builder_, FLEX_PERC_48);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_49);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<line '%{'>> java_code <<line '%}'>>
  public static boolean option_user_code(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_user_code")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_8)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = line(builder_, level_ + 1, perc_8_parser_);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_code(builder_, level_ + 1));
    result_ = pinned_ && line(builder_, level_ + 1, perc_9_parser_) && result_;
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '%yylexthrow' java_fqn_list | <<line '%yylexthrow{'>> java_fqn_list <<line '%yylexthrow}'>>
  public static boolean option_yylexthrow(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_yylexthrow")) return false;
    if (!nextTokenIs(builder_, FLEX_PERC_24) && !nextTokenIs(builder_, FLEX_PERC_25)
        && replaceVariants(builder_, 2, "<option>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<option>");
    result_ = option_yylexthrow_0(builder_, level_ + 1);
    if (!result_) result_ = option_yylexthrow_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_OPTION, result_, false, null);
    return result_;
  }

  // '%yylexthrow' java_fqn_list
  private static boolean option_yylexthrow_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_yylexthrow_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PERC_24);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_fqn_list(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // <<line '%yylexthrow{'>> java_fqn_list <<line '%yylexthrow}'>>
  private static boolean option_yylexthrow_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "option_yylexthrow_1")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = line(builder_, level_ + 1, perc_25_parser_);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, java_fqn_list(builder_, level_ + 1));
    result_ = pinned_ && line(builder_, level_ + 1, perc_26_parser_) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '[:jletter:]'
  //   | '[:jletterdigit:]'
  //   | '[:letter:]'
  //   | '[:digit:]'
  //   | '[:uppercase:]'
  //   | '[:lowercase:]'
  static boolean predefined_class(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predefined_class")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<char class>");
    result_ = consumeToken(builder_, FLEX_CLASS1);
    if (!result_) result_ = consumeToken(builder_, FLEX_CLASS2);
    if (!result_) result_ = consumeToken(builder_, FLEX_CLASS3);
    if (!result_) result_ = consumeToken(builder_, FLEX_CLASS4);
    if (!result_) result_ = consumeToken(builder_, FLEX_CLASS5);
    if (!result_) result_ = consumeToken(builder_, FLEX_CLASS6);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // state_list (rule_group | rule_tail ) | rule_tail
  public static boolean rule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<rule>");
    result_ = rule_0(builder_, level_ + 1);
    if (!result_) result_ = rule_tail(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_RULE, result_, false, rule_recover_parser_);
    return result_;
  }

  // state_list (rule_group | rule_tail )
  private static boolean rule_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = state_list(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && rule_0_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // rule_group | rule_tail
  private static boolean rule_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = rule_group(builder_, level_ + 1);
    if (!result_) result_ = rule_tail(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // !('{' id '}') '{' rule + '}'
  static boolean rule_group(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_group")) return false;
    if (!nextTokenIs(builder_, FLEX_BRACE1)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = rule_group_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FLEX_BRACE1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, rule_group_2(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, FLEX_BRACE2) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // !('{' id '}')
  private static boolean rule_group_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_group_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !rule_group_0_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // '{' id '}'
  private static boolean rule_group_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_group_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_BRACE1);
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    result_ = result_ && consumeToken(builder_, FLEX_BRACE2);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // rule +
  private static boolean rule_group_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_group_2")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = rule(builder_, level_ + 1);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!rule(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "rule_group_2");
        break;
      }
      offset_ = next_offset_;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // !('}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group)
  static boolean rule_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_recover")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !rule_recover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // '}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group
  private static boolean rule_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_recover_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_BRACE2);
    if (!result_) result_ = consumeToken(builder_, FLEX_DOT);
    if (!result_) result_ = consumeToken(builder_, FLEX_ANGLE1);
    if (!result_) result_ = consumeToken(builder_, FLEX_EOF);
    if (!result_) result_ = consumeToken(builder_, FLEX_ROOF);
    if (!result_) result_ = new_line(builder_, level_ + 1);
    if (!result_) result_ = expression(builder_, level_ + 1, -2);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // rule_tail1 | rule_tail2
  static boolean rule_tail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_tail")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = rule_tail1(builder_, level_ + 1);
    if (!result_) result_ = rule_tail2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '<<EOF>>' action
  static boolean rule_tail1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_tail1")) return false;
    if (!nextTokenIs(builder_, FLEX_EOF)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_EOF);
    pinned_ = result_; // pin = 1
    result_ = result_ && action(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // ['^'] expression look_ahead? action
  static boolean rule_tail2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_tail2")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = rule_tail2_0(builder_, level_ + 1);
    result_ = result_ && expression(builder_, level_ + 1, -1);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, rule_tail2_2(builder_, level_ + 1));
    result_ = pinned_ && action(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ['^']
  private static boolean rule_tail2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_tail2_0")) return false;
    consumeToken(builder_, FLEX_ROOF);
    return true;
  }

  // look_ahead?
  private static boolean rule_tail2_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rule_tail2_2")) return false;
    look_ahead(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // '.' !'*'
  static boolean safe_dot(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "safe_dot")) return false;
    if (!nextTokenIs(builder_, FLEX_DOT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_DOT);
    result_ = result_ && safe_dot_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !'*'
  private static boolean safe_dot_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "safe_dot_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !consumeToken(builder_, FLEX_STAR);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<line '%%'>>
  static boolean section_div(PsiBuilder builder_, int level_) {
    return line(builder_, level_ + 1, perc2_parser_);
  }

  /* ********************************************************** */
  // !'%%'
  static boolean section_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "section_recover")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !consumeToken(builder_, FLEX_PERC2);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // &( '(' | '!' | '~' | '[' | ']'
  //   | string | allowed_chars | predefined_class | '.'
  //   | '{' id '}' | !new_line id )
  public static boolean sequence_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_op")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _AND_, "<sequence op>");
    result_ = sequence_op_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_SEQUENCE_OP, result_, false, null);
    return result_;
  }

  // '(' | '!' | '~' | '[' | ']'
  //   | string | allowed_chars | predefined_class | '.'
  //   | '{' id '}' | !new_line id
  private static boolean sequence_op_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_op_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_PAREN1);
    if (!result_) result_ = consumeToken(builder_, FLEX_NOT);
    if (!result_) result_ = consumeToken(builder_, FLEX_NOT2);
    if (!result_) result_ = consumeToken(builder_, FLEX_BRACK1);
    if (!result_) result_ = consumeToken(builder_, FLEX_BRACK2);
    if (!result_) result_ = consumeToken(builder_, FLEX_STRING);
    if (!result_) result_ = allowed_chars(builder_, level_ + 1);
    if (!result_) result_ = predefined_class(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, FLEX_DOT);
    if (!result_) result_ = sequence_op_0_9(builder_, level_ + 1);
    if (!result_) result_ = sequence_op_0_10(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '{' id '}'
  private static boolean sequence_op_0_9(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_op_0_9")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_BRACE1);
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    result_ = result_ && consumeToken(builder_, FLEX_BRACE2);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !new_line id
  private static boolean sequence_op_0_10(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_op_0_10")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = sequence_op_0_10_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FLEX_ID);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !new_line
  private static boolean sequence_op_0_10_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "sequence_op_0_10_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !new_line(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // ('%state' | '%s' | '%xstate' | '%x') state_definition ((','? !(id '=')) state_definition) * ','?
  public static boolean state_declaration(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<state declaration>");
    result_ = state_declaration_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, state_definition(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, state_declaration_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && state_declaration_3(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, FLEX_STATE_DECLARATION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // '%state' | '%s' | '%xstate' | '%x'
  private static boolean state_declaration_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_PERC_57);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_58);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_59);
    if (!result_) result_ = consumeToken(builder_, FLEX_PERC_69);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ((','? !(id '=')) state_definition) *
  private static boolean state_declaration_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!state_declaration_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "state_declaration_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (','? !(id '=')) state_definition
  private static boolean state_declaration_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = state_declaration_2_0_0(builder_, level_ + 1);
    result_ = result_ && state_definition(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ','? !(id '=')
  private static boolean state_declaration_2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_2_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = state_declaration_2_0_0_0(builder_, level_ + 1);
    result_ = result_ && state_declaration_2_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ','?
  private static boolean state_declaration_2_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_2_0_0_0")) return false;
    consumeToken(builder_, FLEX_COMMA);
    return true;
  }

  // !(id '=')
  private static boolean state_declaration_2_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_2_0_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !state_declaration_2_0_0_1_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // id '='
  private static boolean state_declaration_2_0_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_2_0_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_ID);
    result_ = result_ && consumeToken(builder_, FLEX_EQ);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ','?
  private static boolean state_declaration_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_declaration_3")) return false;
    consumeToken(builder_, FLEX_COMMA);
    return true;
  }

  /* ********************************************************** */
  // id
  public static boolean state_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_definition")) return false;
    if (!nextTokenIs(builder_, FLEX_ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_ID);
    exit_section_(builder_, marker_, FLEX_STATE_DEFINITION, result_);
    return result_;
  }

  /* ********************************************************** */
  // '<' state_reference (',' state_reference) * '>'
  static boolean state_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_list")) return false;
    if (!nextTokenIs(builder_, FLEX_ANGLE1)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_ANGLE1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, state_reference(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, state_list_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, FLEX_ANGLE2) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' state_reference) *
  private static boolean state_list_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_list_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!state_list_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "state_list_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ',' state_reference
  private static boolean state_list_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_list_2_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && state_reference(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // id
  public static boolean state_reference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "state_reference")) return false;
    if (!nextTokenIs(builder_, FLEX_ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_ID);
    exit_section_(builder_, marker_, FLEX_STATE_REFERENCE, result_);
    return result_;
  }

  /* ********************************************************** */
  // [] java_code
  public static boolean user_code_section(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "user_code_section")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<user code section>");
    result_ = user_code_section_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && java_code(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_USER_CODE_SECTION, result_, pinned_, section_recover_parser_);
    return result_ || pinned_;
  }

  // []
  private static boolean user_code_section_0(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: N_ARY(choice_expression)
  // 1: N_ARY(sequence_expression)
  // 2: ATOM(paren_expression)
  // 3: ATOM(not_expression)
  // 4: POSTFIX(quantifier_expression)
  // 5: ATOM(class_expression) ATOM(predefined_class_expression) ATOM(macro_ref_expression) ATOM(literal_expression)
  public static boolean expression(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression>");
    result_ = paren_expression(builder_, level_ + 1);
    if (!result_) result_ = not_expression(builder_, level_ + 1);
    if (!result_) result_ = class_expression(builder_, level_ + 1);
    if (!result_) result_ = predefined_class_expression(builder_, level_ + 1);
    if (!result_) result_ = macro_ref_expression(builder_, level_ + 1);
    if (!result_) result_ = literal_expression(builder_, level_ + 1);
    pinned_ = result_;
    result_ = result_ && expression_0(builder_, level_ + 1, priority_);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  public static boolean expression_0(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expression_0")) return false;
    boolean result_ = true;
    while (true) {
      Marker left_marker_ = (Marker) builder_.getLatestDoneMarker();
      if (!invalid_left_marker_guard_(builder_, left_marker_, "expression_0")) return false;
      Marker marker_ = builder_.mark();
      if (priority_ < 0 && consumeToken(builder_, FLEX_OR)) {
        while (true) {
          result_ = report_error_(builder_, expression(builder_, level_, 0));
          if (!consumeToken(builder_, FLEX_OR)) break;
        }
        marker_.drop();
        left_marker_.precede().done(FLEX_CHOICE_EXPRESSION);
      }
      else if (priority_ < 1 && sequence_op(builder_, level_ + 1)) {
        while (true) {
          result_ = report_error_(builder_, expression(builder_, level_, 1));
          if (!sequence_op(builder_, level_ + 1)) break;
        }
        marker_.drop();
        left_marker_.precede().done(FLEX_SEQUENCE_EXPRESSION);
      }
      else if (priority_ < 4 && quantifier_expression_0(builder_, level_ + 1)) {
        result_ = true;
        marker_.drop();
        left_marker_.precede().done(FLEX_QUANTIFIER_EXPRESSION);
      }
      else {
        exit_section_(builder_, marker_, null, false);
        break;
      }
    }
    return result_;
  }

  // '(' expression ')'
  public static boolean paren_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "paren_expression")) return false;
    if (!nextTokenIs(builder_, FLEX_PAREN1)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_PAREN1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, expression(builder_, level_ + 1, -1));
    result_ = pinned_ && consumeToken(builder_, FLEX_PAREN2) && result_;
    exit_section_(builder_, level_, marker_, FLEX_PAREN_EXPRESSION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ('!'|'~') expression
  public static boolean not_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_expression")) return false;
    if (!nextTokenIs(builder_, FLEX_NOT) && !nextTokenIs(builder_, FLEX_NOT2)
        && replaceVariants(builder_, 2, "<expression>")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<expression>");
    result_ = not_expression_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && expression(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, FLEX_NOT_EXPRESSION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // '!'|'~'
  private static boolean not_expression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_expression_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_NOT);
    if (!result_) result_ = consumeToken(builder_, FLEX_NOT2);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '*' | '+' | '?' | '{' number [ ',' number] '}'
  private static boolean quantifier_expression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier_expression_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_STAR);
    if (!result_) result_ = consumeToken(builder_, FLEX_PLUS);
    if (!result_) result_ = consumeToken(builder_, FLEX_QUESTION);
    if (!result_) result_ = quantifier_expression_0_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '{' number [ ',' number] '}'
  private static boolean quantifier_expression_0_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier_expression_0_3")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_BRACE1);
    result_ = result_ && consumeToken(builder_, FLEX_NUMBER);
    result_ = result_ && quantifier_expression_0_3_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FLEX_BRACE2);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ ',' number]
  private static boolean quantifier_expression_0_3_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier_expression_0_3_2")) return false;
    quantifier_expression_0_3_2_0(builder_, level_ + 1);
    return true;
  }

  // ',' number
  private static boolean quantifier_expression_0_3_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "quantifier_expression_0_3_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_COMMA);
    result_ = result_ && consumeToken(builder_, FLEX_NUMBER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '[' '^'? '-'? (string | predefined_class | class_char [ '-' class_char]) * '-'? ']'
  public static boolean class_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression")) return false;
    if (!nextTokenIs(builder_, FLEX_BRACK1)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, FLEX_BRACK1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, class_expression_1(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, class_expression_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, class_expression_3(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, class_expression_4(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, FLEX_BRACK2) && result_;
    exit_section_(builder_, level_, marker_, FLEX_CLASS_EXPRESSION, result_, pinned_, null);
    return result_ || pinned_;
  }

  // '^'?
  private static boolean class_expression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_1")) return false;
    consumeToken(builder_, FLEX_ROOF);
    return true;
  }

  // '-'?
  private static boolean class_expression_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_2")) return false;
    consumeToken(builder_, FLEX_DASH);
    return true;
  }

  // (string | predefined_class | class_char [ '-' class_char]) *
  private static boolean class_expression_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_3")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!class_expression_3_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "class_expression_3");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // string | predefined_class | class_char [ '-' class_char]
  private static boolean class_expression_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_3_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_STRING);
    if (!result_) result_ = predefined_class(builder_, level_ + 1);
    if (!result_) result_ = class_expression_3_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // class_char [ '-' class_char]
  private static boolean class_expression_3_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_3_0_2")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = class_char(builder_, level_ + 1);
    result_ = result_ && class_expression_3_0_2_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ '-' class_char]
  private static boolean class_expression_3_0_2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_3_0_2_1")) return false;
    class_expression_3_0_2_1_0(builder_, level_ + 1);
    return true;
  }

  // '-' class_char
  private static boolean class_expression_3_0_2_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_3_0_2_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_DASH);
    result_ = result_ && class_char(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '-'?
  private static boolean class_expression_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_expression_4")) return false;
    consumeToken(builder_, FLEX_DASH);
    return true;
  }

  // predefined_class | '.'
  public static boolean predefined_class_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "predefined_class_expression")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<expression>");
    result_ = predefined_class(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, FLEX_DOT);
    exit_section_(builder_, level_, marker_, FLEX_PREDEFINED_CLASS_EXPRESSION, result_, false, null);
    return result_;
  }

  // '{' macro_reference '}'
  public static boolean macro_ref_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_ref_expression")) return false;
    if (!nextTokenIs(builder_, FLEX_BRACE1)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FLEX_BRACE1);
    result_ = result_ && macro_reference(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FLEX_BRACE2);
    exit_section_(builder_, marker_, FLEX_MACRO_REF_EXPRESSION, result_);
    return result_;
  }

  // string | id | allowed_chars
  public static boolean literal_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal_expression")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<expression>");
    result_ = consumeToken(builder_, FLEX_STRING);
    if (!result_) result_ = consumeToken(builder_, FLEX_ID);
    if (!result_) result_ = allowed_chars(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, FLEX_LITERAL_EXPRESSION, result_, false, null);
    return result_;
  }

  final static Parser declaration_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return declaration_recover(builder_, level_ + 1);
    }
  };
  final static Parser java_class_statements_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return java_class_statements_0_0(builder_, level_ + 1);
    }
  };
  final static Parser java_code_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return java_code_0_0(builder_, level_ + 1);
    }
  };
  final static Parser macro_definition_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return macro_definition_recover(builder_, level_ + 1);
    }
  };
  final static Parser perc2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC2);
    }
  };
  final static Parser perc_10_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_10);
    }
  };
  final static Parser perc_11_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_11);
    }
  };
  final static Parser perc_13_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_13);
    }
  };
  final static Parser perc_14_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_14);
    }
  };
  final static Parser perc_25_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_25);
    }
  };
  final static Parser perc_26_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_26);
    }
  };
  final static Parser perc_27_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_27);
    }
  };
  final static Parser perc_28_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_28);
    }
  };
  final static Parser perc_29_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_29);
    }
  };
  final static Parser perc_30_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_30);
    }
  };
  final static Parser perc_32_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_32);
    }
  };
  final static Parser perc_33_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_33);
    }
  };
  final static Parser perc_8_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_8);
    }
  };
  final static Parser perc_9_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, FLEX_PERC_9);
    }
  };
  final static Parser rule_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return rule_recover(builder_, level_ + 1);
    }
  };
  final static Parser section_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return section_recover(builder_, level_ + 1);
    }
  };
}
