/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.SimpleJavaParameters;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SimpleJavaSdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.MessageView;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import com.intellij.util.ui.UIUtil;
import org.intellij.grammar.config.Options;
import org.intellij.grammar.generator.batch.FileGeneratorUtil;
import org.intellij.jflex.parser.JFlexFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.intellij.grammar.generator.batch.FileGeneratorUtil.fail;
import static org.intellij.grammar.generator.batch.FileGeneratorUtil.getTargetDirectoryFor;

/**
 * Action that runs the JFlex lexer generator on one or more {@code .flex} files selected in the project view.
 *
 * <p>When invoked, the action:
 * <ol>
 *   <li>Saves all open documents.</li>
 *   <li>Locates the JFlex JAR and {@code idea-flex.skeleton} file, downloading them automatically if they
 *       are not already registered in a global library named "{@value #LIB_NAME}".</li>
 *   <li>Runs each selected file through JFlex sequentially, placing the generated {@code .java} source
 *       in the appropriate source directory (resolved via {@link FileGeneratorUtil#getTargetDirectoryFor}).</li>
 *   <li>Streams process output to a reusable tab in the IDE {@code Messages} tool window.</li>
 * </ol>
 *
 * <p>The action is visible and enabled only when at least one {@code .flex} file is selected.
 *
 * @author greg
 */
public class BnfRunJFlexAction extends DumbAwareAction {

  private static final String JFLEX_URL =
    "https://cache-redirector.jetbrains.com/intellij-dependencies/org/jetbrains/intellij/deps/jflex/jflex/1.10.17/jflex-1.10.17.jar";

  private static final String SKEL_NAME = "idea-flex.skeleton";
  private static final String JFLEX_JAR_PREFIX = "jflex-";
  private static final String LIB_NAME = "JFlex & idea-flex.skeleton";

