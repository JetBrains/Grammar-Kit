Grammar-Kit [[download]](../../releases)
==================

An [IntelliJ IDEA plugin](http://plugins.jetbrains.com/plugin/6606) for language plugin developers.

Adds BNF Grammars and JFlex files editing support including parser/PSI code generator.

Open-source plugins built with Grammar-Kit:
[intellij-erlang](https://github.com/ignatov/intellij-erlang),
[intellij-haskforce](https://github.com/carymrobbins/intellij-haskforce),
[Dart](https://github.com/JetBrains/intellij-plugins/tree/master/Dart),
[intellij-haxe](https://github.com/JetBrains/intellij-haxe),
[OGNL](https://github.com/JetBrains/intellij-plugins/tree/master/struts2).

Quick links: [Tutorial](TUTORIAL.md), [HOWTO](HOWTO.md), [Standalone usage](#standalone-usage).

Recent changes / 1.2.1

* Generator/PSI: fix generic and vararg types generation
* Generator/PSI: fix private recursive rules handling
* Generator: made a parser extend com.intellij.lang.LightPsiParser
* Known exceptions fixed, rebuilt with IntelliJ Platform 14.1.4
* Editor: method reference navigation revived


General usage instructions
--------------------------
1. Create grammar \*.bnf file, see [Grammar.bnf](grammars/Grammar.bnf) in the plugin code.
2. Tune the grammar using _Live Preview_ + Structure view (ctrl-alt-P / meta-alt-P)
3. Generate parser/ElementTypes/PSI classes (ctrl-shift-G / meta-shift-G)
4. Generate lexer \*.flex file and then run JFlex generator (both via context menu) 
5. Implement ParserDefinition and add the corresponding registrations to the plugin.xml
6. Mix-in resolve and other non-trivial functionality to PSI


Tooling support
===========================

![Editor support](images/editor.png)

* Refactoring: extract rule (Ctrl-Alt-R/Meta-Alt-R)
* Refactoring: introduce token (Ctrl-Alt-C/Meta-Alt-C)
* Editing: flip _choice_ branches intention (via Alt-Enter)
* Editing: Unwrap/remove expression (Ctrl-Shift-Del/Meta-Shift-Del)
* Navigation: quick grammar structure popup (Ctrl-F12/Meta-F12)
* Navigation: go to related file (parser and PSI) (Ctrl-Alt-Home/Meta-Alt-Home)
* Navigation: navigate to matched expressions (Ctrl-B/Meta-B inside attribute pattern)
* Highlighting: customizable colors (via Settings/Colors and Fonts)
* Highlighting: pinned expression markers (tooltip shows pin value in charge)
* Highlighting: a number of inspections, the list is available in Settings/Inspections
* Documentation: rule documentation popup shows FIRST/FOLLOWS/PSI content (Ctrl-Q/Meta-J)
* Documentation: attribute documentation popup (Ctrl-Q/Meta-J)
* [Live preview](TUTORIAL.md): open language live preview editor (Ctrl-Alt-P/Meta-Alt-P)
* [Live preview](TUTORIAL.md): start/stop grammar evaluator highlighting (Ctrl-Alt-F7/Meta-Alt-F7 in preview editor)
* Generator: generate parser/PSI code (Ctrl-Shift-G/Meta-Shift-G)
* Generator: generate custom _parserUtilClass_ class
* Generator: generate \*.flex - JFlex lexer definition
* Generator: run JFlex generator on a \*.flex file
* Diagram: PSI tree diagram (UML plugin required)


Quick documentation:
====================
See [Parsing Expression Grammar](http://en.wikipedia.org/wiki/Parsing_expression_grammar) for basic syntax.
Use ::= for ← symbol. You can also use [ .. ] for optional sequences and { | | } for choices as these variants are popular in real-world grammars.
Grammar-Kit source code is the main example of Grammar-Kit application.
The grammar for BNF parser and PSI generation can be found [here](grammars/Grammar.bnf).

Here's how it may look like:

````
root_rule ::= rule_A rule_B rule_C rule_D                // a sequence
rule_A ::= token | 'or_text' | "another_one"             // a choice
rule_B ::= [ optional_token ] and_another_one?           // optional parts
rule_C ::= &required !forbidden                          // predicates
rule_D ::= { can_use_braces + (and_parens) * }           // grouping and repetition

// Grammar-Kit extensions:

private left rule_with_modifier ::= '+'                  // rule modifiers
left rule_with_attributes ::= '?' {elementType=rule_D}   // left rule and attributes

private meta list_macro ::= <<p>> (',' <<p>>) *          // meta rule
private list_usage ::= <<list_macro rule_D>>             // external expression
````

Basic syntax is extended with global and attributes that control code generation.
Attributes are specified by the list of *name=value* pairs enclosed in { .. }.
Rule attributes are placed right after the rule definition.
Global attributes are placed on top or separated from a rule definition with a semicolon.
Rule name or generated method name pattern can specify expression an attribute applies to:

````
{
  pin(".*_list(?:_\d.*)?")=1
}
````

This way you can keep grammar clean.

### Rule modifiers:
1. *private*:  PSI node will not be generated for this rule. Rules are public by default.
2. *external*:  not generated. Used for generated and handwritten code integration.
3. *left*:  left-associativity support. PSI node for this rule will enclose the one to the left.
4. *inner*:  left-injection. PSI node for this rule will be injected into the one to the left.
5. *meta*:  meta grammar support. Meta rules work in conjunction with external expressions.
6. *fake*:  no parser code will be generated. For PSI hierarchy generation only.

Modifiers can be combined, *inner* should only be used together with *left*, *private left* is equivalent to *private left inner*.

### Meta rules & external expressions:
External expression *<< ... >>* is simply an inline variant of external rule. It can also be used to specify meta rule along with arguments.

For example:

````
meta comma_separated_list ::= <<param>> ( ',' <<param>> ) *
option_list ::= <<comma_separated_list (OPTION1 | OPTION2 | OPTION3)>>
````

External rule expression syntax is the same as a body of external expression:

````
 external manually_parsed_rule ::= methodName param1 param2 ...
````

Rule references in parameter list are implemented as [GeneratedParserUtilBase.Parser](support/org/intellij/grammar/parser/GeneratedParserUtilBase.java) instances.

### Tokens:
Tokens should appear in grammar file as is. All conflicts can be resolved by quotation.
If there is an attribute with the same value as single-quoted or double-quoted token
then the corresponding IElementType constant will be generated and matched against
otherwise the token will be matched by text.

Text-matched tokens can span more than one real token returned by lexer.

External expressions and external rules interpret double- and single-quoted strings differently.
Generally anything that appears in an external expression after rule or method name is treated
as parameter and passed "as is" except single-quoted strings. They are unquoted first.
This helps passing qualified enum constants, java expressions, etc.

### Attributes for error recovery and reporting:
* _pin_  tunes the parser to handle incomplete matches. A rule matches if the prefix of the rule up to the pinned token matches.
Pinning an opening brace allows the parser to match the closing brace even if the tokens between them does not match the rule.
Pin takes a position indicated by either a number *(pin=2)* or a pattern string *(pin="rule_B")*.

* _recoverWhile_ (value: predicate rule) matches any number of tokens after the rule
matching completes with any result. This attribute helps parser recover when unmatched token
sequence is encountered. See [HOWTO section](HOWTO.md#22-using-recoverwhile-attribute) for more.

* _name_ (value: string) specifies a name for a rule to be used in error reports. For example, *name("_.*expr")=expression* changes
expression error messages to "&lt;expression&gt; required" instead of a long list of tokens.

### Generated parser structure:
For each rule and every its sub-expression in a grammar a static method is generated.
Sub-expression methods are named *rule_name_K_L_..* where the *(K, L, .. )* number list describes the position of a sub-expression in an enclosing rule. Avoid naming your rules this way.

Generator can split parser code into several classes for better support of large grammars.

For simple cases parser will consists just of several generated classes.

The actual error recovery and reporting code as well as completion functionality for parser-based completion provider and basic token matching code resides
in a _parserUtilClass_ class. It may be altered by specifying some other class that extend or mimic the original [GeneratedParserUtilBase](support/org/intellij/grammar/parser/GeneratedParserUtilBase.java).
There's no need to keep a copy of GeneratedParserUtilBase in a project, it is included in IntelliJ Platform since version 12.1.

The manual parsing code, i.e. _external_ rules must be implemented the same way as generated, by a static method in the _parserUtilClass_ class or any other class that will
be imported via _parserImports_ attribute like this:
````
{
  parserImports=["static org.sample.ManualParsing.*"]
}
````

### Lexer and PSI:
IElementType constants generated by parser generator have to be recognized and returned by the lexer.
JFlex-based lexer can be generated from grammar that defines all the required tokens ( *Generate JFlex Lexer* menu).

*Run JFlex Generator* menu in a \*.flex file calls JFlex to generate lexer java code.
Keywords are picked right from usages while tokens like *string*, *identifier* and *comment* can be defined like this (from [TUTORIAL](TUTORIAL.md)):

````
{
  tokens=[
    ...
    comment='regexp://.*'
    number='regexp:\d+(\.\d*)?'
    id='regexp:\p{Alpha}\w*'
    string="regexp:('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")"
    ...
  ]
  ...
}
````

While *Live Preview* mode supports full Java RegExp syntax and JFlex supports only a subset (see [JFlex documentation](http://jflex.de/manual.html#SECTION00053000000000000000))
Grammar-Kit tries to perform some obvious conversions.

Lexer can be provided separately or one can use the generated \*.flex file as a base.

Parser generator generates token types constants and PSI by default.
This can be switched off via *generateTokens* and *generatePSI* global boolean attributes respectively.
 
*elementType* rule attribute allows to mix the generated code and some existing hand-made PSI.   


Standalone usage
================

Put the following files next to grammar-kit.jar:
* jdom.jar, trove4j.jar, extensions.jar, picocontainer.jar, junit.jar, idea.jar, openapi.jar, util.jar, all in a "lib" folder
* or [light-psi-all.jar](../../releases)

Note: light-psi-all.jar contains just the required subset of IntelliJ IDEA Platform classes. It can be rebuilt for one`s need at any moment via "LightPsi-All: Package" run configurations in this project.

To generate parser/PSI use the following command (light-psi-all.jar will be picked automatically from the current folder):
````
java -jar grammar-kit.jar <output-dir> <grammar1> ...
````

The following command demonstrates the sample [expression parser](testData/generator/ExprParser.bnf) in action:
````
java -jar expression-console-sample.jar
````


Change log
==========

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

