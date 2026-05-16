/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable grammar-derived metadata for a BNF rule. Built once during
 * {@link GrammarInfo#build} and shared across all generator targets. PSI-only
 * mutable state (super-interface set, mixed-AST flag, resolved super-class)
 * lives in {@link PsiRuleInfo}, owned by {@link JavaPsiGenerator}.
 */
record RuleInfo(
  @NotNull String name,
  boolean isFake,
  @NotNull String elementType,
  @Nullable String parserClass,
  @Nullable String intfPackage,
  @Nullable String implPackage,
  @NotNull String intfClass,
  @NotNull String implClass,
  @Nullable String mixin,
  @Nullable String stub,
  @Nullable String realStubClass,
  boolean isAbstract,
  boolean isInElementType
) {
}
