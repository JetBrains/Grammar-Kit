/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Unit tests for {@link PackageDeclarationReader}: confirms the lexer-driven scanner returns the
 * declared package for both Java and Kotlin source files, copes with leading comments / file-level
 * annotations, and falls back to the default package on edge cases (empty file, missing directive,
 * unreadable file).
 */
public class PackageDeclarationReaderTest extends TestCase {

  private Path dir;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dir = Files.createTempDirectory("pkg-decl-reader-test");
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      if (dir != null) {
        try (var stream = Files.walk(dir)) {
          stream.sorted((a, b) -> b.getNameCount() - a.getNameCount())
                .forEach(p -> p.toFile().delete());
        }
      }
    }
    finally {
      super.tearDown();
    }
  }

  // ---------- Kotlin ----------

  public void testKotlinSimplePackage() throws IOException {
    assertEquals("com.foo.bar", readKotlin("package com.foo.bar\nclass X"));
  }

  public void testKotlinDefaultPackageWhenNoDirective() throws IOException {
    assertEquals("", readKotlin("class X"));
  }

  public void testKotlinLeadingLineComments() throws IOException {
    assertEquals("a.b", readKotlin("// header\n// second line\npackage a.b\nclass X"));
  }

  public void testKotlinLeadingBlockComment() throws IOException {
    assertEquals("a.b", readKotlin("/* license\n   block */\npackage a.b\n"));
  }

  public void testKotlinLeadingKDoc() throws IOException {
    assertEquals("a.b", readKotlin("/**\n * KDoc on the file\n */\npackage a.b\n"));
  }

  public void testKotlinFileLevelAnnotationsBeforePackage() throws IOException {
    assertEquals("util", readKotlin("@file:JvmName(\"Utils\")\n@file:JvmMultifileClass\npackage util\nfun a() = 1\n"));
  }

  public void testKotlinWhitespaceAroundDots() throws IOException {
    // Kotlin accepts arbitrary whitespace inside the package directive.
    assertEquals("a.b.c", readKotlin("package a . b . c\nclass X"));
  }

  public void testKotlinPackageKeywordInsideStringLiteralIgnored() throws IOException {
    // The lexer classifies "package" inside a string as STRING content, so the scanner doesn't
    // mistake it for a directive. Here the file has no real directive — should yield default.
    assertEquals("", readKotlin("val msg = \"package fake.thing\"\nclass X"));
  }

  public void testKotlinEmptyFile() throws IOException {
    assertEquals("", readKotlin(""));
  }

  public void testKotlinTrailingDotIsTrimmed() throws IOException {
    // Malformed directive: trailing dot followed by a terminator. The reader should drop the dot
    // rather than return "a." (which would index the file under a bogus package).
    assertEquals("a", readKotlin("package a.\nclass X"));
  }

  public void testKotlinUnreadableFileFallsBackToDefault() {
    Path missing = dir.resolve("does-not-exist.kt");
    assertEquals("", PackageDeclarationReader.readKotlinPackage(missing));
  }

  // ---------- Java ----------

  public void testJavaSimplePackage() throws IOException {
    assertEquals("com.foo.bar", readJava("package com.foo.bar;\npublic class X {}"));
  }

  public void testJavaDefaultPackageWhenNoDirective() throws IOException {
    assertEquals("", readJava("public class X {}"));
  }

  public void testJavaLeadingLineComments() throws IOException {
    assertEquals("a.b", readJava("// header\n// second\npackage a.b;\nclass X {}"));
  }

  public void testJavaLeadingBlockComment() throws IOException {
    assertEquals("a.b", readJava("/* license */\npackage a.b;\nclass X {}"));
  }

  public void testJavaLeadingJavadoc() throws IOException {
    assertEquals("a.b", readJava("/**\n * file doc\n */\npackage a.b;\nclass X {}"));
  }

  public void testJavaPackageKeywordInsideStringLiteralIgnored() throws IOException {
    // Java doesn't allow string literals at the top level, but a stray declaration with strings
    // shouldn't be misread either. Here only the directive should match.
    assertEquals("real.pkg", readJava("package real.pkg;\nclass X { String s = \"package fake\"; }"));
  }

  public void testJavaEmptyFile() throws IOException {
    assertEquals("", readJava(""));
  }

  public void testJavaTrailingDotIsTrimmed() throws IOException {
    // Malformed directive `package a.;` — the reader should drop the dangling dot rather than
    // return "a.".
    assertEquals("a", readJava("package a.;\nclass X {}"));
  }

  // ---------- helpers ----------

  private @NotNull String readJava(@NotNull String text) throws IOException {
    return PackageDeclarationReader.readJavaPackage(writeTemp(text, ".java"));
  }

  private @NotNull String readKotlin(@NotNull String text) throws IOException {
    return PackageDeclarationReader.readKotlinPackage(writeTemp(text, ".kt"));
  }

  private @NotNull Path writeTemp(@NotNull String content, @NotNull String ext) throws IOException {
    Path f = Files.createTempFile(dir, "src", ext);
    Files.writeString(f, content);
    return f;
  }
}
