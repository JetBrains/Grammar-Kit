// ---- GenOptions.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import static generated.GeneratedSyntaxElementTypes.*;
import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import static com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntimeKt.*;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NONE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._COLLAPSE_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._LEFT_INNER_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._AND_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._NOT_;
import com.intellij.platform.syntax.util.runtime.Modifiers.Companion._UPPER_;

@java.lang.SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GenOptions {

  public com.intellij.platform.syntax.parser.ProductionResult parse(com.intellij.platform.syntax.SyntaxElementType root_, com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_) {
    parseLight(root_, runtime_);
    return com.intellij.platform.syntax.parser.ProductionResultKt.prepareProduction(runtime_.getSyntaxBuilder());
  }

  public void parseLight(com.intellij.platform.syntax.SyntaxElementType root_, com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_) {
    boolean result_;
    kotlin.jvm.functions.Function2<com.intellij.platform.syntax.SyntaxElementType, com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, kotlin.Unit> parse_ = new kotlin.jvm.functions.Function2<com.intellij.platform.syntax.SyntaxElementType, com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime, kotlin.Unit>(){
      @java.lang.Override
      public kotlin.Unit invoke(com.intellij.platform.syntax.SyntaxElementType root_, com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_) {
        parseLight(root_, runtime_);
        return kotlin.Unit.INSTANCE;
      }
    };

    runtime_.init(parse_, EXTENDS_SETS_);
    com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(com.intellij.platform.syntax.SyntaxElementType root_, com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(com.intellij.platform.syntax.SyntaxElementType root_, com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    return root(runtime_, level_ + 1);
  }

  public static final com.intellij.platform.syntax.SyntaxElementTypeSet[] EXTENDS_SETS_ = new com.intellij.platform.syntax.SyntaxElementTypeSet[] {
    create_token_set_(CREATE_STATEMENT, CREATE_TABLE_STATEMENT, DROP_STATEMENT, DROP_TABLE_STATEMENT,
      STATEMENT),
  };

  /* ********************************************************** */
  // create_table_statement
  public static boolean create_statement(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_statement")) return false;
    if (!nextTokenIs(runtime_, create)) return false;
    boolean result_;
    com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, CREATE_STATEMENT, null);
    result_ = create_table_statement(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // CREATE TEMP? (GLOBAL|LOCAL) TABLE table_ref '(' ')'
  public static boolean create_table_statement(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_table_statement")) return false;
    if (!nextTokenIs(runtime_, create)) return false;
    boolean result_, pinned_;
    com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker marker_ = enter_section_(runtime_, level_, _NONE_, CREATE_TABLE_STATEMENT, null);
    result_ = consumeToken(runtime_, create);
    result_ = result_ && create_table_statement_1(runtime_, level_ + 1);
    result_ = result_ && create_table_statement_2(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, table);
    result_ = result_ && table_ref(runtime_, level_ + 1);
    pinned_ = result_; // pin = .*_ref
    result_ = result_ && report_error_(runtime_, consumeToken(runtime_, "("));
    result_ = pinned_ && consumeToken(runtime_, ")") && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // TEMP?
  private static boolean create_table_statement_1(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_table_statement_1")) return false;
    consumeToken(runtime_, temp);
    return true;
  }

  // GLOBAL|LOCAL
  private static boolean create_table_statement_2(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "create_table_statement_2")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, global);
    if (!result_) result_ = consumeToken(runtime_, local);
    return result_;
  }

  /* ********************************************************** */
  // drop_table_statement
  public static boolean drop_statement(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "drop_statement")) return false;
    if (!nextTokenIs(runtime_, drop)) return false;
    boolean result_;
    com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, DROP_STATEMENT, null);
    result_ = drop_table_statement(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // DROP TABLE table_ref
  public static boolean drop_table_statement(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "drop_table_statement")) return false;
    if (!nextTokenIs(runtime_, drop)) return false;
    boolean result_;
    com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker marker_ = enter_section_(runtime_);
    result_ = consumeTokens(runtime_, 0, drop, table);
    result_ = result_ && table_ref(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, DROP_TABLE_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // statement *
  static boolean root(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
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
  public static boolean statement(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement")) return false;
    if (!nextTokenIs(runtime_, "<statement>", create, drop)) return false;
    boolean result_;
    com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, STATEMENT, "<statement>");
    result_ = create_statement(runtime_, level_ + 1);
    if (!result_) result_ = drop_statement(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // id
  public static boolean table_ref(com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "table_ref")) return false;
    if (!nextTokenIs(runtime_, id)) return false;
    boolean result_;
    com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, id);
    exit_section_(runtime_, marker_, TABLE_REF, result_);
    return result_;
  }

}