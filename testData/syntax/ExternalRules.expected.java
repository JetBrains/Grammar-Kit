// ---- ExternalRules.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static org.intellij.grammar.test.ParserUtil.*;
import static com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntimeKt.*;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.parser.ProductionResult;
import com.intellij.platform.syntax.SyntaxElementTypeSet;
import static com.intellij.platform.syntax.parser.ProductionResultKt.prepareProduction;
import kotlin.jvm.functions.Function2;
import kotlin.Unit;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRules {

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

    runtime_.init(parse_, EXTENDS_SETS_);
    Marker marker_ = enter_section_(runtime_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, runtime_);
    exit_section_(runtime_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_) {
    return parse_root_(root_, runtime_, 0);
  }

  static boolean parse_root_(SyntaxElementType root_, SyntaxGeneratedParserRuntime runtime_, int level_) {
    boolean result_;
    if (root_ == EXTRA_ROOT) {
      result_ = ExternalRules2.extra_root(runtime_, level_ + 1);
    }
    else {
      result_ = root(runtime_, level_ + 1);
    }
    return result_;
  }

  public static final SyntaxElementTypeSet[] EXTENDS_SETS_ = new SyntaxElementTypeSet[] {
    create_token_set_(COLLAPSE_ONE, COLLAPSE_TWO),
  };

  /* ********************************************************** */
  // <<uniqueListOf one>>
  public static boolean collapse_one(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "collapse_one")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _COLLAPSE_, COLLAPSE_ONE, "<collapse one>");
    result_ = uniqueListOf(runtime_, level_ + 1, ExternalRules::one);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  public static boolean collapse_two(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, COLLAPSE_TWO, true);
    return true;
  }

  /* ********************************************************** */
  static SyntaxGeneratedParserRuntime.Parser comma_list_$(SyntaxGeneratedParserRuntime.Parser param) {
    return (runtime_, level_) -> comma_list(runtime_, level_ + 1, param);
  }

  // <<param>> (',' <<param>>) *
  public static boolean comma_list(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = param.parse(runtime_, level_);
    result_ = result_ && comma_list_1(runtime_, level_ + 1, param);
    exit_section_(runtime_, marker_, COMMA_LIST, result_);
    return result_;
  }

  // (',' <<param>>) *
  private static boolean comma_list_1(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_list_1")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!comma_list_1_0(runtime_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(runtime_, "comma_list_1", pos_)) break;
    }
    return true;
  }

  // ',' <<param>>
  private static boolean comma_list_1_0(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, COMMA);
    result_ = result_ && param.parse(runtime_, level_);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  static SyntaxGeneratedParserRuntime.Parser comma_list_pinned_$(SyntaxGeneratedParserRuntime.Parser head, SyntaxGeneratedParserRuntime.Parser param) {
    return (runtime_, level_) -> comma_list_pinned(runtime_, level_ + 1, head, param);
  }

  // <<head>> <<param>> (<<comma_list_tail <<param>>>>) *
  public static boolean comma_list_pinned(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser head, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_list_pinned")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = head.parse(runtime_, level_);
    result_ = result_ && param.parse(runtime_, level_);
    result_ = result_ && comma_list_pinned_2(runtime_, level_ + 1, param);
    exit_section_(runtime_, marker_, COMMA_LIST_PINNED, result_);
    return result_;
  }

  // (<<comma_list_tail <<param>>>>) *
  private static boolean comma_list_pinned_2(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_list_pinned_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!comma_list_pinned_2_0(runtime_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(runtime_, "comma_list_pinned_2", pos_)) break;
    }
    return true;
  }

  // <<comma_list_tail <<param>>>>
  private static boolean comma_list_pinned_2_0(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    return comma_list_tail(runtime_, level_ + 1, param);
  }

  /* ********************************************************** */
  // ',' <<param>>
  public static boolean comma_list_tail(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_list_tail")) return false;
    if (!nextTokenIs(runtime_, COMMA)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, COMMA_LIST_TAIL, null);
    result_ = consumeToken(runtime_, COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && param.parse(runtime_, level_);
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '(' <<param>> (',' <<param>>) * ')'
  static boolean comma_paren_list(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_paren_list")) return false;
    if (!nextTokenIs(runtime_, PAREN1)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = consumeToken(runtime_, PAREN1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(runtime_, param.parse(runtime_, level_));
    result_ = pinned_ && report_error_(runtime_, comma_paren_list_2(runtime_, level_ + 1, param)) && result_;
    result_ = pinned_ && consumeToken(runtime_, PAREN2) && result_;
    exit_section_(runtime_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // (',' <<param>>) *
  private static boolean comma_paren_list_2(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_paren_list_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!comma_paren_list_2_0(runtime_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(runtime_, "comma_paren_list_2", pos_)) break;
    }
    return true;
  }

  // ',' <<param>>
  private static boolean comma_paren_list_2_0(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "comma_paren_list_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, COMMA);
    result_ = result_ && param.parse(runtime_, level_);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // empty_external
  static boolean empty_external_usage(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return null(runtime_, level_ + 1);
  }

  /* ********************************************************** */
  // <<>>
  static boolean empty_external_usage2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // !(',' | ';' | ')')
  static boolean item_recover(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "item_recover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !item_recover_0(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  // ',' | ';' | ')'
  private static boolean item_recover_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "item_recover_0")) return false;
    boolean result_;
    result_ = consumeToken(runtime_, COMMA);
    if (!result_) result_ = consumeToken(runtime_, ";");
    if (!result_) result_ = consumeToken(runtime_, PAREN2);
    return result_;
  }

  /* ********************************************************** */
  // <<head>> <<comma_list <<param>>>> (<<comma_list_tail <<comma_list <<param>>>>>>) *
  public static boolean list_of_lists(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser head, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "list_of_lists")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = head.parse(runtime_, level_);
    result_ = result_ && comma_list(runtime_, level_ + 1, param);
    result_ = result_ && list_of_lists_2(runtime_, level_ + 1, param);
    exit_section_(runtime_, marker_, LIST_OF_LISTS, result_);
    return result_;
  }

  // (<<comma_list_tail <<comma_list <<param>>>>>>) *
  private static boolean list_of_lists_2(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "list_of_lists_2")) return false;
    while (true) {
      int pos_ = current_position_(runtime_);
      if (!list_of_lists_2_0(runtime_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(runtime_, "list_of_lists_2", pos_)) break;
    }
    return true;
  }

  // <<comma_list_tail <<comma_list <<param>>>>>>
  private static boolean list_of_lists_2_0(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    return comma_list_tail(runtime_, level_ + 1, comma_list_$(param));
  }

  /* ********************************************************** */
  static SyntaxGeneratedParserRuntime.Parser main_class_meta_$(SyntaxGeneratedParserRuntime.Parser p) {
    return (runtime_, level_) -> main_class_meta(runtime_, level_ + 1, p);
  }

  // <<p>>
  static boolean main_class_meta(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser p) {
    return p.parse(runtime_, level_);
  }

  /* ********************************************************** */
  // <<listOf "1+2" '1+2' <<param>>>>
  static boolean meta_mixed(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    return listOf(runtime_, level_ + 1, "1+2", 1+2, param);
  }

  /* ********************************************************** */
  // <<meta_mixed <<comma_list one>>>>
  static boolean meta_mixed_list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return meta_mixed(runtime_, level_ + 1, meta_mixed_list_0_0_parser_);
  }

  /* ********************************************************** */
  // <<meta_mixed (<<comma_list one>>)>>
  static boolean meta_mixed_list_paren(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return meta_mixed(runtime_, level_ + 1, ExternalRules::meta_mixed_list_paren_0_0);
  }

  // <<comma_list one>>
  private static boolean meta_mixed_list_paren_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list(runtime_, level_ + 1, ExternalRules::one);
  }

  /* ********************************************************** */
  // <<meta_mixed statement>>
  static boolean meta_mixed_simple(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return meta_mixed(runtime_, level_ + 1, ExternalRules::statement);
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
  public static boolean meta_multi_level(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "meta_multi_level")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = comma_list(runtime_, level_ + 1, comma_list_$(comma_list_$(comma_list_$(comma_list_$(param)))));
    exit_section_(runtime_, marker_, META_MULTI_LEVEL, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list <<comma_list some>>>>>>
  static boolean meta_multi_level_no_closure(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list(runtime_, level_ + 1, meta_multi_level_no_closure_0_0_parser_);
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
  public static boolean meta_multi_level_pinned(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser head, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "meta_multi_level_pinned")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = comma_list(runtime_, level_ + 1, comma_list_pinned_$(head, comma_list_$(comma_list_$(comma_list_$(param)))));
    exit_section_(runtime_, marker_, META_MULTI_LEVEL_PINNED, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> (<<comma_list <<comma_list <<comma_list <<param>>>>>>>>)>>>>
  public static boolean meta_multi_level_pinned_paren(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser head, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "meta_multi_level_pinned_paren")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = comma_list(runtime_, level_ + 1, comma_list_pinned_$(head, meta_multi_level_pinned_paren_0_0_1_$(param)));
    exit_section_(runtime_, marker_, META_MULTI_LEVEL_PINNED_PAREN, result_);
    return result_;
  }

  private static SyntaxGeneratedParserRuntime.Parser meta_multi_level_pinned_paren_0_0_1_$(SyntaxGeneratedParserRuntime.Parser param) {
    return (runtime_, level_) -> meta_multi_level_pinned_paren_0_0_1(runtime_, level_ + 1, param);
  }

  // <<comma_list <<comma_list <<comma_list <<param>>>>>>>>
  private static boolean meta_multi_level_pinned_paren_0_0_1(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    return comma_list(runtime_, level_ + 1, comma_list_$(comma_list_$(param)));
  }

  /* ********************************************************** */
  // <<comma_list_pinned one (one | two)>>
  static boolean meta_seq(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list_pinned(runtime_, level_ + 1, ExternalRules::one, ExternalRules::meta_seq_0_1);
  }

  // one | two
  private static boolean meta_seq_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "meta_seq_0_1")) return false;
    boolean result_;
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // <<list_of_lists one (one | two)>>
  static boolean meta_seq_of_lists(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return list_of_lists(runtime_, level_ + 1, ExternalRules::one, ExternalRules::meta_seq_of_lists_0_1);
  }

  // one | two
  private static boolean meta_seq_of_lists_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "meta_seq_of_lists_0_1")) return false;
    boolean result_;
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // (<<list_of_lists one (one | two)>>)?
  static boolean meta_seq_of_lists_opt(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "meta_seq_of_lists_opt")) return false;
    meta_seq_of_lists_opt_0(runtime_, level_ + 1);
    return true;
  }

  // <<list_of_lists one (one | two)>>
  private static boolean meta_seq_of_lists_opt_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return list_of_lists(runtime_, level_ + 1, ExternalRules::one, ExternalRules::meta_seq_of_lists_opt_0_0_1);
  }

  // one | two
  private static boolean meta_seq_of_lists_opt_0_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "meta_seq_of_lists_opt_0_0_1")) return false;
    boolean result_;
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list one>>
  static boolean meta_simple(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list(runtime_, level_ + 1, ExternalRules::one);
  }

  /* ********************************************************** */
  // <<comma_list (<<param>> | some)>>
  public static boolean meta_with_in_place(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "meta_with_in_place")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = comma_list(runtime_, level_ + 1, meta_with_in_place_0_0_$(param));
    exit_section_(runtime_, marker_, META_WITH_IN_PLACE, result_);
    return result_;
  }

  private static SyntaxGeneratedParserRuntime.Parser meta_with_in_place_0_0_$(SyntaxGeneratedParserRuntime.Parser param) {
    return (runtime_, level_) -> meta_with_in_place_0_0(runtime_, level_ + 1, param);
  }

  // <<param>> | some
  private static boolean meta_with_in_place_0_0(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "meta_with_in_place_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = param.parse(runtime_, level_);
    if (!result_) result_ = consumeToken(runtime_, SOME);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<meta_multi_level one>>
  static boolean multi_level(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return meta_multi_level(runtime_, level_ + 1, ExternalRules::one);
  }

  /* ********************************************************** */
  // <<two_params_meta <<nested1>> <<two_params_meta <<nested2>> <<nested3>>>>>>
  static boolean nested_meta(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser nested1, SyntaxGeneratedParserRuntime.Parser nested2, SyntaxGeneratedParserRuntime.Parser nested3) {
    return two_params_meta(runtime_, level_ + 1, nested1, two_params_meta_$(nested2, nested3));
  }

  /* ********************************************************** */
  // <<two_params_meta (<<two_params_meta '%' <<c>>>>) perc_re>>
  static boolean nested_mixed(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser c) {
    return two_params_meta(runtime_, level_ + 1, nested_mixed_0_0_$(c), PERC_RE_parser_);
  }

  private static SyntaxGeneratedParserRuntime.Parser nested_mixed_0_0_$(SyntaxGeneratedParserRuntime.Parser c) {
    return (runtime_, level_) -> nested_mixed_0_0(runtime_, level_ + 1, c);
  }

  // <<two_params_meta '%' <<c>>>>
  private static boolean nested_mixed_0_0(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser c) {
    return two_params_meta(runtime_, level_ + 1, perc_parser_, c);
  }

  /* ********************************************************** */
  // 'one'
  public static boolean one(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "one")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, ONE, "<one>");
    result_ = consumeToken(runtime_, "one");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf (one | two | 10 | some)>> '}'
  static boolean param_choice(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "{");
    result_ = result_ && uniqueListOf(runtime_, level_ + 1, ExternalRules::param_choice_1_0);
    result_ = result_ && consumeToken(runtime_, "}");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // one | two | 10 | some
  private static boolean param_choice_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_choice_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    if (!result_) result_ = consumeToken(runtime_, "10");
    if (!result_) result_ = consumeToken(runtime_, SOME);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf {one | two | 10 | some}>> '}'
  static boolean param_choice_alt(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_choice_alt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "{");
    result_ = result_ && uniqueListOf(runtime_, level_ + 1, ExternalRules::param_choice_alt_1_0);
    result_ = result_ && consumeToken(runtime_, "}");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // one | two | 10 | some
  private static boolean param_choice_alt_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_choice_alt_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    if (!result_) result_ = consumeToken(runtime_, "10");
    if (!result_) result_ = consumeToken(runtime_, SOME);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf [one | two | 10 | some]>> '}'
  static boolean param_opt(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_opt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "{");
    result_ = result_ && uniqueListOf(runtime_, level_ + 1, ExternalRules::param_opt_1_0);
    result_ = result_ && consumeToken(runtime_, "}");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // [one | two | 10 | some]
  private static boolean param_opt_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_opt_1_0")) return false;
    param_opt_1_0_0(runtime_, level_ + 1);
    return true;
  }

  // one | two | 10 | some
  private static boolean param_opt_1_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_opt_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    if (!result_) result_ = consumeToken(runtime_, "10");
    if (!result_) result_ = consumeToken(runtime_, SOME);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf "1+1" '1+1' one two 10 some>> '}'
  static boolean param_seq(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "{");
    result_ = result_ && uniqueListOf(runtime_, level_ + 1, "1+1", 1+1, ExternalRules::one, ExternalRules::two, 10, SOME_parser_);
    result_ = result_ && consumeToken(runtime_, "}");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf {one | two} [10 | some]>> '}'
  static boolean param_seq_alt(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq_alt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "{");
    result_ = result_ && uniqueListOf(runtime_, level_ + 1, ExternalRules::param_seq_alt_1_0, ExternalRules::param_seq_alt_1_1);
    result_ = result_ && consumeToken(runtime_, "}");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // one | two
  private static boolean param_seq_alt_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq_alt_1_0")) return false;
    boolean result_;
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    return result_;
  }

  // [10 | some]
  private static boolean param_seq_alt_1_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq_alt_1_1")) return false;
    param_seq_alt_1_1_0(runtime_, level_ + 1);
    return true;
  }

  // 10 | some
  private static boolean param_seq_alt_1_1_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq_alt_1_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "10");
    if (!result_) result_ = consumeToken(runtime_, SOME);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<unique_list_of one two>> '}'
  static boolean param_seq_alt_ext(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq_alt_ext")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "{");
    result_ = result_ && uniqueListOf(runtime_, level_ + 1, ExternalRules::one, ExternalRules::two);
    result_ = result_ && consumeToken(runtime_, "}");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<unique_list_of_params one !two>> '}'
  static boolean param_seq_alt_params_ext(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq_alt_params_ext")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, "{");
    result_ = result_ && uniqueListOf(runtime_, level_ + 1, ExternalRules::one, "1+1", ExternalRules::param_seq_alt_params_ext_1_1, 1+1);
    result_ = result_ && consumeToken(runtime_, "}");
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // !two
  private static boolean param_seq_alt_params_ext_1_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "param_seq_alt_params_ext_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NOT_);
    result_ = !two(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<listOf '%'>>
  static boolean perc_list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return listOf(runtime_, level_ + 1, perc_parser_);
  }

  /* ********************************************************** */
  // <<listOf perc_re>>
  static boolean perc_re_list1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return listOf(runtime_, level_ + 1, PERC_RE_parser_);
  }

  /* ********************************************************** */
  // <<listOf (perc_re)>>
  static boolean perc_re_list2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return listOf(runtime_, level_ + 1, perc_re_list2_0_0_parser_);
  }

  /* ********************************************************** */
  // <<comma_paren_list (ref | '(' one ')')>>
  public static boolean public_paren_list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "public_paren_list")) return false;
    if (!nextTokenIs(runtime_, PAREN1)) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = comma_paren_list(runtime_, level_ + 1, ExternalRules::public_paren_list_0_0);
    exit_section_(runtime_, marker_, PUBLIC_PAREN_LIST, result_);
    return result_;
  }

  // ref | '(' one ')'
  private static boolean public_paren_list_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "public_paren_list_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = parseRef(runtime_, level_ + 1);
    if (!result_) result_ = public_paren_list_0_0_1(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  // '(' one ')'
  private static boolean public_paren_list_0_0_1(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "public_paren_list_0_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = consumeToken(runtime_, PAREN1);
    result_ = result_ && one(runtime_, level_ + 1);
    result_ = result_ && consumeToken(runtime_, PAREN2);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  public static boolean public_paren_list2(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, PUBLIC_PAREN_LIST, true);
    return true;
  }

  /* ********************************************************** */
  // <<param>>
  static boolean recoverable_item(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "recoverable_item")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = param.parse(runtime_, level_);
    exit_section_(runtime_, level_, marker_, result_, false, ExternalRules::item_recover);
    return result_;
  }

  /* ********************************************************** */
  // <<param>>
  static boolean recoverable_item2(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser param, SyntaxGeneratedParserRuntime.Parser recover_arg) {
    if (!recursion_guard_(runtime_, level_, "recoverable_item2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = param.parse(runtime_, level_);
    exit_section_(runtime_, level_, marker_, result_, false, recover_arg);
    return result_;
  }

  /* ********************************************************** */
  // <<recover_arg>> <<param>>
  static boolean recoverable_item3(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser recover_arg, SyntaxGeneratedParserRuntime.Parser param) {
    if (!recursion_guard_(runtime_, level_, "recoverable_item3")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_);
    result_ = recover_arg.parse(runtime_, level_);
    pinned_ = result_; // pin = 1
    result_ = result_ && param.parse(runtime_, level_);
    exit_section_(runtime_, level_, marker_, result_, pinned_, recover_arg);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // <<listOf statement>>
  static boolean root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return listOf(runtime_, level_ + 1, ExternalRules::statement);
  }

  /* ********************************************************** */
  // <<comma_list <<second_class_meta some>>>>
  static boolean second_class_meta_usage_from_main(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list(runtime_, level_ + 1, second_class_meta_usage_from_main_0_0_parser_);
  }

  /* ********************************************************** */
  // one | two
  public static boolean statement(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, STATEMENT, "<statement>");
    result_ = one(runtime_, level_ + 1);
    if (!result_) result_ = two(runtime_, level_ + 1);
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'two'
  public static boolean two(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "two")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_, level_, _NONE_, TWO, "<two>");
    result_ = consumeToken(runtime_, "two");
    exit_section_(runtime_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static SyntaxGeneratedParserRuntime.Parser two_params_meta_$(SyntaxGeneratedParserRuntime.Parser a, SyntaxGeneratedParserRuntime.Parser b) {
    return (runtime_, level_) -> two_params_meta(runtime_, level_ + 1, a, b);
  }

  // <<a>> <<b>>
  public static boolean two_params_meta(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser a, SyntaxGeneratedParserRuntime.Parser b) {
    if (!recursion_guard_(runtime_, level_, "two_params_meta")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = a.parse(runtime_, level_);
    result_ = result_ && b.parse(runtime_, level_);
    exit_section_(runtime_, marker_, TWO_PARAMS_META, result_);
    return result_;
  }

  static final SyntaxGeneratedParserRuntime.Parser PERC_RE_parser_ = (runtime_, level_) -> consumeToken(runtime_, PERC_RE);
  static final SyntaxGeneratedParserRuntime.Parser SOME_parser_ = (runtime_, level_) -> consumeToken(runtime_, SOME);
  static final SyntaxGeneratedParserRuntime.Parser perc_parser_ = (runtime_, level_) -> consumeToken(runtime_, PERC);
  static final SyntaxGeneratedParserRuntime.Parser perc_re_list2_0_0_parser_ = PERC_RE_parser_;

  private static final SyntaxGeneratedParserRuntime.Parser meta_mixed_list_0_0_parser_ = comma_list_$(ExternalRules::one);
  private static final SyntaxGeneratedParserRuntime.Parser meta_multi_level_no_closure_0_0_0_parser_ = comma_list_$(SOME_parser_);
  private static final SyntaxGeneratedParserRuntime.Parser meta_multi_level_no_closure_0_0_parser_ = comma_list_$(meta_multi_level_no_closure_0_0_0_parser_);
  private static final SyntaxGeneratedParserRuntime.Parser second_class_meta_usage_from_main_0_0_parser_ = ExternalRules2.second_class_meta_$(SOME_parser_);
}
// ---- ExternalRules2.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static org.intellij.grammar.test.ParserUtil.*;
import static com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntimeKt.*;
import static ExternalRules.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRules2 {

  /* ********************************************************** */
  public static boolean extra_root(SyntaxGeneratedParserRuntime runtime_, int level_) {
    Marker marker_ = enter_section_(runtime_);
    exit_section_(runtime_, marker_, EXTRA_ROOT, true);
    return true;
  }

  /* ********************************************************** */
  // <<comma_list <<main_class_meta some>>>>
  static boolean main_class_meta_usage_from_second(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list(runtime_, level_ + 1, main_class_meta_usage_from_second_0_0_parser_);
  }

  /* ********************************************************** */
  // <<listOf one>>
  static boolean one_list(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return listOf(runtime_, level_ + 1, ExternalRules::one);
  }

  /* ********************************************************** */
  // <<listOf (one)>>
  static boolean one_list_par(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return listOf(runtime_, level_ + 1, ExternalRules2::one_list_par_0_0);
  }

  // (one)
  private static boolean one_list_par_0_0(SyntaxGeneratedParserRuntime runtime_, int level_) {
    if (!recursion_guard_(runtime_, level_, "one_list_par_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(runtime_);
    result_ = one(runtime_, level_ + 1);
    exit_section_(runtime_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  static SyntaxGeneratedParserRuntime.Parser second_class_meta_$(SyntaxGeneratedParserRuntime.Parser bmp) {
    return (runtime_, level_) -> second_class_meta(runtime_, level_ + 1, bmp);
  }

  // <<bmp>>
  static boolean second_class_meta(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser bmp) {
    return bmp.parse(runtime_, level_);
  }

  /* ********************************************************** */
  // <<comma_list <<third_class_meta some>>>>
  static boolean third_class_meta_usage_from_second(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list(runtime_, level_ + 1, third_class_meta_usage_from_second_0_0_parser_);
  }

  private static final SyntaxGeneratedParserRuntime.Parser main_class_meta_usage_from_second_0_0_parser_ = main_class_meta_$(ExternalRules.SOME_parser_);
  private static final SyntaxGeneratedParserRuntime.Parser third_class_meta_usage_from_second_0_0_parser_ = ExternalRules3.third_class_meta_$(ExternalRules.SOME_parser_);
}
// ---- ExternalRules3.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntime;
import com.intellij.platform.syntax.parser.SyntaxTreeBuilder.Marker;
import static generated.GeneratedSyntaxElementTypes.*;
import static org.intellij.grammar.test.ParserUtil.*;
import static com.intellij.platform.syntax.runtime.SyntaxGeneratedParserRuntimeKt.*;
import static ExternalRules.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRules3 {

  /* ********************************************************** */
  // <<comma_list <<second_class_meta some>>>>
  static boolean second_class_meta_usage_from_third(SyntaxGeneratedParserRuntime runtime_, int level_) {
    return comma_list(runtime_, level_ + 1, second_class_meta_usage_from_third_0_0_parser_);
  }

  /* ********************************************************** */
  static SyntaxGeneratedParserRuntime.Parser third_class_meta_$(SyntaxGeneratedParserRuntime.Parser fmp) {
    return (runtime_, level_) -> third_class_meta(runtime_, level_ + 1, fmp);
  }

  // <<fmp>>
  static boolean third_class_meta(SyntaxGeneratedParserRuntime runtime_, int level_, SyntaxGeneratedParserRuntime.Parser fmp) {
    return fmp.parse(runtime_, level_);
  }

  private static final SyntaxGeneratedParserRuntime.Parser second_class_meta_usage_from_third_0_0_parser_ = ExternalRules2.second_class_meta_$(ExternalRules.SOME_parser_);
}