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
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.generator.ExpressionGeneratorHelper;
import org.intellij.grammar.generator.ExpressionHelper;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.psi.BnfTypes.BNF_SEQUENCE;

/**
 * @author gregsh
 */
public class BnfExpressionMarkerAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (!(psiElement instanceof BnfRule)) return;
    BnfRule rule = (BnfRule) psiElement;
    //boolean expression = ExpressionGeneratorHelper.getInfoForExpressionParsing(ExpressionHelper.getCached((BnfFile)rule.getContainingFile()), rule) != null;
    //if (expression) {
    //  annotationHolder.createInfoAnnotation(rule.getNameIdentifier(), null).setTextAttributes(key);
    //}
  }

}
