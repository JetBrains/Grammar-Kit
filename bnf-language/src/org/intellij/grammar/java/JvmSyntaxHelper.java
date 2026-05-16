/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.util.SmartList;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.MethodType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * The {@link JavaHelper} adapter over a {@link JvmClassSymbolManager}. Lookup overrides
 * ({@link #findClass}, {@link #findClassMethods}, {@link #getSuperClassName}) consult the manager
 * and wrap results in {@link MyElement}. Metadata reads inherit {@link JavaHelper}'s defaults,
 * which dispatch through {@link ClassSymbolUtil}. The manager owns the cache and the ordered
 * provider list, so this class holds no symbol state of its own.
 */
public class JvmSyntaxHelper extends JavaHelper {

  private final JvmClassSymbolManager manager;

  public JvmSyntaxHelper(@NotNull JvmClassSymbolManager manager) {
    this.manager = manager;
  }

  @Override
  public @Nullable NavigatablePsiElement findClass(@Nullable String className) {
    ClassSymbol info = manager.findClass(Fqn.ofNullable(className));
    return info == null ? null : new MyElement<>(info);
  }

  @Override
  public @NotNull List<NavigatablePsiElement> findClassMethods(@Nullable String className,
                                                               @NotNull MethodType methodType,
                                                               @Nullable String methodName,
                                                               boolean allowAbstract,
                                                               int paramCount,
                                                               String... paramTypes) {
    ClassSymbol aClass = manager.findClass(Fqn.ofNullable(className));
    if (aClass == null || methodName == null) return Collections.emptyList();
    List<NavigatablePsiElement> result = new SmartList<>();
    for (MethodSymbol method : aClass.methods()) {
      if (!ClassSymbolUtil.acceptsName(methodName, method.name())) continue;
      if (method.methodType() != methodType) continue;
      if (!ClassSymbolUtil.acceptsModifiers(method.modifiers(), methodType, allowAbstract)) continue;
      if (!ClassSymbolUtil.acceptsParams(method, paramCount, paramTypes, s -> manager.findClass(Fqn.ofNullable(s)))) continue;
      result.add(new MyElement<>(method));
    }
    return result;
  }

  @Override
  public @Nullable String getSuperClassName(@Nullable String className) {
    ClassSymbol info = manager.findClass(Fqn.ofNullable(className));
    return info == null || info.superClass() == null ? null : info.superClass().value();
  }
}
