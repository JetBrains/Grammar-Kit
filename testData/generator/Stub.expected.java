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
    if (root_ == ELEMENT_1) {
      result_ = element1(builder_, 0);
    }
    else if (root_ == ELEMENT_2) {
      result_ = element2(builder_, 0);
    }
    else if (root_ == ELEMENT_3) {
      result_ = element3(builder_, 0);
    }
    else if (root_ == ELEMENT_4) {
      result_ = element4(builder_, 0);
    }
    else if (root_ == ELEMENT_5) {
      result_ = element5(builder_, 0);
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
  // 'aa'
  public static boolean element1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_1, "<element 1>");
    result_ = consumeToken(builder_, "aa");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'bb'
  public static boolean element2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_2, "<element 2>");
    result_ = consumeToken(builder_, "bb");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'bb'
  public static boolean element3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_3, "<element 3>");
    result_ = consumeToken(builder_, "bb");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'bb'
  public static boolean element4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element4")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_4, "<element 4>");
    result_ = consumeToken(builder_, "bb");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'cc'
  public static boolean element5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element5")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_5, "<element 5>");
    result_ = consumeToken(builder_, "cc");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // element1 | element2 | element3 | element4 | element5
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = element1(builder_, level_ + 1);
    if (!result_) result_ = element2(builder_, level_ + 1);
    if (!result_) result_ = element3(builder_, level_ + 1);
    if (!result_) result_ = element4(builder_, level_ + 1);
    if (!result_) result_ = element5(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

}