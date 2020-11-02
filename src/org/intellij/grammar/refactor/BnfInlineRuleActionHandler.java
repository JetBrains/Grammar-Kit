/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.refactor;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import org.intellij.grammar.BnfLanguage;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/11/11
 * Time: 1:56 PM
 *
 * @author Vadim Romansky
 */
public class BnfInlineRuleActionHandler extends InlineActionHandler {
  @Override
  public boolean isEnabledForLanguage(Language language) {
    return (language == BnfLanguage.INSTANCE);
  }

  @Override
  public boolean canInlineElement(PsiElement psiElement) {
    return psiElement instanceof BnfRule;
  }

  @Override
  public void inlineElement(Project project, Editor editor, PsiElement psiElement) {
    BnfRule rule = (BnfRule)psiElement;
    BnfAttrs attrs = rule.getAttrs();
    if (PsiTreeUtil.hasErrorElements(rule)) {
      CommonRefactoringUtil.showErrorHint(project, editor, "Rule has errors", "Inline Rule", null);
      return;
    }

    if (attrs != null && !attrs.getAttrList().isEmpty()) {
      CommonRefactoringUtil.showErrorHint(project, editor, "Rule has attributes", "Inline Rule", null);
      return;
    }

    Collection<PsiReference> allReferences = ReferencesSearch.search(psiElement).findAll();
    if (allReferences.isEmpty()) {
      CommonRefactoringUtil.showErrorHint(project, editor, "Rule is never used", "Inline Rule", null);
      return;
    }

    boolean hasNonAttributeRefs = false;
    for (PsiReference ref : allReferences) {
      if (!GrammarUtil.isInAttributesReference(ref.getElement())) {
        hasNonAttributeRefs = true;
        break;
      }
    }
    if (!hasNonAttributeRefs) {
      CommonRefactoringUtil.showErrorHint(project, editor, "Rule is referenced only in attributes", "Inline Rule", null);
      return;
    }
    if (!CommonRefactoringUtil.checkReadOnlyStatus(project, rule)) return;
    PsiReference reference = editor != null ? TargetElementUtil.findReference(editor, editor.getCaretModel().getOffset()) : null;
    if (reference != null && !rule.equals(reference.resolve())) {
      reference = null;
    }

    InlineRuleDialog dialog = new InlineRuleDialog(project, rule, reference);
    dialog.show();
  }
}
