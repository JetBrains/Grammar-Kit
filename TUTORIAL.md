
Live Preview introduction:
====================

Create a grammar file like this:
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

This grammar defines a language for files like:
````
expr=1 * 2 + (3 - 8.3!);
text='This is a ' + 'text';
// line comment

test_pin_results=;                        // expression expected
some garbage to test error recovering



recovered =1/2                            // missing semicolon
recovered_again=1/2;
````

So invoke *Live Preview* action via context menu or the ctrl-alt-P shortcut and paste the sample text above.

_Structure_ toolwindow, _File Structure_ popup (ctrl-F12) and _PSI Viewer_ dialog can be used to observe PSI tree.

![Live Preview](images/livePreview.png)
