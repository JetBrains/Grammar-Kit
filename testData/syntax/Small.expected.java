// ---- Small.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.SyntaxTreeBuilder;
import com.intellij.lang.SyntaxTreeBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.platform.syntax.SyntaxGeneratedParserUtil.*;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.lang.ASTNode;
import java.util.Set;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Small implements PsiParser, LightPsiParser {

  public ASTNode parse(SyntaxElementType root_, SyntaxTreeBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(SyntaxElementType root_, SyntaxTreeBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxTreeBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxTreeBuilder builder_, int level_) {
    return parseRoot(builder_, level_ + 1, Small::statement);
  }

  /* ********************************************************** */
  // ()
  public static boolean empty(SyntaxTreeBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, EMPTY, true);
    return true;
  }

  /* ********************************************************** */
  // []
  public static boolean empty10(SyntaxTreeBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_, level_, _LEFT_INNER_, EMPTY_10, null);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // {}
  public static boolean empty2(SyntaxTreeBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, EMPTY_2, true);
    return true;
  }

  /* ********************************************************** */
  // []
  public static boolean empty3(SyntaxTreeBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, EMPTY_3, true);
    return true;
  }

  /* ********************************************************** */
  // ()
  static boolean empty4(SyntaxTreeBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // []
  static boolean empty5(SyntaxTreeBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // &()
  static boolean empty6(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "empty6")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _AND_);
    result_ = empty6_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ()
  private static boolean empty6_0(SyntaxTreeBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // !()
  static boolean empty7(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "empty7")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !empty7_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ()
  private static boolean empty7_0(SyntaxTreeBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // [({})]
  static boolean empty8(SyntaxTreeBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // []
  public static boolean empty9(SyntaxTreeBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, EMPTY_9, null);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // [({token})]
  static boolean not_empty1(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty1")) return false;
    not_empty1_0(builder_, level_ + 1);
    return true;
  }

  // {token}
  private static boolean not_empty1_0(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [({token someString})]
  static boolean not_empty2(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty2")) return false;
    not_empty2_0(builder_, level_ + 1);
    return true;
  }

  // token someString
  private static boolean not_empty2_0(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_empty2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    result_ = result_ && someString(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // ( token )
  public static boolean otherRule(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "otherRule")) return false;
    if (!nextTokenIs(builder_, TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    exit_section_(builder_, marker_, OTHER_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token
  static boolean privateRule(SyntaxTreeBuilder builder_, int level_) {
    return consumeToken(builder_, TOKEN);
  }

  /* ********************************************************** */
  // 'token'
  static boolean privateString(SyntaxTreeBuilder builder_, int level_) {
    return consumeToken(builder_, "token");
  }

  /* ********************************************************** */
  // token
  public static boolean someRule(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someRule")) return false;
    if (!nextTokenIs(builder_, TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    exit_section_(builder_, marker_, SOME_RULE, result_);
    return result_;
  }

  /* ********************************************************** */
  // token?
  public static boolean someRule2(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someRule2")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, SOME_RULE_2, "<some rule 2>");
    consumeToken(builder_, TOKEN);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // 'token'
  public static boolean someString(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someString")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, SOME_STRING, "<some string>");
    result_ = consumeToken(builder_, "token");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // token | someRule | someString
  public static boolean statement(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STATEMENT, "<statement>");
    result_ = consumeToken(builder_, TOKEN);
    if (!result_) result_ = someRule(builder_, level_ + 1);
    if (!result_) result_ = someString(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '=' "=" '==' "=="
  static boolean tokenRule(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tokenRule")) return false;
    if (!nextTokenIs(builder_, OP_EQ)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, OP_EQ, OP_EQ);
    result_ = result_ && consumeToken(builder_, "==");
    result_ = result_ && consumeToken(builder_, "==");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

}