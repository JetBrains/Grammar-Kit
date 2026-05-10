/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.batch;

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
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.*;
import org.intellij.grammar.psi.BnfAttributes;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;

/**
 * Core generation logic: context preparation and the per-grammar generation loop.
 *
 * <p>This class has no dependency on IDE progress/task infrastructure. Threading, progress
 * reporting, and notifications are the caller's responsibility; generation events are delivered
 * through a {@link GenerationListener}.
 */
public class BnfGenerationService {

  public static @NotNull BatchGenerationResult generateInBatch(@NotNull BatchGenerationContext context,
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

  public static @NotNull SingleGrammarGenerationReport generateGrammar(@NotNull VirtualFile bnfVirtualFile,
                                                                       @NotNull BatchGenerationContext context) throws Exception {
    String sourcePath = bnfVirtualFile.isInLocalFileSystem()
                        ? FileUtil.toSystemDependentName(FileUtil.toCanonicalPath(bnfVirtualFile.getParent().getPath()))
                        : "";
    VirtualFile target = context.rootMap().get(bnfVirtualFile);
    if (target == null) {
      return SingleGrammarGenerationReport.notFound();
    }
    // Every output folder (parser, PSI, IElement-type holder, syntax-type holder, converter factory)
    // is registered in `targets` so the IDE refreshes each of them after generation.
    List<VirtualFile> targets = new ArrayList<>();
    File genDir = collectTarget(target, targets);
    collectOptionalTarget(context.psiRootMap().get(bnfVirtualFile), targets);
    collectOptionalTarget(context.elementTypeHolderRootMap().get(bnfVirtualFile), targets);
    collectOptionalTarget(context.syntaxElementTypeHolderRootMap().get(bnfVirtualFile), targets);
    collectOptionalTarget(context.converterFactoryRootMap().get(bnfVirtualFile), targets);

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
        Generator generator = createGenerator((BnfFile)bnfPsiFile, sourcePath, packagePrefix, files);
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

  /**
   * Returns the on-disk {@link File} for {@code vf} and registers it in {@code targetsCollector}
   * (deduped) so the IDE refreshes that directory after generation.
   */
  private static @NotNull File collectTarget(@NotNull VirtualFile vf, @NotNull List<VirtualFile> targetsCollector) {
    if (!targetsCollector.contains(vf)) targetsCollector.add(vf);
    return new File(VfsUtilCore.virtualToIoFile(vf).getAbsolutePath());
  }

  /** Nullable variant: returns {@code null} when {@code vf} is null, otherwise delegates to {@link #collectTarget}. */
  private static @Nullable File collectOptionalTarget(@Nullable VirtualFile vf, @NotNull List<VirtualFile> targetsCollector) {
    return vf == null ? null : collectTarget(vf, targetsCollector);
  }

  public static @NotNull Generator createGenerator(@NotNull BnfFile bnfFile,
                                                   @NotNull String sourcePath,
                                                   @NotNull String packagePrefix,
                                                   @NotNull List<? super File> files) {
    OutputOpener opener = (className, fileToOpen, myBnfFile) -> {
      files.add(fileToOpen);
      return OutputOpener.DEFAULT.openOutput(className, fileToOpen, myBnfFile);
    };
    BnfPathsResolution paths = BnfPaths.resolve(bnfFile);
    if (BnfAttributes.useSyntaxApi(bnfFile)) {
      return new KotlinParserGenerator(bnfFile, sourcePath, packagePrefix, opener, paths);
    }
    else {
      return new JavaParserGenerator(bnfFile, sourcePath, packagePrefix, opener, paths);
    }
  }

  @RequiresWriteLock
  public static @NotNull BatchGenerationContext prepareGenerationContext(@NotNull Project project, @NotNull List<VirtualFile> bnfFiles) {
    PackageIndex packageIndex = PackageIndex.getInstance(project);

    Map<VirtualFile, VirtualFile> rootMap = new LinkedHashMap<>();
    Map<VirtualFile, VirtualFile> psiRootMap = new LinkedHashMap<>();
    Map<VirtualFile, VirtualFile> etHolderMap = new LinkedHashMap<>();
    Map<VirtualFile, VirtualFile> syntaxHolderMap = new LinkedHashMap<>();
    Map<VirtualFile, VirtualFile> converterMap = new LinkedHashMap<>();
    Map<VirtualFile, String> packageMap = new LinkedHashMap<>();

    for (VirtualFile file : bnfFiles) {
      if (!file.isValid()) continue;

      PsiFile bnfFile = PsiManager.getInstance(project).findFile(file);
      if (!(bnfFile instanceof BnfFile bnf)) continue;

      // The cached resolution already applies the parser-class-package fallback, so
      // path(PARSER_OUTPUT_PATH) is non-null whenever the legacy code would have
      // produced a parser target. The only thing left is to materialize each Path
      // as a VirtualFile, creating the directory if it doesn't yet exist on disk.
      BnfPathsResolution paths = BnfPaths.resolve(bnf);
      VirtualFile target             = ensureDirectory(paths.path(KnownAttribute.PARSER_OUTPUT_PATH));
      VirtualFile psiTarget          = ensureDirectory(paths.path(KnownAttribute.PSI_OUTPUT_PATH));
      VirtualFile etHolderTarget     = ensureDirectory(paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
      VirtualFile syntaxHolderTarget = ensureDirectory(paths.path(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
      VirtualFile converterTarget    = ensureDirectory(paths.path(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH));

      rootMap.put(file, target);
      psiRootMap.put(file, psiTarget);
      etHolderMap.put(file, etHolderTarget);
      syntaxHolderMap.put(file, syntaxHolderTarget);
      converterMap.put(file, converterTarget);
      packageMap.put(target, StringUtil.notNullize(target != null ? packageIndex.getPackageNameByDirectory(target) : null));
    }
    return new BatchGenerationContext(project, bnfFiles, rootMap, psiRootMap,
                                      etHolderMap, syntaxHolderMap, converterMap, packageMap);
  }

  private static @Nullable VirtualFile ensureDirectory(@Nullable Path absolutePath) {
    if (absolutePath == null) return null;
    try {
      return com.intellij.openapi.vfs.VfsUtil.createDirectoryIfMissing(absolutePath.toString());
    }
    catch (IOException ex) {
      return null;
    }
  }

}
