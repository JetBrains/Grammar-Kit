# Grammar-Kit Architecture Overview

Grammar-Kit is a self-hosting JetBrains plugin that generates parsers, lexers, and PSI from BNF/JFlex grammars for the IntelliJ Platform. It is *self-hosting* in the sense that the BNF language it consumes is itself defined as a `.bnf` grammar, parsed by code that Grammar-Kit generated.

## Module layout

The project is organized as a multi-module Gradle build. Each module's direct dependencies on other modules:

```
root (plugin)         depends on  :generator, :bnf-language, :jflex-language, :parser-runtime, :base
:generator            depends on  :bnf-language, :parser-runtime, :base
:bnf-language         depends on  :parser-runtime, :base
:jflex-language       depends on  :parser-runtime, :base
:parser-runtime       — no module deps (uses IntelliJ Platform directly)
:base                 — no module deps (uses IntelliJ Platform directly)
```

Notable shape: `:bnf-language` and `:jflex-language` are **peers** — neither depends on the other. They share `:parser-runtime` (which holds the language-agnostic `GeneratedParserUtilBase`).

| Module            | Purpose                                                                                                                                                                                                                                                                                          |
|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `:base`           | Shared infrastructure: `GrammarKitBundle` (i18n), `BnfIcons`, message resources.                                                                                                                                                                                                                 |
| `:parser-runtime` | Language-agnostic parser runtime: `GeneratedParserUtilBase` plus `config.Options`/`config.Option`. No BNF or JFlex specifics.                                                                                                                                                                    |
| `:bnf-language`   | BNF language model: `psi/`, `parser/`, `java/`, language registration (`BnfLanguage`, `BnfFileType`, …), `KnownAttribute`, `LightPsi`. Plus PSI-shared rendering and naming primitives (`Renderer`, `JavaRenderer`, `KotlinRenderer`, `Case`, `NameFormat`, `NameShortener`, `JavaNames`, `*BnfConstants`) that `:generator` and PSI both consume. Owns `grammars/Grammar.bnf` and BNF `gen/`. |
| `:jflex-language` | JFlex language support: PSI, parser, editor, file type. True peer of `:bnf-language` — depends only on `:base` and `:parser-runtime`. Owns `grammars/JFlex.bnf` and JFlex `gen/`.                                                                                                                  |
| `:generator`      | Pure code emission: `Generator`, `JavaParserGenerator`, `KotlinParserGenerator`, `ExpressionGeneratorHelper`, `ExpressionHelper`, `ExpressionInfo`, `RuleMethodsHelper`, `RuleInfo`, `OperatorInfo`, `OperatorType`, `NodeCalls`, `Names`, `FilePrinter`, `OutputOpener`, `GenOptions`, `RuleGraphHelper`, `ParserGeneratorUtil`, `analysis/BnfFirstNextAnalyzer`, `java/JavaNameShortener`, `kotlin/{KotlinNameShortener, KotlinPlatformConstants}`, plus `Main.java` (headless CLI). Depends on `:bnf-language`. |
| Root project      | The IDE plugin itself: `actions/`, `editor/`, `inspection/`, `intention/`, `refactor/`, `search/`, `livePreview/`, `diagram/`, top-level UI providers, `plugin.xml`, signing, publishing.                                                                                                          |

The IntelliJ Platform Gradle plugin (v2) auto-detects subproject dependencies and bundles each module's jar into the plugin distribution.

## How `:generator` was decoupled from PSI

Before the split, several PSI/analysis classes reached into the `org.intellij.grammar.generator` package for what was actually BNF semantics, not code emission:

| Caller (`:bnf-language`) | What it called into `:generator` (before)               | After                                              |
|--------------------------|---------------------------------------------------------|----------------------------------------------------|
| `BnfReferenceImpl`, `BnfStringImpl`, `BnfFirstNextAnalyzer`, `GrammarUtil`, `LivePreviewParser`, etc. | `ParserGeneratorUtil.Rule.{of,isPrivate,isExternal,isMeta,isLeft,isInner,isFake,isUpper,firstNotTrivial}` | `org.intellij.grammar.psi.BnfRules`               |
| Same plus `JavaHelper`, `BnfFileImpl`, `GenOptions` | `ParserGeneratorUtil.{getAttribute,getRootAttribute,findAttribute,getAttributeValue,getLiteralValue}` | `org.intellij.grammar.psi.BnfAttributes`          |
| Same                     | `ParserGeneratorUtil.{isTrivialNode,getNonTrivialNode,getTrivialNodeChild,getEffectiveType,compilePattern,textStrategy}` | `org.intellij.grammar.psi.BnfAst`                 |
| `JavaHelper`, `BnfReferenceImpl` | `GenOptions.UseSyntaxApi(rule)` / `UseSyntaxApi(file)` | `BnfAttributes.useSyntaxApi(...)`                 |

After three preparatory commits (`BnfRules`, `BnfAttributes`, `BnfAst`) plus the `useSyntaxApi` move, the PSI/analysis layer no longer depends on emission code. Then the actual `:generator` extraction moved only the truly emission-only classes; the `org.intellij.grammar.generator.*` package now spans both modules (split package), with each individual class owned by exactly one module.

## Bootstrap

The project has a two-tier bootstrap:

