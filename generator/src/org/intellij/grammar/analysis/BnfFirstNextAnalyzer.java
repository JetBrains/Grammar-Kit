/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.analysis;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.intellij.grammar.psi.impl.GrammarUtil.FakeBnfExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.util.containers.ContainerUtil.union;

/**
 * Computes FIRST and NEXT sets for BNF rules and expressions.
 * <p>
 * The classical compiler-construction notion: {@code FIRST(E)} is the set of tokens that may legally
 * start a string derived from {@code E}; {@code NEXT(E)} (often called FOLLOW) is the set of tokens
 * that may legally appear immediately after {@code E} given the rule call sites where {@code E} is
 * used. Grammar-Kit relies on these sets in many places — recovery sets in the generated parsers
 * ({@code Generator}, {@code JavaParserGenerator}, {@code KotlinParserGenerator}), detection of
 * unreachable choice branches, left-recursion analysis in {@code ExpressionHelper}, completion via
 * {@code LivePreviewParser}, and the FIRST/NEXT block in {@code BnfDocumentationProvider}.
 * <p>
 * Three flavors can be created via the static factories:
 * <ul>
 *   <li>{@link #createAnalyzer(boolean)} — forward FIRST/NEXT, optionally honoring semantic
 *       predicates ({@code &} / {@code !});</li>
 *   <li>{@link #createAnalyzer(boolean, boolean, Condition)} — adds {@code publicRuleOpaque} (stop
 *       expanding at public rules and treat the reference itself as a terminal) and a
 *       {@code parentFilter} that bounds upward traversal during NEXT computation;</li>
 *   <li>{@link #createBackwardAnalyzer(boolean)} — walks sequences right-to-left, used when figuring
 *       out which tokens may <em>end</em> a rule rather than start it (e.g.
 *       {@code RuleGraphHelper}'s collapse-map step).</li>
 * </ul>
 * <p>
 * Three sentinel expressions stand in for situations that cannot be expressed as concrete tokens:
 * {@link #BNF_MATCHES_EOF} (the empty derivation — the analyzed expression may match nothing here,
 * so its NEXT must be propagated outward), {@link #BNF_MATCHES_NOTHING} (no input string can satisfy
 * this expression — surfaced by inspections), and {@link #BNF_MATCHES_ANY} (the analysis hit a meta
 * or external rule, or a {@code recoverWhile} attribute, and cannot constrain the answer further).
 * <p>
 * NEXT computation can fan out aggressively because it follows reference search across the entire
 * grammar; an instance therefore caches NEXT results per target expression. FIRST is cheap enough
 * that it is not cached. Instances are not thread-safe and are normally short-lived (created per
 * parser-generation pass or per inspection visit).
 *
 * @author gregsh
 */
public class BnfFirstNextAnalyzer {

  private static final Logger LOG = Logger.getInstance(BnfFirstNextAnalyzer.class);

  /** Sentinel for the empty derivation: the analyzed expression may match nothing here, so its NEXT must be added by the caller. */
  public static final String MATCHES_EOF = "-eof-";

  /** Sentinel meaning no input can satisfy the expression — produced by predicate intersection and surfaced by the unreachable-choice-branch inspection. */
  public static final String MATCHES_NOTHING = "-never-matches-";

  /** Sentinel meaning analysis cannot constrain the answer (meta rule, external method, {@code recoverWhile} sink). */
  public static final String MATCHES_ANY = "-any-";

  /** Wrapper of {@link #MATCHES_EOF} as a {@link BnfExpression} so it can be returned alongside real expressions. */
  public static final BnfExpression BNF_MATCHES_EOF = new FakeBnfExpression(MATCHES_EOF);

  /** Wrapper of {@link #MATCHES_NOTHING} as a {@link BnfExpression}. */
  public static final BnfExpression BNF_MATCHES_NOTHING = new FakeBnfExpression(MATCHES_NOTHING);

  /** Wrapper of {@link #MATCHES_ANY} as a {@link BnfExpression}. */
  public static final BnfExpression BNF_MATCHES_ANY = new FakeBnfExpression(MATCHES_ANY);

