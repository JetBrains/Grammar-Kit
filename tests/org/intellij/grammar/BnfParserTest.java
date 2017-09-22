package org.intellij.grammar;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;

/**
 * @author gregsh
 */
public class BnfParserTest extends AbstractParsingTestCase {

  public BnfParserTest() {
    super("parser", "bnf", new BnfParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    addExplicitExtension(LanguageASTFactory.INSTANCE, myLanguage, new BnfASTFactory());
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new BnfBraceMatcher());
  }

  public void testSelfBnf() { doTest(true); }
  public void testSelfFlex() { doTest(true); }
  public void testBrokenAttr() { doTest(true); }
  public void testBrokenEverything() { doTest(true); }
  public void testAlternativeSyntax() { doTest(true); }
  public void testExternalExpression() { doTest(true); }
  public void testFixes() { doTest(true); }
  public void testBrokenAttrBeforeEOF() { doTest(true); }

  @Override
  protected String loadFile(@NonNls String name) throws IOException {
    String adjusted;
    if ("SelfBnf.bnf".equals(name)) adjusted = "../../grammars/Grammar.bnf";
    else if ("SelfFlex.bnf".equals(name)) adjusted = "../../grammars/JFlex.bnf";
    else adjusted = name;
    return super.loadFile(adjusted);
  }
}
