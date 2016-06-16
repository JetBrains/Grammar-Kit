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

package org.intellij.grammar.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.BnfParserDefinition;
import org.intellij.grammar.parser.BnfLexer;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.intellij.grammar.psi.BnfTypes.*;

/**
* @author gregsh
*/
class BnfSyntaxHighlighter extends SyntaxHighlighterBase {
  public static final TextAttributesKey ILLEGAL = createTextAttributesKey("BNF_ILLEGAL", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
  public static final TextAttributesKey COMMENT = createTextAttributesKey("BNF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey STRING = createTextAttributesKey("BNF_STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey PATTERN = createTextAttributesKey("BNF_PATTERN", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
  public static final TextAttributesKey NUMBER = createTextAttributesKey("BNF_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
  public static final TextAttributesKey KEYWORD = createTextAttributesKey("BNF_KEYWORD", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
  public static final TextAttributesKey TOKEN = createTextAttributesKey("BNF_TOKEN", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey RULE = createTextAttributesKey("BNF_RULE", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey META_RULE = createTextAttributesKey("BNF_META_RULE", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey META_PARAM = createTextAttributesKey("BNF_META_RULE_PARAM");
  public static final TextAttributesKey ATTRIBUTE = createTextAttributesKey("BNF_ATTRIBUTE", DefaultLanguageHighlighterColors.INTERFACE_NAME);
  public static final TextAttributesKey EXTERNAL = createTextAttributesKey("BNF_EXTERNAL", DefaultLanguageHighlighterColors.STATIC_METHOD);
  public static final TextAttributesKey PARENTHS = createTextAttributesKey("BNF_PARENTHS", DefaultLanguageHighlighterColors.PARENTHESES);
  public static final TextAttributesKey BRACES = createTextAttributesKey("BNF_BRACES", DefaultLanguageHighlighterColors.BRACES);
  public static final TextAttributesKey BRACKETS = createTextAttributesKey("BNF_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
  public static final TextAttributesKey ANGLES = createTextAttributesKey("BNF_ANGLES", DefaultLanguageHighlighterColors.PARENTHESES);

  public static final TextAttributesKey OP_SIGN = createTextAttributesKey("BNF_OP_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
  public static final TextAttributesKey RECOVER_MARKER = createTextAttributesKey("BNF_RECOVER_MARKER");
  public static final TextAttributesKey PIN_MARKER = createTextAttributesKey(
    "BNF_PIN", new TextAttributes(null, null, DefaultLanguageHighlighterColors.LINE_COMMENT.getDefaultAttributes().getForegroundColor(), EffectType.BOLD_DOTTED_LINE, 0));

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return new BnfLexer();
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType type) {
    if (type == TokenType.BAD_CHARACTER) {
      return pack(ILLEGAL);
    }
    if (type == BnfParserDefinition.BNF_LINE_COMMENT || type == BnfParserDefinition.BNF_BLOCK_COMMENT) {
      return pack(COMMENT);
    }
    if (type == BNF_STRING) {
      return pack(STRING);
    }
    if (type == BNF_NUMBER) {
      return pack(NUMBER);
    }
    if (type == BNF_OP_ONEMORE || type == BNF_OP_AND || type == BNF_OP_EQ || type == BNF_OP_IS ||
        type == BNF_OP_NOT || type == BNF_OP_OPT || type == BNF_OP_OR || type == BNF_OP_ZEROMORE) {
      return pack(OP_SIGN);
    }
    if (type == BNF_LEFT_PAREN || type == BNF_RIGHT_PAREN) {
      return pack(PARENTHS);
    }
    if (type == BNF_LEFT_BRACE || type == BNF_RIGHT_BRACE) {
      return pack(BRACES);
    }
    if (type == BNF_LEFT_BRACKET || type == BNF_RIGHT_BRACKET) {
      return pack(BRACKETS);
    }
    if (type == BNF_EXTERNAL_START || type == BNF_EXTERNAL_END) {
      return pack(ANGLES);
    }
    return EMPTY;
  }
}
