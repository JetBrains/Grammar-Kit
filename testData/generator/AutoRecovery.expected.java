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
    return file(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // list (';' list ) *
  static boolean file(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "file")) return false;
    if (!nextTokenIs(builder_, PAREN1)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = list(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && file_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (';' list ) *
  private static boolean file_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "file_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!file_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "file_1", pos_)) break;
    }
    return true;
  }

  // ';' list
  private static boolean file_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "file_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, SEMI);
    pinned_ = result_; // pin = 1
    result_ = result_ && list(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // number
  public static boolean item(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "item")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ITEM, "<item>");
    result_ = consumeToken(builder_, NUMBER);
    exit_section_(builder_, level_, marker_, result_, false, item_auto_recover_);
    return result_;
  }

  /* ********************************************************** */
  // "(" [!")" item (',' item) * ] ")"
  public static boolean list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list")) return false;
    if (!nextTokenIs(builder_, PAREN1)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LIST, null);
    result_ = consumeToken(builder_, PAREN1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, list_1(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, PAREN2) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // [!")" item (',' item) * ]
  private static boolean list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_1")) return false;
    list_1_0(builder_, level_ + 1);
    return true;
  }

  // !")" item (',' item) *
  private static boolean list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = list_1_0_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, item(builder_, level_ + 1));
    result_ = pinned_ && list_1_0_2(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // !")"
  private static boolean list_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !consumeToken(builder_, PAREN2);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (',' item) *
  private static boolean list_1_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_1_0_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!list_1_0_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "list_1_0_2", pos_)) break;
    }
    return true;
  }

  // ',' item
  private static boolean list_1_0_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "list_1_0_2_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && item(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  static final Parser item_auto_recover_ = (builder_, level_) -> !nextTokenIsFast(builder_, PAREN2, COMMA, SEMI);
}