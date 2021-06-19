/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.intellij.jflex.psi.JFlexComposite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JFlexRefactoringSupportProvider extends RefactoringSupportProvider {
  @Override
  public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement psiElement, @Nullable PsiElement context) {
    return psiElement instanceof JFlexComposite && psiElement instanceof PsiNamedElement;
  }
}
