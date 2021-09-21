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
import com.intellij.util.PairConsumer;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.containers.JBTreeTraverser;
import com.intellij.util.containers.TreeTraversal;
import gnu.trove.THashMap;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.OPTIONAL;

/**
 * @author gregsh
 */
public class ExpressionHelper {
  private final BnfFile myFile;
  private final RuleGraphHelper myRuleGraph;
  private final Consumer<String> myWarningConsumer;

  private final Map<BnfRule, ExpressionInfo> myExpressionMap = new THashMap<>();
  private final Map<BnfRule, BnfRule> myRootRulesMap = new THashMap<>();

  private static final Key<CachedValue<ExpressionHelper>> EXPRESSION_HELPER_KEY = Key.create("EXPRESSION_HELPER_KEY");
  public static ExpressionHelper getCached(@NotNull BnfFile file) {
    CachedValue<ExpressionHelper> value = file.getUserData(EXPRESSION_HELPER_KEY);
    if (value == null) {
      file.putUserData(EXPRESSION_HELPER_KEY, value = CachedValuesManager.getManager(file.getProject()).createCachedValue(
        () -> new CachedValueProvider.Result<>(new ExpressionHelper(file, RuleGraphHelper.getCached(file), null), file), false));
    }
    return value.getValue();
  }


  public ExpressionHelper(BnfFile file, RuleGraphHelper ruleGraph, @Nullable Consumer<String> warningConsumer) {
    myFile = file;
    myRuleGraph = ruleGraph;
    myWarningConsumer = warningConsumer;
    buildExpressionRules();
  }

  public boolean hasExpressions() {
    return !myExpressionMap.isEmpty();
  }

  public void addWarning(String text) {
    if (myWarningConsumer == null) return;
    myWarningConsumer.accept(text);
  }

  public ExpressionInfo getExpressionInfo(BnfRule rule) {
    BnfRule root = myRootRulesMap.get(rule);
    ExpressionInfo info = root == null ? null : myExpressionMap.get(root);
    if (info == null) return null;
    if (info.rootRule == rule || Rule.isPrivate(rule)) return info;
    return info.priorityMap.containsKey(rule) ? info : null;
  }

  private void buildExpressionRules() {
    BnfFirstNextAnalyzer analyzer = BnfFirstNextAnalyzer.createAnalyzer(false);
    for (BnfRule rule : myFile.getRules()) {
      if (Rule.isPrivate(rule) || Rule.isFake(rule)) continue;
      if (myRootRulesMap.containsKey(rule)) continue;
      Map<PsiElement, RuleGraphHelper.Cardinality> contentRules = myRuleGraph.getFor(rule);
      if (!contentRules.isEmpty()) continue;
      if (!BnfFirstNextAnalyzer.asStrings(analyzer.calcFirst(rule)).contains(rule.getName())) continue;

      ExpressionInfo expressionInfo = new ExpressionInfo(rule);
      addToPriorityMap(rule, myRuleGraph.getExtendsRules(rule), expressionInfo);
      List<BnfRule> rules = ParserGeneratorUtil.topoSort(expressionInfo.priorityMap.keySet(), myRuleGraph);
      for (BnfRule r : rules) {
        buildOperatorMap(r, rule, expressionInfo);
      }
      if (!expressionInfo.priorityMap.isEmpty()) {
        myRootRulesMap.put(rule, rule);
        myExpressionMap.put(rule, expressionInfo);
      }
      ops: for (OperatorInfo info : expressionInfo.operatorMap.values()) {
        Map<PsiElement, RuleGraphHelper.Cardinality> map = myRuleGraph.collectMembers(info.rule, info.operator, new HashSet<>());
        for (RuleGraphHelper.Cardinality c : map.values()) {
          if (!c.optional()) continue ops;
        }
        expressionInfo.checkEmpty.add(info);
      }
    }
  }

