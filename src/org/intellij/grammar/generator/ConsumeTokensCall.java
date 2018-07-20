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
