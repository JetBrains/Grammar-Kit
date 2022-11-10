/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.editor;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.intellij.grammar.BnfIcons;
import org.intellij.grammar.GrammarKitBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static org.intellij.grammar.editor.BnfSyntaxHighlighter.*;

/**
 * @author gregsh
 */
public class BnfColorSettingsPage implements ColorSettingsPage {
  private static final AttributesDescriptor[] ATTRS;

  static {
    ATTRS = new AttributesDescriptor[]{
      new AttributesDescriptor("Illegal character", ILLEGAL),
      new AttributesDescriptor("Comment", COMMENT),
      new AttributesDescriptor("String", STRING),
      new AttributesDescriptor("Number", NUMBER),
      new AttributesDescriptor("Keyword", KEYWORD),
      new AttributesDescriptor("Token", TOKEN),
      new AttributesDescriptor("Rule", RULE),
      new AttributesDescriptor("Attribute", ATTRIBUTE),
      new AttributesDescriptor("Meta rule", META_RULE),
      new AttributesDescriptor("Meta rule parameter", META_PARAM),
      new AttributesDescriptor("Pattern", PATTERN),
      new AttributesDescriptor("External", EXTERNAL),
      new AttributesDescriptor("Parenthesis", PARENTHS),
      new AttributesDescriptor("Braces", BRACES),
      new AttributesDescriptor("Brackets", BRACKETS),
      new AttributesDescriptor("Angles", ANGLES),
      new AttributesDescriptor("Operation sign", OP_SIGN),
      new AttributesDescriptor("Pin marker", PIN_MARKER),
      new AttributesDescriptor("Recover marker", RECOVER_MARKER),
    };
  }

  @Override
  public @NotNull String getDisplayName() {
    return GrammarKitBundle.message("language.name.bnf");
  }

  @Override
  public Icon getIcon() {
    return BnfIcons.FILE;
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
    return new BnfSyntaxHighlighter();
  }

  @Override
  public @NotNull String getDemoText() {
    return """
      /*
       * Sample grammar
       */
      {
        <a>generatePsi</a>=<k>false</k>
        <a>classHeader</a>=<pa>"header.txt"</pa>
        <a>parserClass</a>=<pa>"org.MyParser"</pa>
        <a>pin</a>(<pa>".*_list(?:_\\d.*)?"</pa>)=1
        <a>tokens</a>=[
          <a>COMMA</a>=<pa>","</pa>
          <a>LEFT_PAREN</a>=<pa>"("</pa>
          <a>RIGHT_PAREN</a>=<pa>")"</pa>
        ]
      }
      // Grammar rules
      <r>root</r> ::= <r>header</r> <r>content</r>
      <r>header</r> ::= <t>DECLARE</t> <r>reference</r>
      <k>external</k> <r>reference</r> ::= <e>parseReference</e>
      <k>private</k> <k>meta</k> <mr>comma_list</mr> ::= <pin><s>'('</s></pin> <mp><<p>></mp> (<pin><s>','</s></pin> <mp><<p>></mp>) * ')'
      <k>private</k> <r>content</r> ::= <pin><t>AS</t></pin> <<<mr>comma_list</mr> <ru><r>element</r></ru>>> {<a>pin</a>=1}
      <ru><r>element</r></ru> ::= <r>reference</r> [ {<pa>'+'</pa> | <pa>'-'</pa>} <r>reference</r> <t>ONLY</t>?] {<a>recoverWhile</a>=<r>element_recover</r>}
      <k>private</k> <r>element_recover</r> ::= !(',' | ')')

      """;
  }

  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    @NonNls
    Map<String, TextAttributesKey> map = new HashMap<>();
    map.put("r", RULE);
    map.put("mr", META_RULE);
    map.put("a", ATTRIBUTE);
    map.put("pa", PATTERN);
    map.put("t", TOKEN);
    map.put("k", KEYWORD);
    map.put("e", EXTERNAL);
    map.put("pin", PIN_MARKER);
    map.put("s", STRING);
    map.put("ru", RECOVER_MARKER);
    map.put("mp", META_PARAM);
    return map;
  }

}