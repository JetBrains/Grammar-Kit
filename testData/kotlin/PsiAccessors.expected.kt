// ---- PsiAccessors.kt -----------------
//header.txt
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import generated.GeneratedTypes.*
import PsiGenUtil.*
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiParser
import com.intellij.lang.LightPsiParser

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class PsiAccessors: PsiParser, LightPsiParser {

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
      boolean result_
      if (root_ == EXPRESSION) {
        result_ = expression(builder_, level_ + 1)
      }
      else {
        result_ = root(builder_, level_ + 1)
      }
      return result_
    }

    /* ********************************************************** */
    // expression operator expression
    fun binary(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "binary")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, BINARY, null)
      result_ = expression(builder_, level_ + 1)
      result_ = result_ && `operator_$`(builder_, level_ + 1)
      pinned_ = result_ // pin = operator
      result_ = result_ && expression(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // value '*' value
    fun expression(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expression")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = `value_$`(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, "*")
      result_ = result_ && `value_$`(builder_, level_ + 1)
      exit_section_(builder_, marker_, EXPRESSION, result_)
      return result_
    }

    /* ********************************************************** */
    // '+' | '-'
    fun `operator_$`(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`operator_$`")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, OPERATOR, "<operator>")
      result_ = consumeToken(builder_, "+")
      if (!result_) result_ = consumeToken(builder_, "-")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // binary
    internal fun root(builder_: PsiBuilder, level_: Int): Boolean {
      return binary(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // id
    fun `value_$`(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`value_$`")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ID)
      exit_section_(builder_, marker_, VALUE, result_)
      return result_
    }

  }
}