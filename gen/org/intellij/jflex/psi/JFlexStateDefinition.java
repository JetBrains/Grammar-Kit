/*
 * Copyright 2011-present JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.jflex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface JFlexStateDefinition extends JFlexNamedElement {

  @NotNull
  PsiElement getId();

  @NotNull
  String getName();

  @NotNull
  PsiNameIdentifierOwner setName(String newName);

  @NotNull
  PsiElement getNameIdentifier();

}
