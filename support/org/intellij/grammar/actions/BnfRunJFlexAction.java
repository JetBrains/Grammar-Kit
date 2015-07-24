/*
 * Copyright 2011-2014 Gregory Shrago
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
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.actions.CloseAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SimpleJavaSdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.libraries.ApplicationLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.Consumer;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.jflex.parser.JFlexFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.intellij.grammar.actions.FileGeneratorUtil.fail;
import static org.intellij.grammar.actions.FileGeneratorUtil.getTargetDirectoryFor;

/**
 * @author greg
 */
public class BnfRunJFlexAction extends DumbAwareAction {

  private static final String[] JETBRAINS_JFLEX_URLS = {
      "https://github.com/JetBrains/intellij-community/raw/master/tools/lexer/jflex-1.4/lib/JFlex.jar",
      "https://raw.github.com/JetBrains/intellij-community/master/tools/lexer/idea-flex.skeleton"
  };

  @Override
  public void update(@NotNull AnActionEvent e) {
    VirtualFile file = LangDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    boolean enabled = e.getProject() != null && file != null &&
                      (file.getFileType() == JFlexFileType.INSTANCE ||
                       !file.getFileType().isBinary() && file.getName().endsWith(".flex"));
    e.getPresentation().setEnabledAndVisible(enabled);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final Project project = e.getProject();
    VirtualFile flexFile = LangDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    if (project == null || flexFile == null) return;

    PsiDocumentManager.getInstance(project).commitAllDocuments();
    FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
    fileDocumentManager.saveAllDocuments();
    Document document = fileDocumentManager.getDocument(flexFile);
    if (document == null) return;

    final String commandName = e.getPresentation().getText();

    String text = document.getText();
    Matcher matcherClass = Pattern.compile("%class\\s+(\\w+)").matcher(text);
    final String lexerClassName = matcherClass.find() ? matcherClass.group(1) : null;
    Matcher matcherPackage = Pattern.compile("package\\s+([^;]+);|(%%)").matcher(text);
    final String lexerPackage = matcherPackage.find() ? StringUtil.trim(matcherPackage.group(1)) : null;
    if (lexerClassName == null) {
      String content = "Lexer class name option not found, use <pre>%class LexerClassName</pre>";
      fail(project, flexFile, content);
      return;
    }

    try {
      List<File> jflex = getOrDownload(project, "JetBrains JFlex", JETBRAINS_JFLEX_URLS);
      if (jflex.isEmpty()) {
        fail(project, flexFile, "JFlex.jar could not be located or downloaded");
        return;
      }

      final VirtualFile virtualDir = getTargetDirectoryFor(project, flexFile, lexerClassName + ".java", lexerPackage, false);

      SimpleJavaParameters javaParameters = new SimpleJavaParameters();
      Sdk sdk = new SimpleJavaSdkType().createJdk("tmp", SystemProperties.getJavaHome());
      javaParameters.setJdk(sdk);
      javaParameters.getClassPath().add(jflex.get(0));
      javaParameters.setMainClass("JFlex.Main");
      javaParameters.getVMParametersList().add("-Xmx512m");
      javaParameters.getProgramParametersList().add("-sliceandcharat");
      javaParameters.getProgramParametersList().add("-skel", jflex.get(1).getAbsolutePath());
      javaParameters.getProgramParametersList().add("-d", VfsUtil.virtualToIoFile(virtualDir).getAbsolutePath());
      javaParameters.getProgramParametersList().add(VfsUtil.virtualToIoFile(flexFile).getAbsolutePath());

      OSProcessHandler processHandler = javaParameters.createOSProcessHandler();

      RunContentDescriptor runContentDescriptor = createConsole(project, commandName);

      ((ConsoleViewImpl) runContentDescriptor.getExecutionConsole()).attachToProcess(processHandler);


      processHandler.addProcessListener(new ProcessAdapter() {
        @Override
        public void processTerminated(ProcessEvent event) {
          if (event.getExitCode() == 0) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
              @Override
              public void run() {
                ensureLexerClassCreated(project, virtualDir, lexerClassName, commandName);
              }
            }, project.getDisposed());
          }
        }
      });
      processHandler.startNotify();
    }
    catch (ExecutionException ex) {
      Messages.showErrorDialog(project, "Unable to run JFlex"+ "\n" + ex.getLocalizedMessage(), commandName);
    }
  }

  private static void ensureLexerClassCreated(final Project project,
                                              final VirtualFile virtualDir,
                                              final String lexerClassName,
                                              String commandName) {
    VfsUtil.markDirtyAndRefresh(false, true, true, virtualDir);
    final String className = lexerClassName.startsWith("_") ? lexerClassName.substring(1) : lexerClassName + "Adapter";

    if (FilenameIndex.getFilesByName(project, className + ".java", ProjectScope.getContentScope(project)) != null) return;

    BnfGenerateParserUtilAction.createClass(className, PsiManager.getInstance(project).findDirectory(virtualDir), "com.intellij.lexer.FlexAdapter", commandName, new Consumer<PsiClass>() {
      @Override
      public void consume(PsiClass aClass) {
        PsiMethod constructor = JavaPsiFacade.getElementFactory(project).createMethodFromText(
            "public " + className + "() {\n" +
            "  super(new " + lexerClassName + "());\n" +
            "}\n", aClass);
        aClass.addAfter(constructor, aClass.getLBrace());

        Notifications.Bus.notify(new Notification(BnfConstants.GENERATION_GROUP,
            aClass.getName() + " lexer class generated", "to " + virtualDir.getPath() +
            "\n<br>Use this class in your ParserDefinition implementation." +
            "\n<br>For complex cases consider employing com.intellij.lexer.LookAheadLexer API.",
            NotificationType.INFORMATION), project);
      }
    });
  }

  private static List<File> getOrDownload(@NotNull Project project, @NotNull String libraryName, String... urls) {
    List<File> result = ContainerUtil.newArrayList();
    if (findExistingLibrary(libraryName, result, urls)) return result;
    if (findCommunitySources(project, result, urls)) return result;

    List<Pair<VirtualFile, DownloadableFileDescription>> pairs = downloadFiles(project, libraryName, urls);
    if (pairs == null) return Collections.emptyList();
    createOrUpdateLibrary(libraryName, pairs);

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
    if (communitySrc != null) {
      List<String> roots = ContainerUtil.newArrayList();
      for (String url : urls) {
        int idx = url.indexOf("/master/");
        if (idx > -1) {
          roots.add(StringUtil.trimEnd(communitySrc, "/") + "/" + url.substring(idx + "/master/".length()));
        }
      }
      return collectFiles(result, roots.toArray(new String[roots.size()]), urls);
    }
    return false;
  }

  private static boolean findExistingLibrary(@NotNull String libraryName, @NotNull List<File> result, String... urls) {
    Library library = ApplicationLibraryTable.getApplicationTable().getLibraryByName(libraryName);
    return library != null && collectFiles(result, library.getUrls(OrderRootType.CLASSES), urls);
  }

  private static boolean collectFiles(List<File> result, String[] roots, String... urls) {
    main: for (int i = 0; i < urls.length; i++) {
      String url = urls[i];
      String name = url.substring(url.lastIndexOf("/") + 1);

      for (String root : roots) {
        root = StringUtil.trimEnd(root, JarFileSystem.JAR_SEPARATOR);
        String rootName = root.substring(root.lastIndexOf("/") + 1);
        if (Comparing.strEqual(name, rootName, SystemInfo.isFileSystemCaseSensitive)) {
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
    List<DownloadableFileDescription> descriptions = ContainerUtil.newArrayList();
    for (String url : urls) {
      descriptions.add(service.createFileDescription(url, url.substring(url.lastIndexOf("/") + 1)));
    }
    return service.createDownloader(descriptions, libraryName).downloadWithProgress(null, project, null);
  }

  private static void createOrUpdateLibrary(@NotNull final String libraryName,
                                            @NotNull final List<Pair<VirtualFile, DownloadableFileDescription>> pairs) {
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
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
    });
  }

  @Nullable
  private static String getCommunitySrcUrl(@NotNull Project project) {
    Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
    if (projectSdk != null) {
      String[] sources = projectSdk.getRootProvider().getUrls(OrderRootType.SOURCES);
      for (String source : sources) {
        String communityPath = StringUtil.trimEnd(source, "platform/lang-api/src");
        if (communityPath.length() < source.length()) {
          return communityPath;
        }
      }
    }
    return null;
  }

  public static RunContentDescriptor createConsole(@NotNull Project project, final String tabTitle) {
    TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
    ConsoleView consoleView = builder.getConsole();

    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    JComponent consoleComponent = new JPanel(new BorderLayout());

    JPanel toolbarPanel = new JPanel(new BorderLayout());
    toolbarPanel.add(ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false).getComponent());
    consoleComponent.add(toolbarPanel, BorderLayout.WEST);
    consoleComponent.add(consoleView.getComponent(), BorderLayout.CENTER);

    RunContentDescriptor descriptor = new RunContentDescriptor(consoleView, null, consoleComponent, tabTitle, null);

    Executor executor = DefaultRunExecutor.getRunExecutorInstance();
    for (AnAction action : consoleView.createConsoleActions()) {
      toolbarActions.add(action);
    }
    toolbarActions.add(new CloseAction(executor, descriptor, project));
    ExecutionManager.getInstance(project).getContentManager().showRunContent(executor, descriptor);
    consoleView.allowHeavyFilters();
    return descriptor;
  }
}
