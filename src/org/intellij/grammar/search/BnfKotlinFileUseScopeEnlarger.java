/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.search;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.KtModifierListOwner;

/**
 * Enlarges the use scope of private Kotlin declarations referenced from {@code .bnf} grammars.
 * Registered only when the Kotlin plugin is present (see {@code plugin-kotlin.xml}).
 */
public final class BnfKotlinFileUseScopeEnlarger extends BnfFileUseScopeEnlarger {
  @Override
  protected @NotNull FileType getApplicableFileType() {
    return KotlinFileType.INSTANCE;
  }

  @Override
  protected boolean isPrivateDeclaration(@NotNull PsiElement element) {
    return element instanceof KtModifierListOwner owner && owner.hasModifier(KtTokens.PRIVATE_KEYWORD);
  }
}
