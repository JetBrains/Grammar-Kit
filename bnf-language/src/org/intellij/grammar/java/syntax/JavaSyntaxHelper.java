/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import org.intellij.grammar.java.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

/**
 * {@link JavaHelper} that resolves classes and methods from Java <i>source</i> files using the
 * {@code syntax-api} + {@code java-syntax} libraries (no IntelliJ Platform / PSI dependency).
 * <p>
 * The {@link JavaHelper} surface is inherited from {@link JvmSyntaxHelperBase}; this class only
 * wires the {@link JavaClassManager} as the source of {@link ClassInfo} records. FQN → source-file
 * mapping, parsing, tree walking, and caching are delegated to {@link JavaClassManager}; the
 * {@link MyElement}-based delegate readers come from {@link ClassInfoUtil}.
 * <p>
 * Used from the headless CLI when the {@code --source-psi} flag is set; the IDE continues to use
 * {@link PsiHelper}. A {@code fallback} helper (typically {@link AsmHelper}) handles classes that
 * live outside the configured source roots — IntelliJ Platform jars, third-party dependencies —
 * so a single helper covers both project sources and bytecode-only references.
 */
public class JavaSyntaxHelper extends JvmSyntaxHelperBase {

  public JavaSyntaxHelper(@NotNull List<Path> sourceRoots) {
    this(sourceRoots, null);
  }

  public JavaSyntaxHelper(@NotNull List<Path> sourceRoots, @Nullable JavaHelper fallback) {
    super(new JavaClassManager(sourceRoots)::findClass, fallback);
  }

  /**
   * Package-private test seam — see {@link JvmSyntaxHelperBase#JvmSyntaxHelperBase(Function, JavaHelper)}.
   */
  JavaSyntaxHelper(@NotNull Function<String, ClassInfo> classLookup, @Nullable JavaHelper fallback) {
    super(classLookup, fallback);
  }
}
