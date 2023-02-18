/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
  private static final Logger LOG = Logger.getInstance(BnfInlineRuleProcessor.class);

  private BnfRule myRule;
  private final PsiReference myReference;
  private final boolean myInlineThisOnly;

  public BnfInlineRuleProcessor(BnfRule rule, Project project, PsiReference ref, boolean isInlineThisOnly) {
    super(project);
    myRule = rule;
    myReference = ref;
    myInlineThisOnly = isInlineThisOnly;
  }

  @Override
  protected @NotNull UsageViewDescriptor createUsageViewDescriptor(UsageInfo @NotNull [] usages) {
    return new BnfInlineViewDescriptor(myRule);
  }

  @Override
  protected @NotNull String getCommandName() {
    return "Inline rule '" + myRule.getName() + "'";
  }

  @Override
  protected UsageInfo @NotNull [] findUsages() {
    if (myInlineThisOnly) return new UsageInfo[]{new UsageInfo(myReference.getElement())};

    List<UsageInfo> result = new ArrayList<>();
    for (PsiReference reference : ReferencesSearch.search(myRule, myRule.getUseScope(), false)) {
      PsiElement element = reference.getElement();
      if (GrammarUtil.isInAttributesReference(element)) continue;
      result.add(new UsageInfo(element));
    }
    return result.toArray(UsageInfo.EMPTY_ARRAY);
  }

  @Override
  protected void refreshElements(PsiElement @NotNull [] elements) {
    LOG.assertTrue(elements.length == 1 && elements[0] instanceof BnfRule);
    myRule = (BnfRule)elements[0];
  }

  @Override
  protected void performRefactoring(UsageInfo @NotNull [] usages) {
    BnfExpression expression = myRule.getExpression();
    boolean meta = ParserGeneratorUtil.Rule.isMeta(myRule);

    CommonRefactoringUtil.sortDepthFirstRightLeftOrder(usages);
    for (UsageInfo info : usages) {
      try {
        BnfExpression element = (BnfExpression)info.getElement();
        boolean metaRuleRef = GrammarUtil.isExternalReference(element);
        if (meta && metaRuleRef) {
          inlineMetaRuleUsage(myProject, element, expression);
        }
        else if (!meta && !metaRuleRef) {
          inlineExpressionUsage(myProject, element, expression);
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

  private static void inlineExpressionUsage(Project project, BnfExpression place, BnfExpression ruleExpr) throws IncorrectOperationException {
    BnfExpression replacement = BnfElementFactory.createExpressionFromText(project, '(' + ruleExpr.getText() + ')');
    BnfExpressionOptimizer.optimize(project, place.replace(replacement));
  }


  private static void inlineMetaRuleUsage(Project project, BnfExpression place, BnfExpression expression) {
    BnfRule rule = PsiTreeUtil.getParentOfType(place, BnfRule.class);
    PsiElement parent = place.getParent();
    List<BnfExpression> expressionList;
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
      LOG.error("Unexpected element: " + (parent == null ? "null" : parent.getClass().getName()));
      return;
    }
    Object2IntMap<String> visited = new Object2IntOpenHashMap<>();
    LinkedList<Pair<PsiElement, PsiElement>> work = new LinkedList<>();
    (expression = (BnfExpression)expression.copy()).acceptChildren(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(@NotNull PsiElement element) {
        if (element instanceof BnfExternalExpression) {
          List<BnfExpression> list = ((BnfExternalExpression)element).getExpressionList();
          if (list.size() == 1) {
            String text = list.get(0).getText();
            int idx = visited.getInt(text);
            if (idx == 0) visited.put(text, idx = visited.size() + 1);
            if (idx < expressionList.size()) {
              work.addFirst(Pair.create(element, expressionList.get(idx)));
            }
          }
        }
        else {
          super.visitElement(element);
        }
      }
    });
    for (Pair<PsiElement, PsiElement> pair : work) {
      BnfExpressionOptimizer.optimize(project, pair.first.replace(pair.second));
    }
    inlineExpressionUsage(project, (BnfExpression)parent, expression);
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
