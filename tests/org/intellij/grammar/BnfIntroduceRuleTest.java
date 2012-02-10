package org.intellij.grammar;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.intellij.grammar.refactor.BnfIntroduceRuleHandler;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/22/11
 * Time: 6:39 PM
 *
 * @author Vadim Romansky
 */
public class BnfIntroduceRuleTest extends LightPlatformCodeInsightFixtureTestCase {

  public void testTokenSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= token", "some ::= <selection>token</selection>"); }
  public void testTokenQuantified() throws Exception { doTest("some ::= rule? rule+ rule*\nprivate rule ::= token", "some ::= token? token+ <selection>token</selection>*"); }
  public void testTokenParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token", "some ::= (x token) [x token] {x <selection>token</selection>}"); }
  public void testTokenParenTrivial() throws Exception { doTest("some ::= (rule) [rule] {rule}\nprivate rule ::= token", "some ::= (token) [token] {<selection>token</selection>}"); }
  public void testTokenChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token", "some ::= (x | token) [x | token] {x | <selection>token</selection>}"); }

  public void testSequenceSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= tok en", "some ::= <selection>tok en</selection>"); }
  public void testSequenceQuantified() throws Exception { doTest("some ::= (rule)? (rule)+ (rule)*\nprivate rule ::= tok en", "some ::= (tok en)? (tok en)+ (<selection>tok en</selection>)*"); }
  public void testSequenceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= tok en", "some ::= (x tok en) [x tok en] {x <selection>tok en</selection>}"); }
  public void testSequenceParenTrivial() throws Exception { doTest("some ::= rule [rule] rule\nprivate rule ::= tok en", "some ::= tok en [tok en] <selection>tok en</selection>"); }
  public void testSequenceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok en", "some ::= (x | tok en) [x | tok en] {x | <selection>tok en</selection>}"); }

