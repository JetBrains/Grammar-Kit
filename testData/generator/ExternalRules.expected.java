// ---- ExternalRules.java -----------------
// This is a generated file. Not intended for manual editing.
package ;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static generated.GeneratedTypes.*;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ExternalRules implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == ONE) {
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

  /* ********************************************************** */
  // <<param>> (',' <<param>>) *
  public static boolean comma_list(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = param.parse(builder_, level_);
    result_ = result_ && comma_list_1(builder_, level_ + 1, param);
    exit_section_(builder_, marker_, COMMA_LIST, result_);
    return result_;
  }

  // (',' <<param>>) *
  private static boolean comma_list_1(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!comma_list_1_0(builder_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(builder_, "comma_list_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' <<param>>
  private static boolean comma_list_1_0(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ",");
    result_ = result_ && param.parse(builder_, level_);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<head>> <<param>> (<<comma_list_tail <<param>>>>) *
  public static boolean comma_list_pinned(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
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
  private static boolean comma_list_pinned_2(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_pinned_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!comma_list_pinned_2_0(builder_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(builder_, "comma_list_pinned_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // <<comma_list_tail <<param>>>>
  private static boolean comma_list_pinned_2_0(PsiBuilder builder_, int level_, final Parser param) {
    return comma_list_tail(builder_, level_ + 1, param);
  }

  /* ********************************************************** */
  // ',' <<param>>
  public static boolean comma_list_tail(PsiBuilder builder_, int level_, final Parser param) {
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
  public static boolean list_of_lists(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
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
  private static boolean list_of_lists_2(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "list_of_lists_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!list_of_lists_2_0(builder_, level_ + 1, param)) break;
      if (!empty_element_parsed_guard_(builder_, "list_of_lists_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // <<comma_list_tail <<comma_list <<param>>>>>>
  private static boolean list_of_lists_2_0(PsiBuilder builder_, int level_, final Parser param) {
    return comma_list_tail(builder_, level_ + 1, new Parser() {
      public boolean parse(PsiBuilder builder_, int level_) {
        return comma_list(builder_, level_ + 1, param);
      }
    });
  }

  /* ********************************************************** */
  // <<listOf "1+2" '1+2' <<param>>>>
  static boolean meta_mixed(PsiBuilder builder_, int level_, final Parser param) {
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
    return meta_mixed(builder_, level_ + 1, meta_mixed_list_paren_0_0_parser_);
  }

  // <<comma_list one>>
  private static boolean meta_mixed_list_paren_0_0(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, one_parser_);
  }

  /* ********************************************************** */
  // <<meta_mixed statement>>
  static boolean meta_mixed_simple(PsiBuilder builder_, int level_) {
    return meta_mixed(builder_, level_ + 1, statement_parser_);
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
  public static boolean meta_multi_level(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comma_list(builder_, level_ + 1, new Parser() {
      public boolean parse(PsiBuilder builder_, int level_) {
        return comma_list(builder_, level_ + 1, new Parser() {
          public boolean parse(PsiBuilder builder_, int level_) {
            return comma_list(builder_, level_ + 1, new Parser() {
              public boolean parse(PsiBuilder builder_, int level_) {
                return comma_list(builder_, level_ + 1, new Parser() {
                  public boolean parse(PsiBuilder builder_, int level_) {
                    return comma_list(builder_, level_ + 1, param);
                  }
                });
              }
            });
          }
        });
      }
    });
    exit_section_(builder_, marker_, META_MULTI_LEVEL, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
  public static boolean meta_multi_level_pinned(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comma_list(builder_, level_ + 1, new Parser() {
      public boolean parse(PsiBuilder builder_, int level_) {
        return comma_list_pinned(builder_, level_ + 1, head, new Parser() {
          public boolean parse(PsiBuilder builder_, int level_) {
            return comma_list(builder_, level_ + 1, new Parser() {
              public boolean parse(PsiBuilder builder_, int level_) {
                return comma_list(builder_, level_ + 1, new Parser() {
                  public boolean parse(PsiBuilder builder_, int level_) {
                    return comma_list(builder_, level_ + 1, param);
                  }
                });
              }
            });
          }
        });
      }
    });
    exit_section_(builder_, marker_, META_MULTI_LEVEL_PINNED, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> (<<comma_list <<comma_list <<comma_list <<param>>>>>>>>)>>>>
  public static boolean meta_multi_level_pinned_paren(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned_paren")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = comma_list(builder_, level_ + 1, new Parser() {
      public boolean parse(PsiBuilder builder_, int level_) {
        return comma_list_pinned(builder_, level_ + 1, head, new Parser() {
          public boolean parse(PsiBuilder builder_, int level_) {
            return meta_multi_level_pinned_paren_0_0_1(builder_, level_ + 1, param);
          }
        });
      }
    });
    exit_section_(builder_, marker_, META_MULTI_LEVEL_PINNED_PAREN, result_);
    return result_;
  }

  // <<comma_list <<comma_list <<comma_list <<param>>>>>>>>
  private static boolean meta_multi_level_pinned_paren_0_0_1(PsiBuilder builder_, int level_, final Parser param) {
    return comma_list(builder_, level_ + 1, new Parser() {
      public boolean parse(PsiBuilder builder_, int level_) {
        return comma_list(builder_, level_ + 1, new Parser() {
          public boolean parse(PsiBuilder builder_, int level_) {
            return comma_list(builder_, level_ + 1, param);
          }
        });
      }
    });
  }

  /* ********************************************************** */
  // <<comma_list_pinned one (one | two)>>
  static boolean meta_seq(PsiBuilder builder_, int level_) {
    return comma_list_pinned(builder_, level_ + 1, one_parser_, meta_seq_0_1_parser_);
  }

  // one | two
  private static boolean meta_seq_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<list_of_lists one (one | two)>>
  static boolean meta_seq_of_lists(PsiBuilder builder_, int level_) {
    return list_of_lists(builder_, level_ + 1, one_parser_, meta_seq_of_lists_0_1_parser_);
  }

  // one | two
  private static boolean meta_seq_of_lists_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
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
    return list_of_lists(builder_, level_ + 1, one_parser_, meta_seq_of_lists_opt_0_0_1_parser_);
  }

  // one | two
  private static boolean meta_seq_of_lists_opt_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt_0_0_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list one>>
  static boolean meta_simple(PsiBuilder builder_, int level_) {
    return comma_list(builder_, level_ + 1, one_parser_);
  }

  /* ********************************************************** */
  // <<meta_multi_level one>>
  static boolean multi_level(PsiBuilder builder_, int level_) {
    return meta_multi_level(builder_, level_ + 1, one_parser_);
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
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_choice_1_0_parser_);
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
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_choice_alt_1_0_parser_);
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
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_opt_1_0_parser_);
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
    result_ = result_ && uniqueListOf(builder_, level_ + 1, "1+1", 1+1, one_parser_, two_parser_, 10, some);
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
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_seq_alt_1_0_parser_, param_seq_alt_1_1_parser_);
    result_ = result_ && consumeToken(builder_, "}");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // one | two
  private static boolean param_seq_alt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
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
    result_ = result_ && uniqueListOf(builder_, level_ + 1, one_parser_, two_parser_);
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
    result_ = result_ && uniqueListOf(builder_, level_ + 1, one_parser_, "1+1", param_seq_alt_params_ext_1_1_parser_, 1+1);
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
  // <<param>>
  static boolean recoverable_item(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "recoverable_item")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = param.parse(builder_, level_);
    exit_section_(builder_, level_, marker_, result_, false, item_recover_parser_);
    return result_;
  }

  /* ********************************************************** */
  // <<listOf statement>>
  static boolean root(PsiBuilder builder_, int level_) {
    return listOf(builder_, level_ + 1, statement_parser_);
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

  final static Parser item_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return item_recover(builder_, level_ + 1);
    }
  };
  final static Parser meta_mixed_list_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return comma_list(builder_, level_ + 1, one_parser_);
    }
  };
  final static Parser meta_mixed_list_paren_0_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return meta_mixed_list_paren_0_0(builder_, level_ + 1);
    }
  };
  final static Parser meta_seq_0_1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return meta_seq_0_1(builder_, level_ + 1);
    }
  };
  final static Parser meta_seq_of_lists_0_1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return meta_seq_of_lists_0_1(builder_, level_ + 1);
    }
  };
  final static Parser meta_seq_of_lists_opt_0_0_1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return meta_seq_of_lists_opt_0_0_1(builder_, level_ + 1);
    }
  };
  final static Parser one_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return one(builder_, level_ + 1);
    }
  };
  final static Parser param_choice_1_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return param_choice_1_0(builder_, level_ + 1);
    }
  };
  final static Parser param_choice_alt_1_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return param_choice_alt_1_0(builder_, level_ + 1);
    }
  };
  final static Parser param_opt_1_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return param_opt_1_0(builder_, level_ + 1);
    }
  };
  final static Parser param_seq_alt_1_0_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return param_seq_alt_1_0(builder_, level_ + 1);
    }
  };
  final static Parser param_seq_alt_1_1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return param_seq_alt_1_1(builder_, level_ + 1);
    }
  };
  final static Parser param_seq_alt_params_ext_1_1_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return param_seq_alt_params_ext_1_1(builder_, level_ + 1);
    }
  };
  final static Parser perc_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return consumeToken(builder_, PERC);
    }
  };
  final static Parser statement_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return statement(builder_, level_ + 1);
    }
  };
  final static Parser two_parser_ = new Parser() {
    public boolean parse(PsiBuilder builder_, int level_) {
      return two(builder_, level_ + 1);
    }
  };
}