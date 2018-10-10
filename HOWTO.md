----------------
I. General Notes
================

1. Writing a grammar doesn't mean the generated parser will work and produce nice AST.
The tricky part is to *tune* some raw grammar that *looks correct* into a *working* grammar i.e. the grammar that produces working parser.
But once you've mastered some basics the rest is as easy as combining different blocks into a working solution.

2. While editing grammar it is better to think that you manipulate generated code on a higher level of abstraction.

3. Handwritten classes and generated classes should be in different source roots.


--------------------------
II. HOWTO: Generated Parser
==========================

2.1 Parser Basics
-----------------

Each rule is either matched or not so every BNF expression is a boolean expression.
**True** means some part of an input sequence is matched, **false** *always* means nothing is matched even if some part of the input was matched.
Here are some of the grammar-to-code mappings:

Sequence:
````
// rule ::= part1 part2 part3
public boolean rule() {
  <header>
  boolean result = false;
  result = part1();
  result = result && part2();
  result = result && part3();
  if (!result) <rollback any state changes>
  <footer>
  return result;
}
````

Ordered choice:
````
// rule ::= part1 | part2 | part3
public boolean rule() {
  <header>
  boolean result = false;
  result = part1();
  if (!result) result = part2();
  if (!result) result = part3();
  if (!result) <rollback any state changes>
  <footer>
  return result;
}
````

Zero-or-more construct:
````
// rule ::= part *
public boolean rule() {
  <header>
  while (true) {
    if (!part()) break;
  }
  <footer>
  return true;
}
````

*One-or-more*, *optional*, *and-predicate* and *not-predicate* constructs are implemented accordingly.
As you can see the generated code can be easily debugged as any handwritten code.
Attributes like *pin* and *recoverWhile*, rule modifiers add some lines to this general structure.


2.2 Using *recoverWhile* attribute
---------------------------------------

1. This attribute in most cases should be specified on a rule that is inside a loop
2. That rule should always have *pin* attribute somewhere as well
3. Attribute value should be a predicate rule, i.e. leave input intact


The contract is defined as follows:

1. The attributed rule is handled as usual
2. And regardless of the result parser will continue to consume tokens while the predicate rule matches


````
script ::= statement *
private statement ::= select_statement | delete_statement | ... {recoverWhile="statement_recover"}
private statement_recover ::= !(';' | SELECT | DELETE | ...)
select_statement ::= SELECT ... {pin=1}  // something has to be pinned!
                                         // pin="SELECT" is a valid alternative
````

2.3 When nothing helps: *external* rules
----------------------------------------

1. Sometimes it's easier to do something right in code.
2. Sometimes there's no way around some external dependency.
3. Sometimes we already have some logic implemented elsewhere.

````
{
  parserUtilClass="com.sample.SampleParserUtil"
}
external my_external_rule ::= parseMyExternalRule false 10  // we can pass some extra parameters
                                                            // .. even other rules!
rule ::= part1 my_external_rule part3
// rule ::= part1 <<parseMyExternalRule true 5>> part3      // is a valid alternative
````

````
public class SampleParserUtil {
  public static boolean parseMyExternalRule(PsiBuilder builder, int level,       // required arguments
                                            boolean extraArg1, int extraArg2) {  // extra arguments
    // do the work, ask the audience, phone a friend
  }
}
````

2.4 Compact expression parsing with priorities
----------------------------------------------

Recursive descent parsers are inefficient in terms of stack depth when it comes to expressions.
A more natural and compact way of dealing with this is supported.

1. All "expression" rules should extend "the root expression rule".
   When done correctly this will ensure that AST will be of optimal depth and consistent even in case of errors.
   Due to *extends* attribute semantics redundant nodes will be collapsed
   and the root expression rule node will never appear in AST, use *Quick Documentation* (Ctrl-Q/Ctrl-J) to verify.
2. Priority increases from top to bottom, ordered choice semantics is preserved
3. Use left recursion for binary and postfix expressions
4. Use *private* rules to define a group of operators with the same priority
5. Use *rightAssociative* attribute when the default left associativity is not appropriate

The following snippet demonstrates that "expression" parts of the BNF look compact and
the described syntax doesn't break much the ordinary BNF syntax ([complete example](testData/generator/ExprParser.bnf)):

