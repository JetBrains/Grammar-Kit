/*
 * Copyright 2000-2011 Gregory Shrago
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
import org.intellij.grammar.psi.BnfStringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import static org.intellij.grammar.psi.BnfTypes.BNF_STRING;

public class BnfStringLiteralExpressionImpl extends BnfStringImpl implements BnfStringLiteralExpression {

  public BnfStringLiteralExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public PsiElement getString() {
    ASTNode child = getNode().findChildByType(BNF_STRING);
    return child == null? null : child.getPsi();
  }

}
