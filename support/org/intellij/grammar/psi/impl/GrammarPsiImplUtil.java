/*
 * Copyright 2011-present Greg Shrago
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
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfListEntry;
import org.intellij.grammar.psi.BnfLiteralExpression;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

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
          List<LookupElement> list = ContainerUtil.newArrayList();
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
}
