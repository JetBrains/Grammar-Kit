package org.intellij.grammar;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.intellij.grammar.inspection.BnfInspectionToolProvider;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 3:02 PM
 *
 * @author Vadim Romansky
 */
public class BnfInspectionTest extends LightCodeInsightFixtureTestCase {
  @Override
  public String getBasePath() {
    return  "testData/inspection";
  }
  public void testDuplicateDefinition(){
    doTest("<warning>rule</warning>::= blablabla rule1" +"\n" + "<warning>rule</warning> ::=aaaaaaaaa");
  }
  public void testSuspiciousToken(){
    doTest("rule ::= <warning>suspicious_token</warning>");
  }
  public void testIdenticalBranchInChoice(){
    doTest("grammar ::= <warning>token</warning>|<warning>token</warning>");
  }
  public void testComplexIdenticalBranchInChoice(){
    doTest("grammar ::= a b (c | <warning>(d e*)</warning>|<warning>(d /* */ e*)</warning>)");
  }

  private void doTest(String text) {
    myFixture.configureByText("a.bnf", text);
    myFixture.enableInspections(new BnfInspectionToolProvider());
    myFixture.checkHighlighting(true, false, false);
  }
}
