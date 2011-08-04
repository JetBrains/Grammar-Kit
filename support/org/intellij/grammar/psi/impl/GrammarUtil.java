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

import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class GrammarUtil {
  public static boolean processChildrenDummyAware(PsiElement element, final Processor<PsiElement> processor) {
    return new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        for (PsiElement child = psiElement.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (child instanceof BnfDummyElementImpl) {
            if (!process(child)) return false;
          }
          else if (!processor.process(child)) return false;
        }
        return true;
      }
    }.process(element);
  }

  public static PsiElement getDummyAwarePrevSibling(PsiElement child) {
    PsiElement prevSibling = child.getPrevSibling();
    while (prevSibling instanceof BnfDummyElementImpl) {
      prevSibling = prevSibling.getLastChild();
    }
    if (prevSibling != null) return prevSibling;
    PsiElement parent = child.getParent();
    while (parent instanceof BnfDummyElementImpl && parent.getPrevSibling() == null) {
      parent = parent.getParent();
    }
    return parent == null? null : parent.getPrevSibling();
  }

  @Nullable
  public static <T extends PsiElement> T findDummyAwareChildOfType(PsiElement element, final Class<T> aClass) {
    final Ref<T> result = Ref.create(null);
    processChildrenDummyAware(element, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (aClass.isInstance(psiElement)) {
          result.set((T)psiElement);
          return false;
        }
        return true;
      }
    });
    return result.get();
  }
}
