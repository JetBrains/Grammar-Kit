/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.PairConsumer;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.Rule;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getChildExpressions;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.OPTIONAL;

/**
 * @author gregsh
 */
public class ExpressionHelper {
    public static final Function<BnfExpression, String> TO_STRING = new Function<BnfExpression, String>() {
    @Override
    public String fun(BnfExpression expression) {
      return expression.getText();
    }
  };
  private final BnfFile myFile;
  private final RuleGraphHelper myRuleGraph;
  private final boolean myAddWarnings;

  private final Map<BnfRule, ExpressionInfo> myExpressionMap = ContainerUtil.newTroveMap();
  private final Map<BnfRule, BnfRule> myRootRulesMap = ContainerUtil.newTroveMap();

  private static final Key<CachedValue<ExpressionHelper>> EXPRESSION_HELPER_KEY = Key.create("EXPRESSION_HELPER_KEY");
  public static ExpressionHelper getCached(final BnfFile file) {
    CachedValue<ExpressionHelper> value = file.getUserData(EXPRESSION_HELPER_KEY);
    if (value == null) {
      file.putUserData(EXPRESSION_HELPER_KEY, value = CachedValuesManager.getManager(file.getProject()).createCachedValue(new CachedValueProvider<ExpressionHelper>() {
          @Nullable
          @Override
          public Result<ExpressionHelper> compute() {
            return new Result<ExpressionHelper>(new ExpressionHelper(file, RuleGraphHelper.getCached(file), false), file);
          }
        }, false));
    }
    return value.getValue();
  }


  public ExpressionHelper(BnfFile file, RuleGraphHelper ruleGraph, boolean addWarnings) {
    myFile = file;
    myRuleGraph = ruleGraph;
    myAddWarnings = addWarnings;
    buildExpressionRules();
  }

  public boolean hasExpressions() {
    return !myExpressionMap.isEmpty();
  }

  public void addWarning(String text) {
    if (!myAddWarnings) return;
    ParserGeneratorUtil.addWarning(myFile.getProject(), text);
  }

  public ExpressionInfo getExpressionInfo(BnfRule rule) {
    BnfRule root = myRootRulesMap.get(rule);
    ExpressionInfo info = root == null ? null : myExpressionMap.get(root);
    if (info == null) return null;
    if (info.rootRule == rule || Rule.isPrivate(rule)) return info;
    return info.priorityMap.containsKey(rule) ? info : null;
  }

