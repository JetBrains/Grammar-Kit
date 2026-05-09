/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.JBTreeTraverser;
import com.intellij.util.containers.TreeTraversal;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static org.intellij.grammar.generator.ParserGeneratorUtil.topoSort;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.OPTIONAL;
import static org.intellij.grammar.psi.BnfAst.getChildExpressions;
import static org.intellij.grammar.psi.BnfRules.getSuperRules;

/**
 * Discovers and classifies expression-rule clusters in a BNF file.
 * <p>
 * An <i>expression rule</i> is a left-recursive rule whose alternatives form an
 * operator-precedence table — atoms, prefix/postfix unaries, binary, and n-ary operators.
 * Rather than rejecting the left recursion, the generator parses these clusters with the
 * precedence-climbing scheme emitted by {@link ExpressionGeneratorHelper} (Java) and
 * {@link KotlinParserGenerator#generateExpressionRoot} (Kotlin).
 * <p>
 * For each detected cluster this helper builds an {@link ExpressionInfo} containing:
 * <ul>
 *   <li>a priority ordering ({@link ExpressionInfo#priorityMap}) derived from the order in
 *       which sub-rules are reachable from the root;</li>
 *   <li>per-rule {@link OperatorInfo} entries telling the emitter the operator type
 *       (ATOM/PREFIX/POSTFIX/BINARY/N_ARY), its operator subtree, and its argument rules;</li>
 *   <li>private "priority groups" — private rules used purely to inject a priority level
 *       without a corresponding generated method;</li>
 *   <li>{@link ExpressionInfo#checkEmpty}, the set of operators that need a
 *       {@code empty_element_parsed_guard_} to break out of an n-ary loop on empty matches.</li>
 * </ul>
 * <p>
 * Detection is best-effort: ill-formed clusters are downgraded to {@code ATOM} with a warning,
 * and rules that don't fit the expected shape (e.g. an n-ary operator without the required
 * {@code rootRule (op rootRule)+} structure) are reported via {@link #addWarning}.
 * <p>
 * Cached per file via {@link #getCached(BnfFile)}; the cache is invalidated when the BNF file
 * changes.
 */
public class ExpressionHelper {
  private static final Key<CachedValue<ExpressionHelper>> EXPRESSION_HELPER_KEY = Key.create("EXPRESSION_HELPER_KEY");
  private static final Key<List<BnfExpression>> ORIGINAL_EXPRESSIONS = Key.create("ORIGINAL_EXPRESSIONS");

  private final BnfFile myFile;
  private final RuleGraphHelper myRuleGraph;
  private final Consumer<String> myWarningConsumer;

  private final Map<BnfRule, ExpressionInfo> myExpressionMap = new HashMap<>();
  private final Map<BnfRule, BnfRule> myRootRulesMap = new HashMap<>();

  /**
   * Returns the cached {@link ExpressionHelper} for {@code file}, building one on first access.
   * The cache is keyed off the file's modification stamp; the cached instance has no warning
   * sink (warnings are only relevant during generation, where callers construct their own).
   */
  public static ExpressionHelper getCached(@NotNull BnfFile file) {
    CachedValue<ExpressionHelper> value = file.getUserData(EXPRESSION_HELPER_KEY);
    if (value == null) {
      file.putUserData(EXPRESSION_HELPER_KEY, value = CachedValuesManager.getManager(file.getProject()).createCachedValue(
        () -> new CachedValueProvider.Result<>(new ExpressionHelper(file, RuleGraphHelper.getCached(file), null), file), false));
    }
    return value.getValue();
  }

  public ExpressionHelper(@NotNull BnfFile file,
                          @NotNull RuleGraphHelper ruleGraph,
                          @Nullable Consumer<String> warningConsumer) {
    myFile = file;
    myRuleGraph = ruleGraph;
    myWarningConsumer = warningConsumer;
    buildExpressionRules();
  }

