/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.config.Options;
import org.intellij.grammar.psi.BnfFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.intellij.grammar.psi.BnfAttributes.getRootAttribute;

/**
 * Single source of truth for the {@code *InputPath} and {@code *OutputPath} BNF attributes:
 * the set of every path attribute, the FQN-attribute ↔ path-attribute pairings, and the
 * BNF-file-parent-relative resolution rules used by every consumer (the IDE's
 * {@code PsiHelperFactory}, the build-time {@code BnfGenerationService}, and the inlay-hints
 * provider).
 *
 * <p>Path values are always resolved relative to the BNF file's parent directory.
 */
public final class BnfPaths {
  private BnfPaths() {
  }

  /** Output-path attributes, in declaration order. Each one represents a directory the
   * generator writes to and that callers (CLI, batch service) may need to materialize on disk. */
  public static final List<KnownAttribute<String>> OUTPUTS = List.of(
    KnownAttribute.PARSER_OUTPUT_PATH,
    KnownAttribute.PSI_OUTPUT_PATH,
    KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH,
    KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH,
    KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH);

  /** Input-path attributes, in declaration order. */
  public static final List<KnownAttribute<String>> INPUTS = List.of(
    KnownAttribute.INPUT_PATH,
    KnownAttribute.PSI_INPUT_PATH);

  /** Every path-valued attribute (input + output), in declaration order. */
  public static final List<KnownAttribute<String>> ALL = ContainerUtil.concat(INPUTS, OUTPUTS);

  /**
   * FQN-attribute → its corresponding {@code *InputPath} sibling. {@code parserUtilClass} has
   * no specific input override and falls through to the global {@code inputPath}.
   */
  public static final Map<KnownAttribute<?>, KnownAttribute<String>> INPUT_FOR = Map.of(
    KnownAttribute.PSI_IMPL_UTIL_CLASS,  KnownAttribute.PSI_INPUT_PATH,
    KnownAttribute.MIXIN,                KnownAttribute.PSI_INPUT_PATH,
    KnownAttribute.IMPLEMENTS,           KnownAttribute.PSI_INPUT_PATH);

  /** FQN-attribute → its corresponding {@code *OutputPath} sibling. */
  public static final Map<KnownAttribute<?>, KnownAttribute<String>> OUTPUT_FOR = Map.ofEntries(
    Map.entry(KnownAttribute.PARSER_CLASS,                         KnownAttribute.PARSER_OUTPUT_PATH),
    Map.entry(KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS,            KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH),
    Map.entry(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_CLASS,     KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH),
    Map.entry(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_CLASS, KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH),
    Map.entry(KnownAttribute.PSI_PACKAGE,                          KnownAttribute.PSI_OUTPUT_PATH),
    Map.entry(KnownAttribute.PSI_IMPL_PACKAGE,                     KnownAttribute.PSI_OUTPUT_PATH));

  private static final Map<String, KnownAttribute<String>> BY_NAME = buildByName();

  private static Map<String, KnownAttribute<String>> buildByName() {
    Map<String, KnownAttribute<String>> map = new LinkedHashMap<>();
    for (KnownAttribute<String> a : ALL) map.put(a.getName(), a);
    return Map.copyOf(map);
  }

  /** Looks up a path attribute by its BNF-source name; null when {@code name} is not a path attribute. */
  public static @Nullable KnownAttribute<String> pathAttributeByName(@Nullable String name) {
    return name == null ? null : BY_NAME.get(name);
  }

  /**
   * Resolves {@code relativePath} on disk against the BNF file's parent directory. Pure path
   * arithmetic; does not touch the VFS or create any directories.
   */
  public static @Nullable Path resolveOnDisk(@NotNull BnfFile bnfFile, @NotNull String relativePath) {
    VirtualFile bnfVf = bnfFile.getOriginalFile().getVirtualFile();
    if (bnfVf == null) return null;
    VirtualFile parent = bnfVf.getParent();
    if (parent == null) return null;
    try {
      return Paths.get(parent.getPath()).resolve(relativePath).normalize();
    }
    catch (InvalidPathException e) {
      return null;
    }
  }

