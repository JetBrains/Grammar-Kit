/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record TypeParameterSymbol(@Nullable String name,
                                  @NotNull List<String> extendsList,
                                  @NotNull List<Fqn> annotations) {

  public TypeParameterSymbol {
    extendsList = List.copyOf(extendsList);
    annotations = List.copyOf(annotations);
  }

  public static @NotNull TypeParameterSymbol of(@NotNull String name) {
    return new TypeParameterSymbol(name, List.of(), List.of());
  }

  public static final class Builder {
    public String name;
    public final List<String> extendsList = new SmartList<>();
    public final List<Fqn> annotations = new SmartList<>();

    public Builder() { }

    public Builder(@NotNull String name) {
      this.name = name;
    }

    public @NotNull TypeParameterSymbol build() {
      return new TypeParameterSymbol(name, extendsList, annotations);
    }
  }
}
