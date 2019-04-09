// ---- FooParser.java -----------------
//header.txt
package test;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static test.FooTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
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
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
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
    return root(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(INTERFACE_TYPE, STRUCT_TYPE, TYPE),
  };

  /* ********************************************************** */
  // 'aa' element5
  public static boolean element1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_1, "<element 1>");
    result_ = consumeToken(builder_, "aa");
    result_ = result_ && element5(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'bb' element4*
  public static boolean element2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_2, "<element 2>");
    result_ = consumeToken(builder_, "bb");
    result_ = result_ && element2_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // element4*
  private static boolean element2_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element2_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!element4(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "element2_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'bb' element4
  public static boolean element3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_3, "<element 3>");
    result_ = consumeToken(builder_, "bb");
    result_ = result_ && element4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'bb' | element2
  public static boolean element4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "element4")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ELEMENT_4, "<element 4>");
    result_ = consumeToken(builder_, "bb");
    if (!result_) result_ = element2(builder_, level_ + 1);
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
  // 'interface'
  public static boolean interface_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, INTERFACE_TYPE, "<interface type>");
    result_ = consumeToken(builder_, "interface");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // element1 | element2 | element3 | element4 | element5 | type
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    boolean result_;
    result_ = element1(builder_, level_ + 1);
    if (!result_) result_ = element2(builder_, level_ + 1);
    if (!result_) result_ = element3(builder_, level_ + 1);
    if (!result_) result_ = element4(builder_, level_ + 1);
    if (!result_) result_ = element5(builder_, level_ + 1);
    if (!result_) result_ = type(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // 'struct'
  public static boolean struct_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRUCT_TYPE, "<struct type>");
    result_ = consumeToken(builder_, "struct");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // interface_type | struct_type
  public static boolean type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, TYPE, "<type>");
    result_ = interface_type(builder_, level_ + 1);
    if (!result_) result_ = struct_type(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

}