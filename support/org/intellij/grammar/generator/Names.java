/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

/**
* @author greg
*/
class Names {
  public final String builder;
  public final String level;
  public final String marker;
  public final String pinned;
  public final String result;
  public final String pos;
  public final String root;
  public final String priority;
  public final String argPrefix;
  public final String psiLocal = "p";

  private Names(String builder,
                String level,
                String marker,
                String pinned,
                String result,
                String pos,
                String root,
                String priority,
                String argPrefix) {
    this.builder = builder;
    this.level = level;
    this.marker = marker;
    this.pinned = pinned;
    this.result = result;
    this.pos = pos;
    this.root = root;
    this.priority = priority;
    this.argPrefix = argPrefix;
  }

  public static Names classicNames() {
    return new Names("builder_", "level_", "marker_", "pinned_", "result_", "pos_", "root_", "priority_", "");
  }

  public static Names longNames() {
    return new Names("builder", "level", "marker", "pinned", "result", "pos", "type", "priority", "a");
  }

  public static Names shortNames() {
    return new Names("b", "l", "m", "p", "r", "c", "t", "g", "_");
  }

  @NotNull
  public static Names forName(String name) {
    if ("long".equals(name)) return longNames();
    if ("short".equals(name)) return shortNames();
    if ("classic".equals(name)) return classicNames();
    return ApplicationManager.getApplication().isUnitTestMode() ? classicNames() : shortNames();
  }
}
