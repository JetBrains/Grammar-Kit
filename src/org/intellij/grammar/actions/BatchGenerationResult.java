/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable accumulator built up as grammars are processed one by one.
 *
 * @param bnfFiles       the original grammar file list
 * @param project        the project
 * @param files          every {@link File} written by any generator in the batch
 * @param targets        union of all VFS output directories touched during the batch
 * @param filesProcessed number of grammars successfully processed so far
 * @param totalWritten   total bytes written across all generated files in the batch
 */
public record BatchGenerationResult(
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
    newTargets.addAll(result.targets());

    List<File> newFiles = ContainerUtil.concat(files, result.files());

    return new BatchGenerationResult(
      bnfFiles,
      project,
      newFiles,
      newTargets,
      filesProcessed + 1,
      totalWritten + result.bytesWritten()
    );
  }
}
