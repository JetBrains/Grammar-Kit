// ---- Small.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType
import java.util.List
import java.util.Map

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object Small {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, null)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return parseRoot(runtime_, level_ + 1, Small::statement)
  }

  /* ********************************************************** */
  // ()
  fun empty(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_()
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.EMPTY, true)
    return true
  }

  /* ********************************************************** */
  // []
  fun empty10(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_INNER_, GeneratedSyntaxElementTypes.EMPTY_10, null)
    runtime_.exit_section_(level_, marker_, true, false, null)
    return true
  }

  /* ********************************************************** */
  // {}
  fun empty2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_()
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.EMPTY_2, true)
    return true
  }

  /* ********************************************************** */
  // []
  fun empty3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_()
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.EMPTY_3, true)
    return true
  }

  /* ********************************************************** */
  // ()
  internal fun empty4(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // []
  internal fun empty5(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // &()
  internal fun empty6(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "empty6")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = empty6_0(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // ()
  private fun empty6_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // !()
  internal fun empty7(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "empty7")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !empty7_0(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // ()
  private fun empty7_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // [({})]
  internal fun empty8(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // []
  fun empty9(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.EMPTY_9, null)
    runtime_.exit_section_(level_, marker_, true, false, null)
    return true
  }

  /* ********************************************************** */
  // [({token})]
  internal fun not_empty1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "not_empty1")) return false
    not_empty1_0(runtime_, level_ + 1)
    return true
  }

  // {token}
  private fun not_empty1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "not_empty1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // [({token someString})]
  internal fun not_empty2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "not_empty2")) return false
    not_empty2_0(runtime_, level_ + 1)
    return true
  }

  // token someString
  private fun not_empty2_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "not_empty2_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    result_ = result_ && someString(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // ( token )
  fun otherRule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "otherRule")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.OTHER_RULE, result_)
    return result_
  }

  /* ********************************************************** */
  // token
  internal fun privateRule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
  }

  /* ********************************************************** */
  // 'token'
  internal fun privateString(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return runtime_.consumeToken("token")
  }

  /* ********************************************************** */
  // token
  fun someRule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "someRule")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.TOKEN)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SOME_RULE, result_)
    return result_
  }

  /* ********************************************************** */
  // token?
  fun someRule2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "someRule2")) return false
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.SOME_RULE_2, "<some rule 2>")
    runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    runtime_.exit_section_(level_, marker_, true, false, null)
    return true
  }

  /* ********************************************************** */
  // 'token'
  fun someString(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "someString")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.SOME_STRING, "<some string>")
    result_ = runtime_.consumeToken("token")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // token | someRule | someString
  fun statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.TOKEN)
    if (!result_) result_ = someRule(runtime_, level_ + 1)
    if (!result_) result_ = someString(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // '=' "=" '==' "=="
  internal fun tokenRule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "tokenRule")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.OP_EQ)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.OP_EQ, GeneratedSyntaxElementTypes.OP_EQ)
    result_ = result_ && runtime_.consumeToken("==")
    result_ = result_ && runtime_.consumeToken("==")
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

}