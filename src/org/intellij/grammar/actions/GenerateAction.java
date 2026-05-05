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
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ExceptionUtil;
import com.intellij.util.concurrency.annotations.RequiresWriteLock;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.*;
import org.intellij.grammar.psi.BnfAttributes;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

import static org.intellij.grammar.actions.FileGeneratorUtil.getTargetDirectoryFor;
import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;

/**
 * IDE action that triggers parser/PSI generation from one or more {@code .bnf} grammar files.
 *
 * <p>Entry points:
 * <ul>
 *   <li>Via the IDE action system — enabled only when the selection contains at least one {@link BnfFile}.</li>
 *   <li>Programmatically via {@link #doGenerate(Project, List)}.</li>
 * </ul>
 *
 * <p>Generation pipeline:
 * <ol>
 *   <li><b>Prepare ({@link #prepareGenerationContext}) — write lock:</b>
 *       resolves output directories and package names for every grammar.
 *       The parser output root comes from {@link KnownAttribute#PARSER_CLASS};
 *       a separate PSI root is used when {@link KnownAttribute#PSI_OUTPUT_PATH} is set.
 *       A write lock is required here because target directories are created in the VFS if absent.</li>
 *   <li><b>Generate ({@link BatchGenerationTask}) — background thread, read lock:</b>
 *       runs each grammar through {@link #generateGrammar} under a smart-mode read action.
 *       A read lock suffices because the generator only <em>reads</em> PSI; all output is written
 *       via plain {@link java.io.File} I/O which is outside the VFS lock.
 *       A write lock would be wrong here — it must be acquired on the EDT and blocks all threads,
 *       which would freeze the IDE for the duration of generation.</li>
 *   <li><b>Refresh:</b> marks all output directories dirty so the VFS picks up new files.</li>
 * </ol>
 *
 * <p>Notifications: each grammar emits an {@link com.intellij.notification.Notification} in
 * {@link CommonBnfConstants#GENERATION_GROUP} with the number of bytes written. When more than
 * three grammars are processed in one batch, an extra summary notification is shown.
 *
 * @author gregory
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

    doGenerate(project, files);
  }

  /**
   * Programmatic entry point: generates parsers/PSI for the given {@code .bnf} files.
   * Preparation runs synchronously under a write lock; generation is then submitted as a
   * cancellable background task visible in the IDE progress bar.
   */
  public static void doGenerate(@NotNull Project project, @NotNull List<VirtualFile> bnfFiles) {
    var generationContext = WriteAction.compute(() -> prepareGenerationContext(project, bnfFiles));

    ProgressManager.getInstance().run(new BatchGenerationTask(project, generationContext));
  }

  /**
   * Pre-computed, immutable context passed from the write-lock preparation phase to the
   * background generation task.
   *
   * @param project    the current project
   * @param bnfFiles   the grammar files selected for generation, in selection order
   * @param rootMap    maps each {@code .bnf} file to the VFS directory where the parser class will
   *                   be written; derived from {@link KnownAttribute#PARSER_CLASS}.
   *                   A {@code null} value means the target directory could not be created.
   * @param psiRootMap maps each {@code .bnf} file to the VFS directory where PSI interface/class
   *                   files will be written; derived from {@link KnownAttribute#PSI_OUTPUT_PATH}.
   *                   {@code null} value means PSI output goes to the same directory as the parser.
   * @param packageMap maps each parser output directory to the Java package name the IDE's
   *                   {@link PackageIndex} assigns to it; used as a prefix when the generator
   *                   computes fully-qualified class names for nested source roots.
   */
  private record BatchGenerationContext(
    @NotNull Project project,
    @NotNull List<VirtualFile> bnfFiles,
    @NotNull Map<VirtualFile, VirtualFile> rootMap,
    @NotNull Map<VirtualFile, VirtualFile> psiRootMap,
    @NotNull Map<VirtualFile, String> packageMap
  ) {
  }

  /**
   * Immutable accumulator built up as grammars are processed one by one in the background task.
   *
   * @param bnfFiles       the original grammar file list; kept here only to decide whether to show
   *                       a batch-summary notification (shown only when more than 3 grammars were
   *                       selected — see {@link #showReportNotification})
   * @param project        the project; used when posting notifications
   * @param files          every {@link File} written by any generator in the batch, across all
   *                       grammars; used to compute {@code totalWritten} and the summary notification
   * @param targets        union of all VFS output directories touched during the batch
   *                       (parser root + optional PSI root for each grammar); passed to
   *                       {@link VfsUtil#markDirtyAndRefresh} at the end so the IDE picks up new files
   * @param filesProcessed number of grammars successfully processed so far
   * @param totalWritten   total bytes written across all generated files in the batch
   */
  private record BatchGenerationResult(
    @NotNull List<VirtualFile> bnfFiles,
    @NotNull Project project,
    @NotNull List<File> files,
    @NotNull Set<VirtualFile> targets,
    int filesProcessed,
    long totalWritten
  ) {
    static @NotNull BatchGenerationResult empty(@NotNull Project project, @NotNull List<VirtualFile> bnfFiles) {
      return new BatchGenerationResult(bnfFiles, project, List.of(), Set.of(), 0, 0);
    }

    public @NotNull BatchGenerationResult append(@NotNull SingleGrammarGenerationReport result) {
      var newTargets = new HashSet<>(targets);
      newTargets.addAll(result.targets);

      List<File> newFiles = ContainerUtil.concat(files, result.files);

      return new BatchGenerationResult(
        bnfFiles,
        project,
        newFiles,
        newTargets,
        filesProcessed + 1,
        totalWritten + result.bytesWritten
      );
    }
  }

  private static void showReportNotification(@NotNull BatchGenerationResult generationResult, long duration) {
    if (generationResult.bnfFiles.size() <= 3) {
      return;
    }

    String report = String.format("%d grammars: %d files generated (%s) in %s",
                                  generationResult.filesProcessed,
                                  generationResult.files.size(),
                                  StringUtil.formatFileSize(generationResult.totalWritten),
                                  StringUtil.formatDuration(duration));
    Notification notification = new Notification(CommonBnfConstants.GENERATION_GROUP, "", report, NotificationType.INFORMATION);
    Notifications.Bus.notify(notification, generationResult.project);
  }

  private static @NotNull BatchGenerationResult generateInBatchUnderProgress(@NotNull BatchGenerationContext generationContext,
                                                                             @NotNull ProgressIndicator indicator) {
    var result = BatchGenerationResult.empty(generationContext.project, generationContext.bnfFiles);

    for (int i = 0, l = generationContext.bnfFiles.size(); i < l; i++) {
      VirtualFile file = generationContext.bnfFiles.get(i);
      indicator.setFraction((double)i / l);
      indicator.setText2(file.getPath());

      try {
        SingleGrammarGenerationReport singleResult = generateGrammar(file, generationContext);
        if (singleResult.targetNotFound) {
          break;
        }
        result = result.append(singleResult);

        notifyGrammarGenerated(generationContext, file, singleResult);
      }
      catch (ProcessCanceledException ignored) {
      }
      catch (Exception ex) {
        reportException(ex, generationContext, file);
      }
    }
    return result;
  }

  private static void notifyGrammarGenerated(@NotNull BatchGenerationContext generationContext,
                                             @NotNull VirtualFile file,
                                             @NotNull SingleGrammarGenerationReport result) {
    String title = String.format("%s generated (%s)", file.getName(), StringUtil.formatFileSize(result.bytesWritten));
    String content = "to " + result.genDir + (result.duration() == null ? "" : " in " + result.duration());
    Notification notification = new Notification(CommonBnfConstants.GENERATION_GROUP, title, content, NotificationType.INFORMATION);
    Notifications.Bus.notify(notification, generationContext.project);
  }

  private static void reportException(@NotNull Exception ex,
                                      @NotNull BatchGenerationContext generationContext,
                                      @NotNull VirtualFile file) {
    String stackTrace = ExceptionUtil.getUserStackTrace(ex, JavaParserGenerator.LOG);
    Notification notification = new Notification(CommonBnfConstants.GENERATION_GROUP,
                                                 file.getName() + " generation failed",
                                                 stackTrace,
                                                 NotificationType.ERROR);
    Notifications.Bus.notify(notification, generationContext.project);
    LOG.warn(ex);
  }

  /**
   * Result of generating a single grammar file.
   *
   * @param targetNotFound {@code true} when the parser output directory from {@link BatchGenerationContext#rootMap}
   *                       was {@code null} — i.e., the target directory could not be created.
   *                       When this flag is set the batch loop stops immediately, because a missing
   *                       root directory usually means a project-wide misconfiguration.
   * @param targets        VFS directories that received generated files for this grammar:
   *                       always contains the parser root; also contains the PSI root when
   *                       {@link KnownAttribute#PSI_OUTPUT_PATH} is set and points to a different location.
   *                       These are merged into {@link BatchGenerationResult#targets} for the final VFS refresh.
   * @param files          {@link File} objects actually written by the generator; used for byte accounting
   * @param bytesWritten   total size of {@code files} after generation completes
   * @param duration       human-readable wall-clock time, or {@code null} when generation took under 1 second
   *                       (omitted from the per-grammar notification in that case)
   * @param genDir         the <em>parser-class</em> output directory (where {@code MyParser.java} /
   *                       {@code MyParser.kt} is written), as a {@link File}.
   *                       This is the {@link File} counterpart of {@code targets.get(0)} and is kept
   *                       separately only because {@link #notifyGrammarGenerated} needs a plain path
   *                       string for the "to &lt;dir&gt;" line in the notification.
   *                       The PSI output directory is not stored here — when it differs from the parser
   *                       directory it is present in {@code targets} as the second element.
   *                       Always non-null when {@code targetNotFound} is {@code false}.
   */
  private record SingleGrammarGenerationReport(
    boolean targetNotFound,
    @NotNull List<VirtualFile> targets,
    @NotNull List<File> files,
    long bytesWritten,
    @Nullable String duration,
    File genDir
  ) {
    static @NotNull SingleGrammarGenerationReport notFound() {
      return new SingleGrammarGenerationReport(true, List.of(), List.of(), 0, null, null);
    }
  }

  private static @NotNull SingleGrammarGenerationReport generateGrammar(@NotNull VirtualFile bnfVirtualFile,
                                                                        @NotNull BatchGenerationContext generationContext) throws Exception {
    String sourcePath = bnfVirtualFile.isInLocalFileSystem()
                        ? FileUtil.toSystemDependentName(FileUtil.toCanonicalPath(bnfVirtualFile.getParent().getPath()))
                        : "";
    VirtualFile target = generationContext.rootMap.get(bnfVirtualFile);
    if (target == null) {
      return SingleGrammarGenerationReport.notFound();
    }
    List<VirtualFile> targets = new ArrayList<>();
    targets.add(target);

    File genDir = new File(VfsUtilCore.virtualToIoFile(target).getAbsolutePath());
    VirtualFile psiTarget = generationContext.psiRootMap.get(bnfVirtualFile);
    File psiGenDir;
    if (psiTarget != null) {
      psiGenDir = new File(VfsUtilCore.virtualToIoFile(psiTarget).getAbsolutePath());
      targets.add(psiTarget);
    }
    else {
      psiGenDir = genDir;
    }

    String packagePrefix = generationContext.packageMap.get(target);
    List<File> files = new ArrayList<>();
    Ref<Exception> exRef = Ref.create();
    long time = System.currentTimeMillis();
    DumbService.getInstance(generationContext.project).runReadActionInSmartMode(() -> {
      if (!bnfVirtualFile.isValid()) return;
      PsiManager psiManager = PsiManager.getInstance(generationContext.project);
      PsiFile bnfPsiFile = psiManager.findFile(bnfVirtualFile);
      if (!(bnfPsiFile instanceof BnfFile)) return;
      try {
        Generator generator = createGenerator((BnfFile)bnfPsiFile, sourcePath, genDir, psiGenDir, packagePrefix, files);
        generator.generate();
      }
      catch (Exception ex) {
        exRef.set(ex);
      }
    });

    if (!exRef.isNull()) throw exRef.get();
    long millis = System.currentTimeMillis() - time;
    String duration = millis < 1000 ? null : StringUtil.formatDuration(millis);
    long bytesWritten = files.stream().mapToLong(File::length).sum();
    return new SingleGrammarGenerationReport(false, targets, files, bytesWritten, duration, genDir);
  }

  private static @NotNull Generator createGenerator(@NotNull BnfFile bnfFile,
                                                    @NotNull String sourcePath,
                                                    @NotNull File genDir,
                                                    @NotNull File psiGenDir,
                                                    @NotNull String packagePrefix,
                                                    @NotNull List<? super File> files) {
    OutputOpener opener = (className, fileToOpen, myBnfFile) -> {
      files.add(fileToOpen);
      return OutputOpener.DEFAULT.openOutput(className, fileToOpen, myBnfFile);
    };
    if (BnfAttributes.useSyntaxApi(bnfFile)) {
      return new KotlinParserGenerator(bnfFile, sourcePath, genDir.getPath(), psiGenDir.getPath(), packagePrefix, opener);
    }
    else {
      return new JavaParserGenerator(bnfFile, sourcePath, genDir.getPath(), packagePrefix, opener);
    }
  }

  @RequiresWriteLock
  private static @NotNull BatchGenerationContext prepareGenerationContext(@NotNull Project project, @NotNull List<VirtualFile> bnfFiles) {
    PackageIndex packageIndex = PackageIndex.getInstance(project);

    Map<VirtualFile, VirtualFile> rootMap = new LinkedHashMap<>();
    Map<VirtualFile, VirtualFile> psiRootMap = new LinkedHashMap<>();
    Map<VirtualFile, String> packageMap = new LinkedHashMap<>();

    for (VirtualFile file : bnfFiles) {
      if (!file.isValid()) continue;

      PsiFile bnfFile = PsiManager.getInstance(project).findFile(file);
      if (!(bnfFile instanceof BnfFile)) continue;

      String parserClass = getRootAttribute(bnfFile, KnownAttribute.PARSER_CLASS);
      VirtualFile target = getTargetDirectoryFor(project, file, StringUtil.getShortName(parserClass) + ".java", StringUtil.getPackageName(parserClass), true);
      String psiOutput = getRootAttribute(bnfFile, KnownAttribute.PSI_OUTPUT_PATH);

      VirtualFile psiTarget = psiOutput.isEmpty() ? null : FileGeneratorUtil.getTargetDirectoryRelativeToContentRoot(file, project, psiOutput, "", true);
      rootMap.put(file, target);
      psiRootMap.put(file, psiTarget);
      packageMap.put(target, StringUtil.notNullize(packageIndex.getPackageNameByDirectory(target)));
    }
    return new BatchGenerationContext(project, bnfFiles, rootMap, psiRootMap, packageMap);
  }

  private static class BatchGenerationTask extends Task.Backgroundable {
    private final BatchGenerationContext myGenerationContext;

    BatchGenerationTask(@NotNull Project project, BatchGenerationContext generationContext) {
      super(project, "Parser generation", true, ALWAYS_BACKGROUND);
      myGenerationContext = generationContext;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
      indicator.setIndeterminate(true);

      long startTime = System.currentTimeMillis();
      var generationResult = generateInBatchUnderProgress(myGenerationContext, indicator);
      long duration = System.currentTimeMillis() - startTime;

      showReportNotification(generationResult, duration);

      VfsUtil.markDirtyAndRefresh(true, true, true, generationResult.targets.toArray(VirtualFile.EMPTY_ARRAY));
    }
  }
}
