package org.intellij.grammar.refactor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.actions.BasePlatformRefactoringAction;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author greg
 */
public class BnfIntroduceTokenAction extends BasePlatformRefactoringAction {
  public BnfIntroduceTokenAction() {
  }

  protected boolean isAvailableInEditorOnly() {
    return true;
  }

  @Override
  protected boolean isAvailableForFile(PsiFile file) {
    return file instanceof BnfFile;
  }

  protected boolean isEnabledOnElements(@NotNull PsiElement[] elements) {
    return false;
  }

  @Override
  protected RefactoringActionHandler getRefactoringHandler(@NotNull RefactoringSupportProvider provider) {
    return new BnfIntroduceTokenHandler();
  }
}

