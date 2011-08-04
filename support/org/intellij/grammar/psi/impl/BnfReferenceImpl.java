/*
 * Copyright 2000-2011 Gregory Shrago
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
package org.intellij.grammar.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfAttrs;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.BnfStringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author gregsh
 */
public class BnfReferenceImpl<T extends PsiElement> extends PsiReferenceBase<T> {
  public BnfReferenceImpl(@NotNull T element, TextRange range) {
    super(element, range);
  }

  @Override
  public PsiElement resolve() {
    final Ref<PsiElement> ref = Ref.create(null);
    final String text = getRangeInElement().substring(myElement.getText());
    processResolveVariants(new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof PsiNamedElement) {
          if (text.equals(((PsiNamedElement)psiElement).getName())) {
            ref.set(psiElement);
            return false;
          }
        }
        return true;
      }
    });
    return ref.get();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final ArrayList<LookupElement> list = new ArrayList<LookupElement>();
    processResolveVariants(new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof BnfRule) {
          list.add(LookupElementBuilder.create(((BnfRule)psiElement).getId().getText()).
            setIcon(PlatformIcons.METHOD_ICON).setBold());
        }
        else if (psiElement instanceof BnfAttr) {
          list.add(LookupElementBuilder.create(((BnfAttr)psiElement).getId().getText()).
            setIcon(PlatformIcons.FIELD_ICON));
        }
        return true;
      }
    });
    return list.toArray(new Object[list.size()]);
  }

  private void processResolveVariants(final Processor<PsiElement> processor) {
    PsiFile file = myElement.getContainingFile();
    if (!(file instanceof BnfFileImpl)) return;
    final boolean ruleMode = myElement instanceof BnfStringLiteralExpression;

    BnfAttrs attrs = PsiTreeUtil.getParentOfType(myElement, BnfAttrs.class);
    if (attrs != null && !ruleMode) {
      if (!ContainerUtil.process(attrs.getChildren(), processor)) return;
      final int textOffset = myElement.getTextOffset();
      GrammarUtil.processChildrenDummyAware(file, new Processor<PsiElement>() {
        @Override
        public boolean process(PsiElement psiElement) {
          if (psiElement.getTextOffset() > textOffset) {
            return false;
          }
          return !(psiElement instanceof BnfAttrs) ||
                 ContainerUtil.process(((BnfAttrs)psiElement).getAttrList(), processor);
        }
      });
    }
    else {
      GrammarUtil.processChildrenDummyAware(file, processor);
    }
  }
}
