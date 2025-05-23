// ---- Autopin.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object Autopin {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, EXTENDS_SETS_)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(GeneratedSyntaxElementTypes.CREATE_STATEMENT, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, GeneratedSyntaxElementTypes.DROP_STATEMENT, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT,
      GeneratedSyntaxElementTypes.STATEMENT),
  )

  /* ********************************************************** */
  // create_table_statement
  fun create_statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.CREATE)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.CREATE_STATEMENT, null)
    result_ = create_table_statement(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
  fun create_table_statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_table_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.CREATE)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.CREATE)
    result_ = result_ && create_table_statement_1(runtime_, level_ + 1)
    result_ = result_ && create_table_statement_2(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.TABLE)
    result_ = result_ && parseReference(runtime_, level_ + 1)
    pinned_ = result_ // pin = .*_ref
    result_ = result_ && runtime_.report_error_(runtime_.consumeToken("("))
    result_ = pinned_ && runtime_.consumeToken(")") && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // TEMP?
  private fun create_table_statement_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_table_statement_1")) return false
    runtime_.consumeToken(GeneratedSyntaxElementTypes.TEMP)
    return true
  }

  // GLOBAL|LOCAL
  private fun create_table_statement_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "create_table_statement_2")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.GLOBAL)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.LOCAL)
    return result_
  }

  /* ********************************************************** */
  // drop_table_statement
  fun drop_statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "drop_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.DROP)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.DROP_STATEMENT, null)
    result_ = drop_table_statement(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // DROP TABLE table_ref
  fun drop_table_statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "drop_table_statement")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.DROP)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.DROP, GeneratedSyntaxElementTypes.TABLE)
    result_ = result_ && parseReference(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT, result_)
    return result_
  }

  /* ********************************************************** */
  // a b (c d e)
  fun override_nested_sequence(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "override_nested_sequence")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.OVERRIDE_NESTED_SEQUENCE, null)
    result_ = runtime_.consumeTokens(1, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)
    pinned_ = result_ // pin = 1
    result_ = result_ && override_nested_sequence_2(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // c d e
  private fun override_nested_sequence_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "override_nested_sequence_2")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // [] (a|b)
  internal fun pinned_on_start(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_on_start")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = pinned_on_start_0(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && pinned_on_start_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // []
  private fun pinned_on_start_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  // a|b
  private fun pinned_on_start_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "pinned_on_start_1")) return false
    var result_: Boolean
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    return result_
  }

  /* ********************************************************** */
  // statement *
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
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
  fun statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement")) return false
    if (!runtime_.nextTokenIs("<statement>", GeneratedSyntaxElementTypes.CREATE, GeneratedSyntaxElementTypes.DROP)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
    result_ = create_statement(runtime_, level_ + 1)
    if (!result_) result_ = drop_statement(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // a b c d table_ref
  internal fun token_sequence1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence1")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeTokens(3, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D)
    pinned_ = result_ // pin = 3
    result_ = result_ && parseReference(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // a b table_ref c d e
  internal fun token_sequence2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence2")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)
    result_ = result_ && parseReference(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeTokens(2, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E)
    pinned_ = result_ // pin = 5
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // table_ref a b table_ref c d e
  internal fun token_sequence3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence3")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = parseReference(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(runtime_.consumeTokens(-1, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B))
    result_ = pinned_ && runtime_.report_error_(parseReference(runtime_, level_ + 1)) && result_
    result_ = pinned_ && runtime_.report_error_(runtime_.consumeTokens(-1, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E)) && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // [] a
  internal fun token_sequence4(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence4")) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = token_sequence4_0(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // []
  private fun token_sequence4_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // (a|&b) pinned_on_start
  internal fun token_sequence5(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence5")) return false
    if (!runtime_.nextTokenIs("", GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = token_sequence5_0(runtime_, level_ + 1)
    result_ = result_ && pinned_on_start(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // a|&b
  private fun token_sequence5_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence5_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    if (!result_) result_ = token_sequence5_0_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // &b
  private fun token_sequence5_0_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence5_0_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.B)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // '+' a "+" a '+++'
  internal fun token_sequence6(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "token_sequence6")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.PLUS)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.PLUS, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.PLUS, GeneratedSyntaxElementTypes.A)
    result_ = result_ && runtime_.consumeToken("+++")
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

}