/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Locates the canonical declaring class of a nested type accessed through a subclass — JLS 7.5.1
 * requires single-type-imports (both regular and static) to name the canonical declaration, and the
 * same rule applies to dotted code references like {@code Sub.Inner} where {@code Inner} is
 * inherited from {@code Sub}'s supertype.
 * <p>
 * Pure resolver walk: superclass + interfaces, transitively, cycle-safe. Returns {@code null} when
 * no nested type with the given simple name is reachable — callers fall back to the as-written FQN.
 */
@SuppressWarnings("UnstableApiUsage")
final class NestedTypeResolver {

  private NestedTypeResolver() {}

  /**
   * Returns the canonical FQN of a nested type named {@code simple} accessible from
   * {@code enclosing}, walking the supertype chain when {@code enclosing} merely inherits the
   * nested type. Returns {@code null} when nothing matching is reachable through {@code resolver}.
   */
  static @Nullable String findDeclaringClass(@NotNull Fqn enclosing,
                                             @NotNull String simple,
                                             @NotNull SymbolResolver resolver) {
    return walk(enclosing, simple, resolver, new HashSet<>());
  }

  private static @Nullable String walk(@NotNull Fqn classFqn,
                                       @NotNull String simple,
                                       @NotNull SymbolResolver resolver,
                                       @NotNull Set<Fqn> visited) {
    if (!visited.add(classFqn)) return null;
    Fqn candidate = classFqn.child(simple);
    if (resolver.findClass(candidate) != null) return candidate.value();
    ClassSymbol enclosingSymbol = resolver.findClass(classFqn);
    if (enclosingSymbol == null) return null;
    if (enclosingSymbol.superClass() != null) {
      String r = walk(enclosingSymbol.superClass(), simple, resolver, visited);
      if (r != null) return r;
    }
    for (Fqn iface : enclosingSymbol.interfaces()) {
      String r = walk(iface, simple, resolver, visited);
      if (r != null) return r;
    }
    return null;
  }
}