  public void testChoiceSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= tok|en", "some ::= <selection>tok|en</selection>"); }
  public void testChoiceQuantified() throws Exception { doTest("some ::= (rule)? (rule)+ (rule)*\nprivate rule ::= tok|en", "some ::= (tok|en)? (tok|en)+ (<selection>tok|en</selection>)*"); }
  public void testChoiceParen() throws Exception { doTest("some ::= (x (rule)) [x (rule)] {x (rule)}\nprivate rule ::= tok|en", "some ::= (x (tok|en)) [x (tok|en)] {x (<selection>tok|en</selection>)}"); }
  public void testChoiceParenTrivial() throws Exception { doTest("some ::= (rule) [rule] {rule}\nprivate rule ::= tok|en", "some ::= (tok|en) [tok|en] {<selection>tok|en</selection>}"); }
  public void testChoiceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | <selection>tok|en</selection>}"); }

  public void testOptionalSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= token?", "some ::= <selection>token?</selection>"); }
  public void testOptionalQuantified() throws Exception { doTest("some ::= token+ token* rule\nprivate rule ::= token?", "some ::= token+ token* <selection>token?</selection>"); }
  public void testOptionalParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token?", "some ::= (x token?) [x token?] {x <selection>token?</selection>}"); }
  public void testOptionalParenTrivial() throws Exception { doTest("some ::= rule rule rule\nprivate rule ::= token?", "some ::= token? token? <selection>token?</selection>"); }
  public void testOptionalChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token?", "some ::= (x | token?) [x | token?] {x | <selection>token?</selection>}"); }

  public void testOneManylSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= token+", "some ::= <selection>token+</selection>"); }
  public void testOneManylQuantified() throws Exception { doTest("some ::= token* rule rule\nprivate rule ::= token+", "some ::= token* token+ <selection>token+</selection>"); }
  public void testOneManylParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token+", "some ::= (x token+) [x token+] {x <selection>token+</selection>}"); }
  public void testOneManylParenTrivial() throws Exception { doTest("some ::= rule token* rule\nprivate rule ::= token+", "some ::= token+ token* <selection>token+</selection>"); }
  public void testOneManylChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token+", "some ::= (x | token+) [x | token+] {x | <selection>token+</selection>}"); }

  public void testZeroManylSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= token*", "some ::= <selection>token*</selection>"); }
  public void testZeroManylQuantified() throws Exception { doTest("some ::= rule rule rule\nprivate rule ::= token*", "some ::= token* token* <selection>token*</selection>"); }
  public void testZeroManylParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token*", "some ::= (x token*) [x token*] {x <selection>token*</selection>}"); }
  public void testZeroManylParenTrivial() throws Exception { doTest("some ::= rule rule rule\nprivate rule ::= token*", "some ::= token* token* <selection>token*</selection>"); }
  public void testZeroManylChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token*", "some ::= (x | token*) [x | token*] {x | <selection>token*</selection>}"); }

  public void testOptSequenceSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= [tok en]","some ::= [<selection>tok en]</selection>"); }
  public void testOptSequenceQuantified() throws Exception { doTest("some ::= [tok en] rule rule\nprivate rule ::= (tok en)*", "some ::= [tok en] (tok en)* (<selection>tok en)*</selection>"); }
  public void testOptSequenceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= [tok en]", "some ::= (x [tok en]) [x [tok en]] {x [<selection>tok en]</selection>}"); }
  public void testOptSequenceParenTrivial() throws Exception { doTest("some ::= rule rule rule\nprivate rule ::= [tok en]", "some ::= [tok en] [tok en] [<selection>tok en]</selection>"); }
  public void testOptSequenceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= [tok en]", "some ::= (x | [tok en]) [x | [tok en]] {x | [<selection>tok en]</selection>}"); }

  public void testAltChoiceSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= tok|en", "some ::= <selection>tok|en</selection>"); }
  public void testAltChoiceQuantified() throws Exception { doTest("some ::= {rule}? {rule}+ {rule}*\nprivate rule ::= tok|en", "some ::= {tok|en}? {tok|en}+ {<selection>tok|en</selection>}*"); }
  public void testAltChoiceParen() throws Exception { doTest("some ::= (x {rule}) [x {rule}] {x {rule}}\nprivate rule ::= tok|en", "some ::= (x {tok|en}) [x {tok|en}] {x {<selection>tok|en</selection>}}"); }
  public void testAltChoiceParenTrivial() throws Exception { doTest("some ::= (rule) [rule] {rule}\nprivate rule ::= tok|en", "some ::= (tok|en) [tok|en] {<selection>tok|en</selection>}"); }
  public void testAltChoiceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | <selection>tok|en</selection>}"); }

  public void testParenTokenSimple() throws Exception { doTest("some ::= (rule)\nprivate rule ::= token", "some ::= (<selection>token</selection>)"); }
  public void testParenTokenQuantified() throws Exception { doTest("some ::= rule? rule+ (rule)*\nprivate rule ::= token", "some ::= token? token+ (<selection>token</selection>)*"); }
  public void testParenTokenParen() throws Exception { doTest("some ::= (x rule) [x rule] {x (rule)}\nprivate rule ::= token", "some ::= (x token) [x token] {x (<selection>token</selection>)}"); }
  public void testParenTokenParenTrivial() throws Exception { doTest("some ::= rule rule? (rule)\nprivate rule ::= token", "some ::= token token? (<selection>token</selection>)"); }
  public void testParenTokenChoice() throws Exception { doTest("some ::= (x | (rule)) [x | (rule)] {x | (rule)}\nprivate rule ::= token", "some ::= (x | (token)) [x | (token)] {x | (<selection>token</selection>)}"); }

  public void testParenSequenceSimple() throws Exception { doTest("some ::= (rule)\nprivate rule ::= tok en", "some ::= (<selection>tok en</selection>)"); }
  public void testParenSequenceQuantified() throws Exception { doTest("some ::= rule? rule+ rule*\nprivate rule ::= tok en", "some ::= (tok en)? (tok en)+ (<selection>tok en)</selection>*"); }
  public void testParenSequenceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= tok en", "some ::= (x tok en) [x tok en] {x <selection>tok en</selection>}"); }
  public void testParenSequenceParenTrivial() throws Exception { doTest("some ::= rule [rule] rule\nprivate rule ::= tok en", "some ::= tok en [tok en] <selection>tok en</selection>"); }
  public void testParenSequenceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok en", "some ::= (x | tok en) [x | tok en] {x | <selection>tok en</selection>}"); }

  public void testParenChoiceSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= tok|en", "some ::= <selection>tok|en</selection>"); }
  public void testParenChoiceQuantified() throws Exception { doTest("some ::= rule? rule+ rule*\nprivate rule ::= tok|en", "some ::= (tok|en)? (tok|en)+ (<selection>tok|en)</selection>*"); }
  public void testParenChoiceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= tok|en", "some ::= (x (tok|en)) [x (tok|en)] {x (<selection>tok|en)</selection>}"); }
  public void testParenChoiceParenTrivial() throws Exception { doTest("some ::= rule [tok|en] rule\nprivate rule ::= tok|en", "some ::= (tok|en) [tok|en] {<selection>tok|en}</selection>"); }
  public void testParenChoiceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | <selection>tok|en</selection>}"); }

  public void testParenOptionalSimple() throws Exception { doTest("some ::= rule\nprivate rule ::= (tok en)?", "some ::= (<selection>tok en)?</selection>"); }
  public void testParenOptionalQuantified() throws Exception { doTest("some ::= (tok en)? rule rule\nprivate rule ::= (tok en)*", "some ::= (tok en)? (tok en)* (<selection>tok en)*</selection>"); }
  public void testParenOptionalParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= (tok en)?", "some ::= (x (tok en)?) [x (tok en)?] {x (<selection>tok en)?</selection>}"); }
  public void testParenOptionalParenTrivial() throws Exception { doTest("some ::= rule rule rule\nprivate rule ::= (tok en)?", "some ::= (tok en)? (tok en)? (<selection>tok en)?</selection>"); }
  public void testParenOptionalChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= (tok en)?", "some ::= (x | (tok en)?) [x | (tok en)?] {x | (<selection>tok en)?</selection>}"); }
  
  public void testChoiceSequence() throws Exception {doTest("some ::= tok|en| x | rule y\nprivate rule ::= tok en","some ::= tok|en| x | <selection>tok en</selection> y");}
  public void testChoicePart() throws Exception {doTest("some ::=tok en x | rule| y\nprivate rule ::= tok| en","some ::=tok en x | <selection>tok| en</selection>| y");}

  public void testFalseStartSequences() throws Exception {doTest("root ::= tok tok rule en tok rule\nsome ::=rule\nprivate rule ::= tok en","root ::= tok tok tok en en tok tok en\nsome ::=<selection>tok en</selection>");}
  public void testAttrsAfterRule() throws Exception {doTest("root ::= tok rule;\nprivate rule ::= tok en;\n{pin=1}","root ::= tok <selection>tok en</selection>;{pin=1}");}

  public void testSpaces() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= [tok? en]", "some ::= (x [  tok  ? en  ]) [x [tok  ?  en]] {x [<selection>tok? en]</selection>}"); }
  public void testNoWsSiblings() throws Exception { doTest("some ::= x's'rule\nprivate rule ::= tok en", "some ::= x's'<selection>tok en</selection>"); }

  private void doTest(/*@Language("BNF")*/ String expected, /*@Language("BNF")*/ String text) throws IOException {
    myFixture.configureByText("a.bnf", text);
    new BnfIntroduceRuleHandler().invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
    assertSameLines(expected, myFixture.getFile().getText());
  }
}
