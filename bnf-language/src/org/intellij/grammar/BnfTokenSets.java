/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.grammar.psi.BnfTypes;

public final class BnfTokenSets {
  public static final TokenSet WS = TokenSet.WHITE_SPACE;

  public static final IElementType BNF_LINE_COMMENT = BnfTypes.BNF_LINE_COMMENT;
  public static final IElementType BNF_BLOCK_COMMENT = BnfTypes.BNF_BLOCK_COMMENT;

  public static final TokenSet COMMENTS = TokenSet.create(BNF_LINE_COMMENT, BNF_BLOCK_COMMENT);
  public static final TokenSet LITERALS = TokenSet.create(BnfTypes.BNF_STRING);

  public static final TokenSet PARENS_L = TokenSet.create(
    BnfTypes.BNF_LEFT_PAREN, BnfTypes.BNF_LEFT_BRACE, BnfTypes.BNF_LEFT_BRACKET, BnfTypes.BNF_EXTERNAL_START);
  public static final TokenSet PARENS_R = TokenSet.create(
    BnfTypes.BNF_RIGHT_PAREN, BnfTypes.BNF_RIGHT_BRACE, BnfTypes.BNF_RIGHT_BRACKET, BnfTypes.BNF_EXTERNAL_END);

  public static final TokenSet OPERATORS = TokenSet.create(
    BnfTypes.BNF_OP_AND, BnfTypes.BNF_OP_EQ, BnfTypes.BNF_OP_NOT, BnfTypes.BNF_OP_ONEMORE, BnfTypes.BNF_OP_OPT,
    BnfTypes.BNF_OP_OR, BnfTypes.BNF_OP_ZEROMORE);
}
