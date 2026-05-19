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
    List<Path> psiInputPaths = resolveTestPaths(psiFile, KnownAttribute.PSI_INPUT_PATH);
    Map<KnownAttribute<?>, List<Path>> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, List.of(parserPath != null ? parserPath : Path.of(outputPath)));
    if (psiPath != null) map.put(KnownAttribute.PSI_OUTPUT_PATH, List.of(psiPath));
    if (etHolderPath != null) map.put(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH, List.of(etHolderPath));
    if (syntaxHolderPath != null) map.put(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH, List.of(syntaxHolderPath));
    if (converterPath != null) map.put(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH, List.of(converterPath));
    if (inputPath != null) map.put(KnownAttribute.INPUT_PATH, List.of(inputPath));
    if (!psiInputPaths.isEmpty()) map.put(KnownAttribute.PSI_INPUT_PATH, psiInputPaths);
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);
    return List.of(new KotlinParserGenerator(psiFile, "", "", outputOpener, paths));
  }

  private @Nullable Path resolveTestPath(@NotNull BnfFile psiFile, @NotNull KnownAttribute<String> attr) {
    String relative = getRootAttribute(psiFile, attr);
    if (relative == null) return null;
    String projectPath = myFullDataPath.substring(0, myFullDataPath.lastIndexOf(File.separatorChar) + 1);
    return Path.of(projectPath + relative);
  }

  private @NotNull List<Path> resolveTestPaths(@NotNull BnfFile psiFile, @NotNull KnownAttribute<KnownAttribute.ListValue> attr) {
    KnownAttribute.ListValue list = getRootAttribute(psiFile, attr);
    if (list == null || list.isEmpty()) return List.of();
    String projectPath = myFullDataPath.substring(0, myFullDataPath.lastIndexOf(File.separatorChar) + 1);
    List<Path> result = new java.util.ArrayList<>(list.size());
    for (String relative : list.asStrings()) {
      if (relative != null && !relative.isEmpty()) result.add(Path.of(projectPath + relative));
    }
    return List.copyOf(result);
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

  /**
   * Borrowing a constructor from a generic Kotlin parent must add {@code @NotNull} on the parameter
   * slot that originally referenced a type variable: the substituted concrete type is non-null per
   * Kotlin's runtime contract, but the source provider does not attach the annotation to bare
   * type-variable references (see {@code jvm-class-info/CLAUDE.md}).
   */
  public void testKotlinMixinCtorGeneric() throws Exception {
    doPsiTest();
  }

  /**
   * A sub-rule extending a parent rule with a resolvable {@code mixin} must NOT import
   * the parent's mixin class in its generated impl: the sub-rule's impl extends the
   * parent's impl class, never the mixin directly. Regression for an unused import
   * leaking through inherited-constructor processing when source-based class resolution
   * is active.
   */
  public void testSubRuleMixinImport() throws Exception {
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

  /**
   * The {@code implements} attribute may name a sister BNF rule, not just a JVM class FQN
   * (e.g. {@code implements=Type} in real-world grammars such as go.bnf). The unresolved-class
   * probe must skip sister-rule entries — both standalone and inside a mixed list — while still
   * warning about genuinely missing FQNs in the same list.
   */
  public void testImplementsRuleNoWarning() throws Exception {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
    try {
      String testName = getTestName(false);
      myFile = createBnfFile(true, testName, loadFile(testName + "." + myFileExt));
      for (ParserGenerator generator : createGenerators((BnfFile)myFile, myFullDataPath, getTestOpener())) {
        generator.generate();
      }
    }
    finally {
      System.setOut(originalOut);
    }
    String captured = buf.toString(StandardCharsets.UTF_8);
    assertFalse("rule-name implements must not warn:\n" + captured,
                captured.contains("usesSister: implements interface target not found"));
    assertFalse("rule-name implements inside a mixed list must not warn:\n" + captured,
                captured.contains("mixed: implements interface target not found"));
    assertTrue("missing FQN in a mixed list must still warn:\n" + captured,
               captured.contains("mixed: implements interface test.psi.AnotherMissing not found"));
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
