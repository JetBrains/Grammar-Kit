/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.mock.MockProject;
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
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.ExtraClassSymbolProvider;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.intellij.grammar.classinfo.asm.AsmClassSymbolProvider;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project-level service that builds {@link JavaHelper} instances tailored to a given context.
 * <p>
 * The helper's class-lookup scope is decided once at construction time by consulting the
 * {@link BnfPathsResolution} associated with the caller and applying the full
 * {@code *InputPath} / {@code *OutputPath} cascade via {@link BnfPaths#referencePaths}.
 * <p>
 * Two entry points cover the supported flavours:
 * <ul>
 *   <li>{@link #getInstance(PsiElement)} — convenience for IDE call-sites that only hold a
 *       {@link PsiElement}. Resolves paths from the enclosing {@link BnfFile} on every call.</li>
 *   <li>{@link #scoped(BnfPathsResolution)} / {@link #scoped(BnfPathsResolution, ExtraClasses)} —
 *       canonical source-scope API for callers that already hold a fully-resolved
 *       {@link BnfPathsResolution} (the build-time {@code Generator}). The returned
 *       {@link ScopedHelpers} is immutable and caches per-attribute helpers internally.</li>
 * </ul>
 * Headless callers (those running under a {@link MockProject}) always receive a source-backed
 * {@link JvmSyntaxHelper}; IDE callers receive a {@link PsiHelper} by default, or a
 * {@code JvmSyntaxHelper} when the {@code grammar.kit.psi.helper.use.syntax} registry flag is set.
 */
@Service(Service.Level.PROJECT)
public final class JavaHelperFactory {

  /**
   * Immutable per-attribute view of a {@link BnfPathsResolution}-scoped {@link JavaHelper}.
   * Calls with the same attribute return the same instance. No mutators: to expose additional
   * classes, build a new {@code ScopedHelpers} from a fresh {@link ExtraClasses}.
   */
  public interface ScopedHelpers {
    @NotNull JavaHelper get(@Nullable KnownAttribute<?> attribute);
  }

  /**
   * Immutable bundle of pre-built {@link ClassSymbol}s to make visible to a {@link ScopedHelpers}.
   * Entries shadow same-FQN classes from on-disk providers — used by the generator to surface
   * not-yet-emitted PSI interfaces and impls.
   */
  public static final class ExtraClasses {

    private static final ExtraClasses EMPTY = new ExtraClasses(Collections.emptyMap());

    private final Map<Fqn, ClassSymbol> byFqn;

    private ExtraClasses(@NotNull Map<Fqn, ClassSymbol> byFqn) {
      this.byFqn = byFqn;
    }

    public ExtraClasses(@NotNull Collection<? extends ClassSymbol> classes) {
      Map<Fqn, ClassSymbol> m = new HashMap<>();
      for (ClassSymbol s : classes) m.put(s.name(), s);
      this.byFqn = Map.copyOf(m);
    }

    public static @NotNull ExtraClasses empty() {
      return EMPTY;
    }

    public boolean isEmpty() {
      return byFqn.isEmpty();
    }

    @NotNull Map<Fqn, ClassSymbol> byFqn() {
      return byFqn;
    }
  }

  private final Project myProject;

  public JavaHelperFactory(@NotNull Project project) {
    myProject = project;
  }

  public static @NotNull JavaHelperFactory getInstance(@NotNull Project project) {
    return project.getService(JavaHelperFactory.class);
  }

  /**
   * Returns a {@link JavaHelper} whose class-lookup scope is derived from the enclosing
   * {@link BnfAttr}'s {@code *InputPath} sibling, anchored at {@code context}'s position in the
   * BNF tree. Convenience for IDE call-sites that only hold a {@link PsiElement} — for build-time
   * callers prefer {@link #scoped(BnfPathsResolution)}.
   */
  public @NotNull JavaHelper getInstance(@NotNull PsiElement context) {
    List<Path> dirs = referenceDirs(context);
    return buildHelper(dirs, ExtraClasses.empty());
  }

  /** Source-scope accessor without any extra synthesised classes. */
  public @NotNull ScopedHelpers scoped(@NotNull BnfPathsResolution paths) {
    return scoped(paths, ExtraClasses.empty());
  }

  /**
   * Source-scope accessor with {@code extras} pinned at the head of the provider chain — these
   * classes shadow on-disk versions and satisfy lookups for classes that aren't on disk yet.
   */
  public @NotNull ScopedHelpers scoped(@NotNull BnfPathsResolution paths, @NotNull ExtraClasses extras) {
    return new ScopedHelpersImpl(paths, extras);
  }

  private final class ScopedHelpersImpl implements ScopedHelpers {
    private final BnfPathsResolution paths;
    private final ExtraClasses extras;
    private final Map<KnownAttribute<?>, JavaHelper> cache = new HashMap<>();
    private @Nullable JavaHelper nullAttrHelper;

    ScopedHelpersImpl(@NotNull BnfPathsResolution paths, @NotNull ExtraClasses extras) {
      this.paths = paths;
      this.extras = extras;
    }

    @Override
    public @NotNull JavaHelper get(@Nullable KnownAttribute<?> attribute) {
      if (attribute == null) {
        if (nullAttrHelper == null) {
          List<Path> dirs = BnfPaths.referencePaths(paths, null);
          nullAttrHelper = buildHelper(dirs, extras);
        }
        return nullAttrHelper;
      }
      return cache.computeIfAbsent(attribute, a -> buildHelper(BnfPaths.referencePaths(paths, a), extras));
    }
  }

  private @NotNull JavaHelper buildHelper(@NotNull List<Path> dirs, @NotNull ExtraClasses extras) {
    if (useSyntaxHelper()) {
      try {
        List<JvmClassSymbolProvider> providers = syntaxProviders(dirs, extras);
        JvmClassSymbolManager symbolManager = new JvmClassSymbolManager(providers);
        return new JvmSyntaxHelper(symbolManager);
      }
      catch (LinkageError e) {
        return new ReflectionHelper();
      }
    }

    return new PsiHelper(myProject, scopeFor(dirs), new JvmClassSymbolManager(psiFallbackProviders(extras)));
  }

  private boolean useSyntaxHelper() {
    if (myProject instanceof MockProject) return true;
    try {
      return Registry.is("grammar.kit.psi.helper.use.syntax", false);
    }
    catch (Exception ignored) {
      return false;
    }
  }

  private static @NotNull List<JvmClassSymbolProvider> syntaxProviders(@NotNull List<Path> dirs, @NotNull ExtraClasses extras) {
    List<JvmClassSymbolProvider> providers = new ArrayList<>();
    if (!extras.isEmpty()) {
      providers.add(new ExtraClassSymbolProvider(extras.byFqn()));
    }
    if (!dirs.isEmpty()) {
      SyntaxTreeCache treeCache = new SyntaxTreeCache();
      providers.add(new KotlinSyntaxClassSymbolProvider(dirs, treeCache));
      providers.add(new JavaSyntaxClassSymbolProvider(dirs, treeCache));
    }
    providers.add(new AsmClassSymbolProvider());
    return providers;
  }

  private static @NotNull List<JvmClassSymbolProvider> psiFallbackProviders(@NotNull ExtraClasses extras) {
    List<JvmClassSymbolProvider> providers = new ArrayList<>();
    if (!extras.isEmpty()) {
      providers.add(new ExtraClassSymbolProvider(extras.byFqn()));
    }
    providers.add(new AsmClassSymbolProvider());
    return providers;
  }

  private static @NotNull List<Path> referenceDirs(@NotNull PsiElement context) {
    BnfFile bnfFile = context.getContainingFile() instanceof BnfFile f ? f : null;
    if (bnfFile == null) return List.of();
    BnfAttr enclosingAttr = PsiTreeUtil.getParentOfType(context, BnfAttr.class, false);
    KnownAttribute<?> fqnAttribute = enclosingAttr == null ? null : KnownAttribute.getAttribute(enclosingAttr.getName());
    BnfPathsResolution paths = BnfPaths.resolve(bnfFile);
    return BnfPaths.referencePaths(paths, fqnAttribute);
  }

  /**
   * Translates a resolved set of input directories into a class-lookup scope. A non-empty list
   * narrows lookup to those subtrees (the explicit {@code *InputPath} declaration). When the
   * resolution carries no input path — i.e. the grammar declared none — we widen to
   * {@link GlobalSearchScope#allScope project + libraries + SDK} so references to classes
   * outside the BNF parent (sibling modules, library bases, etc.) still resolve.
   */
  private @NotNull GlobalSearchScope scopeFor(@NotNull List<Path> dirs) {
    if (dirs.isEmpty()) return GlobalSearchScope.allScope(myProject);
    VirtualFileManager vfm = VirtualFileManager.getInstance();
    List<VirtualFile> vfs = new ArrayList<>(dirs.size());
    for (Path dir : dirs) {
      VirtualFile vf = vfm.findFileByNioPath(dir);
      if (vf != null) vfs.add(vf);
    }
    if (vfs.isEmpty()) return GlobalSearchScope.allScope(myProject);
    return GlobalSearchScopesCore.directoriesScope(myProject, true, vfs.toArray(VirtualFile.EMPTY_ARRAY));
  }
}
