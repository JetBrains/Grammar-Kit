package org.intellij.grammar;

import com.intellij.idea.Bombed;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.CharsetToolkit;
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

  public void testSelfBnf() throws Exception { doGenTest(true); }
  public void testSelfFlex() throws Exception { doGenTest(true); }
  public void testSmall() throws Exception { doGenTest(false); }
  public void testAutopin() throws Exception { doGenTest(false); }
  public void testExternalRules() throws Exception { doGenTest(false); }
  public void testExternalRulesLambdas() throws Exception { doGenTest(false); }
  public void testLeftAssociative() throws Exception { doGenTest(false); }
  public void testPsiGen() throws Exception { doGenTest(true); }
  public void testPsiAccessors() throws Exception { doGenTest(true); }
  public void testPsiStart() throws Exception { doGenTest(true); }
  public void testExprParser() throws Exception { doGenTest(true); }
  public void testTokenSequence() throws Exception { doGenTest(false); }
  public void testTokenChoice() throws Exception { doGenTest(true); }
  public void testTokenChoiceNoSets() throws Exception { doGenTest(true); }
  public void testStub() throws Exception { doGenTest(true); }
  public void testUtilMethods() throws Exception { doGenTest(true); }
  public void testBindersAndHooks() throws Exception { doGenTest(false); }
  public void testAutoRecovery() throws Exception { doGenTest(true); }
  public void testConsumeMethods() throws Exception { doGenTest(false); }

  @Bombed(year = 2030, user = "author", month = 1, day = 1, description = "not implemented")
  public void testUpperRules() throws Exception { doGenTest(true); }
  public void testFixes() throws Exception { doGenTest(true); }

  public void testEmpty() throws Exception {
    myFile = createPsiFile("empty.bnf", "{ }");
    newTestGenerator().generate();
  }

  private ParserGenerator newTestGenerator() {
    return new ParserGenerator((BnfFileImpl)myFile, "", myFullDataPath, "") {

      @Override
      protected PrintWriter openOutputInner(File file) throws IOException {
        String grammarName = FileUtil.getNameWithoutExtension(myFile.getName());
        String fileName = FileUtil.getNameWithoutExtension(file);
        String name = grammarName + (fileName.startsWith(grammarName) || fileName.endsWith("Parser") ? "" : ".PSI") + ".java";
        File targetFile = new File(FileUtilRt.getTempDirectory(), name);
        targetFile.getParentFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream(targetFile, true);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, CharsetToolkit.UTF8));
        out.println("// ---- " + file.getName() + " -----------------");
        return out;
      }
    };
  }

  @Override
  protected String loadFile(@NonNls String name) throws IOException {
    if (name.equals("SelfBnf.bnf")) return super.loadFile("../../grammars/Grammar.bnf");
    if (name.equals("SelfFlex.bnf")) return super.loadFile("../../grammars/JFlex.bnf");
    return super.loadFile(name);
  }

  public void doGenTest(final boolean generatePsi) throws Exception {
    final String name = getTestName(false);
    String text = loadFile(name + "." + myFileExt);
    myFile = createPsiFile(name, text.replaceAll("generatePsi=[^\n]*", "generatePsi=" + generatePsi));
    List<File> filesToCheck = new ArrayList<>();
    filesToCheck.add(new File(FileUtilRt.getTempDirectory(), name + ".java"));
    if (generatePsi) {
      filesToCheck.add(new File(FileUtilRt.getTempDirectory(), name + ".PSI.java"));
    }
    for (File file : filesToCheck) {
      if (file.exists()) {
        assertTrue(file.delete());
      }
    }

    ParserGenerator parserGenerator = newTestGenerator();
    if (generatePsi) parserGenerator.generate();
    else parserGenerator.generateParser();

    for (File file : filesToCheck) {
      assertTrue("Generated file not found: " + file, file.exists());
      final String expectedName = FileUtil.getNameWithoutExtension(file) + ".expected.java";
      String result = FileUtil.loadFile(file, CharsetToolkit.UTF8, true);
      doCheckResult(myFullDataPath, expectedName, result);
    }
  }
}
