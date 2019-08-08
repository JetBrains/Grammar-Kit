// ---- BindersAndHooks.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;
import static com.intellij.lang.WhitespacesBinders.*;
import static com.sample.MyHooks.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class BindersAndHooks implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // A item B
  public static boolean both_binders(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "both_binders")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BOTH_BINDERS, null);
    result_ = consumeToken(builder_, A);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, item(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, B) && result_;
    register_hook_(builder_, WS_BINDERS, GREEDY_LEFT_BINDER, GREEDY_RIGHT_BINDER);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A
  public static boolean got_hook(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "got_hook")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    register_hook_(builder_, MY_HOOK, "my", "hook", "param", "array");
    exit_section_(builder_, marker_, GOT_HOOK, result_);
    return result_;
  }

  /* ********************************************************** */
  static boolean item(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // A B
  public static boolean left_binder(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "left_binder")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, A, B);
    register_hook_(builder_, LEFT_BINDER, GREEDY_LEFT_BINDER);
    exit_section_(builder_, marker_, LEFT_BINDER, result_);
    return result_;
  }

  /* ********************************************************** */
  // item
  public static boolean right_binder(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "right_binder")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, RIGHT_BINDER, "<right binder>");
    result_ = item(builder_, level_ + 1);
    register_hook_(builder_, RIGHT_BINDER, GREEDY_RIGHT_BINDER);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // left_binder right_binder both_binders
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = left_binder(builder_, level_ + 1);
    result_ = result_ && right_binder(builder_, level_ + 1);
    result_ = result_ && both_binders(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

}