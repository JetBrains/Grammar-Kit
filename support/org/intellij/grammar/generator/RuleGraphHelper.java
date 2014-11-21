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
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import gnu.trove.THashMap;
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
  private final MultiMap<BnfRule, BnfRule> myRulesGraph = newMultiMap();
  private final Map<BnfRule, Map<PsiElement, Cardinality>> myRuleContentsMap = ContainerUtil.newTroveMap();
  private final MultiMap<BnfRule, PsiElement> myRulesCollapseMap = newMultiMap();
  private final Set<BnfRule> myRulesWithTokens = ContainerUtil.newTroveSet();
  private final Map<String, PsiElement> myExternalElements = ContainerUtil.newTroveMap();

  private static final IElementType EXTERNAL_TYPE = new FakeElementType("EXTERNAL_TYPE", Language.ANY);
  private static final IElementType MARKER_TYPE = new FakeElementType("MARKER_TYPE", Language.ANY);
  private static final PsiElement LEFT_MARKER = new FakeBnfExpression(MARKER_TYPE, "LEFT_MARKER");
  private static final PsiElement NOT_EMPTY_MARKER = new FakeBnfExpression(MARKER_TYPE, "NOT_EMPTY_MARKER");

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

  public static MultiMap<BnfRule, BnfRule> buildExtendsMap(BnfFile file) {
    MultiMap<BnfRule, BnfRule> ruleExtendsMap = newMultiMap();
    for (BnfRule rule : file.getRules()) {
      if (Rule.isPrivate(rule) || Rule.isExternal(rule)) continue;
      BnfRule superRule = file.getRule(getAttribute(rule, KnownAttribute.EXTENDS));
      if (superRule != null) {
        ruleExtendsMap.putValue(superRule, rule);
      }
      BnfRule target = getSynonymTargetOrSelf(rule);
      if (target != rule) {
        ruleExtendsMap.putValue(target, rule);
      }
    }
    for (int i = 0, len = ruleExtendsMap.size(); i < len; i++) {
      boolean changed = false;
      for (BnfRule superRule : ruleExtendsMap.keySet()) {
        final Collection<BnfRule> rules = ruleExtendsMap.get(superRule);
        for (BnfRule rule : ContainerUtil.newArrayList(rules)) {
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

  private static <K, V> MultiMap<K, V> newMultiMap() {
    return new MultiMap<K, V>() {
      @NotNull
      @Override
      protected Map<K, Collection<V>> createMap() {
        return ContainerUtil.newLinkedHashMap();
      }

      @NotNull
      @Override
      protected Collection<V> createCollection() {
        return ContainerUtil.newLinkedHashSet();
      }
    };
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
    this(file, buildExtendsMap(file));
  }

  public RuleGraphHelper(BnfFile file, MultiMap<BnfRule, BnfRule> ruleExtendsMap) {
    myFile = file;
    myRuleExtendsMap = ruleExtendsMap;

    buildRulesGraph();
    buildCollapseMap();
    buildContentsMap();
  }

  public MultiMap<BnfRule, BnfRule> getRuleExtendsMap() {
    return myRuleExtendsMap;
  }

  public BnfFile getFile() {
    return myFile;
  }

  public boolean canCollapse(@NotNull BnfRule rule) {
    return myRulesCollapseMap.containsKey(rule);
  }

  private void buildCollapseMap() {
    BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer().setPublicRuleOpaque(true).setNoParent(true);

    for (BnfRule rule : myFile.getRules()) {
      if (!myRuleExtendsMap.containsScalarValue(rule)) continue;
      Set<BnfExpression> first = analyzer.calcFirst(rule);
      for (BnfExpression expression : first) {
        BnfRule r = expression instanceof BnfReferenceOrToken ? myFile.getRule(expression.getText()) : null;
        BnfRule commonSuper = r != null ? getCommonSuperRule(rule, r) : null;
        if (expression != BnfFirstNextAnalyzer.BNF_MATCHES_ANY && commonSuper == null) continue;
        Map<BnfExpression, BnfExpression> map = analyzer.calcNext(expression);
        if (!map.containsKey(BnfFirstNextAnalyzer.BNF_MATCHES_EOF)) continue;

        if (commonSuper != null) {
          myRulesCollapseMap.putValue(rule, commonSuper);
        }
        myRulesCollapseMap.putValue(rule, ObjectUtils.notNull(r, rule));
      }
      if (myRulesCollapseMap.containsKey(rule)) {
        myRulesCollapseMap.putValue(rule, rule);
      }
    }
  }

  private void buildContentsMap() {
    List<BnfRule> rules = topoSort(myFile.getRules(), this);

    for (BnfRule rule : rules) {
      collectMembers(rule);
    }
  }

  private Map<PsiElement, Cardinality> collectMembers(BnfRule rule) {
    Map<PsiElement, Cardinality> result = myRuleContentsMap.get(rule);
    if (result != null) return result;
    myRuleContentsMap.put(rule, psiMap(rule, REQUIRED));
    Map<PsiElement, Cardinality> map = collectMembers(rule, rule.getExpression());
    map.remove(NOT_EMPTY_MARKER); // todo private rules should retain this
    myRuleContentsMap.put(rule, map);
    return map;
  }

  @Nullable
  private BnfRule getCommonSuperRule(BnfRule r1, BnfRule r2) {
    int count = Integer.MAX_VALUE;
    BnfRule result = null;
    for (BnfRule superRule : myRuleExtendsMap.keySet()) {
      Collection<BnfRule> set = myRuleExtendsMap.get(superRule);
      if (set.contains(r1) && set.contains(r2)) {
        if (count > set.size()) {
          count = set.size();
          result = superRule;
        }
      }
    }
    return result;
  }

  private void buildRulesGraph() {
    LinkedList<BnfExpression> deque = ContainerUtil.newLinkedList();
    for (BnfRule rule : myFile.getRules()) {
      deque.addFirst(rule.getExpression());
      while (!deque.isEmpty()) {
        BnfExpression cur = deque.removeFirst();
        ListIterator<BnfExpression> listIterator = deque.listIterator();
        for (PsiElement e = cur.getFirstChild(); e != null; e = e.getNextSibling()) {
          if (e instanceof BnfPredicate || !(e instanceof BnfExpression)) continue;
          BnfReferenceOrToken ruleRef =
            e instanceof BnfReferenceOrToken ? (BnfReferenceOrToken)e :
            e instanceof BnfExternalExpression ? PsiTreeUtil.findChildOfType(e, BnfReferenceOrToken.class) :
            null;
          BnfRule r = ruleRef != null ? ruleRef.resolveRule() : null;
          if (r != null) {
            myRulesGraph.putValue(rule, r);
          }
          else if (e instanceof BnfReferenceOrToken || e instanceof BnfStringLiteralExpression) {
            myRulesWithTokens.add(rule);
          }
          else {
            listIterator.add((BnfExpression)e);
          }
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

  public boolean containsTokens(BnfRule rule) {
    return myRulesWithTokens.contains(rule);
  }

  public Collection<BnfRule> getSubRules(BnfRule rule) {
    return myRulesGraph.get(rule);
  }

  @NotNull
  public Map<PsiElement, Cardinality> getFor(BnfRule rule) {
    Map<PsiElement, Cardinality> map = myRuleContentsMap.get(rule); // null for duplicate
    return map == null ? Collections.<PsiElement, Cardinality>emptyMap() : map;
  }

  Map<PsiElement, Cardinality> collectMembers(BnfRule rule, BnfExpression tree) {
    if (tree instanceof BnfPredicate) return Collections.emptyMap();
    if (tree instanceof BnfLiteralExpression) return psiMap(tree, REQUIRED);

    boolean firstNonTrivial = tree == Rule.firstNotTrivial(rule);

    Map<PsiElement, Cardinality> result;
    if (tree instanceof BnfReferenceOrToken) {
      BnfRule targetRule = ((BnfReferenceOrToken)tree).resolveRule();
      if (targetRule != null) {
        if (Rule.isExternal(targetRule)) {
          result = psiMap(newExternalPsi(targetRule.getName()), REQUIRED);
        }
        else if (Rule.isLeft(targetRule)) {
          if (!Rule.isInner(targetRule) && !Rule.isPrivate(targetRule)) {
            result = psiMap();
            result.put(targetRule, REQUIRED);
            result.put(LEFT_MARKER, REQUIRED);
          }
          else {
            result = Collections.emptyMap();
          }
        }
        else if (Rule.isPrivate(targetRule)) {
          result = collectMembers(targetRule);
        }
        else {
          result = psiMap(targetRule, REQUIRED);
        }
      }
      else {
        result = psiMap(tree, REQUIRED);
      }
      if (firstNonTrivial && willCollapse(rule, result) && !Rule.isPrivate(rule)) {
        result = Collections.emptyMap();
      }
    }
    else if (tree instanceof BnfExternalExpression) {
      List<BnfExpression> expressionList = ((BnfExternalExpression)tree).getExpressionList();
      if (expressionList.size() == 1 && Rule.isMeta(rule)) {
        result = psiMap(newExternalPsi(tree.getText()), REQUIRED);
      }
      else {
        BnfExpression ruleRef = expressionList.get(0);
        BnfRule metaRule = ((BnfReferenceOrToken)ruleRef).resolveRule();
        if (metaRule == null) {
          result = psiMap(newExternalPsi("#" + ruleRef.getText()), REQUIRED);
        }
        else if (Rule.isPrivate(metaRule)) {
          result = psiMap();
          Map<PsiElement, Cardinality> metaResults = collectMembers(metaRule);
          List<String> params = null;
          for (PsiElement member : metaResults.keySet()) {
            Cardinality cardinality = metaResults.get(member);
            if (!isExternalPsi(member)) {
              result.put(member, cardinality);
            }
            else {
              if (params == null) {
                params = collectExtraArguments(metaRule, metaRule.getExpression());
              }
              int idx = params.indexOf(member.getText());
              if (idx > -1 && idx + 1 < expressionList.size()) {
                Map<PsiElement, Cardinality> argMap = collectMembers(rule, expressionList.get(idx + 1));
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
      if (firstNonTrivial && willCollapse(rule, result) && !Rule.isPrivate(rule)) {
        result = Collections.emptyMap();
      }
    }
    else {
      List<BnfExpression> pinned = ContainerUtil.newArrayList();
      GrammarUtil.processPinnedExpressions(rule, new CommonProcessors.CollectProcessor<BnfExpression>(pinned));
      boolean pinApplied = false;

      IElementType type = getEffectiveType(tree);

      List<Map<PsiElement, Cardinality>> list = ContainerUtil.newArrayList();
      List<BnfExpression> childExpressions = getChildExpressions(tree);
      for (BnfExpression child : childExpressions) {
        Map<PsiElement, Cardinality> nextMap = collectMembers(rule, child);
        if (pinApplied) {
          nextMap = joinMaps(rule, false, BnfTypes.BNF_OP_OPT, Collections.singletonList(nextMap));
        }
        list.add(nextMap);
        if (!pinApplied && pinned.contains(child)) {
          pinApplied = true;
        }
      }
      result = joinMaps(rule, firstNonTrivial, type, list);
      Cardinality sameCard = firstNonTrivial ? result.remove(rule.getExpression()) : null;
      result = sameCard != null ? joinMaps(rule, false, BnfTypes.BNF_SEQUENCE, Arrays.asList(result, result)) : result;
    }
    if (rule.getExpression() == tree && Rule.isLeft(rule) && !Rule.isPrivate(rule) && !Rule.isInner(rule)) {
      List<Map<PsiElement, Cardinality>> list = ContainerUtil.newArrayList();
      Map<BnfRule, Cardinality> rulesToTheLeft = getRulesToTheLeft(rule);
      for (BnfRule r : rulesToTheLeft.keySet()) {
        Cardinality cardinality = rulesToTheLeft.get(r);
        Map<PsiElement, Cardinality> leftMap = psiMap(r, REQUIRED);
        if (cardinality.many()) {
          list.add(joinMaps(rule, false, BnfTypes.BNF_CHOICE, Arrays.asList(leftMap, psiMap(rule, REQUIRED))));
        }
        else {
          list.add(leftMap);
        }
      }
      Map<PsiElement, Cardinality> combinedLeftMap = joinMaps(rule, false, BnfTypes.BNF_CHOICE, list);
      result = joinMaps(rule, false, BnfTypes.BNF_SEQUENCE, Arrays.asList(result, combinedLeftMap));
    }
    if (firstNonTrivial && Rule.isPrivate(rule) && result.remove(rule) != null) {
      result = joinMaps(rule, false, BnfTypes.BNF_SEQUENCE, Arrays.asList(result, result));
    }
    return result;
  }

  private static Map<BnfRule, Cardinality> getRulesToTheLeft(BnfRule rule) {
    Map<BnfRule, Cardinality> result = ContainerUtil.newLinkedHashMap();
    Map<BnfExpression, BnfExpression> nextMap = new BnfFirstNextAnalyzer().setBackward(true).setPublicRuleOpaque(true).calcNext(rule);
    for (BnfExpression e : nextMap.keySet()) {
      if (!(e instanceof BnfReferenceOrToken)) continue;
      BnfRule r = ((BnfReferenceOrToken)e).resolveRule();
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

  private Map<PsiElement, Cardinality> joinMaps(@NotNull BnfRule rule, boolean tryCollapse, IElementType type, List<Map<PsiElement, Cardinality>> list) {
    if (list.isEmpty()) return Collections.emptyMap();
    if (type == BnfTypes.BNF_OP_OPT || type == BnfTypes.BNF_OP_ZEROMORE || type == BnfTypes.BNF_OP_ONEMORE) {
      ParserGenerator.LOG.assertTrue(list.size() == 1);
      list = compactInheritors(rule, list);
      Map<PsiElement, Cardinality> m = list.get(0);
      if (tryCollapse && willCollapse(rule, m) && type == BnfTypes.BNF_OP_OPT) {
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
      list = ContainerUtil.newArrayList(compactInheritors(rule, list));
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
      if (tryCollapse && willCollapse(rule, map)) {
        return Collections.emptyMap();
      }
      return map;
    }
    else if (type == BnfTypes.BNF_CHOICE) {
      Map<PsiElement, Cardinality> map = psiMap();
      list = compactInheritors(rule, list);
      if (tryCollapse) {
        for (int i = 0, newListSize = list.size(); i < newListSize; i++) {
          Map<PsiElement, Cardinality> m = list.get(i);
          if (willCollapse(rule, m)) {
            list.set(i, Collections.<PsiElement, Cardinality>emptyMap());
          }
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
        if (tryCollapse && willCollapse(rule, m)) continue;
        for (PsiElement t : m.keySet()) {
          if (map.containsKey(t)) continue;
          map.put(t, OPTIONAL.and(m.get(t)));
        }
      }
      boolean notEmpty = true;
      empty: for (Map<PsiElement, Cardinality> m : list) {
        for (Cardinality c : m.values()) {
          if (!c.optional()) continue empty;
        }
        notEmpty = false;
      }
      if (notEmpty) map.put(NOT_EMPTY_MARKER, REQUIRED);
      return map;
    }
    else {
      throw new AssertionError("unexpected: " + type);
    }
  }

  private boolean canCollapseBy(BnfRule rule, PsiElement t) {
    if (myRulesCollapseMap.get(rule).contains(t)) return true;
    if (rule == t || t instanceof BnfRule && getCommonSuperRule(rule, (BnfRule)t) != null) {
      myRulesCollapseMap.putValue(rule, t);
      return true;
    }
    else if (isExternalPsi(t) && myRuleExtendsMap.containsScalarValue(rule)) {
      myRulesCollapseMap.putValue(rule, rule);
      return true;
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

  /** @noinspection UnusedParameters*/
  private List<Map<PsiElement, Cardinality>> compactInheritors(@Nullable BnfRule forRule, @NotNull List<Map<PsiElement, Cardinality>> mapList) {
    Map<BnfRule, BnfRule> rulesAndAlts = ContainerUtil.newLinkedHashMap();
    for (Map<PsiElement, Cardinality> map : mapList) {
      for (BnfRule rule : ContainerUtil.findAll(map.keySet(), BnfRule.class)) {
        rulesAndAlts.put(rule, rule);
      }
    }
    //if (forRule != null && "".equals(forRule.getName())) {
    //  int gotcha = 1;
    //}

    boolean hasSynonyms = collectSynonymsAndCollapseAlternatives(rulesAndAlts);
    if (rulesAndAlts.size() < 2) {
      return !hasSynonyms ? mapList : replaceRulesInMaps(mapList, rulesAndAlts);
    }
    Set<BnfRule> allRules = ContainerUtil.newLinkedHashSet(ContainerUtil.concat(rulesAndAlts.keySet(), rulesAndAlts.values()));

    List<Map.Entry<BnfRule, Collection<BnfRule>>> applicableSupers = ContainerUtil.newArrayList();
    for (Map.Entry<BnfRule, Collection<BnfRule>> e : myRuleExtendsMap.entrySet()) {
      int count = 0;
      for (BnfRule rule : allRules) {
        if (e.getValue().contains(rule)) count ++;
      }
      if (count > 1) {
        applicableSupers.add(e);
      }
    }
    if (applicableSupers.isEmpty()) {
      return !hasSynonyms ? mapList : replaceRulesInMaps(mapList, rulesAndAlts);
    }

    findTheBestReplacement(rulesAndAlts, applicableSupers);

    return replaceRulesInMaps(mapList, rulesAndAlts);
  }

  private boolean collectSynonymsAndCollapseAlternatives(Map<BnfRule, BnfRule> rulesAndAlts) {
    boolean hasSynonyms = false;
    for (Map.Entry<BnfRule, BnfRule> e : ContainerUtil.newArrayList(rulesAndAlts.entrySet())) {
      BnfRule rule = e.getKey();
      e.setValue(getSynonymTargetOrSelf(rule));
      hasSynonyms |= rule != e.getValue();
      for (PsiElement r : myRulesCollapseMap.get(rule)) {
        if (r instanceof BnfRule && !rulesAndAlts.containsKey(r)) {
          rulesAndAlts.put((BnfRule)r, (BnfRule)r);
        }
      }
    }
    return hasSynonyms;
  }

  private boolean willCollapse(BnfRule rule, Map<PsiElement, Cardinality> map) {
    if (!canCollapse(rule, map)) return false;

    boolean requiredFound = false;
    for (PsiElement t : map.keySet()) {
      if (t == NOT_EMPTY_MARKER) continue;
      if (requiredFound || map.get(t) != REQUIRED) return false;
      requiredFound = true;
    }
    return requiredFound;
  }

  private boolean canCollapse(BnfRule rule, Map<PsiElement, Cardinality> map) {
    boolean result = false;
    boolean maybeCollapsed = true;
    PsiElement required = null;
    for (PsiElement t : map.keySet()) {
      if (t == NOT_EMPTY_MARKER) continue;
      if (!map.get(t).optional()) {
        if (required == null) {
          required = t;
          maybeCollapsed = required instanceof BnfRule || isExternalPsi(required);
        }
        else {
          maybeCollapsed = false;
        }
        if (!maybeCollapsed) break;
      }
    }
    if (maybeCollapsed) {
      for (PsiElement t : required != null ? Collections.singleton(required) : map.keySet()) {
        result |= canCollapseBy(rule, t);
      }
    }
    return result;
  }

  private static void findTheBestReplacement(Map<BnfRule, BnfRule> rulesAndAlts,
                                             List<Map.Entry<BnfRule, Collection<BnfRule>>> supers) {
    BitSet bits = new BitSet(rulesAndAlts.size());
    int minI = -1, minC = -1, minS = -1;
    for (int len = Math.min(16, supers.size()), i = (1 << len) - 1; i > 0; i --) {
      if (minC != -1 && Integer.bitCount(i) > minC) continue;
      int curC = 0, curS = 0;
      bits.set(0, rulesAndAlts.size(), true);
      for (int j = 0, bit = 1; j < len; j ++, bit <<= 1) {
        if ((i & bit) == 0) continue;
        Collection<BnfRule> vals = supers.get(j).getValue();
        curC += 1;
        curS += vals.size();
        if (bits.isEmpty()) continue;

        int k = 0;
        for (Map.Entry<BnfRule, BnfRule> e : rulesAndAlts.entrySet()) {
          if (bits.get(k)) {
            if (vals.contains(e.getKey()) || vals.contains(e.getValue())) {
              bits.set(k, false);
            }
          }
          k ++;
        }
        if (!bits.isEmpty()) {
          curC += bits.cardinality();
          curS += bits.cardinality();
        }
      }
      if (minC == -1 || minC > curC || minC == curC && minS > curS) {
        minC = curC;
        minS = curS;
        minI = i;
      }
    }
    for (Map.Entry<BnfRule, BnfRule> e : rulesAndAlts.entrySet()) {
      for (int len = supers.size(), j = 0, bit = 1; j < len; j++, bit <<= 1) {
        if ((minI & bit) == 0) continue;
        Collection<BnfRule> vals = supers.get(j).getValue();
        if (vals.contains(e.getKey()) || vals.contains(e.getValue())) {
          e.setValue(supers.get(j).getKey());
        }
      }
    }
  }

  private static List<Map<PsiElement, Cardinality>> replaceRulesInMaps(List<Map<PsiElement, Cardinality>> mapList,
                                                                       Map<BnfRule, BnfRule> replacementMap) {
    List<Map<PsiElement, Cardinality>> result = ContainerUtil.newArrayListWithCapacity(mapList.size());
    for (Map<PsiElement, Cardinality> map : mapList) {
      Map<PsiElement, Cardinality> copy = psiMap(map);
      result.add(copy);
      for (Map.Entry<BnfRule, BnfRule> e : replacementMap.entrySet()) {
        Cardinality card = copy.remove(e.getKey());
        if (card == null) continue;
        Cardinality cur = copy.get(e.getValue());
        copy.put(e.getValue(), cur == null ? card : cur.or(card));
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
      myExternalElements.put(name, e = new FakeBnfExpression(EXTERNAL_TYPE, name));
    }
    return e;
  }

  private static boolean isExternalPsi(PsiElement t) {
    return t instanceof LeafPsiElement && ((LeafPsiElement)t).getElementType() == EXTERNAL_TYPE;
  }

}
