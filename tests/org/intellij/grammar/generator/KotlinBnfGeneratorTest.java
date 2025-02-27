/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KotlinBnfGeneratorTest extends AbstractBnfGeneratorTest {
  public KotlinBnfGeneratorTest() {
    super("kotlin", ".kt");
  }

  @Override
  protected @NotNull List<@NotNull Generator> createGenerators(@NotNull BnfFile psiFile,
                                                               @NotNull String outputPath,
                                                               @NotNull OutputOpener outputOpener) {
    return List.of(new KotlinParserGenerator(psiFile, "", outputPath, "", outputOpener));
  }

  public void testAutopin() throws Exception {
    doParserTest();
  }

  public void testAutoRecovery() throws Exception {
    doPsiTest();
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
    doPsiTest();
  }

  public void testExternalRules() throws Exception {
    doParserTest();
  }

  public void testExternalRulesLambdas() throws Exception {
    doParserTest();
  }

  public void testFixes() throws Exception {
    doPsiTest();
  }

  public void testGenOptions() throws Exception {
    doPsiTest();
  }

  public void testLeftAssociative() throws Exception {
    doParserTest();
  }

  public void testPsiAccessors() throws Exception {
    doPsiTest();
  }

  public void testPsiGen() throws Exception {
    doPsiTest();
  }

  public void testPsiStart() throws Exception {
    doPsiTest();
  }

  public void testSmall() throws Exception {
    doParserTest();
  }

  public void testStub() throws Exception {
    doPsiTest();
  }

  public void testTokenChoice() throws Exception {
    doPsiTest();
  }

  public void testTokenChoiceNoSets() throws Exception {
    doPsiTest();
  }

  public void testTokenSequence() throws Exception {
    doParserTest();
  }

  public void testUpperRules() throws Exception {
    doParserTest();
  }

  public void testUtilMethods() throws Exception {
    doPsiTest();
  }

  public void testSelfBnf() throws Exception {
    doPsiTest();
  }

  public void testSelfFlex() throws Exception {
    doPsiTest();
  }

  public void testJsonParser() throws Exception {
    doPsiTest();
  }

  public void testEmpty() throws Exception {
    doTestEmpty();
  }
}
