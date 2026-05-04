# Grammar-Kit Architecture Overview

Grammar-Kit is a self-hosting JetBrains plugin that generates parsers, lexers, and PSI from BNF/JFlex grammars for the IntelliJ Platform. It is *self-hosting* in the sense that the BNF language it consumes is itself defined as a `.bnf` grammar, parsed by code that Grammar-Kit generated.

## Module layout

The project is organized as a multi-module Gradle build. Modules form a dependency hierarchy (arrows point to dependencies):

```
                       ┌─► :bnf-language ──┐
root (plugin) ─────────┤                   ├─► :parser-runtime ──► (platform)
                       └─► :jflex-language ┘
                       │
                       └─► :base
```

| Module            | Purpose                                                                                                                                                                                                                                                             |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `:base`           | Shared infrastructure: `GrammarKitBundle` (i18n), `BnfIcons`, message resources.                                                                                                                                                                                    |
| `:parser-runtime` | Language-agnostic parser runtime: `GeneratedParserUtilBase` (the engine every generated parser links against) plus `config.Options`/`config.Option`. No BNF or JFlex specifics.                                                                                     |
| `:bnf-language`   | BNF language model: `psi/`, `parser/`, `analysis/`, `java/`, language registration (`BnfLanguage`, `BnfFileType`, …), `KnownAttribute`, `LightPsi`, plus the code generator (`generator/`) and CLI entry (`Main.java`). Owns `grammars/Grammar.bnf` and BNF `gen/`. |
| `:jflex-language` | JFlex language support: PSI, parser, editor, file type. True peer of `:bnf-language` — depends only on `:base` and `:parser-runtime`. Owns `grammars/JFlex.bnf` and JFlex `gen/`.                                                                                   |
| Root project      | The IDE plugin itself: `actions/`, `editor/`, `inspection/`, `intention/`, `refactor/`, `search/`, `livePreview/`, `diagram/`, top-level UI providers, `plugin.xml`, signing, publishing.                                                                           |

The IntelliJ Platform Gradle plugin (v2) auto-detects subproject dependencies and bundles each module's jar into the plugin distribution.

## Why generator is bundled with `:bnf-language`

The `generator/` package was originally targeted for its own module. In practice, several PSI and analysis classes (`BnfReferenceImpl`, `BnfStringImpl`, `BnfFirstNextAnalyzer`, `GrammarUtil`) reach into generator utilities (`ParserGeneratorUtil.Rule`, `Renderer`, `GenOptions`, `JavaNameShortener`) for rule classification and naming logic. Cleanly extracting `:generator` would first require refactoring those PSI→generator callbacks — moving rule-classification helpers into a neutral location. Until that refactor lands, generator stays in `:bnf-language`.

## Bootstrap

The project has a clear bootstrap split:

**Tier 1 — Meta-layer** (`bnf-language/grammars/Grammar.bnf` → `bnf-language/gen/org/intellij/grammar/parser/GrammarParser.java` and `bnf-language/gen/.../psi/Bnf*`). These let the plugin itself read `.bnf` files at runtime.

**Tier 2 — Generator** (`bnf-language/src/org/intellij/grammar/generator/`). Consumes the user's BNF and emits a parser + PSI for *their* language. `Generator.java` is the sealed base; `JavaParserGenerator.java` and `KotlinParserGenerator.java` are the targets. `ExpressionGeneratorHelper.generateExpressionRoot()` handles operator-precedence climbing for expression parsers, driven by `ExpressionInfo`/`OperatorInfo`. The user-facing trigger is `actions/GenerateAction.java` (in the root plugin module).

## Subsystems

In `:bnf-language`:

- `parser/` — Runtime engine (`GeneratedParserUtilBase` is shipped as a template into generated parsers) plus the BNF lexer.
- `psi/` — PSI interfaces/impl/utility for BNF files.
- `generator/` — Code emission, rule graph analysis, expression precedence tables.
- `analysis/` — `BnfFirstNextAnalyzer` computes FIRST/FOLLOW sets used by precedence handling, recovery, and inspections.
- `java/` — `JavaHelper`: ASM-based Java-class introspection for target type resolution.
- `config/` — Generator option flags.

In root project (IDE plugin):

- `livePreview/` — Real-time in-IDE parser testing. `LivePreviewLanguage` registers a synthetic language per BNF; `LivePreviewParser` runs an interpreter-style parser; `LivePreviewHelper` reparses preview editors via a `MergingUpdateQueue` whenever the source grammar changes.
- `editor/` — Highlighting, annotators, gutter line markers.
- `inspection/` + `intention/` + `refactor/` + `search/` — Standard IDE plugin surface (left-recursion warnings, unused-rule detection, rename, find usages, etc.).
- `actions/` — Generate Parser, Run JFlex, Live Preview triggers.
- `diagram/` — Rule diagram via the IntelliJ diagram plugin.

## Build

Per-module `build.gradle.kts`:

- Library modules (`:base`, `:bnf-language`, `:jflex-language`) apply `org.jetbrains.intellij.platform.module` and pull only the bundled platform plugins they need (Java, IntelliLang).
- Root project applies the full `org.jetbrains.intellij.platform` plugin, owns `plugin.xml`, signing/publishing config, and the `buildPlugin` task graph. Java 17, Gradle 9.3.1, IDEA 2023.3.8.

The `buildPlugin` task produces a zip with one jar per module under `lib/`. `buildGrammarKitJar` is the (root-only) library jar published to Maven Central — see "Known follow-ups".

## Tests

`BnfTestSuite` aggregates fast (parsing, generation, utils) and slow (inspections, refactoring) suites. Tests extend `BnfGeneratorTestCase` / `AbstractParsingTestCase`; inputs live under `testData/<feature>/` and outputs are compared to golden files. Tests currently live in the root project and exercise classes across all modules via the project dependency graph.

When tests need the meta-grammars themselves (`SelfBnf.bnf`, `SelfFlex.bnf`), they reach into `bnf-language/grammars/` and `jflex-language/grammars/` via relative paths.

## Key takeaway

The interesting design choice is the **bootstrap split**: `:bnf-language/gen/` is committed so the plugin can build itself without a chicken-and-egg problem, and `GeneratedParserUtilBase` is both the runtime for `gen/`'s parser and the template stamped into every parser the plugin produces for users.

## Known follow-ups

- **Decouple `:generator`**: refactor `BnfReferenceImpl`, `BnfStringImpl`, `BnfFirstNextAnalyzer`, `GrammarUtil` to remove their dependencies on `ParserGeneratorUtil.Rule`, `Renderer`, `GenOptions`, `JavaNameShortener`. Then `generator/` can move to its own `:generator` module.
- **Aggregate library jar**: `buildGrammarKitJar` currently bundles only root-project classes. To publish a complete `grammar-kit` library to Maven Central post-split, it should also include classes from `:base`, `:bnf-language`, and `:jflex-language` (e.g., via `zipTree(project(...).tasks.jar.archiveFile)`).
- **Rename root → `:plugin`**: the root project still serves as the IDE-features module. Splitting it into a dedicated `:plugin` subproject (with a thin coordinator at root) would make the layout fully symmetric, but requires migrating signing/publishing/changelog config.
