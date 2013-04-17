/*
 * Copyright 2011-2013 Gregory Shrago
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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.intellij.grammar.psi.BnfReferenceOrToken;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 14.07.11
 * Time: 19:17
 */
public abstract class BnfRefOrTokenImpl extends BnfExpressionImpl implements BnfReferenceOrToken {
  public BnfRefOrTokenImpl(ASTNode node) {
    super(node);
  }

  @Override
  public PsiReference getReference() {
    return new BnfReferenceImpl<BnfReferenceOrToken>(this, TextRange.from(0, getTextLength())) {
      @Override
      public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        myElement.getId().replace(BnfElementFactory.createLeafFromText(getElement().getProject(), newElementName));
        return myElement;
      }
    };
  }

  @Override
  public String toString() {
    return super.toString() + ": " + getText();
  }
}
