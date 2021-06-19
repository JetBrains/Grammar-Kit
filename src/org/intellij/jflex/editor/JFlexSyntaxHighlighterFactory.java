/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.jflex.parser.JFlexLexer;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.intellij.jflex.parser.JFlexParserDefinition.CHAR_CLASS_OPERATORS;
import static org.intellij.jflex.parser.JFlexParserDefinition.PATTERN_OPERATORS;
import static org.intellij.jflex.psi.JFlexTypes.*;

public class JFlexSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  public static final TextAttributesKey ILLEGAL    = createTextAttributesKey("FLEX_ILLEGAL", HighlighterColors.BAD_CHARACTER);

  public static final TextAttributesKey MACRO      = createTextAttributesKey("FLEX_MACRO", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
  public static final TextAttributesKey STATE      = createTextAttributesKey("FLEX_STATE", DefaultLanguageHighlighterColors.CLASS_NAME);
  public static final TextAttributesKey CLASS      = createTextAttributesKey("FLEX_CLASS");
  public static final TextAttributesKey PATTERN_OP = createTextAttributesKey("FLEX_PATTERN_OP", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey CLASS_OP   = createTextAttributesKey("FLEX_CHAR_CLASS_OP", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey OP_RANGE   = createTextAttributesKey("FLEX_OP_RANGE", DefaultLanguageHighlighterColors.KEYWORD);

  public static final TextAttributesKey COMMENT    = createTextAttributesKey("FLEX_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey OPTION     = createTextAttributesKey("FLEX_OPTION", DefaultLanguageHighlighterColors.LABEL);
  public static final TextAttributesKey RAW_CODE   = createTextAttributesKey("FLEX_RAW_CODE", EditorColors.INJECTED_LANGUAGE_FRAGMENT);
  public static final TextAttributesKey SECT_DIV   = createTextAttributesKey("FLEX_SECTION_DIV", DefaultLanguageHighlighterColors.LABEL);

  public static final TextAttributesKey STRING     = createTextAttributesKey("FLEX_STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey CHAR       = createTextAttributesKey("FLEX_CHAR", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey CHAR_ESC   = createTextAttributesKey("FLEX_CHAR_ESC", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
  public static final TextAttributesKey NUMBER     = createTextAttributesKey("FLEX_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

  public static final TextAttributesKey CLASS_STD  = createTextAttributesKey("FLEX_PREDEFINED_CLASS", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey DOT        = createTextAttributesKey("FLEX_DOT", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey EOF        = createTextAttributesKey("FLEX_SECTION_DIV", DefaultLanguageHighlighterColors.LABEL);
  public static final TextAttributesKey LOOKAHEAD  = createTextAttributesKey("FLEX_LOOKAHEAD", DefaultLanguageHighlighterColors.OPERATION_SIGN);

  public static final TextAttributesKey COMMA      = createTextAttributesKey("FLEX_COMMA", DefaultLanguageHighlighterColors.COMMA);
  public static final TextAttributesKey OP_EQUAL   = createTextAttributesKey("FLEX_OP_EQUAL", DefaultLanguageHighlighterColors.OPERATION_SIGN);
  public static final TextAttributesKey PARENS     = createTextAttributesKey("FLEX_PARENS", DefaultLanguageHighlighterColors.PARENTHESES);
  public static final TextAttributesKey BRACES     = createTextAttributesKey("FLEX_BRACES", DefaultLanguageHighlighterColors.BRACES);
  public static final TextAttributesKey BRACKETS   = createTextAttributesKey("FLEX_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
  public static final TextAttributesKey ANGLES     = createTextAttributesKey("FLEX_ANGLES", DefaultLanguageHighlighterColors.BRACKETS);


  @Override
  public @NotNull SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
    return new JFlexSyntaxHighlighter();
  }

  private static class JFlexSyntaxHighlighter extends SyntaxHighlighterBase {
    @Override
    public @NotNull Lexer getHighlightingLexer() {
      return new JFlexLexer();
    }

    @Override
    public @NotNull TextAttributesKey @NotNull [] getTokenHighlights(IElementType o) {
      if (o == FLEX_LINE_COMMENT || o == FLEX_BLOCK_COMMENT) return pack(COMMENT);
      if (o == FLEX_RAW) return pack(RAW_CODE);
      if (o == FLEX_TWO_PERCS) return pack(SECT_DIV);
      if (o == FLEX_STRING) return pack(STRING);
      if (o == FLEX_CHAR) return pack(CHAR);
      if (o == FLEX_CHAR_ESC) return pack(CHAR_ESC);
      if (o == FLEX_NUMBER || o == FLEX_VERSION) return pack(NUMBER);
      if (o == FLEX_CHAR_CLASS) return pack(CLASS_STD);
      if (o == FLEX_EOF) return pack(EOF);
      if (o == FLEX_FSLASH) return pack(LOOKAHEAD);
      if (o == FLEX_COMMA) return pack(COMMA);
      if (o == FLEX_DOT) return pack(DOT);
      if (o == FLEX_EQ) return pack(OP_EQUAL);
      if (o == FLEX_DASH) return pack(OP_RANGE);
      if (o == FLEX_PAREN1 || o == FLEX_PAREN2) return pack(PARENS);
      if (o == FLEX_BRACE1 || o == FLEX_BRACE2) return pack(BRACES);
      if (o == FLEX_BRACK1 || o == FLEX_BRACK2) return pack(BRACKETS);
      if (o == FLEX_ANGLE1 || o == FLEX_ANGLE2) return pack(ANGLES);
      if (PATTERN_OPERATORS.contains(o)) return pack(PATTERN_OP);
      if (CHAR_CLASS_OPERATORS.contains(o)) return pack(CLASS_OP);
      if (o == FLEX_UNCLOSED || o == TokenType.BAD_CHARACTER) return pack(ILLEGAL);
      if (o.toString().startsWith("%")) return pack(OPTION);
      return TextAttributesKey.EMPTY_ARRAY;
    }
  }
}
