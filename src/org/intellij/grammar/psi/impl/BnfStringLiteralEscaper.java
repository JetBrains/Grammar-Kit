/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.psi.impl;

import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfStringLiteralEscaper extends LiteralTextEscaper<BnfStringImpl> {
  public BnfStringLiteralEscaper(BnfStringImpl element) {
    super(element);
  }

  @Override
  public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
    // todo implement proper java-like string escapes support
    ProperTextRange.assertProperRange(rangeInsideHost);
    outChars.append(myHost.getText(), rangeInsideHost.getStartOffset(), rangeInsideHost.getEndOffset());
    return true;
  }

  @Override
  public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
    ProperTextRange.assertProperRange(rangeInsideHost);
    int offset = offsetInDecoded;
    // todo implement proper java-like string escapes support
    offset += rangeInsideHost.getStartOffset();
    if (offset < rangeInsideHost.getStartOffset()) offset = rangeInsideHost.getStartOffset();
    if (offset > rangeInsideHost.getEndOffset()) offset = rangeInsideHost.getEndOffset();
    return offset;
  }

  @Override
  public boolean isOneLine() {
    return true;
  }
}