  private final boolean myBackward;
  private final boolean myPublicRuleOpaque;
  private final boolean myPredicateLookAhead;
  private final Condition<PsiElement> myParentFilter;

  // reference search and predicates can quickly get out of control, so NEXT results need to be cached
  private final Map<BnfExpression, Map<BnfExpression, BnfExpression>> myNextCache = new HashMap<>();

  /**
   * Forward analyzer with default behavior: public rules are expanded into their bodies, and there
   * is no parent filter on NEXT traversal.
   *
   * @param predicateLookAhead whether semantic predicates ({@code &}, {@code !}) are evaluated into
   *                           the resulting set. Disable when callers reason about the parser's
   *                           syntactic shape only (e.g., recovery-set generation).
   */
  public static BnfFirstNextAnalyzer createAnalyzer(boolean predicateLookAhead) {
    return new BnfFirstNextAnalyzer(false, false, predicateLookAhead, null);
  }

  /**
   * Forward analyzer with the full set of knobs.
   *
   * @param predicateLookAhead see {@link #createAnalyzer(boolean)}
   * @param publicRuleOpaque   stop expansion at references to public rules and keep the reference
   *                           itself as a result instead. Useful when the caller cares about which
   *                           <em>rules</em> — not which low-level tokens — can begin/follow an
   *                           expression (e.g., rule-collapse and live-preview lookahead).
   * @param parentFilter       bounds NEXT's upward traversal: the climb up the PSI tree halts at
   *                           the first parent that fails the filter. Pass {@code null} for the
   *                           default unbounded traversal.
   */
  public static BnfFirstNextAnalyzer createAnalyzer(boolean predicateLookAhead,
                                                    boolean publicRuleOpaque,
                                                    Condition<PsiElement> parentFilter) {
    return new BnfFirstNextAnalyzer(false, publicRuleOpaque, predicateLookAhead, parentFilter);
  }

  /**
   * Backward analyzer: walks sequences right-to-left so that {@link #calcFirst} effectively yields
   * the set of tokens that can <em>end</em> a derivation. Predicates are not honored in this mode.
   */
  public static BnfFirstNextAnalyzer createBackwardAnalyzer(boolean publicRuleOpaque) {
    return new BnfFirstNextAnalyzer(true, publicRuleOpaque, false, null);
  }

  private BnfFirstNextAnalyzer(boolean backward,
                               boolean publicRuleOpaque,
                               boolean predicateLookAhead,
                               Condition<PsiElement> parentFilter) {
    myBackward = backward;
    myPublicRuleOpaque = publicRuleOpaque;
    myPredicateLookAhead = predicateLookAhead;
    myParentFilter = parentFilter;
  }

  /**
   * FIRST set of {@code rule}'s body. Includes {@link #BNF_MATCHES_EOF} when the body is nullable
   * (i.e., the rule may match the empty string).
   * <p>
   * The rule's own expression is pre-marked as visited, so a self-recursive rule yields the
   * reference itself rather than expanding indefinitely.
   */
  public Set<BnfExpression> calcFirst(@NotNull BnfRule rule) {
    Set<BnfExpression> visited = new HashSet<>();
    BnfExpression expression = rule.getExpression();
    visited.add(expression);
    return calcFirstInner(expression, new HashSet<>(), visited);
  }

  /**
   * FIRST set of an arbitrary expression. Includes {@link #BNF_MATCHES_EOF} when the expression
   * is nullable.
   */
  public Set<BnfExpression> calcFirst(@NotNull BnfExpression expression) {
    return calcFirstInner(expression, new HashSet<>(), new HashSet<>());
  }

  /**
   * NEXT set of {@code targetRule}: every token that may follow the rule at any of its call sites.
   * <p>
   * Map values point back to the originating {@link BnfReferenceOrToken} when one is available, so
   * callers can attribute a NEXT entry to the specific call site that contributed it (e.g., for
   * better diagnostics or for the documentation provider). The map contains {@link #BNF_MATCHES_EOF}
   * when the rule may legally appear at end-of-input. Results are cached on this analyzer.
   */
  public Map<BnfExpression, BnfExpression> calcNext(@NotNull BnfRule targetRule) {
    return calcNextInner(targetRule.getExpression(), new HashMap<>(), new HashSet<>());
  }

