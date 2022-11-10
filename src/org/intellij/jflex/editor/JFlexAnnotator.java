/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.intellij.jflex.psi.*;
import org.intellij.jflex.psi.impl.JFlexPsiImplUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class JFlexAnnotator implements Annotator, DumbAware {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    PsiElement parent = element.getParent();
    if (parent instanceof JFlexMacroDefinition && element == ((JFlexMacroDefinition)parent).getNameIdentifier()) {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(element)
        .textAttributes(JFlexSyntaxHighlighterFactory.MACRO)
        .create();
    }
    else if (element instanceof JFlexMacroRefExpression) {
      PsiElement resolve = ((JFlexMacroRefExpression)element).getMacroReference().getReference().resolve();
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(element)
        .textAttributes(JFlexSyntaxHighlighterFactory.MACRO)
        .create();
      if (resolve == null) {
        holder.newAnnotation(HighlightSeverity.WARNING, "Unresolved macro reference")
          .range(element)
          .create();
      }
    }
    else if (element instanceof JFlexStateDefinition) {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(((JFlexStateDefinition)element).getNameIdentifier())
        .textAttributes(JFlexSyntaxHighlighterFactory.STATE)
        .create();
    }
    else if (element instanceof JFlexStateReference) {
      boolean isYYINITIAL = JFlexPsiImplUtil.isYYINITIAL(element.getText());
      PsiReference reference = isYYINITIAL ? null : element.getReference();
      PsiElement resolve = reference == null ? null : reference.resolve();
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(element)
        .textAttributes(JFlexSyntaxHighlighterFactory.STATE)
        .create();
      if (!isYYINITIAL && resolve == null) {
        holder.newAnnotation(HighlightSeverity.WARNING, "Unresolved state reference")
          .range(element)
          .create();
      }
    }
    else if (element instanceof JFlexClassExpression) {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(element)
        .textAttributes(JFlexSyntaxHighlighterFactory.CLASS)
        .create();
    }
    else if (element instanceof JFlexJavaCode || element instanceof JFlexJavaType) {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(element)
        .textAttributes(JFlexSyntaxHighlighterFactory.RAW_CODE)
        .create();
    }
  }
}
