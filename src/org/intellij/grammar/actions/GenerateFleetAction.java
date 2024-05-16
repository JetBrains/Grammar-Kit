/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.actions;

import com.intellij.psi.PsiFile;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.generator.fleet.FleetConstants;
import org.intellij.grammar.generator.fleet.FleetParserGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getRootAttribute;

public class GenerateFleetAction extends GenerateAction {

  @Override
  protected String getParserClass(PsiFile bnfFile) {
    var original = super.getParserClass(bnfFile);
    if (adjustPackages(bnfFile)) {
      original = FleetConstants.FLEET_NAMESPACE_PREFIX + original;
    }
    return original;
  }

  private static boolean adjustPackages(PsiFile file) {
    return getRootAttribute(file, KnownAttribute.GENERATE).stream()
      .noneMatch(pair -> pair.first.equals("adjustPackagesForFleet") && pair.second.equals("no"));
  }

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
