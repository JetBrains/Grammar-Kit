/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.refactor.BnfIntroduceRuleHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/25/11
 * Time: 7:45 PM
 *
 * @author Vadim Romansky
 */
public class CreateRuleFromTokenFix implements LocalQuickFix {

  private final String myName;

  public CreateRuleFromTokenFix(String name) {
    myName = name;
  }

  @Override
  public @NotNull String getName() {
    return "Create '" + myName + "' rule";
  }

  @Override
  public @NotNull String getFamilyName() {
    return "Create rule from usage";
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    BnfRule rule = PsiTreeUtil.getParentOfType(element, BnfRule.class);
    if (rule == null) return;

    BnfRule addedRule = BnfIntroduceRuleHandler.addNextRule(project, rule, "private " + myName + " ::= ");
    FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(rule.getContainingFile().getVirtualFile());
    if (selectedEditor instanceof TextEditor) {
      Editor editor = ((TextEditor)selectedEditor).getEditor();
      editor.getCaretModel()
        .moveToOffset(addedRule.getTextRange().getEndOffset() - (BnfIntroduceRuleHandler.endsWithSemicolon(addedRule) ? 1 : 0));
      editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
    }
  }
}
