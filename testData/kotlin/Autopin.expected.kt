// ---- Autopin.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class Autopin(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: SyntaxElementType, builder_: SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(GeneratedSyntaxElementTypes.CREATE_STATEMENT, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, GeneratedSyntaxElementTypes.DROP_STATEMENT, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT,
        GeneratedSyntaxElementTypes.STATEMENT),
    )

    /* ********************************************************** */
    // create_table_statement
    fun create_statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.CREATE)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.CREATE_STATEMENT, null)
      result_ = create_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
    fun create_table_statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.CREATE)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.CREATE_TABLE_STATEMENT, null)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.CREATE)
      result_ = result_ && create_table_statement_1(builder_, level_ + 1)
      result_ = result_ && create_table_statement_2(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedSyntaxElementTypes.TABLE)
      result_ = result_ && parseReference(builder_, level_ + 1)
      pinned_ = result_ // pin = .*_ref
      result_ = result_ && report_error_(builder_, consumeToken(builder_, "("))
      result_ = pinned_ && consumeToken(builder_, ")") && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // TEMP?
    private fun create_table_statement_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement_1")) return false
      consumeToken(builder_, GeneratedSyntaxElementTypes.TEMP)
      return true
    }

    // GLOBAL|LOCAL
    private fun create_table_statement_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "create_table_statement_2")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.GLOBAL)
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.LOCAL)
      return result_
    }

    /* ********************************************************** */
    // drop_table_statement
    fun drop_statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.DROP)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.DROP_STATEMENT, null)
      result_ = drop_table_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // DROP TABLE table_ref
    fun drop_table_statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "drop_table_statement")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.DROP)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, GeneratedSyntaxElementTypes.DROP, GeneratedSyntaxElementTypes.TABLE)
      result_ = result_ && parseReference(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.DROP_TABLE_STATEMENT, result_)
      return result_
    }

    /* ********************************************************** */
    // a b (c d e)
    fun override_nested_sequence(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "override_nested_sequence")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.A)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.OVERRIDE_NESTED_SEQUENCE, null)
      result_ = consumeTokens(builder_, 1, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)
      pinned_ = result_ // pin = 1
      result_ = result_ && override_nested_sequence_2(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // c d e
    private fun override_nested_sequence_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "override_nested_sequence_2")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // [] (a|b)
    internal fun pinned_on_start(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_on_start")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = pinned_on_start_0(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && pinned_on_start_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // []
    private fun pinned_on_start_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    // a|b
    private fun pinned_on_start_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "pinned_on_start_1")) return false
      var result_: Boolean
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.A)
      if (!result_) result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.B)
      return result_
    }

    /* ********************************************************** */
    // statement *
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
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
    fun statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      if (!nextTokenIs(builder_, "<statement>", GeneratedSyntaxElementTypes.CREATE, GeneratedSyntaxElementTypes.DROP)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
      result_ = create_statement(builder_, level_ + 1)
      if (!result_) result_ = drop_statement(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // a b c d table_ref
    internal fun token_sequence1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence1")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.A)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeTokens(builder_, 3, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D)
      pinned_ = result_ // pin = 3
      result_ = result_ && parseReference(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // a b table_ref c d e
    internal fun token_sequence2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence2")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.A)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = consumeTokens(builder_, 0, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)
      result_ = result_ && parseReference(builder_, level_ + 1)
      result_ = result_ && consumeTokens(builder_, 2, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E)
      pinned_ = result_ // pin = 5
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // table_ref a b table_ref c d e
    internal fun token_sequence3(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence3")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = parseReference(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, consumeTokens(builder_, -1, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B))
      result_ = pinned_ && report_error_(builder_, parseReference(builder_, level_ + 1)) && result_
      result_ = pinned_ && report_error_(builder_, consumeTokens(builder_, -1, GeneratedSyntaxElementTypes.C, GeneratedSyntaxElementTypes.D, GeneratedSyntaxElementTypes.E)) && result_
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // [] a
    internal fun token_sequence4(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence4")) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_)
      result_ = token_sequence4_0(builder_, level_ + 1)
      pinned_ = result_ // pin = 1
      result_ = result_ && consumeToken(builder_, GeneratedSyntaxElementTypes.A)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    // []
    private fun token_sequence4_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // (a|&b) pinned_on_start
    internal fun token_sequence5(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence5")) return false
      if (!nextTokenIs(builder_, "", GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = token_sequence5_0(builder_, level_ + 1)
      result_ = result_ && pinned_on_start(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // a|&b
    private fun token_sequence5_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence5_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.A)
      if (!result_) result_ = token_sequence5_0_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &b
    private fun token_sequence5_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence5_0_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.B)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // '+' a "+" a '+++'
    internal fun token_sequence6(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "token_sequence6")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.PLUS)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, GeneratedSyntaxElementTypes.PLUS, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.PLUS, GeneratedSyntaxElementTypes.A)
      result_ = result_ && consumeToken(builder_, "+++")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

  }
}