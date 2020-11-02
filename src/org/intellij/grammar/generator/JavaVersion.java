/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum JavaVersion {
  JAVA_6,
  JAVA_8,
  ;

  @NotNull
  static JavaVersion fromString(@Nullable String s) {
    if ("6".equals(s)) return JAVA_6;
    if ("8".equals(s)) return JAVA_8;
    return JAVA_6;
  }
}
