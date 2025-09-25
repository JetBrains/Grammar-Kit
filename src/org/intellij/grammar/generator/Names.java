/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author greg
 */
public class Names {
  public final @NotNull String builder;
  public final @NotNull String level;
  public final @NotNull String marker;
  public final @NotNull String pinned;
  public final @NotNull String result;
  public final @NotNull String parse;
  public final @NotNull String pos;
  public final @NotNull String root;
  public final @NotNull String priority;
  public final @NotNull String metaParamPrefix;
  public final @NotNull String psiLocal = "p";
  public final @NotNull String runtime;

  private Names(@NotNull String builder,
                @NotNull String level,
                @NotNull String marker,
                @NotNull String pinned,
                @NotNull String result,
                @NotNull String parse,
                @NotNull String pos,
                @NotNull String root,
                @NotNull String priority,
                @NotNull String metaParamPrefix,
                @NotNull String runtime) {
    this.builder = builder;
    this.level = level;
    this.marker = marker;
    this.pinned = pinned;
    this.result = result;
    this.parse = parse;
    this.pos = pos;
    this.root = root;
    this.priority = priority;
    this.metaParamPrefix = metaParamPrefix;
    this.runtime = runtime;
  }

  @Contract(value = " -> new", pure = true)
  public static Names classicNames() {
    return new Names("builder_", "level_", "marker_", "pinned_", "result_", "parse_", "pos_", "root_", "priority_", "", "runtime_");
  }

  public static Names longNames() {
    return new Names("builder", "level", "marker", "pinned", "result", "parse", "pos", "type", "priority", "a", "runtime");
  }

  public static Names shortNames() {
    return new Names("b", "l", "m", "p", "r", "f", "c", "t", "g", "_", "s");
  }

  public static @NotNull Names forName(String name) {
    if ("long".equals(name)) return longNames();
    if ("short".equals(name)) return shortNames();
    if ("classic".equals(name)) return classicNames();
    return ApplicationManager.getApplication().isUnitTestMode() ? classicNames() : shortNames();
  }
}