  /**
   * Returns the cached {@link BnfPathsResolution} for {@code bnfFile}: every path attribute
   * resolved against the BNF file's parent, with the parser-class-package fallback for
   * {@code parserOutputPath} and the full output-path cascade applied.
   *
   * <p>When the grammar declares no {@code inputPath}, the resolution leaves
   * {@link KnownAttribute#INPUT_PATH} unset rather than defaulting it to the BNF file's parent.
   * IDE-side consumers ({@link org.intellij.grammar.java.PsiHelperFactory}) treat the missing
   * input path as "no user-declared scope" and fall back to a project-wide search scope; CLI
   * consumers without a {@code Project} seed the default themselves via
   * {@link #resolveExplicit(Map, Path)}.
   *
   * <p>Cached via {@link CachedValuesManager}, invalidated by
   * {@link PsiModificationTracker#MODIFICATION_COUNT}. Read-only — never creates directories.
   */
  public static @NotNull BnfPathsResolution resolve(@NotNull BnfFile bnfFile) {
    return CachedValuesManager.getCachedValue(bnfFile, () -> CachedValueProvider.Result.create(
      compute(bnfFile),
      PsiModificationTracker.MODIFICATION_COUNT));
  }

  /**
   * Builds a {@link BnfPathsResolution} from a partial map of explicit path values, applying
   * the same input/output cascade used for real BNF files (see {@link #applyCascade}). Use
   * this from build-time/CLI/test entry points that have already resolved paths from non-PSI
   * sources (e.g. command-line flags).
   */
  public static @NotNull BnfPathsResolution resolveExplicit(@NotNull Map<KnownAttribute<String>, Path> explicit) {
    return new BnfPathsResolution(applyCascade(explicit));
  }

  /**
   * CLI variant of {@link #resolveExplicit(Map)} that also seeds a default for
   * {@link KnownAttribute#INPUT_PATH}: when neither the CLI nor the grammar provided one, it
   * defaults to {@code bnfParent}. Mirrors the default applied by {@link #compute} for IDE
   * editing contexts so headless runs and IDE views agree on the input scope.
   */
  public static @NotNull BnfPathsResolution resolveExplicit(@NotNull Map<KnownAttribute<String>, Path> explicit,
                                                            @NotNull Path bnfParent) {
    Map<KnownAttribute<String>, Path> seeded = new HashMap<>(explicit);
    seeded.putIfAbsent(KnownAttribute.INPUT_PATH, bnfParent);
    return new BnfPathsResolution(applyCascade(seeded));
  }

  private static @NotNull BnfPathsResolution compute(@NotNull BnfFile bnfFile) {
    VirtualFile bnfVf = bnfFile.getOriginalFile().getVirtualFile();
    VirtualFile bnfParent = bnfVf == null ? null : bnfVf.getParent();
    if (bnfParent == null) {
      return BnfPathsResolution.EMPTY;
    }

    Path parentPath;
    try {
      parentPath = Paths.get(bnfParent.getPath());
    }
    catch (InvalidPathException e) {
      return BnfPathsResolution.EMPTY;
    }

    Map<KnownAttribute<String>, Path> resolved = collectExplicitPaths(bnfFile, parentPath);

    // parserOutputPath fallback: derive from parserClass package via read-only inference.
    if (!resolved.containsKey(KnownAttribute.PARSER_OUTPUT_PATH)) {
      Path inferred = findParserPathInProject(bnfFile, bnfVf);
      if (inferred != null) resolved.put(KnownAttribute.PARSER_OUTPUT_PATH, inferred);
    }

    // No inputPath default in IDE mode: PsiHelperFactory falls back to a project-wide search
    // scope when the resolution has no inputPath. The CLI overload of resolveExplicit seeds
    // bnfParent itself because standalone runs have no Project to fall back to.

    Map<KnownAttribute<String>, Path> effectivePaths = applyCascade(resolved);
    return new BnfPathsResolution(effectivePaths);
  }

  /**
   * Reads every {@link #ALL} path attribute from {@code bnfFile} and resolves each non-empty
   * value relative to {@code bnfParent}. Pure path arithmetic — does not touch the VFS or
   * create any directories. Returns a fresh mutable map so callers can layer their own
   * fallbacks (e.g. a CLI {@code <output-dir>} default for {@code parserOutputPath}) before
   * passing the result to {@link #resolveExplicit}.
   */
  public static @NotNull Map<KnownAttribute<String>, Path> collectExplicitPaths(
    @NotNull BnfFile bnfFile, @NotNull Path bnfParent) {
    Map<KnownAttribute<String>, Path> resolved = new HashMap<>();
    for (KnownAttribute<String> attr : ALL) {
      String value = getRootAttribute(bnfFile, attr);
      if (StringUtil.isEmpty(value)) continue;
      try {
        resolved.put(attr, bnfParent.resolve(value).normalize());
      }
      catch (InvalidPathException ignored) {
      }
    }
    return resolved;
  }

