/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.livePreview.LivePreviewHelper;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class LivePreviewAction extends DumbAwareAction {
  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  @Override
  public void update(AnActionEvent e) {
    PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
    e.getPresentation().setEnabledAndVisible(psiFile instanceof BnfFile);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
    if (!(psiFile instanceof BnfFile)) return;

    LivePreviewHelper.getInstance().showFor((BnfFile)psiFile);
  }
}
