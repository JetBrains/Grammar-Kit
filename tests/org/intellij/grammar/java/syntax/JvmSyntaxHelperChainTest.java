/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.MyElement;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * End-to-end smoke test for the three-layer source-backed chain
 * {@code KotlinSyntaxHelper → JavaSyntaxHelper → mock-bytecode-fallback}. Verifies each layer
 * resolves the FQNs it owns and that lookups pass through cleanly.
 */
public class JvmSyntaxHelperChainTest extends TestCase {

  private Path root;
  private RecordingFallback bytecode;
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
    bytecode = new RecordingFallback();
    ClassInfo platformInfo = new ClassInfo();
    platformInfo.name = "platform.Stub";
    platformInfo.superClass = "java.lang.Object";
    bytecode.classes.put("platform.Stub", platformInfo);

    List<Path> roots = List.of(root);
    chain = new KotlinSyntaxHelper(roots, new JavaSyntaxHelper(roots, bytecode));
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
    assertTrue("Kotlin layer should answer", bytecode.findClassCalls.isEmpty());
  }

  public void testJavaResolvedAtSecondLayer() {
    NavigatablePsiElement clazz = chain.findClass("jv.Counter");
    assertNotNull(clazz);
    assertTrue("Java layer should answer", bytecode.findClassCalls.isEmpty());
  }

  public void testPlatformFallsThroughToBytecode() {
    NavigatablePsiElement clazz = chain.findClass("platform.Stub");
    assertNotNull(clazz);
    assertFalse("Bytecode fallback should have been consulted", bytecode.findClassCalls.isEmpty());
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

  private void write(@NotNull String relative, @NotNull String content) throws java.io.IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }

  /** Minimal recording fallback playing the role of an AsmHelper. */
  private static final class RecordingFallback extends JavaHelper {
    final Map<String, ClassInfo> classes = new HashMap<>();
    final java.util.List<String> findClassCalls = new java.util.ArrayList<>();

    @Override public boolean isPublic(NavigatablePsiElement element) { return false; }

    @Override public NavigatablePsiElement findClass(String className) {
      findClassCalls.add(className);
      ClassInfo info = classes.get(className);
      return info == null ? null : new MyElement<>(info);
    }
  }
}
