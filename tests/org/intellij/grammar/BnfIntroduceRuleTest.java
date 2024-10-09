package org.intellij.grammar;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.refactor.BnfIntroduceRuleHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/22/11
 * Time: 6:39 PM
 *
 * @author Vadim Romansky
 */
public class BnfIntroduceRuleTest extends BasePlatformTestCase {

  public void testTokenSimple() { doTest("some ::= rule\nprivate rule ::= token", "some ::= <selection>token</selection>"); }
  public void testTokenQuantified() { doTest("some ::= rule? rule+ rule*\nprivate rule ::= token", "some ::= token? token+ <selection>token</selection>*"); }
  public void testTokenParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token", "some ::= (x token) [x token] {x <selection>token</selection>}"); }
  public void testTokenParenTrivial() { doTest("some ::= (rule) [rule] {rule}\nprivate rule ::= token", "some ::= (token) [token] {<selection>token</selection>}"); }
  public void testTokenChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token", "some ::= (x | token) [x | token] {x | <selection>token</selection>}"); }

  public void testSequenceSimple() { doTest("some ::= rule\nprivate rule ::= tok en", "some ::= <selection>tok en</selection>"); }
  public void testSequenceQuantified() { doTest("some ::= (rule)? (rule)+ (rule)*\nprivate rule ::= tok en", "some ::= (tok en)? (tok en)+ (<selection>tok en</selection>)*"); }
  public void testSequenceParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= tok en", "some ::= (x tok en) [x tok en] {x <selection>tok en</selection>}"); }
  public void testSequenceParenTrivial() { doTest("some ::= rule [rule] rule\nprivate rule ::= tok en", "some ::= tok en [tok en] <selection>tok en</selection>"); }
  public void testSequenceChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok en", "some ::= (x | tok en) [x | tok en] {x | <selection>tok en</selection>}"); }

