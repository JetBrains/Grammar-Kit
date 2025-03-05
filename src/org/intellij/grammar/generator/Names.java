/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

/**
* @author greg
*/
class Names {
  public final String stateHolder;
  public final String level;
  public final String marker;
  public final String pinned;
  public final String result;
  public final String parse;
  public final String pos;
  public final String root;
  public final String priority;
  public final String metaParamPrefix;
  public final String psiLocal = "p";

  private Names(String stateHolder,
                String level,
                String marker,
                String pinned,
                String result, String parse,
                String pos,
                String root,
                String priority,
                String metaParamPrefix) {
    this.stateHolder = stateHolder;
    this.level = level;
    this.marker = marker;
    this.pinned = pinned;
    this.result = result;
    this.parse = parse;
    this.pos = pos;
    this.root = root;
    this.priority = priority;
    this.metaParamPrefix = metaParamPrefix;
  }

  public static Names classicNames(Boolean useRuntime) {
    return new Names(useRuntime ? "runtime_" : "builder_", "level_", "marker_", "pinned_", "result_", "parse_", "pos_", "root_", "priority_", "");
  }

  public static Names longNames(Boolean useRuntime) {
    return new Names(useRuntime ? "runtime" : "builder", "level", "marker", "pinned", "result", "parse", "pos", "type", "priority", "a");
  }

  public static Names shortNames(Boolean useRuntime) {
    return new Names(useRuntime ? "s" : "b", "l", "m", "p", "r", "f", "c", "t", "g", "_");
  }

  public static @NotNull Names forName(String name, Boolean useRuntime) {
    if ("long".equals(name)) return longNames(useRuntime);
    if ("short".equals(name)) return shortNames(useRuntime);
    if ("classic".equals(name)) return classicNames(useRuntime);
    return ApplicationManager.getApplication().isUnitTestMode() ? classicNames(useRuntime) : shortNames(useRuntime);
  }
}
