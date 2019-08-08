// ---- PsiAccessors.java -----------------
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
public class PsiAccessors implements PsiParser, LightPsiParser {

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
    boolean result_;
    if (root_ == EXPRESSION) {
      result_ = expression(builder_, level_ + 1);
    }
    else {
      result_ = root(builder_, level_ + 1);
    }
    return result_;
  }

  /* ********************************************************** */
  // expression operator expression
  public static boolean binary(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "binary")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BINARY, null);
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && operator(builder_, level_ + 1);
    pinned_ = result_; // pin = operator
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // value '*' value
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = value(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, "*");
    result_ = result_ && value(builder_, level_ + 1);
    exit_section_(builder_, marker_, EXPRESSION, result_);
    return result_;
  }

  /* ********************************************************** */
  // '+' | '-'
  public static boolean operator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "operator")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, OPERATOR, "<operator>");
    result_ = consumeToken(builder_, "+");
    if (!result_) result_ = consumeToken(builder_, "-");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // binary
  static boolean root(PsiBuilder builder_, int level_) {
    return binary(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // id
  public static boolean value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "value")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, VALUE, result_);
    return result_;
  }

}