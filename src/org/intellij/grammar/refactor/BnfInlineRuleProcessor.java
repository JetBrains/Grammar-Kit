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

package org.intellij.grammar.refactor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.usageView.UsageViewDescriptor;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.TObjectIntHashMap;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

    List<UsageInfo> result = ContainerUtil.newArrayList();
    for (PsiReference reference : ReferencesSearch.search(myRule, myRule.getUseScope(), false)) {
      PsiElement element = reference.getElement();
      if (GrammarUtil.isInAttributesReference(element)) continue;
      result.add(new UsageInfo(element));
    }
    return result.toArray(new UsageInfo[result.size()]);
  }

  protected void refreshElements(PsiElement[] elements) {
    LOG.assertTrue(elements.length == 1 && elements[0] instanceof BnfRule);
    myRule = (BnfRule)elements[0];
  }

  protected void performRefactoring(UsageInfo[] usages) {
    BnfExpression expression = myRule.getExpression();
    boolean meta = ParserGeneratorUtil.Rule.isMeta(myRule);

    CommonRefactoringUtil.sortDepthFirstRightLeftOrder(usages);
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
    BnfRule rule = PsiTreeUtil.getParentOfType(place, BnfRule.class);
    PsiElement parent = place.getParent();
    final List<BnfExpression> expressionList;
    if (parent instanceof BnfExternalExpression) {
      expressionList = ((BnfExternalExpression)parent).getExpressionList();
    }
    else if (parent instanceof BnfSequence) {
      expressionList = ((BnfSequence)parent).getExpressionList();
    }
    else if (parent instanceof BnfRule) {
      expressionList = Collections.emptyList();
    }
    else {
      LOG.error(parent);
      return;
    }
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
      BnfExpressionOptimizer.optimize(pair.first.replace(pair.second));
    }
    inlineExpressionUsage((BnfExpression)parent, expression);
    if (!(parent instanceof BnfExternalExpression)) {
      for (BnfModifier modifier : rule.getModifierList()) {
        if (modifier.getText().equals("external")) {
          modifier.getNextSibling().delete(); // whitespace
          modifier.delete();
          break;
        }
      }
    }
  }
}
