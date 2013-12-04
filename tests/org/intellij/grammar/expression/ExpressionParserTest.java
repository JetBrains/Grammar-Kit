package org.intellij.grammar.expression;

import com.intellij.lang.LanguageBraceMatching;
import org.intellij.grammar.AbstractParsingTestCase;
import org.intellij.grammar.BnfBraceMatcher;

/**
 * @author gregsh
 */
public class ExpressionParserTest extends AbstractParsingTestCase {
  public ExpressionParserTest() {
    super("parser/expression", "expr", new ExpressionParserDefinition());
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new BnfBraceMatcher());
  }

  public void testSimple() { doTest(true); }

}
