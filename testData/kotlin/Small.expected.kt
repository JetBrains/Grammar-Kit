// ---- Small.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase
import java.util.List
import java.util.Map

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class Small(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: SyntaxElementType, builder_: SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, null)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return parseRoot(builder_, level_ + 1, Small::statement)
    }

    /* ********************************************************** */
    // ()
    fun empty(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.EMPTY, true)
      return true
    }

    /* ********************************************************** */
    // []
    fun empty10(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_INNER_, GeneratedSyntaxElementTypes.EMPTY_10, null)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // {}
    fun empty2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.EMPTY_2, true)
      return true
    }

    /* ********************************************************** */
    // []
    fun empty3(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.EMPTY_3, true)
      return true
    }

    /* ********************************************************** */
    // ()
    internal fun empty4(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // []
    internal fun empty5(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // &()
    internal fun empty6(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "empty6")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = empty6_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ()
    private fun empty6_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // !()
    internal fun empty7(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "empty7")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !empty7_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ()
    private fun empty7_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // [({})]
    internal fun empty8(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // []
    fun empty9(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedSyntaxElementTypes.EMPTY_9, null)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // [({token})]
    internal fun not_empty1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty1")) return false
      not_empty1_0(builder_, level_ + 1)
      return true
    }

    // {token}
    private fun not_empty1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.TOKEN)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // [({token someString})]
    internal fun not_empty2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty2")) return false
      not_empty2_0(builder_, level_ + 1)
      return true
    }

    // token someString
    private fun not_empty2_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty2_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.TOKEN)
      result_ = result_ && someString(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // ( token )
    fun otherRule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "otherRule")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.TOKEN)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.OTHER_RULE, result_)
      return result_
    }

    /* ********************************************************** */
    // token
    internal fun privateRule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return consumeToken(builder_, GeneratedSyntaxElementTypes.TOKEN)
    }

    /* ********************************************************** */
    // 'token'
    internal fun privateString(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return consumeToken(builder_, "token")
    }

    /* ********************************************************** */
    // token
    fun someRule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "someRule")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.TOKEN)
      exit_section_(builder_, marker_, GeneratedSyntaxElementTypes.SOME_RULE, result_)
      return result_
    }

    /* ********************************************************** */
    // token?
    fun someRule2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "someRule2")) return false
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.SOME_RULE_2, "<some rule 2>")
      consumeToken(builder_, GeneratedSyntaxElementTypes.TOKEN)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // 'token'
    fun someString(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "someString")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.SOME_STRING, "<some string>")
      result_ = consumeToken(builder_, "token")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // token | someRule | someString
    fun statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
      result_ = consumeToken(builder_, GeneratedSyntaxElementTypes.TOKEN)
      if (!result_) result_ = someRule(builder_, level_ + 1)
      if (!result_) result_ = someString(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // '=' "=" '==' "=="
    internal fun tokenRule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "tokenRule")) return false
      if (!nextTokenIs(builder_, GeneratedSyntaxElementTypes.OP_EQ)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, GeneratedSyntaxElementTypes.OP_EQ, GeneratedSyntaxElementTypes.OP_EQ)
      result_ = result_ && consumeToken(builder_, "==")
      result_ = result_ && consumeToken(builder_, "==")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

  }
}