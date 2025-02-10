/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.intellij.grammar.generator.GeneratorBase;
import org.intellij.grammar.generator.KotlinParserGenerator;
import org.intellij.grammar.psi.BnfFile;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class KotlinBnfGeneratorTest extends BnfGeneratorAbstractTest {
  public KotlinBnfGeneratorTest() {
    super("kotlin");
  }

  @Override
  protected List<GeneratorBase> newTestGenerator() {
    return List.of(
      new KotlinParserGenerator((BnfFile)myFile, "", myFullDataPath, "") {
        @Override
        protected PrintWriter openOutputInner(String className, File file) throws IOException {
          final var grammarName = FileUtil.getNameWithoutExtension(this.myFile.getName());
          final var fileName = FileUtil.getNameWithoutExtension(file);
          final var name = grammarName + (fileName.startsWith(grammarName) || fileName.endsWith("Parser") ? "" : ".PSI") + ".kt";
          final var targetFile = new File(FileUtilRt.getTempDirectory(), name);
          targetFile.getParentFile().mkdirs();
          final var outputStream = new FileOutputStream(targetFile, true);
          final var out = new PrintWriter(new OutputStreamWriter(outputStream, this.myFile.getVirtualFile().getCharset()));
          out.println("// ---- " + file.getName() + " -----------------");
          return out;
        }
      }
    );
  }

  public void doGenTest() throws Exception {
    doGenTest(false);
  }

  @Override
  public void doGenTest(boolean generatePsi) throws Exception {
    String name = getTestName(false);
    String text = loadFile(name + "." + myFileExt);
    myFile = createBnfFile(false, name, text);
    final var fileToCheck = new File(FileUtilRt.getTempDirectory(), name + ".kt");
    if (fileToCheck.exists()) {
      assertTrue(fileToCheck.delete());
    }

    for (GeneratorBase generator : newTestGenerator()) {
      generator.generate();
    }


    assertTrue("Generated file not found: " + fileToCheck, fileToCheck.exists());
    String expectedName = FileUtil.getNameWithoutExtension(fileToCheck) + ".expected.kt";
    String result = FileUtil.loadFile(fileToCheck, CharsetToolkit.UTF8, true);
    doCheckResult(myFullDataPath, expectedName, result);
  }
  
  public void testAutoRecovery() throws Exception { doGenTest(); }
  public void testGenOptions() throws Exception { doGenTest(); }
  public void testSmall() throws Exception { doGenTest(); }
  public void testStub() throws Exception { doGenTest(); }
  public void testUpperRules() throws Exception { doGenTest(); }
  public void testUtilMethods() throws Exception { doGenTest(); }
}
