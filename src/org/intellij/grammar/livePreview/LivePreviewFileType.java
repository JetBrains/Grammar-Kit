/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.livePreview;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.grammar.BnfIcons;
import org.intellij.grammar.GrammarKitBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author gregsh
 */
public class LivePreviewFileType extends LanguageFileType {
  public static final FileType INSTANCE = new LivePreviewFileType();

  protected LivePreviewFileType() {
    super(LivePreviewLanguage.BASE_INSTANCE);
  }

  @Override
  public @NotNull String getName() {
    return "BNF_LP";
  }

  @Override
  public @NotNull String getDescription() {
    return GrammarKitBundle.message("language.name.bnf.live.preview");
  }

  @Override
  public @NotNull String getDefaultExtension() {
    return "preview";
  }

  @Override
  public @Nullable Icon getIcon() {
    return BnfIcons.FILE;
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public @Nullable String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
    return null;
  }
}
