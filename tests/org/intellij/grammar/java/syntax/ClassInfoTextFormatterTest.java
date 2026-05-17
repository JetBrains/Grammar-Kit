/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmTypeRef;
import org.intellij.grammar.classinfo.JvmTypeRefs;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * Pins the textual format produced by {@link ClassSymbolTextFormatter}. Hand-built {@link ClassSymbol}
 * objects exercise every branch (empty class, multi-class sort, modifiers, class/method
 * annotations, type parameters with bounds and annotations, multifileFacade, each
 * {@link MethodType}, method generics, throws, per-parameter annotations, annotatedTypes
 * divergence). When this test fails the golden tests' .txt files all need to be re-recorded.
 */
public class ClassInfoTextFormatterTest extends TestCase {

  public void testEmptyClassRendersHeaderAndEmptyMethods() {
    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = Fqn.of("a.b.Empty");
    ClassSymbol built = info.build();
    String expected = """
      class a.b.Empty
        methods: (none)""";
    assertEquals(expected, ClassSymbolTextFormatter.format(Map.of(built.name(), built)));
  }

  public void testClassWithModifiersExtendsAndImplements() {
    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = Fqn.of("a.b.Foo");
    info.superClass = Fqn.of("a.b.Base");
    info.modifiers = Modifier.PUBLIC | Modifier.FINAL;
    info.interfaces.add(Fqn.of("a.b.I1"));
    info.interfaces.add(Fqn.of("a.b.I2"));
    ClassSymbol built = info.build();
    String expected = """
      class a.b.Foo extends a.b.Base implements a.b.I1, a.b.I2
        modifiers: public final
        methods: (none)""";
    assertEquals(expected, ClassSymbolTextFormatter.format(Map.of(built.name(), built)));
  }

  public void testClassAnnotationsAndTypeParameters() {
    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = Fqn.of("a.b.Boxed");
    info.modifiers = Modifier.PUBLIC;
    info.annotations.add(Fqn.of("java.lang.Deprecated"));
    info.typeParameters.add("T");
    info.typeParameters.add("U extends java.lang.Number");
    ClassSymbol built = info.build();
    String expected = """
      class a.b.Boxed
        modifiers: public
        annotations: @java.lang.Deprecated
        typeParameters: T, U extends java.lang.Number
        methods: (none)""";
    assertEquals(expected, ClassSymbolTextFormatter.format(Map.of(built.name(), built)));
  }

  public void testMultifileFacadeAndMethodVariants() {
    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = Fqn.of("util.Utils");
    info.modifiers = Modifier.PUBLIC | Modifier.FINAL;
    info.multifileFacade = true;

    MethodSymbol.Builder ctor = new MethodSymbol.Builder();
    ctor.methodType = MethodType.CONSTRUCTOR;
    ctor.name = "Utils";
    ctor.declaringClass = info.name;
    ctor.modifiers = Modifier.PRIVATE;
    ctor.returnType = JvmTypeRefs.raw("util.Utils"); // synthetic return = declaring class
    info.methods.add(ctor);

    MethodSymbol.Builder instanceMethod = new MethodSymbol.Builder();
    instanceMethod.methodType = MethodType.INSTANCE;
    instanceMethod.name = "greet";
    instanceMethod.declaringClass = info.name;
    instanceMethod.modifiers = Modifier.PUBLIC;
    instanceMethod.returnType = new JvmTypeRef.UserType(Fqn.of("java.lang.String"),
                                                        List.of(Fqn.of("a.b.A")),
                                                        List.of());
    ParameterSymbol.Builder nameParam = new ParameterSymbol.Builder();
    nameParam.type = JvmTypeRefs.raw("java.lang.String");
    nameParam.name = "name";
    nameParam.annotations.add(Fqn.of("org.jetbrains.annotations.Nullable"));
    instanceMethod.parameters.add(nameParam);
    instanceMethod.annotations.add(Fqn.of("org.jetbrains.annotations.NotNull"));
    instanceMethod.exceptions.add(Fqn.of("java.io.IOException"));
    TypeParameterSymbol.Builder tp = new TypeParameterSymbol.Builder("T");
    tp.extendsList.add(JvmTypeRefs.raw("java.lang.Comparable"));
    tp.annotations.add(Fqn.of("a.b.Marker"));
    instanceMethod.generics.add(tp);
    info.methods.add(instanceMethod);

    MethodSymbol.Builder staticMethod = new MethodSymbol.Builder();
    staticMethod.methodType = MethodType.STATIC;
    staticMethod.name = "helper";
    staticMethod.declaringClass = info.name;
    staticMethod.modifiers = Modifier.PRIVATE | Modifier.STATIC;
    staticMethod.returnType = JvmTypeRefs.raw("void");
    info.methods.add(staticMethod);

    ClassSymbol built = info.build();
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
            annotatedTypes: [@a.b.A java.lang.String, java.lang.String, name]
          STATIC private static void helper()""";
    assertEquals(expected, ClassSymbolTextFormatter.format(Map.of(built.name(), built)));
  }

  public void testMultipleClassesAreSortedByFqn() {
    ClassSymbol.Builder a = new ClassSymbol.Builder();
    a.name = Fqn.of("z.Aardvark");
    ClassSymbol.Builder b = new ClassSymbol.Builder();
    b.name = Fqn.of("a.Banana");
    ClassSymbol builtA = a.build();
    ClassSymbol builtB = b.build();
    String expected = """
      class a.Banana
        methods: (none)

      class z.Aardvark
        methods: (none)""";
    assertEquals(expected, ClassSymbolTextFormatter.format(Map.of(builtA.name(), builtA, builtB.name(), builtB)));
  }
}
