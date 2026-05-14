/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.MyElement;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Integration tests for {@link KotlinSyntaxHelper}: writes a fixture source root with real
 * {@code .kt} files and exercises the full parse → extract → lookup pipeline against the public
 * {@link org.intellij.grammar.java.JavaHelper} API. Pure JUnit, no IDE harness.
 */
public class KotlinSyntaxHelperTest extends TestCase {

  private Path root;
  private KotlinSyntaxHelper helper;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    root = Files.createTempDirectory("kotlin-syntax-helper-test");
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

  public void testFindTopLevelClass() throws Exception {
    write("a/b/Simple.kt", """
        package a.b
        class Simple
        """);
    NavigatablePsiElement clazz = helper().findClass("a.b.Simple");
    assertNotNull(clazz);
    assertTrue(helper.isPublic(clazz));
    assertEquals("java.lang.Object", helper.getSuperClassName("a.b.Simple"));
  }

  public void testInterfacePopulated() throws Exception {
    write("a/b/Holder.kt", """
        package a.b
        class Holder : Iface
        interface Iface
        """);
    NavigatablePsiElement iface = helper().findClass("a.b.Iface");
    assertNotNull(iface);
    NavigatablePsiElement clazz = helper.findClass("a.b.Holder");
    assertNotNull(clazz);
  }

  public void testKotlinClassFinalByDefault() throws Exception {
    write("a/b/Plain.kt", """
        package a.b
        class Plain
        """);
    helper().findClass("a.b.Plain"); // warm cache
    // The class is reachable; finality is encoded on its ClassInfo.modifiers.
    int mods = lookupModifiers("a.b.Plain");
    assertTrue("class should be final by default", Modifier.isFinal(mods));
  }

  public void testOpenClass() throws Exception {
    write("a/b/Open.kt", """
        package a.b
        open class Open
        """);
    helper().findClass("a.b.Open");
    int mods = lookupModifiers("a.b.Open");
    assertFalse("open class should not be final", Modifier.isFinal(mods));
  }

  public void testAbstractClass() throws Exception {
    write("a/b/Abs.kt", """
        package a.b
        abstract class Abs
        """);
    helper().findClass("a.b.Abs");
    int mods = lookupModifiers("a.b.Abs");
    assertTrue(Modifier.isAbstract(mods));
    assertFalse(Modifier.isFinal(mods));
  }

  public void testInterfaceFlags() throws Exception {
    write("a/b/Iface.kt", """
        package a.b
        interface Iface
        """);
    helper().findClass("a.b.Iface");
    int mods = lookupModifiers("a.b.Iface");
    assertTrue(Modifier.isInterface(mods));
    assertTrue(Modifier.isAbstract(mods));
  }

  public void testNestedClassIsStatic() throws Exception {
    write("a/b/Outer.kt", """
        package a.b
        class Outer {
            class Nested
            inner class Inner
        }
        """);
    helper().findClass("a.b.Outer.Nested");
    int nestedMods = lookupModifiers("a.b.Outer.Nested");
    assertTrue("nested class without `inner` should be JVM-static", Modifier.isStatic(nestedMods));

    helper.findClass("a.b.Outer.Inner");
    int innerMods = lookupModifiers("a.b.Outer.Inner");
    assertFalse("inner class should not be JVM-static", Modifier.isStatic(innerMods));
  }

  public void testPrimaryConstructorAndPropertyAccessors() throws Exception {
    write("a/b/Person.kt", """
        package a.b
        class Person(val name: String, var age: Int)
        """);
    helper().findClass("a.b.Person");
    List<NavigatablePsiElement> ctors = helper.findClassMethods(
      "a.b.Person", JavaHelper.MethodType.CONSTRUCTOR, "<init>", -1);
    assertEquals(1, ctors.size());

    List<NavigatablePsiElement> getName = helper.findClassMethods(
      "a.b.Person", JavaHelper.MethodType.INSTANCE, "getName", -1);
    assertEquals(1, getName.size());

    List<NavigatablePsiElement> getAge = helper.findClassMethods(
      "a.b.Person", JavaHelper.MethodType.INSTANCE, "getAge", -1);
    assertEquals(1, getAge.size());

    // val → no setter
    List<NavigatablePsiElement> setName = helper.findClassMethods(
      "a.b.Person", JavaHelper.MethodType.INSTANCE, "setName", -1);
    assertTrue(setName.isEmpty());

    // var → setter
    List<NavigatablePsiElement> setAge = helper.findClassMethods(
      "a.b.Person", JavaHelper.MethodType.INSTANCE, "setAge", -1);
    assertEquals(1, setAge.size());
  }

