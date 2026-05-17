/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.PsiVariable;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.classinfo.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Single dispatch point for reading method/class metadata across every element shape any
 * {@link JavaHelper} subclass can produce:
 * <ul>
 *   <li>PSI elements ({@link PsiMethod}, {@link PsiModifierListOwner}) from {@link PsiHelper}.</li>
 *   <li>{@link MyElement}-wrapped {@link ClassSymbol} / {@link MethodSymbol} records from {@link JvmSyntaxHelper}.</li>
 *   <li>{@link MyElement}-wrapped {@code java.lang.reflect.*} delegates from {@link ReflectionHelper}.</li>
 * </ul>
 * Each public method switches on the element type and routes to the right private extractor.
 * Unknown non-null element shapes are a programming error and throw {@link IllegalArgumentException}
 * — there is no silent empty-list fallback. {@code null} is tolerated and returns the natural empty
 * answer.
 */
public final class ClassSymbolUtil {

  private ClassSymbolUtil() { }

  public static boolean isPublic(@Nullable NavigatablePsiElement element) {
    if (element == null) return false;
    if (element instanceof PsiModifierListOwner psi) return psi.hasModifierProperty(PsiModifier.PUBLIC);
    Object d = delegateOf(element);
    if (d instanceof ClassSymbol c) return Modifier.isPublic(c.modifiers());
    if (d instanceof MethodSymbol m) return Modifier.isPublic(m.modifiers());
    if (d instanceof Class<?> c) return Modifier.isPublic(c.getModifiers());
    if (d instanceof Method m) return Modifier.isPublic(m.getModifiers());
    throw unsupported(element);
  }

