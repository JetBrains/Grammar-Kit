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

package org.intellij.grammar.psi;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfAttrPatternRefSearcher extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
  public BnfAttrPatternRefSearcher() {
    super(true);
  }

  @Override
  public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull final Processor<PsiReference> consumer) {
    final PsiElement target = queryParameters.getElementToSearch();
    if (!(target instanceof BnfRule)) return;

    SearchScope scope = queryParameters.getEffectiveSearchScope();
    if (!(scope instanceof LocalSearchScope)) return;

    for (PsiElement psiElement : ((LocalSearchScope)scope).getScope()) {
      psiElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
        @Override
        public void visitElement(PsiElement element) {
          if (element instanceof BnfAttrs || element instanceof BnfAttr) {
            super.visitElement(element);
          }
          else if (element instanceof BnfAttrPattern) {
            BnfStringLiteralExpression patternExpression = ((BnfAttrPattern)element).getLiteralExpression();
            PsiReference ref = patternExpression != null ? patternExpression.getReference() : null;
            if (ref != null && ref.isReferenceTo(target)) {
              if (!consumer.process(ref)) stopWalking();
            }
          }
        }
      });
    }
  }
}
