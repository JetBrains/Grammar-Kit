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
package org.intellij.grammar.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 19:11
 */
public class BnfCompositeElementImpl extends ASTWrapperPsiElement implements BnfCompositeElement {
  public BnfCompositeElementImpl(ASTNode node) {
    super(node);
  }

  /** @noinspection InstanceofThis*/
  @Override
  public String toString() {
    String elementType = getNode().getElementType().toString();
    boolean addText = this instanceof BnfExpression && !(this instanceof BnfValueList);
    if (addText) {
      String text = getText();
      if (!(this instanceof BnfLiteralExpression) && text.length() > 50) {
        text = text.substring(0, 30) + " ... " + text.substring(text.length() - 20, text.length());
      }
      return elementType + (StringUtil.isEmptyOrSpaces(text)? "" : ": " + text);
    }
    else {
      return elementType;
    }
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitCompositeElement(this);
  }
}
