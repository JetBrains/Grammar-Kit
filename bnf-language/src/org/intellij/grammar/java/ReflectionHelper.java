/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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
 * Limitation compared to {@link AsmHelper}: parameter names cannot be recovered from reflection,
 * so {@link #getMethodTypes} synthesises {@code "p0"}, {@code "p1"}… placeholders. Type annotations
 * on parameters are also not exposed — {@link #getParameterAnnotations} falls back to the method's
 * own annotation list.
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
      if (acceptsName(paramType, parameter.getCanonicalName())) continue;
      Class<?> paramClass = findClassSafe(paramType);
      if (paramClass != null && parameter.isAssignableFrom(paramClass)) continue;
      return false;
    }
    return true;
  }

  private static boolean acceptsMethod(Member method, MethodType methodType, boolean allowAbstract) {
    int modifiers = method.getModifiers();
    return (methodType == MethodType.STATIC) == Modifier.isStatic(modifiers) &&
           acceptsModifiers(modifiers, methodType, allowAbstract);
  }

  private static @NotNull List<String> getAnnotationsInner(@NotNull AnnotatedElement delegate) {
    Annotation[] annotations = delegate.getDeclaredAnnotations();
    List<String> result = new ArrayList<>(annotations.length);
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType(); // todo parameters?
      ContainerUtil.addIfNotNull(result, annotationType.getCanonicalName());
    }
    return result;
  }

  @Override
  public boolean isPublic(@Nullable NavigatablePsiElement element) {
    Object delegate = element instanceof MyElement ? ((MyElement<?>)element).delegate : null;
    int modifiers = delegate instanceof Class ? ((Class<?>)delegate).getModifiers() :
                    delegate instanceof Method ? ((Method)delegate).getModifiers() :
                    0;
    return Modifier.isPublic(modifiers);
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
      if (!acceptsName(methodName, method.getName())) continue;
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

  @Override
  public @NotNull List<String> getMethodTypes(NavigatablePsiElement method) {
    if (method == null) return Collections.emptyList();
    Method delegate = ((MyElement<Method>)method).delegate;
    Type[] parameterTypes = delegate.getGenericParameterTypes();
    List<String> result = new ArrayList<>(parameterTypes.length + 1);
    result.add(delegate.getGenericReturnType().toString());
    int paramCounter = 0;
    for (Type parameterType : parameterTypes) {
      result.add(parameterType.toString());
      result.add("p" + (paramCounter++));
    }
    return result;
  }

  @Override
  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    if (method == null) return Collections.emptyList();
    Method delegate = ((MyElement<Method>)method).delegate;

    TypeVariable<Method>[] typeParameters = delegate.getTypeParameters();
    return ContainerUtil.map(typeParameters, param -> new TypeParameterInfo(
      param.getName(),
      ContainerUtil.mapNotNull(param.getBounds(), type -> {
        String typeName = type.getTypeName();
        return "java.lang.Object".equals(typeName) ? null : typeName;
      }),
      ContainerUtil.mapNotNull(param.getAnnotations(), o -> o.annotationType().getCanonicalName())));
  }

  @Override
  public List<String> getExceptionList(NavigatablePsiElement method) {
    if (method == null) return Collections.emptyList();
    Method delegate = ((MyElement<Method>)method).delegate;

    Class<?>[] exceptionTypes = delegate.getExceptionTypes();
    return ContainerUtil.map(exceptionTypes, Class::getName);
  }

  @Override
  public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    if (method == null) return "";
    return ((MyElement<Method>)method).delegate.getDeclaringClass().getName();
  }

  @Override
  public @NotNull List<String> getAnnotations(NavigatablePsiElement element) {
    if (element == null) return Collections.emptyList();
    AnnotatedElement delegate = ((MyElement<AnnotatedElement>)element).delegate;
    return getAnnotationsInner(delegate);
  }

  @Override
  public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    if (method == null) return Collections.emptyList();
    Method delegate = ((MyElement<Method>)method).delegate;
    AnnotatedType[] parameterTypes = delegate.getAnnotatedParameterTypes();
    if (paramIndex < 0 || paramIndex >= parameterTypes.length) return Collections.emptyList();
    return getAnnotationsInner(delegate);
  }
}
