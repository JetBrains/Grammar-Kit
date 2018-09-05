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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum JavaVersion {
  JAVA_6,
  JAVA_8,
  ;

  @NotNull
  static JavaVersion fromString(@Nullable String s) {
    if ("6".equals(s)) return JAVA_6;
    if ("8".equals(s)) return JAVA_8;
    return JAVA_6;
  }
}
