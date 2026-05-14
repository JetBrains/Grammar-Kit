/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.SourceRootResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Kotlin counterpart to {@link org.intellij.grammar.classinfo.java.JavaClassManager}: maps FQN →
 * {@link ClassInfo} by parsing {@code .kt} source files under the configured source roots.
 * <p>
 * Lookup is two-tiered, mirroring the Java side:
 * <ol>
 *   <li><b>Fast path</b>: resolve {@code com.foo.Bar} to {@code <root>/com/foo/Bar.kt}.</li>
 *   <li><b>Slow path</b>: walk dotted prefixes longest-first and parse every {@code .kt} file in
 *       the matching package directory. Needed because Kotlin freely allows file names to differ
 *       from the public class name and supports multiple top-level declarations per file.</li>
 * </ol>
 * Each package directory is scanned at most once and each file is ingested at most once per
 * {@link KotlinClassManager} instance.
 */
@SuppressWarnings("UnstableApiUsage")
public final class KotlinClassManager {

  private final SourceRootResolver resolver;
  private final KotlinSyntaxTreeManager treeManager = new KotlinSyntaxTreeManager();
  private final Map<String, ClassInfo> classCache = new HashMap<>();
  private final Set<String> negativeCache = new HashSet<>();
  private final Set<String> scannedPackages = new HashSet<>();
  private final Set<Path> ingestedFiles = new HashSet<>();

  public KotlinClassManager(@NotNull List<Path> sourceRoots) {
    this.resolver = new SourceRootResolver(sourceRoots);
  }

  public @Nullable ClassInfo findClass(@Nullable String fqn) {
    if (fqn == null || fqn.isEmpty()) return null;
    ClassInfo cached = classCache.get(fqn);
    if (cached != null) return cached;
    if (negativeCache.contains(fqn)) return null;

    Path source = resolver.findSourceFile(fqn, ".kt");
    if (source != null) ingest(source);

    String prefix = fqn;
    while (!classCache.containsKey(fqn)) {
      int lastDot = prefix.lastIndexOf('.');
      if (lastDot < 0) break;
      prefix = prefix.substring(0, lastDot);
      if (!scannedPackages.add(prefix)) break;
      for (Path dir : resolver.findPackageDirs(prefix)) {
        scanPackage(dir);
      }
    }

    ClassInfo result = classCache.get(fqn);
    if (result == null) negativeCache.add(fqn);
    return result;
  }

  private void scanPackage(@NotNull Path dir) {
    try (Stream<Path> files = Files.list(dir)) {
      files.filter(p -> p.getFileName().toString().endsWith(".kt")).forEach(this::ingest);
    }
    catch (IOException ignored) { }
  }

  private void ingest(@NotNull Path source) {
    if (!ingestedFiles.add(source)) return;
    SyntaxNode root = treeManager.parseFile(source);
    if (root == null) return;
    String fileStem = stem(source);
    classCache.putAll(KotlinSyntaxClassExtractor.extractFrom(root, fileStem));
  }

  private static @NotNull String stem(@NotNull Path file) {
    String name = file.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot < 0 ? name : name.substring(0, dot);
  }
}
