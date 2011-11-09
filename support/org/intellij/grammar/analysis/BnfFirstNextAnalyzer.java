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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashSet;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfFirstNextAnalyzer {
  
  public static final String EMPTY_STRING = "";

  public static Set<String> calcFirst(BnfExpression expression) {
    return calcFirstInner(expression, new THashSet<String>(), new LinkedList<BnfRule>());
  }

  public static Set<String> calcNext(BnfExpression expression) {
    THashSet<String> totalResult = new THashSet<String>();
    LinkedList<BnfExpression> stack = new LinkedList<BnfExpression>();
    THashSet<BnfRule> totalVisited = new THashSet<BnfRule>();
    LinkedList<BnfRule> visited = new LinkedList<BnfRule>();
    THashSet<String> result = new THashSet<String>();
    stack.add(expression);
    main: while (!stack.isEmpty()) {
      result.clear();
      visited.clear();

      PsiElement cur = stack.removeLast();
      PsiElement parent = cur.getParent();
      while (parent instanceof BnfExpression) {
        if (parent instanceof BnfSequence) {
          List<BnfExpression> children = ((BnfSequence)parent).getExpressionList();
          int idx = children.indexOf(cur);
          result.addAll(calcSequenceFirstInner(children.subList(idx + 1, children.size()), result, visited));
          boolean skipResolve = !result.remove(EMPTY_STRING);
          totalResult.addAll(result);
          if (skipResolve) continue main;
        }
        cur = parent;
        parent = parent.getParent();
      }
      if (parent instanceof BnfRule && totalVisited.add((BnfRule)parent)) {
        BnfRule rule = (BnfRule)parent;
        for (PsiReference reference : ReferencesSearch.search(rule, rule.getUseScope()).findAll()) {
          PsiElement element = reference.getElement();
          if (element instanceof BnfExpression) {
            stack.add((BnfExpression)element);
          }
        }
      }
    }
    return totalResult;
  }

  private static Set<String> calcSequenceFirstInner(List<BnfExpression> expressions, final Set<String> result, final LinkedList<BnfRule> visited) {
    result.add(EMPTY_STRING);
    for (BnfExpression expression : expressions) {
      if (!result.remove(EMPTY_STRING)) break;
      calcFirstInner(expression, result, visited);
    }
    return result;
  }

  public static Set<String> calcFirstInner(BnfExpression expression, Set<String> result, LinkedList<BnfRule> visited) {
    if (expression instanceof BnfLiteralExpression) {
      result.add(expression.getText());
    }
    else if (expression instanceof BnfReferenceOrToken) {
      PsiReference reference = expression.getReference();
      PsiElement resolve = reference == null? null : reference.resolve();
      if (resolve instanceof BnfRule) {
        BnfRule rule = (BnfRule)resolve;
        if (visited.contains(rule)) {
          result.add(rule.getName()); // cyclic dependency
        }
        else {
          visited.addLast(rule);
          calcFirstInner(rule.getExpression(), result, visited);
          BnfRule removed = visited.removeLast();
          assert removed == rule: "path corruption detected";
        }
      }
      else {
        result.add(expression.getText());
      }
    }
    else if (expression instanceof BnfParenExpression) {
      calcFirstInner(((BnfParenExpression)expression).getExpression(), result, visited);
    }
    else if (expression instanceof BnfChoice) {
      for (BnfExpression child : ((BnfChoice)expression).getExpressionList()) {
        result.addAll(calcFirstInner(child, new THashSet<String>(), visited));
      }
    }
    else if (expression instanceof BnfSequence) {
      result.addAll(calcSequenceFirstInner(((BnfSequence)expression).getExpressionList(), new THashSet<String>(), visited));
    }
    else if (expression instanceof BnfQuantified) {
      calcFirstInner(((BnfQuantified)expression).getExpression(), result, visited);
      IElementType effectiveType = ParserGeneratorUtil.getEffectiveType(expression);
      if (effectiveType == BnfTypes.BNF_OP_OPT || effectiveType == BnfTypes.BNF_OP_ZEROMORE) {
        result.add(EMPTY_STRING);
      }
    }
    else if (expression instanceof BnfExternalExpression) {
      // todo
    }
    else if (expression instanceof BnfPredicate) {
      result.add(EMPTY_STRING);
      // todo
      //IElementType elementType = ((BnfPredicate)expression).getPredicateSign().getNode().getElementType();
    }

    return result;
  }

}
