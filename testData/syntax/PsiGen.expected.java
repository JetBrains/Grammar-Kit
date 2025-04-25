// ---- PsiGen.java -----------------
//header.txt
package ;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static PsiGenUtil.*;
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
public class PsiGen {

  public ProductionResult parse(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    parseLight(root_, runtime_);
    return prepareProduction(runtime_.getSyntaxBuilder());
  }

  public void parseLight(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    boolean result_;
    Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit> parse_ = new Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit>(){
      @Override
      public Unit invoke(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
        parseLight(root_, runtime_);
        return Unit.INSTANCE;
      }
    };

    runtime_.init(parse_, EXTENDS_SETS_);
    Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_, int level_) {
    return grammar_root(runtime_, level_ + 1);
  }

  public static final SyntaxElementTypeSet[] EXTENDS_SETS_ = new SyntaxElementTypeSet[] {
    create_token_set_(ROOT, ROOT_B, ROOT_C, ROOT_D),
    create_token_set_(A_STATEMENT, B_STATEMENT, C_STATEMENT, STATEMENT),
    create_token_set_(CAST_EXPR, CHOICE_JOINED, EXPR, ID_EXPR,
      ITEM_EXPR, LITERAL, MISSING_EXTERNAL_TYPE, MUL_EXPR,
      PLUS_EXPR, REF_EXPR, SOME_EXPR, SPECIAL_REF),
  };

