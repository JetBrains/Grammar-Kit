/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author gregsh
 */
public enum Case {
  LOWER, UPPER, AS_IS, CAMEL;

  public @NotNull String apply(@NotNull String s) {
    if (s.isEmpty()) return s;
    return switch (this) {
      case LOWER -> s.toLowerCase(Locale.ENGLISH);
      case UPPER -> s.toUpperCase(Locale.ENGLISH);
      case AS_IS -> s;
      case CAMEL -> s.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                    s.substring(1).toLowerCase(Locale.ENGLISH);
    };
  }
}