  private static final Key<Pair<String, OSProcessHandler>> BATCH_ID_KEY = Key.create("BnfRunJFlexAction.batchId");

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    List<VirtualFile> files = getJFlexFiles(e);
    e.getPresentation().setEnabledAndVisible(project != null && !files.isEmpty());
  }

  /**
   * Returns the JFlex files from the current selection.
   * Accepts files recognised as {@link JFlexFileType} as well as any non-binary file whose name ends with {@code .flex}.
   */
  private static List<VirtualFile> getJFlexFiles(@NotNull AnActionEvent e) {
    return JBIterable.of(e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)).filter(file -> {
      FileType fileType = file.getFileType();
      return fileType == JFlexFileType.INSTANCE ||
             !fileType.isBinary() && file.getName().endsWith(".flex");
    }).toList();
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = getEventProject(e);
    List<VirtualFile> files = getJFlexFiles(e);
    if (project == null || files.isEmpty()) return;

    PsiDocumentManager.getInstance(project).commitAllDocuments();
    FileDocumentManager.getInstance().saveAllDocuments();

    Couple<File> flexFiles = getOrDownload(project);
    if (flexFiles == null) {
      fail(project, "JFlex jar not found",
           "Create global library with jflex-xxx.jar and idea-flex.skeleton file to fix.");
      return;
    }
    String batchId = "jflex@" + System.nanoTime();
    new Runnable() {
      final Iterator<VirtualFile> it = files.iterator();

      @Override
      public void run() {
        if (it.hasNext()) {
          doGenerate(project, it.next(), flexFiles, batchId).doWhenProcessed(this);
        }
      }
    }.run();
  }

  /**
   * Runs JFlex on a single {@code .flex} file and returns a callback that completes when the process exits.
   *
   * <p>The method parses the {@code %class} directive to determine the output class name, and the
   * {@code package} statement to place the generated file in the correct source root subdirectory.
   * JFlex is launched as a child process of the current JVM; output is shown in the console tab
   * identified by {@code batchId}.
   *
   * @param project  the current project
   * @param flexFile the {@code .flex} grammar file to compile
   * @param jflex    {@code first} — the JFlex JAR; {@code second} — the skeleton file (may be {@code null})
   * @param batchId  opaque identifier that groups multiple files from a single action invocation
   *                 into the same console tab (see {@link #showConsole})
   * @return a callback that is {@linkplain ActionCallback#setDone() done} on exit code 0,
   * or {@linkplain ActionCallback#setRejected() rejected} on failure
   */
  public static ActionCallback doGenerate(@NotNull Project project,
                                          @NotNull VirtualFile flexFile,
                                          @NotNull Couple<File> jflex,
                                          @NotNull String batchId) {
    FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
    Document document = fileDocumentManager.getDocument(flexFile);
    if (document == null) return ActionCallback.REJECTED;

    String text = document.getText();
    Matcher matcherClass = Pattern.compile("%class\\s+(\\w+)").matcher(text);
    String lexerClassName = matcherClass.find() ? matcherClass.group(1) : null;
    Matcher matcherPackage = Pattern.compile("package\\s+([^;]+);|(%%)").matcher(text);
    String lexerPackage = matcherPackage.find() ? StringUtil.trim(matcherPackage.group(1)) : null;
    if (lexerClassName == null) {
      String content = "Lexer class name option not found, use <pre>%class LexerClassName</pre>";
      fail(project, flexFile, content);
      return ActionCallback.REJECTED;
    }

    try {
      VirtualFile virtualDir = getTargetDirectoryFor(project, flexFile, lexerClassName + ".java", lexerPackage, false, true);
      File workingDir = VfsUtilCore.virtualToIoFile(flexFile).getParentFile().getAbsoluteFile();

      Sdk sdk = new SimpleJavaSdkType().createJdk("tmp", SystemProperties.getJavaHome());

      SimpleJavaParameters javaParameters = new SimpleJavaParameters();
      javaParameters.setCharset(flexFile.getCharset());
      javaParameters.setWorkingDirectory(workingDir);
      javaParameters.setJdk(sdk);
      javaParameters.setJarPath(jflex.first.getAbsolutePath());
      javaParameters.getVMParametersList().add("-Xmx512m");
      javaParameters.getProgramParametersList().addParametersString(StringUtil.nullize(Options.GEN_JFLEX_ARGS.get()));
      if (jflex.second != null) {
        javaParameters.getProgramParametersList().add("-skel", jflex.second.getAbsolutePath());
      }
      javaParameters.getProgramParametersList().add("-d", VfsUtilCore.virtualToIoFile(virtualDir).getAbsolutePath());
      javaParameters.getProgramParametersList().add(flexFile.getName());

      OSProcessHandler processHandler = javaParameters.createOSProcessHandler();

      showConsole(project, "JFlex", batchId, processHandler);

      ActionCallback callback = new ActionCallback();
      processHandler.addProcessListener(new ProcessAdapter() {
        @Override
        public void processTerminated(@NotNull ProcessEvent event) {
          Runnable runnable = event.getExitCode() == 0 ? callback::setDone : callback::setRejected;
          ApplicationManager.getApplication().invokeLater(runnable, project.getDisposed());
          VfsUtil.markDirtyAndRefresh(true, false, true, virtualDir);
        }
      });
      processHandler.startNotify();
      return callback;
    }
    catch (ExecutionException ex) {
      Messages.showErrorDialog(project, "Unable to run JFlex" + "\n" + ex.getLocalizedMessage(), "JFlex");
      return ActionCallback.REJECTED;
    }
  }

  /**
   * Attaches a process to a console tab in the {@code Messages} tool window and activates the window.
   *
   * <p>Tab-reuse strategy:
   * <ul>
   *   <li>If a tab already exists for the given {@code batchId} (i.e., this is not the first file in a
   *       multi-file invocation), the existing console is reused and a separator is printed.</li>
   *   <li>Otherwise, if there is an unpinned tab whose previous process has already terminated, that tab
   *       is cleared and reused.</li>
   *   <li>If neither condition holds, a new tab is created with the given {@code title}.</li>
   * </ul>
   *
   * @param project        the current project
   * @param title          tab label used when a new tab must be created
   * @param batchId        opaque identifier shared by all files in a single action invocation
   * @param processHandler the process whose output should be shown in the console
   */
  public static void showConsole(@NotNull Project project,
                                 @NotNull String title,
                                 @NotNull String batchId,
                                 @NotNull OSProcessHandler processHandler) {
    MessageView messageView = MessageView.getInstance(project);
    Content batchContent = null, stoppedContent = null;
    for (Content c : messageView.getContentManager().getContents()) {
      Pair<String, OSProcessHandler> data = c.getUserData(BATCH_ID_KEY);
      if (data == null) continue;
      if (data.first.equals(batchId)) {
        batchContent = c;
      }
      else if (data.second.isProcessTerminated() || data.second.isProcessTerminating()) {
        if (!c.isPinned()) {
          stoppedContent = c;
        }
      }
    }
    Content content = ObjectUtils.chooseNotNull(batchContent, stoppedContent);
    ConsoleView consoleView = content == null ? null : UIUtil.uiTraverser(content.getComponent()).filter(ConsoleView.class).first();

    if (content != null && consoleView != null) {
      if (content == batchContent) {
        consoleView.print("\n\n\n", ConsoleViewContentType.SYSTEM_OUTPUT);
      }
      else {
        consoleView.clear();
      }
      attachAndActivate(project, batchId, processHandler, content, consoleView);
      return;
    }

    consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();

    JComponent panel = new JPanel(new BorderLayout());
    panel.add(consoleView.getComponent(), BorderLayout.CENTER);

    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    for (AnAction action : consoleView.createConsoleActions()) {
      toolbarActions.add(action);
    }
    ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, toolbarActions, false);
    toolbar.setTargetComponent(consoleView.getComponent());
    panel.add(toolbar.getComponent(), BorderLayout.WEST);

    content = ContentFactory.getInstance().createContent(panel, title, true);
    messageView.getContentManager().addContent(content);
    Disposer.register(content, consoleView);

    attachAndActivate(project, batchId, processHandler, content, consoleView);
  }

  /**
   * Wires a process handler to a console view, stamps the content tab with the batch identifier,
   * and brings the {@code Messages} tool window to the front with that tab selected.
   *
   * <p>Stamping the tab with {@link #BATCH_ID_KEY} is what allows subsequent calls from the same
   * batch (multi-file invocation) to locate and reuse the tab instead of opening a new one.
   */
  private static void attachAndActivate(@NotNull Project project,
                                        @NotNull String batchId,
                                        @NotNull OSProcessHandler processHandler,
                                        @NotNull Content content,
                                        @NotNull ConsoleView consoleView) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.MESSAGES_WINDOW);
    content.putUserData(BATCH_ID_KEY, Pair.create(batchId, processHandler));
    consoleView.attachToProcess(processHandler);

    if (toolWindow != null) {
      toolWindow.activate(() -> toolWindow.getContentManager().setSelectedContent(content), false, false);
    }
  }

  /**
   * Returns the JFlex JAR and skeleton file, downloading them if necessary.
   *
   * <p>Search order:
   * <ol>
   *   <li>Scan every global library's class roots via {@link #findInUrls}; return the first match.</li>
   *   <li>If nothing is found, download the JAR via {@link #downloadJFlex}.</li>
   *   <li>Extract {@value #SKEL_NAME} from inside the downloaded JAR and write it next to the JAR
   *       on disk so JFlex can reference it as a plain file via {@code -skel}.</li>
   *   <li>Register (or update) the global library "{@value #LIB_NAME}" so future invocations
   *       hit step 1 instead of downloading again.</li>
   * </ol>
   *
   * @return a pair of {@code (jflex jar, skeleton file)}, where the skeleton may be {@code null}
   * if extraction failed; or {@code null} if the JAR could not be obtained at all
   */
  private static @Nullable Couple<File> getOrDownload(@NotNull Project project) {
    LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable();
    for (Library library : libraryTable.getLibraries()) {
      Couple<File> result = findInUrls(library.getUrls(OrderRootType.CLASSES));
      if (result != null) return result;
    }

    File jarFile = downloadJFlex(project);
    if (jarFile == null) return null;
    File skelFile = new File(jarFile.getParent(), SKEL_NAME);
    VirtualFile skel = JarFileSystem.getInstance().findFileByPath(jarFile.getPath() + JarFileSystem.JAR_SEPARATOR + "/jflex/" + SKEL_NAME);
    if (!skelFile.exists() && skel != null && !skel.isDirectory()) {
      try {
        FileUtil.writeToFile(skelFile, skel.contentsToByteArray());
      }
      catch (IOException e) {
        fail(project, "Writing " + SKEL_NAME + " failed", e.toString());
        skelFile = null;
      }
    }
    Couple<File> result = Couple.of(jarFile, skelFile);
    WriteAction.run(() -> createOrUpdateLibrary(result));
    return result;
  }


  /**
   * Scans a list of library class-root URLs for a JFlex JAR and the skeleton file.
   *
   * <p>A root is recognised as the JFlex JAR if its filename starts with {@value #JFLEX_JAR_PREFIX}
   * and ends with {@code .jar}. A root is recognised as the skeleton if its filename equals
   * {@value #SKEL_NAME}. Both files must exist on disk; virtual entries are ignored.
   *
   * @param rootUrls class-root URLs as returned by {@link Library#getUrls(OrderRootType)}
   * @return a pair of {@code (jar, skeleton)} if at least the JAR was found, otherwise {@code null};
   * the skeleton element may be {@code null} if only the JAR was present in the roots
   */
  private static @Nullable Couple<File> findInUrls(String... rootUrls) {
    File jarFile = null, skelFile = null;
    for (String root : rootUrls) {
      root = StringUtil.trimEnd(root, JarFileSystem.JAR_SEPARATOR);
      String rootName = root.substring(root.lastIndexOf("/") + 1);
      boolean isJar;
      if (jarFile == null && rootName.startsWith(JFLEX_JAR_PREFIX) && rootName.endsWith(".jar")) {
        isJar = true;
      }
      else if (skelFile == null && rootName.equals(SKEL_NAME)) {
        isJar = false;
      }
      else {
        continue;
      }
      File file = new File(FileUtil.toSystemDependentName(VfsUtilCore.urlToPath(root)));
      if (!file.exists() || !file.isFile()) continue;
      if (isJar) {
        jarFile = file;
      }
      else {
        skelFile = file;
      }
    }
    return jarFile != null ? Couple.of(jarFile, skelFile) : null;
  }

  /**
   * Downloads the JFlex JAR from {@link #JFLEX_URL} using the IDE's built-in download infrastructure,
   * which shows a progress dialog to the user.
   *
   * @return the downloaded JAR as a local {@link File}, or {@code null} if the download was
   * cancelled or failed
   */
  private static @Nullable File downloadJFlex(@NotNull Project project) {
    String url = JFLEX_URL;
    DownloadableFileService service = DownloadableFileService.getInstance();
    List<DownloadableFileDescription> descriptions = new ArrayList<>();
    descriptions.add(service.createFileDescription(url, url.substring(url.lastIndexOf("/") + 1)));
    Pair<VirtualFile, DownloadableFileDescription> pair = ContainerUtil.getFirstItem(
      service.createDownloader(descriptions, LIB_NAME)
        .downloadWithProgress(null, project, null));
    File file = pair == null ? null : VfsUtil.virtualToIoFile(pair.first);
    return file != null && file.exists() && file.isFile() ? file : null;
  }

  /**
   * Creates or updates the application-level library named "{@value #LIB_NAME}" to point at the
   * given JAR and skeleton files as class roots.
   *
   * <p>If the library does not yet exist it is created. Roots are only ever added, never removed,
   * so calling this method after a fresh download is idempotent with respect to pre-existing roots.
   * Must be called under a write action.
   *
   * @param files {@code first} — the JFlex JAR; {@code second} — the skeleton file (may be {@code null})
   */
  private static void createOrUpdateLibrary(@NotNull Couple<File> files) {
    String libraryName = LIB_NAME;
    ApplicationManager.getApplication().assertWriteAccessAllowed();
    LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable();
    Library library = libraryTable.getLibraryByName(libraryName);
    if (library == null) {
      LibraryTable.ModifiableModel modifiableModel = libraryTable.getModifiableModel();
      library = modifiableModel.createLibrary(libraryName);
      modifiableModel.commit();
    }
    Library.ModifiableModel modifiableModel = library.getModifiableModel();
    modifiableModel.addRoot(VfsUtil.fileToUrl(files.first), OrderRootType.CLASSES);
    if (files.second != null) {
      modifiableModel.addRoot(VfsUtil.fileToUrl(files.second), OrderRootType.CLASSES);
    }
    modifiableModel.commit();
  }
}
