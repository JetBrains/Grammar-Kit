/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;

import java.util.List;

public class ParameterSymbol {
  public String name;
  public String type;
  /** Defaults to {@link #type}; may carry inlined annotation FQNs, e.g. {@code java.lang.@A String}. */
  public String annotatedType;
  public final List<Fqn> annotations = new SmartList<>();

  @Override
  public String toString() {
    return (annotatedType != null ? annotatedType : type) + " " + name;
  }
}
