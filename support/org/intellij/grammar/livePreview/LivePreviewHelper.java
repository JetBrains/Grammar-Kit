package org.intellij.grammar.livePreview;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.LanguageStructureViewBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.FileContentUtil;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

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

  private static final Key<Boolean> LIVE_PREVIEW_INITIALIZED = Key.create("LIVE_PREVIEW_INITIALIZED");
  private static void installUpdateListener(final Project project) {
    if (Boolean.TRUE.equals(project.getUserData(LIVE_PREVIEW_INITIALIZED))) return;
    project.putUserData(LIVE_PREVIEW_INITIALIZED, Boolean.TRUE);
    final FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
    EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentAdapter() {
      @Override
      public void documentChanged(DocumentEvent e) {
        Document document = e.getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);

        if (file != null && file.getFileType() == BnfFileType.INSTANCE) {
          Collection<VirtualFile> list = new ArrayList<VirtualFile>();
          for (VirtualFile virtualFile : fileEditorManager.getOpenFiles()) {
            Language language = virtualFile instanceof LightVirtualFile ? ((LightVirtualFile)virtualFile).getLanguage() : null;
            if (!(language instanceof LivePreviewLanguage)) continue;
            list.add(virtualFile);
          }
          FileContentUtil.reparseFiles(project, list, false);
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
}
