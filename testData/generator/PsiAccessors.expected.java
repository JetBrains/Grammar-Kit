// ---- PsiAccessors.java -----------------
//header.txt
package ;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.ParserTypes.*;
import static PsiGenUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PsiAccessors implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("PsiAccessors");

  @NotNull
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
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
      Marker marker_ = builder_.mark();
      enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
      result_ = parse_root_(root_, builder_, level_);
      exitErrorRecordingSection(builder_, level_, result_, true, _SECTION_RECOVER_, TOKEN_ADVANCER);
      marker_.done(root_);
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
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = expression(builder_, level_ + 1);
    result_ = result_ && operator(builder_, level_ + 1);
    pinned_ = result_; // pin = operator
    result_ = result_ && expression(builder_, level_ + 1);
    if (result_ || pinned_) {
      marker_.done(BINARY);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // value '*' value
  public static boolean expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = value(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, "*");
    result_ = result_ && value(builder_, level_ + 1);
    if (result_) {
      marker_.done(EXPRESSION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // '+' | '-'
  public static boolean operator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "operator")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<operator>");
    result_ = consumeToken(builder_, "+");
    if (!result_) result_ = consumeToken(builder_, "-");
    if (result_) {
      marker_.done(OPERATOR);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean re(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "re")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ID);
    if (result_) {
      marker_.done(RE);
    }
    else {
      marker_.rollbackTo();
    }
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
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ID);
    if (result_) {
      marker_.done(VALUE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

}