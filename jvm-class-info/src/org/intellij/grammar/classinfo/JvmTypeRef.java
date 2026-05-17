/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Canonical type representation used inside {@link ClassSymbol} / {@link MethodSymbol} /
 * {@link ParameterSymbol} / {@link TypeParameterSymbol}. Each node carries the annotations that
 * apply at its own position; rendering with {@link JvmTypeRefs#renderAnnotated} walks the tree
 * and prefixes each annotation as {@code @<fqn> }. The plain rendering ({@link JvmTypeRefs#renderPlain})
 * ignores those lists and reproduces the dotted type string consumed by the rest of the codebase.
 * <p>
 * The model is shared across Kotlin source, Java source, and ASM bytecode providers, so the same
 * structural comparison applies regardless of how the symbol was loaded. Built-in primitives use
 * the JVM names ({@code int}, {@code long}, ...); Kotlin {@code Int}/{@code Unit}/etc. are mapped
 * at parse time. {@code Array<X>} is represented as {@link ArrayType}; primitive arrays
 * ({@code IntArray}) are also {@link ArrayType} wrapping a {@link PrimitiveType} component.
 */
public sealed interface JvmTypeRef
    permits JvmTypeRef.UserType, JvmTypeRef.ArrayType, JvmTypeRef.PrimitiveType,
            JvmTypeRef.TypeVariable, JvmTypeRef.FunctionType, JvmTypeRef.DynamicType {

  /** Annotations that apply at this type position, in declaration order. Never null; may be empty. */
  @NotNull List<Fqn> annotations();

  /**
   * Named reference type ({@code java.util.List<...>}, {@code com.foo.Bar}, etc.).
   * Inner class names are stored already-dotted via {@link Fqn} convention.
   */
  record UserType(@NotNull Fqn name,
                  @NotNull List<Fqn> annotations,
                  @NotNull List<TypeProjection> args) implements JvmTypeRef {
    public UserType {
      annotations = List.copyOf(annotations);
      args = List.copyOf(args);
    }
  }

  /** {@code component[]} — covers both Kotlin {@code Array<X>} and primitive arrays. */
  record ArrayType(@NotNull JvmTypeRef component,
                   @NotNull List<Fqn> annotations) implements JvmTypeRef {
    public ArrayType {
      annotations = List.copyOf(annotations);
    }
  }

  /**
   * JVM primitive — {@code int}, {@code long}, {@code void}, etc. Never carries annotations.
   * Kotlin {@code Int?} boxes to a {@link UserType} of {@code java.lang.Integer} at parse time.
   */
  record PrimitiveType(@NotNull String name) implements JvmTypeRef {
    @Override
    public @NotNull List<Fqn> annotations() { return List.of(); }
  }

  /** Bare type-variable reference ({@code T}). Never carries annotations in our model. */
  record TypeVariable(@NotNull String name) implements JvmTypeRef {
    @Override
    public @NotNull List<Fqn> annotations() { return List.of(); }
  }

  /** Kotlin {@code FunctionN<...>} reference rendered as the raw {@code kotlin.Function} type. */
  record FunctionType(@NotNull List<Fqn> annotations) implements JvmTypeRef {
    public FunctionType {
      annotations = List.copyOf(annotations);
    }
  }

  /** Kotlin {@code dynamic} reference rendered as {@code java.lang.Object}. */
  record DynamicType(@NotNull List<Fqn> annotations) implements JvmTypeRef {
    public DynamicType {
      annotations = List.copyOf(annotations);
    }
  }
}
