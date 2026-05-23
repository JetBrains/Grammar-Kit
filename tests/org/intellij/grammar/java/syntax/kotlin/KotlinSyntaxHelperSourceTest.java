/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax.kotlin;

import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.kotlin.KotlinSyntaxClassSymbolProvider;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.intellij.grammar.java.syntax.FixtureExtractor;
import org.intellij.grammar.java.syntax.GoldenClassInfoTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
  // helpers
  // ---------------------------------------------------------------------------------------------

  private @NotNull Map<Fqn, ClassSymbol> extractAll() {
    return FixtureExtractor.extractAll(root, new KotlinSyntaxClassSymbolProvider(List.of(root)), ".kt");
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
