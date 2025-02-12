// ---- LeftAssociative.kt -----------------
// This is a generated file. Not intended for manual editing.
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import generated.GeneratedTypes.*
import com.intellij.lang.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import com.intellij.lang.PsiParser
import com.intellij.lang.LightPsiParser

@Suppress("unused", "FunctionName", "JoinDeclarationAndAssignment")
open class LeftAssociative: PsiParser, LightPsiParser {

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
      return from(builder_, level_ + 1)
    }

    /* ********************************************************** */
    // AS? id
    fun alias_definition(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "alias_definition")) return false
      if (!nextTokenIs(builder_, "<alias definition>", AS, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, ALIAS_DEFINITION, "<alias definition>")
      result_ = alias_definition_0(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ID)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // AS?
    private fun alias_definition_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "alias_definition_0")) return false
      consumeToken(builder_, AS)
      return true
    }

    /* ********************************************************** */
    // AS? id
    fun alias_definition2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "alias_definition2")) return false
      if (!nextTokenIs(builder_, "<alias definition 2>", AS, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, ALIAS_DEFINITION_2, "<alias definition 2>")
      result_ = alias_definition2_0(builder_, level_ + 1)
      result_ = result_ && consumeToken(builder_, ID)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // AS?
    private fun alias_definition2_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "alias_definition2_0")) return false
      consumeToken(builder_, AS)
      return true
    }

    /* ********************************************************** */
    // reference alias_definition? alias_definition2? leech? leech2?
    internal fun from(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "from")) return false
      if (!nextTokenIs(builder_, REFERENCE)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, REFERENCE)
      result_ = result_ && from_1(builder_, level_ + 1)
      result_ = result_ && from_2(builder_, level_ + 1)
      result_ = result_ && from_3(builder_, level_ + 1)
      result_ = result_ && from_4(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    // alias_definition?
    private fun from_1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "from_1")) return false
      alias_definition(builder_, level_ + 1)
      return true
    }

    // alias_definition2?
    private fun from_2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "from_2")) return false
      alias_definition2(builder_, level_ + 1)
      return true
    }

    // leech?
    private fun from_3(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "from_3")) return false
      leech(builder_, level_ + 1)
      return true
    }

    // leech2?
    private fun from_4(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "from_4")) return false
      leech2(builder_, level_ + 1)
      return true
    }

    /* ********************************************************** */
    // id
    fun leech(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "leech")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_INNER_, LEECH, null)
      result_ = consumeToken(builder_, ID)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id
    internal fun leech2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "leech2")) return false
      if (!nextTokenIs(builder_, ID)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_INNER_)
      result_ = consumeToken(builder_, ID)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // id?
    internal fun leech3(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "leech3")) return false
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_INNER_)
      consumeToken(builder_, ID)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

  }
}