  private void addToPriorityMap(BnfRule rule, Collection<BnfRule> rulesCluster, ExpressionInfo info) {
    JBTreeTraverser<BnfRule> traverser = new JBTreeTraverser<>(
      o -> ObjectUtils.notNull(rule == o || Rule.isPrivate(o) ? myRuleGraph.getSubRules(o) : null, Collections.emptyList()));
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
        if (!Rule.isPrivate(subRule) || !myRuleGraph.getFor(subRule).isEmpty()) {
          info.priorityMap.put(subRule, priority);
        }
      }
      else if (Rule.isPrivate(subRule)) {
        info.privateGroups.put(subRule, priority);
      }
      else {
        addWarning(String.format("'%s' is not an expression rule nor private priority group", subRule.getName()));
      }
    }
  }

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
    if (Rule.isExternal(rule)) {
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
        addWarning(rule +": " + rootRuleName + " reference not found, treating as ATOM");
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
        addWarning(rule +": binary or n-ary expression cannot have prefix, treating as ATOM");
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
      addWarning(rule +": unexpected cardinality " + cardinality + " of " + rootRuleName +", treating as ATOM");
      info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
    }
    expressionInfo.operatorMap.put(rule, info);
  }

  private @Nullable BnfRule substRule(List<BnfExpression> list, int idx, BnfRule rootRule) {
    if (idx < 0) return null;
    BnfRule rule = myFile.getRule(list.get(idx).getText());
    return rule == rootRule? null : rule;
  }

  private static final Key<List<BnfExpression>> ORIGINAL_EXPRESSIONS = Key.create("ORIGINAL_EXPRESSIONS");
  private static BnfExpression combine(List<BnfExpression> list) {
    if (list.isEmpty()) return null;
    if (list.size() == 1) return list.get(0);
    Project project = list.get(0).getProject();
    String text = StringUtil.join(list, PsiElement::getText, " ");
    BnfExpression result = BnfElementFactory.createExpressionFromText(project, text);
    result.putUserData(ORIGINAL_EXPRESSIONS, list);
    return result;
  }

  public static @NotNull List<BnfExpression> getOriginalExpressions(BnfExpression expression) {
    List<BnfExpression> data = expression.getUserData(ORIGINAL_EXPRESSIONS);
    return data == null ? Collections.singletonList(expression) : data;
  }

  public @NotNull RuleGraphHelper.Cardinality fixCardinality(BnfRule rule, PsiElement tree, RuleGraphHelper.Cardinality type) {
    if (type.optional()) return type;
    // in Expression parsing mode REQUIRED may go OPTIONAL
    ExpressionHelper.ExpressionInfo info = getExpressionInfo(rule);
    ExpressionHelper.OperatorInfo operatorInfo = info == null ? null : info.operatorMap.get(rule);
    if (operatorInfo == null || operatorInfo.type == ExpressionHelper.OperatorType.ATOM) return type;

    // emulate expr-parsing pin processing
    if ((operatorInfo.type == OperatorType.BINARY ||
         operatorInfo.type == OperatorType.N_ARY ||
         operatorInfo.type == OperatorType.POSTFIX) &&
        ObjectUtils.chooseNotNull(operatorInfo.arg1, info.rootRule) == tree ||
        isRealAncestor(rule, operatorInfo.operator, tree)) {
      // pinned! return as is
      return type;
    }
    else {
      return type.and(OPTIONAL);
    }
  }

  private boolean isRealAncestor(BnfRule rule, BnfExpression expression, PsiElement target) {
    List<BnfExpression> list = getOriginalExpressions(expression);
    if (list.size() == 1 && PsiTreeUtil.isAncestor(list.get(0), target, false)) return true;
    for (BnfExpression expr : list) {
      Map<PsiElement, RuleGraphHelper.Cardinality> map = myRuleGraph.collectMembers(rule, expr, new LinkedHashSet<>());
      if (map.containsKey(target)) return true;
    }
    return false;
  }

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

  public static class ExpressionInfo {
    public final BnfRule rootRule;
    public final Map<BnfRule, Integer> priorityMap = new LinkedHashMap<>();
    public final Map<BnfRule, OperatorInfo> operatorMap = new LinkedHashMap<>();
    public final Map<BnfRule, Integer> privateGroups = new HashMap<>();
    public int nextPriority;
    public final Set<OperatorInfo> checkEmpty = new HashSet<>();

    public ExpressionInfo(BnfRule rootRule) {
      this.rootRule = rootRule;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("Expression root: " + rootRule.getName());
      sb.append("\nOperator priority table:\n");
      return dumpPriorityTable(sb).toString();
    }

    public StringBuilder dumpPriorityTable(StringBuilder sb) {
      return dumpPriorityTable(sb, StringBuilder::append);
    }

    public StringBuilder dumpPriorityTable(StringBuilder sb, PairConsumer<StringBuilder, OperatorInfo> printer) {
      for (int i = 0; i < nextPriority; i++) {
        sb.append(i).append(":");
        int count = 0;
        for (BnfRule rule : priorityMap.keySet()) {
          if (priorityMap.get(rule) == i) {
            if ((count ++ % 4) == 0 && count > 1) sb.append("\n  ");
            sb.append(" ");
            printer.consume(sb, operatorMap.get(rule));
          }
        }
        sb.append("\n");
      }
      return sb;
    }

    public int getPriority(BnfRule subRule) {
      if (subRule == rootRule) return 0;
      Integer op = priorityMap.get(subRule);
      if (op != null) return op;
      Integer group = privateGroups.get(subRule);
      return group == null ? -1 : group;
    }
  }

  public enum OperatorType {ATOM, PREFIX, POSTFIX, BINARY, N_ARY}

  public static class OperatorInfo {
    public final BnfRule rule;
    public final OperatorType type;
    public final BnfExpression operator;
    public final BnfExpression tail; // null for postfix
    public final BnfRule arg1;
    public final BnfRule arg2;

    public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail) {
      this(rule, type, operator, tail, null, null);
    }

    public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail,
                        @Nullable BnfRule arg1,
                        @Nullable BnfRule arg2) {
      if (operator == null) {
        throw new AssertionError(rule + ": operator must not be null");
      }
      this.rule = rule;
      this.type = type;
      this.operator = operator;
      this.tail = tail;
      this.arg1 = arg1;
      this.arg2 = arg2;
    }

    @Override
    public String toString() {
      return type + "(" + rule.getName() + ")";
    }
  }
}
