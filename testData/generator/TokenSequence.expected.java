// ---- GeneratedParser.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GeneratedParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ instanceof IFileElementType) {
      result_ = parse_root_(root_, builder_, 0);
    }
    else {
      result_ = false;
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return foo(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // a
  public static boolean bar(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bar")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    exit_section_(builder_, marker_, BAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // a 
  //   | b c d
  //   | c d e
  //   | bar d e f
  static boolean foo(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foo")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = parseTokens(builder_, 2, B, C, D);
    if (!result_) result_ = parseTokens(builder_, 2, C, D, E);
    if (!result_) result_ = foo_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // bar d e f
  private static boolean foo_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foo_3")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = bar(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 1, D, E, F);
    pinned_ = result_; // pin = 2
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

}