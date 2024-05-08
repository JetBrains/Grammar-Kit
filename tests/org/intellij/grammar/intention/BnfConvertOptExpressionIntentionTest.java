/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.intention;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class BnfConvertOptExpressionIntentionTest extends BasePlatformTestCase {

  public void testQuantifiedToParenOpt() {
    doExpressionTest("a?", "[a]");
    doExpressionTest("'l'?", "['l']");
    doExpressionTest("<<ex>>?", "[<<ex>>]");
    doExpressionTest("()?", "[]");
    doExpressionTest("(a)?", "[a]");
    doExpressionTest("((((a))))?", "[a]");
    doExpressionTest("(a b)?", "[a b]");
    doExpressionTest("(a b c)?", "[a b c]");
    doExpressionTest("(a | b | c)?", "[a | b | c]");
    doExpressionTest("(a+)?", "[a+]");
  }

  public void testParenOptToQuantified() {
    doExpressionTest("[a]", "a?");
    doExpressionTest("['l']", "'l'?");
    doExpressionTest("[(a b)]", "(a b)?");
    doExpressionTest("[<<ex>>]", "<<ex>>?");
    doExpressionTest("[]", "()?");
    doExpressionTest("[[[[a]]]]", "a?");
    doExpressionTest("[a b c]", "(a b c)?");
    doExpressionTest("[a | b | c]", "(a | b | c)?");
    doExpressionTest("[a+]", "(a+)?");
  }

  private void doExpressionTest(String before, String after) {
    myFixture.configureByText("_.bnf", "r ::= <caret>" + before);
    myFixture.launchAction(myFixture.findSingleIntention("Convert"));
    myFixture.checkResult("r ::= " + after);
  }
}
