/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

import static org.intellij.grammar.generator.ParserGeneratorUtil.getWrapperParserMetaMethodName;

class MetaMethodCallArgument implements NodeArgument {

  private final @NotNull MetaMethodCall myCall;

  MetaMethodCallArgument(@NotNull MetaMethodCall call) {
    myCall = call;
  }

  @Override
  public boolean referencesMetaParameter() {
    return true;
  }

  @NotNull
  private String getMethodRef() {
    String ref = getWrapperParserMetaMethodName(myCall.getMethodName());
    String className = myCall.getTargetClassName();
    return className == null ? ref : String.format("%s.%s", className, ref);
  }

  @NotNull
  @Override
  public String render() {
    String arguments = String.join(", ", myCall.getArgumentStrings());
    return String.format("%s(%s)", getMethodRef(), arguments);
  }
}
