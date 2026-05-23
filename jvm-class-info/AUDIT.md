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

### 4. Nullable extension-function receiver loses nullability — **NOT REPRODUCIBLE** (2026-05-23)
`KotlinSyntaxMethodExtractor.java:94` parses the receiver TYPE_REFERENCE through `typeFormatter.parseType`, which already routes NULLABLE_TYPE wrappers to `parseNullable` and emits `@Nullable` on the resulting `UserType`. Regression test `testExtensionFunctionReceiverNullability` confirms: `fun String?.shoutOrEmpty()` produces a receiver param with `@org.jetbrains.annotations.Nullable java.lang.String`, while `fun String.shout()` produces `@NotNull java.lang.String`. Closed; regression test added.

### 5. Empty-FQN placeholders on parse errors — **FIXED** (2026-05-23)
Investigation: java-syntax parser error recovery doesn't actually produce the malformed shapes the defensive branches guard against (verified by `testMalformedMethodGracefullyDegradesNotCrashes`), so the branches are cold in practice. Still worth fixing because the silent empty-FQN placeholder would be invisible if/when they ever fire.

Replaced the empty-FQN placeholder with a visible `<Missing type>` stub across **all 7 sites** that produced one — both Java and Kotlin extractors:
- `JavaSyntaxTypeFormatter.parseType` (TYPE node with no recognised child)
- `JavaSyntaxMethodExtractor.collectParameters` (PARAMETER without TYPE child)
- `KotlinSyntaxMethodExtractor.collectValueParameters` (VALUE_PARAMETER without TYPE_REFERENCE)
- `KotlinSyntaxTypeFormatter.parseType` (null typeNode + unrecognised-type fallthrough)
- `KotlinSyntaxTypeFormatter.formatTypeFqn` (null typeNode + unrecognised-type fallthrough)

All sites now go through `JvmTypeRefs.missingType(reason)` / `JvmTypeRefs.missingFqn(reason)`, which centralize the `Fqn.MISSING` constant (value `<Missing type>`) and the `LOG.warn` emission. `Fqn.MISSING` is distinct from `Fqn.ROOT` (the "no enclosing class" sentinel) so semantically-different empty FQNs don't share a representation.

### 6. Cycle-tolerance contract not enforced uniformly — **NOT REPRODUCIBLE** (2026-05-23)
Audited every `SymbolResolver.findClass` caller (`AbstractImportContext.resolveSimpleName`, `JavaSyntaxTypeFormatter.isTypeUseAnnotation`, `JavaSyntaxTypeFormatter.isConfirmedClass`, `NestedTypeResolver.walk` x2, `KotlinSyntaxClassExtractor.populateSuperTypes`). All 6 sites explicitly check the result against `null` before dereferencing — none of the audit's three specific claims hold against the current code:
- `isConfirmedClass` already does `findClass(...) != null` and the `resolvedHead` parameter is `@NotNull`.
- `resolveClassLiteralFqn` doesn't call `findClass` at all (pure AST string manipulation).
- `NestedTypeResolver.walk`'s cycle short-circuit correctly returns null because `candidate = classFqn.child(simple)` was already tested at the previous visit of this `classFqn`.

Regression test `testCyclicSupertypeResolutionDoesNotCrash` exercises the cycle path end-to-end with `A extends B, B extends A` and a dotted nested reference (`@A.Marker`) whose canonicalization walks the cyclic supertype chain. Test passes without NPE; the resolver returns the FQN it could deduce before the cycle broke recursion. Closed; regression test added.

---

## 🟠 P1 — ASM provider correctness bugs

Only triggered on the fallback path, but that path covers every stdlib / library class — so the blast radius is wide.

### 7. ASM `@Target` default not applied — **NOT REPRODUCIBLE** (2026-05-23)
The audit's stated consequence (`TypeUseAnnotationLifter` treats no-`@Target` annotations as non-TYPE_USE) is actually the **correct** JLS behaviour: when `@Target` is absent, the default explicitly excludes `TYPE_USE`, so empty `annotationTargets` → `isTypeUseAnnotation` returns false → annotation stays on declaration → matches JLS.

