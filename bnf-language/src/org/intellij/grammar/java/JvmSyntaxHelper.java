/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.SmartList;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.MethodInfo;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.TypeParameterInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * The {@link JavaHelper} adapter over a {@link JvmClassSymbolManager}. Every override delegates to
 * the manager (for {@link ClassInfo} lookups) or to {@link ClassInfoUtil} (for unwrapping
 * {@link MyElement} wrappers). The manager owns the cache and the ordered provider list, so this
 * class holds no symbol state of its own.
 * <p>
 * {@link #findRuleImplMethods} and {@link #getClassReferences} are intentionally <i>not</i>
 * overridden — they fall through to {@link JavaHelper}'s defaults (a no-op return for headless use,
 * meaningful only when a PSI-backed subclass overrides them).
 */
public class JvmSyntaxHelper extends JavaHelper {

  private final JvmClassSymbolManager manager;

  public JvmSyntaxHelper(@NotNull JvmClassSymbolManager manager) {
    this.manager = manager;
  }

  @Override
  public boolean isPublic(@Nullable NavigatablePsiElement element) {
    return ClassInfoUtil.isPublic(element);
  }

  @Override
  public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
    ClassInfo info = manager.findClass(Fqn.ofNullable(className));
    return info == null ? null : new MyElement<>(info);
  }

  @Override
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    ClassInfo aClass = manager.findClass(Fqn.ofNullable(className));
    if (aClass == null || methodName == null) return Collections.emptyList();
    List<NavigatablePsiElement> result = new SmartList<>();
    for (MethodInfo method : aClass.methods) {
      if (!acceptsName(methodName, method.name)) continue;
      if (method.methodType != methodType) continue;
      if (!acceptsModifiers(method.modifiers, methodType, allowAbstract)) continue;
      if (!ClassInfoUtil.acceptsParams(method, paramCount, paramTypes, s -> manager.findClass(Fqn.ofNullable(s)))) continue;
      result.add(new MyElement<>(method));
    }
    return result;
  }

  @Override
  public @Nullable String getSuperClassName(@Nullable String className) {
    ClassInfo info = manager.findClass(Fqn.ofNullable(className));
    return info == null || info.superClass == null ? null : info.superClass.value();
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
}
