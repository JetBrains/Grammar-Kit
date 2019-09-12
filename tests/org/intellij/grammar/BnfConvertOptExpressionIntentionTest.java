/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.grammar;

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
