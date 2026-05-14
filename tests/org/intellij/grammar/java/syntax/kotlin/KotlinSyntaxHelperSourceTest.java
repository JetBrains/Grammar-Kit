/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.java.syntax.KotlinSyntaxHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Slow-path / source-layout scenarios for {@link KotlinSyntaxHelper}. Mirrors
 * {@code JavaSyntaxHelperSourceTest} for the Kotlin side: multiple top-level classes per file,
 * non-matching file names, file-class synthesis, and negative caching.
 */
public class KotlinSyntaxHelperSourceTest extends TestCase {

  private Path root;
  private KotlinSyntaxHelper helper;

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
    NavigatablePsiElement alpha = helper().findClass("pkg.Alpha");
    NavigatablePsiElement beta = helper().findClass("pkg.Beta");
    assertNotNull("Alpha should be found via package scan", alpha);
    assertNotNull("Beta should be found via package scan", beta);
  }

  public void testFileClassSynthesisFromTopLevelFunction() throws Exception {
    write("util/Strings.kt", """
        package util
        fun shout(s: String): String = s.uppercase()
        """);
    NavigatablePsiElement fileClass = helper().findClass("util.StringsKt");
    assertNotNull(fileClass);
    List<NavigatablePsiElement> staticHelpers = helper.findClassMethods(
      "util.StringsKt", MethodType.STATIC, "shout", -1);
    assertEquals(1, staticHelpers.size());
  }

  public void testFileClassNotSynthesisedWhenOnlyClassesDeclared() throws Exception {
    write("only/Nothing.kt", """
        package only
        class Plain
        """);
    helper().findClass("only.Plain"); // warm cache
    assertNull("No top-level callables → no <File>Kt class",
               helper.findClass("only.NothingKt"));
  }

  public void testNegativeCacheMissReturnsNull() {
    assertNull(helper().findClass("nowhere.Missing"));
    // Second lookup: same answer, no exception (negative cache shouldn't double-scan).
    assertNull(helper.findClass("nowhere.Missing"));
  }

  public void testFileClassWithMixedTopLevelAndClass() throws Exception {
    write("mix/Mixed.kt", """
        package mix
        fun util(): String = ""
        class Other
        """);
    assertNotNull(helper().findClass("mix.MixedKt"));
    assertNotNull(helper.findClass("mix.Other"));
  }

  public void testInternalClassWithNonMatchingFileName() throws Exception {
    // Kotlin freely allows a file's primary class name to differ from the file name. The fast
    // path will miss; the slow-path package scan finds it.
    write("pkg/wrong.kt", """
        package pkg
        class RightlyNamed
        """);
    NavigatablePsiElement clazz = helper().findClass("pkg.RightlyNamed");
    assertNotNull(clazz);
  }

  private @org.jetbrains.annotations.NotNull KotlinSyntaxHelper helper() {
    if (helper == null) helper = new KotlinSyntaxHelper(List.of(root));
    return helper;
  }

  private void write(@org.jetbrains.annotations.NotNull String relative,
                     @org.jetbrains.annotations.NotNull String content) throws java.io.IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }
}
