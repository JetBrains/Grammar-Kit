// ---- ExternalRules.java -----------------
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
    int level_ = 0;
    boolean result_;
    if (root_ == ONE) {
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
      result_ = root(builder_, level_ + 1);
      while (builder_.getTokenType() != null) {
        builder_.advanceLexer();
      }
      marker_.done(root_);
    }
    return builder_.getTreeBuilt();
  }

  /* ********************************************************** */
  // <<param>> (',' <<param>>) *
  public static boolean comma_list(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = param.parse(builder_, level_);
    result_ = result_ && comma_list_1(builder_, level_ + 1, param);
    if (result_) {
      marker_.done(COMMA_LIST);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (',' <<param>>) *
  private static boolean comma_list_1(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!comma_list_1_0(builder_, level_ + 1, param)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "comma_list_1");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (',' <<param>>)
  private static boolean comma_list_1_0(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1_0")) return false;
    return comma_list_1_0_0(builder_, level_ + 1, param);
  }

  // ',' <<param>>
  private static boolean comma_list_1_0_0(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, ",");
    result_ = result_ && param.parse(builder_, level_);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<head>> <<param>> (<<comma_list_tail <<param>>>>) *
  public static boolean comma_list_pinned(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_pinned")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = head.parse(builder_, level_);
    result_ = result_ && param.parse(builder_, level_);
    result_ = result_ && comma_list_pinned_2(builder_, level_ + 1, param);
    if (result_) {
      marker_.done(COMMA_LIST_PINNED);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (<<comma_list_tail <<param>>>>) *
  private static boolean comma_list_pinned_2(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_pinned_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!comma_list_pinned_2_0(builder_, level_ + 1, param)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "comma_list_pinned_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (<<comma_list_tail <<param>>>>)
  private static boolean comma_list_pinned_2_0(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_pinned_2_0")) return false;
    return comma_list_tail(builder_, level_ + 1, param);
  }

  /* ********************************************************** */
  // ',' <<param>>
  public static boolean comma_list_tail(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "comma_list_tail")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    final Marker marker_ = builder_.mark();
    enterErrorRecordingSection(builder_, level_, _SECTION_GENERAL_);
    result_ = consumeToken(builder_, ",");
    pinned_ = result_; // pin = 1
    result_ = result_ && param.parse(builder_, level_);
    if (result_ || pinned_) {
      marker_.done(COMMA_LIST_TAIL);
    }
    else {
      marker_.rollbackTo();
    }
    result_ = exitErrorRecordingSection(builder_, result_, level_, pinned_, _SECTION_GENERAL_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // <<head>> <<comma_list <<param>>>> (<<comma_list_tail <<comma_list <<param>>>>>>) *
  public static boolean list_of_lists(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
    if (!recursion_guard_(builder_, level_, "list_of_lists")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = head.parse(builder_, level_);
    result_ = result_ && comma_list(builder_, level_ + 1, param);
    result_ = result_ && list_of_lists_2(builder_, level_ + 1, param);
    if (result_) {
      marker_.done(LIST_OF_LISTS);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (<<comma_list_tail <<comma_list <<param>>>>>>) *
  private static boolean list_of_lists_2(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "list_of_lists_2")) return false;
    int offset_ = builder_.getCurrentOffset();
    while (true) {
      if (!list_of_lists_2_0(builder_, level_ + 1, param)) break;
      int next_offset_ = builder_.getCurrentOffset();
      if (offset_ == next_offset_) {
        empty_element_parsed_guard_(builder_, offset_, "list_of_lists_2");
        break;
      }
      offset_ = next_offset_;
    }
    return true;
  }

  // (<<comma_list_tail <<comma_list <<param>>>>>>)
  private static boolean list_of_lists_2_0(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "list_of_lists_2_0")) return false;
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

  // (<<comma_list one>>)
  private static boolean meta_mixed_list_paren_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_mixed_list_paren_0_0")) return false;
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
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
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
    if (result_) {
      marker_.done(META_MULTI_LEVEL);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> <<comma_list <<comma_list <<comma_list <<param>>>>>>>>>>>>
  public static boolean meta_multi_level_pinned(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
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
    if (result_) {
      marker_.done(META_MULTI_LEVEL_PINNED);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<comma_list <<comma_list_pinned <<head>> (<<comma_list <<comma_list <<comma_list <<param>>>>>>>>)>>>>
  public static boolean meta_multi_level_pinned_paren(PsiBuilder builder_, int level_, final Parser head, final Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned_paren")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = comma_list(builder_, level_ + 1, new Parser() {
        public boolean parse(PsiBuilder builder_, int level_) {
          return comma_list_pinned(builder_, level_ + 1, head, new Parser() {
            public boolean parse(PsiBuilder builder_, int level_) {
              return meta_multi_level_pinned_paren_0_0_1(builder_, level_ + 1, param);
            }
          });
        }
      });
    if (result_) {
      marker_.done(META_MULTI_LEVEL_PINNED_PAREN);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  // (<<comma_list <<comma_list <<comma_list <<param>>>>>>>>)
  private static boolean meta_multi_level_pinned_paren_0_0_1(PsiBuilder builder_, int level_, final Parser param) {
    if (!recursion_guard_(builder_, level_, "meta_multi_level_pinned_paren_0_0_1")) return false;
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

  // (one | two)
  private static boolean meta_seq_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_0_1")) return false;
    return meta_seq_0_1_0(builder_, level_ + 1);
  }

  // one | two
  private static boolean meta_seq_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_0_1_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // <<list_of_lists one (one | two)>>
  static boolean meta_seq_of_lists(PsiBuilder builder_, int level_) {
    return list_of_lists(builder_, level_ + 1, one_parser_, meta_seq_of_lists_0_1_parser_);
  }

  // (one | two)
  private static boolean meta_seq_of_lists_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_0_1")) return false;
    return meta_seq_of_lists_0_1_0(builder_, level_ + 1);
  }

  // one | two
  private static boolean meta_seq_of_lists_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_0_1_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // (<<list_of_lists one (one | two)>>)?
  static boolean meta_seq_of_lists_opt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt")) return false;
    meta_seq_of_lists_opt_0(builder_, level_ + 1);
    return true;
  }

  // (<<list_of_lists one (one | two)>>)
  private static boolean meta_seq_of_lists_opt_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt_0")) return false;
    return list_of_lists(builder_, level_ + 1, one_parser_, meta_seq_of_lists_opt_0_0_1_parser_);
  }

  // (one | two)
  private static boolean meta_seq_of_lists_opt_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt_0_0_1")) return false;
    return meta_seq_of_lists_opt_0_0_1_0(builder_, level_ + 1);
  }

  // one | two
  private static boolean meta_seq_of_lists_opt_0_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "meta_seq_of_lists_opt_0_0_1_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
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
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "one");
    if (result_) {
      marker_.done(ONE);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf (one | two | 10 | some)>> '}'
  static boolean param_choice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_choice_1_0_parser_);
    result_ = result_ && consumeToken(builder_, "}");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // (one | two | 10 | some)
  private static boolean param_choice_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_1_0")) return false;
    return param_choice_1_0_0(builder_, level_ + 1);
  }

  // one | two | 10 | some
  private static boolean param_choice_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf {one | two | 10 | some}>> '}'
  static boolean param_choice_alt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_alt")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_choice_alt_1_0_parser_);
    result_ = result_ && consumeToken(builder_, "}");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // {one | two | 10 | some}
  private static boolean param_choice_alt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_alt_1_0")) return false;
    return param_choice_alt_1_0_0(builder_, level_ + 1);
  }

  // one | two | 10 | some
  private static boolean param_choice_alt_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_choice_alt_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf [one | two | 10 | some]>> '}'
  static boolean param_opt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_opt")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_opt_1_0_parser_);
    result_ = result_ && consumeToken(builder_, "}");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
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
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf "1+1" '1+1' one two 10 some>> '}'
  static boolean param_seq(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, "1+1", 1+1, one_parser_, two_parser_, 10, some);
    result_ = result_ && consumeToken(builder_, "}");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  /* ********************************************************** */
  // '{' <<uniqueListOf {one | two} [10 | some]>> '}'
  static boolean param_seq_alt(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "{");
    result_ = result_ && uniqueListOf(builder_, level_ + 1, param_seq_alt_1_0_parser_, param_seq_alt_1_1_parser_);
    result_ = result_ && consumeToken(builder_, "}");
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
    return result_;
  }

  // {one | two}
  private static boolean param_seq_alt_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_1_0")) return false;
    return param_seq_alt_1_0_0(builder_, level_ + 1);
  }

  // one | two
  private static boolean param_seq_alt_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "param_seq_alt_1_0_0")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
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
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "10");
    if (!result_) result_ = consumeToken(builder_, SOME);
    if (!result_) {
      marker_.rollbackTo();
    }
    else {
      marker_.drop();
    }
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
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = one(builder_, level_ + 1);
    if (!result_) result_ = two(builder_, level_ + 1);
    if (result_) {
      marker_.done(STATEMENT);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

  /* ********************************************************** */
  // 'two'
  public static boolean two(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "two")) return false;
    boolean result_ = false;
    final Marker marker_ = builder_.mark();
    result_ = consumeToken(builder_, "two");
    if (result_) {
      marker_.done(TWO);
    }
    else {
      marker_.rollbackTo();
    }
    return result_;
  }

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