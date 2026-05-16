/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.TypeParameterInfo;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * Pins the textual format produced by {@link ClassInfoTextFormatter}. Hand-built {@link ClassInfo}
 * objects exercise every branch (empty class, multi-class sort, modifiers, class/method
 * annotations, type parameters with bounds and annotations, multifileFacade, each
 * {@link MethodType}, method generics, throws, per-parameter annotations, annotatedTypes
 * divergence). When this test fails the golden tests' .txt files all need to be re-recorded.
 */
public class ClassInfoTextFormatterTest extends TestCase {

  public void testEmptyClassRendersHeaderAndEmptyMethods() {
    ClassInfo info = new ClassInfo();
    info.name = Fqn.of("a.b.Empty");
    String expected = """
      class a.b.Empty
        methods: (none)""";
    assertEquals(expected, ClassInfoTextFormatter.format(Map.of(info.name, info)));
  }

  public void testClassWithModifiersExtendsAndImplements() {
    ClassInfo info = new ClassInfo();
    info.name = Fqn.of("a.b.Foo");
    info.superClass = Fqn.of("a.b.Base");
    info.modifiers = Modifier.PUBLIC | Modifier.FINAL;
    info.interfaces.add(Fqn.of("a.b.I1"));
    info.interfaces.add(Fqn.of("a.b.I2"));
    String expected = """
      class a.b.Foo extends a.b.Base implements a.b.I1, a.b.I2
        modifiers: public final
        methods: (none)""";
    assertEquals(expected, ClassInfoTextFormatter.format(Map.of(info.name, info)));
  }

  public void testClassAnnotationsAndTypeParameters() {
    ClassInfo info = new ClassInfo();
    info.name = Fqn.of("a.b.Boxed");
    info.modifiers = Modifier.PUBLIC;
    info.annotations.add(Fqn.of("java.lang.Deprecated"));
    info.typeParameters.add("T");
    info.typeParameters.add("U extends java.lang.Number");
    String expected = """
      class a.b.Boxed
        modifiers: public
        annotations: @java.lang.Deprecated
        typeParameters: T, U extends java.lang.Number
        methods: (none)""";
    assertEquals(expected, ClassInfoTextFormatter.format(Map.of(info.name, info)));
  }

  public void testMultifileFacadeAndMethodVariants() {
    ClassInfo info = new ClassInfo();
    info.name = Fqn.of("util.Utils");
    info.modifiers = Modifier.PUBLIC | Modifier.FINAL;
    info.multifileFacade = true;

    MethodSymbol ctor = new MethodSymbol();
    ctor.methodType = MethodType.CONSTRUCTOR;
    ctor.name = "Utils";
    ctor.declaringClass = info.name;
    ctor.modifiers = Modifier.PRIVATE;
    ctor.types.add("util.Utils"); // synthetic return = declaring class
    info.methods.add(ctor);

    MethodSymbol instanceMethod = new MethodSymbol();
    instanceMethod.methodType = MethodType.INSTANCE;
    instanceMethod.name = "greet";
    instanceMethod.declaringClass = info.name;
    instanceMethod.modifiers = Modifier.PUBLIC;
    instanceMethod.types.add("java.lang.String");      // return
    instanceMethod.types.add("java.lang.String");      // p0 type
    instanceMethod.types.add("name");                  // p0 name
    instanceMethod.annotations.get(0).add(Fqn.of("org.jetbrains.annotations.NotNull"));
    instanceMethod.annotations.get(1).add(Fqn.of("org.jetbrains.annotations.Nullable"));
    instanceMethod.exceptions.add(Fqn.of("java.io.IOException"));
    instanceMethod.generics.add(new TypeParameterInfo(
      "T",
      List.of("java.lang.Comparable"),
      List.of(Fqn.of("a.b.Marker"))));
    instanceMethod.annotatedTypes.add("java.lang.@A String");
    instanceMethod.annotatedTypes.add("java.lang.String");
    instanceMethod.annotatedTypes.add("name");
    info.methods.add(instanceMethod);

    MethodSymbol staticMethod = new MethodSymbol();
    staticMethod.methodType = MethodType.STATIC;
    staticMethod.name = "helper";
    staticMethod.declaringClass = info.name;
    staticMethod.modifiers = Modifier.PRIVATE | Modifier.STATIC;
    staticMethod.types.add("void");
    info.methods.add(staticMethod);

    String expected = """
      class util.Utils
        modifiers: public final
        multifileFacade
        methods:
          CONSTRUCTOR private Utils()
          INSTANCE public java.lang.String greet(java.lang.String name)
            typeParameters: @a.b.Marker T extends java.lang.Comparable
            annotations: @org.jetbrains.annotations.NotNull
            param[0] annotations: @org.jetbrains.annotations.Nullable
            throws: java.io.IOException
            annotatedTypes: [java.lang.@A String, java.lang.String, name]
          STATIC private static void helper()""";
    assertEquals(expected, ClassInfoTextFormatter.format(Map.of(info.name, info)));
  }

  public void testMultipleClassesAreSortedByFqn() {
    ClassInfo a = new ClassInfo();
    a.name = Fqn.of("z.Aardvark");
    ClassInfo b = new ClassInfo();
    b.name = Fqn.of("a.Banana");
    String expected = """
      class a.Banana
        methods: (none)

      class z.Aardvark
        methods: (none)""";
    assertEquals(expected, ClassInfoTextFormatter.format(Map.of(a.name, a, b.name, b)));
  }
}
