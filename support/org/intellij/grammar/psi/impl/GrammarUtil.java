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

import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author gregsh
 */
public class GrammarUtil {
  public static boolean processChildrenDummyAware(PsiElement element, final Processor<PsiElement> processor) {
    return new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        for (PsiElement child = psiElement.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (child instanceof BnfDummyElementImpl) {
            if (!process(child)) return false;
          }
          else if (!processor.process(child)) return false;
        }
        return true;
      }
    }.process(element);
  }

  public static PsiElement getDummyAwarePrevSibling(PsiElement child) {
    PsiElement prevSibling = child.getPrevSibling();
    while (prevSibling instanceof BnfDummyElementImpl) {
      prevSibling = prevSibling.getLastChild();
    }
    if (prevSibling != null) return prevSibling;
    PsiElement parent = child.getParent();
    while (parent instanceof BnfDummyElementImpl && parent.getPrevSibling() == null) {
      parent = parent.getParent();
    }
    return parent == null? null : parent.getPrevSibling();
  }

  @Nullable
  public static <T extends PsiElement> T findDummyAwareChildOfType(PsiElement element, final Class<T> aClass) {
    final Ref<T> result = Ref.create(null);
    processChildrenDummyAware(element, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (aClass.isInstance(psiElement)) {
          result.set((T)psiElement);
          return false;
        }
        return true;
      }
    });
    return result.get();
  }

  public static BnfRule findRuleByName(PsiFile file, final String name) {
    final Ref<BnfRule> result = Ref.create(null);
    file.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof BnfRule) {
          if (name.equals(((BnfRule)element).getName())) {
            result.set((BnfRule)element);
            stopWalking();
          }
        }
        super.visitElement(element);
      }
    });
    return result.get();
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
        if (c1 == null || c2 == null) return c1 == c2;
        boolean f1 = c1 instanceof BnfExpression;
        boolean f2 = c2 instanceof BnfExpression;
        if (f1 && f2 && !equalsElement((BnfExpression)c1, (BnfExpression)c2)) return false;
        if (f1 && f2 || !f1) c1 = c1.getNextSibling();
        if (f1 && f2 || !f2) c2 = c2.getNextSibling();
      }
    }
  }

  public static boolean isOneTokenExpression(BnfExpression e1) {
    return e1 instanceof BnfLiteralExpression || e1 instanceof BnfReferenceOrToken;
  }

  public static boolean isExternalReference(PsiElement psiElement) {
    PsiElement parent = psiElement.getParent();
    if (parent instanceof BnfExternalExpression && ((BnfExternalExpression)parent).getExpressionList().get(0) == psiElement) return true;
    BnfRule rule = PsiTreeUtil.getParentOfType(psiElement, BnfRule.class);
    return psiElement == getExternalMethodExpression(rule);
  }

  @Nullable
  public static BnfExpression getExternalMethodExpression(BnfRule rule) {
    if (!ParserGeneratorUtil.Rule.isExternal(rule)) return null;
    final BnfExpression expression = rule.getExpression();
    final BnfExpression result;
    if (expression instanceof BnfReferenceOrToken) {
      result = expression;
    }
    else if (expression instanceof BnfSequence) {
      List<BnfExpression> list = ((BnfSequence)expression).getExpressionList();
      result = list.isEmpty() ? null : list.get(0);
    }
    else result = null;
    return result;
  }
}
