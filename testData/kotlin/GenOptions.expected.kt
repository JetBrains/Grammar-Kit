// ---- GenOptions.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes

@kotlin.Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class GenOptions(protected val runtime_: com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: com.intellij.platform.syntax.SyntaxElementType, builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: com.intellij.platform.syntax.SyntaxElementType, builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: com.intellij.platform.syntax.SyntaxElementType, builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: kotlin.Array<kotlin.collections.Set<com.intellij.platform.syntax.SyntaxElementType>> = kotlin.arrayOf(
      create_token_set_(GeneratedSyntaxElementTypes.CREATE_STATEMENT, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, GeneratedSyntaxElementTypes.DROP_STATEMENT, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT,
        GeneratedSyntaxElementTypes.STATEMENT),
    )

    /* ********************************************************** */
    // create_table_statement
    fun create_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.create)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.CREATE_STATEMENT, null)
      result_ = create_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
    fun create_table_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.create)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, null)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.create)
      result_ = result_ && create_table_statement_1(builder_, level_ + 1)
      result_ = result_ && create_table_statement_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedSyntaxElementTypes.table)
      result_ = result_ && table_ref(builder_, level_ + 1)
      pinned_ = result_ // pin = .*_ref
      result_ = result_ && report_error_(builder_, consumeToken(builder_, "("))
      result_ = pinned_ && consumeToken(builder_, ")") && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // TEMP?
    private fun create_table_statement_1(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement_1")) return false
      consumeToken(builder_, GeneratedSyntaxElementTypes.temp)
      return true
    }

    // GLOBAL|LOCAL
    private fun create_table_statement_2(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement_2")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.global)
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.local)
      return result_
    }

    /* ********************************************************** */
    // drop_table_statement
    fun drop_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.drop)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.DROP_STATEMENT, null)
      result_ = drop_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // DROP TABLE table_ref
    fun drop_table_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_table_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.drop)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, GeneratedSyntaxElementTypes.drop, GeneratedSyntaxElementTypes.table)
      result_ = result_ && table_ref(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT, result_)
      return result_
    }

    /* ********************************************************** */
    // statement *
    internal fun root(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
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
    fun statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      if (!nextTokenIs(builder_, "<statement>", GeneratedSyntaxElementTypes.create, GeneratedSyntaxElementTypes.drop)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
      result_ = create_statement(builder_, level_ + 1)
      if (!result_) result_ = drop_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id
    fun table_ref(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "table_ref")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.id)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.id)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.TABLE_REF, result_)
      return result_
    }

  }
}