  /** NEXT set for an arbitrary expression — see {@link #calcNext(BnfRule)}. */
  public Map<BnfExpression, BnfExpression> calcNext(@NotNull BnfExpression targetExpression) {
    return calcNextInner(targetExpression, new HashMap<>(), new HashSet<>());
  }

  private Map<BnfExpression, BnfExpression> calcNextInner(@NotNull BnfExpression targetExpression,
                                                          Map<BnfExpression, BnfExpression> result,
                                                          Set<BnfExpression> visited) {
    Map<BnfExpression, BnfExpression> cached = myNextCache.get(targetExpression);
    if (cached != null) return cached;

    LinkedList<BnfExpression> stack = new LinkedList<>();
    Set<BnfRule> totalVisited = new HashSet<>();
    Set<BnfExpression> curResult = new HashSet<>();
    stack.add(targetExpression);
    main: while (!stack.isEmpty()) {

      PsiElement cur = stack.removeLast();
      BnfExpression startingExpr = cur instanceof BnfReferenceOrToken? (BnfExpression)cur : null;
      PsiElement parent = cur.getParent();
      while (parent instanceof BnfExpression && (myParentFilter == null || myParentFilter.value(parent))) {
        curResult.clear();
        PsiElement grandPa = parent.getParent();
        if (grandPa instanceof BnfRule && BnfRules.isExternal((BnfRule)grandPa) ||
            grandPa instanceof BnfExternalExpression /*todo support meta rules*/) {
          result.put(BNF_MATCHES_ANY, startingExpr);
          break;
        }
        else if (parent instanceof BnfSequence) {
          List<BnfExpression> children  = ((BnfSequence)parent).getExpressionList();
          int idx = children.indexOf(cur);
          List<BnfExpression> sublist = myBackward? children.subList(0, idx) : children.subList(idx + 1, children.size());
          calcSequenceFirstInner(sublist, curResult, visited);
          boolean skipResolve = !curResult.contains(BNF_MATCHES_EOF);
          for (BnfExpression e : curResult) {
            result.put(e, startingExpr);
          }
          if (skipResolve) continue main;
        }
        else if (parent instanceof BnfQuantified) {
          IElementType effectiveType = BnfAst.getEffectiveType(parent);
          if (effectiveType == BnfTypes.BNF_OP_ZEROMORE || effectiveType == BnfTypes.BNF_OP_ONEMORE) {
            calcFirstInner((BnfExpression)parent, curResult, visited);
            for (BnfExpression e : curResult) {
              result.put(e, startingExpr);
            }
          }
        }
        cur = parent;
        parent = grandPa;
      }
      if (parent instanceof BnfRule rule &&
          (myParentFilter == null || myParentFilter.value(parent)) &&
          totalVisited.add(rule)) {
        for (PsiReference reference : ReferencesSearch.search(rule, rule.getUseScope()).findAll()) {
          PsiElement element = reference.getElement();
          if (element instanceof BnfExpression && PsiTreeUtil.getParentOfType(element, BnfPredicate.class) == null) {
            BnfAttr attr = PsiTreeUtil.getParentOfType(element, BnfAttr.class);
            if (attr != null) {
              if (KnownAttribute.getCompatibleAttribute(attr.getName()) == KnownAttribute.RECOVER_WHILE) {
                result.put(BNF_MATCHES_ANY, startingExpr);
              }
            }
            else {
              stack.add((BnfExpression)element);
            }
          }
        }
      }
    }
    if (result.isEmpty()) result.put(BNF_MATCHES_EOF, null);
    myNextCache.put(targetExpression, result);
    return result;
  }

