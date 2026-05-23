/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Renderers for {@link JvmTypeRef} — produce the dotted-type strings consumed by
 * {@link ClassSymbol} / {@link MethodSymbol} text-formatting and downstream generator code.
 * <p>
 * {@link #renderPlain} reproduces the form that string-based providers used to emit (no inline
 * {@code @<fqn>} annotations). {@link #renderAnnotated} additionally inlines every annotation
 * carried on each type-tree node, in declaration order, as {@code @<fqn> } prefixes.
 * {@link #rawFqn} surfaces a single {@link Fqn} for places that only need the underlying class
 * name (supertypes, annotation types, typealias right-hand sides).
 */
public final class JvmTypeRefs {

  private static final Logger LOG = Logger.getInstance(JvmTypeRefs.class);

  private JvmTypeRefs() { }

  /**
   * Single source of truth for the "extractor couldn't determine a type" placeholder. Returns a
   * {@link JvmTypeRef.UserType} carrying {@link Fqn#MISSING}; logs the {@code reason} at WARN so
   * any future leak from a defensive parse-error branch becomes visible. Callers should pass a
   * short description plus source position so the log is diagnostic.
   *
   * @see #missingFqn for the {@link Fqn}-only variant.
   */
  public static @NotNull JvmTypeRef missingType(@NotNull String reason) {
    logMissing(reason);
    return new JvmTypeRef.UserType(Fqn.MISSING, List.of(), List.of());
  }

  /** {@link Fqn}-only counterpart of {@link #missingType}. Same logging, same {@link Fqn#MISSING}. */
  public static @NotNull Fqn missingFqn(@NotNull String reason) {
    logMissing(reason);
    return Fqn.MISSING;
  }

  private static void logMissing(@NotNull String reason) {
    LOG.warn("<Missing type> stub produced: " + reason);
  }

  public static @NotNull String renderPlain(@NotNull JvmTypeRef ref) {
    StringBuilder sb = new StringBuilder();
    appendPlain(sb, ref);
    return sb.toString();
  }

  public static @NotNull String renderAnnotated(@NotNull JvmTypeRef ref) {
    StringBuilder sb = new StringBuilder();
    appendAnnotated(sb, ref);
    return sb.toString();
  }

  /**
   * Underlying class FQN — strips generics, array wrappers, and annotations. For
   * {@link JvmTypeRef.PrimitiveType} / {@link JvmTypeRef.TypeVariable} returns the JVM-keyword /
   * type-variable name wrapped as an {@link Fqn} (the existing convention from the previous
   * {@code formatTypeFqn} implementation).
   */
  public static @NotNull Fqn rawFqn(@NotNull JvmTypeRef ref) {
    if (ref instanceof JvmTypeRef.UserType u) {
      return u.name();
    }
    else if (ref instanceof JvmTypeRef.ArrayType a) {
      return rawFqn(a.component());
    }
    else if (ref instanceof JvmTypeRef.PrimitiveType p) {
      return Fqn.of(p.name());
    }
    else if (ref instanceof JvmTypeRef.TypeVariable t) {
      return Fqn.of(t.name());
    }
    else if (ref instanceof JvmTypeRef.FunctionType) {
      return Fqn.of("kotlin.Function");
    }
    else if (ref instanceof JvmTypeRef.DynamicType) {
      return Fqn.of("java.lang.Object");
    }
    throw new IllegalArgumentException();
  }

  /**
   * True when {@link #renderPlain} and {@link #renderAnnotated} disagree on {@code ref} — i.e. any
   * node anywhere in the tree carries a non-empty annotations list. Used by the test text-formatter
   * to decide whether to emit the {@code annotatedTypes:} line.
   */
  public static boolean hasInlineAnnotations(@NotNull JvmTypeRef ref) {
    if (!ref.annotations().isEmpty()) return true;
    if (ref instanceof JvmTypeRef.UserType u) {
      for (TypeProjection p : u.args()) {
        if (p instanceof TypeProjection.WithVariance wv && hasInlineAnnotations(wv.type())) return true;
      }
      return false;
    }
    if (ref instanceof JvmTypeRef.ArrayType a) return hasInlineAnnotations(a.component());
    return false;
  }

  /**
   * Wrap a pre-formatted type-expression string as an opaque {@link JvmTypeRef.UserType}. Used by
   * generator/PSI consumers that don't have a structured source for the type (e.g. types coming
   * back from {@link com.intellij.psi.PsiType#getCanonicalText(boolean)}). The plain and annotated
   * renderings both reproduce {@code typeExpression} verbatim — there is no per-position annotation
   * information to inline.
   */
  public static @NotNull JvmTypeRef raw(@NotNull String typeExpression) {
    return new JvmTypeRef.UserType(Fqn.of(typeExpression), List.of(), List.of());
  }

  /**
   * Structural equality for type comparisons — record {@code equals} already does this; this helper
   * exists so callers in the bnf-language module don't have to import the record types directly.
   */
  public static boolean equal(@NotNull JvmTypeRef a, @NotNull JvmTypeRef b) {
    return Objects.equals(a, b);
  }

  // ---------------------------------------------------------------------------------------------
  // internals
  // ---------------------------------------------------------------------------------------------

  private static void appendPlain(@NotNull StringBuilder sb, @NotNull JvmTypeRef ref) {
    if (ref instanceof JvmTypeRef.UserType u) {
      sb.append(u.name().value());
      if (!u.args().isEmpty()) appendArgs(sb, u.args(), false);
    }
    else if (ref instanceof JvmTypeRef.ArrayType a) {
      appendPlain(sb, a.component());
      sb.append("[]");
    }
    else if (ref instanceof JvmTypeRef.PrimitiveType p) {
      sb.append(p.name());
    }
    else if (ref instanceof JvmTypeRef.TypeVariable t) {
      sb.append(t.name());
    }
    else if (ref instanceof JvmTypeRef.FunctionType) {
      sb.append("kotlin.Function");
    }
    else if (ref instanceof JvmTypeRef.DynamicType) {
      sb.append("java.lang.Object");
    }
  }

  private static void appendAnnotated(@NotNull StringBuilder sb, @NotNull JvmTypeRef ref) {
    if (ref instanceof JvmTypeRef.UserType u) {
      appendAnnotations(sb, u.annotations());
      sb.append(u.name().value());
      if (!u.args().isEmpty()) appendArgs(sb, u.args(), true);
    }
    else if (ref instanceof JvmTypeRef.ArrayType a) {
      appendAnnotated(sb, a.component());
      // JLS form: type-use annotations on an array go BETWEEN the component and the brackets, so
      // `T @A []` rather than `@A T[]`. The component already rendered its own annotations.
      if (!a.annotations().isEmpty()) {
        sb.append(' ');
        appendAnnotations(sb, a.annotations());
      }
      sb.append("[]");
    }
    else if (ref instanceof JvmTypeRef.PrimitiveType p) {
      sb.append(p.name());
    }
    else if (ref instanceof JvmTypeRef.TypeVariable t) {
      appendAnnotations(sb, t.annotations());
      sb.append(t.name());
    }
    else if (ref instanceof JvmTypeRef.FunctionType f) {
      appendAnnotations(sb, f.annotations());
      sb.append("kotlin.Function");
    }
    else if (ref instanceof JvmTypeRef.DynamicType d) {
      appendAnnotations(sb, d.annotations());
      sb.append("java.lang.Object");
    }
  }

  private static void appendAnnotations(@NotNull StringBuilder sb, @NotNull List<Fqn> annotations) {
    for (Fqn anno : annotations) {
      sb.append('@').append(anno.value()).append(' ');
    }
  }

  private static void appendArgs(@NotNull StringBuilder sb,
                                 @NotNull List<TypeProjection> args,
                                 boolean annotated) {
    sb.append('<');
    for (int i = 0; i < args.size(); i++) {
      if (i > 0) sb.append(", ");
      appendProjection(sb, args.get(i), annotated);
    }
    sb.append('>');
  }

  private static void appendProjection(@NotNull StringBuilder sb,
                                       @NotNull TypeProjection projection,
                                       boolean annotated) {
    if (projection instanceof TypeProjection.Star) {
      sb.append('?');
      return;
    }
    if (projection instanceof TypeProjection.WithVariance p) {
      switch (p.variance()) {
        case INVARIANT -> { }
        case OUT -> sb.append("? extends ");
        case IN -> sb.append("? super ");
      }
      if (annotated) appendAnnotated(sb, p.type());
      else appendPlain(sb, p.type());
    }
  }
}
