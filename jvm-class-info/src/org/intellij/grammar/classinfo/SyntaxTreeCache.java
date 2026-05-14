/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.platform.syntax.tree.SyntaxNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Language-agnostic cache of parsed syntax trees, keyed by source-file path. Plain
 * {@link HashMap} — no mtime checks, no soft references, no invalidation. The cache is intended for
 * single-build use; do not share an instance across separate generator invocations.
 * <p>
 * The caller supplies the parser as a {@link Function} so a single cache can hold trees from any
 * language. {@code null} (from unreadable files or a parser that returns {@code null}) is cached the
 * same way as a successful parse — once attempted, never retried.
 */
@SuppressWarnings("UnstableApiUsage")
public final class SyntaxTreeCache {

  private final Map<Path, SyntaxNode> cache = new HashMap<>();

  /**
   * Returns the parsed tree for {@code file}, parsing it on first request and caching the result
   * (including {@code null} for unreadable files) for the lifetime of this cache.
   */
  public @Nullable SyntaxNode parseOrGet(@NotNull Path file,
                                         @NotNull Function<@NotNull String, @NotNull SyntaxNode> parser) {
    if (cache.containsKey(file)) return cache.get(file);
    SyntaxNode root = readAndParse(file, parser);
    cache.put(file, root);
    return root;
  }

  private static @Nullable SyntaxNode readAndParse(@NotNull Path file,
                                                   @NotNull Function<String, SyntaxNode> parser) {
    String text;
    try {
      text = Files.readString(file);
    }
    catch (IOException e) {
      return null;
    }
    return parser.apply(text);
  }
}
