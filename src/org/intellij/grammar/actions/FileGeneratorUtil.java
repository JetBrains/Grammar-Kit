/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.config.Options;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.jflex.parser.JFlexFileType;
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
  public static @NotNull VirtualFile getTargetDirectoryFor(@NotNull Project project,
                                                           @NotNull VirtualFile sourceFile,
                                                           @Nullable String targetFile,
                                                           @Nullable String targetPackage,
                                                           boolean returnRoot) {
    boolean hasPackage = StringUtil.isNotEmpty(targetPackage);
    ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
    PackageIndex packageIndex = PackageIndex.getInstance(project);
    ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);

    VirtualFile existingFile = null;
    if (targetFile != null) {
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
        if (!hasPackage || existingFilePackage == null || targetPackage.equals(existingFilePackage)) {
          existingFile = file;
          break;
        }
      }
    }

    VirtualFile existingFileRoot =
      existingFile == null ? null :
      fileIndex.isInSourceContent(existingFile) ? fileIndex.getSourceRootForFile(existingFile) :
      fileIndex.isInContent(existingFile) ? fileIndex.getContentRootForFile(existingFile) : null;

    boolean preferGenRoot = sourceFile.getFileType() == BnfFileType.INSTANCE ||
                            sourceFile.getFileType() == JFlexFileType.INSTANCE;
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
    catch (ProcessCanceledException ex) {
      throw ex;
    }
    catch (Exception ex) {
      fail(project, sourceFile, ex.getMessage());
      throw new ProcessCanceledException();
    }
  }

  static void fail(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull String message) {
    fail(project, sourceFile.getName(), message);
  }

  static void fail(@NotNull Project project, @NotNull String title, @NotNull String message) {
    Notifications.Bus.notify(new Notification(
      BnfConstants.GENERATION_GROUP,
      title, message,
      NotificationType.ERROR), project);
    throw new ProcessCanceledException();
  }
}
