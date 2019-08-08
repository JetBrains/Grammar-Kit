// ---- GeneratedParser.java -----------------
//header.txt
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
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return file(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ABC, ABC_ONE, ABC_PIN, ABC_SEQ,
      ABC_THREE, ABC_TWO),
  };

  /* ********************************************************** */
  // prefix? abc_three? (abc_one | abc_two | abc_three)
  public static boolean abc(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, ABC, "<abc>");
    result_ = abc_0(builder_, level_ + 1);
    result_ = result_ && abc_1(builder_, level_ + 1);
    result_ = result_ && abc_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // prefix?
  private static boolean abc_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_0")) return false;
    prefix(builder_, level_ + 1);
    return true;
  }

  // abc_three?
  private static boolean abc_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_1")) return false;
    abc_three(builder_, level_ + 1);
    return true;
  }

  // abc_one | abc_two | abc_three
  private static boolean abc_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_2")) return false;
    boolean result_;
    result_ = abc_one(builder_, level_ + 1);
    if (!result_) result_ = abc_two(builder_, level_ + 1);
    if (!result_) result_ = abc_three(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // B
  public static boolean abc_one(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_one")) return false;
    if (!nextTokenIs(builder_, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _UPPER_, ABC_ONE, null);
    result_ = consumeToken(builder_, B);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // prefix (abc_one | abc_two)
  public static boolean abc_pin(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_pin")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ABC_PIN, null);
    result_ = prefix(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && abc_pin_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // abc_one | abc_two
  private static boolean abc_pin_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_pin_1")) return false;
    boolean result_;
    result_ = abc_one(builder_, level_ + 1);
    if (!result_) result_ = abc_two(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // prefix abc_one abc_two
  public static boolean abc_seq(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_seq")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = prefix(builder_, level_ + 1);
    result_ = result_ && abc_one(builder_, level_ + 1);
    result_ = result_ && abc_two(builder_, level_ + 1);
    exit_section_(builder_, marker_, ABC_SEQ, result_);
    return result_;
  }

  /* ********************************************************** */
  // X
  public static boolean abc_three(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_three")) return false;
    if (!nextTokenIs(builder_, X)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, X);
    exit_section_(builder_, marker_, ABC_THREE, result_);
    return result_;
  }

  /* ********************************************************** */
  // C
  public static boolean abc_two(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abc_two")) return false;
    if (!nextTokenIs(builder_, C)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _UPPER_, ABC_TWO, null);
    result_ = consumeToken(builder_, C);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // root (';' root) *
  static boolean file(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "file")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = root(builder_, level_ + 1);
    result_ = result_ && file_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (';' root) *
  private static boolean file_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "file_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!file_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "file_1", pos_)) break;
    }
    return true;
  }

  // ';' root
  private static boolean file_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "file_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ";");
    result_ = result_ && root(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // A
  public static boolean prefix(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "prefix")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    exit_section_(builder_, marker_, PREFIX, result_);
    return result_;
  }

  /* ********************************************************** */
  // (abc | abc_pin | abc_seq) *
  public static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ROOT, "<root>");
    while (true) {
      int pos_ = current_position_(builder_);
      if (!root_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "root", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  // abc | abc_pin | abc_seq
  private static boolean root_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root_0")) return false;
    boolean result_;
    result_ = abc(builder_, level_ + 1);
    if (!result_) result_ = abc_pin(builder_, level_ + 1);
    if (!result_) result_ = abc_seq(builder_, level_ + 1);
    return result_;
  }

}