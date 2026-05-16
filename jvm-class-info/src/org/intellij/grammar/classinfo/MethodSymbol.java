/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;

import java.util.List;

public class MethodSymbol {
  public String returnType;
  /** Defaults to {@link #returnType}; may carry inlined annotation FQNs, e.g. {@code java.lang.@A String}. */
  public String annotatedReturnType;
  public final List<ParameterSymbol> parameters = new SmartList<>();
  public final List<Fqn> annotations = new SmartList<>();
  public final List<TypeParameterInfo> generics = new SmartList<>();
  public final List<Fqn> exceptions = new SmartList<>();
  public MethodType methodType;
  public String name;
  public Fqn declaringClass;
  public int modifiers;

  @Override
  public String toString() {
    return "MethodSymbol{" + name + "(" + parameters + "):" + returnType + ", @" + annotations + "<" + generics + ">" + " throws " + exceptions + '}';
  }
}
