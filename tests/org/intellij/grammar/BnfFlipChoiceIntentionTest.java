package org.intellij.grammar;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.intellij.grammar.intention.BnfFlipChoiceIntention;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/1/11
 * Time: 6:42 PM
 *
 * @author Vadim Romansky
 */
public class BnfFlipChoiceIntentionTest extends LightPlatformCodeInsightFixtureTestCase {
  public void testCaretAtSeparator() throws Exception {doTest("rule ::= a <caret>| b","rule ::= b | a");}
  public void testCaretBeforeSeparator() throws Exception {doTest("rule ::= a<caret> | b","rule ::= b | a");}
  public void testCaretAfterSeparator() throws Exception {doTest("rule ::= a | <caret>b","rule ::= b | a");}
  public void testCaretInComment() throws Exception {doTest("rule ::= a | /* <caret>*/ b","rule ::= b | /* */ a");}
  public void testMultipleChoice() throws Exception {doTest("rule ::= a | b | c <caret>| d","rule ::= a | b | d | c");}
  public void testComplexCase() throws Exception {doTest("rule ::= a | b | c c c <caret>| d [d d]","rule ::= a | b | d [d d] | c c c");}

  private void doTest(/*@Language("BNF")*/ String text, /*@Language("BNF")*/ final String expected) throws IOException {
    myFixture.configureByText("a.bnf", text);
    final IntentionAction action = new BnfFlipChoiceIntention();
    assertTrue("intention not available", action.isAvailable(getProject(), myFixture.getEditor(), myFixture.getFile()));
    new WriteCommandAction.Simple(getProject()) {
      @Override
      protected void run() throws Throwable {
        action.invoke(getProject(), myFixture.getEditor(), myFixture.getFile());
      }
    }.execute();
    assertSameLines(expected, myFixture.getFile().getText());
  }
}