  public void addWarning(@NotNull String text) {
    if (myWarningConsumer == null) return;
    myWarningConsumer.accept(text);
  }

  /**
   * Returns the {@link ExpressionInfo} of the cluster that contains {@code rule}, or
   * {@code null} if {@code rule} isn't part of an expression cluster. The root rule and any
   * private rule always yield their cluster's info; non-private sub-rules only yield it when
   * they have an entry in {@link ExpressionInfo#priorityMap}.
   */
  public @Nullable ExpressionInfo getExpressionInfo(@Nullable BnfRule rule) {
    BnfRule root = myRootRulesMap.get(rule);
    ExpressionInfo info = root == null ? null : myExpressionMap.get(root);
    if (info == null) return null;
    if (info.rootRule == rule || BnfRules.isPrivate(rule)) return info;
    return info.priorityMap.containsKey(rule) ? info : null;
  }

  /**
   * Scans every non-private, non-fake rule and treats it as the root of an expression cluster
   * if (a) the rule-graph reports no content rules for it (so it isn't a regular composite
   * rule) and (b) its FIRST set contains a self-reference (the tell-tale sign of left
   * recursion). For each such root, populates {@link #myExpressionMap} with priorities, an
   * operator map (via {@link #buildOperatorMap}), and the {@code checkEmpty} set of operators
   * whose entire body is optional and so need an empty-match guard at runtime.
   */
  private void buildExpressionRules() {
    BnfFirstNextAnalyzer analyzer = BnfFirstNextAnalyzer.createAnalyzer(false);
    for (BnfRule rule : myFile.getRules()) {
      if (BnfRules.isPrivate(rule) || BnfRules.isFake(rule)) continue;
      if (myRootRulesMap.containsKey(rule)) continue;
      Map<PsiElement, RuleGraphHelper.Cardinality> contentRules = myRuleGraph.getFor(rule);
      if (!contentRules.isEmpty()) continue;
      if (!BnfFirstNextAnalyzer.asStrings(analyzer.calcFirst(rule)).contains(rule.getName())) continue;

      ExpressionInfo expressionInfo = new ExpressionInfo(rule);
      addToPriorityMap(rule, myRuleGraph.getExtendsRules(rule), expressionInfo);
      List<BnfRule> rules = topoSort(expressionInfo.priorityMap.keySet(), myRuleGraph);
      for (BnfRule r : rules) {
        buildOperatorMap(r, rule, expressionInfo);
      }
      if (!expressionInfo.priorityMap.isEmpty()) {
        myRootRulesMap.put(rule, rule);
        myExpressionMap.put(rule, expressionInfo);
      }
      ops:
      for (OperatorInfo info : expressionInfo.operatorMap.values()) {
        Map<PsiElement, RuleGraphHelper.Cardinality> map = myRuleGraph.collectMembers(info.rule(), info.operator(), new HashSet<>());
        for (RuleGraphHelper.Cardinality c : map.values()) {
          if (!c.optional()) continue ops;
        }
        expressionInfo.checkEmpty.add(info);
      }
    }
  }