````
// to keep this sample short function calls and other expressions are omitted
{
  extends(".*expr")=expr
  tokens=[number="regexp:[0-9]+" id="regexp:[a-z][a-z0-9]*"]
}
// the root expression rule
expr ::= assign_expr
  | add_group
  | mul_group
  | unary_group
  | exp_expr
  | qualification_expr
  | primary_group

// private rules to define operators with the same priority
private unary_group ::= unary_plus_expr | unary_min_expr
private mul_group ::= mul_expr | div_expr
private add_group ::= plus_expr | minus_expr
private primary_group ::= simple_ref_expr | literal_expr | paren_expr

// public rules for each expression
assign_expr ::= expr '=' expr { rightAssociative=true }
unary_min_expr ::= '-' expr
unary_plus_expr ::= '+' expr
div_expr ::= expr '/' expr
mul_expr ::= expr '*' expr
minus_expr ::= expr '-' expr
plus_expr ::= expr '+' expr
exp_expr ::= expr ('^' expr) + // N-ary variant, the "(<op> expr ) +" syntax is mandatory.
paren_expr ::= '(' expr ')'

// introduce fake rule with @Nullable qualifier getter and
// let qualified and simple references have its elementType
fake ref_expr ::= expr? '.' identifier
simple_ref_expr ::= identifier {extends=ref_expr elementType=ref_expr}
qualification_expr ::= expr '.' identifier {extends=ref_expr elementType=ref_expr}

literal_expr ::= number
identifier ::= id
````

Notes:

1. *operator* part can contain any valid BNF expressions and define "tails", i.e.

   ````div_expr ::= expr [div_modifier | '*'] '/' expr div_expr_tail````

2. specific expression rule can be used instead of *expr* to narrow the parsing

3. there can be any number of "expression roots" in a grammar as long as they do not intersect


All operators will be present in error messages. To avoid this and also increase performance add this:
````
{
   consumeTokenMethod(".*_expr|expr")="consumeTokenFast"
}
````