  private void buildExpressionRules() {
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
    for (BnfRule rule : myFile.getRules()) {
      if (Rule.isPrivate(rule) || Rule.isFake(rule)) continue;
      if (myRootRulesMap.containsKey(rule)) continue;
      Map<PsiElement, RuleGraphHelper.Cardinality> contentRules = myRuleGraph.getFor(rule);
      if (!contentRules.isEmpty()) continue;
      if (!analyzer.asStrings(analyzer.calcFirst(rule)).contains(rule.getName())) continue;

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
        Map<PsiElement, RuleGraphHelper.Cardinality> map = myRuleGraph.collectMembers(info.rule, info.operator, ContainerUtil.newHashSet());
        for (RuleGraphHelper.Cardinality c : map.values()) {
          if (!c.optional()) continue ops;
        }
        expressionInfo.checkEmpty.add(info);
      }
    }
  }

  private void addToPriorityMap(BnfRule rule, Collection<BnfRule> rulesCluster, ExpressionInfo info) {
    Collection<BnfRule> subRules = myRuleGraph.getSubRules(rule);
    int priority = rulesCluster.contains(rule) ? -1 : info.nextPriority++;

    for (BnfRule subRule : subRules) {
      if (info.priorityMap.containsKey(subRule)) {
        addWarning(subRule + " has duplicate appearance!");
        continue;
      }
      BnfRule prev = myRootRulesMap.put(subRule, info.rootRule);
      if (prev != null) {
        addWarning(
            subRule + " must not be in several expression hierarchies: " + prev.getName() + " and " + info.rootRule.getName());
      }

      if (rulesCluster.contains(subRule)) {
        if (!Rule.isPrivate(subRule) || !myRuleGraph.getFor(subRule).isEmpty()) {
          info.priorityMap.put(subRule, priority == -1 ? info.nextPriority++ : priority);
        }
      }
      else if (ParserGeneratorUtil.Rule.isPrivate(subRule)) {
        addToPriorityMap(subRule, rulesCluster, info);
        info.privateGroups.add(subRule);
      }
      else {
        addWarning(subRule + ": priority group must be 'private'");
      }
    }
  }

  private void buildOperatorMap(BnfRule rule, BnfRule rootRule, ExpressionInfo expressionInfo) {
    Map<PsiElement, RuleGraphHelper.Cardinality> ruleContent = myRuleGraph.getFor(rule);
    RuleGraphHelper.Cardinality cardinality = ruleContent.get(rootRule);
    BnfRule rootRuleSubst = rootRule;
    if (cardinality == null) {
      Collection<BnfRule> extendsRules = myRuleGraph.getExtendsRules(rootRule);
      for (PsiElement r : ruleContent.keySet()) {
        if (r instanceof BnfRule && extendsRules.contains(r)) {
          cardinality = ruleContent.get(r);
          rootRuleSubst = (BnfRule)r;
          break;
        }
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
          List<BnfExpression> childExpressions2 = badNAry ? Collections.<BnfExpression>emptyList() :
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

  @Nullable
  private BnfRule substRule(List<BnfExpression> list, int idx, BnfRule rootRule) {
    if (idx < 0) return null;
    BnfRule rule = myFile.getRule(list.get(idx).getText());
    return rule == rootRule? null : rule;
  }

  private static final Key<List<BnfExpression>> ORIGINAL_EXPRESSIONS = Key.create("ORIGINAL_EXPRESSIONS");
  private static BnfExpression combine(List<BnfExpression> list) {
    if (list.isEmpty()) return null;
    if (list.size() == 1) return list.get(0);
    Project project = list.get(0).getProject();
    String text = StringUtil.join(list, TO_STRING, " ");
    BnfExpression result = BnfElementFactory.createExpressionFromText(project, text);
    result.putUserData(ORIGINAL_EXPRESSIONS, list);
    return result;
  }

  @NotNull
  public static List<BnfExpression> getOriginalExpressions(BnfExpression expression) {
    List<BnfExpression> data = expression.getUserData(ORIGINAL_EXPRESSIONS);
    return data == null ? Collections.singletonList(expression) : data;
  }

  @NotNull
  public RuleGraphHelper.Cardinality fixCardinality(BnfRule rule, PsiElement tree, RuleGraphHelper.Cardinality type) {
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
      Map<PsiElement, RuleGraphHelper.Cardinality> map = myRuleGraph.collectMembers(rule, expr, ContainerUtil.newLinkedHashSet());
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
      if (rootRule == rule || extendsRules.contains(rule) || expressionInfo.privateGroups.contains(rule)) {
        return i;
      }
    }
    return -1;
  }

  public static class ExpressionInfo {
    public final BnfRule rootRule;
    public final Map<BnfRule, Integer> priorityMap = ContainerUtil.newLinkedHashMap();
    public final Map<BnfRule, OperatorInfo> operatorMap = ContainerUtil.newLinkedHashMap();
    public final Set<BnfRule> privateGroups = ContainerUtil.newHashSet();
    public int nextPriority;
    public final Set<OperatorInfo> checkEmpty = ContainerUtil.newHashSet();

    public ExpressionInfo(BnfRule rootRule) {
      this.rootRule = rootRule;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("Expression root: " + rootRule.getName());
      sb.append("\nOperator priority table:\n");
      dumpPriorityTable(sb);
      return sb.toString();
    }

    public StringBuilder dumpPriorityTable(StringBuilder sb) {
      return dumpPriorityTable(sb, new PairConsumer<StringBuilder, OperatorInfo>() {
        @Override
        public void consume(StringBuilder sb, OperatorInfo operatorInfo) {
          sb.append(operatorInfo);
        }
      });
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
      Integer integer = priorityMap.get(subRule);
      return integer == null ? -1 : integer;
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
