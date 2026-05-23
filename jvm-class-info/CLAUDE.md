# `jvm-class-info` — agent notes

This module builds `Map<Fqn, ClassSymbol>` snapshots of JVM classes from three sources — Java source, Kotlin source, and JVM bytecode (ASM) — and serves them through a single `JvmClassSymbolManager`. The output is consumed by `JvmSyntaxHelper` (`../bnf-language/src/org/intellij/grammar/java/JvmSyntaxHelper.java`), which is the `JavaHelper` flavour returned by `JavaHelperFactory` for headless/syntax-mode callers (the generator, MockProject tests).

Use this file as orientation before editing under this subtree. It's an index — read the linked source for detail.

## Provider chain

Built in `bnf-language/src/org/intellij/grammar/java/JavaHelperFactory.java::syntaxProviders` (around line 191). Order matters — first non-empty result wins; collisions log `LOG.warn(...)` and keep the earlier definition (`JvmClassSymbolManager.mergeBatch`, line 71).

1. `ExtraClassSymbolProvider` — pre-built classes pinned at the head. Used by the generator to surface not-yet-emitted PSI interfaces/impls.
2. `KotlinSyntaxClassSymbolProvider` — Kotlin sources. Beats a same-FQN Java file.
3. `JavaSyntaxClassSymbolProvider` — Java sources.
4. `AsmClassSymbolProvider` — bytecode fallback. Always last.

## SPI

`classinfo/JvmClassSymbolProvider.java` — single method `resolve(Fqn, SymbolResolver)`. The `SymbolResolver` passed in is the manager itself, so providers can recurse for unqualified imports / wildcard imports / same-package refs / generic bounds in foreign files.

`classinfo/SymbolResolver.java` — exposes `findClass(Fqn)`. Returns `null` for unknown FQNs **and** for cycle hits (an FQN currently being built returns `null` to break the cycle — see `JvmClassSymbolManager.inProgress`, line 46). **All extractors must be null-tolerant when calling the resolver.**

`classinfo/JvmClassSymbolManager.java` — one-shot. **No invalidation, ever.** Positive cache, negative cache, and provider-internal scanned-package/ingested-file sets are populated once and never cleared. Do not reuse a manager across builds. Construct one per generator invocation and discard. (See memory `[[jvm-symbols-cross-language]]`.)

## Canonical conventions

These are hard requirements that **must** hold across every provider — `JvmSyntaxHelper` parameter-type matching is string-based, so the providers need to converge on a single representation of the JVM-visible API surface. **Neither source nor ASM is "ground truth":** source carries generic information bytecode erases (declared type arguments, type-use annotations, declared variance), while ASM sees a lowered post-compilation form (synthetic accessors, bridge methods, `suspend` → Continuation, `@JvmOverloads` → `$default` overloads). Where they disagree, source usually wins on richness and ASM normalizes away bytecode artifacts. The narrow rules below are the points where there *is* one right answer and both sides must agree.

- **FQN form is dotted source-style.** `com.foo.Outer.Inner`. Never `Outer$Inner`. `Fqn.fromBytecode` normalizes `/` → `.` and `$` → `.` on the way in; the ASM provider probes JVM-style filenames right-to-left on lookup.
- **JVM primitive names** — `int`/`long`/`void`/etc. Kotlin `Int`/`Long`/`Unit`/`Nothing` map to these (`KotlinSyntaxTypeFormatter.JVM_BUILTINS`). Kotlin `String` → `java.lang.String`.
- **Variance → wildcards.** Kotlin `out X` → `? extends X`, `in X` → `? super X`, `*` → `?`. Implemented in `KotlinSyntaxTypeFormatter.formatProjection`. Mirrors `AsmClassSymbolProvider.visitTypeArgument` (lines 422–438) which decodes `+`/`-` from generic signatures.
- **`Array<X>` → `X[]`.** JVM arrays don't carry wildcards, so variance is stripped here. `Array<*>` → `java.lang.Object` (kotlinc's choice for the unbounded element type) — produces the string `java.lang.Object[]`.
- **Nullability annotations.** `@org.jetbrains.annotations.NotNull` / `@Nullable` are emitted only on non-primitive, non-void parameters/returns; **not** on bare type-variable references. See `KotlinSyntaxTypeFormatter.classifyNullability`.
- **Kotlin → Java aliases.** `kotlin.collections.List` → `java.util.List`, `kotlin.Any` → `java.lang.Object`, etc. (`KotlinSyntaxTypeFormatter.KOTLIN_TO_JAVA_ALIASES`). Kotlinc applies these at the compile boundary, so source providers must too.

## File map

### `classinfo/` (SPI + shared types)

