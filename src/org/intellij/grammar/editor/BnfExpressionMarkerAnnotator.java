/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfExpressionMarkerAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (!(psiElement instanceof BnfRule)) return;
    BnfRule rule = (BnfRule) psiElement;
    // todo
    //boolean expression = ExpressionGeneratorHelper.getInfoForExpressionParsing(ExpressionHelper.getCached((BnfFile)rule.getContainingFile()), rule) != null;
    //if (expression) {
    //  annotationHolder.createInfoAnnotation(rule.getNameIdentifier(), null).setTextAttributes(key);
    //}
  }

}
