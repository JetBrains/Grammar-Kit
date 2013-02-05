
Live Preview introduction:
====================
(The latest 1.0.10 version of Grammar-Kit plugin is required)

Suppose we want to create a grammar for some expression language like this:
````
expr=1 * 2 + (3 - 8.3!);
text='This is a ' + 'text';
// line comment

test_pin_results=;                        // expression expected
some garbage to test error recovering



recovered =1/2                            // missing semicolon
recovered_again=1/2;
````

To do this lets make a new file _sample.bnf_.
We can invoke *Live Preview* action via context menu or the ctrl-alt-P shortcut and paste the sample text above right on start.

_Structure_ toolwindow, _File Structure_ popup (ctrl-F12) and _PSI Viewer_ dialog can be used to observe the PSI tree as we modify the grammar.
In the end my IDE looked like that:

![Live Preview](images/livePreview.png)


Here is the grammar I designed without the need to generate or run anything.
I still need to add a lexer and some extra attributes to generate a real parser like package and some class
names as described in the [main readme](README) but now I'm sure the BNF part is OK.
The fun part is that I even can _inject_ this language in some other files I work with to quickly test the syntax.


Try playing with _pin_ and _recover_until_ attributes, tokens and rule modifiers to see how this all works.
````
{
  tokens=[
    SEMI=';'
    EQ='='
    LP='('
    RP=')'

    comment='regexp://.*(\n|$)'
    number='regexp:\d+(\.\d*)?'
    id='regexp:\p{Alpha}\w*'
    string="regexp:('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")"

    op_1='+'
    op_2='-'
    op_3='*'
    op_4='/'
    op_5='!'
  ]

  name(".*expr")='expression'
  extends(".*expr")=expr
}

root ::= (property ';') * {pin(".*")=1}
private rule_recover ::= !(';' | id '=')
property ::= id '=' expr  {pin=2 recoverUntil=rule_recover}

expr ::= factor plus_expr *
left plus_expr ::= plus_op factor
private plus_op ::= '+'|'-'
private factor ::= primary mul_expr *
left mul_expr  ::= mul_op primary
private mul_op ::= '*'|'/'
private primary ::= primary_inner factorial_expr ?
left factorial_expr ::= '!'
private primary_inner ::= literal_expr | ref_expr | paren_expr
paren_expr ::= '(' expr ')' {pin=1}
ref_expr ::= id
literal_expr ::= number | string | float
````

