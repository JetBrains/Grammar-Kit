/*
 * Copyright 2011-2013 Gregory Shrago
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

package org.intellij.jflex.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
import org.intellij.jflex.parser.JFlexLexer;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.intellij.jflex.psi.JFlexTypes.*;

public class JFlexSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  //public static final TextAttributesKey ILLEGAL = createTextAttributesKey("FLEX_ILLEGAL", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
  public static final TextAttributesKey COMMENT = createTextAttributesKey("FLEX_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey KEYWORD = createTextAttributesKey("FLEX_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey PREDEFINED_CLASS = createTextAttributesKey("FLEX_PREDEFINED_CLASS", DefaultLanguageHighlighterColors.STATIC_FIELD);
  public static final TextAttributesKey CLASS = createTextAttributesKey("FLEX_CLASS", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
  public static final TextAttributesKey MACRO = createTextAttributesKey("FLEX_MACRO", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
  public static final TextAttributesKey STATE = createTextAttributesKey("FLEX_MACRO", DefaultLanguageHighlighterColors.CLASS_NAME);

  public static final TextAttributesKey STRING = createTextAttributesKey("FLEX_STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey ESCAPED_CHAR = createTextAttributesKey("FLEX_ESCAPED_CHAR", DefaultLanguageHighlighterColors.NUMBER);
  public static final TextAttributesKey CHAR = createTextAttributesKey("FLEX_CHAR", DefaultLanguageHighlighterColors.IDENTIFIER);
  public static final TextAttributesKey NUMBER = createTextAttributesKey("FLEX_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

  public static final TextAttributesKey PARENTHS = createTextAttributesKey("FLEX_PARENTHS", DefaultLanguageHighlighterColors.PARENTHESES);
  public static final TextAttributesKey BRACES = createTextAttributesKey("FLEX_BRACES", DefaultLanguageHighlighterColors.BRACES);
  public static final TextAttributesKey BRACKETS = createTextAttributesKey("FLEX_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
  public static final TextAttributesKey OP_SIGN = createTextAttributesKey("FLEX_OP_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);

  @NotNull
  @Override
  public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
    return new JFlexSyntaxHighlighter();
  }

  private class JFlexSyntaxHighlighter extends SyntaxHighlighterBase {
    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
      return new JFlexLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
      if (tokenType == FLEX_LINE_COMMENT || tokenType == FLEX_BLOCK_COMMENT) {
        return pack(COMMENT);
      }
      else if (tokenType == FLEX_NUMBER) {
        return pack(NUMBER);
      }
      else if (tokenType == FLEX_STRING) {
        return pack(STRING);
      }
      else if (tokenType == FLEX_ESCAPED_CHAR) {
        return pack(ESCAPED_CHAR);
      }
      else if (tokenType == FLEX_CHAR) {
        return pack(CHAR);
      }
      else if (tokenType.toString().startsWith("%")) {
        return pack(KEYWORD);
      }
      else if (tokenType.toString().length() == 1) {
        return pack(OP_SIGN);
      }
      else if (tokenType.toString().startsWith("[:")) {
        return pack(PREDEFINED_CLASS);
      }
      return EMPTY;
    }
  }
}
