/*
 * Copyright 2011-2012 Gregory Shrago
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
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

/**
 * @author gregsh
 */
public class BnfPinMarkerAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (!(psiElement instanceof BnfRule)) return;
    BnfRule rule = (BnfRule) psiElement;
    annotateExpression(rule, rule.getExpression(), rule.getName(), annotationHolder);
  }

  private static boolean annotateExpression(BnfRule rule, BnfExpression tree, String funcName, AnnotationHolder annotationHolder) {
    if (isAtomicExpression(tree)) return false;
    final List<BnfExpression> children = getChildExpressions(tree);
    if (isTrivialNode(tree)) return annotateExpression(rule, children.get(0), funcName, annotationHolder);

    IElementType type = getEffectiveType(tree);
    boolean firstNonTrivial = tree == ParserGeneratorUtil.Rule.firstNotTrivial(rule);
    final Object pinValue = type == BNF_SEQUENCE ? getAttribute(rule, "pin", null, firstNonTrivial ? rule.getName() : funcName) : null;
    final int pinIndex = pinValue instanceof Integer ? (Integer)pinValue : -1;
    final Pattern pinPattern = pinValue instanceof String ? Pattern.compile(StringUtil.unescapeStringCharacters((String) pinValue)) : null;
    boolean pinApplied = false;
    for (int i = 0, childExpressionsSize = children.size(); i < childExpressionsSize; i++) {
      BnfExpression child = children.get(i);
      boolean isAtomic = isAtomicExpression(child);
      boolean fullRange = isAtomic || !annotateExpression(rule, child, getNextName(funcName, i), annotationHolder);
      if (type == BNF_SEQUENCE && !pinApplied && (i == pinIndex - 1 || pinPattern != null && pinPattern.matcher(child.getText()).matches())) {
        pinApplied = true;
        TextRange textRange = child.getTextRange();
        TextRange infoRange = fullRange? textRange : new TextRange(Math.max(textRange.getStartOffset(), textRange.getEndOffset() - 5), textRange.getEndOffset());
        annotationHolder.createInfoAnnotation(infoRange, fullRange? "pinned" : "pinned again").setTextAttributes(BnfSyntaxHighlighter.PIN);
      }
    }
    return pinApplied;
  }

  private static boolean isAtomicExpression(BnfExpression tree) {
    return tree instanceof BnfReferenceOrToken || tree instanceof BnfLiteralExpression || tree instanceof BnfExternalExpression;
  }
}
