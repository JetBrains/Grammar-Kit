package org.intellij.jflex.parser;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.intellij.jflex.parser.JFlexParserDefinition.FLEX_NEWLINE;
import static org.intellij.jflex.psi.JFlexTypes.*;

%%

%{
  private int prevState;
  private int parenCount;

  public _JFlexLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _JFlexLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{
  return;
%eof}

//%state USER_SECTION = YYINITIAL
%state DECLARATIONS
%state RULES
%state CHAR_CLASS
%state BLOCK
%state BLOCK_0

//EOL="\r"|"\n"|"\r\n"
//LINE_WS=[\ \t\f]
//WHITE_SPACE=({LINE_WS}|{EOL})+

ID=(_|[:letter:])[a-zA-Z_0-9]*
UNCLOSED_STRING=\"([^\n\"\\]|\\.)*
STRING={UNCLOSED_STRING}\"
NUMBER=[:digit:]+
LINE_COMMENT="//".*
BLOCK_COMMENT="/*" !([^]* "*/" [^]*) ("*/")?
ESCAPED_CHAR=\\.
CHAR=[^\"\{\}\[\]\(\)\+\*\?\\/]
JAVA_CHAR=\'({ESCAPED_CHAR} | [^'])\'

%%
<YYINITIAL, DECLARATIONS, RULES, BLOCK> {
  [\t\r ]+                { return WHITE_SPACE; }
  \R+                     { return FLEX_NEWLINE; }
  {LINE_COMMENT}          { return FLEX_LINE_COMMENT; }
  {BLOCK_COMMENT}         { return FLEX_BLOCK_COMMENT; }
}

<DECLARATIONS, RULES, CHAR_CLASS> {
  "[:jletter:]"           { return FLEX_CLASS_JL; }
  "[:jletterdigit:]"      { return FLEX_CLASS_JLD; }
  "[:letter:]"            { return FLEX_CLASS_L; }
  "[:digit:]"             { return FLEX_CLASS_D; }
  "[:uppercase:]"         { return FLEX_CLASS_LU; }
  "[:lowercase:]"         { return FLEX_CLASS_LL; }
}

<YYINITIAL> {
  "%%"                    { yybegin(DECLARATIONS); return FLEX_TWO_PERCS; }

  [^% \n]+                { return FLEX_JAVA; }
}

<DECLARATIONS> {
  "="                     { return FLEX_EQ; }

  "%{"                    { parenCount=1; yybegin(BLOCK_0); return FLEX_OPT_CODE1; }
  "%init{"                { parenCount=1; yybegin(BLOCK_0); return FLEX_OPT_INIT1; }
  "%eofval{"              { parenCount=1; yybegin(BLOCK_0); return FLEX_OPT_EOFVAL1; }
  "%eof{"                 { parenCount=1; yybegin(BLOCK_0); return FLEX_OPT_EOF1; }

  "%initthrow{"           { return FLEX_OPT_INITTHROW1; }
  "%yylexthrow{"          { return FLEX_OPT_YYLEXTHROW1; }
  "%eofthrow{"            { return FLEX_OPT_EOFTHROW1; }
  "%initthrow}"           { return FLEX_OPT_INITTHROW2; }
  "%yylexthrow}"          { return FLEX_OPT_YYLEXTHROW2; }
  "%eofthrow}"            { return FLEX_OPT_EOFTHROW2; }
}

<BLOCK_0> {
  [^\n]+                  { return FLEX_LINE_COMMENT; }
  "\n"                    { yybegin(BLOCK); return FLEX_NEWLINE; }
}

<BLOCK> {
  "%}"                    { parenCount=0; yybegin(DECLARATIONS); return FLEX_OPT_CODE2; }
  "%init}"                { parenCount=0; yybegin(DECLARATIONS); return FLEX_OPT_INIT2; }
  "%eofval}"              { parenCount=0; yybegin(DECLARATIONS); return FLEX_OPT_EOFVAL2; }
  "%eof}"                 { parenCount=0; yybegin(DECLARATIONS); return FLEX_OPT_EOF2; }

  ([^%} \n] | {JAVA_CHAR})*   { return FLEX_JAVA; }
}

<BLOCK, DECLARATIONS, RULES> {
  "{"                     { parenCount++; return FLEX_BRACE1; }
  "}"                     { --parenCount; return FLEX_BRACE2; }
}

<DECLARATIONS, RULES> {
  "%include"              { return FLEX_OPT_INCLUDE; }
}

