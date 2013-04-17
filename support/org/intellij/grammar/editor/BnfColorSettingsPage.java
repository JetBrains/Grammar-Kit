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

package org.intellij.grammar.editor;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import gnu.trove.THashMap;
import org.intellij.grammar.BnfIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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
      new AttributesDescriptor("Pattern", PATTERN),
      new AttributesDescriptor("External", EXTERNAL),
      new AttributesDescriptor("Parenthesis", PARENTHS),
      new AttributesDescriptor("Braces", BRACES),
      new AttributesDescriptor("Brackets", BRACKETS),
      new AttributesDescriptor("Angles", ANGLES),
      new AttributesDescriptor("Operation sign", OP_SIGN),
      new AttributesDescriptor("Pin marker", PIN),
    };
  }

  @NotNull
  public String getDisplayName() {
    return "Grammar";
  }

  public Icon getIcon() {
    return BnfIcons.FILE;
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
    return new BnfSyntaxHighlighter();
  }

  @NotNull
  public String getDemoText() {
    return "/*\n" +
           " * Sample grammar\n" +
           " */\n" +
           "{\n" +
           "  <a>generatePsi</a>=<k>false</k>\n" +
           "  <a>classHeader</a>=<pa>\"header.txt\"</pa>\n" +
           "  <a>parserClass</a>=<pa>\"org.MyParser\"</pa>\n" +
           "  <a>pin</a>(<pa>\".*_list(?:_\\d.*)?\"</pa>)=1\n" +
           "  <a>tokens</a>=[\n" +
           "    <a>COMMA</a>=<pa>\",\"</pa>\n" +
           "    <a>LEFT_PAREN</a>=<pa>\"(\"</pa>\n" +
           "    <a>RIGHT_PAREN</a>=<pa>\")\"</pa>\n" +
           "  ]\n" +
           "}\n" +
           "// Grammar rules\n" +
           "<r>root</r> ::= <r>header</r> <r>content</r>\n" +
           "<r>header</r> ::= <t>DECLARE</t> <r>reference</r>\n" +
           "<k>external</k> <r>reference</r> ::= <e>parseReference</e>\n" +
           "<k>private</k> <k>meta</k> <r>comma_list</r> ::= <p><s>'('</s></p> <<<e>p</e>>> (<p><s>','</s></p> <<<e>p</e>>>) * ')'\n" +
           "<k>private</k> <r>content</r> ::= <p><t>AS</t></p> <<<r>comma_list</r> <r>element</r>>> {<a>pin</a>=1}\n" +
           "<r>element</r> ::= <r>reference</r> [ {<pa>'+'</pa> | <pa>'-'</pa>} <r>reference</r> <t>ONLY</t>?] {<a>recoverUntil</a>=<r>element_recover</r>}\n" +
           "<k>private</k> <r>element_recover</r> ::= !(',' | ')')\n" +
           "\n";
  }

  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    @NonNls
    final Map<String, TextAttributesKey> map = new THashMap<String, TextAttributesKey>();
    map.put("r", RULE);
    map.put("a", ATTRIBUTE);
    map.put("pa", PATTERN);
    map.put("t", TOKEN);
    map.put("k", KEYWORD);
    map.put("e", EXTERNAL);
    map.put("p", PIN);
    map.put("s", STRING);
    return map;
  }

}