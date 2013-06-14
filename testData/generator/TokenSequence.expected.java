// ---- GeneratedParser.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import org.jetbrains.annotations.*;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GeneratedParser implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("generated.GeneratedParser");

  @NotNull
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
    if (root_ == BAR) {
      result_ = bar(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      enterErrorRecordingSection(builder_, level_, _SECTION_RECOVER_, null);
      result_ = parse_root_(root_, builder_, level_);
      exitErrorRecordingSection(builder_, level_, result_, true, _SECTION_RECOVER_, TOKEN_ADVANCER);
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return foo(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // a
  public static boolean bar(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "bar")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, A);
    if (result_) {
      marker_.done(BAR);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // a 
  //   | b c d
  //   | c d e
  //   | bar d e f
  static boolean foo(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foo")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = parseTokens(builder_, 2, B, C, D);
    if (!result_) result_ = parseTokens(builder_, 2, C, D, E);
    if (!result_) result_ = foo_3(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // bar d e f
  private static boolean foo_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foo_3")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = bar(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 1, D, E, F);
    pinned_ = result_; // pin = 2
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

}