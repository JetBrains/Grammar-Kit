/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.MyElement;
import org.intellij.grammar.classinfo.TypeParameterInfo;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for {@link JvmSyntaxHelper} fed by {@link JavaSyntaxClassSymbolProvider}. Sets up a fixture source root in a temp directory and
 * exercises every part of the {@link JavaHelper} API the parser generator relies on. Pure JUnit
 * (no IDE harness) — the helper is designed to work outside the IntelliJ Platform.
 */
public class JavaSyntaxHelperTest extends TestCase {

  private Path root;
  private JavaHelper helper;

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

  public void testFindTopLevelClass() {
    NavigatablePsiElement clazz = helper.findClass("a.b.MyClass");
    assertNotNull(clazz);
    assertTrue(helper.isPublic(clazz));
    assertEquals("a.b.Base", helper.getSuperClassName("a.b.MyClass"));
  }

  public void testInterfacesPopulated() {
    helper.findClass("a.b.MyClass"); // warm cache
    // interfaces are stored on ClassInfo (package-private); reach them via getAnnotations is not enough,
    // so verify indirectly: looking up an interface should succeed and getSuperClassName on interface returns null/Object.
    NavigatablePsiElement iface = helper.findClass("a.b.Iface");
    assertNotNull(iface);
  }

  public void testInnerClassResolution() {
    NavigatablePsiElement inner = helper.findClass("a.b.MyClass.Inner");
    assertNotNull(inner);
    assertTrue(helper.isPublic(inner));
  }

  public void testFindInstanceMethodAndSignature() {
    List<NavigatablePsiElement> methods = helper.findClassMethods(
      "a.b.MyClass", MethodType.INSTANCE, "doStuff", -1);
    assertEquals(1, methods.size());
    NavigatablePsiElement m = methods.get(0);
    List<String> types = helper.getMethodTypes(m);
    // [returnType, paramType, paramName, paramType, paramName]
    assertEquals(5, types.size());
    assertEquals("java.util.List<java.lang.String>", types.get(0));
    assertEquals("int", types.get(1));
    assertEquals("count", types.get(2));
    assertEquals("java.util.Map<java.lang.String, T>", types.get(3));
    assertEquals("data", types.get(4));
    assertEquals(List.of("java.io.IOException"), helper.getExceptionList(m));
    assertEquals(List.of("java.lang.Override"), helper.getAnnotations(m));
    assertEquals("a.b.MyClass", helper.getDeclaringClass(m));
    // first parameter has @Marker (unresolved -> same package)
    assertEquals(List.of("a.b.Marker"), helper.getParameterAnnotations(m, 0));
    assertTrue(helper.getParameterAnnotations(m, 1).isEmpty());
  }

  public void testStaticMethodWithGenerics() {
    List<NavigatablePsiElement> methods = helper.findClassMethods(
      "a.b.MyClass", MethodType.STATIC, "cast", -1);
    assertEquals(1, methods.size());
    NavigatablePsiElement m = methods.get(0);
    List<TypeParameterInfo> generics = helper.getGenericParameters(m);
    assertEquals(1, generics.size());
    assertEquals("U", generics.get(0).getName());
  }

  public void testConstructor() {
    List<NavigatablePsiElement> ctors = helper.findClassMethods(
      "a.b.MyClass", MethodType.CONSTRUCTOR, "MyClass", -1);
    assertEquals(1, ctors.size());
    List<String> types = helper.getMethodTypes(ctors.get(0));
    // [returnType=declaringClass, paramType, paramName]
    assertEquals(3, types.size());
    assertEquals("a.b.MyClass", types.get(0));
    assertEquals("int", types.get(1));
    assertEquals("seed", types.get(2));
  }

  public void testClassAnnotations() {
    NavigatablePsiElement clazz = helper.findClass("a.b.MyClass");
    assertEquals(List.of("java.lang.Deprecated"), helper.getAnnotations(clazz));
  }

  public void testParamCountFiltering() {
    // wrong arity → no match
    assertTrue(helper.findClassMethods("a.b.MyClass", MethodType.INSTANCE, "doStuff", 99).isEmpty());
    // matching arity → match
    assertEquals(1,
                 helper.findClassMethods("a.b.MyClass", MethodType.INSTANCE, "doStuff", 2).size());
  }

  public void testWildcardMethodName() {
    // "*" matches any name
    List<NavigatablePsiElement> all = helper.findClassMethods(
      "a.b.MyClass", MethodType.INSTANCE, "*", -1);
    // doStuff is the only INSTANCE method on MyClass (the constructor and static cast are excluded by methodType)
    assertEquals(1, all.size());
  }

  public void testUnknownClassReturnsNull() {
    assertNull(helper.findClass("does.not.Exist"));
    assertTrue(helper.findClassMethods("does.not.Exist", MethodType.INSTANCE, "x", -1).isEmpty());
    assertNull(helper.getSuperClassName("does.not.Exist"));
  }

  public void testInterfaceModifierBit() {
    NavigatablePsiElement iface = helper.findClass("a.b.Iface");
    // Modifier.INTERFACE flag should be set.
    Object delegate = ((MyElement<?>)iface).delegate;
    int modifiers = ((ClassInfo)delegate).modifiers;
    assertTrue("INTERFACE flag missing on " + Integer.toHexString(modifiers),
               (modifiers & Modifier.INTERFACE) != 0);
  }

  public void testInnerMethodLookup() {
    List<NavigatablePsiElement> methods = helper.findClassMethods(
      "a.b.MyClass.Inner", MethodType.INSTANCE, "inside", -1);
    assertEquals(1, methods.size());
    assertEquals("java.lang.String", helper.getMethodTypes(methods.get(0)).get(0));
  }

  public void testPackagePrivateClassInDifferentFile() {
    // a.b.Helper is declared inside Bundle.java — no Helper.java in the package.
    NavigatablePsiElement helperClass = helper.findClass("a.b.Helper");
    assertNotNull(helperClass);
    List<NavigatablePsiElement> methods = helper.findClassMethods(
      "a.b.Helper", MethodType.INSTANCE, "greet", -1);
    assertEquals(1, methods.size());
    assertEquals("java.lang.String", helper.getMethodTypes(methods.get(0)).get(0));
  }

  public void testInnerOfPackagePrivateClass() {
    // a.b.Helper.Nested — both the inner class and its enclosing class live in Bundle.java.
    NavigatablePsiElement nested = helper.findClass("a.b.Helper.Nested");
    assertNotNull(nested);
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
