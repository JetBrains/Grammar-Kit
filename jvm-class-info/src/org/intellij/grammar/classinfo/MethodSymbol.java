/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record MethodSymbol(@NotNull String name,
                           @NotNull Fqn declaringClass,
                           @NotNull MethodType methodType,
                           int modifiers,
                           @NotNull JvmTypeRef returnType,
                           @NotNull List<ParameterSymbol> parameters,
                           @NotNull List<TypeParameterSymbol> generics,
                           @NotNull List<Fqn> annotations,
                           @NotNull List<Fqn> exceptions) {

  public MethodSymbol {
    parameters = List.copyOf(parameters);
    generics = List.copyOf(generics);
    annotations = List.copyOf(annotations);
    exceptions = List.copyOf(exceptions);
  }

  @Override
  public @NotNull String toString() {
    return "MethodSymbol{" + name + "(" + parameters + "):" + JvmTypeRefs.renderPlain(returnType)
           + ", @" + annotations + "<" + generics + ">" + " throws " + exceptions + '}';
  }

  public static final class Builder {
    public String name;
    public Fqn declaringClass;
    public MethodType methodType;
    public int modifiers;
    /** Carries the per-position annotations used to derive both the plain and annotated rendering. */
    public JvmTypeRef returnType;
    public final List<ParameterSymbol.Builder> parameters = new SmartList<>();
    public final List<TypeParameterSymbol.Builder> generics = new SmartList<>();
    public final List<Fqn> annotations = new SmartList<>();
    public final List<Fqn> exceptions = new SmartList<>();

    public @NotNull MethodSymbol build() {
      List<ParameterSymbol> builtParams = new ArrayList<>(parameters.size());
      for (ParameterSymbol.Builder p : parameters) builtParams.add(p.build());
      List<TypeParameterSymbol> builtGenerics = new ArrayList<>(generics.size());
      for (TypeParameterSymbol.Builder t : generics) builtGenerics.add(t.build());
      return new MethodSymbol(name, declaringClass, methodType, modifiers,
                              returnType, builtParams, builtGenerics, annotations, exceptions);
    }
  }
}
