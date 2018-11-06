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

package org.intellij.jflex.psi.impl;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.containers.JBIterable;
import org.intellij.jflex.psi.JFlexDeclarationsSection;
import org.intellij.jflex.psi.JFlexJavaCode;
import org.intellij.jflex.psi.JFlexStateDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class JFlexStateUsageSearcher extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> implements DumbAware {
  protected JFlexStateUsageSearcher() {
    super(true);
  }

  @Override
  public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<PsiReference> consumer) {
    PsiElement element = queryParameters.getElementToSearch();
    PsiFile containingFile = element.getContainingFile();
    if (element instanceof PsiField) {
      PsiElement context = containingFile == null ? null : containingFile.getContext();
      if (!(context instanceof JFlexJavaCode)) return;
      String name = ((PsiField)element).getName();
      if (name == null) return;
      PsiFile file = context.getContainingFile();
      JFlexStateDefinition state =
        SyntaxTraverser.psiTraverser(
          PsiTreeUtil.findChildOfType(file, JFlexDeclarationsSection.class))
          .filter(JFlexStateDefinition.class)
          .filter(o -> name.equals(o.getName()))
          .first();
      if (state != null) {
        SearchScope scope = queryParameters.getEffectiveSearchScope().intersectWith(new LocalSearchScope(file));
        queryParameters.getOptimizer().searchWord(state.getName(), scope, true, state);
      }
    }
    else if (element instanceof JFlexStateDefinition) {
      JFlexStateDefinition state = (JFlexStateDefinition)element;
      String name = state.getName();
      JFlexJavaCode javaCode = SyntaxTraverser.psiTraverser(containingFile).filter(JFlexJavaCode.class).first();

      if (javaCode == null) return;
      Pair<PsiElement, TextRange> injectedFile =
        JBIterable.from(InjectedLanguageManager.getInstance(javaCode.getProject()).getInjectedPsiFiles(javaCode)).first();
      if (injectedFile != null && injectedFile.first instanceof PsiJavaFile) {
        PsiJavaFile javaFile = (PsiJavaFile)injectedFile.first;
        PsiField field = JBIterable.of(javaFile.getClasses())
          .take(1).flatMap(o -> JBIterable.of(o.getFields()))
          .find(o -> name.equals(o.getName()));
        if (field != null) {
          SearchScope scope = queryParameters.getEffectiveSearchScope().intersectWith(new LocalSearchScope(javaFile));
          queryParameters.getOptimizer().searchWord(name, scope, true, field);
        }
      }
    }
  }
}
