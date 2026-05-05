/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.grammar.KnownAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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
record BatchGenerationContext(
  @NotNull Project project,
  @NotNull List<VirtualFile> bnfFiles,
  @NotNull Map<VirtualFile, VirtualFile> rootMap,
  @NotNull Map<VirtualFile, VirtualFile> psiRootMap,
  @NotNull Map<VirtualFile, String> packageMap
) {
}
