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

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfRefOrTokenImpl;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.StringTokenizer;

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
      KnownAttribute attribute = attr != null ? KnownAttribute.getCompatibleAttribute(attr.getName()) : null;
      if (attribute == KnownAttribute.METHODS && !hasValue) {
        PsiReference reference = parent.findReferenceAt(psiElement.getStartOffsetInParent());
        PsiElement resolve = reference == null ? null : reference.resolve();
        if (resolve == null) {
          annotationHolder.createWarningAnnotation(psiElement, "Unresolved method reference");
        }
        else {
          annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.EXTERNAL);
        }
      }
    }
    else if (psiElement instanceof BnfReferenceOrToken) {
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
        addRuleHighlighting((BnfRule)resolve, psiElement, annotationHolder);
      }
      else if (resolve instanceof BnfAttr) {
        annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(BnfSyntaxHighlighter.ATTRIBUTE);
      }
      else if (resolve == null && parent instanceof BnfAttr) {
        annotationHolder.createWarningAnnotation(psiElement, "Unresolved rule reference");
      }
      else if (GrammarUtil.isExternalReference(psiElement)) {
        if (resolve == null && parent instanceof BnfExternalExpression && ((BnfExternalExpression)parent).getExpressionList().size() == 1 &&
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
      if (parent instanceof BnfAttrPattern) {
        PsiReference reference = psiElement.getReference();
        if (reference instanceof PsiPolyVariantReference && ((PsiPolyVariantReference)reference).multiResolve(false).length == 0) {
          annotationHolder.createWarningAnnotation(psiElement, "Pattern doesn't match any rule");
        }
      }
      else if (parent instanceof BnfAttr || parent instanceof BnfListEntry) {
        final String attrName = ObjectUtils.assertNotNull(PsiTreeUtil.getParentOfType(psiElement, BnfAttr.class)).getName();
        KnownAttribute attribute = KnownAttribute.getCompatibleAttribute(attrName);
        if (attribute != null) {
          String value = (String)ParserGeneratorUtil.getAttributeValue((BnfExpression)psiElement);
          Object resolve;
          String refType = "";
          JavaHelper javaHelper = JavaHelper.getJavaHelper(psiElement);
          if (attribute.getName().endsWith("Class") || attribute == KnownAttribute.MIXIN) {
            resolve = checkJavaResolve(value, javaHelper);
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
            resolve = value.contains(".") ? checkJavaResolve(value, javaHelper) :
                      ((BnfFile)psiElement.getContainingFile()).getRule(value);
            refType = "rule or class ";
          }
          else if (attribute == KnownAttribute.ELEMENT_TYPE || attribute == KnownAttribute.NAME) {
            resolve = ObjectUtils.chooseNotNull(((BnfFile)psiElement.getContainingFile()).getRule(value), Boolean.TRUE);
            refType = "rule or constant ";
          }
          else if (attribute == KnownAttribute.RECOVER_WHILE && !BnfConstants.RECOVER_AUTO.equals(value)) {
            if (GrammarUtil.isDoubleAngles(value) &&
                ParserGeneratorUtil.Rule.isMeta(ParserGeneratorUtil.Rule.of((BnfStringLiteralExpression)psiElement))) {
              resolve = Boolean.TRUE;
            }
            else {
              resolve = ((BnfFile)psiElement.getContainingFile()).getRule(value);
              refType = "rule ";
            }
          }
          else {
            resolve = Boolean.TRUE;
          }
          TextRange range = ElementManipulators.getValueTextRange(psiElement).shiftRight(psiElement.getTextRange().getStartOffset());
          if (resolve instanceof BnfRule) {
            annotationHolder.createInfoAnnotation(range, null).setTextAttributes(BnfSyntaxHighlighter.RULE);
          }
          else if (resolve == null) {
            annotationHolder.createWarningAnnotation(range, "Unresolved " + refType + "reference");
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

  private static Object checkJavaResolve(String value, JavaHelper javaHelper) {
    Object resolve = null;
    for (String s : StringUtil.tokenize(new StringTokenizer(value, "<>,?", false))) {
      resolve = javaHelper.findClass(s.trim());
      if (resolve == null) break;
    }
    return resolve;
  }
}
