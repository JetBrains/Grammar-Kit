// ---- PsiAccessors.kt -----------------
//header.txt
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedTypes
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class PsiAccessors {

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
      var result_: Boolean
      if (root_ == GeneratedTypes.EXPRESSION) {
        result_ = expression(builder_, level_ + 1)
      }
      else {
        result_ = root(builder_, level_ + 1)
      }
      return result_
    }

    /* ********************************************************** */
    // expression operator expression
    fun binary(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "binary")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      var pinned_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.BINARY, null)
      result_ = expression(builder_, level_ + 1)
      result_ = result_ && `operator_$`(builder_, level_ + 1)
      pinned_ = result_ // pin = operator
      result_ = result_ && expression(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, pinned_, null)
      return result_ || pinned_
    }

    /* ********************************************************** */
    // value '*' value
    fun expression(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expression")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = `value_$`(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, "*")
      result_ = result_ && `value_$`(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.EXPRESSION, result_)
      return result_
    }

    /* ********************************************************** */
    // '+' | '-'
    fun `operator_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`operator_$`")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.OPERATOR, "<operator>")
      result_ = consumeToken(builder_, "+")
      if (!result_) result_ = consumeToken(builder_, "-")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // binary
    internal fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return binary(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // id
    fun `value_$`(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "`value_$`")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      exit_section_(builder_, marker_, GeneratedTypes.VALUE, result_)
      return result_
    }

  }
}