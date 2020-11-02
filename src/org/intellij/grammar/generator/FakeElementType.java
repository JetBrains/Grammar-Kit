/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;

/**
 * @author gregsh
 */
public class FakeElementType extends IElementType {
  public FakeElementType(String debugName, Language language) {
    super(debugName, language, false);
  }

  public static boolean isFakeElement(@Nullable PsiElement e) {
    return PsiUtilCore.getElementType(e) instanceof FakeElementType;
  }
}
