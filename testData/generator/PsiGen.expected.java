// ---- PsiGen.java -----------------
//header.txt
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static PsiGenUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGen implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
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
    return grammar_root(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ROOT, ROOT_B, ROOT_C, ROOT_D),
    create_token_set_(A_STATEMENT, B_STATEMENT, C_STATEMENT, STATEMENT),
    create_token_set_(CAST_EXPR, CHOICE_JOINED, EXPR, ID_EXPR,
      ITEM_EXPR, LITERAL, MISSING_EXTERNAL_TYPE, MUL_EXPR,
      PLUS_EXPR, REF_EXPR, SOME_EXPR, SPECIAL_REF),
  };

  /* ********************************************************** */
  // b_expr plus_expr *
  static boolean a_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr")) return false;
    if (!nextTokenIs(builder_, "", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = b_expr(builder_, level_ + 1);
    result_ = result_ && a_expr_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // plus_expr *
  private static boolean a_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!plus_expr(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "a_expr_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // id_expr mul_expr *
  static boolean b_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr")) return false;
    if (!nextTokenIs(builder_, "", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = id_expr(builder_, level_ + 1);
    result_ = result_ && b_expr_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // mul_expr *
  private static boolean b_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!mul_expr(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "b_expr_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // a_expr (',' a_expr) *
  public static boolean expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    if (!nextTokenIs(builder_, "<expr>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, EXPR, "<expr>");
    result_ = a_expr(builder_, level_ + 1);
    result_ = result_ && expr_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (',' a_expr) *
  private static boolean expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!expr_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "expr_1", pos_)) break;
    }
    return true;
  }

  // ',' a_expr
  private static boolean expr_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && a_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean external_same_as_type2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "external_same_as_type2")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, ID_EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // number
  public static boolean external_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "external_type")) return false;
    if (!nextTokenIs(builder_, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, marker_, MISSING_EXTERNAL_TYPE, result_);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean external_type2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "external_type2")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, ID_EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean external_type3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "external_type3")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr | external_type3
  public static boolean grammar_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element")) return false;
    if (!nextTokenIs(builder_, "<grammar element>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, GRAMMAR_ELEMENT, "<grammar element>");
    result_ = expr(builder_, level_ + 1);
    if (!result_) result_ = external_type3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // root
  static boolean grammar_root(PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // specialRef | reference | literal | external_type | external_type2
  static boolean id_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "id_expr")) return false;
    if (!nextTokenIs(builder_, "", ID, NUMBER)) return false;
    boolean result_;
    result_ = PsiGen2.specialRef(builder_, level_ + 1);
    if (!result_) result_ = PsiGen2.reference(builder_, level_ + 1);
    if (!result_) result_ = PsiGen2.literal(builder_, level_ + 1);
    if (!result_) result_ = external_type(builder_, level_ + 1);
    if (!result_) result_ = external_type2(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // id number
  public static boolean include__section__alt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "include__section__alt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, INCLUDE__SECTION__ALT, "<include section alt>");
    result_ = consumeTokens(builder_, 0, ID, NUMBER);
    exit_section_(builder_, level_, marker_, result_, false, PsiGen::include_section_recover_);
    return result_;
  }

  /* ********************************************************** */
  // id number include-section <include (section) alt>
  public static boolean include_section(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "include_section")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, INCLUDE_SECTION, "<include section>");
    result_ = consumeTokens(builder_, 0, ID, NUMBER);
    result_ = result_ && include_section(builder_, level_ + 1);
    result_ = result_ && include__section__alt(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, PsiGen::include_section_recover_);
    return result_;
  }

  /* ********************************************************** */
  // !()
  static boolean include_section_recover_(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "include_section_recover_")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !include_section_recover__0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ()
  private static boolean include_section_recover__0(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // <<p>> +
  static boolean listOf(PsiBuilder builder_, int level_, Parser p) {
    if (!recursion_guard_(builder_, level_, "listOf")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = p.parse(builder_, level_);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!p.parse(builder_, level_)) break;
      if (!empty_element_parsed_guard_(builder_, "listOf", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '*' expr
  public static boolean mul_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mul_expr")) return false;
    if (!nextTokenIs(builder_, OP_MUL)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, MUL_EXPR, null);
    result_ = consumeToken(builder_, OP_MUL);
    result_ = result_ && expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '+' expr
  public static boolean plus_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "plus_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, PLUS_EXPR, "<plus expr>");
    result_ = consumeToken(builder_, "+");
    result_ = result_ && expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // root_a | root_b | root_c | root_d
  public static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, ROOT, "<root>");
    result_ = parseGrammar(builder_, level_ + 1, PsiGen::grammar_element);
    if (!result_) result_ = root_b(builder_, level_ + 1);
    if (!result_) result_ = root_c(builder_, level_ + 1);
    if (!result_) result_ = root_d(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<parseGrammar grammar_element>>
  public static boolean root_b(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_b")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, ROOT_B, "<root b>");
    result_ = parseGrammar(builder_, level_ + 1, PsiGen::grammar_element);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<blockOf grammar_element>>
  public static boolean root_c(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_c")) return false;
    if (!nextTokenIs(builder_, "<root c>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ROOT_C, "<root c>");
    result_ = PsiGen2.blockOf(builder_, level_ + 1, PsiGen::grammar_element);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<listOf grammar_element>>
  public static boolean root_d(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_d")) return false;
    if (!nextTokenIs(builder_, "<root d>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ROOT_D, "<root d>");
    result_ = listOf(builder_, level_ + 1, PsiGen::grammar_element);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

}
// ---- PsiGen2.java -----------------
//header.txt
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static PsiGenUtil.*;
import static PsiGen.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGen2 {

  /* ********************************************************** */
  // <<p>> +
  public static boolean blockOf(PsiBuilder builder_, int level_, Parser p) {
    if (!recursion_guard_(builder_, level_, "blockOf")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = p.parse(builder_, level_);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!p.parse(builder_, level_)) break;
      if (!empty_element_parsed_guard_(builder_, "blockOf", pos_)) break;
    }
    exit_section_(builder_, marker_, BLOCK_OF, result_);
    return result_;
  }

  /* ********************************************************** */
  // '::' id
  public static boolean cast_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cast_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, CAST_EXPR, "<cast expr>");
    result_ = consumeToken(builder_, "::");
    result_ = result_ && consumeToken(builder_, ID);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, IDENTIFIER, result_);
    return result_;
  }

  /* ********************************************************** */
  // '[' number ']'
  public static boolean item_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "item_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, ITEM_EXPR, "<item expr>");
    result_ = consumeToken(builder_, "[");
    result_ = result_ && consumeToken(builder_, NUMBER);
    result_ = result_ && consumeToken(builder_, "]");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // number
  public static boolean literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal")) return false;
    if (!nextTokenIs(builder_, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, marker_, LITERAL, result_);
    return result_;
  }

  /* ********************************************************** */
  // '.' identifier
  public static boolean qref_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qref_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, REF_EXPR, "<qref expr>");
    result_ = consumeToken(builder_, ".");
    result_ = result_ && identifier(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier
  public static boolean ref_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ref_expr")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = identifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, REF_EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // ref_expr qref_expr *
  static boolean reference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "reference")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = ref_expr(builder_, level_ + 1);
    result_ = result_ && reference_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // qref_expr *
  private static boolean reference_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "reference_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!qref_expr(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "reference_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (a_expr | specialRef b_expr | some_expr_private) (cast_expr) (item_expr) *
  public static boolean some_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr")) return false;
    if (!nextTokenIs(builder_, "<some expr>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, SOME_EXPR, "<some expr>");
    result_ = some_expr_0(builder_, level_ + 1);
    result_ = result_ && some_expr_1(builder_, level_ + 1);
    result_ = result_ && some_expr_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // a_expr | specialRef b_expr | some_expr_private
  private static boolean some_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = a_expr(builder_, level_ + 1);
    if (!result_) result_ = some_expr_0_1(builder_, level_ + 1);
    if (!result_) result_ = some_expr_private(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // specialRef b_expr
  private static boolean some_expr_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = specialRef(builder_, level_ + 1);
    result_ = result_ && b_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (cast_expr)
  private static boolean some_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = cast_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (item_expr) *
  private static boolean some_expr_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!some_expr_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "some_expr_2", pos_)) break;
    }
    return true;
  }

  // (item_expr)
  private static boolean some_expr_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = item_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // specialRef b_expr
  static boolean some_expr_private(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_private")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = specialRef(builder_, level_ + 1);
    result_ = result_ && b_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // identifier OF reference
  public static boolean specialRef(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "specialRef")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = identifier(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OF);
    result_ = result_ && reference(builder_, level_ + 1);
    exit_section_(builder_, marker_, SPECIAL_REF, result_);
    return result_;
  }

}
// ---- PsiGenFixes.java -----------------
//header.txt
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static PsiGenUtil.*;
import static PsiGen.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGenFixes {

  /* ********************************************************** */
  // ',' identifier
  public static boolean LeftShadow(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LeftShadow")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, LEFT_SHADOW, "<left shadow>");
    result_ = consumeToken(builder_, ",");
    result_ = result_ && PsiGen2.identifier(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier LeftShadow *
  public static boolean LeftShadowTest(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LeftShadowTest")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = PsiGen2.identifier(builder_, level_ + 1);
    result_ = result_ && LeftShadowTest_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, LEFT_SHADOW_TEST, result_);
    return result_;
  }

  // LeftShadow *
  private static boolean LeftShadowTest_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LeftShadowTest_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!LeftShadow(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "LeftShadowTest_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // id | number
  public static boolean a_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_statement")) return false;
    if (!nextTokenIs(builder_, "<a statement>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, A_STATEMENT, "<a statement>");
    result_ = consumeToken(builder_, ID);
    if (!result_) result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id | number
  public static boolean b_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_statement")) return false;
    if (!nextTokenIs(builder_, "<b statement>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, B_STATEMENT, "<b statement>");
    result_ = consumeToken(builder_, ID);
    if (!result_) result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id | number
  public static boolean c_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "c_statement")) return false;
    if (!nextTokenIs(builder_, "<c statement>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, C_STATEMENT, "<c statement>");
    result_ = consumeToken(builder_, ID);
    if (!result_) result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // literal id '%' | '%' id literal
  public static boolean choice_joined(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_joined")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CHOICE_JOINED, "<choice joined>");
    result_ = choice_joined_0(builder_, level_ + 1);
    if (!result_) result_ = choice_joined_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // literal id '%'
  private static boolean choice_joined_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_joined_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = PsiGen2.literal(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ID);
    result_ = result_ && consumeToken(builder_, "%");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '%' id literal
  private static boolean choice_joined_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "choice_joined_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "%");
    result_ = result_ && consumeToken(builder_, ID);
    result_ = result_ && PsiGen2.literal(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<blockOf identifier>>
  static boolean fixMetaRule(PsiBuilder builder_, int level_) {
    return PsiGen2.blockOf(builder_, level_ + 1, PsiGen2::identifier);
  }

  /* ********************************************************** */
  // identifier
  public static boolean publicMethodToCall(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "publicMethodToCall")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = PsiGen2.identifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // &<<external>> (a_statement | b_statement) | !<<external>> (c_statement)
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, STATEMENT, "<statement>");
    result_ = statement_0(builder_, level_ + 1);
    if (!result_) result_ = statement_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // &<<external>> (a_statement | b_statement)
  private static boolean statement_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = statement_0_0(builder_, level_ + 1);
    result_ = result_ && statement_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // &<<external>>
  private static boolean statement_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = external(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // a_statement | b_statement
  private static boolean statement_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_0_1")) return false;
    boolean result_;
    result_ = a_statement(builder_, level_ + 1);
    if (!result_) result_ = b_statement(builder_, level_ + 1);
    return result_;
  }

  // !<<external>> (c_statement)
  private static boolean statement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = statement_1_0(builder_, level_ + 1);
    result_ = result_ && statement_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !<<external>>
  private static boolean statement_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !external(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (c_statement)
  private static boolean statement_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = c_statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

}