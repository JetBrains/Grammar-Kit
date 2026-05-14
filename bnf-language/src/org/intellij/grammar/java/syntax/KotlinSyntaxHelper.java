/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.kotlin.KotlinClassManager;
import org.intellij.grammar.java.AsmHelper;
import org.intellij.grammar.java.JavaHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

/**
 * {@link JavaHelper} that resolves classes and methods from Kotlin <i>source</i> files using the
 * {@code syntax-api} + {@code kotlin-syntax} libraries (no IntelliJ Platform / PSI dependency).
 * <p>
 * Designed to be chained with {@link JavaSyntaxHelper} so a single helper instance covers both
 * languages in mixed Java/Kotlin projects:
 * <pre>
 *     new KotlinSyntaxHelper(roots, new JavaSyntaxHelper(roots, new AsmHelper()));
 * </pre>
 * Kotlin source wins when both {@code Foo.kt} and {@code Foo.java} declare the same FQN under the
 * configured roots; in well-organised projects this collision does not occur. Classes outside the
 * configured roots flow through the fallback chain to {@link AsmHelper}.
 *
 * <h2>Kotlin → JVM modelling</h2>
 * Kotlin source is mapped onto the JVM-shaped {@link ClassInfo} /
 * {@link org.intellij.grammar.classinfo.MethodInfo} records that Grammar-Kit's parser generator
 * already consumes for Java:
 * <ul>
 *   <li>Classes (regular, sealed, data, annotation) → {@link ClassInfo} with translated JVM
 *       modifiers; Kotlin classes are {@code final} by default, so {@code FINAL} is set unless
 *       {@code open}/{@code abstract}/{@code sealed} or the declaration is an interface.</li>
 *   <li>{@code object} declarations → {@link ClassInfo} with {@code FINAL} modifier; instance
 *       members map to instance methods.</li>
 *   <li>{@code companion object} → its own {@link ClassInfo} (e.g. {@code Outer.Companion}); members
 *       annotated {@code @JvmStatic} are additionally copied onto the enclosing class as static
 *       methods, matching the bytecode layout.</li>
 *   <li>Properties → synthesised {@code getX()} / {@code setX(value)} methods on the declaring
 *       class. Private properties and {@code const val} are skipped.</li>
 *   <li>{@code typealias} → {@link ClassInfo} whose {@code superClass} is the aliased type, so
 *       {@code findClass} succeeds and callers walking {@code getSuperClassName} chain through.</li>
 *   <li>Top-level functions and properties → static members on a synthesised
 *       {@code <FileStem>Kt} class.</li>
 *   <li>Extension functions → static methods whose first parameter is the receiver type.</li>
 *   <li>{@code internal} visibility → mapped to {@code public} (bytecode reality; the name-mangling
 *       detail is invisible to Grammar-Kit).</li>
 *   <li>{@code inner} on a nested class clears the otherwise-default static modifier.</li>
 * </ul>
 *
 * <h2>Limitations (deferred)</h2>
 * <ul>
 *   <li>{@code @file:JvmName} — the file-class is always named {@code <FileStem>Kt}.</li>
 *   <li>{@code @JvmName} on members — the Kotlin name is used.</li>
 *   <li>{@code @JvmOverloads} — only the full-parameter overload is emitted.</li>
 *   <li>{@code is}-prefix convention for {@code Boolean} property accessors — always emits
 *       {@code getX} / {@code setX}.</li>
 *   <li>{@code data class} synthesis ({@code componentN}, {@code copy},
 *       {@code equals}/{@code hashCode}/{@code toString}).</li>
 *   <li>{@code suspend} continuation-parameter rewriting.</li>
 *   <li>{@code enum class} entries (the {@code Foo.ENTRY} static fields).</li>
 *   <li>{@code kotlin.jvm.functions.FunctionN} arity rendering — function types render as a
 *       placeholder {@code kotlin.Function}.</li>
 *   <li>Generic variance ({@code in} / {@code out}) on type arguments.</li>
 *   <li>Synthesised default no-arg primary constructor for classes that declare none.</li>
 *   <li>{@code kotlin.collections.*} / {@code kotlin.*} wildcard import resolution — covered by a
 *       hand-rolled allow-list of common names.</li>
 *   <li>{@code @JvmField} field synthesis — Grammar-Kit only inspects methods.</li>
 *   <li>{@code SUPER_TYPE_ENTRY} class-vs-interface disambiguation when no
 *       {@code SUPER_TYPE_CALL_ENTRY} is present — best-effort heuristic.</li>
 * </ul>
 */
public class KotlinSyntaxHelper extends JvmSyntaxHelperBase {

  public KotlinSyntaxHelper(@NotNull List<Path> sourceRoots) {
    this(sourceRoots, null);
  }

  public KotlinSyntaxHelper(@NotNull List<Path> sourceRoots, @Nullable JavaHelper fallback) {
    super(new KotlinClassManager(sourceRoots)::findClass, fallback);
  }

  /**
   * Package-private test seam — see {@link JvmSyntaxHelperBase}.
   */
  KotlinSyntaxHelper(@NotNull Function<String, ClassInfo> classLookup, @Nullable JavaHelper fallback) {
    super(classLookup, fallback);
  }
}
