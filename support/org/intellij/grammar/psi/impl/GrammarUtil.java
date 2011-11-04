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

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author gregsh
 */
public class GrammarUtil {

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
    BnfRule rule = PsiTreeUtil.getParentOfType(psiElement, BnfRule.class);
    return rule != null && psiElement == getExternalMethodExpression(rule);
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
