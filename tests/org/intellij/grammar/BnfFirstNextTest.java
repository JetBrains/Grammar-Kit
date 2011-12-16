package org.intellij.grammar;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.util.ArrayUtil;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author gregsh
 */
public class BnfFirstNextTest extends LightCodeInsightFixtureTestCase {

  public void testSeq() { doTest("r ::= A B", "A"); }
  public void testChoice() { doTest("r ::= A | B", "A", "B"); }
  public void testSeqOpt() { doTest("r ::= [A] B", "A", "B"); }
  public void testChoiceOpt() { doTest("r ::= [A] | B", "", "A", "B"); }
  public void testExternal() { doTest("r ::= s external s ::= some A", "some"); }
  public void testMeta() { doTest("r ::= <<s B>> meta s ::= A <<p>>", "A"); }
  public void testMeta2() { doTest("r ::= <<s B>> meta s ::= <<p1>>", "B"); }
  public void testMeta3() { doTest("r ::= <<s A B>> meta s ::= <<p1>>|<<p2>>", "A", "B"); }

  private void doTest(String text, String... expected) {
    BnfFile f = (BnfFile)myFixture.configureByText("a.bnf", text);
    List<BnfRule> rules = f.getRules();
    assertFalse(rules.isEmpty());
    Set<String> strings = BnfFirstNextAnalyzer.calcFirst(rules.get(0));
    String[] result = ArrayUtil.toStringArray(strings);
    Arrays.sort(result);
    assertOrderedEquals(result, expected);
  }
}
