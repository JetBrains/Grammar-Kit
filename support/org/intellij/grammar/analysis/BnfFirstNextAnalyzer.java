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

package org.intellij.grammar.analysis;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.FakeBnfExpression;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author gregsh
 */
public class BnfFirstNextAnalyzer {

  private static final Logger LOG = Logger.getInstance("org.intellij.grammar.analysis.BnfFirstNextAnalyzer");

  public static final String MATCHES_EOF = "-eof-";
  public static final String MATCHES_NOTHING = "-never-matches-";
  public static final String MATCHES_ANY = "-any-";

  public static final BnfExpression BNF_MATCHES_EOF     = new FakeBnfExpression(MATCHES_EOF);
  public static final BnfExpression BNF_MATCHES_NOTHING = new FakeBnfExpression(MATCHES_NOTHING);
  public static final BnfExpression BNF_MATCHES_ANY     = new FakeBnfExpression(MATCHES_ANY);

  private boolean myBackward;
  private boolean myPublicRuleOpaque;
  private boolean myNoParent;

  public BnfFirstNextAnalyzer setBackward(boolean backward) {
    myBackward = backward;
    return this;
  }

  public BnfFirstNextAnalyzer setPublicRuleOpaque(boolean publicRuleOpaque) {
    myPublicRuleOpaque = publicRuleOpaque;
    return this;
  }

  public BnfFirstNextAnalyzer setNoParent(boolean noParent) {
    myNoParent = noParent;
    return this;
  }

  public Set<BnfExpression> calcFirst(@NotNull BnfRule rule) {
    Set<BnfExpression> visited = new THashSet<BnfExpression>();
    BnfExpression expression = rule.getExpression();
    visited.add(expression);
    return calcFirstInner(expression, new THashSet<BnfExpression>(), visited);
  }

  public Map<BnfExpression, BnfExpression> calcNext(@NotNull BnfRule targetRule) {
    return calcNextInner(targetRule.getExpression(), new THashMap<BnfExpression, BnfExpression>(), new THashSet<BnfExpression>());
  }

  public Map<BnfExpression, BnfExpression> calcNext(@NotNull BnfExpression targetExpression) {
    return calcNextInner(targetExpression, new THashMap<BnfExpression, BnfExpression>(), new THashSet<BnfExpression>());
  }

