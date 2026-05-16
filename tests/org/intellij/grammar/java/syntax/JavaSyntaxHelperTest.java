/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * File-system integration tests for the Java syntax-generation pipeline. A shared fixture under
 * a temp directory drives the same parse → extract → lookup path the IDE uses. The full extraction
 * output is snapshotted by {@link #testFixtureExtraction} (golden:
 * {@code testData/syntax/java/file/testFixtureExtraction.txt}); the other tests exercise
 * {@link JvmSyntaxHelper} filtering / fallback behaviours that go beyond field readback.
 */
public class JavaSyntaxHelperTest extends GoldenClassInfoTestCase {

  private Path root;
  private JavaHelper helper;

  @Override
  protected @NotNull String goldenDir() {
    return "syntax/java/file";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    root = Files.createTempDirectory("syntax-helper-test");
    write("a/b/MyClass.java", """
      package a.b;

      import java.util.List;
      import java.util.Map;

      @Deprecated
      public class MyClass<T extends Number> extends Base implements Iface, Other {

        public static final int CONST = 0;

        public MyClass(int seed) {
        }

        @Override
        public List<String> doStuff(@Marker int count, Map<String, T> data) throws java.io.IOException {
          return null;
        }

        public static <U> U cast(Object o) {
          return (U) o;
        }

        public static class Inner implements Cloneable {
          public Inner() {}
          public String inside() { return ""; }
        }
      }
      """);
    write("a/b/Base.java", """
      package a.b;
      public class Base {
        public void baseMethod() {}
      }
      """);
    write("a/b/Iface.java", """
      package a.b;
      public interface Iface {}
      """);
    // Bundle.java declares a public class (Bundle) and a package-private sibling (Helper).
    // Helper has a non-matching file name, exercising the slow-path package scan.
    write("a/b/Bundle.java", """
      package a.b;

      public class Bundle {
        public Helper helper() { return null; }
      }

      class Helper {
        public String greet() { return ""; }
        public Helper.Nested nest() { return null; }
        static class Nested {}
      }
      """);
    helper = new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(new JavaSyntaxClassSymbolProvider(List.of(root)))));
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

  /**
   * Full extraction shape for the shared fixture: every class the provider produces from the four
   * fixture files, with modifiers, supertypes, interfaces, methods, annotations, generics, throws,
   * and per-parameter annotations. Updating any of those in the extractor surfaces here first.
   */
  public void testFixtureExtraction() {
    Map<Fqn, ClassInfo> classes = FixtureExtractor.extractAll(
      root, new JavaSyntaxClassSymbolProvider(List.of(root)), ".java");
    assertClassInfoMatchesGolden(classes);
  }

  // ---------------------------------------------------------------------------------------------
  // Helper-behaviour tests (filtering, fallback, negative cache) — not covered by the golden
  // ---------------------------------------------------------------------------------------------

  public void testParamCountFiltering() {
    // wrong arity → no match
    assertTrue(helper.findClassMethods("a.b.MyClass", MethodType.INSTANCE, "doStuff", 99).isEmpty());
    // matching arity → match
    assertEquals(1, helper.findClassMethods("a.b.MyClass", MethodType.INSTANCE, "doStuff", 2).size());
  }

  public void testWildcardMethodName() {
    // "*" matches any name; doStuff is the only INSTANCE method on MyClass.
    List<NavigatablePsiElement> all = helper.findClassMethods(
      "a.b.MyClass", MethodType.INSTANCE, "*", -1);
    assertEquals(1, all.size());
  }

  public void testUnknownClassReturnsNull() {
    assertNull(helper.findClass("does.not.Exist"));
    assertTrue(helper.findClassMethods("does.not.Exist", MethodType.INSTANCE, "x", -1).isEmpty());
    assertNull(helper.getSuperClassName("does.not.Exist"));
  }

  public void testNegativeCacheStillWorks() {
    // First call exercises the slow path and adds to negative cache.
    assertNull(helper.findClass("a.b.NoSuchClass"));
    // Second call must short-circuit via negative cache (no exception, same result).
    assertNull(helper.findClass("a.b.NoSuchClass"));
  }

  private void write(String relativePath, String content) throws Exception {
    Path target = root.resolve(relativePath);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }
}
