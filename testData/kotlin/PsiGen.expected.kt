// ---- PsiGen.kt -----------------
//header.txt
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import com.intellij.platform.syntax.SyntaxElementTypeSet
import com.intellij.platform.syntax.syntaxElementTypeSetOf
import com.intellij.platform.syntax.SyntaxElementType

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object PsiGen {

  fun parse(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime) {
    var result_: Boolean
    runtime_.init(::parse, EXTENDS_SETS_)
    val marker_: Marker = runtime_.enter_section_(0, Modifiers._COLLAPSE_, null)
    result_ = parse_root_(root_, runtime_, 0)
    runtime_.exit_section_(0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  internal fun parse_root_(root_: SyntaxElementType, runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return grammar_root(runtime_, level_ + 1)
  }

  val EXTENDS_SETS_: Array<SyntaxElementTypeSet> = arrayOf(
    create_token_set_(GeneratedSyntaxElementTypes.ROOT, GeneratedSyntaxElementTypes.ROOT_B, GeneratedSyntaxElementTypes.ROOT_C, GeneratedSyntaxElementTypes.ROOT_D),
    create_token_set_(GeneratedSyntaxElementTypes.A_STATEMENT, GeneratedSyntaxElementTypes.B_STATEMENT, GeneratedSyntaxElementTypes.C_STATEMENT, GeneratedSyntaxElementTypes.STATEMENT),
    create_token_set_(GeneratedSyntaxElementTypes.CAST_EXPR, GeneratedSyntaxElementTypes.CHOICE_JOINED, GeneratedSyntaxElementTypes.EXPR, GeneratedSyntaxElementTypes.ID_EXPR,
      GeneratedSyntaxElementTypes.ITEM_EXPR, GeneratedSyntaxElementTypes.LITERAL, GeneratedSyntaxElementTypes.MISSING_EXTERNAL_TYPE, GeneratedSyntaxElementTypes.MUL_EXPR,
      GeneratedSyntaxElementTypes.PLUS_EXPR, GeneratedSyntaxElementTypes.REF_EXPR, GeneratedSyntaxElementTypes.SOME_EXPR, GeneratedSyntaxElementTypes.SPECIAL_REF),
  )

  /* ********************************************************** */
  // b_expr plus_expr *
  internal fun a_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "a_expr")) return false
    if (!runtime_.nextTokenIs("", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = b_expr(runtime_, level_ + 1)
    result_ = result_ && a_expr_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // plus_expr *
  private fun a_expr_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "a_expr_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!plus_expr(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("a_expr_1", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // id_expr mul_expr *
  internal fun b_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "b_expr")) return false
    if (!runtime_.nextTokenIs("", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = id_expr(runtime_, level_ + 1)
    result_ = result_ && b_expr_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // mul_expr *
  private fun b_expr_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "b_expr_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!mul_expr(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("b_expr_1", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // a_expr (',' a_expr) *
  fun expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "expr")) return false
    if (!runtime_.nextTokenIs("<expr>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.EXPR, "<expr>")
    result_ = a_expr(runtime_, level_ + 1)
    result_ = result_ && expr_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // (',' a_expr) *
  private fun expr_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "expr_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!expr_1_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("expr_1", pos_)) break
    }
    return true
  }

  // ',' a_expr
  private fun expr_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "expr_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(",")
    result_ = result_ && a_expr(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // id
  fun external_same_as_type2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "external_same_as_type2")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.ID_EXPR, result_)
    return result_
  }

  /* ********************************************************** */
  // number
  fun external_type(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "external_type")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.NUMBER)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.MISSING_EXTERNAL_TYPE, result_)
    return result_
  }

  /* ********************************************************** */
  // id
  fun external_type2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "external_type2")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.ID_EXPR, result_)
    return result_
  }

  /* ********************************************************** */
  // id
  fun external_type3(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "external_type3")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.EXPR, result_)
    return result_
  }

  /* ********************************************************** */
  // expr | external_type3
  fun grammar_element(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "grammar_element")) return false
    if (!runtime_.nextTokenIs("<grammar element>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.GRAMMAR_ELEMENT, "<grammar element>")
    result_ = expr(runtime_, level_ + 1)
    if (!result_) result_ = external_type3(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // root
  internal fun grammar_root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return root(runtime_, level_ + 1)
  }

  /* ********************************************************** */
  // specialRef | reference | literal | external_type | external_type2
  internal fun id_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "id_expr")) return false
    if (!runtime_.nextTokenIs("", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    result_ = PsiGen2.specialRef(runtime_, level_ + 1)
    if (!result_) result_ = PsiGen2.reference(runtime_, level_ + 1)
    if (!result_) result_ = PsiGen2.literal(runtime_, level_ + 1)
    if (!result_) result_ = external_type(runtime_, level_ + 1)
    if (!result_) result_ = external_type2(runtime_, level_ + 1)
    return result_
  }

  /* ********************************************************** */
  // id number
  fun include__section__alt(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "include__section__alt")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.INCLUDE__SECTION__ALT, "<include section alt>")
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)
    runtime_.exit_section_(level_, marker_, result_, false, PsiGen::include_section_recover_)
    return result_
  }

  /* ********************************************************** */
  // id number include-section <include (section) alt>
  fun include_section(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "include_section")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.INCLUDE_SECTION, "<include section>")
    result_ = runtime_.consumeTokens(0, GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)
    result_ = result_ && include_section(runtime_, level_ + 1)
    result_ = result_ && include__section__alt(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, PsiGen::include_section_recover_)
    return result_
  }

  /* ********************************************************** */
  // !()
  internal fun include_section_recover_(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "include_section_recover_")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !include_section_recover__0(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // ()
  private fun include_section_recover__0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return true
  }

  /* ********************************************************** */
  // <<p>> +
  internal fun listOf(runtime_: SyntaxGeneratedParserRuntime, level_: Int, p: Parser): Boolean {
    if (!runtime_.recursion_guard_(level_, "listOf")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = p.parse(runtime_, level_)
    while (result_) {
      val pos_: Int = runtime_.current_position_()
      if (!p.parse(runtime_, level_)) break
      if (!runtime_.empty_element_parsed_guard_("listOf", pos_)) break
    }
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // '*' expr
  fun mul_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "mul_expr")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.OP_MUL)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.MUL_EXPR, null)
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.OP_MUL)
    result_ = result_ && expr(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // '+' expr
  fun plus_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "plus_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.PLUS_EXPR, "<plus expr>")
    result_ = runtime_.consumeToken("+")
    result_ = result_ && expr(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // root_a | root_b | root_c | root_d
  fun root(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.ROOT, "<root>")
    result_ = PsiGenUtil.parseGrammar(runtime_, level_ + 1, PsiGen::grammar_element)
    if (!result_) result_ = root_b(runtime_, level_ + 1)
    if (!result_) result_ = root_c(runtime_, level_ + 1)
    if (!result_) result_ = root_d(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // <<parseGrammar grammar_element>>
  fun root_b(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root_b")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.ROOT_B, "<root b>")
    result_ = PsiGenUtil.parseGrammar(runtime_, level_ + 1, PsiGen::grammar_element)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // <<blockOf grammar_element>>
  fun root_c(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root_c")) return false
    if (!runtime_.nextTokenIs("<root c>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ROOT_C, "<root c>")
    result_ = PsiGen2.blockOf(runtime_, level_ + 1, PsiGen::grammar_element)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // <<listOf grammar_element>>
  fun root_d(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "root_d")) return false
    if (!runtime_.nextTokenIs("<root d>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.ROOT_D, "<root d>")
    result_ = listOf(runtime_, level_ + 1, PsiGen::grammar_element)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

}
// ---- PsiGen2.kt -----------------
//header.txt
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object PsiGen2 {

  /* ********************************************************** */
  // <<p>> +
  fun blockOf(runtime_: SyntaxGeneratedParserRuntime, level_: Int, p: Parser): Boolean {
    if (!runtime_.recursion_guard_(level_, "blockOf")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = p.parse(runtime_, level_)
    while (result_) {
      val pos_: Int = runtime_.current_position_()
      if (!p.parse(runtime_, level_)) break
      if (!runtime_.empty_element_parsed_guard_("blockOf", pos_)) break
    }
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.BLOCK_OF, result_)
    return result_
  }

  /* ********************************************************** */
  // '::' id
  fun cast_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "cast_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.CAST_EXPR, "<cast expr>")
    result_ = runtime_.consumeToken("::")
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // id
  fun identifier(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "identifier")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.IDENTIFIER, result_)
    return result_
  }

  /* ********************************************************** */
  // '[' number ']'
  fun item_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "item_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.ITEM_EXPR, "<item expr>")
    result_ = runtime_.consumeToken("[")
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.NUMBER)
    result_ = result_ && runtime_.consumeToken("]")
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // number
  fun literal(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "literal")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.NUMBER)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.LITERAL, result_)
    return result_
  }

  /* ********************************************************** */
  // '.' identifier
  fun qref_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "qref_expr")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.REF_EXPR, "<qref expr>")
    result_ = runtime_.consumeToken(".")
    result_ = result_ && identifier(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // identifier
  fun ref_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "ref_expr")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = identifier(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.REF_EXPR, result_)
    return result_
  }

  /* ********************************************************** */
  // ref_expr qref_expr *
  internal fun reference(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "reference")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = ref_expr(runtime_, level_ + 1)
    result_ = result_ && reference_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // qref_expr *
  private fun reference_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "reference_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!qref_expr(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("reference_1", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // (a_expr | specialRef b_expr | some_expr_private) (cast_expr) (item_expr) *
  fun some_expr(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr")) return false
    if (!runtime_.nextTokenIs("<some expr>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.SOME_EXPR, "<some expr>")
    result_ = some_expr_0(runtime_, level_ + 1)
    result_ = result_ && some_expr_1(runtime_, level_ + 1)
    result_ = result_ && some_expr_2(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // a_expr | specialRef b_expr | some_expr_private
  private fun some_expr_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = a_expr(runtime_, level_ + 1)
    if (!result_) result_ = some_expr_0_1(runtime_, level_ + 1)
    if (!result_) result_ = some_expr_private(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // specialRef b_expr
  private fun some_expr_0_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr_0_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = specialRef(runtime_, level_ + 1)
    result_ = result_ && b_expr(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // (cast_expr)
  private fun some_expr_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = cast_expr(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // (item_expr) *
  private fun some_expr_2(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr_2")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!some_expr_2_0(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("some_expr_2", pos_)) break
    }
    return true
  }

  // (item_expr)
  private fun some_expr_2_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr_2_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = item_expr(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // specialRef b_expr
  internal fun some_expr_private(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "some_expr_private")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = specialRef(runtime_, level_ + 1)
    result_ = result_ && b_expr(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // identifier OF reference
  fun specialRef(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "specialRef")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = identifier(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.OF)
    result_ = result_ && reference(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.SPECIAL_REF, result_)
    return result_
  }

}
// ---- PsiGenFixes.kt -----------------
//header.txt
import generated.GeneratedSyntaxElementTypes
import com.intellij.platform.syntax.util.runtime.*
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
object PsiGenFixes {

  /* ********************************************************** */
  // ',' identifier
  fun LeftShadow(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "LeftShadow")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._LEFT_, GeneratedSyntaxElementTypes.LEFT_SHADOW, "<left shadow>")
    result_ = runtime_.consumeToken(",")
    result_ = result_ && PsiGen2.identifier(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // identifier LeftShadow *
  fun LeftShadowTest(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "LeftShadowTest")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = PsiGen2.identifier(runtime_, level_ + 1)
    result_ = result_ && LeftShadowTest_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, GeneratedSyntaxElementTypes.LEFT_SHADOW_TEST, result_)
    return result_
  }

  // LeftShadow *
  private fun LeftShadowTest_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "LeftShadowTest_1")) return false
    while (true) {
      val pos_: Int = runtime_.current_position_()
      if (!LeftShadow(runtime_, level_ + 1)) break
      if (!runtime_.empty_element_parsed_guard_("LeftShadowTest_1", pos_)) break
    }
    return true
  }

  /* ********************************************************** */
  // id | number
  fun a_statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "a_statement")) return false
    if (!runtime_.nextTokenIs("<a statement>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.A_STATEMENT, "<a statement>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.NUMBER)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // id | number
  fun b_statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "b_statement")) return false
    if (!runtime_.nextTokenIs("<b statement>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.B_STATEMENT, "<b statement>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.NUMBER)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // id | number
  fun c_statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "c_statement")) return false
    if (!runtime_.nextTokenIs("<c statement>", GeneratedSyntaxElementTypes.ID, GeneratedSyntaxElementTypes.NUMBER)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.C_STATEMENT, "<c statement>")
    result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    if (!result_) result_ = runtime_.consumeToken(GeneratedSyntaxElementTypes.NUMBER)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  /* ********************************************************** */
  // literal id '%' | '%' id literal
  fun choice_joined(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "choice_joined")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NONE_, GeneratedSyntaxElementTypes.CHOICE_JOINED, "<choice joined>")
    result_ = choice_joined_0(runtime_, level_ + 1)
    if (!result_) result_ = choice_joined_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // literal id '%'
  private fun choice_joined_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "choice_joined_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = PsiGen2.literal(runtime_, level_ + 1)
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    result_ = result_ && runtime_.consumeToken("%")
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // '%' id literal
  private fun choice_joined_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "choice_joined_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = runtime_.consumeToken("%")
    result_ = result_ && runtime_.consumeToken(GeneratedSyntaxElementTypes.ID)
    result_ = result_ && PsiGen2.literal(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // <<blockOf identifier>>
  internal fun fixMetaRule(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    return PsiGen2.blockOf(runtime_, level_ + 1, PsiGen2::identifier)
  }

  /* ********************************************************** */
  // identifier
  fun publicMethodToCall(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "publicMethodToCall")) return false
    if (!runtime_.nextTokenIs(GeneratedSyntaxElementTypes.ID)) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = PsiGen2.identifier(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  /* ********************************************************** */
  // &<<external>> (a_statement | b_statement) | !<<external>> (c_statement)
  fun statement(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._COLLAPSE_, GeneratedSyntaxElementTypes.STATEMENT, "<statement>")
    result_ = statement_0(runtime_, level_ + 1)
    if (!result_) result_ = statement_1(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // &<<external>> (a_statement | b_statement)
  private fun statement_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = statement_0_0(runtime_, level_ + 1)
    result_ = result_ && statement_0_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // &<<external>>
  private fun statement_0_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement_0_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._AND_)
    result_ = PsiGenUtil.external(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // a_statement | b_statement
  private fun statement_0_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement_0_1")) return false
    var result_: Boolean
    result_ = a_statement(runtime_, level_ + 1)
    if (!result_) result_ = b_statement(runtime_, level_ + 1)
    return result_
  }

  // !<<external>> (c_statement)
  private fun statement_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = statement_1_0(runtime_, level_ + 1)
    result_ = result_ && statement_1_1(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

  // !<<external>>
  private fun statement_1_0(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement_1_0")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_(level_, Modifiers._NOT_)
    result_ = !PsiGenUtil.external(runtime_, level_ + 1)
    runtime_.exit_section_(level_, marker_, result_, false, null)
    return result_
  }

  // (c_statement)
  private fun statement_1_1(runtime_: SyntaxGeneratedParserRuntime, level_: Int): Boolean {
    if (!runtime_.recursion_guard_(level_, "statement_1_1")) return false
    var result_: Boolean
    val marker_: Marker = runtime_.enter_section_()
    result_ = c_statement(runtime_, level_ + 1)
    runtime_.exit_section_(marker_, null, result_)
    return result_
  }

}