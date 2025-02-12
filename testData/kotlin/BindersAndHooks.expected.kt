// ---- BindersAndHooks.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import generated.GeneratedTypes.*
import org.intellij.grammar.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiParser
import com.intellij.lang.LightPsiParser
import com.intellij.lang.WhitespacesBinders.*
import com.sample.MyHooks.*

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class BindersAndHooks: PsiParser, LightPsiParser {

  override fun parse(root_: IElementType, builder_: PsiBuilder): ASTNode {
    parseLight(root_, builder_)
    return builder_.getTreeBuilt()
  }

  override fun parseLight(root_: IElementType, builder_: PsiBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, null)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: IElementType, builder_: PsiBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: IElementType, builder_: PsiBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // A item B
    fun both_binders(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "both_binders")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, BOTH_BINDERS, null)
      result_ = consumeToken(builder_, A)
      pinned_ = result_ // pin = 1
      result_ = result_ && report_error_(builder_, item(builder_, level_ + 1))
      result_ = pinned_ && consumeToken(builder_, B) && result_
      register_hook_(builder_, WS_BINDERS, GREEDY_LEFT_BINDER, GREEDY_RIGHT_BINDER)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // A
    fun got_hook(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "got_hook")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, A)
      register_hook_(builder_, MY_HOOK, "my", "hook", "param", "array")
      exit_section_(builder_, marker_, GOT_HOOK, result_)
      return result_
    }

    /* ********************************************************** */
    internal fun item(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // A B
    fun left_binder(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "left_binder")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, A, B)
      register_hook_(builder_, LEFT_BINDER, GREEDY_LEFT_BINDER)
      exit_section_(builder_, marker_, LEFT_BINDER, result_)
      return result_
    }

    /* ********************************************************** */
    // item
    fun right_binder(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "right_binder")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, RIGHT_BINDER, "<right binder>")
      result_ = item(builder_, level_ + 1)
      register_hook_(builder_, RIGHT_BINDER, GREEDY_RIGHT_BINDER)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // left_binder right_binder both_binders
    internal fun root(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root")) return false
      if (!nextTokenIs(builder_, A)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = left_binder(builder_, level_ + 1)
      result_ = result_ && right_binder(builder_, level_ + 1)
      result_ = result_ && both_binders(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

  }
}