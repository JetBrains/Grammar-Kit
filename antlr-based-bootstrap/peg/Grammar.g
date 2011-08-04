grammar Grammar;

options {
  language = Java;
  output = AST;
  ASTLabelType='org.antlr.runtime.tree.CommonTree';
}

tokens {
  RULE;
  ATTR;
  SEQ;
  CHOICE;
  ATTRS; ATTR;
  MODIFIER;
}

@parser::header {
package peg;
}

@lexer::header {
package peg;
}

@members {
//    List<CommonTree> myRules = new ArrayList<CommonTree>();
}

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;
    


STRING
	:	'\'' (ESC|~('\''|'\\'))* '\'' 
	|	'"' (ESC | ~('\\'|'"'))* '"'
	;
fragment
ESC	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	'>'
		|	'u' XDIGIT XDIGIT XDIGIT XDIGIT
		|	. // unknown, leave as it is
		)
	;

fragment
DIGIT 	:	'0' .. '9';
XDIGIT :
	|	'a' .. 'f'
	|	'A' .. 'F'
	;
NUMBER 	:	DIGIT+;
OR	:	'|';
AND	:	'&';
NOT	:	'!';
OPT	:	'?';
ONEMORE	:	'+';
ZEROMORE:	'*';

literal : NUMBER | STRING;

file	: (attrs | rule) +;

rule
 	: modifier* ID '::=' choice attrs? ';'? -> ^(RULE ID modifier* choice attrs?);
//		finally {
//		myRules.add($rule.tree);		
//		}

modifier	:	'private' -> ^(MODIFIER["private"])
	;

attrs 	:
        '{' attr* '}' -> ^(ATTRS attr*)
	;
	
attr 	: ID '=' value ';'? -> ^(ATTR ID value)
	;

value	:	literal | ID
	;

	
choice
	: sequence (OR sequence)* -> ^(CHOICE sequence+);

sequence 
	: option* -> ^(SEQ option*);

option	:		
	simple quantifier^?
	| AND^ simple
	| NOT^ simple
	;

quantifier 
	: OPT
	| ONEMORE
	| ZEROMORE
	;

simple 	: ID
	| literal
	| '(' choice ')' -> choice;



