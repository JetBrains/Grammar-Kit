package org.intellij.grammar.livePreview;

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.PsiDocumentManagerImpl;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.FileContentUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.PairProcessor;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;

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
    if (language == null) return null;

    String fileName = bnfFile.getName() + ".preview";
    LightVirtualFile virtualFile = new LightVirtualFile(fileName, language, text);
    final Project project = bnfFile.getProject();
    return PsiManager.getInstance(project).findFile(virtualFile);
  }

  @Nullable
  public static Language getLanguageFor(BnfFile psiFile) {
    VirtualFile vFile = psiFile.getVirtualFile();
    if (vFile == null) return null;
    for (Language language : Language.getRegisteredLanguages()) {
      if (language instanceof LivePreviewLanguage &&
          vFile == ((LivePreviewLanguage)language).getGrammarFile()) {
        return language;
      }
    }
    // create new one
    LivePreviewLanguage language = LivePreviewLanguage.newInstance(vFile);
    LanguageStructureViewBuilder.INSTANCE.addExplicitExtension(language, new LivePreviewStructureViewFactory());
    LanguageParserDefinitions.INSTANCE.addExplicitExtension(language, new LivePreviewParserDefinition(language));
    //SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(language, new LivePreviewSyntaxHighlighterFactory(language));
    return language;
  }

  public static MergingUpdateQueue getUpdateQueue(Project project) {
    return project.getUserData(LIVE_PREVIEW_QUEUE);
  }

  private static final Key<MergingUpdateQueue> LIVE_PREVIEW_QUEUE = Key.create("LIVE_PREVIEW_QUEUE");
  private static void installUpdateListener(final Project project) {
    MergingUpdateQueue queue = project.getUserData(LIVE_PREVIEW_QUEUE);
    if (queue == null) {
      JComponent activationComponent = WindowManager.getInstance().getFrame(project).getRootPane();
      project.putUserData(LIVE_PREVIEW_QUEUE, queue = new MergingUpdateQueue("LIVE_PREVIEW_QUEUE", 1000, true, null, project, activationComponent));
    }
    final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
    final MergingUpdateQueue finalQueue = queue;
    EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentAdapter() {
      @Override
      public void documentChanged(DocumentEvent e) {
        Document document = e.getDocument();
        final FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        VirtualFile file = fileDocumentManager.getFile(document);
        if (file == null || file.getFileType() != BnfFileType.INSTANCE) return;
        finalQueue.cancelAllUpdates();
        finalQueue.queue(new Update(Boolean.TRUE, true) {
          @Override
          public void run() {
            FileManager fileManager = ((PsiManagerEx)PsiManager.getInstance(project)).getFileManager();
            for (FileEditor fileEditor : fileEditorManager.getAllEditors()) {
              if (!(fileEditor instanceof TextEditor)) continue;
              EditorEx editor = (EditorEx)((TextEditor)fileEditor).getEditor();
              Document document = editor.getDocument();
              VirtualFile virtualFile = editor.getVirtualFile();
              Language language = virtualFile instanceof LightVirtualFile ? ((LightVirtualFile)virtualFile).getLanguage() : null;
              if (!(language instanceof LivePreviewLanguage)) continue;

              FileContentUtil.reparseFiles(project, Collections.singletonList(virtualFile), false);
              fileManager.setViewProvider(virtualFile, fileManager.createFileViewProvider(virtualFile, true));
              PsiDocumentManagerImpl.cachePsi(document, ObjectUtils.assertNotNull(PsiManager.getInstance(project).findFile(virtualFile)));
              editor.setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, virtualFile));
            }
          }
        });
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

  public static boolean collectExpressionsAtOffset(Project project, Editor previewEditor, LivePreviewLanguage language, final PairProcessor<BnfExpression, Boolean> processor) {
    Lexer lexer = new LivePreviewLexer(project, language);
    final ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(language);
    final PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(parserDefinition, lexer, previewEditor.getDocument().getText());
    final int caretOffset = previewEditor.getCaretModel().getOffset();
    final PsiParser parser = new LivePreviewParser(project, language) {
      @Override
      protected boolean generateNodeCall(PsiBuilder builder, int level, BnfRule rule, @Nullable BnfExpression node, String nextName) {
        int tokenStartOffset = builder.getCurrentOffset();
        int initialOffset = builder.rawLookup(-1) == TokenType.WHITE_SPACE ? builder.rawTokenTypeStart(-1) : builder.getCurrentOffset();
        String tokenText = builder.getTokenText();
        int tokenEndOffset = tokenText == null? tokenStartOffset : tokenStartOffset + tokenText.length();
        boolean result = super.generateNodeCall(builder, level, rule, node, nextName);
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
