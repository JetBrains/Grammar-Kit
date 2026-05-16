/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.*;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.intellij.grammar.classinfo.asm.AsmClassSymbolProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
   * Returns the {@link JavaHelper} to use for the given {@code context}. Inside the IDE this is a
   * fresh {@code PsiHelper} produced by the project-level {@code PsiHelperFactory} service
   * (registered in {@code plugin-java.xml}); the factory bakes a {@code *InputPath}-derived search
   * scope into the helper based on {@code context}'s position in the BNF tree. When no factory is
   * available (e.g. headless / CLI), returns the {@link JavaHelper} registered as a project
   * service — a {@link JvmSyntaxHelper} by default, or a source-backed {@link JvmSyntaxHelper}
   * when the CLI opted in via {@code --source-psi}. Falls back to a fresh {@link JvmSyntaxHelper}
   * when nothing is registered so callers always get a usable helper rather than {@code null}.
   */
  public static JavaHelper getJavaHelper(@NotNull PsiElement context) {
    Project project = context.getProject();
    PsiHelperFactory factory = project.getService(PsiHelperFactory.class);
    if (factory != null) {
      return factory.getInstance(context);
    }

    //can be installed by Main to JavaSyntaxHelper
    JavaHelper service = project.getService(JavaHelper.class);
    if (service != null) {
      return service;
    }

    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(new AsmClassSymbolProvider())));
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

  /**
   * Looks up methods on {@code className} that match the given filters.
   *
   * @param methodType  whether to look at static methods, instance methods, or constructors
   * @param methodName  exact name, {@code "*"} wildcard, or {@code null} for "no match"
   * @param paramCount  required number of parameters; pass {@code -1} to skip the arity check
   * @param paramTypes  prefix of expected parameter type names; an empty array matches any parameter list
   *                    (entries follow the {@link ClassSymbolUtil#acceptsName} matching rules and may also resolve via
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
   * Returns the method's signature as a flat string list:
   * index 0 is the return type, then alternating {@code (type, paramName)} pairs for each parameter.
   * The {@code paramName} entries are synthesised ({@code "p0"}, {@code "p1"}…) when the source does
   * not preserve them (bytecode / reflection). Used by the generator when emitting delegate code.
   */
  public @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  /** Generic type parameters declared on the method itself, in declaration order. */
  public List<TypeParameterSymbol> getGenericParameters(NavigatablePsiElement method) {
    return Collections.emptyList();
  }

  /** FQNs of the method's declared checked exceptions ({@code throws} clause). */
  public List<String> getExceptionList(NavigatablePsiElement method) {
    return Collections.emptyList();
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

}
