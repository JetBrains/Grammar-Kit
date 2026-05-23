/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.convergence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Compiles a small batch of in-memory Java sources to a fresh temp directory using the system
 * {@link JavaCompiler}. Used by the source ↔ ASM convergence harness ({@code #33} in AUDIT.md) to
 * exercise the same source through both the source-extraction pipeline and the bytecode-extraction
 * pipeline.
 * <p>
 * Compiles with {@code -parameters} so parameter names survive into the class file — that lets the
 * ASM-extracted {@link org.intellij.grammar.classinfo.ParameterSymbol} carry the same name as the
 * source-extracted symbol, enabling exact text-level comparison.
 */
final class InProcessJavaCompiler {

  private InProcessJavaCompiler() { }

  /**
   * Compile {@code sources} (mapping path-relative-to-source-root to source text) to a fresh
   * directory. Returns the output directory.
   *
   * @throws IllegalStateException if no system Java compiler is available (e.g. running on a JRE),
   *                               or if compilation fails — the message includes the diagnostics.
   */
  static @NotNull Path compile(@NotNull Map<String, String> sources) throws IOException {
    JavaCompiler compiler = resolveCompiler();
    if (compiler == null) {
      throw new IllegalStateException(
        "No JavaCompiler available — convergence tests require a JDK runtime with jdk.compiler");
    }

    Path outDir = Files.createTempDirectory("convergence-classes");
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
      fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(outDir));

      List<JavaFileObject> units = new ArrayList<>(sources.size());
      for (Map.Entry<String, String> e : sources.entrySet()) {
        units.add(new InMemorySource(e.getKey(), e.getValue()));
      }

      // -parameters: keep parameter names in the class file so ASM-side ParameterSymbols carry the
      //              same names as the source-side, enabling text-level convergence comparison.
      Boolean ok = compiler.getTask(null, fileManager, diagnostics,
                                    List.of("-parameters"), null, units).call();
      if (ok == null || !ok) {
        StringBuilder msg = new StringBuilder("javac failed:\n");
        diagnostics.getDiagnostics().forEach(d -> msg.append("  ").append(d).append('\n'));
        throw new IllegalStateException(msg.toString());
      }
    }
    return outDir;
  }

  /**
   * Look up the {@link JavaCompiler} implementation. {@link ToolProvider#getSystemJavaCompiler}
   * uses the system classloader, which under the IntelliJ-Platform test runtime is
   * {@code com.intellij.util.lang.PathClassLoader} — it doesn't expose {@code jdk.compiler}'s
   * {@code com.sun.tools.javac.api.JavacTool}, so the standard lookup returns {@code null} even
   * though the JBR module is present. Fall back to instantiating {@code JavacTool} via the
   * platform classloader (which always sees JDK module exports).
   */
  static @Nullable JavaCompiler resolveCompiler() {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler != null) return compiler;
    try {
      Class<?> javacTool = Class.forName("com.sun.tools.javac.api.JavacTool", true,
                                         ClassLoader.getPlatformClassLoader());
      return (JavaCompiler) javacTool.getDeclaredConstructor().newInstance();
    }
    catch (ReflectiveOperationException ignored) {
      return null;
    }
  }

  private static final class InMemorySource extends javax.tools.SimpleJavaFileObject {
    private final String content;

    InMemorySource(@NotNull String pathFromSourceRoot, @NotNull String content) {
      super(URI.create("string:///" + pathFromSourceRoot), Kind.SOURCE);
      this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return content;
    }
  }
}
