/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.ArrayUtil;
import org.intellij.grammar.classinfo.MethodType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link JavaHelper} backed by {@link Class#forName} and {@code java.lang.reflect}.
 * <p>
 * Used as a last-resort fallback when ASM is not on the classpath (see
 * {@link org.intellij.grammar.LightPsi.Init#initExtensions}). Because it uses {@link Class#forName},
 * it actually initialises the target classes, which is sometimes undesirable but acceptable in the
 * minimal {@code LightPsi} runtime where ASM is missing.
 * <p>
 * Only lookup methods are overridden here. Metadata reads inherit {@link JavaHelper}'s defaults,
 * which dispatch through {@link ClassSymbolUtil} on the wrapped {@code java.lang.reflect.*}
 * delegate. Limitation versus the ASM-backed path: parameter names are synthesised
 * ({@code "p0"}, {@code "p1"}…) since reflection does not preserve them.
 */
public class ReflectionHelper extends JavaHelper {
  private static @Nullable Class<?> findClassSafe(String className) {
    if (className == null) return null;
    try {
      return Class.forName(className);
    }
    catch (Exception e) {
      return null;
    }
  }

  private static boolean acceptsMethod(Member method, int paramCount, String... paramTypes) {
    Class<?>[] parameterTypes = method instanceof Method ? ((Method)method).getParameterTypes() :
                                method instanceof Constructor ? ((Constructor<?>)method).getParameterTypes() :
                                ArrayUtil.EMPTY_CLASS_ARRAY;
    if (paramCount >= 0 && paramCount != parameterTypes.length) return false;
    if (paramTypes.length == 0) return true;
    if (paramTypes.length > parameterTypes.length) return false;
    for (int i = 0; i < paramTypes.length; i++) {
      String paramType = paramTypes[i];
      Class<?> parameter = parameterTypes[i];
      if (ClassSymbolUtil.acceptsName(paramType, parameter.getCanonicalName())) continue;
      Class<?> paramClass = findClassSafe(paramType);
      if (paramClass != null && parameter.isAssignableFrom(paramClass)) continue;
      return false;
    }
    return true;
  }

  private static boolean acceptsMethod(Member method, MethodType methodType, boolean allowAbstract) {
    int modifiers = method.getModifiers();
    return (methodType == MethodType.STATIC) == Modifier.isStatic(modifiers) &&
           ClassSymbolUtil.acceptsModifiers(modifiers, methodType, allowAbstract);
  }

  @Override
  public @Nullable NavigatablePsiElement findClass(String className) {
    Class<?> aClass = findClassSafe(className);
    return aClass == null ? null : new MyElement<Class<?>>(aClass);
  }

  @Override
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    Class<?> aClass = findClassSafe(className);
    if (aClass == null || methodName == null) return Collections.emptyList();
    List<NavigatablePsiElement> result = new ArrayList<>();
    Member[] methods = methodType == MethodType.CONSTRUCTOR ? aClass.getDeclaredConstructors() : aClass.getDeclaredMethods();
    for (Member method : methods) {
      if (!ClassSymbolUtil.acceptsName(methodName, method.getName())) continue;
      if (!acceptsMethod(method, methodType, allowAbstract)) continue;
      if (!acceptsMethod(method, paramCount, paramTypes)) continue;
      result.add(new MyElement<>(method));
    }
    return result;
  }

  @Override
  public @Nullable String getSuperClassName(@Nullable String className) {
    Class<?> aClass = findClassSafe(className);
    Class<?> superClass = aClass == null ? null : aClass.getSuperclass();
    return superClass != null && superClass != Object.class ? superClass.getName() : null;
  }
}
