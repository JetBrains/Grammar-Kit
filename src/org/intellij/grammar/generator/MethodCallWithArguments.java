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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class MethodCallWithArguments implements NodeCall {

  private final @NotNull String myMethodName;
  private final @NotNull List<NodeArgument> myArguments;

  MethodCallWithArguments(@NotNull String methodName, @NotNull List<NodeArgument> arguments) {
    myMethodName = methodName;
    myArguments = Collections.unmodifiableList(arguments);
  }

  @NotNull
  List<NodeArgument> getArguments() {
    return myArguments;
  }

  @NotNull
  @Override
  public String render(@NotNull Names names) {
    String arguments = getArguments().stream()
      .map(NodeArgument::render)
      .map(it -> ", " + it)
      .collect(Collectors.joining());
    return String.format("%s(%s, %s + 1%s)", myMethodName, names.builder, names.level, arguments);
  }
}
