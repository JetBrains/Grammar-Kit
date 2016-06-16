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
