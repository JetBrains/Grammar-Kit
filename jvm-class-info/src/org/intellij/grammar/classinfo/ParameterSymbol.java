/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ParameterSymbol(@NotNull String name,
                              @NotNull JvmTypeRef type,
                              @NotNull List<Fqn> annotations) {

  public ParameterSymbol {
    annotations = List.copyOf(annotations);
  }

  @Override
  public @NotNull String toString() {
    return JvmTypeRefs.renderAnnotated(type) + " " + name;
  }

  public static final class Builder {
    public String name;
    /** Carries the per-position annotations used to derive both the plain and annotated rendering. */
    public JvmTypeRef type;
    public final List<Fqn> annotations = new SmartList<>();

    public @NotNull ParameterSymbol build() {
      return new ParameterSymbol(name, type, annotations);
    }
  }
}
