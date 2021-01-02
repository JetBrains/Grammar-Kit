/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.refactor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.intellij.grammar.psi.BnfNamedElement;
import org.intellij.grammar.psi.impl.BnfReferenceImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/11/11
 * Time: 7:34 PM
 *
 * @author Vadim Romansky
 */
public class BnfRefactoringSupportProvider extends RefactoringSupportProvider {
  @Override
  public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
    return element instanceof BnfNamedElement || element instanceof BnfReferenceImpl.MetaParameter;
  }
}
