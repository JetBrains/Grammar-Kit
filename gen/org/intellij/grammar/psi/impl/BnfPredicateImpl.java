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
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.psi.BnfChoice;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfPredicate;
import org.intellij.grammar.psi.BnfPredicateSign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BnfPredicateImpl extends BnfExpressionImpl implements BnfPredicate {

  public BnfPredicateImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public BnfChoice getChoice() {
    return PsiTreeUtil.getChildOfType(this, BnfChoice.class);
  }

  @Override
  @Nullable
  public BnfExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, BnfExpression.class);
  }

  @Override
  @NotNull
  public BnfPredicateSign getPredicateSign() {
    return PsiTreeUtil.getChildOfType(this, BnfPredicateSign.class);
  }

}
