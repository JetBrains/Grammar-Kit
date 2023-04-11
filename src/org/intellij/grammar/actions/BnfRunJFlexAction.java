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

import static org.intellij.grammar.actions.FileGeneratorUtil.fail;
import static org.intellij.grammar.actions.FileGeneratorUtil.getTargetDirectoryFor;

/**
 * @author greg
 */
public class BnfRunJFlexAction extends DumbAwareAction {

  private static final String JFLEX_URL = "https://cache-redirector.jetbrains.com/intellij-dependencies" +
                                          "/org/jetbrains/intellij/deps/jflex/jflex/1.9.1/jflex-1.9.1.jar";
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
    List<VirtualFile> files = getFiles(e);
    e.getPresentation().setEnabledAndVisible(project != null && !files.isEmpty());
  }

  private static List<VirtualFile> getFiles(@NotNull AnActionEvent e) {
    return JBIterable.of(e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)).filter(file -> {
      FileType fileType = file.getFileType();
      return fileType == JFlexFileType.INSTANCE ||
             !fileType.isBinary() && file.getName().endsWith(".flex");
    }).toList();
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = getEventProject(e);
    List<VirtualFile> files = getFiles(e);
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
      VirtualFile virtualDir = getTargetDirectoryFor(project, flexFile, lexerClassName + ".java", lexerPackage, false);
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
      Messages.showErrorDialog(project, "Unable to run JFlex"+ "\n" + ex.getLocalizedMessage(), "JFlex");
      return ActionCallback.REJECTED;
    }
  }

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


  private static @Nullable Couple<File> findInUrls(String... rootUrls) {
    File jarFile = null, skelFile = null;
    for (String root : rootUrls) {
      root = StringUtil.trimEnd(root, JarFileSystem.JAR_SEPARATOR);
      String rootName = root.substring(root.lastIndexOf("/") + 1);
      boolean isJar;
      if (jarFile == null && rootName.startsWith(JFLEX_JAR_PREFIX) && rootName.endsWith(".jar")) isJar = true;
      else if (skelFile == null && rootName.equals(SKEL_NAME)) isJar = false;
      else continue;
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
