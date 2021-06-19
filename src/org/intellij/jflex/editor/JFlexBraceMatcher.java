/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.jflex.psi.JFlexTypes.*;

/**
 * @author gregsh
 */
public class JFlexBraceMatcher implements PairedBraceMatcher {

  private static final BracePair[] PAIRS = new BracePair[]{
    new BracePair(FLEX_BRACE1, FLEX_BRACE2, true),
    new BracePair(FLEX_PAREN1, FLEX_PAREN2, false),
    new BracePair(FLEX_BRACK1, FLEX_BRACK2, false),
    new BracePair(FLEX_ANGLE1, FLEX_ANGLE2, false),
    new BracePair(FLEX_OPT_CODE1, FLEX_OPT_CODE2, false),
  };

  @Override
  public BracePair @NotNull [] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
    return true;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
