/*
 * Copyright 2011-present Greg Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.jflex.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.intellij.jflex.parser.JFlexParserDefinition.FLEX_NEWLINE;
import static org.intellij.jflex.psi.JFlexTypes.*;

%%

// The outline, states and macros are borrowed from the original JFlex lexer.
// See https://github.com/jflex-de/jflex/blob/master/jflex/src/main/jflex/LexScan.flex

%public
%class _JFlexLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state COMMENT, MACROS, CODE, REGEXPSTART, EXPRBAR
%state REGEXP, STATES, ACTION_CODE, STRING_CONTENT
%state CHARCLASS, MACROUSE, REPEATEXP, CLASSCHARS, CHARRANGE
%state REGEXP_CODEPOINT, STRING_CODEPOINT
%state SKIP_TOEOL, SKIP_WSNL, REPORT_UNCLOSED, REGEXPSTART0

%{
  int nextState;
  boolean macroDefinition;

  int braceCount;
  int bracketCount;
  int commentCount;

  public _JFlexLexer() {
    this((java.io.Reader)null);
  }
%}

Digit      = [0-9]
HexDigit   = [0-9a-fA-F]
OctDigit   = [0-7]

Number     = {Digit}+
HexNumber  = \\ x {HexDigit} {2}
OctNumber  = \\ [0-3]? {OctDigit} {1, 2}

DottedVersion =  [1-9][0-9]*(\.[0-9]+){0,2}

Unicode4  = \\ u {HexDigit} {4}
Unicode6  = \\ U {HexDigit} {6}

WSP        = [ \t\b]
WSPNL      = [\u2028\u2029\u000A\u000B\u000C\u000D\u0085\t\b\ ]
NWSPNL     = [^\u2028\u2029\u000A\u000B\u000C\u000D\u0085\t\b\ ]
NL         = [\u2028\u2029\u000A\u000B\u000C\u000D\u0085] | \u000D\u000A
NNL        = [^\u2028\u2029\u000A\u000B\u000C\u000D\u0085]

Ident      = {IdentStart} {IdentPart}*

IdentStart = [:jletter:]
IdentPart  = [:jletterdigit:]

Char = [_\p{Alpha}\p{Digit}]
Chars = {Char}+

JavaComment = {TraditionalComment}|{EndOfLineComment}
TraditionalComment = "/*"{CommentContent}\*+"/"
EndOfLineComment = "//" {NNL}*
CommentContent = ([^*]|\*+[^*/])*
StringCharacter = [^\u2028\u2029\u000A\u000B\u000C\u000D\u0085\"\\]
CharLiteral = \'([^\u2028\u2029\u000A\u000B\u000C\u000D\u0085\'\\]|{EscapeSequence})\'
StringLiteral = \"({StringCharacter}|{EscapeSequence})*\"
EscapeSequence = \\[^\u2028\u2029\u000A\u000B\u000C\u000D\u0085]|\\+u{HexDigit}{4}|\\[0-3]?{OctDigit}{1,2}
JavaRest = [^\{\}\"\'/]|"/"[^*/]
JavaCode = ({JavaRest}|{StringLiteral}|{CharLiteral}|{JavaComment})+


%%
<YYINITIAL> {
  "%%"             { macroDefinition=true; nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_TWO_PERCS; }
  ([^\%]|\%[^\%])* { return FLEX_RAW; }
}

<SKIP_WSNL> {
  {WSP}+         { return WHITE_SPACE; }
  {NL}+          { return FLEX_NEWLINE; }
  .              { yypushback(yylength()); yybegin(nextState); }
}

<SKIP_TOEOL> {
  .*             { return FLEX_LINE_COMMENT; }
  {NL}+          { yybegin(nextState); return FLEX_NEWLINE; }
}

<REPORT_UNCLOSED> {
  [^]            { yypushback(yylength()); yybegin(nextState); return FLEX_UNCLOSED; }
}

<COMMENT> {
  "/"+ "*"       { commentCount++; }
  "*"+ "/"       { if (commentCount > 0) commentCount--; else { yybegin(nextState); return FLEX_BLOCK_COMMENT; } }

  [^]            {  }
  <<EOF>>        { yybegin(REPORT_UNCLOSED); return FLEX_BLOCK_COMMENT; }
}

<MACROS> {
  "%{"                    { nextState=CODE; yybegin(SKIP_TOEOL); return FLEX_OPT_CODE1; }
  "%init{"                { nextState=CODE; yybegin(SKIP_TOEOL); return FLEX_OPT_INIT1; }
  "%eofval{"              { nextState=CODE; yybegin(SKIP_TOEOL); return FLEX_OPT_EOFVAL1; }
  "%eof{"                 { nextState=CODE; yybegin(SKIP_TOEOL); return FLEX_OPT_EOF1; }
  "%initthrow{"           { nextState=CODE; yybegin(SKIP_TOEOL); return FLEX_OPT_INITTHROW1; }
  "%yylexthrow{"          { nextState=CODE; yybegin(SKIP_TOEOL); return FLEX_OPT_YYLEXTHROW1; }
  "%eofthrow{"            { nextState=CODE; yybegin(SKIP_TOEOL); return FLEX_OPT_EOFTHROW1; }
}

<CODE> {
  "%}"                    { nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_OPT_CODE2; }
  "%init}"                { nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_OPT_INIT2; }
  "%eofval}"              { nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_OPT_EOFVAL2; }
  "%eof}"                 { nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_OPT_EOF2; }
  "%initthrow}"           { nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_OPT_INITTHROW2; }
  "%yylexthrow}"          { nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_OPT_YYLEXTHROW2; }
  "%eofthrow}"            { nextState=MACROS; yybegin(SKIP_TOEOL); return FLEX_OPT_EOFTHROW2; }

  ([^\%]|\%[^\}iey])*     { return FLEX_RAW; }
  <<EOF>>                 { yybegin(REPORT_UNCLOSED); return FLEX_RAW; }
}

<MACROS> {
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
  "%include"              { return FLEX_OPT_INCLUDE; }
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
  ^"%s" ("tate" "s"?)?    { return FLEX_OPT_STATE; }
  ^"%x" ("state" "s"?)?   { return FLEX_OPT_XSTATE; }

  // noop/deprecated
  //"%pack"                 { return FLEX_OPT_NOOP; }
  //"%inputstreamctor"      { return FLEX_OPT_NOOP; }
  //"%byacc"                { return FLEX_OPT_BYACC; }
  //"%switch"               { return FLEX_OPT_SWITCH; }
  //"%table"                { return FLEX_OPT_TABLE; }
}

<MACROS> {
  ^ "%%"                  { macroDefinition=false; nextState=REGEXPSTART; yybegin(SKIP_TOEOL); return FLEX_TWO_PERCS; }
  {EndOfLineComment}      { return FLEX_LINE_COMMENT; }
  "/*"                    { nextState=MACROS; yybegin(COMMENT); }
  "."                     { return FLEX_DOT; }
  ","                     { return FLEX_COMMA; }
  "<"                     { return FLEX_ANGLE1; }
  ">"                     { return FLEX_ANGLE2; }

  "="                     { yybegin(REGEXP); return FLEX_EQ; }
  {Ident}                 { return FLEX_ID; }
  {StringLiteral}         { return FLEX_STRING; }
  {Number}                { return FLEX_NUMBER; }
  {DottedVersion}         { return FLEX_VERSION; }

  {WSP}+                  { return WHITE_SPACE; }
  {NL}+                   { return FLEX_NEWLINE; }
}

<REGEXPSTART> {
  {EndOfLineComment}      { return FLEX_LINE_COMMENT; }
  "/*"                    { nextState=REGEXPSTART; yybegin(COMMENT); }

  ^ {WSP}* "%include"     { return FLEX_OPT_INCLUDE; }

  "<<EOF>>"               { yypushback(yylength()); yybegin(REGEXP); }
  "<"                     { yybegin(STATES); return FLEX_ANGLE1; }
  "}"                     { return FLEX_BRACE2; }

  {WSP}+                  { return WHITE_SPACE; }
  {NL}+                   { return FLEX_NEWLINE; }
  .                       { yypushback(yylength()); yybegin(REGEXP); }
}

<STATES> {
  ">" / {WSPNL}* "{"      { nextState=REGEXPSTART0; yybegin(SKIP_WSNL); return FLEX_ANGLE2; }
  ">"                     { nextState=REGEXP; yybegin(SKIP_WSNL); return FLEX_ANGLE2; }

  ","                     { return FLEX_COMMA; }
  {Ident}                 { return FLEX_ID; }

  {WSP}+                  { return WHITE_SPACE; }
  {NL}+                   { return FLEX_NEWLINE; }
  <<EOF>>                 { nextState=REGEXPSTART; yybegin(REPORT_UNCLOSED); }
}

<REGEXPSTART0> {
  "{" / {WSP}* {Ident} {WSP}* "}" { nextState=REGEXP; yybegin(MACROUSE); return FLEX_BRACE1; }
  "{" / "{" {WSP}* {Number}       { nextState=REGEXP; yybegin(REPEATEXP); return FLEX_BRACE1; }

  "{"                     { yybegin(REGEXPSTART); return FLEX_BRACE1; }
}

<REGEXP, CHARCLASS> {
  {HexNumber}             { return FLEX_CHAR_ESC; }
  {OctNumber}             { return FLEX_CHAR_ESC; }
  {Unicode4}              { return FLEX_CHAR_ESC; }
  {Unicode6}              { return FLEX_CHAR_ESC; }

  \\ [nbtrf]              { return FLEX_CHAR_ESC; }

  "[:jletter:]"           { return FLEX_CHAR_CLASS; }
  "[:jletterdigit:]"      { return FLEX_CHAR_CLASS; }
  "[:letter:]"            { return FLEX_CHAR_CLASS; }
  "[:uppercase:]"         { return FLEX_CHAR_CLASS; }
  "[:lowercase:]"         { return FLEX_CHAR_CLASS; }
  "[:digit:]"             { return FLEX_CHAR_CLASS; }
  "\\d"                   { return FLEX_CHAR_CLASS; }
  "\\D"                   { return FLEX_CHAR_CLASS; }
  "\\s"                   { return FLEX_CHAR_CLASS; }
  "\\S"                   { return FLEX_CHAR_CLASS; }
  "\\w"                   { return FLEX_CHAR_CLASS; }
  "\\W"                   { return FLEX_CHAR_CLASS; }
  "\\p{"[^}]*"}"          { return FLEX_CHAR_CLASS; }
  "\\P{"[^}]*"}"          { return FLEX_CHAR_CLASS; }

  \\.                     { return FLEX_CHAR_ESC; }
}

