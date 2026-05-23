# `jvm-class-info` audit — 2026-05-23

Findings from a three-branch audit (Java / Kotlin / ASM) of the JVM class-info pipeline. Each branch was investigated by a separate research agent. Findings are grouped by severity under the convergence model described below, then by theme so fixes can be planned in independently-shippable chunks.

Citations are `file:line` against `jvm-class-info/src/org/intellij/grammar/classinfo/…`.

## A note on "ASM as oracle" — it isn't

An earlier framing of this audit treated ASM as ground truth that the source providers must match byte-for-byte. **That's wrong.** Bytecode erases information that the source providers have:

- **Generics.** Source carries full type arguments, declared variance, and parameter type-use annotations. Bytecode keeps some of this in `Signature` attributes and discards the rest at use sites; raw-type call sites lose the signature entirely.
- **Compiler intent.** Source-level constructs (`suspend fun`, `data class`, `@JvmField`, `@JvmOverloads`, `internal`) compile to lower-level shapes (Continuation parameters, synthetic accessors, `$default` overloads, name mangling). ASM sees the lowered form; source sees the intent.
- **Synthetic noise.** Bridge methods, captured-locals accessors, `<clinit>`, enum `$VALUES` — all live in bytecode and have no source counterpart.

The two providers feed `JvmSyntaxHelper`'s string-based type matching, which needs them to **converge on the JVM-visible API surface as a source consumer would see it**. That usually means:

- **Source wins on richness** (generics, type-use annotations, declared nullability).
- **ASM should normalize away bytecode artifacts** (bridges, synthetics, `$default` overloads, Continuation parameters from `suspend`) so its view aligns with what source-level code would write.
- **Both must agree on canonical FQN form** (dotted source-style) and on a handful of narrow encoding rules where there genuinely is one right answer (variance → wildcard, primitive names, `Array<X>` → `X[]`).

## Priority model

The provider chain is **Extra → Kotlin source → Java source → ASM**, "first non-empty wins" (see parent CLAUDE.md). In typical use (running the generator with `--source-psi` against a real project) most user classes resolve through the source providers; ASM is the fallback for stdlib, libraries, and dependencies. That asymmetry drives the priorities:

- **P0** — Real correctness bugs in source providers. Fire on the typical resolution path. Each silently produces a wrong string that downstream string-matching can't recover from.
- **P1** — Real correctness bugs in the ASM provider. Only fire on the fallback path, but that path covers all platform/library classes, so impact is still wide.
- **P2** — Normalization gaps where source and ASM disagree about lowered-form artifacts. Source loses information (e.g. doesn't synthesize what `data` / `enum` / `@JvmField` promise); ASM carries information that shouldn't surface (bridges, `$default` overloads, Continuation parameters). These need design choices, not just code changes.
- **P3** — Architecture / duplication. Code-quality work, not behaviour.
- **P4** — Polish / forward-looking. Minor and/or speculative.

---

## 🔴 P0 — Source provider correctness bugs

These hit on the typical resolution path and produce objectively wrong strings.

### 1. Java varargs render as `T[][]` — **NOT REPRODUCIBLE** (2026-05-23)
`JavaSyntaxTypeFormatter.java:149` — the research agent claimed `ELLIPSIS` was treated identically to `LBRACKET` and that `foo(String... args)` emitted `java.lang.String[][]`. Verification (regression test `testVarargsParameterRenderedAsSingleArrayDimension`) shows the code already produces `java.lang.String[]` correctly — the parser emits exactly one `ELLIPSIS` token per varargs declaration, so the one-dim-per-token rule at line 149 is correct. Legitimate `String[]...` (varargs of arrays) also renders correctly as `java.lang.String[][]`. Closed; regression test added. **Lesson:** Explore agents read excerpts and can misjudge interactions across siblings — verify before trusting research-agent findings.

### 2. Java dotted nested ref not canonicalized after head resolution — **NOT REPRODUCIBLE** (2026-05-23)
`JavaSyntaxTypeFormatter.java:343` already calls `canonicalize()` on the resolved head + tail, and `canonicalize` already walks segment-by-segment through `NestedTypeResolver.findDeclaringClass`, which walks supertype chains to find the declaring class of each hop. Existing test `testMultiHopDottedRefCanonicalizes` covers the import-resolved-head path; new regression test `testDottedRefThroughSiblingNestedClassCanonicalizes` covers the `nestedScope`-resolved-head path (the exact path the audit cited). Both produce JLS-canonical output. Closed; regression test added.

### 3. Kotlin `@JvmField` produces getter, not field — **FIXED** (2026-05-23)
Was: extractor unconditionally synthesized `getX()` / `setX()` for `@JvmField val/var` properties; kotlinc emits no accessors for these because the backing field is exposed directly. Fix: added `KotlinSyntaxNodes.hasJvmField`; `synthesizeGetter` / `synthesizeSetter` now short-circuit when `@JvmField` is on the modifier list. (Field exposure itself is not modelled — `JavaHelper` has no field API — but accessor suppression brings source and ASM views into convergence at the method-list level.) Regression test `testJvmFieldSuppressesAccessorSynthesis` covers `val` / `var`, with non-`@JvmField` properties continuing to get accessors.

### 4. Nullable extension-function receiver loses nullability
`KotlinSyntaxMethodExtractor.java:93–96` — `fun String?.shouted()` synthesizes a first parameter without `@Nullable`. The `?` is right there in the source; the extractor just drops it on extension receivers.

### 5. Empty-FQN placeholders on parse errors
- `JavaSyntaxTypeFormatter.java:162` — null base TYPE produces `UserType(Fqn.of(""), …)`.
- `JavaSyntaxMethodExtractor.java:114–116` — parameter without TYPE child produces same.

A `Fqn.of("")` survives downstream and quietly breaks every type-matching call against the surrounding method. Fail loud or use a dedicated `ErrorType` sentinel.

### 6. Cycle-tolerance contract not enforced uniformly
Per parent CLAUDE.md: `SymbolResolver.findClass` returns null on cycles, and all extractors **must** be null-tolerant.
- `JavaSyntaxTypeFormatter.isConfirmedClass:368–369` — doesn't null-check `resolvedHead` before `Fqn.of`.
- `KotlinSyntaxTypeFormatter.resolveClassLiteralFqn:490` — same.
- `NestedTypeResolver.java:45–46` — on cycle hit returns null without probing the candidate at the current hop.

NPE risk under cycles; the cycle case is rare, but cyclic-supertype configurations exist in real code.

---

## 🟠 P1 — ASM provider correctness bugs

Only triggered on the fallback path, but that path covers every stdlib / library class — so the blast radius is wide.

### 7. ASM `@Target` default not applied
`AsmClassSymbolProvider.java:174–200` — when an annotation class has no explicit `@Target`, `annotationTargets` is left empty. JLS says the default is "all targets except `TYPE_USE` and `TYPE_PARAMETER`." Consequence: `TypeUseAnnotationLifter` treats every annotation in that class as non-TYPE_USE.

### 8. ASM `returnType` can be null
`MethodSignatureVisitor.returnType` stays null when `visitReturnType` isn't called (malformed signature). `MethodSymbol.Builder.build()` propagates null into the immutable record. Initialize to `JvmTypeRef.of("void")` defensively.

### 9. ASM `InputStream` leak in filename probe
`AsmClassSymbolProvider.java:75–89` — `do/while` reassigns `is` until success; earlier non-null streams aren't closed. The successful `is.close()` lives outside try-with-resources, so any exception between `loadBytes(is)` and the close also leaks. Wrap each probe in try-with-resources.

---

## 🟡 P2 — Source/ASM normalization gaps

These need design choices, not just code changes. Each one is a place where source and ASM disagree about how lowered-form artifacts should surface. Most are unblocked by the convergence test harness (P3-G below).

### 10. ASM doesn't filter `ACC_SYNTHETIC` / `ACC_BRIDGE` / `<clinit>`
`AsmClassSymbolProvider.java:208–218` — surfaces every method in the class file, including bridges from generic erasure, Kotlin `$default` overloads, captured-local accessors, and class initializers. These have no source counterpart. Filter `ACC_BRIDGE` always; filter `<clinit>` by name; filter `ACC_SYNTHETIC` with carveouts (enum members if we keep them; Kotlin companion-object accessors that the source extractor is expected to lift).

### 11. Kotlin data-class synthetics missing
`data class Pair(val a: Int, val b: String)` produces only `getA()` / `getB()`. Bytecode has `copy(...)`, `component1()`, `component2()`, `equals()`, `hashCode()`, `toString()`. The `data` modifier is a written-in-source contract that promises these — synthesize on the source side. The alternative (have ASM filter them) loses semantics.

### 12. Kotlin enum-class synthetics missing
Same shape as #11 for `values(): T[]`, `valueOf(String): T`, and enum constants as static fields. Synthesize on the source side; the `enum class` modifier promises them by JLS.

### 13. Kotlin `@JvmOverloads` expansion
Source: one function with default parameter values. Bytecode: one canonical function plus N `$default` synthetic overloads. The source-level API is one function. ASM should filter the `$default` overloads (covered by #10's `ACC_SYNTHETIC` rule).

### 14. Kotlin `suspend` and the `Continuation` parameter
Source: `suspend fun foo(): Int`. Bytecode: `Object foo(Continuation<Integer>)`. The source view is the right view — consumers writing Kotlin code call `suspend fun foo()` directly. ASM should detect the suspend lowering and present the source-level signature (re-attach the return type from the Continuation's type argument, drop the Continuation parameter).

### 15. ASM doesn't decode `ACC_VARARGS`
Trailing parameter renders as `T[]` instead of `T...`. Representation choice driven by #1: once Java is fixed to emit `T[]` (not `T[][]`), the two providers already converge if we accept `T[]` as canonical. Decoding `ACC_VARARGS` for `T...` rendering is only needed if we instead pick the syntactic form. Decide first, then act on both sides.

### 16. `internal` → `public` mapping doesn't mangle names
Kotlin compiles `internal fun foo()` to a public method with a mangled name (`foo$module-name`). Source maps visibility but not the name. ASM sees the mangled name. Choose un-mangled on both sides — mangling is a bytecode-level encoding, not part of the source API.

### 17. Java `scanPackage` swallows `IOException`
- `JavaSyntaxClassSymbolProvider.java:89` — `catch (IOException ignored) {}`.
- `KotlinSyntaxClassSymbolProvider.scanPackage:77` — same pattern.

Not a correctness bug, but a "we'll never know if it breaks" debuggability bug. Log at minimum.

---

## 🟢 P3 — Architecture / duplication

Code quality, not behaviour. No user-visible change. Do **after** the convergence harness lands so refactors are backed by tests.

### 18. Variance decoding duplicated
- Kotlin: `KotlinSyntaxTypeFormatter.parseProjection:318–328` (`out`/`in`/`*` from AST modifiers)
- ASM: `AsmClassSymbolProvider.visitTypeArgument:422–438` and `:525–530` (`+`/`-`/`*` from signature chars)

Both decode to `TypeProjection.Variance`. Consolidation point: static factories on `TypeProjection`:
```java
TypeProjection.Variance.fromKotlinModifier(SyntaxNode)
TypeProjection.Variance.fromBytecodeWildcard(char)
```

### 19. `buildDottedText` duplicated
Identical tree-walking code in `JavaSyntaxNodes` (~113–133) and `KotlinSyntaxNodes`. Lift to `SyntaxTreeUtil`.

### 20. Import-extraction loop duplicated
`JavaSyntaxImportContext.extractImports:68–106` and `KotlinSyntaxImportContext.extractImports` differ only in AST type-name constants and the null-check around `NestedTypeResolver.findDeclaringClass`. Lift the skeleton into `AbstractImportContext` parameterized by an import-statement predicate + a callback.

### 21. Built-in type lists overlap with drift
- `JavaSyntaxImportContext.JAVA_LANG_TYPES` (~32 entries)
- `KotlinSyntaxImportContext.JAVA_LANG_FALLBACK` (~17 entries — subset, with minor divergence)

Common core in `AbstractImportContext`; each branch extends with language-specific overlays.

### 22. Modifier extraction duplicated
`JavaSyntaxNodes.extractModifiers` vs `KotlinSyntaxNodes.extractModifiers` — ~30 lines each, same visibility-bitmask shape. Shared `ModifierBitmaskFactory` parameterized by visibility model.

### 23. ASM file is monolithic (~550 lines)
`ClassVisitor`, `MethodVisitor`, `MethodSignatureVisitor`, `TypeRefBuilder`, and the type-annotation walker (`annotateAt` / `walk` / `appendAnnotation`) all nested in one file. The walker is the most painful — untestable in isolation. Extract `AsmTypeAnnotationWalker`.

### 24. Kotlin formatter mixes parsing / policy / annotation extraction
`KotlinSyntaxTypeFormatter.java` (~500 lines) combines `JVM_BUILTINS` + `KOTLIN_TO_JAVA_ALIASES` (policy/config), `parseType` / `parseUserType` (parser), and `extractAnnotationFqns` (extraction). Split into `KotlinTypeResolutionPolicy` + parser + annotation extractor.

### 25. Kotlin formatter mutates `nestedScope` per walk
`walkClass` / `walkObject` swap a mutable field on the formatter instance. Unsafe for any future parallel ingestion. Pass scope as an explicit parameter.

### 26. Annotation extraction logic duplicated three ways
- ASM `MyAnnotationVisitor` (lines 308–336) with parameter-counter empty-annotation filter
- Java `collectTypeUseAnnotations:273–286` and `parseRefs:238–266` (same accumulate-then-merge pattern, intra-file duplication)
- Kotlin `extractAnnotationFqns`

Lift the "is this annotation effectively empty / source-only / hijacked" decision into a shared `AnnotationUtil`.

### 27. Type-use annotation lifting partly re-implemented in ASM
`AsmClassSymbolProvider` walks `TypePath` and modifies the JvmTypeRef tree; `TypeUseAnnotationLifter` does the same for declaration-position annotations. Either fold both into one (target, path)-driven walker, or have ASM call into the existing lifter.

---

## 🔵 P4 — Polish / forward-looking

### 28. Dead `@JvmStatic` lift block in Kotlin `walkObject`
`KotlinSyntaxClassExtractor.java:288–304` — `origin` is unconditionally null, making the entire block unreachable. Working path is `walkObjectInsideClassBody` (line 388).

### 29. Java `isAnnotationType` requires both AT and INTERFACE_KEYWORD
`JavaSyntaxNodes.java:78–80` — a malformed `@interface` missing the `@` token is misclassified as a regular interface. Edge case — depends on parser recovery.

### 30. ASM doesn't parse `Record` / `PermittedSubclasses` / `NestMembers` attributes
JDK 14+ records lose component metadata; JDK 15+ sealed classes lose their permits list. Source providers face the symmetric question. Document as known gap or implement if downstream needs it.

### 31. ASM `// todo looks stupid` at line 81
The substring rebuild trick works but the loop bound (`lastDot > 0`) means the all-dots-as-slashes case is never probed. Document or rewrite.

### 32. Kotlin `Array<*>` element type hardcoded to `Object`
`KotlinSyntaxTypeFormatter:285–287` — matches kotlinc's lowering; document the assumption.

---

## Convergence test harness — the unblocker

**There is no test that compares source-extracted `ClassSymbol`s against ASM-extracted ones for the same compiled source.** Such a test would catch every P0 above and define the normalization rules for P2 as code rather than prose.

It can't be naive equality. The shape:

- Allow source to carry *more* type information than ASM (generic args at use sites, type-use annotations).
- Run ASM output through normalizers: bridge/synthetic/`<clinit>` filtering, `suspend` lowering reversed, `@JvmOverloads` `$default`s dropped, internal-mangling un-applied.
- Where neither side has a clear advantage (varargs as `T...` vs `T[]`), require agreement on a chosen canonical form.

```java
assertSourceConvergesWithBytecode("foo/Bar.java", sourceText);
assertSourceConvergesWithBytecode("foo/Bar.kt", sourceText);
```

Without this, every P2 fix is being made blind, and P0 fixes lack regression coverage at the convergence boundary.

---

## Proposed PR groups

Each row is independently shippable.

| PR | Scope | Risk |
|---|---|---|
| **A. Source-side P0 bugs** | #1 varargs, #2 nested-FQN canonicalization, #3 `@JvmField`, #4 nullable extension receiver. Four small focused fixes; each verifiable with a single golden test. | Low. |
| **B. Robustness P0** | #5 empty-FQN placeholders, #6 cycle-tolerance audit. | Low. |
| **C. ASM P1 bugs** | #7 `@Target` default, #8 null return type, #9 stream leak. | Low. |
| **D. Convergence test harness** | The missing infrastructure. Compile-in-test for both Java and Kotlin; normalizers expressed as code. | Medium — new test infra, but contained. |
| **E. ASM normalization** | #10 (synthetic/bridge filtering) + #13 (`@JvmOverloads`, folds into #10) + #14 (`suspend` lowering reversal) + #15 (varargs representation choice). | Medium — needs D as backstop. |
| **F. Source-side synthesis** | #11 (data class), #12 (enum), #16 (`internal` name). | Medium-high — touches `ClassSymbol` shape; needs D. |
| **G. Architecture consolidation** | #18–22, #26. Mechanical duplication lifts. | Low–medium; do after D. |
| **H. ASM walker extraction** | #23 + #27 — `AsmTypeAnnotationWalker`. | Medium. |
| **I. Kotlin formatter split** | #24 + #25 — separate policy/parser/extractor; remove mutable `nestedScope`. | High — invasive; do last, with full coverage from D. |
| **J. Polish + debuggability** | #17 (log IOException), #28 (dead code), #29–32 (small standalone cleanups). | Trivial. |

## Recommended ordering

1. **A** + **B** + **C** in parallel. Pure correctness wins; each PR is small. No infrastructure dependencies.
2. **D**. Convergence harness. Unblocks everything in P2.
3. **E** + **F** in parallel (after D). The meaty normalization work.
4. **G**, then **H**, then **I**. Architecture cleanup, lowest urgency.
5. **J**. Drive-by alongside any of the above.
