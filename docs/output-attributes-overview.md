Per-attribute input & output path attributes — change overview & manual verification
====================================================================================

This document describes the changes on branch `medvedev/add-output-attributes`
(two commits on top of `master`: per-attribute *output* paths, then *input*
paths for class resolution) and lists hands-on checks you can run from the IDE
and from a terminal to confirm the new behavior.

What changed
------------

Before this branch, Grammar-Kit had exactly one attribute that pinned where
generated code lands — `psiOutputPath` (relative, always under the parser
output dir). Everything else followed a single `<output-dir>` either passed
to the headless `Main` CLI as a positional argument or derived in the IDE
from `parserClass`'s package. Class-lookup for things like `mixin`,
`parserUtilClass`, etc. used the whole project as scope — the new
`inputPath` / `psiInputPath` attributes below narrow that.

After this branch:

1. **Seven path-valued BNF attributes** can be declared in the grammar header.
   Each one is optional; values are resolved relative to the BNF file's
   parent directory; an empty string means "unset".

   Outputs (where generated artifacts are written):
   - `parserOutputPath`
   - `psiOutputPath`
   - `elementTypeHolderOutputPath`
   - `syntaxElementTypeHolderOutputPath`
   - `elementTypeConverterFactoryOutputPath`

   Inputs (which directory tree the IDE searches when resolving FQN-valued
   attributes):
   - `inputPath` — global default; covers every FQN-valued input attribute
     (`parserUtilClass`, `extends`, `psiImplUtilClass`, `mixin`,
     `implements`, …).
   - `psiInputPath` — PSI-side override covering `psiImplUtilClass`, per-rule
     `mixin`, and per-rule `implements`. One knob, because these three
     typically share a source root.

   **Output cascade** (`BnfPaths.applyCascade`). Two-level chain rooted at
   `parserOutputPath`: `psiOutputPath` → `parserOutputPath`, then the three
   element-type artifacts → the *effective* `psiOutputPath` (which may itself
   be the parser dir when PSI is unset). Element-type artifacts travel with
   PSI rather than with the parser, which matters when the two live in
   different source roots (e.g. a Kotlin parser with Java PSI). When
   `parserOutputPath` is unset, no output default is applied — instead
   `BnfPaths.compute` infers it from the `parserClass` package, so a grammar
   that sets nothing behaves exactly as before this branch.

   **Input cascade.** `psiInputPath` → `inputPath`. When `inputPath` itself is
   unset it defaults to the BNF file's parent directory — in the IDE via
   `BnfPaths.compute`, and headless via
   `BnfPaths.resolveExplicit(map, bnfParent)` — so existing grammars get a
   sane scope for free instead of project-wide lookup, and CLI and IDE agree.

   **Outputs double as input scopes.** FQN attributes that name generated
   artifacts (`parserClass`, `elementTypeHolderClass`,
   `syntaxElementTypeHolderClass`, `elementTypeConverterFactoryClass`,
   `psiPackage`, `psiImplPackage`) resolve against their own `*OutputPath`
   and deliberately do **not** fall back to the global `inputPath`. The full
   lookup order lives in `BnfPaths.referencePath`.

2. **`BnfPaths` / `BnfPathsResolution`** (in `:bnf-language`) is the single
   source of truth for path resolution, used by the IDE Generate Parser
   action, the headless `Main` CLI, `PsiHelperFactory`, the inlay-hints
   provider, and tests. Resolution is PSI-cached on
   `PsiModificationTracker.MODIFICATION_COUNT` and never creates directories.

3. **Headless CLI gains a flag-driven form.** New shape:

   ```
   java ... org.intellij.grammar.Main <grammar-file> [options]
   ```

   One flag per path attribute — the five outputs (`--parser-output`,
   `--psi-output`, `--element-type-holder-output`,
   `--syntax-element-type-holder-output`,
   `--element-type-converter-factory-output`) plus the two inputs
   (`--input-path`, `--psi-input`); see `README.md` for the full table — plus
   `--strict-paths` to make CLI/grammar conflicts fatal. The legacy
   positional form `Main <output-dir> <grammars or patterns>` still works
   and prints a deprecation warning to stderr. Multiple grammar files are
   only allowed in the legacy form.

4. **CLI ↔ grammar conflict handling.** Per attribute, CLI wins. When CLI
   and grammar disagree on the same attribute:

   - default: warning to stderr, CLI value used,
   - `--strict-paths`: error printed and exit code `1`.

   Attributes set on only one side pass through unchanged. The input flags
   follow exactly the same rules as the output flags.

