/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.syntax.FixtureExtractor;
import org.intellij.grammar.java.syntax.GoldenClassInfoTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source-driven tests for the Kotlin side of the syntax-generation pipeline. Each test writes a
 * small {@code .kt} fixture to a temp dir, then asks
 * {@link KotlinSyntaxClassSymbolProvider} for the resulting {@code Map<Fqn, ClassSymbol>} via
 * {@link FixtureExtractor#extractAll} and compares it to a golden under
 * {@code testData/syntax/kotlin/source/}. Helper-behaviour assertions (negative cache) keep their
 * own checks.
 */
public class KotlinSyntaxHelperSourceTest extends GoldenClassInfoTestCase {

  private Path root;

  @Override
  protected @NotNull String goldenDir() {
    return "syntax/kotlin/source";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    root = Files.createTempDirectory("kotlin-syntax-helper-source");
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

  public void testMultipleTopLevelClassesPerFile() throws Exception {
    // Single file declares two classes whose names don't match the file name. The slow-path
    // package scan must ingest the file and surface both.
    write("pkg/Pair.kt", """
        package pkg
        class Alpha
        class Beta
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassSynthesisFromTopLevelFunction() throws Exception {
    write("util/Strings.kt", """
        package util
        fun shout(s: String): String = s.uppercase()
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassNotSynthesisedWhenOnlyClassesDeclared() throws Exception {
    write("only/Nothing.kt", """
        package only
        class Plain
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testNegativeCacheMissReturnsNull() throws Exception {
    // No fixture, no extraction: this is a pure helper-behaviour test verifying the negative cache
    // short-circuits a second lookup of an unknown FQN.
    JavaHelper helper = helper();
    assertNull(helper.findClass("nowhere.Missing"));
    assertNull(helper.findClass("nowhere.Missing"));
  }

  public void testFileClassWithMixedTopLevelAndClass() throws Exception {
    write("mix/Mixed.kt", """
        package mix
        fun util(): String = ""
        class Other
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassJvmNameRename() throws Exception {
    write("util/Bar.kt", """
        @file:JvmName("Utils")
        package util
        fun shout(s: String): String = s.uppercase()
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassJvmMultifileFacadeMergesAcrossFiles() throws Exception {
    write("util/A.kt", """
        @file:JvmName("Utils")
        @file:JvmMultifileClass
        package util
        fun a(): String = "a"
        """);
    write("util/B.kt", """
        @file:JvmName("Utils")
        @file:JvmMultifileClass
        package util
        fun b(): String = "b"
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testFileClassJvmNameWithoutTopLevelCallablesDoesNotSynthesize() throws Exception {
    write("util/Bar.kt", """
        @file:JvmName("Utils")
        package util
        class Bar
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testInternalClassWithNonMatchingFileName() throws Exception {
    // Kotlin allows a file's primary class name to differ from the file name.
    write("pkg/wrong.kt", """
        package pkg
        class RightlyNamed
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testNullabilityAnnotationsOnGenericTypeArguments() throws Exception {
    // Inline @NotNull/@Nullable annotations should appear on each generic type argument
    // and on the Array<X> element type — not just on the outer reference type.
    write("gen/G.kt", """
        package gen
        class G<T> {
          fun listStr(): List<String> = TODO()
          fun listStrN(): List<String?> = TODO()
          fun mapStrIntN(): Map<String, Int?> = TODO()
          fun listStar(): List<*> = TODO()
          fun listT(t: T): List<T> = TODO()
          fun listOut(): List<out String> = TODO()
          fun listInN(): List<in String?> = TODO()
          fun arrStr(): Array<String> = TODO()
          fun arrStrN(): Array<String?> = TODO()
          fun arrStar(): Array<*> = TODO()
          fun intArr(): IntArray = TODO()
          fun nested(xs: List<List<String?>>): Unit { }
          fun nullableInt(x: Int?): Int? = x
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testNullabilityAnnotationsFromKotlinTypes() throws Exception {
    // Cross-section of the rules in KotlinSyntaxTypeFormatter.classifyNullability:
    //  - reference types: NotNull vs Nullable based on the '?' marker
    //  - primitives: no annotation; nullable primitives box and get Nullable
    //  - bare type variables: no annotation
    //  - val/var properties: getter/setter follow the property type
    //  - extension functions: receiver follows its own nullability
    //  - Unit/void return: no annotation
    write("nul/Sample.kt", """
        package nul
        class Sample {
          fun ref(s: String, n: String?): String? = n
          fun prim(x: Int, y: Int?): Boolean = x == 0
          fun <T> id(x: T): T = x
          val title: String = ""
          var nick: String? = null
        }
        fun String?.ext(x: Int?): Unit { }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testNestedTypeReturnedBySiblingMethodResolvesToEnclosingClass() throws Exception {
    // Unqualified `Direction` inside `Util`'s companion object must resolve to `pkg.Util.Direction`,
    // not the same-package fallback `pkg.Direction`. Mirrors the Java fix on the Kotlin extractor.
    write("pkg/Util.kt", """
        package pkg
        class Util {
          enum class Direction { SEND, RECEIVE }
          companion object {
            fun getDirection(): Direction = Direction.SEND
          }
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testDoubleNestedTypePartiallyQualifiedFromOuterResolvesToFullFqn() throws Exception {
    // `Wrap.Deep` written from the companion-object scope (one level above `Wrap`) must qualify the
    // head segment `Wrap` to its in-scope nested FQN, giving `pkg.Util.Wrap.Deep`. The partial form
    // is the natural Kotlin idiom — bare `Deep` wouldn't compile here.
    write("pkg/Util.kt", """
        package pkg
        class Util {
          class Wrap {
            enum class Deep { X, Y }
          }
          companion object {
            fun foo(): Wrap.Deep = Wrap.Deep.X
          }
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testCompanionLiftedJvmStaticReturningSiblingNestedType() throws Exception {
    // `@JvmStatic` inside a companion object lifts the method onto the enclosing class. The lifted
    // copy must keep the resolved sibling-nested FQN (`pkg.Util.E`) — not the same-package fallback.
    write("pkg/Util.kt", """
        package pkg
        class Util {
          enum class E { X }
          companion object {
            @JvmStatic fun mk(): E = E.X
          }
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testInterfaceSupertypeGoesToInterfacesNotSuperClass() throws Exception {
    // Bug 3: `class Bag : MyList` (no parens) — the resolver must classify MyList as an interface
    // and route it to `interfaces`, leaving superClass as java.lang.Object. The old code put any
    // unparenthesized first supertype in superClass unconditionally. Goes through the manager so
    // the resolver actually knows MyList's modifiers at populateSuperTypes time.
    write("pkg/MyList.kt", """
        package pkg
        interface MyList {
          fun size(): Int
        }
        """);
    write("pkg/Bag.kt", """
        package pkg
        class Bag : MyList {
          override fun size(): Int = 0
        }
        """);
    JvmClassSymbolManager manager = new JvmClassSymbolManager(
      List.of(new KotlinSyntaxClassSymbolProvider(List.of(root))));
    ClassSymbol bag = manager.findClass(Fqn.of("pkg.Bag"));
    assertNotNull("Bag must be discoverable", bag);
    assertEquals(Fqn.JAVA_LANG_OBJECT, bag.superClass());
    assertEquals(List.of(Fqn.of("pkg.MyList")), bag.interfaces());
  }

  public void testExtensionFunctionReceiverNullability() throws Exception {
    // Audit task #4: nullable extension receivers (`fun String?.demo()`) must preserve `@Nullable`
    // on the synthesized first parameter. Non-nullable receivers continue to carry `@NotNull` like
    // any other reference-type parameter.
    write("util/Strings.kt", """
        package util
        fun String.shout(): String = uppercase()
        fun String?.shoutOrEmpty(): String = this?.uppercase() ?: ""
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testEnumClassSynthesizesValuesAndValueOf() throws Exception {
    // Audit task #12: kotlinc auto-generates `values(): Array<T>` and `valueOf(String): T` for
    // enum classes. The source extractor synthesizes them so its view matches what ASM sees.
    write("pkg/Suit.kt", """
        package pkg
        enum class Suit { HEARTS, DIAMONDS, CLUBS, SPADES }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testDataClassSynthesizesComponentCopyEqualsHashCodeToString() throws Exception {
    // Audit task #11: data class kotlinc-generates componentN(), copy(...), equals(Object),
    // hashCode(), toString(). The source extractor synthesizes the same so its view converges
    // with what ASM sees in the compiled bytecode.
    write("pkg/Pair.kt", """
        package pkg
        data class Pair(val first: Int, val second: String)
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  public void testJvmFieldSuppressesAccessorSynthesis() throws Exception {
    // Audit task #3: kotlinc does NOT generate get/set accessors for @JvmField properties — the
    // field is exposed directly at the JVM level. The source extractor must match that view: no
    // synthesized getX()/setX() methods for @JvmField val/var. Other properties continue to get
    // accessors as before; @JvmField suppresses only the annotated one.
    write("pkg/Holder.kt", """
        package pkg
        class Holder {
          @JvmField val a: Int = 1
          @JvmField var b: String = ""
          val c: Int = 0
          var d: String = ""
        }
        """);
    assertClassInfoMatchesGolden(extractAll());
  }

  // ---------------------------------------------------------------------------------------------
  // Path / package mismatch (lexer-indexed fallback)
  // ---------------------------------------------------------------------------------------------

  public void testPackageMismatchRootLevelFileResolves() throws Exception {
    // Kotlin allows any file path regardless of declared package. A file at the root of the source
    // root declaring `package com.foo` is invisible to both the FQN-derived fast path
    // (`com/foo/Bar.kt` doesn't exist) and the package-directory slow path (`com/foo/` isn't a
    // directory). The package-index fallback must lex the file, learn its real package, and ingest
    // it on demand.
    write("Misc.kt", """
        package com.foo
        class Bar
        """);
    assertClassInfoMatchesGolden(resolve(Fqn.of("com.foo.Bar")));
  }

  public void testPackageMismatchUnrelatedDirectoryResolves() throws Exception {
    // The file is in a subdirectory whose name has nothing to do with the declared package.
    write("scratch/Misc.kt", """
        package my.deep.pkg
        class C
        """);
    assertClassInfoMatchesGolden(resolve(Fqn.of("my.deep.pkg.C")));
  }

  public void testPackageMismatchConventionalLayoutShadowsMismatched() throws Exception {
    // pkg/Bar.kt is reachable via the FQN-derived fast path; the mismatched Misc.kt declaring the
    // same FQN is at a non-matching path and only the fallback could surface it. Because the fast
    // path resolves the FQN first, the fallback never runs and Misc.kt remains unread.
    write("pkg/Bar.kt", """
        package pkg
        class Bar {
          fun fromConventional(): Int = 1
        }
        """);
    write("Misc.kt", """
        package pkg
        class Bar {
          fun fromMismatched(): Int = 2
        }
        """);
    Map<Fqn, ClassSymbol> result = resolve(Fqn.of("pkg.Bar"));
    ClassSymbol bar = result.get(Fqn.of("pkg.Bar"));
    assertNotNull(bar);
    assertEquals(1, bar.methods().size());
    assertEquals("fromConventional", bar.methods().get(0).name());
  }

  public void testPackageMismatchDuplicateFqnAcrossMismatchedFiles() throws Exception {
    // Two mismatched files declare the same FQN com.foo.Bar plus one distinct sibling class each.
    // The fallback ingests both files; the manager keeps a single Bar (last-wins within the batch,
    // OS-dependent walk order). The contract: the duplicate FQN appears exactly once, and the
    // unique sibling classes from both files are present — i.e. both files were actually
    // processed by the fallback.
    write("first.kt", """
        package com.foo
        class Bar {
          fun fromFirst(): Int = 1
        }
        class UniqueFromFirst
        """);
    write("second.kt", """
        package com.foo
        class Bar {
          fun fromSecond(): Int = 2
        }
        class UniqueFromSecond
        """);
    Map<Fqn, ClassSymbol> result = resolve(Fqn.of("com.foo.Bar"));
    assertNotNull(result.get(Fqn.of("com.foo.Bar")));
    assertNotNull("Unique class from first.kt should be present (proves first.kt was ingested)",
                  result.get(Fqn.of("com.foo.UniqueFromFirst")));
    assertNotNull("Unique class from second.kt should be present (proves second.kt was ingested)",
                  result.get(Fqn.of("com.foo.UniqueFromSecond")));
    ClassSymbol bar = result.get(Fqn.of("com.foo.Bar"));
    assertEquals(1, bar.methods().size());
    String winner = bar.methods().get(0).name();
    assertTrue("Bar's method must come from one of the two files but was: " + winner,
               winner.equals("fromFirst") || winner.equals("fromSecond"));
  }

  public void testPackageMismatchAlongsideConventionalFile() throws Exception {
    // Both layouts should resolve: the conventional one continues to hit the fast path, the
    // mismatched one is picked up by the index fallback.
    write("Misc.kt", """
        package mismatched
        class FromMismatched
        """);
    write("conventional/A.kt", """
        package conventional
        class A
        """);
    assertClassInfoMatchesGolden(resolve(Fqn.of("mismatched.FromMismatched"), Fqn.of("conventional.A")));
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private @NotNull Map<Fqn, ClassSymbol> extractAll() {
    return FixtureExtractor.extractAll(root, new KotlinSyntaxClassSymbolProvider(List.of(root)), ".kt");
  }

  /**
   * Probes a fresh provider for each FQN and merges all returned ClassSymbols. Used by mismatch
   * tests where the directory layout doesn't reveal the package, so {@link FixtureExtractor} can't
   * derive a probe FQN that would trigger the lexer-indexed fallback.
   */
  private @NotNull Map<Fqn, ClassSymbol> resolve(@NotNull Fqn... fqns) {
    KotlinSyntaxClassSymbolProvider provider = new KotlinSyntaxClassSymbolProvider(List.of(root));
    SymbolResolver nullResolver = fqn -> null;
    Map<Fqn, ClassSymbol> result = new HashMap<>();
    for (Fqn fqn : fqns) result.putAll(provider.resolve(fqn, nullResolver));
    return result;
  }

  private @NotNull JavaHelper helper() {
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(new KotlinSyntaxClassSymbolProvider(List.of(root)))));
  }

  private void write(@NotNull String relative, @NotNull String content) throws IOException {
    Path target = root.resolve(relative);
    Files.createDirectories(target.getParent());
    Files.writeString(target, content);
  }
}
