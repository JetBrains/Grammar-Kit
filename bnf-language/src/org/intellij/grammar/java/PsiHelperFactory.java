/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Project-level service that builds {@link PsiHelper} instances tailored to a given context.
 * <p>
 * The helper's class-lookup scope is decided once at construction time by walking the context
 * to find an enclosing {@link BnfAttr} and consulting the cached {@link BnfPathsResolution} for
 * the file. The resolution applies the full {@code *InputPath} / {@code *OutputPath} cascade
 * (see {@link BnfPaths#referencePath}), including the global {@code inputPath} fallback for
 * input attributes.
 */
@Service(Service.Level.PROJECT)
public final class PsiHelperFactory {
  private final Project myProject;

  public PsiHelperFactory(@NotNull Project project) {
    myProject = project;
  }

  public static @NotNull PsiHelperFactory getInstance(@NotNull Project project) {
    return project.getService(PsiHelperFactory.class);
  }

  public @NotNull JavaHelper getInstance(@NotNull PsiElement context) {
    return new PsiHelper(myProject, computeReferenceScope(context));
  }

  /**
   * Returns a {@link PsiHelper} whose class-lookup scope is decided by
   * {@link BnfPaths#referencePath}{@code (paths, fqnAttribute)}. Use this from contexts that
   * already hold a fully-resolved {@link BnfPathsResolution} (e.g. the build-time
   * {@code Generator}) to avoid re-resolving via the PSI cache, which may not reflect CLI
   * overrides or in-flight test fixtures.
   */
  public @NotNull JavaHelper getInstance(@NotNull BnfPathsResolution paths,
                                        @Nullable KnownAttribute<?> fqnAttribute) {
    Path dir = BnfPaths.referencePath(paths, fqnAttribute);
    GlobalSearchScope scope = scopeFor(dir);
    return new PsiHelper(myProject, scope);
  }

  private @Nullable GlobalSearchScope computeReferenceScope(@NotNull PsiElement context) {
    BnfFile bnfFile = context.getContainingFile() instanceof BnfFile f ? f : null;
    if (bnfFile == null) return null;

    BnfAttr enclosingAttr = PsiTreeUtil.getParentOfType(context, BnfAttr.class, false);
    KnownAttribute<?> fqnAttribute = enclosingAttr == null ? null : KnownAttribute.getAttribute(enclosingAttr.getName());
    BnfPathsResolution paths = BnfPaths.resolve(bnfFile);
    return scopeFor(BnfPaths.referencePath(paths, fqnAttribute));
  }

  private @Nullable GlobalSearchScope scopeFor(@Nullable Path dir) {
    if (dir == null) return null;
    VirtualFile vf = VirtualFileManager.getInstance().findFileByNioPath(dir);
    return vf == null ? null : GlobalSearchScopesCore.directoriesScope(myProject, true, vf);
  }
}