<DECLARATIONS> {
  "%class"                { return FLEX_OPT_CLASS; }
  "%implements"           { return FLEX_OPT_IMPLEMENTS; }
  "%extends"              { return FLEX_OPT_EXTENDS; }
  "%public"               { return FLEX_OPT_PUBLIC; }
  "%final"                { return FLEX_OPT_FINAL; }
  "%abstract"             { return FLEX_OPT_ABSTRACT; }
  "%apiprivate"           { return FLEX_OPT_APIPRIVATE; }
  "%initthrow"            { return FLEX_OPT_INITTHROW; }
  "%ctorarg"              { return FLEX_OPT_CTORARG; }
  "%scanerror"            { return FLEX_OPT_SCANERROR; }
  "%buffer"               { return FLEX_OPT_BUFFER; }
  "%function"             { return FLEX_OPT_FUNCTION; }
  "%integer"              { return FLEX_OPT_INTEGER; }
  "%int"                  { return FLEX_OPT_INT; }
  "%intwrap"              { return FLEX_OPT_INTWRAP; }
  "%type"                 { return FLEX_OPT_TYPE; }
  "%yylexthrow"           { return FLEX_OPT_YYLEXTHROW; }
  "%eofthrow"             { return FLEX_OPT_EOFTHROW; }
  "%eofclose"             { return FLEX_OPT_EOFCLOSE; }
  "%debug"                { return FLEX_OPT_DEBUG; }
  "%standalone"           { return FLEX_OPT_STANDALONE; }
  "%cup"                  { return FLEX_OPT_CUP; }
  "%cupsym"               { return FLEX_OPT_CUPSYM; }
  "%cupdebug"             { return FLEX_OPT_CUPDEBUG; }
  "%byacc"                { return FLEX_OPT_BYACC; }
  "%switch"               { return FLEX_OPT_SWITCH; }
  "%table"                { return FLEX_OPT_TABLE; }
  "%pack"                 { return FLEX_OPT_PACK; }
  "%7bit"                 { return FLEX_OPT_7BIT; }
  "%full"                 { return FLEX_OPT_FULL; }
  "%8bit"                 { return FLEX_OPT_8BIT; }
  "%unicode"              { return FLEX_OPT_UNICODE; }
  "%16bit"                { return FLEX_OPT16BIT; }
  "%caseless"             { return FLEX_OPT_CASELESS; }
  "%ignorecase"           { return FLEX_OPT_IGNORECASE; }
  "%char"                 { return FLEX_OPT_CHAR; }
  "%line"                 { return FLEX_OPT_LINE; }
  "%column"               { return FLEX_OPT_COLUMN; }
  "%notunix"              { return FLEX_OPT_NOTUNIX; }
  "%yyeof"                { return FLEX_OPT_YYEOF; }
  "%state"                { return FLEX_OPT_STATE; }
  "%xstate"               { return FLEX_OPT_XSTATE; }
  "%s"                    { return FLEX_OPT_S; }
  "%x"                    { return FLEX_OPT_X; }
}

<RULES> {
  "<"                     { return FLEX_ANGLE1; }
  ">"                     { return FLEX_ANGLE2; }
  {JAVA_CHAR}             { return FLEX_JAVA; }
}

<DECLARATIONS, RULES> {
  "%%"                    { yybegin(RULES); return FLEX_TWO_PERCS; }
  "*"                     { return FLEX_STAR; }
  "("                     { return FLEX_PAREN1; }
  ")"                     { return FLEX_PAREN2; }
  "["                     { prevState = yystate(); yybegin(CHAR_CLASS); return FLEX_BRACK1; }
  "]"                     { return FLEX_BRACK2; }
  "?"                     { return FLEX_QUESTION; }
  "-"                     { return FLEX_DASH; }
  "+"                     { return FLEX_PLUS; }
  "^"                     { return FLEX_ROOF; }
  "/"                     { return FLEX_SLASH2; }
  "."                     { return FLEX_DOT; }
  ","                     { return FLEX_COMMA; }
  "|"                     { return FLEX_OR; }
  "\\"                    { return FLEX_SLASH; }
  "$"                     { return FLEX_DOLLAR; }
  "!"                     { return FLEX_NOT; }
  "~"                     { return FLEX_NOT2; }
  "<<EOF>>"               { return FLEX_EOF; }

  {ID}                    { return FLEX_ID; }
  {STRING}                { return FLEX_STRING; }
  {NUMBER}                { return FLEX_NUMBER; }
  {ESCAPED_CHAR}          { return FLEX_ESCAPED_CHAR; }
  {CHAR}                  { return FLEX_CHAR; }
}

<CHAR_CLASS> {
  "]"                     { yybegin(prevState); return FLEX_BRACK2; }
  "-"                     { return FLEX_DASH; }
  "^"                     { return FLEX_ROOF; }
  {ESCAPED_CHAR}          { return FLEX_ESCAPED_CHAR; }
  {STRING}                { return FLEX_STRING; }

  "\n"                    { yybegin(prevState); return FLEX_NEWLINE; }
  [^\[]                   { return FLEX_CHAR; }
}

[^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }