// ---- PsiGen.kt -----------------
//header.txt
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedTypes
import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.util.SyntaxGeneratedParserRuntimeBase

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class PsiGen(protected val runtime_: SyntaxGeneratedParserRuntimeBase) {

  fun parse(root_: SyntaxElementType, builder_: SyntaxTreeBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: SyntaxElementType, builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return grammar_root(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<Set<SyntaxElementType>> = arrayOf(
      create_token_set_(GeneratedTypes.ROOT, GeneratedTypes.ROOT_B, GeneratedTypes.ROOT_C, GeneratedTypes.ROOT_D),
      create_token_set_(GeneratedTypes.A_STATEMENT, GeneratedTypes.B_STATEMENT, GeneratedTypes.C_STATEMENT, GeneratedTypes.STATEMENT),
      create_token_set_(GeneratedTypes.CAST_EXPR, GeneratedTypes.CHOICE_JOINED, GeneratedTypes.EXPR, GeneratedTypes.ID_EXPR,
        GeneratedTypes.ITEM_EXPR, GeneratedTypes.LITERAL, GeneratedTypes.MISSING_EXTERNAL_TYPE, GeneratedTypes.MUL_EXPR,
        GeneratedTypes.PLUS_EXPR, GeneratedTypes.REF_EXPR, GeneratedTypes.SOME_EXPR, GeneratedTypes.SPECIAL_REF),
    )

    /* ********************************************************** */
    // b_expr plus_expr *
    internal fun a_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "a_expr")) return false
      if (!nextTokenIs(builder_, "", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = b_expr(builder_, level_ + 1)
      result_ = result_ && a_expr_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // plus_expr *
    private fun a_expr_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "a_expr_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!plus_expr(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "a_expr_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // id_expr mul_expr *
    internal fun b_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "b_expr")) return false
      if (!nextTokenIs(builder_, "", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = id_expr(builder_, level_ + 1)
      result_ = result_ && b_expr_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // mul_expr *
    private fun b_expr_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "b_expr_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!mul_expr(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "b_expr_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // a_expr (',' a_expr) *
    fun expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr")) return false
      if (!nextTokenIs(builder_, "<expr>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.EXPR, "<expr>")
      result_ = a_expr(builder_, level_ + 1)
      result_ = result_ && expr_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' a_expr) *
    private fun expr_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!expr_1_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "expr_1", pos_)) break
      }
      return true
    }

    // ',' a_expr
    private fun expr_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ",")
      result_ = result_ && a_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // id
    fun external_same_as_type2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_same_as_type2")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      exit_section_(builder_, marker_, GeneratedTypes.ID_EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // number
    fun external_type(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_type")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.NUMBER)
      exit_section_(builder_, marker_, GeneratedTypes.MISSING_EXTERNAL_TYPE, result_)
      return result_
    }

    /* ********************************************************** */
    // id
    fun external_type2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_type2")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      exit_section_(builder_, marker_, GeneratedTypes.ID_EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // id
    fun external_type3(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_type3")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      exit_section_(builder_, marker_, GeneratedTypes.EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // expr | external_type3
    fun grammar_element(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "grammar_element")) return false
      if (!nextTokenIs(builder_, "<grammar element>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.GRAMMAR_ELEMENT, "<grammar element>")
      result_ = expr(builder_, level_ + 1)
      if (!result_) result_ = external_type3(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // root
    internal fun grammar_root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // specialRef | reference | literal | external_type | external_type2
    internal fun id_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "id_expr")) return false
      if (!nextTokenIs(builder_, "", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      result_ = PsiGen2.specialRef(builder_, level_ + 1)
      if (!result_) result_ = PsiGen2.reference(builder_, level_ + 1)
      if (!result_) result_ = PsiGen2.literal(builder_, level_ + 1)
      if (!result_) result_ = external_type(builder_, level_ + 1)
      if (!result_) result_ = external_type2(builder_, level_ + 1)
      return result_
    }

    /* ********************************************************** */
    // id number
    fun include__section__alt(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "include__section__alt")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.INCLUDE__SECTION__ALT, "<include section alt>")
      result_ = consumeTokens(builder_, 0, GeneratedTypes.ID, GeneratedTypes.NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, PsiGen::include_section_recover_)
      return result_
    }

    /* ********************************************************** */
    // id number include-section <include (section) alt>
    fun include_section(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "include_section")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.INCLUDE_SECTION, "<include section>")
      result_ = consumeTokens(builder_, 0, GeneratedTypes.ID, GeneratedTypes.NUMBER)
      result_ = result_ && include_section(builder_, level_ + 1)
      result_ = result_ && include__section__alt(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, PsiGen::include_section_recover_)
      return result_
    }

    /* ********************************************************** */
    // !()
    internal fun include_section_recover_(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "include_section_recover_")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !include_section_recover__0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ()
    private fun include_section_recover__0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // <<p>> +
    internal fun listOf(builder_: SyntaxTreeBuilder, level_: Int, p: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "listOf")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = p.parse(builder_, level_)
      while (result_) {
        val pos_: Int = current_position_(builder_)
        if (!p.parse(builder_, level_)) break
        if (!empty_element_parsed_guard_(builder_, "listOf", pos_)) break
      }
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // '*' expr
    fun mul_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "mul_expr")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.OP_MUL)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.MUL_EXPR, null)
      result_ = consumeToken(builder_, GeneratedTypes.OP_MUL)
      result_ = result_ && expr(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // '+' expr
    fun plus_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "plus_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.PLUS_EXPR, "<plus expr>")
      result_ = consumeToken(builder_, "+")
      result_ = result_ && expr(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // root_a | root_b | root_c | root_d
    fun root(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.ROOT, "<root>")
      result_ = parseGrammar(builder_, level_ + 1, PsiGen::grammar_element)
      if (!result_) result_ = root_b(builder_, level_ + 1)
      if (!result_) result_ = root_c(builder_, level_ + 1)
      if (!result_) result_ = root_d(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // <<parseGrammar grammar_element>>
    fun root_b(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root_b")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.ROOT_B, "<root b>")
      result_ = parseGrammar(builder_, level_ + 1, PsiGen::grammar_element)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // <<blockOf grammar_element>>
    fun root_c(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root_c")) return false
      if (!nextTokenIs(builder_, "<root c>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.ROOT_C, "<root c>")
      result_ = PsiGen2.blockOf(builder_, level_ + 1, PsiGen::grammar_element)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // <<listOf grammar_element>>
    fun root_d(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root_d")) return false
      if (!nextTokenIs(builder_, "<root d>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.ROOT_D, "<root d>")
      result_ = listOf(builder_, level_ + 1, PsiGen::grammar_element)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}
// ---- PsiGen2.kt -----------------
//header.txt
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedTypes

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class PsiGen2 {

  companion object {
    /* ********************************************************** */
    // <<p>> +
    fun blockOf(builder_: SyntaxTreeBuilder, level_: Int, p: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "blockOf")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = p.parse(builder_, level_)
      while (result_) {
        val pos_: Int = current_position_(builder_)
        if (!p.parse(builder_, level_)) break
        if (!empty_element_parsed_guard_(builder_, "blockOf", pos_)) break
      }
      exit_section_(builder_, marker_, GeneratedTypes.BLOCK_OF, result_)
      return result_
    }

    /* ********************************************************** */
    // '::' id
    fun cast_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "cast_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.CAST_EXPR, "<cast expr>")
      result_ = consumeToken(builder_, "::")
      result_ = result_ && consumeToken(builder_, GeneratedTypes.ID)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id
    fun identifier(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "identifier")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      exit_section_(builder_, marker_, GeneratedTypes.IDENTIFIER, result_)
      return result_
    }

    /* ********************************************************** */
    // '[' number ']'
    fun item_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "item_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.ITEM_EXPR, "<item expr>")
      result_ = consumeToken(builder_, "[")
      result_ = result_ && consumeToken(builder_, GeneratedTypes.NUMBER)
      result_ = result_ && consumeToken(builder_, "]")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // number
    fun literal(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "literal")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, GeneratedTypes.NUMBER)
      exit_section_(builder_, marker_, GeneratedTypes.LITERAL, result_)
      return result_
    }

    /* ********************************************************** */
    // '.' identifier
    fun qref_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "qref_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.REF_EXPR, "<qref expr>")
      result_ = consumeToken(builder_, ".")
      result_ = result_ && identifier(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // identifier
    fun ref_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "ref_expr")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = identifier(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.REF_EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // ref_expr qref_expr *
    internal fun reference(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "reference")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = ref_expr(builder_, level_ + 1)
      result_ = result_ && reference_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // qref_expr *
    private fun reference_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "reference_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!qref_expr(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "reference_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // (a_expr | specialRef b_expr | some_expr_private) (cast_expr) (item_expr) *
    fun some_expr(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr")) return false
      if (!nextTokenIs(builder_, "<some expr>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.SOME_EXPR, "<some expr>")
      result_ = some_expr_0(builder_, level_ + 1)
      result_ = result_ && some_expr_1(builder_, level_ + 1)
      result_ = result_ && some_expr_2(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // a_expr | specialRef b_expr | some_expr_private
    private fun some_expr_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = a_expr(builder_, level_ + 1)
      if (!result_) result_ = some_expr_0_1(builder_, level_ + 1)
      if (!result_) result_ = some_expr_private(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // specialRef b_expr
    private fun some_expr_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_0_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = specialRef(builder_, level_ + 1)
      result_ = result_ && b_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // (cast_expr)
    private fun some_expr_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = cast_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // (item_expr) *
    private fun some_expr_2(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!some_expr_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "some_expr_2", pos_)) break
      }
      return true
    }

    // (item_expr)
    private fun some_expr_2_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_2_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = item_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // specialRef b_expr
    internal fun some_expr_private(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_private")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = specialRef(builder_, level_ + 1)
      result_ = result_ && b_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // identifier OF reference
    fun specialRef(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "specialRef")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = identifier(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedTypes.OF)
      result_ = result_ && reference(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.SPECIAL_REF, result_)
      return result_
    }

  }
}
// ---- PsiGenFixes.kt -----------------
//header.txt
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker
import generated.GeneratedTypes

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class PsiGenFixes {

  companion object {
    /* ********************************************************** */
    // ',' identifier
    fun LeftShadow(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "LeftShadow")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, GeneratedTypes.LEFT_SHADOW, "<left shadow>")
      result_ = consumeToken(builder_, ",")
      result_ = result_ && PsiGen2.identifier(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // identifier LeftShadow *
    fun LeftShadowTest(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "LeftShadowTest")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = PsiGen2.identifier(builder_, level_ + 1)
      result_ = result_ && LeftShadowTest_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, GeneratedTypes.LEFT_SHADOW_TEST, result_)
      return result_
    }

    // LeftShadow *
    private fun LeftShadowTest_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "LeftShadowTest_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!LeftShadow(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "LeftShadowTest_1", pos_)) break
      }
      return true
    }

    /* ********************************************************** */
    // id | number
    fun a_statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "a_statement")) return false
      if (!nextTokenIs(builder_, "<a statement>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.A_STATEMENT, "<a statement>")
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id | number
    fun b_statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "b_statement")) return false
      if (!nextTokenIs(builder_, "<b statement>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.B_STATEMENT, "<b statement>")
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id | number
    fun c_statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "c_statement")) return false
      if (!nextTokenIs(builder_, "<c statement>", GeneratedTypes.ID, GeneratedTypes.NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.C_STATEMENT, "<c statement>")
      result_ = consumeToken(builder_, GeneratedTypes.ID)
      if (!result_) result_ = consumeToken(builder_, GeneratedTypes.NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // literal id '%' | '%' id literal
    fun choice_joined(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "choice_joined")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GeneratedTypes.CHOICE_JOINED, "<choice joined>")
      result_ = choice_joined_0(builder_, level_ + 1)
      if (!result_) result_ = choice_joined_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // literal id '%'
    private fun choice_joined_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "choice_joined_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = PsiGen2.literal(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, GeneratedTypes.ID)
      result_ = result_ && consumeToken(builder_, "%")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // '%' id literal
    private fun choice_joined_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "choice_joined_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "%")
      result_ = result_ && consumeToken(builder_, GeneratedTypes.ID)
      result_ = result_ && PsiGen2.literal(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // <<blockOf identifier>>
    internal fun fixMetaRule(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      return PsiGen2.blockOf(builder_, level_ + 1, PsiGen2::identifier)
    }

    /* ********************************************************** */
    // identifier
    fun publicMethodToCall(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "publicMethodToCall")) return false
      if (!nextTokenIs(builder_, GeneratedTypes.ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = PsiGen2.identifier(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // &<<external>> (a_statement | b_statement) | !<<external>> (c_statement)
    fun statement(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, GeneratedTypes.STATEMENT, "<statement>")
      result_ = statement_0(builder_, level_ + 1)
      if (!result_) result_ = statement_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // &<<external>> (a_statement | b_statement)
    private fun statement_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = statement_0_0(builder_, level_ + 1)
      result_ = result_ && statement_0_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &<<external>>
    private fun statement_0_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = external(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // a_statement | b_statement
    private fun statement_0_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_0_1")) return false
      var result_: Boolean
      result_ = a_statement(builder_, level_ + 1)
      if (!result_) result_ = b_statement(builder_, level_ + 1)
      return result_
    }

    // !<<external>> (c_statement)
    private fun statement_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = statement_1_0(builder_, level_ + 1)
      result_ = result_ && statement_1_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // !<<external>>
    private fun statement_1_0(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !external(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (c_statement)
    private fun statement_1_1(builder_: SyntaxTreeBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = c_statement(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

  }
}