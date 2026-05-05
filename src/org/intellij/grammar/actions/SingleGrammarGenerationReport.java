/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Result of generating a single grammar file.
 *
 * @param targetNotFound {@code true} when the parser output directory could not be created;
 *                       when set the batch loop stops immediately.
 * @param targets        VFS directories that received generated files for this grammar
 * @param files          {@link File} objects actually written by the generator
 * @param bytesWritten   total size of {@code files} after generation completes
 * @param duration       human-readable wall-clock time, or {@code null} when under 1 second
 * @param genDir         the parser-class output directory
 */
public record SingleGrammarGenerationReport(
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
