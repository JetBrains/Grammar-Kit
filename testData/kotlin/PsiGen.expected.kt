// ---- PsiGen.kt -----------------
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
open class PsiGen: PsiParser, LightPsiParser {

  override fun parse(root_: IElementType, builder_: PsiBuilder): ASTNode {
    parseLight(root_, builder_)
    return builder_.getTreeBuilt()
  }

  override fun parseLight(root_: IElementType, builder_: PsiBuilder) {
    var result_: Boolean
    val builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_)
    val marker_: Marker = enter_section_(builder_, 0, _COLLAPSE_, null)
    result_ = parse_root_(root_, builder_)
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION)
  }

  protected fun parse_root_(root_: IElementType, builder_: PsiBuilder): Boolean {
    return parse_root_(root_, builder_, 0)
  }

  companion object {
    internal fun parse_root_(root_: IElementType, builder_: PsiBuilder, level_: Int): Boolean {
      return grammar_root(builder_, level_ + 1)
    }

    val EXTENDS_SETS_: Array<TokenSet> = arrayOf(
      create_token_set_(ROOT, ROOT_B, ROOT_C, ROOT_D),
      create_token_set_(A_STATEMENT, B_STATEMENT, C_STATEMENT, STATEMENT),
      create_token_set_(CAST_EXPR, CHOICE_JOINED, EXPR, ID_EXPR,
        ITEM_EXPR, LITERAL, MISSING_EXTERNAL_TYPE, MUL_EXPR,
        PLUS_EXPR, REF_EXPR, SOME_EXPR, SPECIAL_REF),
    )

    /* ********************************************************** */
    // b_expr plus_expr *
    internal fun a_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "a_expr")) return false
      if (!nextTokenIs(builder_, "", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = b_expr(builder_, level_ + 1)
      result_ = result_ && a_expr_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // plus_expr *
    private fun a_expr_1(builder_: PsiBuilder, level_: Int): Boolean {
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
    internal fun b_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "b_expr")) return false
      if (!nextTokenIs(builder_, "", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = id_expr(builder_, level_ + 1)
      result_ = result_ && b_expr_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // mul_expr *
    private fun b_expr_1(builder_: PsiBuilder, level_: Int): Boolean {
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
    fun expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr")) return false
      if (!nextTokenIs(builder_, "<expr>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, EXPR, "<expr>")
      result_ = a_expr(builder_, level_ + 1)
      result_ = result_ && expr_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (',' a_expr) *
    private fun expr_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "expr_1")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!expr_1_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "expr_1", pos_)) break
      }
      return true
    }

    // ',' a_expr
    private fun expr_1_0(builder_: PsiBuilder, level_: Int): Boolean {
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
    fun external_same_as_type2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_same_as_type2")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ID)
      exit_section_(builder_, marker_, ID_EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // number
    fun external_type(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_type")) return false
      if (!nextTokenIs(builder_, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, NUMBER)
      exit_section_(builder_, marker_, MISSING_EXTERNAL_TYPE, result_)
      return result_
    }

    /* ********************************************************** */
    // id
    fun external_type2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_type2")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ID)
      exit_section_(builder_, marker_, ID_EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // id
    fun external_type3(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "external_type3")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ID)
      exit_section_(builder_, marker_, EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // expr | external_type3
    fun grammar_element(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "grammar_element")) return false
      if (!nextTokenIs(builder_, "<grammar element>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, GRAMMAR_ELEMENT, "<grammar element>")
      result_ = expr(builder_, level_ + 1)
      if (!result_) result_ = external_type3(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // root
    internal fun grammar_root(builder_: PsiBuilder, level_: Int): Boolean {
      return root(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // specialRef | reference | literal | external_type | external_type2
    internal fun id_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "id_expr")) return false
      if (!nextTokenIs(builder_, "", ID, NUMBER)) return false
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
    fun include__section__alt(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "include__section__alt")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, INCLUDE__SECTION__ALT, "<include section alt>")
      result_ = consumeTokens(builder_, 0, ID, NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, PsiGen::include_section_recover_)
      return result_
    }

    /* ********************************************************** */
    // id number include-section <include (section) alt>
    fun include_section(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "include_section")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, INCLUDE_SECTION, "<include section>")
      result_ = consumeTokens(builder_, 0, ID, NUMBER)
      result_ = result_ && include_section(builder_, level_ + 1)
      result_ = result_ && include__section__alt(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, PsiGen::include_section_recover_)
      return result_
    }

    /* ********************************************************** */
    // !()
    internal fun include_section_recover_(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "include_section_recover_")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !include_section_recover__0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ()
    private fun include_section_recover__0(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // <<p>> +
    internal fun listOf(builder_: PsiBuilder, level_: Int, p: Parser): Boolean {
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
    fun mul_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "mul_expr")) return false
      if (!nextTokenIs(builder_, OP_MUL)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, MUL_EXPR, null)
      result_ = consumeToken(builder_, OP_MUL)
      result_ = result_ && expr(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // '+' expr
    fun plus_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "plus_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, PLUS_EXPR, "<plus expr>")
      result_ = consumeToken(builder_, "+")
      result_ = result_ && expr(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // root_a | root_b | root_c | root_d
    fun root(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, ROOT, "<root>")
      result_ = parseGrammar(builder_, level_ + 1, PsiGen::grammar_element)
      if (!result_) result_ = root_b(builder_, level_ + 1)
      if (!result_) result_ = root_c(builder_, level_ + 1)
      if (!result_) result_ = root_d(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // <<parseGrammar grammar_element>>
    fun root_b(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root_b")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, ROOT_B, "<root b>")
      result_ = parseGrammar(builder_, level_ + 1, PsiGen::grammar_element)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // <<blockOf grammar_element>>
    fun root_c(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root_c")) return false
      if (!nextTokenIs(builder_, "<root c>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, ROOT_C, "<root c>")
      result_ = PsiGen2.blockOf(builder_, level_ + 1, PsiGen::grammar_element)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // <<listOf grammar_element>>
    fun root_d(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "root_d")) return false
      if (!nextTokenIs(builder_, "<root d>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, ROOT_D, "<root d>")
      result_ = listOf(builder_, level_ + 1, PsiGen::grammar_element)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

  }
}
// ---- PsiGen2.kt -----------------
//header.txt
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import generated.GeneratedTypes.*
import PsiGenUtil.*
import PsiGen.*

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class PsiGen2 {

  companion object {
    /* ********************************************************** */
    // <<p>> +
    fun blockOf(builder_: PsiBuilder, level_: Int, p: Parser): Boolean {
      if (!recursion_guard_(builder_, level_, "blockOf")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = p.parse(builder_, level_)
      while (result_) {
        val pos_: Int = current_position_(builder_)
        if (!p.parse(builder_, level_)) break
        if (!empty_element_parsed_guard_(builder_, "blockOf", pos_)) break
      }
      exit_section_(builder_, marker_, BLOCK_OF, result_)
      return result_
    }

    /* ********************************************************** */
    // '::' id
    fun cast_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "cast_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, CAST_EXPR, "<cast expr>")
      result_ = consumeToken(builder_, "::")
      result_ = result_ && consumeToken(builder_, ID)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id
    fun identifier(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "identifier")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, ID)
      exit_section_(builder_, marker_, IDENTIFIER, result_)
      return result_
    }

    /* ********************************************************** */
    // '[' number ']'
    fun item_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "item_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, ITEM_EXPR, "<item expr>")
      result_ = consumeToken(builder_, "[")
      result_ = result_ && consumeToken(builder_, NUMBER)
      result_ = result_ && consumeToken(builder_, "]")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // number
    fun literal(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "literal")) return false
      if (!nextTokenIs(builder_, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, NUMBER)
      exit_section_(builder_, marker_, LITERAL, result_)
      return result_
    }

    /* ********************************************************** */
    // '.' identifier
    fun qref_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "qref_expr")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, REF_EXPR, "<qref expr>")
      result_ = consumeToken(builder_, ".")
      result_ = result_ && identifier(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // identifier
    fun ref_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "ref_expr")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = identifier(builder_, level_ + 1)
      exit_section_(builder_, marker_, REF_EXPR, result_)
      return result_
    }

    /* ********************************************************** */
    // ref_expr qref_expr *
    internal fun reference(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "reference")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = ref_expr(builder_, level_ + 1)
      result_ = result_ && reference_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // qref_expr *
    private fun reference_1(builder_: PsiBuilder, level_: Int): Boolean {
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
    fun some_expr(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr")) return false
      if (!nextTokenIs(builder_, "<some expr>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, SOME_EXPR, "<some expr>")
      result_ = some_expr_0(builder_, level_ + 1)
      result_ = result_ && some_expr_1(builder_, level_ + 1)
      result_ = result_ && some_expr_2(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // a_expr | specialRef b_expr | some_expr_private
    private fun some_expr_0(builder_: PsiBuilder, level_: Int): Boolean {
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
    private fun some_expr_0_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_0_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = specialRef(builder_, level_ + 1)
      result_ = result_ && b_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // (cast_expr)
    private fun some_expr_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = cast_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // (item_expr) *
    private fun some_expr_2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_2")) return false
      while (true) {
        val pos_: Int = current_position_(builder_)
        if (!some_expr_2_0(builder_, level_ + 1)) break
        if (!empty_element_parsed_guard_(builder_, "some_expr_2", pos_)) break
      }
      return true
    }

    // (item_expr)
    private fun some_expr_2_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_2_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = item_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // specialRef b_expr
    internal fun some_expr_private(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "some_expr_private")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = specialRef(builder_, level_ + 1)
      result_ = result_ && b_expr(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // identifier OF reference
    fun specialRef(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "specialRef")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = identifier(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, OF)
      result_ = result_ && reference(builder_, level_ + 1)
      exit_section_(builder_, marker_, SPECIAL_REF, result_)
      return result_
    }

  }
}
// ---- PsiGenFixes.kt -----------------
//header.txt
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import generated.GeneratedTypes.*
import PsiGenUtil.*
import PsiGen.*

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class PsiGenFixes {

  companion object {
    /* ********************************************************** */
    // ',' identifier
    fun LeftShadow(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "LeftShadow")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, LEFT_SHADOW, "<left shadow>")
      result_ = consumeToken(builder_, ",")
      result_ = result_ && PsiGen2.identifier(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // identifier LeftShadow *
    fun LeftShadowTest(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "LeftShadowTest")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = PsiGen2.identifier(builder_, level_ + 1)
      result_ = result_ && LeftShadowTest_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, LEFT_SHADOW_TEST, result_)
      return result_
    }

    // LeftShadow *
    private fun LeftShadowTest_1(builder_: PsiBuilder, level_: Int): Boolean {
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
    fun a_statement(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "a_statement")) return false
      if (!nextTokenIs(builder_, "<a statement>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, A_STATEMENT, "<a statement>")
      result_ = consumeToken(builder_, ID)
      if (!result_) result_ = consumeToken(builder_, NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id | number
    fun b_statement(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "b_statement")) return false
      if (!nextTokenIs(builder_, "<b statement>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, B_STATEMENT, "<b statement>")
      result_ = consumeToken(builder_, ID)
      if (!result_) result_ = consumeToken(builder_, NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id | number
    fun c_statement(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "c_statement")) return false
      if (!nextTokenIs(builder_, "<c statement>", ID, NUMBER)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, C_STATEMENT, "<c statement>")
      result_ = consumeToken(builder_, ID)
      if (!result_) result_ = consumeToken(builder_, NUMBER)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // literal id '%' | '%' id literal
    fun choice_joined(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "choice_joined")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, CHOICE_JOINED, "<choice joined>")
      result_ = choice_joined_0(builder_, level_ + 1)
      if (!result_) result_ = choice_joined_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // literal id '%'
    private fun choice_joined_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "choice_joined_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = PsiGen2.literal(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ID)
      result_ = result_ && consumeToken(builder_, "%")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // '%' id literal
    private fun choice_joined_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "choice_joined_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, "%")
      result_ = result_ && consumeToken(builder_, ID)
      result_ = result_ && PsiGen2.literal(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // <<blockOf identifier>>
    internal fun fixMetaRule(builder_: PsiBuilder, level_: Int): Boolean {
      return PsiGen2.blockOf(builder_, level_ + 1, PsiGen2::identifier)
    }

    /* ********************************************************** */
    // identifier
    fun publicMethodToCall(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "publicMethodToCall")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = PsiGen2.identifier(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // &<<external>> (a_statement | b_statement) | !<<external>> (c_statement)
    fun statement(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _COLLAPSE_, STATEMENT, "<statement>")
      result_ = statement_0(builder_, level_ + 1)
      if (!result_) result_ = statement_1(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // &<<external>> (a_statement | b_statement)
    private fun statement_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = statement_0_0(builder_, level_ + 1)
      result_ = result_ && statement_0_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // &<<external>>
    private fun statement_0_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_0_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = external(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // a_statement | b_statement
    private fun statement_0_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_0_1")) return false
      var result_: Boolean
      result_ = a_statement(builder_, level_ + 1)
      if (!result_) result_ = b_statement(builder_, level_ + 1)
      return result_
    }

    // !<<external>> (c_statement)
    private fun statement_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = statement_1_0(builder_, level_ + 1)
      result_ = result_ && statement_1_1(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // !<<external>>
    private fun statement_1_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !external(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // (c_statement)
    private fun statement_1_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement_1_1")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = c_statement(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

  }
}