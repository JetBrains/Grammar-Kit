package org.intellij.grammar;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightCodeInsightTestCase;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.refactor.BnfInlineRuleProcessor;

/**
 * @author gregsh
 */
public class BnfInlineRuleTest extends LightCodeInsightTestCase {

  public void testTokenSimple() throws Exception { doTest("inline ::= token; rule ::= inline", "rule ::= token"); }
  public void testTokenQuantified() throws Exception { doTest("inline ::= token; rule ::= inline? inline+ inline*", "rule ::= token? token+ token*"); }
  public void testTokenParen() throws Exception { doTest("inline ::= token; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token) [x token] {x token}"); }
  public void testTokenParenTrivial() throws Exception { doTest("inline ::= token; rule ::= (inline) [inline] {inline}", "rule ::= token token? token"); }
  public void testTokenChoice() throws Exception { doTest("inline ::= token; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token) [x | token] {x | token}"); }

  public void testSequenceSimple() throws Exception { doTest("inline ::= tok en; rule ::= inline", "rule ::= tok en"); }
  public void testSequenceQuantified() throws Exception { doTest("inline ::= tok en; rule ::= inline? inline+ inline*", "rule ::= (tok en)? (tok en)+ (tok en)*"); }
  public void testSequenceParen() throws Exception { doTest("inline ::= tok en; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x tok en) [x tok en] {x tok en}"); }
  public void testSequenceParenTrivial() throws Exception { doTest("inline ::= tok en; rule ::= (inline) [inline] {inline}", "rule ::= tok en [tok en] tok en"); }
  public void testSequenceChoice() throws Exception { doTest("inline ::= tok en; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok en) [x | tok en] {x | tok en}"); }

  public void testChoiceSimple() throws Exception { doTest("inline ::= tok|en; rule ::= inline", "rule ::= tok|en"); }
  public void testChoiceQuantified() throws Exception { doTest("inline ::= tok|en; rule ::= inline? inline+ inline*", "rule ::= (tok|en)? (tok|en)+ (tok|en)*"); }
  public void testChoiceParen() throws Exception { doTest("inline ::= tok|en; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}"); }
  public void testChoiceParenTrivial() throws Exception { doTest("inline ::= tok|en; rule ::= (inline) [inline] {inline}", "rule ::= (tok|en) [tok|en] {tok|en}"); }
  public void testChoiceChoice() throws Exception { doTest("inline ::= tok|en; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}"); }

  public void testOptionalSimple() throws Exception { doTest("inline ::= token?; rule ::= inline", "rule ::= token?"); }
  public void testOptionalQuantified() throws Exception { doTest("inline ::= token?; rule ::= inline? inline+ inline*", "rule ::= token? token* token*"); }
  public void testOptionalParen() throws Exception { doTest("inline ::= token?; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token?) [x token?] {x token?}"); }
  public void testOptionalParenTrivial() throws Exception { doTest("inline ::= token?; rule ::= (inline) [inline] {inline}", "rule ::= token? token? token?"); }
  public void testOptionalChoice() throws Exception { doTest("inline ::= token?; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token?) [x | token?] {x | token?}"); }

  public void testOneManylSimple() throws Exception { doTest("inline ::= token+; rule ::= inline", "rule ::= token+"); }
  public void testOneManylQuantified() throws Exception { doTest("inline ::= token+; rule ::= inline? inline+ inline*", "rule ::= token* token+ token*"); }
  public void testOneManylParen() throws Exception { doTest("inline ::= token+; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token+) [x token+] {x token+}"); }
  public void testOneManylParenTrivial() throws Exception { doTest("inline ::= token+; rule ::= (inline) [inline] {inline}", "rule ::= token+ token* token+"); }
  public void testOneManylChoice() throws Exception { doTest("inline ::= token+; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token+) [x | token+] {x | token+}"); }

  public void testZeroManylSimple() throws Exception { doTest("inline ::= token*; rule ::= inline", "rule ::= token*"); }
  public void testZeroManylQuantified() throws Exception { doTest("inline ::= token*; rule ::= inline? inline+ inline*", "rule ::= token* token* token*"); }
  public void testZeroManylParen() throws Exception { doTest("inline ::= token*; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token*) [x token*] {x token*}"); }
  public void testZeroManylParenTrivial() throws Exception { doTest("inline ::= token*; rule ::= (inline) [inline] {inline}", "rule ::= token* token* token*"); }
  public void testZeroManylChoice() throws Exception { doTest("inline ::= token*; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token*) [x | token*] {x | token*}"); }

  public void testOptSequenceSimple() throws Exception { doTest("inline ::= [tok en]; rule ::= inline", "rule ::= [tok en]"); }
  public void testOptSequenceQuantified() throws Exception { doTest("inline ::= [tok en]; rule ::= inline? inline+ inline*", "rule ::= [tok en] (tok en)* (tok en)*"); }
  public void testOptSequenceParen() throws Exception { doTest("inline ::= [tok en]; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x [tok en]) [x [tok en]] {x [tok en]}"); }
  public void testOptSequenceParenTrivial() throws Exception { doTest("inline ::= [tok en]; rule ::= (inline) [inline] {inline}", "rule ::= [tok en] [tok en] [tok en]"); }
  public void testOptSequenceChoice() throws Exception { doTest("inline ::= [tok en]; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | [tok en]) [x | [tok en]] {x | [tok en]}"); }

