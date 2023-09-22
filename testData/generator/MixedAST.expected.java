// ---- FooParser.java -----------------
//header.txt
package test;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static test.FooTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class FooParser implements PsiParser, LightPsiParser {

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
  // AA
  public static boolean element1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element1")) return false;
    if (!nextTokenIs(builder_, AA)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, AA);
    exit_section_(builder_, marker_, ELEMENT_1, result_);
    return result_;
  }

  /* ********************************************************** */
  // BB
  public static boolean element2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element2")) return false;
    if (!nextTokenIs(builder_, BB)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BB);
    exit_section_(builder_, marker_, ELEMENT_2, result_);
    return result_;
  }

  /* ********************************************************** */
  // BB
  public static boolean element3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element3")) return false;
    if (!nextTokenIs(builder_, BB)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, BB);
    exit_section_(builder_, marker_, ELEMENT_3, result_);
    return result_;
  }

  /* ********************************************************** */
  // element1 | element2 | element3
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    if (!nextTokenIs(builder_, "", AA, BB)) return false;
    boolean result_;
    result_ = element1(builder_, level_ + 1);
    if (!result_) result_ = element2(builder_, level_ + 1);
    if (!result_) result_ = element3(builder_, level_ + 1);
    return result_;
  }

}