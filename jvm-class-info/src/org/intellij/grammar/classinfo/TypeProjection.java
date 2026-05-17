/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import org.jetbrains.annotations.NotNull;

/**
 * A single generic-argument slot inside {@link JvmTypeRef.UserType#args}. Either a star projection
 * ({@code *} / {@code ?}) or a {@link JvmTypeRef} with a {@link Variance} marker that drives the
 * {@code ? extends X} / {@code ? super X} wildcard rendering on the JVM side.
 */
public sealed interface TypeProjection permits TypeProjection.Star, TypeProjection.WithVariance {

  /** {@code *} / {@code ?}. Carries no inner type. */
  record Star() implements TypeProjection { }

  /** Variance marker for a non-star projection. {@code INVARIANT} renders as the bare type. */
  enum Variance { INVARIANT, OUT, IN }

  /** {@code X} / {@code out X} / {@code in X}. The inner type carries its own annotations. */
  record WithVariance(@NotNull Variance variance, @NotNull JvmTypeRef type) implements TypeProjection { }
}
