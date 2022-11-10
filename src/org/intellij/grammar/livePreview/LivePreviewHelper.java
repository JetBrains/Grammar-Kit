/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.TokenType;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.Alarm;
import com.intellij.util.FileContentUtilCore;
import com.intellij.util.PairProcessor;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author gregsh
 */
@Service
public final class LivePreviewHelper implements Disposable {

  public static LivePreviewHelper getInstance() {
    return ApplicationManager.getApplication().getService(LivePreviewHelper.class);
  }

  private final MergingUpdateQueue myQueue;

  public LivePreviewHelper() {
    myQueue = new MergingUpdateQueue(
      "LivePreview update queue", 500, true, null, this, null, Alarm.ThreadToUse.SWING_THREAD);
    FileDocumentManager fileManager = FileDocumentManager.getInstance();
    EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
      @Override
      public void documentChanged(@NotNull DocumentEvent e) {
        Document document = e.getDocument();
        VirtualFile file = fileManager.getFile(document);
        if (file == null) return;
        if (FileTypeManager.getInstance().getFileTypeByFileName(file.getName()) != BnfFileType.INSTANCE) return;
        myQueue.run(Update.create(file, () -> reparseAllLivePreviews(file, document)));
      }
    }, this);
  }

  @Override
  public void dispose() {
  }

  /** @noinspection MethodMayBeStatic*/
  public void showFor(@NotNull BnfFile bnfFile) {
    Project project = bnfFile.getProject();
    PsiFile psiFile = parseFile(bnfFile, "");
    VirtualFile virtualFile = psiFile == null ? null : psiFile.getVirtualFile();
    if (virtualFile == null) return;

    FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(project);
    EditorWindow curWindow = fileEditorManager.getCurrentWindow();
    curWindow.split(SwingConstants.HORIZONTAL, false, virtualFile, true);
    fileEditorManager.openFile(virtualFile, true);
  }

  public static @Nullable PsiFile parseFile(BnfFile bnfFile, String text) {
    Language language = getLanguageFor(bnfFile);
    String fileName = bnfFile.getName() + ".preview";
    LightVirtualFile virtualFile = new LightVirtualFile(fileName, language, text);
    Project project = bnfFile.getProject();
    return PsiManager.getInstance(project).findFile(virtualFile);
  }

  public static @NotNull Language getLanguageFor(BnfFile psiFile) {
    LivePreviewLanguage existing = LivePreviewLanguage.findInstance(psiFile);
    if (existing != null) return existing;
    LivePreviewLanguage language = LivePreviewLanguage.newInstance(psiFile);
    registerLanguageExtensions(language);
    return language;
  }

  public static void registerLanguageExtensions(LivePreviewLanguage language) {
    LanguageStructureViewBuilder.INSTANCE.addExplicitExtension(language, new LivePreviewStructureViewFactory());
    LanguageParserDefinitions.INSTANCE.addExplicitExtension(language, new LivePreviewParserDefinition(language));
  }

  public static void unregisterLanguageExtensions(LivePreviewLanguage language) {
    LanguageStructureViewBuilder.INSTANCE.removeExplicitExtension(language, LanguageStructureViewBuilder.INSTANCE.forLanguage(language));
    LanguageParserDefinitions.INSTANCE.removeExplicitExtension(language, LanguageParserDefinitions.INSTANCE.forLanguage(language));
  }

  private static void reparseAllLivePreviews(@NotNull VirtualFile bnfFile, @NotNull Document document) {
    Collection<VirtualFile> files = new LinkedHashSet<>();
    for (Project project : ProjectManager.getInstance().getOpenProjects()) {
      FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
      boolean committed = false;
      for (VirtualFile file : fileEditorManager.getOpenFiles()) {
        Language language = file instanceof LightVirtualFile ? ((LightVirtualFile)file).getLanguage() : null;
        if (!(language instanceof LivePreviewLanguage previewLanguage)) continue;
        if (!bnfFile.equals(previewLanguage.getGrammarFile())) continue;
        files.add(file);
        if (!committed) {
          PsiDocumentManager.getInstance(project).commitDocument(document);
          committed = true;
        }
      }
    }
    if (files.isEmpty()) return;
    FileContentUtilCore.reparseFiles(files);
  }

  public static void collectExpressionsAtOffset(Project project, Editor previewEditor, LivePreviewLanguage language,
                                                PairProcessor<? super BnfExpression, ? super Boolean> processor) {
    Lexer lexer = new LivePreviewLexer(project, language);
    ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(language);
    PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(parserDefinition, lexer, previewEditor.getDocument().getText());
    int caretOffset = previewEditor.getCaretModel().getOffset();
    PsiParser parser = new LivePreviewParser(project, language) {
      @Override
      protected boolean generateNodeCall(PsiBuilder builder,
                                         int level,
                                         BnfRule rule,
                                         @Nullable BnfExpression node,
                                         String nextName,
                                         Map<String, GeneratedParserUtilBase.Parser> externalArguments) {
        int tokenStartOffset = builder.getCurrentOffset();
        int initialOffset = builder.rawLookup(-1) == TokenType.WHITE_SPACE ? builder.rawTokenTypeStart(-1) : builder.getCurrentOffset();
        String tokenText = builder.getTokenText();
        int tokenEndOffset = tokenText == null? tokenStartOffset : tokenStartOffset + tokenText.length();
        boolean result = super.generateNodeCall(builder, level, rule, node, nextName, externalArguments);
        builder.getCurrentOffset(); // advance to the next token first
        int finalOffset = builder.rawLookup(-1) == TokenType.WHITE_SPACE ? builder.rawTokenTypeStart(-1) : builder.getCurrentOffset();
        if (node != null) {
          if (result && initialOffset <= caretOffset && finalOffset > caretOffset ||
              !result && initialOffset <= caretOffset && tokenEndOffset > caretOffset) {
            boolean inWhitespace = isTokenExpression(node) && tokenStartOffset > caretOffset;
            if (!processor.process(node, result && !inWhitespace)) {
              throw new ProcessCanceledException();
            }
          }
        }
        return result;
      }

    };
    parser.parse(parserDefinition.getFileNodeType(), builder);
  }
}
