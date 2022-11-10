/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.jflex.editor;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.grammar.GrammarKitBundle;
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
      new AttributesDescriptor("Illegal symbol", ILLEGAL),

      new AttributesDescriptor("Macro", MACRO),
      new AttributesDescriptor("State", STATE),
      new AttributesDescriptor("Character class", CLASS),
      new AttributesDescriptor("Pattern operator", PATTERN_OP),
      new AttributesDescriptor("Character class operator", CLASS_OP),

      new AttributesDescriptor("Comment", COMMENT),
      new AttributesDescriptor("Lexer option", OPTION),
      new AttributesDescriptor("Java code", RAW_CODE),
      new AttributesDescriptor("Section divider", SECT_DIV),

      new AttributesDescriptor("String", STRING),
      new AttributesDescriptor("Character", CHAR),
      new AttributesDescriptor("Character escape", CHAR_ESC),
      new AttributesDescriptor("Number", NUMBER),

      new AttributesDescriptor("Predefined character class", CLASS_STD),
      new AttributesDescriptor("EOF matcher", EOF),
      new AttributesDescriptor("Lookahead separator", LOOKAHEAD),

      new AttributesDescriptor("Comma", COMMA),
      new AttributesDescriptor("Dot", DOT),
      new AttributesDescriptor("Equal sign", OP_EQUAL),
      new AttributesDescriptor("Range operator", OP_RANGE),
      new AttributesDescriptor("Parentheses", PARENS),
      new AttributesDescriptor("Curly braces", BRACES),
      new AttributesDescriptor("Square brackets", BRACKETS),
      new AttributesDescriptor("Angle brackets", ANGLES),
    };
  }

  @Override
  public @NotNull String getDisplayName() {
    return GrammarKitBundle.message("language.name.jflex");
  }

  @Override
  public Icon getIcon() {
    return JFlexFileType.INSTANCE.getIcon();
  }

  @Override
  public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
    return ATTRS;
  }

  @Override
  public ColorDescriptor @NotNull [] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @Override
  public @NotNull SyntaxHighlighter getHighlighter() {
    return new JFlexSyntaxHighlighterFactory().getSyntaxHighlighter(null, null);
  }

  @Override
  public @NotNull String getDemoText() {
    return """
      /* Header comment */
      package sample.lexer;

      %%
      %public
      %class _MyLexer
      %unicode
      %{
        private int parenCount;
      %}

      // lexer states
      %state <s>BLOCK</s>, <s>QUALIFICATION</s>

      // macro definitions
      <m>WHITESPACE</m>=<c>[ \\n\\r\\t]</c>
      <m>ESCAPED_CHAR</m>=\\\\.
      <m>STRING</m>=\\"(<c>[^\\"\\\\]</c>|\\\\.)*\\"
      <m>ID</m> = [a-z_&&[A-Z]]([:letter:]|[:digit:]|_)*
      <m>BLOCK_COMMENT</m>="//".* | "/*" !(<c>[^]</c>* "*/" <c>[^]</c>*) ("*/")?
      <m>NUMBER</m>=<c>[+-]</c>[:digit:]+
      <m>FLOAT</m>=<m>{NUMBER}</m>(\\.[:digit:]){1, 3}

      %%
      <<s>YYINITIAL</s>, <s>BLOCK</s>> {
          <m>{WHITESPACE}</m>      { return WHITESPACE; }
          <m>{STRING}</m>          { return STRING; }
          "("               { return PAREN1; }
          ")"               { return PAREN2; }
          "." / !<<EOF>>    { yybegin(QUALIFICATION); return DOT; }
          <c>[^]</c>               { return BAD_CHARACTER; }
      }
      """;
  }

  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return ContainerUtil.newHashMap(Arrays.asList("s", "m", "c"), Arrays.asList(STATE, MACRO, CLASS));
  }
}