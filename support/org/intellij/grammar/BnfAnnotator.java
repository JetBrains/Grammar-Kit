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
package org.intellij.grammar;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfRefOrTokenImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 23:05
 */
public class BnfAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (psiElement.getParent() instanceof BnfRule && ((BnfRule)psiElement.getParent()).getId() == psiElement) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(SyntaxHighlighterColors.KEYWORD);
    }
    if (psiElement.getParent() instanceof BnfAttr && ((BnfAttr)psiElement.getParent()).getId() == psiElement) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(SyntaxHighlighterColors.LINE_COMMENT);
    }
    if (psiElement instanceof BnfRefOrTokenImpl) {
      String text = psiElement.getText();
      if (text.equals("true") || text.equals("false")) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(SyntaxHighlighterColors.KEYWORD);
        return;
      }
      PsiReference reference = psiElement.getReference();
      Object resolve = reference == null ? null : reference.resolve();
      if (resolve instanceof BnfRule) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(SyntaxHighlighterColors.KEYWORD);
      }
      else if (resolve instanceof BnfAttr) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(SyntaxHighlighterColors.LINE_COMMENT);
      }
      else if (resolve == null && psiElement.getParent() instanceof BnfAttrValue) {
        annotationHolder.createErrorAnnotation(psiElement, "Unresolved reference");
      }
      else if (resolve == null && !(psiElement.getParent() instanceof BnfModifier)) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(SyntaxHighlighterColors.STRING);
        if (isTokenTextSuspicious(text)) {
          annotationHolder.createWarningAnnotation(psiElement, "Suspicious lowercase token");
        }
      }
    }
    else if (psiElement instanceof BnfStringLiteralExpression && psiElement.getParent() instanceof BnfAttrValue) {
      final String attrName = ((PsiNamedElement)psiElement.getParent().getParent()).getName();
      if (Arrays.asList("extends", "implements", "recoverUntil").contains(attrName)
          && !psiElement.getText().contains(".")) {
        PsiReference reference = psiElement.getReference();
        Object resolve = reference == null ? null : reference.resolve();
        if (resolve instanceof BnfRule) {
          annotationHolder.createInfoAnnotation(reference.getRangeInElement().shiftRight(psiElement.getTextRange().getStartOffset()), null)
            .setTextAttributes(SyntaxHighlighterColors.KEYWORD);
        }
        else if (resolve == null) {
          annotationHolder.createErrorAnnotation(psiElement, "Unresolved reference");
        }
      }
    }
  }

  public static boolean isTokenTextSuspicious(String text) {
    return text.contains("_") && text.equals(text.toLowerCase());
  }
}