  /* ********************************************************** */
  // b_expr plus_expr *
  static boolean a_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "a_expr")) return false;
    if (!nextTokenIs(runtime_, "", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = b_expr(runtime_, level_ + 1);
    result_ = result_ && a_expr_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // plus_expr *
  private static boolean a_expr_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "a_expr_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!plus_expr(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "a_expr_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // id_expr mul_expr *
  static boolean b_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "b_expr")) return false;
    if (!nextTokenIs(runtime_, "", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = id_expr(runtime_, level_ + 1);
    result_ = result_ && b_expr_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // mul_expr *
  private static boolean b_expr_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "b_expr_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!mul_expr(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "b_expr_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // a_expr (',' a_expr) *
  public static boolean expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "expr")) return false;
    if (!nextTokenIs(runtime_, "<expr>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, EXPR, "<expr>");
    result_ = a_expr(runtime_, level_ + 1);
    result_ = result_ && expr_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // (',' a_expr) *
  private static boolean expr_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "expr_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!expr_1_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "expr_1", pos_)) break;
    }
    return true;
  }

  // ',' a_expr
  private static boolean expr_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "expr_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ",");
    result_ = result_ && a_expr(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean external_same_as_type2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "external_same_as_type2")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, marker_, ID_EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // number
  public static boolean external_type(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "external_type")) return false;
    if (!nextTokenIs(runtime_, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, NUMBER);
    exit_section_(runtime_, marker_, MISSING_EXTERNAL_TYPE, result_);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean external_type2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "external_type2")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, marker_, ID_EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean external_type3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "external_type3")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, marker_, EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr | external_type3
  public static boolean grammar_element(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "grammar_element")) return false;
    if (!nextTokenIs(runtime_, "<grammar element>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, GRAMMAR_ELEMENT, "<grammar element>");
    result_ = expr(runtime_, level_ + 1);
    if (!result_) result_ = external_type3(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // root
  static boolean grammar_root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return root(runtime_, level_ + 1);
  }

  /* ********************************************************** */
  // specialRef | reference | literal | external_type | external_type2
  static boolean id_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "id_expr")) return false;
    if (!nextTokenIs(runtime_, "", ID, NUMBER)) return false;
    boolean result_;
    result_ = PsiGen2.specialRef(runtime_, level_ + 1);
    if (!result_) result_ = PsiGen2.reference(runtime_, level_ + 1);
    if (!result_) result_ = PsiGen2.literal(runtime_, level_ + 1);
    if (!result_) result_ = external_type(runtime_, level_ + 1);
    if (!result_) result_ = external_type2(runtime_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // id number
  public static boolean include__section__alt(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "include__section__alt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, INCLUDE__SECTION__ALT, "<include section alt>");
    result_ = consumeTokens(runtime_, 0, ID, NUMBER);
    exit_section_(runtime_, level_, marker_, result_, false, PsiGen::include_section_recover_);
    return result_;
  }

  /* ********************************************************** */
  // id number include-section <include (section) alt>
  public static boolean include_section(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "include_section")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, INCLUDE_SECTION, "<include section>");
    result_ = consumeTokens(runtime_, 0, ID, NUMBER);
    result_ = result_ && include_section(runtime_, level_ + 1);
    result_ = result_ && include__section__alt(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, PsiGen::include_section_recover_);
    return result_;
  }

  /* ********************************************************** */
  // !()
  static boolean include_section_recover_(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "include_section_recover_")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !include_section_recover__0(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // ()
  private static boolean include_section_recover__0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // <<p>> +
  static boolean listOf(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser p) {
    if (!recursion_guard_(runtime_, level_, "listOf")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = p.parse(runtime_, level_);
    while (result_) {
      int pos_ = current_position_(runtime_);
      if (!p.parse(runtime_, level_)) break;
      if (!empty_element_parsed_guard_(runtime_, "listOf", pos_)) break;
    }
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '*' expr
  public static boolean mul_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "mul_expr")) return false;
    if (!nextTokenIs(runtime_, OP_MUL)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, MUL_EXPR, null);
    result_ = consumeToken(runtime_, OP_MUL);
    result_ = result_ && expr(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '+' expr
  public static boolean plus_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "plus_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, PLUS_EXPR, "<plus expr>");
    result_ = consumeToken(runtime_, "+");
    result_ = result_ && expr(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // root_a | root_b | root_c | root_d
  public static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, ROOT, "<root>");
    result_ = parseGrammar(runtime_, level_ + 1, PsiGen::grammar_element);
    if (!result_) result_ = root_b(runtime_, level_ + 1);
    if (!result_) result_ = root_c(runtime_, level_ + 1);
    if (!result_) result_ = root_d(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<parseGrammar grammar_element>>
  public static boolean root_b(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root_b")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, ROOT_B, "<root b>");
    result_ = parseGrammar(runtime_, level_ + 1, PsiGen::grammar_element);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<blockOf grammar_element>>
  public static boolean root_c(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root_c")) return false;
    if (!nextTokenIs(runtime_, "<root c>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ROOT_C, "<root c>");
    result_ = PsiGen2.blockOf(runtime_, level_ + 1, PsiGen::grammar_element);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<listOf grammar_element>>
  public static boolean root_d(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root_d")) return false;
    if (!nextTokenIs(runtime_, "<root d>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ROOT_D, "<root d>");
    result_ = listOf(runtime_, level_ + 1, PsiGen::grammar_element);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

}
// ---- PsiGen2.java -----------------
//header.txt
package ;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static PsiGenUtil.*;
import static com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntimeKt.*;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NONE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._COLLAPSE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_INNER_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._AND_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NOT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._UPPER_;
import static PsiGen.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGen2 {

  /* ********************************************************** */
  // <<p>> +
  public static boolean blockOf(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser p) {
    if (!recursion_guard_(runtime_, level_, "blockOf")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = p.parse(runtime_, level_);
    while (result_) {
      int pos_ = current_position_(runtime_);
      if (!p.parse(runtime_, level_)) break;
      if (!empty_element_parsed_guard_(runtime_, "blockOf", pos_)) break;
    }
    exit_section_(runtime_, marker_, BLOCK_OF, result_);
    return result_;
  }

  /* ********************************************************** */
  // '::' id
  public static boolean cast_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "cast_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, CAST_EXPR, "<cast expr>");
    result_ = consumeToken(runtime_, "::");
    result_ = result_ && consumeToken(runtime_, ID);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "identifier")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, marker_, IDENTIFIER, result_);
    return result_;
  }

  /* ********************************************************** */
  // '[' number ']'
  public static boolean item_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "item_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, ITEM_EXPR, "<item expr>");
    result_ = consumeToken(runtime_, "[");
    result_ = result_ && consumeToken(runtime_, NUMBER);
    result_ = result_ && consumeToken(runtime_, "]");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // number
  public static boolean literal(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "literal")) return false;
    if (!nextTokenIs(runtime_, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, NUMBER);
    exit_section_(runtime_, marker_, LITERAL, result_);
    return result_;
  }

  /* ********************************************************** */
  // '.' identifier
  public static boolean qref_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "qref_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, REF_EXPR, "<qref expr>");
    result_ = consumeToken(runtime_, ".");
    result_ = result_ && identifier(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier
  public static boolean ref_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "ref_expr")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = identifier(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, REF_EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // ref_expr qref_expr *
  static boolean reference(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "reference")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = ref_expr(runtime_, level_ + 1);
    result_ = result_ && reference_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // qref_expr *
  private static boolean reference_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "reference_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!qref_expr(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "reference_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (a_expr | specialRef b_expr | some_expr_private) (cast_expr) (item_expr) *
  public static boolean some_expr(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr")) return false;
    if (!nextTokenIs(runtime_, "<some expr>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, SOME_EXPR, "<some expr>");
    result_ = some_expr_0(runtime_, level_ + 1);
    result_ = result_ && some_expr_1(runtime_, level_ + 1);
    result_ = result_ && some_expr_2(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // a_expr | specialRef b_expr | some_expr_private
  private static boolean some_expr_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = a_expr(runtime_, level_ + 1);
    if (!result_) result_ = some_expr_0_1(runtime_, level_ + 1);
    if (!result_) result_ = some_expr_private(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // specialRef b_expr
  private static boolean some_expr_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = specialRef(runtime_, level_ + 1);
    result_ = result_ && b_expr(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // (cast_expr)
  private static boolean some_expr_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = cast_expr(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // (item_expr) *
  private static boolean some_expr_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!some_expr_2_0(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "some_expr_2", pos_)) break;
    }
    return true;
  }

  // (item_expr)
  private static boolean some_expr_2_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = item_expr(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // specialRef b_expr
  static boolean some_expr_private(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "some_expr_private")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = specialRef(runtime_, level_ + 1);
    result_ = result_ && b_expr(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // identifier OF reference
  public static boolean specialRef(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "specialRef")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = identifier(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, OF);
    result_ = result_ && reference(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, SPECIAL_REF, result_);
    return result_;
  }

}
// ---- PsiGenFixes.java -----------------
//header.txt
package ;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static PsiGenUtil.*;
import static com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntimeKt.*;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NONE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._COLLAPSE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_INNER_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._AND_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NOT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._UPPER_;
import static PsiGen.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGenFixes {

  /* ********************************************************** */
  // ',' identifier
  public static boolean LeftShadow(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "LeftShadow")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _LEFT_, LEFT_SHADOW, "<left shadow>");
    result_ = consumeToken(runtime_, ",");
    result_ = result_ && PsiGen2.identifier(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier LeftShadow *
  public static boolean LeftShadowTest(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "LeftShadowTest")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = PsiGen2.identifier(runtime_, level_ + 1);
    result_ = result_ && LeftShadowTest_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, LEFT_SHADOW_TEST, result_);
    return result_;
  }

  // LeftShadow *
  private static boolean LeftShadowTest_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "LeftShadowTest_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!LeftShadow(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "LeftShadowTest_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // id | number
  public static boolean a_statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "a_statement")) return false;
    if (!nextTokenIs(runtime_, "<a statement>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, A_STATEMENT, "<a statement>");
    result_ = consumeToken(runtime_, ID);
    if (!result_) result_ = consumeToken(runtime_, NUMBER);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id | number
  public static boolean b_statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "b_statement")) return false;
    if (!nextTokenIs(runtime_, "<b statement>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, B_STATEMENT, "<b statement>");
    result_ = consumeToken(runtime_, ID);
    if (!result_) result_ = consumeToken(runtime_, NUMBER);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id | number
  public static boolean c_statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "c_statement")) return false;
    if (!nextTokenIs(runtime_, "<c statement>", ID, NUMBER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, C_STATEMENT, "<c statement>");
    result_ = consumeToken(runtime_, ID);
    if (!result_) result_ = consumeToken(runtime_, NUMBER);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // literal id '%' | '%' id literal
  public static boolean choice_joined(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "choice_joined")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, CHOICE_JOINED, "<choice joined>");
    result_ = choice_joined_0(runtime_, level_ + 1);
    if (!result_) result_ = choice_joined_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // literal id '%'
  private static boolean choice_joined_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "choice_joined_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = PsiGen2.literal(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, ID);
    result_ = result_ && consumeToken(runtime_, "%");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // '%' id literal
  private static boolean choice_joined_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "choice_joined_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "%");
    result_ = result_ && consumeToken(runtime_, ID);
    result_ = result_ && PsiGen2.literal(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<blockOf identifier>>
  static boolean fixMetaRule(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return PsiGen2.blockOf(runtime_, level_ + 1, PsiGen2::identifier);
  }

  /* ********************************************************** */
  // identifier
  public static boolean publicMethodToCall(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "publicMethodToCall")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = PsiGen2.identifier(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // &<<external>> (a_statement | b_statement) | !<<external>> (c_statement)
  public static boolean statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, STATEMENT, "<statement>");
    result_ = statement_0(runtime_, level_ + 1);
    if (!result_) result_ = statement_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // &<<external>> (a_statement | b_statement)
  private static boolean statement_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = statement_0_0(runtime_, level_ + 1);
    result_ = result_ && statement_0_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // &<<external>>
  private static boolean statement_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = external(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // a_statement | b_statement
  private static boolean statement_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement_0_1")) return false;
    boolean result_;
    result_ = a_statement(runtime_, level_ + 1);
    if (!result_) result_ = b_statement(runtime_, level_ + 1);
    return result_;
  }

  // !<<external>> (c_statement)
  private static boolean statement_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = statement_1_0(runtime_, level_ + 1);
    result_ = result_ && statement_1_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // !<<external>>
  private static boolean statement_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !external(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // (c_statement)
  private static boolean statement_1_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = c_statement(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

}