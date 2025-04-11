// ---- GeneratedParser.java -----------------
// This is a generated file. Not intended for manual editing.
package generated;

import com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntimeKt.*;
import static com.intellij.platform.syntax.parser.ProductionResult.*;
import static com.intellij.platform.syntax.parser.ProductionResultKt.prepareProduction;
import kotlin.jvm.functions.Function2;
import kotlin.Unit;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.parser.ProductionResult;
import com.intellij.platform.syntax.SyntaxElementTypeSet;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GeneratedParser {

  public ProductionResult parse(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    parseLight(root_, runtime_);
    return prepareProduction(runtime_.getBuilder());
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

    runtime_.init(parse_, null);
    Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_, int level_) {
    return foo(runtime_, level_ + 1);
  }

  /* ********************************************************** */
  // a
  public static boolean bar(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "bar")) return false;
    if (!nextTokenIs(runtime_, A)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    exit_section_(runtime_, marker_, BAR, result_);
    return result_;
  }

  /* ********************************************************** */
  // a 
  //   | b c d
  //   | c d e
  //   | bar d e f
  static boolean foo(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "foo")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, A);
    if (!result_) result_ = parseTokens(runtime_, 2, B, C, D);
    if (!result_) result_ = parseTokens(runtime_, 2, C, D, E);
    if (!result_) result_ = foo_3(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // bar d e f
  private static boolean foo_3(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "foo_3")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = bar(runtime_, level_ + 1);
    result_ = result_ && consumeTokens(runtime_, 1, D, E, F);
    pinned_ = result_; // pin = 2
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

}