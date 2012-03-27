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

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PairProcessor;
import gnu.trove.THashSet;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfLiteralExpression;
import org.intellij.grammar.psi.BnfReferenceOrToken;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfRuleLineMarkerProvider extends RelatedItemLineMarkerProvider {

  @Override
  public void collectNavigationMarkers(List<PsiElement> elements,
                                       Collection<? super RelatedItemLineMarkerInfo> result,
                                       boolean forNavigation) {
    Set<PsiElement> visited = forNavigation? new THashSet<PsiElement>() : null;
    for (PsiElement element : elements) {
      PsiElement parent = element.getParent();
      boolean isRuleId = parent instanceof BnfRule && (forNavigation || element == ((BnfRule)parent).getId());
      if (!(isRuleId || forNavigation && element instanceof BnfExpression)) continue;
      List<PsiElement> items = new ArrayList<PsiElement>();
      NavigatablePsiElement method = getMethod(element);
      if (method != null && (!forNavigation || visited.add(method))) {
        items.add(method);
      }
      if (isRuleId) {
        BnfRule rule = RuleGraphHelper.getRealRule((BnfRule)parent);
        if (RuleGraphHelper.shouldGeneratePsi(rule, true)) {
          JavaHelper javaHelper = JavaHelper.getJavaHelper(rule.getProject());
          for (String className : new String[]{ParserGeneratorUtil.getQualifiedRuleClassName(rule, false),
            ParserGeneratorUtil.getQualifiedRuleClassName(rule, true)}) {
            NavigatablePsiElement aClass = javaHelper.findClass(className);
            if (aClass != null && (!forNavigation || visited.add(aClass))) {
              items.add(aClass);
            }
          }
        }
      }
      if (!items.isEmpty()) {
        final NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(BnfIcons.RELATED_METHOD).
          setTargets(items).setTooltipText("Related files");
        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }

  @Nullable
  private static NavigatablePsiElement getMethod(PsiElement element) {
    BnfRule rule = PsiTreeUtil.getParentOfType(element, BnfRule.class);
    if (rule == null) return null;
    Project project = element.getProject();
    String parserClass = ParserGeneratorUtil.getAttribute(rule, "parserClass", "");
    if (StringUtil.isEmpty(parserClass)) return null;
    return JavaHelper.getJavaHelper(project).findClassMethod(parserClass, getMethodName(rule, element), -1);
  }

  public static String getMethodName(BnfRule rule, PsiElement element) {
    final BnfExpression target = PsiTreeUtil.getParentOfType(element, BnfExpression.class, false);
    String ruleName = rule.getName();
    if (target == null) return ruleName;
    final Ref<String> ref = Ref.create(ruleName);
    processExpressionNames(ruleName, rule.getExpression(), new PairProcessor<BnfExpression, String>() {
      @Override
      public boolean process(BnfExpression expression, String s) {
        if (target == expression) {
          ref.set(s);
          return false;
        }
        return true;
      }
    });
    return ref.get();
  }

  public static boolean processExpressionNames(String curName, BnfExpression expression, PairProcessor<BnfExpression, String> processor) {
    int i = 0;
    for (BnfExpression node : ParserGeneratorUtil.getChildExpressions(expression)) {
      if (node instanceof BnfLiteralExpression || node instanceof BnfReferenceOrToken ) {
        if (!processor.process(node, curName)) return false;
      }
      else {
        if (!processor.process(node, curName)) return false;
        String nextName = ParserGeneratorUtil.getNextName(curName, i);
        if (!processExpressionNames(nextName, node, processor)) return false;
      }
      i ++;
    }
    return true;
  }
}
