// ---- GenOptions.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

@java.lang.SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GenOptions implements com.intellij.lang.PsiParser, com.intellij.lang.LightPsiParser {

  public com.intellij.lang.ASTNode parse(com.intellij.psi.tree.IElementType root_, com.intellij.lang.PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(com.intellij.psi.tree.IElementType root_, com.intellij.lang.PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    com.intellij.lang.PsiBuilder.Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(com.intellij.psi.tree.IElementType root_, com.intellij.lang.PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(com.intellij.psi.tree.IElementType root_, com.intellij.lang.PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  public static final com.intellij.psi.tree.TokenSet[] EXTENDS_SETS_ = new com.intellij.psi.tree.TokenSet[] {
    create_token_set_(CREATE_STATEMENT, CREATE_TABLE_STATEMENT, DROP_STATEMENT, DROP_TABLE_STATEMENT,
      STATEMENT),
  };

  /* ********************************************************** */
  // create_table_statement
  public static boolean create_statement(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_statement")) return false;
    if (!nextTokenIs(builder_, create)) return false;
    boolean result_;
    com.intellij.lang.PsiBuilder.Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, CREATE_STATEMENT, null);
    result_ = create_table_statement(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
  public static boolean create_table_statement(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement")) return false;
    if (!nextTokenIs(builder_, create)) return false;
    boolean result_, pinned_;
    com.intellij.lang.PsiBuilder.Marker marker_ = enter_section_(builder_, level_, _NONE_, CREATE_TABLE_STATEMENT, null);
    result_ = consumeToken(builder_, create);
    result_ = result_ && create_table_statement_1(builder_, level_ + 1);
    result_ = result_ && create_table_statement_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, table);
    result_ = result_ && table_ref(builder_, level_ + 1);
    pinned_ = result_; // pin = .*_ref
    result_ = result_ && report_error_(builder_, consumeToken(builder_, "("));
    result_ = pinned_ && consumeToken(builder_, ")") && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // TEMP?
  private static boolean create_table_statement_1(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement_1")) return false;
    consumeToken(builder_, temp);
    return true;
  }

  // GLOBAL|LOCAL
  private static boolean create_table_statement_2(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement_2")) return false;
    boolean result_;
    result_ = consumeToken(builder_, global);
    if (!result_) result_ = consumeToken(builder_, local);
    return result_;
  }

  /* ********************************************************** */
  // drop_table_statement
  public static boolean drop_statement(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "drop_statement")) return false;
    if (!nextTokenIs(builder_, drop)) return false;
    boolean result_;
    com.intellij.lang.PsiBuilder.Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, DROP_STATEMENT, null);
    result_ = drop_table_statement(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // DROP TABLE table_ref
  public static boolean drop_table_statement(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "drop_table_statement")) return false;
    if (!nextTokenIs(builder_, drop)) return false;
    boolean result_;
    com.intellij.lang.PsiBuilder.Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, drop, table);
    result_ = result_ && table_ref(builder_, level_ + 1);
    exit_section_(builder_, marker_, DROP_TABLE_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // statement *
  static boolean root(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!statement(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "root", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // create_statement | drop_statement
  public static boolean statement(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    if (!nextTokenIs(builder_, "<statement>", create, drop)) return false;
    boolean result_;
    com.intellij.lang.PsiBuilder.Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, STATEMENT, "<statement>");
    result_ = create_statement(builder_, level_ + 1);
    if (!result_) result_ = drop_statement(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean table_ref(com.intellij.lang.PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "table_ref")) return false;
    if (!nextTokenIs(builder_, id)) return false;
    boolean result_;
    com.intellij.lang.PsiBuilder.Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, id);
    exit_section_(builder_, marker_, TABLE_REF, result_);
    return result_;
  }

}