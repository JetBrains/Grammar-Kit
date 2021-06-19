/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.livePreview.GrammarAtCaretPassFactory;
import org.intellij.grammar.livePreview.LivePreviewLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author gregsh
 */
public class HighlightGrammarAtCaretAction extends AnAction {

  private static @Nullable Editor getPreviewEditor(@NotNull AnActionEvent e) {
    Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    Language language = psiFile == null ? null : psiFile.getLanguage();
    LivePreviewLanguage livePreviewLanguage = language instanceof LivePreviewLanguage ? (LivePreviewLanguage)language : null;
    if (livePreviewLanguage == null) return null;
    List<Editor> editors = livePreviewLanguage.getGrammarEditors(psiFile.getProject());
    return editors.isEmpty() ? null : editor;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    Editor editor = getPreviewEditor(e);
    boolean enabled = editor != null;
    String command = !enabled ? "" : GrammarAtCaretPassFactory.GRAMMAR_AT_CARET_KEY.get(editor) != null ? "Stop " : "Start ";
    e.getPresentation().setText(command + getTemplatePresentation().getText());
    e.getPresentation().setEnabledAndVisible(enabled);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Editor editor = getPreviewEditor(e);
    if (editor == null) return;
    Boolean value = GrammarAtCaretPassFactory.GRAMMAR_AT_CARET_KEY.get(editor);
    GrammarAtCaretPassFactory.GRAMMAR_AT_CARET_KEY.set(editor, value == null ? Boolean.TRUE : null);

    Project project = Objects.requireNonNull(e.getProject());
    DaemonCodeAnalyzer.getInstance(project).restart();
  }
}
