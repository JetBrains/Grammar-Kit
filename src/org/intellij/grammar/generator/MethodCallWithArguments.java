/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.util.containers.ContainerUtil.map;

class MethodCallWithArguments implements NodeCall {

  private final @NotNull String myMethodName;
  private final @NotNull List<NodeArgument> myArguments;

  MethodCallWithArguments(@NotNull String methodName, @NotNull List<NodeArgument> arguments) {
    myMethodName = methodName;
    myArguments = Collections.unmodifiableList(arguments);
  }

  @NotNull
  String getMethodName() {
    return myMethodName;
  }

  @NotNull
  protected String getMethodRef() {
    return getMethodName();
  }

  @NotNull
  List<NodeArgument> getArguments() {
    return myArguments;
  }

  @NotNull
  List<String> getArgumentStrings() {
    return map(getArguments(), NodeArgument::render);
  }

  @NotNull
  @Override
  public String render(@NotNull Names names) {
    String arguments = getArgumentStrings().stream()
      .map(it -> ", " + it)
      .collect(Collectors.joining());
    return String.format("%s(%s, %s + 1%s)", getMethodRef(), names.builder, names.level, arguments);
  }
}
