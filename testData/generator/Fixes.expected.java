// ---- Fixes.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Fixes implements PsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parse_only_(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parse_only_(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == SOME) {
      result_ = some(builder_, 0);
    }
    else if (root_ == SOME_SEQ) {
      result_ = some_seq(builder_, 0);
    }
    else if (root_ == WITH_RECURSIVE) {
      result_ = with_recursive(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // some [recursive]
  static boolean recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = some(builder_, level_ + 1);
    result_ = result_ && recursive_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [recursive]
  private static boolean recursive_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "recursive_1")) return false;
    recursive(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  static boolean root(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // <<p>> (',' <<p>>)*
  static boolean sequence(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = p.parse(builder_, level_);
    pinned_ = result_; // pin = 1
    result_ = result_ && sequence_1(builder_, level_ + 1, p);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' <<p>>)*
  private static boolean sequence_1(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!sequence_1_0(builder_, level_ + 1, p)) break;
      if (!empty_element_parsed_guard_(builder_, "sequence_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' <<p>>
  private static boolean sequence_1_0(PsiBuilder builder_, int level_, final Parser p) {
    if (!recursion_guard_(builder_, level_, "sequence_1_0")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && p.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // A
  public static boolean some(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, A);
    exit_section_(builder_, marker_, SOME, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<sequence some>>
  public static boolean some_seq(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "some_seq")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = sequence(builder_, level_ + 1, some_parser_);
    exit_section_(builder_, marker_, SOME_SEQ, result_);
    return result_;
  }

  /* ********************************************************** */
  // recursive
  public static boolean with_recursive(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "with_recursive")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = recursive(builder_, level_ + 1);
    exit_section_(builder_, marker_, WITH_RECURSIVE, result_);
    return result_;
  }

  final static Parser some_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return some(builder_, level_ + 1);
    }
  };
}