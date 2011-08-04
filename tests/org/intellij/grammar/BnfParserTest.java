package org.intellij.grammar;

import com.intellij.openapi.application.PathManager;
import com.intellij.testFramework.ParsingTestCase;

/**
 * @author gregsh
 */
public class BnfParserTest extends ParsingTestCase {
  public BnfParserTest() {
    super("parser", "bnf", new BnfParserDefinition());
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  public void testSelf() {
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
}
