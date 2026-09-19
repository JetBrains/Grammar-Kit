/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.MessageView;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Console tabs in the {@code Messages} tool window shared by all generator actions.
 *
 * <p>Tabs are keyed by an opaque {@code batchId} so that several files processed by a single
 * action invocation stream into one tab.
 */
public final class GrammarKitConsole {

  private static final Key<Pair<String, OSProcessHandler>> BATCH_ID_KEY = Key.create("GrammarKitConsole.batchId");

  private GrammarKitConsole() {
  }

  /**
   * Attaches a process to a console tab in the {@code Messages} tool window and activates the window.
   *
   * <p>Tab-reuse strategy:
   * <ul>
   *   <li>If a tab already exists for the given {@code batchId} (i.e., this is not the first file in a
   *       multi-file invocation), the existing console is reused and a separator is printed.</li>
   *   <li>Otherwise, if there is an unpinned tab whose previous process has fully terminated, that
   *       tab is cleared, relabelled {@code title} and reused.</li>
   *   <li>If neither condition holds, a new tab is created with the given {@code title}.</li>
   * </ul>
   *
   * @param project        the current project
   * @param title          tab label used when a new tab must be created
   * @param batchId        opaque identifier shared by all files in a single action invocation
   * @param processHandler the process whose output should be shown in the console
   */
  public static void showConsole(@NotNull Project project,
                                 @NotNull String title,
                                 @NotNull String batchId,
                                 @NotNull OSProcessHandler processHandler) {
    MessageView messageView = MessageView.getInstance(project);
    Content batchContent = null, stoppedContent = null;
    for (Content c : messageView.getContentManager().getContents()) {
      Pair<String, OSProcessHandler> data = c.getUserData(BATCH_ID_KEY);
      if (data == null) continue;
      if (data.first.equals(batchId)) {
        batchContent = c;
      }
      // Terminated only, never merely terminating: a process still shutting down would
      // interleave its tail into the new run's output, and clear() would wipe it.
      else if (data.second.isProcessTerminated() && !c.isPinned()) {
        stoppedContent = c;
      }
    }
    Content content = ObjectUtils.chooseNotNull(batchContent, stoppedContent);
    ConsoleView consoleView = content == null ? null : UIUtil.uiTraverser(content.getComponent()).filter(ConsoleView.class).first();

    if (content != null && consoleView != null) {
      if (content == batchContent) {
        consoleView.print("\n\n\n", ConsoleViewContentType.SYSTEM_OUTPUT);
      }
      else {
        // A terminated tab of any kind is fair game, but the label must follow what now runs in it.
        consoleView.clear();
        content.setDisplayName(title);
      }
      attachAndActivate(project, batchId, processHandler, content, consoleView);
      return;
    }

    consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();

    JComponent panel = new JPanel(new BorderLayout());
    panel.add(consoleView.getComponent(), BorderLayout.CENTER);

    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    for (AnAction action : consoleView.createConsoleActions()) {
      toolbarActions.add(action);
    }
    ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, toolbarActions, false);
    toolbar.setTargetComponent(consoleView.getComponent());
    panel.add(toolbar.getComponent(), BorderLayout.WEST);

    content = ContentFactory.getInstance().createContent(panel, title, true);
    messageView.getContentManager().addContent(content);
    Disposer.register(content, consoleView);

    attachAndActivate(project, batchId, processHandler, content, consoleView);
  }

  /**
   * Wires a process handler to a console view, stamps the content tab with the batch identifier,
   * and brings the {@code Messages} tool window to the front with that tab selected.
   *
   * <p>Stamping the tab with {@link #BATCH_ID_KEY} is what allows subsequent calls from the same
   * batch (multi-file invocation) to locate and reuse the tab instead of opening a new one.
   */
  private static void attachAndActivate(@NotNull Project project,
                                        @NotNull String batchId,
                                        @NotNull OSProcessHandler processHandler,
                                        @NotNull Content content,
                                        @NotNull ConsoleView consoleView) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.MESSAGES_WINDOW);
    content.putUserData(BATCH_ID_KEY, Pair.create(batchId, processHandler));
    consoleView.attachToProcess(processHandler);

    if (toolWindow != null) {
      toolWindow.activate(() -> toolWindow.getContentManager().setSelectedContent(content), false, false);
    }
  }
}