  public void testChoiceSimple() { doTest("some ::= rule\nprivate rule ::= tok|en", "some ::= <selection>tok|en</selection>"); }
  public void testChoiceQuantified() { doTest("some ::= (rule)? (rule)+ (rule)*\nprivate rule ::= tok|en", "some ::= (tok|en)? (tok|en)+ (<selection>tok|en</selection>)*"); }
  public void testChoiceParen() { doTest("some ::= (x (rule)) [x (rule)] {x (rule)}\nprivate rule ::= tok|en", "some ::= (x (tok|en)) [x (tok|en)] {x (<selection>tok|en</selection>)}"); }
  public void testChoiceParenTrivial() { doTest("some ::= (rule) [rule] {rule}\nprivate rule ::= tok|en", "some ::= (tok|en) [tok|en] {<selection>tok|en</selection>}"); }
  public void testChoiceChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | <selection>tok|en</selection>}"); }

  public void testOptionalSimple() { doTest("some ::= rule\nprivate rule ::= token?", "some ::= <selection>token?</selection>"); }
  public void testOptionalQuantified() { doTest("some ::= token+ token* rule\nprivate rule ::= token?", "some ::= token+ token* <selection>token?</selection>"); }
  public void testOptionalParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token?", "some ::= (x token?) [x token?] {x <selection>token?</selection>}"); }
  public void testOptionalParenTrivial() { doTest("some ::= rule rule rule\nprivate rule ::= token?", "some ::= token? token? <selection>token?</selection>"); }
  public void testOptionalChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token?", "some ::= (x | token?) [x | token?] {x | <selection>token?</selection>}"); }

  public void testOneManylSimple() { doTest("some ::= rule\nprivate rule ::= token+", "some ::= <selection>token+</selection>"); }
  public void testOneManylQuantified() { doTest("some ::= token* rule rule\nprivate rule ::= token+", "some ::= token* token+ <selection>token+</selection>"); }
  public void testOneManylParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token+", "some ::= (x token+) [x token+] {x <selection>token+</selection>}"); }
  public void testOneManylParenTrivial() { doTest("some ::= rule token* rule\nprivate rule ::= token+", "some ::= token+ token* <selection>token+</selection>"); }
  public void testOneManylChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token+", "some ::= (x | token+) [x | token+] {x | <selection>token+</selection>}"); }

  public void testZeroManylSimple() { doTest("some ::= rule\nprivate rule ::= token*", "some ::= <selection>token*</selection>"); }
  public void testZeroManylQuantified() { doTest("some ::= rule rule rule\nprivate rule ::= token*", "some ::= token* token* <selection>token*</selection>"); }
  public void testZeroManylParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= token*", "some ::= (x token*) [x token*] {x <selection>token*</selection>}"); }
  public void testZeroManylParenTrivial() { doTest("some ::= rule rule rule\nprivate rule ::= token*", "some ::= token* token* <selection>token*</selection>"); }
  public void testZeroManylChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= token*", "some ::= (x | token*) [x | token*] {x | <selection>token*</selection>}"); }

  public void testOptSequenceSimple() { doTest("some ::= rule\nprivate rule ::= [tok en]", "some ::= [<selection>tok en]</selection>"); }
  public void testOptSequenceQuantified() { doTest("some ::= [tok en] rule rule\nprivate rule ::= (tok en)*", "some ::= [tok en] (tok en)* (<selection>tok en)*</selection>"); }
  public void testOptSequenceParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= [tok en]", "some ::= (x [tok en]) [x [tok en]] {x [<selection>tok en]</selection>}"); }
  public void testOptSequenceParenTrivial() { doTest("some ::= rule rule rule\nprivate rule ::= [tok en]", "some ::= [tok en] [tok en] [<selection>tok en]</selection>"); }
  public void testOptSequenceChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= [tok en]", "some ::= (x | [tok en]) [x | [tok en]] {x | [<selection>tok en]</selection>}"); }

  public void testAltChoiceSimple() { doTest("some ::= rule\nprivate rule ::= tok|en", "some ::= <selection>tok|en</selection>"); }
  public void testAltChoiceQuantified() { doTest("some ::= {rule}? {rule}+ {rule}*\nprivate rule ::= tok|en", "some ::= {tok|en}? {tok|en}+ {<selection>tok|en</selection>}*"); }
  public void testAltChoiceParen() { doTest("some ::= (x {rule}) [x {rule}] {x {rule}}\nprivate rule ::= tok|en", "some ::= (x {tok|en}) [x {tok|en}] {x {<selection>tok|en</selection>}}"); }
  public void testAltChoiceParenTrivial() { doTest("some ::= (rule) [rule] {rule}\nprivate rule ::= tok|en", "some ::= (tok|en) [tok|en] {<selection>tok|en</selection>}"); }
  public void testAltChoiceChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | <selection>tok|en</selection>}"); }

  public void testParenTokenSimple() { doTest("some ::= (rule)\nprivate rule ::= token", "some ::= (<selection>token</selection>)"); }
  public void testParenTokenQuantified() { doTest("some ::= rule? rule+ (rule)*\nprivate rule ::= token", "some ::= token? token+ (<selection>token</selection>)*"); }
  public void testParenTokenParen() { doTest("some ::= (x rule) [x rule] {x (rule)}\nprivate rule ::= token", "some ::= (x token) [x token] {x (<selection>token</selection>)}"); }
  public void testParenTokenParenTrivial() { doTest("some ::= rule rule? (rule)\nprivate rule ::= token", "some ::= token token? (<selection>token</selection>)"); }
  public void testParenTokenChoice() { doTest("some ::= (x | (rule)) [x | (rule)] {x | (rule)}\nprivate rule ::= token", "some ::= (x | (token)) [x | (token)] {x | (<selection>token</selection>)}"); }

  public void testParenSequenceSimple() { doTest("some ::= (rule)\nprivate rule ::= tok en", "some ::= (<selection>tok en</selection>)"); }
  public void testParenSequenceQuantified() { doTest("some ::= rule? rule+ rule*\nprivate rule ::= tok en", "some ::= (tok en)? (tok en)+ (<selection>tok en)</selection>*"); }
  public void testParenSequenceParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= tok en", "some ::= (x tok en) [x tok en] {x <selection>tok en</selection>}"); }
  public void testParenSequenceParenTrivial() { doTest("some ::= rule [rule] rule\nprivate rule ::= tok en", "some ::= tok en [tok en] <selection>tok en</selection>"); }
  public void testParenSequenceChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok en", "some ::= (x | tok en) [x | tok en] {x | <selection>tok en</selection>}"); }

  public void testParenChoiceSimple() { doTest("some ::= rule\nprivate rule ::= tok|en", "some ::= <selection>tok|en</selection>"); }
  public void testParenChoiceQuantified() { doTest("some ::= rule? rule+ rule*\nprivate rule ::= tok|en", "some ::= (tok|en)? (tok|en)+ (<selection>tok|en)</selection>*"); }
  public void testParenChoiceParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= tok|en", "some ::= (x (tok|en)) [x (tok|en)] {x (<selection>tok|en)</selection>}"); }
  public void testParenChoiceParenTrivial() { doTest("some ::= rule [tok|en] rule\nprivate rule ::= tok|en", "some ::= (tok|en) [tok|en] {<selection>tok|en}</selection>"); }
  public void testParenChoiceChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= tok|en", "some ::= (x | tok|en) [x | tok|en] {x | <selection>tok|en</selection>}"); }

  public void testParenOptionalSimple() { doTest("some ::= rule\nprivate rule ::= (tok en)?", "some ::= (<selection>tok en)?</selection>"); }
  public void testParenOptionalQuantified() { doTest("some ::= (tok en)? rule rule\nprivate rule ::= (tok en)*", "some ::= (tok en)? (tok en)* (<selection>tok en)*</selection>"); }
  public void testParenOptionalParen() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= (tok en)?", "some ::= (x (tok en)?) [x (tok en)?] {x (<selection>tok en)?</selection>}"); }
  public void testParenOptionalParenTrivial() { doTest("some ::= rule rule rule\nprivate rule ::= (tok en)?", "some ::= (tok en)? (tok en)? (<selection>tok en)?</selection>"); }
  public void testParenOptionalChoice() { doTest("some ::= (x | rule) [x | rule] {x | rule}\nprivate rule ::= (tok en)?", "some ::= (x | (tok en)?) [x | (tok en)?] {x | (<selection>tok en)?</selection>}"); }
  
  public void testChoiceSequence() {doTest("some ::= tok|en| x | rule y\nprivate rule ::= tok en", "some ::= tok|en| x | <selection>tok en</selection> y");}
  public void testChoicePart() {doTest("some ::=tok en x | rule| y\nprivate rule ::= tok| en", "some ::=tok en x | <selection>tok| en</selection>| y");}

  public void testFalseStartSequences() {doTest("root ::= tok tok rule en tok rule\nsome ::=rule\nprivate rule ::= tok en", "root ::= tok tok tok en en tok tok en\nsome ::=<selection>tok en</selection>");}
  public void testAttrsAfterRule() {doTest("root ::= tok rule;\nprivate rule ::= tok en;\n{pin=1}", "root ::= tok <selection>tok en</selection>;{pin=1}");}

  public void testSpaces() { doTest("some ::= (x rule) [x rule] {x rule}\nprivate rule ::= [tok? en]", "some ::= (x [  tok  ? en  ]) [x [tok  ?  en]] {x [<selection>tok? en]</selection>}"); }
  public void testNoWsSiblings() { doTest("some ::= x's'rule\nprivate rule ::= tok en", "some ::= x's'<selection>tok en</selection>"); }

  public void testWithoutSelectionSingleVariant() { doTest("some ::= rule\nprivate rule ::= expr1", "some ::= exp<caret>r1"); }
  public void testWithoutSelectionAtTheStartOfExpression() { doTest("some ::= rule\nprivate rule ::= expr1", "some ::= <caret>expr1"); }
  public void testWithoutSelection() {
    doTest("some ::= rule | expr3\nprivate rule ::= expr1    expr2+", "some ::= expr1    exp<caret>r2+ | expr3", bnfExpressions -> {
      assertSameElements(ContainerUtil.map(bnfExpressions, BnfIntroduceRuleHandler.RENDER_FUNCTION), "expr2", "expr2+", "expr1 expr2+", "expr1 expr2+ | expr3");
      return bnfExpressions.get(2);
    });
  }

  private void doTest(/*@Language("BNF")*/ String expected, /*@Language("BNF")*/ String text) {
    doTest(expected, text, null);
  }

  private void doTest(/*@Language("BNF")*/ String expected, /*@Language("BNF")*/ String text, @Nullable Function<List<BnfExpression>, BnfExpression> popupHandler) {
    myFixture.configureByText("a.bnf", text);
    new BnfIntroduceRuleHandler(popupHandler).invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), null);
    assertSameLines(expected, myFixture.getFile().getText());
  }
}
