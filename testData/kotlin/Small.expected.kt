// ---- Small.kt -----------------
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
import java.util.List
import java.util.Map

class Small: PsiParser, LightPsiParser {

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
      return parseRoot(builder_, level_ + 1, Small::statement)
    }

    /* ********************************************************** */
    // ()
    fun empty(builder_: PsiBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, EMPTY, true)
      return true
    }

    /* ********************************************************** */
    // []
    fun empty10(builder_: PsiBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_INNER_, EMPTY_10, null)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // {}
    fun empty2(builder_: PsiBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, EMPTY_2, true)
      return true
    }

    /* ********************************************************** */
    // []
    fun empty3(builder_: PsiBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_)
      exit_section_(builder_, marker_, EMPTY_3, true)
      return true
    }

    /* ********************************************************** */
    // ()
    internal fun empty4(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // []
    internal fun empty5(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // &()
    internal fun empty6(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "empty6")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _AND_)
      result_ = empty6_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ()
    private fun empty6_0(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // !()
    internal fun empty7(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "empty7")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NOT_)
      result_ = !empty7_0(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    // ()
    private fun empty7_0(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // [({})]
    internal fun empty8(builder_: PsiBuilder, level_: Int): Boolean {
      return true
    }

    /* ********************************************************** */
    // []
    fun empty9(builder_: PsiBuilder, level_: Int): Boolean {
      val marker_: Marker = enter_section_(builder_, level_, _LEFT_, EMPTY_9, null)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // [({token})]
    internal fun not_empty1(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty1")) return false
      not_empty1_0(builder_, level_ + 1)
      return true
    }

    // {token}
    private fun not_empty1_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty1_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, TOKEN)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // [({token someString})]
    internal fun not_empty2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty2")) return false
      not_empty2_0(builder_, level_ + 1)
      return true
    }

    // token someString
    private fun not_empty2_0(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "not_empty2_0")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, TOKEN)
      result_ = result_ && someString(builder_, level_ + 1)
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

    /* ********************************************************** */
    // ( token )
    fun otherRule(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "otherRule")) return false
      if (!nextTokenIs(builder_, TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, TOKEN)
      exit_section_(builder_, marker_, OTHER_RULE, result_)
      return result_
    }

    /* ********************************************************** */
    // token
    internal fun privateRule(builder_: PsiBuilder, level_: Int): Boolean {
      return consumeToken(builder_, TOKEN)
    }

    /* ********************************************************** */
    // 'token'
    internal fun privateString(builder_: PsiBuilder, level_: Int): Boolean {
      return consumeToken(builder_, "token")
    }

    /* ********************************************************** */
    // token
    fun someRule(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "someRule")) return false
      if (!nextTokenIs(builder_, TOKEN)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeToken(builder_, TOKEN)
      exit_section_(builder_, marker_, SOME_RULE, result_)
      return result_
    }

    /* ********************************************************** */
    // token?
    fun someRule2(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "someRule2")) return false
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, SOME_RULE_2, "<some rule 2>")
      consumeToken(builder_, TOKEN)
      exit_section_(builder_, level_, marker_, true, false, null)
      return true
    }

    /* ********************************************************** */
    // 'token'
    fun someString(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "someString")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, SOME_STRING, "<some string>")
      result_ = consumeToken(builder_, "token")
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // token | someRule | someString
    fun statement(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "statement")) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_, level_, _NONE_, STATEMENT, "<statement>")
      result_ = consumeToken(builder_, TOKEN)
      if (!result_) result_ = someRule(builder_, level_ + 1)
      if (!result_) result_ = someString(builder_, level_ + 1)
      exit_section_(builder_, level_, marker_, result_, false, null)
      return result_
    }

    /* ********************************************************** */
    // '=' "=" '==' "=="
    internal fun tokenRule(builder_: PsiBuilder, level_: Int): Boolean {
      if (!recursion_guard_(builder_, level_, "tokenRule")) return false
      if (!nextTokenIs(builder_, OP_EQ)) return false
      var result_: Boolean
      val marker_: Marker = enter_section_(builder_)
      result_ = consumeTokens(builder_, 0, OP_EQ, OP_EQ)
      result_ = result_ && consumeToken(builder_, "==")
      result_ = result_ && consumeToken(builder_, "==")
      exit_section_(builder_, marker_, null, result_)
      return result_
    }

  }
}