  public void testAltChoiceSimple() throws Exception { doTest("inline ::= {tok|en}; rule ::= inline", "rule ::= tok|en"); }
  public void testAltChoiceQuantified() throws Exception { doTest("inline ::= {tok|en}; rule ::= inline? inline+ inline*", "rule ::= {tok|en}? {tok|en}+ {tok|en}*"); }
  public void testAltChoiceParen() throws Exception { doTest("inline ::= {tok|en}; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x {tok|en}) [x {tok|en}] {x {tok|en}}"); }
  public void testAltChoiceParenTrivial() throws Exception { doTest("inline ::= {tok|en}; rule ::= (inline) [inline] {inline}", "rule ::= (tok|en) [tok|en] {tok|en}"); }
  public void testAltChoiceChoice() throws Exception { doTest("inline ::= {tok|en}; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}"); }

  public void testParenTokenSimple() throws Exception { doTest("inline ::= (token); rule ::= inline", "rule ::= token"); }
  public void testParenTokenQuantified() throws Exception { doTest("inline ::= (token); rule ::= inline? inline+ inline*", "rule ::= token? token+ token*"); }
  public void testParenTokenParen() throws Exception { doTest("inline ::= (token); rule ::= (x inline) [x inline] {x inline}", "rule ::= (x token) [x token] {x token}"); }
  public void testParenTokenParenTrivial() throws Exception { doTest("inline ::= (token); rule ::= (inline) [inline] {inline}", "rule ::= token token? token"); }
  public void testParenTokenChoice() throws Exception { doTest("inline ::= (token); rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | token) [x | token] {x | token}"); }

  public void testParenSequenceSimple() throws Exception { doTest("inline ::= (tok en); rule ::= inline", "rule ::= tok en"); }
  public void testParenSequenceQuantified() throws Exception { doTest("inline ::= (tok en); rule ::= inline? inline+ inline*", "rule ::= (tok en)? (tok en)+ (tok en)*"); }
  public void testParenSequenceParen() throws Exception { doTest("inline ::= (tok en); rule ::= (x inline) [x inline] {x inline}", "rule ::= (x tok en) [x tok en] {x tok en}"); }
  public void testParenSequenceParenTrivial() throws Exception { doTest("inline ::= (tok en); rule ::= (inline) [inline] {inline}", "rule ::= tok en [tok en] tok en"); }
  public void testParenSequenceChoice() throws Exception { doTest("inline ::= (tok en); rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok en) [x | tok en] {x | tok en}"); }

  public void testParenChoiceSimple() throws Exception { doTest("inline ::= (tok|en); rule ::= inline", "rule ::= tok|en"); }
  public void testParenChoiceQuantified() throws Exception { doTest("inline ::= (tok|en); rule ::= inline? inline+ inline*", "rule ::= (tok|en)? (tok|en)+ (tok|en)*"); }
  public void testParenChoiceParen() throws Exception { doTest("inline ::= (tok|en); rule ::= (x inline) [x inline] {x inline}", "rule ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}"); }
  public void testParenChoiceParenTrivial() throws Exception { doTest("inline ::= (tok|en); rule ::= (inline) [inline] {inline}", "rule ::= (tok|en) [tok|en] {tok|en}"); }
  public void testParenChoiceChoice() throws Exception { doTest("inline ::= (tok|en); rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}"); }

  public void testParenOptionalSimple() throws Exception { doTest("inline ::= (tok en)?; rule ::= inline", "rule ::= (tok en)?"); }
  public void testParenOptionalQuantified() throws Exception { doTest("inline ::= (tok en)?; rule ::= inline? inline+ inline*", "rule ::= (tok en)? (tok en)* (tok en)*"); }
  public void testParenOptionalParen() throws Exception { doTest("inline ::= (tok en)?; rule ::= (x inline) [x inline] {x inline}", "rule ::= (x (tok en)?) [x (tok en)?] {x (tok en)?}"); }
  public void testParenOptionalParenTrivial() throws Exception { doTest("inline ::= (tok en)?; rule ::= (inline) [inline] {inline}", "rule ::= (tok en)? (tok en)? (tok en)?"); }
  public void testParenOptionalChoice() throws Exception { doTest("inline ::= (tok en)?; rule ::= (x | inline) [x | inline] {x | inline}", "rule ::= (x | (tok en)?) [x | (tok en)?] {x | (tok en)?}"); }


  private void doTest(/*@Language("BNF")*/ String text, /*@Language("BNF")*/ String expected) {
    PsiFile file = createFile("a.bnf", text);
    BnfRule rule = PsiTreeUtil.getChildOfType(file, BnfRule.class);
    assertNotNull(rule);
    new BnfInlineRuleProcessor(rule, getProject(), null, false).run();
    assertSameLines(expected, file.getText());
  }
}
