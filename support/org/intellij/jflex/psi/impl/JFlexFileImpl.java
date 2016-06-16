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
