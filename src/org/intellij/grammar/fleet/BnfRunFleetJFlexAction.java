/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.fleet;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.grammar.actions.BnfRunJFlexAction;
import org.intellij.grammar.java.JavaHelper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;

public class BnfRunFleetJFlexAction extends BnfRunJFlexAction {
  private static final String TEMP_FLEX_DIRECTORY = "temp-grammar-kit";
  private static final String PACKAGE_PREFIX = "package ";
  private static final String IMPORT_PREFIX = "import ";
  private static final String IMPORT_STATIC_PREFIX = "import static ";

  private static final String IELEMENTTYPE_CLASS = "com.intellij.psi.tree.IElementType";
  private static final String FLEX_LEXER_CLASS = "com.intellij.lexer.FlexLexer";
  private static final String WHITESPACE_TOKEN = "com.intellij.psi.TokenType.WHITE_SPACE";
  private static final String BAD_CHARACTER_TOKEN = "com.intellij.psi.TokenType.BAD_CHARACTER";

  private JavaHelper javaHelper;

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    var project = e.getProject();
    if (project == null) return;

    javaHelper = project.getService(JavaHelper.class);
    javaHelper = javaHelper == null ? new JavaHelper.AsmHelper() : javaHelper;

    super.actionPerformed(e);
  }

  @Override
  public void doGenerate(@NotNull Project project,
                                   @NotNull Collection<VirtualFile> flexFiles,
                                   @NotNull Couple<File> jflex,
                                   @NotNull String batchId) {
    flexFiles.stream().map(f -> {
      try {
        if (f == null) return null;
        var tempFileDirectory = f.getParent().getPath() + VfsUtil.VFS_SEPARATOR + TEMP_FLEX_DIRECTORY;
        var newFile = new File(tempFileDirectory, f.getName());
        newFile.getParentFile().mkdirs();
        var printer = new PrintWriter(new FileOutputStream(newFile), false, f.getCharset());
        Files.readAllLines(f.toNioPath()).forEach(line -> {
          if (line.startsWith(PACKAGE_PREFIX) || line.startsWith(IMPORT_PREFIX) || line.startsWith(IMPORT_STATIC_PREFIX)) {
            printer.println(adjustLine(line, javaHelper));
          }
          else {
            printer.println(line);
          }
        });
        printer.close();
        return VfsUtil.findFileByIoFile(newFile, true);
      }
      catch (Exception ignored) {
        return null;
      }
    }).forEach(flexFile -> {
      if (flexFile == null) return;
      var result = doGenerateInner(project, flexFile, jflex, batchId);
      result.doWhenProcessed(() -> {
        try {
          FileUtil.delete(flexFile.getParent().toNioPath());
          VfsUtil.markDirtyAndRefresh(true, false, true, flexFile.getParent());
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    });
  }

  private static String adjustLine(String line, JavaHelper javaHelper) {
    var tokens = line.split("[ ;]");
    var name = tokens[tokens.length - 1];

    if ((line.startsWith(PACKAGE_PREFIX) && (!name.startsWith(FleetConstants.FLEET_NAMESPACE_PREFIX))) ||
        nameNeedsAdjusting(name, javaHelper)) {
      name = FleetConstants.FLEET_NAMESPACE_PREFIX + name;
      StringBuilder lineBuilder = new StringBuilder();
      for (int i = 0; i < tokens.length - 1; i++) {
        lineBuilder.append(tokens[i]).append(" ");
      }
      return lineBuilder + name + ';';
    }
    return line;
  }

  private static Boolean nameNeedsAdjusting(String className, JavaHelper javaHelper) {
    if (className.startsWith(FleetConstants.FLEET_NAMESPACE_PREFIX)) {
      return false;
    }

    if (className.equals(IELEMENTTYPE_CLASS) ||
        className.equals(FLEX_LEXER_CLASS) ||
        className.equals(WHITESPACE_TOKEN) ||
        className.equals(BAD_CHARACTER_TOKEN)) {
      return true;
    }

    var name = className;
    if (className.endsWith(".*")) {
      name = className.substring(0, className.length() - 2);
    }

    var adjustedName = FleetConstants.FLEET_NAMESPACE_PREFIX + name;
    if (javaHelper.findClass(adjustedName) != null || javaHelper.findPackage(adjustedName) != null) {
      return true;
    }

    return javaHelper.findClass(name) == null && javaHelper.findPackage(name) == null;
  }
}