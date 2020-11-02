/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.generator;

import org.jetbrains.annotations.NotNull;

public class MetaParameterArgument extends TextArgument {

  public MetaParameterArgument(@NotNull String text) {
    super(text);
  }

  @Override
  public boolean referencesMetaParameter() {
    return true;
  }
}
