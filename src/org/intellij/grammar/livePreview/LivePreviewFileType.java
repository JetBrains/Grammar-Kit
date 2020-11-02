/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.grammar.BnfIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static org.intellij.grammar.generator.BnfConstants.LP_DISPLAY_NAME;

/**
 * @author gregsh
 */
public class LivePreviewFileType extends LanguageFileType {
  public static final FileType INSTANCE = new LivePreviewFileType();

  protected LivePreviewFileType() {
    super(LivePreviewLanguage.BASE_INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "BNF_LP";
  }

  @NotNull
  @Override
  public String getDescription() {
    return LP_DISPLAY_NAME;
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "preview";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return BnfIcons.FILE;
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Nullable
  @Override
  public String getCharset(@NotNull VirtualFile file, byte[] content) {
    return null;
  }
}
