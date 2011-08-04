/*
 * Copyright 2000-2011 Gregory Shrago
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
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.generator.RuleGraphHelper.Cardinality.*;

/**
 * @author gregory
 *         Date: 16.07.11 10:41
 */
public class RuleGraphHelper {
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
        return Cardinality.OPTIONAL;
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

  private Map<String, BnfRule> ruleMap;

  public RuleGraphHelper(Map<String, BnfRule> ruleMap) {
    this.ruleMap = ruleMap;
  }

  @Nullable
  private BnfRule resolveRule(String text) {
    BnfRule rule = ruleMap.get(text);
    if (rule == null) return null;
    String superRuleName = getAttribute(rule, "extends", null);
    if (superRuleName == null) return null;
    BnfRule superRule = ruleMap.get(superRuleName);
    return superRule == null ? rule : superRule;
  }


  public Map<PsiElement, Cardinality> getFor(BnfRule rule) {
    BnfExpression body = Rule.body(rule);
    Map<PsiElement, Cardinality> map = collectMembers(body, new HashSet<PsiElement>());
    if (map.size() == 1 && map.containsKey(rule)) {
      return Collections.emptyMap();
    }
    return map;
  }


  private Map<PsiElement, Cardinality> collectMembers(BnfExpression tree, Set<PsiElement> visited) {
    if (tree instanceof BnfPredicate) return Collections.emptyMap();
    if (tree instanceof BnfLiteralExpression) return psiMap(tree, REQUIRED);

    if (!visited.add(tree)) return psiMap(tree, REQUIRED);
    if (tree instanceof BnfReferenceOrToken) {
      BnfRule targetRule = resolveRule(tree.getText());
      if (targetRule != null) {
        if (Rule.isPrivate(targetRule)) {
          BnfExpression body = Rule.body(targetRule);
          Map<PsiElement, Cardinality> map = collectMembers(body, visited);
          return map.containsKey(body) ? joinMaps(tree, BnfTypes.BNF_CHOICE, Arrays.asList(map, map)) : map;
        }
        else {
          return psiMap(targetRule, REQUIRED);
        }
      }
      return psiMap(tree, REQUIRED);
    }

    List<Map<PsiElement, Cardinality>> list = new ArrayList<Map<PsiElement, Cardinality>>();
    for (BnfExpression child : getChildExpressions(tree)) {
      list.add(collectMembers(child, visited));
    }
    return joinMaps(tree, getEffectiveType(tree), list);
  }

  private Map<PsiElement, Cardinality> joinMaps(BnfExpression tree, IElementType type, List<Map<PsiElement, Cardinality>> list) {
    if (list.isEmpty()) return Collections.emptyMap();
    if (type == BnfTypes.BNF_OP_OPT || type == BnfTypes.BNF_OP_ZEROMORE || type == BnfTypes.BNF_OP_ONEMORE) {
      Map<PsiElement, Cardinality> map = new HashMap<PsiElement, Cardinality>();
      assert list.size() == 1;
      Map<PsiElement, Cardinality> m = list.get(0);
      for (PsiElement t : m.keySet()) {
        map.put(t, fromNodeType(type).and(m.get(t)));
      }
      return map;
    }
    else if (type == BnfTypes.BNF_SEQUENCE || type == BnfTypes.BNF_EXPRESSION) {
      if (list.size() == 1) return list.get(0);
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
      Map<PsiElement, Cardinality> m0 = list.get(0);
      map.putAll(m0);
      for (Map<PsiElement, Cardinality> m : list) {
        map.keySet().retainAll(m.keySet());
      }
      for (PsiElement t : map.keySet()) {
        map.put(t, REQUIRED.and(m0.get(t)));
        for (Map<PsiElement, Cardinality> m : list) {
          if (m == list.get(0)) continue;
          map.put(t, map.get(t).and(m.get(t)));
        }
      }
      for (Map<PsiElement, Cardinality> m : list) {
        for (PsiElement t : m.keySet()) {
          if (map.containsKey(t)) continue;
          if (t == tree.getParent()) continue;
          map.put(t, OPTIONAL.and(m.get(t)));
        }
      }
      return map;
    }
    else {
      throw new AssertionError("unexpected: " + type);
    }
  }

  // damn java generics
  public static <V> java.util.Map<PsiElement, V> psiMap(PsiElement k, V v) {
    return Collections.singletonMap(k, v);
  }
}
