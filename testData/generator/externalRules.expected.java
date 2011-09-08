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
      try {
        result_ = root(builder_, level_ + 1);
        while (builder_.getTokenType() != null) {
          builder_.advanceLexer();
        }
      }
      finally {
        marker_.done(root_);
      }
    }
    return builder_.getTreeBuilt();
  }

  /* ********************************************************** */
  // DO <<uniqueListOf (one | two | 10 | some)>> END
  public static boolean complex_case(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, DO);
      result_ = result_ && uniqueListOf(builder_, level_ + 1, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return complex_case_1_0(builder_, level_ + 1); }});
      result_ = result_ && consumeToken(builder_, END);
    }
    finally {
      if (result_) {
        marker_.done(COMPLEX_CASE);
      }
      else {
        marker_.rollbackTo();
      }
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
    try {
      result_ = one(builder_, level_ + 1);
      if (!result_) result_ = two(builder_, level_ + 1);
      if (!result_) result_ = consumeToken(builder_, "10");
      if (!result_) result_ = consumeToken(builder_, SOME);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // DO <<uniqueListOf {one | two | 10 | some}>> END
  public static boolean complex_case_braces(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_braces")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, DO);
      result_ = result_ && uniqueListOf(builder_, level_ + 1, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return complex_case_braces_1_0(builder_, level_ + 1); }});
      result_ = result_ && consumeToken(builder_, END);
    }
    finally {
      if (result_) {
        marker_.done(COMPLEX_CASE_BRACES);
      }
      else {
        marker_.rollbackTo();
      }
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
    try {
      result_ = one(builder_, level_ + 1);
      if (!result_) result_ = two(builder_, level_ + 1);
      if (!result_) result_ = consumeToken(builder_, "10");
      if (!result_) result_ = consumeToken(builder_, SOME);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // DO <<uniqueListOf [one | two | 10 | some]>> END
  public static boolean complex_case_brackets(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_brackets")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, DO);
      result_ = result_ && uniqueListOf(builder_, level_ + 1, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return complex_case_brackets_1_0(builder_, level_ + 1); }});
      result_ = result_ && consumeToken(builder_, END);
    }
    finally {
      if (result_) {
        marker_.done(COMPLEX_CASE_BRACKETS);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }

  // [one | two | 10 | some]
  private static boolean complex_case_brackets_1_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_brackets_1_0")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      complex_case_brackets_1_0_0(builder_, level_ + 1);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }

  // one | two | 10 | some
  private static boolean complex_case_brackets_1_0_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "complex_case_brackets_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = one(builder_, level_ + 1);
      if (!result_) result_ = two(builder_, level_ + 1);
      if (!result_) result_ = consumeToken(builder_, "10");
      if (!result_) result_ = consumeToken(builder_, SOME);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // DO <<uniqueListOf {one | two} [10 | some]>> (option) END
  public static boolean multi_complex_case(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, DO);
      result_ = result_ && uniqueListOf(builder_, level_ + 1, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return multi_complex_case_1_0(builder_, level_ + 1); }}, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return multi_complex_case_1_1(builder_, level_ + 1); }});
      result_ = result_ && multi_complex_case_2(builder_, level_ + 1);
      result_ = result_ && consumeToken(builder_, END);
    }
    finally {
      if (result_) {
        marker_.done(MULTI_COMPLEX_CASE);
      }
      else {
        marker_.rollbackTo();
      }
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
    try {
      result_ = one(builder_, level_ + 1);
      if (!result_) result_ = two(builder_, level_ + 1);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // [10 | some]
  private static boolean multi_complex_case_1_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_1_1")) return false;
    boolean result_ = true;
    final Marker marker_ = builder_.mark();
    try {
      multi_complex_case_1_1_0(builder_, level_ + 1);
    }
    finally {
      marker_.drop();
    }
    return result_;
  }

  // 10 | some
  private static boolean multi_complex_case_1_1_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_1_1_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, "10");
      if (!result_) result_ = consumeToken(builder_, SOME);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }

  // (option)
  private static boolean multi_complex_case_2(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "multi_complex_case_2")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, OPTION);
    }
    finally {
      if (!result_) {
        marker_.rollbackTo();
      }
      else {
        marker_.drop();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // some value
  public static boolean one(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "one")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, SOME);
      result_ = result_ && consumeToken(builder_, VALUE);
    }
    finally {
      if (result_) {
        marker_.done(ONE);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */

  /* ********************************************************** */
  // DO <<uniqueListOf 'zero' one two 10 some>> END
  public static boolean simple_case(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "simple_case")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, DO);
      result_ = result_ && uniqueListOf(builder_, level_ + 1, 'zero', 
        new Parser() { public boolean parse(PsiBuilder builder_) { return one(builder_, level_ + 1); }}, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return two(builder_, level_ + 1); }}, 10, some);
      result_ = result_ && consumeToken(builder_, END);
    }
    finally {
      if (result_) {
        marker_.done(SIMPLE_CASE);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // simple_case | complex_case | complex_case_brackets | complex_case_braces | multi_complex_case
  public static boolean statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = simple_case(builder_, level_ + 1);
      if (!result_) result_ = complex_case(builder_, level_ + 1);
      if (!result_) result_ = complex_case_brackets(builder_, level_ + 1);
      if (!result_) result_ = complex_case_braces(builder_, level_ + 1);
      if (!result_) result_ = multi_complex_case(builder_, level_ + 1);
    }
    finally {
      if (result_) {
        marker_.done(STATEMENT);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // some other value
  public static boolean two(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "two")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, SOME);
      result_ = result_ && consumeToken(builder_, OTHER);
      result_ = result_ && consumeToken(builder_, VALUE);
    }
    finally {
      if (result_) {
        marker_.done(TWO);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


}