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

import com.intellij.codeInsight.unwrap.UnwrapDescriptor;
import com.intellij.codeInsight.unwrap.Unwrapper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfElementFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfUnwrapDescriptor implements UnwrapDescriptor, Unwrapper {
  
  @Override
  public List<Pair<PsiElement, Unwrapper>> collectUnwrappers(Project project, Editor editor, PsiFile file) {
    PsiElement element = findTargetElement(editor, file);
    List<Pair<PsiElement, Unwrapper>> result = new ArrayList<Pair<PsiElement, Unwrapper>>();
    while (element != null) {
      if (element instanceof BnfParenthesized) {
        result.add(new Pair<PsiElement, Unwrapper>(element, this));
      }
      element = element.getParent();
    }
    return result;
  }

  @Override
  public boolean showOptionsDialog() {
    return true;
  }

  @Override
  public boolean shouldTryToRestoreCaretPosition() {
    return true;
  }

  @Override
  public boolean isApplicableTo(PsiElement e) {
    return e instanceof BnfParenthesized && !PsiUtil.hasErrorElementChild(e);
  }

  @Override
  public void collectElementsToIgnore(PsiElement element, Set<PsiElement> result) {
  }

  @Override
  public String getDescription(PsiElement e) {
    PsiElement parent = e.getParent();
    BnfQuantifier quantifier = parent instanceof BnfQuantified ? ((BnfQuantified)parent).getQuantifier() : null;
    BnfPredicateSign sign = parent instanceof BnfPredicate ? ((BnfPredicate)parent).getPredicateSign() : null;
    String prefix = sign == null? "" : sign.getText();
    String suffix =  quantifier == null? "" : quantifier.getText();
    return "Unwrap " + prefix + e.getFirstChild().getText() + "..." + e.getLastChild().getText() + suffix;
  }

  @Override
  public PsiElement collectAffectedElements(PsiElement element, List<PsiElement> toExtract) {
    PsiElement last = element.getLastChild();
    PsiElement first = element.getFirstChild();
    if (element instanceof BnfParenthesized) {
      last = last.getPrevSibling();
      first = first.getNextSibling();
    }
    while (first != last && first instanceof PsiWhiteSpace) {
      first = first.getNextSibling();
    }
    while (last != first && last instanceof PsiWhiteSpace) {
      last = last.getPrevSibling();
    }
    if (first == null || last == null || first == last && last instanceof PsiWhiteSpace) return null;
    for (PsiElement c = first; c != last && c != null; c = c.getNextSibling()) {
      toExtract.add(c);
    }
    PsiElement parent = element.getParent();
    PsiElement target = parent instanceof BnfQuantified || parent instanceof BnfPredicate? parent : element;
    return target;
  }

  @Override
  public List<PsiElement> unwrap(Editor editor, PsiElement element) throws IncorrectOperationException {
    PsiElement last = element.getLastChild();
    PsiElement first = element.getFirstChild();
    if (element instanceof BnfParenthesized) {
      last = last.getPrevSibling();
      first = first.getNextSibling();
    }
    while (first != last && first instanceof PsiWhiteSpace) {
      first = first.getNextSibling();
    }
    while (last != first && last instanceof PsiWhiteSpace) {
      last = last.getPrevSibling();
    }
    if (first == null || last == null || first == last && last instanceof PsiWhiteSpace) return null;
    PsiElement parent = element.getParent();
    PsiElement target = parent instanceof BnfQuantified || parent instanceof BnfPredicate? parent : element;
    return Collections.singletonList(target.replace(BnfElementFactory.createExpressionFromText(
      editor.getProject(), element.getContainingFile().getText().substring(first.getTextRange().getStartOffset(), last.getTextRange().getEndOffset()))));
  }

  @Nullable
  private static PsiElement findTargetElement(Editor editor, PsiFile file) {
    int offset = editor.getCaretModel().getOffset();
    PsiElement endElement = file.findElementAt(offset);
    SelectionModel selectionModel = editor.getSelectionModel();
    if (selectionModel.hasSelection() && selectionModel.getSelectionStart() < offset) {
      PsiElement startElement = file.findElementAt(selectionModel.getSelectionStart());
      if (startElement != null && startElement != endElement && startElement.getTextRange().getEndOffset() == offset) {
        return startElement;
      }
    }
    return endElement;
  }

}
