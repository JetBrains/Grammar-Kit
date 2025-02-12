/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import org.intellij.grammar.generator.KotlinParserGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public final class GenerateKotlinAction extends GenerateActionBase {
  public GenerateKotlinAction() {
    super((bnfFile, sourcePath, outputPath, packagePrefix, filesRef) -> new KotlinParserGenerator(bnfFile, sourcePath, outputPath,
                                                                                                  packagePrefix) {
      @Override
      protected PrintWriter openOutputInner(String className, File file) throws IOException {
        filesRef.get().add(file);
        return super.openOutputInner(className, file);
      }
    });
  }
}
