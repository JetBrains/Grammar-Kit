/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.*;
import com.intellij.util.*;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author gregsh
 */
public abstract class JavaHelper {

  public static JavaHelper getJavaHelper(@NotNull PsiElement context) {
    JavaHelper service = context.getProject().getService(JavaHelper.class);
    return service == null ? new AsmHelper() : service;
  }

  static boolean acceptsName(@Nullable String expected, @Nullable String actual) {
    return "*".equals(expected) || expected != null && expected.equals(actual) || expected != null && actual != null 
                                                                                  && actual.contains("$") 
                                                                                  && actual.startsWith(expected);
  }

  static boolean acceptsModifiers(int modifiers, MethodType methodType, boolean allowAbstract) {
    return (!Modifier.isAbstract(modifiers) || allowAbstract) &&
           !(methodType == MethodType.CONSTRUCTOR && Modifier.isPrivate(modifiers));
  }

  public abstract boolean isPublic(@Nullable NavigatablePsiElement element);

  public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
    return null;
  }

  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               int paramCount,
                                                               String... paramTypes) {
    return findClassMethods(className, methodType, methodName, false, paramCount, paramTypes);
  }

  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    return Collections.emptyList();
  }

  public @Nullable String getSuperClassName(@Nullable String className) {
    return null;
  }

  public @NotNull List<NavigatablePsiElement> findRuleImplMethods(@Nullable String psiImplUtilClass,
                                                                  @Nullable String methodName,
                                                                  @Nullable BnfRule rule) {
    if (rule == null) return Collections.emptyList();
    List<NavigatablePsiElement> methods = Collections.emptyList();
    String selectedSuperClass = null;
    main: for (String ruleClass : BnfRules.getRuleClasses(rule)) {
      for (String utilClass = psiImplUtilClass; utilClass != null; utilClass = getSuperClassName(utilClass)) {
        methods = findClassMethods(utilClass, MethodType.STATIC, methodName, -1, ruleClass);
        selectedSuperClass = ruleClass;
        if (!methods.isEmpty()) break main;
      }
    }
    return filterOutShadowedRuleImplMethods(selectedSuperClass, methods);
  }

  private @NotNull List<NavigatablePsiElement> filterOutShadowedRuleImplMethods(String selectedClass,
                                                                                List<NavigatablePsiElement> methods) {
    if (methods.size() <= 1) return methods;

    List<NavigatablePsiElement> result = new ArrayList<>(methods);
    Map<String, NavigatablePsiElement> prototypes = new LinkedHashMap<>();
    for (NavigatablePsiElement m2 : methods) {
      List<String> types = getMethodTypes(m2);
      String proto = m2.getName() + types.subList(3, types.size());
      NavigatablePsiElement m1 = prototypes.get(proto);
      if (m1 == null) {
        prototypes.put(proto, m2);
        continue;
      }
      String type1 = getMethodTypes(m1).get(1);
      String type2 = types.get(1);
      if (Objects.equals(type1, type2)) continue;
      for (String s = selectedClass; s != null; s = getSuperClassName(s)) {
        if (Objects.equals(type1, s)) {
          result.remove(m2);
        }
        else if (Objects.equals(type2, s)) {
          result.remove(m1);
        }
        else continue;
        break;
      }
    }
    return result;
  }

  public @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public List<String> getExceptionList(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    return "";
  }

  public @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    return Collections.emptyList();
  }

  public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    return Collections.emptyList();
  }

  public PsiReference @NotNull [] getClassReferences(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    return PsiReference.EMPTY_ARRAY;
  }

  public @Nullable NavigationItem findPackage(@Nullable String packageName) {
    return null;
  }

  public enum MethodType {STATIC, INSTANCE, CONSTRUCTOR}
}
