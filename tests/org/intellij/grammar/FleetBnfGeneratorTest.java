/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.generator.fleet.FleetParserGenerator;
import org.intellij.grammar.psi.impl.BnfFileImpl;

import java.io.*;

public class FleetBnfGeneratorTest extends BnfGeneratorAbstractTest {

  private FileGeneratorParams myFileGeneratorParams = null;
  private record FileGeneratorParams(boolean doGenerate, String className, String debugName, String languageClass) {}

  public FleetBnfGeneratorTest() {
    super("fleet");
  }

  @Override
  protected ParserGenerator newTestGenerator() {
    var doGenerate = false;
    var className = "";
    var languageName = "";
    var debugName = "";
    if (myFileGeneratorParams != null) {
      doGenerate = true;
      className = myFileGeneratorParams.className;
      languageName = myFileGeneratorParams.languageClass;
      debugName = myFileGeneratorParams.debugName;
    }
    return new FleetParserGenerator((BnfFileImpl)myFile, "", myFullDataPath, "", doGenerate, className, debugName, languageName) {

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
}
