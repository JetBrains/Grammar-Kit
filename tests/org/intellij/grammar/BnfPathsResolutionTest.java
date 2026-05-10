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
 * single source of truth for {@code *OutputPath} resolution.
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

  public void testRelativePathsAreNormalized() {
    BnfFile bnf = bnfWith("g.bnf", "{ parserOutputPath=\"./a/../b\" }\nroot ::= 'a'");
    Path parent = bnfParent(bnf);
    assertEquals(parent.resolve("b").normalize(), BnfPaths.resolve(bnf).path(KnownAttribute.PARSER_OUTPUT_PATH));
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

  // ---- Caching -------------------------------------------------------------

  public void testRepeatedResolveReturnsSameInstance() {
    BnfFile bnf = bnfWith("g.bnf", "{ parserOutputPath=\"../x\" }\nroot ::= 'a'");
    BnfPathsResolution a = BnfPaths.resolve(bnf);
    BnfPathsResolution b = BnfPaths.resolve(bnf);
    assertSame(a, b);
  }

  public void testDifferentBnfFilesGetIndependentResolutions() {
    BnfFile a = bnfWith("a.bnf", "{ parserOutputPath=\"../a-out\" }\nroot ::= 'x'");
    Path aParent = bnfParent(a);
    BnfPathsResolution aPaths = BnfPaths.resolve(a);
    assertEquals(aParent.resolve("../a-out").normalize(), aPaths.path(KnownAttribute.PARSER_OUTPUT_PATH));
    // configureByText replaces the previous fixture file, so we re-fetch the parent for the new BNF.
    BnfFile b = bnfWith("b.bnf", "{ parserOutputPath=\"../b-out\" }\nroot ::= 'x'");
    Path bParent = bnfParent(b);
    BnfPathsResolution bPaths = BnfPaths.resolve(b);
    assertEquals(bParent.resolve("../b-out").normalize(), bPaths.path(KnownAttribute.PARSER_OUTPUT_PATH));
    assertNotSame(aPaths, bPaths);
  }

  // ---- Construction from explicit map (test fixtures + Main.java path) ----

  public void testEmptyResolutionExposesNoPaths() {
    assertNull(BnfPathsResolution.EMPTY.path(KnownAttribute.PARSER_OUTPUT_PATH));
    assertNull(BnfPathsResolution.EMPTY.path(KnownAttribute.PSI_OUTPUT_PATH));
  }

  public void testResolveExplicitFromMap() {
    Map<KnownAttribute<String>, Path> map = new HashMap<>();
    map.put(KnownAttribute.PARSER_OUTPUT_PATH, Path.of("/tmp/parser"));
    BnfPathsResolution paths = BnfPaths.resolveExplicit(map);

    assertEquals(Path.of("/tmp/parser"), paths.path(KnownAttribute.PARSER_OUTPUT_PATH));
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
    try {
      BnfPathsResolution.EMPTY.pathString(KnownAttribute.PSI_OUTPUT_PATH);
      fail("expected IllegalStateException for unset output attribute");
    }
    catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().contains("psiOutputPath"));
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

  public void testEmptyOutputPathTreatedAsUnset() {
    BnfFile bnf = bnfWith("g.bnf", "{ parserOutputPath=\"\" }\nroot ::= 'a'");
    // PARSER_OUTPUT_PATH may be filled in by the parserClass-package fallback, but never by "".
    Path resolved = BnfPaths.resolve(bnf).path(KnownAttribute.PARSER_OUTPUT_PATH);
    if (resolved != null) {
      assertFalse("empty string must not become a resolved path",
                  resolved.toString().equals(bnfParent(bnf).toString() + "/"));
    }
  }

  public void testManyDoubleDotsEscapeProjectRoot() {
    BnfFile bnf = bnfWith("g.bnf", "{ parserOutputPath=\"../../../../../../tmp\" }\nroot ::= 'a'");
    Path parent = bnfParent(bnf);
    Path expected = parent.resolve("../../../../../../tmp").normalize();
    assertEquals(expected, BnfPaths.resolve(bnf).path(KnownAttribute.PARSER_OUTPUT_PATH));
  }
}
