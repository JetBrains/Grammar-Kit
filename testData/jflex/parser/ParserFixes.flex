 /* It's an automatically generated code. Do not modify it. */
package sample.lexer;

%%
%{
  private int lBraceCount;
%}

%{ // empty user code
%}

%class X<A, B<C, D>>
%implements Y<A, B<C, D>>, B<C, D>

%state STATE, STATE2,

FUNCTION        = [_a-zA-Z]([$_a-zA-Z0-9])*?[:]([^\n\r])*?(->|=>)
NOTTAGORCOMMENTBEGIN = ("<"[^c/C!] | "<c"[^fF] | "<C"[^fF] | "</"[^Cc] | "</c"[^Ff] | "</C"[^fF] |
             "<!"[^-] | "<!-"[^-] | "<!--"[^-])
%x STAT
%s STAT
%{
String escapes="\\ \n \r \t \f" + '\\' + '\n' + '\r' + '\t' + '\f' ;
%}

ID_CHAR = [a-zA-Z_0-9-]
INT_LITERAL = [-+]?[0-9]+
EXPONENT_PART=[Ee][+-]?[0-9]+
ZENDCOMMENT=;{COMMENTCONTENT}
QUASI_LITERAL_CHAR=[^\\$`] | \\ {ANY_CHAR} | \$ [^`{[:jletter:]]
NO_STATE=<TAG>
VARIABLE = "$"(::)?{NAME}(::{NAME})*
BOOL            = true|yes|on|false|no|off|undefined|null
SYNTAX = ,|\.
M=(-?0b[01]+)

%%
<YYINITIAL> {
  "empty_braces"          { /* ignored*/ }
  "double_braces"         {{ return com.intellij.psi.TokenType.WHITE_SPACE; }}

	// tab-indent

  "if_clause"             { if (yystate() != ANNO_PATTERN) { yybegin(PATH_ELEMENT); } return IF_CLAUSE; }

  (<#noparse(>)?)         { return checkStyle(ANGLE, NOPARSE_START); }
  =(=)?                   { return EQ; }
  true|false              { return BOOLEAN; }

  %{FUNCTION}             {}

  <LONG_TEMPLATE_ENTRY> "{"              { lBraceCount++; return LBRACE; }
  <LONG_TEMPLATE_ENTRY> "}"              {
                                             if (lBraceCount == 0) {
                                               popState();
                                               return LONG_TEMPLATE_ENTRY_END;
                                             }
                                             lBraceCount--;
                                             return RBRACE;
                                         }
  <STATE> {
        (\[\/@]) { return 1; }
        (<\/@>)  { return 1; }
  }

  <TAG_DOC_SPACE>  {WHITE_DOC_SPACE_CHAR}+ {
    if (checkAhead('<') || checkAhead('\"')) yybegin(COMMENT_DATA);
    else if (checkAhead('\u007b') ) yybegin(COMMENT_DATA);  // lbrace - there's a error in JLex when typing lbrace directly
    else yybegin(DOC_TAG_VALUE);
    return myTokenTypes.space();
  }

  "{{!"[^"--"]~"}}"   { }
  "<=>"                                   {   return myTM.process(tCMP); }
  "\""                                    {  if ("a".indexOf('"') <= -1) { return '-'; } }

  <INTERPOLATION> {
    [^\n}]* "}"? { if (zzBuffer.charAt(getTokenEnd()-1) == '}') yypushback(1); yybegin(stateToGo.pop()); return JadeTokenTypes.JS_EXPR; }
  }


  {HexDigit}{1,6} { }
  {HexDigit}{ 1 , 6 } { }
  [\w_--\d] { }
  <<EOF>> { }
}