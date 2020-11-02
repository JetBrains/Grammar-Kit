/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.psi.BnfTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class BnfBraceMatcher implements PairedBraceMatcher {

  private static final BracePair[] PAIRS = new BracePair[]{
    new BracePair(BnfTypes.BNF_LEFT_PAREN, BnfTypes.BNF_RIGHT_PAREN, false),
    new BracePair(BnfTypes.BNF_LEFT_BRACE, BnfTypes.BNF_RIGHT_BRACE, false),
    new BracePair(BnfTypes.BNF_LEFT_BRACKET, BnfTypes.BNF_RIGHT_BRACKET, false),
    new BracePair(BnfTypes.BNF_EXTERNAL_START, BnfTypes.BNF_EXTERNAL_END, false)
  };

  @NotNull
  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType t) {
    return t == BnfTypes.BNF_SEMICOLON ||
           BnfParserDefinition.WS.contains(t) ||
           BnfParserDefinition.OPERATORS.contains(t) && t != BnfTypes.BNF_OP_NOT && t != BnfTypes.BNF_OP_AND ||
           BnfParserDefinition.PARENS_R.contains(t) ||
           BnfParserDefinition.COMMENTS.contains(t);
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
