/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java;

import com.intellij.util.SmartList;

import java.util.List;

class ClassInfo {
  final List<String> typeParameters = new SmartList<>();
  final List<String> interfaces = new SmartList<>();
  final List<String> annotations = new SmartList<>();
  final List<MethodInfo> methods = new SmartList<>();
  String name;
  String superClass;
  int modifiers;
}
