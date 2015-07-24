/*
 * Copyright 2011-2014 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.grammar.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.ObjectUtils;
import org.intellij.grammar.BnfFileType;
import org.intellij.grammar.generator.BnfConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static com.intellij.util.ArrayUtil.getFirstElement;

/**
 * @author gregsh
 */
public class FileGeneratorUtil {
  @NotNull
  public static VirtualFile getTargetDirectoryFor(@NotNull Project project,
                                                  @NotNull VirtualFile sourceFile,
                                                  @Nullable String targetFile,
                                                  @Nullable String targetPackage,
                                                  boolean returnRoot) {
    boolean hasPackage = StringUtil.isNotEmpty(targetPackage);
    ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
    ProjectFileIndex fileIndex = ProjectFileIndex.SERVICE.getInstance(project);
    Collection<VirtualFile> files = targetFile == null ? Collections.<VirtualFile>emptyList() :
                                    FilenameIndex.getVirtualFilesByName(project, targetFile,
                                                                        ProjectScope.getAllScope(project));

    VirtualFile existingFile = null;
    for (VirtualFile file : files) {
      String existingFilePackage = fileIndex.getPackageNameByDirectory(file.getParent());
      if (!hasPackage || existingFilePackage == null || targetPackage.equals(existingFilePackage)) {
        existingFile = file;
        break;
      }
    }

    VirtualFile existingFileRoot =
      existingFile == null ? null :
      fileIndex.isInSourceContent(existingFile) ? fileIndex.getSourceRootForFile(existingFile) :
      fileIndex.isInContent(existingFile) ? fileIndex.getContentRootForFile(existingFile) : null;

    boolean preferGenRoot = sourceFile.getFileType() == BnfFileType.INSTANCE;
    boolean preferSourceRoot = hasPackage && !preferGenRoot;
    VirtualFile[] sourceRoots = rootManager.getContentSourceRoots();
    VirtualFile[] contentRoots = rootManager.getContentRoots();
    final VirtualFile virtualRoot = existingFileRoot != null ? existingFileRoot :
                                    preferSourceRoot && fileIndex.isInSource(sourceFile) ? fileIndex.getSourceRootForFile(sourceFile) :
                                    fileIndex.isInContent(sourceFile) ? fileIndex.getContentRootForFile(sourceFile) :
                                    getFirstElement(preferSourceRoot && sourceRoots.length > 0? sourceRoots : contentRoots);
    if (virtualRoot == null) {
      fail(project, sourceFile, "Unable to guess target source root");
      throw new ProcessCanceledException();
    }
    try {
      boolean newGenRoot = !fileIndex.isInSourceContent(virtualRoot);
      final String relativePath = (hasPackage && newGenRoot? "gen." + targetPackage :
                                  hasPackage ? targetPackage :
                                  newGenRoot ? "gen" : "").replace('.', '/');
      if (relativePath.isEmpty()) {
        return virtualRoot;
      }
      else {
        VirtualFile result = new WriteAction<VirtualFile>() {
          @Override
          protected void run(@NotNull Result<VirtualFile> result) throws Throwable {
            result.setResult(VfsUtil.createDirectoryIfMissing(virtualRoot, relativePath));
          }
        }.execute().throwException().getResultObject();
        VfsUtil.markDirtyAndRefresh(false, true, true, result);
        return returnRoot && newGenRoot? ObjectUtils.assertNotNull(virtualRoot.findChild("gen")) :
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
    Notifications.Bus.notify(new Notification(
      BnfConstants.GENERATION_GROUP,
      sourceFile.getName(), message,
      NotificationType.ERROR), project);
    throw new ProcessCanceledException();
  }
}
