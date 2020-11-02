/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.editor;

import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class BnfSpellCheckingStrategy extends SpellcheckingStrategy {
  @NotNull
  @Override
  public Tokenizer getTokenizer(PsiElement element) {
    return super.getTokenizer(element);
  }
}
