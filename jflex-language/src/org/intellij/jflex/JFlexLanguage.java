/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.jflex;

import com.intellij.lang.Language;
import org.intellij.grammar.GrammarKitBundle;
import org.jetbrains.annotations.NotNull;

public class JFlexLanguage extends Language {

  public static final JFlexLanguage INSTANCE = new JFlexLanguage();

  protected JFlexLanguage() {
    super("JFlex");
  }

  @Override
  public @NotNull String getDisplayName() {
    return GrammarKitBundle.message("language.name.jflex");
  }
}
