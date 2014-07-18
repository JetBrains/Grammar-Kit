/*
 * Copyright 2011-2014 Gregory Shrago
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

import com.intellij.lang.Language;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.MultiMapBasedOnSet;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import gnu.trove.TObjectHashingStrategy;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;
import static org.intellij.grammar.psi.impl.GrammarUtil.collectExtraArguments;
import static org.intellij.grammar.psi.impl.GrammarUtil.nextOrParent;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class RuleGraphHelper {
  private static final TObjectHashingStrategy<PsiElement> CARDINALITY_HASHING_STRATEGY = new TObjectHashingStrategy<PsiElement>() {
    @Override
    public int computeHashCode(PsiElement e) {
      if (e instanceof BnfReferenceOrToken || e instanceof BnfLiteralExpression) {
        return e.getText().hashCode();
      }
      return CANONICAL.computeHashCode(e);
    }

    @Override
    public boolean equals(PsiElement e1, PsiElement e2) {
      if (e1 instanceof BnfReferenceOrToken && e2 instanceof BnfReferenceOrToken ||
          e1 instanceof BnfLiteralExpression && e2 instanceof BnfLiteralExpression) {
        return e1.getText().equals(e2.getText());
      }
      return CANONICAL.equals(e1, e2);
    }
  };
  private final BnfFile myFile;
  private final MultiMap<BnfRule, BnfRule> myRuleExtendsMap;
  private final Map<BnfRule, Map<PsiElement, Cardinality>> myRuleContentsMap;
  private final MultiMap<BnfRule,BnfRule> myRulesGraph;
  private final Set<BnfRule> myRulesWithTokens = new THashSet<BnfRule>();
  private final Map<String, PsiElement> myExternalElements = new THashMap<String, PsiElement>();

  private static final LeafPsiElement LEFT_MARKER = new LeafPsiElement(new IElementType("LEFT_MARKER", Language.ANY, false) {}, "LEFT_MARKER");
  private static final IElementType EXTERNAL_TYPE = new IElementType("EXTERNAL_TYPE", Language.ANY, false) {};

  public static String getCardinalityText(Cardinality cardinality) {
    if (cardinality == AT_LEAST_ONE) {
      return "+";
    }
    else if (cardinality == ANY_NUMBER) {
      return "*";
    }
    else if (cardinality == OPTIONAL) {
      return "?";
    }
    return "";
  }

  public enum Cardinality {
    NONE, OPTIONAL, REQUIRED, AT_LEAST_ONE, ANY_NUMBER;

    public boolean optional() {
      return this == OPTIONAL || this == ANY_NUMBER || this == NONE;
    }

    public boolean many() {
      return this == AT_LEAST_ONE || this == ANY_NUMBER;
    }

    public Cardinality single() {
      return this == AT_LEAST_ONE? REQUIRED : this == ANY_NUMBER? OPTIONAL : this;
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
      if (c == null) return this;
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

  public static MultiMap<BnfRule, BnfRule> computeInheritance(BnfFile file) {
    final MultiMap<BnfRule, BnfRule> ruleExtendsMap = new MultiMapBasedOnSet<BnfRule, BnfRule>();
    for (BnfRule rule : file.getRules()) {
      if (Rule.isPrivate(rule) || Rule.isExternal(rule)) continue;
      BnfRule superRule = file.getRule(getAttribute(rule, KnownAttribute.EXTENDS));
      if (superRule == null) continue;
      ruleExtendsMap.putValue(superRule, rule);
      ruleExtendsMap.putValue(superRule, getSynonymTargetOrSelf(rule));
    }
    for (int i = 0, len = ruleExtendsMap.size(); i < len; i++) {
      boolean changed = false;
      for (BnfRule superRule : ruleExtendsMap.keySet()) {
        final Collection<BnfRule> rules = ruleExtendsMap.get(superRule);
        for (BnfRule rule : new ArrayList<BnfRule>(rules)) {
          changed |= rules.addAll(ruleExtendsMap.get(rule));
        }
      }
      if (!changed) break;
    }
    for (BnfRule rule : ruleExtendsMap.keySet()) {
      ruleExtendsMap.putValue(rule, rule); // add super to itself
    }
    return ruleExtendsMap;
  }

  private static final Key<CachedValue<Map<String, String>>> TOKEN_MAP_KEY = Key.create("TOKEN_MAP_KEY");
  public static Map<String, String> getTokenMap(final BnfFile file) {
    CachedValue<Map<String, String>> value = file.getUserData(TOKEN_MAP_KEY);
    if (value == null) {
      file.putUserData(TOKEN_MAP_KEY, value =
        CachedValuesManager.getManager(file.getProject()).createCachedValue(new CachedValueProvider<Map<String, String>>() {
          @Nullable
          @Override
          public Result<Map<String, String>> compute() {
            return new Result<Map<String, String>>(computeTokens(file).asInverseMap(), file);
          }
        }, false));
    }
    return value.getValue();
  }

  // string value to constant name
  public static KnownAttribute.ListValue computeTokens(BnfFile file) {
    return getRootAttribute(file, KnownAttribute.TOKENS);
  }

  private static final Key<CachedValue<RuleGraphHelper>> RULE_GRAPH_HELPER_KEY = Key.create("RULE_GRAPH_HELPER_KEY");
  public static RuleGraphHelper getCached(final BnfFile file) {
    CachedValue<RuleGraphHelper> value = file.getUserData(RULE_GRAPH_HELPER_KEY);
    if (value == null) {
      file.putUserData(RULE_GRAPH_HELPER_KEY, value = CachedValuesManager.getManager(file.getProject()).createCachedValue(new CachedValueProvider<RuleGraphHelper>() {
        @Nullable
        @Override
        public Result<RuleGraphHelper> compute() {
          return new Result<RuleGraphHelper>(new RuleGraphHelper(file), file);
        }
      }, false));
    }
    return value.getValue();
  }

  public RuleGraphHelper(BnfFile file) {
    this(file, computeInheritance(file));
  }

  public RuleGraphHelper(BnfFile file, MultiMap<BnfRule, BnfRule> ruleExtendsMap) {
    myFile = file;
    myRuleExtendsMap = ruleExtendsMap;
    myRuleContentsMap = new THashMap<BnfRule, Map<PsiElement, Cardinality>>();

    // ordered!
    myRulesGraph = new MultiMapBasedOnSet<BnfRule, BnfRule>() {
      @Override
      protected Collection<BnfRule> createCollection() {
        return new LinkedHashSet<BnfRule>();
      }
    };
    buildRulesGraph();
    buildContentsMap();
  }

  public MultiMap<BnfRule, BnfRule> getRuleExtendsMap() {
    return myRuleExtendsMap;
  }

  public BnfFile getFile() {
    return myFile;
  }

  private void buildContentsMap() {
    final Collection<? extends BnfRule> inheritors = new THashSet<BnfRule>(myRuleExtendsMap.values());
    List<BnfRule> rules = topoSort(myFile.getRules(), new Topology<BnfRule>() {
      @Override
      public boolean contains(BnfRule t1, BnfRule t2) {
        return myRulesGraph.get(t1).contains(t2);
      }

      @Override
      public BnfRule forceChoose(Collection<BnfRule> col) {
        return super.forceChooseInner(
          col, new Condition<BnfRule>() {
            @Override
            public boolean value(BnfRule bnfRule) {
              if (myRulesWithTokens.contains(bnfRule)) return false;
              for (BnfRule r : myRulesGraph.get(bnfRule)) {
                if (!inheritors.contains(r)) return false;
              }
              return true;
            }
          }, new Condition<BnfRule>() {
            @Override
            public boolean value(BnfRule bnfRule) {
              return !myRulesWithTokens.contains(bnfRule);
            }
          }, Condition.TRUE
        );
      }
    });
    THashSet<PsiElement> visited = new THashSet<PsiElement>();
    for (BnfRule rule : rules) {
      if (myRuleContentsMap.containsKey(rule)) continue;
      Map<PsiElement, Cardinality> map = collectMembers(rule, rule.getExpression(), visited);
      if (map.size() == 1 && ContainerUtil.getFirstItem(map.values()) == REQUIRED && !Rule.isPrivate(rule)) {
        PsiElement r = ContainerUtil.getFirstItem(map.keySet());
        if (r == rule || r instanceof BnfRule && collapseEachOther((BnfRule)r, rule)) {
          myRuleContentsMap.put(rule, Collections.<PsiElement, Cardinality>emptyMap());
        }
        else {
          myRuleContentsMap.put(rule, map);
        }
      }
      else {
        myRuleContentsMap.put(rule, map);
      }
      ParserGenerator.LOG.assertTrue(visited.isEmpty());
    }
  }

  public boolean collapseEachOther(BnfRule r1, BnfRule r2) {
    for (BnfRule superRule : myRuleExtendsMap.keySet()) {
      Collection<BnfRule> set = myRuleExtendsMap.get(superRule);
      if (set.contains(r1) && set.contains(r2)) return true;
    }
    return false;
  }

  private void buildRulesGraph() {
    for (BnfRule rule : myFile.getRules()) {
      BnfExpression expression = rule.getExpression();
      for (PsiElement cur = nextOrParent(expression.getPrevSibling(), expression);
           cur != null;
           cur = nextOrParent(cur, expression) ) {
        boolean checkPredicate = cur instanceof BnfReferenceOrToken || cur instanceof BnfStringLiteralExpression;
        if (!checkPredicate || PsiTreeUtil.getParentOfType(cur, BnfPredicate.class) != null) continue;
        BnfRule r = cur instanceof BnfReferenceOrToken? myFile.getRule(cur.getText()) : null;
        if (r != null) {
          myRulesGraph.putValue(rule, r);
        }
        else {
          myRulesWithTokens.add(rule);
        }
      }
    }
    for (BnfRule rule : myFile.getRules()) {
      if (Rule.isLeft(rule) && !Rule.isPrivate(rule) && !Rule.isInner(rule)) {
        for (BnfRule r : getRulesToTheLeft(rule).keySet()) {
          myRulesGraph.putValue(rule, r);
        }
      }
    }
  }

  public Collection<BnfRule> getExtendsRules(BnfRule rule) {
    return myRuleExtendsMap.get(rule);
  }

  public Collection<BnfRule> getSubRules(BnfRule rule) {
    return myRulesGraph.get(rule);
  }

  @NotNull
  public Map<PsiElement, Cardinality> getFor(BnfRule rule) {
    Map<PsiElement, Cardinality> map = myRuleContentsMap.get(rule); // null for duplicate
    return map == null ? Collections.<PsiElement, Cardinality>emptyMap() : map;
  }

  Map<PsiElement, Cardinality> collectMembers(BnfRule rule, BnfExpression tree, Set<PsiElement> visited) {
    if (tree instanceof BnfPredicate) return Collections.emptyMap();
    if (tree instanceof BnfLiteralExpression) return psiMap(tree, REQUIRED);

    if (!visited.add(tree)) return psiMap(tree, REQUIRED);

    Map<PsiElement, Cardinality> result;
    if (tree instanceof BnfReferenceOrToken) {
      BnfRule targetRule = myFile.getRule(tree.getText());
      if (targetRule != null) {
        if (Rule.isExternal(targetRule)) {
          result = psiMap(newExternalPsi(targetRule.getName()), REQUIRED);
        }
        else if (Rule.isLeft(targetRule)) {
          if (!Rule.isInner(targetRule) && !Rule.isPrivate(targetRule)) {
            result = psiMap();
            result.put(getSynonymTargetOrSelf(targetRule), REQUIRED);
            result.put(LEFT_MARKER, REQUIRED);
          }
          else {
            result = Collections.emptyMap();
          }
        }
        else if (Rule.isPrivate(targetRule)) {
          result = myRuleContentsMap.get(targetRule); // optimize performance
          if (result == null) {
            BnfExpression body = targetRule.getExpression();
            Map<PsiElement, Cardinality> map = collectMembers(targetRule, body, visited);
            result = map.containsKey(body) ? joinMaps(null, BnfTypes.BNF_CHOICE, Arrays.asList(map, map)) : map;
            myRuleContentsMap.put(targetRule, result);
          }
        }
        else {
          result = psiMap(getSynonymTargetOrSelf(targetRule), REQUIRED);
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
          result = psiMap(newExternalPsi("#" + ruleRef.getText()), REQUIRED);
        }
        else if (Rule.isPrivate(metaRule)) {
          if (visited.contains(ruleRef)) return psiMap(metaRule, REQUIRED);

          result = psiMap();
          Map<PsiElement, Cardinality> metaResults = collectMembers(rule, ruleRef, new HashSet<PsiElement>());
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
                Map<PsiElement, Cardinality> argMap = collectMembers(rule, expressionList.get(idx + 1), visited);
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
      ArrayList<BnfExpression> pinned = new ArrayList<BnfExpression>();
      GrammarUtil.processPinnedExpressions(rule, new CommonProcessors.CollectProcessor<BnfExpression>(pinned));
      boolean pinApplied = false;

      IElementType type = getEffectiveType(tree);
      boolean firstNonTrivial = tree == Rule.firstNotTrivial(rule);

      List<Map<PsiElement, Cardinality>> list = new ArrayList<Map<PsiElement, Cardinality>>();
      List<BnfExpression> childExpressions = getChildExpressions(tree);
      for (BnfExpression child : childExpressions) {
        Map<PsiElement, Cardinality> nextMap = collectMembers(rule, child, visited);
        if (pinApplied) {
          nextMap = joinMaps(null, BnfTypes.BNF_OP_OPT, Collections.singletonList(nextMap));
        }
        list.add(nextMap);
        if (!pinApplied && pinned.contains(child)) {
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
        Map<PsiElement, Cardinality> leftMap = psiMap(getSynonymTargetOrSelf(r), REQUIRED);
        if (cardinality.many()) {
          list.add(joinMaps(null, BnfTypes.BNF_CHOICE, Arrays.asList(leftMap, psiMap(getSynonymTargetOrSelf(rule), REQUIRED))));
        }
        else {
          list.add(leftMap);
        }
      }
      Map<PsiElement, Cardinality> combinedLeftMap = joinMaps(null, BnfTypes.BNF_CHOICE, list);
      result = joinMaps(null, BnfTypes.BNF_SEQUENCE, Arrays.asList(result, combinedLeftMap));
    }
    visited.remove(tree);
    return result;
  }

  private static Map<BnfRule, Cardinality> getRulesToTheLeft(BnfRule rule) {
    Map<BnfRule, Cardinality> result = new HashMap<BnfRule, Cardinality>();
    Map<BnfExpression, BnfExpression> nextMap = new BnfFirstNextAnalyzer().setBackward(true).setPublicRuleOpaque(true).calcNext(rule);
    BnfFile containingFile = (BnfFile)rule.getContainingFile();
    for (BnfExpression e : nextMap.keySet()) {
      if (!(e instanceof BnfReferenceOrToken)) continue;
      BnfRule r = containingFile.getRule(e.getText());
      if (r == null || ParserGeneratorUtil.Rule.isPrivate(r)) continue;
      BnfExpression context = nextMap.get(e);
      Cardinality cardinality = REQUIRED;
      for (PsiElement cur = context; !(cur instanceof BnfRule); cur = cur.getParent()) {
        if (PsiTreeUtil.isAncestor(cur, e, true)) break;
        IElementType curType = getEffectiveType(cur);
        if (curType == BnfTypes.BNF_OP_OPT || curType == BnfTypes.BNF_OP_ONEMORE || curType == BnfTypes.BNF_OP_ZEROMORE) {
          cardinality = cardinality.and(Cardinality.fromNodeType(curType));
        }
      }
      Cardinality prev = result.get(r);
      result.put(r, prev == null? cardinality : cardinality.or(prev));
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
      Map<PsiElement, Cardinality> map = psiMap();
      boolean leftMarker = m.containsKey(LEFT_MARKER);
      for (PsiElement t : m.keySet()) {
        Cardinality joinedCard = fromNodeType(type).and(m.get(t));
        if (leftMarker) {
          joinedCard = joinedCard.single();
        }
        map.put(t, joinedCard);
      }
      return map;
    }
    else if (type == BnfTypes.BNF_SEQUENCE || type == BnfTypes.BNF_EXPRESSION || type == BnfTypes.BNF_REFERENCE_OR_TOKEN) {
      list = new ArrayList<Map<PsiElement, Cardinality>>(compactInheritors(list));
      for (Iterator<Map<PsiElement, Cardinality>> it = list.iterator(); it.hasNext(); ) {
        if (it.next().isEmpty()) it.remove();
      }
      Map<PsiElement, Cardinality> map = psiMap();
      for (Map<PsiElement, Cardinality> m : list) {
        Cardinality leftMarker = m.get(LEFT_MARKER);
        if (leftMarker == REQUIRED) {
          map.clear();
          leftMarker = null;
        }
        else if (leftMarker == OPTIONAL) {
          for (PsiElement t : map.keySet()) {
            if (!m.containsKey(t)) {
              map.put(t, map.get(t).and(Cardinality.OPTIONAL));
            }
          }
        }
        for (PsiElement t : m.keySet()) {
          if (t == LEFT_MARKER && m != list.get(0)) continue;
          Cardinality c1 = map.get(t);
          Cardinality c2 = m.get(t);
          Cardinality joinedCard;
          if (leftMarker == null) {
            joinedCard = c2.or(c1);

          }
          // handle left semantic in a choice-like way
          else if (c1 == null) {
            joinedCard = c2;
          }
          else {
            if (c1 == REQUIRED) joinedCard = c2.many()? AT_LEAST_ONE : REQUIRED;
            else if (c1 == AT_LEAST_ONE) joinedCard = ANY_NUMBER;
            else joinedCard = c1;
          }
          map.put(t, joinedCard);
        }
      }
      if (checkInheritance && map.size() == 1 && ContainerUtil.getFirstItem(map.values()) == REQUIRED) {
        if (collapseNode(rule, ContainerUtil.getFirstItem(map.keySet()))) {
          return Collections.emptyMap();
        }
      }
      return map;
    }
    else if (type == BnfTypes.BNF_CHOICE) {
      Map<PsiElement, Cardinality> map = psiMap();
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

  private static <V> Map<PsiElement, V> psiMap(PsiElement k, V v) {
    Map<PsiElement, V> map = new THashMap<PsiElement, V>(1, 1, CARDINALITY_HASHING_STRATEGY);
    map.put(k, v);
    return map;
  }

  private static <V> Map<PsiElement, V> psiMap(Map<PsiElement, V> map) {
    return new THashMap<PsiElement, V>(map, CARDINALITY_HASHING_STRATEGY);
  }

  private static <V> Map<PsiElement, V> psiMap() {
    return new THashMap<PsiElement, V>(3, CARDINALITY_HASHING_STRATEGY);
  }

  private List<Map<PsiElement, Cardinality>> compactInheritors(List<Map<PsiElement, Cardinality>> mapList) {
    Set<BnfRule> rulesToTry = new LinkedHashSet<BnfRule>();
    // collect all rules
    for (Map<PsiElement, Cardinality> map : mapList) {
      for (PsiElement psiElement : map.keySet()) {
        if (!(psiElement instanceof BnfRule)) continue;
        rulesToTry.add((BnfRule)psiElement);
      }
    }
    // add their supers & collapse-caused rules
    BnfRule[] origRulesCopy = rulesToTry.toArray(new BnfRule[rulesToTry.size()]);
    Set<BnfRule> origRules = new LinkedHashSet<BnfRule>(rulesToTry);
    for (BnfRule realRule : origRulesCopy) {
      Map<PsiElement, Cardinality> availableResult = myRuleContentsMap.get(realRule);
      Set<PsiElement> content = availableResult == null? null : availableResult.keySet();
      for (BnfRule superRule : myRuleExtendsMap.keySet()) {
        if (superRule == realRule) continue;
        Collection<BnfRule> inheritors = myRuleExtendsMap.get(superRule);
        if (inheritors.contains(realRule)) {
          rulesToTry.add(superRule);
          if (content != null) {
            if (content.isEmpty()) {  // will be definitely collapsed, replace with super
              origRules.remove(realRule);
              origRules.add(superRule);
            }
            else {
              for (PsiElement element : content) {
                if (!(element instanceof BnfRule)) continue;
                BnfRule r = (BnfRule)element;
                if (inheritors.contains(r)) {
                  origRules.add(r);
                }
              }
            }
          }
        }
      }
    }

    if (rulesToTry.size() < 2) return mapList;
    // sort rules along with their supers
    List<BnfRule> sorted = topoSort(rulesToTry, new Topology<BnfRule>() {
      @Override
      public boolean contains(BnfRule t1, BnfRule t2) {
        return myRuleExtendsMap.get(t1).contains(t2);
      }
    });

    // drop unnecessary super rules: doesn't combine much, not present due collapse
    origRulesCopy = origRules.toArray(new BnfRule[origRules.size()]);
    int max = 0;
    BitSet bitSet = new BitSet(origRulesCopy.length);
    for (Iterator<BnfRule> it = sorted.iterator(); it.hasNext(); ) {
      BnfRule superRule = it.next();
      Collection<BnfRule> inheritors = myRuleExtendsMap.get(superRule);
      int count = 0;
      boolean changed = false;
      for (int i = 0; i < origRulesCopy.length; i++) {
        BnfRule r = origRulesCopy[i];
        if (r == superRule || inheritors.contains(r)) {
          count ++;
          if (!bitSet.get(i)) {
            bitSet.set(i);
            changed = true;
          }
        }
      }
      if (changed) {
        max = 0;
      }
      else if (count > 1 && max < count) {
        max = count;
      }
      else {
        it.remove();
      }
    }

    // apply changes and merge cards
    Collections.reverse(sorted);
    List<Map<PsiElement, Cardinality>> result = new ArrayList<Map<PsiElement, Cardinality>>(mapList.size());
    for (Map<PsiElement, Cardinality> map : mapList) {
      result.add(psiMap(map));
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

  public static BnfRule getSynonymTargetOrSelf(BnfRule rule) {
    String attr = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
    if (attr != null) {
      BnfRule realRule = ((BnfFile)rule.getContainingFile()).getRule(attr);
      if (realRule != null && shouldGeneratePsi(realRule, false)) return realRule;
    }
    return rule;
  }

  public static boolean shouldGeneratePsi(BnfRule rule, boolean psiClasses) {
    BnfFile containingFile = (BnfFile)rule.getContainingFile();
    BnfRule grammarRoot = containingFile.getRules().get(0);
    if (grammarRoot == rule) return false;
    if (Rule.isPrivate(rule) || Rule.isExternal(rule)) return false;
    String elementType = getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
    if (!psiClasses) return elementType == null;
    BnfRule thatRule = containingFile.getRule(elementType);
    return thatRule == null || thatRule == grammarRoot || Rule.isPrivate(thatRule) || Rule.isExternal(thatRule);
  }

  @NotNull
  private PsiElement newExternalPsi(String name) {
    PsiElement e = myExternalElements.get(name);
    if (e == null) {
      myExternalElements.put(name, e = new LeafPsiElement(EXTERNAL_TYPE, name));
    }
    return e;
  }
}
