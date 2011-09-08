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
    if (root_ == CREATE_TABLE_STATEMENT) {
      result_ = create_table_statement(builder_, level_ + 1);
    }
    else if (root_ == ONE) {
      result_ = one(builder_, level_ + 1);
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
  // CREATE <<uniqueListOf 'zero' one two 10 some>> TABLE table_ref '(' ')'
  public static boolean create_table_statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = consumeToken(builder_, CREATE);
      result_ = result_ && uniqueListOf(builder_, level_ + 1, 'zero', 
        new Parser() { public boolean parse(PsiBuilder builder_) { return one(builder_, level_ + 1); }}, 
        new Parser() { public boolean parse(PsiBuilder builder_) { return two(builder_, level_ + 1); }}, 10, some);
      result_ = result_ && consumeToken(builder_, TABLE);
      result_ = result_ && consumeToken(builder_, TABLE_REF);
      result_ = result_ && consumeToken(builder_, "(");
      result_ = result_ && consumeToken(builder_, ")");
    }
    finally {
      if (result_) {
        marker_.done(CREATE_TABLE_STATEMENT);
      }
      else {
        marker_.rollbackTo();
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
  // create_table_statement
  public static boolean statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    try {
      result_ = create_table_statement(builder_, level_ + 1);
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