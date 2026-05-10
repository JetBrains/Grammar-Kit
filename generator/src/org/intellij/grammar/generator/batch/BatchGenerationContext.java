/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.batch;

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
 * @param project                          the current project
 * @param bnfFiles                         the grammar files selected for generation, in selection order
 * @param rootMap                          maps each {@code .bnf} file to the VFS directory where the parser class
 *                                         will be written. Derived from {@link KnownAttribute#PARSER_OUTPUT_PATH}
 *                                         when set, otherwise from {@link KnownAttribute#PARSER_CLASS}'s package.
 *                                         A {@code null} value means the target directory could not be created.
 * @param psiRootMap                       maps each {@code .bnf} file to the VFS directory where PSI interface/class
 *                                         files will be written; from {@link KnownAttribute#PSI_OUTPUT_PATH}.
 *                                         {@code null} value means PSI output goes to the same directory as the parser.
 * @param elementTypeHolderRootMap         maps each {@code .bnf} file to the VFS directory where the Java
 *                                         {@code IElementType} holder will be written; from
 *                                         {@link KnownAttribute#ELEMENT_TYPE_HOLDER_OUTPUT_PATH}. {@code null} = use the
 *                                         generator's default (parser dir in Java mode, PSI dir in Kotlin mode).
 * @param syntaxElementTypeHolderRootMap   maps each {@code .bnf} file to the VFS directory where the Kotlin
 *                                         syntax-element-type holder will be written; from
 *                                         {@link KnownAttribute#SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH}. {@code null} =
 *                                         use the parser dir.
 * @param converterFactoryRootMap          maps each {@code .bnf} file to the VFS directory where the
 *                                         element-type converter factory will be written; from
 *                                         {@link KnownAttribute#ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH}. {@code null}
 *                                         = use the PSI dir.
 * @param packageMap                       maps each parser output directory to the Java package name the IDE's
 *                                         {@link PackageIndex} assigns to it; used as a prefix when the generator
 *                                         computes fully-qualified class names for nested source roots.
 */
public record BatchGenerationContext(
  @NotNull Project project,
  @NotNull List<VirtualFile> bnfFiles,
  @NotNull Map<VirtualFile, VirtualFile> rootMap,
  @NotNull Map<VirtualFile, VirtualFile> psiRootMap,
  @NotNull Map<VirtualFile, VirtualFile> elementTypeHolderRootMap,
  @NotNull Map<VirtualFile, VirtualFile> syntaxElementTypeHolderRootMap,
  @NotNull Map<VirtualFile, VirtualFile> converterFactoryRootMap,
  @NotNull Map<VirtualFile, String> packageMap
) {
}
