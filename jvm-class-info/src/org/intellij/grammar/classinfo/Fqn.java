/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.classinfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Canonical fully-qualified class name in dotted source form: {@code com.foo.Outer.Inner} — never
 * {@code com/foo/Outer$Inner}. Wraps a single {@link String} so the type system can tell an FQN
 * apart from arbitrary text (parameter names, generic type expressions, simple identifiers,
 * package names, etc.) and so the recurring dotted-name surgery has one home.
 */
public record Fqn(@NotNull String value) {

  /** The empty FQN — used as the "no enclosing class" sentinel when walking class trees. */
  public static final Fqn ROOT = new Fqn("");

  /** Canonical {@code java.lang.Object} — implicit superclass for non-interface classes. */
  public static final Fqn JAVA_LANG_OBJECT = new Fqn("java.lang.Object");

  public Fqn {
    Objects.requireNonNull(value);
  }

  /** Build from an already-canonical dotted source-form name. No normalisation performed. */
  public static @NotNull Fqn of(@NotNull String dotted) {
    return new Fqn(dotted);
  }

  /** Tolerant factory used at module boundaries: returns {@code null} for null/empty input. */
  public static @Nullable Fqn ofNullable(@Nullable String dotted) {
    return dotted == null || dotted.isEmpty() ? null : new Fqn(dotted);
  }

  /** Normalise a bytecode-style name ({@code com/foo/Outer$Inner}) into canonical dotted form. */
  public static @NotNull Fqn fromBytecode(@NotNull String bytecodeName) {
    return new Fqn(bytecodeName.replace('/', '.').replace('$', '.'));
  }

  /** Append a simple name. An empty receiver yields the bare simple name. */
  public @NotNull Fqn child(@NotNull String simpleName) {
    return new Fqn(value.isEmpty() ? simpleName : value + "." + simpleName);
  }

  /** Last dotted segment ({@code com.foo.Bar} → {@code Bar}). Returns the full value if no dot. */
  public @NotNull String simpleName() {
    int dot = value.lastIndexOf('.');
    return dot < 0 ? value : value.substring(dot + 1);
  }

  /** Strip the last segment ({@code com.foo.Bar} → {@code com.foo}; {@code Bar} → empty Fqn). */
  public @NotNull Fqn parent() {
    int dot = value.lastIndexOf('.');
    return dot < 0 ? new Fqn("") : new Fqn(value.substring(0, dot));
  }

  public boolean isEmpty() {
    return value.isEmpty();
  }

  public boolean contains(char c) {
    return value.indexOf(c) >= 0;
  }

  public boolean startsWith(@NotNull String prefix) {
    return value.startsWith(prefix);
  }

  @Override
  public @NotNull String toString() {
    return value;
  }
}
