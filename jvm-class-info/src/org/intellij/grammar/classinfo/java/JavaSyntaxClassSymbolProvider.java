/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.java;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.PackageDeclarationReader;
import org.intellij.grammar.classinfo.SourceRootResolver;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Java-source backed {@link JvmClassSymbolProvider}: turns a {@code .java} file under one of the
 * configured source-root directories into {@link ClassSymbol} records via
 * {@link JavaSyntaxTreeManager#parseText parsing} and {@link JavaSyntaxClassExtractor extraction}.
 * <p>
 * Lookup is two-tiered:
 * <ol>
 *   <li><b>Fast path</b>: map FQN to {@code <root>/com/foo/Bar.java}. Works when the file name
 *       matches the public class name (the JLS-required common case).</li>
 *   <li><b>Slow path</b>: when the fast path doesn't find the class — typical for package-private
 *       top-level classes, multi-class-per-file declarations, and inner classes whose enclosing
 *       class is in a non-matching file — walk dotted prefixes of the FQN longest-first and parse
 *       every {@code .java} file in the matching package directory.</li>
 * </ol>
 * Each package directory is scanned at most once and each file is ingested at most once per
 * provider instance. The {@link ClassSymbol} cache lives in {@link JvmClassSymbolManager};
 * this provider only memoises source-walking work.
 */
@SuppressWarnings("UnstableApiUsage")
public final class JavaSyntaxClassSymbolProvider implements JvmClassSymbolProvider {

  private static final Logger LOG = Logger.getInstance(JavaSyntaxClassSymbolProvider.class);

  private final SourceRootResolver sourceResolver;
  private final SyntaxTreeCache treeCache;
  private final Set<Fqn> scannedPackages = new HashSet<>();
  private final Set<Path> ingestedFiles = new HashSet<>();
  // Lazy index of files keyed by their declared package, used when both the fast path and the
  // package-dir slow path miss. Built at most once per provider instance; null until then.
  private Map<String, List<Path>> packageIndex;

  public JavaSyntaxClassSymbolProvider(@NotNull List<Path> sourceRoots) {
    this(sourceRoots, new SyntaxTreeCache());
  }

  public JavaSyntaxClassSymbolProvider(@NotNull List<Path> sourceRoots, @NotNull SyntaxTreeCache treeCache) {
    this.sourceResolver = new SourceRootResolver(sourceRoots);
    this.treeCache = treeCache;
  }

  @Override
  public @NotNull Map<Fqn, ClassSymbol> resolve(@NotNull Fqn fqn, @NotNull SymbolResolver resolver) {
    Map<Fqn, ClassSymbol> batch = new HashMap<>();

    // Fast path: file name matches the (public) class name.
    Path source = sourceResolver.findSourceFile(fqn, ".java");
    if (source != null) ingest(source, batch, resolver);

    // Slow path: walk dotted prefixes longest-first so inner-class FQNs find the right
    // enclosing-file scope. Stops as soon as we've found the requested FQN or run out of prefixes.
    Fqn prefix = fqn;
    while (!batch.containsKey(fqn)) {
      prefix = prefix.parent();
      if (prefix.isEmpty()) break;
      if (!scannedPackages.add(prefix)) continue;
      for (Path dir : sourceResolver.findPackageDirs(prefix)) {
        scanPackage(dir, batch, resolver);
      }
    }

    // Package-index fallback: when a .java file lives at a path that doesn't match its declared
    // package — rare under the JLS but possible for hand-crafted layouts — lex every remaining file
    // once to learn its declared package, then ingest files whose package is a prefix of the
    // requested FQN. The index is built lazily and at most once per provider.
    if (!batch.containsKey(fqn)) {
      ensurePackageIndex();
      Fqn p = fqn;
      while (!batch.containsKey(fqn)) {
        p = p.parent();
        if (p.isEmpty()) {
          ingestFromPackageIndex("", batch, resolver);
          break;
        }
        ingestFromPackageIndex(p.value(), batch, resolver);
      }
    }

    return batch;
  }

  private void ensurePackageIndex() {
    if (packageIndex != null) return;
    packageIndex = new HashMap<>();
    for (Path file : sourceResolver.walkFiles(".java")) {
      if (ingestedFiles.contains(file)) continue;
      String pkg = PackageDeclarationReader.readJavaPackage(file);
      packageIndex.computeIfAbsent(pkg, k -> new ArrayList<>()).add(file);
    }
  }

  private void ingestFromPackageIndex(@NotNull String pkg,
                                      @NotNull Map<Fqn, ClassSymbol> batch,
                                      @NotNull SymbolResolver resolver) {
    List<Path> files = packageIndex.get(pkg);
    if (files == null) return;
    for (Path file : files) {
      ingest(file, batch, resolver);
    }
  }

  private void scanPackage(@NotNull Path dir, @NotNull Map<Fqn, ClassSymbol> batch, @NotNull SymbolResolver resolver) {
    try (Stream<Path> files = Files.list(dir)) {
      files.filter(p -> p.getFileName().toString().endsWith(".java")).forEach(p -> ingest(p, batch, resolver));
    }
    catch (IOException e) {
      LOG.warn("Failed to scan Java source package directory: " + dir, e);
    }
  }

  private void ingest(@NotNull Path source, @NotNull Map<Fqn, ClassSymbol> batch, @NotNull SymbolResolver resolver) {
    if (!ingestedFiles.add(source)) return;
    SyntaxNode root = treeCache.parseOrGet(source, JavaSyntaxTreeManager::parseText);
    if (root != null) batch.putAll(JavaSyntaxClassExtractor.extractFrom(root, resolver));
  }
}
