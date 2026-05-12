/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.SmartList;
import org.intellij.grammar.java.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * {@link JavaHelper} that resolves classes and methods from Java <i>source</i> files using the
 * {@code syntax-api} + {@code java-syntax} libraries (no IntelliJ Platform / PSI dependency).
 * <p>
 * This class is the public {@link JavaHelper} surface only. All actual work — FQN → source-file
 * mapping, parsing, tree walking, caching — is delegated to {@link JavaClassManager}; the
 * {@link MyElement}-based delegate readers come from {@link ClassInfoUtil}.
 * <p>
 * Used from the headless CLI when the {@code --source-psi} flag is set; the IDE continues to use
 * {@link PsiHelper}. A {@code fallback} helper (typically {@link AsmHelper}) handles classes that
 * live outside the configured source roots — IntelliJ Platform jars, third-party dependencies —
 * so a single helper covers both project sources and bytecode-only references.
 */
public class JavaSyntaxHelper extends JavaHelper {

  private final Function<String, ClassInfo> classLookup;
  private final @Nullable JavaHelper fallback;

  public JavaSyntaxHelper(@NotNull List<Path> sourceRoots) {
    this(sourceRoots, null);
  }

  public JavaSyntaxHelper(@NotNull List<Path> sourceRoots, @Nullable JavaHelper fallback) {
    this(new JavaClassManager(sourceRoots)::findClass, fallback);
  }

  /**
   * Package-private test seam: lets unit tests inject an in-memory FQN → {@link ClassInfo} lookup
   * and bypass the source-file parsing pipeline ({@link JavaClassManager},
   * {@link JavaSyntaxTreeManager}, {@link JavaSyntaxClassExtractor}).
   */
  JavaSyntaxHelper(@NotNull Function<String, ClassInfo> classLookup, @Nullable JavaHelper fallback) {
    this.classLookup = classLookup;
    this.fallback = fallback;
  }

  @Override
  public boolean isPublic(@Nullable NavigatablePsiElement element) {
    return ClassInfoUtil.isPublic(element);
  }

  @Override
  public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
    ClassInfo info = classLookup.apply(className);
    if (info != null) return new MyElement<>(info);
    return fallback == null ? null : fallback.findClass(className);
  }

  @Override
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    ClassInfo aClass = classLookup.apply(className);
    if (aClass == null) {
      return fallback == null ? Collections.emptyList()
                              : fallback.findClassMethods(className, methodType, methodName, allowAbstract, paramCount, paramTypes);
    }
    if (methodName == null) return Collections.emptyList();
    List<NavigatablePsiElement> result = new SmartList<>();
    for (MethodInfo method : aClass.methods) {
      if (!acceptsName(methodName, method.name)) continue;
      if (method.methodType != methodType) continue;
      if (!acceptsModifiers(method.modifiers, methodType, allowAbstract)) continue;
      if (!ClassInfoUtil.acceptsParams(method, paramCount, paramTypes, this::lookupClassInfo)) continue;
      result.add(new MyElement<>(method));
    }
    return result;
  }

  @Override
  public @Nullable String getSuperClassName(@Nullable String className) {
    ClassInfo info = classLookup.apply(className);
    if (info != null) return info.superClass;
    return fallback == null ? null : fallback.getSuperClassName(className);
  }

  @Override
  public @NotNull List<String> getMethodTypes(@Nullable NavigatablePsiElement method) {
    return ClassInfoUtil.getMethodTypes(method);
  }

  @Override
  public List<TypeParameterInfo> getGenericParameters(NavigatablePsiElement method) {
    return ClassInfoUtil.getGenericParameters(method);
  }

  @Override
  public List<String> getExceptionList(NavigatablePsiElement method) {
    return ClassInfoUtil.getExceptionList(method);
  }

  @Override
  public @NotNull String getDeclaringClass(@Nullable NavigatablePsiElement method) {
    return ClassInfoUtil.getDeclaringClass(method);
  }

  @Override
  public @NotNull List<String> getAnnotations(@Nullable NavigatablePsiElement element) {
    return ClassInfoUtil.getAnnotations(element);
  }

  @Override
  public @NotNull List<String> getParameterAnnotations(@Nullable NavigatablePsiElement method, int paramIndex) {
    return ClassInfoUtil.getParameterAnnotations(method, paramIndex);
  }

  /**
   * Class-info lookup used by {@link ClassInfoUtil#acceptsParams} to probe supertypes during
   * parameter-arity matching. Tries the source roots first, then unwraps a {@link MyElement} from
   * the fallback so subtype checks see the same {@link ClassInfo} shape regardless of helper.
   */
  private @Nullable ClassInfo lookupClassInfo(@Nullable String fqn) {
    ClassInfo info = classLookup.apply(fqn);
    if (info != null) return info;
    if (fallback == null) return null;
    NavigatablePsiElement element = fallback.findClass(fqn);
    return element instanceof MyElement<?> e && e.delegate instanceof ClassInfo ci ? ci : null;
  }
}
