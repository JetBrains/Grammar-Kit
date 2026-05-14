/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;

import java.util.List;

public class ClassInfo {
  public final List<String> typeParameters = new SmartList<>();
  public final List<Fqn> interfaces = new SmartList<>();
  public final List<Fqn> annotations = new SmartList<>();
  public final List<MethodInfo> methods = new SmartList<>();
  public Fqn name;
  public Fqn superClass;
  public int modifiers;
}
