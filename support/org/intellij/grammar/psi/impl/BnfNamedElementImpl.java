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
import com.intellij.psi.PsiElement;
import com.intellij.ui.RowIcon;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import org.intellij.grammar.BnfIcons;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 14.07.11
 * Time: 20:04
 */
public abstract class BnfNamedElementImpl extends BnfCompositeElementImpl implements BnfNamedElement {
  public BnfNamedElementImpl(ASTNode node) {
    super(node);
  }

  @Override
  public String getName() {
    return getId().getText();
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

  @Override
  public Icon getIcon(int flags) {
    if (this instanceof BnfRule) {
      final Icon base = hasModifier((BnfRule)this, "external") ? BnfIcons.RULE : BnfIcons.EXTERNAL_RULE;
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

  @NotNull
  public PsiElement getId() {
    ASTNode child = getNode().findChildByType(BnfTypes.BNF_ID);
    return child == null ? null : child.getPsi();
  }

  public static boolean hasModifier(BnfRule rule, String modifier) {
    for (BnfModifier o : rule.getModifierList()) {
      if (modifier.equals(o.getText())) return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return super.toString() + ":" + getName();
  }
}
