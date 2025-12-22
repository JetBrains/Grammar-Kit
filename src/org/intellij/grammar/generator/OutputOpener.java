/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@FunctionalInterface
public interface OutputOpener {
  @NotNull OutputOpener DEFAULT = (className, fileToOpen, myBnfFile) -> {
    fileToOpen.getParentFile().mkdirs();
    return new PrintWriter(new FileOutputStream(fileToOpen), false, myBnfFile.getVirtualFile().getCharset());
  };

  @NotNull PrintWriter openOutput(
    @NotNull String className,
    @NotNull File fileToOpen,
    @NotNull BnfFile myBnfFile
  ) throws IOException;
}
