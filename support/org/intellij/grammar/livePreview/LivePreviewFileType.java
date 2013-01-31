package org.intellij.grammar.livePreview;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.grammar.BnfIcons;
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

  @NotNull
  @Override
  public String getName() {
    return "Grammar Live Preview";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Grammar Live Preview";
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
