/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaMethodCall extends MethodCallWithArguments {

  private final @Nullable String myTargetClassName;

  MetaMethodCall(@Nullable String targetClassName, @NotNull String methodName, @NotNull List<NodeArgument> arguments) {
    super(methodName, arguments);
    myTargetClassName = targetClassName;
  }

  boolean referencesMetaParameter() {
    return getArguments().stream().anyMatch(NodeArgument::referencesMetaParameter);
  }

  @Nullable
  String getTargetClassName() {
    return myTargetClassName;
  }

  @NotNull
  @Override
  protected String getMethodRef() {
    String ref = super.getMethodRef();
    return myTargetClassName == null ? ref : String.format("%s.%s", myTargetClassName, ref);
  }
}
