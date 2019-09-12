/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
