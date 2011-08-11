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

package org.intellij.grammar;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.intellij.grammar.psi.*;
import org.intellij.grammar.psi.impl.BnfDummyElementImpl;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author gregsh
 */
public class BnfCompletionContributor extends CompletionContributor {
  public static final List<String> KNOWN_ATTRIBUTES =
    Arrays.asList("maxRecursionLevel", "generatePsi", "psiClassPrefix", "psiImplClassSuffix", "psiPackage", "psiImplPackage",
                  "elementTypeClass", "tokenTypeClass",
                  "parserClass", "stubParserClass", "elementTypeHolderClass",
                  "elementTypePrefix", "elementTypeFactory", "tokenClassType", "tokenTypeFactory", "parserImports",
                  "extends", "implements", "methodRenames", "pin", "mixin", "recoverUntil", "memoization", "classHeader");

  public BnfCompletionContributor() {
    extend(CompletionType.BASIC, psiElement().inFile(PlatformPatterns.instanceOf(BnfFileImpl.class)), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        PsiElement parent = position.getParent();
        if (parent instanceof BnfAttrs || parent instanceof BnfAttr && ((BnfAttr)parent).getId() == position) {
          for (String attribute : KNOWN_ATTRIBUTES) {
            result.addElement(LookupElementBuilder.create(attribute).setIcon(BnfIcons.ATTRIBUTE));
          }
        }
      }
    });
    extend(CompletionType.BASIC, psiElement().inFile(PlatformPatterns.instanceOf(BnfFileImpl.class)), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull final CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        PsiElement parent = position.getParent();
        BnfRule rule = parent instanceof BnfReferenceOrToken? PsiTreeUtil.getParentOfType(parent, BnfRule.class) : null;
        if (rule != null && PsiTreeUtil.getDeepestLast(rule) == position || parent instanceof BnfDummyElementImpl) {
          parameters.getOriginalFile().acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
              if (element instanceof BnfReferenceOrToken && !(element.getParent() instanceof BnfModifier)) {
                String text = element.getText();
                if (BnfAnnotator.isTokenTextSuspicious(text) && element.getReference().resolve() == null) {
                  result.addElement(LookupElementBuilder.create(text));
                }
              }
              super.visitElement(element);
            }
          });
        }
      }
    });
  }
}
