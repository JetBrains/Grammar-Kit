package org.intellij.grammar;

import com.intellij.lang.LanguageASTFactory;
import com.intellij.lang.LanguageBraceMatching;

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
    addExplicitExtension(LanguageASTFactory.INSTANCE, myLanguage, new BnfASTFactory());
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new BnfBraceMatcher());
  }

}