5. **Scoped class resolution.** `PsiHelperFactory` is a project-level service
   that hands out `PsiHelper` instances bound to a
   `GlobalSearchScopesCore.directoriesScope` derived from
   `BnfPaths.referencePath(resolution, fqnAttribute)`. In the editor the
   scope is computed from the enclosing `BnfAttr`; in the generator,
   `Generator.helperFor(rule, attr)` caches one helper per FQN attribute
   against the already-resolved `BnfPathsResolution` (so CLI overrides and
   test fixtures are honored rather than re-resolving through the PSI cache).
   A null scope means "whole project", i.e. the pre-branch behavior.

   Only `PsiHelper` honors the scope. `AsmHelper` and `ReflectionHelper`
   (headless / classloader-based) are unaffected, and `helperFor` falls back
   to the shared unscoped helper when the service is absent.

   Note the registration change: `JavaHelper`/`PsiHelper` is no longer
   registered as a project service; `PsiHelperFactory` is
   (`resources/META-INF/plugin-java.xml`).

6. **Editor — declarative inlay hints.** Next to each path-attribute string
   literal in a `.bnf` file — inputs and outputs alike, since the provider
   gates on `BnfPaths.pathAttributeByName`, which is keyed off `BnfPaths.ALL`
   — the IDE shows the absolute on-disk path the value resolves to.
   Provider: `BnfPathAttributeInlayHintsProvider` (registered in
   `plugin.xml`, declarative-inlay group `OTHER_GROUP`).

7. **Editor — quick-doc.** Ctrl-Q / Cmd-J on any of the new attributes
   shows the description from
   `bnf-language/resources/messages/attributeDescriptions/<attr>.html`.
   Pages exist for all seven, including `inputPath.html` and
   `psiInputPath.html`.

8. **Editor — Cmd-click on classes in attributes.**
   `BnfStringRefContributor` now contributes Java-class references for
   every class-bearing attribute (`parserClass`, `parserUtilClass`,
   `psiImplUtilClass`, `psiTreeUtilClass`, `elementTypeClass`,
   `elementTypeHolderClass`, `elementTypeConverterFactoryClass`,
   `syntaxElementTypeHolderClass`, `tokenTypeClass`, `stubClass`,
   `psiPackage`, `psiImplPackage`, plus the existing
   `extends` / `implements` / `mixin`). Navigation to the class works on
   all of them, within the scope computed in (5).

9. **Refactoring.** Path-resolution duplication between
   `Main`, `BnfGenerationService`, and `FileGeneratorUtil` has been
   collapsed onto `BnfPaths`. `BatchGenerationContext` now carries one
   `Map<VirtualFile, VirtualFile>` per output category instead of just
   parser + psi, and the batch service refreshes every output dir after
   generation. The previous nested `GrammarPattern` record inside `Main`
   was promoted to its own top-level file.

10. **Tests.** `BnfPathsResolutionTest`, `OutputPathOverridesTest`, and new
    `MainTest` cases cover resolution rules, generator routing, CLI
    parsing, and conflict handling. Existing batch and Java/Kotlin
    generator tests were updated to the new generator constructor
    signatures (no positional `outputPath`; pass `BnfPathsResolution`
    instead).


Files of interest (`master..HEAD`)
----------------------------------

- New / single source of truth
  - `bnf-language/src/org/intellij/grammar/BnfPaths.java`
  - `bnf-language/src/org/intellij/grammar/BnfPathsResolution.java`
- Attributes & references
  - `bnf-language/src/org/intellij/grammar/KnownAttribute.java`
  - `bnf-language/src/org/intellij/grammar/psi/impl/BnfStringRefContributor.java`
  - `bnf-language/resources/messages/attributeDescriptions/*.html` (new + edited)
- Scoped class resolution
  - `bnf-language/src/org/intellij/grammar/java/PsiHelperFactory.java`
  - `bnf-language/src/org/intellij/grammar/java/PsiHelper.java`
  - `bnf-language/src/org/intellij/grammar/java/JavaHelper.java`
- Editor surface
  - `src/org/intellij/grammar/editor/BnfPathAttributeInlayHintsProvider.java`
  - `resources/META-INF/plugin.xml`
  - `resources/META-INF/plugin-java.xml`
- Headless CLI
  - `generator/src/org/intellij/grammar/Main.java`
  - `generator/src/org/intellij/grammar/CliArgs.java`
  - `generator/src/org/intellij/grammar/PathConflicts.java`
  - `generator/src/org/intellij/grammar/GrammarPattern.java`
  - `generator/src/org/intellij/grammar/UsageException.java`
