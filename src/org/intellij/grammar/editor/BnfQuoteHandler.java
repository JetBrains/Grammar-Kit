/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import org.intellij.grammar.psi.BnfTypes;

/**
 * @author gregsh
 */
public class BnfQuoteHandler extends SimpleTokenSetQuoteHandler {
  public BnfQuoteHandler() {
    super(BnfTypes.BNF_STRING, TokenType.BAD_CHARACTER);
  }
}
