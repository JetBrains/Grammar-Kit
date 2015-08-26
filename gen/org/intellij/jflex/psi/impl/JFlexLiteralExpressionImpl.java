/*
 * Copyright 2011-2014 Gregory Shrago
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

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.jflex.psi.JFlexTypes.*;
import org.intellij.jflex.psi.*;

public class JFlexLiteralExpressionImpl extends JFlexExpressionImpl implements JFlexLiteralExpression {

  public JFlexLiteralExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull JFlexVisitor visitor) {
    visitor.visitLiteralExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JFlexVisitor) accept((JFlexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getChar() {
    return findChildByType(FLEX_CHAR);
  }

  @Override
  @Nullable
  public PsiElement getEscapedChar() {
    return findChildByType(FLEX_ESCAPED_CHAR);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(FLEX_ID);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(FLEX_NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(FLEX_STRING);
  }

}
