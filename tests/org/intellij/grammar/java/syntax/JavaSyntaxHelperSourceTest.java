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

  public void testStaticWildcardImportOfNestedType() {
    // Coverage: `import static c.d.Outer.*;` exposes inner types by simple name via the wildcard
    // probe. `@Marker` resolves to `c.d.Outer.Marker` after the resolver confirms it exists.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Outer {
        public @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import static c.d.Outer.*;
      public class C {
        public void doIt(@Marker int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testStaticImportShadowsWildcard() {
    // Coverage: when both a single-type static import and a wildcard static import could provide
    // the same simple name, the single-type import wins — it's checked first in resolveSimpleName.
    // Distinguishable here because the two imports name different enclosing classes.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Outer {
        public @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package c.d;
      public class Other {
        public @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import static c.d.Outer.Marker;
      import static c.d.Other.*;
      public class C {
        public void doIt(@Marker int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testStaticImportOfMethodDoesNotBreakExtraction() {
    // Coverage: `import static java.lang.Math.max;` — the imported name `max` is a method, not a
    // class. Extraction must not crash, and the file's classes must extract cleanly. Annotations
    // are not relevant here since `max` would never be used as one; pin the basic shape.
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      import static java.lang.Math.max;
      public class C {
        public int doIt(int p) { return max(p, 0); }
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

  public void testRegularImportOfInheritedNestedType() {
    // Bug A: JLS 7.5.1 also applies to regular (non-static) imports. `import c.d.Sub.Marker`
    // where Marker is inherited from Sub's superclass must canonicalize to the declaring class.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Parent {
        public @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package c.d;
      public class Sub extends Parent {
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d.Sub.Marker;
      public class C {
        public void doIt(@Marker int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testRegularImportOfDirectlyDeclaredNestedType() {
    // Coverage: regular nested-type import where the nested type IS declared on the named class.
    // The supertype walk finds the direct match on the first hop — no change to FQN. Pins the
    // base case so we'd notice if the walk ever started rewriting direct-declaration imports.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Outer {
        public @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d.Outer.Marker;
      public class C {
        public void doIt(@Marker int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testDottedRefHeadResolvedViaImport() {
    // Bug B: `Sub.Inner x;` with `import com.foo.Sub` must qualify the head through imports so the
    // field type is recorded as `com.foo.Sub.Inner`, not the as-written `Sub.Inner` (which the old
    // code returned with a misleading "already qualified" comment).
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Sub {
        public @interface Inner {}
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d.Sub;
      public class C {
        public void doIt(@Sub.Inner int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testDottedRefHeadResolvedViaImportAndSupertypeWalk() {
    // Bug B + canonicalization: when the dotted-ref head resolves through imports and the nested
    // type is inherited from a supertype of the imported class, the final FQN must name the
    // declaring class. Composes head resolution with the supertype walk on the same call site.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Parent {
        public @interface Inner {}
      }
      """));
    universe.putAll(extract("""
      package c.d;
      public class Sub extends Parent {
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d.Sub;
      public class C {
        public void doIt(@Sub.Inner int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testLowercaseHeadResolvesViaImport() {
    // Bug 9: a dotted ref whose head starts with a lowercase letter (here `legacyType`) must still
    // resolve through file-level imports. The old uppercase-first-letter heuristic in resolveDotted
    // silently treated such heads as package-qualified and left the reference unresolved.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class legacyType {
        public @interface Inner {}
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d.legacyType;
      public class C {
        public void doIt(@legacyType.Inner int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testUnderscoreHeadResolvesViaImport() {
    // Bug 9: underscore-leading class names (`_Inner`) also need to flow through import resolution.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class _Inner {
        public @interface Leaf {}
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d._Inner;
      public class C {
        public void doIt(@_Inner.Leaf int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testDottedRefAlreadyFullyQualifiedUnchanged() {
    // Bug B guardrail: fully-qualified dotted refs (lowercase head, no import in scope) must NOT
    // be rewritten by head-resolution. `java.util.Map.Entry` stays as `java.util.Map.Entry`.
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public void doIt(java.util.Map.Entry e) {}
      }
      """));
  }

  public void testUnqualifiedInheritedNestedTypeRef() {
    // Bug 6: unqualified `Marker` referenced inside `class Sub extends Parent` where Parent declares
    // Marker must resolve to `c.d.Parent.Marker` (JLS 6.4.1 — inherited members are accessible by
    // simple name). Without the supertype walk it falls through to same-package `c.d.Marker`.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Parent {
        public @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package c.d;
      public class Sub extends Parent {
        public void doIt(@Marker int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("c.d.Sub"), universe.get(Fqn.of("c.d.Sub")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testMultiHopDottedRefCanonicalizes() {
    // Bug 7: `Sub.Inner.Leaf` where Sub is imported and Inner is inherited from Sub's supertype.
    // canonicalize must walk every hop, not just the last. The intermediate `Inner` segment must
    // be rewritten to its declaring class so the final FQN names canonical owners at each level.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Grand {
        public @interface Leaf {}
      }
      """));
    universe.putAll(extract("""
      package c.d;
      public class Parent {
        public static class Inner extends Grand {}
      }
      """));
    universe.putAll(extract("""
      package c.d;
      public class Sub extends Parent {
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d.Sub;
      public class C {
        public void doIt(@Sub.Inner.Leaf int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.C"), universe.get(Fqn.of("a.b.C")));
    assertClassInfoMatchesGolden(userFile);
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

  public void testMalformedMethodGracefullyDegradesNotCrashes() {
    // Audit task #5: probe what happens when the parser produces structurally-broken input.
    // The extractor must not crash; methods may end up with empty-FQN placeholders. This test
    // characterizes the current behavior so any future change to error handling is visible in
    // the golden diff.
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public class C {
        public void noParamType(int);
        public void empty();
        public  whatIsThis();
      }
      """));
  }

  public void testDottedRefThroughSiblingNestedClassCanonicalizes() {
    // Audit task #2: when a dotted ref's head is a *sibling* nested class (resolved through
    // `nestedScope` at line 342–343 of JavaSyntaxTypeFormatter, not through an import), intermediate
    // hops that are inherited from the sibling's supertype must canonicalize to the declaring class.
    // testMultiHopDottedRefCanonicalizes covers the import-resolved head; this one covers the
    // sibling/nested-scope head, which is the specific path the audit cited.
    Map<Fqn, ClassSymbol> universe = new HashMap<>();
    universe.putAll(extract("""
      package c.d;
      public class Parent {
        public @interface Marker {}
      }
      """));
    universe.putAll(extract("""
      package a.b;
      import c.d.Parent;
      public class Outer {
        public static class Sub extends Parent {}
        public void doIt(@Sub.Marker int x) {}
      }
      """, universe));
    Map<Fqn, ClassSymbol> userFile = new HashMap<>();
    userFile.put(Fqn.of("a.b.Outer"), universe.get(Fqn.of("a.b.Outer")));
    userFile.put(Fqn.of("a.b.Outer.Sub"), universe.get(Fqn.of("a.b.Outer.Sub")));
    assertClassInfoMatchesGolden(userFile);
  }

  public void testVarargsParameterRenderedAsSingleArrayDimension() {
    // Audit task #1: confirm `T... args` renders as `T[]` (one dimension), not `T[][]` or any other
    // doubled shape. Covers reference type, primitive, type-use annotation on the component, and the
    // legitimate `T[]...` form (varargs of arrays — *this* one is genuinely two-dim).
    assertClassInfoMatchesGolden(extract("""
      package a.b;
      public @interface A {}
      public class C {
        public void refs(String... args) {}
        public void prims(int... counts) {}
        public void annotated(@A String... names) {}
        public void arrayOfVarargs(String[]... mixed) {}
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
