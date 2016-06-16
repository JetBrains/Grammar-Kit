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

package org.intellij.grammar.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
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
  @NotNull
  @Override
  public String getText() {
    return "Flip arguments";
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return "Flip choice intention";
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    return getArguments(file, editor.getCaretModel().getOffset()) != null;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    final Pair<PsiElement, PsiElement> arguments = getArguments(file, editor.getCaretModel().getOffset());
    if (arguments == null) return;
    PsiElement newFirst = BnfElementFactory.createRuleFromText(project, "a ::=" + arguments.second.getText()).getExpression();
    PsiElement newSecond = BnfElementFactory.createRuleFromText(project, "a ::=" + arguments.first.getText()).getExpression();
    arguments.second.replace(newSecond);
    arguments.first.replace(newFirst);
  }
  
  @Nullable
  private static Pair<PsiElement, PsiElement> getArguments(PsiFile file, int offset) {
    PsiElement element = file.getViewProvider().findElementAt(offset);
    final BnfChoice choice = PsiTreeUtil.getParentOfType(element, BnfChoice.class);
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
