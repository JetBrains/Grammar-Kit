package org.intellij.grammar;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.refactor.BnfInlineRuleProcessor;

/**
 * @author gregsh
 */
public class BnfInlineRuleTest extends BasePlatformTestCase {

  public void testTokenSimple() { doTest("inline ::= token; rule ::= inline", "rule ::= token"); }
  public void testTokenQuantified() { doTest("inline ::= token; rule ::= inline? inline+ inline*", "rule ::= token? token+ token*"); }
  public void testTokenParen() { doTest("inline ::= token; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token) [x token] {x token}"); }
  public void testTokenParenTrivial() { doTest("inline ::= token; rule ::= (inline) [inline] {inline}", "rule ::= token token? token"); }
  public void testTokenChoice() { doTest("inline ::= token; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token) [x | token] {x | token}"); }

  public void testSequenceSimple() { doTest("inline ::= tok en; rule ::= inline", "rule ::= tok en"); }
  public void testSequenceQuantified() { doTest("inline ::= tok en; rule ::= inline? inline+ inline*", "rule ::= (tok en)? (tok en)+ (tok en)*"); }
  public void testSequenceParen() { doTest("inline ::= tok en; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x tok en) [x tok en] {x tok en}"); }
  public void testSequenceParenTrivial() { doTest("inline ::= tok en; rule ::= (inline) [inline] {inline}", "rule ::= tok en [tok en] tok en"); }
  public void testSequenceChoice() { doTest("inline ::= tok en; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok en) [x | tok en] {x | tok en}"); }

  public void testChoiceSimple() { doTest("inline ::= tok|en; rule ::= inline", "rule ::= tok|en"); }
  public void testChoiceQuantified() { doTest("inline ::= tok|en; rule ::= inline? inline+ inline*", "rule ::= (tok|en)? (tok|en)+ (tok|en)*"); }
  public void testChoiceParen() { doTest("inline ::= tok|en; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}"); }
  public void testChoiceParenTrivial() { doTest("inline ::= tok|en; rule ::= (inline) [inline] {inline}", "rule ::= (tok|en) [tok|en] {tok|en}"); }
  public void testChoiceChoice() { doTest("inline ::= tok|en; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}"); }

  public void testOptionalSimple() { doTest("inline ::= token?; rule ::= inline", "rule ::= token?"); }
  public void testOptionalQuantified() { doTest("inline ::= token?; rule ::= inline? inline+ inline*", "rule ::= token? token* token*"); }
  public void testOptionalParen() { doTest("inline ::= token?; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token?) [x token?] {x token?}"); }
  public void testOptionalParenTrivial() { doTest("inline ::= token?; rule ::= (inline) [inline] {inline}", "rule ::= token? token? token?"); }
  public void testOptionalChoice() { doTest("inline ::= token?; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token?) [x | token?] {x | token?}"); }

  public void testOneManylSimple() { doTest("inline ::= token+; rule ::= inline", "rule ::= token+"); }
  public void testOneManylQuantified() { doTest("inline ::= token+; rule ::= inline? inline+ inline*", "rule ::= token* token+ token*"); }
  public void testOneManylParen() { doTest("inline ::= token+; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token+) [x token+] {x token+}"); }
  public void testOneManylParenTrivial() { doTest("inline ::= token+; rule ::= (inline) [inline] {inline}", "rule ::= token+ token* token+"); }
  public void testOneManylChoice() { doTest("inline ::= token+; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token+) [x | token+] {x | token+}"); }

  public void testZeroManylSimple() { doTest("inline ::= token*; rule ::= inline", "rule ::= token*"); }
  public void testZeroManylQuantified() { doTest("inline ::= token*; rule ::= inline? inline+ inline*", "rule ::= token* token* token*"); }
  public void testZeroManylParen() { doTest("inline ::= token*; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token*) [x token*] {x token*}"); }
  public void testZeroManylParenTrivial() { doTest("inline ::= token*; rule ::= (inline) [inline] {inline}", "rule ::= token* token* token*"); }
  public void testZeroManylChoice() { doTest("inline ::= token*; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token*) [x | token*] {x | token*}"); }

  public void testOptSequenceSimple() { doTest("inline ::= [tok en]; rule ::= inline", "rule ::= [tok en]"); }
  public void testOptSequenceQuantified() { doTest("inline ::= [tok en]; rule ::= inline? inline+ inline*", "rule ::= [tok en] (tok en)* (tok en)*"); }
  public void testOptSequenceParen() { doTest("inline ::= [tok en]; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x [tok en]) [x [tok en]] {x [tok en]}"); }
  public void testOptSequenceParenTrivial() { doTest("inline ::= [tok en]; rule ::= (inline) [inline] {inline}", "rule ::= [tok en] [tok en] [tok en]"); }
  public void testOptSequenceChoice() { doTest("inline ::= [tok en]; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | [tok en]) [x | [tok en]] {x | [tok en]}"); }

