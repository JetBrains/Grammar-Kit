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
import org.intellij.grammar.generator.java.JavaNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

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
      if (!acceptsParams(method, paramCount, paramTypes, s -> manager.findClass(Fqn.ofNullable(s)))) continue;
      result.add(new MyElement<>(method));
    }
    return result;
  }

  @Override
  public @Nullable String getSuperClassName(@Nullable String className) {
    ClassSymbol info = manager.findClass(Fqn.ofNullable(className));
    return info == null || info.superClass() == null ? null : info.superClass().value();
  }

  /**
   * Filters by parameter arity and parameter type compatibility.
   * <p>
   * Subtype probes walk the full supertype DAG ({@link ClassSymbol#superClass} +
   * {@link ClassSymbol#interfaces}, transitively) through {@code classLookup}, so each helper
   * drives the lookup with its own backing model — bytecode for the ASM provider, source files
   * for the syntax providers, etc. {@code paramCount = -1} skips the arity check; an empty
   * {@code paramTypes} accepts any parameter list.
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private static boolean acceptsParams(@NotNull MethodSymbol method,
                                      int paramCount,
                                      @NotNull String[] paramTypes,
                                      @NotNull Function<String, ClassSymbol> classLookup) {
    int actualParams = method.parameters().size();
    if (paramCount >= 0 && paramCount != actualParams) return false;
    if (paramTypes.length == 0) return true;
    if (paramTypes.length > actualParams) return false;
    for (int i = 0; i < paramTypes.length; i++) {
      String paramType = paramTypes[i];
      String parameter = JavaNames.getRawClassName(method.parameters().get(i).type());
      if (ClassSymbolUtil.acceptsName(paramType, parameter)) continue;
      if (!isSubtype(paramType, parameter, classLookup)) return false;
    }
    return true;
  }

  /**
   * Returns {@code true} if {@code candidate} extends or implements {@code target} (transitively).
   * Walks the superclass chain and the full interface DAG through {@code classLookup}; each class
   * is visited at most once.
   */
  private static boolean isSubtype(@NotNull String candidate,
                                   @NotNull String target,
                                   @NotNull Function<String, ClassSymbol> classLookup) {
    Set<String> visited = new HashSet<>();
    Deque<String> stack = new ArrayDeque<>();
    stack.push(candidate);
    while (!stack.isEmpty()) {
      String current = stack.pop();
      if (!visited.add(current)) continue;
      ClassSymbol info = classLookup.apply(current);
      if (info == null) continue;
      if (info.superClass() != null) {
        String superName = info.superClass().value();
        if (target.equals(superName)) return true;
        stack.push(superName);
      }
      for (Fqn iface : info.interfaces()) {
        String ifaceName = iface.value();
        if (target.equals(ifaceName)) return true;
        stack.push(ifaceName);
      }
    }
    return false;
  }
}
