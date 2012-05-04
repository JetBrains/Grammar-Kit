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
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfStringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    PsiElement parent = getParent();
    if (!(parent instanceof BnfAttr)) return null;
    KnownAttribute attribute = KnownAttribute.getAttribute(((BnfAttr)parent).getName());
    if (attribute == null) return null;
    boolean addJavaRefs = attribute.getName().endsWith("Class") || attribute.getName().endsWith("Package") ||
                       (attribute == KnownAttribute.EXTENDS || attribute == KnownAttribute.IMPLEMENTS);
    boolean addBnfRef = attribute == KnownAttribute.EXTENDS || attribute == KnownAttribute.IMPLEMENTS || attribute == KnownAttribute.RECOVER_UNTIL;

    BnfReferenceImpl<BnfStringLiteralExpression> bnfReference = null;
    if (addBnfRef) {
      TextRange range = TextRange.from(1, getTextLength() - 2);
      bnfReference = new BnfReferenceImpl<BnfStringLiteralExpression>(this, range) {
        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
          PsiElement string = getString();
          char quote = string.getText().charAt(0);
          return string.replace(BnfElementFactory.createLeafFromText(getProject(), quote + newElementName + quote));
        }
      };
    }
    if (addJavaRefs) {
      PsiReferenceProvider provider = JavaHelper.getJavaHelper(getProject()).getClassReferenceProvider();
      PsiReference[] javaRefs = provider == null ? PsiReference.EMPTY_ARRAY : provider.getReferencesByElement(this, new ProcessingContext());
      return new MyMultiReference(addBnfRef? ArrayUtil.mergeArrays(new PsiReference[]{bnfReference}, javaRefs, PsiReference.ARRAY_FACTORY) : javaRefs, this);
    }
    else if (addBnfRef) {
      return bnfReference;
    }
    return null;
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
    return super.toString() + ": " + getText();
  }

  private static class MyMultiReference extends PsiMultiReference {
    MyMultiReference(PsiReference[] psiReferences, BnfStringLiteralExpression element) {
      super(psiReferences, element);
    }

    @Override
    public TextRange getRangeInElement() {
      PsiReference[] references = getReferences();
      TextRange result = references[0].getRangeInElement();
      for (PsiReference reference : references) {
        result.union(reference.getRangeInElement());
      }
      return result;
    }
  }
}
