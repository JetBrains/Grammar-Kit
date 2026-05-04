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

public class BnfRuleImpl extends BnfNamedImpl implements BnfRule {

  public BnfRuleImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitRule(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BnfVisitor) accept((BnfVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BnfAttrs getAttrs() {
    return PsiTreeUtil.getChildOfType(this, BnfAttrs.class);
  }

  @Override
  @NotNull
  public BnfExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfExpression.class);
  }

  @Override
  @NotNull
  public List<BnfModifier> getModifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BnfModifier.class);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(BNF_ID);
  }

}
