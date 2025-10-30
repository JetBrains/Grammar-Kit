// ---- PsiAccessors.kt -----------------
//header.txt
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object PsiAccessors {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, null)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    var result_: Boolean
    if (root_ == GeneratedSyntaxElementTypes.EXPRESSION) {
      result_ = expression(runtime_, level_ + 1)
    }
    else {
      result_ = root(runtime_, level_ + 1)
    }
    return result_
  }

  /* ********************************************************** */
  // expression operator expression
  fun binary(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "binary")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    var pinned_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.BINARY, null)
    result_ = expression(runtime_, level_ + 1)
    result_ = result_ && operator__(runtime_, level_ + 1)
    pinned_ = result_ // pin = operator
    result_ = result_ && expression(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, pinned_, null)
    return result_ || pinned_
  }

  /* ********************************************************** */
  // value '*' value
  fun expression(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "expression")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = value__(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken("*")
    result_ = result_ && value__(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.EXPRESSION, result_)
    return result_
  }

  /* ********************************************************** */
  // '+' | '-'
  fun operator__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "operator__")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.OPERATOR, "<operator>")
    result_ = runtime_.consumeToken("+")
    if (!result_) result_ = runtime_.consumeToken("-")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // binary
  internal fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return binary(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // id
  fun value__(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "value__")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.VALUE, result_)
    return result_
  }

}