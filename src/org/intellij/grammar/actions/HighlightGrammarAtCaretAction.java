/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.intellij.util.ObjectUtils;
import org.intellij.grammar.livePreview.GrammarAtCaretPassFactory;
import org.intellij.grammar.livePreview.LivePreviewLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author gregsh
 */
public class HighlightGrammarAtCaretAction extends AnAction {

  @Nullable
  private static Editor getPreviewEditor(@NotNull AnActionEvent e) {
    Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    Language language = psiFile == null ? null : psiFile.getLanguage();
    LivePreviewLanguage livePreviewLanguage = language instanceof LivePreviewLanguage ? (LivePreviewLanguage)language : null;
    if (livePreviewLanguage == null) return null;
    List<Editor> editors = livePreviewLanguage.getGrammarEditors(psiFile.getProject());
    return editors.isEmpty() ? null : editor;
  }

  @Override
  public void update(AnActionEvent e) {
    Editor editor = getPreviewEditor(e);
    boolean enabled = editor != null;
    String command = !enabled ? "" : GrammarAtCaretPassFactory.GRAMMAR_AT_CARET_KEY.get(editor) != null ? "Stop " : "Start ";
    e.getPresentation().setText(command + getTemplatePresentation().getText());
    e.getPresentation().setEnabledAndVisible(enabled);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    Editor editor = getPreviewEditor(e);
    if (editor == null) return;
    Boolean value = GrammarAtCaretPassFactory.GRAMMAR_AT_CARET_KEY.get(editor);
    GrammarAtCaretPassFactory.GRAMMAR_AT_CARET_KEY.set(editor, value == null ? Boolean.TRUE : null);

    Project project = ObjectUtils.assertNotNull(e.getProject());
    DaemonCodeAnalyzer.getInstance(project).restart();
  }
}
