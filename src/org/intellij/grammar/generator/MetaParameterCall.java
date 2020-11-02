/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

class MetaParameterCall implements NodeCall {

  private final @NotNull String myMetaParameterName;

  MetaParameterCall(@NotNull String metaParameterName) {
    myMetaParameterName = metaParameterName;
  }

  @NotNull
  @Override
  public String render(@NotNull Names names) {
    return String.format("%s.parse(%s, %s)", myMetaParameterName, names.builder, names.level);
  }
}
