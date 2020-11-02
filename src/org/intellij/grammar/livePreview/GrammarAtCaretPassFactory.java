/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.codeHighlighting.*;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class GrammarAtCaretPassFactory implements TextEditorHighlightingPassFactory, TextEditorHighlightingPassFactoryRegistrar {
  public static final Key<Boolean> GRAMMAR_AT_CARET_KEY = Key.create("GRAMMAR_AT_CARET_KEY");

  @Override
  public void registerHighlightingPassFactory(@NotNull TextEditorHighlightingPassRegistrar registrar, @NotNull Project project) {
    registrar.registerTextEditorHighlightingPass(this, null, new int[]{Pass.UPDATE_ALL}, false, -1);
  }

  @Override
  public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
    if (ApplicationManager.getApplication().isHeadlessEnvironment()) return null;

    if (editor.isOneLineMode()) return null;
    if (!(file instanceof BnfFile)) return null;

    VirtualFile virtualFile = file.getVirtualFile();
    if (virtualFile == null || !FileEditorManager.getInstance(file.getProject()).isFileOpen(virtualFile)) return null;

    return new TextEditorHighlightingPass(file.getProject(), editor.getDocument(), false) {
      final List<HighlightInfo> infos = new ArrayList<>();

      @Override
      public void doCollectInformation(@NotNull ProgressIndicator progress) {
        infos.clear();
        LivePreviewLanguage previewLanguage = LivePreviewLanguage.findInstance(file);
        if (previewLanguage == null) return;
        List<Editor> previewEditors = previewLanguage.getPreviewEditors(myProject);
        for (Editor e : previewEditors) {
          if (Boolean.TRUE.equals(GRAMMAR_AT_CARET_KEY.get(e))) {
            collectHighlighters(myProject, previewEditors.get(0), previewLanguage, infos);
          }
        }
      }

      @Override
      public void doApplyInformationToEditor() {
        Document document = editor.getDocument();
        UpdateHighlightersUtil.setHighlightersToEditor(myProject, document, 0, file.getTextLength(), infos, getColorsScheme(), getId());
      }
    };
  }

  private static void collectHighlighters(@NotNull final Project project,
                                          @NotNull Editor editor,
                                          @NotNull LivePreviewLanguage livePreviewLanguage,
                                          @NotNull List<HighlightInfo> result) {
    final Set<TextRange> trueRanges = new HashSet<>();
    final Set<TextRange> falseRanges = new HashSet<>();
    final Set<BnfExpression> visited = new HashSet<>();
    LivePreviewHelper.collectExpressionsAtOffset(project, editor, livePreviewLanguage, (bnfExpression, result1) -> {
      for (PsiElement parent = bnfExpression.getParent();
           parent instanceof BnfExpression && visited.add((BnfExpression)parent); ) {
        parent = parent.getParent();
      }
      if (visited.add(bnfExpression)) {
        (result1 ? trueRanges : falseRanges).add(bnfExpression.getTextRange());
      }
      return true;
    });
    createHighlights(trueRanges, falseRanges, result);
  }

  private static void createHighlights(Set<TextRange> trueRanges,
                                       Set<TextRange> falseRanges,
                                       List<HighlightInfo> result) {
    EditorColorsManager manager = EditorColorsManager.getInstance();
    TextAttributes trueAttrs = manager.getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);
    TextAttributes falseAttrs = manager.getGlobalScheme().getAttributes(EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES);

    for (TextRange range : trueRanges) {
      HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
        .range(range)
        .textAttributes(trueAttrs)
        .createUnconditionally();
      result.add(info);
    }
    for (TextRange range : falseRanges) {
      HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
        .range(range)
        .textAttributes(falseAttrs)
        .createUnconditionally();
      result.add(info);
    }
  }

}
