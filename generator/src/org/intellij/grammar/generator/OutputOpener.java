/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

@FunctionalInterface
public interface OutputOpener {
  @NotNull OutputOpener DEFAULT = (className, fileToOpen, myBnfFile) -> {
    fileToOpen.getParentFile().mkdirs();
    return new OutputStreamWriter(new FileOutputStream(fileToOpen), myBnfFile.getVirtualFile().getCharset());
  };

  /**
   * Opens the destination of one generated file.
   * <p>
   * A {@link Writer}, deliberately not a {@link java.io.PrintWriter}: {@code println} terminates
   * lines with the platform separator, and generated sources are committed artifacts that must
   * come out byte-identical on every host. Anything written here - a banner, say - spells its
   * line breaks {@code "\n"}, and {@link FilePrinter} does the same for the body.
   */
  @NotNull Writer openOutput(
    @NotNull String className,
    @NotNull File fileToOpen,
    @NotNull BnfFile myBnfFile
  ) throws IOException;
}
