/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;

/**
 * Immutable snapshot of every {@code *InputPath} / {@code *OutputPath} resolution for a single
 * BNF file. Pure data holder — no inference, no cascade. The map passed in is treated as the
 * fully resolved view: {@link #path} and {@link #pathString} are direct map lookups.
 *
 * <p>Build via {@link BnfPaths#resolve(org.intellij.grammar.psi.BnfFile)} for a real file
 * (cached) or {@link BnfPaths#resolveExplicit(Map)} for a partial map (applies the
 * output-path cascade). Construct directly only when the map is already fully resolved.
 */
public final class BnfPathsResolution {
  public static final BnfPathsResolution EMPTY = new BnfPathsResolution(Map.of());

  private final Map<KnownAttribute<String>, Path> myPaths;

  public BnfPathsResolution(@NotNull Map<KnownAttribute<String>, Path> resolvedPaths) {
    myPaths = Map.copyOf(resolvedPaths);
  }

  /**
   * Effective absolute on-disk path for {@code attribute}, or {@code null} when the resolution
   * has no value for it.
   *
   * <p>A {@code null} for {@link KnownAttribute#INPUT_PATH} is the normal "no user-declared
   * input scope" signal for IDE-mode resolutions built via
   * {@link BnfPaths#resolve(org.intellij.grammar.psi.BnfFile)}: consumers with a {@code Project}
   * (e.g. {@link org.intellij.grammar.java.PsiHelperFactory}) fall back to a project-wide
   * search scope. CLI consumers without a {@code Project} should seed a default via
   * {@link BnfPaths#resolveExplicit(java.util.Map, Path)}.
   *
   * <p>A {@code null} for an output attribute means the resolution has no
   * {@code parserOutputPath} root from which to derive the output cascade.
   */
  public @Nullable Path path(@NotNull KnownAttribute<String> attribute) {
    return myPaths.get(attribute);
  }

  /**
   * Same as {@link #path} but as a string and never null — throws {@link IllegalStateException}
   * when {@code attribute} has no resolved value. Use this when the caller requires a directory
   * and a missing one is a programming error (e.g. emitting an output artifact whose owning
   * attribute must resolve to a directory).
   */
  public @NotNull String pathString(@NotNull KnownAttribute<String> attribute) {
    Path p = myPaths.get(attribute);
    if (p == null) {
      throw new IllegalStateException(
        "No path resolved for attribute '" + attribute.getName() + "'. " +
        "For output attributes, ensure parserOutputPath is set on the resolution (it acts as the " +
        "fallback for psi/etHolder/syntax/converter cascades).");
    }
    return p.toString();
  }
}
