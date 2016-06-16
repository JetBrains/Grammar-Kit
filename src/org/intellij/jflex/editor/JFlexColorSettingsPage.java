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

package org.intellij.jflex.editor;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.jflex.parser.JFlexFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.Map;

import static org.intellij.jflex.editor.JFlexSyntaxHighlighterFactory.*;

/**
 * @author gregsh
 */
public class JFlexColorSettingsPage implements ColorSettingsPage {
  private static final AttributesDescriptor[] ATTRS;

  static {
    ATTRS = new AttributesDescriptor[]{
      new AttributesDescriptor("Illegal char", ILLEGAL),
      new AttributesDescriptor("Comment", COMMENT),
      new AttributesDescriptor("Keyword", KEYWORD),
      new AttributesDescriptor("Predefined class", STD_CLASS),
      new AttributesDescriptor("Character class", CLASS),
      new AttributesDescriptor("Macro", MACRO),
      new AttributesDescriptor("State", STATE),
      new AttributesDescriptor("String", STRING),
      new AttributesDescriptor("Escaped character", ESCAPED_CHAR),
      new AttributesDescriptor("Character", CHAR),
      new AttributesDescriptor("Number", NUMBER),
      new AttributesDescriptor("Code", CODE),
      new AttributesDescriptor("Parenthesis", PARENTHS),
      new AttributesDescriptor("Braces", BRACES),
      new AttributesDescriptor("Brackets", BRACKETS),
      new AttributesDescriptor("Angles", ANGLES),
      new AttributesDescriptor("Equal sign", OP_EQUAL),
      new AttributesDescriptor("Operation sign", OP_SIGN),
    };
  }

  @NotNull
  public String getDisplayName() {
    return "JFlex";
  }

  public Icon getIcon() {
    return JFlexFileType.INSTANCE.getIcon();
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return ATTRS;
  }

  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  public SyntaxHighlighter getHighlighter() {
    return new JFlexSyntaxHighlighterFactory().getSyntaxHighlighter(null, null);
  }

  @NotNull
  public String getDemoText() {
    return "/* Header comment */\n" +
           "<j>package sample.lexer;</j>\n" +
           "\n" +
           "%%\n" +
           "%public\n" +
           "%class _MyLexer\n" +
           "%unicode\n" +
           "%eof{ return;\n" +
           "%eof}\n" +
           "%{\n" +
           "  <j>private int parenCount;</j>\n" +
           "%}\n" +
           "\n" +
           "// lexer states\n" +
           "%state <s>BLOCK</s>, <s>QUALIFICATION</s>\n" +
           "\n" +
           "// macro definitions\n" +
           "<m>WHITESPACE</m>=<c>[ \\n\\r\\t]</c>\n" +
           "<m>ESCAPED_CHAR</m>=\\\\.\n" +
           "<m>STRING</m>=\\\"(<c>[^\\\"\\\\]</c>|\\\\.)*\\\"\n" +
           "<m>ID</m> = [:letter:]([:letter:]|[:digit:]|_)*\n" +
           "<m>BLOCK_COMMENT</m>=\"//\".* | \"/*\" !(<c>[^]</c>* \"*/\" <c>[^]</c>*) (\"*/\")?\n" +
           "<m>NUMBER</m>=<c>[+-]</c>[:digit:]+\n" +
           "<m>FLOAT</m>=<m>{NUMBER}</m>(\\.[:digit:]){1, 3}\n" +
           "\n" +
           "%%\n" +
           "<<s>YYINITIAL</s>, <s>BLOCK</s>> {\n" +
           "    <m>{WHITESPACE}</m>      <j>{ return WHITESPACE; }</j>\n" +
           "    <m>{STRING}</m>          <j>{ return STRING; }</j>\n" +
           "    \"(\"               <j>{ return PAREN1; }</j>\n" +
           "    \")\"               <j>{ return PAREN2; }</j>\n" +
           "    \".\"               <j>{ yybegin(QUALIFICATION); return DOT; }</j>\n" +
           "    <c>[^]</c>  <j>{ return BAD_CHARACER; }</j>\n" +
           "}\n";
  }

  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return ContainerUtil.newHashMap(Arrays.asList("s", "m", "c", "j"), Arrays.asList(STATE, MACRO, CLASS, CODE));
  }
}