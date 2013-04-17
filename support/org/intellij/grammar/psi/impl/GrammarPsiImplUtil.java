/*
 * Copyright 2011-2013 Gregory Shrago
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
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfListEntry;
import org.intellij.grammar.psi.BnfLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

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
    final JavaHelper javaHelper = JavaHelper.getJavaHelper(o.getProject());
    return new PsiReference[] {
      new PsiReferenceBase<BnfListEntry>(o, TextRange.from(id.getStartOffsetInParent(), id.getTextLength())) {
        @Override
        public PsiElement resolve() {
          return javaHelper.findClassMethod(psiImplUtilClass, getElement().getText(), -1);
        }

        @NotNull
        @Override
        public Object[] getVariants() {
          final ArrayList<LookupElement> list = new ArrayList<LookupElement>();
          //ParserGeneratorUtil.getQualifiedRuleClassName(rule, false)
          for (NavigatablePsiElement element : javaHelper.getClassMethods(psiImplUtilClass, true)) {
            List<String> methodTypes = javaHelper.getMethodTypes(element);
            if (methodTypes.size() > 1) {
              list.add(LookupElementBuilder.createWithIcon((PsiNamedElement)element));
            }
          }
          return ArrayUtil.toObjectArray(list);
        }
      }
    };
  }
}
