package org.intellij.grammar;

/**
 * @author gregsh
 */
public class BnfGeneratorTestCase extends AbstractParsingTestCase {
  public BnfGeneratorTestCase(String testDataName) {
    super(testDataName, "bnf", new BnfParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    LightPsi.Init.initExtensions(getProject());
  }

}
