/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import org.intellij.grammar.generator.java.JavaNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Read-only helpers shared by every {@link JavaHelper} that returns {@link MyElement}-wrapped
 * {@link ClassInfo} / {@link MethodInfo} records (currently {@link AsmHelper} and the
 * {@code JavaSyntaxHelper} in the {@code syntax} subpackage). Each method unwraps the
 * {@link MyElement} delegate and surfaces the field the {@link JavaHelper} contract expects, so
 * the helpers themselves can keep their override methods to one line each.
 */
public final class ClassInfoUtil {

  private ClassInfoUtil() { }

  public static boolean isPublic(@Nullable NavigatablePsiElement element) {
    Object delegate = delegateOf(element);
    int access = delegate instanceof ClassInfo ? ((ClassInfo)delegate).modifiers :
                 delegate instanceof MethodInfo ? ((MethodInfo)delegate).modifiers :
                 0;
    return Modifier.isPublic(access);
  }

  public static @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    return delegate instanceof MethodInfo ? ((MethodInfo)delegate).annotatedTypes
                                          : Collections.emptyList();
  }

  public static @NotNull List<TypeParameterInfo> getGenericParameters(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    return delegate instanceof MethodInfo ? ((MethodInfo)delegate).generics
                                          : Collections.emptyList();
  }

  public static @NotNull List<String> getExceptionList(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    return delegate instanceof MethodInfo ? ((MethodInfo)delegate).exceptions
                                          : Collections.emptyList();
  }

  public static @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    return delegate instanceof MethodInfo ? ((MethodInfo)delegate).declaringClass : "";
  }

  public static @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    Object delegate = delegateOf(element);
    if (delegate instanceof ClassInfo) return ((ClassInfo)delegate).annotations;
    if (delegate instanceof MethodInfo) return ((MethodInfo)delegate).annotations.get(0);
    return Collections.emptyList();
  }

  public static @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    Object delegate = delegateOf(method);
    if (!(delegate instanceof MethodInfo)) return Collections.emptyList();
    Map<Integer, List<String>> annotations = ((MethodInfo)delegate).annotations;
    if (paramIndex < 0 || paramIndex >= annotations.size()) return Collections.emptyList();
    List<String> result = annotations.get(paramIndex + 1);
    return result == null ? Collections.emptyList() : result;
  }

  /**
   * Filters by parameter arity and parameter type compatibility.
   * <p>
   * Subtype probes ({@link ClassInfo#superClass} / {@link ClassInfo#interfaces}) go through
   * {@code classLookup} so each helper drives the lookup with its own backing model — bytecode for
   * {@link AsmHelper}, source files for {@code JavaSyntaxHelper}, etc. {@code paramCount = -1}
   * skips the arity check; an empty {@code paramTypes} accepts any parameter list.
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean acceptsParams(@NotNull MethodInfo method,
                                      int paramCount,
                                      @NotNull String[] paramTypes,
                                      @NotNull Function<String, ClassInfo> classLookup) {
    int actualParams = (method.types.size() - 1) / 2;
    if (paramCount >= 0 && paramCount != actualParams) return false;
    if (paramTypes.length == 0) return true;
    if (paramTypes.length > actualParams) return false;
    for (int i = 0; i < paramTypes.length; i++) {
      String paramType = paramTypes[i];
      String parameter = method.types.get(2 * i + 1);
      if (JavaHelper.acceptsName(paramType, JavaNames.getRawClassName(parameter))) continue;
      ClassInfo info = classLookup.apply(paramType);
      if (info != null) {
        if (Objects.equals(info.superClass, parameter)) continue;
        if (info.interfaces.contains(parameter)) continue;
      }
      return false;
    }
    return true;
  }

  private static @Nullable Object delegateOf(@Nullable NavigatablePsiElement element) {
    return element instanceof MyElement ? ((MyElement<?>)element).delegate : null;
  }
}
