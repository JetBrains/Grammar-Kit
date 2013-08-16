/*
 * Copyright 2011-2011 Gregory Shrago
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
package org.intellij.jflex.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.jflex.psi.impl.*;

public interface JFlexTypes {

  IElementType FLEX_CHOICE_EXPRESSION = new JFlexCompositeElementType("FLEX_CHOICE_EXPRESSION");
  IElementType FLEX_CLASS_EXPRESSION = new JFlexCompositeElementType("FLEX_CLASS_EXPRESSION");
  IElementType FLEX_DECLARATIONS_SECTION = new JFlexCompositeElementType("FLEX_DECLARATIONS_SECTION");
  IElementType FLEX_EXPRESSION = new JFlexCompositeElementType("FLEX_EXPRESSION");
  IElementType FLEX_JAVA_CODE = new JFlexCompositeElementType("FLEX_JAVA_CODE");
  IElementType FLEX_JAVA_FQN = new JFlexCompositeElementType("FLEX_JAVA_FQN");
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
  IElementType FLEX_SEQUENCE_OP = new JFlexCompositeElementType("FLEX_SEQUENCE_OP");
  IElementType FLEX_STATE_DECLARATION = new JFlexCompositeElementType("FLEX_STATE_DECLARATION");
  IElementType FLEX_STATE_DEFINITION = new JFlexCompositeElementType("FLEX_STATE_DEFINITION");
  IElementType FLEX_STATE_REFERENCE = new JFlexCompositeElementType("FLEX_STATE_REFERENCE");
  IElementType FLEX_USER_CODE_SECTION = new JFlexCompositeElementType("FLEX_USER_CODE_SECTION");

  IElementType FLEX_ANGLE1 = new JFlexTokenType("<");
  IElementType FLEX_ANGLE2 = new JFlexTokenType(">");
  IElementType FLEX_BLOCK_COMMENT = new JFlexTokenType("block_comment");
  IElementType FLEX_BRACE1 = new JFlexTokenType("{");
  IElementType FLEX_BRACE2 = new JFlexTokenType("}");
  IElementType FLEX_BRACK1 = new JFlexTokenType("[");
  IElementType FLEX_BRACK2 = new JFlexTokenType("]");
  IElementType FLEX_CHAR = new JFlexTokenType("char");
  IElementType FLEX_CLASS1 = new JFlexTokenType("[:jletter:]");
  IElementType FLEX_CLASS2 = new JFlexTokenType("[:jletterdigit:]");
  IElementType FLEX_CLASS3 = new JFlexTokenType("[:letter:]");
  IElementType FLEX_CLASS4 = new JFlexTokenType("[:digit:]");
  IElementType FLEX_CLASS5 = new JFlexTokenType("[:uppercase:]");
  IElementType FLEX_CLASS6 = new JFlexTokenType("[:lowercase:]");
  IElementType FLEX_COMMA = new JFlexTokenType(",");
  IElementType FLEX_DASH = new JFlexTokenType("-");
  IElementType FLEX_DOLLAR = new JFlexTokenType("$");
  IElementType FLEX_DOT = new JFlexTokenType(".");
  IElementType FLEX_EOF = new JFlexTokenType("<<EOF>>");
  IElementType FLEX_EQ = new JFlexTokenType("=");
  IElementType FLEX_ESCAPED_CHAR = new JFlexTokenType("escaped_char");
  IElementType FLEX_ID = new JFlexTokenType("id");
  IElementType FLEX_JAVA = new JFlexTokenType("java");
  IElementType FLEX_LINE_COMMENT = new JFlexTokenType("line_comment");
  IElementType FLEX_NEWLINE = new JFlexTokenType("newline");
  IElementType FLEX_NOT = new JFlexTokenType("!");
  IElementType FLEX_NOT2 = new JFlexTokenType("~");
  IElementType FLEX_NUMBER = new JFlexTokenType("number");
  IElementType FLEX_OR = new JFlexTokenType("|");
  IElementType FLEX_PAREN1 = new JFlexTokenType("(");
  IElementType FLEX_PAREN2 = new JFlexTokenType(")");
  IElementType FLEX_PERC2 = new JFlexTokenType("%%");
  IElementType FLEX_PERC_1 = new JFlexTokenType("%class");
  IElementType FLEX_PERC_10 = new JFlexTokenType("%init{");
  IElementType FLEX_PERC_11 = new JFlexTokenType("%init}");
  IElementType FLEX_PERC_12 = new JFlexTokenType("%initthrow");
  IElementType FLEX_PERC_13 = new JFlexTokenType("%initthrow{");
  IElementType FLEX_PERC_14 = new JFlexTokenType("%initthrow}");
  IElementType FLEX_PERC_15 = new JFlexTokenType("%ctorarg");
  IElementType FLEX_PERC_16 = new JFlexTokenType("%scanerror");
  IElementType FLEX_PERC_17 = new JFlexTokenType("%buffer");
  IElementType FLEX_PERC_18 = new JFlexTokenType("%include");
  IElementType FLEX_PERC_19 = new JFlexTokenType("%function");
  IElementType FLEX_PERC_2 = new JFlexTokenType("%implements");
  IElementType FLEX_PERC_20 = new JFlexTokenType("%integer");
  IElementType FLEX_PERC_21 = new JFlexTokenType("%int");
  IElementType FLEX_PERC_22 = new JFlexTokenType("%intwrap");
  IElementType FLEX_PERC_23 = new JFlexTokenType("%type");
  IElementType FLEX_PERC_24 = new JFlexTokenType("%yylexthrow");
  IElementType FLEX_PERC_25 = new JFlexTokenType("%yylexthrow{");
  IElementType FLEX_PERC_26 = new JFlexTokenType("%yylexthrow}");
  IElementType FLEX_PERC_27 = new JFlexTokenType("%eofval{");
  IElementType FLEX_PERC_28 = new JFlexTokenType("%eofval}");
  IElementType FLEX_PERC_29 = new JFlexTokenType("%eof{");
  IElementType FLEX_PERC_3 = new JFlexTokenType("%extends");
  IElementType FLEX_PERC_30 = new JFlexTokenType("%eof}");
  IElementType FLEX_PERC_31 = new JFlexTokenType("%eofthrow");
  IElementType FLEX_PERC_32 = new JFlexTokenType("%eofthrow{");
  IElementType FLEX_PERC_33 = new JFlexTokenType("%eofthrow}");
  IElementType FLEX_PERC_34 = new JFlexTokenType("%eofclose");
  IElementType FLEX_PERC_36 = new JFlexTokenType("%debug");
  IElementType FLEX_PERC_37 = new JFlexTokenType("%standalone");
  IElementType FLEX_PERC_38 = new JFlexTokenType("%cup");
  IElementType FLEX_PERC_39 = new JFlexTokenType("%cupsym");
  IElementType FLEX_PERC_4 = new JFlexTokenType("%public");
  IElementType FLEX_PERC_40 = new JFlexTokenType("%cupdebug");
  IElementType FLEX_PERC_41 = new JFlexTokenType("%byacc");
  IElementType FLEX_PERC_42 = new JFlexTokenType("%switch");
  IElementType FLEX_PERC_43 = new JFlexTokenType("%table");
  IElementType FLEX_PERC_44 = new JFlexTokenType("%pack");
  IElementType FLEX_PERC_45 = new JFlexTokenType("%7bit");
  IElementType FLEX_PERC_46 = new JFlexTokenType("%full");
  IElementType FLEX_PERC_47 = new JFlexTokenType("%8bit");
  IElementType FLEX_PERC_48 = new JFlexTokenType("%unicode");
  IElementType FLEX_PERC_49 = new JFlexTokenType("%16bit");
  IElementType FLEX_PERC_5 = new JFlexTokenType("%final");
  IElementType FLEX_PERC_50 = new JFlexTokenType("%caseless");
  IElementType FLEX_PERC_51 = new JFlexTokenType("%ignorecase");
  IElementType FLEX_PERC_52 = new JFlexTokenType("%char");
  IElementType FLEX_PERC_53 = new JFlexTokenType("%line");
  IElementType FLEX_PERC_54 = new JFlexTokenType("%column");
  IElementType FLEX_PERC_55 = new JFlexTokenType("%notunix");
  IElementType FLEX_PERC_56 = new JFlexTokenType("%yyeof");
  IElementType FLEX_PERC_57 = new JFlexTokenType("%state");
  IElementType FLEX_PERC_58 = new JFlexTokenType("%s");
  IElementType FLEX_PERC_59 = new JFlexTokenType("%xstate");
  IElementType FLEX_PERC_6 = new JFlexTokenType("%abstract");
  IElementType FLEX_PERC_69 = new JFlexTokenType("%x");
  IElementType FLEX_PERC_7 = new JFlexTokenType("%apiprivate");
  IElementType FLEX_PERC_8 = new JFlexTokenType("%{");
  IElementType FLEX_PERC_9 = new JFlexTokenType("%}");
  IElementType FLEX_PLUS = new JFlexTokenType("+");
  IElementType FLEX_QUESTION = new JFlexTokenType("?");
  IElementType FLEX_ROOF = new JFlexTokenType("^");
  IElementType FLEX_SLASH = new JFlexTokenType("\\");
  IElementType FLEX_SLASH2 = new JFlexTokenType("/");
  IElementType FLEX_STAR = new JFlexTokenType("*");
  IElementType FLEX_STRING = new JFlexTokenType("string");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == FLEX_CHOICE_EXPRESSION) {
        return new JFlexChoiceExpressionImpl(node);
      }
      else if (type == FLEX_CLASS_EXPRESSION) {
        return new JFlexClassExpressionImpl(node);
      }
      else if (type == FLEX_DECLARATIONS_SECTION) {
        return new JFlexDeclarationsSectionImpl(node);
      }
      else if (type == FLEX_EXPRESSION) {
        return new JFlexExpressionImpl(node);
      }
      else if (type == FLEX_JAVA_CODE) {
        return new JFlexJavaCodeImpl(node);
      }
      else if (type == FLEX_JAVA_FQN) {
        return new JFlexJavaFqnImpl(node);
      }
      else if (type == FLEX_LEXICAL_RULES_SECTION) {
        return new JFlexLexicalRulesSectionImpl(node);
      }
      else if (type == FLEX_LITERAL_EXPRESSION) {
        return new JFlexLiteralExpressionImpl(node);
      }
      else if (type == FLEX_LOOK_AHEAD) {
        return new JFlexLookAheadImpl(node);
      }
      else if (type == FLEX_MACRO_DEFINITION) {
        return new JFlexMacroDefinitionImpl(node);
      }
      else if (type == FLEX_MACRO_REFERENCE) {
        return new JFlexMacroReferenceImpl(node);
      }
      else if (type == FLEX_MACRO_REF_EXPRESSION) {
        return new JFlexMacroRefExpressionImpl(node);
      }
      else if (type == FLEX_NOT_EXPRESSION) {
        return new JFlexNotExpressionImpl(node);
      }
      else if (type == FLEX_OPTION) {
        return new JFlexOptionImpl(node);
      }
      else if (type == FLEX_PAREN_EXPRESSION) {
        return new JFlexParenExpressionImpl(node);
      }
      else if (type == FLEX_PREDEFINED_CLASS_EXPRESSION) {
        return new JFlexPredefinedClassExpressionImpl(node);
      }
      else if (type == FLEX_QUANTIFIER_EXPRESSION) {
        return new JFlexQuantifierExpressionImpl(node);
      }
      else if (type == FLEX_RULE) {
        return new JFlexRuleImpl(node);
      }
      else if (type == FLEX_SEQUENCE_EXPRESSION) {
        return new JFlexSequenceExpressionImpl(node);
      }
      else if (type == FLEX_SEQUENCE_OP) {
        return new JFlexSequenceOpImpl(node);
      }
      else if (type == FLEX_STATE_DECLARATION) {
        return new JFlexStateDeclarationImpl(node);
      }
      else if (type == FLEX_STATE_DEFINITION) {
        return new JFlexStateDefinitionImpl(node);
      }
      else if (type == FLEX_STATE_REFERENCE) {
        return new JFlexStateReferenceImpl(node);
      }
      else if (type == FLEX_USER_CODE_SECTION) {
        return new JFlexUserCodeSectionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
