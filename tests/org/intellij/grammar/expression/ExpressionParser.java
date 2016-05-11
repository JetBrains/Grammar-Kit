//header.txt
package org.intellij.grammar.expression;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static org.intellij.grammar.expression.ExpressionTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExpressionParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ARG_LIST) {
      r = arg_list(b, 0);
    }
    else if (t == ASSIGN_EXPR) {
      r = expr(b, 0, -1);
    }
    else if (t == BETWEEN_EXPR) {
      r = expr(b, 0, 2);
    }
    else if (t == CALL_EXPR) {
      r = expr(b, 0, 7);
    }
    else if (t == CONDITIONAL_EXPR) {
      r = expr(b, 0, 0);
    }
    else if (t == DIV_EXPR) {
      r = expr(b, 0, 3);
    }
    else if (t == ELVIS_EXPR) {
      r = expr(b, 0, 0);
    }
    else if (t == EXP_EXPR) {
      r = expr(b, 0, 5);
    }
    else if (t == EXPR) {
      r = expr(b, 0, -1);
    }
    else if (t == FACTORIAL_EXPR) {
      r = expr(b, 0, 6);
    }
    else if (t == IDENTIFIER) {
      r = identifier(b, 0);
    }
    else if (t == IS_NOT_EXPR) {
      r = expr(b, 0, 2);
    }
    else if (t == LITERAL_EXPR) {
      r = literal_expr(b, 0);
    }
    else if (t == MINUS_EXPR) {
      r = expr(b, 0, 1);
    }
    else if (t == MUL_EXPR) {
      r = expr(b, 0, 3);
    }
    else if (t == PAREN_EXPR) {
      r = paren_expr(b, 0);
    }
    else if (t == PLUS_EXPR) {
      r = expr(b, 0, 1);
    }
    else if (t == REF_EXPR) {
      r = ref_expr(b, 0);
    }
    else if (t == SPECIAL_EXPR) {
      r = special_expr(b, 0);
    }
    else if (t == UNARY_MIN_EXPR) {
      r = unary_min_expr(b, 0);
    }
    else if (t == UNARY_NOT_EXPR) {
      r = unary_not_expr(b, 0);
    }
    else if (t == UNARY_PLUS_EXPR) {
      r = unary_plus_expr(b, 0);
    }
    else if (t == XOR_EXPR) {
      r = expr(b, 0, 2);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ASSIGN_EXPR, BETWEEN_EXPR, CALL_EXPR, CONDITIONAL_EXPR,
      DIV_EXPR, ELVIS_EXPR, EXPR, EXP_EXPR,
      FACTORIAL_EXPR, IS_NOT_EXPR, LITERAL_EXPR, MINUS_EXPR,
      MUL_EXPR, PAREN_EXPR, PLUS_EXPR, REF_EXPR,
      SPECIAL_EXPR, UNARY_MIN_EXPR, UNARY_NOT_EXPR, UNARY_PLUS_EXPR,
      XOR_EXPR),
  };

  /* ********************************************************** */
  // '(' [ !')' expr  (',' expr) * ] ')'
  public static boolean arg_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARG_LIST, "<arg list>");
    r = consumeToken(b, "(");
    p = r; // pin = 1
    r = r && report_error_(b, arg_list_1(b, l + 1));
    r = p && consumeToken(b, ")") && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // [ !')' expr  (',' expr) * ]
  private static boolean arg_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1")) return false;
    arg_list_1_0(b, l + 1);
    return true;
  }

  // !')' expr  (',' expr) *
  private static boolean arg_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = arg_list_1_0_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, expr(b, l + 1, -1));
    r = p && arg_list_1_0_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // !')'
  private static boolean arg_list_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, ")");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' expr) *
  private static boolean arg_list_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!arg_list_1_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arg_list_1_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' expr
  private static boolean arg_list_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arg_list_1_0_2_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, ",");
    p = r; // pin = 1
    r = r && expr(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // expr ';'?
  static boolean element(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "element")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = expr(b, l + 1, -1);
    r = r && element_1(b, l + 1);
    exit_section_(b, l, m, r, false, element_recover_parser_);
    return r;
  }

  // ';'?
  private static boolean element_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "element_1")) return false;
    consumeToken(b, ";");
    return true;
  }

  /* ********************************************************** */
  // !('(' | '+' | '-' | '!' | 'multiply' | id | number)
  static boolean element_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "element_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !element_recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '(' | '+' | '-' | '!' | 'multiply' | id | number
  private static boolean element_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "element_recover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    if (!r) r = consumeToken(b, "+");
    if (!r) r = consumeToken(b, "-");
    if (!r) r = consumeToken(b, "!");
    if (!r) r = consumeToken(b, "multiply");
    if (!r) r = consumeToken(b, ID);
    if (!r) r = consumeToken(b, NUMBER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // id
  public static boolean identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    exit_section_(b, m, IDENTIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // expr? '.' identifier
  public static boolean ref_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ref_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, REF_EXPR, "<ref expr>");
    r = ref_expr_0(b, l + 1);
    r = r && consumeToken(b, ".");
    r = r && identifier(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr?
  private static boolean ref_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ref_expr_0")) return false;
    expr(b, l + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // element *
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    int c = current_position_(b);
    while (true) {
      if (!element(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY(assign_expr)
  // 1: BINARY(elvis_expr) BINARY(conditional_expr)
  // 2: BINARY(plus_expr) BINARY(minus_expr)
  // 3: BINARY(xor_expr) BINARY(between_expr) BINARY(is_not_expr)
  // 4: BINARY(mul_expr) BINARY(div_expr)
  // 5: PREFIX(unary_plus_expr) PREFIX(unary_min_expr) PREFIX(unary_not_expr)
  // 6: N_ARY(exp_expr)
  // 7: POSTFIX(factorial_expr)
  // 8: POSTFIX(call_expr)
  // 9: POSTFIX(qualification_expr)
  // 10: ATOM(special_expr) ATOM(simple_ref_expr) ATOM(literal_expr) PREFIX(paren_expr)
  public static boolean expr(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expr")) return false;
    addVariant(b, "<expr>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expr>");
    r = unary_plus_expr(b, l + 1);
    if (!r) r = unary_min_expr(b, l + 1);
    if (!r) r = unary_not_expr(b, l + 1);
    if (!r) r = special_expr(b, l + 1);
    if (!r) r = simple_ref_expr(b, l + 1);
    if (!r) r = literal_expr(b, l + 1);
    if (!r) r = paren_expr(b, l + 1);
    p = r;
    r = r && expr_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expr_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expr_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && consumeTokenSmart(b, "=")) {
        r = expr(b, l, -1);
        exit_section_(b, l, m, ASSIGN_EXPR, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, "?")) {
        r = report_error_(b, expr(b, l, 1));
        r = elvis_expr_1(b, l + 1) && r;
        exit_section_(b, l, m, ELVIS_EXPR, r, true, null);
      }
      else if (g < 1 && conditional_expr_0(b, l + 1)) {
        r = expr(b, l, 1);
        exit_section_(b, l, m, CONDITIONAL_EXPR, r, true, null);
      }
      else if (g < 2 && consumeTokenSmart(b, "+")) {
        r = expr(b, l, 2);
        exit_section_(b, l, m, PLUS_EXPR, r, true, null);
      }
      else if (g < 2 && consumeTokenSmart(b, "-")) {
        r = expr(b, l, 2);
        exit_section_(b, l, m, MINUS_EXPR, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, "^")) {
        r = expr(b, l, 3);
        exit_section_(b, l, m, XOR_EXPR, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, BETWEEN)) {
        r = report_error_(b, expr(b, l, 3));
        r = between_expr_1(b, l + 1) && r;
        exit_section_(b, l, m, BETWEEN_EXPR, r, true, null);
      }
      else if (g < 3 && parseTokensSmart(b, 0, IS, NOT)) {
        r = expr(b, l, 3);
        exit_section_(b, l, m, IS_NOT_EXPR, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, "*")) {
        r = expr(b, l, 4);
        exit_section_(b, l, m, MUL_EXPR, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, "/")) {
        r = expr(b, l, 4);
        exit_section_(b, l, m, DIV_EXPR, r, true, null);
      }
      else if (g < 7 && consumeTokenSmart(b, "!")) {
        r = true;
        exit_section_(b, l, m, FACTORIAL_EXPR, r, true, null);
      }
      else if (g < 6 && consumeTokenSmart(b, "**")) {
        while (true) {
          r = report_error_(b, expr(b, l, 6));
          if (!consumeTokenSmart(b, "**")) break;
        }
        exit_section_(b, l, m, EXP_EXPR, r, true, null);
      }
      else if (g < 8 && leftMarkerIs(b, REF_EXPR) && arg_list(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, CALL_EXPR, r, true, null);
      }
      else if (g < 9 && qualification_expr_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, REF_EXPR, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // ':' expr
  private static boolean elvis_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "elvis_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ":");
    r = r && expr(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '<' | '>' | '<=' | '>=' | '==' | '!='
  private static boolean conditional_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "conditional_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, "<");
    if (!r) r = consumeTokenSmart(b, ">");
    if (!r) r = consumeTokenSmart(b, "<=");
    if (!r) r = consumeTokenSmart(b, ">=");
    if (!r) r = consumeTokenSmart(b, "==");
    if (!r) r = consumeTokenSmart(b, "!=");
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean unary_plus_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_plus_expr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, "+");
    p = r;
    r = p && expr(b, l, 5);
    exit_section_(b, l, m, UNARY_PLUS_EXPR, r, p, null);
    return r || p;
  }

  public static boolean unary_min_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_min_expr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, "-");
    p = r;
    r = p && expr(b, l, 5);
    exit_section_(b, l, m, UNARY_MIN_EXPR, r, p, null);
    return r || p;
  }

  // AND add_group
  private static boolean between_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "between_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND);
    r = r && expr(b, l + 1, -2);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean unary_not_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_not_expr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, "!");
    p = r;
    r = p && expr(b, l, 5);
    exit_section_(b, l, m, UNARY_NOT_EXPR, r, p, null);
    return r || p;
  }

  // '.' identifier
  private static boolean qualification_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "qualification_expr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, ".");
    r = r && identifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'multiply' '(' simple_ref_expr ',' mul_expr ')'
  public static boolean special_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "special_expr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SPECIAL_EXPR, "<special expr>");
    r = consumeTokenSmart(b, "multiply");
    r = r && consumeToken(b, "(");
    p = r; // pin = 2
    r = r && report_error_(b, simple_ref_expr(b, l + 1));
    r = p && report_error_(b, consumeToken(b, ",")) && r;
    r = p && report_error_(b, expr(b, l + 1, 3)) && r;
    r = p && consumeToken(b, ")") && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // identifier
  public static boolean simple_ref_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_ref_expr")) return false;
    if (!nextTokenIsSmart(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = identifier(b, l + 1);
    exit_section_(b, m, REF_EXPR, r);
    return r;
  }

  // number
  public static boolean literal_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expr")) return false;
    if (!nextTokenIsSmart(b, NUMBER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, NUMBER);
    exit_section_(b, m, LITERAL_EXPR, r);
    return r;
  }

  public static boolean paren_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, "(");
    p = r;
    r = p && expr(b, l, -1);
    r = p && report_error_(b, consumeToken(b, ")")) && r;
    exit_section_(b, l, m, PAREN_EXPR, r, p, null);
    return r || p;
  }

  final static Parser element_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return element_recover(b, l + 1);
    }
  };
}
