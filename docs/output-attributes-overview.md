Per-attribute path overrides — change overview & manual verification
=====================================================================

This document describes the changes on branch `medvedev/add-output-attributes`
(now squashed into a single commit on top of `master`) and lists hands-on
checks you can run from the IDE and from a terminal to confirm the new
behavior.

What changed
------------

Before this branch, Grammar-Kit had exactly one attribute that pinned where
generated code lands — `psiOutputPath` (relative, always under the parser
output dir). Everything else followed a single `<output-dir>` either passed
to the headless `Main` CLI as a positional argument or derived in the IDE
from `parserClass`'s package. Class-lookup for things like `mixin`,
`parserUtilClass`, etc. used the whole project as scope.

After this branch:

1. **Five path-valued BNF attributes** can be declared in the grammar header.
   Each one is optional; values are resolved relative to the BNF file's
   parent directory; an empty string means "unset".

   Outputs (where generated artifacts are written):
   - `parserOutputPath`
   - `psiOutputPath`
   - `elementTypeHolderOutputPath`
   - `syntaxElementTypeHolderOutputPath`
   - `elementTypeConverterFactoryOutputPath`

   The output cascade is preserved: an unset child output falls back to its
   parent (parser → psi → et-holder/syntax/converter). A grammar that sets
   only `parserOutputPath` behaves exactly as before this branch.

2. **`BnfPaths` / `BnfPathsResolution`** (in `:bnf-language`) is the single
   source of truth for path resolution, used by the IDE Generate Parser
   action, the headless `Main` CLI, the new inlay-hints provider, and tests.
   Resolution is PSI-cached on `PsiModificationTracker.MODIFICATION_COUNT`
   and never creates directories.

3. **Headless CLI gains a flag-driven form.** New shape:

   ```
   java ... org.intellij.grammar.Main <grammar-file> [options]
   ```

   One flag per path attribute (see `README.md` for the full table) plus
   `--strict-paths` to make CLI/grammar conflicts fatal. The legacy
   positional form `Main <output-dir> <grammars or patterns>` still works
   and prints a deprecation warning to stderr. Multiple grammar files are
   only allowed in the legacy form.

4. **CLI ↔ grammar conflict handling.** Per attribute, CLI wins. When CLI
   and grammar disagree on the same attribute:

   - default: warning to stderr, CLI value used,
   - `--strict-paths`: error printed and exit code `1`.

   Attributes set on only one side pass through unchanged.

5. **Editor — declarative inlay hints.** Next to each `*OutputPath` string
   literal in a `.bnf` file, the IDE shows the absolute on-disk path the
   value resolves to. Provider:
   `BnfPathAttributeInlayHintsProvider` (registered in `plugin.xml`,
   declarative-inlay group `OTHER_GROUP`).

6. **Editor — quick-doc.** Ctrl-Q / Cmd-J on any of the new attributes
   shows the description from
   `bnf-language/resources/messages/attributeDescriptions/<attr>.html`.

7. **Editor — Cmd-click on classes in attributes.**
   `BnfStringRefContributor` now contributes Java-class references for
   every class-bearing attribute (`parserClass`, `parserUtilClass`,
   `psiImplUtilClass`, `psiTreeUtilClass`, `elementTypeClass`,
   `elementTypeHolderClass`, `elementTypeConverterFactoryClass`,
   `syntaxElementTypeHolderClass`, `tokenTypeClass`, `stubClass`,
   `psiPackage`, `psiImplPackage`, plus the existing
   `extends` / `implements` / `mixin`). Navigation to the class works on
   all of them.

8. **Refactoring.** Path-resolution duplication between
   `Main`, `BnfGenerationService`, and `FileGeneratorUtil` has been
   collapsed onto `BnfPaths`. `BatchGenerationContext` now carries one
   `Map<VirtualFile, VirtualFile>` per output category instead of just
   parser + psi, and the batch service refreshes every output dir after
   generation. The previous nested `GrammarPattern` record inside `Main`
   was promoted to its own top-level file.

9. **Tests.** `BnfPathsResolutionTest`, `OutputPathOverridesTest`, and new
    `MainTest` cases cover resolution rules, generator routing, CLI
    parsing, and conflict handling. Existing batch and Java/Kotlin
    generator tests were updated to the new generator constructor
    signatures (no positional `outputPath`; pass `BnfPathsResolution`
    instead).


Files of interest (squashed commit `master..HEAD`)
--------------------------------------------------

- New / single source of truth
  - `bnf-language/src/org/intellij/grammar/BnfPaths.java`
  - `bnf-language/src/org/intellij/grammar/BnfPathsResolution.java`
- Attributes & references
  - `bnf-language/src/org/intellij/grammar/KnownAttribute.java`
  - `bnf-language/src/org/intellij/grammar/psi/impl/BnfStringRefContributor.java`
  - `bnf-language/resources/messages/attributeDescriptions/*.html` (new + edited)
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
   element-type holder now falls back to the parser dir (cascade).

### E. Cmd-click on classes in attributes

1. Hover over each `mixin` / `parserUtilClass` / `implements` /
   `parserClass` value — the IDE highlights it as a resolvable Java
   reference (this exercises the `BnfStringRefContributor` change), and
   Cmd-click navigates to the class file.

### F. Inlay hints

1. Open a BNF that has any `*OutputPath` declared.
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
   `elementTypeConverterFactoryOutputPath`.
2. Press Ctrl-Q (Linux/Win) or F1 / Cmd-J (Mac). The popup should show
   the description from
   `bnf-language/resources/messages/attributeDescriptions/<attr>.html`.

### H. Backward compatibility — grammars with no new attributes

1. Open / regenerate a grammar from `testData/` that uses **only**
   `parserClass` (and possibly the old `psiOutputPath`).
2. Confirm output ends up in the same place it did on `master`. Diff the
   generated tree against pre-branch output if you keep one around — no
   files should change locations.

### I. Tests touched by the refactor

Beyond running `./gradlew test`, spot-check these files compile and pass
in the IDE:

- `tests/org/intellij/grammar/BnfPathsResolutionTest.java` — pure
  resolution semantics: cascade and explicit-map handling.
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

If any of B–H surfaces a regression, the most likely suspects are
`BnfPaths.compute`/`applyCascade` (resolution semantics) and
`BnfGenerationService.prepareContext` / `ensureDirectory` (where
absolute on-disk paths are turned into VirtualFiles for the IDE).
