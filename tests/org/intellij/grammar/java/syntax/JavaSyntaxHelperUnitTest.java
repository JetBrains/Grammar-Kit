/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.classinfo.MethodInfo;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.MyElement;
import org.intellij.grammar.classinfo.TypeParameterInfo;
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
 * FQN → {@link ClassInfo} lookup via the package-private test constructor, so each test owns a
 * small, controlled type universe. The matching integration test {@link JavaSyntaxHelperTest}
 * exercises the full file → ClassInfo flow against real {@code .java} fixtures.
 */
public class JavaSyntaxHelperUnitTest extends TestCase {

  private final Map<String, ClassInfo> classes = new HashMap<>();
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
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object");
    NavigatablePsiElement el = helper().findClass("a.b.Foo");
    assertNotNull(el);
    assertSame(info, ((MyElement<?>)el).delegate);
  }

  public void testFindClassMissAndNoFallbackReturnsNull() {
    assertNull(helperNoFallback().findClass("a.b.Missing"));
  }

  public void testFindClassMissDelegatesToFallback() {
    ClassInfo fallbackInfo = new ClassInfo();
    fallbackInfo.name = Fqn.of("a.b.PlatformClass");
    fallback.classes.put("a.b.PlatformClass", fallbackInfo);

    NavigatablePsiElement el = helper().findClass("a.b.PlatformClass");
    assertNotNull(el);
    assertSame(fallbackInfo, ((MyElement<?>)el).delegate);
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
    ClassInfo external = new ClassInfo();
    external.name = Fqn.of("a.b.PlatformClass");
    external.methods.add(method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    fallback.classes.put("a.b.PlatformClass", external);

    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.PlatformClass", MethodType.INSTANCE, "ping", -1);
    assertEquals(1, result.size());
    assertEquals(List.of("a.b.PlatformClass"), fallback.findClassCalls);
  }

  public void testFindClassMethodsMissNoFallbackReturnsEmpty() {
    JavaHelper noFallback = helperNoFallback();
    assertTrue(noFallback.findClassMethods("a.b.Missing", MethodType.INSTANCE, "x", -1).isEmpty());
  }

  public void testFindClassMethodsNullMethodNameReturnsEmpty() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, null, -1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - name matching
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsExactNameMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"),
                  method("pong", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", -1);
    assertEquals(1, result.size());
    assertEquals("ping", ((MethodInfo)((MyElement<?>)result.get(0)).delegate).name);
  }

  public void testFindClassMethodsWildcardMatchesEverything() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"),
                  method("pong", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "*", -1);
    assertEquals(2, result.size());
  }

  public void testFindClassMethodsInnerClassPrefixMatch() {
    // acceptsName: method.name "Outer$Inner" matches expected "Outer".
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("Outer$Inner", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "Outer", -1);
    assertEquals(1, result.size());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - methodType filtering
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsMethodTypeMismatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.STATIC, "ping", -1).isEmpty());
  }

  public void testFindClassMethodsConstructorMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("Foo", Modifier.PUBLIC, MethodType.CONSTRUCTOR, "a.b.Foo"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.CONSTRUCTOR, "Foo", -1);
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
      "a.b.Foo", MethodType.INSTANCE, "ping", -1).isEmpty());
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
      "a.b.Foo", MethodType.CONSTRUCTOR, "Foo", -1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // findClassMethods - parameter matching
  // ---------------------------------------------------------------------------------------------

  public void testFindClassMethodsParamCountSkippedWhenMinusOne() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String"));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", -1);
    assertEquals(1, result.size());
  }

  public void testFindClassMethodsParamCountMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String", "int"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", 2).size());
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", 1).isEmpty());
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", 3).isEmpty());
  }

  public void testFindClassMethodsParamTypesEmptyAcceptsAny() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", -1).size());
  }

  public void testFindClassMethodsParamTypesExactMatch() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "java.lang.String"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", -1, "java.lang.String").size());
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", -1, "java.lang.Integer").isEmpty());
  }

  public void testFindClassMethodsParamTypeResolvedViaSuperclass() {
    // Method expects an "a.b.Base"; caller passes "a.b.Child" which extends Base in the source
    // universe. The supertype probe must consult our classLookup.
    registerClass("a.b.Child", Modifier.PUBLIC, "a.b.Base");
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Base"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", -1, "a.b.Child").size());
  }

  public void testFindClassMethodsParamTypeResolvedViaInterface() {
    ClassInfo child = registerClass("a.b.Child", Modifier.PUBLIC, "java.lang.Object");
    child.interfaces.add(Fqn.of("a.b.Iface"));
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Iface"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", -1, "a.b.Child").size());
  }

  public void testFindClassMethodsParamTypeResolvedViaFallbackSupertype() {
    // The probe class is only known to the fallback. JavaSyntaxHelper must unwrap the fallback's
    // MyElement<ClassInfo> so the supertype check still succeeds.
    ClassInfo external = new ClassInfo();
    external.name = Fqn.of("ext.Child");
    external.superClass = Fqn.of("a.b.Base");
    fallback.classes.put("ext.Child", external);

    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("doIt", Modifier.PUBLIC, MethodType.INSTANCE,
                         "void", "a.b.Base"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "doIt", -1, "ext.Child").size());
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
    ClassInfo external = new ClassInfo();
    external.name = Fqn.of("ext.External");
    external.superClass = Fqn.of("ext.Parent");
    fallback.classes.put("ext.External", external);
    assertEquals("ext.Parent", helper().getSuperClassName("ext.External"));
    // Fallback supplies the ClassInfo via findClass; JvmSyntaxHelper reads superClass off the record.
    assertEquals(List.of("ext.External"), fallback.findClassCalls);
  }

  public void testGetSuperClassNameNullWhenMissAndNoFallback() {
    assertNull(helperNoFallback().getSuperClassName("a.b.Missing"));
  }

  // ---------------------------------------------------------------------------------------------
  // isPublic — delegates to ClassInfoUtil
  // ---------------------------------------------------------------------------------------------

  public void testIsPublicTrueForPublicClass() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertTrue(helper().isPublic(new MyElement<>(info)));
  }

  public void testIsPublicFalseForPackagePrivateClass() {
    ClassInfo info = registerClass("a.b.Foo", 0, null);
    assertFalse(helper().isPublic(new MyElement<>(info)));
  }

  public void testIsPublicTrueForPublicMethod() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    assertTrue(helper().isPublic(new MyElement<>(m)));
  }

  public void testIsPublicFalseForPrivateMethod() {
    MethodInfo m = method("ping", Modifier.PRIVATE, MethodType.INSTANCE, "void");
    assertFalse(helper().isPublic(new MyElement<>(m)));
  }

  public void testIsPublicFalseForNullElement() {
    assertFalse(helper().isPublic(null));
  }

  // ---------------------------------------------------------------------------------------------
  // element-based accessors (delegate to ClassInfoUtil)
  // ---------------------------------------------------------------------------------------------

  public void testGetMethodTypesReturnsAnnotatedTypes() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    m.annotatedTypes.addAll(List.of("java.lang.String", "int", "n"));
    assertEquals(List.of("java.lang.String", "int", "n"),
                 helper().getMethodTypes(new MyElement<>(m)));
  }

  public void testGetMethodTypesEmptyForClassInfo() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertEquals(Collections.emptyList(), helper().getMethodTypes(new MyElement<>(info)));
  }

  public void testGetMethodTypesEmptyForNull() {
    assertEquals(Collections.emptyList(), helper().getMethodTypes(null));
  }

  public void testGetGenericParameters() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    m.generics.add(new TypeParameterInfo("T"));
    List<TypeParameterInfo> generics = helper().getGenericParameters(new MyElement<>(m));
    assertEquals(1, generics.size());
    assertEquals("T", generics.get(0).getName());
  }

  public void testGetGenericParametersEmptyForNonMethod() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertTrue(helper().getGenericParameters(new MyElement<>(info)).isEmpty());
  }

  public void testGetExceptionList() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    m.exceptions.add(Fqn.of("java.io.IOException"));
    assertEquals(List.of("java.io.IOException"),
                 helper().getExceptionList(new MyElement<>(m)));
  }

  public void testGetExceptionListEmptyForNonMethod() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertTrue(helper().getExceptionList(new MyElement<>(info)).isEmpty());
  }

  public void testGetDeclaringClass() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    m.declaringClass = Fqn.of("a.b.Foo");
    assertEquals("a.b.Foo", helper().getDeclaringClass(new MyElement<>(m)));
  }

  public void testGetDeclaringClassEmptyForNonMethod() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertEquals("", helper().getDeclaringClass(new MyElement<>(info)));
  }

  public void testGetDeclaringClassEmptyForNull() {
    assertEquals("", helper().getDeclaringClass(null));
  }

  public void testGetAnnotationsForClass() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    info.annotations.add(Fqn.of("java.lang.Deprecated"));
    assertEquals(List.of("java.lang.Deprecated"),
                 helper().getAnnotations(new MyElement<>(info)));
  }

  public void testGetAnnotationsForMethod() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void");
    m.annotations.get(0).add(Fqn.of("java.lang.Override"));
    assertEquals(List.of("java.lang.Override"),
                 helper().getAnnotations(new MyElement<>(m)));
  }

  public void testGetAnnotationsEmptyForNull() {
    assertTrue(helper().getAnnotations(null).isEmpty());
  }

  public void testGetParameterAnnotations() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void", "java.lang.String");
    // index 0 is the method itself; param 0 is at key 1.
    m.annotations.get(1).add(Fqn.of("a.b.Marker"));
    assertEquals(List.of("a.b.Marker"),
                 helper().getParameterAnnotations(new MyElement<>(m), 0));
  }

  public void testGetParameterAnnotationsOutOfRange() {
    MethodInfo m = method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void", "java.lang.String");
    assertTrue(helper().getParameterAnnotations(new MyElement<>(m), 99).isEmpty());
    assertTrue(helper().getParameterAnnotations(new MyElement<>(m), -1).isEmpty());
  }

  public void testGetParameterAnnotationsEmptyForNonMethod() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertTrue(helper().getParameterAnnotations(new MyElement<>(info), 0).isEmpty());
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

  private static @NotNull JvmClassSymbolProvider mapProvider(@NotNull Map<String, ClassInfo> classes) {
    return (fqn, resolver) -> {
      ClassInfo info = classes.get(fqn.value());
      return info == null ? Map.of() : Map.of(fqn, info);
    };
  }

  private static @NotNull JvmClassSymbolProvider fallbackProvider(@NotNull JavaHelper fallback) {
    return (fqn, resolver) -> {
      NavigatablePsiElement el = fallback.findClass(fqn.value());
      if (el instanceof MyElement<?> e && e.delegate instanceof ClassInfo ci) {
        return Map.of(fqn, ci);
      }
      return Map.of();
    };
  }

  private ClassInfo registerClass(@NotNull String fqn,
                                  int modifiers,
                                  @Nullable String superClass,
                                  MethodInfo... methods) {
    ClassInfo info = new ClassInfo();
    info.name = Fqn.of(fqn);
    info.modifiers = modifiers;
    info.superClass = superClass == null ? null : Fqn.of(superClass);
    for (MethodInfo m : methods) info.methods.add(m);
    classes.put(fqn, info);
    return info;
  }

  /**
   * Builds a {@link MethodInfo} matching the shape that {@link JavaSyntaxClassExtractor} would
   * produce. {@link MethodInfo#types} is a flat list: index 0 is the return type, then
   * alternating {@code (paramType, paramName)} pairs.
   */
  private static MethodInfo method(@NotNull String name,
                                   int modifiers,
                                   @NotNull MethodType type,
                                   @NotNull String returnType,
                                   @NotNull String... paramTypes) {
    MethodInfo m = new MethodInfo();
    m.name = name;
    m.modifiers = modifiers;
    m.methodType = type;
    m.types.add(returnType);
    for (int i = 0; i < paramTypes.length; i++) {
      m.types.add(paramTypes[i]);
      m.types.add("p" + i);
    }
    return m;
  }

  /**
   * Test double for {@link JavaHelper} that records every dispatched call and serves canned
   * answers from in-memory maps. Returns {@link MyElement}-wrapped delegates so the
   * supertype-probe code path in {@code JavaSyntaxHelper.lookupClassInfo} can unwrap them.
   */
  private static class RecordingFallback extends JavaHelper {
    final Map<String, ClassInfo> classes = new HashMap<>();
    final Map<String, List<MethodInfo>> methods = new HashMap<>();
    final List<String> findClassCalls = new ArrayList<>();
    final List<String> superClassCalls = new ArrayList<>();
    @Nullable String lastDispatch;

    @Override
    public boolean isPublic(@Nullable NavigatablePsiElement element) {
      return false;
    }

    @Override
    public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
      lastDispatch = "findClass";
      findClassCalls.add(className);
      ClassInfo info = classes.get(className);
      return info == null ? null : new MyElement<>(info);
    }

    @Override
    public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                                 @NotNull MethodType methodType,
                                                                 @Nullable String methodName,
                                                                 boolean allowAbstract,
                                                                 int paramCount,
                                                                 String... paramTypes) {
      lastDispatch = "findClassMethods";
      List<MethodInfo> all = methods.get(className);
      if (all == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = new ArrayList<>();
      for (MethodInfo m : all) result.add(new MyElement<>(m));
      return result;
    }

    @Override
    public @Nullable String getSuperClassName(@Nullable String className) {
      lastDispatch = "getSuperClassName";
      superClassCalls.add(className);
      ClassInfo info = classes.get(className);
      return info == null || info.superClass == null ? null : info.superClass.value();
    }
  }
}
