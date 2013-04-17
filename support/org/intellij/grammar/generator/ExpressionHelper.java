/*
 * Copyright 2011-2013 Gregory Shrago
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
import com.intellij.util.Function;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

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
  private boolean myAddWarnings;

  private final Map<BnfRule, ExpressionInfo> myExpressionMap = new HashMap<BnfRule, ExpressionInfo>();
  private final Map<BnfRule, BnfRule> myRootRulesMap = new HashMap<BnfRule, BnfRule>();

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
      List<BnfRule> rules = new ArrayList<BnfRule>(expressionInfo.priorityMap.keySet());
      rules = ParserGeneratorUtil.topoSort(rules, new Topology<BnfRule>() {
        @Override
        public boolean contains(BnfRule t1, BnfRule t2) {
          return myRuleGraph.getSubRules(t1).contains(t2);
        }
      });
      for (BnfRule r : rules) {
        buildOperatorMap(r, rule, expressionInfo.operatorMap);
      }
      if (!expressionInfo.priorityMap.isEmpty()) {
        myRootRulesMap.put(rule, rule);
        myExpressionMap.put(rule, expressionInfo);
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
      }
      else {
        addWarning(subRule + ": priority group must be 'private'");
      }
    }
  }

  private void buildOperatorMap(BnfRule rule, BnfRule rootRule, Map<BnfRule, OperatorInfo> operatorMap) {
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
      int index = indexOf(rootRuleSubst, 0, childExpressions, operatorMap);
      BnfRule substRule = substRule(childExpressions, index, rootRule);
      if (index == 0) {
        info = new OperatorInfo(rule, OperatorType.POSTFIX, combine(childExpressions.subList(1, childExpressions.size())), null, substRule);
      }
      else if (index == -1) {
        addWarning(rule +": " + rootRuleName + " reference not found, treating as ATOM");
        info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
      }
      else {
        info = new OperatorInfo(rule, OperatorType.PREFIX, combine(childExpressions.subList(0, index)),
                                combine(childExpressions.subList(index + 1, childExpressions.size())), substRule);
      }
    }
    else if (cardinality == RuleGraphHelper.Cardinality.AT_LEAST_ONE) {
      // binary or n-ary expression
      int index = indexOf(rootRuleSubst, 0, childExpressions, operatorMap);
      if (index != 0) {
        addWarning(rule +": binary or n-ary expression cannot have prefix, treating as ATOM");
        info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
      }
      else {
        int index2 = indexOf(rootRuleSubst, 1, childExpressions, operatorMap);
        BnfRule substRule = substRule(childExpressions, index, rootRule);
        if (index2 == -1) {
          BnfExpression lastExpression = childExpressions.get(1);
          boolean badNAry = childExpressions.size() != 2 || !(lastExpression instanceof BnfQuantified) ||
                            !(((BnfQuantified)lastExpression).getQuantifier().getText().equals("+")) ||
                            !(((BnfQuantified)lastExpression).getExpression() instanceof BnfParenExpression);
          List<BnfExpression> childExpressions2 = badNAry ? Collections.<BnfExpression>emptyList() :
                                                  getChildExpressions(
                                                    ((BnfParenExpression)((BnfQuantified)lastExpression).getExpression()).getExpression());
          int index3 = indexOf(rootRuleSubst, 0, childExpressions2, operatorMap);
          if (badNAry || index3 == -1) {
            addWarning(
                rule + ": '" + rootRuleName + " ( <op> " + rootRuleName + ") +' expected for N-ary operator, treating as POSTFIX"
            );
            info = new OperatorInfo(rule, OperatorType.POSTFIX, combine(childExpressions.subList(1, childExpressions.size())), null,
                                    substRule);
          }
          else {
            info = new OperatorInfo(rule, OperatorType.N_ARY, combine(childExpressions2.subList(0, index3)),
                                    combine(childExpressions2.subList(index3 + 1, childExpressions2.size())), substRule);
          }
        }
        else {
          info = new OperatorInfo(rule, OperatorType.BINARY, combine(childExpressions.subList(index + 1, index2)),
                                  combine(childExpressions.subList(index2 + 1, childExpressions.size())), substRule);
        }
      }
    }
    else {
      addWarning(rule +": unexpected cardinality " + cardinality + " of " + rootRuleName +", treating as ATOM");
      info = new OperatorInfo(rule, OperatorType.ATOM, rule.getExpression(), null);
    }
    operatorMap.put(rule, info);
  }

  @Nullable
  private BnfRule substRule(List<BnfExpression> list, int idx, BnfRule rootRule) {
    if (idx < 0) return null;
    BnfRule rule = myFile.getRule(list.get(idx).getText());
    return rule == rootRule? null : rule;
  }

  private static BnfExpression combine(List<BnfExpression> list) {
    if (list.isEmpty()) return null;
    if (list.size() == 1) return list.get(0);
    Project project = list.get(0).getProject();
    String text = StringUtil.join(list, TO_STRING, " ");
    return BnfElementFactory.createExpressionFromText(project, text);
  }

  private int indexOf(BnfRule rootRule,
                      int startIndex,
                      List<BnfExpression> childExpressions,
                      Map<BnfRule, OperatorInfo> operatorMap) {
    Collection<BnfRule> extendsRules = myRuleGraph.getExtendsRules(rootRule);
    for (int i = startIndex, childExpressionsSize = childExpressions.size(); i < childExpressionsSize; i++) {
      BnfRule rule = myFile.getRule(childExpressions.get(i).getText());
      if (rootRule == rule || extendsRules.contains(rule)) {
        OperatorInfo info = operatorMap.get(rule);
        if (info != null && info.type == OperatorType.ATOM) continue;
        return i;
      }
    }
    return -1;
  }

  public static class ExpressionInfo {
    public final BnfRule rootRule;
    public final Map<BnfRule, Integer> priorityMap = new LinkedHashMap<BnfRule, Integer>();
    public final Map<BnfRule, OperatorInfo> operatorMap = new LinkedHashMap<BnfRule, OperatorInfo>();
    public int nextPriority;

    public ExpressionInfo(BnfRule rootRule) {
      this.rootRule = rootRule;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("Expression root: " + rootRule.getName());
      sb.append("\nOperator priority table:\n");
      for (int i = 0; i < nextPriority; i++) {
        sb.append(i).append(":");
        for (BnfRule rule : priorityMap.keySet()) {
          if (priorityMap.get(rule) == i) {
            OperatorInfo operatorInfo = operatorMap.get(rule);
            sb.append(" ").append(operatorInfo);
          }
        }
        sb.append("\n");
      }
      return sb.toString();
    }

    public int getPriority(BnfRule subRule) {
      if (subRule == rootRule) return 0;
      Integer integer = priorityMap.get(subRule);
      return integer == null ? -1 : integer;
    }
  }

  public static enum OperatorType {ATOM, PREFIX, POSTFIX, BINARY, N_ARY}

  public static class OperatorInfo {
    public final BnfRule rule;
    public final OperatorType type;
    public final BnfExpression operator;
    public final BnfExpression tail; // null for postfix
    public final BnfRule substitutor;

    public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail) {
      this(rule, type, operator, tail, null);
    }

    public OperatorInfo(BnfRule rule, OperatorType type, BnfExpression operator, BnfExpression tail, @Nullable BnfRule substitutor) {
      assert operator != null : rule + ": operator must not be null";
      this.rule = rule;
      this.type = type;
      this.operator = operator;
      this.tail = tail;
      this.substitutor = substitutor;
    }

    @Override
    public String toString() {
      return type + "(" + rule.getName() + ")";
    }
  }
}
