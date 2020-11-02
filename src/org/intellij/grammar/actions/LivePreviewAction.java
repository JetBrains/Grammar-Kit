/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.livePreview.LivePreviewHelper;
import org.intellij.grammar.psi.BnfFile;

/**
 * @author gregsh
 */
public class LivePreviewAction extends DumbAwareAction {
  @Override
  public void update(AnActionEvent e) {
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    e.getPresentation().setEnabledAndVisible(psiFile instanceof BnfFile);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    if (!(psiFile instanceof BnfFile)) return;

    LivePreviewHelper.showFor((BnfFile)psiFile);
  }
}
