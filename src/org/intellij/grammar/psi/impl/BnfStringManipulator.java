/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfStringManipulator extends AbstractElementManipulator<BnfStringImpl> {
  @Override
  public BnfStringImpl handleContentChange(@NotNull BnfStringImpl psi, @NotNull TextRange range, String newContent) {
    String oldText = psi.getText();
    String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
    return psi.updateText(newText);
  }

  @Override
  public @NotNull TextRange getRangeInElement(@NotNull BnfStringImpl element) {
    return getStringTokenRange(element);
  }

  public static TextRange getStringTokenRange(BnfStringImpl element) {
    return TextRange.from(1, element.getTextLength()-2);
  }
}
