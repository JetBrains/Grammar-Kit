/*
 * Copyright 2011-present JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.jflex.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.jflex.psi.impl.*;
import com.intellij.psi.impl.source.tree.CompositePsiElement;

public interface JFlexTypes {

  IElementType FLEX_CHAR_RANGE = new JFlexCompositeElementType("FLEX_CHAR_RANGE");
  IElementType FLEX_CHOICE_EXPRESSION = new JFlexCompositeElementType("FLEX_CHOICE_EXPRESSION");
  IElementType FLEX_CLASS_EXPRESSION = new JFlexCompositeElementType("FLEX_CLASS_EXPRESSION");
  IElementType FLEX_DECLARATIONS_SECTION = new JFlexCompositeElementType("FLEX_DECLARATIONS_SECTION");
  IElementType FLEX_EXPRESSION = new JFlexCompositeElementType("FLEX_EXPRESSION");
  IElementType FLEX_JAVA_CODE = new JFlexCompositeElementType("FLEX_JAVA_CODE");
  IElementType FLEX_JAVA_TYPE = new JFlexCompositeElementType("FLEX_JAVA_TYPE");
  IElementType FLEX_LEXICAL_RULES_SECTION = new JFlexCompositeElementType("FLEX_LEXICAL_RULES_SECTION");
  IElementType FLEX_LITERAL_EXPRESSION = new JFlexCompositeElementType("FLEX_LITERAL_EXPRESSION");
  IElementType FLEX_LOOK_AHEAD = new JFlexCompositeElementType("FLEX_LOOK_AHEAD");
  IElementType FLEX_MACRO_DEFINITION = new JFlexCompositeElementType("FLEX_MACRO_DEFINITION");
  IElementType FLEX_MACRO_REFERENCE = new JFlexCompositeElementType("FLEX_MACRO_REFERENCE");
  IElementType FLEX_MACRO_REF_EXPRESSION = new JFlexCompositeElementType("FLEX_MACRO_REF_EXPRESSION");
  IElementType FLEX_NOT_EXPRESSION = new JFlexCompositeElementType("FLEX_NOT_EXPRESSION");
  IElementType FLEX_OPTION = new JFlexCompositeElementType("FLEX_OPTION");
  IElementType FLEX_PAREN_EXPRESSION = new JFlexCompositeElementType("FLEX_PAREN_EXPRESSION");
  IElementType FLEX_PREDEFINED_CLASS_EXPRESSION = new JFlexCompositeElementType("FLEX_PREDEFINED_CLASS_EXPRESSION");
  IElementType FLEX_QUANTIFIER_EXPRESSION = new JFlexCompositeElementType("FLEX_QUANTIFIER_EXPRESSION");
  IElementType FLEX_RULE = new JFlexCompositeElementType("FLEX_RULE");
  IElementType FLEX_SEQUENCE_EXPRESSION = new JFlexCompositeElementType("FLEX_SEQUENCE_EXPRESSION");
  IElementType FLEX_STATE_DECLARATION = new JFlexCompositeElementType("FLEX_STATE_DECLARATION");
  IElementType FLEX_STATE_DEFINITION = new JFlexCompositeElementType("FLEX_STATE_DEFINITION");
  IElementType FLEX_STATE_LIST = new JFlexCompositeElementType("FLEX_STATE_LIST");
  IElementType FLEX_STATE_REFERENCE = new JFlexCompositeElementType("FLEX_STATE_REFERENCE");
  IElementType FLEX_USER_CODE_SECTION = new JFlexCompositeElementType("FLEX_USER_CODE_SECTION");
  IElementType FLEX_USER_VALUE = new JFlexCompositeElementType("FLEX_USER_VALUE");

  IElementType FLEX_AMPAMP = new JFlexTokenType("&&");
  IElementType FLEX_ANGLE1 = new JFlexTokenType("<");
  IElementType FLEX_ANGLE2 = new JFlexTokenType(">");
  IElementType FLEX_BANG = new JFlexTokenType("!");
  IElementType FLEX_BAR = new JFlexTokenType("|");
  IElementType FLEX_BARBAR = new JFlexTokenType("||");
  IElementType FLEX_BLOCK_COMMENT = new JFlexTokenType("block_comment");
  IElementType FLEX_BRACE1 = new JFlexTokenType("{");
  IElementType FLEX_BRACE2 = new JFlexTokenType("}");
  IElementType FLEX_BRACK1 = new JFlexTokenType("[");
  IElementType FLEX_BRACK2 = new JFlexTokenType("]");
  IElementType FLEX_CHAR = new JFlexTokenType("char");
  IElementType FLEX_CHAR_CLASS = new JFlexTokenType("char_class");
  IElementType FLEX_CHAR_ESC = new JFlexTokenType("char_esc");
  IElementType FLEX_COMMA = new JFlexTokenType(",");
  IElementType FLEX_DASH = new JFlexTokenType("-");
  IElementType FLEX_DASHDASH = new JFlexTokenType("--");
  IElementType FLEX_DOLLAR = new JFlexTokenType("$");
  IElementType FLEX_DOT = new JFlexTokenType(".");
  IElementType FLEX_EOF = new JFlexTokenType("<<EOF>>");
  IElementType FLEX_EQ = new JFlexTokenType("=");
  IElementType FLEX_FSLASH = new JFlexTokenType("/");
  IElementType FLEX_HAT = new JFlexTokenType("^");
  IElementType FLEX_ID = new JFlexTokenType("id");
  IElementType FLEX_LINE_COMMENT = new JFlexTokenType("line_comment");
  IElementType FLEX_NUMBER = new JFlexTokenType("number");
  IElementType FLEX_OPT16BIT = new JFlexTokenType("%16bit");
  IElementType FLEX_OPT_7BIT = new JFlexTokenType("%7bit");
  IElementType FLEX_OPT_8BIT = new JFlexTokenType("%8bit");
  IElementType FLEX_OPT_ABSTRACT = new JFlexTokenType("%abstract");
  IElementType FLEX_OPT_APIPRIVATE = new JFlexTokenType("%apiprivate");
  IElementType FLEX_OPT_BUFFER = new JFlexTokenType("%buffer");
  IElementType FLEX_OPT_CASELESS = new JFlexTokenType("%caseless");
  IElementType FLEX_OPT_CHAR = new JFlexTokenType("%char");
  IElementType FLEX_OPT_CLASS = new JFlexTokenType("%class");
  IElementType FLEX_OPT_CODE1 = new JFlexTokenType("%{");
  IElementType FLEX_OPT_CODE2 = new JFlexTokenType("%}");
  IElementType FLEX_OPT_COLUMN = new JFlexTokenType("%column");
  IElementType FLEX_OPT_CTORARG = new JFlexTokenType("%ctorarg");
  IElementType FLEX_OPT_CUP = new JFlexTokenType("%cup");
  IElementType FLEX_OPT_CUPDEBUG = new JFlexTokenType("%cupdebug");
  IElementType FLEX_OPT_CUPSYM = new JFlexTokenType("%cupsym");
  IElementType FLEX_OPT_DEBUG = new JFlexTokenType("%debug");
  IElementType FLEX_OPT_EOF1 = new JFlexTokenType("%eof{");
  IElementType FLEX_OPT_EOF2 = new JFlexTokenType("%eof}");
  IElementType FLEX_OPT_EOFCLOSE = new JFlexTokenType("%eofclose");
  IElementType FLEX_OPT_EOFTHROW = new JFlexTokenType("%eofthrow");
  IElementType FLEX_OPT_EOFTHROW1 = new JFlexTokenType("%eofthrow{");
  IElementType FLEX_OPT_EOFTHROW2 = new JFlexTokenType("%eofthrow}");
  IElementType FLEX_OPT_EOFVAL1 = new JFlexTokenType("%eofval{");
  IElementType FLEX_OPT_EOFVAL2 = new JFlexTokenType("%eofval}");
  IElementType FLEX_OPT_EXTENDS = new JFlexTokenType("%extends");
  IElementType FLEX_OPT_FINAL = new JFlexTokenType("%final");
  IElementType FLEX_OPT_FULL = new JFlexTokenType("%full");
  IElementType FLEX_OPT_FUNCTION = new JFlexTokenType("%function");
  IElementType FLEX_OPT_IGNORECASE = new JFlexTokenType("%ignorecase");
  IElementType FLEX_OPT_IMPLEMENTS = new JFlexTokenType("%implements");
  IElementType FLEX_OPT_INCLUDE = new JFlexTokenType("%include");
  IElementType FLEX_OPT_INIT1 = new JFlexTokenType("%init{");
  IElementType FLEX_OPT_INIT2 = new JFlexTokenType("%init}");
  IElementType FLEX_OPT_INITTHROW = new JFlexTokenType("%initthrow");
  IElementType FLEX_OPT_INITTHROW1 = new JFlexTokenType("%initthrow{");
  IElementType FLEX_OPT_INITTHROW2 = new JFlexTokenType("%initthrow}");
  IElementType FLEX_OPT_INT = new JFlexTokenType("%int");
  IElementType FLEX_OPT_INTEGER = new JFlexTokenType("%integer");
  IElementType FLEX_OPT_INTWRAP = new JFlexTokenType("%intwrap");
  IElementType FLEX_OPT_LINE = new JFlexTokenType("%line");
  IElementType FLEX_OPT_NOTUNIX = new JFlexTokenType("%notunix");
  IElementType FLEX_OPT_PUBLIC = new JFlexTokenType("%public");
  IElementType FLEX_OPT_SCANERROR = new JFlexTokenType("%scanerror");
  IElementType FLEX_OPT_STANDALONE = new JFlexTokenType("%standalone");
  IElementType FLEX_OPT_STATE = new JFlexTokenType("%state");
  IElementType FLEX_OPT_TYPE = new JFlexTokenType("%type");
  IElementType FLEX_OPT_UNICODE = new JFlexTokenType("%unicode");
  IElementType FLEX_OPT_XSTATE = new JFlexTokenType("%xstate");
  IElementType FLEX_OPT_YYEOF = new JFlexTokenType("%yyeof");
  IElementType FLEX_OPT_YYLEXTHROW = new JFlexTokenType("%yylexthrow");
  IElementType FLEX_OPT_YYLEXTHROW1 = new JFlexTokenType("%yylexthrow{");
  IElementType FLEX_OPT_YYLEXTHROW2 = new JFlexTokenType("%yylexthrow}");
  IElementType FLEX_PAREN1 = new JFlexTokenType("(");
  IElementType FLEX_PAREN2 = new JFlexTokenType(")");
  IElementType FLEX_PLUS = new JFlexTokenType("+");
  IElementType FLEX_QUESTION = new JFlexTokenType("?");
  IElementType FLEX_RAW = new JFlexTokenType("code block");
  IElementType FLEX_STAR = new JFlexTokenType("*");
  IElementType FLEX_STRING = new JFlexTokenType("string");
  IElementType FLEX_TILDE = new JFlexTokenType("~");
  IElementType FLEX_TILDETILDE = new JFlexTokenType("~~");
  IElementType FLEX_TWO_PERCS = new JFlexTokenType("%%");
  IElementType FLEX_UNCLOSED = new JFlexTokenType("unclosed");
  IElementType FLEX_VERSION = new JFlexTokenType("version");

  class Factory {
    public static CompositePsiElement createElement(IElementType type) {
       if (type == FLEX_CHAR_RANGE) {
        return new JFlexCharRangeImpl(type);
      }
      else if (type == FLEX_CHOICE_EXPRESSION) {
        return new JFlexChoiceExpressionImpl(type);
      }
      else if (type == FLEX_CLASS_EXPRESSION) {
        return new JFlexClassExpressionImpl(type);
      }
      else if (type == FLEX_DECLARATIONS_SECTION) {
        return new JFlexDeclarationsSectionImpl(type);
      }
      else if (type == FLEX_JAVA_CODE) {
        return new JFlexJavaCodeImpl(type);
      }
      else if (type == FLEX_JAVA_TYPE) {
        return new JFlexJavaTypeImpl(type);
      }
      else if (type == FLEX_LEXICAL_RULES_SECTION) {
        return new JFlexLexicalRulesSectionImpl(type);
      }
      else if (type == FLEX_LITERAL_EXPRESSION) {
        return new JFlexLiteralExpressionImpl(type);
      }
      else if (type == FLEX_LOOK_AHEAD) {
        return new JFlexLookAheadImpl(type);
      }
      else if (type == FLEX_MACRO_DEFINITION) {
        return new JFlexMacroDefinitionImpl(type);
      }
      else if (type == FLEX_MACRO_REFERENCE) {
        return new JFlexMacroReferenceImpl(type);
      }
      else if (type == FLEX_MACRO_REF_EXPRESSION) {
        return new JFlexMacroRefExpressionImpl(type);
      }
      else if (type == FLEX_NOT_EXPRESSION) {
        return new JFlexNotExpressionImpl(type);
      }
      else if (type == FLEX_OPTION) {
        return new JFlexOptionImpl(type);
      }
      else if (type == FLEX_PAREN_EXPRESSION) {
        return new JFlexParenExpressionImpl(type);
      }
      else if (type == FLEX_PREDEFINED_CLASS_EXPRESSION) {
        return new JFlexPredefinedClassExpressionImpl(type);
      }
      else if (type == FLEX_QUANTIFIER_EXPRESSION) {
        return new JFlexQuantifierExpressionImpl(type);
      }
      else if (type == FLEX_RULE) {
        return new JFlexRuleImpl(type);
      }
      else if (type == FLEX_SEQUENCE_EXPRESSION) {
        return new JFlexSequenceExpressionImpl(type);
      }
      else if (type == FLEX_STATE_DECLARATION) {
        return new JFlexStateDeclarationImpl(type);
      }
      else if (type == FLEX_STATE_DEFINITION) {
        return new JFlexStateDefinitionImpl(type);
      }
      else if (type == FLEX_STATE_LIST) {
        return new JFlexStateListImpl(type);
      }
      else if (type == FLEX_STATE_REFERENCE) {
        return new JFlexStateReferenceImpl(type);
      }
      else if (type == FLEX_USER_CODE_SECTION) {
        return new JFlexUserCodeSectionImpl(type);
      }
      else if (type == FLEX_USER_VALUE) {
        return new JFlexUserValueImpl(type);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