**Tier 1 — Meta-layer** (`bnf-language/grammars/Grammar.bnf` → `bnf-language/gen/org/intellij/grammar/parser/GrammarParser.java` and `bnf-language/gen/.../psi/Bnf*`). These let the plugin itself read `.bnf` files at runtime.

**Tier 2 — Generator** (`generator/src/org/intellij/grammar/generator/`). Consumes the user's BNF and emits a parser + PSI for *their* language. `Generator.java` is the sealed base; `JavaParserGenerator.java` and `KotlinParserGenerator.java` are the targets. `ExpressionGeneratorHelper.generateExpressionRoot()` handles operator-precedence climbing, driven by `ExpressionInfo`/`OperatorInfo`. The user-facing trigger is `actions/GenerateAction.java` (root plugin module).

## Subsystems

In `:bnf-language`:

- `parser/` — BNF lexer (`GeneratedParserUtilBase` itself lives in `:parser-runtime`, shipped as a template into generated parsers).
- `psi/` — PSI interfaces/impl plus the BNF-semantic utilities `BnfRules` (rule modifiers, hierarchy, class names), `BnfAttributes` (attribute readers), `BnfAst` (PSI traversal, token maps, `isTokenSequence`, `getChildExpressions`), and `PinMatcher`.
- `java/` — `JavaHelper`: ASM-based Java-class introspection for target type resolution. Owns `findRuleImplMethods`.
- `generator/` (split package, partial) — `Renderer` family (with `forPsiClass`/`forPsiImplClass` factories on `NameFormat`), `Case`, `NameFormat`, `NameShortener`, `*BnfConstants`, plus `generator/java/{JavaBnfConstants, JavaNames, JavaRenderer}` and `generator/kotlin/{KotlinBnfConstants, KotlinRenderer}`. These are PSI-shared naming primitives that happen to live in the generator package historically.

In `:generator`:

- `generator/` — Code emission (`Generator`, `Java/KotlinParserGenerator`, `ExpressionGeneratorHelper`, `RuleMethodsHelper`, etc.), the rule-graph builder `RuleGraphHelper` and emission utilities `ParserGeneratorUtil`, output sinks (`FilePrinter`, `OutputOpener`), generator config (`GenOptions`, `Names`), plus `generator/kotlin/{KotlinNameShortener, KotlinPlatformConstants}`.
- `analysis/` — `BnfFirstNextAnalyzer` computes FIRST/FOLLOW sets used by precedence handling, recovery, and inspections.
- `Main.java` — Headless CLI entry point.

In root project (IDE plugin):

- `livePreview/` — Real-time in-IDE parser testing. `LivePreviewLanguage` registers a synthetic language per BNF; `LivePreviewParser` runs an interpreter-style parser; `LivePreviewHelper` reparses preview editors via a `MergingUpdateQueue`.
- `editor/` — Highlighting, annotators, gutter line markers.
- `inspection/` + `intention/` + `refactor/` + `search/` — Standard IDE plugin surface.
- `actions/` — Generate Parser, Run JFlex, Live Preview triggers.
- `diagram/` — Rule diagram via the IntelliJ diagram plugin.

## Build

Per-module `build.gradle.kts`:

- Library modules apply `org.jetbrains.intellij.platform.module` and pull only the bundled platform plugins they need (Java, IntelliLang).
- Root project applies the full `org.jetbrains.intellij.platform` plugin, owns `plugin.xml`, signing/publishing config, and the `buildPlugin` task graph. Java 17, Gradle 9.3.1, IDEA 2023.3.8.

`buildPlugin` produces a zip with one jar per module under `lib/` (six jars total: `base`, `parser-runtime`, `bnf-language`, `jflex-language`, `generator`, root).

## Tests

`BnfTestSuite` aggregates fast (parsing, generation, utils) and slow (inspections, refactoring) suites. Tests extend `BnfGeneratorTestCase` / `AbstractParsingTestCase`; inputs live under `testData/<feature>/` and outputs are compared to golden files. Tests live in the root project and exercise classes across all modules via the project dependency graph.

## Key takeaway

The interesting design choice is the **bootstrap split**: `:bnf-language/gen/` is committed so the plugin can build itself without a chicken-and-egg problem, and `GeneratedParserUtilBase` is both the runtime for `gen/`'s parser and the template stamped into every parser the plugin produces for users.

## Known follow-ups

- **Aggregate library jar**: `buildGrammarKitJar` currently bundles only root-project classes. To publish a complete `grammar-kit` library to Maven Central post-split, it should also include classes from `:base`, `:parser-runtime`, `:bnf-language`, `:jflex-language`, and `:generator` (e.g., via `zipTree(project(...).tasks.jar.archiveFile)`).
- **Rename root → `:plugin`**: the root project still serves as the IDE-features module. Splitting it into a dedicated `:plugin` subproject (with a thin coordinator at root) would make the layout fully symmetric, but requires migrating signing/publishing/changelog config.
- **Tighten the `:bnf-language`/`:generator` split**: today some PSI-shared naming primitives (`Renderer`, `JavaRenderer`, `KotlinRenderer`) physically live in `:bnf-language` under the `org.intellij.grammar.generator` package — a deliberate split package. If desired, these could be renamed to `org.intellij.grammar.naming` (or similar) to make module ownership match package boundaries.
