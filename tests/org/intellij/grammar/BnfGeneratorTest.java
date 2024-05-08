/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.idea.Bombed;

public class BnfGeneratorTest extends BnfGeneratorAbstractTest {
  public BnfGeneratorTest() {
    super("generator");
  }

  public void testSelfBnf() throws Exception { doGenTest(true); }
  public void testSelfFlex() throws Exception { doGenTest(true); }
  public void testSmall() throws Exception { doGenTest(false); }
  public void testAutopin() throws Exception { doGenTest(false); }
  public void testExternalRules() throws Exception { doGenTest(false); }
  public void testExternalRulesLambdas() throws Exception { doGenTest(false); }
  public void testLeftAssociative() throws Exception { doGenTest(false); }
  public void testPsiGen() throws Exception { doGenTest(true); }
  public void testPsiAccessors() throws Exception { doGenTest(true); }
  public void testPsiStart() throws Exception { doGenTest(true); }
  public void testExprParser() throws Exception { doGenTest(true); }
  public void testTokenSequence() throws Exception { doGenTest(false); }
  public void testTokenChoice() throws Exception { doGenTest(true); }
  public void testTokenChoiceNoSets() throws Exception { doGenTest(true); }
  public void testStub() throws Exception { doGenTest(true); }
  public void testUtilMethods() throws Exception { doGenTest(true); }
  public void testBindersAndHooks() throws Exception { doGenTest(false); }
  public void testAutoRecovery() throws Exception { doGenTest(true); }
  public void testConsumeMethods() throws Exception { doGenTest(false); }
  public void testGenOptions() throws Exception { doGenTest(true); }

  @Bombed(year = 2030, user = "author", month = 1, day = 1, description = "not implemented")
  public void testUpperRules() throws Exception { doGenTest(true); }
  public void testFixes() throws Exception { doGenTest(true); }

  public void testEmpty() throws Exception {
    myFile = createPsiFile("empty.bnf", "{ }");
    newTestGenerator().generate();
  }
}
