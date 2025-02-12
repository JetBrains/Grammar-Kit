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
    return parseRoot(builder_, level_ + 1, GeneratedParser::class_$);
  }

  /* ********************************************************** */
  // token?
  public static boolean abstract_$(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "abstract_$")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ABSTRACT, "<abstract>");
    consumeToken(builder_, TOKEN);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // token | interface | record
  public static boolean class_$(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_$")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CLASS, "<class>");
    result_ = consumeToken(builder_, TOKEN);
    if (!result_) result_ = interface_$(builder_, level_ + 1);
    if (!result_) result_ = record(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // token
  public static boolean interface_$(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "interface_$")) return false;
    if (!nextTokenIs(builder_, TOKEN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, TOKEN);
    exit_section_(builder_, marker_, INTERFACE, result_);
    return result_;
  }

  /* ********************************************************** */
  // 'token'
  public static boolean record(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "record")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, RECORD, "<record>");
    result_ = consumeToken(builder_, "token");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

}