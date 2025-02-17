/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class JavaBnfGeneratorTest extends AbstractBnfGeneratorTest {
  public JavaBnfGeneratorTest() {
    super("generator", ".java");
  }

  public void testSelfBnf() throws Exception {
    doPsiTest();
  }

  public void testSelfFlex() throws Exception {
    doPsiTest();
  }

  public void testSmall() throws Exception {
    doParserTest();
  }

  public void testAutopin() throws Exception {
    doParserTest();
  }

  public void testExternalRules() throws Exception {
    doParserTest();
  }

  public void testExternalRulesLambdas() throws Exception {
    doParserTest();
  }

  public void testLeftAssociative() throws Exception {
    doParserTest();
  }

  public void testPsiGen() throws Exception {
    doPsiTest();
  }

  public void testPsiAccessors() throws Exception {
    doPsiTest();
  }

  public void testPsiStart() throws Exception {
    doPsiTest();
  }

  public void testExprParser() throws Exception {
    doPsiTest();
  }

  public void testTokenSequence() throws Exception {
    doParserTest();
  }

  public void testTokenChoice() throws Exception {
    doPsiTest();
  }

  public void testTokenChoiceNoSets() throws Exception {
    doPsiTest();
  }

  public void testStub() throws Exception {
    doPsiTest();
  }

  public void testUtilMethods() throws Exception {
    doPsiTest();
  }

  public void testBindersAndHooks() throws Exception {
    doParserTest();
  }

  public void testAutoRecovery() throws Exception {
    doPsiTest();
  }

  public void testConsumeMethods() throws Exception {
    doParserTest();
  }

  public void testGenOptions() throws Exception {
    doPsiTest();
  }

  public void testDollar() throws Exception {
    doParserTest();
  }

  // TODO not implemented
  public void _testUpperRules() throws Exception {
    doPsiTest();
  }

  public void testFixes() throws Exception {
    doPsiTest();
  }

  public void testEmpty() throws IOException {
    doTestEmpty();
  }

  @Override
  protected @NotNull List<@NotNull Generator> createGenerators() {
    return List.of(Generator.JAVA_GENERATOR);
  }
}
