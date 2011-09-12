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

package org.intellij.grammar.refactor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.usageView.UsageViewDescriptor;
import com.intellij.util.IncorrectOperationException;
import gnu.trove.TObjectIntHashMap;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfExternalExpression;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/11/11
 * Time: 4:19 PM
 *
 * @author Vadim Romansky
 */
public class BnfInlineRuleProcessor extends BaseRefactoringProcessor {
  private static final Logger LOG = Logger.getInstance("org.intellij.grammar.refactor.BnfInlineRuleProcessor");
  private BnfRule myRule;
  private final PsiReference myReference;
  private final boolean myInlineThisOnly;

  public BnfInlineRuleProcessor(BnfRule rule, Project project, PsiReference ref, boolean isInlineThisOnly) {
    super(project);
    myRule = rule;
    myReference = ref;
    myInlineThisOnly = isInlineThisOnly;
  }

  @NotNull
  protected UsageViewDescriptor createUsageViewDescriptor(UsageInfo[] usages) {
    return new BnfInlineViewDescriptor(myRule);
  }

  protected String getCommandName() {
    return "Inline rule '"+myRule.getName()+"'";
  }

  @NotNull
  protected UsageInfo[] findUsages() {
    if (myInlineThisOnly) return new UsageInfo[]{new UsageInfo(myReference.getElement())};

    PsiReference[] refs = ReferencesSearch.search(myRule, myRule.getUseScope(), false).toArray(new PsiReference[0]);
    UsageInfo[] infos = new UsageInfo[refs.length];
    for (int i = 0, len = refs.length; i < len; i++) {
      PsiElement element = refs[len - i - 1].getElement();
      infos[i] = new UsageInfo(element);
    }
    return infos;
  }

  protected void refreshElements(PsiElement[] elements) {
    LOG.assertTrue(elements.length == 1 && elements[0] instanceof BnfRule);
    myRule = (BnfRule)elements[0];
  }

  protected void performRefactoring(UsageInfo[] usages) {
    BnfExpression expression = myRule.getExpression();
    boolean meta = ParserGeneratorUtil.Rule.isMeta(myRule);
    LOG.assertTrue(expression != null);    

    for (UsageInfo info : usages) {
      try {
        final BnfExpression element = (BnfExpression)info.getElement();
        boolean metaRuleRef = GrammarUtil.isExternalReference(element);
        if (meta && metaRuleRef) {
          inlineMetaRuleUsage(element, expression);
        }
        else if (!meta && !metaRuleRef) {
          inlineExpressionUsage(element, expression);
        }
      }
      catch (IncorrectOperationException e) {
        LOG.error(e);
      }
    }

    if (!myInlineThisOnly) {
      try {
        myRule.delete();
      }
      catch (IncorrectOperationException e) {
        LOG.error(e);
      }
    }
  }

  private static void inlineExpressionUsage(BnfExpression place, BnfExpression ruleExpr) throws IncorrectOperationException {
    BnfExpression replacement = BnfElementFactory.createExpressionFromText(ruleExpr.getProject(), '(' + ruleExpr.getText() + ')');
    BnfExpressionOptimizer.optimize(place.replace(replacement));
  }


  private static void inlineMetaRuleUsage(BnfExpression place, BnfExpression expression) {
    PsiElement parent = place.getParent();
    if (parent instanceof BnfExternalExpression) {
      final List<BnfExpression> expressionList = ((BnfExternalExpression)parent).getExpressionList();
      final TObjectIntHashMap<String> visited = new TObjectIntHashMap<String>();
      final LinkedList<Pair<PsiElement, PsiElement>> work = new LinkedList<Pair<PsiElement, PsiElement>>();
        (expression = (BnfExpression)expression.copy()).acceptChildren(new PsiRecursiveElementWalkingVisitor() {
          @Override
          public void visitElement(PsiElement element) {
            if (element instanceof BnfExternalExpression) {
              List<BnfExpression> list = ((BnfExternalExpression)element).getExpressionList();
              if (list.size() == 1) {
                String text = list.get(0).getText();
                int idx = visited.get(text);
                if (idx == 0) visited.put(text, idx = visited.size() + 1);
                if (idx < expressionList.size()) {
                  work.addFirst(Pair.create(element, (PsiElement)expressionList.get(idx)));
                }
              }
            }
            else {
              super.visitElement(element);
            }
          }
        });
      for (Pair<PsiElement, PsiElement> pair : work) {
        pair.first.replace(pair.second);
      }
      inlineExpressionUsage((BnfExpression)parent, expression);
    }
    else {
      // todo
    }
  }
}
