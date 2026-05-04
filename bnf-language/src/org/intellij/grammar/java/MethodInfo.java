/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.util.SmartList;
import com.intellij.util.containers.FactoryMap;

import java.util.List;
import java.util.Map;

class MethodInfo {
  final List<String> types = new SmartList<>();
  final List<String> annotatedTypes = new SmartList<>();

  /**
   * 0 corresponds to the annotations of the method itself.
   * i corresponds to the annotations of the parameter at index i-1.
   */
  final Map<Integer, List<String>> annotations = FactoryMap.create(o -> new SmartList<>());
  final List<TypeParameterInfo> generics = new SmartList<>();
  final List<String> exceptions = new SmartList<>();
  JavaHelper.MethodType methodType;
  String name;
  String declaringClass;
  int modifiers;

  @Override
  public String toString() {
    return "MethodInfo{" + name + types + ", @" + annotations.get(0) + "<" + generics + ">" + " throws " + exceptions + '}';
  }
}
