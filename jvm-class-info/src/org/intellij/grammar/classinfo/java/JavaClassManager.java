/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

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
 * Resolves a fully-qualified class name to a {@link ClassInfo} record by parsing a Java source
 * file under one of the configured source-root directories. Owns the wiring that makes that
 * possible — {@link SourceRootResolver} for FQN → file-path mapping, {@link JavaSyntaxTreeManager}
 * for parsing, {@link JavaSyntaxClassExtractor} for tree-walking — plus the result cache.
 * <p>
 * Lookup is two-tiered:
 * <ol>
 *   <li><b>Fast path</b>: map FQN to {@code <root>/com/foo/Bar.java}. Works when the file name
 *       matches the public class name (the JLS-required common case).</li>
 *   <li><b>Slow path</b>: when the fast path doesn't find the class — typical for
 *       package-private top-level classes, multi-class-per-file declarations, and inner classes
 *       whose enclosing class is in a non-matching file — walk dotted prefixes of the FQN
 *       longest-first and parse every {@code .java} file in the matching package directory.
 *       Lazy parsing in {@code syntax-api} keeps this cheap.</li>
 * </ol>
 * Each package directory is scanned at most once and each file is ingested at most once per
 * {@link JavaClassManager} instance.
 */
@SuppressWarnings("UnstableApiUsage")
public final class JavaClassManager {

  private final SourceRootResolver resolver;
  private final JavaSyntaxTreeManager treeManager = new JavaSyntaxTreeManager();
  private final Map<String, ClassInfo> classCache = new HashMap<>();
  private final Set<String> negativeCache = new HashSet<>();
  private final Set<String> scannedPackages = new HashSet<>();
  private final Set<Path> ingestedFiles = new HashSet<>();

  public JavaClassManager(@NotNull List<Path> sourceRoots) {
    this.resolver = new SourceRootResolver(sourceRoots);
  }

  /**
   * Returns the {@link ClassInfo} for {@code fqn}, or {@code null} if no source file under the
   * configured roots declares it. Cached: subsequent calls for the same FQN — and for any sibling
   * classes declared in the same file — return without re-parsing.
   */
  public @Nullable ClassInfo findClass(@Nullable String fqn) {
    if (fqn == null || fqn.isEmpty()) return null;
    ClassInfo cached = classCache.get(fqn);
    if (cached != null) return cached;
    if (negativeCache.contains(fqn)) return null;

    // Fast path: file name matches the (public) class name.
    Path source = resolver.findSourceFile(fqn, ".java");
    if (source != null) ingest(source);

    // Slow path: package-private classes and multi-class-per-file. Walk dotted prefixes
    // longest-first so inner-class FQNs find the right enclosing-file scope.
    String prefix = fqn;
    while (!classCache.containsKey(fqn)) {
      int lastDot = prefix.lastIndexOf('.');
      if (lastDot < 0) break;
      prefix = prefix.substring(0, lastDot);
      if (!scannedPackages.add(prefix)) break;       // already scanned this and broader scopes
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
      files.filter(p -> p.getFileName().toString().endsWith(".java")).forEach(this::ingest);
    }
    catch (IOException ignored) { }
  }

  private void ingest(@NotNull Path source) {
    if (!ingestedFiles.add(source)) return;          // already extracted from this file
    SyntaxNode root = treeManager.parseFile(source);
    if (root != null) classCache.putAll(JavaSyntaxClassExtractor.extractFrom(root));
  }
}
