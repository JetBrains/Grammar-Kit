package org.intellij.grammar;

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
    addExplicitExtension(LanguageBraceMatching.INSTANCE, myLanguage, new BnfBraceMatcher());
  }

  public void testBnfGrammar() { doTest(true); }
  public void testSelf() { doTest(true); }
  public void testBrokenAttr() { doTest(true); }
  public void testBrokenEverything() { doTest(true); }
  public void testAlternativeSyntax() { doTest(true); }
  public void testExternalExpression() { doTest(true); }
  public void testFixes() { doTest(true); }
  public void testBrokenAttrBeforeEOF() { doTest(true); }

  @Override
  protected String loadFile(@NonNls String name) throws IOException {
    if (name.equals("BnfGrammar.bnf")) return super.loadFile("../../grammars/Grammar.bnf");
    return super.loadFile(name);
  }
}
