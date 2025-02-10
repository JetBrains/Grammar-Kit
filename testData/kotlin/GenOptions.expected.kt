// ---- GenOptions.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedTypes.*
import com.intellij.lang.parser.GeneratedParserUtilBase.*

class GenOptions: com.intellij.lang.PsiParser, com.intellij.lang.LightPsiParser {

  override fun parse(root_: com.intellij.psi.tree.IElementType, builder_: com.intellij.lang.PsiBuilder): com.intellij.lang.ASTNode {
    parseLight(root_, builder_)
    return builder_.getTreeBuilt()
  }

  override fun parseLight(root_: com.intellij.psi.tree.IElementType, builder_: com.intellij.lang.PsiBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: com.intellij.lang.PsiBuilder.Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: com.intellij.psi.tree.IElementType, builder_: com.intellij.lang.PsiBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: com.intellij.psi.tree.IElementType, builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<com.intellij.psi.tree.TokenSet> = arrayOf(
      create_token_set_(CREATE_STATEMENT, CREATE_TABLE_STATEMENT, DROP_STATEMENT, DROP_TABLE_STATEMENT,
        STATEMENT),
    )

    /* ********************************************************** */
    // create_table_statement
    fun create_statement(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_statement")) return false
      if (!nextTokenIs(builder_, create)) return false
      var result_: Boolean
      val marker_: com.intellij.lang.PsiBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, CREATE_STATEMENT, null)
      result_ = create_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
    fun create_table_statement(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement")) return false
      if (!nextTokenIs(builder_, create)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: com.intellij.lang.PsiBuilder.Marker = enter_section_(builder_, level_, _NONE_, CREATE_TABLE_STATEMENT, null)
      result_ = consumeToken(builder_, create)
      result_ = result_ && create_table_statement_1(builder_, level_ + 1)
      result_ = result_ && create_table_statement_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, table)
      result_ = result_ && table_ref(builder_, level_ + 1)
      pinned_ = result_ // pin = .*_ref
      result_ = result_ && report_error_(builder_, consumeToken(builder_, "("))
      result_ = pinned_ && consumeToken(builder_, ")") && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // TEMP?
    private fun create_table_statement_1(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement_1")) return false
      consumeToken(builder_, temp)
      return true
    }

    // GLOBAL|LOCAL
    private fun create_table_statement_2(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement_2")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, global)
      if (!result_) result_ = consumeToken(builder_, local)
      return result_
    }

    /* ********************************************************** */
    // drop_table_statement
    fun drop_statement(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_statement")) return false
      if (!nextTokenIs(builder_, drop)) return false
      var result_: Boolean
      val marker_: com.intellij.lang.PsiBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, DROP_STATEMENT, null)
      result_ = drop_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // DROP TABLE table_ref
    fun drop_table_statement(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_table_statement")) return false
      if (!nextTokenIs(builder_, drop)) return false
      var result_: Boolean
      val marker_: com.intellij.lang.PsiBuilder.Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, drop, table)
      result_ = result_ && table_ref(builder_, level_ + 1)
      exit_section_(builder_, marker_, DROP_TABLE_STATEMENT, result_)
      return result_
    }

    /* ********************************************************** */
    // statement *
    internal fun root(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!statement(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "root", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // create_statement | drop_statement
    fun statement(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      if (!nextTokenIs(builder_, "<statement>", create, drop)) return false
      var result_: Boolean
      val marker_: com.intellij.lang.PsiBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, STATEMENT, "<statement>")
      result_ = create_statement(builder_, level_ + 1)
      if (!result_) result_ = drop_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id
    fun table_ref(builder_: com.intellij.lang.PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "table_ref")) return false
      if (!nextTokenIs(builder_, id)) return false
      var result_: Boolean
      val marker_: com.intellij.lang.PsiBuilder.Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, id)
      exit_section_(builder_, marker_, TABLE_REF, result_)
      return result_
    }

  }
}