| File                            | Role                                                                                                                                                   |
|---------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `JvmClassSymbolManager.java`    | Dispatch + cache + cycle protection. Implements `SymbolResolver`.                                                                                      |
| `JvmClassSymbolProvider.java`   | SPI: `resolve(Fqn, SymbolResolver) -> Map<Fqn, ClassSymbol>`.                                                                                          |
| `SymbolResolver.java`           | Read-only `findClass(Fqn)`.                                                                                                                            |
| `ClassSymbol.java`              | Immutable record: name, super, modifiers, multifileFacade, typeParams, interfaces, annotations, methods.                                               |
| `MethodSymbol.java`             | Immutable record: name, declaringClass, methodType, modifiers, return/param types (with inlined annotation FQNs), exceptions.                          |
| `ParameterSymbol.java`          | Immutable record: name, type, annotated type, annotations.                                                                                             |
| `TypeParameterSymbol.java`      | Immutable record: name, bounds, annotations.                                                                                                           |
| `MethodType.java`               | Enum: `STATIC`, `INSTANCE`, `CONSTRUCTOR`.                                                                                                             |
| `Fqn.java`                      | Dotted FQN value type. `Fqn.fromBytecode` normalizes from JVM internal form.                                                                           |
| `ExtraClassSymbolProvider.java` | Provider backed by a fixed `Map<Fqn, ClassSymbol>` — shadows on-disk providers.                                                                        |
| `AbstractImportContext.java`    | Shared base for per-file name resolution (explicit imports → built-ins → wildcards → same package).                                                    |
| `SourceRootResolver.java`       | FQN → source file path. Fast path is direct mapping; slow path walks dotted prefixes right-to-left for inner / package-private / multi-class-per-file. |
| `SyntaxTreeCache.java`          | Path → parsed tree cache; one-shot like the manager.                                                                                                   |
| `SyntaxTreeUtil.java`           | `firstChildOfType` / `childrenOfType` for `SyntaxNode` walking.                                                                                        |

### `classinfo/java/`

| File                                 | Role                                                                                                           |
|--------------------------------------|----------------------------------------------------------------------------------------------------------------|
| `JavaSyntaxClassSymbolProvider.java` | Provider entry. Two-tier lookup (fast direct path → slow package walk).                                        |
| `JavaSyntaxClassExtractor.java`      | Walks parsed file → `ClassSymbol.Builder`s for top-level + nested classes.                                     |
| `JavaSyntaxMethodExtractor.java`     | Method/constructor `MethodSymbol.Builder`s.                                                                    |
| `JavaSyntaxImportContext.java`       | Per-file scope. Built-ins are a small `java.lang` allow-list (~32 entries); imports from `import` + wildcards. |
| `JavaSyntaxTypeFormatter.java`       | Renders `TYPE` and `JAVA_CODE_REFERENCE` to dotted-FQN strings. Preserves `?`/`extends`/`super`/`[]`/`...`.    |
| `JavaSyntaxNodes.java`               | Java AST navigation helpers.                                                                                   |
| `JavaSyntaxTreeManager.java`         | Parser shim (delegates to the `java-syntax` library).                                                          |

### `classinfo/kotlin/`

| File                                   | Role                                                                                                                                                                                                                                    |
|----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `KotlinSyntaxClassSymbolProvider.java` | Provider entry. Merges `@file:JvmMultifileClass` siblings before sealing.                                                                                                                                                               |
| `KotlinSyntaxClassExtractor.java`      | File-level annotations, `<FileStem>Kt` class synthesis for top-level callables, `@JvmName` override, multifile-facade flagging.                                                                                                         |
| `KotlinSyntaxMethodExtractor.java`     | Functions / constructors / property accessors. Uses `KotlinSyntaxTypeFormatter.extractMethodAnnotations` to split `@Throws(...)` into the `exceptions` list and ignore source-only annotations.                                         |
| `KotlinSyntaxImportContext.java`       | Per-file scope. Built-ins are the Kotlin auto-imports (~68 entries) plus a small Java fallback. Handles import aliases.                                                                                                                 |
| `KotlinSyntaxTypeFormatter.java`       | `JVM_BUILTINS`, `PRIMITIVE_ARRAYS`, `KOTLIN_TO_JAVA_ALIASES`. `formatType` / `formatTypeFqn` / `formatProjection` / `classifyNullability` / `extractAnnotationFqns` / `extractMethodAnnotations`.                                       |
| `KotlinSyntaxNodes.java`               | AST helpers: `hasModifier`, `rightmostIdentifier` (for matching annotation simple names through `CONSTRUCTOR_CALLEE → TYPE_REFERENCE → USER_TYPE → REFERENCE_EXPRESSION → IDENTIFIER`), `buildDottedText`, modifier-bitmask conversion. |
| `KotlinSyntaxTreeManager.java`         | Parser shim (delegates to the `kotlin-syntax` / Fleet KMP library).                                                                                                                                                                     |

### `classinfo/asm/`

Single file: `AsmClassSymbolProvider.java`. Loads `.class` files via `ClassLoader.getResourceAsStream`, decodes with ASM (`ClassReader` + `SignatureReader`), normalizes `/` and `$` to `.`. ASM sees the post-compilation form, so its output is concrete but lossy: generic information may have been erased, and bytecode-only artifacts (bridges, synthetics, `<clinit>`, `suspend` Continuation params) appear that have no source counterpart. The ASM provider is responsible for normalizing those artifacts away so its output aligns with what a source-level consumer would see.

## Wire-up to `JavaHelper`

