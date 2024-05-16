/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FleetActionsUtil {

  public static boolean HasFleetLibraries(@NotNull AnActionEvent e, LibraryTablesRegistrar libraryTablesRegistrar) {
    var project = e.getProject();
    if (project == null) {
      return false;
    }
    var tables = libraryTablesRegistrar.getLibraryTable(project);
    return Arrays.stream(tables.getLibraries()).anyMatch(l -> l.getName() != null && l.getName().contains("ij-parsing-core"));
  }
}
