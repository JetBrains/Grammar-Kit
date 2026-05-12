/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.java.syntax.JavaSyntaxDefinition;
import com.intellij.platform.syntax.SyntaxElementType;
import com.intellij.platform.syntax.SyntaxLanguage;
import com.intellij.platform.syntax.tree.ParseKt;
import com.intellij.platform.syntax.tree.SyntaxNode;
import com.intellij.platform.syntax.util.language.SyntaxElementLanguageProvider;
import com.intellij.util.containers.ContainerUtil;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Parses a Java source file with the {@code syntax-api} + {@code java-syntax} libraries and returns
 * the root {@link SyntaxNode} of the parsed tree.
 * <p>
 * The actual parsing is one-line — {@link ParseKt#parse} hides the lexer / token-list / builder /
 * production-result pipeline behind a single function that yields a navigable
 * {@link com.intellij.platform.syntax.tree.KmpSyntaxNode}. Trees are cached per {@link Path} +
 * last-modified timestamp so the same file isn't re-parsed on every {@code findClass} call. The
 * cache uses <b>soft-ref'd values</b> — a parsed tree can be hundreds of KB and the slow-path
 * package scan in {@link JavaClassManager} can ingest dozens of files at once, so under memory
 * pressure the JVM is free to drop entries and force a re-parse on the next lookup.
 */
@SuppressWarnings("UnstableApiUsage")
final class JavaSyntaxTreeManager {
  private final Map<Path, CacheEntry> cache = ContainerUtil.createSoftValueMap();

  /**
   * Parses the file at {@code path}. Returns {@code null} when the file is unreadable. Subsequent
   * calls return the cached root as long as the file's mtime hasn't changed.
   */
  @Nullable SyntaxNode parseFile(@NotNull Path path) {
    long mtime;
    try {
      mtime = Files.getLastModifiedTime(path).toMillis();
    }
    catch (IOException e) {
      return null;
    }
    CacheEntry entry = cache.get(path);
    if (entry != null && entry.mtime == mtime) return entry.root;
    String text;
    try {
      text = Files.readString(path);
    }
    catch (IOException e) {
      return null;
    }
    SyntaxNode root = parseText(text);
    cache.put(path, new CacheEntry(mtime, root));
    return root;
  }

  /**
   * Parses an in-memory source string.
   */
  static @NotNull SyntaxNode parseText(@NotNull String text) {
    return ParseKt.parse(text, JavaSyntaxDefinition.INSTANCE, JavaLang.INSTANCE, null, null);
  }

  private record CacheEntry(long mtime, SyntaxNode root) {
  }

  private static final class JavaLang implements SyntaxElementLanguageProvider {
    public static final JavaLang INSTANCE = new JavaLang();

    @Override
    public @NotNull Sequence<SyntaxLanguage> getLanguages(@NotNull SyntaxElementType type) {
      return SequencesKt.sequenceOf(JavaSyntaxDefinition.INSTANCE.getLanguage());
    }
  }
}
