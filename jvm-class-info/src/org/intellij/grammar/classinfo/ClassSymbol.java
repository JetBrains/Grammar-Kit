/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable view of a JVM class. {@code multifileFacade} marks a Kotlin facade synthesised from a
 * {@code @file:JvmMultifileClass}-annotated file: callables from sibling files with the same JVM
 * name merge into one entry.
 */
public record ClassSymbol(@NotNull Fqn name,
                          @Nullable Fqn superClass,
                          int modifiers,
                          boolean multifileFacade,
                          @NotNull List<String> typeParameters,
                          @NotNull List<Fqn> interfaces,
                          @NotNull List<Fqn> annotations,
                          @NotNull List<MethodSymbol> methods) {

  public ClassSymbol {
    typeParameters = List.copyOf(typeParameters);
    interfaces = List.copyOf(interfaces);
    annotations = List.copyOf(annotations);
    methods = List.copyOf(methods);
  }

  public static final class Builder {
    public Fqn name;
    public Fqn superClass;
    public int modifiers;
    public boolean multifileFacade;
    public final List<String> typeParameters = new SmartList<>();
    public final List<Fqn> interfaces = new SmartList<>();
    public final List<Fqn> annotations = new SmartList<>();
    public final List<MethodSymbol.Builder> methods = new SmartList<>();

    public @NotNull ClassSymbol build() {
      List<MethodSymbol> builtMethods = new ArrayList<>(methods.size());
      for (MethodSymbol.Builder m : methods) builtMethods.add(m.build());
      return new ClassSymbol(name, superClass, modifiers, multifileFacade,
                             typeParameters, interfaces, annotations, builtMethods);
    }
  }
}
