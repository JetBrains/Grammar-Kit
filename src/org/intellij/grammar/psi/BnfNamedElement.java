/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

/**
 * User: gregory
 * Date: 13.07.11
 * Time: 19:02
 */
public interface BnfNamedElement extends BnfComposite, PsiNameIdentifierOwner {
  @NotNull
  String getName();

  @NotNull
  PsiElement getId();
}
