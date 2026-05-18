/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Merges CLI-supplied path overrides with paths declared in the grammar file.
 * Per-path semantics: CLI wins on collision. When both sides set the same attribute to
 * different absolute paths, a conflict is reported. In strict mode, conflicts abort
 * generation; otherwise they are printed as warnings and the CLI value takes effect.
 *
 * <p>Each attribute carries a {@link List} of paths so multi-valued attributes (e.g.
 * {@link KnownAttribute#PSI_INPUT_PATH}) round-trip unchanged. Conflict detection compares the
 * full list — order matters.
 */
final class PathConflicts {
  private PathConflicts() {
  }

  /**
   * Merge {@code cli} into {@code grammar}. Attributes only on one side pass through
   * unchanged. Attributes on both sides with equal value lists pass through silently.
   * Attributes on both sides with different lists produce a warning (strict=false) or a
   * thrown {@link ConflictException} after all conflicts have been printed (strict=true).
   */
  public static @NotNull Map<KnownAttribute<?>, List<Path>> merge(@NotNull Map<KnownAttribute<?>, List<Path>> cli,
                                                                  @NotNull Map<KnownAttribute<?>, List<Path>> grammar,
                                                                  boolean strict,
                                                                  @NotNull PrintStream warnSink) {
    List<String> conflicts = new ArrayList<>();
    Map<KnownAttribute<?>, List<Path>> merged = new HashMap<>(grammar);
    for (Map.Entry<KnownAttribute<?>, List<Path>> e : cli.entrySet()) {
      KnownAttribute<?> attr = e.getKey();
      List<Path> cliPaths = e.getValue();
      List<Path> grammarPaths = grammar.get(attr);
      if (grammarPaths != null && !grammarPaths.equals(cliPaths)) {
        String msg = attr.getName() + ": CLI value " + format(cliPaths) +
                     " overrides grammar value " + format(grammarPaths);
        conflicts.add(msg);
        if (!strict) warnSink.println("warning: " + msg);
      }
      merged.put(attr, cliPaths);
    }
    if (strict && !conflicts.isEmpty()) {
      for (String c : conflicts) warnSink.println("error: " + c);
      throw new ConflictException(conflicts);
    }
    return merged;
  }

  private static @NotNull String format(@NotNull List<Path> paths) {
    return paths.size() == 1 ? "'" + paths.get(0) + "'" : paths.toString();
  }

  public static final class ConflictException extends RuntimeException {
    private final List<String> conflicts;

    ConflictException(@NotNull List<String> conflicts) {
      super("path conflicts in --strict-paths mode: " + conflicts.size());
      this.conflicts = List.copyOf(conflicts);
    }

    public @NotNull List<String> conflicts() {
      return conflicts;
    }
  }
}
