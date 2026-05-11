/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Exhaustive coverage of {@link BnfPaths#resolve} and {@link BnfPathsResolution} — the cached
 * single source of truth for {@code *InputPath} / {@code *OutputPath} resolution.
 */
public class BnfPathsResolutionTest extends JavaCodeInsightFixtureTestCase {

  /** Build a BNF file in the test fixture and return its parsed BnfFile. */
  private @NotNull BnfFile bnfWith(@NotNull String name, @NotNull String content) {
    return (BnfFile)myFixture.configureByText(name, content);
  }

  private @NotNull Path bnfParent(@NotNull BnfFile bnfFile) {
    VirtualFile vf = bnfFile.getOriginalFile().getVirtualFile();
    assertNotNull("BnfFile has no VFS file", vf);
    return Path.of(vf.getParent().getPath());
  }

  // ---- Direct path lookup ---------------------------------------------------

  public void testEachInputPathReturnsAbsoluteResolved() {
    BnfFile bnf = bnfWith("g.bnf", """
      {
        inputPath="../in"
        psiInputPath="../psi-in"
      }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    assertEquals(parent.resolve("../in").normalize(), paths.path(KnownAttribute.INPUT_PATH));
    assertEquals(parent.resolve("../psi-in").normalize(), paths.path(KnownAttribute.PSI_INPUT_PATH));
  }

  public void testEachOutputPathReturnsAbsoluteResolved() {
    BnfFile bnf = bnfWith("g.bnf", """
      {
        parserClass="com.example.MyParser"
        parserOutputPath="../parser"
        psiOutputPath="../psi"
        elementTypeHolderOutputPath="../et"
        syntaxElementTypeHolderOutputPath="../syntax"
        elementTypeConverterFactoryOutputPath="../conv"
      }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    assertEquals(parent.resolve("../parser").normalize(),  paths.path(KnownAttribute.PARSER_OUTPUT_PATH));
    assertEquals(parent.resolve("../psi").normalize(),     paths.path(KnownAttribute.PSI_OUTPUT_PATH));
    assertEquals(parent.resolve("../et").normalize(),      paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    assertEquals(parent.resolve("../syntax").normalize(),  paths.path(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    assertEquals(parent.resolve("../conv").normalize(),    paths.path(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH));
  }

  public void testInputPathDefaultsToBnfParent() {
    BnfFile bnf = bnfWith("g.bnf", "{ parserClass=\"com.example.P\" }\nroot ::= 'a'");
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    // Unset inputPath defaults to the BNF file's parent directory; the input cascade then fills
    // psiInputPath from the global default.
    assertEquals(parent, paths.path(KnownAttribute.INPUT_PATH));
    assertEquals(parent, paths.path(KnownAttribute.PSI_INPUT_PATH));
  }

  public void testEmptyStringTreatedAsUnset() {
    BnfFile bnf = bnfWith("g.bnf", """
      { parserOutputPath="" inputPath="" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    // The empty string is treated as unset; "unset" now resolves to the BNF parent default.
    assertEquals(bnfParent(bnf), paths.path(KnownAttribute.INPUT_PATH));
    // PARSER_OUTPUT_PATH may be filled in by the fallback (parserClass package), but never by ""
  }

  public void testRelativePathsAreNormalized() {
    BnfFile bnf = bnfWith("g.bnf", "{ inputPath=\"./a/../b\" }\nroot ::= 'a'");
    Path parent = bnfParent(bnf);
    assertEquals(parent.resolve("b").normalize(), BnfPaths.resolve(bnf).path(KnownAttribute.INPUT_PATH));
  }

  // ---- Output-path fallback chain ------------------------------------------

  public void testPsiOutputFallsBackToParserOutput() {
    BnfFile bnf = bnfWith("g.bnf", """
      { parserOutputPath="../parser" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    assertEquals(paths.path(KnownAttribute.PARSER_OUTPUT_PATH),
                 paths.path(KnownAttribute.PSI_OUTPUT_PATH));
  }

  public void testEtHolderFallsBackToParserOutputWhenPsiUnset() {
    BnfFile bnf = bnfWith("g.bnf", """
      { parserOutputPath="../parser" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    assertEquals(paths.path(KnownAttribute.PARSER_OUTPUT_PATH),
                 paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
  }

  public void testEtHolderFallsBackToPsiOutputWhenPsiSet() {
    BnfFile bnf = bnfWith("g.bnf", """
      { parserOutputPath="../parser" psiOutputPath="../psi" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    // Element-type artifacts travel with PSI rather than the parser.
    assertEquals(paths.path(KnownAttribute.PSI_OUTPUT_PATH),
                 paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
  }

  public void testSyntaxHolderFallsBackToPsiOutput() {
    BnfFile bnf = bnfWith("g.bnf", """
      { parserOutputPath="../parser" psiOutputPath="../psi" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    assertEquals(paths.path(KnownAttribute.PSI_OUTPUT_PATH),
                 paths.path(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
  }

  public void testConverterFallsBackToPsiOutput() {
    BnfFile bnf = bnfWith("g.bnf", """
      { parserOutputPath="../parser" psiOutputPath="../psi" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    assertEquals(paths.path(KnownAttribute.PSI_OUTPUT_PATH),
                 paths.path(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH));
  }

  public void testExplicitOverridesShadowFallbacks() {
    BnfFile bnf = bnfWith("g.bnf", """
      {
        parserOutputPath="../parser"
        psiOutputPath="../psi"
        elementTypeHolderOutputPath="../et"
      }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    assertEquals(parent.resolve("../et").normalize(), paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    assertNotSame(paths.path(KnownAttribute.PARSER_OUTPUT_PATH),
                  paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
  }

  // ---- referencePath cascade ----------------------------------------------

  public void testReferencePathPrefersSpecificInputOverGlobal() {
    BnfFile bnf = bnfWith("g.bnf", """
      { inputPath="../global" psiInputPath="../psi" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    // mixin / implements / psiImplUtilClass all share psiInputPath now; parserUtilClass keeps
    // falling through to the global inputPath.
    assertEquals(parent.resolve("../psi").normalize(), BnfPaths.referencePath(paths, KnownAttribute.MIXIN));
    assertEquals(parent.resolve("../psi").normalize(), BnfPaths.referencePath(paths, KnownAttribute.IMPLEMENTS));
    assertEquals(parent.resolve("../psi").normalize(), BnfPaths.referencePath(paths, KnownAttribute.PSI_IMPL_UTIL_CLASS));
    assertEquals(parent.resolve("../global").normalize(), BnfPaths.referencePath(paths, KnownAttribute.PARSER_UTIL_CLASS));
  }

  public void testReferencePathFallsBackToGlobalInputPath() {
    BnfFile bnf = bnfWith("g.bnf", """
      { inputPath="../global" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    assertEquals(parent.resolve("../global").normalize(), BnfPaths.referencePath(paths, KnownAttribute.MIXIN));
    assertEquals(parent.resolve("../global").normalize(), BnfPaths.referencePath(paths, KnownAttribute.PARSER_UTIL_CLASS));
    assertEquals(parent.resolve("../global").normalize(), BnfPaths.referencePath(paths, KnownAttribute.IMPLEMENTS));
  }

  public void testReferencePathFallsBackToBnfParentInIdeContext() {
    BnfFile bnf = bnfWith("g.bnf", "{ parserClass=\"com.example.P\" }\nroot ::= 'a'");
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    // BnfPaths.compute() seeds INPUT_PATH = BNF parent when nothing is set, so referencePath
    // never returns null for an IDE-resolved file.
    assertEquals(parent, BnfPaths.referencePath(paths, KnownAttribute.MIXIN));
    assertEquals(parent, BnfPaths.referencePath(paths, KnownAttribute.IMPLEMENTS));
  }

  public void testReferencePathOutputAttributeUsesOutputPath() {
    BnfFile bnf = bnfWith("g.bnf", """
      { parserOutputPath="../parser" inputPath="../in" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    // parserClass → parserOutputPath, NOT global inputPath
    assertEquals(parent.resolve("../parser").normalize(), BnfPaths.referencePath(paths, KnownAttribute.PARSER_CLASS));
  }

  public void testReferencePathOutputDoesNotFallBackToInputPath() {
    BnfFile bnf = bnfWith("g.bnf", """
      { inputPath="../in" parserClass="com.example.P" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    // parserOutputPath is unset; the parserClass-package fallback may produce a value, but
    // critically referencePath(parserClass) must NOT return inputPath.
    Path ref = BnfPaths.referencePath(paths, KnownAttribute.PARSER_CLASS);
    Path globalInput = paths.path(KnownAttribute.INPUT_PATH);
    if (ref != null && globalInput != null) {
      assertFalse("referencePath(parserClass) must not fall back to inputPath",
                  globalInput.equals(ref));
    }
  }

  public void testReferencePathFqnNotInAnyMapFallsBackToGlobalInputPath() {
    BnfFile bnf = bnfWith("g.bnf", """
      { inputPath="../global" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    // psiTreeUtilClass has no entry in INPUT_FOR or OUTPUT_FOR — falls through to inputPath.
    assertEquals(parent.resolve("../global").normalize(),
                 BnfPaths.referencePath(paths, KnownAttribute.PSI_TREE_UTIL_CLASS));
  }

  public void testReferencePathNullArgFallsBackToGlobalInputPath() {
    BnfFile bnf = bnfWith("g.bnf", """
      { inputPath="../global" }
      root ::= 'a'
      """);
    BnfPathsResolution paths = BnfPaths.resolve(bnf);
    Path parent = bnfParent(bnf);
    assertEquals(parent.resolve("../global").normalize(), BnfPaths.referencePath(paths, null));
  }

  // ---- Caching -------------------------------------------------------------

  public void testRepeatedResolveReturnsSameInstance() {
    BnfFile bnf = bnfWith("g.bnf", "{ inputPath=\"../x\" }\nroot ::= 'a'");
    BnfPathsResolution a = BnfPaths.resolve(bnf);
    BnfPathsResolution b = BnfPaths.resolve(bnf);
    assertSame(a, b);
  }

  public void testDifferentBnfFilesGetIndependentResolutions() {
    BnfFile a = bnfWith("a.bnf", "{ inputPath=\"../a-in\" }\nroot ::= 'x'");
    Path aParent = bnfParent(a);
    BnfPathsResolution aPaths = BnfPaths.resolve(a);
    assertEquals(aParent.resolve("../a-in").normalize(), aPaths.path(KnownAttribute.INPUT_PATH));
    // configureByText replaces the previous fixture file, so we re-fetch the parent for the new BNF.
    BnfFile b = bnfWith("b.bnf", "{ inputPath=\"../b-in\" }\nroot ::= 'x'");
    Path bParent = bnfParent(b);
    BnfPathsResolution bPaths = BnfPaths.resolve(b);
    assertEquals(bParent.resolve("../b-in").normalize(), bPaths.path(KnownAttribute.INPUT_PATH));
    assertNotSame(aPaths, bPaths);
  }

  // ---- Construction from explicit map (test fixtures + Main.java path) ----

  public void testEmptyResolutionExposesNoPaths() {
    assertNull(BnfPathsResolution.EMPTY.path(KnownAttribute.INPUT_PATH));
    assertNull(BnfPathsResolution.EMPTY.path(KnownAttribute.PARSER_OUTPUT_PATH));
    assertNull(BnfPaths.referencePath(BnfPathsResolution.EMPTY, KnownAttribute.MIXIN));
    assertNull(BnfPaths.referencePath(BnfPathsResolution.EMPTY, null));
  }

  public void testResolveExplicitFromMap() {
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, Path.of("/tmp/parser"));
    map.put(KnownAttribute.PSI_INPUT_PATH, Path.of("/tmp/psi-in"));
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);

    assertEquals(Path.of("/tmp/parser"), paths.path(KnownAttribute.PARSER_OUTPUT_PATH));
    assertEquals(Path.of("/tmp/psi-in"), paths.path(KnownAttribute.PSI_INPUT_PATH));
    // BnfPaths.resolveExplicit applies the output cascade — psi falls back to parser.
    assertEquals(Path.of("/tmp/parser"), paths.path(KnownAttribute.PSI_OUTPUT_PATH));
  }

  public void testEveryOutputAttributeCascadesToParserWhenPsiUnset() {
    BnfPathsResolution paths = BnfPaths.resolveExplicit(
      Map.of(KnownAttribute.PARSER_OUTPUT_PATH, Path.of("/tmp/parser")));
    // No PSI override: the cascade collapses to parser for every dependent attribute.
    assertEquals(Path.of("/tmp/parser"), paths.path(KnownAttribute.PSI_OUTPUT_PATH));
    assertEquals(Path.of("/tmp/parser"), paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    assertEquals(Path.of("/tmp/parser"), paths.path(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    assertEquals(Path.of("/tmp/parser"), paths.path(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH));
  }

  public void testEtAttributesCascadeToPsiWhenPsiSet() {
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, Path.of("/tmp/parser"));
    map.put(KnownAttribute.PSI_OUTPUT_PATH, Path.of("/tmp/psi"));
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);
    // Element-type artifacts travel with PSI, not the parser, when PSI has its own root.
    assertEquals(Path.of("/tmp/psi"), paths.path(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    assertEquals(Path.of("/tmp/psi"), paths.path(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH));
    assertEquals(Path.of("/tmp/psi"), paths.path(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH));
  }

  public void testPathStringReturnsExplicitValue() {
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, Path.of("/tmp/parser"));
    map.put(KnownAttribute.PSI_OUTPUT_PATH, Path.of("/tmp/psi"));
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);
    assertEquals("/tmp/psi", paths.pathString(KnownAttribute.PSI_OUTPUT_PATH));
    assertEquals("/tmp/parser", paths.pathString(KnownAttribute.PARSER_OUTPUT_PATH));
  }

  public void testPathStringThrowsWhenAttributeUnresolved() {
    BnfPathsResolution paths = BnfPaths.resolveExplicit(
      Map.of(KnownAttribute.PARSER_OUTPUT_PATH, Path.of("/tmp/parser")));
    try {
      paths.pathString(KnownAttribute.PSI_INPUT_PATH);
      fail("expected IllegalStateException for unset input attribute");
    }
    catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().contains("psiInputPath"));
    }
  }

  public void testPathStringThrowsWhenOutputCascadeHasNoParserPath() {
    try {
      BnfPathsResolution.EMPTY.pathString(KnownAttribute.PARSER_OUTPUT_PATH);
      fail("expected IllegalStateException for empty resolution");
    }
    catch (IllegalStateException expected) {
      // PARSER_OUTPUT_PATH unset means the whole output cascade has nothing to fall back to.
    }
  }

  // ---- Negative / malformed input -----------------------------------------

  public void testEmptyInputPathTreatedAsUnset() {
    BnfFile bnf = bnfWith("g.bnf", "{ inputPath=\"\" }\nroot ::= 'a'");
    // Empty string is still "unset"; the unset fallback resolves to the BNF file's parent.
    assertEquals(bnfParent(bnf), BnfPaths.resolve(bnf).path(KnownAttribute.INPUT_PATH));
  }

  public void testDotResolvesToBnfParent() {
    BnfFile bnf = bnfWith("g.bnf", "{ inputPath=\".\" }\nroot ::= 'a'");
    Path parent = bnfParent(bnf);
    assertEquals(parent.normalize(), BnfPaths.resolve(bnf).path(KnownAttribute.INPUT_PATH));
  }

  public void testManyDoubleDotsEscapeProjectRoot() {
    BnfFile bnf = bnfWith("g.bnf", "{ inputPath=\"../../../../../../tmp\" }\nroot ::= 'a'");
    Path parent = bnfParent(bnf);
    Path expected = parent.resolve("../../../../../../tmp").normalize();
    assertEquals(expected, BnfPaths.resolve(bnf).path(KnownAttribute.INPUT_PATH));
  }

  // ---- Input cascade -------------------------------------------------------

  public void testInputCascadeFillsPsiInputFromGlobal() {
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.INPUT_PATH, Path.of("/tmp/in"));
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);
    assertEquals(Path.of("/tmp/in"), paths.path(KnownAttribute.PSI_INPUT_PATH));
  }

  public void testInputCascadePreservesExplicitOverrides() {
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.INPUT_PATH, Path.of("/tmp/in"));
    map.put(KnownAttribute.PSI_INPUT_PATH, Path.of("/tmp/psi-in"));
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);
    assertEquals(Path.of("/tmp/psi-in"), paths.path(KnownAttribute.PSI_INPUT_PATH));
    assertEquals(Path.of("/tmp/in"), paths.path(KnownAttribute.INPUT_PATH));
  }

  public void testResolveExplicitWithBnfParentDefaultsInputPath() {
    Path bnfParent = Path.of("/tmp/grammar");
    BnfPathsResolution paths = BnfPaths.resolveExplicit(Map.of(), bnfParent);
    assertEquals(bnfParent, paths.path(KnownAttribute.INPUT_PATH));
    assertEquals(bnfParent, paths.path(KnownAttribute.PSI_INPUT_PATH));
  }

  public void testResolveExplicitWithBnfParentRespectsExplicitInput() {
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.INPUT_PATH, Path.of("/tmp/explicit-in"));
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map, Path.of("/tmp/grammar"));
    assertEquals(Path.of("/tmp/explicit-in"), paths.path(KnownAttribute.INPUT_PATH));
    assertEquals(Path.of("/tmp/explicit-in"), paths.path(KnownAttribute.PSI_INPUT_PATH));
  }
}
