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
import java.util.Collections;
import java.util.Set;
import java.util.LinkedHashMap;

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

  class Classes {
    public static Class<?> findClass(IElementType elementType) {
      return ourMap.get(elementType);
    }

    public static Set<IElementType> elementTypes() {
      return Collections.unmodifiableSet(ourMap.keySet());
    }

    private static final LinkedHashMap<IElementType, Class<?>> ourMap = new LinkedHashMap<IElementType, Class<?>>();

    static {
      ourMap.put(BNF_ATTR, BnfAttrImpl.class);
      ourMap.put(BNF_ATTRS, BnfAttrsImpl.class);
      ourMap.put(BNF_ATTR_PATTERN, BnfAttrPatternImpl.class);
      ourMap.put(BNF_CHOICE, BnfChoiceImpl.class);
      ourMap.put(BNF_EXTERNAL_EXPRESSION, BnfExternalExpressionImpl.class);
      ourMap.put(BNF_LIST_ENTRY, BnfListEntryImpl.class);
      ourMap.put(BNF_LITERAL_EXPRESSION, BnfLiteralExpressionImpl.class);
      ourMap.put(BNF_MODIFIER, BnfModifierImpl.class);
      ourMap.put(BNF_PAREN_EXPRESSION, BnfParenExpressionImpl.class);
      ourMap.put(BNF_PAREN_OPT_EXPRESSION, BnfParenOptExpressionImpl.class);
      ourMap.put(BNF_PREDICATE, BnfPredicateImpl.class);
      ourMap.put(BNF_PREDICATE_SIGN, BnfPredicateSignImpl.class);
      ourMap.put(BNF_QUANTIFIED, BnfQuantifiedImpl.class);
      ourMap.put(BNF_QUANTIFIER, BnfQuantifierImpl.class);
      ourMap.put(BNF_REFERENCE_OR_TOKEN, BnfReferenceOrTokenImpl.class);
      ourMap.put(BNF_RULE, BnfRuleImpl.class);
      ourMap.put(BNF_SEQUENCE, BnfSequenceImpl.class);
      ourMap.put(BNF_STRING_LITERAL_EXPRESSION, BnfStringLiteralExpressionImpl.class);
      ourMap.put(BNF_VALUE_LIST, BnfValueListImpl.class);
    }
  }
}
