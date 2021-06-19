/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.refactor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.actions.BasePlatformRefactoringAction;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/16/11
 * Time: 5:14 PM
 *
 * @author Vadim Romansky
 */
public class BnfIntroduceRuleAction extends BasePlatformRefactoringAction {
  public BnfIntroduceRuleAction() {
    setInjectedContext(true);
  }

  protected boolean isAvailableInEditorOnly() {
    return true;
  }

  @Override
  protected boolean isAvailableForFile(PsiFile file) {
    return file instanceof BnfFile;
  }

  protected boolean isEnabledOnElements(PsiElement @NotNull [] elements) {
    return false;
  }

  @Override
  protected RefactoringActionHandler getRefactoringHandler(@NotNull RefactoringSupportProvider provider) {
    return new BnfIntroduceRuleHandler();
  }
}
