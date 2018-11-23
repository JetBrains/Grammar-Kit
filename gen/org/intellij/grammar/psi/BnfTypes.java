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
package org.intellij.grammar.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.grammar.psi.impl.*;
import com.intellij.psi.impl.source.tree.CompositePsiElement;

public interface BnfTypes {

  IElementType BNF_ATTR = new BnfCompositeElementType("BNF_ATTR");
  IElementType BNF_ATTRS = new BnfCompositeElementType("BNF_ATTRS");
  IElementType BNF_ATTR_PATTERN = new BnfCompositeElementType("BNF_ATTR_PATTERN");
  IElementType BNF_CHOICE = new BnfCompositeElementType("BNF_CHOICE");
  IElementType BNF_EXPRESSION = new BnfCompositeElementType("BNF_EXPRESSION");
  IElementType BNF_EXTERNAL_EXPRESSION = new BnfCompositeElementType("BNF_EXTERNAL_EXPRESSION");
  IElementType BNF_LIST_ENTRY = new BnfCompositeElementType("BNF_LIST_ENTRY");
  IElementType BNF_LITERAL_EXPRESSION = new BnfCompositeElementType("BNF_LITERAL_EXPRESSION");
  IElementType BNF_MODIFIER = new BnfCompositeElementType("BNF_MODIFIER");
  IElementType BNF_PAREN_EXPRESSION = new BnfCompositeElementType("BNF_PAREN_EXPRESSION");
  IElementType BNF_PAREN_OPT_EXPRESSION = new BnfCompositeElementType("BNF_PAREN_OPT_EXPRESSION");
  IElementType BNF_PREDICATE = new BnfCompositeElementType("BNF_PREDICATE");
  IElementType BNF_PREDICATE_SIGN = new BnfCompositeElementType("BNF_PREDICATE_SIGN");
  IElementType BNF_QUANTIFIED = new BnfCompositeElementType("BNF_QUANTIFIED");
  IElementType BNF_QUANTIFIER = new BnfCompositeElementType("BNF_QUANTIFIER");
  IElementType BNF_REFERENCE_OR_TOKEN = new BnfCompositeElementType("BNF_REFERENCE_OR_TOKEN");
  IElementType BNF_RULE = new BnfCompositeElementType("BNF_RULE");
  IElementType BNF_SEQUENCE = new BnfCompositeElementType("BNF_SEQUENCE");
  IElementType BNF_STRING_LITERAL_EXPRESSION = new BnfCompositeElementType("BNF_STRING_LITERAL_EXPRESSION");
  IElementType BNF_VALUE_LIST = new BnfCompositeElementType("BNF_VALUE_LIST");

  IElementType BNF_BLOCK_COMMENT = new BnfTokenType("block_comment");
  IElementType BNF_EXTERNAL_END = new BnfTokenType(">>");
  IElementType BNF_EXTERNAL_START = new BnfTokenType("<<");
  IElementType BNF_ID = new BnfTokenType("id");
  IElementType BNF_LEFT_BRACE = new BnfTokenType("{");
  IElementType BNF_LEFT_BRACKET = new BnfTokenType("[");
  IElementType BNF_LEFT_PAREN = new BnfTokenType("(");
  IElementType BNF_LINE_COMMENT = new BnfTokenType("line_comment");
  IElementType BNF_NUMBER = new BnfTokenType("number");
  IElementType BNF_OP_AND = new BnfTokenType("&");
  IElementType BNF_OP_EQ = new BnfTokenType("=");
  IElementType BNF_OP_IS = new BnfTokenType("::=");
  IElementType BNF_OP_NOT = new BnfTokenType("!");
  IElementType BNF_OP_ONEMORE = new BnfTokenType("+");
  IElementType BNF_OP_OPT = new BnfTokenType("?");
  IElementType BNF_OP_OR = new BnfTokenType("|");
  IElementType BNF_OP_ZEROMORE = new BnfTokenType("*");
  IElementType BNF_RIGHT_BRACE = new BnfTokenType("}");
  IElementType BNF_RIGHT_BRACKET = new BnfTokenType("]");
  IElementType BNF_RIGHT_PAREN = new BnfTokenType(")");
  IElementType BNF_SEMICOLON = new BnfTokenType(";");
  IElementType BNF_STRING = new BnfTokenType("string");

  class Factory {
    public static CompositePsiElement createElement(IElementType type) {
       if (type == BNF_ATTR) {
        return new BnfAttrImpl(type);
      }
      else if (type == BNF_ATTRS) {
        return new BnfAttrsImpl(type);
      }
      else if (type == BNF_ATTR_PATTERN) {
        return new BnfAttrPatternImpl(type);
      }
      else if (type == BNF_CHOICE) {
        return new BnfChoiceImpl(type);
      }
      else if (type == BNF_EXTERNAL_EXPRESSION) {
        return new BnfExternalExpressionImpl(type);
      }
      else if (type == BNF_LIST_ENTRY) {
        return new BnfListEntryImpl(type);
      }
      else if (type == BNF_LITERAL_EXPRESSION) {
        return new BnfLiteralExpressionImpl(type);
      }
      else if (type == BNF_MODIFIER) {
        return new BnfModifierImpl(type);
      }
      else if (type == BNF_PAREN_EXPRESSION) {
        return new BnfParenExpressionImpl(type);
      }
      else if (type == BNF_PAREN_OPT_EXPRESSION) {
        return new BnfParenOptExpressionImpl(type);
      }
      else if (type == BNF_PREDICATE) {
        return new BnfPredicateImpl(type);
      }
      else if (type == BNF_PREDICATE_SIGN) {
        return new BnfPredicateSignImpl(type);
      }
      else if (type == BNF_QUANTIFIED) {
        return new BnfQuantifiedImpl(type);
      }
      else if (type == BNF_QUANTIFIER) {
        return new BnfQuantifierImpl(type);
      }
      else if (type == BNF_REFERENCE_OR_TOKEN) {
        return new BnfReferenceOrTokenImpl(type);
      }
      else if (type == BNF_RULE) {
        return new BnfRuleImpl(type);
      }
      else if (type == BNF_SEQUENCE) {
        return new BnfSequenceImpl(type);
      }
      else if (type == BNF_STRING_LITERAL_EXPRESSION) {
        return new BnfStringLiteralExpressionImpl(type);
      }
      else if (type == BNF_VALUE_LIST) {
        return new BnfValueListImpl(type);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
