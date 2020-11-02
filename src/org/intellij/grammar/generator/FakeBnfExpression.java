/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfTypes;
import org.intellij.grammar.psi.BnfVisitor;
import org.jetbrains.annotations.NotNull;

/**
* @author gregsh
*/
public class FakeBnfExpression extends LeafPsiElement implements BnfExpression {
  public FakeBnfExpression(@NotNull String text) {
    this(BnfTypes.BNF_EXPRESSION, text);
  }

  public FakeBnfExpression(@NotNull IElementType elementType, @NotNull String text) {
    super(elementType, text);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return null;
  }

  @Override
  public String toString() {
    return getText();
  }
}
