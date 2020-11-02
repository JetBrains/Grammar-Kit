/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
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
      holder.createInfoAnnotation(element, null).setTextAttributes(JFlexSyntaxHighlighterFactory.MACRO);
    }
    else if (element instanceof JFlexMacroRefExpression) {
      PsiElement resolve = ((JFlexMacroRefExpression)element).getMacroReference().getReference().resolve();
      holder.createInfoAnnotation(element, null).setTextAttributes(JFlexSyntaxHighlighterFactory.MACRO);
      if (resolve == null) {
        holder.createWarningAnnotation(element, "Unresolved macro reference");
      }
    }
    else if (element instanceof JFlexStateDefinition) {
      holder.createInfoAnnotation(((JFlexStateDefinition)element).getNameIdentifier(), null).setTextAttributes(JFlexSyntaxHighlighterFactory.STATE);
    }
    else if (element instanceof JFlexStateReference) {
      boolean isYYINITIAL = JFlexPsiImplUtil.isYYINITIAL(element.getText());
      PsiReference reference = isYYINITIAL ? null : element.getReference();
      PsiElement resolve = reference == null ? null : reference.resolve();
      holder.createInfoAnnotation(element, null).setTextAttributes(JFlexSyntaxHighlighterFactory.STATE);
      if (!isYYINITIAL && resolve == null) {
        holder.createWarningAnnotation(element, "Unresolved state reference");
      }
    }
    else if (element instanceof JFlexClassExpression) {
      holder.createInfoAnnotation(element, null).setTextAttributes(JFlexSyntaxHighlighterFactory.CLASS);
    }
    else if (element instanceof JFlexJavaCode || element instanceof JFlexJavaType) {
      holder.createInfoAnnotation(element, null).setTextAttributes(JFlexSyntaxHighlighterFactory.RAW_CODE);
    }
  }
}