The generated parser for this grammar (which is a procedural rewrite of the Pratt parsing described
[here](http://javascript.crockford.com/tdop/tdop.html)) doesn't include methods for all expressions.
There is only 2 methods for the root rule. The comment includes the operator priority table:

````
  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY(assign_expr)
  // 1: BINARY(plus_expr) BINARY(minus_expr)
  // 2: BINARY(mul_expr) BINARY(div_expr)
  // 3: PREFIX(unary_plus_expr) PREFIX(unary_min_expr)
  // 4: N_ARY(exp_expr)
  // 5: POSTFIX(ref_expr)
  // 6: ATOM(simple_ref_expr) ATOM(literal_expr) PREFIX(paren_expr)
  public static boolean expr(PsiBuilder builder_, int level_, int priority_) {
     // code to parse ATOM and PREFIX operators
     // .. and ..
     // call expr_0()
  }

  public static boolean expr_0(PsiBuilder builder_, int level_, int priority_) {
     // here goes priority-driven while loop for BINARY, N-ARY and POSTFIX operators
  }
````

----------------------------------------
III. HOWTO: Generated PSI Classes Hierarchy
========================================

3.1 PSI Basics
--------------

1. Specify *private* attribute on any rule if you don't want it to be present in AST as early as possible. The first rule is implicitly *private*.
2. Make use of *extends* attribute to achieve two goals at once: make PSI hierarchy look nice and make AST shallow.

````
{
  extends(".*_expr")=expr               // make AST for literal one level deep: FileNode/LiteralExpr
                                        //   otherwise it will look like: FileNode/Expr/LiteralExpr
  tokens = [
    PLUS='+'
    MINUS='-'
  ]
  ...
}
expr ::= factor add_expr *
private factor ::= primary mul_expr *   // we don't need this node in AST
private primary ::= literal_expr        // .. and this as well
left add_expr ::= ('+'|'-') factor      // if classic recursive descent is used without "left" rules
left mul_expr ::= ('*'|'/') primary     //    then the AST without "extends" will look even worse:
literal_expr ::= ...                    //    FileNode/Expr/AddExpr/MulExpr/LiteralExpr
````

3.2 Organize PSI using *fake* rules and user methods
----------------------------------------------------
````
{
  extends("(add|mul)_expr")=binary_expr // this attributes can be placed directly after rule
  extends(".*_expr")=expr               // .. but I prefer grammars less cluttered with alien gibberish
}

// won't be taken into account by parser
fake binary_expr ::= expr + {
  methods=[
    left="/expr[0]"                     // will be @NotNull as far as we have "+" in the expression
    right="/expr[1]"                    // "expr" is the name of the auto-calculated child property (singular or list)
  ]
}

expr ::= factor add_expr *
... and the rest of "PSI Basics" example
````

Will produce among other code:
````
public interface BinaryExpr {
  List<Expr> getExprList();
  @NotNull
  Expr getLeft();
  @Nullable
  Expr getRight();
}

public interface AddExpr extends BinaryExpr { ... }

public interface MulExpr extends BinaryExpr { ... }

// and PsiElementVisitor implementation
public class Visitor extends PsiElementVisitor {
  ...
  public visitAddExpr(AddExpr o) {
    visitBinaryExpr(o);
  }
  public visitMulExpr(MulExpr o) {
    visitBinaryExpr(o);
  }
  public visitBinaryExpr(BinaryExpr o) {
    visitExpr(o);
  }
  ...
}
````


3.3 Implement interface via implementation *mixin*
--------------------------------------------------
````
{
  mixin("my_named")="com.sample.psi.impl.MyNamedImplMixin"
}
my_named ::= part1 part2 part3
// my_named ::= part1 part2 part3 {mixin="com.sample.psi.impl.MyNamedImplMixin"} // is a valid alternative

````

````
public class MyNamedImplMixin extends MyNamed implements PsiNamedElement {
  // methods from PsiNamedElement interface
  @Nullable @NonNls String getName() { ... }
  PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException { ... }
}
````

3.4 Implement interface via method injection
--------------------------------------------
````
{
  psiImplUtilClass="com.sample.SamplePsiImplUtil"
  implements("my_named")="com.intellij.psi.PsiNamedElement"
}
my_named ::= part1 part2 part3 {
  methods=[getName setName]             // no need to specify arguments or return type
}
````

````
public class SamplePsiImplUtil {
  // methods from PsiNamedElement interface with an extra MyName parameter
  @Nullable @NonNls String getName(MyNamed o) { ... }
  PsiElement setName(MyNamed o, @NonNls @NotNull String name) throws IncorrectOperationException { ... }
}
````


3.5 Stub indices support
------------------------

Stub indices API forces a bit different contract on PSI classes:
* There should be a manually written so-called *stub* class
* PSI node type should extend _IStubElementType_ (comparing to usual _IElementType_)
* PSI interface should extend _StubBasedPsiElementBase&lt;StubClass&gt;_
* PSI implementation class should extend _StubBasedPsiElementBase&lt;StubClass&gt;_

The first two points are not covered by the generator.
_IStubElementType_ as well as a _stub_ class itself should be carefully implemented by hand.
_IStubElementType_ value can be provided to the generated parser via _elementTypeFactory_ attribute.

Note that the last point can break PSI inheritance implied by _extends_ attribute.

The rest can be solved in two ways. Direct _implements/mixin_ approach:
````
property ::= id '=' expr
  {
    implements=["com.sample.SampleElement" "com.intellij.psi.StubBasedPsiElement<com.sample.PropertyStub>"]
    mixin="com.sample.SampleStubElement<com.sample.PropertyStub>"

    // minimum requirements are:
    // implements=["com.intellij.psi.PsiElement" "com.intellij.psi.StubBasedPsiElement<com.sample.PropertyStub>"]
    // mixin="com.intellij.extapi.psi.StubBasedPsiElementBase<com.sample.PropertyStub>"
  }

````

_stubClass_-based approach:
````
property ::= id '=' expr
  {
    stubClass="com.sample.PropertyStub"
  }

````

IV. Tips and Tricks
===================

4.1. Trailing commas
--------------------
Imagine a language which allows trailing commas in lists, e.g.
```
( item1, item2, item3, )
```

In that situation you may use such parser rule:
```
element_list ::= '(' element (',' (element | &')'))* ')' {pin(".*")=1}
```

... to be continued
