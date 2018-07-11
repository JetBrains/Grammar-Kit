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
