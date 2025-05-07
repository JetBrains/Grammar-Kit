/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.config.Options;
import org.intellij.grammar.generator.CommonBnfConstants;
import org.intellij.jflex.parser.JFlexFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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
                              getFirstElement(preferSourceRoot && sourceRoots.length > 0 ? sourceRoots : contentRoots);
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

  public static @NotNull VirtualFile getOuterModuleTargetDirectoryFor(@NotNull Project project,
                                                                      @NotNull String outerModuleName,
                                                                      @Nullable String targetPackage,
                                                                      boolean returnRoot) {
    Module module = ModuleManager.getInstance(project).findModuleByName(outerModuleName);
    if (module != null) {
      boolean hasPackage = StringUtil.isNotEmpty(targetPackage);
      PackageIndex packageIndex = PackageIndex.getInstance(project);
      ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
      ModuleFileIndex fileIndex = rootManager.getFileIndex();
      VirtualFile moduleDir = ProjectUtil.guessModuleDir(module);
      if (moduleDir != null) {
        try {
          return createOutputDirectory(moduleDir, targetPackage, packageIndex, fileIndex, returnRoot);
        }
        catch (ProcessCanceledException ex) {
          throw ex;
        }
        catch (Exception ex) {
          fail(project, outerModuleName, ex.getMessage());
          throw new ProcessCanceledException();
        }
      }
    }
    fail(project, outerModuleName, "Unable to find target source root");
    throw new ProcessCanceledException();
  }
  
  public static @NotNull VirtualFile getTargetDirectoryFor(@NotNull Project project,
                                                           @NotNull String targetRelativePath,
                                                           @Nullable String targetPackage,
                                                           boolean returnRoot) {
    VirtualFile basePath = ProjectUtil.guessProjectDir(project);
    PackageIndex packageIndex = PackageIndex.getInstance(project);
    ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
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

  static void fail(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull String message) {
    fail(project, sourceFile.getName(), message);
  }

  static void fail(@NotNull Project project, @NotNull String title, @NotNull String message) {
    Notifications.Bus.notify(new Notification(
      CommonBnfConstants.GENERATION_GROUP,
      title, message,
      NotificationType.ERROR), project);
    throw new ProcessCanceledException();
  }
}
