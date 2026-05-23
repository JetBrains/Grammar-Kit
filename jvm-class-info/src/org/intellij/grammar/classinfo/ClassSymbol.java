/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable view of a JVM class. {@code multifileFacade} marks a Kotlin facade synthesised from a
 * {@code @file:JvmMultifileClass}-annotated file: callables from sibling files with the same JVM
 * name merge into one entry.
 * <p>
 * {@code annotationTargets} is non-empty only for annotation types — the parsed {@code @Target}
 * value set, or the JLS default targets (everything except {@link TargetType#TYPE_USE} and
 * {@link TargetType#TYPE_PARAMETER}) when {@code @Target} is absent. Consumed by
 * {@link TypeUseAnnotationLifter} to decide whether a declaration-position annotation should be
 * lifted onto a type.
 */
public record ClassSymbol(@NotNull Fqn name,
                          @Nullable Fqn superClass,
                          int modifiers,
                          boolean multifileFacade,
                          @NotNull List<String> typeParameters,
                          @NotNull List<Fqn> interfaces,
                          @NotNull List<Fqn> annotations,
                          @NotNull Set<TargetType> annotationTargets,
                          @NotNull List<MethodSymbol> methods) {

  public ClassSymbol {
    typeParameters = List.copyOf(typeParameters);
    interfaces = List.copyOf(interfaces);
    annotations = List.copyOf(annotations);
    annotationTargets = annotationTargets.isEmpty() ? Set.of() : Set.copyOf(annotationTargets);
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
    public final EnumSet<TargetType> annotationTargets = EnumSet.noneOf(TargetType.class);
    public final List<MethodSymbol.Builder> methods = new SmartList<>();

    public @NotNull ClassSymbol build() {
      List<MethodSymbol> builtMethods = new ArrayList<>(methods.size());
      for (MethodSymbol.Builder m : methods) builtMethods.add(m.build());
      return new ClassSymbol(name, superClass, modifiers, multifileFacade,
                             typeParameters, interfaces, annotations, annotationTargets, builtMethods);
    }
  }
}
