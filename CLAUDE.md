# Grammar-Kit — agent notes

Grammar-Kit is the BNF + parser generator for the IntelliJ Platform. The repo is split into several Gradle modules; the most agent-relevant pieces are:

- `bnf-language/` — IDE language support for `.bnf` / `.flex`, plus the `JavaHelper` API surface (`PsiHelper`, `JvmSyntaxHelper`, `JavaHelperFactory`) used by the generator and inspections to look up referenced Java/Kotlin classes.
- `generator/` — the build-time parser / PSI / lexer generator.
- `jvm-class-info/` — the manager + provider pipeline that builds `ClassSymbol` snapshots from **Java source, Kotlin source, and JVM bytecode (ASM)** and serves them to `JvmSyntaxHelper`. See [`jvm-class-info/CLAUDE.md`](jvm-class-info/CLAUDE.md) before changing anything under this subtree — there are non-obvious invariants (canonical FQN form, source-vs-bytecode parity, one-shot manager lifetime) that aren't visible from the file you're editing.
- `tests/` — JUnit 3 tests dispatched through `org.intellij.grammar.BnfTestSuite`. The `--tests` Gradle filter must target the suite, not an individual test class.

Human-facing docs: `README.md`, `HOWTO.md`, `TUTORIAL.md`, `CHANGELOG.md` at the repo root.
