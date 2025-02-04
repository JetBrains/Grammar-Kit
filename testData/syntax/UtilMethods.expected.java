// ---- FooParser.java -----------------
//header.txt
package test;

import com.intellij.lang.SyntaxTreeBuilder;
import com.intellij.lang.SyntaxTreeBuilder.Marker;
import static test.FooTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.lang.ASTNode;
import java.util.Set;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class FooParser implements PsiParser, LightPsiParser {

  public ASTNode parse(SyntaxElementType root_, SyntaxTreeBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(SyntaxElementType root_, SyntaxTreeBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxTreeBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxTreeBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // 'aa'
  public static boolean element1(SyntaxTreeBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_1, "<element 1>");
    result_ = consumeToken(builder_, "aa");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // element1
  static boolean root(SyntaxTreeBuilder builder_, int level_) {
    return element1(builder_, level_ + 1);
  }

}