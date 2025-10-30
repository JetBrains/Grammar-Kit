// ---- LeftAssociative.kt -----------------
// This is a generated file. Not intended for manual editing.
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object LeftAssociative {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, null)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return from(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // AS? id
  fun alias_definition(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "alias_definition")) return false
    if (!runtime_.nextTokenIs("<alias definition>", GeneratedSyntaxElementTypes.AS, GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.ALIAS_DEFINITION, "<alias definition>")
    result_ = alias_definition_0(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // AS?
  private fun alias_definition_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "alias_definition_0")) return false
    runtime_.consumeToken(GeneratedSyntaxElementTypes.AS)
    return true
  }

  /* ********************************************************** */
  // AS? id
  fun alias_definition2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "alias_definition2")) return false
    if (!runtime_.nextTokenIs("<alias definition 2>", GeneratedSyntaxElementTypes.AS, GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.ALIAS_DEFINITION_2, "<alias definition 2>")
    result_ = alias_definition2_0(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // AS?
  private fun alias_definition2_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "alias_definition2_0")) return false
    runtime_.consumeToken(GeneratedSyntaxElementTypes.AS)
    return true
  }

  /* ********************************************************** */
  // reference alias_definition? alias_definition2? leech? leech2?
  internal fun from(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "from")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.REFERENCE)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.REFERENCE)
    result_ = result_ && from_1(runtime_, level_ + 1)
    result_ = result_ && from_2(runtime_, level_ + 1)
    result_ = result_ && from_3(runtime_, level_ + 1)
    result_ = result_ && from_4(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // alias_definition?
  private fun from_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "from_1")) return false
    alias_definition(runtime_, level_ + 1)
    return true
  }

  // alias_definition2?
  private fun from_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "from_2")) return false
    alias_definition2(runtime_, level_ + 1)
    return true
  }

  // leech?
  private fun from_3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "from_3")) return false
    leech(runtime_, level_ + 1)
    return true
  }

  // leech2?
  private fun from_4(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "from_4")) return false
    leech2(runtime_, level_ + 1)
    return true
  }

  /* ********************************************************** */
  // id
  fun leech(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "leech")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_INNER_, GeneratedSyntaxElementTypes.LEECH, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // id
  internal fun leech2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "leech2")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_INNER_)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // id?
  internal fun leech3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "leech3")) return false
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_INNER_)
    runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(level_, marker_, true, false, null)
    return true
  }

}