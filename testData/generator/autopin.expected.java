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
public class Autopin implements PsiParser {

  public static Logger LOG_ = Logger.getInstance("Autopin");

  @NotNull
  public ASTNode parse(final IElementType root_, final PsiBuilder builder_) {
    final int level_ = 0;
    boolean result_;
    if (root_ == CREATE_STATEMENT) {
      result_ = create_statement(builder_, level_ + 1);
    }
    else if (root_ == CREATE_TABLE_STATEMENT) {
      result_ = create_table_statement(builder_, level_ + 1);
    }
    else if (root_ == DROP_STATEMENT) {
      result_ = drop_statement(builder_, level_ + 1);
    }
    else if (root_ == DROP_TABLE_STATEMENT) {
      result_ = drop_table_statement(builder_, level_ + 1);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, level_ + 1);
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

  private static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    TokenSet.create(CREATE_STATEMENT, CREATE_TABLE_STATEMENT, DROP_STATEMENT, DROP_TABLE_STATEMENT,
      STATEMENT),
  };
  public static boolean type_extends_(IElementType child_, IElementType parent_) {
    for (TokenSet set : EXTENDS_SETS_) {
      if (set.contains(child_) && set.contains(parent_)) return true;
    }
    return false;
  }

  /* ********************************************************** */
  // create_table_statement
  public static boolean create_statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "create_statement")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = create_table_statement(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), CREATE_STATEMENT)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(CREATE_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


  /* ********************************************************** */
  // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
  public static boolean create_table_statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
    result_ = consumeToken(builder_, CREATE);
    result_ = result_ && create_table_statement_1(builder_, level_ + 1);
    result_ = result_ && create_table_statement_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TABLE);
    result_ = result_ && parseReference(builder_, level_ + 1);
    pinned_ = result_; // pin = .*_ref
    result_ = result_ && consumeToken(builder_, "(");
    result_ = result_ && consumeToken(builder_, ")");
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), CREATE_TABLE_STATEMENT)) {
      marker_.drop();
    }
    else if (result_ || pinned_) {
      marker_.done(CREATE_TABLE_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // TEMP?
  private static boolean create_table_statement_1(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement_1")) return false;
    consumeToken(builder_, TEMP);
    return true;
  }

  // (GLOBAL|LOCAL)
  private static boolean create_table_statement_2(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement_2")) return false;
    return create_table_statement_2_0(builder_, level_ + 1);
  }

  // GLOBAL|LOCAL
  private static boolean create_table_statement_2_0(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement_2_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, GLOBAL);
    if (!result_) result_ = consumeToken(builder_, LOCAL);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }


  /* ********************************************************** */
  // drop_table_statement
  public static boolean drop_statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "drop_statement")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = drop_table_statement(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), DROP_STATEMENT)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(DROP_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


  /* ********************************************************** */
  // DROP TABLE table_ref
  public static boolean drop_table_statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "drop_table_statement")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
    result_ = consumeToken(builder_, DROP);
    result_ = result_ && consumeToken(builder_, TABLE);
    result_ = result_ && parseReference(builder_, level_ + 1);
    pinned_ = result_; // pin = .*_ref
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), DROP_TABLE_STATEMENT)) {
      marker_.drop();
    }
    else if (result_ || pinned_) {
      marker_.done(DROP_TABLE_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }


  /* ********************************************************** */
  // statement *
  static boolean root(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!statement(builder_, level_ + 1)) break;
      if (offset_ == builder_.getCurrentOffset()) {
        builder_.error("Empty element parsed in root");
        break;
      }
      offset_ = builder_.getCurrentOffset();
    }
    return true;
  }


  /* ********************************************************** */
  // create_statement | drop_statement
  public static boolean statement(PsiBuilder builder_, final int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_ = false;
    final int start_ = builder_.getCurrentOffset();
    final Marker marker_ = builder_.mark();
    result_ = create_statement(builder_, level_ + 1);
    if (!result_) result_ = drop_statement(builder_, level_ + 1);
    LighterASTNode last_ = result_? builder_.getLatestDoneMarker() : null;
    if (last_ != null && last_.getStartOffset() == start_ && type_extends_(last_.getTokenType(), STATEMENT)) {
      marker_.drop();
    }
    else if (result_) {
      marker_.done(STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }


}