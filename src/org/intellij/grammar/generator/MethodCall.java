/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
