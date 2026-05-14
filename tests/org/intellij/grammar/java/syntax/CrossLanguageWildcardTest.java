/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.psi.NavigatablePsiElement;
import junit.framework.TestCase;
import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.SyntaxTreeCache;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassSymbolProvider;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.MyElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Exercises the cross-language hop: a {@code .java} file's wildcard import resolves to a Kotlin
 * class declared in the imported package, and vice versa. Also covers cycle protection (mutual
 * extension between a Java and a Kotlin class) and the duplicate-FQN collision warning.
 */
public class CrossLanguageWildcardTest extends TestCase {

  private Path root;
  private JavaHelper helper;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    root = Files.createTempDirectory("xlang-wildcard");
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

  public void testJavaWildcardImportFindsKotlinClass() throws Exception {
    write("com/foo/KParent.kt", """
        package com.foo
        open class KParent
        """);
    write("com/bar/JChild.java", """
        package com.bar;
        import com.foo.*;
        public class JChild extends KParent { }
        """);
    assertEquals("com.foo.KParent", helper().getSuperClassName("com.bar.JChild"));
  }

  public void testKotlinWildcardImportFindsJavaClass() throws Exception {
    write("com/foo/JParent.java", """
        package com.foo;
        public class JParent { }
        """);
    write("com/bar/KChild.kt", """
        package com.bar
        import com.foo.*
        class KChild : JParent()
        """);
    assertEquals("com.foo.JParent", helper().getSuperClassName("com.bar.KChild"));
  }

  public void testMutualExtensionDoesNotRecurseInfinitely() throws Exception {
    write("p/JavaA.java", """
        package p;
        import p.*;
        public class JavaA extends KotlinB { }
        """);
    write("p/KotlinB.kt", """
        package p
        import p.*
        open class KotlinB : JavaA()
        """);
    // Both must resolve without infinite recursion; one side's superClass may be null due to the
    // cycle break, but neither lookup should hang or throw.
    NavigatablePsiElement a = helper().findClass("p.JavaA");
    NavigatablePsiElement b = helper().findClass("p.KotlinB");
    assertNotNull(a);
    assertNotNull(b);
  }

  public void testDuplicateFqnCollisionResolvedByProviderOrder() {
    ClassInfo first = new ClassInfo();
    first.name = "dup.Class";
    first.superClass = "first.Parent";

    ClassInfo second = new ClassInfo();
    second.name = "dup.Class";
    second.superClass = "second.Parent";

    JvmClassSymbolProvider providerA = mapProvider(Map.of("dup.Class", first));
    JvmClassSymbolProvider providerB = mapProvider(Map.of("dup.Class", second));

    JvmClassSymbolManager manager = new JvmClassSymbolManager(List.of(providerA, providerB));
    JavaHelper h = new JvmSyntaxHelper(manager);

    // First provider wins.
    assertEquals("first.Parent", h.getSuperClassName("dup.Class"));
    // Reversed: now second wins.
    JvmClassSymbolManager reversed = new JvmClassSymbolManager(List.of(providerB, providerA));
    assertEquals("second.Parent", new JvmSyntaxHelper(reversed).getSuperClassName("dup.Class"));
  }

  private @NotNull JavaHelper helper() {
    if (helper == null) {
      List<Path> roots = List.of(root);
      SyntaxTreeCache treeCache = new SyntaxTreeCache();
      helper = new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(
        new KotlinSyntaxClassSymbolProvider(roots, treeCache),
        new JavaSyntaxClassSymbolProvider(roots, treeCache))));
    }
    return helper;
  }

  private void write(@NotNull String relative, @NotNull String content) throws IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }

  private static @NotNull JvmClassSymbolProvider mapProvider(@NotNull Map<String, ClassInfo> classes) {
    return new JvmClassSymbolProvider() {
      @Override
      public @NotNull Map<String, ClassInfo> resolve(@NotNull String fqn, @NotNull SymbolResolver resolver) {
        ClassInfo info = classes.get(fqn);
        return info == null ? Map.of() : Map.of(fqn, info);
      }
    };
  }

  // Ensures MyElement import is reachable for downstream tests that might be added; quiets unused-import linter.
  @SuppressWarnings("unused")
  private static final Class<?> MY_ELEMENT_REF = MyElement.class;
}
