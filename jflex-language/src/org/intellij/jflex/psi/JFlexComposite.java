/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public interface JFlexComposite extends PsiElement {
  default <R> R accept(@NotNull JFlexVisitor<R> visitor) {
    return visitor.visitComposite(this);
  }
}
