/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.MyElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Surface check that {@link KotlinSyntaxHelper} inherits the shared {@code JvmSyntaxHelperBase}
 * boilerplate correctly: package-private test-seam constructor, lookup → fallback chaining, and
 * filter rules in {@code findClassMethods}. The Java-side {@code JavaSyntaxHelperUnitTest} covers
 * the full matrix of these behaviours; the Kotlin test set focuses on Kotlin-specific extraction
 * via the fixture-based {@link KotlinSyntaxHelperTest}.
 */
public class KotlinSyntaxHelperUnitTest extends TestCase {

  private final Map<String, ClassSymbol.Builder> classes = new HashMap<>();
  private RecordingFallback fallback;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    classes.clear();
    fallback = new RecordingFallback();
  }

  public void testFindClassWrapsClassInfo() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object");
    NavigatablePsiElement el = helper().findClass("a.b.Foo");
    assertNotNull(el);
    Object delegate = ((MyElement<?>)el).delegate;
    assertTrue(delegate instanceof ClassSymbol);
    assertEquals(Fqn.of("a.b.Foo"), ((ClassSymbol)delegate).name());
  }

  public void testFindClassDelegatesToFallback() {
    ClassSymbol.Builder fallbackInfo = new ClassSymbol.Builder();
    fallbackInfo.name = Fqn.of("ext.Platform");
    fallback.classes.put("ext.Platform", fallbackInfo);
    NavigatablePsiElement el = helper().findClass("ext.Platform");
    assertNotNull(el);
    Object delegate = ((MyElement<?>)el).delegate;
    assertTrue(delegate instanceof ClassSymbol);
    assertEquals(Fqn.of("ext.Platform"), ((ClassSymbol)delegate).name());
  }

  public void testFindClassReturnsNullWithoutFallback() {
    assertNull(helperNoFallback().findClass("a.b.Missing"));
  }

  public void testFindClassMethodsFiltersByName() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"),
                  method("pong", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", -1).size());
  }

  public void testFindClassMethodsFiltersByMethodType() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, MethodType.STATIC, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", -1).isEmpty());
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.STATIC, "ping", -1).size());
  }

  public void testFindClassMethodsAbstractFilteredByDefault() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC | Modifier.ABSTRACT, MethodType.INSTANCE, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", -1).isEmpty());
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", MethodType.INSTANCE, "ping", true, -1).size());
  }

  public void testFindClassMethodsDelegatesOnMiss() {
    ClassSymbol.Builder external = new ClassSymbol.Builder();
    external.name = Fqn.of("ext.Platform");
    external.methods.add(method("ping", Modifier.PUBLIC, MethodType.INSTANCE, "void"));
    fallback.classes.put("ext.Platform", external);
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "ext.Platform", MethodType.INSTANCE, "ping", -1);
    assertEquals(1, result.size());
  }

  public void testGetSuperClassNameFromSource() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "a.b.Base");
    assertEquals("a.b.Base", helper().getSuperClassName("a.b.Foo"));
  }

  public void testGetSuperClassNameDelegates() {
    ClassSymbol.Builder external = new ClassSymbol.Builder();
    external.name = Fqn.of("ext.External");
    external.superClass = Fqn.of("ext.Parent");
    fallback.classes.put("ext.External", external);
    assertEquals("ext.Parent", helper().getSuperClassName("ext.External"));
  }

  public void testIsPublicTrue() {
    ClassSymbol info = registerClass("a.b.Foo", Modifier.PUBLIC, null).build();
    assertTrue(helper().isPublic(new MyElement<>(info)));
  }

  public void testIsPublicFalseForNull() {
    assertFalse(helper().isPublic(null));
  }

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

  private MethodSymbol.Builder method(@NotNull String name,
                                      int modifiers,
                                      @NotNull MethodType methodType,
                                      @NotNull String returnType,
                                      String... paramTypes) {
    MethodSymbol.Builder m = new MethodSymbol.Builder();
    m.name = name;
    m.declaringClass = TEST_DECLARING;
    m.modifiers = modifiers;
    m.methodType = methodType;
    m.returnType = returnType;
    m.annotatedReturnType = returnType;
    for (int i = 0; i < paramTypes.length; i++) {
      ParameterSymbol.Builder p = new ParameterSymbol.Builder();
      p.type = paramTypes[i];
      p.annotatedType = paramTypes[i];
      p.name = "p" + i;
      m.parameters.add(p);
    }
    return m;
  }

  /** Minimal recording fallback so we can assert delegation. */
  private static final class RecordingFallback extends JavaHelper {
    final Map<String, ClassSymbol.Builder> classes = new HashMap<>();
    final Map<String, List<MethodSymbol.Builder>> methods = new HashMap<>();

    @Override
    public boolean isPublic(NavigatablePsiElement element) { return false; }

    @Override
    public NavigatablePsiElement findClass(String className) {
      ClassSymbol.Builder info = classes.get(className);
      return info == null ? null : new MyElement<>(info.build());
    }

    @Override
    public List<NavigatablePsiElement> findClassMethods(String className,
                                                        MethodType methodType,
                                                        String methodName,
                                                        boolean allowAbstract,
                                                        int paramCount,
                                                        String... paramTypes) {
      List<MethodSymbol.Builder> ms = methods.get(className);
      if (ms == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = new ArrayList<>();
      for (MethodSymbol.Builder m : ms) result.add(new MyElement<>(m.build()));
      return result;
    }

    @Override
    public String getSuperClassName(String className) {
      ClassSymbol.Builder info = classes.get(className);
      return info == null || info.superClass == null ? null : info.superClass.value();
    }
  }
}
