package org.intellij.grammar.livePreview;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.LanguageStructureViewBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiDocumentManagerImpl;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.FileContentUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.Nullable;

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
    FileEditorManager.getInstance(project).openFile(virtualFile, true);
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

  private static final Key<MergingUpdateQueue> LIVE_PREVIEW_INITIALIZED = Key.create("LIVE_PREVIEW_INITIALIZED");
  private static void installUpdateListener(final Project project) {
    MergingUpdateQueue queue = project.getUserData(LIVE_PREVIEW_INITIALIZED);
    if (queue == null) {
      project.putUserData(LIVE_PREVIEW_INITIALIZED, queue = new MergingUpdateQueue("LIVE_PREVIEW_QUEUE", 1000, true, null, project));
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
}
