
Grammar-Kit plugin
==================

Edit BNF grammars with _Live Preview_ and generate parser/PSI java code.

Download latest [GrammarKit.zip](binaries/GrammarKit.zip) and
[GeneratedParserUtilBase.java](support/org/intellij/grammar/parser/GeneratedParserUtilBase.java)
for manual installation.

General usage instructions
--------------------------
1. Create grammar *.bnf file, see [Grammar.bnf](grammars/Grammar.bnf) in the plugin code.
2. Tune the grammar using _Live Preview_ + Structure view (ctrl-alt-P / meta-alt-P)
3. Generate parser/ElementTypes/PSI classes (ctrl-shift-G / meta-shift-G)
4. Add lexer, parser definition & plugin.xml
5. Mix-in resolve and other non-trivial functionality to PSI

You've just build a custom language plugin (approx. 1 day).

See [Tutorial](TUTORIAL.md) page for a sample to play with *Live Preview*.

See [HOWTO](HOWTO.md) page for different tips and tricks.

Other open-source plugins built with Grammar-Kit: [intellij-erlang](https://github.com/ignatov/intellij-erlang).

![Editor support](images/editor.png)

Recent changes
--------------
1.0.10

* [Live Preview](TUTORIAL.md) mode for fast language prototyping
* Live Preview: _Highlight Grammar_ action helps visually debug grammars
* _stubParserClass_ attribute renamed to _parserUtilClass_ for consistency
* StubIndex support via _stubClass_ attribute or direct _implements/mixin_ approach


Quick documentation:
====================
See [Parsing Expression Grammar](http://en.wikipedia.org/wiki/Parsing_expression_grammar) for basic syntax. Use ::= for ← symbol. You can also use [ .. ] for optional sequences and { | | } for choices as these variants are popular in real-world grammars. Grammar-Kit source code is the main example of Grammar-Kit application. The grammar for BNF parser and PSI generation can be found [here](grammars/Grammar.bnf).

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

### Error recovery and reporting:
* _pin_ attribute (value: number or pattern string) makes partially matched rule match
if the specified prefix is matched. This attribute tunes the parser to handle incomplete rules.
  * _extendedPin_ global attribute (_true_ by default) extends the notion of pinning. In this mode the
generated parser tries to match the rest part of an already pinned rule even if some parts are missed. This allows parser to match the closing brace if the opening brace is pinned for example.

* _recoverUntil_ attribute (value: predicate rule) matches any number of tokens after the rule
matching completes with any result. This attribute helps parser recover when unmatched token
sequence is encountered.

* _name_ attribute (value: string) specifies a nice name for a rule. For example *name("_.*expr")=expression* attribute creates a well recognized "&lt;expression&gt; required" error message for different expression rules instead of a long token list.

### Generated parser structure:
For each rule and every its sub-expression in a grammar a static method is generated.
Sub-expression methods are named *rule_name_K_L_..* where the *(K, L, .. )* number list describes the position of a sub-expression in an enclosing rule. Avoid naming your rules this way.

Generator can split parser code into several classes for better support of large grammars.

For simple cases parser will consists of several generated classes and a copy [GeneratedParserUtilBase](support/org/intellij/grammar/parser/GeneratedParserUtilBase.java) the first time generator is run. It contains error recovery and reporting code as well as completion functionality for parser-based completion provider and basic token matching code. Make sure you have the latest version as it contains the latest fixes, improvements and a fresh portion of bugs.

Each external rule must be implemented the same way as generated i.e. by static method.

*parserUtilClass* global attribute should specify a subclass of GeneratedParserUtilBase. All public static methods of this class will be available to generated parser classes via  _import static .*_ directive.

### Lexer and PSI:
Currently JFlex-based or any other lexer must be provided separately, you can use _BnfLexer.flex as a base. IElementType constants generated by parser generator have to be recognized and returned by the lexer. Token types and PSI generation can be switched off via *generateTokens* and *generatePSI* global boolean attributes respectively.
 
*elementType* rule attribute allows to mix the generated code and some existing hand-made PSI.   

### Arithmetic expressions with qualified references example

````
{
  // make class hierarchy nice and AST tree short
  extends(".*expression")="expression"
  // make expressions stable enough
  pin(".*expression")=1

  // tokens beyond id, number and string
  tokens = [
    ADD='+'
    MUL='*'
    DOT='.'
  ]
}
root ::= expression *
private recover ::= !(number | string | id)

expression ::= factor add_expression * {recoverUntil="recover"}
private factor ::= primary mul_expression *
private primary ::= literal_expression | reference_expression qreference_expression *

left add_expression ::= '+' factor
left mul_expression ::= '*' primary

identifier ::= id
reference_expression ::= identifier {mixin="mypackage.MyReferenceExpressionImpl"}
left qreference_expression ::= '.' identifier {elementType="reference_expression"}

literal_expression ::= number | string
````

Just add *mypackage.MyReferenceExpressionImpl* class with proper *getReference()* implementation.

Change log
==========
1.0.10

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

