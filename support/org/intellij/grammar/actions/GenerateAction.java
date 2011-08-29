/*
 * Copyright 2011-2011 Gregory Shrago
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

import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ExceptionUtil;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.impl.BnfFileImpl;

import java.io.File;

/**
 * @author gregory
 *         Date: 15.07.11 17:12
 */
public class GenerateAction extends AnAction {
  
  private static Logger LOG = Logger.getInstance("org.intellij.grammar.actions.GenerateAction");

  @Override
  public void update(AnActionEvent e) {
    PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    e.getPresentation().setEnabled(file instanceof BnfFileImpl);
    e.getPresentation().setVisible(file instanceof BnfFileImpl);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    PsiFile file = LangDataKeys.PSI_FILE.getData(e.getDataContext());
    VirtualFile virtualFile = file instanceof BnfFileImpl ? file.getVirtualFile() : null;
    if (virtualFile == null) return;

    PsiDocumentManager.getInstance(file.getProject()).commitAllDocuments();
    VirtualFile content = ProjectRootManager.getInstance(file.getProject()).getFileIndex().getContentRootForFile(virtualFile);
    VirtualFile parentDir = content == null ? virtualFile.getParent() : content;
    String toDir = new File(VfsUtil.virtualToIoFile(parentDir), "gen").getAbsolutePath();
    try {

      new ParserGenerator((BnfFileImpl)file, toDir).generate();
      Notifications.Bus.notify(new Notification(e.getPresentation().getText(),
                                                file.getName() + " parser generated", "to " + toDir, NotificationType.INFORMATION),
                               file.getProject());
    }
    catch (Exception ex) {
      Notifications.Bus.notify(new Notification(e.getPresentation().getText(),
                                                file.getName() + " parser generation failed",
                                                ExceptionUtil.getUserStackTrace(ex, ParserGenerator.LOG),
                                                NotificationType.ERROR), file.getProject());
      LOG.error(ex);
    }
    finally {
      // refresh everything
      FileDocumentManager.getInstance().saveAllDocuments();

      SaveAndSyncHandler.refreshOpenFiles();
      VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }
  }
}