- Generator integration
  - `generator/src/org/intellij/grammar/generator/Generator.java`
  - `generator/src/org/intellij/grammar/generator/JavaParserGenerator.java`
  - `generator/src/org/intellij/grammar/generator/KotlinParserGenerator.java`
  - `generator/src/org/intellij/grammar/generator/batch/BatchGenerationContext.java`
  - `generator/src/org/intellij/grammar/generator/batch/BnfGenerationService.java`
  - `generator/src/org/intellij/grammar/generator/batch/FileGeneratorUtil.java`
- Tests
  - `tests/org/intellij/grammar/BnfPathsResolutionTest.java`
  - `tests/org/intellij/grammar/MainTest.java`
  - `tests/org/intellij/grammar/generator/OutputPathOverridesTest.java`
- Docs
  - `README.md` (new "Headless CLI" section + path-attribute table)


Manual verification
-------------------

The checks below assume a working sandbox IDE (`./gradlew runIde`) and a
local checkout. Build first if needed:

```
./gradlew assemble
```

### A. Automated baseline (run before exploring manually)

```
./gradlew test
```

Look for green `MainTest`, `BnfPathsResolutionTest`, and
`OutputPathOverridesTest` in particular. They cover the bulk of the wiring;
manual checks below focus on what tests can't reach (UI, navigation,
multi-directory layout on disk).

### B. Headless CLI — new flag form

1. Pick any small grammar in the repo, e.g. `testData/generator/StubsAndCases.bnf`,
   or write a minimal one in a temp dir:

   ```
   mkdir -p /tmp/gk-cli && cd /tmp/gk-cli
   cat > Demo.bnf <<'EOF'
   {
     parserClass="demo.DemoParser"
     elementTypeHolderClass="demo.DemoTypes"
     psiPackage="demo.psi"
     psiImplPackage="demo.psi.impl"
     psiClassPrefix="X"
   }
   root ::= 'a' 'b'
   foo  ::= 'a' root
   EOF
   ```

2. Run the new flag form (use the classpath your build emits — typically the
   `runtime` classpath of `:generator` plus light-psi-all):

   ```
   java -cp <grammar-kit-and-deps> org.intellij.grammar.Main \
        Demo.bnf \
        --parser-output ./out/parser \
        --psi-output ./out/psi \
        --element-type-holder-output ./out/types
   ```

   Expect:
   - exit code `0`,
   - `out/parser/demo/DemoParser.java`,
   - `out/psi/demo/psi/{XRoot.java,XFoo.java,...}` and
     `out/psi/demo/psi/impl/{XRootImpl.java,...}`,
   - `out/types/demo/DemoTypes.java`,
   - **no** deprecation warning on stderr.

3. Re-run the legacy form and confirm the deprecation warning:

   ```
   java -cp ... org.intellij.grammar.Main ./out-legacy Demo.bnf
   ```

   Expect: stderr contains
   `warning: positional <output-dir> is deprecated; use --parser-output <dir>`,
   and `out-legacy/demo/DemoParser.java` is generated.

4. Run with no args / unknown flag / two grammars + `--flag`:

   ```
   java -cp ... org.intellij.grammar.Main                       # exit 0, prints usage
   java -cp ... org.intellij.grammar.Main --bogus Demo.bnf       # exit 1, "Unknown option"
   java -cp ... org.intellij.grammar.Main A.bnf B.bnf --parser-output o    # exit 1, "exactly one grammar file"
   ```

### C. CLI vs grammar conflict

1. Edit `Demo.bnf` to also pin `parserOutputPath`:

   ```
   {
     parserClass="demo.DemoParser"
     parserOutputPath="/tmp/gk-grammar-out"
     ...
   }
   ```

2. Run with a different `--parser-output`:

   ```
   java -cp ... org.intellij.grammar.Main Demo.bnf \
        --parser-output /tmp/gk-cli-out
   ```

   Expect: stderr contains
   `warning: parserOutputPath: CLI value '/tmp/gk-cli-out' overrides grammar value '/tmp/gk-grammar-out'`,
   and the file lands in `/tmp/gk-cli-out`.

3. Re-run with `--strict-paths` and confirm exit code `1` plus an `error:`
   line on stderr; nothing should be written to either directory.

4. Repeat 1–3 with `inputPath` in the grammar and `--input-path` on the CLI
   (and again with `psiInputPath` / `--psi-input`): the same warning shape
   and the same `--strict-paths` failure should apply.

### D. IDE — Generate Parser routes per attribute

