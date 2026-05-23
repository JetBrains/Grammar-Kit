# Grammar-Kit — agent notes

Grammar-Kit is the BNF + parser generator for the IntelliJ Platform. The repo is split into several Gradle modules; the most agent-relevant pieces are:

- `bnf-language/` — IDE language support for `.bnf` / `.flex`, plus the `JavaHelper` API surface (`PsiHelper`, `JvmSyntaxHelper`, `JavaHelperFactory`) used by the generator and inspections to look up referenced Java/Kotlin classes.
- `generator/` — the build-time parser / PSI / lexer generator.
- `jvm-class-info/` — the manager + provider pipeline that builds `ClassSymbol` snapshots from **Java source, Kotlin source, and JVM bytecode (ASM)** and serves them to `JvmSyntaxHelper`. See [`jvm-class-info/CLAUDE.md`](jvm-class-info/CLAUDE.md) before changing anything under this subtree — there are non-obvious invariants (canonical FQN form, source-vs-bytecode parity, one-shot manager lifetime) that aren't visible from the file you're editing.
- `tests/` — JUnit 3 tests dispatched through `org.intellij.grammar.BnfTestSuite`. The `--tests` Gradle filter must target the suite, not an individual test class.

Human-facing docs: `README.md`, `HOWTO.md`, `TUTORIAL.md`, `CHANGELOG.md` at the repo root.

## Testing
- Always run the full test suite (`./gradlew test` or module-specific) after code changes and report the exact pass count.
- Never claim tests pass without actually running them — verify before announcing success.
- When modifying golden-file tests, ensure the OVERWRITE_TESTDATA system property is forwarded to the test JVM, or patch expected files directly.

## Planning
- For any refactor touching multiple files or modules, present a plan via ExitPlanMode before editing.
- Do NOT begin implementation without explicit plan approval; if scope is ambiguous, ask one clarifying question first.
Before making any edits, enter plan mode and present a phased plan covering: (1) files to touch, (2) test impact and golden file regeneration, (3) risks/regressions to watch. Wait for my approval. Do not edit until I approve.
- 
## Java/Kotlin Conventions
- This project targets Java 17 version WITHOUT switch pattern matching enabled — use traditional switch or if/else chains.
- Annotations like @Nls, @NotNull, @Nullable can target METHOD, FIELD, TYPE, PARAMETER — always check annotation @Target before deciding where to lift them.
- Prefer simple class names with imports over fully qualified names in code.