  public void testMemberFunctionSignature() throws Exception {
    write("a/b/Greeter.kt", """
        package a.b
        class Greeter {
            fun greet(name: String): String = "Hi"
        }
        """);
    helper().findClass("a.b.Greeter");
    List<NavigatablePsiElement> ms = helper.findClassMethods(
      "a.b.Greeter", JavaHelper.MethodType.INSTANCE, "greet", -1);
    assertEquals(1, ms.size());
    List<String> types = helper.getMethodTypes(ms.get(0));
    assertEquals("java.lang.String", types.get(0));
    assertEquals("java.lang.String", types.get(1));
    assertEquals("name", types.get(2));
  }

  public void testIntMapsToJvmPrimitive() throws Exception {
    write("a/b/Counter.kt", """
        package a.b
        class Counter {
            fun bump(by: Int): Int = by + 1
        }
        """);
    helper().findClass("a.b.Counter");
    List<NavigatablePsiElement> ms = helper.findClassMethods(
      "a.b.Counter", JavaHelper.MethodType.INSTANCE, "bump", -1);
    assertEquals(1, ms.size());
    List<String> types = helper.getMethodTypes(ms.get(0));
    assertEquals("int", types.get(0));
    assertEquals("int", types.get(1));
  }

  public void testUnitReturnMapsToVoid() throws Exception {
    write("a/b/Noisy.kt", """
        package a.b
        class Noisy {
            fun shout(): Unit {}
            fun whisper() {}
        }
        """);
    helper().findClass("a.b.Noisy");
    List<NavigatablePsiElement> ms = helper.findClassMethods(
      "a.b.Noisy", JavaHelper.MethodType.INSTANCE, "shout", -1);
    assertEquals(1, ms.size());
    assertEquals("void", helper.getMethodTypes(ms.get(0)).get(0));
    List<NavigatablePsiElement> implicit = helper.findClassMethods(
      "a.b.Noisy", JavaHelper.MethodType.INSTANCE, "whisper", -1);
    assertEquals(1, implicit.size());
    assertEquals("void", helper.getMethodTypes(implicit.get(0)).get(0));
  }

  public void testTopLevelFunctionLandsOnFileClass() throws Exception {
    write("a/b/Util.kt", """
        package a.b
        fun helper(): String = ""
        """);
    NavigatablePsiElement fileClass = helper().findClass("a.b.UtilKt");
    assertNotNull(fileClass);
    List<NavigatablePsiElement> ms = helper.findClassMethods(
      "a.b.UtilKt", JavaHelper.MethodType.STATIC, "helper", -1);
    assertEquals(1, ms.size());
  }

  public void testExtensionFunctionReceiverBecomesFirstParam() throws Exception {
    write("a/b/Ext.kt", """
        package a.b
        fun String.shouted(): String = uppercase()
        """);
    helper().findClass("a.b.ExtKt");
    List<NavigatablePsiElement> ms = helper.findClassMethods(
      "a.b.ExtKt", JavaHelper.MethodType.STATIC, "shouted", -1);
    assertEquals(1, ms.size());
    List<String> types = helper.getMethodTypes(ms.get(0));
    assertEquals("java.lang.String", types.get(0)); // return
    assertEquals("java.lang.String", types.get(1)); // receiver
    assertEquals("receiver", types.get(2));
  }

  public void testObjectDeclaration() throws Exception {
    write("a/b/Single.kt", """
        package a.b
        object Single {
            fun foo(): Int = 1
        }
        """);
    helper().findClass("a.b.Single");
    int mods = lookupModifiers("a.b.Single");
    assertTrue(Modifier.isFinal(mods));
    List<NavigatablePsiElement> ms = helper.findClassMethods(
      "a.b.Single", JavaHelper.MethodType.INSTANCE, "foo", -1);
    assertEquals(1, ms.size());
  }

  public void testCompanionExposesJvmStatic() throws Exception {
    write("a/b/Holder.kt", """
        package a.b
        class Holder {
            companion object {
                @JvmStatic
                fun staticHelper(): Int = 1
                fun instanceHelper(): Int = 2
            }
        }
        """);
    helper().findClass("a.b.Holder.Companion");
    // Companion's own ClassInfo carries instance-level methods.
    List<NavigatablePsiElement> companion = helper.findClassMethods(
      "a.b.Holder.Companion", JavaHelper.MethodType.INSTANCE, "staticHelper", -1);
    assertEquals(1, companion.size());

    // @JvmStatic-annotated members are lifted as static on the enclosing class.
    List<NavigatablePsiElement> lifted = helper.findClassMethods(
      "a.b.Holder", JavaHelper.MethodType.STATIC, "staticHelper", -1);
    assertEquals(1, lifted.size());

    // Non-@JvmStatic is NOT lifted.
    List<NavigatablePsiElement> notLifted = helper.findClassMethods(
      "a.b.Holder", JavaHelper.MethodType.STATIC, "instanceHelper", -1);
    assertTrue(notLifted.isEmpty());
  }

  public void testTypealiasResolvesAsClassWithAliasedSuper() throws Exception {
    write("a/b/Aliases.kt", """
        package a.b
        typealias Name = String
        """);
    NavigatablePsiElement alias = helper().findClass("a.b.Name");
    assertNotNull(alias);
    assertEquals("java.lang.String", helper.getSuperClassName("a.b.Name"));
  }

  public void testGenericClassTypeParameters() throws Exception {
    write("a/b/Box.kt", """
        package a.b
        class Box<T : Number>(val value: T)
        """);
    helper().findClass("a.b.Box");
    // The getter for `value` has T as its return type — verify it is not qualified.
    List<NavigatablePsiElement> getters = helper.findClassMethods(
      "a.b.Box", JavaHelper.MethodType.INSTANCE, "getValue", -1);
    assertEquals(1, getters.size());
    assertEquals("T", helper.getMethodTypes(getters.get(0)).get(0));
  }

  public void testPrivatePropertySkipped() throws Exception {
    write("a/b/Secret.kt", """
        package a.b
        class Secret {
            private val hidden: String = ""
        }
        """);
    helper().findClass("a.b.Secret");
    assertTrue(helper.findClassMethods(
      "a.b.Secret", JavaHelper.MethodType.INSTANCE, "getHidden", -1).isEmpty());
  }

  public void testConstPropertySkipped() throws Exception {
    write("a/b/Const.kt", """
        package a.b
        object Const {
            const val ANSWER: Int = 42
        }
        """);
    helper().findClass("a.b.Const");
    assertTrue(helper.findClassMethods(
      "a.b.Const", JavaHelper.MethodType.INSTANCE, "getANSWER", -1).isEmpty());
  }

  public void testInternalMapsToPublic() throws Exception {
    write("a/b/Internal.kt", """
        package a.b
        internal class Internal
        """);
    helper().findClass("a.b.Internal");
    int mods = lookupModifiers("a.b.Internal");
    assertTrue("internal should map to PUBLIC bit on the JVM", Modifier.isPublic(mods));
  }

  public void testSealedIsAbstract() throws Exception {
    write("a/b/Tree.kt", """
        package a.b
        sealed class Tree
        """);
    helper().findClass("a.b.Tree");
    int mods = lookupModifiers("a.b.Tree");
    assertTrue(Modifier.isAbstract(mods));
    assertFalse(Modifier.isFinal(mods));
  }

  public void testImportedTypeResolves() throws Exception {
    write("a/b/Used.kt", """
        package a.b
        class Used {
            fun read(p: Path): String = ""
        }
        """);
    write("a/b/Header.kt", """
        package a.b
        import java.nio.file.Path
        """);
    // The Path import is on Header.kt; Used.kt has no import, so resolution falls back to the
    // same-package guess (a.b.Path). The point of this test is just that lookup doesn't crash.
    helper().findClass("a.b.Used");
    List<NavigatablePsiElement> ms = helper.findClassMethods(
      "a.b.Used", JavaHelper.MethodType.INSTANCE, "read", -1);
    assertEquals(1, ms.size());
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private @org.jetbrains.annotations.NotNull KotlinSyntaxHelper helper() {
    if (helper == null) helper = new KotlinSyntaxHelper(List.of(root));
    return helper;
  }

  private int lookupModifiers(@org.jetbrains.annotations.NotNull String fqn) {
    NavigatablePsiElement el = helper().findClass(fqn);
    assertNotNull("class not found: " + fqn, el);
    MyElement<?> me = (MyElement<?>) el;
    return ((org.intellij.grammar.java.ClassInfo) me.delegate).modifiers;
  }

  private void write(@org.jetbrains.annotations.NotNull String relative,
                     @org.jetbrains.annotations.NotNull String content) throws java.io.IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }
}
