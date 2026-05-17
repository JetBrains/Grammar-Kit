/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Read-only {@link JvmClassSymbolProvider} backed by a fixed, pre-built {@link ClassSymbol} map.
 * Used to inject synthesised classes (e.g. parser-generator stubs for not-yet-emitted PSI
 * interfaces) at the head of a manager's provider chain so they shadow on-disk versions.
 */
public final class ExtraClassSymbolProvider implements JvmClassSymbolProvider {

  private final Map<Fqn, ClassSymbol> byFqn;

  public ExtraClassSymbolProvider(@NotNull Map<Fqn, ClassSymbol> byFqn) {
    this.byFqn = Map.copyOf(byFqn);
  }

  @Override
  public @NotNull Map<Fqn, ClassSymbol> resolve(@NotNull Fqn fqn, @NotNull SymbolResolver resolver) {
    ClassSymbol symbol = byFqn.get(fqn);
    return symbol == null ? Collections.emptyMap() : Map.of(fqn, symbol);
  }
}
