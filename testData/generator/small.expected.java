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
public class Small implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("Small");

  @NotNull
  public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {
    final int level_ = 0;
    boolean result_;
    if (root_ == OTHERRULE) {
      result_ = otherRule(builder_, level_ + 1);
    }
    else if (root_ == SOMERULE) {
      result_ = someRule(builder_, level_ + 1);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      try {
        result_ = parseRoot(builder_, level_ + 1, 
          new Parser() { public boolean parse(PsiBuilder builder_) { return statement(builder_, level_ + 1); }});
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
  // ( token )
  public static boolean otherRule(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "otherRule")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, TOKEN);
    }
    finally {
      if (result_) {
        marker_.done(OTHERRULE);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // token
  static boolean privateRule(PsiBuilder builder_, final int level_) {
    return consumeToken(builder_, TOKEN);
  }

  /* ********************************************************** */
  // token
  public static boolean someRule(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "someRule")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, TOKEN);
    }
    finally {
      if (result_) {
        marker_.done(SOMERULE);
      }
      else {
        marker_.rollbackTo();
      }
    }
    return result_;
  }


  /* ********************************************************** */
  // token | someRule | otherRule | privateRule
  public static boolean statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, TOKEN);
      if (!result_) result_ = someRule(builder_, level_ + 1);
      if (!result_) result_ = otherRule(builder_, level_ + 1);
      if (!result_) result_ = privateRule(builder_, level_ + 1);
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


}