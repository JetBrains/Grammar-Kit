/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

// ---- ExpressionTypes.java -----------------
//header.txt
package fleet.org.intellij.grammar.expression;

import fleet.com.intellij.psi.tree.IElementType;

public interface ExpressionTypes {

  IElementType ARG_LIST = new IElementType("ARG_LIST", null);
  IElementType ASSIGN_EXPR = new IElementType("ASSIGN_EXPR", null);
  IElementType BETWEEN_EXPR = new IElementType("BETWEEN_EXPR", null);
  IElementType CALL_EXPR = new IElementType("CALL_EXPR", null);
  IElementType CONDITIONAL_EXPR = new IElementType("CONDITIONAL_EXPR", null);
  IElementType DIV_EXPR = new IElementType("DIV_EXPR", null);
  IElementType ELVIS_EXPR = new IElementType("ELVIS_EXPR", null);
  IElementType EXPR = new IElementType("EXPR", null);
  IElementType EXP_EXPR = new IElementType("EXP_EXPR", null);
  IElementType FACTORIAL_EXPR = new IElementType("FACTORIAL_EXPR", null);
  IElementType IDENTIFIER = new IElementType("IDENTIFIER", null);
  IElementType IS_NOT_EXPR = new IElementType("IS_NOT_EXPR", null);
  IElementType LITERAL_EXPR = new IElementType("LITERAL_EXPR", null);
  IElementType MINUS_EXPR = new IElementType("MINUS_EXPR", null);
  IElementType MUL_EXPR = new IElementType("MUL_EXPR", null);
  IElementType PAREN_EXPR = new IElementType("PAREN_EXPR", null);
  IElementType PLUS_EXPR = new IElementType("PLUS_EXPR", null);
  IElementType REF_EXPR = new IElementType("REF_EXPR", null);
  IElementType SPECIAL_EXPR = new IElementType("SPECIAL_EXPR", null);
  IElementType UNARY_MIN_EXPR = new IElementType("UNARY_MIN_EXPR", null);
  IElementType UNARY_NOT_EXPR = new IElementType("UNARY_NOT_EXPR", null);
  IElementType UNARY_PLUS_EXPR = new IElementType("UNARY_PLUS_EXPR", null);
  IElementType XOR_EXPR = new IElementType("XOR_EXPR", null);

  IElementType AND = new IElementType("AND", null);
  IElementType BETWEEN = new IElementType("BETWEEN", null);
  IElementType COMMENT = new IElementType("comment", null);
  IElementType ID = new IElementType("id", null);
  IElementType IS = new IElementType("IS", null);
  IElementType NOT = new IElementType("NOT", null);
  IElementType NUMBER = new IElementType("number", null);
  IElementType STRING = new IElementType("string", null);
  IElementType SYNTAX = new IElementType("syntax", null);
}