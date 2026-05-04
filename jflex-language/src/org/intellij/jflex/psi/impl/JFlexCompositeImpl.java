/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi.impl;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.PlatformIcons;
import org.intellij.jflex.psi.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author gregsh
 */
public class JFlexCompositeImpl extends CompositePsiElement implements JFlexComposite {

  public JFlexCompositeImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitComposite(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor<?>)visitor);
    else super.accept(visitor);
  }

  @Override
  public @NotNull SearchScope getUseScope() {
    return new LocalSearchScope(getContainingFile());
  }

  @Override
  public Icon getIcon(int flags) {
    if (this instanceof JFlexRule) {
      JFlexExpression e = ((JFlexRule)this).getExpression();
      return e == null ? PlatformIcons.PACKAGE_ICON : PlatformIcons.METHOD_ICON;
    }
    else if (this instanceof JFlexOption) {
      return PlatformIcons.PROPERTY_ICON;
    }
    else if (this instanceof JFlexMacroDefinition) {
      return PlatformIcons.FIELD_ICON;
    }
    return super.getIcon(flags);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + getElementType() + ")";
  }
}