  private Map<BnfExpression, BnfExpression> calcNextInner(@NotNull BnfExpression targetExpression, Map<BnfExpression, BnfExpression> result, Set<BnfExpression> visited) {
    LinkedList<BnfExpression> stack = new LinkedList<BnfExpression>();
    THashSet<BnfRule> totalVisited = new THashSet<BnfRule>();
    Set<BnfExpression> curResult = new THashSet<BnfExpression>();
    stack.add(targetExpression);
    main: while (!stack.isEmpty()) {

      PsiElement cur = stack.removeLast();
      BnfExpression startingExpr = cur instanceof BnfReferenceOrToken? (BnfExpression)cur : null;
      PsiElement parent = cur.getParent();
      while (parent instanceof BnfExpression) {
        curResult.clear();
        PsiElement grandPa = parent.getParent();
        if (grandPa instanceof BnfRule && ParserGeneratorUtil.Rule.isExternal((BnfRule)grandPa) ||
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
          IElementType effectiveType = ParserGeneratorUtil.getEffectiveType(parent);
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
      if (!myNoParent && parent instanceof BnfRule && totalVisited.add((BnfRule)parent)) {
        BnfRule rule = (BnfRule)parent;
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
    return result;
  }

  private Set<BnfExpression> calcSequenceFirstInner(List<BnfExpression> expressions, final Set<BnfExpression> result, final Set<BnfExpression> visited) {
    boolean matchesEof = !result.add(BNF_MATCHES_EOF);

    boolean pinApplied = false;
    Set<BnfExpression> pinned;
    if (!myBackward) {
      BnfExpression firstItem = ContainerUtil.getFirstItem(expressions);
      if (firstItem == null) return result;
      BnfRule rule = ParserGeneratorUtil.Rule.of(firstItem);
      pinned = new HashSet<BnfExpression>();
      GrammarUtil.processPinnedExpressions(rule, new CommonProcessors.CollectProcessor<BnfExpression>(pinned));
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

  public Set<BnfExpression> calcFirstInner(BnfExpression expression, Set<BnfExpression> result, Set<BnfExpression> visited) {
    return calcFirstInner(expression, result, visited, null);
  }

  public Set<BnfExpression> calcFirstInner(BnfExpression expression, Set<BnfExpression> result, Set<BnfExpression> visited, @Nullable Pair<Boolean, List<BnfExpression>> forcedNext) {
    BnfFile file = (BnfFile)expression.getContainingFile();
    if (expression instanceof BnfLiteralExpression) {
      result.add(expression);
    }
    else if (expression instanceof BnfReferenceOrToken) {
      BnfRule rule = file.getRule(expression.getText());
      if (rule != null) {
        if (ParserGeneratorUtil.Rule.isExternal(rule)) {
          BnfExpression callExpr = ContainerUtil.getFirstItem(GrammarUtil.getExternalRuleExpressions(rule));
          if (callExpr instanceof BnfReferenceOrToken && file.getRule(callExpr.getText()) == null) {
            result.add(callExpr);
            return result;
          }
        }
        BnfExpression ruleExpression = rule.getExpression();
        if (myPublicRuleOpaque && !ParserGeneratorUtil.Rule.isPrivate(rule) ||
            !visited.add(ruleExpression)) {
          if (!(ParserGeneratorUtil.Rule.firstNotTrivial(rule) instanceof BnfPredicate)) {
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
    else if (expression instanceof BnfParenthesized) {
      calcFirstInner(((BnfParenthesized)expression).getExpression(), result, visited, forcedNext);
      if (expression instanceof BnfParenOptExpression) {
        result.add(BNF_MATCHES_EOF);
      }
    }
    else if (expression instanceof BnfChoice) {
      boolean matchesNothing = result.remove(BNF_MATCHES_NOTHING);
      boolean matchesSomething = false;
      for (BnfExpression child : ((BnfChoice)expression).getExpressionList()) {
        calcFirstInner(child, result, visited, forcedNext);
        matchesSomething |= !result.remove(BNF_MATCHES_NOTHING);
      }
      if (!matchesSomething || matchesNothing) result.add(BNF_MATCHES_NOTHING);
    }
    else if (expression instanceof BnfSequence) {
      calcSequenceFirstInner(((BnfSequence)expression).getExpressionList(), result, visited);
    }
    else if (expression instanceof BnfQuantified) {
      calcFirstInner(((BnfQuantified)expression).getExpression(), result, visited, forcedNext);
      IElementType effectiveType = ParserGeneratorUtil.getEffectiveType(expression);
      if (effectiveType == BnfTypes.BNF_OP_OPT || effectiveType == BnfTypes.BNF_OP_ZEROMORE) {
        result.add(BNF_MATCHES_EOF);
      }
    }
    else if (expression instanceof BnfExternalExpression) {
      List<BnfExpression> expressionList = ((BnfExternalExpression)expression).getExpressionList();
      if (expressionList.size() == 1 && ParserGeneratorUtil.Rule.isMeta(ParserGeneratorUtil.Rule.of(expression))) {
        result.add(expression);
      }
      else {
        BnfExpression ruleRef = expressionList.get(0);
        Set<BnfExpression> metaResults = calcFirstInner(ruleRef, new LinkedHashSet<BnfExpression>(), visited, forcedNext);
        List<String> params = null;
        for (BnfExpression e : metaResults) {
          if (e instanceof BnfExternalExpression) {
            if (params == null) {
              BnfRule metaRule = (BnfRule)ruleRef.getReference().resolve();
              if (metaRule == null) {
                LOG.error("ruleRef:" + ruleRef.getText() +", metaResult:" + metaResults);
                continue;
              }
              params = GrammarUtil.collectExtraArguments(metaRule, metaRule.getExpression());
            }
            int idx = params.indexOf(e.getText());
            if (idx > -1 && idx + 1 < expressionList.size()) {
              calcFirstInner(expressionList.get(idx + 1), result, visited, null);
            }
          }
          else {
            result.add(e);
          }
        }
      }
    }
    else if (myBackward && expression instanceof BnfPredicate) {
      result.add(BNF_MATCHES_EOF);
    }
    else if (expression instanceof BnfPredicate) {
      IElementType elementType = ((BnfPredicate)expression).getPredicateSign().getFirstChild().getNode().getElementType();
      BnfExpression predicateExpression = ParserGeneratorUtil.getNonTrivialNode(((BnfPredicate)expression).getExpression());
      boolean skip = predicateExpression instanceof BnfSequence &&
                     ((BnfSequence)predicateExpression).getExpressionList().size() > 1; // todo calc min length ?
      // take only one token into account which is not exactly correct but better than nothing
      Set<BnfExpression> conditions = calcFirstInner(predicateExpression, newExprSet(), visited, null);
      Set<BnfExpression> next;
      if (!visited.add(predicateExpression)) {
        skip = true;
        next = Collections.emptySet();
        result.add(BNF_MATCHES_NOTHING);
      }
      else {
        if (forcedNext == null) {
          next = calcNextInner(expression, new THashMap<BnfExpression, BnfExpression>(), visited).keySet();
        }
        else {
          next = calcSequenceFirstInner(forcedNext.second, newExprSet(), visited);
        }
        visited.remove(predicateExpression);
        if (!skip) {
          skip = filterExternalMethods(next) || filterExternalMethods(conditions);
        }
      }
      Set<BnfExpression> mixed = newExprSet();
      if (elementType == BnfTypes.BNF_OP_AND) {
        if (forcedNext != null && forcedNext.first) {
          mixed.addAll(conditions);
        }
        else if (skip) {
          mixed.addAll(next);
          mixed.remove(BNF_MATCHES_EOF);
        }
        else if (!conditions.contains(BNF_MATCHES_EOF)) {
          if (next.contains(BNF_MATCHES_ANY)) {
            mixed.addAll(conditions);
          }
          else {
            mixed.addAll(next);
            mixed.retainAll(conditions);
            if (mixed.isEmpty()) {
              mixed.add(BNF_MATCHES_NOTHING);
            }
          }
        }
        else {
          mixed.addAll(next);
        }
      }
      else {
        if (skip) {
          mixed.addAll(next);
          mixed.remove(BNF_MATCHES_EOF);
        }
        else if (!conditions.contains(BNF_MATCHES_EOF)) {
          mixed.addAll(next);
          mixed.removeAll(conditions);
          if (mixed.isEmpty()) {
            mixed.add(BNF_MATCHES_NOTHING);
          }
        }
        else {
          mixed.add(BNF_MATCHES_NOTHING);
        }
      }
      result.addAll(mixed);
    }

    return result;
  }

  private static boolean filterExternalMethods(Set<BnfExpression> set) {
    boolean skip = false;
    boolean addEof = false;
    for (Iterator<BnfExpression> iterator = set.iterator(); !skip && iterator.hasNext(); ) {
      BnfExpression o = iterator.next();
      boolean isExternal = GrammarUtil.isExternalReference(o);
      if ("<<eof>>".equals(o.getText())) {
        addEof = true;
        iterator.remove();
      }
      else {
        skip = isExternal;
      }
    }
    if (addEof) set.add(BNF_MATCHES_EOF);
    return skip;
  }

  public Set<String> asStrings(Set<BnfExpression> expressions) {
    Set<String> result = new TreeSet<String>();
    for (BnfExpression expression : expressions) {
      if (expression instanceof BnfLiteralExpression) {
        String text = expression.getText();
        result.add(StringUtil.isQuotedString(text) ? '\'' + StringUtil.unquoteString(text) + '\'' : text);
      }
      else if (GrammarUtil.isExternalReference(expression)) {
        result.add("#" + expression.getText());
      }
      else {
        result.add(expression.getText());
      }
    }
    return result;
  }

  private static Set<BnfExpression> newExprSet() {
    return ContainerUtil.newTroveSet(ParserGeneratorUtil.<BnfExpression>textStrategy());
  }

}
