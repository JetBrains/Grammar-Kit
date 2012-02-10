package org.intellij.grammar;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.intellij.grammar.inspection.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 3:02 PM
 *
 * @author Vadim Romansky
 */
public class BnfInspectionTest extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "testData/inspection";
  }

  public void testDuplicateDefinition() { doTest("<warning>rule</warning>::= blablabla rule1" + "\n" + "<warning>rule</warning> ::=aaaaaaaaa"); }
  public void testSuspiciousToken() { doTest("rule ::= <warning>suspicious_token</warning>"); }
  public void testIdenticalBranchInChoice() { doTest("grammar ::= <warning>token</warning>|<warning>token</warning>"); }
  public void testComplexIdenticalBranchInChoice() { doTest("grammar ::= a b (c | <warning>(d e*)</warning>|<warning>(d /* */ e*)</warning>)"); }
  public void testLeftRecursion1() { doTest("<warning>grammar</warning> ::= grammar"); }
  public void testLeftRecursion2() { doTest("<warning>grammar</warning> ::= [r] [(r | grammar)]"); }
  public void testLeftRecursion3() { doTest("<warning>grammar</warning> ::= [r] [(r | rule)] <warning>rule</warning> ::= [r] ([r | grammar] r)"); }
  public void testLeftRecursion4() { doTest("meta m ::= (<<p1>> | <<p2>>) <warning>r</warning> ::= <<m x r>>"); }
  public void testUnreachableBranch1() { doTest("m ::= <warning>r</warning> | B | C r ::= A?"); }
  public void testUnreachableBranch2() { doTest("m ::= A<warning>||</warning> B | C"); }
  public void testUnreachableBranch3() { doTest("m ::=<warning>|</warning> B <warning>|</warning>"); }
  public void testUnreachableBranch4() { doTest("m ::=(A | (<warning>|</warning> B<warning>|</warning>))"); }
  public void testNeverMatchingBranch1() { doTest("m ::= <warning>! A r</warning> | B | C r ::= A"); }
  public void testSelf2() { doFileTest(); }

  private void doFileTest() {
    myFixture.configureByFile(getTestName(false)+".bnf");
    doTest();
  }

  private void doTest(String text) {
    myFixture.configureByText("a.bnf", text);
    doTest();
  }

  private void doTest() {
    myFixture.enableInspections(BnfSuspiciousTokenInspection.class,
                                BnfDuplicateRuleInspection.class,
                                BnfIdenticalChoiceBranchesInspection.class,
                                BnfLeftRecursionInspection.class,
                                BnfUnreachableChoiceBranchInspection.class);
    myFixture.checkHighlighting(true, false, false);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

}
