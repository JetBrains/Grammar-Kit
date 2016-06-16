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

import java.util.Locale;

/**
 * @author gregsh
 */
public enum Case {
  LOWER, UPPER, AS_IS, CAMEL;

  @NotNull
  public String apply(@NotNull String s) {
    if (s.isEmpty()) return s;
    switch (this) {
      case LOWER:
        return s.toLowerCase(Locale.ENGLISH);
      case UPPER:
        return s.toUpperCase(Locale.ENGLISH);
      case AS_IS:
        return s;
      case CAMEL:
        return s.substring(0, 1).toUpperCase(Locale.ENGLISH) +
               s.substring(1).toLowerCase(Locale.ENGLISH);
      default:
        throw new AssertionError();
    }
  }
}
