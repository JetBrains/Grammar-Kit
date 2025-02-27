/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.kotlin;

import org.jetbrains.annotations.NotNull;

public record KotlinPlatformConstants(
  @NotNull String SyntaxTreeBuilderClass
) {
  public static final @NotNull KotlinPlatformConstants DEFAULT_CONSTANTS = new KotlinPlatformConstants(
    KotlinBnfConstants.KT_BUILDER_CLASS
  );
}
