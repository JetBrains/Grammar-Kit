# `classinfo/asm` — Bytecode (ASM) provider

Bytecode-backed branch of the JVM class-info pipeline. Loads `.class` files through a `ClassLoader`'s resources and decodes them with ASM (`ClassReader` + `SignatureReader`). **ASM is *not* the oracle for the source providers** — bytecode has erased information that source still carries (full generic signatures at use sites, declared variance, type-use annotations), and bytecode carries lowered artifacts that have no source counterpart (bridges, `$default` overloads from `@JvmOverloads`, `<clinit>`, `suspend` Continuation parameters, `@JvmField`-as-field-vs-getter, data-class synthetics). This provider's job is to produce a *normalized* view that aligns with what a source-level consumer would see, so its output converges with the source providers for `JvmSyntaxHelper`'s string-based matching. See the parent `jvm-class-info/CLAUDE.md` for the convergence model.

Single file: `AsmClassSymbolProvider.java`. Position in the chain: **last**. Fallback for platform/library classes that no source root can produce.

## Lookup

`findClassSafe(Fqn, ClassLoader)` walks dotted prefixes of the FQN right-to-left and tries each `.`/`/`/`$` permutation, so `com.foo.Outer.Inner` resolves either `com/foo/Outer/Inner.class` or `com/foo/Outer$Inner.class`. First non-null `getResourceAsStream` wins.

## Decoder shape

A single `ClassVisitor` collects:
- Class name + supertypes (`visit`).
- Class-level annotations and type-use annotations on supertypes (`visitAnnotation`, `visitTypeAnnotation`).
- `@Target` for annotation classes (drives `TypeUseAnnotationLifter` downstream).
- Methods: a per-method `MethodVisitor` decodes parameter names, annotations (with type-path lifting), exception list, and return/parameter types via a `MethodSignatureVisitor`.

`MethodSignatureVisitor` extends ASM's `SignatureVisitor` and reconstructs structured `JvmTypeRef`s from generic-signature descriptors. `TypeRefBuilder` is the nested builder collecting projections (variance from `+`/`-`/`*`) and array layers.

Pending type-use annotations (declaration-position annotations whose `@Target` includes `TYPE_USE`) are buffered and applied via `annotateAt` after the type tree is built. Type paths from `RuntimeVisibleTypeAnnotations` are walked node-by-node into the JvmTypeRef tree.

## Known gaps (intentional)

These bytecode features are **not** surfaced by the ASM provider, and they are **also not** surfaced by the source providers. They have no consumer in `JvmSyntaxHelper` / `JavaHelper` today, so the gaps are symmetric and don't break convergence.

- **JDK 14+ `Record` attribute** — component names + erased types of a record class. `RecordComponentVisitor` would surface them; currently we just treat the synthesized accessor methods as ordinary methods.
- **JDK 15+ `PermittedSubclasses` attribute** — list of permitted subtypes of a `sealed` class. ASM `visitPermittedSubclass` callback exists but is unused; `sealed` modifier itself shows up as `ABSTRACT` in our modifier bitmask.
- **`NestMembers` / `NestHost`** — JVM 11+ nest mates for private cross-access between inner classes. No consumer.

Implement only when a downstream feature actually needs the metadata; until then, the symmetry between providers is the more important property.

## Canonical-form responsibilities

- **`/`→`.` and `$`→`.` via `Fqn.fromBytecode`** at every entry of a class/type name. Called from `visit`, `visitAnnotation` / `visitTypeAnnotation`, `visitClassType` in `MethodSignatureVisitor`, etc.
- **Variance decode**: `+`→`OUT`, `-`→`IN`, `*`→`*` (lines ~525–530). Result feeds `TypeProjection.Variance`.
- **Primitive descriptors**: `I`→`int`, `[I`→`int[]`, etc., via the standard `Type.getType` indirection.

## Audit findings (2026-05-23)

### Architecture / robustness

- **`InputStream` leak in the filename probe loop** (lines 75–89). The `do/while` reassigns `is` until a probe succeeds, but earlier non-null streams are not closed. Plus the successful `is.close()` (line 89) lives outside a try-with-resources, so any exception between `loadBytes(is)` and the close leaks the descriptor. Wrap each probe in try-with-resources.
- **Monolithic 500+ line file**, nesting `ClassVisitor`, `MethodVisitor`, `MethodSignatureVisitor`, `TypeRefBuilder`, and the type-annotation walker. The walker (`annotateAt` / `walk` / `appendAnnotation`) deserves extraction as its own testable unit. Same for the annotation visitor with parameter-count tracking.
- **No validation on malformed bytecode** — exceptions logged via `reportException` and silently returns null. A truncated `.class` or invalid signature string yields a partially built `ClassSymbol`. Either fail loud or guard `MethodSymbol.Builder.build()` against null/empty fields.
- **`todo looks stupid` comment at line 81** — the substring-rebuild trick works but the loop bound (`lastDot > 0`) means the all-dots-as-slashes case never gets probed. Document or rewrite.

