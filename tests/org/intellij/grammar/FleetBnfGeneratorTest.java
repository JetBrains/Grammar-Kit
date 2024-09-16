/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiFile;
import org.intellij.grammar.generator.GeneratorBase;
import org.intellij.grammar.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.fleet.FleetFileTypeGenerator;
import org.intellij.grammar.psi.BnfFile;

import java.io.*;
import java.util.List;

public class FleetBnfGeneratorTest extends BnfGeneratorAbstractTest {

  private FileGeneratorParams myFileGeneratorParams = null;
  private record FileGeneratorParams(boolean doGenerate, String className, String debugName, String languageClass) {}

  public FleetBnfGeneratorTest() {
    super("fleet");
  }

  @Override
  protected List<GeneratorBase> newTestGenerator() {
    var listOfGens = super.newTestGenerator();
    if (myFileGeneratorParams != null) {
      var fileTypeGenerator = new FleetFileTypeGenerator((BnfFile)myFile,
                                                         "", myFullDataPath, "",
                                                         myFileGeneratorParams.className,
                                                         myFileGeneratorParams.debugName,
                                                         myFileGeneratorParams.languageClass) {
        @Override
        protected PrintWriter openOutputInner(String className, File file) throws IOException {
          String grammarName = FileUtil.getNameWithoutExtension(this.file.getName());
          String name = grammarName + ".PSI.java";
          File targetFile = new File(FileUtilRt.getTempDirectory(), name);
          targetFile.getParentFile().mkdirs();
          FileOutputStream outputStream = new FileOutputStream(targetFile, true);
          PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, this.file.getVirtualFile().getCharset()));
          out.println("// ---- " + file.getName() + " -----------------");
          return out;
        }
      };
      listOfGens.add(fileTypeGenerator);
    }
    return listOfGens;
  }

  @Override
  protected PsiFile createBnfFile(boolean generatePsi, String name, String text) {
    return FleetBnfFileWrapper.wrapBnfFile((BnfFile)createPsiFile(name, text.replaceAll("generatePsi=[^\n]*", "generatePsi=" + generatePsi)));
  }

  public void doGenTest(boolean generatePsi, String fileTypeClass, String debugName, String languageClass) throws Exception {
    myFileGeneratorParams = new FileGeneratorParams(true, fileTypeClass, debugName, languageClass);
    super.doGenTest(generatePsi);
    myFileGeneratorParams = null;
  }

  public void testIFileTypeGeneration() throws Exception { doGenTest(true, "some.filetype.psi.MyFileType", "TEST", "some.language.MyLanguage");}
  public void testFleetPsiGen() throws Exception { doGenTest(true);}
  public void testFleetExprParser() throws Exception { doGenTest(true);}
  public void testFleetExternalRules() throws Exception { doGenTest(false);}
}
