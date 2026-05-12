/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.util.SmartList;
import com.intellij.util.containers.FactoryMap;

import java.util.List;
import java.util.Map;

public class MethodInfo {
  public final List<String> types = new SmartList<>();
  public final List<String> annotatedTypes = new SmartList<>();

  /**
   * 0 corresponds to the annotations of the method itself.
   * i corresponds to the annotations of the parameter at index i-1.
   */
  public final Map<Integer, List<String>> annotations = FactoryMap.create(o -> new SmartList<>());
  public final List<TypeParameterInfo> generics = new SmartList<>();
  public final List<String> exceptions = new SmartList<>();
  public JavaHelper.MethodType methodType;
  public String name;
  public String declaringClass;
  public int modifiers;

  @Override
  public String toString() {
    return "MethodInfo{" + name + types + ", @" + annotations.get(0) + "<" + generics + ">" + " throws " + exceptions + '}';
  }
}
