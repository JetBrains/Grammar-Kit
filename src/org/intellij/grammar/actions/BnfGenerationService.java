/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.concurrency.annotations.RequiresWriteLock;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.*;
import org.intellij.grammar.psi.BnfAttributes;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

import static org.intellij.grammar.actions.FileGeneratorUtil.getTargetDirectoryFor;
import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;

/**
 * Core generation logic: context preparation and the per-grammar generation loop.
 *
 * <p>This class has no dependency on IDE progress/task infrastructure. Threading, progress
 * reporting, and notifications are the caller's responsibility; generation events are delivered
 * through a {@link GenerationListener}.
 */
public class BnfGenerationService {

  static @NotNull BatchGenerationResult generateInBatch(@NotNull BatchGenerationContext context,
                                                        @NotNull GenerationListener listener) {
    var result = BatchGenerationResult.empty(context.project(), context.bnfFiles());
    int total = context.bnfFiles().size();

    for (int i = 0; i < total; i++) {
      VirtualFile file = context.bnfFiles().get(i);
      listener.onGrammarStarted(file, i, total);

      try {
        SingleGrammarGenerationReport singleResult = generateGrammar(file, context);
        if (singleResult.targetNotFound()) {
          break;
        }
        result = result.append(singleResult);
        listener.onGrammarGenerated(file, singleResult);
      }
      catch (ProcessCanceledException ignored) {
      }
      catch (Exception ex) {
        listener.onGenerationFailed(file, ex);
      }
    }
    return result;
  }

  static @NotNull SingleGrammarGenerationReport generateGrammar(@NotNull VirtualFile bnfVirtualFile,
                                                                @NotNull BatchGenerationContext context) throws Exception {
    String sourcePath = bnfVirtualFile.isInLocalFileSystem()
                        ? FileUtil.toSystemDependentName(FileUtil.toCanonicalPath(bnfVirtualFile.getParent().getPath()))
                        : "";
    VirtualFile target = context.rootMap().get(bnfVirtualFile);
    if (target == null) {
      return SingleGrammarGenerationReport.notFound();
    }
    List<VirtualFile> targets = new ArrayList<>();
    targets.add(target);

    File genDir = new File(VfsUtilCore.virtualToIoFile(target).getAbsolutePath());
    VirtualFile psiTarget = context.psiRootMap().get(bnfVirtualFile);
    File psiGenDir;
    if (psiTarget != null) {
      psiGenDir = new File(VfsUtilCore.virtualToIoFile(psiTarget).getAbsolutePath());
      targets.add(psiTarget);
    }
    else {
      psiGenDir = genDir;
    }

    String packagePrefix = context.packageMap().get(target);
    List<File> files = new ArrayList<>();
    Ref<Exception> exRef = Ref.create();
    long time = System.currentTimeMillis();
    DumbService.getInstance(context.project()).runReadActionInSmartMode(() -> {
      if (!bnfVirtualFile.isValid()) return;
      PsiManager psiManager = PsiManager.getInstance(context.project());
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

  static @NotNull Generator createGenerator(@NotNull BnfFile bnfFile,
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
  static @NotNull BatchGenerationContext prepareGenerationContext(@NotNull Project project, @NotNull List<VirtualFile> bnfFiles) {
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
}