  private Set<BnfExpression> calcSequenceFirstInner(List<BnfExpression> expressions,
                                                    Set<BnfExpression> result,
                                                    Set<BnfExpression> visited) {
    boolean matchesEof = !result.add(BNF_MATCHES_EOF);
    boolean pinApplied = false;
    Set<BnfExpression> pinned;
    if (!myBackward) {
      BnfExpression firstItem = ContainerUtil.getFirstItem(expressions);
      if (firstItem == null) return result;
      BnfRule rule = BnfRules.of(firstItem);
      pinned = new HashSet<>();
      GrammarUtil.processPinnedExpressions(rule, new CommonProcessors.CollectProcessor<>(pinned));
      if (firstItem.getParent() instanceof BnfSequence) {
        for (BnfExpression e : ((BnfSequence)firstItem.getParent()).getExpressionList()) {
          if (e == firstItem) break;
          pinApplied |= pinned.contains(e);
        }
      }
    }
    else pinned = Collections.emptySet();

    List<BnfExpression> list = myBackward ? ContainerUtil.reverse(expressions) : expressions;
    for (int i = 0, size = list.size(); i < size; i++) {
      if (!result.remove(BNF_MATCHES_EOF)) break;
      matchesEof |= pinApplied;
      BnfExpression e = list.get(i);
      calcFirstInner(e, result, visited, i < size - 1 ? Pair.create(pinned.contains(e), list.subList(i + 1, size)) : null);
      pinApplied |= pinned.contains(e);
    }
    // add empty back if was there before
    if (matchesEof) result.add(BNF_MATCHES_EOF);
    return result;
  }

  /**
   * Recursive worker that accumulates FIRST of {@code expression} into {@code result}; exposed as
   * public so callers that already track {@code visited} can fold further analysis into the same
   * traversal (used by {@link #calcSequenceFirstInner}).
   */
  public Set<BnfExpression> calcFirstInner(BnfExpression expression, Set<BnfExpression> result, Set<BnfExpression> visited) {
    return calcFirstInner(expression, result, visited, null);
  }

