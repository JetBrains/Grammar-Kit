/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

import static org.intellij.jflex.psi.JFlexTypes.*;

public final class JFlexTokenSets {
  public static final IElementType FLEX_NEWLINE = new JFlexTokenType("newline");

  public static final TokenSet WS = TokenSet.create(TokenType.WHITE_SPACE, FLEX_NEWLINE);

  public static final TokenSet COMMENTS = TokenSet.create(FLEX_LINE_COMMENT, FLEX_BLOCK_COMMENT);
  public static final TokenSet LITERALS = TokenSet.create(FLEX_STRING);

  public static final TokenSet CHAR_CLASS_OPERATORS =
    TokenSet.create(FLEX_AMPAMP, FLEX_BARBAR, FLEX_DASHDASH, FLEX_HAT, FLEX_TILDETILDE);
  public static final TokenSet PATTERN_OPERATORS =
    TokenSet.create(FLEX_BAR, FLEX_BANG, FLEX_DOLLAR, FLEX_PLUS, FLEX_QUESTION, FLEX_STAR, FLEX_TILDE);
}
