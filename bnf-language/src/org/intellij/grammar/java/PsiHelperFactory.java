/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.BnfPaths;
import org.intellij.grammar.BnfPathsResolution;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.java.syntax.JavaSyntaxHelper;
import org.intellij.grammar.java.syntax.KotlinSyntaxHelper;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

/**
 * Project-level service that builds {@link JavaHelper} instances tailored to a given context.
 * <p>
 * The helper's class-lookup scope is decided once at construction time by walking the context
 * to find an enclosing {@link BnfAttr} and consulting the cached {@link BnfPathsResolution} for
 * the file. The resolution applies the full {@code *InputPath} / {@code *OutputPath} cascade
 * (see {@link BnfPaths#referencePath}), including the global {@code inputPath} fallback for
 * input attributes.
 * <p>
 * By default returns {@link PsiHelper} (PSI-backed, scoped to the resolved directory). The
 * {@code grammar.kit.psi.helper.use.syntax} registry flag swaps the implementation to a source-file
 * chain {@link KotlinSyntaxHelper} → {@link JavaSyntaxHelper} → {@link AsmHelper}, rooted at the
 * resolved directory — used to drive headless-style resolution in IDE tests.
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
    Path dir = referenceDir(context);
    if (useSyntaxHelper()) {
      return buildSyntaxHelper(dir);
    }
    return new PsiHelper(myProject, scopeFor(dir));
  }

  /**
   * Returns a {@link JavaHelper} whose class-lookup scope is decided by
   * {@link BnfPaths#referencePath}{@code (paths, fqnAttribute)}. Use this from contexts that
   * already hold a fully-resolved {@link BnfPathsResolution} (e.g. the build-time
   * {@code Generator}) to avoid re-resolving via the PSI cache, which may not reflect CLI
   * overrides or in-flight test fixtures.
   */
  public @NotNull JavaHelper getInstance(@NotNull BnfPathsResolution paths,
                                        @Nullable KnownAttribute<?> fqnAttribute) {
    Path dir = BnfPaths.referencePath(paths, fqnAttribute);
    if (useSyntaxHelper()) {
      return buildSyntaxHelper(dir);
    }
    return new PsiHelper(myProject, scopeFor(dir));
  }

  private static boolean useSyntaxHelper() {
    return Registry.is("grammar.kit.psi.helper.use.syntax", false);
  }

  private static @NotNull JavaHelper buildSyntaxHelper(@Nullable Path dir) {
    AsmHelper asm = new AsmHelper();
    if (dir == null) return asm;
    List<Path> roots = List.of(dir);
    return new KotlinSyntaxHelper(roots, new JavaSyntaxHelper(roots, asm));
  }

  private static @Nullable Path referenceDir(@NotNull PsiElement context) {
    BnfFile bnfFile = context.getContainingFile() instanceof BnfFile f ? f : null;
    if (bnfFile == null) return null;
    BnfAttr enclosingAttr = PsiTreeUtil.getParentOfType(context, BnfAttr.class, false);
    KnownAttribute<?> fqnAttribute = enclosingAttr == null ? null : KnownAttribute.getAttribute(enclosingAttr.getName());
    BnfPathsResolution paths = BnfPaths.resolve(bnfFile);
    return BnfPaths.referencePath(paths, fqnAttribute);
  }

  private @Nullable GlobalSearchScope scopeFor(@Nullable Path dir) {
    if (dir == null) return null;
    VirtualFile vf = VirtualFileManager.getInstance().findFileByNioPath(dir);
    return vf == null ? null : GlobalSearchScopesCore.directoriesScope(myProject, true, vf);
  }
}
