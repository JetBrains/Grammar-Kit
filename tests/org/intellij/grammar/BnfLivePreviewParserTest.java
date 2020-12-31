package org.intellij.grammar;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.LightVirtualFile;
import org.intellij.grammar.livePreview.LivePreviewHelper;
import org.intellij.grammar.livePreview.LivePreviewLanguage;
import org.intellij.grammar.psi.BnfFile;

import java.io.File;
import java.io.IOException;

/**
 * @author gregsh
 */
public class BnfLivePreviewParserTest extends BnfGeneratorTestCase {

  public BnfLivePreviewParserTest() {
    super("livePreview");
    myFileExt = "live.txt";
  }

  public void testEmpty() throws IOException { doTest("Empty.live.txt"); }
  public void testJsonRecovery() throws IOException { doTest("Json.bnf"); }
  public void testLivePreviewTutorial() throws IOException { doTest(); }
  public void testAutoRecovery() throws IOException { doTest(); }
  public void testExprParser() throws IOException { doTest("../generator/ExprParser.bnf"); }
  public void testUpperRules() throws IOException { doTest("../generator/UpperRules.bnf"); }

  public void testCase75() throws IOException { doTest(); }
  public void testCase153() throws IOException { doTest(); }
  public void testCase254() throws IOException { doTest(); }

  public void testLiveFixes() throws IOException { doTest(); }

  protected void doTest() throws IOException {
    doTest(getTestName(false) + ".bnf");
  }

  @Override
  protected void doTest(String grammarFile) throws IOException {
    File grammarIOFile = new File(myFullDataPath, grammarFile);
    assertNotNull(grammarFile + "not found", grammarIOFile.exists());
    LightVirtualFile grammarVFile = new LightVirtualFile(grammarFile, FileUtil.loadFile(grammarIOFile));
    myLanguage = BnfLanguage.INSTANCE;
    BnfFile grammarPsi = (BnfFile) createFile(grammarVFile);
    myLanguage = LivePreviewHelper.getLanguageFor(grammarPsi);
    try {
      super.doTest(true);
    }
    finally {
      LivePreviewHelper.unregisterLanguageExtensions((LivePreviewLanguage) myLanguage);
    }
  }

}
