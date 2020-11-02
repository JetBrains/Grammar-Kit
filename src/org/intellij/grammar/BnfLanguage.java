/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.lang.Language;
import org.intellij.grammar.generator.BnfConstants;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 22:44
 */
public class BnfLanguage extends Language {

  public static final BnfLanguage INSTANCE = new BnfLanguage();

  protected BnfLanguage() {
    super("BNF");
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return BnfConstants.BNF_DISPLAY_NAME;
  }
}