  /**
   * Variant of {@link #calcFirstInner(BnfExpression, Set, Set)} that lets the caller supply the
   * sequence tail to use when evaluating a semantic predicate inside this expression. The
   * {@code forcedNext} pair carries (a) whether the predicate is preceded by a {@code pin} marker
   * in its sequence and (b) the rest of the sequence after the predicate; together they let the
   * predicate intersect/subtract against the actual local follow set rather than the global NEXT.
   */
  public Set<BnfExpression> calcFirstInner(BnfExpression expression, Set<BnfExpression> result, Set<BnfExpression> visited, @Nullable Pair<Boolean, List<BnfExpression>> forcedNext) {
    BnfFile file = (BnfFile)expression.getContainingFile();
    if (expression instanceof BnfLiteralExpression) {
      result.add(expression);
    }
    else if (expression instanceof BnfReferenceOrToken) {
      BnfRule rule = file.getRule(expression.getText());
      if (rule != null) {
        if (BnfRules.isExternal(rule)) {
          BnfExpression callExpr = ContainerUtil.getFirstItem(GrammarUtil.getExternalRuleExpressions(rule));
          if (callExpr instanceof BnfReferenceOrToken && file.getRule(callExpr.getText()) == null) {
            result.add(callExpr);
            return result;
          }
        }
        BnfExpression ruleExpression = rule.getExpression();
        if (myPublicRuleOpaque && !BnfRules.isPrivate(rule) ||
            !visited.add(ruleExpression)) {
          if (!(BnfRules.firstNotTrivial(rule) instanceof BnfPredicate)) {
            result.add(expression);
          }
        }
        else {
          calcFirstInner(ruleExpression, result, visited, forcedNext);
          boolean removed = visited.remove(ruleExpression);
          LOG.assertTrue(removed, "path corruption detected: " + ruleExpression.getText());
        }
      }
      else {
        result.add(expression);
      }
    }
    else if (expression instanceof BnfParenthesized parenthesized) {
      calcFirstInner(parenthesized.getExpression(), result, visited, forcedNext);
      if (expression instanceof BnfParenOptExpression) {
        result.add(BNF_MATCHES_EOF);
      }
    }
    else if (expression instanceof BnfChoice choiceExpression) {
      boolean matchesNothing = result.remove(BNF_MATCHES_NOTHING);
      boolean matchesSomething = false;
      for (BnfExpression child : choiceExpression.getExpressionList()) {
        calcFirstInner(child, result, visited, forcedNext);
        matchesSomething |= !result.remove(BNF_MATCHES_NOTHING);
      }
      if (!matchesSomething || matchesNothing) result.add(BNF_MATCHES_NOTHING);
    }
    else if (expression instanceof BnfSequence sequenceExpression) {
      calcSequenceFirstInner(sequenceExpression.getExpressionList(), result, visited);
    }
    else if (expression instanceof BnfQuantified quantified) {
      calcFirstInner(quantified.getExpression(), result, visited, forcedNext);
      IElementType effectiveType = BnfAst.getEffectiveType(expression);
      if (effectiveType == BnfTypes.BNF_OP_OPT || effectiveType == BnfTypes.BNF_OP_ZEROMORE) {
        result.add(BNF_MATCHES_EOF);
      }
    }
    else if (expression instanceof BnfExternalExpression externalExpression) {
      List<BnfExpression> arguments = externalExpression.getArguments();
      if (arguments.isEmpty() && BnfRules.isMeta(BnfRules.of(expression))) {
        result.add(expression);
      }
      else {
        BnfExpression ruleRef = externalExpression.getRefElement();
        Set<BnfExpression> metaResults = calcFirstInner(ruleRef, new LinkedHashSet<>(), visited, forcedNext);
        List<String> params = null;
        for (BnfExpression e : metaResults) {
          if (e instanceof BnfExternalExpression) {
            if (params == null) {
              BnfRule metaRule = (BnfRule)ruleRef.getReference().resolve();
              if (metaRule == null) {
                LOG.error("ruleRef:" + ruleRef.getText() +", metaResult:" + metaResults);
                continue;
              }
              params = GrammarUtil.collectMetaParameters(metaRule, metaRule.getExpression());
            }
            int idx = params.indexOf(e.getText());
            if (idx > -1 && idx < arguments.size()) {
              calcFirstInner(arguments.get(idx), result, visited, null);
            }
          }
          else {
            result.add(e);
          }
        }
      }
    }
    else if ((myBackward || !myPredicateLookAhead) && expression instanceof BnfPredicate) {
      result.add(BNF_MATCHES_EOF);
    }
    else if (expression instanceof BnfPredicate) {
      IElementType elementType = ((BnfPredicate)expression).getPredicateSign().getFirstChild().getNode().getElementType();
      BnfExpression predicateExpression = BnfAst.getNonTrivialNode(((BnfPredicate)expression).getExpression());
      boolean skip = predicateExpression instanceof BnfSequence &&
                     ((BnfSequence)predicateExpression).getExpressionList().size() > 1; // todo calc min length ?
      // take only one token into account which is not exactly correct but better than nothing
      Set<BnfExpression> conditions = calcFirstInner(predicateExpression, newExprSet(), visited, null);
      Set<BnfExpression> next;
      List<BnfExpression> externalCond = Collections.emptyList();
      List<BnfExpression> externalNext = Collections.emptyList();
      if (!visited.add(predicateExpression)) {
        skip = true;
        next = Collections.emptySet();
        //result.add(BNF_MATCHES_NOTHING);
      }
      else {
        if (forcedNext == null) {
          next = calcNextInner(expression, new HashMap<>(), visited).keySet();
        }
        else {
          next = calcSequenceFirstInner(forcedNext.second, newExprSet(), visited);
        }
        visited.remove(predicateExpression);
        externalCond = filterExternalMethods(conditions);
        externalNext = filterExternalMethods(next);
        if (!skip) skip = !externalCond.isEmpty();
      }
      Set<BnfExpression> mixed;
      if (elementType == BnfTypes.BNF_OP_AND) {
        if (forcedNext != null && forcedNext.first) {
          mixed = newExprSet(conditions);
        }
        else if (skip) {
          mixed = exprSetUnion(next, externalCond);
          mixed.remove(BNF_MATCHES_EOF);
        }
        else if (!conditions.contains(BNF_MATCHES_EOF)) {
          if (next.contains(BNF_MATCHES_ANY)) {
            mixed = newExprSet(conditions);
          }
          else {
            if (externalNext.isEmpty()) {
              mixed = exprSetIntersection(conditions, next);
              if (mixed.isEmpty() && !involvesTextMatching(conditions)) {
                mixed.add(BNF_MATCHES_NOTHING);
              }
            }
            else {
              mixed = newExprSet(conditions);
            }
          }
        }
        else {
          mixed = newExprSet(next);
        }
      }
      else {
        if (skip) {
          mixed = exprSetUnion(next, externalCond); // todo shall be actually inverted
          mixed.remove(BNF_MATCHES_EOF);
        }
        else if (!conditions.contains(BNF_MATCHES_EOF)) {
          mixed = exprSetDifference(next, conditions);
          if (mixed.isEmpty() && !involvesTextMatching(conditions)) {
            mixed.add(BNF_MATCHES_NOTHING);
          }
        }
        else {
          mixed = Collections.singleton(BNF_MATCHES_NOTHING);
        }
      }
      result.addAll(mixed);
    }

    return result;
  }