### Convergence gaps with source providers

- **No filtering of `ACC_SYNTHETIC` / `ACC_BRIDGE` methods**. ASM surfaces every method in the class file, including:
  - Enum `values()` / `valueOf(String)` (compiler-synthesized but `ACC_SYNTHETIC` is *not* always set — sometimes only on the enum-`$VALUES` field).
  - Kotlin `$default` overloads from `@JvmOverloads`.
  - Bridge methods from generic erasure.
  - Inner-class `this$0` / captured-locals accessors.
  - `<clinit>()` class initializers.

  Source providers don't (and shouldn't) emit these — they're bytecode artifacts. Filter by name (`<clinit>`) and by modifier (`ACC_BRIDGE`, optionally `ACC_SYNTHETIC` for non-enum cases). For the data-class / enum / `@JvmOverloads` story, the *source* providers should synthesize the missing source-level methods, while ASM filters the synthetic ones — neither view is complete on its own, but with both normalizations they converge on the source-level API surface.

- **No `Record` attribute parsing**. JDK 14+ records: component list, canonical constructor, auto-accessors. Currently surfaces only what bytecode exposes as regular methods (which is most of it, but the `Record` attribute carries metadata sources need).
- **No `PermittedSubclasses` attribute parsing**. JDK 15+ sealed classes — the permits list is lost.
- **`ACC_VARARGS` not decoded**. The trailing parameter type ends up as `T[]` instead of `T...`. Combined with the Java varargs bug (`T[]`→`T[][]`), the two providers disagree about how varargs should be represented. Pick a single representation (`T...` or `T[]` + a varargs modifier bit) and apply on both sides.
- **`@Target` default not applied** when an annotation class has no `@Target` (lines 174–200). Per JLS: defaults to everything except `TYPE_USE` and `TYPE_PARAMETER`. Currently leaves `annotationTargets` empty, causing `TypeUseAnnotationLifter` to treat all such annotations as non-type-use.
- **`null` return type on malformed signature**. `MethodSignatureVisitor.returnType` stays null if `visitReturnType` is never called; `method.build()` propagates null. Initialize to `JvmTypeRef.of("void")` as a defensive default.

### Duplication with source providers

- **Variance decoding** mirrors `KotlinSyntaxTypeFormatter.parseProjection`. Centralize in `TypeProjection`:
  ```java
  public static Variance fromBytecodeWildcard(char c) { … }
  ```
- **Annotation extraction with empty-parameter filtering** (`MyAnnotationVisitor`, lines 308–336) mirrors logic in both source formatters. Lift into a shared `AnnotationUtil`.
- **Type-use annotation lifting** partly re-implements `TypeUseAnnotationLifter`. The lifter is for declaration-position annotations; the ASM provider does TYPE_PATH-driven lifting. Consider folding both into one walker that takes (target, path) tuples.

### Test gaps to add (highest value: convergence tests)

1. **Convergence harness (Java)**: compile Java source → load via ASM → canonicalize both sides through the same normalizers (filter synthetics/bridges/`<clinit>`, agreed varargs representation) → assert structural equivalence with `JavaSyntaxClassSymbolProvider` output. Cover generics, varargs, type-use annotations, inner classes.
2. **Convergence harness (Kotlin)**: compile Kotlin source → load via ASM → canonicalize (filter `$default` overloads, reverse `suspend` Continuation lowering, drop synthetic accessors) → assert structural equivalence with `KotlinSyntaxClassSymbolProvider`. Cover data classes, enums, `@JvmField`, `@JvmStatic`, `@JvmName`, suspend, `@file:JvmMultifileClass`.
3. Inner-class FQN: `class Outer { class Inner { class Deep {} } }` → `Outer.Inner.Deep`, never `Outer$Inner$Deep`.
4. Generic inner: `Outer<T>.Inner<U>` signature → args cleared at each nesting hop, FQN built correctly.
5. Type-use annotation on a deep generic: `@A List<@B Map<@C String, @D Integer>>` — each annotation lands on the correct type-tree node.
6. Sealed class (JDK 15+): assert `PermittedSubclasses` is surfaced or document it as a known gap.
7. Record (JDK 14+): canonical constructor + auto-accessors + `Record` attribute decoded.
8. Enum: `values()` / `valueOf()` are filtered (not surfaced) so the ASM view converges with the source view (alternatively: filter on the ASM side, synthesize on the source side — pick one).
9. Varargs: `void log(String... args)` → `ACC_VARARGS` decoded, last param renders as `String...` or matches the source providers' convention.
10. Annotation class without `@Target` → defaults applied, `TypeUseAnnotationLifter` behaves correctly.
11. Malformed/truncated `.class` file → caught, logged, `null` returned, no leaked descriptor.
12. Annotation default values (`@Foo(x="bar")`) → still emitted by `MyAnnotationVisitor` (parameter-count filter doesn't suppress).
