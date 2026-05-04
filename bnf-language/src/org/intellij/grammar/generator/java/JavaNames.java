/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator.java;

import org.jetbrains.annotations.NotNull;

public final class JavaNames {
  public static @NotNull String getRawClassName(@NotNull String name) {
    return name.indexOf("<") < name.indexOf(">") ? name.substring(0, name.indexOf("<")) : name;
  }
}
