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
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.intellij.grammar.classinfo.asm.AsmClassSymbolProvider;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
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
 * {@link JvmSyntaxHelper} over a Kotlin-syntax / Java-syntax / ASM provider list, rooted at the
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
   *
   * <p>When the resolution has no path for {@code fqnAttribute} — typically because the grammar
   * declares no {@code inputPath} and the attribute is not bound to a known output sibling —
   * the helper is scoped to {@link GlobalSearchScope#allScope}, i.e. the entire project plus
   * libraries and SDK.
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
    if (dir == null) {
      return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(new AsmClassSymbolProvider())));
    }
    List<Path> roots = List.of(dir);
    SyntaxTreeCache treeCache = new SyntaxTreeCache();
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(
      new KotlinSyntaxClassSymbolProvider(roots, treeCache),
      new JavaSyntaxClassSymbolProvider(roots, treeCache),
      new AsmClassSymbolProvider())));
  }

  private static @Nullable Path referenceDir(@NotNull PsiElement context) {
    BnfFile bnfFile = context.getContainingFile() instanceof BnfFile f ? f : null;
    if (bnfFile == null) return null;
    BnfAttr enclosingAttr = PsiTreeUtil.getParentOfType(context, BnfAttr.class, false);
    KnownAttribute<?> fqnAttribute = enclosingAttr == null ? null : KnownAttribute.getAttribute(enclosingAttr.getName());
    BnfPathsResolution paths = BnfPaths.resolve(bnfFile);
    return BnfPaths.referencePath(paths, fqnAttribute);
  }

  /**
   * Translates a resolved input directory into a class-lookup scope. A non-null {@code dir}
   * narrows lookup to that subtree (the explicit {@code *InputPath} declaration). When the
   * resolution carries no input path — i.e. the grammar declared none — we widen to
   * {@link GlobalSearchScope#allScope project + libraries + SDK} so references to classes
   * outside the BNF parent (sibling modules, library bases, etc.) still resolve.
   */
  private @NotNull GlobalSearchScope scopeFor(@Nullable Path dir) {
    if (dir == null) return GlobalSearchScope.allScope(myProject);
    VirtualFile vf = VirtualFileManager.getInstance().findFileByNioPath(dir);
    if (vf == null) return GlobalSearchScope.allScope(myProject);
    return GlobalSearchScopesCore.directoriesScope(myProject, true, vf);
  }
}
