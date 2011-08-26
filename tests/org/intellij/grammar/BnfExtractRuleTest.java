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

  public void testTokenSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= token", "rule ::= token",0); }
  public void testTokenQuantified() throws Exception { doTest("rule ::= newRule? newRule+ newRule*"+"\n"+"private newRule ::= token", "rule ::= token? token+ token*",0); }
  public void testTokenParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= token", "rule ::= (x token) [x token] {x token}",0); }
  public void testTokenParenTrivial() throws Exception { doTest("rule ::= newRule newRule? newRule"+"\n"+"private newRule ::= token", "rule ::= (token) [token] {token}",0); }
  public void testTokenChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= token", "rule ::= (x | token) [x | token] {x | token}",0); }

  public void testSequenceSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= tok en", "rule ::= tok en",0); }
  public void testSequenceQuantified() throws Exception { doTest("rule ::= newRule? newRule+ newRule*"+"\n"+"private newRule ::= tok en", "rule ::= (tok en)? (tok en)+ (tok en)*",0); }
  public void testSequenceParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= tok en", "rule ::= (x tok en) [x tok en] {x tok en}",0); }
  public void testSequenceParenTrivial() throws Exception { doTest("rule ::= newRule newRule? newRule"+"\n"+"private newRule ::= tok en", "rule ::= tok en [tok en] tok en",0); }
  public void testSequenceChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= tok en", "rule ::= (x | tok en) [x | tok en] {x | tok en}",0); }

  public void testChoiceSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= tok|en", "rule ::= tok|en",0); }
  public void testChoiceQuantified() throws Exception { doTest("rule ::= newRule? newRule+ newRule*"+"\n"+"private newRule ::= tok|en", "rule ::= (tok|en)? (tok|en)+ (tok|en)*",0); }
  public void testChoiceParen() throws Exception { doTest("rule ::= (x newRule) [x newRule] {x newRule}"+"\n"+"private newRule ::= tok|en", "rule ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}",0); }
  public void testChoiceParenTrivial() throws Exception { doTest("rule ::= newRule newRule? newRule"+"\n"+"private newRule ::= tok|en", "rule ::= (tok|en) [tok|en] {tok|en}",0); }
  public void testChoiceChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= tok|en", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}",0); }

  public void testOptionalSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= token?", "rule ::= token?",1); }
  public void testOptionalQuantified() throws Exception { doTest("rule ::= token+ token* newRule"+"\n"+"private newRule ::= token?", "rule ::= token+ token* token?",1); }
  public void testOptionalParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= token?", "rule ::= (x token?) [x token?] {x token?}",1); }
  public void testOptionalParenTrivial() throws Exception { doTest("rule ::= newRule newRule newRule"+"\n"+"private newRule ::= token?", "rule ::= token? token? token?",1); }
  public void testOptionalChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= token?", "rule ::= (x | token?) [x | token?] {x | token?}",1); }

  public void testOneManylSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= token+", "rule ::= token+",1); }
  public void testOneManylQuantified() throws Exception { doTest("rule ::= token* newRule newRule"+"\n"+"private newRule ::= token+", "rule ::= token* token+ token+",1); }
  public void testOneManylParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= token+", "rule ::= (x token+) [x token+] {x token+}",1); }
  public void testOneManylParenTrivial() throws Exception { doTest("rule ::= newRule token* newRule"+"\n"+"private newRule ::= token+", "rule ::= token+ token* token+",1); }
  public void testOneManylChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= token+", "rule ::= (x | token+) [x | token+] {x | token+}",1); }

  public void testZeroManylSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= token*", "rule ::= token*",1); }
  public void testZeroManylQuantified() throws Exception { doTest("rule ::= newRule newRule newRule"+"\n"+"private newRule ::= token*", "rule ::= token* token* token*",1); }
  public void testZeroManylParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= token*", "rule ::= (x token*) [x token*] {x token*}",1); }
  public void testZeroManylParenTrivial() throws Exception { doTest("rule ::= newRule newRule newRule"+"\n"+"private newRule ::= token*", "rule ::= token* token* token*",1); }
  public void testZeroManylChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= token*", "rule ::= (x | token*) [x | token*] {x | token*}",1); }

  public void testOptSequenceSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= [tok en]","rule ::= [tok en]",1); }
  public void testOptSequenceQuantified() throws Exception { doTest("rule ::= [tok en] newRule newRule"+"\n"+"private newRule ::= (tok en)*", "rule ::= [tok en] (tok en)* (tok en)*",2); }
  public void testOptSequenceParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= [tok en]", "rule ::= (x [tok en]) [x [tok en]] {x [tok en]}",2); }
  public void testOptSequenceParenTrivial() throws Exception { doTest("rule ::= newRule newRule newRule"+"\n"+"private newRule ::= [tok en]", "rule ::= [tok en] [tok en] [tok en]",2); }
  public void testOptSequenceChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= [tok en]", "rule ::= (x | [tok en]) [x | [tok en]] {x | [tok en]}",2); }

  public void testAltChoiceSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= tok|en", "rule ::= tok|en",0); }
  public void testAltChoiceQuantified() throws Exception { doTest("rule ::= newRule? newRule+ newRule*"+"\n"+"private newRule ::= tok|en", "rule ::= {tok|en}? {tok|en}+ {tok|en}*",0); }
  public void testAltChoiceParen() throws Exception { doTest("rule ::= (x newRule) [x newRule] {x newRule}"+"\n"+"private newRule ::= tok|en", "rule ::= (x {tok|en}) [x {tok|en}] {x {tok|en}}",0); }
  public void testAltChoiceParenTrivial() throws Exception { doTest("rule ::= newRule newRule? newRule"+"\n"+"private newRule ::= tok|en", "rule ::= (tok|en) [tok|en] {tok|en}",0); }
  public void testAltChoiceChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= tok|en", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}",0); }

  public void testParenTokenSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= token", "rule ::= (token)",0); }
  public void testParenTokenQuantified() throws Exception { doTest("rule ::= newRule? newRule+ newRule*"+"\n"+"private newRule ::= token", "rule ::= token? token+ (token)*",0); }
  public void testParenTokenParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= token", "rule ::= (x token) [x token] {x (token)}",0); }
  public void testParenTokenParenTrivial() throws Exception { doTest("rule ::= newRule newRule? newRule"+"\n"+"private newRule ::= token", "rule ::= token token? (token)",0); }
  public void testParenTokenChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= token", "rule ::= (x | (token)) [x | (token)] {x | (token)}",0); }

  public void testParenSequenceSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= tok en", "rule ::= (tok en)",0); }
  public void testParenSequenceQuantified() throws Exception { doTest("rule ::= newRule? newRule+ newRule*"+"\n"+"private newRule ::= tok en", "rule ::= (tok en)? (tok en)+ (tok en)*",1); }
  public void testParenSequenceParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= tok en", "rule ::= (x tok en) [x tok en] {x tok en}",0); }
  public void testParenSequenceParenTrivial() throws Exception { doTest("rule ::= newRule newRule? newRule"+"\n"+"private newRule ::= tok en", "rule ::= tok en [tok en] tok en",0); }
  public void testParenSequenceChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= tok en", "rule ::= (x | tok en) [x | tok en] {x | tok en}",0); }

  public void testParenChoiceSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= tok|en", "rule ::= tok|en",0); }
  public void testParenChoiceQuantified() throws Exception { doTest("rule ::= newRule? newRule+ newRule*"+"\n"+"private newRule ::= tok|en", "rule ::= (tok|en)? (tok|en)+ (tok|en)*",1); }
  public void testParenChoiceParen() throws Exception { doTest("rule ::= (x newRule) [x newRule] {x newRule}"+"\n"+"private newRule ::= tok|en", "rule ::= (x (tok|en)) [x (tok|en)] {x (tok|en)}",1); }
  public void testParenChoiceParenTrivial() throws Exception { doTest("rule ::= newRule newRule? newRule"+"\n"+"private newRule ::= tok|en", "rule ::= (tok|en) [tok|en] {tok|en}",1); }
  public void testParenChoiceChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= tok|en", "rule ::= (x | tok|en) [x | tok|en] {x | tok|en}",0); }

  public void testParenOptionalSimple() throws Exception { doTest("rule ::= newRule"+"\n"+"private newRule ::= (tok en)?", "rule ::= (tok en)?",2); }
  public void testParenOptionalQuantified() throws Exception { doTest("rule ::= (tok en)? newRule newRule"+"\n"+"private newRule ::= (tok en)*", "rule ::= (tok en)? (tok en)* (tok en)*",2); }
  public void testParenOptionalParen() throws Exception { doTest("rule ::= x newRule [x newRule] x newRule"+"\n"+"private newRule ::= (tok en)?", "rule ::= (x (tok en)?) [x (tok en)?] {x (tok en)?}",2); }
  public void testParenOptionalParenTrivial() throws Exception { doTest("rule ::= newRule newRule newRule"+"\n"+"private newRule ::= (tok en)?", "rule ::= (tok en)? (tok en)? (tok en)?",2); }
  public void testParenOptionalChoice() throws Exception { doTest("rule ::= (x | newRule) [x | newRule] {x | newRule}"+"\n"+"private newRule ::= (tok en)?", "rule ::= (x | (tok en)?) [x | (tok en)?] {x | (tok en)?}",2); }
  
  public void testChoiceSequence() throws Exception {doTest("rule ::= tok|en| x | newRule y" + "\n" + "private newRule ::= tok en","rule ::= tok|en| x | tok en y",0);}
  public void testChoicePart() throws Exception {doTest("rule ::=tok en x | newRule| y" + "\n" + "private newRule ::= tok| en","rule ::=tok en x | tok| en| y",0);}

  private void doTest(/*@Language("BNF")*/ String expected, /*@Language("BNF")*/ String text,int addOffset) throws IOException {
    configureFromFileText("a.bnf", text);
    int tPosition = text.lastIndexOf("t");
    int nPosition = text.lastIndexOf("n");
    myEditor.getSelectionModel().setSelection(tPosition, nPosition + addOffset + 1);
    new BnfIntroduceRuleHandler().invoke(getProject(), myEditor, myFile, null);
    assertSameLines(expected, myFile.getText());
  }
}
