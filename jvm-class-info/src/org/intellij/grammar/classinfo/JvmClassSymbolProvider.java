/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * SPI for producers of {@link ClassSymbol} records. The {@link JvmClassSymbolManager} owns the
 * cache and dispatches lookups across an ordered list of providers; each provider knows how to
 * build {@link ClassSymbol} from one kind of input (Java sources, Kotlin sources, JVM bytecode).
 * <p>
 * Implementations return a batch — every class discovered while resolving the requested FQN —
 * so a single file parse can populate sibling classes in the same compilation unit without
 * forcing the caller to ask for each one separately. An empty map means "I cannot resolve this
 * FQN" and the manager moves on to the next provider.
 */
public interface JvmClassSymbolProvider {

  /**
   * Resolves {@code fqn} and returns every class discovered during the resolution attempt.
   * Returns an empty map if this provider cannot satisfy the request.
   *
   * @param resolver a read-only view of the manager. Extractors should consult this for any
   *                 simple-name → FQN resolution that might cross language boundaries (wildcard
   *                 imports, same-package references). Cycle protection is enforced by the
   *                 manager; treat a {@code null} return as "I don't know".
   */
  @NotNull Map<Fqn, ClassSymbol> resolve(@NotNull Fqn fqn, @NotNull SymbolResolver resolver);
}
