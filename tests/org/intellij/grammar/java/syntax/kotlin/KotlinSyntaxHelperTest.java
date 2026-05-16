/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import org.intellij.grammar.classinfo.ClassInfo;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
import org.intellij.grammar.java.syntax.FixtureExtractor;
import org.intellij.grammar.java.syntax.GoldenClassInfoTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * File-system integration tests for the Kotlin syntax-generation pipeline. Each test writes its
 * own {@code .kt} fixture and compares the full extraction
 * {@code Map<Fqn, ClassInfo>} against a golden under {@code testData/syntax/kotlin/file/}.
 */
public class KotlinSyntaxHelperTest extends GoldenClassInfoTestCase {

  private Path root;

  @Override
  protected @NotNull String goldenDir() {
    return "syntax/kotlin/file";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    root = Files.createTempDirectory("kotlin-syntax-helper-test");
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

  public void testFindTopLevelClass() throws Exception {
    write("a/b/Simple.kt", """
        package a.b
        class Simple
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testInterfacePopulated() throws Exception {
    write("a/b/Holder.kt", """
        package a.b
        class Holder : Iface
        interface Iface
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testKotlinClassFinalByDefault() throws Exception {
    write("a/b/Plain.kt", """
        package a.b
        class Plain
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testOpenClass() throws Exception {
    write("a/b/Open.kt", """
        package a.b
        open class Open
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testAbstractClass() throws Exception {
    write("a/b/Abs.kt", """
        package a.b
        abstract class Abs
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testInterfaceFlags() throws Exception {
    write("a/b/Iface.kt", """
        package a.b
        interface Iface
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testNestedClassIsStatic() throws Exception {
    write("a/b/Outer.kt", """
        package a.b
        class Outer {
            class Nested
            inner class Inner
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testPrimaryConstructorAndPropertyAccessors() throws Exception {
    write("a/b/Person.kt", """
        package a.b
        class Person(val name: String, var age: Int)
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testMemberFunctionSignature() throws Exception {
    write("a/b/Greeter.kt", """
        package a.b
        class Greeter {
            fun greet(name: String): String = "Hi"
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testIntMapsToJvmPrimitive() throws Exception {
    write("a/b/Counter.kt", """
        package a.b
        class Counter {
            fun bump(by: Int): Int = by + 1
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testUnitReturnMapsToVoid() throws Exception {
    write("a/b/Noisy.kt", """
        package a.b
        class Noisy {
            fun shout(): Unit {}
            fun whisper() {}
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testTopLevelFunctionLandsOnFileClass() throws Exception {
    write("a/b/Util.kt", """
        package a.b
        fun helper(): String = ""
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testExtensionFunctionReceiverBecomesFirstParam() throws Exception {
    write("a/b/Ext.kt", """
        package a.b
        fun String.shouted(): String = uppercase()
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testObjectDeclaration() throws Exception {
    write("a/b/Single.kt", """
        package a.b
        object Single {
            fun foo(): Int = 1
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testCompanionExposesJvmStatic() throws Exception {
    write("a/b/Holder.kt", """
        package a.b
        class Holder {
            companion object {
                @JvmStatic
                fun staticHelper(): Int = 1
                fun instanceHelper(): Int = 2
            }
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testTypealiasResolvesAsClassWithAliasedSuper() throws Exception {
    write("a/b/Aliases.kt", """
        package a.b
        typealias Name = String
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testGenericClassTypeParameters() throws Exception {
    write("a/b/Box.kt", """
        package a.b
        class Box<T : Number>(val value: T)
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testPrivatePropertySkipped() throws Exception {
    write("a/b/Secret.kt", """
        package a.b
        class Secret {
            private val hidden: String = ""
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testConstPropertySkipped() throws Exception {
    write("a/b/Const.kt", """
        package a.b
        object Const {
            const val ANSWER: Int = 42
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testInternalMapsToPublic() throws Exception {
    write("a/b/Internal.kt", """
        package a.b
        internal class Internal
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testSealedIsAbstract() throws Exception {
    write("a/b/Tree.kt", """
        package a.b
        sealed class Tree
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testImportedTypeResolves() throws Exception {
    write("a/b/Used.kt", """
        package a.b
        class Used {
            fun read(p: Path): String = ""
        }
        """);
    write("a/b/Header.kt", """
        package a.b
        import java.nio.file.Path
        """);
    // Header.kt has the Path import, but Used.kt does not — extraction surfaces the unresolved
    // Path type as the same-package guess (a.b.Path).
    assertClassInfoMatchesGolden(extractAll());
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private @NotNull Map<Fqn, ClassInfo> extractAll() {
    return FixtureExtractor.extractAll(root, new KotlinSyntaxClassSymbolProvider(List.of(root)), ".kt");
  }

  private void write(@NotNull String relative, @NotNull String content) throws IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }
}
