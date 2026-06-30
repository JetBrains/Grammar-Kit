// ---- To.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object To {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, null)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // privilege <<t>> user_ref CASCADE?
  internal fun grant_revoke_tail(runtime_: SyntaxGeneratedParserRuntime, level_: Int, t: Parser): Boolean {
    if (!runtime_.recursion_guard_(level_, "grant_revoke_tail")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_)
    result_ = privilege(runtime_, level_ + 1)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(t.parse(runtime_, level_))
    result_ = pinned_ && runtime_.report_error_(user_ref(runtime_, level_ + 1)) && result_
    result_ = pinned_ && grant_revoke_tail_3(runtime_, level_ + 1) && result_
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  // CASCADE?
  private fun grant_revoke_tail_3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "grant_revoke_tail_3")) return false
    runtime_.consumeToken(GeneratedSyntaxElementTypes.CASCADE)
    return true
  }

  /* ********************************************************** */
  // <<grant_revoke_tail <<to>>>>
  internal fun grant_tail(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return grant_revoke_tail(runtime_, level_ + 1, grant_tail_0_0_parser_)
  }

  /* ********************************************************** */
  // ID
  fun privilege(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "privilege")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.PRIVILEGE, result_)
    return result_
  }

  /* ********************************************************** */
  // to | grant_tail
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root")) return false
    if (!runtime_.nextTokenIs("", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.TO)) return false
    var result_: Boolean
    result_ = to__(runtime_, level_ + 1)
    if (!result_) result_ = grant_tail(runtime_, level_ + 1)
    return result_
  }

  /* ********************************************************** */
  // TO
  internal fun to__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return runtime_.consumeToken(GeneratedSyntaxElementTypes.TO)
  }

  /* ********************************************************** */
  // ID
  fun user_ref(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "user_ref")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.USER_REF, result_)
    return result_
  }

  internal val grant_tail_0_0_parser_: Parser = { runtime_, level_ -> to__(runtime_, level_ + 1) }
}
