// ---- Small.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.GeneratedTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Small implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("Small");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    if (root_ == EMPTY) {
      result_ = empty(builder_, level_ + 1);
    }
    else if (root_ == EMPTY_2) {
      result_ = empty2(builder_, level_ + 1);
    }
    else if (root_ == EMPTY_3) {
      result_ = empty3(builder_, level_ + 1);
    }
    else if (root_ == OTHER_RULE) {
      result_ = otherRule(builder_, level_ + 1);
    }
    else if (root_ == SOME_RULE) {
      result_ = someRule(builder_, level_ + 1);
    }
    else if (root_ == SOME_RULE_2) {
      result_ = someRule2(builder_, level_ + 1);
    }
    else if (root_ == SOME_STRING) {
      result_ = someString(builder_, level_ + 1);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, level_ + 1);
    }
    else {
      Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
      result_ = parse_root_(root_, builder_, level_);
      exit_section_(builder_, level_, marker_, root_, result_, true, TOKEN_ADVANCER);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return parseRoot(builder_, level_ + 1, statement_parser_);
  }

  /* ********************************************************** */
  // ()
  public static boolean empty(PsiBuilder builder_, int level_) {
    builder_.mark().done(EMPTY);
    return true;
  }

  /* ********************************************************** */
  // {}
  public static boolean empty2(PsiBuilder builder_, int level_) {
    builder_.mark().done(EMPTY_2);
    return true;
  }

  /* ********************************************************** */
  // []
  public static boolean empty3(PsiBuilder builder_, int level_) {
    builder_.mark().done(EMPTY_3);
    return true;
  }

  /* ********************************************************** */
  // ()
  static boolean empty4(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // []
  static boolean empty5(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // &()
  static boolean empty6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "empty6")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _AND_, null);
    result_ = empty6_0(builder_, level_ + 1);
    result_ = exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // ()
  private static boolean empty6_0(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // !()
  static boolean empty7(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "empty7")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NOT_, null);
    result_ = !empty7_0(builder_, level_ + 1);
    result_ = exit_section_(builder_, level_, marker_, null, result_, false, null);
    return result_;
  }

  // ()
  private static boolean empty7_0(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // [({})]
  static boolean empty8(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // [({token})]
  static boolean not_empty1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty1")) return false;
    not_empty1_0(builder_, level_ + 1);
    return true;
  }

  // {token}
  private static boolean not_empty1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [({token someString})]
  static boolean not_empty2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty2")) return false;
    not_empty2_0(builder_, level_ + 1);
    return true;
  }

  // token someString
  private static boolean not_empty2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    result_ = result_ && someString(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // ( token )
  public static boolean otherRule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "otherRule")) return false;
    if (!nextTokenIs(builder_, TOKEN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    exit_section_(builder_, marker_, OTHER_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token
  static boolean privateRule(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, TOKEN);
  }

  /* ********************************************************** */
  // 'token'
  static boolean privateString(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, "token");
  }

  /* ********************************************************** */
  // token
  public static boolean someRule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someRule")) return false;
    if (!nextTokenIs(builder_, TOKEN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    exit_section_(builder_, marker_, SOME_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token?
  public static boolean someRule2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someRule2")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<some rule 2>");
    consumeToken(builder_, TOKEN);
    exit_section_(builder_, level_, marker_, SOME_RULE_2, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // 'token'
  public static boolean someString(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someString")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<some string>");
    result_ = consumeToken(builder_, "token");
    result_ = exit_section_(builder_, level_, marker_, SOME_STRING, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // token | someRule | someString
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<statement>");
    result_ = consumeToken(builder_, TOKEN);
    if (!result_) result_ = someRule(builder_, level_ + 1);
    if (!result_) result_ = someString(builder_, level_ + 1);
    result_ = exit_section_(builder_, level_, marker_, STATEMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '=' "=" '==' "=="
  static boolean tokenRule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tokenRule")) return false;
    if (!nextTokenIs(builder_, OP_EQ)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OP_EQ);
    result_ = result_ && consumeToken(builder_, OP_EQ);
    result_ = result_ && consumeToken(builder_, "==");
    result_ = result_ && consumeToken(builder_, "==");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  final static Parser statement_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return statement(builder_, level_ + 1);
    }
  };
}