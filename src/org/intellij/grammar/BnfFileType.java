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

  @NotNull
  @Override
  public String getName() {
    return "BNF";
  }

  @NotNull
  @Override
  public String getDescription() {
    return BnfConstants.BNF_DISPLAY_NAME;
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "bnf";
  }

  @Override
  public Icon getIcon() {
    return BnfIcons.FILE;
  }

}
