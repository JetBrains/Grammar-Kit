/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.*;
import com.intellij.util.*;
import org.intellij.grammar.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;

/**
 * Abstraction over Java type introspection used by Grammar-Kit to resolve method and class references
 * declared in BNF attributes (e.g. {@code mixin}, {@code methods}, {@code psiImplUtilClass}).
 * <p>
 * Grammar-Kit needs to know about Java classes and methods in three very different environments:
 * <ul>
 *   <li>Inside the IDE, where a real PSI model exists for project sources and library classes.</li>
 *   <li>In a headless / build-time generator that has only the compiled bytecode on the classpath.</li>
 *   <li>In a runtime environment where classes are already loaded by the JVM.</li>
 * </ul>
 * Each environment is served by a dedicated subclass — {@link PsiHelper}, {@link AsmHelper}, and
 * {@link ReflectionHelper} respectively. All of them produce {@link NavigatablePsiElement}s so that
 * call-sites (navigation, completion, line markers, the parser generator) can treat the result
 * uniformly. {@link PsiHelper} returns real {@code PsiClass} / {@code PsiMethod} instances; the other
 * two wrap their bytecode/reflection findings in {@link MyElement} fakes.
 * <p>
 * The base class itself returns empty results from every query, so a partially-implemented subclass
 * still degrades gracefully.
 *
 * @author gregsh
 * @see AsmHelper
 * @see PsiHelper
 * @see ReflectionHelper
 */
public abstract class JavaHelper {

  /**
   * Returns the {@link JavaHelper} to use for the given {@code context}. Inside the IDE this is the
   * project-level {@link PsiHelper} service registered in {@code plugin-java.xml}. When no service is
   * available (e.g. light tests, command-line usage), falls back to a fresh {@link AsmHelper} so the
   * caller still gets bytecode-driven introspection rather than {@code null}.
   */
  public static JavaHelper getJavaHelper(@NotNull PsiElement context) {
    JavaHelper service = context.getProject().getService(JavaHelper.class);
    return service == null ? new AsmHelper() : service;
  }

  /**
   * Matches a {@code paramTypes}/{@code methodName} entry from a BNF attribute against an actual
   * member name. Accepts the wildcard {@code "*"}, an exact match, or — for inner classes — a prefix
   * match where {@code actual} is something like {@code "Outer$Inner"} and {@code expected} is the
   * outer-class name.
   */
  static boolean acceptsName(@Nullable String expected, @Nullable String actual) {
    return "*".equals(expected) ||
           expected != null && expected.equals(actual) ||
           expected != null && actual != null && actual.contains("$") && actual.startsWith(expected);
  }

  /**
   * Filters out members that are abstract (unless {@code allowAbstract}) or private constructors,
   * which Grammar-Kit cannot meaningfully invoke from generated code.
   */
  static boolean acceptsModifiers(int modifiers, MethodType methodType, boolean allowAbstract) {
    return (!Modifier.isAbstract(modifiers) || allowAbstract) &&
           !(methodType == MethodType.CONSTRUCTOR && Modifier.isPrivate(modifiers));
  }

  /** Whether the given class or method is declared {@code public}. */
  public abstract boolean isPublic(@Nullable NavigatablePsiElement element);

  /**
   * Resolves a fully-qualified class name to a navigatable element, or {@code null} when the class
   * cannot be located in this helper's view of the world.
   */
  public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
    return null;
  }

  /** Convenience overload of {@link #findClassMethods(String, MethodType, String, boolean, int, String...)} with {@code allowAbstract = false}. */
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               int paramCount,
                                                               String... paramTypes) {
    return findClassMethods(className, methodType, methodName, false, paramCount, paramTypes);
  }

  /**
   * Looks up methods on {@code className} that match the given filters.
   *
   * @param methodType  whether to look at static methods, instance methods, or constructors
   * @param methodName  exact name, {@code "*"} wildcard, or {@code null} for "no match"
   * @param paramCount  required number of parameters; pass {@code -1} to skip the arity check
   * @param paramTypes  prefix of expected parameter type names; an empty array matches any parameter list
   *                    (entries follow the {@link #acceptsName} matching rules and may also resolve via
   *                    a supertype/interface check on the candidate parameter)
   */
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    return Collections.emptyList();
  }

  /** FQN of the direct superclass of {@code className}, or {@code null} for {@code Object} / unknown classes. */
  public @Nullable String getSuperClassName(@Nullable String className) {
    return null;
  }

  /**
   * Resolves the static helper methods that implement extra members of a generated PSI rule (the
   * {@code methods} attribute combined with {@code psiImplUtilClass}).
   * <p>
   * Walks the rule's PSI hierarchy — every class returned by {@link BnfRules#getRuleClasses(BnfRule)} —
   * and for each one walks the {@code psiImplUtilClass} chain via {@link #getSuperClassName} until a
   * matching static method is found whose first parameter accepts the rule's PSI type. If multiple
   * overloads match, {@link #filterOutShadowedRuleImplMethods} keeps only the most specific overload
   * for the matched rule class.
   */
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

  /**
   * Returns the method's signature as a flat string list:
   * index 0 is the return type, then alternating {@code (type, paramName)} pairs for each parameter.
   * The {@code paramName} entries are synthesised ({@code "p0"}, {@code "p1"}…) when the source does
   * not preserve them (bytecode / reflection). Used by the generator when emitting delegate code.
   */
  public @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  /** Generic type parameters declared on the method itself, in declaration order. */
  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  /** FQNs of the method's declared checked exceptions ({@code throws} clause). */
  public List<String> getExceptionList(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  /** FQN of the class that declares the method, or {@code ""} when unavailable. */
  public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    return "";
  }

  /** FQNs of annotations declared directly on the class or method. */
  public @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    return Collections.emptyList();
  }

  /** FQNs of annotations on the parameter at the given index. Returns empty if the index is out of range. */
  public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    return Collections.emptyList();
  }

  /**
   * Builds {@link PsiReference}s for a class-name string literal living inside a BNF attribute, so the
   * IDE can offer navigation, completion and rename. Only meaningful in a real IDE context — the
   * default and bytecode/reflection helpers return no references.
   */
  public PsiReference @NotNull [] getClassReferences(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    return PsiReference.EMPTY_ARRAY;
  }

  /** Selector for {@link #findClassMethods}: which kind of member is being looked up. */
  public enum MethodType {STATIC, INSTANCE, CONSTRUCTOR}
}
