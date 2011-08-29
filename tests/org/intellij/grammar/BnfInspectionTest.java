package org.intellij.grammar;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.intellij.grammar.inspection.BnfInspectionToolProvider;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/29/11
 * Time: 3:02 PM
 *
 * @author Vadim Romansky
 */
public class BnfInspectionTest extends JavaCodeInsightFixtureTestCase {
  @Override
  public String getTestDataPath() {
    return  "testData/inspection";
  }
  public void testDuplicateDefinition(){
    doTest();
  }
  public void testSuspiciousToken(){
    doTest();
  }

  private void doTest() {
    myFixture.configureByFile(getTestName(false)+".bnf");
    myFixture.enableInspections(new BnfInspectionToolProvider());
    myFixture.checkHighlighting(true, false, false);
  }
}
