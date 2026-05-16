/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TypeParameterSymbol {
  private final String name;
  final List<String> extendsList;
  private final List<Fqn> annotations;

  public TypeParameterSymbol(@Nullable String name,
                           @NotNull List<String> extendsList,
                           @NotNull List<Fqn> annotations) {
    this.name = name;
    this.extendsList = extendsList;
    this.annotations = annotations;
  }

  public TypeParameterSymbol(@NotNull String name) {
    this(name, new SmartList<>(), new SmartList<>());
  }

  public String getName() {
    return name;
  }

  public List<String> getExtendsList() {
    return extendsList;
  }

  public List<Fqn> getAnnotations() {
    return annotations;
  }
}
