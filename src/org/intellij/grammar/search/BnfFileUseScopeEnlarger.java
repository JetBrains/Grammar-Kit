/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.search;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UseScopeEnlarger;
import org.intellij.grammar.BnfFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BnfFileUseScopeEnlarger extends UseScopeEnlarger {
  @Override
  public @Nullable SearchScope getAdditionalUseScope(@NotNull PsiElement element) {
    VirtualFile file = element.getContainingFile().getVirtualFile();
    if ("java".equals(file.getExtension()) || "kt".equals(file.getExtension())){
      return GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(element.getProject()), BnfFileType.INSTANCE);
    }
    return null;
  }
}
