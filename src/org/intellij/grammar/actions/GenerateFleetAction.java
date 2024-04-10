/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.generator.fleet.FleetParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GenerateFleetAction extends GenerateAction {

  @Override
  @NotNull
  protected ParserGenerator createGenerator(BnfFile bnfFile, String sourcePath, File genDir, String packagePrefix, List<File> files) {
    return new FleetParserGenerator(bnfFile, sourcePath, genDir.getPath(), packagePrefix) {
      @Override
      protected PrintWriter openOutputInner(String className, File file) throws IOException {
        files.add(file);
        return super.openOutputInner(className, file);
      }
    };
  }
}
