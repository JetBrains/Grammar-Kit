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
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.generator.CommonBnfConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author gregsh
 */
public class FileGeneratorUtil {
  /**
   * Resolves the output directory for a file to be generated. Wraps the read-only inference in
   * {@link BnfPaths#inferTargetDirectory} with the create step: the inferred path is realized
   * on disk via {@link VfsUtil#createDirectoryIfMissing(String)} inside a write action.
   *
   * <p>Throws {@link ProcessCanceledException} (after showing an error notification) when no
   * suitable root can be guessed or directory creation fails.
   *
   * @param project       the current project
   * @param sourceFile    the grammar or source file that triggers generation
   * @param targetFile    short file name (without path) to look up in the project index as an
   *                      anchor; may be {@code null} to skip the lookup
   * @param targetPackage fully qualified package name for the generated file
   * @param returnRoot    if {@code true}, returns the generation root rather than the deepest
   *                      package subdirectory
   * @param preferGenRoot if {@code true}, prefer a content-root + {@code gen} layout over the
   *                      source root of {@code sourceFile}
   * @return the {@link VirtualFile} directory where the generated file should be written;
   *         created if it does not exist yet
   */
  public static @NotNull VirtualFile getTargetDirectoryFor(@NotNull Project project,
                                                           @NotNull VirtualFile sourceFile,
                                                           @Nullable String targetFile,
                                                           @Nullable String targetPackage,
                                                           boolean returnRoot,
                                                           boolean preferGenRoot
  ) {
    Path inferred = BnfPaths.inferTargetDirectory(project, sourceFile, targetFile, targetPackage, returnRoot, preferGenRoot);
    if (inferred == null) {
      fail(project, sourceFile, "Unable to guess target source root");
      throw new ProcessCanceledException();
    }

    try {
      VirtualFile result = WriteAction.computeAndWait(() -> {
        try {
          return VfsUtil.createDirectoryIfMissing(inferred.toString());
        }
        catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      });
      if (result != null) VfsUtil.markDirtyAndRefresh(false, true, true, result);
      if (result == null) {
        fail(project, sourceFile, "Cannot create directory: " + inferred);
        throw new ProcessCanceledException();
      }
      return result;
    }
    catch (ProcessCanceledException ex) {
      throw ex;
    }
    catch (Exception ex) {
      fail(project, sourceFile, ex.getMessage());
      throw new ProcessCanceledException();
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
