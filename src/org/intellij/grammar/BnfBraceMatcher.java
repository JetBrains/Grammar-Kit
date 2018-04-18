/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
