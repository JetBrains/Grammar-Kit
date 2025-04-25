// ---- org/intellij/jflex/parser/JFlexParser.java -----------------
// license.txt
package org.intellij.jflex.parser;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static org.intellij.jflex.parser.JFlexParserUtil.*;
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
public class JFlexParser {

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
    return flex_file(runtime, level + 1);
  }

  public static final SyntaxElementTypeSet[] EXTENDS_SETS_ = new SyntaxElementTypeSet[] {
    create_token_set_(FLEX_DECLARATIONS_SECTION, FLEX_LEXICAL_RULES_SECTION, FLEX_USER_CODE_SECTION),
    create_token_set_(FLEX_CHAR_RANGE, FLEX_CHOICE_EXPRESSION, FLEX_CLASS_EXPRESSION, FLEX_EXPRESSION,
      FLEX_LITERAL_EXPRESSION, FLEX_MACRO_REF_EXPRESSION, FLEX_NOT_EXPRESSION, FLEX_PAREN_EXPRESSION,
      FLEX_PREDEFINED_CLASS_EXPRESSION, FLEX_QUANTIFIER_EXPRESSION, FLEX_SEQUENCE_EXPRESSION),
  };

  /* ********************************************************** */
  // '{' raw? '}' | '|'
  public static boolean action(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "action")) return false;
    if (!nextTokenIs(runtime, "<action>", FLEX_BAR, FLEX_BRACE1)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_JAVA_CODE, "<action>");
    result = action_0(runtime, level + 1);
    if (!result) result = consumeToken(runtime, FLEX_BAR);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '{' raw? '}'
  private static boolean action_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "action_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_BRACE1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, action_0_1(runtime, level + 1));
    result = pinned && consumeToken(runtime, FLEX_BRACE2) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // raw?
  private static boolean action_0_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "action_0_1")) return false;
    consumeToken(runtime, FLEX_RAW);
    return true;
  }

  /* ********************************************************** */
  // string | char_class | char_or_esc
  static boolean char_class_atom(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "char_class_atom")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, null, "<char>");
    result = consumeToken(runtime, FLEX_STRING);
    if (!result) result = consumeToken(runtime, FLEX_CHAR_CLASS);
    if (!result) result = char_or_esc(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // char_range | class_expression | macro_ref_expression | char_class_atom
  static boolean char_class_item(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "char_class_item")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, null, "<char>");
    result = char_range(runtime, level + 1);
    if (!result) result = class_expression(runtime, level + 1);
    if (!result) result = macro_ref_expression(runtime, level + 1);
    if (!result) result = char_class_atom(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '&&' | '||' | '~~' | '--'
  static boolean char_class_op(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "char_class_op")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, null, "<char>");
    result = consumeToken(runtime, FLEX_AMPAMP);
    if (!result) result = consumeToken(runtime, FLEX_BARBAR);
    if (!result) result = consumeToken(runtime, FLEX_TILDETILDE);
    if (!result) result = consumeToken(runtime, FLEX_DASHDASH);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // char | char_esc
  static boolean char_or_esc(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "char_or_esc")) return false;
    if (!nextTokenIs(runtime, "", FLEX_CHAR, FLEX_CHAR_ESC)) return false;
    boolean result;
    result = consumeToken(runtime, FLEX_CHAR);
    if (!result) result = consumeToken(runtime, FLEX_CHAR_ESC);
    return result;
  }

  /* ********************************************************** */
  // char_or_esc '-' char_or_esc
  public static boolean char_range(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "char_range")) return false;
    if (!nextTokenIs(runtime, "<char>", FLEX_CHAR, FLEX_CHAR_ESC)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_CHAR_RANGE, "<char>");
    result = char_or_esc(runtime, level + 1);
    result = result && consumeToken(runtime, FLEX_DASH);
    result = result && char_or_esc(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // macro_definition | state_declaration | option
  static boolean declaration(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declaration")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = macro_definition(runtime, level + 1);
    if (!result) result = state_declaration(runtime, level + 1);
    if (!result) result = option(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, JFlexParser::declaration_recover);
    return result;
  }

  /* ********************************************************** */
  // !(<<is_percent>> | id '=') section_recover
  static boolean declaration_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declaration_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = declaration_recover_0(runtime, level + 1);
    result = result && section_recover(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !(<<is_percent>> | id '=')
  private static boolean declaration_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declaration_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !declaration_recover_0_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // <<is_percent>> | id '='
  private static boolean declaration_recover_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declaration_recover_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = is_percent(runtime, level + 1);
    if (!result) result = parseTokens(runtime, 0, FLEX_ID, FLEX_EQ);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // [] declaration (!(<<eof>> | '%%') declaration) *
  public static boolean declarations_section(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declarations_section")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_DECLARATIONS_SECTION, "<declarations section>");
    result = declarations_section_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, declaration(runtime, level + 1));
    result = pinned && declarations_section_2(runtime, level + 1) && result;
    exit_section_(runtime, level, marker, result, pinned, JFlexParser::section_recover);
    return result || pinned;
  }

  // []
  private static boolean declarations_section_0(SyntaxGeneratedParserRuntime runtime, int level) {
    return true;
  }

  // (!(<<eof>> | '%%') declaration) *
  private static boolean declarations_section_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declarations_section_2")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!declarations_section_2_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "declarations_section_2", pos)) break;
    }
    return true;
  }

  // !(<<eof>> | '%%') declaration
  private static boolean declarations_section_2_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declarations_section_2_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = declarations_section_2_0_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && declaration(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // !(<<eof>> | '%%')
  private static boolean declarations_section_2_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declarations_section_2_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !declarations_section_2_0_0_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // <<eof>> | '%%'
  private static boolean declarations_section_2_0_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "declarations_section_2_0_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = eof(runtime, level + 1);
    if (!result) result = consumeToken(runtime, FLEX_TWO_PERCS);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // []
  //   user_code_section
  //   section_div
  //   declarations_section
  //   section_div
  //   lexical_rules_section
  static boolean flex_file(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "flex_file")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = flex_file_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, user_code_section(runtime, level + 1));
    result = pinned && report_error_(runtime, section_div(runtime, level + 1)) && result;
    result = pinned && report_error_(runtime, declarations_section(runtime, level + 1)) && result;
    result = pinned && report_error_(runtime, section_div(runtime, level + 1)) && result;
    result = pinned && lexical_rules_section(runtime, level + 1) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // []
  private static boolean flex_file_0(SyntaxGeneratedParserRuntime runtime, int level) {
    return true;
  }

  /* ********************************************************** */
  // raw?
  public static boolean java_code(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_code")) return false;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_JAVA_CODE, "<java code>");
    consumeToken(runtime, FLEX_RAW);
    exit_section_(runtime, level, marker, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // id ( '.' id ) *
  public static boolean java_name(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_name")) return false;
    if (!nextTokenIs(runtime, FLEX_ID)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_JAVA_NAME, null);
    result = consumeToken(runtime, FLEX_ID);
    pinned = result; // pin = 1
    result = result && java_name_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // ( '.' id ) *
  private static boolean java_name_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_name_1")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!java_name_1_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "java_name_1", pos)) break;
    }
    return true;
  }

  // '.' id
  private static boolean java_name_1_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_name_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeTokens(runtime, 1, FLEX_DOT, FLEX_ID);
    pinned = result; // pin = 1
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // java_name java_type_parameters?
  public static boolean java_type(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_JAVA_TYPE, "<java type>");
    result = java_name(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && java_type_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, JFlexParser::type_recover);
    return result || pinned;
  }

  // java_type_parameters?
  private static boolean java_type_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_1")) return false;
    java_type_parameters(runtime, level + 1);
    return true;
  }

  /* ********************************************************** */
  // [java_type (',' java_type) *]
  static boolean java_type_list(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_list")) return false;
    java_type_list_0(runtime, level + 1);
    return true;
  }

  // java_type (',' java_type) *
  private static boolean java_type_list_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_list_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = java_type(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && java_type_list_0_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (',' java_type) *
  private static boolean java_type_list_0_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_list_0_1")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!java_type_list_0_1_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "java_type_list_0_1", pos)) break;
    }
    return true;
  }

  // ',' java_type
  private static boolean java_type_list_0_1_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_list_0_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_COMMA);
    pinned = result; // pin = 1
    result = result && java_type(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '<' !'>' java_type (',' java_type) * '>'
  public static boolean java_type_parameters(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_parameters")) return false;
    if (!nextTokenIs(runtime, FLEX_ANGLE1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_JAVA_TYPE_PARAMETERS, null);
    result = consumeToken(runtime, FLEX_ANGLE1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_type_parameters_1(runtime, level + 1));
    result = pinned && report_error_(runtime, java_type(runtime, level + 1)) && result;
    result = pinned && report_error_(runtime, java_type_parameters_3(runtime, level + 1)) && result;
    result = pinned && consumeToken(runtime, FLEX_ANGLE2) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // !'>'
  private static boolean java_type_parameters_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_parameters_1")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !consumeToken(runtime, FLEX_ANGLE2);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // (',' java_type) *
  private static boolean java_type_parameters_3(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_parameters_3")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!java_type_parameters_3_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "java_type_parameters_3", pos)) break;
    }
    return true;
  }

  // ',' java_type
  private static boolean java_type_parameters_3_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "java_type_parameters_3_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_COMMA);
    pinned = result; // pin = 1
    result = result && java_type(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // [] rule_group_item +
  public static boolean lexical_rules_section(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "lexical_rules_section")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_LEXICAL_RULES_SECTION, "<lexical rules section>");
    result = lexical_rules_section_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && lexical_rules_section_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, JFlexParser::section_recover);
    return result || pinned;
  }

  // []
  private static boolean lexical_rules_section_0(SyntaxGeneratedParserRuntime runtime, int level) {
    return true;
  }

  // rule_group_item +
  private static boolean lexical_rules_section_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "lexical_rules_section_1")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = rule_group_item(runtime, level + 1);
    while (result) {
      int pos = current_position_(runtime);
      if (!rule_group_item(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "lexical_rules_section_1", pos)) break;
    }
    exit_section_(runtime, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // new_line <<p>> new_line
  static boolean line(SyntaxGeneratedParserRuntime runtime, int level, SyntaxGeneratedParserRuntime.Parser aP) {
    if (!recursion_guard_(runtime, level, "line")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = new_line(runtime, level + 1);
    result = result && aP.parse(runtime, level);
    pinned = result; // pin = 2
    result = result && new_line(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '$' | '/' expression
  public static boolean look_ahead(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "look_ahead")) return false;
    if (!nextTokenIs(runtime, "<look ahead>", FLEX_DOLLAR, FLEX_FSLASH)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_LOOK_AHEAD, "<look ahead>");
    result = consumeToken(runtime, FLEX_DOLLAR);
    if (!result) result = look_ahead_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '/' expression
  private static boolean look_ahead_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "look_ahead_1")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, FLEX_FSLASH);
    result = result && expression(runtime, level + 1, -1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // new_line id '=' expression
  public static boolean macro_definition(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "macro_definition")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_MACRO_DEFINITION, "<macro definition>");
    result = new_line(runtime, level + 1);
    result = result && consumeTokens(runtime, 2, FLEX_ID, FLEX_EQ);
    pinned = result; // pin = 3
    result = result && expression(runtime, level + 1, -1);
    exit_section_(runtime, level, marker, result, pinned, JFlexParser::macro_definition_recover);
    return result || pinned;
  }

  /* ********************************************************** */
  // !(new_line id '=' | '%%' | <<is_percent>>)
  static boolean macro_definition_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "macro_definition_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !macro_definition_recover_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // new_line id '=' | '%%' | <<is_percent>>
  private static boolean macro_definition_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "macro_definition_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = macro_definition_recover_0_0(runtime, level + 1);
    if (!result) result = consumeToken(runtime, FLEX_TWO_PERCS);
    if (!result) result = is_percent(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // new_line id '='
  private static boolean macro_definition_recover_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "macro_definition_recover_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = new_line(runtime, level + 1);
    result = result && consumeTokens(runtime, 0, FLEX_ID, FLEX_EQ);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // id
  public static boolean macro_reference(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "macro_reference")) return false;
    if (!nextTokenIs(runtime, FLEX_ID)) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, FLEX_ID);
    exit_section_(runtime, marker, FLEX_MACRO_REFERENCE, result);
    return result;
  }

  /* ********************************************************** */
  // &<<is_new_line>>
  static boolean new_line(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "new_line")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _AND_);
    result = is_new_line(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
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
  public static boolean option(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _COLLAPSE_, FLEX_OPTION, "<option>");
    result = option_class(runtime, level + 1);
    if (!result) result = option_implements(runtime, level + 1);
    if (!result) result = option_extends(runtime, level + 1);
    if (!result) result = option_public(runtime, level + 1);
    if (!result) result = option_final(runtime, level + 1);
    if (!result) result = option_abstract(runtime, level + 1);
    if (!result) result = option_api_private(runtime, level + 1);
    if (!result) result = option_user_code(runtime, level + 1);
    if (!result) result = option_init(runtime, level + 1);
    if (!result) result = option_init_throw(runtime, level + 1);
    if (!result) result = option_ctor_arg(runtime, level + 1);
    if (!result) result = option_scan_error(runtime, level + 1);
    if (!result) result = option_buffer_size(runtime, level + 1);
    if (!result) result = option_include(runtime, level + 1);
    if (!result) result = option_function(runtime, level + 1);
    if (!result) result = option_integer(runtime, level + 1);
    if (!result) result = option_intwrap(runtime, level + 1);
    if (!result) result = option_type(runtime, level + 1);
    if (!result) result = option_yylexthrow(runtime, level + 1);
    if (!result) result = option_eof_val(runtime, level + 1);
    if (!result) result = option_eof(runtime, level + 1);
    if (!result) result = option_eof_throw(runtime, level + 1);
    if (!result) result = option_eof_close(runtime, level + 1);
    if (!result) result = option_debug(runtime, level + 1);
    if (!result) result = option_standalone(runtime, level + 1);
    if (!result) result = option_cup(runtime, level + 1);
    if (!result) result = option_cup_sym(runtime, level + 1);
    if (!result) result = option_cup_debug(runtime, level + 1);
    if (!result) result = option_byacc(runtime, level + 1);
    if (!result) result = option_switch(runtime, level + 1);
    if (!result) result = option_table(runtime, level + 1);
    if (!result) result = option_7bit(runtime, level + 1);
    if (!result) result = option_16bit(runtime, level + 1);
    if (!result) result = option_full(runtime, level + 1);
    if (!result) result = option_unicode(runtime, level + 1);
    if (!result) result = option_ignore_case(runtime, level + 1);
    if (!result) result = option_count_char(runtime, level + 1);
    if (!result) result = option_count_line(runtime, level + 1);
    if (!result) result = option_count_column(runtime, level + 1);
    if (!result) result = option_obsolete(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%16bit'
  public static boolean option_16bit(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_16bit")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT16BIT)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT16BIT);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%7bit'
  public static boolean option_7bit(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_7bit")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_7BIT)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_7BIT);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%abstract'
  public static boolean option_abstract(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_abstract")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_ABSTRACT)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_ABSTRACT);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%apiprivate'
  public static boolean option_api_private(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_api_private")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_APIPRIVATE)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_APIPRIVATE);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%buffer' number
  public static boolean option_buffer_size(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_buffer_size")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_BUFFER)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeTokens(runtime, 1, FLEX_OPT_BUFFER, FLEX_NUMBER);
    pinned = result; // pin = 1
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%byacc'
  public static boolean option_byacc(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_byacc")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, "%byacc");
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%class' java_type
  public static boolean option_class(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_class")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_CLASS)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_CLASS);
    pinned = result; // pin = 1
    result = result && java_type(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%char'
  public static boolean option_count_char(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_count_char")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_CHAR)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_CHAR);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%column'
  public static boolean option_count_column(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_count_column")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_COLUMN)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_COLUMN);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%line'
  public static boolean option_count_line(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_count_line")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_LINE)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_LINE);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%ctorarg' java_type id
  public static boolean option_ctor_arg(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_ctor_arg")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_CTORARG)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_CTORARG);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_type(runtime, level + 1));
    result = pinned && consumeToken(runtime, FLEX_ID) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%cup'
  public static boolean option_cup(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_cup")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_CUP)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_CUP);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%cupdebug'
  public static boolean option_cup_debug(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_cup_debug")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_CUPDEBUG)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_CUPDEBUG);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%cupsym' java_type
  public static boolean option_cup_sym(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_cup_sym")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_CUPSYM)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_CUPSYM);
    pinned = result; // pin = 1
    result = result && java_type(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%debug'
  public static boolean option_debug(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_debug")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_DEBUG)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_DEBUG);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // <<line '%eof{'>> java_code <<line '%eof}'>>
  public static boolean option_eof(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_eof")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(runtime, level + 1, opt_eof1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_code(runtime, level + 1));
    result = pinned && line(runtime, level + 1, opt_eof2_parser_) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%eofclose' ['false']
  public static boolean option_eof_close(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_eof_close")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_EOFCLOSE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_EOFCLOSE);
    pinned = result; // pin = 1
    result = result && option_eof_close_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // ['false']
  private static boolean option_eof_close_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_eof_close_1")) return false;
    consumeToken(runtime, "false");
    return true;
  }

  /* ********************************************************** */
  // '%eofthrow' java_type_list | <<line '%eofthrow{'>> java_type_list <<line '%eofthrow}'>>
  public static boolean option_eof_throw(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_eof_throw")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = option_eof_throw_0(runtime, level + 1);
    if (!result) result = option_eof_throw_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '%eofthrow' java_type_list
  private static boolean option_eof_throw_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_eof_throw_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_OPT_EOFTHROW);
    pinned = result; // pin = 1
    result = result && java_type_list(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // <<line '%eofthrow{'>> java_type_list <<line '%eofthrow}'>>
  private static boolean option_eof_throw_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_eof_throw_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = line(runtime, level + 1, opt_eofthrow1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_type_list(runtime, level + 1));
    result = pinned && line(runtime, level + 1, opt_eofthrow2_parser_) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // <<line '%eofval{'>> java_code <<line '%eofval}'>>
  public static boolean option_eof_val(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_eof_val")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(runtime, level + 1, opt_eofval1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_code(runtime, level + 1));
    result = pinned && line(runtime, level + 1, opt_eofval2_parser_) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%extends' java_type
  public static boolean option_extends(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_extends")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_EXTENDS)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_EXTENDS);
    pinned = result; // pin = 1
    result = result && java_type(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%final'
  public static boolean option_final(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_final")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_FINAL)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_FINAL);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%full' | '%8bit'
  public static boolean option_full(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_full")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_8BIT, FLEX_OPT_FULL)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_FULL);
    if (!result) result = consumeToken(runtime, FLEX_OPT_8BIT);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%function' id
  public static boolean option_function(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_function")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_FUNCTION)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeTokens(runtime, 1, FLEX_OPT_FUNCTION, FLEX_ID);
    pinned = result; // pin = 1
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%caseless' | '%ignorecase'
  public static boolean option_ignore_case(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_ignore_case")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_CASELESS, FLEX_OPT_IGNORECASE)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_CASELESS);
    if (!result) result = consumeToken(runtime, FLEX_OPT_IGNORECASE);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%implements' java_type_list
  public static boolean option_implements(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_implements")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_IMPLEMENTS)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_IMPLEMENTS);
    pinned = result; // pin = 1
    result = result && java_type_list(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%include' user_value
  public static boolean option_include(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_include")) return false;
    if (!nextTokenIs(runtime, "<include>", FLEX_OPT_INCLUDE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<include>");
    result = consumeToken(runtime, FLEX_OPT_INCLUDE);
    pinned = result; // pin = 1
    result = result && user_value(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // <<line '%init{'>> java_code <<line '%init}'>>
  public static boolean option_init(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_init")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(runtime, level + 1, opt_init1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_code(runtime, level + 1));
    result = pinned && line(runtime, level + 1, opt_init2_parser_) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%initthrow' java_type_list | <<line '%initthrow{'>> java_type_list <<line '%initthrow}'>>
  public static boolean option_init_throw(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_init_throw")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = option_init_throw_0(runtime, level + 1);
    if (!result) result = option_init_throw_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '%initthrow' java_type_list
  private static boolean option_init_throw_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_init_throw_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_OPT_INITTHROW);
    pinned = result; // pin = 1
    result = result && java_type_list(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // <<line '%initthrow{'>> java_type_list <<line '%initthrow}'>>
  private static boolean option_init_throw_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_init_throw_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = line(runtime, level + 1, opt_initthrow1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_type_list(runtime, level + 1));
    result = pinned && line(runtime, level + 1, opt_initthrow2_parser_) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%integer' | '%int'
  public static boolean option_integer(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_integer")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_INT, FLEX_OPT_INTEGER)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_INTEGER);
    if (!result) result = consumeToken(runtime, FLEX_OPT_INT);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%intwrap'
  public static boolean option_intwrap(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_intwrap")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_INTWRAP)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_INTWRAP);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%notunix' | '%yyeof'
  public static boolean option_obsolete(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_obsolete")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_NOTUNIX, FLEX_OPT_YYEOF)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_NOTUNIX);
    if (!result) result = consumeToken(runtime, FLEX_OPT_YYEOF);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%public'
  public static boolean option_public(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_public")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_PUBLIC)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_PUBLIC);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%scanerror' java_type
  public static boolean option_scan_error(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_scan_error")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_SCANERROR)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_SCANERROR);
    pinned = result; // pin = 1
    result = result && java_type(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%standalone'
  public static boolean option_standalone(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_standalone")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_STANDALONE)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_STANDALONE);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%switch'
  public static boolean option_switch(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_switch")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, "%switch");
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%table'
  public static boolean option_table(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_table")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, "%table");
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // '%type' java_type
  public static boolean option_type(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_type")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_TYPE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_TYPE);
    pinned = result; // pin = 1
    result = result && java_type(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%unicode' [number | version]
  public static boolean option_unicode(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_unicode")) return false;
    if (!nextTokenIs(runtime, "<option>", FLEX_OPT_UNICODE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = consumeToken(runtime, FLEX_OPT_UNICODE);
    pinned = result; // pin = 1
    result = result && option_unicode_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // [number | version]
  private static boolean option_unicode_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_unicode_1")) return false;
    option_unicode_1_0(runtime, level + 1);
    return true;
  }

  // number | version
  private static boolean option_unicode_1_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_unicode_1_0")) return false;
    boolean result;
    result = consumeToken(runtime, FLEX_NUMBER);
    if (!result) result = consumeToken(runtime, FLEX_VERSION);
    return result;
  }

  /* ********************************************************** */
  // <<line '%{'>> java_code <<line '%}'>>
  public static boolean option_user_code(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_user_code")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = line(runtime, level + 1, opt_code1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_code(runtime, level + 1));
    result = pinned && line(runtime, level + 1, opt_code2_parser_) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // '%yylexthrow' java_type_list | <<line '%yylexthrow{'>> java_type_list <<line '%yylexthrow}'>>
  public static boolean option_yylexthrow(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_yylexthrow")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_OPTION, "<option>");
    result = option_yylexthrow_0(runtime, level + 1);
    if (!result) result = option_yylexthrow_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '%yylexthrow' java_type_list
  private static boolean option_yylexthrow_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_yylexthrow_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_OPT_YYLEXTHROW);
    pinned = result; // pin = 1
    result = result && java_type_list(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // <<line '%yylexthrow{'>> java_type_list <<line '%yylexthrow}'>>
  private static boolean option_yylexthrow_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "option_yylexthrow_1")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = line(runtime, level + 1, opt_yylexthrow1_parser_);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, java_type_list(runtime, level + 1));
    result = pinned && line(runtime, level + 1, opt_yylexthrow2_parser_) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // state_list (rule_group | rule_tail ) | rule_tail
  public static boolean rule(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_RULE, "<rule>");
    result = rule_0(runtime, level + 1);
    if (!result) result = rule_tail(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // state_list (rule_group | rule_tail )
  private static boolean rule_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = state_list(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && rule_0_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // rule_group | rule_tail
  private static boolean rule_0_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_0_1")) return false;
    boolean result;
    result = rule_group(runtime, level + 1);
    if (!result) result = rule_tail(runtime, level + 1);
    return result;
  }

  /* ********************************************************** */
  // !('{' id '}') '{' rule_group_item + '}'
  static boolean rule_group(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_group")) return false;
    if (!nextTokenIs(runtime, FLEX_BRACE1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = rule_group_0(runtime, level + 1);
    result = result && consumeToken(runtime, FLEX_BRACE1);
    pinned = result; // pin = 2
    result = result && report_error_(runtime, rule_group_2(runtime, level + 1));
    result = pinned && consumeToken(runtime, FLEX_BRACE2) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // !('{' id '}')
  private static boolean rule_group_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_group_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !rule_group_0_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '{' id '}'
  private static boolean rule_group_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_group_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeTokens(runtime, 0, FLEX_BRACE1, FLEX_ID, FLEX_BRACE2);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // rule_group_item +
  private static boolean rule_group_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_group_2")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = rule_group_item(runtime, level + 1);
    while (result) {
      int pos = current_position_(runtime);
      if (!rule_group_item(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "rule_group_2", pos)) break;
    }
    exit_section_(runtime, marker, null, result);
    return result;
  }

  /* ********************************************************** */
  // option_include | rule
  static boolean rule_group_item(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_group_item")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = option_include(runtime, level + 1);
    if (!result) result = rule(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, JFlexParser::rule_recover);
    return result;
  }

  /* ********************************************************** */
  // !('}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group)
  static boolean rule_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !rule_recover_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '}' | '.' | '<' | '<<EOF>>' | '^'| new_line | atom_group
  private static boolean rule_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_recover_0")) return false;
    boolean result;
    result = consumeToken(runtime, FLEX_BRACE2);
    if (!result) result = consumeToken(runtime, FLEX_DOT);
    if (!result) result = consumeToken(runtime, FLEX_ANGLE1);
    if (!result) result = consumeToken(runtime, FLEX_EOF);
    if (!result) result = consumeToken(runtime, FLEX_HAT);
    if (!result) result = new_line(runtime, level + 1);
    if (!result) result = expression(runtime, level + 1, 4);
    return result;
  }

  /* ********************************************************** */
  // rule_tail1 | rule_tail2
  static boolean rule_tail(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_tail")) return false;
    boolean result;
    result = rule_tail1(runtime, level + 1);
    if (!result) result = rule_tail2(runtime, level + 1);
    return result;
  }

  /* ********************************************************** */
  // '<<EOF>>' action
  static boolean rule_tail1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_tail1")) return false;
    if (!nextTokenIs(runtime, FLEX_EOF)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_EOF);
    pinned = result; // pin = 1
    result = result && action(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // ['^'] expression look_ahead? action
  static boolean rule_tail2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_tail2")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = rule_tail2_0(runtime, level + 1);
    result = result && expression(runtime, level + 1, -1);
    pinned = result; // pin = 2
    result = result && report_error_(runtime, rule_tail2_2(runtime, level + 1));
    result = pinned && action(runtime, level + 1) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // ['^']
  private static boolean rule_tail2_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_tail2_0")) return false;
    consumeToken(runtime, FLEX_HAT);
    return true;
  }

  // look_ahead?
  private static boolean rule_tail2_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "rule_tail2_2")) return false;
    look_ahead(runtime, level + 1);
    return true;
  }

  /* ********************************************************** */
  // [] new_line '%%' new_line
  static boolean section_div(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "section_div")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = section_div_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, new_line(runtime, level + 1));
    result = pinned && report_error_(runtime, consumeToken(runtime, FLEX_TWO_PERCS)) && result;
    result = pinned && new_line(runtime, level + 1) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // []
  private static boolean section_div_0(SyntaxGeneratedParserRuntime runtime, int level) {
    return true;
  }

  /* ********************************************************** */
  // !'%%'
  static boolean section_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "section_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !consumeToken(runtime, FLEX_TWO_PERCS);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // &('!' | '(' | '.' | '[' | '~'
  //   | char_or_esc | char_class | number | string
  //   | '{' id | !new_line id )
  static boolean sequence_op(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence_op")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _AND_, null, "<expression>");
    result = sequence_op_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '!' | '(' | '.' | '[' | '~'
  //   | char_or_esc | char_class | number | string
  //   | '{' id | !new_line id
  private static boolean sequence_op_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence_op_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, FLEX_BANG);
    if (!result) result = consumeToken(runtime, FLEX_PAREN1);
    if (!result) result = consumeToken(runtime, FLEX_DOT);
    if (!result) result = consumeToken(runtime, FLEX_BRACK1);
    if (!result) result = consumeToken(runtime, FLEX_TILDE);
    if (!result) result = char_or_esc(runtime, level + 1);
    if (!result) result = consumeToken(runtime, FLEX_CHAR_CLASS);
    if (!result) result = consumeToken(runtime, FLEX_NUMBER);
    if (!result) result = consumeToken(runtime, FLEX_STRING);
    if (!result) result = parseTokens(runtime, 0, FLEX_BRACE1, FLEX_ID);
    if (!result) result = sequence_op_0_10(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !new_line id
  private static boolean sequence_op_0_10(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence_op_0_10")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = sequence_op_0_10_0(runtime, level + 1);
    result = result && consumeToken(runtime, FLEX_ID);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !new_line
  private static boolean sequence_op_0_10_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "sequence_op_0_10_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !new_line(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  /* ********************************************************** */
  // ('%state' | '%xstate') state_definition ((','? !(id '=')) state_definition) * ','?
  public static boolean state_declaration(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration")) return false;
    if (!nextTokenIs(runtime, "<state declaration>", FLEX_OPT_STATE, FLEX_OPT_XSTATE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_STATE_DECLARATION, "<state declaration>");
    result = state_declaration_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, state_definition(runtime, level + 1));
    result = pinned && report_error_(runtime, state_declaration_2(runtime, level + 1)) && result;
    result = pinned && state_declaration_3(runtime, level + 1) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // '%state' | '%xstate'
  private static boolean state_declaration_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_0")) return false;
    boolean result;
    result = consumeToken(runtime, FLEX_OPT_STATE);
    if (!result) result = consumeToken(runtime, FLEX_OPT_XSTATE);
    return result;
  }

  // ((','? !(id '=')) state_definition) *
  private static boolean state_declaration_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_2")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!state_declaration_2_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "state_declaration_2", pos)) break;
    }
    return true;
  }

  // (','? !(id '=')) state_definition
  private static boolean state_declaration_2_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_2_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = state_declaration_2_0_0(runtime, level + 1);
    result = result && state_definition(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // ','? !(id '=')
  private static boolean state_declaration_2_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_2_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = state_declaration_2_0_0_0(runtime, level + 1);
    result = result && state_declaration_2_0_0_1(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // ','?
  private static boolean state_declaration_2_0_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_2_0_0_0")) return false;
    consumeToken(runtime, FLEX_COMMA);
    return true;
  }

  // !(id '=')
  private static boolean state_declaration_2_0_0_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_2_0_0_1")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !state_declaration_2_0_0_1_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // id '='
  private static boolean state_declaration_2_0_0_1_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_2_0_0_1_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeTokens(runtime, 0, FLEX_ID, FLEX_EQ);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // ','?
  private static boolean state_declaration_3(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_declaration_3")) return false;
    consumeToken(runtime, FLEX_COMMA);
    return true;
  }

  /* ********************************************************** */
  // id
  public static boolean state_definition(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_definition")) return false;
    if (!nextTokenIs(runtime, FLEX_ID)) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, FLEX_ID);
    exit_section_(runtime, marker, FLEX_STATE_DEFINITION, result);
    return result;
  }

  /* ********************************************************** */
  // '<' state_reference (',' state_reference) * '>'
  public static boolean state_list(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_list")) return false;
    if (!nextTokenIs(runtime, FLEX_ANGLE1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_STATE_LIST, null);
    result = consumeToken(runtime, FLEX_ANGLE1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, state_reference(runtime, level + 1));
    result = pinned && report_error_(runtime, state_list_2(runtime, level + 1)) && result;
    result = pinned && consumeToken(runtime, FLEX_ANGLE2) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (',' state_reference) *
  private static boolean state_list_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_list_2")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!state_list_2_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "state_list_2", pos)) break;
    }
    return true;
  }

  // ',' state_reference
  private static boolean state_list_2_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_list_2_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = consumeToken(runtime, FLEX_COMMA);
    pinned = result; // pin = 1
    result = result && state_reference(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  /* ********************************************************** */
  // id
  public static boolean state_reference(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "state_reference")) return false;
    if (!nextTokenIs(runtime, FLEX_ID)) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeToken(runtime, FLEX_ID);
    exit_section_(runtime, marker, FLEX_STATE_REFERENCE, result);
    return result;
  }

  /* ********************************************************** */
  // !(',' | '>') declaration_recover
  static boolean type_recover(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "type_recover")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = type_recover_0(runtime, level + 1);
    result = result && declaration_recover(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // !(',' | '>')
  private static boolean type_recover_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "type_recover_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !type_recover_0_0(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // ',' | '>'
  private static boolean type_recover_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "type_recover_0_0")) return false;
    boolean result;
    result = consumeToken(runtime, FLEX_COMMA);
    if (!result) result = consumeToken(runtime, FLEX_ANGLE2);
    return result;
  }

  /* ********************************************************** */
  // [] java_code
  public static boolean user_code_section(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "user_code_section")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_USER_CODE_SECTION, "<user code section>");
    result = user_code_section_0(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && java_code(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, JFlexParser::section_recover);
    return result || pinned;
  }

  // []
  private static boolean user_code_section_0(SyntaxGeneratedParserRuntime runtime, int level) {
    return true;
  }

  /* ********************************************************** */
  // <<anything2 !new_line>>
  public static boolean user_value(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "user_value")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_USER_VALUE, "<user value>");
    result = anything2(runtime, level + 1, JFlexParser::user_value_0_0);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // !new_line
  private static boolean user_value_0_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "user_value_0_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NOT_);
    result = !new_line(runtime, level + 1);
    exit_section_(runtime, level, marker, result, false, null);
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
  public static boolean expression(SyntaxGeneratedParserRuntime runtime, int level, int priority) {
    if (!recursion_guard_(runtime, level, "expression")) return false;
    addVariant(runtime, "<expression>");
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, "<expression>");
    result = paren_expression(runtime, level + 1);
    if (!result) result = not_expression(runtime, level + 1);
    if (!result) result = class_expression(runtime, level + 1);
    if (!result) result = predefined_class_expression(runtime, level + 1);
    if (!result) result = macro_ref_expression(runtime, level + 1);
    if (!result) result = literal_expression(runtime, level + 1);
    pinned = result;
    result = result && expression_0(runtime, level + 1, priority);
    exit_section_(runtime, level, marker, null, result, pinned, null);
    return result || pinned;
  }

  public static boolean expression_0(SyntaxGeneratedParserRuntime runtime, int level, int priority) {
    if (!recursion_guard_(runtime, level, "expression_0")) return false;
    boolean result = true;
    while (true) {
      Marker marker = enter_section_(runtime, level, _LEFT_, null);
      if (priority < 0 && consumeTokenSmart(runtime, FLEX_BAR)) {
        while (true) {
          result = report_error_(runtime, expression(runtime, level, 0));
          if (!consumeTokenSmart(runtime, FLEX_BAR)) break;
        }
        exit_section_(runtime, level, marker, FLEX_CHOICE_EXPRESSION, result, true, null);
      }
      else if (priority < 1 && sequence_op(runtime, level + 1)) {
        int pos = current_position_(runtime);
        while (true) {
          result = report_error_(runtime, expression(runtime, level, 1));
          if (!sequence_op(runtime, level + 1)) break;
          if (!empty_element_parsed_guard_(runtime, "sequence_expression", pos)) break;
          pos = current_position_(runtime);
        }
        exit_section_(runtime, level, marker, FLEX_SEQUENCE_EXPRESSION, result, true, null);
      }
      else if (priority < 4 && quantifier_expression_0(runtime, level + 1)) {
        result = true;
        exit_section_(runtime, level, marker, FLEX_QUANTIFIER_EXPRESSION, result, true, null);
      }
      else {
        exit_section_(runtime, level, marker, null, false, false, null);
        break;
      }
    }
    return result;
  }

  // '(' expression ')'
  public static boolean paren_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "paren_expression")) return false;
    if (!nextTokenIsSmart(runtime, FLEX_PAREN1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_PAREN_EXPRESSION, "<expression>");
    result = consumeTokenSmart(runtime, FLEX_PAREN1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, expression(runtime, level + 1, -1));
    result = pinned && consumeToken(runtime, FLEX_PAREN2) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  public static boolean not_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "not_expression")) return false;
    if (!nextTokenIsSmart(runtime, FLEX_BANG, FLEX_TILDE)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, null);
    result = not_expression_0(runtime, level + 1);
    pinned = result;
    result = pinned && expression(runtime, level, 3);
    exit_section_(runtime, level, marker, FLEX_NOT_EXPRESSION, result, pinned, null);
    return result || pinned;
  }

  // '!'|'~'
  private static boolean not_expression_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "not_expression_0")) return false;
    boolean result;
    result = consumeTokenSmart(runtime, FLEX_BANG);
    if (!result) result = consumeTokenSmart(runtime, FLEX_TILDE);
    return result;
  }

  // '*' | '+' | '?' | '{' number [ ',' number] '}'
  private static boolean quantifier_expression_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "quantifier_expression_0")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeTokenSmart(runtime, FLEX_STAR);
    if (!result) result = consumeTokenSmart(runtime, FLEX_PLUS);
    if (!result) result = consumeTokenSmart(runtime, FLEX_QUESTION);
    if (!result) result = quantifier_expression_0_3(runtime, level + 1);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // '{' number [ ',' number] '}'
  private static boolean quantifier_expression_0_3(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "quantifier_expression_0_3")) return false;
    boolean result;
    Marker marker = enter_section_(runtime);
    result = consumeTokensSmart(runtime, 0, FLEX_BRACE1, FLEX_NUMBER);
    result = result && quantifier_expression_0_3_2(runtime, level + 1);
    result = result && consumeToken(runtime, FLEX_BRACE2);
    exit_section_(runtime, marker, null, result);
    return result;
  }

  // [ ',' number]
  private static boolean quantifier_expression_0_3_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "quantifier_expression_0_3_2")) return false;
    parseTokensSmart(runtime, 0, FLEX_COMMA, FLEX_NUMBER);
    return true;
  }

  // '[' '^'? (char_class_item (char_class_op char_class_item)* )* ']'
  public static boolean class_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "class_expression")) return false;
    if (!nextTokenIsSmart(runtime, FLEX_BRACK1)) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_CLASS_EXPRESSION, "<expression>");
    result = consumeTokenSmart(runtime, FLEX_BRACK1);
    pinned = result; // pin = 1
    result = result && report_error_(runtime, class_expression_1(runtime, level + 1));
    result = pinned && report_error_(runtime, class_expression_2(runtime, level + 1)) && result;
    result = pinned && consumeToken(runtime, FLEX_BRACK2) && result;
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // '^'?
  private static boolean class_expression_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "class_expression_1")) return false;
    consumeTokenSmart(runtime, FLEX_HAT);
    return true;
  }

  // (char_class_item (char_class_op char_class_item)* )*
  private static boolean class_expression_2(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "class_expression_2")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!class_expression_2_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "class_expression_2", pos)) break;
    }
    return true;
  }

  // char_class_item (char_class_op char_class_item)*
  private static boolean class_expression_2_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "class_expression_2_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = char_class_item(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && class_expression_2_0_1(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // (char_class_op char_class_item)*
  private static boolean class_expression_2_0_1(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "class_expression_2_0_1")) return false;
    while (true) {
      int pos = current_position_(runtime);
      if (!class_expression_2_0_1_0(runtime, level + 1)) break;
      if (!empty_element_parsed_guard_(runtime, "class_expression_2_0_1", pos)) break;
    }
    return true;
  }

  // char_class_op char_class_item
  private static boolean class_expression_2_0_1_0(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "class_expression_2_0_1_0")) return false;
    boolean result, pinned;
    Marker marker = enter_section_(runtime, level, _NONE_);
    result = char_class_op(runtime, level + 1);
    pinned = result; // pin = 1
    result = result && char_class_item(runtime, level + 1);
    exit_section_(runtime, level, marker, result, pinned, null);
    return result || pinned;
  }

  // char_class | '.'
  public static boolean predefined_class_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "predefined_class_expression")) return false;
    if (!nextTokenIsSmart(runtime, FLEX_CHAR_CLASS, FLEX_DOT)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_PREDEFINED_CLASS_EXPRESSION, "<expression>");
    result = consumeTokenSmart(runtime, FLEX_CHAR_CLASS);
    if (!result) result = consumeTokenSmart(runtime, FLEX_DOT);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // '{' macro_reference '}'
  public static boolean macro_ref_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "macro_ref_expression")) return false;
    if (!nextTokenIsSmart(runtime, FLEX_BRACE1)) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_MACRO_REF_EXPRESSION, "<expression>");
    result = consumeTokenSmart(runtime, FLEX_BRACE1);
    result = result && macro_reference(runtime, level + 1);
    result = result && consumeToken(runtime, FLEX_BRACE2);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  // string | id | char_or_esc | number
  public static boolean literal_expression(SyntaxGeneratedParserRuntime runtime, int level) {
    if (!recursion_guard_(runtime, level, "literal_expression")) return false;
    boolean result;
    Marker marker = enter_section_(runtime, level, _NONE_, FLEX_LITERAL_EXPRESSION, "<expression>");
    result = consumeTokenSmart(runtime, FLEX_STRING);
    if (!result) result = consumeTokenSmart(runtime, FLEX_ID);
    if (!result) result = char_or_esc(runtime, level + 1);
    if (!result) result = consumeTokenSmart(runtime, FLEX_NUMBER);
    exit_section_(runtime, level, marker, result, false, null);
    return result;
  }

  static final SyntaxGeneratedParserRuntime.Parser opt_code1_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_CODE1);
  static final SyntaxGeneratedParserRuntime.Parser opt_code2_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_CODE2);
  static final SyntaxGeneratedParserRuntime.Parser opt_eof1_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_EOF1);
  static final SyntaxGeneratedParserRuntime.Parser opt_eof2_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_EOF2);
  static final SyntaxGeneratedParserRuntime.Parser opt_eofthrow1_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_EOFTHROW1);
  static final SyntaxGeneratedParserRuntime.Parser opt_eofthrow2_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_EOFTHROW2);
  static final SyntaxGeneratedParserRuntime.Parser opt_eofval1_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_EOFVAL1);
  static final SyntaxGeneratedParserRuntime.Parser opt_eofval2_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_EOFVAL2);
  static final SyntaxGeneratedParserRuntime.Parser opt_init1_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_INIT1);
  static final SyntaxGeneratedParserRuntime.Parser opt_init2_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_INIT2);
  static final SyntaxGeneratedParserRuntime.Parser opt_initthrow1_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_INITTHROW1);
  static final SyntaxGeneratedParserRuntime.Parser opt_initthrow2_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_INITTHROW2);
  static final SyntaxGeneratedParserRuntime.Parser opt_yylexthrow1_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_YYLEXTHROW1);
  static final SyntaxGeneratedParserRuntime.Parser opt_yylexthrow2_parser_ = (runtime, level) -> consumeToken(runtime, FLEX_OPT_YYLEXTHROW2);
}