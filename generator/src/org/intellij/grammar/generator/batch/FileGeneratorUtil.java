/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.batch;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.config.Options;
import org.intellij.grammar.generator.CommonBnfConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.intellij.util.ArrayUtil.getFirstElement;

/**
 * @author gregsh
 */
public class FileGeneratorUtil {
  /**
   * Resolves the output directory for a file to be generated.
   *
   * <p>The resolution follows these steps:
   * <ol>
   *   <li><b>Anchor on an existing file.</b> If {@code targetFile} is provided, the project index
   *       is searched for a file with that name. Candidates are ranked by preferring source roots
   *       over content roots, then by shorter path. The first candidate whose parent directory
   *       belongs to {@code targetPackage} (or any directory if {@code targetPackage} is empty)
   *       is used as the anchor. Its source/content root becomes the base for generation.</li>
   *   <li><b>Fall back to the source file's root.</b> If no existing file is found, the root is
   *       derived from {@code sourceFile}:
   *       <ul>
   *         <li>For {@code .bnf} and {@code .jflex} files the content root is preferred, because
   *             generated sources are expected to land in a separate "gen" subtree rather than
   *             next to the grammar.</li>
   *         <li>For other file types with a non-empty {@code targetPackage} the source root of
   *             {@code sourceFile} is preferred.</li>
   *         <li>If neither applies, the first available content root is used.</li>
   *       </ul>
   *   </li>
   *   <li><b>Append the package path.</b> Inside the resolved root, subdirectories matching
   *       {@code targetPackage} are created if absent. When the root is not already a registered
   *       source root (i.e., the "gen" root scenario), the configured {@link Options#GEN_DIR}
   *       folder is prepended to the path.</li>
   * </ol>
   *
   * @param project       the current project
   * @param sourceFile    the grammar or source file that triggers generation; used to locate a
   *                      fallback root when no existing target file is found
   * @param targetFile    short file name (without path) to look up in the project index as an
   *                      anchor; may be {@code null} to skip the lookup
   * @param targetPackage fully qualified package name for the generated file (e.g.
   *                      {@code "com.example.psi"}); may be {@code null} or empty when the file
   *                      should go directly into the root directory
   * @param returnRoot    if {@code true}, returns the generation root directory (the source root
   *                      or the {@link Options#GEN_DIR} child of the content root) rather than
   *                      the deepest package subdirectory
   * @return the {@link VirtualFile} directory where the generated file should be written;
   *         created if it does not exist yet
   * @throws ProcessCanceledException if no suitable root can be determined, or if directory
   *                                  creation fails; an error notification is shown to the user
   */
  public static @NotNull VirtualFile getTargetDirectoryFor(@NotNull Project project,
                                                           @NotNull VirtualFile sourceFile,
                                                           @Nullable String targetFile,
                                                           @Nullable String targetPackage,
                                                           boolean returnRoot,
                                                           boolean preferGenRoot
  ) {
    boolean hasPackage = StringUtil.isNotEmpty(targetPackage);
    ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
    PackageIndex packageIndex = PackageIndex.getInstance(project);
    ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);

    VirtualFile existingFile = findExistingFile(project, targetFile, targetPackage, fileIndex, packageIndex);

    VirtualFile existingFileRoot =
      existingFile == null ? null :
      fileIndex.isInSourceContent(existingFile) ? fileIndex.getSourceRootForFile(existingFile) :
      fileIndex.isInContent(existingFile) ? fileIndex.getContentRootForFile(existingFile) : null;