  public void testAltChoiceSimple() { doTest("inline ::= {tok|en}; rule ::= inline", "rule ::= tok|en"); }
  public void testAltChoiceQuantified() { doTest("inline ::= {tok|en}; rule ::= inline? inline+ inline*", "rule ::= {tok|en}? {tok|en}+ {tok|en}*"); }
  public void testAltChoiceParen() { doTest("inline ::= {tok|en}; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x {tok|en}) [x {tok|en}] {x {tok|en}}"); }
  public void testAltChoiceParenTrivial() { doTest("inline ::= {tok|en}; rule ::= (inline) [inline] {inline}", "rule ::= (tok|en) [tok|en] {tok|en}"); }
  public void testAltChoiceChoice() { doTest("inline ::= {tok|en}; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}"); }

  public void testParenTokenSimple() { doTest("inline ::= (token); rule ::= inline", "rule ::= token"); }
  public void testParenTokenQuantified() { doTest("inline ::= (token); rule ::= inline? inline+ inline*", "rule ::= token? token+ token*"); }
  public void testParenTokenParen() { doTest("inline ::= (token); rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token) [x token] {x token}"); }
  public void testParenTokenParenTrivial() { doTest("inline ::= (token); rule ::= (inline) [inline] {inline}", "rule ::= token token? token"); }
  public void testParenTokenChoice() { doTest("inline ::= (token); rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token) [x | token] {x | token}"); }

  public void testParenSequenceSimple() { doTest("inline ::= (tok en); rule ::= inline", "rule ::= tok en"); }
  public void testParenSequenceQuantified() { doTest("inline ::= (tok en); rule ::= inline? inline+ inline*", "rule ::= (tok en)? (tok en)+ (tok en)*"); }
  public void testParenSequenceParen() { doTest("inline ::= (tok en); rule ::= (x inline) [x inline] {x inline}", "rule ::= (x tok en) [x tok en] {x tok en}"); }
  public void testParenSequenceParenTrivial() { doTest("inline ::= (tok en); rule ::= (inline) [inline] {inline}", "rule ::= tok en [tok en] tok en"); }
  public void testParenSequenceChoice() { doTest("inline ::= (tok en); rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok en) [x | tok en] {x | tok en}"); }

  public void testParenChoiceSimple() { doTest("inline ::= (tok|en); rule ::= inline", "rule ::= tok|en"); }
  public void testParenChoiceQuantified() { doTest("inline ::= (tok|en); rule ::= inline? inline+ inline*", "rule ::= (tok|en)? (tok|en)+ (tok|en)*"); }
  public void testParenChoiceParen() { doTest("inline ::= (tok|en); rule ::= (x inline) [x inline] {x inline}", "rule ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}"); }
  public void testParenChoiceParenTrivial() { doTest("inline ::= (tok|en); rule ::= (inline) [inline] {inline}", "rule ::= (tok|en) [tok|en] {tok|en}"); }
  public void testParenChoiceChoice() { doTest("inline ::= (tok|en); rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}"); }

  public void testParenOptionalSimple() { doTest("inline ::= (tok en)?; rule ::= inline", "rule ::= (tok en)?"); }
  public void testParenOptionalQuantified() { doTest("inline ::= (tok en)?; rule ::= inline? inline+ inline*", "rule ::= (tok en)? (tok en)* (tok en)*"); }
  public void testParenOptionalParen() { doTest("inline ::= (tok en)?; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x (tok en)?) [x (tok en)?] {x (tok en)?}"); }
  public void testParenOptionalParenTrivial() { doTest("inline ::= (tok en)?; rule ::= (inline) [inline] {inline}", "rule ::= (tok en)? (tok en)? (tok en)?"); }
  public void testParenOptionalChoice() { doTest("inline ::= (tok en)?; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | (tok en)?) [x | (tok en)?] {x | (tok en)?}"); }

  public void testChoiceInChoice() { doTest("inline ::= (tok|en) x; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | (tok|en) x) [x | (tok|en) x] {x | (tok|en) x}"); }

  public void testSimpleMetaRule() { doTest("meta inline ::= <<p>> (x <<p>>) *; rule ::= <<inline y>>", "rule ::= y (x y) *"); }
  public void testComplexMetaRule() { doTest("meta inline ::= <<p>> (x <<p>>) *; rule ::= <<inline <<inline y>>>>", "rule ::= y (x y) * (x y (x y) *) *"); }
  public void testComplexExternalMetaRule() { doTest("meta inline ::= <<p>> (x <<p>>) *; external rule ::= inline <<inline y>>", "rule ::= y (x y) * (x y (x y) *) *"); }


  private void doTest(/*@Language("BNF")*/ String text, /*@Language("BNF")*/ String expected) {
    PsiFile file = myFixture.configureByText("a.bnf", text);
    BnfRule rule = PsiTreeUtil.getChildOfType(file, BnfRule.class);
    assertNotNull(rule);
    new BnfInlineRuleProcessor(rule, getProject(), null, false).run();
    assertSameLines(expected, file.getText());
  }
}
