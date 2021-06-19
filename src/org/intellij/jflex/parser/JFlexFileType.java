/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.parser;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import org.intellij.jflex.JFlexLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JFlexFileType extends LanguageFileType {

  public static final JFlexFileType INSTANCE = new JFlexFileType();

  protected JFlexFileType() {
    super(JFlexLanguage.INSTANCE);
  }

  @Override
  public @NotNull String getName() {
    return "JFlex";
  }

  @Override
  public @NotNull String getDescription() {
    return "JFlex lexer";
  }

  @Override
  public @NotNull String getDefaultExtension() {
    return "flex";
  }

  @Override
  public Icon getIcon() {
    return PlainTextFileType.INSTANCE.getIcon();
  }
}