  /**
   * Walks the cluster rooted at {@code rule}, descending only through the root and private
   * priority groups, and assigns each visited rule either a priority entry or a private-group
   * entry. Sub-rules outside {@code rulesCluster} (the extends-set of the root) that aren't
   * private get a warning. Detects rules that participate in two clusters or are reached
   * twice.
   */
  private void addToPriorityMap(BnfRule rule, Collection<BnfRule> rulesCluster, ExpressionInfo info) {
    JBTreeTraverser<BnfRule> traverser = new JBTreeTraverser<>(
      o -> ObjectUtils.notNull(rule == o || BnfRules.isPrivate(o) ? myRuleGraph.getSubRules(o) : null, Collections.emptyList()));
    for (TreeTraversal.TracingIt<BnfRule> it = traverser.withRoot(rule).unique().traverse().skip(1).typedIterator(); it.hasNext(); ) {
      BnfRule subRule = it.next();
      if (info.priorityMap.containsKey(subRule)) {
        addWarning(String.format("'%s' priority is calculated twice", subRule.getName()));
        continue;
      }
      BnfRule prev = myRootRulesMap.put(subRule, info.rootRule);
      if (prev != null) {
        addWarning(String.format("''%s' is in several expression hierarchies: %s and %s",
                                 subRule.getName(), prev.getName(), info.rootRule.getName()));
      }
      Integer groupPriority = info.privateGroups.get(it.parent());
      int priority = groupPriority == null ? info.nextPriority++ : groupPriority;
      if (rulesCluster.contains(subRule)) {
        if (!BnfRules.isPrivate(subRule) || !myRuleGraph.getFor(subRule).isEmpty()) {
          info.priorityMap.put(subRule, priority);
        }
      }
      else if (BnfRules.isPrivate(subRule)) {
        info.privateGroups.put(subRule, priority);
      }
      else {
        addWarning(String.format("'%s' is not an expression rule nor private priority group", subRule.getName()));
      }
    }
  }

