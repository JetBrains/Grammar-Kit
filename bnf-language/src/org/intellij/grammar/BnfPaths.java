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
import org.intellij.grammar.KnownAttribute.ListValue;
import org.intellij.grammar.config.Options;
import org.intellij.grammar.java.JavaHelperFactory;
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
 * {@code JavaHelperFactory}, the build-time {@code BnfGenerationService}, and the inlay-hints
 * provider).
 *
 * <p>Path values are always resolved relative to the BNF file's parent directory.
 *
 * <p>{@link KnownAttribute#PSI_INPUT_PATH} is multi-valued: the grammar may declare a list of
 * directories. Every other path attribute is single-valued. The resolution stores all values as
 * {@link List} of {@link Path}; single-valued attributes carry a one-element list.
 */
public final class BnfPaths {
  private BnfPaths() {
  }

  /** Input-path attributes, in declaration order. Directories the generator reads from. */
  public static final List<KnownAttribute<?>> INPUTS = List.of(
    KnownAttribute.INPUT_PATH,
    KnownAttribute.PSI_INPUT_PATH);

  /** Single-valued output-path attributes, in declaration order. Each represents a directory
   * the generator writes to and that callers (CLI, batch service) may need to materialize on disk. */
  public static final List<KnownAttribute<String>> OUTPUTS = List.of(
    KnownAttribute.PARSER_OUTPUT_PATH,
    KnownAttribute.PSI_OUTPUT_PATH,
    KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH,
    KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH,
    KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH);

  /** Every path-valued attribute (input + output), in declaration order. Heterogeneous: most
   * attributes are {@code KnownAttribute<String>}; {@link KnownAttribute#PSI_INPUT_PATH} alone
   * is {@code KnownAttribute<ListValue>}. */
  public static final List<KnownAttribute<?>> ALL = buildAll();

  private static List<KnownAttribute<?>> buildAll() {
    List<KnownAttribute<?>> list = new ArrayList<>();
    list.addAll(INPUTS);
    list.addAll(OUTPUTS);
    return List.copyOf(list);
  }

  /**
   * FQN-attribute → its corresponding {@code *InputPath} sibling. {@code parserUtilClass} has
   * no specific input override and falls through to the global {@code inputPath}.
   */
  public static final Map<KnownAttribute<?>, KnownAttribute<?>> INPUT_FOR = Map.of(
    KnownAttribute.PSI_IMPL_UTIL_CLASS,  KnownAttribute.PSI_INPUT_PATH,
    KnownAttribute.MIXIN,                KnownAttribute.PSI_INPUT_PATH,
    KnownAttribute.EXTENDS,              KnownAttribute.PSI_INPUT_PATH,
    KnownAttribute.IMPLEMENTS,           KnownAttribute.PSI_INPUT_PATH);

  /** FQN-attribute → its corresponding {@code *OutputPath} sibling. */
  public static final Map<KnownAttribute<?>, KnownAttribute<String>> OUTPUT_FOR = Map.ofEntries(
    Map.entry(KnownAttribute.PARSER_CLASS,                         KnownAttribute.PARSER_OUTPUT_PATH),
    Map.entry(KnownAttribute.ELEMENT_TYPE_HOLDER_CLASS,            KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH),
    Map.entry(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_CLASS,     KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH),
    Map.entry(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_CLASS, KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH),
    Map.entry(KnownAttribute.PSI_PACKAGE,                          KnownAttribute.PSI_OUTPUT_PATH),
    Map.entry(KnownAttribute.PSI_IMPL_PACKAGE,                     KnownAttribute.PSI_OUTPUT_PATH));

  private static final Map<String, KnownAttribute<?>> BY_NAME = buildByName();

  private static Map<String, KnownAttribute<?>> buildByName() {
    Map<String, KnownAttribute<?>> map = new LinkedHashMap<>();
    for (KnownAttribute<?> a : ALL) map.put(a.getName(), a);
    return Map.copyOf(map);
  }

  /** Looks up a path attribute by its BNF-source name; null when {@code name} is not a path attribute. */
  public static @Nullable KnownAttribute<?> pathAttributeByName(@Nullable String name) {
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
   * IDE-side consumers ({@link JavaHelperFactory}) treat the missing
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
  public static @NotNull BnfPathsResolution resolveExplicit(@NotNull Map<KnownAttribute<?>, List<Path>> explicit) {
    return new BnfPathsResolution(applyCascade(explicit));
  }

  /**
   * CLI variant of {@link #resolveExplicit(Map)} that also seeds a default for
   * {@link KnownAttribute#INPUT_PATH}: when neither the CLI nor the grammar provided one, it
   * defaults to {@code bnfParent}. Mirrors the default applied by {@link #compute} for IDE
   * editing contexts so headless runs and IDE views agree on the input scope.
   */
  public static @NotNull BnfPathsResolution resolveExplicit(@NotNull Map<KnownAttribute<?>, List<Path>> explicit,
                                                            @NotNull Path bnfParent) {
    Map<KnownAttribute<?>, List<Path>> seeded = new HashMap<>(explicit);
    seeded.putIfAbsent(KnownAttribute.INPUT_PATH, List.of(bnfParent));
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

    Map<KnownAttribute<?>, List<Path>> resolved = collectExplicitPaths(bnfFile, parentPath);

    // parserOutputPath fallback: derive from parserClass package via read-only inference.
    if (!resolved.containsKey(KnownAttribute.PARSER_OUTPUT_PATH)) {
      Path inferred = findParserPathInProject(bnfFile, bnfVf);
      if (inferred != null) resolved.put(KnownAttribute.PARSER_OUTPUT_PATH, List.of(inferred));
    }

    // No inputPath default in IDE mode: JavaHelperFactory falls back to a project-wide search
    // scope when the resolution has no inputPath. The CLI overload of resolveExplicit seeds
    // bnfParent itself because standalone runs have no Project to fall back to.

    Map<KnownAttribute<?>, List<Path>> effectivePaths = applyCascade(resolved);
    return new BnfPathsResolution(effectivePaths);
  }

  /**
   * Reads every {@link #ALL} path attribute from {@code bnfFile} and resolves each non-empty
   * value relative to {@code bnfParent}. Pure path arithmetic — does not touch the VFS or
   * create any directories. Returns a fresh mutable map so callers can layer their own
   * fallbacks (e.g. a CLI {@code <output-dir>} default for {@code parserOutputPath}) before
   * passing the result to {@link #resolveExplicit}.
   *
   * <p>Single-valued attributes ({@code String}-typed) yield a one-element list; the
   * multi-valued {@link KnownAttribute#PSI_INPUT_PATH} yields one entry per declared root.
   */
  public static @NotNull Map<KnownAttribute<?>, List<Path>> collectExplicitPaths(
    @NotNull BnfFile bnfFile, @NotNull Path bnfParent) {
    Map<KnownAttribute<?>, List<Path>> resolved = new HashMap<>();
    for (KnownAttribute<?> attr : ALL) {
      List<Path> paths = readPathsForAttribute(bnfFile, attr, bnfParent);
      if (!paths.isEmpty()) resolved.put(attr, paths);
    }
    return resolved;
  }

  @SuppressWarnings("unchecked")
  private static @NotNull List<Path> readPathsForAttribute(@NotNull BnfFile bnfFile,
                                                           @NotNull KnownAttribute<?> attr,
                                                           @NotNull Path bnfParent) {
    if (attr == KnownAttribute.PSI_INPUT_PATH) {
      ListValue list = getRootAttribute(bnfFile, (KnownAttribute<ListValue>)attr);
      if (list == null || list.isEmpty()) return List.of();
      List<Path> result = new ArrayList<>(list.size());
      for (String value : list.asStrings()) {
        if (StringUtil.isEmpty(value)) continue;
        Path p = tryResolve(bnfParent, value);
        if (p != null) result.add(p);
      }
      return List.copyOf(result);
    }
    String value = getRootAttribute(bnfFile, (KnownAttribute<String>)attr);
    if (StringUtil.isEmpty(value)) return List.of();
    Path p = tryResolve(bnfParent, value);
    return p == null ? List.of() : List.of(p);
  }

  private static @Nullable Path tryResolve(@NotNull Path bnfParent, @NotNull String value) {
    try {
      return bnfParent.resolve(value).normalize();
    }
    catch (InvalidPathException ignored) {
      return null;
    }
  }

  /**
   * Bakes both the input- and output-path fallback chains into {@code explicit}, in one pass,
   * so direct lookups via {@link BnfPathsResolution#path} agree with the on-the-fly cascade
   * computed by {@link #referencePaths}.
   *
   * <p><b>Input cascade.</b> When {@link KnownAttribute#INPUT_PATH global inputPath} is set,
   * an unset {@code psiInputPath} inherits its value (as a one-element list). When
   * {@code inputPath} is unset, no input default is applied.
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
  private static @NotNull Map<KnownAttribute<?>, List<Path>> applyCascade(
    @NotNull Map<KnownAttribute<?>, List<Path>> explicit) {
    Map<KnownAttribute<?>, List<Path>> next = new HashMap<>(explicit);

    List<Path> inputGlobal = next.get(KnownAttribute.INPUT_PATH);
    if (inputGlobal != null && !inputGlobal.isEmpty()) {
      next.putIfAbsent(KnownAttribute.PSI_INPUT_PATH, inputGlobal);
    }

    List<Path> parser = next.get(KnownAttribute.PARSER_OUTPUT_PATH);
    if (parser != null && !parser.isEmpty()) {
      next.putIfAbsent(KnownAttribute.PSI_OUTPUT_PATH, parser);
      List<Path> psi = next.get(KnownAttribute.PSI_OUTPUT_PATH);
      next.putIfAbsent(KnownAttribute.ELEMENT_TYPE_HOLDER_OUTPUT_PATH, psi);
      next.putIfAbsent(KnownAttribute.SYNTAX_ELEMENT_TYPE_HOLDER_OUTPUT_PATH, psi);
      next.putIfAbsent(KnownAttribute.ELEMENT_TYPE_CONVERTER_FACTORY_OUTPUT_PATH, psi);
    }

    return Map.copyOf(next);
  }

  /**
   * Cascade lookup keyed on a class-reference FQN attribute (e.g. {@code mixin},
   * {@code parserClass}). Returns the full list of effective input roots — multi-element for
   * {@link KnownAttribute#PSI_INPUT_PATH} when the grammar declared several, single-element
   * for output-side attributes or for the global {@code inputPath} fallback.
   * <ol>
   *   <li>Specific {@code *InputPath} sibling per {@link #INPUT_FOR} — if non-empty,
   *       returned. Otherwise falls through to the global {@code inputPath}.</li>
   *   <li>Specific {@code *OutputPath} sibling per {@link #OUTPUT_FOR} — resolved via
   *       {@link BnfPathsResolution#paths}. Output attributes do <i>not</i> fall back to the
   *       global {@code inputPath}.</li>
   *   <li>Global {@link KnownAttribute#INPUT_PATH} — if non-empty, returned.</li>
   *   <li>Otherwise empty.</li>
   * </ol>
   */
  public static @NotNull List<Path> referencePaths(@NotNull BnfPathsResolution resolution,
                                                   @Nullable KnownAttribute<?> fqnAttribute) {
    if (fqnAttribute != null) {
      KnownAttribute<?> input = INPUT_FOR.get(fqnAttribute);
      if (input != null) {
        List<Path> p = resolution.paths(input);
        if (!p.isEmpty()) return p;
        // fall through to global inputPath
      }
      else {
        KnownAttribute<String> output = OUTPUT_FOR.get(fqnAttribute);
        if (output != null) {
          // output attributes do not fall back to the global inputPath default
          return resolution.paths(output);
        }
      }
    }
    return resolution.paths(KnownAttribute.INPUT_PATH);
  }

  /**
   * Single-path variant of {@link #referencePaths} — returns the first effective root or
   * {@code null}. Use this only when one directory is sufficient; multi-path resolutions
   * (e.g. {@link KnownAttribute#PSI_INPUT_PATH} with several declared roots) collapse to the
   * first element through this accessor.
   */
  public static @Nullable Path referencePath(@NotNull BnfPathsResolution resolution,
                                             @Nullable KnownAttribute<?> fqnAttribute) {
    List<Path> list = referencePaths(resolution, fqnAttribute);
    return list.isEmpty() ? null : list.get(0);
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
