/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.grammar.GrammarKitBundle;
import org.intellij.grammar.psi.BnfChoice;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Vadim Romansky
 * @author gregsh
 */
public class BnfFlipChoiceIntention implements IntentionAction {
  @Override
  public @NotNull String getText() {
    return GrammarKitBundle.message("intention.flip.arguments.text");
  }

  @Override
  public @NotNull String getFamilyName() {
    return GrammarKitBundle.message("intention.flip.arguments.family");
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    return getArguments(file, editor.getCaretModel().getOffset()) != null;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    Pair<PsiElement, PsiElement> arguments = getArguments(file, editor.getCaretModel().getOffset());
    if (arguments == null) return;
    PsiElement newFirst = BnfElementFactory.createRuleFromText(project, "a ::=" + arguments.second.getText()).getExpression();
    PsiElement newSecond = BnfElementFactory.createRuleFromText(project, "a ::=" + arguments.first.getText()).getExpression();
    arguments.second.replace(newSecond);
    arguments.first.replace(newFirst);
  }
  
  private static @Nullable Pair<PsiElement, PsiElement> getArguments(PsiFile file, int offset) {
    PsiElement element = file.getViewProvider().findElementAt(offset);
    BnfChoice choice = PsiTreeUtil.getParentOfType(element, BnfChoice.class);
    if (choice == null) return null;
    for (PsiElement cur = choice.getFirstChild(), prev = null; cur != null; cur = cur.getNextSibling()) {
      if (!(cur instanceof BnfExpression) ) continue;
      int start = prev == null? choice.getTextRange().getStartOffset() : prev.getTextRange().getEndOffset();
      int end = cur.getTextRange().getStartOffset();
      if (start <= offset && offset <= end) return prev == null ? null : Pair.create(cur, prev);
      prev = cur;
    }
    return null;
  }

  @Override
  public boolean startInWriteAction() {
    return true;
  }
}