    boolean preferSourceRoot = hasPackage && !preferGenRoot;
    VirtualFile[] sourceRoots = rootManager.getContentSourceRoots();
    VirtualFile[] contentRoots = rootManager.getContentRoots();
    VirtualFile virtualRoot = existingFileRoot != null ? existingFileRoot :
                                    preferSourceRoot && fileIndex.isInSource(sourceFile) ? fileIndex.getSourceRootForFile(sourceFile) :
                                    fileIndex.isInContent(sourceFile) ? fileIndex.getContentRootForFile(sourceFile) :
                                    getFirstElement(preferSourceRoot && sourceRoots.length > 0? sourceRoots : contentRoots);
    if (virtualRoot == null) {
      fail(project, sourceFile, "Unable to guess target source root");
      throw new ProcessCanceledException();
    }
    try {
      return createOutputDirectory(virtualRoot, targetPackage, packageIndex, fileIndex, returnRoot);
    }
    catch (ProcessCanceledException ex) {
      throw ex;
    }
    catch (Exception ex) {
      fail(project, sourceFile, ex.getMessage());
      throw new ProcessCanceledException();
    }
  }

  private static @Nullable VirtualFile findExistingFile(@NotNull Project project,
                                                        @Nullable String targetFile,
                                                        @Nullable String targetPackage,
                                                        ProjectFileIndex fileIndex,
                                                        PackageIndex packageIndex) {
    if (targetFile == null) {
      return null;
    }

    Collection<VirtualFile> fromIndex = FilenameIndex.getVirtualFilesByName(targetFile, ProjectScope.getProjectScope(project));
    List<VirtualFile> files = new ArrayList<>(fromIndex);
    files.sort((f1, f2) -> {
      boolean b1 = fileIndex.isInSource(f1);
      boolean b2 = fileIndex.isInSource(f2);
      if (b1 != b2) return b1 ? -1 : 1;
      return Integer.compare(f1.getPath().length(), f2.getPath().length());
    });

    for (VirtualFile file : files) {
      String existingFilePackage = packageIndex.getPackageNameByDirectory(file.getParent());
      if (StringUtil.isEmpty(targetPackage) || existingFilePackage == null || targetPackage.equals(existingFilePackage)) {
        return file;
      }
    }
    return null;
  }

  public static @NotNull VirtualFile getTargetDirectoryRelativeToContentRoot(@NotNull VirtualFile bnfFile,
                                                                             @NotNull Project project,
                                                                             @NotNull String targetRelativePath,
                                                                             @Nullable String targetPackage,
                                                                             boolean returnRoot) {
    PackageIndex packageIndex = PackageIndex.getInstance(project);
    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
    VirtualFile basePath = fileIndex.getContentRootForFile(bnfFile);
    if (basePath != null) {
    try {
      VirtualFile file = VirtualFileUtil.findOrCreateDirectory(basePath, targetRelativePath);
      return createOutputDirectory(file, targetPackage, packageIndex, fileIndex, returnRoot);
    }
    catch (ProcessCanceledException ex) {
      throw ex;
    }
    catch (Exception ex) {
      fail(project, targetRelativePath, ex.getMessage());
      throw new ProcessCanceledException();
    }
  }
    fail(project, targetRelativePath, "Unable to find target source root");
    throw new ProcessCanceledException();
  }

  private static VirtualFile createOutputDirectory(@NotNull VirtualFile virtualRoot,
                                                   @Nullable String targetPackage,
                                                   @NotNull PackageIndex packageIndex,
                                                   @NotNull FileIndex fileIndex,
                                                   boolean returnRoot) throws Exception {
    boolean hasPackage = StringUtil.isNotEmpty(targetPackage);
    String packagePrefix = StringUtil.notNullize(packageIndex.getPackageNameByDirectory(virtualRoot));
    String genDirName = Options.GEN_DIR.get();
    boolean newGenRoot = !fileIndex.isInSourceContent(virtualRoot);
    String relativePath = (hasPackage && newGenRoot ? genDirName + "/" + targetPackage :
                           hasPackage ? StringUtil.trimStart(StringUtil.trimStart(targetPackage, packagePrefix), ".") :
                           newGenRoot ? genDirName : "").replace('.', '/');
    if (relativePath.isEmpty()) {
      return virtualRoot;
    }
    else {
      VirtualFile result = WriteAction.compute(() -> VfsUtil.createDirectoryIfMissing(virtualRoot, relativePath));
      VfsUtil.markDirtyAndRefresh(false, true, true, result);
      return returnRoot && newGenRoot ? Objects.requireNonNull(virtualRoot.findChild(genDirName)) :
             returnRoot ? virtualRoot : result;
    }
  }

  public static void fail(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull String message) {
    fail(project, sourceFile.getName(), message);
  }

  public static void fail(@NotNull Project project, @NotNull String title, @NotNull String message) {
    Notifications.Bus.notify(new Notification(
      CommonBnfConstants.GENERATION_GROUP,
      title, message,
      NotificationType.ERROR), project);
    throw new ProcessCanceledException();
  }
}
