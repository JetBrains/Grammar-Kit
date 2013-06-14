// ---- PsiAccessors.java -----------------
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
public class PsiAccessors implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("PsiAccessors");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    if (root_ == BINARY) {
      result_ = binary(builder_, level_ + 1);
    }
    else if (root_ == EXPRESSION) {
      result_ = expression(builder_, level_ + 1);
    }
    else if (root_ == OPERATOR) {
      result_ = operator(builder_, level_ + 1);
    }
    else if (root_ == RE) {
      result_ = re(builder_, level_ + 1);
    }
    else if (root_ == VALUE) {
      result_ = value(builder_, level_ + 1);
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

  /* ********************************************************** */
  // expression operator expression
  public static boolean binary(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "binary")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && operator(builder_, level_ + 1);
    pinned_ = result_; // pin = operator
    result_ = result_ && expression(builder_, level_ + 1);
    result_ = exit_section_(builder_, level_, marker_, BINARY, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // value '*' value
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
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
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<operator>");
    result_ = consumeToken(builder_, "+");
    if (!result_) result_ = consumeToken(builder_, "-");
    result_ = exit_section_(builder_, level_, marker_, OPERATOR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean re(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "re")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, RE, result_);
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
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, VALUE, result_);
    return result_;
  }

}