/*
 * Copyright 2011-2013 Gregory Shrago
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

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInsight.highlighting.HighlightManagerImpl;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.PairProcessor;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.update.Update;
import org.intellij.grammar.livePreview.LivePreviewHelper;
import org.intellij.grammar.livePreview.LivePreviewLanguage;
import org.intellij.grammar.psi.BnfExpression;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gregsh
 */
public class HighlightGrammarAtCaretAction extends AnAction {

  private static final Key<CaretListener> GRAMMAR_UPDATE_RUNNING = Key.create("GRAMMAR_UPDATE_RUNNING");

  @Override
  public void update(AnActionEvent e) {
    Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    Language language = psiFile == null ? null : psiFile.getLanguage();
    LivePreviewLanguage livePreviewLanguage = language instanceof LivePreviewLanguage? (LivePreviewLanguage)language : null;
    Editor grammarEditor = livePreviewLanguage == null? null : getGrammarEditor(psiFile.getProject(), livePreviewLanguage);

    boolean enabled = editor != null && grammarEditor != null;
    if (enabled) {
      boolean running = editor.getUserData(GRAMMAR_UPDATE_RUNNING) != null;
      e.getPresentation().setText((running? "Stop " : "Start ") + getTemplatePresentation().getText());
    }
    e.getPresentation().setEnabledAndVisible(enabled);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    Language language = psiFile == null ? null : psiFile.getLanguage();
    final LivePreviewLanguage livePreviewLanguage = language instanceof LivePreviewLanguage ? (LivePreviewLanguage)language : null;
    final Editor grammarEditor = livePreviewLanguage == null ? null : getGrammarEditor(psiFile.getProject(), livePreviewLanguage);

    if (editor == null || grammarEditor == null) return;

    final Project project = psiFile.getProject();
    CaretListener caretListener = editor.getUserData(GRAMMAR_UPDATE_RUNNING);
    if (caretListener != null) {
      editor.putUserData(GRAMMAR_UPDATE_RUNNING, null);
      editor.getCaretModel().removeCaretListener(caretListener);
      HighlightManagerImpl highlightManager = (HighlightManagerImpl)HighlightManager.getInstance(project);
      highlightManager.hideHighlights(grammarEditor, HighlightManager.HIDE_BY_ESCAPE | HighlightManager.HIDE_BY_ANY_KEY);
    }
    else {
      updateGrammarHighlighters(project, editor, livePreviewLanguage, grammarEditor);

      caretListener = new CaretListener() {
        @Override
        public void caretPositionChanged(final CaretEvent e) {
          final CaretListener caretListener = this;
          LivePreviewHelper.getUpdateQueue(project).queue(new Update(e.getEditor()) {
            @Override
            public void run() {
              if (grammarEditor.isDisposed()) {
                e.getEditor().getCaretModel().removeCaretListener(caretListener);
                e.getEditor().putUserData(GRAMMAR_UPDATE_RUNNING, null);
                return;
              }
              ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                  ApplicationManagerEx.getApplicationEx().tryRunReadAction(new Runnable() {
                    @Override
                    public void run() {
                      updateGrammarHighlighters(project, editor, livePreviewLanguage, grammarEditor);
                    }
                  });
                }
              });
            }
          });
        }
      };
      editor.getCaretModel().addCaretListener(caretListener);
      editor.putUserData(GRAMMAR_UPDATE_RUNNING, caretListener);
    }
  }

  private static void updateGrammarHighlighters(@NotNull final Project project,
                                                @NotNull Editor editor,
                                                @NotNull LivePreviewLanguage livePreviewLanguage,
                                                @NotNull final Editor grammarEditor) {
    final Set<TextRange> trueRanges = new HashSet<TextRange>();
    final Set<TextRange> falseRanges = new HashSet<TextRange>();
    final Set<BnfExpression> visited = new HashSet<BnfExpression>();
    LivePreviewHelper.collectExpressionsAtOffset(project, editor, livePreviewLanguage, new PairProcessor<BnfExpression, Boolean>() {
      @Override
      public boolean process(BnfExpression bnfExpression, Boolean result) {
        for (PsiElement parent = bnfExpression.getParent(); parent instanceof BnfExpression && visited.add((BnfExpression)parent); ) {
          parent = parent.getParent();
        }
        if (visited.add(bnfExpression)) {
          (result ? trueRanges : falseRanges).add(bnfExpression.getTextRange());
        }
        return true;
      }
    });
    UIUtil.invokeLaterIfNeeded(new Runnable() {
      @Override
      public void run() {
        applyHighlights(project, grammarEditor, trueRanges, falseRanges);
      }
    });
  }

  private static void applyHighlights(Project project, Editor grammarEditor, Set<TextRange> trueRanges, Set<TextRange> falseRanges) {
    if (grammarEditor.isDisposed()) return;
    EditorColorsManager manager = EditorColorsManager.getInstance();
    TextAttributes trueAttrs = manager.getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
    TextAttributes falseAttrs = manager.getGlobalScheme().getAttributes(EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES);

    HighlightManagerImpl highlightManager = (HighlightManagerImpl)HighlightManager.getInstance(project);
    highlightManager.hideHighlights(grammarEditor, HighlightManager.HIDE_BY_ESCAPE | HighlightManager.HIDE_BY_ANY_KEY);
    for (TextRange range : trueRanges) {
      highlightManager.addRangeHighlight(grammarEditor, range.getStartOffset(), range.getEndOffset(), trueAttrs, true, null);
    }
    for (TextRange range : falseRanges) {
      highlightManager.addRangeHighlight(grammarEditor, range.getStartOffset(), range.getEndOffset(), falseAttrs, true, null);
    }
  }

  private static Editor getGrammarEditor(Project project, LivePreviewLanguage livePreviewLanguage) {
    VirtualFile grammarFile = livePreviewLanguage.getGrammarFile();
    FileEditor selectedEditor = grammarFile == null? null : FileEditorManager.getInstance(project).getSelectedEditor(grammarFile);
    return selectedEditor instanceof TextEditor? ((TextEditor)selectedEditor).getEditor() : null;
  }
}
