package org.intellij.grammar;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author gregsh
 */
public class BnfCompletionTest extends JavaCodeInsightFixtureTestCase {
  enum CheckType { EQUALS, INCLUDES, EXCLUDES }

  public void testKeywords1() throws Throwable { doTestVariants("", CompletionType.BASIC, 1, CheckType.INCLUDES, "private", "meta"); }
  public void testKeywords2() throws Throwable { doTestVariants("{a=b}<caret>", CompletionType.BASIC, 1, CheckType.INCLUDES, "private", "meta"); }
  public void testKeywords21() throws Throwable { doTestVariants("{a=b}<caret>", CompletionType.BASIC, 1, CheckType.EXCLUDES, "pin"); }
  public void testKeywords3() throws Throwable { doTestVariants("{a=b} private rule::= <caret>", CompletionType.BASIC, 1, CheckType.INCLUDES, "private", "meta"); }
  public void testKeywords4() throws Throwable { doTestVariants("{a=b} <caret>rule::=", CompletionType.BASIC, 1, CheckType.INCLUDES, "private", "meta"); }
  public void testKeywords5() throws Throwable { doTestVariants("{a=b} pri<caret>rule::=", CompletionType.BASIC, 1, CheckType.INCLUDES, "private"); }
  public void testKeywords6() throws Throwable { doTestVariants("rule::= pri<caret>other ::=", CompletionType.BASIC, 1, CheckType.INCLUDES, "private"); }
  public void testKeywords7() throws Throwable { doTestVariants("pr<caret>rule::=", CompletionType.BASIC, 1, CheckType.INCLUDES, "private"); }
  public void testKeywords8() throws Throwable { doTestVariants("rule::=\npr<caret>rule::=", CompletionType.BASIC, 1, CheckType.INCLUDES, "private"); }
  public void testKeywordsNotInComments() throws Throwable { doTestVariants("root ::= //rule ::= p<caret>", CompletionType.BASIC, 1, CheckType.EXCLUDES, "public", "private"); }

  public void testAttr1() throws Throwable { doTestVariants("{<caret>}", CompletionType.BASIC, 1, CheckType.INCLUDES, "pin"); }
  public void testAttr2() throws Throwable { doTestVariants("{<caret>}", CompletionType.BASIC, 1, CheckType.INCLUDES, "pin"); }
  public void testAttr3() throws Throwable { doTestVariants("{pin=1 rec<caret>overUntil=abc}", CompletionType.BASIC, 1, CheckType.INCLUDES, "recoverWhile"); }
  public void testAttr4() throws Throwable { doTestVariants("a::= {name=\"A\" p<caret>=\"\"}", CompletionType.BASIC, 1, CheckType.INCLUDES, "pin", "elementType"); }
  public void testRule1() throws Throwable { doTestVariants("rule::= <caret>", CompletionType.BASIC, 1, CheckType.INCLUDES, "rule"); }
  public void testRule2() throws Throwable { doTestTextAfter("<with space>::= ws<caret>", CompletionType.BASIC, 1, "<with space>::= <with space>"); }

  public void testExternalMethod1() throws Throwable { doTestVariants(initUtil() + "external rule::= <caret>", CompletionType.BASIC, 1, CheckType.INCLUDES, "eofX", "eofY"); }
  public void testExternalMethod2() throws Throwable { doTestVariants(initUtil() + "external rule::= abc <caret>", CompletionType.BASIC, 1, CheckType.EXCLUDES, "eofX", "eofY"); }
  public void testExternalMethod3() throws Throwable { doTestVariants(initUtil() + "root ::= external rule ::= <caret>", CompletionType.BASIC, 1, CheckType.EXCLUDES, "root"); }
  public void testExternalMethod4() throws Throwable { doTestVariants(initUtil() + "root ::= external rule ::= abc <caret>", CompletionType.BASIC, 1, CheckType.INCLUDES, "root"); }
  public void testExternalMethod5() throws Throwable { doTestVariants(initUtil() + "root ::= meta metaR ::= rule ::= <<<caret> >>", CompletionType.BASIC, 1, CheckType.INCLUDES, "eofX", "eofY", "metaR"); }
  public void testExternalMethod6() throws Throwable { doTestVariants(initUtil() + "root ::= rule ::= <<<caret> >>", CompletionType.BASIC, 1, CheckType.EXCLUDES, "root"); }

  private String initUtil() {
    myFixture.addClass("public class X { public static boolean eofX(com.intellij.lang.PsiBuilder b, int l) { } }");
    myFixture.addClass("public class Y extends X { public static boolean eofY(com.intellij.lang.PsiBuilder b, int l) { } }");
    return "{ parserUtilClass='Y' } ";
  }

  protected void doTestVariants(String txt, CompletionType type, int count, CheckType checkType, String... variants) throws Throwable {
    myFixture.configureByText("a.bnf", txt);
    doTestVariantsInner(type, count, checkType, variants);
  }

  protected void doTestTextAfter(String txt, CompletionType type, int count, String textAfter) throws Throwable {
    myFixture.configureByText("a.bnf", txt);
    myFixture.complete(type, count);
    assertEquals(textAfter, myFixture.getEditor().getDocument().getText());
  }

  protected void doTestVariantsInner(CompletionType type, int count, CheckType checkType, String... variants) throws Throwable {
    myFixture.complete(type, count);
    List<String> stringList = myFixture.getLookupElementStrings();
    assertNotNull(stringList);
    Collection<String> varList = new ArrayList<String>(Arrays.asList(variants));
    if (checkType == CheckType.EQUALS) {
      UsefulTestCase.assertSameElements(stringList, variants);
    }
    else if (checkType == CheckType.INCLUDES) {
      varList.removeAll(stringList);
      assertTrue("Missing variants: " + varList, varList.isEmpty());
    }
    else if (checkType == CheckType.EXCLUDES) {
      varList.retainAll(stringList);
      assertTrue("Unexpected variants: "+varList, varList.isEmpty());
    }
  }

}