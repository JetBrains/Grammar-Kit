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

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.intellij.grammar.psi.BnfAttrValue;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfStringLiteralExpression;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public abstract class BnfStringImpl extends BnfExpressionImpl implements BnfStringLiteralExpression, PsiLanguageInjectionHost {
  public BnfStringImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiElement getNumber() {
    return null;
  }

  @Override
  public PsiReference getReference() {
    if (!(getParent() instanceof BnfAttrValue)) return null;
    return new BnfReferenceImpl<BnfStringLiteralExpression>(this, TextRange.from(1, getTextLength() - 2)) {
      @Override
      public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return getString().replace(BnfElementFactory.createLeafFromText(getProject(), '\"' + newElementName + '\"'));
      }
    };
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public BnfStringImpl updateText(@NotNull final String text) {
    final BnfExpression expression = BnfElementFactory.createExpressionFromText(getProject(), text);
    assert expression instanceof BnfStringImpl : text + "-->" + expression;
    return (BnfStringImpl)this.replace(expression);
  }

  @NotNull
  @Override
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new BnfStringLiteralEscaper(this);
  }

  @Override
  public String toString() {
    return getText();
  }
}
