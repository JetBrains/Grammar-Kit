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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.SimpleJavaParameters;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.*;
import com.intellij.execution.ui.actions.CloseAction;
import com.intellij.ide.actions.PinActiveTabAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SimpleJavaSdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.libraries.ApplicationLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.PathUtil;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import org.intellij.grammar.config.Options;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.jflex.parser.JFlexFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.intellij.grammar.actions.FileGeneratorUtil.fail;
import static org.intellij.grammar.actions.FileGeneratorUtil.getTargetDirectoryFor;

/**
 * @author greg
 */
public class BnfRunJFlexAction extends DumbAwareAction {

  private static final String[] JETBRAINS_JFLEX_URLS = {
    System.getProperty("grammar.kit.jflex.jar",
                       "https://jetbrains.bintray.com/intellij-third-party-dependencies/org/jetbrains/intellij/deps/jflex/jflex/1.7.0-2/jflex-1.7.0-2.jar"),
    System.getProperty("grammar.kit.jflex.skeleton",
                       "https://raw.github.com/JetBrains/intellij-community/master/tools/lexer/idea-flex.skeleton")
  };

  @Override
  public void update(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    List<VirtualFile> files = getFiles(e);
    e.getPresentation().setEnabledAndVisible(project != null && !files.isEmpty());
  }

