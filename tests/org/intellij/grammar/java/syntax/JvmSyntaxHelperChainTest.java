/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * End-to-end smoke test for the ordered provider list inside a single
 * {@link JvmClassSymbolManager}: {@code KotlinSyntaxClassSymbolProvider} →
 * {@code JavaSyntaxClassSymbolProvider} → recording bytecode-style fallback. Verifies each provider
 * resolves the FQNs it owns and that lookups pass cleanly through to the next provider on miss.
 */
public class JvmSyntaxHelperChainTest extends TestCase {

  private Path root;
  private RecordingProvider bytecode;
  private JavaHelper chain;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    root = Files.createTempDirectory("jvm-syntax-chain");
    write("kt/Greeter.kt", """
        package kt
        class Greeter {
            fun hello(): String = ""
        }
        """);
    write("jv/Counter.java", """
        package jv;
        public class Counter {
            public int bump(int x) { return x + 1; }
        }
        """);
    bytecode = new RecordingProvider();
    ClassInfo platformInfo = new ClassInfo();
    platformInfo.name = Fqn.of("platform.Stub");
    platformInfo.superClass = Fqn.of("java.lang.Object");
    bytecode.classes.put("platform.Stub", platformInfo);

    List<Path> roots = List.of(root);
    SyntaxTreeCache treeCache = new SyntaxTreeCache();
    chain = new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(
      new KotlinSyntaxClassSymbolProvider(roots, treeCache),
      new JavaSyntaxClassSymbolProvider(roots, treeCache),
      bytecode)));
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      if (root != null) {
        try (var stream = Files.walk(root)) {
          stream.sorted((p1, p2) -> p2.getNameCount() - p1.getNameCount())
                .forEach(p -> p.toFile().delete());
        }
      }
    }
    finally {
      super.tearDown();
    }
  }

  public void testKotlinResolvedAtFirstLayer() {
    NavigatablePsiElement clazz = chain.findClass("kt.Greeter");
    assertNotNull(clazz);
    assertTrue("Kotlin provider should answer", bytecode.resolveCalls.isEmpty());
  }

  public void testJavaResolvedAtSecondLayer() {
    NavigatablePsiElement clazz = chain.findClass("jv.Counter");
    assertNotNull(clazz);
    assertTrue("Java provider should answer", bytecode.resolveCalls.isEmpty());
  }

  public void testPlatformFallsThroughToBytecode() {
    NavigatablePsiElement clazz = chain.findClass("platform.Stub");
    assertNotNull(clazz);
    assertFalse("Bytecode provider should have been consulted", bytecode.resolveCalls.isEmpty());
  }

  public void testMissingFqnReturnsNull() {
    assertNull(chain.findClass("nowhere.Missing"));
  }

  public void testKotlinMemberMethodReachable() {
    chain.findClass("kt.Greeter");
    List<NavigatablePsiElement> ms = chain.findClassMethods(
      "kt.Greeter", MethodType.INSTANCE, "hello", -1);
    assertEquals(1, ms.size());
  }

  public void testJavaMemberMethodReachable() {
    chain.findClass("jv.Counter");
    List<NavigatablePsiElement> ms = chain.findClassMethods(
      "jv.Counter", MethodType.INSTANCE, "bump", -1);
    assertEquals(1, ms.size());
  }

  private void write(@NotNull String relative, @NotNull String content) throws IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }

  /** Recording provider playing the role of a bytecode-style fallback. */
  private static final class RecordingProvider implements JvmClassSymbolProvider {
    final Map<String, ClassInfo> classes = new HashMap<>();
    final List<String> resolveCalls = new ArrayList<>();

    @Override
    public @NotNull Map<Fqn, ClassInfo> resolve(@NotNull Fqn fqn, @NotNull SymbolResolver resolver) {
      resolveCalls.add(fqn.value());
      ClassInfo info = classes.get(fqn.value());
      return info == null ? Map.of() : Map.of(fqn, info);
    }
  }
}
