/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ParameterSymbol(@NotNull String name,
                              @NotNull String type,
                              @Nullable String annotatedType,
                              @NotNull List<Fqn> annotations) {

  public ParameterSymbol {
    annotations = List.copyOf(annotations);
  }

  @Override
  public @NotNull String toString() {
    return (annotatedType != null ? annotatedType : type) + " " + name;
  }

  public static final class Builder {
    public String name;
    public String type;
    /** Defaults to {@link #type}; may carry inlined annotation FQNs, e.g. {@code java.lang.@A String}. */
    public String annotatedType;
    public final List<Fqn> annotations = new SmartList<>();

    public @NotNull ParameterSymbol build() {
      return new ParameterSymbol(name, type, annotatedType, annotations);
    }
  }
}
