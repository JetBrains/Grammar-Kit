/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import org.intellij.grammar.generator.GeneratorBase;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.fleet.FleetBnfFileWrapper;
import org.intellij.grammar.fleet.FleetFileTypeGenerator;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FleetBnfGeneratorTest extends BnfGeneratorAbstractTest {

  private FileGeneratorParams myFileGeneratorParams = null;
  private record FileGeneratorParams(boolean doGenerate, String className, String debugName, String languageClass) {}

  public FleetBnfGeneratorTest() {
    super("fleet");
  }

  @Override
  protected Collection<GeneratorBase> newTestGenerator() {
    var fleetBnfFile = new FleetBnfFileWrapper(myFile.getViewProvider());

    var parserGenerator = new ParserGenerator(fleetBnfFile, "", myFullDataPath, "") {

        @Override
        protected PrintWriter openOutputInner(String className, File file) throws IOException {
          String grammarName = FileUtil.getNameWithoutExtension(myFile.getName());
          String fileName = FileUtil.getNameWithoutExtension(file);
          String name = grammarName + (fileName.startsWith(grammarName) || fileName.endsWith("Parser") ? "" : ".PSI") + ".java";
          File targetFile = new File(FileUtilRt.getTempDirectory(), name);
          targetFile.getParentFile().mkdirs();
          FileOutputStream outputStream = new FileOutputStream(targetFile, true);
          PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, myFile.getVirtualFile().getCharset()));
          out.println("// ---- " + file.getName() + " -----------------");
          return out;
        }
      };

    if (myFileGeneratorParams != null) {
      var fileTypeGenerator = new FleetFileTypeGenerator(fleetBnfFile,
                                                         "", myFullDataPath, "",
                                                         myFileGeneratorParams.className,
                                                         myFileGeneratorParams.debugName,
                                                         myFileGeneratorParams.languageClass) {
        @Override
        protected PrintWriter openOutputInner(String className, File file) throws IOException {
          String grammarName = FileUtil.getNameWithoutExtension(myFile.getName());
          String name = grammarName + ".PSI.java";
          File targetFile = new File(FileUtilRt.getTempDirectory(), name);
          targetFile.getParentFile().mkdirs();
          FileOutputStream outputStream = new FileOutputStream(targetFile, true);
          PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, myFile.getVirtualFile().getCharset()));
          out.println("// ---- " + file.getName() + " -----------------");
          return out;
        }
      };
      return Arrays.asList(parserGenerator, fileTypeGenerator);
    }
    return List.of(parserGenerator);
  }

  public void doGenTest(boolean generatePsi, String fileTypeClass, String debugName, String languageClass) throws Exception {
    myFileGeneratorParams = new FileGeneratorParams(true, fileTypeClass, debugName, languageClass);
    super.doGenTest(generatePsi);
    myFileGeneratorParams = null;
  }

  public void testFleetJson() throws Exception { doGenTest(true, "fleet.com.intellij.json.psi.JsonFileType", "JSON", "fleet.com.intellij.json.JsonLanguage");}
  public void testAdjustPackages() throws Exception { doGenTest(true);}
  public void testIFileTypeGeneration() throws Exception { doGenTest(true, "some.filetype.psi.MyFileType", "TEST", "some.language.MyLanguage");}
  public void testFleetPsiGen() throws Exception { doGenTest(true);}
  public void testExprParser() throws Exception { doGenTest(true);}
}
