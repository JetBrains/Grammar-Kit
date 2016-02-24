package org.intellij.grammar;

import com.intellij.idea.Bombed;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.rt.execution.junit.FileComparisonFailure;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NonNls;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfGeneratorTest extends BnfGeneratorTestCase {

  public BnfGeneratorTest() {
    super("generator");
  }

  public void testBnfGrammar() throws Exception { doGenTest(true); }
  public void testFlexGrammar() throws Exception { doGenTest(true); }
  public void testSelf() throws Exception { doGenTest(true); }
  public void testSmall() throws Exception { doGenTest(false); }
  public void testAutopin() throws Exception { doGenTest(false); }
  public void testExternalRules() throws Exception { doGenTest(false); }
  public void testLeftAssociative() throws Exception { doGenTest(false); }
  public void testPsiGen() throws Exception { doGenTest(true); }
  public void testPsiAccessors() throws Exception { doGenTest(true); }
  public void testPsiStart() throws Exception { doGenTest(true); }
  public void testExprParser() throws Exception { doGenTest(true); }
  public void testTokenSequence() throws Exception { doGenTest(false); }
  public void testStub() throws Exception { doGenTest(true); }
  public void testAutoRecovery() throws Exception { doGenTest(true); }

  @Bombed(year = 2030, user = "author", month = 1, day = 1, description = "not implemented")
  public void testUpperRules() throws Exception { doGenTest(true); }
  public void testFixes() throws Exception { doGenTest(true); }

  public void testEmpty() throws Exception {
    myFile = createPsiFile("empty.bnf", "{ }");
    newTestGenerator().generate();
  }

  private ParserGenerator newTestGenerator() {
    return new ParserGenerator((BnfFileImpl)myFile, "", myFullDataPath) {

      @Override
      protected PrintWriter openOutputInner(File file) throws IOException {
        String grammarName = FileUtil.getNameWithoutExtension(myFile.getName());
        String fileName = FileUtil.getNameWithoutExtension(file);
        String name = grammarName + (fileName.startsWith(grammarName) || fileName.endsWith("Parser") ? "" : ".PSI") + ".java";
        PrintWriter out = new PrintWriter(new FileOutputStream(new File(myFullDataPath, name), true));
        out.println("// ---- " + file.getName() + " -----------------");
        return out;
      }
    };
  }

  @Override
  protected String loadFile(@NonNls String name) throws IOException {
    if (name.equals("BnfGrammar.bnf")) return super.loadFile("../../grammars/Grammar.bnf");
    if (name.equals("FlexGrammar.bnf")) return super.loadFile("../../grammars/JFlex.bnf");
    return super.loadFile(name);
  }

  public void doGenTest(final boolean generatePsi) throws Exception {
    final String name = getTestName(false);
    String text = loadFile(name + "." + myFileExt);
    myFile = createPsiFile(name, text.replaceAll("generatePsi=[^\n]*", "generatePsi=" + generatePsi));
    List<File> filesToCheck = ContainerUtil.newArrayList();
    if (generatePsi) {
      filesToCheck.add(new File(myFullDataPath, name + ".PSI.java"));
    }
    filesToCheck.add(new File(myFullDataPath, name + ".java"));
    for (File file : filesToCheck) {
      if (file.exists()) {
        assertTrue(file.delete());
      }
    }

    ParserGenerator parserGenerator = newTestGenerator();
    if (generatePsi) parserGenerator.generate();
    else parserGenerator.generateParser();

    List<String> messages = new ArrayList<String>();
    try {
      for (File file : filesToCheck) {
        assertTrue("Generated file not found: "+file, file.exists());
        final String expectedName = FileUtil.getNameWithoutExtension(file) + ".expected.java";
        String result = loadFile(file.getName());
        try {
          if (OVERWRITE_TESTDATA) throw new FileNotFoundException();
          String expectedText = loadFile(expectedName);
          if (!Comparing.equal(expectedText, result)) {
            throw new FileComparisonFailure(expectedName, expectedText, result, new File(myFullDataPath + File.separator + expectedName).getAbsolutePath());
          }
        }
        catch (FileNotFoundException e) {
          FileWriter writer = new FileWriter(new File(myFullDataPath, expectedName));
          try {
            writer.write(result);
          }
          finally {
            writer.close();
          }
          messages.add("No output text found. File " + expectedName + " created.");
        }
      }
    }
    finally {
      for (File file : filesToCheck) {
        file.delete();
      }
    }
    for (String message : messages) {
      System.err.println(message);
    }
    assertTrue(OVERWRITE_TESTDATA || messages.isEmpty());
  }
}
