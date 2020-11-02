/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

class MethodCall implements NodeCall {

  private final boolean myRenderClass;
  private final @NotNull String myClassName;
  private final @NotNull String myMethodName;

  MethodCall(boolean renderClass, @NotNull String className, @NotNull String methodName) {
    myRenderClass = renderClass;
    myClassName = className;
    myMethodName = methodName;
  }

  @NotNull
  String getMethodName() {
    return myMethodName;
  }

  @NotNull
  String getClassName() {
    return myClassName;
  }

  @NotNull
  public String render(@NotNull Names names) {
    if (myRenderClass) {
      return String.format("%s.%s(%s, %s + 1)", myClassName, myMethodName, names.builder, names.level);
    }
    else {
      return String.format("%s(%s, %s + 1)", myMethodName, names.builder, names.level);
    }
  }
}
