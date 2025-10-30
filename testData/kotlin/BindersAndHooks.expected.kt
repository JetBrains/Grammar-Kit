// ---- BindersAndHooks.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.lang.WhitespacesBinders.*
import com.sample.MyHooks.*

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object BindersAndHooks {

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
  // A item B
  fun both_binders(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "both_binders")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.BOTH_BINDERS, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    pinned_ = result_ // pin = 1
    result_ = result_ && runtime_.report_error_(item(runtime_, level_ + 1))
    result_ = pinned_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.B) && result_
    runtime_.register_hook_(WS_BINDERS, GREEDY_LEFT_BINDER, GREEDY_RIGHT_BINDER)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // A
  fun got_hook(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "got_hook")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.A)
    runtime_.register_hook_(MY_HOOK, "my", "hook", "param", "array")
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.GOT_HOOK, result_)
    return result_
  }

  /* ********************************************************** */
  internal fun item(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // A B
  fun left_binder(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "left_binder")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.A, GeneratedSyntaxElementTypes.B)
    runtime_.register_hook_(LEFT_BINDER, GREEDY_LEFT_BINDER)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.LEFT_BINDER, result_)
    return result_
  }

  /* ********************************************************** */
  // item
  fun right_binder(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "right_binder")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.RIGHT_BINDER, "<right binder>")
    result_ = item(runtime_, level_ + 1)
    runtime_.register_hook_(RIGHT_BINDER, GREEDY_RIGHT_BINDER)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // left_binder right_binder both_binders
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.A)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = left_binder(runtime_, level_ + 1)
    result_ = result_ && right_binder(runtime_, level_ + 1)
    result_ = result_ && both_binders(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

}