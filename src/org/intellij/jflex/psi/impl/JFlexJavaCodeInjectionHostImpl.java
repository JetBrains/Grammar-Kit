/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.tree.IElementType;
import org.intellij.jflex.psi.JFlexPsiElementFactory;
import org.jetbrains.annotations.NotNull;


public class JFlexJavaCodeInjectionHostImpl extends JFlexCompositeImpl implements PsiLanguageInjectionHost {

  public JFlexJavaCodeInjectionHostImpl(IElementType type) {
    super(type);
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public JFlexJavaCodeInjectionHostImpl updateText(@NotNull String text) {
    PsiElement newElement = JFlexPsiElementFactory.createJavaCodeFromText(getProject(), text);
    return (JFlexJavaCodeInjectionHostImpl)this.replace(newElement);
  }

  @Override
  public @NotNull LiteralTextEscaper<JFlexJavaCodeInjectionHostImpl> createLiteralTextEscaper() {
    return new LiteralTextEscaper<>(this) {
      @Override
      public boolean decode(@NotNull TextRange textrange, @NotNull StringBuilder stringbuilder) {
        stringbuilder.append(myHost.getText(), textrange.getStartOffset(), textrange.getEndOffset());
        return true;
      }

      @Override
      public int getOffsetInHost(int i, @NotNull TextRange textrange) {
        int j = i + textrange.getStartOffset();
        if (j < textrange.getStartOffset()) {
          j = textrange.getStartOffset();
        }
        if (j > textrange.getEndOffset()) {
          j = textrange.getEndOffset();
        }
        return j;
      }

      @Override
      public boolean isOneLine() {
        return false;
      }
    };
  }
}
