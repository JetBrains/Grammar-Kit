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
Attributes like *pin* and *recoverUntil*, rule modifiers add some lines to this general structure.


2.2 Making *recoverUntil* actually work
---------------------------------------

*recoverUntil* rule attribute tells parser after matching the rule and regardless of whether this was successful or not
to consume all tokens from the input sequence while the rule specified in *recoverUntil* value matches input.


1. This attribute in most cases should be specified on a rule that is inside a loop.
2. That rule should always have *pin* attribute somewhere as well.
3. Predicate rule should look like *!( token_to_stop_at | rule_to_stop_at | ....) *

````
script ::= statement *
private statement ::= select_statement | delete_statement | ... {recoverUntil="statement_recover"}
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
* PSI interface should extend _StubBasesPsiElementBase&lt;StubClass&gt;_
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
