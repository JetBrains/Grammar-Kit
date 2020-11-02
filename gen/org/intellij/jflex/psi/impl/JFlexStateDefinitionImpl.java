/*
 * Copyright 2011-present JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.jflex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;

public class JFlexStateDefinitionImpl extends JFlexCompositeImpl implements JFlexStateDefinition {

  public JFlexStateDefinitionImpl(IElementType type) {
    super(type);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitStateDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findPsiChildByType(FLEX_ID);
  }

  @Override
  @NotNull
  public String getName() {
    return JFlexPsiImplUtil.getName(this);
  }

  @Override
  @NotNull
  public PsiNameIdentifierOwner setName(String newName) {
    return JFlexPsiImplUtil.setName(this, newName);
  }

  @Override
  @NotNull
  public PsiElement getNameIdentifier() {
    return JFlexPsiImplUtil.getNameIdentifier(this);
  }

}
