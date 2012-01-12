package org.intellij.grammar;

import com.intellij.lang.LanguageBraceMatching;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.testFramework.ParsingTestCase;

/**
 * @author gregsh
 */
public class BnfParserTest extends ParsingTestCase {
  public BnfParserTest() {
    super("parser", "bnf", new BnfParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new BnfBraceMatcher());
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  public void testSelf() {
    doTest(true);
  }

  public void testSelf2() {
    doTest(true);
  }

  public void testBrokenAttr() {
    doTest(true);
  }

  public void testBrokenEverything() {
    doTest(true);
  }

  public void testAlternativeSyntax() {
    doTest(true);
  }

  public void testExternalExpression() {
    doTest(true);
  }

  public void testFixes() {
    doTest(true);
  }
}
