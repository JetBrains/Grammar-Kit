/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfReferenceImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author gregsh
 */
final class BnfAnnotator implements Annotator, DumbAware {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    PsiElement parent = psiElement.getParent();
    if (parent instanceof BnfRule rule && rule.getId() == psiElement) {
      addRuleHighlighting(rule, psiElement, annotationHolder);
    }
    else if (parent instanceof BnfAttr attr && attr.getId() == psiElement) {
      annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(psiElement)
        .textAttributes(BnfSyntaxHighlighter.ATTRIBUTE)
        .create();
    }
    else if (parent instanceof BnfModifier) {
      annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(psiElement)
        .textAttributes(BnfSyntaxHighlighter.KEYWORD)
        .create();
    }
    else if (parent instanceof BnfListEntry listEntry && listEntry.getId() == psiElement) {
      boolean hasValue = listEntry.getLiteralExpression() != null;
      BnfAttr attr = PsiTreeUtil.getParentOfType(listEntry, BnfAttr.class);
      KnownAttribute<?> attribute = attr != null ? KnownAttribute.getCompatibleAttribute(attr.getName()) : null;
      if (attribute == KnownAttribute.METHODS && !hasValue) {
        annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
          .range(psiElement)
          .textAttributes(BnfSyntaxHighlighter.EXTERNAL)
          .create();
      }
    }
    else if (psiElement instanceof BnfReferenceOrToken refOrToken) {
      if (parent instanceof BnfAttr) {
        String text = refOrToken.getText();
        if ("true".equals(text) || "false".equals(text)) {
          annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(refOrToken)
            .textAttributes(BnfSyntaxHighlighter.KEYWORD)
            .create();
          return;
        }
      }
      PsiReference reference = refOrToken.getReference();
      Object resolve = reference == null ? null : reference.resolve();
      if (resolve instanceof BnfRule rule) {
        addRuleHighlighting(rule, refOrToken, annotationHolder);
      }
      else if (resolve instanceof BnfAttr) {
        annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
          .range(refOrToken)
          .textAttributes(BnfSyntaxHighlighter.ATTRIBUTE)
          .create();
      }
      else if (GrammarUtil.isExternalReference(refOrToken)) {
        if (resolve == null && parent instanceof BnfExternalExpression extExpr && extExpr.getArguments().isEmpty() &&
            ParserGeneratorUtil.Rule.isMeta(ParserGeneratorUtil.Rule.of(refOrToken))) {
          annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(parent)
            .textAttributes(BnfSyntaxHighlighter.META_PARAM)
            .create();
        }
        else {
          annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(refOrToken)
            .textAttributes(BnfSyntaxHighlighter.EXTERNAL)
            .create();
        }
      }
      else if (resolve == null) {
        var text = refOrToken.getId().getText();
        if (RuleGraphHelper.getTokenNameToTextMap((BnfFile)refOrToken.getContainingFile()).containsKey(text)) {
          annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(refOrToken)
            .textAttributes(BnfSyntaxHighlighter.EXPLICIT_TOKEN)
            .create();
        } else {
          annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(refOrToken)
            .textAttributes(BnfSyntaxHighlighter.IMPLICIT_TOKEN)
            .create();
        }
      }
    }
    else if (psiElement instanceof BnfStringLiteralExpression) {
      if (parent instanceof BnfAttrPattern || parent instanceof BnfAttr || parent instanceof BnfListEntry) {
        annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
          .range(psiElement)
          .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
          .create();
        annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
          .range(psiElement)
          .textAttributes(BnfSyntaxHighlighter.PATTERN)
          .create();
      }
      if (parent instanceof BnfAttr || parent instanceof BnfListEntry) {
        String attrName = Objects.requireNonNull(PsiTreeUtil.getParentOfType(psiElement, BnfAttr.class)).getName();
        KnownAttribute<?> attribute = KnownAttribute.getCompatibleAttribute(attrName);
        if (attribute != null) {
          BnfReferenceImpl<?> reference = ContainerUtil.findInstance(psiElement.getReferences(), BnfReferenceImpl.class);
          PsiElement resolve = reference == null ? null : reference.resolve();
          if (resolve instanceof BnfRule) {
            TextRange range = reference.getRangeInElement().shiftRight(psiElement.getTextRange().getStartOffset());
            annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
              .range(range)
              .textAttributes(BnfSyntaxHighlighter.RULE)
              .create();
          }
        }
      }
      else {
        String text = ParserGeneratorUtil.getLiteralValue((BnfStringLiteralExpression)psiElement);
        if (!RuleGraphHelper.getTokenTextToNameMap((BnfFile)psiElement.getContainingFile()).containsKey(text)) {
          String message = "Tokens matched by text are slower than tokens matched by types";
          annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(psiElement)
            .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
            .create();
          annotationHolder.newAnnotation(HighlightSeverity.INFORMATION, message)
            .range(psiElement)
            .textAttributes(BnfSyntaxHighlighter.PATTERN)
            .create();
        }
      }
    }
  }

  private static void addRuleHighlighting(BnfRule rule, PsiElement psiElement, AnnotationHolder annotationHolder) {
    annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
      .range(psiElement)
      .textAttributes(ParserGeneratorUtil.Rule.isMeta(rule) ? BnfSyntaxHighlighter.META_RULE : BnfSyntaxHighlighter.RULE)
      .create();
    PsiFile file = rule.getContainingFile();
    if (StringUtil.isNotEmpty(((BnfFile)file).findAttributeValue(rule, KnownAttribute.RECOVER_WHILE, null))) {
      annotationHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(psiElement)
        .textAttributes(BnfSyntaxHighlighter.RECOVER_MARKER)
        .create();
    }
  }
}
