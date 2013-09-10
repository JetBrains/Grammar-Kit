package org.intellij.jflex.parser;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
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
  [\t\r ]+                  { return com.intellij.psi.TokenType.WHITE_SPACE; }
  "\n"+                   { return FLEX_NEWLINE; }
  {LINE_COMMENT}          { return FLEX_LINE_COMMENT; }
  {BLOCK_COMMENT}         { return FLEX_BLOCK_COMMENT; }
}

<DECLARATIONS, RULES, CHAR_CLASS> {
  "[:jletter:]"           { return FLEX_CLASS1; }
  "[:jletterdigit:]"      { return FLEX_CLASS2; }
  "[:letter:]"            { return FLEX_CLASS3; }
  "[:digit:]"             { return FLEX_CLASS4; }
  "[:uppercase:]"         { return FLEX_CLASS5; }
  "[:lowercase:]"         { return FLEX_CLASS6; }
}

<YYINITIAL> {
  "%%"                    { yybegin(DECLARATIONS); return FLEX_PERC2; }

  [^% \n]+                { return FLEX_JAVA; }
}

<DECLARATIONS> {
  "="                     { return FLEX_EQ; }

  "%{"                    { parenCount=1; yybegin(BLOCK); return FLEX_PERC_8; }
  "%init{"                { parenCount=1; yybegin(BLOCK); return FLEX_PERC_10; }
  "%eofval{"              { parenCount=1; yybegin(BLOCK); return FLEX_PERC_27; }
  "%eof{"                 { parenCount=1; yybegin(BLOCK); return FLEX_PERC_29; }

  "%initthrow{"           { return FLEX_PERC_13; }
  "%yylexthrow{"          { return FLEX_PERC_25; }
  "%eofthrow{"            { return FLEX_PERC_32; }
  "%initthrow}"           { return FLEX_PERC_14; }
  "%yylexthrow}"          { return FLEX_PERC_26; }
  "%eofthrow}"            { return FLEX_PERC_33; }
}

<BLOCK> {
  "%}"                    { parenCount=0; yybegin(DECLARATIONS); return FLEX_PERC_9; }
  "%init}"                { parenCount=0; yybegin(DECLARATIONS); return FLEX_PERC_11; }
  "%eofval}"              { parenCount=0; yybegin(DECLARATIONS); return FLEX_PERC_28; }
  "%eof}"                 { parenCount=0; yybegin(DECLARATIONS); return FLEX_PERC_30; }

  ([^%} \n] | {JAVA_CHAR})*   { return FLEX_JAVA; }
}

<BLOCK, DECLARATIONS, RULES> {
  "{"                     { parenCount++; return FLEX_BRACE1; }
  "}"                     { --parenCount; return FLEX_BRACE2; }
}

<DECLARATIONS> {
  "%class"                { return FLEX_PERC_1; }
  "%implements"           { return FLEX_PERC_2; }
  "%extends"              { return FLEX_PERC_3; }
  "%public"               { return FLEX_PERC_4; }
  "%final"                { return FLEX_PERC_5; }
  "%abstract"             { return FLEX_PERC_6; }
  "%apiprivate"           { return FLEX_PERC_7; }
  "%initthrow"            { return FLEX_PERC_12; }
  "%ctorarg"              { return FLEX_PERC_15; }
  "%scanerror"            { return FLEX_PERC_16; }
  "%buffer"               { return FLEX_PERC_17; }
  "%include"              { return FLEX_PERC_18; }
  "%function"             { return FLEX_PERC_19; }
  "%integer"              { return FLEX_PERC_20; }
  "%int"                  { return FLEX_PERC_21; }
  "%intwrap"              { return FLEX_PERC_22; }
  "%type"                 { return FLEX_PERC_23; }
  "%yylexthrow"           { return FLEX_PERC_24; }
  "%eofthrow"             { return FLEX_PERC_31; }
  "%eofclose"             { return FLEX_PERC_34; }
  "%debug"                { return FLEX_PERC_36; }
  "%standalone"           { return FLEX_PERC_37; }
  "%cup"                  { return FLEX_PERC_38; }
  "%cupsym"               { return FLEX_PERC_39; }
  "%cupdebug"             { return FLEX_PERC_40; }
  "%byacc"                { return FLEX_PERC_41; }
  "%switch"               { return FLEX_PERC_42; }
  "%table"                { return FLEX_PERC_43; }
  "%pack"                 { return FLEX_PERC_44; }
  "%7bit"                 { return FLEX_PERC_45; }
  "%full"                 { return FLEX_PERC_46; }
  "%8bit"                 { return FLEX_PERC_47; }
  "%unicode"              { return FLEX_PERC_48; }
  "%16bit"                { return FLEX_PERC_49; }
  "%caseless"             { return FLEX_PERC_50; }
  "%ignorecase"           { return FLEX_PERC_51; }
  "%char"                 { return FLEX_PERC_52; }
  "%line"                 { return FLEX_PERC_53; }
  "%column"               { return FLEX_PERC_54; }
  "%notunix"              { return FLEX_PERC_55; }
  "%yyeof"                { return FLEX_PERC_56; }
  "%state"                { return FLEX_PERC_57; }
  "%xstate"               { return FLEX_PERC_59; }
  "%s"                    { return FLEX_PERC_58; }
  "%x"                    { return FLEX_PERC_69; }
}

<RULES> {
  "<"                     { return FLEX_ANGLE1; }
  ">"                     { return FLEX_ANGLE2; }
  {JAVA_CHAR}             { return FLEX_JAVA; }
}

<DECLARATIONS, RULES> {
  "%%"                    { yybegin(RULES); return FLEX_PERC2; }
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