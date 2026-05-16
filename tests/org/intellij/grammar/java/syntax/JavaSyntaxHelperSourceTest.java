/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassExtractor;
import org.intellij.grammar.classinfo.java.JavaSyntaxTreeManager;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source-driven tests for the syntax-generation pipeline: each test parses an in-memory Java
 * source string with {@link JavaSyntaxTreeManager#parseText}, runs
 * {@link JavaSyntaxClassExtractor#extractFrom} on the result, and compares the produced
 * {@code Map<Fqn, ClassInfo>} against a golden file under {@code testData/syntax/java/source/}.
 * Tests that exercise {@link JvmSyntaxHelper} filtering on top of the extracted records (e.g.
 * abstract-method gating, supertype probing) keep an extra assertion against a helper built from
 * the same map.
 */
@SuppressWarnings("UnstableApiUsage")
public class JavaSyntaxHelperSourceTest extends GoldenClassInfoTestCase {

  @Override
  protected @NotNull String goldenDir() {
    return "syntax/java/source";
  }

  // ---------------------------------------------------------------------------------------------
  // Class shape
  // ---------------------------------------------------------------------------------------------

  public void testPublicTopLevelClass() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Foo extends Base {}
      """));
  }

  public void testPackagePrivateClass() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      class Hidden {}
      """));
  }

  public void testInterfaceFlagSet() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public interface Iface {}
      """));
  }

  public void testNoExtendsClauseDefaultsToObject() {
    // No explicit `extends`: extractor inserts java.lang.Object so reflection-style chains work.
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Free {}
      """));
  }

  public void testImplementsListPopulated() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C implements I1, I2 {}
      """));
  }

  public void testClassAnnotation() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      @Deprecated
      public class C {}
      """));
  }

  // ---------------------------------------------------------------------------------------------
  // Multiple class declarations in one file
  // ---------------------------------------------------------------------------------------------

  public void testPublicAndPackagePrivateClassesInSameFile() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Bundle {
        public Helper helper() { return null; }
      }
      class Helper {
        public String greet() { return ""; }
      }
      """));
  }

  public void testInnerClassFqn() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Outer {
        public static class Inner {
          public String inside() { return ""; }
        }
      }
      """));
  }

  // ---------------------------------------------------------------------------------------------
  // Methods: shape, signatures
  // ---------------------------------------------------------------------------------------------

  public void testInstanceMethodSignature() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      import java.util.List;
      import java.util.Map;
      public class C {
        public List<String> doIt(int count, Map<String, Integer> data) {
          return null;
        }
      }
      """));
  }

  public void testStaticMethodFiltering() {
    Map<Fqn, ClassInfo> classes = extract("""
      package a.b;
      public class C {
        public void inst() {}
        public static void stat() {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: helper.findClassMethods filters by MethodType.
    JavaHelper helper = helperFrom(classes);
    assertEquals(1, helper.findClassMethods("a.b.C", MethodType.STATIC, "stat", -1).size());
    assertTrue(helper.findClassMethods("a.b.C", MethodType.STATIC, "inst", -1).isEmpty());
    assertEquals(1, helper.findClassMethods("a.b.C", MethodType.INSTANCE, "inst", -1).size());
  }

  public void testConstructorExtraction() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public C(int seed) {}
      }
      """));
  }

  public void testAbstractMethodGatedByAllowAbstract() {
    Map<Fqn, ClassInfo> classes = extract("""
      package a.b;
      public abstract class C {
        public abstract void doIt();
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: by default findClassMethods filters out abstract methods unless allowAbstract=true.
    JavaHelper helper = helperFrom(classes);
    assertTrue(helper.findClassMethods("a.b.C", MethodType.INSTANCE, "doIt", -1).isEmpty());
    assertEquals(1, helper.findClassMethods("a.b.C", MethodType.INSTANCE, "doIt", true, -1).size());
  }

  public void testPrivateConstructorExcluded() {
    Map<Fqn, ClassInfo> classes = extract("""
      package a.b;
      public class C {
        private C() {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: the helper excludes private constructors from match results.
    JavaHelper helper = helperFrom(classes);
    assertTrue(helper.findClassMethods("a.b.C", MethodType.CONSTRUCTOR, "C", -1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // Method-level metadata
  // ---------------------------------------------------------------------------------------------

  public void testMethodAnnotation() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        @Override
        public String toString() { return ""; }
      }
      """));
  }

  public void testThrowsClause() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public void doIt() throws java.io.IOException, RuntimeException {}
      }
      """));
  }

  public void testGenericMethodTypeParameter() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public static <U> U cast(Object o) { return (U)o; }
      }
      """));
  }

  public void testParameterAnnotationsResolvedAgainstSamePackage() {
    // Unresolved @Marker is qualified as a.b.Marker (same-package fallback).
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public void doIt(@Marker int count, int plain) {}
      }
      """));
  }

  // ---------------------------------------------------------------------------------------------
  // Parameter-type matching via supertype probe (helper behaviour, not extraction shape)
  // ---------------------------------------------------------------------------------------------

  public void testParamTypeMatchedViaSuperclass() {
    Map<Fqn, ClassInfo> classes = extract("""
      package a.b;
      public class Base {}
      public class Child extends Base {}
      public class Receiver {
        public void take(Base b) {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: caller passes Child as the param-type probe; receiver wants Base.
    JavaHelper helper = helperFrom(classes);
    assertEquals(1, helper.findClassMethods("a.b.Receiver", MethodType.INSTANCE, "take", -1, "a.b.Child").size());
  }

  public void testParamTypeMatchedViaInterface() {
    Map<Fqn, ClassInfo> classes = extract("""
      package a.b;
      public interface Iface {}
      public class Child implements Iface {}
      public class Receiver {
        public void take(Iface i) {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    JavaHelper helper = helperFrom(classes);
    assertEquals(1, helper.findClassMethods("a.b.Receiver", MethodType.INSTANCE, "take", -1, "a.b.Child").size());
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private static @NotNull Map<Fqn, ClassInfo> extract(@NotNull String source) {
    SyntaxNode root = JavaSyntaxTreeManager.parseText(source);
    // Tests run extraction in isolation — no cross-language probing, so a null-returning resolver is fine.
    return new HashMap<>(JavaSyntaxClassExtractor.extractFrom(root, fqn -> null));
  }

  private static @NotNull JavaHelper helperFrom(@NotNull Map<Fqn, ClassInfo> classes) {
    JvmClassSymbolProvider provider = (fqn, resolver) -> {
      ClassInfo info = classes.get(fqn);
      return info == null ? Map.of() : Map.of(fqn, info);
    };
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(provider)));
  }
}
