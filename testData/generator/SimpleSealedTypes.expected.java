// ---- GeneratedParser.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
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
  // 'bar'
  public static boolean bar(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bar")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BAR, "<bar>");
    result_ = consumeToken(builder_, "bar");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'baz'
  public static boolean baz(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "baz")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BAZ, "<baz>");
    result_ = consumeToken(builder_, "baz");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // bar | baz
  public static boolean foo(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foo")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_);
    result_ = bar(builder_, level_ + 1);
    if (!result_) result_ = baz(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // foo
  static boolean root(PsiBuilder builder_, int level_) {
    return foo(builder_, level_ + 1);
  }

}