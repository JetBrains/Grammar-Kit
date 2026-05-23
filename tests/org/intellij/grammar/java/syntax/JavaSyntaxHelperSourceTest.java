/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.java.syntax;

import com.intellij.platform.syntax.tree.SyntaxNode;
import org.intellij.grammar.classinfo.ClassSymbol;
import org.intellij.grammar.classinfo.Fqn;
import org.intellij.grammar.classinfo.JvmClassSymbolManager;
import org.intellij.grammar.classinfo.JvmClassSymbolProvider;
import org.intellij.grammar.classinfo.MethodType;
import org.intellij.grammar.classinfo.SymbolResolver;
import org.intellij.grammar.classinfo.java.JavaSyntaxClassExtractor;
import org.intellij.grammar.classinfo.java.JavaSyntaxTreeManager;
import org.intellij.grammar.java.JavaHelper;
import org.intellij.grammar.java.JvmSyntaxHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source-driven tests for the syntax-generation pipeline: each test parses an in-memory Java
 * source string with {@link JavaSyntaxTreeManager#parseText}, runs
 * {@link JavaSyntaxClassExtractor#extractFrom} on the result, and compares the produced
 * {@code Map<Fqn, ClassSymbol>} against a golden file under {@code testData/syntax/java/source/}.
 * Tests that exercise {@link JvmSyntaxHelper} filtering on top of the extracted records (e.g.
 * abstract-method gating, supertype probing) keep an extra assertion against a helper built from
 * the same map.
 */
@SuppressWarnings("UnstableApiUsage")
public class JavaSyntaxHelperSourceTest extends GoldenClassInfoTestCase {

  @Override
  protected @NotNull String goldenDir() {
    return "syntax/java/source";
  }

  // ---------------------------------------------------------------------------------------------
  // Class shape
  // ---------------------------------------------------------------------------------------------

  public void testPublicTopLevelClass() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Foo extends Base {}
      """));
  }

  public void testPackagePrivateClass() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      class Hidden {}
      """));
  }

  public void testInterfaceFlagSet() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public interface Iface {}
      """));
  }

  public void testNoExtendsClauseDefaultsToObject() {
    // No explicit `extends`: extractor inserts java.lang.Object so reflection-style chains work.
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Free {}
      """));
  }

  public void testImplementsListPopulated() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C implements I1, I2 {}
      """));
  }

  public void testClassAnnotation() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      @Deprecated
      public class C {}
      """));
  }

  // ---------------------------------------------------------------------------------------------
  // Multiple class declarations in one file
  // ---------------------------------------------------------------------------------------------

  public void testPublicAndPackagePrivateClassesInSameFile() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Bundle {
        public Helper helper() { return null; }
      }
      class Helper {
        public String greet() { return ""; }
      }
      """));
  }

  public void testInnerClassFqn() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class Outer {
        public static class Inner {
          public String inside() { return ""; }
        }
      }
      """));
  }

  // ---------------------------------------------------------------------------------------------
  // Methods: shape, signatures
  // ---------------------------------------------------------------------------------------------

  public void testInstanceMethodSignature() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      import java.util.List;
      import java.util.Map;
      public class C {
        public List<String> doIt(int count, Map<String, Integer> data) {
          return null;
        }
      }
      """));
  }

  public void testStaticMethodFiltering() {
    Map<Fqn, ClassSymbol> classes = extract("""
      package a.b;
      public class C {
        public void inst() {}
        public static void stat() {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: helper.findClassMethods filters by MethodType.
    JavaHelper helper = helperFrom(classes);
    assertEquals(1, helper.findClassMethods("a.b.C", MethodType.STATIC, "stat", false,  -1).size());
    assertTrue(helper.findClassMethods("a.b.C", MethodType.STATIC, "inst", false, -1).isEmpty());
    assertEquals(1, helper.findClassMethods("a.b.C", MethodType.INSTANCE, "inst", false,  -1).size());
  }

  public void testConstructorExtraction() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public C(int seed) {}
      }
      """));
  }

  public void testAbstractMethodGatedByAllowAbstract() {
    Map<Fqn, ClassSymbol> classes = extract("""
      package a.b;
      public abstract class C {
        public abstract void doIt();
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: by default findClassMethods filters out abstract methods unless allowAbstract=true.
    JavaHelper helper = helperFrom(classes);
    assertTrue(helper.findClassMethods("a.b.C", MethodType.INSTANCE, "doIt", false,  -1).isEmpty());
    assertEquals(1, helper.findClassMethods("a.b.C", MethodType.INSTANCE, "doIt", true, -1).size());
  }

  public void testPrivateConstructorExcluded() {
    Map<Fqn, ClassSymbol> classes = extract("""
      package a.b;
      public class C {
        private C() {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: the helper excludes private constructors from match results.
    JavaHelper helper = helperFrom(classes);
    assertTrue(helper.findClassMethods("a.b.C", MethodType.CONSTRUCTOR, "C", false,  -1).isEmpty());
  }

  // ---------------------------------------------------------------------------------------------
  // Method-level metadata
  // ---------------------------------------------------------------------------------------------

  public void testMethodAnnotation() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        @Override
        public String toString() { return ""; }
      }
      """));
  }

  public void testThrowsClause() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public void doIt() throws java.io.IOException, RuntimeException {}
      }
      """));
  }

  public void testGenericMethodTypeParameter() {
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public static <U> U cast(Object o) { return (U)o; }
      }
      """));
  }

  public void testParameterAnnotationsResolvedAgainstSamePackage() {
    // Unresolved @Marker is qualified as a.b.Marker (same-package fallback).
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public void doIt(@Marker int count, int plain) {}
      }
      """));
  }

  public void testStaticImportOfNestedAnnotation() {
    // `import static c.d.Outer.Marker` makes `@Marker` resolve to `c.d.Outer.Marker`
    // — the static-import's full reference text — not `c.d.Marker` with the enclosing
    // class dropped. Mirrors the Goland scenario `import static com.goide.psi.impl.GoLightType.IconFlags`.
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      import static c.d.Outer.Marker;
      public class C {
        public void doIt(@Marker int x) {}
      }
      """));
  }

  public void testStaticImportOfInheritedNestedAnnotation() {
    // JLS 7.5.1 requires single-type-import to name the canonical declaration. When a static import
    // names the nested type through a subclass (the subclass merely inherits it), canonicalize to
    // the supertype that actually declares it. Mirrors the Goland scenario where
    // `import static com.goide.psi.impl.GoLightType.IconFlags` should resolve to
    // `com.intellij.openapi.util.Iconable.IconFlags`, not `GoLightType.IconFlags`.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public interface Iconable {
        @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package c.d;
      public class Sub implements Iconable {
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import static c.d.Sub.Marker;
      public class C {
        public void doIt(@Marker int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testTypeUseAnnotationsLiftedToType() {
    // Any annotation whose @Target declares ElementType.TYPE_USE is lifted off the
    // declaration-position MODIFIER_LIST (which the JetBrains Java parser groups with `public`)
    // onto the outermost JvmTypeRef position. Recognition is resolver-based: the extractor
    // consults ClassSymbol.annotationTargets via the SymbolResolver, mirroring IntelliJ's
    // AnnotationTargetUtil.findAnnotationTarget(..., TYPE_USE) approach.
    // Edge cases exercised: primitive return (no JvmTypeRef slot — annotation stays on method),
    // bare type-variable return (TypeVariable can't carry annotations — stays on method), array
    // return (lift onto the outermost ArrayType), non-TYPE_USE annotations alongside (the
    // method-only @MethodOnly stays on the method, only TYPE_USE moves), unresolved annotation
    // (@MissingAnno stays on the method — null-from-resolver fallback), and constructors (no
    // return-type lift — the synthesized self-reference is not where a user-written annotation
    // belongs).
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      import java.lang.annotation.ElementType;
      import java.lang.annotation.Target;
      @Target(ElementType.TYPE_USE) @interface NotNull {}
      @Target(ElementType.TYPE_USE) @interface Nullable {}
      @Target({ElementType.METHOD, ElementType.TYPE_USE}) @interface Mixed {}
      @Target(ElementType.METHOD) @interface MethodOnly {}
      public class C {
        public @NotNull String foo() { return ""; }
        public @Nullable String bar() { return null; }
        public void params(@NotNull String s, @Nullable Integer n) {}
        public @NotNull int prim() { return 0; }
        public @NotNull <T> T cast(Object o) { return (T)o; }
        public @NotNull String[] arr() { return new String[0]; }
        public @Mixed String mixed() { return ""; }
        public @MethodOnly String methodOnly() { return ""; }
        public @MissingAnno String unresolved() { return ""; }
        public @NotNull C() {}
      }
      """));
  }

  public void testTypeUseAnnotationsOnArraysAndComponents() {
    // JLS 9.7.4 type-use annotations on array dimensions and component types. The pre-parameter
    // @NotNull on `one(@NotNull GoImportSpec o)` is a declaration-target annotation on the PARAMETER's
    // MODIFIER_LIST, distinct from the type-use form — it stays in `param[0] annotations:` as today.
    // The other shapes test annotations on arrays (`T @A []`), on components (`@A T`), on combinations
    // (`@A T @B []`), on primitive-array dims (`char @A []`), and on multi-dim arrays (`T @A [] @B []`).
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public @interface A {}
      public @interface B {}
      public class PsiReference {}
      public class PsiElement {}
      public class GoImportSpec {}
      public class C {
        public static PsiReference @A [] one(@A GoImportSpec o) { return null; }
        public @A PsiElement @B [] two() { return null; }
        public char @A [] three() { return null; }
        public String @A [] @B [] four() { return null; }
      }
      """));
  }

  // ---------------------------------------------------------------------------------------------
  // Parameter-type matching via supertype probe (helper behaviour, not extraction shape)
  // ---------------------------------------------------------------------------------------------

  public void testParamTypeMatchedViaSuperclass() {
    Map<Fqn, ClassSymbol> classes = extract("""
      package a.b;
      public class Base {}
      public class Child extends Base {}
      public class Receiver {
        public void take(Base b) {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    // Behaviour: caller passes Child as the param-type probe; receiver wants Base.
    JavaHelper helper = helperFrom(classes);
    assertEquals(1, helper.findClassMethods("a.b.Receiver", MethodType.INSTANCE, "take",  false, -1, "a.b.Child").size());
  }

  public void testParamTypeMatchedViaInterface() {
    Map<Fqn, ClassSymbol> classes = extract("""
      package a.b;
      public interface Iface {}
      public class Child implements Iface {}
      public class Receiver {
        public void take(Iface i) {}
      }
      """);
    assertClassInfoMatchesGolden(classes);

    JavaHelper helper = helperFrom(classes);
    assertEquals(1, helper.findClassMethods("a.b.Receiver", MethodType.INSTANCE, "take",  false, -1, "a.b.Child").size());
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private static @NotNull Map<Fqn, ClassSymbol> extract(@NotNull String source) {
    return extract(source, Map.of());
  }

  /**
   * Two-pass extraction with an optional pre-existing universe of foreign classes. The seed pass
   * builds @interface/class declarations without resolution; the real pass uses a resolver that
   * combines the seed map with {@code foreign} so cross-file references (e.g. a static import's
   * supertype walk) can succeed.
   */
  private static @NotNull Map<Fqn, ClassSymbol> extract(@NotNull String source,
                                                        @NotNull Map<Fqn, ClassSymbol> foreign) {
    SyntaxNode root = JavaSyntaxTreeManager.parseText(source);
    Map<Fqn, ClassSymbol> seed = JavaSyntaxClassExtractor.extractFrom(root, fqn -> foreign.get(fqn));
    SymbolResolver resolver = fqn -> {
      ClassSymbol s = seed.get(fqn);
      return s != null ? s : foreign.get(fqn);
    };
    return new HashMap<>(JavaSyntaxClassExtractor.extractFrom(root, resolver));
  }

  private static @NotNull JavaHelper helperFrom(@NotNull Map<Fqn, ClassSymbol> classes) {
    JvmClassSymbolProvider provider = (fqn, resolver) -> {
      ClassSymbol info = classes.get(fqn);
      return info == null ? Map.of() : Map.of(fqn, info);
    };
    return new JvmSyntaxHelper(new JvmClassSymbolManager(List.of(provider)));
  }
}
