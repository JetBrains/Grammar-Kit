/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Key;
import org.intellij.grammar.KnownAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IAttributePostProcessor {
  Key<IAttributePostProcessor> ATTRIBUTE_POSTPROCESSOR = Key.create("ATTRIBUTE_POSTPROCESSOR");

  @Nullable
  default <T> T postProcessValue(@NotNull KnownAttribute<T> knownAttribute, @Nullable T value) {
    return null;
  }
}