  /**
   * Classifies {@code rule} as one of {@link OperatorType#ATOM}, {@code PREFIX},
   * {@code POSTFIX}, {@code BINARY}, or {@code N_ARY} and stores the resulting
   * {@link OperatorInfo} in {@code expressionInfo}. The classification is driven by the
   * cardinality of {@code rootRule} inside this rule and the position(s) of the root reference
   * among the child expressions:
   * <ul>
   *   <li>no occurrence → ATOM;</li>
   *   <li>required (one occurrence): position 0 → POSTFIX, last → PREFIX;</li>
   *   <li>at-least-one occurrence: two leading occurrences → BINARY; one occurrence followed by
   *       {@code (op rootRule) +} → N_ARY.</li>
   * </ul>
   * Anything else triggers a warning and falls back to ATOM.
   */
  private void buildOperatorMap(BnfRule rule, BnfRule rootRule, ExpressionInfo expressionInfo) {
    Map<PsiElement, RuleGraphHelper.Cardinality> ruleContent = myRuleGraph.getFor(rule);
    RuleGraphHelper.Cardinality cardinality = ruleContent.get(rootRule);
    BnfRule rootRuleSubst = rootRule;
    if (cardinality == null) {
      Collection<BnfRule> extendsRules = myRuleGraph.getExtendsRules(rootRule);
      JBIterable<BnfRule> tryOtherRules = JBIterable.from(ruleContent.keySet())
        .filter(BnfRule.class)
        .filter(extendsRules::contains)
        .append(getSuperRules(myFile, rootRule).filter(Conditions.notNull()));
      for (BnfRule r : tryOtherRules) {
        cardinality = ruleContent.get(r);
        if (cardinality == null) continue;
        rootRuleSubst = r;
        break;
      }
    }
    if (BnfRules.isExternal(rule)) {
      BnfExpression expression = (BnfExpression)ContainerUtil.getFirstItem(ruleContent.keySet());
      expressionInfo.operatorMap.put(rule, new OperatorInfo(rule, OperatorType.ATOM, expression, null));
      return;
    }
    String rootRuleName = rootRule.getName();
    List<BnfExpression> childExpressions = getChildExpressions(rule.getExpression());
    OperatorInfo info;
    if (cardinality == null) {
      // atom
      info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
    }
    else if (childExpressions.size() < 2) {
      addWarning("invalid expression definition for " + rule + ": 2 or more arguments expected");
      info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
    }
    else if (cardinality == RuleGraphHelper.Cardinality.REQUIRED) {
      // postfix or prefix unary expression
      int index = indexOf(rootRuleSubst, 0, childExpressions, expressionInfo);
      BnfRule arg1 = substRule(childExpressions, index, rootRule);
      if (index == 0) {
        info = new OperatorInfo(rule, OperatorType.POSTFIX, combine(childExpressions.subList(1, childExpressions.size())), null, arg1, null);
      }
      else if (index == -1) {
        addWarning(rule + ": " + rootRuleName + " reference not found, treating as ATOM");
        info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
      }
      else {
        info = new OperatorInfo(rule, OperatorType.PREFIX, combine(childExpressions.subList(0, index)),
                                combine(childExpressions.subList(index + 1, childExpressions.size())), arg1, null);
      }
    }
    else if (cardinality == RuleGraphHelper.Cardinality.AT_LEAST_ONE) {
      // binary or n-ary expression
      int index1 = indexOf(rootRuleSubst, 0, childExpressions, expressionInfo);
      int index2 = indexOf(rootRuleSubst, 1, childExpressions, expressionInfo);
      if (index1 != 0) {
        addWarning(rule + ": binary or n-ary expression cannot have prefix, treating as ATOM");
        info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
      }
      else if (index2 == 1) {
        addWarning(rule + ": binary expression needs operator, treating as ATOM");
        info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
      }
      else {
        BnfRule arg1 = substRule(childExpressions, index1, rootRule);
        if (index2 == -1) {
          BnfExpression lastExpression = childExpressions.get(1);
          boolean badNAry = childExpressions.size() != 2 || !(lastExpression instanceof BnfQuantified) ||
                            !(((BnfQuantified)lastExpression).getQuantifier().getText().equals("+")) ||
                            !(((BnfQuantified)lastExpression).getExpression() instanceof BnfParenExpression);
          List<BnfExpression> childExpressions2 = badNAry ? Collections.emptyList() :
                                                  getChildExpressions(
                                                    ((BnfParenExpression)((BnfQuantified)lastExpression).getExpression()).getExpression());
          int index3 = indexOf(rootRuleSubst, 0, childExpressions2, expressionInfo);
          if (badNAry || index3 == -1) {
            addWarning(
              rule + ": '" + rootRuleName + " ( <op> " + rootRuleName + ") +' expected for N-ary operator, treating as POSTFIX"
            );
            info = new OperatorInfo(rule, OperatorType.POSTFIX, combine(childExpressions.subList(1, childExpressions.size())), null,
                                    arg1, null);
          }
          else {
            BnfRule arg2 = substRule(childExpressions2, index3, rootRule);
            info = new OperatorInfo(rule, OperatorType.N_ARY, combine(childExpressions2.subList(0, index3)),
                                    combine(childExpressions2.subList(index3 + 1, childExpressions2.size())), arg1, arg2);
          }
        }
        else {
          BnfRule arg2 = substRule(childExpressions, index2, rootRule);
          info = new OperatorInfo(rule, OperatorType.BINARY, combine(childExpressions.subList(index1 + 1, index2)),
                                  combine(childExpressions.subList(index2 + 1, childExpressions.size())), arg1, arg2);
        }
      }
    }
    else {
      addWarning(rule + ": unexpected cardinality " + cardinality + " of " + rootRuleName + ", treating as ATOM");
      info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
    }
    expressionInfo.operatorMap.put(rule, info);
  }

  /** Resolves the rule referenced at {@code list[idx]}, returning {@code null} when it's the root rule itself or {@code idx < 0}. */
  private @Nullable BnfRule substRule(List<BnfExpression> list, int idx, BnfRule rootRule) {
    if (idx < 0) return null;
    BnfRule rule = myFile.getRule(list.get(idx).getText());
    return rule == rootRule ? null : rule;
  }