  /**
   * Bakes both the input- and output-path fallback chains into {@code explicit}, in one pass,
   * so direct lookups via {@link BnfPathsResolution#path} agree with the on-the-fly cascade
   * computed by {@link #referencePath}.
   *
   * <p><b>Input cascade.</b> When {@link KnownAttribute#INPUT_PATH global inputPath} is set,
   * an unset {@code psiInputPath} inherits its value. When {@code inputPath} is unset, no
   * input default is applied.
   *
   * <p><b>Output cascade.</b> Two-level chain rooted at {@code parserOutputPath}:
   * {@code psiOutputPath} → {@code parserOutputPath}; element-type artifacts
   * ({@code elementTypeHolderOutputPath}, {@code syntaxElementTypeHolderOutputPath},
   * {@code elementTypeConverterFactoryOutputPath}) → effective {@code psiOutputPath} (which
   * itself may be the parser dir if PSI is unset). Element-type artifacts travel with PSI
   * rather than the parser, which matters when parser and PSI live in different source roots
   * (e.g. Kotlin parser + Java PSI). When {@code parserOutputPath} is unset, no output
   * default is applied.
   *
   * <p>Input and output cascades touch disjoint key sets, so their order does not matter.
   */
  private static @NotNull Map<KnownAttribute<String>, Path> applyCascade(@NotNull Map<KnownAttribute<String>, Path> explicit) {
    Map<KnownAttribute<String>, Path> next = new HashMap<>(explicit);

    Path inputGlobal = next.get(KnownAttribute.INPUT_PATH);
    if (inputGlobal != null) {
      next.putIfAbsent(KnownAttribute.PSI_INPUT_PATH, inputGlobal);
    }

    Path parser = next.get(KnownAttribute.PARSER_OUTPUT_PATH);
    if (parser != null) {
      next.putIfAbsent(KnownAttribute.PSI_OUTPUT_PATH, parser);
      Path psi = next.get(KnownAttribute.PSI_OUTPUT_PATH);
      next.putIfAbsent(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH, psi);
      next.putIfAbsent(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH, psi);
      next.putIfAbsent(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH, psi);
    }

    return Map.copyOf(next);
  }

  /**
   * Cascade lookup keyed on a class-reference FQN attribute (e.g. {@code mixin},
   * {@code parserClass}). Used by {@code PsiHelperFactory} to scope reference resolution for
   * each FQN attribute to the directory tree it points into.
   * <ol>
   *   <li>Specific {@code *InputPath} sibling per {@link #INPUT_FOR} — if non-null,
   *       returned. Otherwise falls through to the global {@code inputPath}.</li>
   *   <li>Specific {@code *OutputPath} sibling per {@link #OUTPUT_FOR} — resolved via
   *       {@link BnfPathsResolution#path}. Output attributes do <i>not</i> fall back to the
   *       global {@code inputPath}.</li>
   *   <li>Global {@link KnownAttribute#INPUT_PATH} — if non-null, returned.</li>
   *   <li>Otherwise null.</li>
   * </ol>
   */
  public static @Nullable Path referencePath(@NotNull BnfPathsResolution resolution,
                                             @Nullable KnownAttribute<?> fqnAttribute) {
    if (fqnAttribute != null) {
      KnownAttribute<String> input = INPUT_FOR.get(fqnAttribute);
      if (input != null) {
        Path p = resolution.path(input);
        if (p != null) return p;
        // fall through to global inputPath
      }
      else {
        KnownAttribute<String> output = OUTPUT_FOR.get(fqnAttribute);
        if (output != null) {
          // output attributes do not fall back to the global inputPath default
          return resolution.path(output);
        }
      }
    }
    return resolution.path(KnownAttribute.INPUT_PATH);
  }

  private static @Nullable Path findParserPathInProject(@NotNull BnfFile bnfFile, VirtualFile bnfVf) {
    String parserClass = getRootAttribute(bnfFile, KnownAttribute.PARSER_CLASS);
    if (!StringUtil.isNotEmpty(parserClass)) {
      return null;
    }

    return inferTargetDirectory(
      bnfFile.getProject(),
      bnfVf,
      StringUtil.getShortName(parserClass) + ".java",
      StringUtil.getPackageName(parserClass),
      true,
      true
    );
  }

