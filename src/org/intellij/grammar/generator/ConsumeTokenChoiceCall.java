/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.intellij.grammar.generator.ParserGeneratorUtil.ConsumeType;
import org.jetbrains.annotations.NotNull;

public class ConsumeTokenChoiceCall implements NodeCall {

  private final ConsumeType myConsumeType;
  private final String myTokenSetName;

  public ConsumeTokenChoiceCall(@NotNull ConsumeType type, @NotNull String name) {
    myConsumeType = type;
    myTokenSetName = name;
  }

  @NotNull
  @Override
  public String render(@NotNull Names names) {
    return String.format("%s(%s, %s)", myConsumeType.getMethodName(), names.builder, myTokenSetName);
  }
}