  private static List<VirtualFile> getFiles(@NotNull AnActionEvent e) {
    return JBIterable.of(e.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)).filter(file -> {
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

    List<File> flexFiles = getOrDownload(project, JETBRAINS_JFLEX_URLS);
    if (flexFiles.isEmpty()) {
      fail(project, "JFlex jar not found",
           "Create global library with jflex-xxx.jar and idea-flex.skeleton file to fix.");
      return;
    }
    if (StringUtil.endsWithIgnoreCase(flexFiles.get(0).getName(), "JFlex.jar")) {
      String DOC_URL = "http://jflex.de/changelog.html";
      Notifications.Bus.notify(new Notification(
        BnfConstants.GENERATION_GROUP, "An old JFlex.jar is detected",
        flexFiles.get(0).getAbsolutePath() +
        "<br>See <a href=\"" + DOC_URL + "\">" + DOC_URL + "</a>." +
        "<br><b>Compatibility note</b>: . (dot) semantics is changed, use [^] instead of .|\\n." +
        "<br><b>To update</b>: remove the old version and the global library if present." +
        "",
        NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER), project);
    }
    new Runnable() {
      boolean first = true;
      final Iterator<VirtualFile> it = files.iterator();
      @Override
      public void run() {
        if (it.hasNext()) {
          doGenerate(project, it.next(), flexFiles, first).doWhenProcessed(this);
          first = false;
        }
      }
    }.run();
  }

  public static ActionCallback doGenerate(Project project, VirtualFile flexFile, List<File> jflex, boolean forceNew) {
    FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
    Document document = fileDocumentManager.getDocument(flexFile);
    if (document == null) return ActionCallback.REJECTED;

    final String commandName = "JFlex Generator";

    String text = document.getText();
    Matcher matcherClass = Pattern.compile("%class\\s+(\\w+)").matcher(text);
    final String lexerClassName = matcherClass.find() ? matcherClass.group(1) : null;
    Matcher matcherPackage = Pattern.compile("package\\s+([^;]+);|(%%)").matcher(text);
    final String lexerPackage = matcherPackage.find() ? StringUtil.trim(matcherPackage.group(1)) : null;
    if (lexerClassName == null) {
      String content = "Lexer class name option not found, use <pre>%class LexerClassName</pre>";
      fail(project, flexFile, content);
      return ActionCallback.REJECTED;
    }

    try {
      VirtualFile virtualDir = getTargetDirectoryFor(project, flexFile, lexerClassName + ".java", lexerPackage, false);
      File workingDir = VfsUtil.virtualToIoFile(flexFile).getParentFile().getAbsoluteFile();

      Sdk sdk = new SimpleJavaSdkType().createJdk("tmp", SystemProperties.getJavaHome());

      SimpleJavaParameters javaParameters = new SimpleJavaParameters();
      javaParameters.setCharset(flexFile.getCharset());
      javaParameters.setWorkingDirectory(workingDir);
      javaParameters.setJdk(sdk);
      javaParameters.setJarPath(jflex.get(0).getAbsolutePath());
      javaParameters.getVMParametersList().add("-Xmx512m");
      javaParameters.getProgramParametersList().addParametersString(StringUtil.nullize(Options.GEN_JFLEX_ARGS.get()));
      javaParameters.getProgramParametersList().add("-skel", jflex.get(1).getAbsolutePath());
      javaParameters.getProgramParametersList().add("-d", VfsUtil.virtualToIoFile(virtualDir).getAbsolutePath());
      javaParameters.getProgramParametersList().add(flexFile.getName());

      OSProcessHandler processHandler = javaParameters.createOSProcessHandler();

      RunContentDescriptor runContentDescriptor = createConsole(project, commandName, forceNew);

      ((ConsoleViewImpl) runContentDescriptor.getExecutionConsole()).attachToProcess(processHandler);

      final ActionCallback callback = new ActionCallback();
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
      Messages.showErrorDialog(project, "Unable to run JFlex"+ "\n" + ex.getLocalizedMessage(), commandName);
      return ActionCallback.REJECTED;
    }
  }

  private static List<File> getOrDownload(@NotNull Project project, String... urls) {
    List<File> result = new ArrayList<>();
    if (findCommunitySources(project, result, urls)) return result;
    if (findInProject(project, true, result, urls)) return result;
    if (findExistingLibrary(result, urls)) return result;
    if (findInProject(project, false, result, urls)) return result;

    String libraryName = "JFlex & idea-flex.skeleton";
    List<Pair<VirtualFile, DownloadableFileDescription>> pairs = downloadFiles(project, libraryName, urls);
    if (pairs == null) return Collections.emptyList();
    ApplicationManager.getApplication().runWriteAction(
      () -> createOrUpdateLibrary(libraryName, pairs));

    // ensure the order is the same
    for (String url : urls) {
      for (Pair<VirtualFile, DownloadableFileDescription> pair : pairs) {
        if (Comparing.equal(url, pair.second.getDownloadUrl())) {
          result.add(VfsUtil.virtualToIoFile(pair.first));
          break;
        }
      }
    }

    return result;
  }

  private static boolean findCommunitySources(@NotNull Project project, List<File> result, String... urls) {
    String communitySrc = getCommunitySrcUrl(project);
    if (communitySrc == null) return false;
    List<String> roots = new ArrayList<>();
    for (String url : urls) {
      int idx = url.indexOf("/master/");
      if (idx > -1) {
        roots.add(StringUtil.trimEnd(communitySrc, "/") + "/" + url.substring(idx + "/master/".length()));
      }
      else {
        String file = PathUtil.getFileName(url);
        roots.add(PathUtil.getParentPath(communitySrc) + "/tools/lexer/lib/" + file);
      }
    }
    return collectFiles(result, roots, urls);
  }

  private static boolean findInProject(@NotNull Project project, boolean forceDir, List<File> result, String... urls) {
    List<String> roots = new ArrayList<>();
    for (String url : urls) {
      String fileName = url.substring(url.lastIndexOf("/") + 1);
      for (VirtualFile file : FilenameIndex.getVirtualFilesByName(project, fileName, ProjectScope.getAllScope(project))) {
        String fileUrl = file.getUrl();
        if (forceDir) {
          int idx = url.indexOf("/master/");
          if (idx <= -1) continue;
          if (!StringUtil.endsWithIgnoreCase(fileUrl, url.substring(idx + "/master/".length()))) continue;
        }
        roots.add(fileUrl);
      }
    }
    return collectFiles(result, roots, urls);
  }

  private static boolean findExistingLibrary(@NotNull List<File> result, String... urls) {
    for (Library library : ApplicationLibraryTable.getApplicationTable().getLibraries()) {
      if (collectFiles(result, Arrays.asList(library.getUrls(OrderRootType.CLASSES)), urls)) return true;
    }
    return false;
  }

  private static boolean collectFiles(List<File> result, List<String> roots, String... urls) {
    main: for (int i = 0; i < urls.length; i++) {
      String url = urls[i];
      String name = url.substring(url.lastIndexOf("/") + 1);

      for (String root : roots) {
        root = StringUtil.trimEnd(root, JarFileSystem.JAR_SEPARATOR);
        String rootName = root.substring(root.lastIndexOf("/") + 1);
        int length = StringUtil.commonPrefix(rootName.toLowerCase(Locale.ENGLISH), name.toLowerCase(Locale.ENGLISH)).length();
        if (length < 4) continue;
        if (rootName.length() == length || rootName.length() > length && "-_.".indexOf(rootName.charAt(length)) > -1) {
          File file = new File(FileUtil.toSystemDependentName(VfsUtil.urlToPath(root)));
          if (file.exists() && file.isFile()) {
            result.add(file);
            continue main;
          }
        }
      }
      if(result.size() < i) break;
    }
    if (result.size() == urls.length) return true;
    result.clear();
    return false;
  }

  private static List<Pair<VirtualFile, DownloadableFileDescription>> downloadFiles(@NotNull Project project,
                                                                                    @NotNull String libraryName,
                                                                                    String... urls) {
    DownloadableFileService service = DownloadableFileService.getInstance();
    List<DownloadableFileDescription> descriptions = new ArrayList<>();
    for (String url : urls) {
      descriptions.add(service.createFileDescription(url, url.substring(url.lastIndexOf("/") + 1)));
    }
    return service.createDownloader(descriptions, libraryName).downloadWithProgress(null, project, null);
  }

  private static void createOrUpdateLibrary(@NotNull String libraryName,
                                            @NotNull List<Pair<VirtualFile, DownloadableFileDescription>> pairs) {
    ApplicationManager.getApplication().assertWriteAccessAllowed();
    Library library = ApplicationLibraryTable.getApplicationTable().getLibraryByName(libraryName);
    if (library == null) {
      LibraryTable.ModifiableModel modifiableModel = ApplicationLibraryTable.getApplicationTable().getModifiableModel();
      library = modifiableModel.createLibrary(libraryName);
      modifiableModel.commit();
    }
    Library.ModifiableModel modifiableModel = library.getModifiableModel();
    for (Pair<VirtualFile, DownloadableFileDescription> pair : pairs) {
      modifiableModel.addRoot(pair.first, OrderRootType.CLASSES);
    }
    modifiableModel.commit();
  }

  @Nullable
  private static String getCommunitySrcUrl(@NotNull Project project) {
    Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
    Sdk[] jdks = ProjectJdkTable.getInstance().getAllJdks();
    for (Sdk sdk : JBIterable.of(projectSdk).append(jdks).filter(Conditions.notNull())) {
      String result = getCommunitySrcUrlInner(sdk);
      if (result != null) return result;
    }
    return null;
  }

  @Nullable
  private static String getCommunitySrcUrlInner(@NotNull Sdk projectSdk) {
    String homePath = projectSdk.getHomePath();
    String API_SCR = "/platform/lang-api/src";
    if (homePath != null) {
      for (String prefix : Arrays.asList("community", "")) {
        File file = new File(homePath, FileUtil.toSystemDependentName(prefix + API_SCR));
        if (file.exists() && file.isDirectory()) {
          return VfsUtil.pathToUrl(FileUtil.toSystemDependentName(homePath + "/" + prefix));
        }
      }
    }
    String[] sources = projectSdk.getRootProvider().getUrls(OrderRootType.SOURCES);
    for (String source : sources) {
      String communityPath = StringUtil.trimEnd(source, API_SCR);
      if (communityPath.length() < source.length()) {
        return communityPath;
      }
    }
    return null;
  }

  public static RunContentDescriptor createConsole(@NotNull Project project, String tabTitle, boolean forceNew) {
    RunContentManager runContentManager = ExecutionManager.getInstance(project).getContentManager();
    if (!forceNew) {
      for (RunContentDescriptor descriptor : runContentManager.getAllDescriptors()) {
        if (!Comparing.equal(tabTitle, descriptor.getDisplayName())) continue;
        ProcessHandler handler = descriptor.getProcessHandler();
        if (handler == null || handler.isProcessTerminated()) {
          ExecutionConsole console = descriptor.getExecutionConsole();
          if (console instanceof ConsoleViewImpl) {
            ConsoleView consoleView = (ConsoleView)console;
            consoleView.print("\n\n" + StringUtil.repeatSymbol('-', 60) + "\n", ConsoleViewContentType.SYSTEM_OUTPUT);
            ((ConsoleViewImpl)consoleView).scrollToEnd();
          }
          return descriptor;
        }
      }
    }
    TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
    ConsoleView consoleView = builder.getConsole();

    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    JComponent consoleComponent = new JPanel(new BorderLayout());

    JPanel toolbarPanel = new JPanel(new BorderLayout());
    toolbarPanel.add(ActionManager.getInstance().createActionToolbar(ActionPlaces.RUNNER_TOOLBAR, toolbarActions, false).getComponent());
    consoleComponent.add(toolbarPanel, BorderLayout.WEST);
    consoleComponent.add(consoleView.getComponent(), BorderLayout.CENTER);

    RunContentDescriptor descriptor = new RunContentDescriptor(consoleView, null, consoleComponent, tabTitle, null);

    Executor executor = DefaultRunExecutor.getRunExecutorInstance();
    for (AnAction action : consoleView.createConsoleActions()) {
      toolbarActions.add(action);
    }
    toolbarActions.add(new PinActiveTabAction());
    toolbarActions.add(new CloseAction(executor, descriptor, project));
    runContentManager.showRunContent(executor, descriptor);
    consoleView.allowHeavyFilters();
    return descriptor;
  }
}
