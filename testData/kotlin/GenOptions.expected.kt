// ---- GenOptions.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*

@kotlin.Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object GenOptions {

  fun parse(root_: com.intellij.platform.syntax.SyntaxElementType, runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, EXTENDS_SETS_)
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = runtime_.enter_section_(0, com.intellij.platform.syntax.util.runtime.Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: com.intellij.platform.syntax.SyntaxElementType, runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  val EXTENDS_SETS_: kotlin.Array<com.intellij.platform.syntax.SyntaxElementTypeSet> = kotlin.arrayOf(
    create_token_set_(GeneratedSyntaxElementTypes.CREATE_STATEMENT, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, GeneratedSyntaxElementTypes.DROP_STATEMENT, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT,
      GeneratedSyntaxElementTypes.STATEMENT),
  )

  /* ********************************************************** */
  // create_table_statement
  fun create_statement(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.create)) return false
    var result_: Boolean
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = runtime_.enter_section_(level_, com.intellij.platform.syntax.util.runtime.Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.CREATE_STATEMENT, null)
    result_ = create_table_statement(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
  fun create_table_statement(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_table_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.create)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = runtime_.enter_section_(level_, com.intellij.platform.syntax.util.runtime.Modifiers._NONE_, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.create)
    result_ = result_ && create_table_statement_1(runtime_, level_ + 1)
    result_ = result_ && create_table_statement_2(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.table)
    result_ = result_ && table_ref(runtime_, level_ + 1)
    pinned_ = result_ // pin = .*_ref
    result_ = result_ && runtime_.report_error_(runtime_.consumeToken("("))
    result_ = pinned_ && runtime_.consumeToken(")") && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // TEMP?
  private fun create_table_statement_1(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_table_statement_1")) return false
    runtime_.consumeToken(GeneratedSyntaxElementTypes.temp)
    return true
  }

  // GLOBAL|LOCAL
  private fun create_table_statement_2(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_table_statement_2")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.global)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.local)
    return result_
  }

  /* ********************************************************** */
  // drop_table_statement
  fun drop_statement(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "drop_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.drop)) return false
    var result_: Boolean
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = runtime_.enter_section_(level_, com.intellij.platform.syntax.util.runtime.Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.DROP_STATEMENT, null)
    result_ = drop_table_statement(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // DROP TABLE table_ref
  fun drop_table_statement(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "drop_table_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.drop)) return false
    var result_: Boolean
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.drop, GeneratedSyntaxElementTypes.table)
    result_ = result_ && table_ref(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT, result_)
    return result_
  }

  /* ********************************************************** */
  // statement *
  internal fun root(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!statement(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("root", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // create_statement | drop_statement
  fun statement(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement")) return false
    if (!runtime_.nextTokenIs("<statement>", GeneratedSyntaxElementTypes.create, GeneratedSyntaxElementTypes.drop)) return false
    var result_: Boolean
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = runtime_.enter_section_(level_, com.intellij.platform.syntax.util.runtime.Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
    result_ = create_statement(runtime_, level_ + 1)
    if (!result_) result_ = drop_statement(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // id
  fun table_ref(runtime_: com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "table_ref")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.id)) return false
    var result_: Boolean
    val marker_: com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.id)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.TABLE_REF, result_)
    return result_
  }

}