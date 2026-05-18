/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo.kotlin;

import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.SourceRootResolver;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.jetbrains.annotations.NotNull;

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
 * Kotlin counterpart to {@link JavaSyntaxClassSymbolProvider}.
 * Maps FQN → {@link ClassSymbol} by parsing {@code .kt} source files under the configured source
 * roots. Source-walking memoisation lives here; the {@link ClassSymbol} cache lives in
 * {@link JvmClassSymbolManager}.
 */
@SuppressWarnings("UnstableApiUsage")
public final class KotlinSyntaxClassSymbolProvider implements JvmClassSymbolProvider {

  private final SourceRootResolver sourceResolver;
  private final SyntaxTreeCache treeCache;
  private final Set<Fqn> scannedPackages = new HashSet<>();
  private final Set<Path> ingestedFiles = new HashSet<>();

  public KotlinSyntaxClassSymbolProvider(@NotNull List<Path> sourceRoots) {
    this(sourceRoots, new SyntaxTreeCache());
  }

  public KotlinSyntaxClassSymbolProvider(@NotNull List<Path> sourceRoots, @NotNull SyntaxTreeCache treeCache) {
    this.sourceResolver = new SourceRootResolver(sourceRoots);
    this.treeCache = treeCache;
  }

  @Override
  public @NotNull Map<Fqn, ClassSymbol> resolve(@NotNull Fqn fqn, @NotNull SymbolResolver resolver) {
    Map<Fqn, ClassSymbol.Builder> builders = new HashMap<>();

    Path source = sourceResolver.findSourceFile(fqn, ".kt");
    if (source != null) ingest(source, builders, resolver);

    Fqn prefix = fqn;
    while (!builders.containsKey(fqn)) {
      prefix = prefix.parent();
      if (prefix.isEmpty()) break;
      if (!scannedPackages.add(prefix)) continue;
      for (Path dir : sourceResolver.findPackageDirs(prefix)) {
        scanPackage(dir, builders, resolver);
      }
    }

    Map<Fqn, ClassSymbol> batch = new HashMap<>(builders.size());
    for (Map.Entry<Fqn, ClassSymbol.Builder> e : builders.entrySet()) batch.put(e.getKey(), e.getValue().build());
    return batch;
  }

  private void scanPackage(@NotNull Path dir, @NotNull Map<Fqn, ClassSymbol.Builder> batch, @NotNull SymbolResolver resolver) {
    try (Stream<Path> files = Files.list(dir)) {
      files.filter(p -> p.getFileName().toString().endsWith(".kt")).forEach(p -> ingest(p, batch, resolver));
    }
    catch (IOException ignored) { }
  }

  private void ingest(@NotNull Path source, @NotNull Map<Fqn, ClassSymbol.Builder> batch, @NotNull SymbolResolver resolver) {
    if (!ingestedFiles.add(source)) return;
    SyntaxNode root = treeCache.parseOrGet(source, KotlinSyntaxTreeManager::parseText);
    if (root == null) return;
    String fileStem = stem(source);
    Map<Fqn, ClassSymbol.Builder> extracted = KotlinSyntaxClassExtractor.extractFrom(root, fileStem, resolver);
    for (Map.Entry<Fqn, ClassSymbol.Builder> e : extracted.entrySet()) {
      ClassSymbol.Builder incoming = e.getValue();
      ClassSymbol.Builder existing = batch.get(e.getKey());
      // Sibling files annotated @file:JvmMultifileClass with the same JVM name contribute callables
      // to a single facade — merge methods rather than overwrite.
      if (existing != null && existing.multifileFacade && incoming.multifileFacade) {
        existing.methods.addAll(incoming.methods);
      }
      else {
        batch.put(e.getKey(), incoming);
      }
    }
  }

  private static @NotNull String stem(@NotNull Path file) {
    String name = file.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot < 0 ? name : name.substring(0, dot);
  }
}
