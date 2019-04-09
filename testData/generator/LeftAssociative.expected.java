// ---- LeftAssociative.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

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
public class LeftAssociative implements PsiParser, LightPsiParser {

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
    return from(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // AS? id
  public static boolean alias_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition")) return false;
    if (!nextTokenIs(builder_, "<alias definition>", AS, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, ALIAS_DEFINITION, "<alias definition>");
    result_ = alias_definition_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ID);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // AS?
  private static boolean alias_definition_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition_0")) return false;
    consumeToken(builder_, AS);
    return true;
  }

  /* ********************************************************** */
  // AS? id
  public static boolean alias_definition2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition2")) return false;
    if (!nextTokenIs(builder_, "<alias definition 2>", AS, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, ALIAS_DEFINITION_2, "<alias definition 2>");
    result_ = alias_definition2_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ID);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // AS?
  private static boolean alias_definition2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition2_0")) return false;
    consumeToken(builder_, AS);
    return true;
  }

  /* ********************************************************** */
  // reference alias_definition? alias_definition2? leech? leech2?
  static boolean from(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from")) return false;
    if (!nextTokenIs(builder_, REFERENCE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, REFERENCE);
    result_ = result_ && from_1(builder_, level_ + 1);
    result_ = result_ && from_2(builder_, level_ + 1);
    result_ = result_ && from_3(builder_, level_ + 1);
    result_ = result_ && from_4(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // alias_definition?
  private static boolean from_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_1")) return false;
    alias_definition(builder_, level_ + 1);
    return true;
  }

  // alias_definition2?
  private static boolean from_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_2")) return false;
    alias_definition2(builder_, level_ + 1);
    return true;
  }

  // leech?
  private static boolean from_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_3")) return false;
    leech(builder_, level_ + 1);
    return true;
  }

  // leech2?
  private static boolean from_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_4")) return false;
    leech2(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // id
  public static boolean leech(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "leech")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_INNER_, LEECH, null);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  static boolean leech2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "leech2")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_INNER_);
    result_ = consumeToken(builder_, ID);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id?
  static boolean leech3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "leech3")) return false;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_INNER_);
    consumeToken(builder_, ID);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

}