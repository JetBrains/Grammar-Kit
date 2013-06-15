// ---- PsiGen.java -----------------
//header.txt
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.GeneratedTypes.*;
import static PsiGenUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGen implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("PsiGen");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    if (root_ == EXPR) {
      result_ = expr(builder_, level_ + 1);
    }
    else if (root_ == GRAMMAR_ELEMENT) {
      result_ = grammar_element(builder_, level_ + 1);
    }
    else if (root_ == MUL_EXPR) {
      result_ = mul_expr(builder_, level_ + 1);
    }
    else if (root_ == PLUS_EXPR) {
      result_ = plus_expr(builder_, level_ + 1);
    }
    else if (root_ == ROOT_B) {
      result_ = root_b(builder_, level_ + 1);
    }
    else if (root_ == ROOT_C) {
      result_ = root_c(builder_, level_ + 1);
    }
    else if (root_ == ROOT_D) {
      result_ = root_d(builder_, level_ + 1);
    }
    else {
      Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
      result_ = parse_root_(root_, builder_, level_);
      exit_section_(builder_, level_, marker_, root_, result_, true, TOKEN_ADVANCER);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return root(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(CAST_EXPR, EXPR, ID_EXPR, ITEM_EXPR,
      LITERAL, MISSING_EXTERNAL_TYPE, MUL_EXPR, PLUS_EXPR,
      REF_EXPR, SOME_EXPR, SPECIAL_REF),
    create_token_set_(REF_EXPR, SPECIAL_REF),
    create_token_set_(REF_EXPR, SPECIAL_REF),
    create_token_set_(ROOT, ROOT_B, ROOT_C, ROOT_D),
  };

  /* ********************************************************** */
  // b_expr plus_expr *
  static boolean a_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = b_expr(builder_, level_ + 1);
    result_ = result_ && a_expr_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // plus_expr *
  private static boolean a_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "a_expr_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!plus_expr(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "a_expr_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // id_expr mul_expr *
  static boolean b_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = id_expr(builder_, level_ + 1);
    result_ = result_ && b_expr_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // mul_expr *
  private static boolean b_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "b_expr_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!mul_expr(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "b_expr_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // a_expr (',' a_expr) *
  public static boolean expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)
        && replaceVariants(builder_, 2, "<expr>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<expr>");
    result_ = a_expr(builder_, level_ + 1);
    result_ = result_ && expr_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, EXPR, result_, false, null);
    return result_;
  }

  // (',' a_expr) *
  private static boolean expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!expr_1_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "expr_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // ',' a_expr
  private static boolean expr_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expr_1_0")) return false;
    boolean result_ = false;
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
    boolean result_ = false;
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
    boolean result_ = false;
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
    boolean result_ = false;
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
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, EXPR, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr | external_type3
  public static boolean grammar_element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grammar_element")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)
        && replaceVariants(builder_, 2, "<grammar element>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<grammar element>");
    result_ = expr(builder_, level_ + 1);
    if (!result_) result_ = external_type3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GRAMMAR_ELEMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // specialRef | reference | literal | external_type | external_type2
  static boolean id_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "id_expr")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = PsiGen2.specialRef(builder_, level_ + 1);
    if (!result_) result_ = PsiGen2.reference(builder_, level_ + 1);
    if (!result_) result_ = PsiGen2.literal(builder_, level_ + 1);
    if (!result_) result_ = external_type(builder_, level_ + 1);
    if (!result_) result_ = external_type2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<p>> +
  static boolean listOf(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "listOf")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = p.parse(builder_, level_);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!p.parse(builder_, level_)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "listOf");
        break;
      }
      offset_ = next_offset_;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '*' expr
  public static boolean mul_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mul_expr")) return false;
    if (!nextTokenIs(builder_, OP_MUL)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, null);
    result_ = consumeToken(builder_, OP_MUL);
    result_ = result_ && expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, MUL_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '+' expr
  public static boolean plus_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "plus_expr")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, "<plus expr>");
    result_ = consumeToken(builder_, "+");
    result_ = result_ && expr(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, PLUS_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // root_a | root_b | root_c | root_d
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = parseGrammar(builder_, level_ + 1, grammar_element_parser_);
    if (!result_) result_ = root_b(builder_, level_ + 1);
    if (!result_) result_ = root_c(builder_, level_ + 1);
    if (!result_) result_ = root_d(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<parseGrammar grammar_element>>
  public static boolean root_b(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_b")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<root b>");
    result_ = parseGrammar(builder_, level_ + 1, grammar_element_parser_);
    exit_section_(builder_, level_, marker_, ROOT_B, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<blockOf grammar_element>>
  public static boolean root_c(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_c")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)
        && replaceVariants(builder_, 2, "<root c>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<root c>");
    result_ = PsiGen2.blockOf(builder_, level_ + 1, grammar_element_parser_);
    exit_section_(builder_, level_, marker_, ROOT_C, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<listOf grammar_element>>
  public static boolean root_d(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_d")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)
        && replaceVariants(builder_, 2, "<root d>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<root d>");
    result_ = listOf(builder_, level_ + 1, grammar_element_parser_);
    exit_section_(builder_, level_, marker_, ROOT_D, result_, false, null);
    return result_;
  }

  final static Parser grammar_element_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return grammar_element(builder_, level_ + 1);
    }
  };
}
// ---- PsiGen2.java -----------------
//header.txt
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.GeneratedTypes.*;
import static PsiGenUtil.*;
import static PsiGen.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGen2 {

  public static final Logger LOG_ = Logger.getInstance("PsiGen2");

  /* ********************************************************** */
  // <<p>> +
  public static boolean blockOf(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "blockOf")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = p.parse(builder_, level_);
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!p.parse(builder_, level_)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "blockOf");
        break;
      }
      offset_ = next_offset_;
    }
    exit_section_(builder_, marker_, BLOCK_OF, result_);
    return result_;
  }

  /* ********************************************************** */
  // '::' id
  public static boolean cast_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cast_expr")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, "<cast expr>");
    result_ = consumeToken(builder_, "::");
    result_ = result_ && consumeToken(builder_, ID);
    exit_section_(builder_, level_, marker_, CAST_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, IDENTIFIER, result_);
    return result_;
  }

  /* ********************************************************** */
  // '[' number ']'
  public static boolean item_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "item_expr")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, "<item expr>");
    result_ = consumeToken(builder_, "[");
    result_ = result_ && consumeToken(builder_, NUMBER);
    result_ = result_ && consumeToken(builder_, "]");
    exit_section_(builder_, level_, marker_, ITEM_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // number
  public static boolean literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "literal")) return false;
    if (!nextTokenIs(builder_, NUMBER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, marker_, LITERAL, result_);
    return result_;
  }

  /* ********************************************************** */
  // '.' identifier
  public static boolean qref_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "qref_expr")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, "<qref expr>");
    result_ = consumeToken(builder_, ".");
    result_ = result_ && identifier(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, REF_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier
  public static boolean ref_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ref_expr")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
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
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ref_expr(builder_, level_ + 1);
    result_ = result_ && reference_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // qref_expr *
  private static boolean reference_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "reference_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!qref_expr(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "reference_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // (a_expr | specialRef b_expr | some_expr_private) (cast_expr) (item_expr) *
  public static boolean some_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr")) return false;
    if (!nextTokenIs(builder_, ID) && !nextTokenIs(builder_, NUMBER)
        && replaceVariants(builder_, 2, "<some expr>")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, "<some expr>");
    result_ = some_expr_0(builder_, level_ + 1);
    result_ = result_ && some_expr_1(builder_, level_ + 1);
    result_ = result_ && some_expr_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, SOME_EXPR, result_, false, null);
    return result_;
  }

  // a_expr | specialRef b_expr | some_expr_private
  private static boolean some_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_0")) return false;
    boolean result_ = false;
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
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = specialRef(builder_, level_ + 1);
    result_ = result_ && b_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (cast_expr)
  private static boolean some_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = cast_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (item_expr) *
  private static boolean some_expr_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!some_expr_2_0(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "some_expr_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (item_expr)
  private static boolean some_expr_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_expr_2_0")) return false;
    boolean result_ = false;
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
    boolean result_ = false;
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
    boolean result_ = false;
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
import com.intellij.openapi.diagnostic.Logger;
import static generated.GeneratedTypes.*;
import static PsiGenUtil.*;
import static PsiGen.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiGenFixes {

  public static final Logger LOG_ = Logger.getInstance("PsiGenFixes");

  /* ********************************************************** */
  // ',' identifier
  public static boolean LeftShadow(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LeftShadow")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, "<left shadow>");
    result_ = consumeToken(builder_, ",");
    result_ = result_ && PsiGen2.identifier(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, LEFT_SHADOW, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier LeftShadow *
  public static boolean LeftShadowTest(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LeftShadowTest")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = PsiGen2.identifier(builder_, level_ + 1);
    result_ = result_ && LeftShadowTest_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, LEFT_SHADOW_TEST, result_);
    return result_;
  }

  // LeftShadow *
  private static boolean LeftShadowTest_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LeftShadowTest_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!LeftShadow(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "LeftShadowTest_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // <<blockOf identifier>>
  static boolean fixMetaRule(PsiBuilder builder_, int level_) {
    return PsiGen2.blockOf(builder_, level_ + 1, identifier_parser_);
  }

  /* ********************************************************** */
  // identifier
  public static boolean publicMethodToCall(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "publicMethodToCall")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = PsiGen2.identifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  final static Parser identifier_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return PsiGen2.identifier(builder_, level_ + 1);
    }
  };
}