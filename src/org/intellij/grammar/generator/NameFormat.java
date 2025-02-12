/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a name format consisting of a prefix and a suffix.
 * It provides methods for applying and stripping the format from a string.
 */
public class NameFormat {
  private static final @NotNull NameFormat EMPTY = new NameFormat("");

  final String prefix;
  final String suffix;

  private NameFormat(@Nullable String format) {
    JBIterable<String> parts = JBIterable.of(format == null ? null : format.split("/"));
    prefix = parts.get(0);
    suffix = StringUtil.join(parts.skip(1), "");
  }

  public static @NotNull NameFormat from(@Nullable String format) {
    return StringUtil.isEmpty(format) ? EMPTY : new NameFormat(format);
  }

  public @NotNull String apply(@NotNull String s) {
    if (prefix != null) s = prefix + s;
    if (suffix != null) s += suffix;
    return s;
  }

  public @NotNull String strip(@NotNull String s) {
    if (prefix != null && s.startsWith(prefix)) s = s.substring(prefix.length());
    if (suffix != null && s.endsWith(suffix)) s = s.substring(0, s.length() - suffix.length());
    return s;
  }
}
