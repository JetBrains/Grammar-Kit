/*
 * Copyright 2011-present JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.grammar.psi.BnfTypes.*;
import org.intellij.grammar.psi.*;
import com.intellij.psi.tree.IElementType;

public class BnfValueListImpl extends BnfExpressionImpl implements BnfValueList {

  public BnfValueListImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitValueList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BnfListEntry> getListEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfListEntry.class);
  }

}
