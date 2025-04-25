// ---- PsiAccessors.java -----------------
//header.txt
package ;

import com.intellij.platform.syntax.util.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static PsiGenUtil.*;
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
public class PsiAccessors {

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

    runtime_.init(parse_, null);
    Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_, int level_) {
    boolean result_;
    if (root_ == EXPRESSION) {
      result_ = expression(runtime_, level_ + 1);
    }
    else {
      result_ = root(runtime_, level_ + 1);
    }
    return result_;
  }

  /* ********************************************************** */
  // expression operator expression
  public static boolean binary(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "binary")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, BINARY, null);
    result_ = expression(runtime_, level_ + 1);
    result_ = result_ && operator(runtime_, level_ + 1);
    pinned_ = result_; // pin = operator
    result_ = result_ && expression(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // value '*' value
  public static boolean expression(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "expression")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = value(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, "*");
    result_ = result_ && value(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, EXPRESSION, result_);
    return result_;
  }

  /* ********************************************************** */
  // '+' | '-'
  public static boolean operator(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "operator")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, OPERATOR, "<operator>");
    result_ = consumeToken(runtime_, "+");
    if (!result_) result_ = consumeToken(runtime_, "-");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // binary
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return binary(runtime_, level_ + 1);
  }

  /* ********************************************************** */
  // id
  public static boolean value(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "value")) return false;
    if (!nextTokenIs(runtime_, ID)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, ID);
    exit_section_(runtime_, marker_, VALUE, result_);
    return result_;
  }

}