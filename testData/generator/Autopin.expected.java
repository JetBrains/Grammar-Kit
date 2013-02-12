// ---- Autopin.java -----------------
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
  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    int level_ = 0;
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this);
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
    else if (root_ == OVERRIDE_NESTED_SEQUENCE) {
      result_ = override_nested_sequence(builder_, level_ + 1);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, level_ + 1);
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
    return root(builder_, level_ + 1);
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
  public static boolean create_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_statement")) return false;
    if (!nextTokenIs(builder_, CREATE)) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
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
  public static boolean create_table_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement")) return false;
    if (!nextTokenIs(builder_, CREATE)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeToken(builder_, CREATE);
    result_ = result_ && create_table_statement_1(builder_, level_ + 1);
    result_ = result_ && create_table_statement_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, TABLE);
    result_ = result_ && parseReference(builder_, level_ + 1);
    pinned_ = result_; // pin = .*_ref
    result_ = result_ && report_error_(builder_, consumeToken(builder_, "("));
    result_ = pinned_ && consumeToken(builder_, ")") && result_;
    if (result_ || pinned_) {
      marker_.done(CREATE_TABLE_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // TEMP?
  private static boolean create_table_statement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement_1")) return false;
    consumeToken(builder_, TEMP);
    return true;
  }

  // GLOBAL|LOCAL
  private static boolean create_table_statement_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "create_table_statement_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
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
  public static boolean drop_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "drop_statement")) return false;
    if (!nextTokenIs(builder_, DROP)) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
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
  public static boolean drop_table_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "drop_table_statement")) return false;
    if (!nextTokenIs(builder_, DROP)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeTokens(builder_, 0, DROP, TABLE);
    result_ = result_ && parseReference(builder_, level_ + 1);
    pinned_ = result_; // pin = .*_ref
    if (result_ || pinned_) {
      marker_.done(DROP_TABLE_STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // a b (c d e)
  public static boolean override_nested_sequence(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "override_nested_sequence")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeTokens(builder_, 1, A, B);
    pinned_ = result_; // pin = 1
    result_ = result_ && override_nested_sequence_2(builder_, level_ + 1);
    if (result_ || pinned_) {
      marker_.done(OVERRIDE_NESTED_SEQUENCE);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // c d e
  private static boolean override_nested_sequence_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "override_nested_sequence_2")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeTokens(builder_, 0, C, D, E);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // [] (a|b)
  static boolean pinned_on_start(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_on_start")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = pinned_on_start_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && pinned_on_start_1(builder_, level_ + 1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // []
  private static boolean pinned_on_start_0(PsiBuilder builder_, int level_) {
    return true;
  }

  // a|b
  private static boolean pinned_on_start_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pinned_on_start_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = consumeToken(builder_, B);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // statement *
  static boolean root(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "root")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!statement(builder_, level_ + 1)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "root");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  /* ********************************************************** */
  // create_statement | drop_statement
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    if (!nextTokenIs(builder_, CREATE) && !nextTokenIs(builder_, DROP)
        && replaceVariants(builder_, 2, "<statement>")) return false;
    boolean result_ = false;
    int start_ = builder_.getCurrentOffset();
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, "<statement>");
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
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_GENERAL_, null);
    return result_;
  }

  /* ********************************************************** */
  // a b c d table_ref
  static boolean token_sequence1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_sequence1")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeTokens(builder_, 3, A, B, C, D);
    pinned_ = result_; // pin = 3
    result_ = result_ && parseReference(builder_, level_ + 1);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // a b table_ref c d e
  static boolean token_sequence2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_sequence2")) return false;
    if (!nextTokenIs(builder_, A)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = consumeTokens(builder_, 0, A, B);
    result_ = result_ && parseReference(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 2, C, D, E);
    pinned_ = result_; // pin = 5
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // table_ref a b table_ref c d e
  static boolean token_sequence3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_sequence3")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = parseReference(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, consumeTokens(builder_, -1, A, B));
    result_ = pinned_ && report_error_(builder_, parseReference(builder_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(builder_, consumeTokens(builder_, -1, C, D, E)) && result_;
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // [] a
  static boolean token_sequence4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_sequence4")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_, null);
    result_ = token_sequence4_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, A);
    if (!result_ && !pinned_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    result_ = exitErrorRecordingSection(builder_, level_, result_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  // []
  private static boolean token_sequence4_0(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // (a|&b) pinned_on_start
  static boolean token_sequence5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_sequence5")) return false;
    if (!nextTokenIs(builder_, A) && !nextTokenIs(builder_, B)) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = token_sequence5_0(builder_, level_ + 1);
    result_ = result_ && pinned_on_start(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // a|&b
  private static boolean token_sequence5_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_sequence5_0")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, A);
    if (!result_) result_ = token_sequence5_0_1(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // &b
  private static boolean token_sequence5_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "token_sequence5_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_AND_, null);
    result_ = consumeToken(builder_, B);
    marker_.rollbackTo();
    result_ = exitErrorRecordingSection(builder_, level_, result_, false, _SECTION_AND_, null);
    return result_;
  }

}