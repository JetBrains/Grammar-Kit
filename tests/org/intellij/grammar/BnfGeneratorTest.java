package org.intellij.grammar;

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

  public void testSelf() throws Exception { doTest(); }
  public void testSelf2() throws Exception { doTest(); }
  public void testSmall() throws Exception { doTest(); }
  public void testAutopin() throws Exception { doTest(); }
  public void testExternalRules() throws Exception { doTest(); }
  public void testLeftAssociative() throws Exception { doTest(); }

  public void doTest() throws Exception {
    final String name = getTestName(false);
    String text = loadFile(name + "." + myFileExt);
    myFile = createPsiFile(name, text.replaceAll("parserClass=\"[^\"]*\"", "parserClass=\""+name+"\" generatePsi=false"));
    final File file = new File(myFullDataPath + File.separator + name + ".java");
    if (file.exists()) {
      assertTrue(file.delete());
    }
    new ParserGenerator((BnfFileImpl)myFile, myFullDataPath).generateParser();
    assertTrue("Generated file not found: "+file, file.exists());
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
