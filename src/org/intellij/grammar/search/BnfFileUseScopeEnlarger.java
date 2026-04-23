/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.search;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UseScopeEnlarger;
import org.intellij.grammar.BnfFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BnfFileUseScopeEnlarger extends UseScopeEnlarger {
  @Override
  public @Nullable SearchScope getAdditionalUseScope(@NotNull PsiElement element) {
    PsiFile containingFile = element.getContainingFile();
    if (containingFile == null) return null;

    VirtualFile file = containingFile.getVirtualFile();
    if (file == null) return null;

    String extension = file.getExtension();
    if (!"java".equals(extension) && !"kt".equals(extension)) {
      return null;
    }
    return GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(element.getProject()), BnfFileType.INSTANCE);
  }
}
