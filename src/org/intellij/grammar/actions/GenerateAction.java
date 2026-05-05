/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;
import com.intellij.util.ExceptionUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.generator.CommonBnfConstants;
import org.intellij.grammar.generator.JavaParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * IDE action that triggers parser/PSI generation from one or more {@code .bnf} grammar files.
 * Generation logic lives in {@link BnfGenerationService}; this class wires it to the IDE action
 * system, drives the progress bar, and posts notifications — all via {@link BatchGenerationTask}.
 */
public class GenerateAction extends AnAction {
  private static final Logger LOG = Logger.getInstance(GenerateAction.class);

  private static @NotNull JBIterable<VirtualFile> getFiles(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    JBIterable<VirtualFile> files = JBIterable.of(e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY));
    if (project == null || files.isEmpty()) return JBIterable.empty();
    PsiManager manager = PsiManager.getInstance(project);
    return files.filter(o -> manager.findFile(o) instanceof BnfFile);
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    JBIterable<VirtualFile> files = getFiles(e);
    e.getPresentation().setEnabledAndVisible(project != null && !files.isEmpty());
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project == null) return;
    PsiDocumentManager.getInstance(project).commitAllDocuments();
    FileDocumentManager.getInstance().saveAllDocuments();

    List<VirtualFile> files = getFiles(e).toList();
    if (files.isEmpty()) return;

    var context = WriteAction.compute(() -> BnfGenerationService.prepareGenerationContext(project, files));
    ProgressManager.getInstance().run(new BatchGenerationTask(project, context));
  }

  private static class BatchGenerationTask extends Task.Backgroundable {

    private final BatchGenerationContext myContext;

    BatchGenerationTask(@NotNull Project project, @NotNull BatchGenerationContext context) {
      super(project, "Parser generation", true, ALWAYS_BACKGROUND);
      myContext = context;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
      indicator.setIndeterminate(true);

      long start = System.currentTimeMillis();
      var result = BnfGenerationService.generateInBatch(myContext, new GenerationListener() {
        @Override
        public void onGrammarStarted(@NotNull VirtualFile file, int index, int total) {
          indicator.setFraction((double)index / total);
          indicator.setText2(file.getPath());
        }

        @Override
        public void onGrammarGenerated(@NotNull VirtualFile file,
                                       @NotNull SingleGrammarGenerationReport report) {
          String title = String.format("%s generated (%s)", file.getName(), StringUtil.formatFileSize(report.bytesWritten()));
          String content = "to " + report.genDir() + (report.duration() == null ? "" : " in " + report.duration());
          showNotification(new Notification(CommonBnfConstants.GENERATION_GROUP, title, content, NotificationType.INFORMATION));
        }

        @Override
        public void onGenerationFailed(@NotNull VirtualFile file, @NotNull Exception ex) {
          String stackTrace = ExceptionUtil.getUserStackTrace(ex, JavaParserGenerator.LOG);
          showNotification(new Notification(CommonBnfConstants.GENERATION_GROUP,
                                            file.getName() + " generation failed",
                                            stackTrace,
                                            NotificationType.ERROR));
          LOG.warn(ex);
        }
      });
      onBatchCompleted(result, System.currentTimeMillis() - start);
      VfsUtil.markDirtyAndRefresh(true, true, true, result.targets().toArray(VirtualFile.EMPTY_ARRAY));
    }

    private void onBatchCompleted(@NotNull BatchGenerationResult result, long duration) {
      if (result.bnfFiles().size() <= 3) return;

      String report = String.format("%d grammars: %d files generated (%s) in %s",
                                    result.filesProcessed(),
                                    result.files().size(),
                                    StringUtil.formatFileSize(result.totalWritten()),
                                    StringUtil.formatDuration(duration));
      showNotification(new Notification(CommonBnfConstants.GENERATION_GROUP, "", report, NotificationType.INFORMATION));
    }

    private void showNotification(@NotNull Notification notification) {
      Notifications.Bus.notify(notification, getProject());
    }
  }
}
