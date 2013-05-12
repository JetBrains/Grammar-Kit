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
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SimpleJavaSdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.ApplicationLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.Consumer;
import com.intellij.util.SystemProperties;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import org.intellij.grammar.generator.BnfConstants;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author greg
 */
public class BnfRunJFlexAction extends AnAction {

  private static final String[] JETBRAINS_JFLEX_URLS = {
      "https://github.com/JetBrains/intellij-community/raw/master/tools/lexer/jflex-1.4/lib/JFlex.jar",
      "https://raw.github.com/JetBrains/intellij-community/master/tools/lexer/idea-flex.skeleton"
  };

  @Override
  public void update(AnActionEvent e) {
    PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    boolean enabled = psiFile != null && !psiFile.getFileType().isBinary() &&
        psiFile.getName().endsWith(".flex");
    e.getPresentation().setEnabledAndVisible(enabled);
  }

  @Override
  public void actionPerformed(final AnActionEvent e) {
    final PsiFile psiFile = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    final VirtualFile virtualFile = psiFile != null ? psiFile.getVirtualFile() : null;
    if (virtualFile == null) return;

    FileDocumentManager.getInstance().saveAllDocuments();

    final Project project = psiFile.getProject();
    final String commandName = e.getPresentation().getText();
    final PsiDirectory directory = PsiManager.getInstance(project).findDirectory(virtualFile.getParent());
    if (directory == null) return;

    Matcher matcher = Pattern.compile("%class (\\w+)").matcher(psiFile.getText());
    if (!matcher.find()) return;
    final String lexerClassName = matcher.group(1);

    try {
      List<File> jflex = getOrDownload(project, "JetBrains JFlex", JETBRAINS_JFLEX_URLS);
      if (jflex.isEmpty()) return;

      SimpleJavaParameters javaParameters = new SimpleJavaParameters();
      Sdk sdk = new SimpleJavaSdkType().createJdk("tmp", SystemProperties.getJavaHome());
      javaParameters.setJdk(sdk);
      javaParameters.getClassPath().add(jflex.get(0));
      javaParameters.setMainClass("JFlex.Main");
      javaParameters.getVMParametersList().add("-Xmx512m");
      javaParameters.getProgramParametersList().add("-sliceandcharat");
      javaParameters.getProgramParametersList().add("-skel", jflex.get(1).getAbsolutePath());
      javaParameters.getProgramParametersList().add("-d", VfsUtil.virtualToIoFile(virtualFile.getParent()).getAbsolutePath());
      javaParameters.getProgramParametersList().add(VfsUtil.virtualToIoFile(virtualFile).getAbsolutePath());

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
                ensureLexerClassCreated(directory, lexerClassName, commandName);
              }
            }, directory.getProject().getDisposed());
          }
        }
      });
      processHandler.startNotify();
    }
    catch (ExecutionException ex) {
      Messages.showErrorDialog(project, "Unable to run JFlex"+ "\n" + ex.getLocalizedMessage(), commandName);
    }
  }

  private void ensureLexerClassCreated(final PsiDirectory directory, final String lexerClassName, String commandName) {
    LocalFileSystem.getInstance().refreshFiles(Arrays.asList(directory.getVirtualFile()));
    final String className = lexerClassName.startsWith("_") ? lexerClassName.substring(1) : lexerClassName + "Adapter";
    if (directory.findFile(className + ".java") != null) return;

    final Project project = directory.getProject();
    BnfGenerateParserUtilAction.createClass(className, directory, "com.intellij.lexer.FlexAdapter", commandName, new Consumer<PsiClass>() {
      @Override
      public void consume(PsiClass aClass) {
        PsiMethod constructor = JavaPsiFacade.getElementFactory(project).createMethodFromText(
            "public " + className + "() {\n" +
            "  super(new " + lexerClassName + "());\n" +
            "}\n", aClass);
        aClass.addAfter(constructor, aClass.getLBrace());

        Notifications.Bus.notify(new Notification(BnfConstants.GENERATION_GROUP,
            aClass.getName() + " lexer class generated", "to " + directory.getVirtualFile().getPath() +
            "\n<br>Use this class in your ParserDefinition implementation." +
            "\n<br>For complex cases consider employing com.intellij.lexer.LookAheadLexer API.",
            NotificationType.INFORMATION), project);
      }
    });
  }

  private static List<File> getOrDownload(Project project, final String libraryName, String... urls) {
    final ArrayList<File> result = new ArrayList<File>(urls.length);
    final Library[] library = {ApplicationLibraryTable.getApplicationTable().getLibraryByName(libraryName)};
    if (library[0] != null) {
      main: for (int i = 0; i < urls.length; i++) {
        String url = urls[i];
        String name = url.substring(url.lastIndexOf("/") + 1);

        for (VirtualFile file : library[0].getFiles(OrderRootType.CLASSES)) {
          if (file.getName().equals(name)) {
            result.add(VfsUtil.virtualToIoFile(file));
            continue main;
          }
        }
        if(result.size() < i) break;
      }
      if (result.size() == urls.length) return result;
    }
    DownloadableFileService service = DownloadableFileService.getInstance();
    ArrayList<DownloadableFileDescription> descriptions = new ArrayList<DownloadableFileDescription>();
    for (String url : urls) {
      descriptions.add(service.createFileDescription(url, url.substring(url.lastIndexOf("/") + 1)));
    }
    final List<Pair<VirtualFile,DownloadableFileDescription>> pairs = service.createDownloader(descriptions, project, null, libraryName).downloadAndReturnWithDescriptions();
    if (pairs == null) return Collections.emptyList();

    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        if (library[0] == null) {
          LibraryTable.ModifiableModel modifiableModel = ApplicationLibraryTable.getApplicationTable().getModifiableModel();
          library[0] = modifiableModel.createLibrary(libraryName);
          modifiableModel.commit();
        }
        result.clear();
        Library.ModifiableModel modifiableModel = library[0].getModifiableModel();
        for (Pair<VirtualFile, DownloadableFileDescription> pair : pairs) {
          modifiableModel.addRoot(pair.first, OrderRootType.CLASSES);
          result.add(VfsUtil.virtualToIoFile(pair.first));
        }
        modifiableModel.commit();

      }
    });
    return result;
  }

  public static RunContentDescriptor createConsole(Project project, final String tabTitle) {
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
