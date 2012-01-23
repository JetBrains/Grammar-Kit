package org.intellij.grammar;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.impl.BnfFileImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfPsiGeneratorTest extends LightCodeInsightFixtureTestCase {

  public void testSelf() throws Exception { generate(true); }
  public void testSelf2() throws Exception { generate(true); }


  public void generate(final boolean generatePsi) throws Exception {
    final String name = getTestName(false);
    String testData = "testData/generator";

    String text = loadFile(new File(testData, name + ".bnf"));

    PsiFile myFile = myFixture.addFileToProject(name+".bnf", text.replaceAll("parserClass=\"[^\"]*\"", "parserClass=\"" + name + "\" generatePsi=" + generatePsi));

    List<File> filesToCheck = new ArrayList<File>();
    filesToCheck.add(new File(testData, name + ".java"));
    if (generatePsi) {
      filesToCheck.add(new File(testData, name + ".PSI.java"));
    }
    for (File file : filesToCheck) {
      if (file.exists()) {
        assertTrue(file.delete());
      }
    }

    ParserGenerator parserGenerator = new ParserGenerator((BnfFileImpl)myFile, testData);
    if (generatePsi) parserGenerator.generate();
    else parserGenerator.generateParser();

    for (File file : filesToCheck) {
      assertTrue("Generated file not found: "+file, file.exists());
      final String expectedName = FileUtil.getNameWithoutExtension(file) + ".expected.java";
      String result = loadFile(file);
      File expectedFile = new File(testData, expectedName);
      try {
        String expectedText = loadFile(expectedFile);
        assertEquals(expectedName, expectedText, result);
      }
      catch (FileNotFoundException e) {
        FileWriter writer = new FileWriter(expectedFile);
        try {
          writer.write(result);
        }
        finally {
          writer.close();
        }
        fail("No output text found. File " + expectedFile + " created.");
      }
      finally {
        file.delete();
      }
    }
  }

  public static String loadFile(File expectedFile) throws IOException {
    return StringUtil.convertLineSeparators(FileUtil.loadFile(expectedFile, CharsetToolkit.UTF8).trim());
  }
}
