package org.intellij.grammar;

import com.intellij.testFramework.ParsingTestCase;

/**
 * @author gregsh
 */
public class BnfGeneratorTestCase extends ParsingTestCase {
  public BnfGeneratorTestCase(String testDataName) {
    super(testDataName, "bnf", new BnfParserDefinition());
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    LightPsi.Init.initExtensions(getProject(), getPsiManager());
  }

}