All three providers converge on the same empty-set-when-absent behaviour (`JavaSyntaxTypeFormatter.parseAnnotationTargetSet:418` docstring documents it explicitly; Kotlin and ASM agree). The only consumer of `annotationTargets` (`isTypeUseAnnotation`) checks specifically for `TYPE_USE`, which is correctly excluded by the JLS default. Existing test `testTypeUseAnnotationsOnArraysAndComponents` already exercises this with `@interface A` (no `@Target`): `@A` on a parameter stays in `param[0] annotations` and is never lifted.

Populating `annotationTargets` with "all-except-TYPE_USE-and-TYPE_PARAMETER" defaults would change ASM output without changing the only check that reads the set — pure regression risk for no consumer benefit.

### 8. ASM `returnType` can be null — **FIXED** (2026-05-23)
Was: `MethodSymbol.Builder.returnType` had no default, so a malformed ASM signature whose parsing aborted before `visitReturnType` would leak a null `JvmTypeRef` into the immutable record (the record's `@NotNull` is not runtime-enforced). Fix: defaulted the field to a `<Missing type>` stub (`new JvmTypeRef.UserType(Fqn.MISSING, …)`) so `build()` always produces a valid record, and any leak surfaces visibly via the canonical missing marker rather than NPE-ing downstream. Used the stub rather than the audit-suggested `void` because `void` is misleading (the method may have had a return type, we just couldn't decode it). Regression test `testMethodBuilderReturnTypeDefaultsToMissingStub` covers the default.

### 9. ASM `InputStream` leak in filename probe — **FIXED** (2026-05-23)
The loop itself doesn't actually leak (the `is == null && lastDot > 0` condition guarantees the loop stops on the first non-null result), but the audit's secondary point was correct: `is.close()` lived outside any try-with-resources, so a thrown `FileUtil.loadBytes` would leak the descriptor. Refactored to put the resource in try-with-resources for guaranteed close on read failure. No behavioural change on the happy path.

---

## 🟡 P2 — Source/ASM normalization gaps

These need design choices, not just code changes. Each one is a place where source and ASM disagree about how lowered-form artifacts should surface. Most are unblocked by the convergence test harness (P3-G below).

### 10. ASM doesn't filter `ACC_SYNTHETIC` / `ACC_BRIDGE` / `<clinit>` — **FIXED** (2026-05-23)
`AsmClassSymbolProvider.visitMethod` now returns null for `<clinit>`, `ACC_BRIDGE` (0x0040), and `ACC_SYNTHETIC` (0x1000) methods, so they're filtered at the extraction source. Convergence harness normalizer simplified — only the residual ACC_SUPER class flag + javac's synthesized default constructor (which is plain public, not synthetic) stay there. Two convergence tests added: `testGenericErasureBridgeIsFiltered` (Comparable bridge), `testStaticInitializerIsFiltered` (`<clinit>`).

### 11. Kotlin data-class synthetics missing
`data class Pair(val a: Int, val b: String)` produces only `getA()` / `getB()`. Bytecode has `copy(...)`, `component1()`, `component2()`, `equals()`, `hashCode()`, `toString()`. The `data` modifier is a written-in-source contract that promises these — synthesize on the source side. The alternative (have ASM filter them) loses semantics.

### 12. Kotlin enum-class synthetics missing
Same shape as #11 for `values(): T[]`, `valueOf(String): T`, and enum constants as static fields. Synthesize on the source side; the `enum class` modifier promises them by JLS.

