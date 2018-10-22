// ---- JFlexParser.java -----------------
// license.txt
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
    if (type == FLEX_CHAR_RANGE) {
      result = char_range(builder, 0);
    }
    else if (type == FLEX_DECLARATIONS_SECTION) {
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
    else if (type == FLEX_STATE_LIST) {
      result = state_list(builder, 0);
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
    create_token_set_(FLEX_DECLARATIONS_SECTION, FLEX_LEXICAL_RULES_SECTION, FLEX_USER_CODE_SECTION),
    create_token_set_(FLEX_CHAR_RANGE, FLEX_CHOICE_EXPRESSION, FLEX_CLASS_EXPRESSION, FLEX_EXPRESSION,
      FLEX_LITERAL_EXPRESSION, FLEX_MACRO_REF_EXPRESSION, FLEX_NOT_EXPRESSION, FLEX_PAREN_EXPRESSION,
      FLEX_PREDEFINED_CLASS_EXPRESSION, FLEX_QUANTIFIER_EXPRESSION, FLEX_SEQUENCE_EXPRESSION),
  };

  /* ********************************************************** */
  // '{' raw? '}' | '|'
  public static boolean action(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "action")) return false;
    if (!nextTokenIs(builder, "<action>", FLEX_BAR, FLEX_BRACE1)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_JAVA_CODE, "<action>");
    result = action_0(builder, level + 1);
    if (!result) result = consumeToken(builder, FLEX_BAR);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '{' raw? '}'
  private static boolean action_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "action_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = consumeToken(builder, FLEX_BRACE1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, action_0_1(builder, level + 1));
    result = pinned && consumeToken(builder, FLEX_BRACE2) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // raw?
  private static boolean action_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "action_0_1")) return false;
    consumeToken(builder, FLEX_RAW);
    return true;
  }

  /* ********************************************************** */
  // string | char_class | char_or_esc
  static boolean char_class_atom(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "char_class_atom")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, null, "<char>");
    result = consumeToken(builder, FLEX_STRING);
    if (!result) result = consumeToken(builder, FLEX_CHAR_CLASS);
    if (!result) result = char_or_esc(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // char_range | class_expression | macro_ref_expression | char_class_atom
  static boolean char_class_item(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "char_class_item")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, null, "<char>");
    result = char_range(builder, level + 1);
    if (!result) result = class_expression(builder, level + 1);
    if (!result) result = macro_ref_expression(builder, level + 1);
    if (!result) result = char_class_atom(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '&&' | '||' | '~~' | '--'
  static boolean char_class_op(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "char_class_op")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, null, "<char>");
    result = consumeToken(builder, FLEX_AMPAMP);
    if (!result) result = consumeToken(builder, FLEX_BARBAR);
    if (!result) result = consumeToken(builder, FLEX_TILDETILDE);
    if (!result) result = consumeToken(builder, FLEX_DASHDASH);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // char | char_esc
  static boolean char_or_esc(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "char_or_esc")) return false;
    if (!nextTokenIs(builder, "", FLEX_CHAR, FLEX_CHAR_ESC)) return false;
    boolean result;
    result = consumeToken(builder, FLEX_CHAR);
    if (!result) result = consumeToken(builder, FLEX_CHAR_ESC);
    return result;
  }

  /* ********************************************************** */
  // char_or_esc '-' char_or_esc
  public static boolean char_range(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "char_range")) return false;
    if (!nextTokenIs(builder, "<char>", FLEX_CHAR, FLEX_CHAR_ESC)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_CHAR_RANGE, "<char>");
    result = char_or_esc(builder, level + 1);
    result = result && consumeToken(builder, FLEX_DASH);
    result = result && char_or_esc(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
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
    if (!result) result = parseTokens(builder, 0, FLEX_ID, FLEX_EQ);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // [] declaration (!(<<eof>> | '%%') declaration) *
  public static boolean declarations_section(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_DECLARATIONS_SECTION, "<declarations section>");
    result = declarations_section_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, declaration(builder, level + 1));
    result = pinned && declarations_section_2(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, section_recover_parser_);
    return result || pinned;
  }

  // []
  private static boolean declarations_section_0(PsiBuilder builder, int level) {
    return true;
  }

  // (!(<<eof>> | '%%') declaration) *
  private static boolean declarations_section_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!declarations_section_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "declarations_section_2", pos)) break;
    }
    return true;
  }

  // !(<<eof>> | '%%') declaration
  private static boolean declarations_section_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section_2_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = declarations_section_2_0_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && declaration(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // !(<<eof>> | '%%')
  private static boolean declarations_section_2_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section_2_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !declarations_section_2_0_0_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // <<eof>> | '%%'
  private static boolean declarations_section_2_0_0_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "declarations_section_2_0_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = eof(builder, level + 1);
    if (!result) result = consumeToken(builder, FLEX_TWO_PERCS);
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
  // raw?
  public static boolean java_code(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "java_code")) return false;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_JAVA_CODE, "<java code>");
    consumeToken(builder, FLEX_RAW);
    exit_section_(builder, level, marker, true, false, null);
    return true;
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
    while (true) {
      int pos = current_position_(builder);
      if (!java_type_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "java_type_1", pos)) break;
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
    while (true) {
      int pos = current_position_(builder);
      if (!java_type_list_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "java_type_list_0_1", pos)) break;
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
    while (result) {
      int pos = current_position_(builder);
      if (!rule_group_item(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "lexical_rules_section_1", pos)) break;
    }
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // new_line <<p>> new_line
  static boolean line(PsiBuilder builder, int level, Parser aP) {
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
    if (!nextTokenIs(builder, "<look ahead>", FLEX_DOLLAR, FLEX_FSLASH)) return false;
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
    result = consumeToken(builder, FLEX_FSLASH);
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
    result = result && consumeTokens(builder, 2, FLEX_ID, FLEX_EQ);
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
    result = result && consumeTokens(builder, 0, FLEX_ID, FLEX_EQ);
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
  //   | option_7bit
  //   | option_16bit
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
    if (!result) result = option_7bit(builder, level + 1);
    if (!result) result = option_16bit(builder, level + 1);
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
  // '%16bit'
  public static boolean option_16bit(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_16bit")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT16BIT)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT16BIT);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%7bit'
  public static boolean option_7bit(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_7bit")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_7BIT)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_ABSTRACT)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_APIPRIVATE)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_BUFFER)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeTokens(builder, 1, FLEX_OPT_BUFFER, FLEX_NUMBER);
    pinned = result; // pin = 1
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%byacc'
  public static boolean option_byacc(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_byacc")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, "%byacc");
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%class' java_type
  public static boolean option_class(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_class")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_CLASS)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_CHAR)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_COLUMN)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_LINE)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_CTORARG)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_CUP)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_CUPDEBUG)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_CUPSYM)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_DEBUG)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_EOFCLOSE)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_EXTENDS)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_FINAL)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_FUNCTION)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeTokens(builder, 1, FLEX_OPT_FUNCTION, FLEX_ID);
    pinned = result; // pin = 1
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_IMPLEMENTS)) return false;
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
    if (!nextTokenIs(builder, "<include>", FLEX_OPT_INCLUDE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<include>");
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_INTWRAP)) return false;
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
  // '%public'
  public static boolean option_public(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_public")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_PUBLIC)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_SCANERROR)) return false;
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
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_STANDALONE)) return false;
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
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, "%switch");
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%table'
  public static boolean option_table(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_table")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, "%table");
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%type' java_type
  public static boolean option_type(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_type")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_TYPE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_TYPE);
    pinned = result; // pin = 1
    result = result && java_type(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%unicode' [number | version]
  public static boolean option_unicode(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_unicode")) return false;
    if (!nextTokenIs(builder, "<option>", FLEX_OPT_UNICODE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(builder, FLEX_OPT_UNICODE);
    pinned = result; // pin = 1
    result = result && option_unicode_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // [number | version]
  private static boolean option_unicode_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_unicode_1")) return false;
    option_unicode_1_0(builder, level + 1);
    return true;
  }

  // number | version
  private static boolean option_unicode_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_unicode_1_0")) return false;
    boolean result;
    result = consumeToken(builder, FLEX_NUMBER);
    if (!result) result = consumeToken(builder, FLEX_VERSION);
    return result;
  }

  /* ********************************************************** */
  // <<line '%{'>> java_code <<line '%}'>>
  public static boolean option_user_code(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "option_user_code")) return false;
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
    result = rule_group(builder, level + 1);
    if (!result) result = rule_tail(builder, level + 1);
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
    result = consumeTokens(builder, 0, FLEX_BRACE1, FLEX_ID, FLEX_BRACE2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // rule_group_item +
  private static boolean rule_group_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_group_2")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = rule_group_item(builder, level + 1);
    while (result) {
      int pos = current_position_(builder);
      if (!rule_group_item(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "rule_group_2", pos)) break;
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
    if (!result) result = consumeToken(builder, FLEX_HAT);
    if (!result) result = new_line(builder, level + 1);
    if (!result) result = expression(builder, level + 1, 4);
    exit_section_(builder, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // rule_tail1 | rule_tail2
  static boolean rule_tail(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "rule_tail")) return false;
    boolean result;
    result = rule_tail1(builder, level + 1);
    if (!result) result = rule_tail2(builder, level + 1);
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
    consumeToken(builder, FLEX_HAT);
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
  // [] new_line '%%' new_line
  static boolean section_div(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "section_div")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = section_div_0(builder, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, new_line(builder, level + 1));
    result = pinned && report_error_(builder, consumeToken(builder, FLEX_TWO_PERCS)) && result;
    result = pinned && new_line(builder, level + 1) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // []
  private static boolean section_div_0(PsiBuilder builder, int level) {
    return true;
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
  // &('!' | '(' | '.' | '[' | '~'
  //   | char_or_esc | char_class | number | string
  //   | '{' id | !new_line id )
  static boolean sequence_op(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _AND_, null, "<expression>");
    result = sequence_op_0(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  // '!' | '(' | '.' | '[' | '~'
  //   | char_or_esc | char_class | number | string
  //   | '{' id | !new_line id
  private static boolean sequence_op_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_BANG);
    if (!result) result = consumeToken(builder, FLEX_PAREN1);
    if (!result) result = consumeToken(builder, FLEX_DOT);
    if (!result) result = consumeToken(builder, FLEX_BRACK1);
    if (!result) result = consumeToken(builder, FLEX_TILDE);
    if (!result) result = char_or_esc(builder, level + 1);
    if (!result) result = consumeToken(builder, FLEX_CHAR_CLASS);
    if (!result) result = consumeToken(builder, FLEX_NUMBER);
    if (!result) result = consumeToken(builder, FLEX_STRING);
    if (!result) result = parseTokens(builder, 0, FLEX_BRACE1, FLEX_ID);
    if (!result) result = sequence_op_0_10(builder, level + 1);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !new_line id
  private static boolean sequence_op_0_10(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0_10")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = sequence_op_0_10_0(builder, level + 1);
    result = result && consumeToken(builder, FLEX_ID);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // !new_line
  private static boolean sequence_op_0_10_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "sequence_op_0_10_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NOT_);
    result = !new_line(builder, level + 1);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // ('%state' | '%xstate') state_definition ((','? !(id '=')) state_definition) * ','?
  public static boolean state_declaration(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration")) return false;
    if (!nextTokenIs(builder, "<state declaration>", FLEX_OPT_STATE, FLEX_OPT_XSTATE)) return false;
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

  // '%state' | '%xstate'
  private static boolean state_declaration_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_0")) return false;
    boolean result;
    Marker marker = enter_section_(builder);
    result = consumeToken(builder, FLEX_OPT_STATE);
    if (!result) result = consumeToken(builder, FLEX_OPT_XSTATE);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // ((','? !(id '=')) state_definition) *
  private static boolean state_declaration_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_declaration_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!state_declaration_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "state_declaration_2", pos)) break;
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
    result = consumeTokens(builder, 0, FLEX_ID, FLEX_EQ);
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
  public static boolean state_list(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "state_list")) return false;
    if (!nextTokenIs(builder, FLEX_ANGLE1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_STATE_LIST, null);
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
    while (true) {
      int pos = current_position_(builder);
      if (!state_list_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "state_list_2", pos)) break;
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
      if (priority < 0 && consumeTokenSmart(builder, FLEX_BAR)) {
        while (true) {
          result = report_error_(builder, expression(builder, level, 0));
          if (!consumeTokenSmart(builder, FLEX_BAR)) break;
        }
        exit_section_(builder, level, marker, FLEX_CHOICE_EXPRESSION, result, true, null);
      }
      else if (priority < 1 && sequence_op(builder, level + 1)) {
        int pos = current_position_(builder);
        while (true) {
          result = report_error_(builder, expression(builder, level, 1));
          if (!sequence_op(builder, level + 1)) break;
          if (!empty_element_parsed_guard_(builder, "sequence_expression", pos)) break;
          pos = current_position_(builder);
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
    if (!nextTokenIsSmart(builder, FLEX_BANG, FLEX_TILDE)) return false;
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
    result = consumeTokenSmart(builder, FLEX_BANG);
    if (!result) result = consumeTokenSmart(builder, FLEX_TILDE);
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
    result = consumeTokensSmart(builder, 0, FLEX_BRACE1, FLEX_NUMBER);
    result = result && quantifier_expression_0_3_2(builder, level + 1);
    result = result && consumeToken(builder, FLEX_BRACE2);
    exit_section_(builder, marker, null, result);
    return result;
  }

  // [ ',' number]
  private static boolean quantifier_expression_0_3_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "quantifier_expression_0_3_2")) return false;
    parseTokensSmart(builder, 0, FLEX_COMMA, FLEX_NUMBER);
    return true;
  }

  // '[' '^'? (char_class_item (char_class_op char_class_item)* )* ']'
  public static boolean class_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression")) return false;
    if (!nextTokenIsSmart(builder, FLEX_BRACK1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_CLASS_EXPRESSION, "<expression>");
    result = consumeTokenSmart(builder, FLEX_BRACK1);
    pinned = result; // pin = 1
    result = result && report_error_(builder, class_expression_1(builder, level + 1));
    result = pinned && report_error_(builder, class_expression_2(builder, level + 1)) && result;
    result = pinned && consumeToken(builder, FLEX_BRACK2) && result;
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // '^'?
  private static boolean class_expression_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_1")) return false;
    consumeTokenSmart(builder, FLEX_HAT);
    return true;
  }

  // (char_class_item (char_class_op char_class_item)* )*
  private static boolean class_expression_2(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_2")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!class_expression_2_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "class_expression_2", pos)) break;
    }
    return true;
  }

  // char_class_item (char_class_op char_class_item)*
  private static boolean class_expression_2_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_2_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = char_class_item(builder, level + 1);
    pinned = result; // pin = 1
    result = result && class_expression_2_0_1(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (char_class_op char_class_item)*
  private static boolean class_expression_2_0_1(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_2_0_1")) return false;
    while (true) {
      int pos = current_position_(builder);
      if (!class_expression_2_0_1_0(builder, level + 1)) break;
      if (!empty_element_parsed_guard_(builder, "class_expression_2_0_1", pos)) break;
    }
    return true;
  }

  // char_class_op char_class_item
  private static boolean class_expression_2_0_1_0(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "class_expression_2_0_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(builder, level, _NONE_);
    result = char_class_op(builder, level + 1);
    pinned = result; // pin = 1
    result = result && char_class_item(builder, level + 1);
    exit_section_(builder, level, marker, result, pinned, null);
    return result || pinned;
  }

  // char_class | '.'
  public static boolean predefined_class_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "predefined_class_expression")) return false;
    if (!nextTokenIsSmart(builder, FLEX_CHAR_CLASS, FLEX_DOT)) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_PREDEFINED_CLASS_EXPRESSION, "<expression>");
    result = consumeTokenSmart(builder, FLEX_CHAR_CLASS);
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

  // string | id | char_or_esc | number
  public static boolean literal_expression(PsiBuilder builder, int level) {
    if (!recursion_guard_(builder, level, "literal_expression")) return false;
    boolean result;
    Marker marker = enter_section_(builder, level, _NONE_, FLEX_LITERAL_EXPRESSION, "<expression>");
    result = consumeTokenSmart(builder, FLEX_STRING);
    if (!result) result = consumeTokenSmart(builder, FLEX_ID);
    if (!result) result = char_or_esc(builder, level + 1);
    if (!result) result = consumeTokenSmart(builder, FLEX_NUMBER);
    exit_section_(builder, level, marker, result, false, null);
    return result;
  }

  static final Parser declaration_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return declaration_recover(builder, level + 1);
    }
  };
  static final Parser macro_definition_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return macro_definition_recover(builder, level + 1);
    }
  };
  static final Parser opt_code1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_CODE1);
    }
  };
  static final Parser opt_code2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_CODE2);
    }
  };
  static final Parser opt_eof1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOF1);
    }
  };
  static final Parser opt_eof2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOF2);
    }
  };
  static final Parser opt_eofthrow1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFTHROW1);
    }
  };
  static final Parser opt_eofthrow2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFTHROW2);
    }
  };
  static final Parser opt_eofval1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFVAL1);
    }
  };
  static final Parser opt_eofval2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_EOFVAL2);
    }
  };
  static final Parser opt_init1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INIT1);
    }
  };
  static final Parser opt_init2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INIT2);
    }
  };
  static final Parser opt_initthrow1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INITTHROW1);
    }
  };
  static final Parser opt_initthrow2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_INITTHROW2);
    }
  };
  static final Parser opt_yylexthrow1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_YYLEXTHROW1);
    }
  };
  static final Parser opt_yylexthrow2_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return consumeToken(builder, FLEX_OPT_YYLEXTHROW2);
    }
  };
  static final Parser rule_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return rule_recover(builder, level + 1);
    }
  };
  static final Parser section_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return section_recover(builder, level + 1);
    }
  };
  static final Parser user_value_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder, int level) {
      return user_value_0_0(builder, level + 1);
    }
  };
}