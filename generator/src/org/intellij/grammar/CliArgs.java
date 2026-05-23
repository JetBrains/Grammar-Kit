/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parsed CLI invocation. {@code paths} holds explicit absolute path overrides keyed by the
 * corresponding {@link KnownAttribute}: each value is a {@link List} of paths so the multi-valued
 * {@link KnownAttribute#PSI_INPUT_PATH} (repeatable {@code --psiInputPath} flag) round-trips through
 * the same shape as the in-memory {@link BnfPathsResolution}. Single-valued flags carry
 * one-element lists.
 *
 * <p>{@code grammarPatterns} are the grammar-file arguments (one entry under the new form, one
 * or more under the legacy form). {@code sourcePsi} opts the headless generator into
 * source-backed Java class lookup via {@link org.intellij.grammar.java.syntax.JavaSyntaxHelper},
 * rooted at the resolved {@code inputPath} / {@code psiInputPath}.
 */
record CliArgs(@NotNull Map<KnownAttribute<?>, List<Path>> paths,
               @NotNull List<String> grammarPatterns,
               boolean strictPaths,
               boolean sourcePsi) {

  static CliArgs parse(@NotNull String[] args, @NotNull PrintStream out, @NotNull PrintStream err) throws UsageException {
    if (args.length == 0) {
      throw new UsageException(0, null);
    }

    boolean hasFlag = false;
    for (String a : args) {
      if (a.startsWith("--")) {
        hasFlag = true;
        break;
      }
    }

    if (!hasFlag) {
      if (args.length == 1) {
        if (!looksLikeGrammar(args[0])) {
          throw new UsageException(0, null);
        }
        return parseNew(args);
      }
      if (!looksLikeGrammar(args[0])) {
        err.println("warning: positional <output-dir> is deprecated; use --parserOutputPath <dir>");
        return parseLegacy(args);
      }
    }
    return parseNew(args);
  }

  private static boolean looksLikeGrammar(@NotNull String arg) {
    if (arg.startsWith("--")) return false;
    String lower = arg.toLowerCase();
    // Pattern-based detection (extension only, never file existence): callers may pass globs
    // like "*.bnf", and existence-based detection misclassifies non-grammar files passed as
    // legacy <output-dir>.
    return lower.endsWith(".bnf") || lower.endsWith(".pg");
  }

  private static CliArgs parseLegacy(@NotNull String[] args) throws UsageException {
    Map<KnownAttribute<?>, List<Path>> paths = new LinkedHashMap<>();
    paths.put(KnownAttribute.PARSER_OUTPUT_PATH, List.of(toAbsolute(args[0])));
    List<String> patterns = new ArrayList<>();
    for (int i = 1; i < args.length; i++) patterns.add(args[i]);
    return new CliArgs(Map.copyOf(paths), List.copyOf(patterns), false, false);
  }

  private static CliArgs parseNew(@NotNull String[] args) throws UsageException {
    Map<KnownAttribute<?>, List<Path>> paths = new LinkedHashMap<>();
    List<String> positionals = new ArrayList<>();
    boolean strict = false;
    boolean sourcePsi = false;

    int i = 0;
    while (i < args.length) {
      String a = args[i];
      if ("--strict-paths".equals(a)) {
        strict = true;
        i++;
        continue;
      }
      if ("--source-psi".equals(a)) {
        sourcePsi = true;
        i++;
        continue;
      }
      KnownAttribute<?> attr = Main.FLAG_TO_ATTR.get(a);
      if (attr != null) {
        if (i + 1 >= args.length) {
          throw new UsageException(1, "Missing value for " + a);
        }
        Path resolved = toAbsolute(args[i + 1]);
        if (Main.MULTI_VALUE_ATTRS.contains(attr)) {
          // Repeatable flag: accumulate occurrences in declaration order.
          paths.merge(attr, List.of(resolved), (oldList, newList) -> {
            List<Path> combined = new ArrayList<>(oldList);
            combined.addAll(newList);
            return List.copyOf(combined);
          });
        }
        else {
          // Single-value flag: last occurrence wins (preserves existing CLI semantics).
          paths.put(attr, List.of(resolved));
        }
        i += 2;
        continue;
      }
      if (a.startsWith("--")) {
        throw new UsageException(1, "Unknown option: " + a);
      }
      positionals.add(a);
      i++;
    }

    if (positionals.isEmpty()) {
      throw new UsageException(0, null);
    }
    if (positionals.size() > 1) {
      throw new UsageException(1, "Expected exactly one grammar file in new form, got " + positionals.size() +
                                  "; legacy multi-grammar form does not accept --flags.");
    }
    return new CliArgs(Map.copyOf(paths), List.copyOf(positionals), strict, sourcePsi);
  }

  /** First resolved path for {@code attr}, or {@code null} when the CLI did not supply one. */
  Path firstPath(@NotNull KnownAttribute<?> attr) {
    List<Path> list = paths.get(attr);
    return list == null || list.isEmpty() ? null : list.get(0);
  }

  private static @NotNull Path toAbsolute(@NotNull String value) throws UsageException {
    try {
      return Paths.get(value).toAbsolutePath().normalize();
    }
    catch (InvalidPathException e) {
      throw new UsageException(1, "Invalid path: " + value);
    }
  }
}
