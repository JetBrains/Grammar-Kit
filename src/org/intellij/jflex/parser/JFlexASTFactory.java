/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.jflex.psi.JFlexTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class JFlexASTFactory extends ASTFactory {

  @Nullable
  @Override
  public CompositeElement createComposite(@NotNull IElementType type) {
    return JFlexTypes.Factory.createElement(type);
  }
}
