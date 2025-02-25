/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public interface Generator {
  @NotNull Generator KOTLIN_GENERATOR = fromGeneratorBase(KotlinParserGenerator::new);
  @NotNull Generator JAVA_GENERATOR = fromGeneratorBase(JavaParserGenerator::new);

  static <T extends GeneratorBase> @NotNull Generator fromGeneratorBase(@NotNull Generator.GeneratorBaseFactory<T> generatorBaseFactory) {
    return new Generator() {
      @Override
      public void generate(@NotNull BnfFile bnfFile,
                           @NotNull String sourcePath,
                           @NotNull String outputPath,
                           @NotNull String packagePrefix,
                           @NotNull OutputOpener outputOpener) throws IOException {
        generatorBaseFactory.create(bnfFile, sourcePath, outputPath, packagePrefix, outputOpener).generate();
      }

      @Override
      public void generateParser(@NotNull BnfFile bnfFile,
                                 @NotNull String sourcePath,
                                 @NotNull String outputPath,
                                 @NotNull String packagePrefix,
                                 @NotNull OutputOpener outputOpener) throws IOException {
        generatorBaseFactory.create(bnfFile, sourcePath, outputPath, packagePrefix, outputOpener).generateParser();
      }
    };
  }

  /**
   * Generates the parser file.
   *
   * @param bnfFile       the BNF file to generate the parser from
   * @param sourcePath    the path containing the BNF file
   * @param outputPath    the path to write the generated parser to
   * @param packagePrefix the package prefix to use for the generated parser
   * @throws IOException if an I/O error occurs
   */
  void generate(
    @NotNull BnfFile bnfFile,
    @NotNull String sourcePath,
    @NotNull String outputPath,
    @NotNull String packagePrefix,
    @NotNull OutputOpener outputOpener
  ) throws IOException;

  void generateParser(
    @NotNull BnfFile bnfFile,
    @NotNull String sourcePath,
    @NotNull String outputPath,
    @NotNull String packagePrefix,
    @NotNull OutputOpener outputOpener
  ) throws IOException;

  @FunctionalInterface
  interface GeneratorBaseFactory<T extends GeneratorBase> {
    @NotNull T create(
      @NotNull BnfFile bnfFile,
      @NotNull String sourcePath,
      @NotNull String outputPath,
      @NotNull String packagePrefix,
      @NotNull OutputOpener outputOpener
    );
  }
}
