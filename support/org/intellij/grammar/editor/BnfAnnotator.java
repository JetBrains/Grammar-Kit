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
package org.intellij.grammar.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfRefOrTokenImpl;
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
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.RULE);
    }
    else if (parent instanceof BnfAttr && ((BnfAttr)parent).getId() == psiElement) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.ATTRIBUTE);
    }
    else if (parent instanceof BnfModifier) {
      annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.KEYWORD);
    }
    else if (psiElement instanceof BnfRefOrTokenImpl) {
      if (parent instanceof BnfAttr) {
        String text = psiElement.getText();
        if (text.equals("true") || text.equals("false")) {
          annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.KEYWORD);
          return;
        }
      }
      PsiReference reference = psiElement.getReference();
      Object resolve = reference == null ? null : reference.resolve();
      if (resolve instanceof BnfRule) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.RULE);
      }
      else if (resolve instanceof BnfAttr) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.ATTRIBUTE);
      }
      else if (resolve == null && parent instanceof BnfAttr) {
        annotationHolder.createWarningAnnotation(psiElement, "Unresolved rule reference");
      }
      else if (resolve == null) {
        if (GrammarUtil.isExternalReference(psiElement)) {
          annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.EXTERNAL);
        }
        else {
          annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.TOKEN);
        }
      }
    }
    else if (psiElement instanceof BnfStringLiteralExpression && parent instanceof BnfAttr) {
      final String attrName = ((PsiNamedElement)parent).getName();
      KnownAttribute attribute = KnownAttribute.getAttribute(attrName);
      if (attribute != null) {
        String value = (String)ParserGeneratorUtil.getAttributeValue((BnfExpression)psiElement);
        Object resolve;
        String refType = "";
        JavaHelper javaHelper = JavaHelper.getJavaHelper(psiElement.getProject());
        if (attribute.getName().endsWith("Class")) {
          resolve = javaHelper.findClass(value);
          refType = "class ";
        }
        else if (attribute.getName().endsWith("Package")) {
          resolve = javaHelper.findPackage(value);
          refType = "package ";
        }
        else if (attribute.getName().endsWith("Factory")) {
          resolve = Boolean.TRUE; // todo
        }
        else if (attribute == KnownAttribute.EXTENDS || attribute == KnownAttribute.IMPLEMENTS) {
          resolve = value.contains(".")? javaHelper.findClass(value) :
                    ((BnfFile)parent.getContainingFile()).getRule(value);
          refType = "rule or class ";
        }
        else if (attribute == KnownAttribute.RECOVER_UNTIL) {
          resolve = ((BnfFile)parent.getContainingFile()).getRule(value);
          refType = "rule ";
        }
        else {
          resolve = Boolean.TRUE;
        }
        TextRange range = ElementManipulators.getValueTextRange(psiElement).shiftRight(psiElement.getTextRange().getStartOffset());
        if (resolve instanceof BnfRule) {
          annotationHolder.createInfoAnnotation(range, null).setTextAttributes(BnfSyntaxHighlighter.RULE);
        }
        else if (resolve == null) {
          annotationHolder.createWarningAnnotation(range, "Unresolved "+refType+"reference");
        }
      }
    }
  }
}
