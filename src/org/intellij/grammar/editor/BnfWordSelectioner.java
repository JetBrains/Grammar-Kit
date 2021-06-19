/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.editor;

import com.intellij.codeInsight.editorActions.BraceMatcherBasedSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.intellij.grammar.psi.BnfComposite;
import org.intellij.grammar.psi.BnfTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author gregsh
 */
public class BnfWordSelectioner extends BraceMatcherBasedSelectioner{
  @Override
  public boolean canSelect(@NotNull PsiElement e) {
    return e instanceof BnfComposite ||
           e instanceof LeafPsiElement && ((LeafPsiElement)e).getElementType() == BnfTypes.BNF_STRING;
  }

  @Override
  public List<TextRange> select(@NotNull PsiElement e, @NotNull CharSequence editorText, int cursorOffset, @NotNull Editor editor) {
    List<TextRange> list = super.select(e, editorText, cursorOffset, editor);
    if (list == null) return null;
    if (e instanceof LeafPsiElement && ((LeafPsiElement)e).getElementType() == BnfTypes.BNF_STRING) {
      TextRange range = e.getTextRange();
      list.add(TextRange.from(range.getStartOffset() + 1, range.getLength() - 2));
    }
    return list;
  }

}
