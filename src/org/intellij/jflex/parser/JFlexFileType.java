/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.parser;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import org.intellij.jflex.JFlexLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JFlexFileType extends LanguageFileType {

  public static final JFlexFileType INSTANCE = new JFlexFileType();

  protected JFlexFileType() {
    super(JFlexLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "JFlex";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "JFlex lexer";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "flex";
  }

  @Override
  public Icon getIcon() {
    return StdFileTypes.PLAIN_TEXT.getIcon();
  }
}
