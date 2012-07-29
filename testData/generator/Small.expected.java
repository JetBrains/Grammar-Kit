// ---- Small.java -----------------
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
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Small implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("Small");

  @NotNull
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
    if (root_ == OTHERRULE) {
      result_ = otherRule(builder_, level_ + 1);
    }
    else if (root_ == SOMERULE) {
      result_ = someRule(builder_, level_ + 1);
    }
    else if (root_ == SOMERULE2) {
      result_ = someRule2(builder_, level_ + 1);
    }
    else if (root_ == SOMESTRING) {
      result_ = someString(builder_, level_ + 1);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, level_ + 1);
    }
    else {
      Marker marker_ = builder_.mark();
      result_ = parse_root_(root_, builder_, level_);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return parseRoot(builder_, level_ + 1, statement_parser_);
  }

  /* ********************************************************** */
  // ( token )
  public static boolean otherRule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "otherRule")) return false;
    if (!nextTokenIs(builder_, TOKEN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, TOKEN);
    if (result_) {
      marker_.done(OTHERRULE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // token
  static boolean privateRule(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, TOKEN);
  }

  /* ********************************************************** */
  // 'token'
  static boolean privateString(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, "token");
  }

  /* ********************************************************** */
  // token
  public static boolean someRule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someRule")) return false;
    if (!nextTokenIs(builder_, TOKEN)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, TOKEN);
    if (result_) {
      marker_.done(SOMERULE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // token?
  public static boolean someRule2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someRule2")) return false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<some rule 2>");
    consumeToken(builder_, TOKEN);
    marker_.done(SOMERULE2);
    exitErrorRecordingSection(builder_, level_, true, false, _SECTION_GENERAL_, null);
    return true;
  }

  /* ********************************************************** */
  // 'token'
  public static boolean someString(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "someString")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<some string>");
    result_ = consumeToken(builder_, "token");
    if (result_) {
      marker_.done(SOMESTRING);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // token | someRule | someString
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<statement>");
    result_ = consumeToken(builder_, TOKEN);
    if (!result_) result_ = someRule(builder_, level_ + 1);
    if (!result_) result_ = someString(builder_, level_ + 1);
    if (result_) {
      marker_.done(STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // '=' "="
  static boolean tokenRule(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "tokenRule")) return false;
    if (!nextTokenIs(builder_, OP_EQ)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, OP_EQ);
    result_ = result_ && consumeToken(builder_, "=");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  final static Parser statement_parser_ = new Parser() {
      public boolean parse(PsiBuilder builder_, int level_) {
        return statement(builder_, level_ + 1);
      }
    };
}