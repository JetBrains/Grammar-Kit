package org.intellij.grammar;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.intellij.grammar.BnfCompletionTest.CheckType.*;

/**
 * @author gregsh
 */
public class BnfCompletionTest extends JavaCodeInsightFixtureTestCase {

  public void testKeywords1() { variants("", INCLUDES, "private", "meta"); }
  public void testKeywords2() { variants("{a=b}<caret>", INCLUDES, "private", "meta"); }
  public void testKeywords21() { variants("{a=b}<caret>", EXCLUDES, "pin"); }
  public void testKeywords3() { variants("{a=b} private rule::= <caret>", INCLUDES, "private", "meta"); }
  public void testKeywords4() { variants("{a=b} <caret>rule::=", INCLUDES, "private", "meta"); }
  public void testKeywords5() { variants("{a=b} pri<caret>rule::=", INCLUDES, "private"); }
  public void testKeywords6() { variants("rule::= pri<caret>other ::=", INCLUDES, "private"); }
  public void testKeywords7() { variants("pr<caret>rule::=", INCLUDES, "private"); }
  public void testKeywords8() { variants("rule::=\npr<caret>rule::=", INCLUDES, "private"); }
  public void testKeywordsNotInComments() { variants("root ::= //rule ::= p<caret>", EXCLUDES, "public", "private"); }

  public void testAttr1() { variants("{<caret>}", INCLUDES, "pin"); }
  public void testAttr2() { variants("{<caret>}", INCLUDES, "pin"); }
  public void testAttr3() { variants("{pin=1 rec<caret>overUntil=abc}", INCLUDES, "recoverWhile"); }
  public void testAttr4() { variants("a::= {name=\"A\" p<caret>=\"\"}", INCLUDES, "pin", "elementType"); }
  public void testAttr5() { variants("expr ::= {name=\"A\" e<caret>xtends=\"\"}", EXCLUDES, "expr"); }
  public void testAttr6() { variants("expr ::= {<caret>e= }", EXCLUDES, "expr"); }

  public void testToken1() { variants("a ::= TOK TOKEN b ::= T<caret>", INCLUDES, "TOKEN"); }
  public void testToken2() { variants("{tokens=[TOK TOKEN]} a ::= T<caret>", INCLUDES, "TOKEN"); }

  public void testRule1() { variants("rule::= <caret>", INCLUDES, "rule"); }
  public void testRule2() { textAfter("<with space>::= ws<caret>", CompletionType.BASIC, "<with space>::= <with space>");
  }

  public void testExternalMethod1() { variants(initUtil() + "external rule::= <caret>",
                                               INCLUDES, "eofX", "eofY"); }
  public void testExternalMethod2() { variants(initUtil() + "external rule::= abc <caret>",
                                               EXCLUDES, "eofX", "eofY"); }
  public void testExternalMethod3() { variants(initUtil() + "root ::= external rule ::= <caret>",
                                               EXCLUDES, "root"); }
  public void testExternalMethod4() { variants(initUtil() + "root ::= external rule ::= abc <caret>",
                                               INCLUDES, "root"); }
  public void testExternalMethod5() { variants(initUtil() + "root ::= meta metaR ::= rule ::= <<<caret> >>",
                                               INCLUDES, "eofX", "eofY", "metaR"); }
  public void testExternalMethod6() { variants(initUtil() + "root ::= rule ::= <<<caret> >>",
                                               EXCLUDES, "root"); }

  private String initUtil() {
    myFixture.addClass("public class X { public static boolean eofX(com.intellij.lang.PsiBuilder b, int l) { } }");
    myFixture.addClass("public class Y extends X { public static boolean eofY(com.intellij.lang.PsiBuilder b, int l) { } }");
    return "{ parserUtilClass='Y' } ";
  }

  protected void variants(String txt, CheckType checkType, String... variants) {
    myFixture.configureByText("a.bnf", txt);
    doVariantsTestInner(myFixture, CompletionType.BASIC, checkType, variants);
  }

  protected void textAfter(String txt, CompletionType type, String textAfter) {
    myFixture.configureByText("a.bnf", txt);
    myFixture.complete(type, 1);
    assertEquals(textAfter, myFixture.getEditor().getDocument().getText());
  }

  public enum CheckType {EQUALS, INCLUDES, EXCLUDES}

  public static void doVariantsTestInner(CodeInsightTestFixture fixture, CompletionType type, CheckType checkType, String... variants) {
    fixture.complete(type, 1);
    List<String> stringList = fixture.getLookupElementStrings();
    assertNotNull(stringList);
    Collection<String> varList = new ArrayList<>(Arrays.asList(variants));
    if (checkType == EQUALS) {
      UsefulTestCase.assertSameElements(stringList, variants);
    }
    else if (checkType == INCLUDES) {
      varList.removeAll(stringList);
      assertTrue("Missing: " + varList + " in " + stringList, varList.isEmpty());
    }
    else if (checkType == EXCLUDES) {
      varList.retainAll(stringList);
      assertTrue("Unexpected: " + varList + " in " + stringList, varList.isEmpty());
    }
  }

}