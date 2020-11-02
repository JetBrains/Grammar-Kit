/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

class ConsumeTokensCall implements NodeCall {

  private final @NotNull String myMethodName;
  private final int myPin;
  private final @NotNull List<String> myTokens;

  ConsumeTokensCall(@NotNull String methodName, int pin, @NotNull List<String> tokens) {
    myMethodName = methodName;
    myPin = pin;
    myTokens = Collections.unmodifiableList(tokens);
  }

  @NotNull
  @Override
  public String render(@NotNull Names names) {
    return String.format("%s(%s, %d, %s)", myMethodName, names.builder, myPin, StringUtil.join(myTokens, ", "));
  }
}
