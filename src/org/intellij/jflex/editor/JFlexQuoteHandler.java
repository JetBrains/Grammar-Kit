/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import org.intellij.jflex.psi.JFlexTypes;

/**
 * @author gregsh
 */
final class JFlexQuoteHandler extends SimpleTokenSetQuoteHandler {
  JFlexQuoteHandler() {
    super(JFlexTypes.FLEX_STRING, TokenType.BAD_CHARACTER);
  }
}
