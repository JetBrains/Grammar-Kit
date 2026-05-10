/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parsed CLI invocation. {@code paths} holds explicit absolute path overrides keyed by
 * the corresponding {@link KnownAttribute}; {@code grammarPatterns} are the grammar-file
 * arguments (one entry under the new form, one or more under the legacy form).
 */
record CliArgs(@NotNull Map<KnownAttribute<String>, Path> paths,
               @NotNull List<String> grammarPatterns,
               boolean strictPaths) {

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
        err.println("warning: positional <output-dir> is deprecated; use --parser-output <dir>");
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
    Map<KnownAttribute<String>, Path> paths = new LinkedHashMap<>();
    paths.put(KnownAttribute.PARSER_OUTPUT_PATH, toAbsolute(args[0]));
    List<String> patterns = new java.util.ArrayList<>();
    for (int i = 1; i < args.length; i++) patterns.add(args[i]);
    return new CliArgs(Map.copyOf(paths), List.copyOf(patterns), false);
  }

  private static CliArgs parseNew(@NotNull String[] args) throws UsageException {
    Map<KnownAttribute<String>, Path> paths = new LinkedHashMap<>();
    List<String> positionals = new java.util.ArrayList<>();
    boolean strict = false;

    int i = 0;
    while (i < args.length) {
      String a = args[i];
      if ("--strict-paths".equals(a)) {
        strict = true;
        i++;
        continue;
      }
      KnownAttribute<String> attr = Main.FLAG_TO_ATTR.get(a);
      if (attr != null) {
        if (i + 1 >= args.length) {
          throw new UsageException(1, "Missing value for " + a);
        }
        paths.put(attr, toAbsolute(args[i + 1]));
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
    return new CliArgs(Map.copyOf(paths), List.copyOf(positionals), strict);
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