`bnf-language/src/org/intellij/grammar/java/JavaHelperFactory.java` is a project-level service.

- `buildHelper(dir, extras)` (line 166) picks between `JvmSyntaxHelper` (manager-backed; headless / MockProject / when registry flag `grammar.kit.psi.helper.use.syntax` is set) and `PsiHelper` (IDE-backed with an ASM-only fallback manager). Both call paths construct a manager via `syntaxProviders` / `psiFallbackProviders`.
- `JvmSyntaxHelper` (`JvmSyntaxHelper.java`) overrides `findClass`, `findClassMethods`, `getSuperClassName`. Method filtering walks the full supertype DAG via `isSubtype`. **Do not override `findRuleImplMethods` or `getClassReferences`** — they stay on the `JavaHelper` defaults.

## Recipes

### Support a new Kotlin annotation with JVM semantics

1. Decide where the annotation surfaces:
   - **Source-only, ignored at the JVM** (like `@Suppress`): add a simple-name check to `KotlinSyntaxTypeFormatter.isIgnoredAnnotation`. The check uses `KotlinSyntaxNodes.rightmostIdentifier` so both `@Foo` and `@kotlin.Foo` match.
   - **Special semantics that hijack the entry** (like `@Throws` → exceptions list, `@JvmStatic` → method type, `@JvmName` → method/class name): pattern-match the simple name in the relevant extractor before the generic `extractAnnotationFqns` / `extractMethodAnnotations` loop. Existing examples: `KotlinSyntaxTypeFormatter.extractMethodAnnotations` for `@Throws`; `KotlinSyntaxNodes.hasJvmStatic` for `@JvmStatic`; `KotlinSyntaxNodes.firstStringArgument` for reading `@JvmName("Foo")`.
   - **Regular annotation to surface as-is on the symbol**: nothing to do — `extractAnnotationFqns` already emits it.
2. Add a golden test (see below).

### Support a new type-shape

`KotlinSyntaxTypeFormatter.formatType` is the dispatch switch on `SyntaxElementType`. Add a branch there for the new shape (e.g. context receivers, definitely-non-nullable types). Cross-check the ASM output for the same source compiled — for type-shape rendering specifically (generic args, variance, array wraps), source and ASM should produce identical strings unless bytecode has erased something source still knows about.

The same applies to Java: `JavaSyntaxTypeFormatter.formatType`.

### Add a Kotlin → Java alias

Extend `KotlinSyntaxTypeFormatter.KOTLIN_TO_JAVA_ALIASES`. Cross-check kotlinc's actual mapping for the type at the compile boundary.

### Add a golden test

1. Add a `testFoo` method to `tests/org/intellij/grammar/java/syntax/kotlin/KotlinSyntaxHelperTest.java` (or the Java equivalent under `tests/org/intellij/grammar/java/syntax/`). Pattern: `write("pkg/File.kt", "...")` then `assertClassInfoMatchesGolden(extractAll())`.
2. First run will fail with the golden missing — re-run with `-Dgrammar.kit.override.test.data=true` or env `GRAMMAR_KIT_OVERRIDE_TEST_DATA=true` to write `testData/syntax/kotlin/file/testFoo.txt`. Inspect, commit. (Mechanism in `GoldenClassInfoTestCase.java`, lines 30–31 and 84–85.)
3. Tests are dispatched through `org.intellij.grammar.BnfTestSuite`. To run them with Gradle: `./gradlew test --tests "org.intellij.grammar.BnfTestSuite"`. Filtering by individual class via `--tests` does **not** work — the suite is the only registered include.

### Cross-language references

Sample trace: a Java file does `import kotlin.collections.*; ... List<String> xs`. `JavaSyntaxImportContext.resolveSimpleName("List")` walks: java.lang allow-list (miss) → explicit imports (miss) → wildcard imports → probes `kotlin.collections.List` via the `SymbolResolver`. The resolver is the manager, so it dispatches across providers — `KotlinSyntaxClassSymbolProvider` produces the symbol, `KOTLIN_TO_JAVA_ALIASES` rewrites the FQN to `java.util.List` at the formatter. The alias rewrite is essential because kotlinc applies the same mapping at the compile boundary, so both the source-resolved and the bytecode-resolved view agree on `java.util.List` as the canonical FQN.

## Gotchas

- Manager is **one-shot**. Never invalidated. Never reuse across builds.
- First provider wins on FQN collision; manager logs a warning. Order in `JavaHelperFactory.syntaxProviders` is load-bearing.
- `SymbolResolver.findClass` can return `null` from cycle protection — extractors **must** be null-tolerant.
- `Array<*>` element type is `java.lang.Object`, **not** `Object[]` directly — the `[]` comes from the array wrap in `formatUserType`.
- Test classes live under `tests/org/intellij/grammar/java/syntax/...` even when they're Kotlin-specific — the `java/syntax` prefix is the package, not a content filter.
- The `--tests` Gradle filter only accepts `BnfTestSuite`. Run the whole suite even when iterating on a single test method.
- `JvmSyntaxHelper` deliberately leaves `findRuleImplMethods` and `getClassReferences` on the `JavaHelper` defaults.
