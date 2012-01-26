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

package org.intellij.grammar.analysis;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashSet;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfFirstNextAnalyzer {
  
  private static final Logger LOG = Logger.getInstance("org.intellij.grammar.analysis.BnfFirstNextAnalyzer"); 
  
  public static final String MATCHES_EOF = "-eof-";
  public static final String MATCHES_NOTHING = "-never-matches-";

  public static Set<String> calcFirst(@NotNull BnfRule rule) {
    Set<BnfRule> visited = new THashSet<BnfRule>();
    visited.add(rule);
    return calcFirstInner(rule.getExpression(), new THashSet<String>(), visited);
  }

  public static Set<String> calcNext(@NotNull BnfRule targetRule) {
    return calcNextInner(targetRule.getExpression(), new THashSet<String>(), new THashSet<BnfRule>());
  }
  
  public static Set<String> calcNextInner(@NotNull BnfExpression targetExpression, Set<String> result, Set<BnfRule> visited) {
    LinkedList<BnfExpression> stack = new LinkedList<BnfExpression>();
    THashSet<BnfRule> totalVisited = new THashSet<BnfRule>();
    Set<String> curResult = new THashSet<String>();
    stack.add(targetExpression);
    main: while (!stack.isEmpty()) {
      curResult.clear();

      PsiElement cur = stack.removeLast();
      PsiElement parent = cur.getParent();
      while (parent instanceof BnfExpression) {
        if (parent instanceof BnfSequence) {
          List<BnfExpression> children = ((BnfSequence)parent).getExpressionList();
          int idx = children.indexOf(cur);
          calcSequenceFirstInner(children.subList(idx + 1, children.size()), curResult, visited);
          boolean skipResolve = !curResult.contains(MATCHES_EOF);
          result.addAll(curResult);
          if (skipResolve) continue main;
        }
        else if (parent instanceof BnfQuantified) {
          IElementType effectiveType = ParserGeneratorUtil.getEffectiveType(parent);
          if (effectiveType == BnfTypes.BNF_OP_ZEROMORE || effectiveType == BnfTypes.BNF_OP_ONEMORE) {
            calcFirstInner((BnfExpression)parent, result, visited);
          }
        }
        cur = parent;
        parent = parent.getParent();
      }
      if (parent instanceof BnfRule && totalVisited.add((BnfRule)parent)) {
        BnfRule rule = (BnfRule)parent;
        for (PsiReference reference : ReferencesSearch.search(rule, rule.getUseScope()).findAll()) {
          PsiElement element = reference.getElement();
          if (element instanceof BnfExpression && PsiTreeUtil.getParentOfType(element, BnfPredicate.class) == null) {
            stack.add((BnfExpression)element);
          }
        }
      }
    }
    if (result.isEmpty()) result.add(MATCHES_EOF);
    return result;
  }

  private static Set<String> calcSequenceFirstInner(List<BnfExpression> expressions, final Set<String> result, final Set<BnfRule> visited) {
    boolean matchesEof = !result.add(MATCHES_EOF);
    for (BnfExpression expression : expressions) {
      if (!result.remove(MATCHES_EOF)) break;
      calcFirstInner(expression, result, visited);
    }
    // add empty back if was there before
    if (matchesEof) result.add(MATCHES_EOF);
    return result;
  }

  public static Set<String> calcFirstInner(BnfExpression expression, Set<String> result, Set<BnfRule> visited) {
    if (expression instanceof BnfLiteralExpression) {
      result.add(expression.getText());
    }
    else if (expression instanceof BnfReferenceOrToken) {
      PsiReference reference = expression.getReference();
      PsiElement resolve = reference == null? null : reference.resolve();
      if (resolve instanceof BnfRule) {
        BnfRule rule = (BnfRule)resolve;
        if (!visited.add(rule)) {
          result.add(rule.getName()); // cyclic dependency
        }
        else {
          calcFirstInner(rule.getExpression(), result, visited);
          boolean removed = visited.remove(rule);
          LOG.assertTrue(removed, "path corruption detected");
        }
      }
      else if (GrammarUtil.isExternalReference(expression)) {
        result.add("#" + expression.getText());
      }
      else {
        result.add(expression.getText());
      }
    }
    else if (expression instanceof BnfParenthesized) {
      calcFirstInner(((BnfParenthesized)expression).getExpression(), result, visited);
      if (expression instanceof BnfParenOptExpression) {
        result.add(MATCHES_EOF);
      }
    }
    else if (expression instanceof BnfChoice) {
      boolean matchesNothing = result.remove(MATCHES_NOTHING);
      boolean matchesSomething = false;
      for (BnfExpression child : ((BnfChoice)expression).getExpressionList()) {
        calcFirstInner(child, result, visited);
        matchesSomething |= !result.remove(MATCHES_NOTHING);
      }
      if (!matchesSomething || matchesNothing) result.add(MATCHES_NOTHING);
    }
    else if (expression instanceof BnfSequence) {
      calcSequenceFirstInner(((BnfSequence)expression).getExpressionList(), result, visited);
    }
    else if (expression instanceof BnfQuantified) {
      calcFirstInner(((BnfQuantified)expression).getExpression(), result, visited);
      IElementType effectiveType = ParserGeneratorUtil.getEffectiveType(expression);
      if (effectiveType == BnfTypes.BNF_OP_OPT || effectiveType == BnfTypes.BNF_OP_ZEROMORE) {
        result.add(MATCHES_EOF);
      }
    }
    else if (expression instanceof BnfExternalExpression) {
      List<BnfExpression> expressionList = ((BnfExternalExpression)expression).getExpressionList();
      if (expressionList.size() == 1 && ParserGeneratorUtil.Rule.isMeta(ParserGeneratorUtil.Rule.of(expression))) {
        result.add(expression.getText());
      }
      else {
        BnfExpression ruleRef = expressionList.get(0);
        Set<String> metaResults = calcFirstInner(ruleRef, new LinkedHashSet<String>(), visited);
        List<String> params = null;
        for (String str : metaResults) {
          if (!str.startsWith("<<")) {
            result.add(str);
          }
          else {
            if (params == null) {
              BnfRule metaRule = (BnfRule)ruleRef.getReference().resolve();
              if (metaRule == null) {
                LOG.error("ruleRef:" + ruleRef.getText() +", metaResult:" + metaResults);
                continue;
              }
              params = GrammarUtil.collectExtraArguments(metaRule, metaRule.getExpression());
            }
            int idx = params.indexOf(str);
            if (idx > -1 && idx + 1 < expressionList.size()) {
              calcFirstInner(expressionList.get(idx + 1), result, visited);
            }
          }
        }
      }
    }
    else if (expression instanceof BnfPredicate) {
      IElementType elementType = ((BnfPredicate)expression).getPredicateSign().getFirstChild().getNode().getElementType();
      BnfExpression predicateExpression = ((BnfPredicate)expression).getExpression();
      // take only one token into account which is not exactly correct but better than nothing
      Set<String> conditions = calcFirstInner(predicateExpression, new THashSet<String>(), visited);
      Set<String> next = calcNextInner(expression, new THashSet<String>(), visited);
      if (predicateExpression instanceof BnfParenExpression) predicateExpression = ((BnfParenExpression)predicateExpression).getExpression();
      boolean skip = predicateExpression instanceof BnfSequence && ((BnfSequence)predicateExpression).getExpressionList().size() > 1; // todo calc min length ?
      if (!skip) {
        // skip text-matching
        for (String condition : conditions) {
          if (StringUtil.isQuotedString(condition)) { skip = true; break; }
        }
      }
      if (!skip) {
        // skip external methods
        for (String s : next) {
          if (s.startsWith("#")) { skip = true; break; }
        }
      }
      if (skip) {
        next.remove(MATCHES_EOF);
      }
      else if (elementType == BnfTypes.BNF_OP_AND) {
        if (!conditions.contains(MATCHES_EOF)) {
          next.retainAll(conditions);
          if (next.isEmpty()) next.add(MATCHES_NOTHING);
        }
      }
      else {
        if (!conditions.contains(MATCHES_EOF)) {
          next.removeAll(conditions);
          if (next.isEmpty()) next.add(MATCHES_NOTHING);
        }
        else { next.clear(); next.add(MATCHES_NOTHING); }
      }
      result.addAll(next);
    }

    return result;
  }

}
