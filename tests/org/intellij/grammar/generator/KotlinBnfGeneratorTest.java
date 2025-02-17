/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KotlinBnfGeneratorTest extends AbstractBnfGeneratorTest {
  public KotlinBnfGeneratorTest() {
    super("kotlin", ".kt");
  }

  @Override
  protected @NotNull List<@NotNull Generator> createGenerators() {
    return List.of(Generator.KOTLIN_GENERATOR);
  }

  public void testAutopin() throws Exception {
    doParserTest();
  }

  public void testAutoRecovery() throws Exception {
    doParserTest();
  }

  public void testBindersAndHooks() throws Exception {
    doParserTest();
  }

  public void testConsumeMethods() throws Exception {
    doParserTest();
  }

  public void testDollar() throws Exception {
    doParserTest();
  }

  public void testExprParser() throws Exception {
    doParserTest();
  }

  public void testExternalRules() throws Exception {
    doParserTest();
  }

  public void testExternalRulesLambdas() throws Exception {
    doParserTest();
  }

  public void testGenOptions() throws Exception {
    doParserTest();
  }

  public void testLeftAssociative() throws Exception {
    doParserTest();
  }

  public void testPsiAccessors() throws Exception {
    doParserTest();
  }

  public void testPsiGen() throws Exception {
    doParserTest();
  }

  public void testPsiStart() throws Exception {
    doParserTest();
  }

  public void testSmall() throws Exception {
    doParserTest();
  }

  public void testStub() throws Exception {
    doParserTest();
  }

  public void testTokenChoice() throws Exception {
    doParserTest();
  }

  public void testTokenChoiceNoSets() throws Exception {
    doParserTest();
  }

  public void testTokenSequence() throws Exception {
    doParserTest();
  }

  public void testUpperRules() throws Exception {
    doParserTest();
  }

  public void testUtilMethods() throws Exception {
    doParserTest();
  }

  public void testSelfBnf() throws Exception {
    doParserTest();
  }

  public void testSelfFlex() throws Exception {
    doParserTest();
  }

  public void testJson() throws Exception {
    doParserTest();
  }

  public void testEmpty() throws Exception {
    doTestEmpty();
  }
}
