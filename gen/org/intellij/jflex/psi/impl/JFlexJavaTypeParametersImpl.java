/*
 * Copyright 2011-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import com.intellij.psi.tree.IElementType;

public class JFlexJavaTypeParametersImpl extends JFlexCompositeImpl implements JFlexJavaTypeParameters {

  public JFlexJavaTypeParametersImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitJavaTypeParameters(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JFlexJavaType> getJavaTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JFlexJavaType.class);
  }

}
