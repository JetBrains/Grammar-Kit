/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;

public class KotlinBnfGeneratorTest extends AbstractBnfGeneratorTest {
  protected static final @NotNull String KOTLIN_BNF_FILES_DIR = "generatorSyntax";

  public KotlinBnfGeneratorTest() {
    super("kotlin", ".kt");
  }

  @Override
  protected @NotNull List<@NotNull ParserGenerator> createGenerators(@NotNull BnfFile psiFile,
                                                                     @NotNull String outputPath,
                                                                     @NotNull OutputOpener outputOpener) {
    Path parserPath        = resolveTestPath(psiFile, KnownAttribute.PARSER_OUTPUT_PATH);
    Path psiPath           = resolveTestPath(psiFile, KnownAttribute.PSI_OUTPUT_PATH);
    Path etHolderPath      = resolveTestPath(psiFile, KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH);
    Path syntaxHolderPath  = resolveTestPath(psiFile, KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH);
    Path converterPath     = resolveTestPath(psiFile, KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH);
    Path inputPath         = resolveTestPath(psiFile, KnownAttribute.INPUT_PATH);
    Path psiInputPath      = resolveTestPath(psiFile, KnownAttribute.PSI_INPUT_PATH);
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, parserPath != null ? parserPath : Path.of(outputPath));
    if (psiPath != null) map.put(KnownAttribute.PSI_OUTPUT_PATH, psiPath);
    if (etHolderPath != null) map.put(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH, etHolderPath);
    if (syntaxHolderPath != null) map.put(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH, syntaxHolderPath);
    if (converterPath != null) map.put(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH, converterPath);
    if (inputPath != null) map.put(KnownAttribute.INPUT_PATH, inputPath);
    if (psiInputPath != null) map.put(KnownAttribute.PSI_INPUT_PATH, psiInputPath);
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);
    return List.of(new KotlinParserGenerator(psiFile, "", "", outputOpener, paths));
  }

  private @Nullable Path resolveTestPath(@NotNull BnfFile psiFile, @NotNull KnownAttribute<String> attr) {
    String relative = getRootAttribute(psiFile, attr);
    if (relative == null) return null;
    String projectPath = myFullDataPath.substring(0, myFullDataPath.lastIndexOf(File.separatorChar) + 1);
    return Path.of(projectPath + relative);
  }

  @Override
  protected String loadFile(@NotNull String name) throws IOException {
    String text = super.loadFile(name);

    if (name.equals("SelfBnf.bnf") || name.equals("SelfFlex.bnf")) {
      String paramLine = "generate=[java=\"8\" names=\"long\" visitor-value=\"R\"]";
      assert text.contains(paramLine);
      String patchedText = text.replace(paramLine, "generate=[java=\"8\" names=\"long\" visitor-value=\"R\" parser-api=\"syntax\"]");
      return patchedText;
    }
    else {
      return text;
    }
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

  /**
   * The mixin's stub constructor is declared in a Kotlin source whose filename differs from
   * the class name and is reached via {@code psiInputPath}. The generated PSI impl must mirror
   * the mixin's constructor signature ({@code IElementType}, not the hard-coded fallback
   * {@code IStubElementType}).
   */
  public void testKotlinMixinCtor() throws Exception {
    doPsiTest();
  }

  public void testStubFallback() throws Exception {
    doGenTest(true);
  }

  /**
   * When the rule's mixin/extends class can't be resolved, the generator falls back to
   * {@code astNodeConstructorSignature} / {@code stubConstructorSignature}. Both fallback
   * emitters must still mark the constructor parameters {@code @NotNull} so the generated
   * code keeps the same nullability contract as the inherited path.
   */
  public void testFallbackCtorsHaveNotNull() throws Exception {
    doPsiTest();
  }

  /**
   * A rule whose {@code mixin}/{@code implements} target can't be resolved must emit a
   * generator warning naming the rule and the missing FQN — the silent fallback that previously
   * masked misconfigured {@code psiInputPath} entries broke usability for downstream users.
   */
  public void testUnresolvedMixinWarning() throws Exception {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
    try {
      doPsiTest();
    }
    finally {
      System.setOut(originalOut);
    }
    String captured = buf.toString(StandardCharsets.UTF_8);
    assertTrue("expected mixin warning in:\n" + captured,
               captured.contains("unresolved: mixin class test.psi.ext.MissingMixin not found"));
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

  public void testEmpty() throws Exception {
    doTestEmpty();
  }
}
