/*
 * Copyright 2011-2016 Gregory Shrago
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

package org.intellij.jflex.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.PlatformIcons;
import org.intellij.jflex.psi.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author gregsh
 */
public class JFlexCompositeElementImpl extends ASTWrapperPsiElement implements JFlexCompositeElement {
  public JFlexCompositeElementImpl(@NotNull ASTNode astNode) {
    super(astNode);
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
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
}
