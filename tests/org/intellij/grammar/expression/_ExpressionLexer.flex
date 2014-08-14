package org.intellij.grammar.expression;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static org.intellij.grammar.expression.ExpressionTypes.*;

%%

%{
  public _ExpressionLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _ExpressionLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

COMMENT="//".*
NUMBER=[0-9]+(\.[0-9]*)?
ID=[:letter:][a-zA-Z_0-9]*
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
SYNTAX=;|.|\+|-|\*\*|\*|==|=|"/"|,|\(|\)|\^|\!=|\!|>=|<=|>|<

%%
<YYINITIAL> {
  {WHITE_SPACE}      { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "BETWEEN"          { return BETWEEN; }
  "AND"              { return AND; }

  {COMMENT}          { return COMMENT; }
  {NUMBER}           { return NUMBER; }
  {ID}               { return ID; }
  {STRING}           { return STRING; }
  {SYNTAX}           { return SYNTAX; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
