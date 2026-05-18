/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import org.intellij.grammar.java.JavaHelperFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable snapshot of every {@code *InputPath} / {@code *OutputPath} resolution for a single
 * BNF file. Pure data holder — no inference, no cascade. The map passed in is treated as the
 * fully resolved view: {@link #path} and {@link #pathString} are direct map lookups.
 *
 * <p>Each attribute resolves to a {@link List} of paths: single-path attributes carry a
 * one-element list, the multi-path {@link KnownAttribute#PSI_INPUT_PATH} carries one entry per
 * declared root. Convenience accessor {@link #path} returns the first element for callers that
 * only need a single directory; {@link #paths} exposes the full list.
 *
 * <p>Build via {@link BnfPaths#resolve(org.intellij.grammar.psi.BnfFile)} for a real file
 * (cached) or {@link BnfPaths#resolveExplicit(Map)} for a partial map (applies the
 * output-path cascade). Construct directly only when the map is already fully resolved.
 */
public final class BnfPathsResolution {
  public static final BnfPathsResolution EMPTY = new BnfPathsResolution(Map.of());

  private final Map<KnownAttribute<?>, List<Path>> myPaths;

  public BnfPathsResolution(@NotNull Map<KnownAttribute<?>, List<Path>> resolvedPaths) {
    Map<KnownAttribute<?>, List<Path>> copy = new HashMap<>();
    for (Map.Entry<KnownAttribute<?>, List<Path>> e : resolvedPaths.entrySet()) {
      List<Path> v = e.getValue();
      if (v != null && !v.isEmpty()) copy.put(e.getKey(), List.copyOf(v));
    }
    myPaths = Map.copyOf(copy);
  }

  /**
   * Effective absolute on-disk path for {@code attribute}, or {@code null} when the resolution
   * has no value for it. For multi-path attributes this returns the first element of
   * {@link #paths}; use {@link #paths} when every root matters.
   *
   * <p>A {@code null} for {@link KnownAttribute#INPUT_PATH} is the normal "no user-declared
   * input scope" signal for IDE-mode resolutions built via
   * {@link BnfPaths#resolve(org.intellij.grammar.psi.BnfFile)}: consumers with a {@code Project}
   * (e.g. {@link JavaHelperFactory}) fall back to a project-wide
   * search scope. CLI consumers without a {@code Project} should seed a default via
   * {@link BnfPaths#resolveExplicit(java.util.Map, Path)}.
   *
   * <p>A {@code null} for an output attribute means the resolution has no
   * {@code parserOutputPath} root from which to derive the output cascade.
   */
  public @Nullable Path path(@NotNull KnownAttribute<?> attribute) {
    List<Path> list = myPaths.get(attribute);
    return list == null || list.isEmpty() ? null : list.get(0);
  }

  /**
   * Every absolute on-disk path resolved for {@code attribute}, in declaration order. Empty
   * when the resolution has no value for it. Single-path attributes return a one-element list.
   */
  public @NotNull List<Path> paths(@NotNull KnownAttribute<?> attribute) {
    List<Path> list = myPaths.get(attribute);
    return list == null ? List.of() : list;
  }

  /**
   * Same as {@link #path} but as a string and never null — throws {@link IllegalStateException}
   * when {@code attribute} has no resolved value. Use this when the caller requires a directory
   * and a missing one is a programming error (e.g. emitting an output artifact whose owning
   * attribute must resolve to a directory).
   */
  public @NotNull String pathString(@NotNull KnownAttribute<?> attribute) {
    Path p = path(attribute);
    if (p == null) {
      throw new IllegalStateException(
        "No path resolved for attribute '" + attribute.getName() + "'. " +
        "For output attributes, ensure parserOutputPath is set on the resolution (it acts as the " +
        "fallback for psi/etHolder/syntax/converter cascades).");
    }
    return p.toString();
  }
}