  private static List<BnfExpression> filterExternalMethods(Set<BnfExpression> set) {
    if (set.removeIf(o -> "<<eof>>".equals(o.getText()))) {
      set.add(BNF_MATCHES_EOF);
    }
    return JBIterable.from(set).filter(GrammarUtil::isExternalReference).toList();
  }

  private static boolean involvesTextMatching(Set<BnfExpression> set) {
    for (BnfExpression o : set) {
      if (o instanceof BnfStringLiteralExpression &&
          !BnfAst.getTokenTextToNameMap((BnfFile)o.getContainingFile())
            .containsKey(BnfAttributes.getLiteralValue((BnfStringLiteralExpression)o))) {
        return true;
      }
    }
    return false;
  }

  /** Renders every expression in {@code expressions} via {@link #asString} into a sorted set. */
  public static Set<String> asStrings(Set<BnfExpression> expressions) {
    Set<String> result = new TreeSet<>();
    for (BnfExpression expression : expressions) {
      result.add(asString(expression));
    }
    return result;
  }

  /**
   * Renders {@code expression} as a human-readable token form: string literals are quoted with
   * single quotes, external references are prefixed with {@code "#"}, and the {@link FakeBnfExpression}
   * sentinels round-trip through their {@code MATCHES_*} text.
   */
  public static @NotNull String asString(@NotNull BnfExpression expression) {
    if (expression instanceof BnfLiteralExpression) {
      String text = expression.getText();
      return StringUtil.isQuotedString(text) ? '\'' + GrammarUtil.unquote(text) + '\'' : text;
    }
    else if (GrammarUtil.isExternalReference(expression)) {
      return "#" + expression.getText();
    }
    else {
      return expression.getText();
    }
  }

  private static @NotNull Set<BnfExpression> newExprSet() {
    return new ObjectOpenCustomHashSet<>(BnfAst.textStrategy());
  }

  private static @NotNull Set<BnfExpression> newExprSet(Collection<BnfExpression> expressions) {
    return new ObjectOpenCustomHashSet<>(expressions, BnfAst.textStrategy());
  }

  private static @NotNull Set<BnfExpression> exprSetUnion(Collection<BnfExpression> a, Collection<BnfExpression> b) {
    Set<BnfExpression> result = newExprSet(a);
    result.addAll(b);
    return result;
  }

  private static @NotNull Set<BnfExpression> exprSetIntersection(@NotNull Set<BnfExpression> a, @NotNull Set<BnfExpression> b) {
    Set<BnfExpression> filter = newExprSet(a);
    filter.retainAll(newExprSet(b));
    Set<BnfExpression> result = new HashSet<>(union(a, b));
    result.retainAll(filter);
    return result;
  }

  private static @NotNull Set<BnfExpression> exprSetDifference(@NotNull Set<BnfExpression> a, @NotNull Set<BnfExpression> b) {
    Set<BnfExpression> filter = newExprSet(a);
    filter.removeAll(newExprSet(b));
    Set<BnfExpression> result = new HashSet<>(union(a, b));
    result.retainAll(filter);
    return result;
  }
}
