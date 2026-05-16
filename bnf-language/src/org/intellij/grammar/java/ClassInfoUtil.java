/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.intellij.grammar.generator.java.JavaNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Read-only helpers shared by every {@link JavaHelper} that returns {@link MyElement}-wrapped
 * {@link ClassSymbol} / {@link MethodSymbol} records (currently {@link AsmHelper} and the
 * {@code JavaSyntaxHelper} in the {@code syntax} subpackage). Each method unwraps the
 * {@link MyElement} delegate and surfaces the field the {@link JavaHelper} contract expects, so
 * the helpers themselves can keep their override methods to one line each.
 */
public final class ClassInfoUtil {

  private ClassInfoUtil() { }

  public static boolean isPublic(@Nullable NavigatablePsiElement element) {
    Object delegate = delegateOf(element);
    int access = delegate instanceof ClassSymbol ? ((ClassSymbol)delegate).modifiers :
                 delegate instanceof MethodSymbol ? ((MethodSymbol)delegate).modifiers :
                 0;
    return Modifier.isPublic(access);
  }

  public static @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    if (!(delegate instanceof MethodSymbol m)) return Collections.emptyList();
    List<String> out = new ArrayList<>(1 + 2 * m.parameters.size());
    out.add(m.annotatedReturnType != null ? m.annotatedReturnType : m.returnType);
    for (ParameterSymbol p : m.parameters) {
      out.add(p.annotatedType != null ? p.annotatedType : p.type);
      out.add(p.name);
    }
    return out;
  }

  public static @NotNull List<TypeParameterSymbol> getGenericParameters(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    return delegate instanceof MethodSymbol ? ((MethodSymbol)delegate).generics
                                          : Collections.emptyList();
  }

  public static @NotNull List<String> getExceptionList(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    return delegate instanceof MethodSymbol ? ContainerUtil.map(((MethodSymbol)delegate).exceptions, Fqn::value)
                                          : Collections.emptyList();
  }

  public static @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    if (!(delegate instanceof MethodSymbol)) return "";
    Fqn declaring = ((MethodSymbol)delegate).declaringClass;
    return declaring == null ? "" : declaring.value();
  }

  public static @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    Object delegate = delegateOf(element);
    if (delegate instanceof ClassSymbol) return ContainerUtil.map(((ClassSymbol)delegate).annotations, Fqn::value);
    if (delegate instanceof MethodSymbol) return ContainerUtil.map(((MethodSymbol)delegate).annotations, Fqn::value);
    return Collections.emptyList();
  }

  public static @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    Object delegate = delegateOf(method);
    if (!(delegate instanceof MethodSymbol)) return Collections.emptyList();
    List<ParameterSymbol> parameters = ((MethodSymbol)delegate).parameters;
    if (paramIndex < 0 || paramIndex >= parameters.size()) return Collections.emptyList();
    return ContainerUtil.map(parameters.get(paramIndex).annotations, Fqn::value);
  }

  /**
   * Filters by parameter arity and parameter type compatibility.
   * <p>
   * Subtype probes ({@link ClassSymbol#superClass} / {@link ClassSymbol#interfaces}) go through
   * {@code classLookup} so each helper drives the lookup with its own backing model — bytecode for
   * {@link AsmHelper}, source files for {@code JavaSyntaxHelper}, etc. {@code paramCount = -1}
   * skips the arity check; an empty {@code paramTypes} accepts any parameter list.
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean acceptsParams(@NotNull MethodSymbol method,
                                      int paramCount,
                                      @NotNull String[] paramTypes,
                                      @NotNull Function<String, ClassSymbol> classLookup) {
    int actualParams = method.parameters.size();
    if (paramCount >= 0 && paramCount != actualParams) return false;
    if (paramTypes.length == 0) return true;
    if (paramTypes.length > actualParams) return false;
    for (int i = 0; i < paramTypes.length; i++) {
      String paramType = paramTypes[i];
      String parameter = method.parameters.get(i).type;
      if (JavaHelper.acceptsName(paramType, JavaNames.getRawClassName(parameter))) continue;
      ClassSymbol info = classLookup.apply(paramType);
      if (info != null) {
        if (info.superClass != null && Objects.equals(info.superClass.value(), parameter)) continue;
        if (containsValue(info.interfaces, parameter)) continue;
      }
      return false;
    }
    return true;
  }

  private static boolean containsValue(@NotNull List<Fqn> fqns, @NotNull String value) {
    for (Fqn fqn : fqns) {
      if (Objects.equals(fqn.value(), value)) return true;
    }
    return false;
  }

  private static @Nullable Object delegateOf(@Nullable NavigatablePsiElement element) {
    return element instanceof MyElement ? ((MyElement<?>)element).delegate : null;
  }
}
