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
  public void testAttr1() throws Throwable { doTestVariants("{<caret>}", CompletionType.BASIC, 1, CheckType.INCLUDES, "pin"); }
  public void testAttr11() throws Throwable { doTestVariants("{<caret>}", CompletionType.BASIC, 1, CheckType.EXCLUDES, "private"); }
  public void testRule1() throws Throwable { doTestVariants("rule::= <caret>", CompletionType.BASIC, 1, CheckType.INCLUDES, "rule"); }

  protected void doTestVariants(String txt, CompletionType type, int count, CheckType checkType, String... variants) throws Throwable {
    myFixture.configureByText("a.bnf", txt);
    doTestVariantsInner(type, count, checkType, variants);
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