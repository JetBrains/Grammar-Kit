/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

class ExpressionMethodCall implements NodeCall {

  private final @NotNull String myMethodName;
  private final int myPriority;

  ExpressionMethodCall(@NotNull String name, int priority) {
    myMethodName = name;
    myPriority = priority;
  }

  @NotNull
  @Override
  public String render(@NotNull Names names) {
    return String.format("%s(%s, %s + 1, %d)", myMethodName, names.builder, names.level, myPriority);
  }
}
