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

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.changes.BackgroundFromStartOption;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ExceptionUtil;
import com.intellij.util.Function;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.intellij.grammar.actions.FileGeneratorUtil.getTargetDirectoryFor;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

/**
 * @author gregory
 *         Date: 15.07.11 17:12
 */
public class GenerateAction extends AnAction implements DumbAware {

  public static final NotificationGroup LOG_GROUP = NotificationGroup.logOnlyGroup("Parser Generator Log");
  
  private static final Logger LOG = Logger.getInstance("org.intellij.grammar.actions.GenerateAction");

  @Override
  public void update(@NotNull AnActionEvent e) {
    List<BnfFile> bnfFiles = getFiles(e);
    boolean enabled = !bnfFiles.isEmpty();
    e.getPresentation().setEnabled(enabled);
    e.getPresentation().setVisible(enabled);
  }

  private static List<BnfFile> getFiles(AnActionEvent e) {
    Project project = getEventProject(e);
    VirtualFile[] files = LangDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
    if (project == null || files == null) return Collections.emptyList();
    final PsiManager manager = PsiManager.getInstance(project);
    return ContainerUtil.mapNotNull(files, new Function<VirtualFile, BnfFile>() {
      @Override
      public BnfFile fun(VirtualFile file) {
        PsiFile psiFile = manager.findFile(file);
        return psiFile instanceof BnfFile ? (BnfFile)psiFile : null;
      }
    });
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final Project project = getEventProject(e);
    final List<BnfFile> bnfFiles = getFiles(e);
    if (project == null || bnfFiles.isEmpty()) return;
    PsiDocumentManager.getInstance(project).commitAllDocuments();
    FileDocumentManager.getInstance().saveAllDocuments();

    final Map<BnfFile, VirtualFile> rootMap = ContainerUtil.newLinkedHashMap();
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        for (BnfFile file : bnfFiles) {
          String parserClass = getRootAttribute(file, KnownAttribute.PARSER_CLASS);
          VirtualFile target =
            getTargetDirectoryFor(project, file.getVirtualFile(),
                                  StringUtil.getShortName(parserClass) + ".java",
                                  StringUtil.getPackageName(parserClass), true);
          rootMap.put(file, target);
        }
      }
    });

    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Parser Generation", true, new BackgroundFromStartOption()) {

      Set<File> files = ContainerUtil.newLinkedHashSet();

      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        try {
          runInner();
        }
        finally {
          LocalFileSystem.getInstance().refreshIoFiles(files, true, false, new Runnable() {
            @Override
            public void run() {
              //System.out.println(System.currentTimeMillis() + ": refreshed!");
            }
          });
        }
      }

      private void runInner() {
        for (final BnfFile file : bnfFiles) {
          final String sourcePath = FileUtil.toSystemDependentName(PathUtil.getCanonicalPath(
            file.getVirtualFile().getParent().getPath()));
          VirtualFile target = rootMap.get(file);
          if (target == null) return;
          final File genDir = new File(VfsUtil.virtualToIoFile(target).getAbsolutePath());
          try {
            ApplicationManager.getApplication().runReadAction(new ThrowableComputable<Boolean, Exception>() {
              @Override
              public Boolean compute() throws Exception {
                new ParserGenerator(file, sourcePath, genDir.getPath()) {
                  @Override
                  protected PrintWriter openOutputInner(File file) throws IOException {
                    files.add(file);
                    return super.openOutputInner(file);
                  }
                }.generate();
                return true;
              }
            });

            Notifications.Bus.notify(new Notification(BnfConstants.GENERATION_GROUP,
                                                      file.getName() + " parser generated", "to " + genDir,
                                                      NotificationType.INFORMATION), project);
          }
          catch (Exception ex) {
            Notifications.Bus.notify(new Notification(BnfConstants.GENERATION_GROUP,
                                                      file.getName() + " parser generation failed",
                                                      ExceptionUtil.getUserStackTrace(ex, ParserGenerator.LOG),
                                                      NotificationType.ERROR), project);
            LOG.warn(ex);
          }
        }

      }
    });
  }
}
