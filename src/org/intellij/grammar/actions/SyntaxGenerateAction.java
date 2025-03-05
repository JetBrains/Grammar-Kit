/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.syntax.SyntaxBnfAttributePostProcessor;
import org.jetbrains.annotations.Nullable;

public class SyntaxGenerateAction extends GenerateAction{

  @Override
  protected @Nullable PsiFile getBnfFile(VirtualFile file, PsiManager psiManager) {
    var newFile = psiManager.findFile(file);
    assert newFile != null;
    SyntaxBnfAttributePostProcessor.prepareForGeneration((BnfFile)newFile);
    return newFile;
  }
}
