/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 19:11
 */
public class BnfCompositeImpl extends CompositePsiElement implements BnfComposite {
  public BnfCompositeImpl(IElementType type) {
    super(type);
  }

  @Override
  public <R> R accept(@NotNull BnfVisitor<R> visitor) {
    return visitor.visitComposite(this);
  }

  /** @noinspection InstanceofThis*/
  @Override
  public String toString() {
    String elementType = getNode().getElementType().toString();
    boolean addText = this instanceof BnfExpression && !(this instanceof BnfValueList);
    if (addText) {
      String text = getText();
      if (!(this instanceof BnfLiteralExpression) && text.length() > 50) {
        text = text.substring(0, 30) + " ... " + text.substring(text.length() - 20);
      }
      return elementType + (StringUtil.isEmptyOrSpaces(text)? "" : ": " + text);
    }
    else {
      return elementType;
    }
  }
}