<REGEXP> {
  "<<EOF>>"               { return FLEX_EOF; }
  {WSP}+                  { return WHITE_SPACE; }
  {EndOfLineComment}      { return FLEX_LINE_COMMENT; }
  "/*"                    { nextState=REGEXP; yybegin(COMMENT); }

  "|"                     { if (macroDefinition) yybegin(EXPRBAR); return FLEX_BAR; }

  "{" / {WSP}* {Ident} {WSP}* "}" { nextState=REGEXP; yybegin(MACROUSE); return FLEX_BRACE1; }
  "{" / {WSP}* {Number}   { nextState=REGEXP; yybegin(REPEATEXP); return FLEX_BRACE1; }
  "{"                     { if (macroDefinition) return FLEX_CHAR; else yybegin(ACTION_CODE); return FLEX_BRACE1; }

  {NL} / {WSPNL}* [\|\[\{\"\\|\!\~\(\)\*\+\?\$\^\.\/]
                          { yypushback(yylength()); nextState=REGEXP; yybegin(SKIP_WSNL); }

  \"                      { nextState=REGEXP; yybegin(STRING_CONTENT); }
  "\\u{"                  { yybegin(REGEXP_CODEPOINT); }

  "!"                     { return FLEX_BANG; }
  "~"                     { return FLEX_TILDE; }
  "("                     { return FLEX_PAREN1; }
  ")"                     { return FLEX_PAREN2; }
  "*"                     { return FLEX_STAR; }
  "+"                     { return FLEX_PLUS; }
  "?"                     { return FLEX_QUESTION; }
  "$"                     { return FLEX_DOLLAR; }
  "^"                     { return FLEX_HAT; }
  "."                     { return FLEX_DOT; }
  "\\R"                   { return FLEX_CHAR_CLASS; }
  "["                     { yybegin(CHARCLASS); return FLEX_BRACK1; }
  "/"                     { return FLEX_FSLASH; }

  {NL}                    { yypushback(yylength()); if (macroDefinition) yybegin(MACROS); else yybegin(REGEXPSTART); }
  {Chars}                 { return FLEX_CHAR; }
  .                       { return FLEX_CHAR; }
}

<EXPRBAR> {
  {WSP}+                  { return WHITE_SPACE; }
  {NL}+                   { return FLEX_NEWLINE; }
  .                       { yypushback(yylength()); yybegin(REGEXP); }
}

<REPEATEXP, MACROUSE> {
  "}"                    { yybegin(nextState); return FLEX_BRACE2; }
  ","                    { return FLEX_COMMA; }
  {Number}               { return FLEX_NUMBER; }
  {Ident}                { return FLEX_ID; }

  {WSP}+                 { return WHITE_SPACE; }
  .                      { yypushback(yylength()); yybegin(nextState); } // fallback
}

<CHARCLASS> {
  "{" / {Ident} "}"          { nextState=CHARCLASS; yybegin(MACROUSE); return FLEX_BRACE1; }
  "["                        { bracketCount++; return FLEX_BRACK1; }
  "]"                        { if (bracketCount > 0) bracketCount--; else yybegin(REGEXP); return FLEX_BRACK2; }
  "^"                        { return FLEX_HAT; }
  \"                         { nextState=CHARCLASS; yybegin(STRING_CONTENT); }
  "\\u{" {HexDigit}{1,6} "}" { return FLEX_CHAR; }

  "--"                       { return FLEX_DASHDASH; }
  "&&"                       { return FLEX_AMPAMP; }
  "||"                       { return FLEX_BARBAR; }
  "~~"                       { return FLEX_TILDETILDE; }

  [.[^\^\"\\\[\]]] / "--"    { return FLEX_CHAR; }
  \\.              / "--"    { return FLEX_CHAR_ESC; }
  [.[^\^\"\\\[\]]] / "-"     { yybegin(CHARRANGE); return FLEX_CHAR; }
  \\.              / "-"     { yybegin(CHARRANGE); return FLEX_CHAR_ESC; }
  {Char}                     { yypushback(yylength()); yybegin(CLASSCHARS); }
  .                          { return FLEX_CHAR; }

  {NL}    { bracketCount=0; yypushback(yylength()); nextState=REGEXP; yybegin(REPORT_UNCLOSED); } // fallback

  <<EOF>> { nextState=REGEXP; yybegin(REPORT_UNCLOSED); return FLEX_BRACK2; }
}

<CLASSCHARS> {
  {Char} / "--" { yybegin(CHARCLASS); return FLEX_CHAR; }
  {Char} / "-"  { yypushback(yylength()); yybegin(CHARCLASS); return FLEX_CHAR; }
  {Char}        { }
  [^]           { yypushback(yylength()); yybegin(CHARCLASS); return FLEX_CHAR; }
}

<CHARRANGE> {
  "-]"         { yypushback(1); yybegin(CHARCLASS); return FLEX_CHAR; }
  "-"          { return FLEX_DASH; }
  {Char}       { yybegin(CHARCLASS); return FLEX_CHAR; }
  \\.          { yybegin(CHARCLASS); return FLEX_CHAR_ESC; }
  .            { yypushback(yylength()); yybegin(CHARCLASS); }
}

<STRING_CONTENT> {
  \"       { yybegin(nextState); return FLEX_STRING; }
  \\\"     { }
  [^\"\\\u2028\u2029\u000A\u000B\u000C\u000D\u0085]+ { }

  {HexNumber} {  }
  {OctNumber} {  }
  {Unicode4}  {  }
  {Unicode6}  {  }

  "\\u{"      { yybegin(STRING_CODEPOINT); }

  \\[btrf]    {  }
  \\.         {  }

  {NL}     { yypushback(yylength()); yybegin(nextState); return FLEX_STRING; }
  <<EOF>>  { yybegin(REPORT_UNCLOSED); return FLEX_STRING; }
}


<ACTION_CODE> {
  "{"        { braceCount++; }
  "}"        { if (braceCount > 0) braceCount--; else { yypushback(1); yybegin(REGEXPSTART); return FLEX_RAW; } }
  {JavaCode} {  }
  <<EOF>>    { nextState=REGEXPSTART; yybegin(REPORT_UNCLOSED); return FLEX_RAW; }
}


<REGEXP_CODEPOINT> {
  "}"             { yybegin(REGEXP); return FLEX_BRACE2; }
  {HexDigit}{1,6} { return FLEX_NUMBER; }
  {WSP}+          { return WHITE_SPACE; }
  {NL}+           { return FLEX_NEWLINE; }
}

<STRING_CODEPOINT> {
  "}"             { yybegin(STRING_CONTENT); }
  {HexDigit}{1,6} { }
  {WSP}+          { }
  {NL}            { yypushback(yylength()); yybegin(REPORT_UNCLOSED); return FLEX_STRING; }
}


[^] { return BAD_CHARACTER; }
