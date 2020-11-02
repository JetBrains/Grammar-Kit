/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.intellij.jflex.JFlexLanguage;
import org.intellij.jflex.parser.JFlexFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class JFlexFileImpl extends PsiFileBase implements JFlexFile {
  public JFlexFileImpl(FileViewProvider fileViewProvider) {
    super(fileViewProvider, JFlexLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return JFlexFileType.INSTANCE;
  }
}
