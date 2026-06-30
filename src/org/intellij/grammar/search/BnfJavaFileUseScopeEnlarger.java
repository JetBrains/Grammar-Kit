/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.search;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

/**
 * Enlarges the use scope of private Java declarations referenced from {@code .bnf} grammars.
 * Registered only when the Java plugin is present (see {@code plugin-java.xml}).
 */
public final class BnfJavaFileUseScopeEnlarger extends BnfFileUseScopeEnlarger {
  @Override
  protected @NotNull FileType getApplicableFileType() {
    return JavaFileType.INSTANCE;
  }

  @Override
  protected boolean isPrivateDeclaration(@NotNull PsiElement element) {
    return element instanceof PsiModifierListOwner owner && owner.hasModifierProperty(PsiModifier.PRIVATE);
  }
}
