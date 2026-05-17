/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.syntax.FixtureExtractor;
import org.intellij.grammar.java.syntax.GoldenClassInfoTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Source-driven tests for the Kotlin side of the syntax-generation pipeline. Each test writes a
 * small {@code .kt} fixture to a temp dir, then asks
 * {@link KotlinSyntaxClassSymbolProvider} for the resulting {@code Map<Fqn, ClassSymbol>} via
 * {@link FixtureExtractor#extractAll} and compares it to a golden under
 * {@code testData/syntax/kotlin/source/}. Helper-behaviour assertions (negative cache) keep their
 * own checks.
 */
public class KotlinSyntaxHelperSourceTest extends GoldenClassInfoTestCase {

  private Path root;

  @Override
  protected @NotNull String goldenDir() {
    return "syntax/kotlin/source";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    root = Files.createTempDirectory("kotlin-syntax-helper-source");
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      if (root != null) {
        try (var stream = Files.walk(root)) {
          stream.sorted((p1, p2) -> p2.getNameCount() - p1.getNameCount())
                .forEach(p -> p.toFile().delete());
        }
      }
    }
    finally {
      super.tearDown();
    }
  }

  public void testMultipleTopLevelClassesPerFile() throws Exception {
    // Single file declares two classes whose names don't match the file name. The slow-path
    // package scan must ingest the file and surface both.
    write("pkg/Pair.kt", """
        package pkg
        class Alpha
        class Beta
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassSynthesisFromTopLevelFunction() throws Exception {
    write("util/Strings.kt", """
        package util
        fun shout(s: String): String = s.uppercase()
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassNotSynthesisedWhenOnlyClassesDeclared() throws Exception {
    write("only/Nothing.kt", """
        package only
        class Plain
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testNegativeCacheMissReturnsNull() throws Exception {
    // No fixture, no extraction: this is a pure helper-behaviour test verifying the negative cache
    // short-circuits a second lookup of an unknown FQN.
    JavaHelper helper = helper();
    assertNull(helper.findClass("nowhere.Missing"));
    assertNull(helper.findClass("nowhere.Missing"));
  }

  public void testFileClassWithMixedTopLevelAndClass() throws Exception {
    write("mix/Mixed.kt", """
        package mix
        fun util(): String = ""
        class Other
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassJvmNameRename() throws Exception {
    write("util/Bar.kt", """
        @file:JvmName("Utils")
        package util
        fun shout(s: String): String = s.uppercase()
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassJvmMultifileFacadeMergesAcrossFiles() throws Exception {
    write("util/A.kt", """
        @file:JvmName("Utils")
        @file:JvmMultifileClass
        package util
        fun a(): String = "a"
        """);
    write("util/B.kt", """
        @file:JvmName("Utils")
        @file:JvmMultifileClass
        package util
        fun b(): String = "b"
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassJvmNameWithoutTopLevelCallablesDoesNotSynthesize() throws Exception {
    write("util/Bar.kt", """
        @file:JvmName("Utils")
        package util
        class Bar
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testInternalClassWithNonMatchingFileName() throws Exception {
    // Kotlin allows a file's primary class name to differ from the file name.
    write("pkg/wrong.kt", """
        package pkg
        class RightlyNamed
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testNullabilityAnnotationsFromKotlinTypes() throws Exception {
    // Cross-section of the rules in KotlinSyntaxTypeFormatter.classifyNullability:
    //  - reference types: NotNull vs Nullable based on the '?' marker
    //  - primitives: no annotation; nullable primitives box and get Nullable
    //  - bare type variables: no annotation
    //  - val/var properties: getter/setter follow the property type
    //  - extension functions: receiver follows its own nullability
    //  - Unit/void return: no annotation
    write("nul/Sample.kt", """
        package nul
        class Sample {
          fun ref(s: String, n: String?): String? = n
          fun prim(x: Int, y: Int?): Boolean = x == 0
          fun <T> id(x: T): T = x
          val title: String = ""
          var nick: String? = null
        }
        fun String?.ext(x: Int?): Unit { }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private @NotNull Map<Fqn, ClassSymbol> extractAll() {
    return FixtureExtractor.extractAll(root, new KotlinSyntaxClassSymbolProvider(List.of(root)), ".kt");
  }

  private @NotNull JavaHelper helper() {
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(new KotlinSyntaxClassSymbolProvider(List.of(root)))));
  }

  private void write(@NotNull String relative, @NotNull String content) throws IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }
}
