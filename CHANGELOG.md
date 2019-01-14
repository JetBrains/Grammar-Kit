2018.3

* Update artifacts to be based on IntelliJ IDEA 2018.3.

2017.1.7

* GPUB: extract extend_marker_impl and improve error elements handling
* Generator: fix base class calculation for merged AST and PSI
* NPE: JFlexStateUsageSearcher.processQuery

2017.1.6

* Generator: generate Java 8 syntax: `generate=[java="8"]`
* Generator: generate TokenSets for token choices: `generate=[token-sets="yes"]`
* Generator: wrap anonymous classes with functions
* RGH: fix left rule with several usage detected as recursive

2017.1.5

* Editor: _Move Element Left/Right_ action in BNF
* Editor: convert `expr?` to `[expr]` and back intention (#183)
* Generator: FIRST check with the exact `consumeTokenType` (#185)
* Generator/PSI: parameter annotations
* GPUB: configurable token-advancer

2017.1.4

* Editor: inject Regexp in BNF and Java in JFlex
* Generator: less markers & less lines in generated code
* Generator: improve target dir detection, refresh files faster
* Generator: FIRST check external rules with non-external predicates
* GPUB: cleanup and error reporting tweaks
* GPUB: re-check recoverWhile condition after advance when recovering

2017.1.3

* Generator: fix nested metas generation once again
* Editor: fix rule attrs being shown in File Structure (improves performance too)
* Editor: support custom folding regions (region/endregion comments)
* Editor: tweak pairing paren insertion
* Editor: fix incorrectly highlighted pin in expression-parsing rules
* Refactorings: move common sub-menu, reuse common shortcuts

2017.1.2

* Editor: fix some recoverWhile highlighting
* Generator: drop confusing LexerAdapter generation
* Generator: allow predicate rules be public, left and recoverable
* Generator: make nested meta rules transparent for pinning
* JFlex: explicitly reuse *.flex encoding on generation

2017.1.1

* Generator: per-rule psiPackage and psiImplPackage attributes
* Inspections: comment-based inspection suppression, e.g. `//noinspection BnfUnusedRule`
* Editor: improved java references highlighting and "Create Class" quick fixes
* JFlex: new download link and VM options to override defaults: `-Dgrammar.kit.jflex.jar=...`, `-Dgrammar.kit.jflex.skeleton=...`

2017.1

* Plugin: switch to IntelliJ IDEA versioning scheme
* BNF and JFlex PSI: merge AST and PSI trees (memory/performance)
* Generator: support dash-separated and angle-quoted rules
* Generator: "psi-factory" support for merged AST and PSI trees
* Generator: honor explicitly set rule name
* Generator: fix freeze on generation several grammars at once

1.5.2

* Generator: disable first-check in presence of external predicates
* Generator: support merged AST and PSI trees (psi.impl.source.tree.CompositePsiElement inheritors)
* Generator: add `generate=[psi-classes-map="yes"]` option
* Generator: alert of missing psi implementation methods #155
* Generator: merge multiple list-valued attributes, e.g. "implements", "methods"

1.5.1

* RGH: rule-graph analysis fix for trivial rules (#153)
* Diagrams: NPE fix on app save/load
* Generator: support rule names that happen to be java reserved words (#32)

1.5.0

* Generator: grammar analysis and expression parsing fixes
* Generator: include quoted tokens in token sequences 

1.4.3

* Generator: constructor and visitor fixes (issues 133, 135, 136)
* Copyright plugin integration
* IntelliJ compatibility: 2016.3, 2017.X

1.4.2

* JFlex: more color options & better AST
* Generator: meta rule parameter as 'recoverWhile' predicate
* Generator: support 'extends' with 'upper' and 'external' modifiers
* Generator/PSI: fix for fake rules PSI accessors calculation
* Generator/PSi: improved methods/classes names generation logic

1.4.0

* Generator: introduce "hooks" attribute and white-space binders #91
* Generator: override 'gen' folder name via 'grammar.kit.gen.dir' system property
* Generator: generate elementTypes for fake rules when needed #76
* Generator: improved/unified identifiers generation #97
* Generator/PSI: generate constructors from super-class #105
* JFlex: 1.6 syntax and running
* JFlex: file structure popup (Ctrl-F12)
* JFlex: additional arguments via 'grammar.kit.gen.jflex.args' system property
* JFlex: resolve returned token constants & yybegin(state) in action code
* JFlex: unify default generation path with BNF generator #96
* Editor: improved completion for angles-quoted rules

1.3.0

* LivePreview: critical bugs fixed, revitalized
* Generator: "upper" rules initial (no proper analyzer support)
* Generator/GPUB: introduce frame.resultType for manual manipulation
* Generator/Visitor: "visitor-value" generate option to generate `Visitor<R>`
* Generator: more "generate" options: elements, element-case, token-case

1.2.1

* Generator/PSI: fix generic and vararg types generation
* Generator/PSI: fix private recursive rules handling
* Generator: made a parser extend com.intellij.lang.LightPsiParser
* Known exceptions fixed, rebuilt with IntelliJ Platform 14.1.4
* Editor: method reference navigation revived

1.2.0

* Generator: mixin-class methods can be used as method-mixin's to populate rule PSI interface
* Generator: output directories detection for JFlex/BNF generators
* Generator: "generate" attribute supersedes global "generateXXX" attributes
* Generator: compact local variables names by default to improve code readability
* Generator: better PSI generation in complex "extends" / "elementType" cases
* Generator: improved rule content calculation leading to more correct PSI classes
* Generator/Pratt: no more explicit PsiBuilder.Marker manipulations in generated code
* LivePreview: pre-configured tokens are dropped, numbers/strings/whitespaces detected
* BNF: allow "-" in rule names and other identifiers

1.1.10

* GPUB: improve error reporting in some cases
* Generator/Editor: performance fix
* JFlex: treat "%eof{ return;" case as forced comment instead of error

1.1.8 / 1.1.9

* Refactoring: expression chooser for introduce rule
* JFlex: local jar/skeleton detection
* Generator: parser-based keyword completion improvements
* Generator: expression parsing improvements

1.1.6 / 1.1.7

* PSI generator: Improve *methods* attribute semantics: rename/add new PSI tree accessors
* PSI generator: Drop obsolete *methodRenames* attribute and add *generateTokenAccessors*
* Live Preview: Auto-generate tokens from usage (no more BAD_TOKEN)
* Editor: Examples added to attributes documentation (Ctrl-Q)
* Editor: Turn grammar spell-check

1.1.5

* Historical typo fixed: _recoverUntil_ attribute renamed to _recoverWhile_ (indeed it always meant _while_)
* Editor: recoverable rule highlighting, help improved for _pin_ and _recoverWhile_
* GPUB: cleanup and OOM protection
* Generator: parser size reduced, fragment parsing fixed, empty tokens support (lexer-based preprocessors)
* Generator: simple #auto recovery predicate generation, i.e. _recoverWhile_ = "#auto"

1.1.4

* JFlex language support: highlighting/navigation/completion/rename
* Generator: parser size reduced (most of PsiBuilder-specific logic moved to GPUB)

1.1.3

* Extensibility: "extends" hooks in GPUB
* Generator: compact token sequence parsing

1.1.2

* Context menu: Generate JFlex \*.flex file
* Context menu: Run JFlex generator
* Context menu: Create custom parser util class

1.1.1

* IDEA Platform 12.1 compatibility

1.1.0

* [Live Preview](TUTORIAL.md) mode for fast language prototyping
* Live Preview: _Highlight Grammar_ action helps visually debug grammars
* _stubParserClass_ attribute renamed to _parserUtilClass_ for consistency
* StubIndex support via _stubClass_ attribute or direct _implements/mixin_ approach

1.0.9

* [Pratt-like](http://javascript.crockford.com/tdop/tdop.html) expression parsing stabilized, see [grammar sample](testData/generator/ExprParser.bnf) for more
* No compile errors in generated code on cold start
* Drop unneeded inheritance checks during parse-time

1.0.8

* Experimental [Pratt-like](http://javascript.crockford.com/tdop/tdop.html) expression parsing ([compact grammars and improved performance](testData/generator/ExprParser.bnf))
* Per-rule elementTypeClass/Factory attribute & empty elementType attribute support
* CamelCase rule names support and PSI classes calculation fixes
* Grammar code folding for attribute groups and multiline comments

1.0.7

* Attribute pattern navigation & inspection
* Short error messages generation instead of long token lists
* Token sequence completion
* Standalone invocation (for ant integration & etc.)

1.0.6

* Grammar diagram (UML support plugin required)
* Dedicated "tokens" attribute
* Minor fixes

1.0.5

* PSI visitor generation
* Improved PSI generation
  * better children discovery (esp. for left rules)
  * user methods and method mixins
  * *fake* rules to generate better hierarchy

1.0.4

* Bug fixes and performance

1.0.3

* Improved PSI generation (*extends* and *elementType* attributes handling)
* Configurable FIRST-based parser optimization

1.0.2

* Error recovery: extended *pinning*
* Pin marker annotator
* Configurable colors (Settings/Editor/Colors and Fonts/Grammar)
* Parser-based context-aware keyword completion (sample)
* Language injection in string literals (sample)
* Performance improvement & other fixes

1.0.1

* Inspection: Left-recursion detection

1.0

* Highlighting
* Completion
* Navigation
* Refactorings: inline rule, introduce rule
* Intentions: flip choice branches
* Readable PsiBuilder-based recursive descent parser and PSI hierarchy generation
