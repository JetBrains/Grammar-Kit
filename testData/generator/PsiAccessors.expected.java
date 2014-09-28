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

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiAccessors implements PsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parse_only_(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parse_only_(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == BINARY) {
      result_ = binary(builder_, 0);
    }
    else if (root_ == EXPRESSION) {
      result_ = expression(builder_, 0);
    }
    else if (root_ == OPERATOR) {
      result_ = operator(builder_, 0);
    }
    else if (root_ == VALUE) {
      result_ = value(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // expression operator expression
  public static boolean binary(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "binary")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && operator(builder_, level_ + 1);
    pinned_ = result_; // pin = operator
    result_ = result_ && expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, BINARY, result_, pinned_, null);
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
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<operator>");
    result_ = consumeToken(builder_, "+");
    if (!result_) result_ = consumeToken(builder_, "-");
    exit_section_(builder_, level_, marker_, OPERATOR, result_, false, null);
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