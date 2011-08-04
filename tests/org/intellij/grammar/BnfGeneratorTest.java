package org.intellij.grammar;

import com.intellij.openapi.application.PathManager;
import com.intellij.testFramework.ParsingTestCase;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.impl.BnfFileImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

/**
 * @author gregsh
 */
public class BnfGeneratorTest extends ParsingTestCase {
  public BnfGeneratorTest() {
    super("generator", "bnf", new BnfParserDefinition());
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  public void testSelf() throws Exception {
    doTest();
  }

  public void testSmall() throws Exception {
    doTest();
  }

  public void testAutopin() throws Exception {
    doTest();
  }

  public void doTest() throws Exception {
    final String name = getTestName(true);
    String text = loadFile(name + "." + myFileExt);
    myFile = createPsiFile(name, text);
    final File file = new File(myFullDataPath + File.separator + name + ".java");
    if (file.exists()) {
      assertTrue(file.delete());
    }
    new ParserGenerator((BnfFileImpl)myFile, myFullDataPath).generateParser();
    assertTrue(file.exists());
    final String expectedName = name + ".expected.java";
    String result = loadFile(name + ".java");
    try {
      String expectedText = loadFile(expectedName);
      assertEquals(expectedName, expectedText, result);
    }
    catch (FileNotFoundException e) {
      String fullName = myFullDataPath + File.separatorChar + expectedName;
      FileWriter writer = new FileWriter(fullName);
      try {
        writer.write(result);
      }
      finally {
        writer.close();
      }
      fail("No output text found. File " + fullName + " created.");
    }
    finally {
      file.delete();
    }
  }
}
