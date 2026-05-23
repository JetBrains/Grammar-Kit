/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.convergence;

import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.asm.AsmClassSymbolProvider;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Source ↔ ASM convergence tests (audit task #33).
 * <p>
 * Each test method compiles a Java source string in-process, runs the source-extraction pipeline
 * and the ASM-extraction pipeline against the same code, applies a chain of normalizers to the
 * ASM side, and asserts text-level equality on the formatted {@link ClassSymbol}.
 * <p>
 * Adding a P2 normalization fix (#10 / #11 / #12 / #13 / #14 / #15 / #16) generally means:
 * <ol>
 *   <li>Add a test case here that fails before the fix (or extend an existing one).</li>
 *   <li>Land the fix in the source / ASM provider.</li>
 *   <li>Drop or simplify the corresponding normalizer in
 *       {@link SourceAsmConvergence} — the convergence should hold without it.</li>
 * </ol>
 * The Kotlin counterpart is deferred: it needs the kotlin-compiler-embeddable dependency
 * (significant artifact) plus the Kotlin-side normalizers (suspend lowering, $default filter,
 * @JvmField, …). Java-first establishes the structural pattern.
 * <p>
 * <b>JDK requirement.</b> The harness needs a {@code javax.tools.JavaCompiler}.
 * {@link InProcessJavaCompiler#resolveCompiler} falls back to instantiating
 * {@code com.sun.tools.javac.api.JavacTool} via the platform classloader when the standard
 * {@link javax.tools.ToolProvider#getSystemJavaCompiler} returns null (the typical situation under
 * IntelliJ's {@code PathClassLoader}-based test runtime, even though the bundled JBR has the
 * {@code jdk.compiler} module).
 */
public class SourceAsmConvergenceTest extends TestCase {

  private Path sourceRoot;
  private Path classOutput;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    sourceRoot = Files.createTempDirectory("convergence-src");
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      if (sourceRoot != null) deleteRecursively(sourceRoot);
      if (classOutput != null) deleteRecursively(classOutput);
    }
    finally {
      super.tearDown();
    }
  }

  // ---------------------------------------------------------------------------------------------
  // tests
  // ---------------------------------------------------------------------------------------------

  public void testEmptyClass() throws Exception {
    assertConverges("pkg/Empty.java", """
      package pkg;
      public class Empty {}
      """, "pkg.Empty");
  }

  public void testClassWithInstanceMethod() throws Exception {
    assertConverges("pkg/Greeter.java", """
      package pkg;
      public class Greeter {
        public String greet(String name) { return "hi " + name; }
      }
      """, "pkg.Greeter");
  }

  public void testClassWithStaticMethod() throws Exception {
    assertConverges("pkg/Util.java", """
      package pkg;
      public class Util {
        public static int squared(int n) { return n * n; }
      }
      """, "pkg.Util");
  }

  public void testGenericMethod() throws Exception {
    assertConverges("pkg/Box.java", """
      package pkg;
      public class Box<T> {
        public <U extends Number> U firstOf(U a, U b) { return a; }
      }
      """, "pkg.Box");
  }

  public void testGenericErasureBridgeIsFiltered() throws Exception {
    // Implementing a generic interface produces a bridge method at the bytecode level:
    //   `int compareTo(Object)` bridges to `int compareTo(IntBox)`. The bridge is marked
    // ACC_BRIDGE | ACC_SYNTHETIC and has no source counterpart. The ASM provider must drop it
    // (audit #10) so the method list converges with what the source extractor produced.
    // No @Override here on purpose: @Override is RetentionPolicy.SOURCE so the source extractor
    // surfaces it but ASM doesn't. That's a separate normalization concern, not the bridge one.
    assertConverges("pkg/IntBox.java", """
      package pkg;
      public class IntBox implements Comparable<IntBox> {
        public int compareTo(IntBox other) { return 0; }
      }
      """, "pkg.IntBox");
  }

  public void testStaticInitializerIsFiltered() throws Exception {
    // A class with a static initializer block produces a <clinit> method at the bytecode level.
    // No source counterpart — ASM provider must filter it (audit #10).
    assertConverges("pkg/Constants.java", """
      package pkg;
      public class Constants {
        public static final int X;
        static { X = 42; }
      }
      """, "pkg.Constants");
  }

  // ---------------------------------------------------------------------------------------------
  // assertConverges
  // ---------------------------------------------------------------------------------------------

  private void assertConverges(@NotNull String pathFromSourceRoot,
                               @NotNull String src,
                               @NotNull String fqnUnderTest) throws Exception {
    writeSource(pathFromSourceRoot, src);
    classOutput = InProcessJavaCompiler.compile(Map.of(pathFromSourceRoot, src));

    Fqn fqn = Fqn.of(fqnUnderTest);
    ClassSymbol fromSource = extractFromSource(fqn);
    ClassSymbol fromAsm = extractFromAsm(fqn);
    assertNotNull("source-side extraction must produce a ClassSymbol for " + fqnUnderTest, fromSource);
    assertNotNull("ASM-side extraction must produce a ClassSymbol for " + fqnUnderTest, fromAsm);

    UnaryOperator<ClassSymbol> normalizeAsm = sym -> {
      sym = SourceAsmConvergence.dropBytecodeArtifacts(sym);
      sym = SourceAsmConvergence.erasePositionalParameterNames(sym);
      sym = SourceAsmConvergence.sortMembers(sym);
      return sym;
    };
    UnaryOperator<ClassSymbol> normalizeSource = sym -> {
      sym = SourceAsmConvergence.erasePositionalParameterNames(sym);
      sym = SourceAsmConvergence.sortMembers(sym);
      return sym;
    };
    ClassSymbol normalizedAsm = normalizeAsm.apply(fromAsm);
    ClassSymbol normalizedSource = normalizeSource.apply(fromSource);

    String expected = SourceAsmConvergence.renderForCompare(fqn, normalizedSource);
    String actual = SourceAsmConvergence.renderForCompare(fqn, normalizedAsm);
    assertEquals("source / ASM divergence for " + fqnUnderTest, expected, actual);
  }

  // ---------------------------------------------------------------------------------------------
  // extraction
  // ---------------------------------------------------------------------------------------------

  private @NotNull ClassSymbol extractFromSource(@NotNull Fqn fqn) {
    JavaSyntaxClassSymbolProvider provider = new JavaSyntaxClassSymbolProvider(List.of(sourceRoot));
    // SymbolResolver only used for cross-file lookups; tests are single-file, so any miss returns null.
    Map<Fqn, ClassSymbol> batch = provider.resolve(fqn, ignored -> null);
    return batch.get(fqn);
  }

  private @NotNull ClassSymbol extractFromAsm(@NotNull Fqn fqn) throws Exception {
    try (URLClassLoader cl = new URLClassLoader(new URL[]{classOutput.toUri().toURL()},
                                                /*parent=*/null)) {
      return AsmClassSymbolProvider.findClassSafe(fqn, cl);
    }
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private void writeSource(@NotNull String pathFromSourceRoot, @NotNull String content) throws IOException {
    Path target = sourceRoot.resolve(pathFromSourceRoot);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }

  private static void deleteRecursively(@NotNull Path root) throws IOException {
    if (!Files.exists(root)) return;
    try (Stream<Path> walk = Files.walk(root)) {
      walk.sorted((a, b) -> b.getNameCount() - a.getNameCount())
          .forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignored) { } });
    }
  }

}
