/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.TypeParameterInfo;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassExtractor;
import org.intellij.grammar.classinfo.java.JavaSyntaxTreeManager;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.MyElement;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source-driven tests for {@link JavaSyntaxHelper}: each test parses an in-memory Java source
 * string with {@link JavaSyntaxTreeManager#parseText} (the same call the file-backed path uses),
 * runs the extracted {@link Map} of {@link ClassInfo} records through the package-private
 * {@code JavaSyntaxHelper(Function, JavaHelper)} constructor, and asserts what the helper produces.
 * <p>
 * This isolates the parse + extract + helper pipeline from the file-I/O layer
 * ({@link SourceRootResolver}, {@link JavaClassManager}'s caching / package scan) — no temp
 * directories, no on-disk fixtures. {@link JavaSyntaxHelperTest} drives the same pipeline against
 * real files on disk; {@link JavaSyntaxHelperUnitTest} drives the helper with a hand-built lookup
 * to cover filtering / fallback paths in isolation.
 */
@SuppressWarnings("UnstableApiUsage")
public class JavaSyntaxHelperSourceTest extends TestCase {

  // ---------------------------------------------------------------------------------------------
  // Class shape
  // ---------------------------------------------------------------------------------------------

  public void testPublicTopLevelClass() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class Foo extends Base {}
      """);
    NavigatablePsiElement el = helper.findClass("a.b.Foo");
    assertNotNull(el);
    assertTrue(helper.isPublic(el));
    assertEquals("a.b.Base", helper.getSuperClassName("a.b.Foo"));
  }

  public void testPackagePrivateClass() {
    JavaHelper helper = helperFor("""
      package a.b;
      class Hidden {}
      """);
    NavigatablePsiElement el = helper.findClass("a.b.Hidden");
    assertNotNull(el);
    assertFalse(helper.isPublic(el));
  }

  public void testInterfaceFlagSet() {
    JavaHelper helper = helperFor("""
      package a.b;
      public interface Iface {}
      """);
    ClassInfo info = unwrap(helper.findClass("a.b.Iface"));
    assertNotNull(info);
    assertTrue("INTERFACE flag should be set, was 0x" + Integer.toHexString(info.modifiers),
               (info.modifiers & Modifier.INTERFACE) != 0);
  }

  public void testNoExtendsClauseDefaultsToObject() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class Free {}
      """);
    // No explicit `extends`: extractor inserts java.lang.Object so reflection-style chains work.
    assertEquals("java.lang.Object", helper.getSuperClassName("a.b.Free"));
  }

  public void testImplementsListPopulated() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C implements I1, I2 {}
      """);
    ClassInfo info = unwrap(helper.findClass("a.b.C"));
    assertNotNull(info);
    assertEquals(List.of("a.b.I1", "a.b.I2"), info.interfaces);
  }

  public void testClassAnnotation() {
    JavaHelper helper = helperFor("""
      package a.b;
      @Deprecated
      public class C {}
      """);
    assertEquals(List.of("java.lang.Deprecated"),
                 helper.getAnnotations(helper.findClass("a.b.C")));
  }

  // ---------------------------------------------------------------------------------------------
  // Multiple class declarations in one file
  // ---------------------------------------------------------------------------------------------

  public void testPublicAndPackagePrivateClassesInSameFile() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class Bundle {
        public Helper helper() { return null; }
      }
      class Helper {
        public String greet() { return ""; }
      }
      """);
    assertNotNull(helper.findClass("a.b.Bundle"));
    NavigatablePsiElement helperClass = helper.findClass("a.b.Helper");
    assertNotNull(helperClass);
    assertFalse(helper.isPublic(helperClass));
  }

  public void testInnerClassFqn() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class Outer {
        public static class Inner {
          public String inside() { return ""; }
        }
      }
      """);
    assertNotNull(helper.findClass("a.b.Outer.Inner"));
    assertEquals(1, helper.findClassMethods(
      "a.b.Outer.Inner", MethodType.INSTANCE, "inside", -1).size());
  }

  // ---------------------------------------------------------------------------------------------
  // Methods: shape, signatures
  // ---------------------------------------------------------------------------------------------

  public void testInstanceMethodSignature() {
    JavaHelper helper = helperFor("""
      package a.b;
      import java.util.List;
      import java.util.Map;
      public class C {
        public List<String> doIt(int count, Map<String, Integer> data) {
          return null;
        }
      }
      """);
    List<NavigatablePsiElement> methods = helper.findClassMethods(
      "a.b.C", MethodType.INSTANCE, "doIt", -1);
    assertEquals(1, methods.size());
    NavigatablePsiElement m = methods.get(0);

    List<String> types = helper.getMethodTypes(m);
    // [returnType, paramType, paramName, paramType, paramName]
    assertEquals(5, types.size());
    assertEquals("java.util.List<java.lang.String>", types.get(0));
    assertEquals("int", types.get(1));
    assertEquals("count", types.get(2));
    assertEquals("java.util.Map<java.lang.String, java.lang.Integer>", types.get(3));
    assertEquals("data", types.get(4));

    assertEquals("a.b.C", helper.getDeclaringClass(m));
  }

  public void testStaticMethodFiltering() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C {
        public void inst() {}
        public static void stat() {}
      }
      """);
    assertEquals(1, helper.findClassMethods(
      "a.b.C", MethodType.STATIC, "stat", -1).size());
    assertTrue(helper.findClassMethods(
      "a.b.C", MethodType.STATIC, "inst", -1).isEmpty());
    assertEquals(1, helper.findClassMethods(
      "a.b.C", MethodType.INSTANCE, "inst", -1).size());
  }

  public void testConstructorExtraction() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C {
        public C(int seed) {}
      }
      """);
    List<NavigatablePsiElement> ctors = helper.findClassMethods(
      "a.b.C", MethodType.CONSTRUCTOR, "C", -1);
    assertEquals(1, ctors.size());
    List<String> types = helper.getMethodTypes(ctors.get(0));
    assertEquals("a.b.C", types.get(0));   // synthetic return = declaring class
    assertEquals("int", types.get(1));
    assertEquals("seed", types.get(2));
  }

  public void testAbstractMethodGatedByAllowAbstract() {
    JavaHelper helper = helperFor("""
      package a.b;
      public abstract class C {
        public abstract void doIt();
      }
      """);
    assertTrue(helper.findClassMethods(
      "a.b.C", MethodType.INSTANCE, "doIt", -1).isEmpty());
    assertEquals(1, helper.findClassMethods(
      "a.b.C", MethodType.INSTANCE, "doIt", true, -1).size());
  }

  public void testPrivateConstructorExcluded() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C {
        private C() {}
      }
      """);
    assertTrue(helper.findClassMethods(
      "a.b.C", MethodType.CONSTRUCTOR, "C", -1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // Method-level metadata
  // ---------------------------------------------------------------------------------------------

  public void testMethodAnnotation() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C {
        @Override
        public String toString() { return ""; }
      }
      """);
    NavigatablePsiElement m = helper.findClassMethods(
      "a.b.C", MethodType.INSTANCE, "toString", -1).get(0);
    assertEquals(List.of("java.lang.Override"), helper.getAnnotations(m));
  }

  public void testThrowsClause() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C {
        public void doIt() throws java.io.IOException, RuntimeException {}
      }
      """);
    NavigatablePsiElement m = helper.findClassMethods(
      "a.b.C", MethodType.INSTANCE, "doIt", -1).get(0);
    assertEquals(List.of("java.io.IOException", "java.lang.RuntimeException"),
                 helper.getExceptionList(m));
  }

  public void testGenericMethodTypeParameter() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C {
        public static <U> U cast(Object o) { return (U)o; }
      }
      """);
    NavigatablePsiElement m = helper.findClassMethods(
      "a.b.C", MethodType.STATIC, "cast", -1).get(0);
    List<TypeParameterInfo> generics = helper.getGenericParameters(m);
    assertEquals(1, generics.size());
    assertEquals("U", generics.get(0).getName());
  }

  public void testParameterAnnotationsResolvedAgainstSamePackage() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class C {
        public void doIt(@Marker int count, int plain) {}
      }
      """);
    NavigatablePsiElement m = helper.findClassMethods(
      "a.b.C", MethodType.INSTANCE, "doIt", -1).get(0);
    // Unresolved @Marker is qualified as a.b.Marker (same-package fallback).
    assertEquals(List.of("a.b.Marker"), helper.getParameterAnnotations(m, 0));
    assertTrue(helper.getParameterAnnotations(m, 1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // Parameter-type matching via supertype probe
  // ---------------------------------------------------------------------------------------------

  public void testParamTypeMatchedViaSuperclass() {
    JavaHelper helper = helperFor("""
      package a.b;
      public class Base {}
      public class Child extends Base {}
      public class Receiver {
        public void take(Base b) {}
      }
      """);
    // Caller passes Child as the param-type probe; receiver wants Base.
    assertEquals(1, helper.findClassMethods(
      "a.b.Receiver", MethodType.INSTANCE, "take", -1, "a.b.Child").size());
  }

  public void testParamTypeMatchedViaInterface() {
    JavaHelper helper = helperFor("""
      package a.b;
      public interface Iface {}
      public class Child implements Iface {}
      public class Receiver {
        public void take(Iface i) {}
      }
      """);
    assertEquals(1, helper.findClassMethods(
      "a.b.Receiver", MethodType.INSTANCE, "take", -1, "a.b.Child").size());
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  /**
   * Parses {@code source}, runs the class-info extractor, and returns a {@link JavaSyntaxHelper}
   * backed by the resulting {@code FQN -> ClassInfo} map (no fallback). Tests that need a fallback
   * can construct the helper directly using {@link #extract}.
   */
  private static @NotNull JavaHelper helperFor(@NotNull String source) {
    Map<String, ClassInfo> classes = extract(source);
    JvmClassSymbolProvider provider = (fqn, resolver) -> {
      ClassInfo info = classes.get(fqn);
      return info == null ? Map.of() : Map.of(fqn, info);
    };
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(provider)));
  }

  private static @NotNull Map<String, ClassInfo> extract(@NotNull String source) {
    SyntaxNode root = JavaSyntaxTreeManager.parseText(source);
    // Tests run extraction in isolation — no cross-language probing, so a null-returning resolver is fine.
    return new HashMap<>(JavaSyntaxClassExtractor.extractFrom(root, fqn -> null));
  }

  private static ClassInfo unwrap(NavigatablePsiElement el) {
    return el instanceof MyElement<?> e && e.delegate instanceof ClassInfo ci ? ci : null;
  }
}
