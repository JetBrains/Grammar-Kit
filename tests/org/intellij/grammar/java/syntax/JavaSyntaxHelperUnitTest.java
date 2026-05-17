/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.JvmTypeRefs;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.MyElement;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for {@link JavaSyntaxHelper} filtering / fallback logic.
 * <p>
 * Bypasses the source-file parsing pipeline ({@link JavaClassManager},
 * {@link JavaSyntaxTreeManager}, {@link JavaSyntaxClassExtractor}) by injecting an in-memory
 * FQN → {@link ClassSymbol.Builder} lookup via the package-private test constructor, so each test owns a
 * small, controlled type universe. The matching integration test {@link JavaSyntaxHelperTest}
 * exercises the full file → ClassSymbol flow against real {@code .java} fixtures.
 */
public class JavaSyntaxHelperUnitTest extends TestCase {

  private final Map<String, ClassSymbol.Builder> classes = new HashMap<>();
  private RecordingFallback fallback;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    classes.clear();
    fallback = new RecordingFallback();
  }

  // ---------------------------------------------------------------------------------------------
  // findClass
  // ---------------------------------------------------------------------------------------------

  public void testFindClassReturnsWrappedClassInfo() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object");
    NavigatablePsiElement el = helper().findClass("a.b.Foo");
    assertNotNull(el);
    Object delegate = ((MyElement<?>)el).delegate;
    assertTrue(delegate instanceof ClassSymbol);
    assertEquals(Fqn.of("a.b.Foo"), ((ClassSymbol)delegate).name());
  }

  public void testFindClassMissAndNoFallbackReturnsNull() {
    assertNull(helperNoFallback().findClass("a.b.Missing"));
  }

  public void testFindClassMissDelegatesToFallback() {
    ClassSymbol.Builder fallbackInfo = new ClassSymbol.Builder();
    fallbackInfo.name = Fqn.of("a.b.PlatformClass");
    fallback.classes.put("a.b.PlatformClass", fallbackInfo);

    NavigatablePsiElement el = helper().findClass("a.b.PlatformClass");
    assertNotNull(el);
    Object delegate = ((MyElement<?>)el).delegate;
    assertTrue(delegate instanceof ClassSymbol);
    assertEquals(Fqn.of("a.b.PlatformClass"), ((ClassSymbol)delegate).name());
    assertEquals(List.of("a.b.PlatformClass"), fallback.findClassCalls);
  }

  public void testFindClassNullArgWithoutFallback() {
    assertNull(helperNoFallback().findClass(null));
  }

  public void testFindClassNullArgWithFallback() {
    // Null FQNs are short-circuited by the manager and never reach a provider.
    assertNull(helper().findClass(null));
    assertTrue(fallback.findClassCalls.isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - lookup and short-circuits
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsMissDelegatesToFallback() {
    // A class known only to the fallback provider must still expose its methods through the
    // unified pipeline: manager looks up the class once, JvmSyntaxHelper iterates its methods.
    ClassSymbol.Builder external = new ClassSymbol.Builder();
    external.name = Fqn.of("a.b.PlatformClass");
    external.methods.add(method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    fallback.classes.put("a.b.PlatformClass", external);

    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.PlatformClass", MethodType.INSTANCE, "ping", false, -1);
    assertEquals(1, result.size());
    assertEquals(List.of("a.b.PlatformClass"), fallback.findClassCalls);
  }

  public void testFindClassMethodsMissNoFallbackReturnsEmpty() {
    JavaHelper noFallback = helperNoFallback();
    assertTrue(noFallback.findClassMethods("a.b.Missing", MethodType.INSTANCE, "x", false, -1).isEmpty());
  }

  public void testFindClassMethodsNullMethodNameReturnsEmpty() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, null, false, -1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - name matching
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsExactNameMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"),
                  method("pong", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", false, -1);
    assertEquals(1, result.size());
    assertEquals("ping", ((MethodSymbol)((MyElement<?>)result.get(0)).delegate).name());
  }

  public void testFindClassMethodsWildcardMatchesEverything() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"),
                  method("pong", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "*", false, -1);
    assertEquals(2, result.size());
  }

  public void testFindClassMethodsInnerClassPrefixMatch() {
    // acceptsName: method.name "Outer$Inner" matches expected "Outer".
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("Outer$Inner", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "Outer", false, -1);
    assertEquals(1, result.size());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - methodType filtering
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsMethodTypeMismatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.STATIC, "ping", false, -1).isEmpty());
  }

  public void testFindClassMethodsConstructorMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("Foo", Modifier.PUBLIC, MethodType.CONSTRUCTOR, "a.b.Foo"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.CONSTRUCTOR, "Foo", false, -1);
    assertEquals(1, result.size());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - modifier filtering
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsExcludesAbstractByDefault() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC | Modifier.ABSTRACT,
                         MethodType.INSTANCE, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", false, -1).isEmpty());
  }

  public void testFindClassMethodsAllowAbstract() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC | Modifier.ABSTRACT,
                         MethodType.INSTANCE, "void"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", true, -1);
    assertEquals(1, result.size());
  }

  public void testFindClassMethodsExcludesPrivateConstructor() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("Foo", Modifier.PRIVATE,
                         MethodType.CONSTRUCTOR, "a.b.Foo"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.CONSTRUCTOR, "Foo", false, -1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - parameter matching
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsParamCountSkippedWhenMinusOne() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1);
    assertEquals(1, result.size());
  }

  public void testFindClassMethodsParamCountMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String", "int"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false,  2).size());
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false,  1).isEmpty());
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false,  3).isEmpty());
  }

  public void testFindClassMethodsParamTypesEmptyAcceptsAny() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1).size());
  }

  public void testFindClassMethodsParamTypesExactMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "java.lang.String").size());
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "java.lang.Integer").isEmpty());
  }

  public void testFindClassMethodsParamTypeResolvedViaSuperclass() {
    // Method expects an "a.b.Base"; caller passes "a.b.Child" which extends Base in the source
    // universe. The supertype probe must consult our classLookup.
    registerClass("a.b.Child", Modifier.PUBLIC, "a.b.Base");
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Base"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "a.b.Child").size());
  }

  public void testFindClassMethodsParamTypeResolvedViaInterface() {
    ClassSymbol.Builder child = registerClass("a.b.Child", Modifier.PUBLIC, "java.lang.Object");
    child.interfaces.add(Fqn.of("a.b.Iface"));
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Iface"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "a.b.Child").size());
  }

  public void testFindClassMethodsParamTypeResolvedViaTransitiveSuperclass() {
    // a.b.Grandchild -> a.b.Child -> a.b.Base. Method expects Base; caller passes Grandchild.
    registerClass("a.b.Base", Modifier.PUBLIC, "java.lang.Object");
    registerClass("a.b.Child", Modifier.PUBLIC, "a.b.Base");
    registerClass("a.b.Grandchild", Modifier.PUBLIC, "a.b.Child");
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Base"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "a.b.Grandchild").size());
  }

  public void testFindClassMethodsParamTypeResolvedViaTransitiveInterface() {
    // a.b.Concrete implements a.b.Mid; a.b.Mid extends a.b.Top. Method expects Top.
    ClassSymbol.Builder concrete = registerClass("a.b.Concrete", Modifier.PUBLIC, "java.lang.Object");
    concrete.interfaces.add(Fqn.of("a.b.Mid"));
    ClassSymbol.Builder mid = registerClass("a.b.Mid", Modifier.PUBLIC | Modifier.INTERFACE, null);
    mid.interfaces.add(Fqn.of("a.b.Top"));
    registerClass("a.b.Top", Modifier.PUBLIC | Modifier.INTERFACE, null);
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Top"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "a.b.Concrete").size());
  }

  public void testFindClassMethodsParamTypeResolvedViaSupertypeInterface() {
    // a.b.Child extends a.b.Parent; a.b.Parent implements a.b.Iface. Method expects Iface.
    registerClass("a.b.Child", Modifier.PUBLIC, "a.b.Parent");
    ClassSymbol.Builder parent = registerClass("a.b.Parent", Modifier.PUBLIC, "java.lang.Object");
    parent.interfaces.add(Fqn.of("a.b.Iface"));
    registerClass("a.b.Iface", Modifier.PUBLIC | Modifier.INTERFACE, null);
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Iface"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "a.b.Child").size());
  }

  public void testFindClassMethodsParamTypeRejectsUnrelatedType() {
    // a.b.Other has no relationship to a.b.Base. The walk must terminate and reject the match.
    registerClass("a.b.Other", Modifier.PUBLIC, "java.lang.Object");
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Base"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "a.b.Other").isEmpty());
  }

  public void testFindClassMethodsParamTypeResolvedAcrossProviders() {
    // a.b.Child is registered with the primary provider; its superclass ext.Base lives in the
    // fallback. The transitive walk must follow the chain across providers via the manager.
    registerClass("a.b.Child", Modifier.PUBLIC, "ext.Base");
    ClassSymbol.Builder external = new ClassSymbol.Builder();
    external.name = Fqn.of("ext.Base");
    external.superClass = Fqn.of("ext.Root");
    fallback.classes.put("ext.Base", external);
    ClassSymbol.Builder root = new ClassSymbol.Builder();
    root.name = Fqn.of("ext.Root");
    fallback.classes.put("ext.Root", root);
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "ext.Root"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", false, -1, "a.b.Child").size());
  }

  public void testFindClassMethodsParamTypeResolvedViaFallbackSupertype() {
    // The probe class is only known to the fallback. JavaSyntaxHelper must unwrap the fallback's
    // MyElement<ClassSymbol> so the supertype check still succeeds.
    ClassSymbol.Builder external = new ClassSymbol.Builder();
    external.name = Fqn.of("ext.Child");
    external.superClass = Fqn.of("a.b.Base");
    fallback.classes.put("ext.Child", external);

    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Base"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt",  false, -1, "ext.Child").size());
  }

  // ---------------------------------------------------------------------------------------------
  // getSuperClassName
  // ---------------------------------------------------------------------------------------------

  public void testGetSuperClassNameFromSource() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "a.b.Base");
    assertEquals("a.b.Base", helper().getSuperClassName("a.b.Foo"));
  }

  public void testGetSuperClassNameNullWhenSourceHasNoSuper() {
    registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertNull(helper().getSuperClassName("a.b.Foo"));
    // fallback NOT consulted: the source class was found, even if its superClass is null.
    assertTrue(fallback.superClassCalls.isEmpty());
  }

  public void testGetSuperClassNameDelegatesToFallback() {
    ClassSymbol.Builder external = new ClassSymbol.Builder();
    external.name = Fqn.of("ext.External");
    external.superClass = Fqn.of("ext.Parent");
    fallback.classes.put("ext.External", external);
    assertEquals("ext.Parent", helper().getSuperClassName("ext.External"));
    // Fallback supplies the ClassSymbol via findClass; JvmSyntaxHelper reads superClass off the record.
    assertEquals(List.of("ext.External"), fallback.findClassCalls);
  }

  public void testGetSuperClassNameNullWhenMissAndNoFallback() {
    assertNull(helperNoFallback().getSuperClassName("a.b.Missing"));
  }

  // ---------------------------------------------------------------------------------------------
  // isPublic — delegates to ClassSymbolUtil
  // ---------------------------------------------------------------------------------------------

  public void testIsPublicTrueForPublicClass() {
    ClassSymbol info = registerClass("a.b.Foo", Modifier.PUBLIC, null).build();
    helper();
    assertTrue(JavaHelper.isPublic(new MyElement<>(info)));
  }

  public void testIsPublicFalseForPackagePrivateClass() {
    ClassSymbol info = registerClass("a.b.Foo", 0, null).build();
    helper();
    assertFalse(JavaHelper.isPublic(new MyElement<>(info)));
  }

  public void testIsPublicTrueForPublicMethod() {
    MethodSymbol m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void").build();
    helper();
    assertTrue(JavaHelper.isPublic(new MyElement<>(m)));
  }

  public void testIsPublicFalseForPrivateMethod() {
    MethodSymbol m = method("ping", Modifier.PRIVATE, MethodType.INSTANCE, "void").build();
    helper();
    assertFalse(JavaHelper.isPublic(new MyElement<>(m)));
  }

  public void testIsPublicFalseForNullElement() {
    helper();
    assertFalse(JavaHelper.isPublic(null));
  }

  // ---------------------------------------------------------------------------------------------
  // element-based accessors (delegate to ClassSymbolUtil)
  // ---------------------------------------------------------------------------------------------

  public void testGetMethodTypesReturnsAnnotatedTypes() {
    MethodSymbol.Builder mb = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "java.lang.String", "int");
    mb.parameters.get(0).name = "n";
    MethodSymbol m = mb.build();
    helper();
    assertEquals(List.of("java.lang.String", "int", "n"),
                 JavaHelper.getMethodTypes(new MyElement<>(m)));
  }

  public void testGetMethodTypesEmptyForClassInfo() {
    ClassSymbol info = registerClass("a.b.Foo", Modifier.PUBLIC, null).build();
    helper();
    assertEquals(Collections.emptyList(), JavaHelper.getMethodTypes(new MyElement<>(info)));
  }

  public void testGetMethodTypesEmptyForNull() {
    helper();
    assertEquals(Collections.emptyList(), JavaHelper.getMethodTypes(null));
  }

  public void testGetGenericParameters() {
    MethodSymbol.Builder mb = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    mb.generics.add(new TypeParameterSymbol.Builder("T"));
    MethodSymbol m = mb.build();
    helper();
    List<TypeParameterSymbol> generics = JavaHelper.getGenericParameters(new MyElement<>(m));
    assertEquals(1, generics.size());
    assertEquals("T", generics.get(0).name());
  }

  public void testGetGenericParametersEmptyForNonMethod() {
    ClassSymbol info = registerClass("a.b.Foo", Modifier.PUBLIC, null).build();
    helper();
    assertTrue(JavaHelper.getGenericParameters(new MyElement<>(info)).isEmpty());
  }

  public void testGetExceptionList() {
    MethodSymbol.Builder mb = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    mb.exceptions.add(Fqn.of("java.io.IOException"));
    MethodSymbol m = mb.build();
    helper();
    assertEquals(List.of("java.io.IOException"),
                 JavaHelper.getExceptionList(new MyElement<>(m)));
  }

  public void testGetExceptionListEmptyForNonMethod() {
    ClassSymbol info = registerClass("a.b.Foo", Modifier.PUBLIC, null).build();
    helper();
    assertTrue(JavaHelper.getExceptionList(new MyElement<>(info)).isEmpty());
  }

  public void testGetAnnotationsForClass() {
    ClassSymbol.Builder b = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    b.annotations.add(Fqn.of("java.lang.Deprecated"));
    helper();
    assertEquals(List.of("java.lang.Deprecated"),
                 JavaHelper.getAnnotations(new MyElement<>(b.build())));
  }

  public void testGetAnnotationsForMethod() {
    MethodSymbol.Builder mb = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    mb.annotations.add(Fqn.of("java.lang.Override"));
    helper();
    assertEquals(List.of("java.lang.Override"),
                 JavaHelper.getAnnotations(new MyElement<>(mb.build())));
  }

  public void testGetAnnotationsEmptyForNull() {
    helper();
    assertTrue(JavaHelper.getAnnotations(null).isEmpty());
  }

  public void testGetParameterAnnotations() {
    MethodSymbol.Builder mb = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void", "java.lang.String");
    mb.parameters.get(0).annotations.add(Fqn.of("a.b.Marker"));
    helper();
    assertEquals(List.of("a.b.Marker"),
                 JavaHelper.getParameterAnnotations(new MyElement<>(mb.build()), 0));
  }

  public void testGetParameterAnnotationsOutOfRange() {
    MethodSymbol m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void", "java.lang.String").build();
    helper();
    assertTrue(JavaHelper.getParameterAnnotations(new MyElement<>(m), 99).isEmpty());
    helper();
    assertTrue(JavaHelper.getParameterAnnotations(new MyElement<>(m), -1).isEmpty());
  }

  public void testGetParameterAnnotationsEmptyForNonMethod() {
    ClassSymbol info = registerClass("a.b.Foo", Modifier.PUBLIC, null).build();
    helper();
    assertTrue(JavaHelper.getParameterAnnotations(new MyElement<>(info), 0).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private @NotNull JavaHelper helper() {
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(
      mapProvider(classes),
      fallbackProvider(fallback))));
  }

  private @NotNull JavaHelper helperNoFallback() {
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(mapProvider(classes))));
  }

  private static @NotNull JvmClassSymbolProvider mapProvider(@NotNull Map<String, ClassSymbol.Builder> classes) {
    return (fqn, resolver) -> {
      ClassSymbol.Builder info = classes.get(fqn.value());
      return info == null ? Map.of() : Map.of(fqn, info.build());
    };
  }

  private static @NotNull JvmClassSymbolProvider fallbackProvider(@NotNull JavaHelper fallback) {
    return (fqn, resolver) -> {
      NavigatablePsiElement el = fallback.findClass(fqn.value());
      if (el instanceof MyElement<?> e && e.delegate instanceof ClassSymbol ci) {
        return Map.of(fqn, ci);
      }
      return Map.of();
    };
  }

  private static final Fqn TEST_DECLARING = Fqn.of("test.Unknown");

  private ClassSymbol.Builder registerClass(@NotNull String fqn,
                                            int modifiers,
                                            @Nullable String superClass,
                                            MethodSymbol.Builder... methods) {
    ClassSymbol.Builder info = new ClassSymbol.Builder();
    info.name = Fqn.of(fqn);
    info.modifiers = modifiers;
    info.superClass = superClass == null ? null : Fqn.of(superClass);
    for (MethodSymbol.Builder m : methods) {
      m.declaringClass = info.name;
      info.methods.add(m);
    }
    classes.put(fqn, info);
    return info;
  }

  /**
   * Builds a {@link MethodSymbol.Builder} matching the shape that {@link JavaSyntaxClassExtractor}
   * would produce: return type plus a list of {@link ParameterSymbol} entries (auto-named
   * {@code p0..pN}), with the annotated-type fields mirroring the plain ones. The declaringClass
   * is a placeholder that {@link #registerClass} overrides when the method is attached to a class.
   */
  private static MethodSymbol.Builder method(@NotNull String name,
                                             int modifiers,
                                             @NotNull MethodType type,
                                             @NotNull String returnType,
                                             @NotNull String... paramTypes) {
    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.name = name;
    m.declaringClass = TEST_DECLARING;
    m.modifiers = modifiers;
    m.methodType = type;
    m.returnType = JvmTypeRefs.raw(returnType);
    for (int i = 0; i < paramTypes.length; i++) {
      ParameterSymbol.Builder p = new ParameterSymbol.Builder();
      p.type = JvmTypeRefs.raw(paramTypes[i]);
      p.name = "p" + i;
      m.parameters.add(p);
    }
    return m;
  }

  /**
   * Test double for {@link JavaHelper} that records every dispatched call and serves canned
   * answers from in-memory maps. Returns {@link MyElement}-wrapped delegates so the
   * supertype-probe code path in {@code JavaSyntaxHelper.lookupClassInfo} can unwrap them.
   */
  private static class RecordingFallback extends JavaHelper {
    final Map<String, ClassSymbol.Builder> classes = new HashMap<>();
    final Map<String, List<MethodSymbol.Builder>> methods = new HashMap<>();
    final List<String> findClassCalls = new ArrayList<>();
    final List<String> superClassCalls = new ArrayList<>();
    @Nullable String lastDispatch;

    @Override
    public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
      lastDispatch = "findClass";
      findClassCalls.add(className);
      ClassSymbol.Builder info = classes.get(className);
      return info == null ? null : new MyElement<>(info.build());
    }

    @Override
    public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                                 @NotNull MethodType methodType,
                                                                 @Nullable String methodName,
                                                                 boolean allowAbstract,
                                                                 int paramCount,
                                                                 String... paramTypes) {
      lastDispatch = "findClassMethods";
      List<MethodSymbol.Builder> all = methods.get(className);
      if (all == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = new ArrayList<>();
      for (MethodSymbol.Builder m : all) result.add(new MyElement<>(m.build()));
      return result;
    }

    @Override
    public @Nullable String getSuperClassName(@Nullable String className) {
      lastDispatch = "getSuperClassName";
      superClassCalls.add(className);
      ClassSymbol.Builder info = classes.get(className);
      return info == null || info.superClass == null ? null : info.superClass.value();
    }
  }
}
