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

package org.intellij.grammar;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;

/**
 * @author gregsh
 */
public class BnfRuleLineMarkerProvider extends RelatedItemLineMarkerProvider {
  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
    PsiElement parent = element.getParent();
    if (parent instanceof BnfRule && ((BnfRule)parent).getId() == element) {
      PsiMethod method = getMethod(element);
      if (method != null) {
        GotoRelatedItem item = new GotoRelatedItem(method);
        result.add(new RelatedItemLineMarkerInfo<PsiElement>(
          element, element.getTextRange(), BnfIcons.RELATED_METHOD, Pass.UPDATE_OVERRIDEN_MARKERS, null, new MyNavHandler(),
          GutterIconRenderer.Alignment.RIGHT, Collections.singletonList(item)));
      }
    }
  }

  @Nullable
  private static PsiMethod getMethod(PsiElement element) {
    BnfRule rule = PsiTreeUtil.getParentOfType(element, BnfRule.class);
    if (rule == null) return null;
    Project project = element.getProject();
    String parserClass = ParserGeneratorUtil.getAttribute(rule, "parserClass", "");
    PsiClass aClass = StringUtil.isEmpty(parserClass)? null : JavaPsiFacade
      .getInstance(project).findClass(parserClass, GlobalSearchScope.allScope(project));
    if (aClass != null) {
      String name = getMethodName(rule, element);
      PsiMethod[] methods = aClass.findMethodsByName(name, false);
      if (methods.length == 1) {
        return methods[0];
      }
    }
    return null;
  }

  private static String getMethodName(BnfRule rule, PsiElement element) {
    // todo add expression-level function discovery
    return rule.getName();
  }

  private static class MyNavHandler implements GutterIconNavigationHandler<PsiElement> {
    @Override
    public void navigate(MouseEvent e, PsiElement elt) {
      PsiMethod method = getMethod(elt);
      if (method != null) {
        method.navigate(true);
      }
    }
  }
}
