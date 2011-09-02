package org.intellij.grammar;

import com.intellij.testFramework.LightCodeInsightTestCase;
import org.intellij.grammar.refactor.BnfIntroduceRuleHandler;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/22/11
 * Time: 6:39 PM
 *
 * @author Vadim Romansky
 */
public class BnfExtractRuleTest extends LightCodeInsightTestCase {

  public void testTokenSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= token", "some ::= token",0); }
  public void testTokenQuantified() throws Exception { doTest("some ::= rule? rule+ rule*"+"\n"+"private rule ::= token", "some ::= token? token+ token*",0); }
  public void testTokenParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= token", "some ::= (x token) [x token] {x token}",0); }
  public void testTokenParenTrivial() throws Exception { doTest("some ::= (rule) [rule] {rule}"+"\n"+"private rule ::= token", "some ::= (token) [token] {token}",0); }
  public void testTokenChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= token", "some ::= (x | token) [x | token] {x | token}",0); }

  public void testSequenceSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= tok en", "some ::= tok en",0); }
  public void testSequenceQuantified() throws Exception { doTest("some ::= (rule)? (rule)+ (rule)*"+"\n"+"private rule ::= tok en", "some ::= (tok en)? (tok en)+ (tok en)*",0); }
  public void testSequenceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= tok en", "some ::= (x tok en) [x tok en] {x tok en}",0); }
  public void testSequenceParenTrivial() throws Exception { doTest("some ::= rule [rule] rule"+"\n"+"private rule ::= tok en", "some ::= tok en [tok en] tok en",0); }
  public void testSequenceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= tok en", "some ::= (x | tok en) [x | tok en] {x | tok en}",0); }