  public static @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    if (method == null) return Collections.emptyList();
    if (method instanceof PsiMethod psi) return methodTypesFromPsi(psi);
    Object d = delegateOf(method);
    if (d instanceof MethodSymbol m) return methodTypesFromSymbol(m);
    if (d instanceof Method m) return methodTypesFromReflection(m);
    requireKnownShape(method, d);
    return Collections.emptyList();
  }

  public static @NotNull List<TypeParameterSymbol> getGenericParameters(@Nullable NavigatablePsiElement method) {
    if (method == null) return Collections.emptyList();
    if (method instanceof PsiMethod psi) return genericsFromPsi(psi);
    Object d = delegateOf(method);
    if (d instanceof MethodSymbol m) return m.generics();
    if (d instanceof Method m) return genericsFromReflection(m);
    requireKnownShape(method, d);
    return Collections.emptyList();
  }

  public static @NotNull List<String> getExceptionList(@Nullable NavigatablePsiElement method) {
    if (method == null) return Collections.emptyList();
    if (method instanceof PsiMethod psi) return exceptionsFromPsi(psi);
    Object d = delegateOf(method);
    if (d instanceof MethodSymbol m) return ContainerUtil.map(m.exceptions(), Fqn::value);
    if (d instanceof Method m) return ContainerUtil.map(m.getExceptionTypes(), Class::getName);
    requireKnownShape(method, d);
    return Collections.emptyList();
  }

  public static @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    Object delegate = delegateOf(method);
    if (!(delegate instanceof MethodSymbol)) return "";
    Fqn declaring = ((MethodSymbol)delegate).declaringClass();
    return declaring == null ? "" : declaring.value();
  }

  public static @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    if (element == null) return Collections.emptyList();
    if (element instanceof PsiModifierListOwner psi) return annotationsFromPsi(psi);
    Object d = delegateOf(element);
    if (d instanceof ClassSymbol c) return ContainerUtil.map(c.annotations(), Fqn::value);
    if (d instanceof MethodSymbol m) return ContainerUtil.map(m.annotations(), Fqn::value);
    if (d instanceof AnnotatedElement ae) return annotationsFromReflection(ae);
    throw unsupported(element);
  }

  public static @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    if (method == null) return Collections.emptyList();
    if (method instanceof PsiMethod psi) return parameterAnnotationsFromPsi(psi, paramIndex);
    Object d = delegateOf(method);
    if (d instanceof MethodSymbol m) {
      List<ParameterSymbol> parameters = m.parameters();
      if (paramIndex < 0 || paramIndex >= parameters.size()) return Collections.emptyList();
      return ContainerUtil.map(parameters.get(paramIndex).annotations(), Fqn::value);
    }
    if (d instanceof Method m) return parameterAnnotationsFromReflection(m, paramIndex);
    requireKnownShape(method, d);
    return Collections.emptyList();
  }

  /**
   * Matches a {@code paramTypes}/{@code methodName} entry from a BNF attribute against an actual
   * member name. Accepts the wildcard {@code "*"}, an exact match, or — for inner classes — a prefix
   * match where {@code actual} is something like {@code "Outer$Inner"} and {@code expected} is the
   * outer-class name.
   */
  public static boolean acceptsName(@Nullable String expected, @Nullable String actual) {
    return "*".equals(expected) ||
           expected != null && expected.equals(actual) ||
           expected != null && actual != null && actual.contains("$") && actual.startsWith(expected);
  }

  /**
   * Filters out members that are abstract (unless {@code allowAbstract}) or private constructors,
   * which Grammar-Kit cannot meaningfully invoke from generated code.
   */
  public static boolean acceptsModifiers(int modifiers, MethodType methodType, boolean allowAbstract) {
    return (!Modifier.isAbstract(modifiers) || allowAbstract) &&
           !(methodType == MethodType.CONSTRUCTOR && Modifier.isPrivate(modifiers));
  }

  // region Symbol-family extractors

  private static @NotNull List<String> methodTypesFromSymbol(@NotNull MethodSymbol m) {
    List<String> out = new ArrayList<>(1 + 2 * m.parameters().size());
    out.add(m.annotatedReturnType() != null ? m.annotatedReturnType() : m.returnType());
    for (ParameterSymbol p : m.parameters()) {
      out.add(p.annotatedType() != null ? p.annotatedType() : p.type());
      out.add(p.name());
    }
    return out;
  }

  // endregion

  // region PSI-family extractors

  private static @NotNull List<String> methodTypesFromPsi(@NotNull PsiMethod method) {
    PsiType returnType = method.getReturnType();
    List<String> strings = new ArrayList<>();
    strings.add(returnType == null ? "" : returnType.getCanonicalText(true));
    for (PsiParameter parameter : method.getParameterList().getParameters()) {
      PsiType type = parameter.getType();
      boolean generic = type instanceof PsiClassType && ((PsiClassType)type).resolve() instanceof PsiTypeParameter;
      String typeText = (generic ? "<" : "") + type.getCanonicalText(true) + (generic ? ">" : "");
      strings.add(typeText);
      strings.add(parameter.getName());
    }
    return strings;
  }

  private static @NotNull List<TypeParameterSymbol> genericsFromPsi(@NotNull PsiMethod method) {
    PsiTypeParameter[] typeParameters = method.getTypeParameters();
    return ContainerUtil.map(typeParameters, param -> new TypeParameterSymbol(
      param.getName(),
      ContainerUtil.map(param.getExtendsListTypes(), bound -> bound.getCanonicalText(false)),
      ContainerUtil.map(annotationsFromPsi(param), Fqn::of)));
  }

  private static @NotNull List<String> exceptionsFromPsi(@NotNull PsiMethod method) {
    PsiClassType[] types = method.getThrowsList().getReferencedTypes();
    return ContainerUtil.map(types, type -> type.getCanonicalText(false));
  }

  private static @NotNull List<String> annotationsFromPsi(@NotNull PsiModifierListOwner element) {
    PsiModifierList modifierList = element.getModifierList();
    if (modifierList == null) return Collections.emptyList();
    PsiType typeToSkip = element instanceof PsiMethod ? ((PsiMethod)element).getReturnType() :
                         element instanceof PsiVariable ? ((PsiVariable)element).getType() : null;
    PsiAnnotation[] annoToSkip = typeToSkip == null ? null :
                                 typeToSkip instanceof PsiArrayType ? ((PsiArrayType)typeToSkip).getComponentType().getAnnotations() :
                                 typeToSkip.getAnnotations();
    String[] textToSkip = annoToSkip == null ? null :
                          ContainerUtil.map(annoToSkip, PsiElement::getText, ArrayUtil.EMPTY_STRING_ARRAY);
    List<String> result = new ArrayList<>();
    for (PsiAnnotation annotation : modifierList.getAnnotations()) {
      if (annotation.getParameterList().getAttributes().length > 0) continue;
      if (textToSkip != null && ArrayUtil.indexOf(textToSkip, annotation.getText()) != -1) continue;
      ContainerUtil.addIfNotNull(result, annotation.getQualifiedName());
    }
    return result;
  }

  private static @NotNull List<String> parameterAnnotationsFromPsi(@NotNull PsiMethod method, int paramIndex) {
    PsiParameter[] parameters = method.getParameterList().getParameters();
    if (paramIndex < 0 || paramIndex >= parameters.length) return Collections.emptyList();
    return annotationsFromPsi(parameters[paramIndex]);
  }

  // endregion

  // region Reflection-family extractors

  private static @NotNull List<String> methodTypesFromReflection(@NotNull Method delegate) {
    Type[] parameterTypes = delegate.getGenericParameterTypes();
    List<String> result = new ArrayList<>(1 + 2 * parameterTypes.length);
    result.add(delegate.getGenericReturnType().toString());
    int paramCounter = 0;
    for (Type parameterType : parameterTypes) {
      result.add(parameterType.toString());
      result.add("p" + (paramCounter++));
    }
    return result;
  }

  private static @NotNull List<TypeParameterSymbol> genericsFromReflection(@NotNull Method delegate) {
    TypeVariable<Method>[] typeParameters = delegate.getTypeParameters();
    return ContainerUtil.map(typeParameters, param -> new TypeParameterSymbol(
      param.getName(),
      ContainerUtil.mapNotNull(param.getBounds(), type -> {
        String typeName = type.getTypeName();
        return "java.lang.Object".equals(typeName) ? null : typeName;
      }),
      ContainerUtil.mapNotNull(param.getAnnotations(), o -> Fqn.ofNullable(o.annotationType().getCanonicalName()))));
  }

  private static @NotNull List<String> annotationsFromReflection(@NotNull AnnotatedElement delegate) {
    Annotation[] annotations = delegate.getDeclaredAnnotations();
    List<String> result = new ArrayList<>(annotations.length);
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      ContainerUtil.addIfNotNull(result, annotationType.getCanonicalName());
    }
    return result;
  }

  private static @NotNull List<String> parameterAnnotationsFromReflection(@NotNull Method delegate, int paramIndex) {
    AnnotatedType[] parameterTypes = delegate.getAnnotatedParameterTypes();
    if (paramIndex < 0 || paramIndex >= parameterTypes.length) return Collections.emptyList();
    return annotationsFromReflection(delegate);
  }

  // endregion

  private static @Nullable Object delegateOf(@Nullable NavigatablePsiElement element) {
    return element instanceof MyElement ? ((MyElement<?>)element).delegate : null;
  }

  /**
   * Throws if {@code element} is not one of the recognized JVM-symbol shapes. Used by method-only
   * dispatch points to distinguish "a known shape that doesn't apply here" (e.g. a class queried
   * for parameter annotations — return empty) from "an element shape this util doesn't understand"
   * (a programming error).
   */
  private static void requireKnownShape(@NotNull NavigatablePsiElement element, @Nullable Object delegate) {
    if (element instanceof PsiModifierListOwner) return;
    if (delegate instanceof ClassSymbol || delegate instanceof MethodSymbol) return;
    if (delegate instanceof Class<?> || delegate instanceof Method || delegate instanceof AnnotatedElement) return;
    throw unsupported(element);
  }

  private static @NotNull IllegalArgumentException unsupported(@NotNull NavigatablePsiElement element) {
    Object delegate = delegateOf(element);
    String shape = delegate != null ? "MyElement<" + delegate.getClass().getName() + ">" : element.getClass().getName();
    return new IllegalArgumentException("Unsupported element shape: " + shape);
  }
}
