/*
 * Copyright 2011-2016 Gregory Shrago
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

package org.intellij.jflex.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.intellij.jflex.psi.JFlexPsiElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.jflex.psi.impl.JavaRefHelper.createJavaFileForExpr;

/**
 * @author gregsh
 */
class TypeRef extends PsiReferenceBase<PsiElement> {
  private static final ResolveCache.AbstractResolver<TypeRef, PsiElement> MY_RESOLVER = new ResolveCache.AbstractResolver<TypeRef, PsiElement>() {
    @Override
    public PsiElement resolve(@NotNull TypeRef ref, boolean b) {
      return resolveInner(ref);
    }
  };

  TypeRef(PsiElement o, TextRange range) {
    super(o, range);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, MY_RESOLVER, false, false);
  }

  @Nullable
  protected static PsiElement resolveInner(TypeRef ref) {
    PsiElement element = ref.getElement();
    String typeText = ref.getRangeInElement().substring(element.getText());

    PsiFile javaFile = createJavaFileForExpr("return " + typeText, element);
    int position = javaFile.getText().lastIndexOf(typeText);
    PsiReference javaRef = javaFile.findReferenceAt(position);
    return javaRef == null ? null : javaRef.resolve();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    PsiElement e = getElement();
    String text = StringUtil.replaceSubstring(e.getText(), getRangeInElement(), newElementName);
    return e.replace(JFlexPsiElementFactory.createJavaCodeFromText(e.getProject(), text));
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return EMPTY_ARRAY;
  }
}
