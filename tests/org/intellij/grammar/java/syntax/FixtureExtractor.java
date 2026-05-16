/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Collects every {@link ClassInfo} that a {@link JvmClassSymbolProvider} would extract from a
 * fixture source root. Walks the root for files of the given extension, derives package FQNs from
 * the directory tree, and probes the provider once per package — the provider scans each package
 * and returns all classes declared there, which we accumulate.
 */
public final class FixtureExtractor {

  private static final SymbolResolver NULL_RESOLVER = fqn -> null;

  private FixtureExtractor() { }

  /**
   * @param root         the fixture's source root directory
   * @param provider     the provider under test (e.g. {@code KotlinSyntaxClassSymbolProvider})
   * @param extension    file extension including dot (e.g. {@code ".kt"}, {@code ".java"})
   */
  public static @NotNull Map<Fqn, ClassInfo> extractAll(@NotNull Path root,
                                                 @NotNull JvmClassSymbolProvider provider,
                                                 @NotNull String extension) {
    Set<Fqn> packages = new HashSet<>();
    try (Stream<Path> stream = Files.walk(root)) {
      stream.filter(p -> p.getFileName().toString().endsWith(extension))
            .map(Path::getParent)
            .filter(dir -> dir != null && dir.startsWith(root))
            .forEach(dir -> packages.add(dirToFqn(root, dir)));
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    Map<Fqn, ClassInfo> all = new HashMap<>();
    for (Fqn pkg : packages) {
      Fqn probe = pkg.child("__PROBE_NONEXISTENT__");
      all.putAll(provider.resolve(probe, NULL_RESOLVER));
    }
    return all;
  }

  private static @NotNull Fqn dirToFqn(@NotNull Path root, @NotNull Path dir) {
    Path rel = root.relativize(dir);
    if (rel.toString().isEmpty()) return Fqn.ROOT;
    return Fqn.of(rel.toString().replace(java.io.File.separatorChar, '.'));
  }
}
