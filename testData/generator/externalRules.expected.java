// This is a generated file. Not intended for manual editing.
package ;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.ParserTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRules implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("ExternalRules");

  @NotNull
  public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {
    final int level_ = 0;
    boolean result_;
    if (root_ == COMPLEX_CASE) {
      result_ = complex_case(builder_, level_ + 1);
    }
    else if (root_ == COMPLEX_CASE_BRACES) {
      result_ = complex_case_braces(builder_, level_ + 1);
    }
    else if (root_ == COMPLEX_CASE_BRACKETS) {
      result_ = complex_case_brackets(builder_, level_ + 1);
    }
    else if (root_ == MULTI_COMPLEX_CASE) {
      result_ = multi_complex_case(builder_, level_ + 1);
    }
    else if (root_ == ONE) {
      result_ = one(builder_, level_ + 1);
    }
    else if (root_ == ONE_LIST) {
      result_ = one_list(builder_, level_ + 1);
    }
    else if (root_ == SIMPLE_CASE) {
      result_ = simple_case(builder_, level_ + 1);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, level_ + 1);
    }
    else if (root_ == TWO) {
      result_ = two(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      result_ = root(builder_, level_ + 1);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  /* ********************************************************** */
  // <<param>> (',' <<param>>) *
  static boolean comma_list(PsiBuilder builder_, final int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = param.parse(builder_);
    result_ = result_ && comma_list_1(builder_, level_ + 1, param);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (',' <<param>>) *
  private static boolean comma_list_1(PsiBuilder builder_, final int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!comma_list_1_0(builder_, level_ + 1, param)) break;
      if (offset_ == builder_.getCurrentOffset()) {
        builder_.error("Empty element parsed in comma_list_1");
        break;
      }
      offset_ = builder_.getCurrentOffset();
    }
    marker_.drop();
    return result_;
  }

  // (',' <<param>>)
  private static boolean comma_list_1_0(PsiBuilder builder_, final int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1_0")) return false;
    return comma_list_1_0_0(builder_, level_ + 1, param);
  }

  // ',' <<param>>
  private static boolean comma_list_1_0_0(PsiBuilder builder_, final int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ",");
    result_ = result_ && param.parse(builder_);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // <<head>> <<param>> (',' <<param>>) *
  static boolean comma_list_with_head(PsiBuilder builder_, final int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_with_head")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = head.parse(builder_);
    result_ = result_ && param.parse(builder_);
    result_ = result_ && comma_list_with_head_2(builder_, level_ + 1, head, param);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (',' <<param>>) *
  private static boolean comma_list_with_head_2(PsiBuilder builder_, final int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_with_head_2")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    int offset_ = builder_.getCurrentOffset();
    while (result_) {
      if (!comma_list_with_head_2_0(builder_, level_ + 1, head, param)) break;
      if (offset_ == builder_.getCurrentOffset()) {
        builder_.error("Empty element parsed in comma_list_with_head_2");
        break;
      }
      offset_ = builder_.getCurrentOffset();
    }
    marker_.drop();
    return result_;
  }

  // (',' <<param>>)
  private static boolean comma_list_with_head_2_0(PsiBuilder builder_, final int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_with_head_2_0")) return false;
    return comma_list_with_head_2_0_0(builder_, level_ + 1, head, param);
  }

  // ',' <<param>>
  private static boolean comma_list_with_head_2_0_0(PsiBuilder builder_, final int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_with_head_2_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ",");
    result_ = result_ && param.parse(builder_);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // DO <<uniqueListOf (one | two | 10 | some)>> END
  public static boolean complex_case(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, DO);
    result_ = result_ && uniqueListOf(builder_, level_ + 1, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return complex_case_1_0(builder_, level_ + 1); }});
    result_ = result_ && consumeToken(builder_, END);
    if (result_) {
      marker_.done(COMPLEX_CASE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (one | two | 10 | some)
  private static boolean complex_case_1_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_1_0")) return false;
    return complex_case_1_0_0(builder_, level_ + 1);
  }

  // one | two | 10 | some
  private static boolean complex_case_1_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // DO <<uniqueListOf {one | two | 10 | some}>> END
  public static boolean complex_case_braces(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_braces")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, DO);
    result_ = result_ && uniqueListOf(builder_, level_ + 1, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return complex_case_braces_1_0(builder_, level_ + 1); }});
    result_ = result_ && consumeToken(builder_, END);
    if (result_) {
      marker_.done(COMPLEX_CASE_BRACES);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // {one | two | 10 | some}
  private static boolean complex_case_braces_1_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_braces_1_0")) return false;
    return complex_case_braces_1_0_0(builder_, level_ + 1);
  }

  // one | two | 10 | some
  private static boolean complex_case_braces_1_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_braces_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // DO <<uniqueListOf [one | two | 10 | some]>> END
  public static boolean complex_case_brackets(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_brackets")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, DO);
    result_ = result_ && uniqueListOf(builder_, level_ + 1, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return complex_case_brackets_1_0(builder_, level_ + 1); }});
    result_ = result_ && consumeToken(builder_, END);
    if (result_) {
      marker_.done(COMPLEX_CASE_BRACKETS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // [one | two | 10 | some]
  private static boolean complex_case_brackets_1_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_brackets_1_0")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    complex_case_brackets_1_0_0(builder_, level_ + 1);
    marker_.drop();
    return result_;
  }

  // one | two | 10 | some
  private static boolean complex_case_brackets_1_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_brackets_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // DO <<uniqueListOf {one | two} [10 | some]>> (option) END
  public static boolean multi_complex_case(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, DO);
    result_ = result_ && uniqueListOf(builder_, level_ + 1, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return multi_complex_case_1_0(builder_, level_ + 1); }}, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return multi_complex_case_1_1(builder_, level_ + 1); }});
    result_ = result_ && multi_complex_case_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END);
    if (result_) {
      marker_.done(MULTI_COMPLEX_CASE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // {one | two}
  private static boolean multi_complex_case_1_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_1_0")) return false;
    return multi_complex_case_1_0_0(builder_, level_ + 1);
  }

  // one | two
  private static boolean multi_complex_case_1_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // [10 | some]
  private static boolean multi_complex_case_1_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_1_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    multi_complex_case_1_1_0(builder_, level_ + 1);
    marker_.drop();
    return result_;
  }

  // 10 | some
  private static boolean multi_complex_case_1_1_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_1_1_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (option)
  private static boolean multi_complex_case_2(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_2")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OPTION);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // some value
  public static boolean one(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "one")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, SOME);
    result_ = result_ && consumeToken(builder_, VALUE);
    if (result_) {
      marker_.done(ONE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


  /* ********************************************************** */
  // <<comma_list one>>
  public static boolean one_list(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "one_list")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = comma_list(builder_, level_ + 1, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return one(builder_, level_ + 1); }});
    if (result_) {
      marker_.done(ONE_LIST);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


  /* ********************************************************** */
  // <<comma_list_with_head (WITH) one>>
  static boolean one_list_with(PsiBuilder builder_, final int level_) {
    return comma_list_with_head(builder_, level_ + 1, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return one_list_with_0_0(builder_, level_ + 1); }}, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return one(builder_, level_ + 1); }});
  }

  // (WITH)
  private static boolean one_list_with_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "one_list_with_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, WITH);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // <<listOf statement>>
  static boolean root(PsiBuilder builder_, final int level_) {
    return listOf(builder_, level_ + 1, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return statement(builder_, level_ + 1); }});
  }


  /* ********************************************************** */
  // DO <<uniqueListOf 'zero' one two 10 some>> END
  public static boolean simple_case(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_case")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, DO);
    result_ = result_ && uniqueListOf(builder_, level_ + 1, 'zero', 
      new Parser() { public boolean parse(PsiBuilder builder_) { return one(builder_, level_ + 1); }}, 
      new Parser() { public boolean parse(PsiBuilder builder_) { return two(builder_, level_ + 1); }}, 10, some);
    result_ = result_ && consumeToken(builder_, END);
    if (result_) {
      marker_.done(SIMPLE_CASE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


  /* ********************************************************** */
  // simple_case | complex_case | complex_case_brackets | complex_case_braces | multi_complex_case
  public static boolean statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = simple_case(builder_, level_ + 1);
    if (!result_) result_ = complex_case(builder_, level_ + 1);
    if (!result_) result_ = complex_case_brackets(builder_, level_ + 1);
    if (!result_) result_ = complex_case_braces(builder_, level_ + 1);
    if (!result_) result_ = multi_complex_case(builder_, level_ + 1);
    if (result_) {
      marker_.done(STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


  /* ********************************************************** */
  // some other value
  public static boolean two(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "two")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, SOME);
    result_ = result_ && consumeToken(builder_, OTHER);
    result_ = result_ && consumeToken(builder_, VALUE);
    if (result_) {
      marker_.done(TWO);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


}