  /**
   * Joins a slice of an operator's child expressions into a single synthetic
   * {@link BnfExpression}, preserving the original list under {@link #ORIGINAL_EXPRESSIONS} so
   * later analyses can recover it via {@link #getOriginalExpressions}. Returns the lone element
   * verbatim for size-1 slices and {@code null} for empty slices.
   */
  private static BnfExpression combine(List<BnfExpression> list) {
    if (list.isEmpty()) return null;
    if (list.size() == 1) return list.get(0);
    Project project = list.get(0).getProject();
    String text = StringUtil.join(list, PsiElement::getText, " ");
    BnfExpression result = BnfElementFactory.createExpressionFromText(project, text);
    result.putUserData(ORIGINAL_EXPRESSIONS, list);
    return result;
  }

  /** Recovers the original child slice that produced a {@link #combine}-synthesized expression, or returns {@code [expression]} for non-synthetic input. */
  public static @NotNull List<BnfExpression> getOriginalExpressions(@NotNull BnfExpression expression) {
    List<BnfExpression> data = expression.getUserData(ORIGINAL_EXPRESSIONS);
    return data == null ? Collections.singletonList(expression) : data;
  }

  /**
   * Adjusts a static cardinality to reflect runtime expression-parsing semantics: in an
   * expression cluster, a {@code REQUIRED} child becomes {@code OPTIONAL} unless it sits in a
   * pinned position (the first argument of a binary/n-ary/postfix operator, or anywhere inside
   * the operator subtree). Atoms and rules outside any cluster are returned unchanged.
   */
  public @NotNull RuleGraphHelper.Cardinality fixCardinality(BnfRule rule, PsiElement tree, @NotNull RuleGraphHelper.Cardinality type) {
    if (type.optional()) return type;
    // in Expression parsing mode REQUIRED may go OPTIONAL
    ExpressionInfo info = getExpressionInfo(rule);
    OperatorInfo operatorInfo = info == null ? null : info.operatorMap.get(rule);
    if (operatorInfo == null || operatorInfo.type() == OperatorType.ATOM) return type;

    // emulate expr-parsing pin processing
    if ((operatorInfo.type() == OperatorType.BINARY ||
         operatorInfo.type() == OperatorType.N_ARY ||
         operatorInfo.type() == OperatorType.POSTFIX) &&
        ObjectUtils.chooseNotNull(operatorInfo.arg1(), info.rootRule) == tree ||
        isRealAncestor(rule, operatorInfo.operator(), tree)) {
      // pinned! return as is
      return type;
    }
    else {
      return type.and(OPTIONAL);
    }
  }

  /**
   * True if {@code target} lives inside {@code expression} either directly or via an original
   * (pre-{@link #combine}) sub-expression. Drives the "is this child pinned by the operator?"
   * check in {@link #fixCardinality}.
   */
  private boolean isRealAncestor(BnfRule rule, BnfExpression expression, PsiElement target) {
    List<BnfExpression> list = getOriginalExpressions(expression);
    if (list.size() == 1 && PsiTreeUtil.isAncestor(list.get(0), target, false)) return true;
    for (BnfExpression expr : list) {
      Map<PsiElement, RuleGraphHelper.Cardinality> map = myRuleGraph.collectMembers(rule, expr, new LinkedHashSet<>());
      if (map.containsKey(target)) return true;
    }
    return false;
  }

  /**
   * Returns the index in {@code childExpressions} (starting at {@code startIndex}) of the
   * first reference to {@code rootRule} or any of its extends-rules / private priority groups,
   * or {@code -1} if none is found. Used to locate the recursive operator argument(s).
   */
  private int indexOf(BnfRule rootRule,
                      int startIndex,
                      List<BnfExpression> childExpressions,
                      ExpressionInfo expressionInfo) {
    Collection<BnfRule> extendsRules = myRuleGraph.getExtendsRules(rootRule);
    for (int i = startIndex, childExpressionsSize = childExpressions.size(); i < childExpressionsSize; i++) {
      BnfRule rule = myFile.getRule(childExpressions.get(i).getText());
      if (rootRule == rule || extendsRules.contains(rule) || expressionInfo.privateGroups.containsKey(rule)) {
        return i;
      }
    }
    return -1;
  }
}
