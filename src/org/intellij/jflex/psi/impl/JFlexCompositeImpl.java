/*
 * Copyright 2011-present Greg Shrago
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

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + getElementType().toString() + ")";
  }
}
