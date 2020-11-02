/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.editor;

import com.intellij.codeInsight.editorActions.moveLeftRight.MoveElementLeftRightHandler;
import com.intellij.psi.PsiElement;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BnfMoveLeftRightHandler extends MoveElementLeftRightHandler {

  @NotNull
  @Override
  public PsiElement[] getMovableSubElements(@NotNull PsiElement element) {
    return doGetMovableSubElements(element).toArray(PsiElement.EMPTY_ARRAY);
  }

  private static List<? extends PsiElement> doGetMovableSubElements(PsiElement element) {
    if (element instanceof BnfChoice) {
      return ((BnfChoice)element).getExpressionList();
    }
    else if (element instanceof BnfSequence) {
      return ((BnfSequence)element).getExpressionList();
    }
    else if (element instanceof BnfAttrs) {
      return ((BnfAttrs)element).getAttrList();
    }
    else if (element instanceof BnfValueList) {
      return ((BnfValueList)element).getListEntryList();
    }
    else if (element instanceof BnfExternalExpression) {
      return ((BnfExternalExpression)element).getArguments();
    }
    else {
      return Collections.emptyList();
    }
  }
}