  public void testChoiceSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= tok|en", "some ::= tok|en",0); }
  public void testChoiceQuantified() throws Exception { doTest("some ::= (rule)? (rule)+ (rule)*"+"\n"+"private rule ::= tok|en", "some ::= (tok|en)? (tok|en)+ (tok|en)*",0); }
  public void testChoiceParen() throws Exception { doTest("some ::= (x (rule)) [x (rule)] {x (rule)}"+"\n"+"private rule ::= tok|en", "some ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}",0); }
  public void testChoiceParenTrivial() throws Exception { doTest("some ::= (rule) [rule] {rule}"+"\n"+"private rule ::= tok|en", "some ::= (tok|en) [tok|en] {tok|en}",0); }
  public void testChoiceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | tok|en}",0); }

  public void testOptionalSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= token?", "some ::= token?",1); }
  public void testOptionalQuantified() throws Exception { doTest("some ::= token+ token* rule"+"\n"+"private rule ::= token?", "some ::= token+ token* token?",1); }
  public void testOptionalParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= token?", "some ::= (x token?) [x token?] {x token?}",1); }
  public void testOptionalParenTrivial() throws Exception { doTest("some ::= rule rule rule"+"\n"+"private rule ::= token?", "some ::= token? token? token?",1); }
  public void testOptionalChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= token?", "some ::= (x | token?) [x | token?] {x | token?}",1); }

  public void testOneManylSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= token+", "some ::= token+",1); }
  public void testOneManylQuantified() throws Exception { doTest("some ::= token* rule rule"+"\n"+"private rule ::= token+", "some ::= token* token+ token+",1); }
  public void testOneManylParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= token+", "some ::= (x token+) [x token+] {x token+}",1); }
  public void testOneManylParenTrivial() throws Exception { doTest("some ::= rule token* rule"+"\n"+"private rule ::= token+", "some ::= token+ token* token+",1); }
  public void testOneManylChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= token+", "some ::= (x | token+) [x | token+] {x | token+}",1); }

  public void testZeroManylSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= token*", "some ::= token*",1); }
  public void testZeroManylQuantified() throws Exception { doTest("some ::= rule rule rule"+"\n"+"private rule ::= token*", "some ::= token* token* token*",1); }
  public void testZeroManylParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= token*", "some ::= (x token*) [x token*] {x token*}",1); }
  public void testZeroManylParenTrivial() throws Exception { doTest("some ::= rule rule rule"+"\n"+"private rule ::= token*", "some ::= token* token* token*",1); }
  public void testZeroManylChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= token*", "some ::= (x | token*) [x | token*] {x | token*}",1); }

  public void testOptSequenceSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= [tok en]","some ::= [tok en]",1); }
  public void testOptSequenceQuantified() throws Exception { doTest("some ::= [tok en] rule rule"+"\n"+"private rule ::= (tok en)*", "some ::= [tok en] (tok en)* (tok en)*",2); }
  public void testOptSequenceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= [tok en]", "some ::= (x [tok en]) [x [tok en]] {x [tok en]}",1); }
  public void testOptSequenceParenTrivial() throws Exception { doTest("some ::= rule rule rule"+"\n"+"private rule ::= [tok en]", "some ::= [tok en] [tok en] [tok en]",1); }
  public void testOptSequenceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= [tok en]", "some ::= (x | [tok en]) [x | [tok en]] {x | [tok en]}",1); }

  public void testAltChoiceSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= tok|en", "some ::= tok|en",0); }
  public void testAltChoiceQuantified() throws Exception { doTest("some ::= {rule}? {rule}+ {rule}*"+"\n"+"private rule ::= tok|en", "some ::= {tok|en}? {tok|en}+ {tok|en}*",0); }
  public void testAltChoiceParen() throws Exception { doTest("some ::= (x {rule}) [x {rule}] {x {rule}}"+"\n"+"private rule ::= tok|en", "some ::= (x {tok|en}) [x {tok|en}] {x {tok|en}}",0); }
  public void testAltChoiceParenTrivial() throws Exception { doTest("some ::= (rule) [rule] {rule}"+"\n"+"private rule ::= tok|en", "some ::= (tok|en) [tok|en] {tok|en}",0); }
  public void testAltChoiceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | tok|en}",0); }

  public void testParenTokenSimple() throws Exception { doTest("some ::= (rule)"+"\n"+"private rule ::= token", "some ::= (token)",0); }
  public void testParenTokenQuantified() throws Exception { doTest("some ::= rule? rule+ (rule)*"+"\n"+"private rule ::= token", "some ::= token? token+ (token)*",0); }
  public void testParenTokenParen() throws Exception { doTest("some ::= (x rule) [x rule] {x (rule)}"+"\n"+"private rule ::= token", "some ::= (x token) [x token] {x (token)}",0); }
  public void testParenTokenParenTrivial() throws Exception { doTest("some ::= rule rule? (rule)"+"\n"+"private rule ::= token", "some ::= token token? (token)",0); }
  public void testParenTokenChoice() throws Exception { doTest("some ::= (x | (rule)) [x | (rule)] {x | (rule)}"+"\n"+"private rule ::= token", "some ::= (x | (token)) [x | (token)] {x | (token)}",0); }

  public void testParenSequenceSimple() throws Exception { doTest("some ::= (rule)"+"\n"+"private rule ::= tok en", "some ::= (tok en)",0); }
  public void testParenSequenceQuantified() throws Exception { doTest("some ::= rule? rule+ rule*"+"\n"+"private rule ::= tok en", "some ::= (tok en)? (tok en)+ (tok en)*",1); }
  public void testParenSequenceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= tok en", "some ::= (x tok en) [x tok en] {x tok en}",0); }
  public void testParenSequenceParenTrivial() throws Exception { doTest("some ::= rule [rule] rule"+"\n"+"private rule ::= tok en", "some ::= tok en [tok en] tok en",0); }
  public void testParenSequenceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= tok en", "some ::= (x | tok en) [x | tok en] {x | tok en}",0); }

  public void testParenChoiceSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= tok|en", "some ::= tok|en",0); }
  public void testParenChoiceQuantified() throws Exception { doTest("some ::= rule? rule+ rule*"+"\n"+"private rule ::= tok|en", "some ::= (tok|en)? (tok|en)+ (tok|en)*",1); }
  public void testParenChoiceParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= tok|en", "some ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}",1); }
  public void testParenChoiceParenTrivial() throws Exception { doTest("some ::= rule [tok|en] rule"+"\n"+"private rule ::= tok|en", "some ::= (tok|en) [tok|en] {tok|en}",1); }
  public void testParenChoiceChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | tok|en}",0); }

  public void testParenOptionalSimple() throws Exception { doTest("some ::= rule"+"\n"+"private rule ::= (tok en)?", "some ::= (tok en)?",2); }
  public void testParenOptionalQuantified() throws Exception { doTest("some ::= (tok en)? rule rule"+"\n"+"private rule ::= (tok en)*", "some ::= (tok en)? (tok en)* (tok en)*",2); }
  public void testParenOptionalParen() throws Exception { doTest("some ::= (x rule) [x rule] {x rule}"+"\n"+"private rule ::= (tok en)?", "some ::= (x (tok en)?) [x (tok en)?] {x (tok en)?}",2); }
  public void testParenOptionalParenTrivial() throws Exception { doTest("some ::= rule rule rule"+"\n"+"private rule ::= (tok en)?", "some ::= (tok en)? (tok en)? (tok en)?",2); }
  public void testParenOptionalChoice() throws Exception { doTest("some ::= (x | rule) [x | rule] {x | rule}"+"\n"+"private rule ::= (tok en)?", "some ::= (x | (tok en)?) [x | (tok en)?] {x | (tok en)?}",2); }
  
  public void testChoiceSequence() throws Exception {doTest("some ::= tok|en| x | rule y" + "\n" + "private rule ::= tok en","some ::= tok|en| x | tok en y",0);}
  public void testChoicePart() throws Exception {doTest("some ::=tok en x | rule| y" + "\n" + "private rule ::= tok| en","some ::=tok en x | tok| en| y",0);}

  private static void doTest(/*@Language("BNF")*/ String expected, /*@Language("BNF")*/ String text, int addOffset) throws IOException {
    configureFromFileText("a.bnf", text);
    int tPosition = text.lastIndexOf("t");
    int nPosition = text.lastIndexOf("n");
    myEditor.getSelectionModel().setSelection(tPosition, nPosition + addOffset + 1);
    new BnfIntroduceRuleHandler().invoke(getProject(), myEditor, myFile, null);
    assertSameLines(expected, myFile.getText());
  }
}
