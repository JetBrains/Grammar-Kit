/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;

/**
 * @author gregsh
 */
public class GrammarPsiImplUtil {
  @NotNull
  public static PsiReference[] getReferences(BnfListEntry o) {
    BnfAttr attr = PsiTreeUtil.getParentOfType(o, BnfAttr.class);
    if (attr == null || !Comparing.equal(KnownAttribute.METHODS.getName(), attr.getName())) return PsiReference.EMPTY_ARRAY;
    PsiElement id = o.getId();
    BnfLiteralExpression value = o.getLiteralExpression();
    if (id == null || value != null) return PsiReference.EMPTY_ARRAY;
    final String psiImplUtilClass = getRootAttribute(attr, KnownAttribute.PSI_IMPL_UTIL_CLASS);
    final JavaHelper javaHelper = JavaHelper.getJavaHelper(o);

    return new PsiReference[] {
      new PsiPolyVariantReferenceBase<BnfListEntry>(o, TextRange.from(id.getStartOffsetInParent(), id.getTextLength())) {

        private List<NavigatablePsiElement> getTargetMethods(String methodName) {
          BnfRule rule = PsiTreeUtil.getParentOfType(getElement(), BnfRule.class);
          String mixinClass = rule == null ? null : getAttribute(rule, KnownAttribute.MIXIN);
          List<NavigatablePsiElement> implMethods = findRuleImplMethods(javaHelper, psiImplUtilClass, methodName, rule);
          if (!implMethods.isEmpty()) return implMethods;
          List<NavigatablePsiElement> mixinMethods = javaHelper.findClassMethods(mixinClass, JavaHelper.MethodType.INSTANCE, methodName, -1);
          return ContainerUtil.concat(implMethods, mixinMethods);
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean b) {
          return PsiElementResolveResult.createResults(getTargetMethods(getElement().getText()));
        }

        @NotNull
        @Override
        public Object[] getVariants() {
          List<LookupElement> list = new ArrayList<>();
          for (NavigatablePsiElement element : getTargetMethods("*")) {
            list.add(LookupElementBuilder.createWithIcon((PsiNamedElement)element));
          }
          return ArrayUtil.toObjectArray(list);
        }

        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
          BnfListEntry element = getElement();
          PsiElement id = ObjectUtils.assertNotNull(element.getId());
          id.replace(BnfElementFactory.createLeafFromText(element.getProject(), newElementName));
          return element;
        }
      }
    };
  }

  @NotNull
  public static List<BnfExpression> getArguments(@NotNull BnfExternalExpression expr) {
    List<BnfExpression> expressions = expr.getExpressionList();
    return expressions.subList(1, expressions.size());
  }
}
