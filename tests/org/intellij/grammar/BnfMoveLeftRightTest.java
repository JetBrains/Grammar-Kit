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

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class BnfMoveLeftRightTest extends BasePlatformTestCase {

  public void testChoice() {
    doExpressionTestRightLeft("<caret>a  | b |c", "b  | <caret>a |c");
  }

  public void testSequence() {
    doExpressionTestRightLeft("<caret>a   b  c", "b   <caret>a  c");
  }

  public void testSequenceWithinChoice() {
    doExpressionTestRight("a | b | c c c<caret> | d [d d]", "a | b | d [d d] | c c c<caret>");
  }

  public void testSequenceWithinChoice2() {
    doExpressionTestRightLeft("a | <caret>b c | d e", "a | c <caret>b | d e");
  }

  public void testAttrs() {
    doExpressionTestRightLeft("{  <caret>a= '' b ='' c=''}", "{  b ='' <caret>a= '' c=''}");
  }

  public void testValueList() {
    doExpressionTestRightLeft("{ v = [<caret>a  b   c] }", "{ v = [b  <caret>a   c] }");
  }

  public void testExternalArguments() {
    doExpressionTestRightLeft("<<a <caret>b  c  >>", "<<a c  <caret>b  >>");
  }

  private void doExpressionTestRight(String text, String after) {
    myFixture.configureByText("_.bnf", "r ::= " + text);
    myFixture.performEditorAction(IdeActions.MOVE_ELEMENT_RIGHT);
    myFixture.checkResult("r ::= " + after);
  }

  private void doExpressionTestRightLeft(String text, String after) {
    doExpressionTestRight(text, after);
    myFixture.performEditorAction(IdeActions.MOVE_ELEMENT_LEFT);
    myFixture.checkResult("r ::= " + text);
  }
}
