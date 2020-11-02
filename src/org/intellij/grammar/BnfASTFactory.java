/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.psi.BnfTypes;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class BnfASTFactory extends ASTFactory {

  @Nullable
  @Override
  public CompositeElement createComposite(IElementType type) {
    return BnfTypes.Factory.createElement(type);
  }
}
