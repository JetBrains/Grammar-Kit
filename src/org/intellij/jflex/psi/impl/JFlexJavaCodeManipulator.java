/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class JFlexJavaCodeManipulator extends AbstractElementManipulator<JFlexJavaCodeInjectionHostImpl> {
  @Override
  public @Nullable JFlexJavaCodeInjectionHostImpl handleContentChange(@NotNull JFlexJavaCodeInjectionHostImpl psi,
                                                                      @NotNull TextRange range,
                                                                      String newContent) throws IncorrectOperationException {
    String oldText = psi.getText();
    String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
    return psi.updateText(newText);
  }
}
