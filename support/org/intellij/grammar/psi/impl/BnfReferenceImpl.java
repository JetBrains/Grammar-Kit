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
package org.intellij.grammar.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author gregsh
 */
public class BnfReferenceImpl<T extends PsiElement> extends PsiReferenceBase<T> {
  private static final ResolveCache.Resolver MY_RESOLVER =
    new ResolveCache.Resolver() {
      @Override
      public PsiElement resolve(PsiReference psiReference, boolean incompleteCode) {
        return ((BnfReferenceImpl)psiReference).resolveInner();
      }
    };

  public BnfReferenceImpl(@NotNull T element, TextRange range) {
    super(element, range);
  }

  @Override
  public PsiElement resolve() {
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, MY_RESOLVER, true, false);
  }

  private PsiElement resolveInner() {
    final Ref<PsiElement> result = Ref.create(null);
    final String text = getRangeInElement().substring(myElement.getText());
    processResolveVariants(new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof PsiNamedElement) {
          if (text.equals(((PsiNamedElement)psiElement).getName())) {
            result.set(psiElement);
            return false;
          }
        }
        return true;
      }
    });
    return result.get();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final ArrayList<LookupElement> list = new ArrayList<LookupElement>();
    processResolveVariants(new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof BnfNamedElement) {
          LookupElementBuilder builder = LookupElementBuilder.create((PsiNamedElement) psiElement).
                  setIcon(psiElement.getIcon(Iconable.ICON_FLAG_OPEN));
          list.add(psiElement instanceof BnfRule? builder.setBold() : builder);
        }
        return true;
      }
    });
    return list.toArray(new Object[list.size()]);
  }

  private void processResolveVariants(final Processor<PsiElement> processor) {
    PsiFile file = myElement.getContainingFile();
    if (!(file instanceof BnfFile)) return;
    final boolean ruleMode = myElement instanceof BnfStringLiteralExpression;

    BnfAttrs attrs = PsiTreeUtil.getParentOfType(myElement, BnfAttrs.class);
    if (attrs != null && !ruleMode) {
      if (!ContainerUtil.process(attrs.getChildren(), processor)) return;
      final int textOffset = myElement.getTextOffset();
      ContainerUtil.process(((BnfFile)file).getAttributes(), new Processor<BnfAttrs>() {
        @Override
        public boolean process(BnfAttrs attrs) {
          return attrs.getTextOffset() <= textOffset && ContainerUtil.process(attrs.getAttrList(), processor);
        }
      });
    }
    else {
      ContainerUtil.process(((BnfFile)file).getRules(), processor);
    }
  }

}
