/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.fleet;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.intellij.grammar.actions.GenerateJavaAction;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.Nullable;

public class GenerateFleetAction extends GenerateJavaAction {

  @Override
  protected @Nullable PsiFile getBnfFile(VirtualFile file, PsiManager psiManager) {
    var psiFile = super.getBnfFile(file, psiManager);
    if (psiFile == null) return null;
    return FleetBnfFileWrapper.wrapBnfFile((BnfFile)psiFile);
  }
}
