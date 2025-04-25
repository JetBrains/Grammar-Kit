// ---- Autopin.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntimeKt.*;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NONE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._COLLAPSE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_INNER_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._AND_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NOT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._UPPER_;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.parser.ProductionResult;
import com.intellij.platform.syntax.SyntaxElementTypeSet;
import static com.intellij.platform.syntax.parser.ProductionResultKt.prepareProduction;
import kotlin.jvm.functions.Function2;
import kotlin.Unit;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class Autopin {

  public ProductionResult parse(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    parseLight(root_, runtime_);
    return prepareProduction(runtime_.getSyntaxBuilder());
  }

  public void parseLight(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    boolean result_;
    Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit> parse_ = new Function2<SyntaxElementType, SyntaxGeneratedParserRuntime, Unit>(){
      @Override
      public Unit invoke(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
        parseLight(root_, runtime_);
        return Unit.INSTANCE;
      }
    };

    runtime_.init(parse_, EXTENDS_SETS_);
    Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_, int level_) {
    return root(runtime_, level_ + 1);
  }

  public static final SyntaxElementTypeSet[] EXTENDS_SETS_ = new SyntaxElementTypeSet[] {
    create_token_set_(CREATE_STATEMENT, CREATE_TABLE_STATEMENT, DROP_STATEMENT, DROP_TABLE_STATEMENT,
      STATEMENT),
  };

  /* ********************************************************** */
  // create_table_statement
  public static boolean create_statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_statement")) return false;
    if (!nextTokenIs(runtime_, CREATE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, CREATE_STATEMENT, null);
    result_ = create_table_statement(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
  public static boolean create_table_statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_table_statement")) return false;
    if (!nextTokenIs(runtime_, CREATE)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, CREATE_TABLE_STATEMENT, null);
    result_ = consumeToken(runtime_, CREATE);
    result_ = result_ && create_table_statement_1(runtime_, level_ + 1);
    result_ = result_ && create_table_statement_2(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, TABLE);
    result_ = result_ && parseReference(runtime_, level_ + 1);
    pinned_ = result_; // pin = .*_ref
    result_ = result_ && report_error_(runtime_, consumeToken(runtime_, "("));
    result_ = pinned_ && consumeToken(runtime_, ")") && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // TEMP?
  private static boolean create_table_statement_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_table_statement_1")) return false;
    consumeToken(runtime_, TEMP);
    return true;
  }

  // GLOBAL|LOCAL
  private static boolean create_table_statement_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_table_statement_2")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, GLOBAL);
    if (!result_) result_ = consumeToken(runtime_, LOCAL);
    return result_;
  }

  /* ********************************************************** */
  // drop_table_statement
  public static boolean drop_statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "drop_statement")) return false;
    if (!nextTokenIs(runtime_, DROP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, DROP_STATEMENT, null);
    result_ = drop_table_statement(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // DROP TABLE table_ref
  public static boolean drop_table_statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "drop_table_statement")) return false;
    if (!nextTokenIs(runtime_, DROP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokens(runtime_, 0, DROP, TABLE);
    result_ = result_ && parseReference(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, DROP_TABLE_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // a b (c d e)
  public static boolean override_nested_sequence(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "override_nested_sequence")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, OVERRIDE_NESTED_SEQUENCE, null);
    result_ = consumeTokens(runtime_, 1, A, B);
    pinned_ = result_; // pin = 1
    result_ = result_ && override_nested_sequence_2(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // c d e
  private static boolean override_nested_sequence_2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "override_nested_sequence_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokens(runtime_, 0, C, D, E);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [] (a|b)
  static boolean pinned_on_start(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_on_start")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = pinned_on_start_0(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && pinned_on_start_1(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // []
  private static boolean pinned_on_start_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  // a|b
  private static boolean pinned_on_start_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "pinned_on_start_1")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = consumeToken(runtime_, B);
    return result_;
  }

  /* ********************************************************** */
  // statement *
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "root")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!statement(runtime_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(runtime_, "root", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // create_statement | drop_statement
  public static boolean statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement")) return false;
    if (!nextTokenIs(runtime_, "<statement>", CREATE, DROP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, STATEMENT, "<statement>");
    result_ = create_statement(runtime_, level_ + 1);
    if (!result_) result_ = drop_statement(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // a b c d table_ref
  static boolean token_sequence1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence1")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeTokens(runtime_, 3, A, B, C, D);
    pinned_ = result_; // pin = 3
    result_ = result_ && parseReference(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // a b table_ref c d e
  static boolean token_sequence2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence2")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeTokens(runtime_, 0, A, B);
    result_ = result_ && parseReference(runtime_, level_ + 1);
    result_ = result_ && consumeTokens(runtime_, 2, C, D, E);
    pinned_ = result_; // pin = 5
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // table_ref a b table_ref c d e
  static boolean token_sequence3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence3")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = parseReference(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, consumeTokens(runtime_, -1, A, B));
    result_ = pinned_ && report_error_(runtime_, parseReference(runtime_, level_ + 1)) && result_;
    result_ = pinned_ && report_error_(runtime_, consumeTokens(runtime_, -1, C, D, E)) && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // [] a
  static boolean token_sequence4(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence4")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = token_sequence4_0(runtime_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(runtime_, A);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // []
  private static boolean token_sequence4_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // (a|&b) pinned_on_start
  static boolean token_sequence5(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence5")) return false;
    if (!nextTokenIs(runtime_, "", A, B)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = token_sequence5_0(runtime_, level_ + 1);
    result_ = result_ && pinned_on_start(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // a|&b
  private static boolean token_sequence5_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence5_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = token_sequence5_0_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // &b
  private static boolean token_sequence5_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence5_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _AND_);
    result_ = consumeToken(runtime_, B);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '+' a "+" a '+++'
  static boolean token_sequence6(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "token_sequence6")) return false;
    if (!nextTokenIs(runtime_, PLUS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokens(runtime_, 0, PLUS, A, PLUS, A);
    result_ = result_ && consumeToken(runtime_, "+++");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

}