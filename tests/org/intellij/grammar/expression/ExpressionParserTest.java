package org.intellij.grammar.expression;

import com.intellij.lang.LanguageBraceMatching;
import com.intellij.testFramework.ParsingTestCase;
import org.intellij.grammar.BnfBraceMatcher;

/**
 * @author gregsh
 */
public class ExpressionParserTest extends ParsingTestCase {
  public ExpressionParserTest() {
    super("parser/expression", "expr", new ExpressionParserDefinition());
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

  public void testSimple() { doTest(true); }

}
