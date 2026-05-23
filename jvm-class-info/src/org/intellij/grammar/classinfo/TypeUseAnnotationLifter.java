/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Moves type-use annotations off a declaration's annotation list onto the outermost {@link JvmTypeRef}
 * position. Used by both the syntax-mode pipeline (Java/Kotlin/ASM source providers) and the IDE
 * PSI pipeline so that {@code public @NotNull String foo()}-style declarations end up with the
 * nullness/`@Nls`/etc. annotation on the return type rather than on the method itself.
 * <p>
 * The decision of "is this FQN a type-use annotation?" is delegated to the caller via a
 * {@link Predicate} — the syntax path consults the symbol resolver's {@link ClassSymbol#annotationTargets},
 * the PSI path consults {@code AnnotationTargetUtil.findAnnotationTarget(..., TYPE_USE)}.
 */
public final class TypeUseAnnotationLifter {

  private TypeUseAnnotationLifter() {}

  public record LiftResult(@NotNull List<Fqn> declarationAnnotations, @NotNull JvmTypeRef type) {}

  /**
   * Partition {@code declarationAnnotations} by {@code isTypeUse}: matching FQNs are prepended to
   * the outermost annotation list of {@code type}, the rest stay on the declaration. Source order
   * is preserved within both partitions; lifted annotations land before any annotations already on
   * the type, since the lifted ones syntactically appear earlier in the declaration.
   * <p>
   * Lift is a no-op for {@link JvmTypeRef.PrimitiveType} and {@link JvmTypeRef.TypeVariable} — these
   * positions cannot carry annotations in the model. Their declaration-list annotations stay put.
   */
  public static @NotNull LiftResult lift(@NotNull List<Fqn> declarationAnnotations,
                                         @NotNull JvmTypeRef type,
                                         @NotNull Predicate<Fqn> isTypeUse) {
    if (declarationAnnotations.isEmpty()
        || type instanceof JvmTypeRef.PrimitiveType
        || type instanceof JvmTypeRef.TypeVariable) {
      return new LiftResult(declarationAnnotations, type);
    }
    List<Fqn> lifted = new SmartList<>();
    List<Fqn> remaining = new SmartList<>();
    for (Fqn f : declarationAnnotations) {
      if (isTypeUse.test(f)) lifted.add(f);
      else remaining.add(f);
    }
    if (lifted.isEmpty()) return new LiftResult(declarationAnnotations, type);
    return new LiftResult(remaining, prependAnnotations(type, lifted));
  }

  private static @NotNull JvmTypeRef prependAnnotations(@NotNull JvmTypeRef type,
                                                        @NotNull List<Fqn> extra) {
    if (type instanceof JvmTypeRef.UserType u) {
      return new JvmTypeRef.UserType(u.name(), concat(extra, u.annotations()), u.args());
    }
    if (type instanceof JvmTypeRef.ArrayType a) {
      return new JvmTypeRef.ArrayType(a.component(), concat(extra, a.annotations()));
    }
    if (type instanceof JvmTypeRef.FunctionType f) {
      return new JvmTypeRef.FunctionType(concat(extra, f.annotations()));
    }
    if (type instanceof JvmTypeRef.DynamicType d) {
      return new JvmTypeRef.DynamicType(concat(extra, d.annotations()));
    }
    return type;
  }

  private static @NotNull List<Fqn> concat(@NotNull List<Fqn> a, @NotNull List<Fqn> b) {
    if (a.isEmpty()) return b;
    if (b.isEmpty()) return a;
    List<Fqn> out = new ArrayList<>(a.size() + b.size());
    out.addAll(a);
    out.addAll(b);
    return out;
  }
}
