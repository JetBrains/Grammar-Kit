// ---- GenOptions.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedTypes

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
      create_token_set_(GeneratedTypes.CREATE_STATEMENT, GeneratedTypes.CREATE_TABLE_STATEMENT, GeneratedTypes.DROP_STATEMENT, GeneratedTypes.DROP_TABLE_STATEMENT,
        GeneratedTypes.STATEMENT),
    )

    /* ********************************************************** */
    // create_table_statement
    fun create_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_statement")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.create)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.CREATE_STATEMENT, null)
      result_ = create_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
    fun create_table_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.create)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.CREATE_TABLE_STATEMENT, null)
      result_ = consumeToken(builder_, GeneratedTypes.create)
      result_ = result_ && create_table_statement_1(builder_, level_ + 1)
      result_ = result_ && create_table_statement_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedTypes.table)
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
      consumeToken(builder_, GeneratedTypes.temp)
      return true
    }

    // GLOBAL|LOCAL
    private fun create_table_statement_2(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement_2")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedTypes.global)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.local)
      return result_
    }

    /* ********************************************************** */
    // drop_table_statement
    fun drop_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_statement")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.drop)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.DROP_STATEMENT, null)
      result_ = drop_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // DROP TABLE table_ref
    fun drop_table_statement(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_table_statement")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.drop)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, GeneratedTypes.drop, GeneratedTypes.table)
      result_ = result_ && table_ref(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.DROP_TABLE_STATEMENT, result_)
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
      if (!nextTokenIs(builder_, "<statement>", GeneratedTypes.create, GeneratedTypes.drop)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.STATEMENT, "<statement>")
      result_ = create_statement(builder_, level_ + 1)
      if (!result_) result_ = drop_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id
    fun table_ref(builder_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "table_ref")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.id)) return false
      var result_: Boolean
      val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.id)
      exit_section_(builder_, marker_, GeneratedTypes.TABLE_REF, result_)
      return result_
    }

  }
}