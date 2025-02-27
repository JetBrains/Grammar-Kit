/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.psi.PsiFile;
import org.intellij.grammar.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.fleet.FleetFileTypeGenerator;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FleetBnfGeneratorTest extends AbstractBnfGeneratorTest {

  private FileGeneratorParams myFileGeneratorParams = null;

  public FleetBnfGeneratorTest() {
    super("fleet", ".java");
  }

  @Override
  protected @NotNull List<@NotNull Generator> createGenerators(@NotNull BnfFile psiFile,
                                                               @NotNull String outputPath,
                                                               @NotNull OutputOpener outputOpener) {
    final var result = new ArrayList<Generator>();
    result.add(new JavaParserGenerator(psiFile, "", outputPath, "", outputOpener));
    if (myFileGeneratorParams != null) {
      result.add(new FleetFileTypeGenerator(
        psiFile, "", outputPath, "", myFileGeneratorParams.className, myFileGeneratorParams.debugName,
        myFileGeneratorParams.languageClass, outputOpener));
    }
    return result;
  }

  @Override
  protected PsiFile createBnfFile(boolean generatePsi, String name, String text) {
    return FleetBnfFileWrapper.wrapBnfFile(
      (BnfFile)createPsiFile(name, text.replaceAll("generatePsi=[^\n]*", "generatePsi=" + generatePsi)));
  }

  public void doGenTest(boolean generatePsi, String fileTypeClass, String debugName, String languageClass) throws Exception {
    myFileGeneratorParams = new FileGeneratorParams(true, fileTypeClass, debugName, languageClass);
    super.doGenTest(generatePsi);
    myFileGeneratorParams = null;
  }

  public void testIFileTypeGeneration() throws Exception {
    doGenTest(true, "some.filetype.psi.MyFileType", "TEST", "some.language.MyLanguage");
  }

  public void testFleetPsiGen() throws Exception {
    doPsiTest();
  }

  public void testFleetExprParser() throws Exception {
    doPsiTest();
  }

  public void testFleetExternalRules() throws Exception {
    doParserTest();
  }

  private record FileGeneratorParams(boolean doGenerate, String className, String debugName, String languageClass) {
  }
}
