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

public class FleetBnfGeneratorTest extends BnfGeneratorTest {

  public FleetBnfGeneratorTest() {
    super("fleet");
  }

  @Override
  protected ParserGenerator newTestGenerator(){
    return new FleetParserGenerator((BnfFileImpl)myFile, "", myFullDataPath, "") {

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

  public void testFleetJson() throws Exception { doGenTest(true);}
  public void testFleetPsiGen() throws Exception { doGenTest(true);}
}
