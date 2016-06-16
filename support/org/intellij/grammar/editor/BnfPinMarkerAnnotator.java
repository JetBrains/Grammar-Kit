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
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PairProcessor;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author gregsh
 */
public class BnfPinMarkerAnnotator implements Annotator, DumbAware {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (!(psiElement instanceof BnfRule)) return;
    BnfRule rule = (BnfRule) psiElement;
    final BnfFile bnfFile = (BnfFile)rule.getContainingFile();
    final ArrayList<Pair<BnfExpression, BnfAttr>> pinned = new ArrayList<Pair<BnfExpression, BnfAttr>>();
    GrammarUtil.processPinnedExpressions(rule, new PairProcessor<BnfExpression, ParserGeneratorUtil.PinMatcher>() {
      @Override
      public boolean process(BnfExpression bnfExpression, ParserGeneratorUtil.PinMatcher pinMatcher) {
        BnfAttr attr = bnfFile.findAttribute(pinMatcher.rule, KnownAttribute.PIN, pinMatcher.funcName);
        return pinned.add(Pair.create(bnfExpression, attr));
      }
    });
    for (int i = 0, len = pinned.size(); i < len; i++) {
      BnfExpression e = pinned.get(i).first;
      BnfExpression prev = i == 0? null : pinned.get(i - 1).first;
      BnfAttr attr = pinned.get(i).second;
      boolean fullRange = prev == null || !PsiTreeUtil.isAncestor(e, prev, true);
      TextRange textRange = e.getTextRange();
      TextRange infoRange = fullRange ? textRange : TextRange.create(prev.getTextRange().getEndOffset() + 1, textRange.getEndOffset());
      String message = attr == null? (fullRange ? "pinned" : "pinned again") : attr.getText();
      annotationHolder.createInfoAnnotation(infoRange, message).setTextAttributes(BnfSyntaxHighlighter.PIN_MARKER);
    }
  }

}