### 13. Kotlin `@JvmOverloads` expansion
Source: one function with default parameter values. Bytecode: one canonical function plus N `$default` synthetic overloads. The source-level API is one function. ASM should filter the `$default` overloads (covered by #10's `ACC_SYNTHETIC` rule).

### 14. Kotlin `suspend` and the `Continuation` parameter
Source: `suspend fun foo(): Int`. Bytecode: `Object foo(Continuation<Integer>)`. The source view is the right view — consumers writing Kotlin code call `suspend fun foo()` directly. ASM should detect the suspend lowering and present the source-level signature (re-attach the return type from the Continuation's type argument, drop the Continuation parameter).

### 15. ASM doesn't decode `ACC_VARARGS` — **FIXED** (2026-05-23)
Decision: settle on the erased array form `T[]` (no `...` marker carried). Both providers already produce that representation for the parameter type. The remaining divergence was on the **method modifier bitmask**: ACC_VARARGS (0x0080) is the same bit value as `Modifier.TRANSIENT` for fields, so the ASM-side method rendered as "public transient" via `Modifier.toString`. Fixed in `AsmClassSymbolProvider.visitMethod` by clearing the 0x0080 bit before storing. Convergence test `testVarargsConvergesAsArray` covers it.

### 16. `internal` → `public` mapping doesn't mangle names
Kotlin compiles `internal fun foo()` to a public method with a mangled name (`foo$module-name`). Source maps visibility but not the name. ASM sees the mangled name. Choose un-mangled on both sides — mangling is a bytecode-level encoding, not part of the source API.

### 17. Java `scanPackage` swallows `IOException` — **FIXED** (2026-05-23)
Replaced `catch (IOException ignored) {}` with `LOG.warn("Failed to scan ... package directory: " + dir, e)` in both `JavaSyntaxClassSymbolProvider.scanPackage` and `KotlinSyntaxClassSymbolProvider.scanPackage`. Behaviour for missing-directory cases is unchanged (no exception thrown there), but legitimate access errors now surface to the log.

---

## 🟢 P3 — Architecture / duplication

Code quality, not behaviour. No user-visible change. Do **after** the convergence harness lands so refactors are backed by tests.

### 18. Variance decoding duplicated — **PARTIAL FIX** (2026-05-23)
Added `TypeProjection.Variance.fromBytecodeWildcard(char)` and routed the ASM site through it. **Did not** add a Kotlin-modifier equivalent: that helper would need to import `KtTokens` / `KotlinSyntaxNodes`, creating a dep from the shared `classinfo/` types into the language-specific `classinfo/kotlin/` package — wrong direction. The Kotlin site's logic (3 lines of `hasModifier` checks) stays local. Net: one fewer duplicate, no architectural compromise.

### 19. `buildDottedText` duplicated — **WON'T FIX** (2026-05-23)
Inspection showed the two implementations are not actually duplicates. Java's version is 12 lines over 3 token types (`JAVA_CODE_REFERENCE` / `DOT` / `IDENTIFIER`) with `StringBuilder` assembly; Kotlin's is 25 lines over 6+ token types (`REFERENCE_EXPRESSION` / `DOT_QUALIFIED_EXPRESSION` / `USER_TYPE` / `PACKAGE_DIRECTIVE` / `PACKAGE_KEYWORD` / etc.) with `List<String>` + `String.join`. Lifting to `SyntaxTreeUtil` would require parameterizing over language-specific predicates ("is identifier", "is dot", "should recurse"), trading 12+25 self-contained lines for a generic SPI plus two thin shims that pass constant predicates — premature abstraction for two non-isomorphic callers.

### 20. Import-extraction loop duplicated — **WON'T FIX** (2026-05-23)
The audit's claim "differ only in AST type-name constants and the null-check" was inspection-overstated. Real divergences:
- Java's version (37 lines) does JLS 7.5.1 nested-type canonicalization via `NestedTypeResolver.findDeclaringClass`.
- Kotlin's version (18 lines) does import-alias handling (`import x.y as z`).
- They use different AST vocabularies (IMPORT_STATEMENT vs IMPORT_DIRECTIVE, ASTERISK vs MUL).

Lifting would need ≥3 callbacks (find-list / is-import / extract-entry) replacing two methods that share maybe an 8-line for-loop skeleton. Premature abstraction with two non-isomorphic callers, same pattern as #19, #21, #22.

(Sidebar worth noting: Kotlin's `extractImports` does **not** apply JLS-style canonicalization to inherited nested-type imports; whether kotlinc does is a separate correctness question, out of scope for this dedup task.)

### 21. Built-in type lists overlap with drift — **DOCUMENTED, NOT LIFTED** (2026-05-23)
The "drift" framing was wrong on inspection — the two lists encode different language policies:
- Java auto-imports the **entire** `java.lang` package by JLS, so `JAVA_LANG_TYPES` is a comprehensive catch-set for names worth resolving before falling through to the resolver (boxed numerics, Throwable hierarchy, Iterable/Comparable, language annotations).
- Kotlin auto-imports `kotlin.*` instead. `JAVA_LANG_FALLBACK` is a much smaller after-the-fact list for names that survived `KOTLIN_AUTO_IMPORTS`, emphasising exception classes that Kotlin stdlib typealiases to `java.lang.*`.

Intersection (~14 names) overlaps but lifting only that would conflate intentional language-policy differences with accidental duplication. Both class-level constants now carry docstrings explaining why they differ, so future readers don't mistake the asymmetry for drift.

### 22. Modifier extraction duplicated — **WON'T FIX** (2026-05-23)
The audit overstated the duplication. Java's `extractModifiers` is 6 lines (pure bit-mask walk); Kotlin's is 18 lines (walk + visibility-default logic mapping `internal` / missing-visibility → `public`). The shared core is a 4-line `for + bits |= map.get(child.type)` loop — lifting it to a generic helper would save 4 lines per call site for two callers while leaving Kotlin's visibility-default logic still local. The "shared ModifierBitmaskFactory parameterized by visibility model" the audit proposed would add an abstraction layer with two implementations for two callers — premature abstraction per CLAUDE.md.

### 23. ASM file is monolithic (~550 lines)
`ClassVisitor`, `MethodVisitor`, `MethodSignatureVisitor`, `TypeRefBuilder`, and the type-annotation walker (`annotateAt` / `walk` / `appendAnnotation`) all nested in one file. The walker is the most painful — untestable in isolation. Extract `AsmTypeAnnotationWalker`.

### 24. Kotlin formatter mixes parsing / policy / annotation extraction
`KotlinSyntaxTypeFormatter.java` (~500 lines) combines `JVM_BUILTINS` + `KOTLIN_TO_JAVA_ALIASES` (policy/config), `parseType` / `parseUserType` (parser), and `extractAnnotationFqns` (extraction). Split into `KotlinTypeResolutionPolicy` + parser + annotation extractor.

### 25. Kotlin formatter mutates `nestedScope` per walk — **DEFERRED, DOCUMENTED** (2026-05-23)
Verified scope: full refactor touches ~30 method signatures across `KotlinSyntaxTypeFormatter` + both extractors (~200-line atomic diff). Each `KotlinSyntaxClassSymbolProvider` instance already owns its own formatter, so no cross-thread sharing happens in practice — the "future parallel ingestion" concern is forward-looking, not a current bug. Per the audit's own PR ordering, this belongs in Pass 4 (do after the convergence harness lands so refactors have a regression backstop).

Documented the not-thread-safe constraint on the formatter's class-level javadoc so any future caller sharing a formatter knows the contract. Will revisit once #33 lands.

### 26. Annotation extraction logic duplicated three ways
- ASM `MyAnnotationVisitor` (lines 308–336) with parameter-counter empty-annotation filter
- Java `collectTypeUseAnnotations:273–286` and `parseRefs:238–266` (same accumulate-then-merge pattern, intra-file duplication)
- Kotlin `extractAnnotationFqns`

Lift the "is this annotation effectively empty / source-only / hijacked" decision into a shared `AnnotationUtil`.

### 27. Type-use annotation lifting partly re-implemented in ASM
`AsmClassSymbolProvider` walks `TypePath` and modifies the JvmTypeRef tree; `TypeUseAnnotationLifter` does the same for declaration-position annotations. Either fold both into one (target, path)-driven walker, or have ASM call into the existing lifter.

---

## 🔵 P4 — Polish / forward-looking

### 28. Dead `@JvmStatic` lift block in Kotlin `walkObject` — **FIXED** (2026-05-23)
Removed the dead block (lines 288–304) where `origin` was unconditionally null. Also dropped the `enclosingClass` parameter from `walkObject` — only the dead block used it, and the only caller (`walkObject(child, Fqn.ROOT, null, Set.of())`) always passed null. Working `@JvmStatic` lift remains in `walkObjectInsideClassBody`.

### 29. Java `isAnnotationType` requires both AT and INTERFACE_KEYWORD — **NOT REPRODUCIBLE** (2026-05-23)
The current detection is correct: a well-formed `@interface` produces a CLASS node with both AT and INTERFACE_KEYWORD children; a regular `interface` has only INTERFACE_KEYWORD. The audit's "malformed @interface missing the @" scenario is itself a regular interface as written — the classification matches what the compiler would say. The only downstream consequence of being classified as annotation type is populating `annotationTargets` from `@Target`; regular interfaces leave that empty, which is correct. Regression test `testAnnotationTypeVsRegularInterfaceDetection` asserts both shapes via direct map inspection (the golden text doesn't surface `annotationTargets`).

### 30. ASM doesn't parse `Record` / `PermittedSubclasses` / `NestMembers` attributes — **DOCUMENTED AS GAP** (2026-05-23)
Decided not to implement: no downstream consumer in `JvmSyntaxHelper` / `JavaHelper` cares about record-component metadata, `permits` lists, or nest mates. The gap is symmetric (source providers don't surface them either), so convergence is preserved. Documented in `jvm-class-info/src/org/intellij/grammar/classinfo/asm/CLAUDE.md` as "Known gaps (intentional)" so future readers don't try to "fix" the omission without a real consumer.

### 31. ASM `// todo looks stupid` at line 81 — **FIXED** (2026-05-23, doc only)
The audit's claim about the all-slashes case being unreachable was wrong — iteration 1 starts with `lastDot = name.length()`, which puts every dot in the package half (all-slashes form). Loop trace for `com.foo.Outer.Inner`: iter 1 tries `com/foo/Outer/Inner.class`, iter 2 tries `com/foo/Outer$Inner.class`, etc. The "todo looks stupid" was about cosmetic dissatisfaction with the substring-rebuild trick, not correctness. Replaced the dismissive todo with a multi-line comment explaining the right-to-left probe invariant. No behaviour change.

### 32. Kotlin `Array<*>` element type hardcoded to `Object` — **DOCUMENTED** (2026-05-23)
Parent CLAUDE.md already had a one-liner; expanded the inline comment at `KotlinSyntaxTypeFormatter.parseArrayComponent` to explain the kotlinc rationale (JVM arrays are invariant → star projection has no wildcard equivalent → `Object` is the only type guaranteed to satisfy any element type) and call out the brittleness assumption (only stable as long as kotlinc keeps lowering `Array<*>` the same way).

---

## Convergence test harness — **LANDED** (2026-05-23)

Lives under `tests/org/intellij/grammar/java/syntax/convergence/`:

- `InProcessJavaCompiler` — drives `javax.tools.JavaCompiler` with `-parameters`. Falls back to instantiating `com.sun.tools.javac.api.JavacTool` via the platform classloader when `ToolProvider.getSystemJavaCompiler()` returns null (which it does under IntelliJ's `PathClassLoader`-based test runtime even though the JBR has `jdk.compiler`).
- `SourceAsmConvergence` — normalizers that bring both sides into convergence:
  - `dropBytecodeArtifacts`: drops `<clinit>` / `ACC_BRIDGE` / `ACC_SYNTHETIC` methods, drops javac's synthesized default constructor, clears the `ACC_SUPER` class flag (which renders as "synchronized" otherwise).
  - `erasePositionalParameterNames`: rewrites all parameter names to `p0`/`p1`/… on both sides — the ASM provider names positionally because it doesn't parse `MethodParameters`, and parameter names aren't part of JVM-visible matching.
  - `sortMembers`: sorts methods by (name, param count) so declaration-order doesn't matter.
  - Renders both sides via `ClassSymbolTextFormatter` so divergence shows up as a textual diff.
- `SourceAsmConvergenceTest` — initial cases: empty class, instance method, static method, generic method. All pass under the IntelliJ-Platform JBR.

**P2 workflow now:** each normalization fix (#10 / #11 / #12 / #13 / #14 / #15 / #16) should:
1. Add a convergence test case that fails before the fix.
2. Land the source/ASM change.
3. Drop the corresponding normalizer from `SourceAsmConvergence` — convergence should hold without it.

The Kotlin counterpart is deferred: it needs the kotlin-compiler-embeddable artifact plus Kotlin-side normalizers (suspend lowering, `$default` filter, `@JvmField`, etc.).

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
