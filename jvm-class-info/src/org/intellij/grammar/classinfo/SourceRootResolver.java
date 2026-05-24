/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves a fully-qualified Java/Kotlin class name to a source file under a configured set of
 * source-root directories. The mapping is the obvious one: {@code com.foo.Bar} →
 * {@code <root>/com/foo/Bar.<extension>}. For inner classes ({@code com.foo.Outer.Inner}) we walk
 * dotted segments right-to-left until we hit an existing file, so the source for an inner type is
 * found via its enclosing top-level file.
 */
public final class SourceRootResolver {

  private final List<Path> roots;
  private final Map<String, Path> cache = new HashMap<>();

  public SourceRootResolver(@NotNull List<Path> roots) {
    this.roots = new ArrayList<>(roots);
  }

  public @NotNull List<Path> getRoots() {
    return Collections.unmodifiableList(roots);
  }

  /**
   * Locates the source file that declares (or, for inner classes, encloses) {@code fqn}.
   * Returns {@code null} when no matching file exists under any configured root.
   */
  public @Nullable Path findSourceFile(@Nullable Fqn fqn, @NotNull String extension) {
    if (fqn == null || fqn.isEmpty()) return null;
    String key = extension + ":" + fqn.value();
    if (cache.containsKey(key)) return cache.get(key);
    Path resolved = resolve(fqn, extension);
    cache.put(key, resolved);
    return resolved;
  }

  private @Nullable Path resolve(@NotNull Fqn fqn, @NotNull String extension) {
    Fqn current = fqn;
    while (true) {
      String candidate = current.value().replace('.', '/') + extension;
      for (Path root : roots) {
        Path p = root.resolve(candidate);
        if (Files.isRegularFile(p)) return p;
      }
      Fqn parent = current.parent();
      if (parent.isEmpty()) return null;
      current = parent;
    }
  }

  /**
   * Returns existing directories matching {@code packageName} across all configured source roots.
   * Used by the slow-path package scan when {@link #findSourceFile} can't find a file by FQN-name
   * (package-private classes, multi-class-per-file).
   */
  public @NotNull List<Path> findPackageDirs(@NotNull Fqn packageName) {
    String relative = packageName.value().replace('.', '/');
    List<Path> result = new SmartList<>();
    for (Path root : roots) {
      Path dir = relative.isEmpty() ? root : root.resolve(relative);
      if (Files.isDirectory(dir)) result.add(dir);
    }
    return result;
  }

  /**
   * Walks every configured root and returns the regular files whose names end with {@code extension}.
   * Hidden directories (names starting with {@code .}) are skipped; symlinks are not followed. Used by
   * the package-index fallback to enumerate candidate files when the FQN-derived path lookup
   * ({@link #findSourceFile}) and the package-directory scan ({@link #findPackageDirs}) both miss —
   * i.e. when a source file's path doesn't match its declared package (legal Kotlin layout).
   */
  public @NotNull List<Path> walkFiles(@NotNull String extension) {
    List<Path> result = new ArrayList<>();
    for (Path root : roots) {
      if (!Files.isDirectory(root)) continue;
      try {
        Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
          new FileVisitor<Path>() {
            @Override
            public @NotNull FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs) {
              if (!dir.equals(root) && dir.getFileName().toString().startsWith(".")) {
                return FileVisitResult.SKIP_SUBTREE;
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) {
              if (attrs.isRegularFile() && file.getFileName().toString().endsWith(extension)) {
                result.add(file);
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult visitFileFailed(@NotNull Path file, @NotNull IOException exc) {
              return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult postVisitDirectory(@NotNull Path dir, IOException exc) {
              return FileVisitResult.CONTINUE;
            }
          });
      }
      catch (IOException ignored) { }
    }
    return result;
  }
}
