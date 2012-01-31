/*
 * Copyright 2011-2011 Gregory Shrago
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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.MultiMapBasedOnSet;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;
import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;
import static org.intellij.grammar.psi.impl.GrammarUtil.*;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class RuleGraphHelper {
  private final BnfFile myFile;
  private final MultiMap<BnfRule, BnfRule> myRuleExtendsMap;
  private final Map<BnfRule, Map<PsiElement, Cardinality>> myMap;
  private final MultiMap<BnfRule,BnfRule> myRulesGraph;

  public enum Cardinality {
    NONE, OPTIONAL, REQUIRED, AT_LEAST_ONE, ANY_NUMBER;

    boolean optional() {
      return this == OPTIONAL || this == ANY_NUMBER || this == NONE;
    }

    boolean many() {
      return this == AT_LEAST_ONE || this == ANY_NUMBER;
    }

    public static Cardinality fromNodeType(IElementType type) {
      if (type == BnfTypes.BNF_OP_OPT) {
        return OPTIONAL;
      }
      else if (type == BnfTypes.BNF_SEQUENCE || type == BnfTypes.BNF_REFERENCE_OR_TOKEN) {
        return REQUIRED;
      }
      else if (type == BnfTypes.BNF_CHOICE) {
        return OPTIONAL;
      }
      else if (type == BnfTypes.BNF_OP_ONEMORE) {
        return AT_LEAST_ONE;
      }
      else if (type == BnfTypes.BNF_OP_ZEROMORE) {
        return ANY_NUMBER;
      }
      else {
        throw new AssertionError("unexpected: " + type);
      }
    }

    public Cardinality and(Cardinality c) {
      if (this == NONE || c == NONE) return NONE;
      if (optional() || c.optional()) {
        return many() || c.many() ? ANY_NUMBER : OPTIONAL;
      }
      else {
        return many() || c.many() ? AT_LEAST_ONE : REQUIRED;
      }
    }

    public Cardinality or(Cardinality c) {
      if (c == null) c = NONE;
      if (this == NONE && c == NONE) return NONE;
      if (this == NONE) return c;
      if (c == NONE) return this;
      return optional() && c.optional() ? ANY_NUMBER : AT_LEAST_ONE;
    }
  }

  public RuleGraphHelper(BnfFile file, MultiMap<BnfRule, BnfRule> ruleExtendsMap) {
    myFile = file;
    myRuleExtendsMap = ruleExtendsMap;
    myMap = new THashMap<BnfRule, Map<PsiElement, Cardinality>>();

    myRulesGraph = new MultiMapBasedOnSet<BnfRule, BnfRule>();
    for (BnfRule rule : myFile.getRules()) {
      BnfExpression expression = rule.getExpression();
      for (PsiElement cur = nextOrParent(expression.getPrevSibling(), expression); cur != null; cur =
        nextOrParent(cur, expression) ) {
        BnfRule r = cur instanceof BnfReferenceOrToken && PsiTreeUtil.getParentOfType(cur, BnfPredicate.class) == null? myFile.getRule(cur.getText()) : null;
        if (r != null) myRulesGraph.putValue(rule, r);
      }
    }
    for (BnfRule rule : myFile.getRules()) {
      if (Rule.isLeft(rule) && !Rule.isPrivate(rule) && !Rule.isInner(rule)) {
        for (BnfRule r : getRulesToTheLeft(rule).keySet()) {
          myRulesGraph.putValue(rule, r);
        }
      }
    }
    THashSet<PsiElement> visited = new THashSet<PsiElement>();
    for (BnfRule rule : myFile.getRules()) {
      Map<PsiElement, Cardinality> map = collectMembers(rule, rule.getExpression(), rule.getName(), visited);
      myMap.put(rule, map.size() == 1 && map.containsKey(rule) ? Collections.<PsiElement, Cardinality>emptyMap() : map);
      visited.clear();
    }
  }

  @NotNull
  public Map<PsiElement, Cardinality> getFor(BnfRule rule) {
    return myMap.get(rule);
  }

  private Map<PsiElement, Cardinality> collectMembers(BnfRule rule, BnfExpression tree, String funcName, Set<PsiElement> visited) {
    if (tree instanceof BnfPredicate) return Collections.emptyMap();
    if (tree instanceof BnfLiteralExpression) return psiMap(tree, REQUIRED);

    if (!visited.add(tree)) return psiMap(tree, REQUIRED);

    Map<PsiElement, Cardinality> result;
    if (tree instanceof BnfReferenceOrToken) {
      BnfRule targetRule = myFile.getRule(tree.getText());
      if (targetRule != null) {
        if (Rule.isExternal(targetRule) || Rule.isLeft(targetRule)) {
          result = Collections.emptyMap();
        }
        else if (Rule.isPrivate(targetRule)) {
          BnfExpression body = targetRule.getExpression();
          Map<PsiElement, Cardinality> map = collectMembers(targetRule, body, targetRule.getName(), visited);
          result = map.containsKey(body) ? joinMaps(null, BnfTypes.BNF_CHOICE, Arrays.asList(map, map)) : map;
        }
        else {
          result = psiMap(targetRule, REQUIRED);
        }
      }
      else {
        result = psiMap(tree, REQUIRED);
      }
    }
    else if (tree instanceof BnfExternalExpression) {
      List<BnfExpression> expressionList = ((BnfExternalExpression)tree).getExpressionList();
      if (expressionList.size() == 1 && Rule.isMeta(rule)) {
        result = psiMap(tree, REQUIRED);
      }
      else {
        BnfExpression ruleRef = expressionList.get(0);
        BnfRule metaRule = myFile.getRule(ruleRef.getText());
        if (metaRule == null) {
          result = Collections.emptyMap();
        }
        else if (Rule.isPrivate(metaRule)) {
          result = new HashMap<PsiElement, Cardinality>();
          Map<PsiElement, Cardinality> metaResults = collectMembers(rule, ruleRef, funcName, new HashSet<PsiElement>());
          List<String> params = null;
          for (PsiElement member : metaResults.keySet()) {
            Cardinality cardinality = metaResults.get(member);
            if (!(member instanceof BnfExternalExpression)) {
              result.put(member, cardinality);
            }
            else {
              if (params == null) {
                params = collectExtraArguments(metaRule, metaRule.getExpression());
              }
              int idx = params.indexOf(member.getText());
              if (idx > -1 && idx + 1 < expressionList.size()) {
                Map<PsiElement, Cardinality> argMap = collectMembers(rule, expressionList.get(idx + 1), getNextName(funcName, idx), visited);
                for (PsiElement element : argMap.keySet()) {
                  result.put(element, cardinality.and(argMap.get(element)));
                }
              }
            }
          }
        }
        else {
          result = psiMap(metaRule, REQUIRED);
        }
      }
    }
    else {
      IElementType type = getEffectiveType(tree);
      boolean firstNonTrivial = tree == Rule.firstNotTrivial(rule);
      PinMatcher pinMatcher = new PinMatcher(rule, type, firstNonTrivial ? rule.getName() : funcName);
      boolean pinApplied = false;

      List<Map<PsiElement, Cardinality>> list = new ArrayList<Map<PsiElement, Cardinality>>();
      List<BnfExpression> childExpressions = getChildExpressions(tree);
      for (int i = 0, childExpressionsSize = childExpressions.size(); i < childExpressionsSize; i++) {
        BnfExpression child = childExpressions.get(i);
        Map<PsiElement, Cardinality> nextMap = collectMembers(rule, child, getNextName(funcName, i), visited);
        if (pinApplied) {
          nextMap = joinMaps(null, BnfTypes.BNF_OP_OPT, Collections.singletonList(nextMap));
        }
        list.add(nextMap);
        if (type == BNF_SEQUENCE && !pinApplied && pinMatcher.matches(i, child)) {
          pinApplied = true;
        }
      }
      result = joinMaps(firstNonTrivial ? rule : null, type, list);
    }
    if (rule.getExpression() == tree && Rule.isLeft(rule) && !Rule.isPrivate(rule) && !Rule.isInner(rule)) {
      List<Map<PsiElement, Cardinality>> list = new ArrayList<Map<PsiElement, Cardinality>>();
      Map<BnfRule, Cardinality> rulesToTheLeft = getRulesToTheLeft(rule);
      for (BnfRule r : rulesToTheLeft.keySet()) {
        Cardinality cardinality = rulesToTheLeft.get(r);
        Map<PsiElement, Cardinality> leftMap = psiMap(r, REQUIRED);
        if (cardinality.many()) {
          list.add(joinMaps(null, BnfTypes.BNF_CHOICE, Arrays.asList(leftMap, psiMap(rule, REQUIRED))));
        }
        else {
          list.add(leftMap);
        }
      }
      result = joinMaps(null, BnfTypes.BNF_SEQUENCE, Arrays.asList(result, joinMaps(null, BnfTypes.BNF_CHOICE, list)));
    }
    return result;
  }

  private Map<BnfRule, Cardinality> getRulesToTheLeft(BnfRule rule) {
    Map<BnfRule, Cardinality> result = new HashMap<BnfRule, Cardinality>();
    for (PsiReference reference : ReferencesSearch.search(rule, rule.getUseScope()).findAll()) {
      PsiElement element = reference.getElement();
      if (!(element instanceof BnfExpression)) continue;
      if (PsiTreeUtil.getParentOfType(element, BnfPredicate.class) != null) continue;
      BnfRule hostRule = Rule.of((BnfExpression)element);
      Cardinality cardinality = REQUIRED;
      for (PsiElement e = prevOrParent(element, hostRule); e != null; e = prevOrParent(e, hostRule)) {
        if (PsiTreeUtil.isAncestor(e, element, true)) {
          IElementType curType = getEffectiveType(e);
          if (curType == BnfTypes.BNF_OP_OPT || curType == BnfTypes.BNF_OP_ONEMORE || curType == BnfTypes.BNF_OP_ZEROMORE) {
            cardinality = cardinality.and(Cardinality.fromNodeType(curType));
          }
        }
        BnfRule leftRule = e instanceof BnfReferenceOrToken ? myFile.getRule(e.getText()) : null;
        if (leftRule == null || Rule.isInner(leftRule) || (Rule.isPrivate(leftRule) && Rule.isLeft(leftRule))) continue;
        if (PsiTreeUtil.getParentOfType(e, BnfPredicate.class) != null) continue;
        boolean found = false;
        if (!Rule.isPrivate(leftRule)) {
          result.put(leftRule, cardinality);
          found = true;
        }
        else {
          for (BnfRule r : myRulesGraph.get(leftRule)) {
            if (Rule.isInner(r) || (Rule.isPrivate(r) && Rule.isLeft(r))) continue;
            result.put(r, cardinality);
            found = true;
          }
        }
        if (found) break;
      }
    }
    return result;
  }

  private Map<PsiElement, Cardinality> joinMaps(@Nullable BnfRule rule, IElementType type, List<Map<PsiElement, Cardinality>> list) {
    if (list.isEmpty()) return Collections.emptyMap();
    boolean checkInheritance = rule != null && myRuleExtendsMap.containsScalarValue(rule);
    if (type == BnfTypes.BNF_OP_OPT || type == BnfTypes.BNF_OP_ZEROMORE || type == BnfTypes.BNF_OP_ONEMORE) {
      assert list.size() == 1;
      list = compactInheritors(list);
      Map<PsiElement, Cardinality> m = list.get(0);
      if (type == BnfTypes.BNF_OP_OPT && checkInheritance && m.size() == 1 && collapseNode(rule, m.keySet().iterator().next())) {
        return Collections.emptyMap();
      }
      Map<PsiElement, Cardinality> map = new HashMap<PsiElement, Cardinality>();
      for (PsiElement t : m.keySet()) {
        map.put(t, fromNodeType(type).and(m.get(t)));
      }
      return map;
    }
    else if (type == BnfTypes.BNF_SEQUENCE || type == BnfTypes.BNF_EXPRESSION || type == BnfTypes.BNF_REFERENCE_OR_TOKEN) {
      list = new ArrayList<Map<PsiElement, Cardinality>>(compactInheritors(list));
      for (Iterator<Map<PsiElement, Cardinality>> it = list.iterator(); it.hasNext(); ) {
        if (it.next().isEmpty()) it.remove();
      }
      if (list.size() == 1) {
        Map<PsiElement, Cardinality> m = list.get(0);
        if (checkInheritance && m.size() == 1 && collapseNode(rule, m.keySet().iterator().next())) {
          return Collections.emptyMap();
        }
        return m;
      }
      Map<PsiElement, Cardinality> map = new HashMap<PsiElement, Cardinality>();
      for (Map<PsiElement, Cardinality> m : list) {
        for (PsiElement t : m.keySet()) {
          map.put(t, m.get(t).or(map.get(t)));
        }
      }
      return map;
    }
    else if (type == BnfTypes.BNF_CHOICE) {
      Map<PsiElement, Cardinality> map = new HashMap<PsiElement, Cardinality>();
      list = compactInheritors(list);
      for (int i = 0, newListSize = list.size(); i < newListSize; i++) {
        Map<PsiElement, Cardinality> m = list.get(i);
        if (checkInheritance && m.size() == 1 && collapseNode(rule, m.keySet().iterator().next())) {
          list.set(i, Collections.<PsiElement, Cardinality>emptyMap());
        }
      }
      Map<PsiElement, Cardinality> m0 = list.get(0);
      map.putAll(m0);
      for (Map<PsiElement, Cardinality> m : list) {
        map.keySet().retainAll(m.keySet());
      }
      for (PsiElement t : new ArrayList<PsiElement>(map.keySet())) {
        map.put(t, REQUIRED.and(m0.get(t)));
        for (Map<PsiElement, Cardinality> m : list) {
          if (m == list.get(0)) continue;
          map.put(t, map.get(t).and(m.get(t)));
        }
      }
      for (Map<PsiElement, Cardinality> m : list) {
        for (PsiElement t : m.keySet()) {
          if (map.containsKey(t)) continue;
          if (checkInheritance && collapseNode(rule, t)) continue;
          map.put(t, OPTIONAL.and(m.get(t)));
        }
      }
      return map;
    }
    else {
      throw new AssertionError("unexpected: " + type);
    }
  }

  private boolean collapseNode(BnfRule rule, PsiElement t) {
    if (!(t instanceof BnfRule)) return false;
    for (BnfRule superRule : myRuleExtendsMap.keySet()) {
      Collection<BnfRule> set = myRuleExtendsMap.get(superRule);
      if (set.contains(t) && set.contains(rule)) {
        return true;
      }
    }
    return false;
  }

  // damn java generics
  public static <V> java.util.Map<PsiElement, V> psiMap(PsiElement k, V v) {
    return Collections.singletonMap(k, v);
  }
  
  private List<Map<PsiElement, Cardinality>> compactInheritors(List<Map<PsiElement, Cardinality>> mapList) {
    Set<BnfRule> rulesToTry = new THashSet<BnfRule>();
    // collect all rules
    for (Map<PsiElement, Cardinality> map : mapList) {
      for (PsiElement psiElement : map.keySet()) {
        if (!(psiElement instanceof BnfRule)) continue;
        rulesToTry.add((BnfRule)psiElement);
      }
    }
    // add their supers
    List<BnfRule> realRules = new ArrayList<BnfRule>(rulesToTry);
    for (BnfRule rule : realRules) {
      for (BnfRule superRule : myRuleExtendsMap.keySet()) {
        if (superRule == rule) continue;
        if (myRuleExtendsMap.get(superRule).contains(rule)) {
          rulesToTry.add(superRule);
        }
      }
    }
    if (rulesToTry.size() < 2) return mapList;
    // sort rules along with their supers
    List<BnfRule> sorted = topoSort(rulesToTry, myRuleExtendsMap);

    // drop unnecessary super rules: doesn't combine much, not present due collapse
    int max = 0;
    for (Iterator<BnfRule> it = sorted.iterator(); it.hasNext(); ) {
      BnfRule superRule = it.next();
      int count = 0;
      boolean collapse = false;
      for (BnfRule r : realRules) {
        if (myRuleExtendsMap.get(superRule).contains(r)) count++;
        if (myRulesGraph.get(r).contains(superRule)) collapse = true;
      }
      if (max < count) {
        max = count;
      }
      else if (!collapse) {
        it.remove();
      }
    }

    // apply changes and merge cards
    Collections.reverse(sorted);
    List<Map<PsiElement, Cardinality>> result = new ArrayList<Map<PsiElement, Cardinality>>(mapList.size());
    for (Map<PsiElement, Cardinality> map : mapList) {
      result.add(new HashMap<PsiElement, Cardinality>(map));
    }
    for (BnfRule superRule : sorted) {
      for (Map<PsiElement, Cardinality> newMap : result) {
        Cardinality cardinality = null;
        for (Iterator<PsiElement> iterator = newMap.keySet().iterator(); iterator.hasNext(); ) {
          PsiElement cur = iterator.next();
          if (cur == superRule || !(cur instanceof BnfRule)) continue;
          if (!myRuleExtendsMap.get(superRule).contains(cur)) {
            continue;
          }
          cardinality = cardinality == null ? newMap.get(cur) : cardinality.or(newMap.get(cur));
          iterator.remove();
        }
        if (cardinality != null) {
          newMap.put(superRule, cardinality);
        }
      }
    }
    return result;
  }

  private static List<BnfRule> topoSort(final Collection<BnfRule> collection, final MultiMap<BnfRule, BnfRule> graph) {
    List<BnfRule> rulesToSort = new ArrayList<BnfRule>(collection);
    if (rulesToSort.size() < 2) return new ArrayList<BnfRule>(rulesToSort);
    Collections.reverse(rulesToSort);
    List<BnfRule> sorted = new ArrayList<BnfRule>(rulesToSort.size());
    main: while (!rulesToSort.isEmpty()) {
      inner: for (BnfRule rule : rulesToSort) {
        for (BnfRule r : rulesToSort) {
          if (rule == r) continue;
          if (graph.get(rule).contains(r)) continue inner;
        }
        sorted.add(rule);
        rulesToSort.remove(rule);
        continue main;
      }
      // choose the first from cycle
      Iterator<BnfRule> it = rulesToSort.iterator();
      sorted.add(it.next());
      it.remove();
    }
    return sorted;
  }
}
