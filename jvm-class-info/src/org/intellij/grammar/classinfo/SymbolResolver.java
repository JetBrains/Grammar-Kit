/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import org.jetbrains.annotations.Nullable;

/**
 * Read-only view a {@link JvmClassSymbolManager} exposes to providers so their extractors can
 * probe arbitrary FQNs during extraction — needed when a class in one language imports a class
 * in another (e.g. a Java file with a wildcard import that resolves to a Kotlin class).
 * <p>
 * Implementations must tolerate recursive calls during in-flight extraction: the manager uses a
 * cycle-protection set that makes a recursive lookup for the FQN currently being built return
 * {@code null}. Extractors must treat {@code null} as "I don't know" and fall through to a
 * textual best guess.
 */
public interface SymbolResolver {
  @Nullable ClassSymbol findClass(@Nullable Fqn fqn);
}
