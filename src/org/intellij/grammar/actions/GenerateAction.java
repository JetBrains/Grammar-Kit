/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import org.intellij.grammar.generator.ParserGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author gregory
 *         Date: 15.07.11 17:12
 */
public class GenerateAction extends GenerateActionBase {
  public GenerateAction() {
    super((bnfFile, sourcePath, outputPath, packagePrefix, filesRef) -> new ParserGenerator(bnfFile, sourcePath, outputPath, packagePrefix) {
      @Override
      protected PrintWriter openOutputInner(String className, File file) throws IOException {
        filesRef.get().add(file);
        return super.openOutputInner(className, file);
      }
    });
  }
}