  /**
   * Read-only sibling of {@link org.intellij.grammar.generator.batch.FileGeneratorUtil#getTargetDirectoryFor}: applies the same
   * root-selection and package-append logic but never invokes a write action and never creates
   * directories. Returns the would-be absolute {@link Path} as if generation had run, or
   * {@code null} when a root cannot be guessed.
   *
   * <p>This is the inference half shared between {@link #compute} (editor read context, no
   * creation) and {@code FileGeneratorUtil.getTargetDirectoryFor} (write context, layers a
   * {@code createDirectoryIfMissing} on top).
   */
  public static @Nullable Path inferTargetDirectory(@NotNull Project project,
                                                    @NotNull VirtualFile sourceFile,
                                                    @Nullable String targetFile,
                                                    @Nullable String targetPackage,
                                                    boolean returnRoot,
                                                    boolean preferGenRoot) {
    boolean hasPackage = StringUtil.isNotEmpty(targetPackage);
    ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
    PackageIndex packageIndex = PackageIndex.getInstance(project);
    ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);

    VirtualFile existingFile = findExistingFile(project, targetFile, targetPackage, fileIndex, packageIndex);
    VirtualFile existingFileRoot =
      existingFile == null ? null :
      fileIndex.isInSourceContent(existingFile) ? fileIndex.getSourceRootForFile(existingFile) :
      fileIndex.isInContent(existingFile) ? fileIndex.getContentRootForFile(existingFile) : null;

    boolean preferSourceRoot = hasPackage && !preferGenRoot;
    VirtualFile[] sourceRoots = rootManager.getContentSourceRoots();
    VirtualFile[] contentRoots = rootManager.getContentRoots();
    VirtualFile virtualRoot = existingFileRoot != null ? existingFileRoot :
                              preferSourceRoot && fileIndex.isInSource(sourceFile) ? fileIndex.getSourceRootForFile(sourceFile) :
                              fileIndex.isInContent(sourceFile) ? fileIndex.getContentRootForFile(sourceFile) :
                              ArrayUtil.getFirstElement(preferSourceRoot && sourceRoots.length > 0 ? sourceRoots : contentRoots);
    if (virtualRoot == null) return null;

    String packagePrefix = StringUtil.notNullize(packageIndex.getPackageNameByDirectory(virtualRoot));
    String genDirName = Options.GEN_DIR.get();
    boolean newGenRoot = !fileIndex.isInSourceContent(virtualRoot);
    String relativePath = (hasPackage && newGenRoot ? genDirName + "/" + targetPackage :
                           hasPackage ? StringUtil.trimStart(StringUtil.trimStart(targetPackage, packagePrefix), ".") :
                           newGenRoot ? genDirName : "").replace('.', '/');
    Path rootPath;
    try {
      rootPath = Paths.get(virtualRoot.getPath());
    }
    catch (InvalidPathException e) {
      return null;
    }
    if (returnRoot) {
      return newGenRoot ? rootPath.resolve(genDirName) : rootPath;
    }
    return relativePath.isEmpty() ? rootPath : rootPath.resolve(relativePath);
  }

  private static @Nullable VirtualFile findExistingFile(@NotNull Project project,
                                                        @Nullable String targetFile,
                                                        @Nullable String targetPackage,
                                                        @NotNull ProjectFileIndex fileIndex,
                                                        @NotNull PackageIndex packageIndex) {
    if (targetFile == null) return null;
    Collection<VirtualFile> fromIndex = FilenameIndex.getVirtualFilesByName(targetFile, ProjectScope.getProjectScope(project));
    List<VirtualFile> files = new ArrayList<>(fromIndex);
    files.sort((f1, f2) -> {
      boolean b1 = fileIndex.isInSource(f1);
      boolean b2 = fileIndex.isInSource(f2);
      if (b1 != b2) return b1 ? -1 : 1;
      return Integer.compare(f1.getPath().length(), f2.getPath().length());
    });
    for (VirtualFile file : files) {
      String existingFilePackage = packageIndex.getPackageNameByDirectory(file.getParent());
      if (StringUtil.isEmpty(targetPackage) || existingFilePackage == null || targetPackage.equals(existingFilePackage)) {
        return file;
      }
    }
    return null;
  }
}