1. `./gradlew runIde`.
2. Open any grammar in the sandbox. In the grammar header, add (paths
   are relative to the BNF file's parent):

   ```
   parserOutputPath="../build/gen-parser"
   psiOutputPath="../build/gen-psi"
   elementTypeHolderOutputPath="../build/gen-types"
   ```

3. Right-click the BNF file → **Generate Parser Code**.
4. Confirm three separate directories appear/refresh in the Project view,
   with the parser, PSI interfaces+impls, and the element-type holder
   each landing in its own directory.
5. Remove `elementTypeHolderOutputPath`, regenerate, and confirm the
   element-type holder now falls back to the *PSI* dir (cascade). Remove
   `psiOutputPath` as well and confirm both fall back to the parser dir.

### E. IDE — input paths narrow class resolution

1. In the sandbox project, create the same class FQN under two source roots,
   e.g. `srcA/com/example/MyMixin.java` and `srcB/com/example/MyMixin.java`.
2. In the grammar, reference it from a PSI-side attribute:

   ```
   {
     mixin="com.example.MyMixin"
     psiInputPath="../srcB"
   }
   ```

   Cmd-click should navigate to the `srcB` copy, not the `srcA` one.
3. Point `psiInputPath` at a directory that contains neither copy and confirm
   the reference is reported unresolved (scope really is applied, not just
   preferred).
4. Delete `psiInputPath` and add `inputPath="../srcA"`; confirm resolution
   falls back to the global input path and lands in `srcA`.
5. Delete both and confirm the default: lookup is scoped to the BNF file's
   parent directory. A class outside that tree that used to resolve
   project-wide will now be unresolved — this is the intended behavior
   change, and the fix is to declare `inputPath`.
6. Check a non-PSI FQN attribute (`parserUtilClass`) — it has no specific
   input override, so it follows the global `inputPath`. Check an
   output-backed one (`parserClass`) — it resolves against
   `parserOutputPath`, *not* `inputPath`.

### F. Inlay hints

1. Open a BNF that has any path attribute declared (`*OutputPath`,
   `inputPath` or `psiInputPath`).
2. To the right of each path literal, an inline gray hint shows the
   absolute resolved path, e.g.

   ```
   parserOutputPath = "../build/gen"   /Users/.../project/build/gen
   ```

3. Toggle the hint provider via **Settings → Editor → Inlay Hints →
   Other → Resolved path for path attributes** and confirm hints
   appear/disappear.
4. Edit a path string and confirm the hint updates within a typing
   pause (it's PSI-cache backed).

### G. Quick-doc

1. Place caret on any of: `parserOutputPath`, `psiOutputPath`,
   `elementTypeHolderOutputPath`, `syntaxElementTypeHolderOutputPath`,
   `elementTypeConverterFactoryOutputPath`, `inputPath`, `psiInputPath`.
2. Press Ctrl-Q (Linux/Win) or F1 / Cmd-J (Mac). The popup should show
   the description from
   `bnf-language/resources/messages/attributeDescriptions/<attr>.html`.

### H. Cmd-click on classes in attributes

1. Hover over each `mixin` / `parserUtilClass` / `implements` /
   `parserClass` value — the IDE highlights it as a resolvable Java
   reference (this exercises the `BnfStringRefContributor` change), and
   Cmd-click navigates to the class file.

### I. Backward compatibility — grammars with no new attributes

1. Open / regenerate a grammar from `testData/` that uses **only**
   `parserClass` (and possibly the old `psiOutputPath`).
2. Confirm output ends up in the same place it did on `master`. Diff the
   generated tree against pre-branch output if you keep one around — no
   files should change locations.
3. Confirm class references still resolve; if one doesn't, it was resolving
   from outside the BNF file's directory tree and now needs an explicit
   `inputPath` (see E.5).

### J. Tests touched by the refactor

Beyond running `./gradlew test`, spot-check these files compile and pass
in the IDE:

- `tests/org/intellij/grammar/BnfPathsResolutionTest.java` — resolution
  semantics: direct lookup, the input and output cascades, explicit-map
  construction, the `referencePath` fallback chain, and the `bnfParent`
  default seeding for `inputPath`.
- `tests/org/intellij/grammar/MainTest.java` — new cases for
  legacy/new CLI and conflict modes.
- `tests/org/intellij/grammar/generator/OutputPathOverridesTest.java` —
  inspects the `File` argument that `OutputOpener.openOutput` receives,
  so it isolates path routing from generated content.
- `tests/org/intellij/grammar/actions/BnfGenerationServiceTest.java`,
  `tests/org/intellij/grammar/generator/{Java,Kotlin}BnfGeneratorTest.java`,
  `BnfGeneratorPsiTest.java` — updated to the new generator
  constructor signatures and the wider `BatchGenerationContext` shape;
  they should still pass without further changes.

If any of B–I surfaces a regression, the most likely suspects are
`BnfPaths.compute`/`applyCascade`/`referencePath` (resolution semantics),
`PsiHelperFactory.scopeFor` (input scoping), and
`BnfGenerationService.prepareContext` / `ensureDirectory` (where
absolute on-disk paths are turned into VirtualFiles for the IDE).
