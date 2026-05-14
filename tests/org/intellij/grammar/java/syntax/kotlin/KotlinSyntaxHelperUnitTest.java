/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.java.ClassInfo;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.MethodInfo;
import org.intellij.grammar.java.MyElement;

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

  private final Map<String, ClassInfo> classes = new HashMap<>();
  private RecordingFallback fallback;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    classes.clear();
    fallback = new RecordingFallback();
  }

  public void testFindClassWrapsClassInfo() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object");
    NavigatablePsiElement el = helper().findClass("a.b.Foo");
    assertNotNull(el);
    assertSame(info, ((MyElement<?>)el).delegate);
  }

  public void testFindClassDelegatesToFallback() {
    ClassInfo fallbackInfo = new ClassInfo();
    fallbackInfo.name = "ext.Platform";
    fallback.classes.put("ext.Platform", fallbackInfo);
    NavigatablePsiElement el = helper().findClass("ext.Platform");
    assertNotNull(el);
    assertSame(fallbackInfo, ((MyElement<?>)el).delegate);
  }

  public void testFindClassReturnsNullWithoutFallback() {
    assertNull(helperNoFallback().findClass("a.b.Missing"));
  }

  public void testFindClassMethodsFiltersByName() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, JavaHelper.MethodType.INSTANCE, "void"),
                  method("pong", Modifier.PUBLIC, JavaHelper.MethodType.INSTANCE, "void"));
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", JavaHelper.MethodType.INSTANCE, "ping", -1).size());
  }

  public void testFindClassMethodsFiltersByMethodType() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC, JavaHelper.MethodType.STATIC, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", JavaHelper.MethodType.INSTANCE, "ping", -1).isEmpty());
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", JavaHelper.MethodType.STATIC, "ping", -1).size());
  }

  public void testFindClassMethodsAbstractFilteredByDefault() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "java.lang.Object",
                  method("ping", Modifier.PUBLIC | Modifier.ABSTRACT, JavaHelper.MethodType.INSTANCE, "void"));
    assertTrue(helper().findClassMethods(
      "a.b.Foo", JavaHelper.MethodType.INSTANCE, "ping", -1).isEmpty());
    assertEquals(1, helper().findClassMethods(
      "a.b.Foo", JavaHelper.MethodType.INSTANCE, "ping", true, -1).size());
  }

  public void testFindClassMethodsDelegatesOnMiss() {
    MethodInfo m = method("ping", Modifier.PUBLIC, JavaHelper.MethodType.INSTANCE, "void");
    fallback.methods.put("ext.Platform", List.of(m));
    List<NavigatablePsiElement> result = helper().findClassMethods(
      "ext.Platform", JavaHelper.MethodType.INSTANCE, "ping", -1);
    assertEquals(1, result.size());
  }

  public void testGetSuperClassNameFromSource() {
    registerClass("a.b.Foo", Modifier.PUBLIC, "a.b.Base");
    assertEquals("a.b.Base", helper().getSuperClassName("a.b.Foo"));
  }

  public void testGetSuperClassNameDelegates() {
    ClassInfo external = new ClassInfo();
    external.name = "ext.External";
    external.superClass = "ext.Parent";
    fallback.classes.put("ext.External", external);
    assertEquals("ext.Parent", helper().getSuperClassName("ext.External"));
  }

  public void testIsPublicTrue() {
    ClassInfo info = registerClass("a.b.Foo", Modifier.PUBLIC, null);
    assertTrue(helper().isPublic(new MyElement<>(info)));
  }

  public void testIsPublicFalseForNull() {
    assertFalse(helper().isPublic(null));
  }

  private @org.jetbrains.annotations.NotNull KotlinSyntaxHelper helper() {
    return new KotlinSyntaxHelper(classes::get, fallback);
  }

  private @org.jetbrains.annotations.NotNull KotlinSyntaxHelper helperNoFallback() {
    return new KotlinSyntaxHelper(classes::get, null);
  }

  private ClassInfo registerClass(@org.jetbrains.annotations.NotNull String fqn,
                                  int modifiers,
                                  @org.jetbrains.annotations.Nullable String superClass,
                                  MethodInfo... methods) {
    ClassInfo info = new ClassInfo();
    info.name = fqn;
    info.modifiers = modifiers;
    info.superClass = superClass;
    for (MethodInfo m : methods) info.methods.add(m);
    classes.put(fqn, info);
    return info;
  }

  private MethodInfo method(@org.jetbrains.annotations.NotNull String name,
                            int modifiers,
                            @org.jetbrains.annotations.NotNull JavaHelper.MethodType methodType,
                            @org.jetbrains.annotations.NotNull String returnType,
                            String... paramTypes) {
    MethodInfo m = new MethodInfo();
    m.name = name;
    m.modifiers = modifiers;
    m.methodType = methodType;
    m.types.add(returnType);
    for (int i = 0; i < paramTypes.length; i++) {
      m.types.add(paramTypes[i]);
      m.types.add("p" + i);
    }
    return m;
  }

  /** Minimal recording fallback so we can assert delegation. */
  private static final class RecordingFallback extends JavaHelper {
    final Map<String, ClassInfo> classes = new HashMap<>();
    final Map<String, List<MethodInfo>> methods = new HashMap<>();

    @Override
    public boolean isPublic(NavigatablePsiElement element) { return false; }

    @Override
    public NavigatablePsiElement findClass(String className) {
      ClassInfo info = classes.get(className);
      return info == null ? null : new MyElement<>(info);
    }

    @Override
    public List<NavigatablePsiElement> findClassMethods(String className,
                                                        MethodType methodType,
                                                        String methodName,
                                                        boolean allowAbstract,
                                                        int paramCount,
                                                        String... paramTypes) {
      List<MethodInfo> ms = methods.get(className);
      if (ms == null) return Collections.emptyList();
      List<NavigatablePsiElement> result = new ArrayList<>();
      for (MethodInfo m : ms) result.add(new MyElement<>(m));
      return result;
    }

    @Override
    public String getSuperClassName(String className) {
      ClassInfo info = classes.get(className);
      return info == null ? null : info.superClass;
    }
  }
}
