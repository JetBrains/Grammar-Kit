/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.convergence;

import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.MethodSymbol;
import org.intellij.grammar.classinfo.ParameterSymbol;
import org.intellij.grammar.classinfo.TypeParameterSymbol;
import org.intellij.grammar.java.syntax.ClassSymbolTextFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;

/**
 * Source ↔ ASM convergence comparator (audit task #33).
 * <p>
 * The two extractors produce {@link ClassSymbol}s for the same compiled source. Their outputs
 * diverge in known structural ways — ASM carries bytecode-only artifacts (bridge methods, synthetic
 * accessors, {@code <clinit>}); source carries the source-level shape. The comparator applies a
 * fixed set of normalizers to both sides before asserting text-level equality on the
 * {@link ClassSymbolTextFormatter#format formatted} output. When P2 tasks land (#10 filter
 * synthetics, #14 reverse {@code suspend} lowering, etc.) they will reduce the set of normalizers
 * needed here — the harness's job is to make those changes safe to make.
 * <p>
 * Normalizers always make the ASM side look more like source, never the other way around. Source
 * is the canonical view of the JVM-visible API surface; ASM is the lossy/lowered view that needs
 * cleanup to converge.
 */
final class SourceAsmConvergence {

  /**
   * {@code ACC_SUPER} class flag (0x20). Same bit value as {@code Modifier.SYNCHRONIZED}, but on
   * classes it has no source-level meaning — javac sets it on every modern class file. The text
   * formatter would otherwise render classes as "public synchronized".
   */
  private static final int ACC_SUPER = 0x0020;

  private SourceAsmConvergence() { }

  /**
   * Drop bytecode-only artifacts that don't have a source counterpart. After audit #10 landed,
   * the ASM provider drops {@code <clinit>} / {@code ACC_BRIDGE} / {@code ACC_SYNTHETIC} at the
   * extraction source — this normalizer only handles the residual divergences:
   * <ul>
   *   <li>javac's synthesized default constructor (public no-arg {@code <init>}) is emitted as a
   *       plain method, not {@code ACC_SYNTHETIC}, so it survives the source-side filter and
   *       must be stripped here.</li>
   *   <li>The class-level {@code ACC_SUPER} flag has no source counterpart but renders as
   *       "synchronized" via {@code Modifier.toString}; clear it here.</li>
   * </ul>
   */
  static @NotNull ClassSymbol dropBytecodeArtifacts(@NotNull ClassSymbol input) {
    ClassSymbol.Builder b = toBuilder(input);
    b.modifiers &= ~ACC_SUPER;
    b.methods.removeIf(SourceAsmConvergence::isSyntheticDefaultConstructor);
    return b.build();
  }

  /**
   * A javac-synthesized default constructor: public {@code <init>()} with no parameters and no
   * declared exceptions. Source extractors don't emit this because it isn't written in source.
   * (Heuristic: doesn't distinguish a user-written {@code public Foo() {}}, but for the harness's
   * happy paths that's fine — and adding an explicit constructor in a test fixture exercises a
   * different normalizer path anyway.)
   */
  private static boolean isSyntheticDefaultConstructor(@NotNull MethodSymbol.Builder m) {
    return "<init>".equals(m.name)
           && m.parameters.isEmpty()
           && m.exceptions.isEmpty()
           && (m.modifiers & java.lang.reflect.Modifier.PUBLIC) != 0;
  }

  /** Sort methods by (name, parameter-count) so declaration-order drift doesn't fail comparison. */
  static @NotNull ClassSymbol sortMembers(@NotNull ClassSymbol input) {
    ClassSymbol.Builder b = toBuilder(input);
    b.methods.sort(Comparator
                     .<MethodSymbol.Builder, String>comparing(m -> m.name == null ? "" : m.name)
                     .thenComparingInt(m -> m.parameters.size()));
    return b.build();
  }

  /**
   * Rewrite all parameter names to positional ({@code p0}, {@code p1}, …). The ASM provider names
   * parameters positionally because it doesn't currently parse the {@code MethodParameters}
   * attribute (compiled with {@code -parameters} or not); the source extractor uses the names the
   * user wrote. Parameter names aren't part of JVM-visible matching (the helper compares by type),
   * so the harness normalizes them away on both sides for textual equality.
   */
  static @NotNull ClassSymbol erasePositionalParameterNames(@NotNull ClassSymbol input) {
    ClassSymbol.Builder b = toBuilder(input);
    for (MethodSymbol.Builder m : b.methods) {
      for (int i = 0; i < m.parameters.size(); i++) {
        m.parameters.get(i).name = "p" + i;
      }
    }
    return b.build();
  }

  /** Render a single class via the shared formatter so divergences appear as a textual diff. */
  static @NotNull String renderForCompare(@NotNull Fqn fqn, @NotNull ClassSymbol symbol) {
    return ClassSymbolTextFormatter.format(Map.of(fqn, symbol));
  }

  /**
   * Copy an immutable {@link ClassSymbol} into a mutable {@link ClassSymbol.Builder}. The fresh
   * builder shares no state with the input — normalizers can mutate freely.
   */
  private static @NotNull ClassSymbol.Builder toBuilder(@NotNull ClassSymbol src) {
    ClassSymbol.Builder b = new ClassSymbol.Builder();
    b.name = src.name();
    b.superClass = src.superClass();
    b.modifiers = src.modifiers();
    b.multifileFacade = src.multifileFacade();
    b.typeParameters.addAll(src.typeParameters());
    b.interfaces.addAll(src.interfaces());
    b.annotations.addAll(src.annotations());
    b.annotationTargets.addAll(src.annotationTargets());
    for (MethodSymbol m : src.methods()) {
      b.methods.add(methodToBuilder(m));
    }
    return b;
  }

  private static @NotNull MethodSymbol.Builder methodToBuilder(@NotNull MethodSymbol m) {
    MethodSymbol.Builder mb = new MethodSymbol.Builder();
    mb.name = m.name();
    mb.declaringClass = m.declaringClass();
    mb.methodType = m.methodType();
    mb.modifiers = m.modifiers();
    mb.returnType = m.returnType();
    for (ParameterSymbol p : m.parameters()) {
      ParameterSymbol.Builder pb = new ParameterSymbol.Builder();
      pb.name = p.name();
      pb.type = p.type();
      pb.annotations.addAll(p.annotations());
      mb.parameters.add(pb);
    }
    for (TypeParameterSymbol t : m.generics()) {
      TypeParameterSymbol.Builder tb = new TypeParameterSymbol.Builder(t.name());
      tb.extendsList.addAll(t.extendsList());
      tb.annotations.addAll(t.annotations());
      mb.generics.add(tb);
    }
    mb.annotations.addAll(m.annotations());
    mb.exceptions.addAll(m.exceptions());
    return mb;
  }
}
