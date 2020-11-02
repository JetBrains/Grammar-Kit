/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyKey;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.TokenType;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.Alarm;
import com.intellij.util.FileContentUtil;
import com.intellij.util.PairProcessor;
import com.intellij.util.SingleAlarm;
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
public class LivePreviewHelper {

  public static void showFor(BnfFile bnfFile) {
    PsiFile psiFile = parseFile(bnfFile, "");
    VirtualFile virtualFile = psiFile == null? null : psiFile.getVirtualFile();
    if (virtualFile == null) return;
    Project project = bnfFile.getProject();
    installUpdateListener(project);

    FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(project);
    EditorWindow curWindow = fileEditorManager.getCurrentWindow();
    curWindow.split(SwingConstants.HORIZONTAL, false, virtualFile, true);
    fileEditorManager.openFile(virtualFile, true);
  }

  @Nullable
  public static PsiFile parseFile(BnfFile bnfFile, String text) {
    Language language = getLanguageFor(bnfFile);

    String fileName = bnfFile.getName() + ".preview";
    LightVirtualFile virtualFile = new LightVirtualFile(fileName, language, text);
    final Project project = bnfFile.getProject();
    return PsiManager.getInstance(project).findFile(virtualFile);
  }

  @NotNull
  public static Language getLanguageFor(BnfFile psiFile) {
    LivePreviewLanguage existing = LivePreviewLanguage.findInstance(psiFile);
    if (existing != null) return existing;
    LivePreviewLanguage language = LivePreviewLanguage.newInstance(psiFile);
    registerLanguageExtensions(language);
    return language;
  }

  public static void registerLanguageExtensions(LivePreviewLanguage language) {
    LanguageStructureViewBuilder.INSTANCE.addExplicitExtension(language, new LivePreviewStructureViewFactory());
    LanguageParserDefinitions.INSTANCE.addExplicitExtension(language, new LivePreviewParserDefinition(language));
    //SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(language, new LivePreviewSyntaxHighlighterFactory(language));
  }

  public static void unregisterLanguageExtensions(LivePreviewLanguage language) {
    LanguageStructureViewBuilder.INSTANCE.removeExplicitExtension(language, LanguageStructureViewBuilder.INSTANCE.forLanguage(language));
    LanguageParserDefinitions.INSTANCE.removeExplicitExtension(language, LanguageParserDefinitions.INSTANCE.forLanguage(language));
  }

  private static final NotNullLazyKey<SingleAlarm, Project> LIVE_PREVIEW_ALARM =
    NotNullLazyKey.create("LIVE_PREVIEW_ALARM",
                          project -> new SingleAlarm(() -> reparseAllLivePreviews(project), 300, Alarm.ThreadToUse.SWING_THREAD, project));
  private static void installUpdateListener(final Project project) {
    EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {

      final FileDocumentManager fileManager = FileDocumentManager.getInstance();
      final PsiManager psiManager = PsiManager.getInstance(project);

      @Override
      public void documentChanged(@NotNull DocumentEvent e) {
        Document document = e.getDocument();
        VirtualFile file = fileManager.getFile(document);
        PsiFile psiFile = file == null ? null : psiManager.findFile(file);
        if (psiFile instanceof BnfFile) {
          LIVE_PREVIEW_ALARM.getValue(project).cancelAndRequest();
        }
      }
    }, project);

    //project.getMessageBus().connect(project).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerAdapter() {
    //  @Override
    //  public void fileOpened(FileEditorManager source, VirtualFile file) {
    //
    //
    //    // add structure component
    //
    //    FileEditor fileEditor = source.getSelectedEditor(file);
    //    if (!(fileEditor instanceof TextEditor)) return;
    //    StructureViewBuilder builder =
    //      StructureViewBuilder.PROVIDER.getStructureViewBuilder(file.getFileType(), file, project);
    //    if (builder == null) return;
    //    StructureView structureView = builder.createStructureView(fileEditor, project);
    //
    //    Editor editor = ((TextEditor)fileEditor).getEditor();
    //    editor.getComponent().getParent().getParent().add(structureView.getComponent(), BorderLayout.EAST);
    //    Disposer.register(fileEditor, structureView);
    //  }
    //});
  }

  private static void reparseAllLivePreviews(@NotNull Project project) {
    if (!project.isOpen()) return;
    PsiDocumentManager.getInstance(project).commitAllDocuments();
    Collection<VirtualFile> files = new LinkedHashSet<>();
    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
    PsiManager psiManager = PsiManager.getInstance(project);
    for (VirtualFile file : fileEditorManager.getOpenFiles()) {
      PsiFile psiFile = psiManager.findFile(file);
      Language language = psiFile == null? null : psiFile.getLanguage();
      if (!(language instanceof LivePreviewLanguage)) continue;
      files.add(file);
    }
    FileContentUtil.reparseFiles(project, files, false);
  }

  public static boolean collectExpressionsAtOffset(Project project, Editor previewEditor, LivePreviewLanguage language, final PairProcessor<BnfExpression, Boolean> processor) {
    Lexer lexer = new LivePreviewLexer(project, language);
    final ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(language);
    final PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(parserDefinition, lexer, previewEditor.getDocument().getText());
    final int caretOffset = previewEditor.getCaretModel().getOffset();
    final PsiParser parser = new LivePreviewParser(project, language) {
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
            boolean inWhitespace = isTokenExpression(node) &&
                                   initialOffset <= caretOffset && tokenStartOffset > caretOffset;
            if (!processor.process(node, result && !inWhitespace)) {
              throw new ProcessCanceledException();
            }
          }
        }
        return result;
      }

    };
    try {
      parser.parse(parserDefinition.getFileNodeType(), builder);
      return true;
    }
    catch (ProcessCanceledException e) {
      return false;
    }
  }
}
