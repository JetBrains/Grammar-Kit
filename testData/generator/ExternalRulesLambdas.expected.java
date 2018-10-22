// ---- ExternalRulesLambdas.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRulesLambdas implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == COLLAPSE_ONE) {
      result_ = collapse_one(builder_, 0);
    }
    else if (root_ == COLLAPSE_TWO) {
      result_ = collapse_two(builder_, 0);
    }
    else if (root_ == ONE) {
      result_ = one(builder_, 0);
    }
    else if (root_ == STATEMENT) {
      result_ = statement(builder_, 0);
    }
    else if (root_ == TWO) {
      result_ = two(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return root(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(COLLAPSE_ONE, COLLAPSE_TWO),
  };

  /* ********************************************************** */
  // <<uniqueListOf one>>
  public static boolean collapse_one(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "collapse_one")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _COLLAPSE_, COLLAPSE_ONE, "<collapse one>");
    result_ = uniqueListOf(builder_, level_ + 1, ExternalRulesLambdas::one);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  public static boolean collapse_two(PsiBuilder builder_, int level_) {
    Marker marker_ = enter_section_(builder_);
    exit_section_(builder_, marker_, COLLAPSE_TWO, true);
    return true;
  }

  /* ********************************************************** */
  static Parser comma_list_$(Parser param) {
    return (builder_, level_) -> comma_list(builder_, level_ + 1, param);
  }

  // <<param>> (',' <<param>>) *
  public static boolean comma_list(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = param.parse(builder_, level_);
    result_ = result_ && comma_list_1(builder_, level_ + 1, param);
    exit_section_(builder_, marker_, COMMA_LIST, result_);
    return result_;
  }

  // (',' <<param>>) *
  private static boolean comma_list_1(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!comma_list_1_0(builder_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(builder_, "comma_list_1", pos_)) break;
    }
    return true;
  }

  // ',' <<param>>
  private static boolean comma_list_1_0(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && param.parse(builder_, level_);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  static Parser comma_list_pinned_$(Parser head, Parser param) {
    return (builder_, level_) -> comma_list_pinned(builder_, level_ + 1, head, param);
  }

  // <<head>> <<param>> (<<comma_list_tail <<param>>>>) *
  public static boolean comma_list_pinned(PsiBuilder builder_, int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_pinned")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = head.parse(builder_, level_);
    result_ = result_ && param.parse(builder_, level_);
    result_ = result_ && comma_list_pinned_2(builder_, level_ + 1, param);
    exit_section_(builder_, marker_, COMMA_LIST_PINNED, result_);
    return result_;
  }

  // (<<comma_list_tail <<param>>>>) *
  private static boolean comma_list_pinned_2(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_pinned_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!comma_list_pinned_2_0(builder_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(builder_, "comma_list_pinned_2", pos_)) break;
    }
    return true;
  }

  // <<comma_list_tail <<param>>>>
  private static boolean comma_list_pinned_2_0(PsiBuilder builder_, int level_, Parser param) {
    return comma_list_tail(builder_, level_ + 1, param);
  }

  /* ********************************************************** */
  // ',' <<param>>
  public static boolean comma_list_tail(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_tail")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, COMMA_LIST_TAIL, null);
    result_ = consumeToken(builder_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && param.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // empty_external
  static boolean empty_external_usage(PsiBuilder builder_, int level_) {
    return null(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // <<>>
  static boolean empty_external_usage2(PsiBuilder builder_, int level_) {
    return true;
  }

  /* ********************************************************** */
  // !(',' | ';' | ')')
  static boolean item_recover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "item_recover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !item_recover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ',' | ';' | ')'
  private static boolean item_recover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "item_recover_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    if (!result_) result_ = consumeToken(builder_, ";");
    if (!result_) result_ = consumeToken(builder_, ")");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<head>> <<comma_list <<param>>>> (<<comma_list_tail <<comma_list <<param>>>>>>) *
  public static boolean list_of_lists(PsiBuilder builder_, int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "list_of_lists")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = head.parse(builder_, level_);
    result_ = result_ && comma_list(builder_, level_ + 1, param);
    result_ = result_ && list_of_lists_2(builder_, level_ + 1, param);
    exit_section_(builder_, marker_, LIST_OF_LISTS, result_);
    return result_;
  }

  // (<<comma_list_tail <<comma_list <<param>>>>>>) *
  private static boolean list_of_lists_2(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "list_of_lists_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!list_of_lists_2_0(builder_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(builder_, "list_of_lists_2", pos_)) break;
    }
    return true;
  }

  // <<comma_list_tail <<comma_list <<param>>>>>>
  private static boolean list_of_lists_2_0(PsiBuilder builder_, int level_, Parser param) {
    return comma_list_tail(builder_, level_ + 1, comma_list_$(param));
  }

  /* ********************************************************** */
  static Parser main_class_meta_$(Parser p) {
    return (builder_, level_) -> main_class_meta(builder_, level_ + 1, p);
  }

  // <<p>>
  static boolean main_class_meta(PsiBuilder builder_, int level_, Parser p) {
    return p.parse(builder_, level_);
  }

  /* ********************************************************** */
  // <<listOf "1+2" '1+2' <<param>>>>
  static boolean meta_mixed(PsiBuilder builder_, int level_, Parser param) {
    return listOf(builder_, level_ + 1, "1+2", 1+2, param);
  }

  /* ********************************************************** */
  // <<meta_mixed <<comma_list one>>>>
  static boolean meta_mixed_list(PsiBuilder builder_, int level_) {
    return meta_mixed(builder_, level_ + 1, meta_mixed_list_0_0_parser_);
  }

  /* ********************************************************** */
  // <<meta_mixed (<<comma_list one>>)>>
  static boolean meta_mixed_list_paren(PsiBuilder builder_, int level_) {
    return meta_mixed(builder_, level_ + 1, ExternalRulesLambdas::meta_mixed_list_paren_0_0);
  }

  // <<comma_list one>>
  private static boolean meta_mixed_list_paren_0_0(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, ExternalRulesLambdas::one);
  }

  /* ********************************************************** */
  // <<meta_mixed statement>>
  static boolean meta_mixed_simple(PsiBuilder builder_, int level_) {
    return meta_mixed(builder_, level_ + 1, ExternalRulesLambdas::statement);
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
  public static boolean meta_multi_level(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comma_list(builder_, level_ + 1, comma_list_$(comma_list_$(comma_list_$(comma_list_$(param)))));
    exit_section_(builder_, marker_, META_MULTI_LEVEL, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list <<comma_list some>>>>>>
  static boolean meta_multi_level_no_closure(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, meta_multi_level_no_closure_0_0_parser_);
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
  public static boolean meta_multi_level_pinned(PsiBuilder builder_, int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comma_list(builder_, level_ + 1, comma_list_pinned_$(head, comma_list_$(comma_list_$(comma_list_$(param)))));
    exit_section_(builder_, marker_, META_MULTI_LEVEL_PINNED, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> (<<comma_list <<comma_list <<comma_list <<param>>>>>>>>)>>>>
  public static boolean meta_multi_level_pinned_paren(PsiBuilder builder_, int level_, Parser head, Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned_paren")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comma_list(builder_, level_ + 1, comma_list_pinned_$(head, meta_multi_level_pinned_paren_0_0_1_$(param)));
    exit_section_(builder_, marker_, META_MULTI_LEVEL_PINNED_PAREN, result_);
    return result_;
  }

  private static Parser meta_multi_level_pinned_paren_0_0_1_$(Parser param) {
    return (builder_, level_) -> meta_multi_level_pinned_paren_0_0_1(builder_, level_ + 1, param);
  }

  // <<comma_list <<comma_list <<comma_list <<param>>>>>>>>
  private static boolean meta_multi_level_pinned_paren_0_0_1(PsiBuilder builder_, int level_, Parser param) {
    return comma_list(builder_, level_ + 1, comma_list_$(comma_list_$(param)));
  }

  /* ********************************************************** */
  // <<comma_list_pinned one (one | two)>>
  static boolean meta_seq(PsiBuilder builder_, int level_) {
    return comma_list_pinned(builder_, level_ + 1, ExternalRulesLambdas::one, ExternalRulesLambdas::meta_seq_0_1);
  }

  // one | two
  private static boolean meta_seq_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_0_1")) return false;
    boolean result_;
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // <<list_of_lists one (one | two)>>
  static boolean meta_seq_of_lists(PsiBuilder builder_, int level_) {
    return list_of_lists(builder_, level_ + 1, ExternalRulesLambdas::one, ExternalRulesLambdas::meta_seq_of_lists_0_1);
  }

  // one | two
  private static boolean meta_seq_of_lists_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_0_1")) return false;
    boolean result_;
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // (<<list_of_lists one (one | two)>>)?
  static boolean meta_seq_of_lists_opt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt")) return false;
    meta_seq_of_lists_opt_0(builder_, level_ + 1);
    return true;
  }

  // <<list_of_lists one (one | two)>>
  private static boolean meta_seq_of_lists_opt_0(PsiBuilder builder_, int level_) {
    return list_of_lists(builder_, level_ + 1, ExternalRulesLambdas::one, ExternalRulesLambdas::meta_seq_of_lists_opt_0_0_1);
  }

  // one | two
  private static boolean meta_seq_of_lists_opt_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt_0_0_1")) return false;
    boolean result_;
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list one>>
  static boolean meta_simple(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, ExternalRulesLambdas::one);
  }

  /* ********************************************************** */
  // <<comma_list (<<param>> | some)>>
  public static boolean meta_with_in_place(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_with_in_place")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comma_list(builder_, level_ + 1, meta_with_in_place_0_0_$(param));
    exit_section_(builder_, marker_, META_WITH_IN_PLACE, result_);
    return result_;
  }

  private static Parser meta_with_in_place_0_0_$(Parser param) {
    return (builder_, level_) -> meta_with_in_place_0_0(builder_, level_ + 1, param);
  }

  // <<param>> | some
  private static boolean meta_with_in_place_0_0(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_with_in_place_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = param.parse(builder_, level_);
    if (!result_) result_ = consumeToken(builder_, SOME);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<meta_multi_level one>>
  static boolean multi_level(PsiBuilder builder_, int level_) {
    return meta_multi_level(builder_, level_ + 1, ExternalRulesLambdas::one);
  }

  /* ********************************************************** */
  // <<two_params_meta <<nested1>> <<two_params_meta <<nested2>> <<nested3>>>>>>
  static boolean nested_meta(PsiBuilder builder_, int level_, Parser nested1, Parser nested2, Parser nested3) {
    return two_params_meta(builder_, level_ + 1, nested1, two_params_meta_$(nested2, nested3));
  }

  /* ********************************************************** */
  // <<two_params_meta (<<two_params_meta '%' <<c>>>>) perc_re>>
  static boolean nested_mixed(PsiBuilder builder_, int level_, Parser c) {
    return two_params_meta(builder_, level_ + 1, nested_mixed_0_0_$(c), PERC_RE_parser_);
  }

  private static Parser nested_mixed_0_0_$(Parser c) {
    return (builder_, level_) -> nested_mixed_0_0(builder_, level_ + 1, c);
  }

  // <<two_params_meta '%' <<c>>>>
  private static boolean nested_mixed_0_0(PsiBuilder builder_, int level_, Parser c) {
    return two_params_meta(builder_, level_ + 1, perc_parser_, c);
  }

  /* ********************************************************** */
  // 'one'
  public static boolean one(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "one")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ONE, "<one>");
    result_ = consumeToken(builder_, "one");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf (one | two | 10 | some)>> '}'
  static boolean param_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRulesLambdas::param_choice_1_0);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // one | two | 10 | some
  private static boolean param_choice_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf {one | two | 10 | some}>> '}'
  static boolean param_choice_alt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_alt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRulesLambdas::param_choice_alt_1_0);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // one | two | 10 | some
  private static boolean param_choice_alt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_alt_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf [one | two | 10 | some]>> '}'
  static boolean param_opt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_opt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRulesLambdas::param_opt_1_0);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [one | two | 10 | some]
  private static boolean param_opt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_opt_1_0")) return false;
    param_opt_1_0_0(builder_, level_ + 1);
    return true;
  }

  // one | two | 10 | some
  private static boolean param_opt_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_opt_1_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf "1+1" '1+1' one two 10 some>> '}'
  static boolean param_seq(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, "1+1", 1+1, ExternalRulesLambdas::one, ExternalRulesLambdas::two, 10, SOME_parser_);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf {one | two} [10 | some]>> '}'
  static boolean param_seq_alt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRulesLambdas::param_seq_alt_1_0, ExternalRulesLambdas::param_seq_alt_1_1);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // one | two
  private static boolean param_seq_alt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_1_0")) return false;
    boolean result_;
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    return result_;
  }

  // [10 | some]
  private static boolean param_seq_alt_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_1_1")) return false;
    param_seq_alt_1_1_0(builder_, level_ + 1);
    return true;
  }

  // 10 | some
  private static boolean param_seq_alt_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_1_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<unique_list_of one two>> '}'
  static boolean param_seq_alt_ext(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_ext")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRulesLambdas::one, ExternalRulesLambdas::two);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' <<unique_list_of_params one !two>> '}'
  static boolean param_seq_alt_params_ext(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_params_ext")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, ExternalRulesLambdas::one, "1+1", ExternalRulesLambdas::param_seq_alt_params_ext_1_1, 1+1);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // !two
  private static boolean param_seq_alt_params_ext_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_params_ext_1_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !two(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // <<listOf '%'>>
  static boolean perc_list(PsiBuilder builder_, int level_) {
    return listOf(builder_, level_ + 1, perc_parser_);
  }

  /* ********************************************************** */
  // <<listOf perc_re>>
  static boolean perc_re_list1(PsiBuilder builder_, int level_) {
    return listOf(builder_, level_ + 1, PERC_RE_parser_);
  }

  /* ********************************************************** */
  // <<listOf (perc_re)>>
  static boolean perc_re_list2(PsiBuilder builder_, int level_) {
    return listOf(builder_, level_ + 1, perc_re_list2_0_0_parser_);
  }

  /* ********************************************************** */
  // <<param>>
  static boolean recoverable_item(PsiBuilder builder_, int level_, Parser param) {
    if (!recursion_guard_(builder_, level_, "recoverable_item")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = param.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, result_, false, ExternalRulesLambdas::item_recover);
    return result_;
  }

  /* ********************************************************** */
  // <<param>>
  static boolean recoverable_item2(PsiBuilder builder_, int level_, Parser param, Parser recover_arg) {
    if (!recursion_guard_(builder_, level_, "recoverable_item2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = param.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, result_, false, recover_arg);
    return result_;
  }

  /* ********************************************************** */
  // <<recover_arg>> <<param>>
  static boolean recoverable_item3(PsiBuilder builder_, int level_, Parser recover_arg, Parser param) {
    if (!recursion_guard_(builder_, level_, "recoverable_item3")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = recover_arg.parse(builder_, level_);
    pinned_ = result_; // pin = 1
    result_ = result_ && param.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, result_, pinned_, recover_arg);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // <<listOf statement>>
  static boolean root(PsiBuilder builder_, int level_) {
    return listOf(builder_, level_ + 1, ExternalRulesLambdas::statement);
  }

  /* ********************************************************** */
  // <<comma_list <<second_class_meta some>>>>
  static boolean second_class_meta_usage_from_main(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, second_class_meta_usage_from_main_0_0_parser_);
  }

  /* ********************************************************** */
  // one | two
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STATEMENT, "<statement>");
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // 'two'
  public static boolean two(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "two")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TWO, "<two>");
    result_ = consumeToken(builder_, "two");
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  static Parser two_params_meta_$(Parser a, Parser b) {
    return (builder_, level_) -> two_params_meta(builder_, level_ + 1, a, b);
  }

  // <<a>> <<b>>
  public static boolean two_params_meta(PsiBuilder builder_, int level_, Parser a, Parser b) {
    if (!recursion_guard_(builder_, level_, "two_params_meta")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = a.parse(builder_, level_);
    result_ = result_ && b.parse(builder_, level_);
    exit_section_(builder_, marker_, TWO_PARAMS_META, result_);
    return result_;
  }

  static final Parser PERC_RE_parser_ = (builder_, level_) -> consumeToken(builder_, PERC_RE);
  static final Parser SOME_parser_ = (builder_, level_) -> consumeToken(builder_, SOME);
  static final Parser perc_parser_ = (builder_, level_) -> consumeToken(builder_, PERC);
  static final Parser perc_re_list2_0_0_parser_ = PERC_RE_parser_;
  private static final Parser meta_mixed_list_0_0_parser_ = comma_list_$(ExternalRulesLambdas::one);
  private static final Parser meta_multi_level_no_closure_0_0_0_parser_ = comma_list_$(SOME_parser_);
  private static final Parser meta_multi_level_no_closure_0_0_parser_ = comma_list_$(meta_multi_level_no_closure_0_0_0_parser_);
  private static final Parser second_class_meta_usage_from_main_0_0_parser_ = ExternalRulesLambdas2.second_class_meta_$(SOME_parser_);
}
// ---- ExternalRulesLambdas2.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static ExternalRulesLambdas.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRulesLambdas2 {

  /* ********************************************************** */
  // <<comma_list <<main_class_meta some>>>>
  static boolean main_class_meta_usage_from_second(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, main_class_meta_usage_from_second_0_0_parser_);
  }

  /* ********************************************************** */
  // <<listOf one>>
  static boolean one_list(PsiBuilder builder_, int level_) {
    return listOf(builder_, level_ + 1, ExternalRulesLambdas::one);
  }

  /* ********************************************************** */
  // <<listOf (one)>>
  static boolean one_list_par(PsiBuilder builder_, int level_) {
    return listOf(builder_, level_ + 1, ExternalRulesLambdas2::one_list_par_0_0);
  }

  // (one)
  private static boolean one_list_par_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "one_list_par_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  static Parser second_class_meta_$(Parser bmp) {
    return (builder_, level_) -> second_class_meta(builder_, level_ + 1, bmp);
  }

  // <<bmp>>
  static boolean second_class_meta(PsiBuilder builder_, int level_, Parser bmp) {
    return bmp.parse(builder_, level_);
  }

  /* ********************************************************** */
  // <<comma_list <<third_class_meta some>>>>
  static boolean third_class_meta_usage_from_second(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, third_class_meta_usage_from_second_0_0_parser_);
  }

  private static final Parser main_class_meta_usage_from_second_0_0_parser_ = main_class_meta_$(ExternalRulesLambdas.SOME_parser_);
  private static final Parser third_class_meta_usage_from_second_0_0_parser_ = ExternalRulesLambdas3.third_class_meta_$(ExternalRulesLambdas.SOME_parser_);
}
// ---- ExternalRulesLambdas3.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static ExternalRulesLambdas.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRulesLambdas3 {

  /* ********************************************************** */
  // <<comma_list <<second_class_meta some>>>>
  static boolean second_class_meta_usage_from_third(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, second_class_meta_usage_from_third_0_0_parser_);
  }

  /* ********************************************************** */
  static Parser third_class_meta_$(Parser fmp) {
    return (builder_, level_) -> third_class_meta(builder_, level_ + 1, fmp);
  }

  // <<fmp>>
  static boolean third_class_meta(PsiBuilder builder_, int level_, Parser fmp) {
    return fmp.parse(builder_, level_);
  }

  private static final Parser second_class_meta_usage_from_third_0_0_parser_ = ExternalRulesLambdas2.second_class_meta_$(ExternalRulesLambdas.SOME_parser_);
}