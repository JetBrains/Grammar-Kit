// ---- LeftAssociative.java -----------------
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
public class LeftAssociative implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("LeftAssociative");

  @NotNull
  public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    if (root_ == ALIAS_DEFINITION) {
      result_ = alias_definition(builder_, level_ + 1);
    }
    else if (root_ == ALIAS_DEFINITION2) {
      result_ = alias_definition2(builder_, level_ + 1);
    }
    else if (root_ == LEECH) {
      result_ = leech(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      result_ = from(builder_, level_ + 1);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  /* ********************************************************** */
  // AS? id
  public static boolean alias_definition(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition")) return false;
    if (!nextTokenIs(builder_, AS) && !nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "alias_definition")) return false;
    final Marker marker_ = builder_.mark();
    result_ = alias_definition_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ID);
    if (result_) {
      marker_.drop();
      left_marker_.precede().done(ALIAS_DEFINITION);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // AS?
  private static boolean alias_definition_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition_0")) return false;
    consumeToken(builder_, AS);
    return true;
  }

  /* ********************************************************** */
  // (AS? id)
  public static boolean alias_definition2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition2")) return false;
    return alias_definition2_0(builder_, level_ + 1);
  }

  // AS? id
  private static boolean alias_definition2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition2_0")) return false;
    if (!nextTokenIs(builder_, AS) && !nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "alias_definition2_0")) return false;
    final Marker marker_ = builder_.mark();
    result_ = alias_definition2_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, ID);
    if (result_) {
      marker_.drop();
      left_marker_.precede().done(ALIAS_DEFINITION2);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // AS?
  private static boolean alias_definition2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "alias_definition2_0_0")) return false;
    consumeToken(builder_, AS);
    return true;
  }

  /* ********************************************************** */
  // reference alias_definition? alias_definition2? leech? leech2?
  static boolean from(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from")) return false;
    if (!nextTokenIs(builder_, REFERENCE)) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, REFERENCE);
    result_ = result_ && from_1(builder_, level_ + 1);
    result_ = result_ && from_2(builder_, level_ + 1);
    result_ = result_ && from_3(builder_, level_ + 1);
    result_ = result_ && from_4(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // alias_definition?
  private static boolean from_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_1")) return false;
    alias_definition(builder_, level_ + 1);
    return true;
  }

  // alias_definition2?
  private static boolean from_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_2")) return false;
    alias_definition2(builder_, level_ + 1);
    return true;
  }

  // leech?
  private static boolean from_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_3")) return false;
    leech(builder_, level_ + 1);
    return true;
  }

  // leech2?
  private static boolean from_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "from_4")) return false;
    leech2(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // id
  public static boolean leech(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "leech")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "leech")) return false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ID);
    if (result_) {
      marker_.done(LEECH);
      left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());
      left_marker_.drop();
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // id
  static boolean leech2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "leech2")) return false;
    if (!nextTokenIs(builder_, ID)) return false;
    boolean result_ = false;
    final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "leech2")) return false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ID);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
      left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());
      left_marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // id?
  static boolean leech3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "leech3")) return false;
    final Marker left_marker_ = (Marker)builder_.getLatestDoneMarker();
    if (!invalid_left_marker_guard_(builder_, left_marker_, "leech3")) return false;
    consumeToken(builder_, ID);
    left_marker_.precede().done(((LighterASTNode)left_marker_).getTokenType());
    left_marker_.drop();
    return true;
  }

}