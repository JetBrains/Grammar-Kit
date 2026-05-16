/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;

import java.util.List;

public class ClassSymbol {
  public final List<String> typeParameters = new SmartList<>();
  public final List<Fqn> interfaces = new SmartList<>();
  public final List<Fqn> annotations = new SmartList<>();
  public final List<MethodSymbol> methods = new SmartList<>();
  public Fqn name;
  public Fqn superClass;
  public int modifiers;
  /** Kotlin facade synthesised from a {@code @file:JvmMultifileClass}-annotated file: callables from sibling files with the same JVM name merge into one entry. */
  public boolean multifileFacade;
}
