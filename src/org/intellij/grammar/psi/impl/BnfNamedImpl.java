/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.RowIcon;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import org.intellij.grammar.BnfIcons;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfModifier;
import org.intellij.grammar.psi.BnfNamedElement;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static org.intellij.grammar.psi.BnfTypes.BNF_ID;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 14.07.11
 * Time: 20:04
 */
public abstract class BnfNamedImpl extends BnfCompositeImpl implements BnfNamedElement {
  
  private volatile String myCachedName;
  
  public BnfNamedImpl(IElementType elementType) {
    super(elementType);
  }

  @Override
  public void subtreeChanged() {
    super.subtreeChanged();
    myCachedName = null;
  }

  @NotNull
  @Override
  public String getName() {
    if (myCachedName == null) {
      myCachedName = GrammarUtil.getIdText(getId());
    }
    return myCachedName;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException {
    getId().replace(BnfElementFactory.createLeafFromText(getProject(), s));
    return this;
  }

  @Override
  public int getTextOffset() {
    return getId().getTextOffset();
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
    return new LocalSearchScope(getContainingFile());
  }

  @Override
  public Icon getIcon(int flags) {
    if (this instanceof BnfRule) {
      final Icon base = hasModifier((BnfRule)this, "external") ? BnfIcons.EXTERNAL_RULE : BnfIcons.RULE;
      final Icon visibility = hasModifier((BnfRule)this, "private") ? PlatformIcons.PRIVATE_ICON : PlatformIcons.PUBLIC_ICON;
      final RowIcon row = new RowIcon(2);
      row.setIcon(base, 0);
      row.setIcon(visibility, 1);
      return row;
    }
    else if (this instanceof BnfAttr) {
      return BnfIcons.ATTRIBUTE;
    }
    return super.getIcon(flags);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return getId();
  }

  public static boolean hasModifier(BnfRule rule, String modifier) {
    for (BnfModifier o : rule.getModifierList()) {
      if (modifier.equals(o.getText())) return true;
    }
    return false;
  }

  @Override
  public String toString() {
    // AE fix in LOG.toString in inconsistent state
    PsiElement nullableId = findPsiChildByType(BNF_ID);
    return super.toString() + ":" + (nullableId == null? null : nullableId.getText());
  }
}
