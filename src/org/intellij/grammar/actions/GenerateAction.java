/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ExceptionUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.intellij.grammar.actions.FileGeneratorUtil.getTargetDirectoryFor;
import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

/**
 * @author gregory
 *         Date: 15.07.11 17:12
 */
public class GenerateAction extends AnAction {

  public static final NotificationGroup LOG_GROUP = NotificationGroup.logOnlyGroup("Parser Generator Log");
  
  private static final Logger LOG = Logger.getInstance("org.intellij.grammar.actions.GenerateAction");

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

    doGenerate(project, files);
  }

  private static @NotNull JBIterable<VirtualFile> getFiles(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    JBIterable<VirtualFile> files = JBIterable.of(e.getData(LangDataKeys.VIRTUAL_FILE_ARRAY));
    if (project == null || files.isEmpty()) return JBIterable.empty();
    PsiManager manager = PsiManager.getInstance(project);
    return files.filter(o -> manager.findFile(o) instanceof BnfFile);
  }

  public static void doGenerate(@NotNull Project project, @NotNull List<VirtualFile> bnfFiles) {
    Map<VirtualFile, VirtualFile> rootMap = new LinkedHashMap<>();
    Map<VirtualFile, String> packageMap = new LinkedHashMap<>();
    PsiManager psiManager = PsiManager.getInstance(project);
    ProjectFileIndex fileIndex = ProjectFileIndex.SERVICE.getInstance(project);
    WriteAction.run(() -> {
      for (VirtualFile file : bnfFiles) {
        if (!file.isValid()) continue;
        PsiFile bnfFile = psiManager.findFile(file);
        if (!(bnfFile instanceof BnfFile)) continue;
        String parserClass = getRootAttribute(bnfFile, KnownAttribute.PARSER_CLASS);
        VirtualFile target =
          getTargetDirectoryFor(project, file,
                                StringUtil.getShortName(parserClass) + ".java",
                                StringUtil.getPackageName(parserClass), true);
        rootMap.put(file, target);
        packageMap.put(target, StringUtil.notNullize(fileIndex.getPackageNameByDirectory(target)));
      }
    });

    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Parser generation", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {

      final List<File> files = new ArrayList<>();
      final Set<VirtualFile> targets = new LinkedHashSet<>();
      int filesProcessed = 0;
      long totalWritten = 0;

      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        long startTime = System.currentTimeMillis();
        indicator.setIndeterminate(true);
        try {
          runInner(indicator);
        }
        finally {
          String report = String.format("%d grammars: %d files generated (%s) in %s",
                                        filesProcessed,
                                        files.size(),
                                        StringUtil.formatFileSize(totalWritten),
                                        StringUtil.formatDuration(System.currentTimeMillis() - startTime));
          if (bnfFiles.size() > 3) {
            Notifications.Bus.notify(new Notification(
              BnfConstants.GENERATION_GROUP,
              "", report, NotificationType.INFORMATION), project);
          }
          VfsUtil.markDirtyAndRefresh(true, true, true, targets.toArray(VirtualFile.EMPTY_ARRAY));
        }
      }

      private void runInner(ProgressIndicator indicator) {
        for (int i = 0, l = bnfFiles.size(); i < l; i++) {
          VirtualFile file = bnfFiles.get(i);
          indicator.setFraction((double)i / l);
          indicator.setText2(file.getPath());
          String sourcePath = FileUtil.toSystemDependentName(FileUtil.toCanonicalPath(file.getParent().getPath()));
          VirtualFile target = rootMap.get(file);
          if (target == null) return;
          targets.add(target);
          File genDir = new File(VfsUtil.virtualToIoFile(target).getAbsolutePath());
          String packagePrefix = packageMap.get(target);
          long time = System.currentTimeMillis();
          int filesCount = files.size();
          Ref<Exception> exRef = Ref.create();
          try {
            DumbService.getInstance(project).runReadActionInSmartMode(() -> {
              if (!file.isValid()) return;
              PsiFile bnfFile = psiManager.findFile(file);
              if (!(bnfFile instanceof BnfFile)) return;
              ParserGenerator generator = new ParserGenerator((BnfFile)bnfFile, sourcePath, genDir.getPath(), packagePrefix) {
                @Override
                protected PrintWriter openOutputInner(String className, File file) throws IOException {
                  files.add(file);
                  return super.openOutputInner(className, file);
                }
              };
              try {
                generator.generate();
              }
              catch (Exception ex) {
                exRef.set(ex);
              }
            });
            if (!exRef.isNull()) throw exRef.get();
            long millis = System.currentTimeMillis() - time;
            String duration = millis < 1000 ? null : StringUtil.formatDuration(millis);
            long written = 0;
            for (File f : files.subList(filesCount, files.size())) {
              written += f.length();
            }
            filesProcessed ++;
            totalWritten += written;
            Notifications.Bus.notify(new Notification(
              BnfConstants.GENERATION_GROUP,
              String.format("%s generated (%s)", file.getName(), StringUtil.formatFileSize(written)),
              "to " + genDir + (duration == null ? "" : " in " + duration), NotificationType.INFORMATION), project);
          }
          catch (ProcessCanceledException ignored) {
          }
          catch (Exception ex) {
            Notifications.Bus.notify(new Notification(
              BnfConstants.GENERATION_GROUP,
              file.getName() + " generation failed",
              ExceptionUtil.getUserStackTrace(ex, ParserGenerator.LOG), NotificationType.ERROR), project);
            LOG.warn(ex);
          }
        }

      }
    });
  }
}
