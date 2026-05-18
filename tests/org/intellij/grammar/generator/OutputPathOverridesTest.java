/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.io.FileUtilRt;
import org.intellij.grammar.BnfGeneratorTestCase;
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verifies that the per-artifact {@code *_OUTPUT_PATH} attributes route generated files to the
 * directories supplied via the generator constructors. The assertions look only at the
 * {@code File} each {@link OutputOpener#openOutput} call receives, so this test is independent
 * of the generated source content.
 */
public class OutputPathOverridesTest extends BnfGeneratorTestCase {

  private static final String GRAMMAR =
    "{\n" +
    "  generate=[parser-api=\"syntax\"]\n" +
    "  parserClass=\"generated.OutputPathsParser\"\n" +
    "  elementTypeHolderClass=\"generated.GenTypes\"\n" +
    "  syntaxElementTypeHolderClass=\"generated.GenSyntaxTypes\"\n" +
    "  elementTypeConverterFactoryClass=\"generated.GenConverterFactory\"\n" +
    "  psiPackage=\"generated.psi\"\n" +
    "  psiImplPackage=\"generated.psi.impl\"\n" +
    "  psiClassPrefix=\"X\"\n" +
    "}\n" +
    "\n" +
    "root ::= 'a' 'b'\n" +
    "foo  ::= 'a' root\n";

  private final String tempBase = FileUtilRt.getTempDirectory();
  private final String parserDir   = tempBase + "/op/parser";
  private final String psiDir      = tempBase + "/op/psi";
  private final String etHolderDir = tempBase + "/op/types";
  private final String syntaxDir   = tempBase + "/op/syntaxtypes";
  private final String converterDir= tempBase + "/op/converter";

  public OutputPathOverridesTest() {
    super("java");
  }

  /** Java-mode: parserOutputPath / psiOutputPath / elementTypeHolderOutputPath. */
  public void testJavaModeRoutesEachArtifactToItsOverride() throws Exception {
    BnfFile bnfFile = (BnfFile)createPsiFile("OutputPaths.bnf",
                                             GRAMMAR.replace("generate=[parser-api=\"syntax\"]", ""));

    Map<String, File> captured = new LinkedHashMap<>();
    OutputOpener opener = capturingOpener(captured);

    Map<KnownAttribute<?>, List<Path>> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, List.of(Path.of(parserDir)));
    map.put(KnownAttribute.PSI_OUTPUT_PATH, List.of(Path.of(psiDir)));
    map.put(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH, List.of(Path.of(etHolderDir)));
    JavaParserGenerator gen = new JavaParserGenerator(
      bnfFile, "", "", opener, BnfPaths.resolveExplicit(map)
    );
    gen.generate();

    assertParent("generated.OutputPathsParser",            captured, parserDir);
    assertParent("generated.GenTypes",                     captured, etHolderDir);
    assertParentStartsWith("generated.psi.XFoo",           captured, psiDir);
    assertParentStartsWith("generated.psi.impl.XFooImpl",  captured, psiDir);
    // The visitor is part of the PSI bundle and must follow PSI_OUTPUT_PATH too.
    assertParentStartsWith("generated.psi.XVisitor",       captured, psiDir);
  }

  /** Kotlin-mode: adds syntaxElementTypeHolderOutputPath and elementTypeConverterFactoryOutputPath. */
  public void testKotlinModeRoutesEachArtifactToItsOverride() throws Exception {
    BnfFile bnfFile = (BnfFile)createPsiFile("OutputPaths.bnf", GRAMMAR);

    Map<String, File> captured = new LinkedHashMap<>();
    OutputOpener opener = capturingOpener(captured);

    Map<KnownAttribute<?>, List<Path>> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, List.of(Path.of(parserDir)));
    map.put(KnownAttribute.PSI_OUTPUT_PATH, List.of(Path.of(psiDir)));
    map.put(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH, List.of(Path.of(etHolderDir)));
    map.put(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH, List.of(Path.of(syntaxDir)));
    map.put(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH, List.of(Path.of(converterDir)));
    KotlinParserGenerator gen = new KotlinParserGenerator(
      bnfFile, "", "", opener, BnfPaths.resolveExplicit(map)
    );
    gen.generate();

    assertParent("generated.OutputPathsParser",            captured, parserDir);
    assertParent("generated.GenSyntaxTypes",               captured, syntaxDir);
    assertParent("generated.GenTypes",                     captured, etHolderDir);
    assertParent("generated.GenConverterFactory",          captured, converterDir);
    assertParentStartsWith("generated.psi.XFoo",           captured, psiDir);
    assertParentStartsWith("generated.psi.impl.XFooImpl",  captured, psiDir);
    assertParentStartsWith("generated.psi.XVisitor",       captured, psiDir);
  }

  /** When overrides are null, every artifact falls back to the parser/psi outputPath (today's behavior). */
  public void testNullOverridesFallBackToOutputPath() throws Exception {
    BnfFile bnfFile = (BnfFile)createPsiFile("OutputPaths.bnf",
                                             GRAMMAR.replace("generate=[parser-api=\"syntax\"]", ""));

    Map<String, File> captured = new LinkedHashMap<>();
    OutputOpener opener = capturingOpener(captured);

    JavaParserGenerator gen = new JavaParserGenerator(
      bnfFile, "", "", opener,
      BnfPaths.resolveExplicit(Map.of(KnownAttribute.PARSER_OUTPUT_PATH, List.of(Path.of(parserDir))))
    );
    gen.generate();

    // Every artifact should have parserDir as its root prefix.
    assertParent("generated.OutputPathsParser",            captured, parserDir);
    assertParent("generated.GenTypes",                     captured, parserDir);
    assertParentStartsWith("generated.psi.XFoo",           captured, parserDir);
    assertParentStartsWith("generated.psi.impl.XFooImpl",  captured, parserDir);
    assertParentStartsWith("generated.psi.XVisitor",       captured, parserDir);
  }

  // ---- helpers ----

  private static @NotNull OutputOpener capturingOpener(@NotNull Map<String, File> captured) {
    return (className, file, bnfFile) -> {
      captured.put(className, file);
      return new PrintWriter(new StringWriter());  // discard generated content
    };
  }

  /** Asserts the captured file for {@code className} sits under {@code expectedRootDir} with FQN-as-path. */
  private static void assertParent(@NotNull String className, @NotNull Map<String, File> captured,
                                   @NotNull String expectedRootDir) {
    File actual = require(className, captured);
    File expected = new File(expectedRootDir, className.replace('.', File.separatorChar) + "." + ext(actual));
    assertEquals("Wrong path for " + className, expected.getAbsolutePath(), actual.getAbsolutePath());
  }

  private static void assertParentStartsWith(@NotNull String className, @NotNull Map<String, File> captured,
                                             @NotNull String expectedRootDir) {
    File actual = require(className, captured);
    String prefix = new File(expectedRootDir).getAbsolutePath() + File.separator;
    assertTrue("Path for " + className + " expected to start with " + prefix + " but was " + actual.getAbsolutePath(),
               actual.getAbsolutePath().startsWith(prefix));
  }

  private static @NotNull File require(@NotNull String className, @NotNull Map<String, File> captured) {
    File f = captured.get(className);
    assertNotNull("Generator did not emit " + className + ". Captured: " + captured.keySet(), f);
    return f;
  }

  private static @NotNull String ext(@NotNull File f) {
    String name = f.getName();
    int dot = name.lastIndexOf('.');
    return dot < 0 ? "" : name.substring(dot + 1);
  }
}
