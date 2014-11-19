// ---- Fixes.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Fixes implements PsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parse_only_(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parse_only_(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == SOME) {
      result_ = some(builder_, 0);
    }
    else if (root_ == WITH_RECURSIVE) {
      result_ = with_recursive(builder_, 0);
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
  // some [recursive]
  static boolean recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = some(builder_, level_ + 1);
    result_ = result_ && recursive_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [recursive]
  private static boolean recursive_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive_1")) return false;
    recursive(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  static boolean root(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // A
  public static boolean some(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    exit_section_(builder_, marker_, SOME, result_);
    return result_;
  }

  /* ********************************************************** */
  // recursive
  public static boolean with_recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "with_recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = recursive(builder_, level_ + 1);
    exit_section_(builder_, marker_, WITH_RECURSIVE, result_);
    return result_;
  }

}