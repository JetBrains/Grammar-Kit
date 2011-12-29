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

  public void testSeq() { doFirstTest("r ::= A B", "A"); }
  public void testChoice() { doFirstTest("r ::= A | B", "A", "B"); }
  public void testSeqOpt() { doFirstTest("r ::= [A] B", "A", "B"); }
  public void testChoiceOpt() { doFirstTest("r ::= [A] | B", "", "A", "B"); }
  public void testExternal() { doFirstTest("r ::= s external s ::= some A", "some"); }
  public void testMeta() { doFirstTest("r ::= <<s B>> meta s ::= A <<p>>", "A"); }
  public void testMeta2() { doFirstTest("r ::= <<s B>> meta s ::= <<p1>>", "B"); }
  public void testMeta3() { doFirstTest("r ::= <<s A B>> meta s ::= <<p1>>|<<p2>>", "A", "B"); }

  public void testNext1() { doNextTest("r ::= X s ::= (r [A | B])", "", "A", "B"); }
  public void testNextMore() { doNextTest("r ::= X s ::= (r * [A | B])", "", "A", "B", "X"); }
  public void testNextPredicate() { doNextTest("r ::= X s ::= r &(r * [A | B])", ""); }

  private void doFirstTest(String text, String... expected) { doTest(text, true, expected); }
  private void doNextTest(String text, String... expected) { doTest(text, false, expected); }

  private void doTest(String text, boolean first, String... expected) {
    BnfFile f = (BnfFile)myFixture.configureByText("a.bnf", text);
    List<BnfRule> rules = f.getRules();
    assertFalse(rules.isEmpty());
    Set<String> strings = first? BnfFirstNextAnalyzer.calcFirst(rules.get(0)) : BnfFirstNextAnalyzer.calcNext(rules.get(0));
    String[] result = ArrayUtil.toStringArray(strings);
    Arrays.sort(result);
    assertOrderedEquals(result, expected);
  }
}
