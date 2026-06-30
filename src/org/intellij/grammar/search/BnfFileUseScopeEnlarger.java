/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.search;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UseScopeEnlarger;
import org.intellij.grammar.BnfFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enlarges the use scope of private declarations referenced from {@code .bnf} grammars
 * (parser-util methods, {@code psiImplUtilClass} methods, etc.) so that <em>Find Usages</em>
 * also searches BNF files. Public/internal members already have a project-wide use scope, so
 * only private declarations need the extra scope.
 *
 * @see BnfJavaFileUseScopeEnlarger
 * @see BnfKotlinFileUseScopeEnlarger
 */
public abstract class BnfFileUseScopeEnlarger extends UseScopeEnlarger {
  @Override
  public final @Nullable SearchScope getAdditionalUseScope(@NotNull PsiElement element) {
    PsiFile containingFile = element.getContainingFile();
    if (containingFile == null) return null;

    VirtualFile file = containingFile.getVirtualFile();
    if (file == null) return null;

    // check the genuine file type (handles substituted/overridden types), not the extension
    if (!FileTypeRegistry.getInstance().isFileOfType(file, getApplicableFileType())) return null;

    if (!isPrivateDeclaration(element)) return null;

    return GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.projectScope(element.getProject()), BnfFileType.INSTANCE);
  }

  protected abstract @NotNull FileType getApplicableFileType();

  protected abstract boolean isPrivateDeclaration(@NotNull PsiElement element);
}
