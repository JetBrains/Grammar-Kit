/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfRefOrTokenImpl;
import org.intellij.grammar.psi.impl.BnfReferenceImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfAnnotator implements Annotator, DumbAware {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    PsiElement parent = psiElement.getParent();
    if (parent instanceof BnfRule && ((BnfRule)parent).getId() == psiElement) {
      addRuleHighlighting((BnfRule)parent, psiElement, annotationHolder);
    }
    else if (parent instanceof BnfAttr && ((BnfAttr)parent).getId() == psiElement) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.ATTRIBUTE);
    }
    else if (parent instanceof BnfModifier) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.KEYWORD);
    }
    else if (parent instanceof BnfListEntry && ((BnfListEntry)parent).getId() == psiElement) {
      boolean hasValue = ((BnfListEntry)parent).getLiteralExpression() != null;
      BnfAttr attr = PsiTreeUtil.getParentOfType(parent, BnfAttr.class);
      KnownAttribute<?> attribute = attr != null ? KnownAttribute.getCompatibleAttribute(attr.getName()) : null;
      if (attribute == KnownAttribute.METHODS && !hasValue) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.EXTERNAL);
      }
    }
    else if (psiElement instanceof BnfReferenceOrToken) {
      if (parent instanceof BnfAttr) {
        String text = psiElement.getText();
        if ("true".equals(text) || "false".equals(text)) {
          annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.KEYWORD);
          return;
        }
      }
      PsiReference reference = psiElement.getReference();
      Object resolve = reference == null ? null : reference.resolve();
      if (resolve instanceof BnfRule) {
        addRuleHighlighting((BnfRule)resolve, psiElement, annotationHolder);
      }
      else if (resolve instanceof BnfAttr) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.ATTRIBUTE);
      }
      else if (GrammarUtil.isExternalReference(psiElement)) {
        if (resolve == null && parent instanceof BnfExternalExpression && ((BnfExternalExpression)parent).getArguments().isEmpty() &&
            ParserGeneratorUtil.Rule.isMeta(ParserGeneratorUtil.Rule.of((BnfRefOrTokenImpl)psiElement))) {
          annotationHolder.createInfoAnnotation(parent, null).setTextAttributes(BnfSyntaxHighlighter.META_PARAM);
        }
        else {
          annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.EXTERNAL);
        }
      }
      else if (resolve == null) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.TOKEN);
      }
    }
    else if (psiElement instanceof BnfStringLiteralExpression) {
      if (parent instanceof BnfAttrPattern || parent instanceof BnfAttr || parent instanceof BnfListEntry) {
        annotationHolder.createInfoAnnotation(psiElement, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.PATTERN);
      }
      if (parent instanceof BnfAttr || parent instanceof BnfListEntry) {
        String attrName = ObjectUtils.assertNotNull(PsiTreeUtil.getParentOfType(psiElement, BnfAttr.class)).getName();
        KnownAttribute<?> attribute = KnownAttribute.getCompatibleAttribute(attrName);
        if (attribute != null) {
          BnfReferenceImpl<?> reference = ContainerUtil.findInstance(psiElement.getReferences(), BnfReferenceImpl.class);
          PsiElement resolve = reference == null ? null : reference.resolve();
          if (resolve instanceof BnfRule) {
            TextRange range = reference.getRangeInElement().shiftRight(psiElement.getTextRange().getStartOffset());
            annotationHolder.createInfoAnnotation(range, null).setTextAttributes(BnfSyntaxHighlighter.RULE);
          }
        }
      }
      else {
        String text = ParserGeneratorUtil.getLiteralValue((BnfStringLiteralExpression)psiElement);
        if (!RuleGraphHelper.getTokenTextToNameMap((BnfFile)psiElement.getContainingFile()).containsKey(text)) {
          String message = "Tokens matched by text are slower than tokens matched by types";
          annotationHolder.createInfoAnnotation(psiElement, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
          annotationHolder.createInfoAnnotation(psiElement, message).setTextAttributes(BnfSyntaxHighlighter.PATTERN);
        }
      }
    }
  }

  private static void addRuleHighlighting(BnfRule rule, PsiElement psiElement, AnnotationHolder annotationHolder) {
    if (ParserGeneratorUtil.Rule.isMeta(rule)) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.META_RULE);
    }
    else {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.RULE);
    }
    PsiFile file = rule.getContainingFile();
    if (StringUtil.isNotEmpty(((BnfFile)file).findAttributeValue(rule, KnownAttribute.RECOVER_WHILE, null))) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.RECOVER_MARKER);
    }
  }

}
