// ---- To.java -----------------
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
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class To implements PsiParser, LightPsiParser {

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
  // privilege <<t>> user_ref CASCADE?
  static boolean grant_revoke_tail(PsiBuilder builder_, int level_, Parser t) {
    if (!recursion_guard_(builder_, level_, "grant_revoke_tail")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = privilege(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, t.parse(builder_, level_));
    result_ = pinned_ && report_error_(builder_, user_ref(builder_, level_ + 1)) && result_;
    result_ = pinned_ && grant_revoke_tail_3(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // CASCADE?
  private static boolean grant_revoke_tail_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "grant_revoke_tail_3")) return false;
    consumeToken(builder_, CASCADE);
    return true;
  }

  /* ********************************************************** */
  // <<grant_revoke_tail <<to>>>>
  static boolean grant_tail(PsiBuilder builder_, int level_) {
    return grant_revoke_tail(builder_, level_ + 1, grant_tail_0_0_parser_);
  }

  /* ********************************************************** */
  // ID
  public static boolean privilege(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "privilege")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, PRIVILEGE, result_);
    return result_;
  }

  /* ********************************************************** */
  // to | grant_tail
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    if (!nextTokenIs(builder_, "", ID, TO)) return false;
    boolean result_;
    result_ = to_$(builder_, level_ + 1);
    if (!result_) result_ = grant_tail(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // TO
  static boolean to_$(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, TO);
  }

  /* ********************************************************** */
  // ID
  public static boolean user_ref(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "user_ref")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, marker_, USER_REF, result_);
    return result_;
  }

  static final Parser grant_tail_0_0_parser_ = (builder_, level_) -> to_$(builder_, level_ + 1);
}
