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
package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PairProcessor;
import com.intellij.util.Processor;
import com.intellij.util.SmartList;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

/**
 * @author gregsh
 */
public class GrammarUtil {

  public final static Comparator<BnfRule> RULE_COMPARATOR = new Comparator<BnfRule>() {
    @Override
    public int compare(BnfRule o1, BnfRule o2) {
      return Comparing.compare(o1.getName(), o2.getName());
    }
  };

  public static PsiElement getDummyAwarePrevSibling(PsiElement child) {
    PsiElement prevSibling = child.getPrevSibling();
    while (prevSibling instanceof GeneratedParserUtilBase.DummyBlock) {
      prevSibling = prevSibling.getLastChild();
    }
    if (prevSibling != null) return prevSibling;
    PsiElement parent = child.getParent();
    while (parent instanceof GeneratedParserUtilBase.DummyBlock && parent.getPrevSibling() == null) {
      parent = parent.getParent();
    }
    return parent == null? null : parent.getPrevSibling();
  }

  public static boolean equalsElement(BnfExpression e1, BnfExpression e2) {
    if (e1 == null) return e2 == null;
    if (e2 == null) return false;
    if (ParserGeneratorUtil.getEffectiveType(e1) != ParserGeneratorUtil.getEffectiveType(e2)) return false;
    if (isOneTokenExpression(e1)) {
      return e1.getText().equals(e2.getText());
    }
    else {
      for (PsiElement c1 = e1.getFirstChild(), c2 = e2.getFirstChild(); ;) {
        boolean f1 = c1 == null || c1 instanceof BnfExpression;
        boolean f2 = c2 == null || c2 instanceof BnfExpression;
        if (f1 && f2 && !equalsElement((BnfExpression)c1, (BnfExpression)c2)) return false;
        if (f1 && f2 || !f1) c1 = c1 == null? null : c1.getNextSibling();
        if (f1 && f2 || !f2) c2 = c2 == null? null : c2.getNextSibling();
        if (c1 == null && c2 == null) return true;
      }
    }
  }

  public static boolean isOneTokenExpression(BnfExpression e1) {
    return e1 instanceof BnfLiteralExpression || e1 instanceof BnfReferenceOrToken;
  }

  public static boolean isExternalReference(PsiElement psiElement) {
    PsiElement parent = psiElement.getParent();
    if (parent instanceof BnfExternalExpression && ((BnfExternalExpression)parent).getExpressionList().get(0) == psiElement) return true;
    if (parent instanceof BnfSequence) parent = parent.getParent();
    return parent instanceof BnfRule && ParserGeneratorUtil.Rule.isExternal((BnfRule)parent);
  }

  public static List<BnfExpression> getExternalRuleExpressions(BnfRule subRule) {
    BnfExpression expression = subRule.getExpression();
    return expression instanceof BnfSequence ? ((BnfSequence)expression).getExpressionList() : Collections.singletonList(expression);
  }

  public static List<String> collectExtraArguments(BnfRule rule, BnfExpression expression) {
    if (!ParserGeneratorUtil.Rule.isMeta(rule) && !ParserGeneratorUtil.Rule.isExternal(rule)) return Collections.emptyList();
    final SmartList<String> result = new SmartList<String>();
    expression.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfExternalExpression) {
          BnfExternalExpression expr = (BnfExternalExpression)element;
          List<BnfExpression> list = expr.getExpressionList();
          if (list.size() == 1) {
            String text = "<<"+list.get(0).getText() +">>";
            if (!result.contains(text)) {
              result.add(text);
            }
          }
        }
        super.visitElement(element);
      }
    });
    return result;
  }

  public static PsiElement prevOrParent(PsiElement e, PsiElement scope) {
    if (e == null || e == scope) return null;
    PsiElement prev = e.getPrevSibling();
    if (prev != null) return PsiTreeUtil.getDeepestLast(prev);
    PsiElement parent = e.getParent();
    return parent == scope || parent instanceof PsiFile ? null : parent;
  }

  public static PsiElement nextOrParent(PsiElement e, PsiElement scope) {
    if (e == null || e == scope) return null;
    PsiElement next = e.getNextSibling();
    if (next != null) return PsiTreeUtil.getDeepestFirst(next);
    PsiElement parent = e.getParent();
    return parent == scope || parent instanceof PsiFile? null : parent;
  }

  public static boolean processExpressionNames(String funcName, BnfExpression expression, PairProcessor<String, BnfExpression> processor) {
    if (isAtomicExpression(expression)) return true;

    final List<BnfExpression> children = getChildExpressions(expression);
    for (int i = 0, childExpressionsSize = children.size(); i < childExpressionsSize; i++) {
      BnfExpression child = children.get(i);
      if (isAtomicExpression(child)) continue;
      while (isTrivialNode(child)) {
        child = getTrivialNodeChild(child);
      }
      if (!processExpressionNames(getNextName(funcName, i), child, processor)) return false;
    }
    return processor.process(funcName, expression);
  }

  public static boolean isAtomicExpression(BnfExpression tree) {
    return tree instanceof BnfReferenceOrToken || tree instanceof BnfLiteralExpression || tree instanceof BnfExternalExpression;
  }


  public static boolean processChildrenDummyAware(PsiElement element, final Processor<PsiElement> processor) {
    return new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        for (PsiElement child = psiElement.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (child instanceof GeneratedParserUtilBase.DummyBlock) {
            if (!process(child)) return false;
          }
          else if (!processor.process(child)) return false;
        }
        return true;
      }
    }.process(element);
  }
}
