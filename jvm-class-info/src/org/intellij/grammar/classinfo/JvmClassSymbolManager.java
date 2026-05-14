/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Owns the canonical {@link ClassInfo} cache and dispatches lookups across an ordered list of
 * {@link JvmClassSymbolProvider}s. Designed for single-build use — no invalidation, no eviction,
 * no thread safety. Construct one per generator invocation and discard.
 * <p>
 * Provider order matters: the first provider that returns a non-empty batch for an FQN wins.
 * The typical production order is Kotlin syntax → Java syntax → ASM, so Kotlin sources beat
 * a same-FQN Java file, and either source language beats a same-FQN bytecode reference.
 */
public final class JvmClassSymbolManager implements SymbolResolver {

  private static final Logger LOG = Logger.getInstance(JvmClassSymbolManager.class);

  private final List<JvmClassSymbolProvider> providers;
  private final Map<String, ClassInfo> cache = new HashMap<>();
  private final Set<String> negativeCache = new HashSet<>();
  private final Set<String> inProgress = new HashSet<>();

  public JvmClassSymbolManager(@NotNull List<JvmClassSymbolProvider> providers) {
    this.providers = new ArrayList<>(providers);
  }

  @Override
  public @Nullable ClassInfo findClass(@Nullable String fqn) {
    if (fqn == null || fqn.isEmpty()) return null;
    if (cache.containsKey(fqn)) return cache.get(fqn);
    if (negativeCache.contains(fqn)) return null;
    if (!inProgress.add(fqn)) return null;          // cycle break: this FQN is currently being built

    try {
      for (JvmClassSymbolProvider provider : providers) {
        Map<String, ClassInfo> batch = provider.resolve(fqn, this);
        mergeBatch(batch, provider);
        if (cache.containsKey(fqn)) return cache.get(fqn);
      }
      negativeCache.add(fqn);
      return null;
    }
    finally {
      inProgress.remove(fqn);
    }
  }

  /**
   * Appends a provider to the dispatch list. Intended for test infrastructure that wants to seed
   * a manager with fake symbols on top of the production provider list.
   */
  @TestOnly
  public void addProvider(@NotNull JvmClassSymbolProvider provider) {
    providers.add(provider);
  }

  private void mergeBatch(@NotNull Map<String, ClassInfo> batch, @NotNull JvmClassSymbolProvider source) {
    for (Map.Entry<String, ClassInfo> e : batch.entrySet()) {
      ClassInfo existing = cache.putIfAbsent(e.getKey(), e.getValue());
      if (existing != null) {
        LOG.warn("Duplicate class symbol for " + e.getKey() +
                 "; kept earlier definition, ignoring one from " + source.getClass().getSimpleName());
      }
    }
  }
}
