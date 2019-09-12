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

package org.intellij.jflex;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.intellij.grammar.BnfCompletionTest;

/**
 * @author gregsh
 */
public class JFlexCompletionTest extends LightJavaCodeInsightFixtureTestCase {

  public void testState1() { textAfter("%%\n%state FIRST\n%%<FIR<caret>", "%%\n%state FIRST\n%%<FIRST"); }

  public void testBufferOption1() { variants("%%\n%b<caret>", BnfCompletionTest.CheckType.INCLUDES, "%buffer"); }
  public void testBufferOption2() { textAfter("%%\n%buf<caret>", "%%\n%buffer "); }

  public void testClosingEof1() { textAfter("%%\n%eofv<caret>", "%%\n%eofval{\n    \n%eofval}\n"); }

  protected void textAfter(String txt, String textAfter) {
    myFixture.configureByText("a.flex", txt);
    myFixture.complete(CompletionType.BASIC, 1);
    assertEquals(textAfter, myFixture.getEditor().getDocument().getText());
  }

  protected void variants(String txt, BnfCompletionTest.CheckType checkType, String... variants) {
    myFixture.configureByText("a.flex", txt);
    BnfCompletionTest.doVariantsTestInner(myFixture, CompletionType.BASIC, checkType, variants);
  }
}
