/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfReferenceOrToken;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 14.07.11
 * Time: 19:17
 */
public abstract class BnfRefOrTokenImpl extends BnfExpressionImpl implements BnfReferenceOrToken {
  public BnfRefOrTokenImpl(IElementType elementType) {
    super(elementType);
  }

  @Override
  public @Nullable BnfRule resolveRule() {
    PsiFile file = getContainingFile();
    return file instanceof BnfFile ? ((BnfFile)file).getRule(GrammarUtil.getIdText(getId())) : null;
  }

  @Override
  public PsiReference getReference() {
    int delta = GrammarUtil.isIdQuoted(getId().getText()) ? 1 : 0;
    TextRange range = TextRange.create(delta, getTextLength() - delta);
    return new BnfReferenceImpl<BnfReferenceOrToken>(this, range) {
      @Override
      public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        myElement.getId().replace(BnfElementFactory.createLeafFromText(getElement().getProject(), newElementName));
        return myElement;
      }
    };
  }
}
