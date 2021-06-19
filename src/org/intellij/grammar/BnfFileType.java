/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.intellij.grammar.generator.BnfConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 22:46
 */
public class BnfFileType extends LanguageFileType {

  public static final BnfFileType INSTANCE = new BnfFileType();

  protected BnfFileType() {
    super(BnfLanguage.INSTANCE);
  }

  @Override
  public @NotNull String getName() {
    return "BNF";
  }

  @Override
  public @NotNull String getDescription() {
    return BnfConstants.BNF_DISPLAY_NAME;
  }

  @Override
  public @NotNull String getDefaultExtension() {
    return "bnf";
  }

  @Override
  public Icon getIcon() {
    return BnfIcons.FILE;
  }

}
