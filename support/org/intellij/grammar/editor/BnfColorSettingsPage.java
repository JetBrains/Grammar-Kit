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
        new AttributesDescriptor("illegal character", ILLEGAL),
        new AttributesDescriptor("comment", COMMENT),
        new AttributesDescriptor("string", STRING),
        new AttributesDescriptor("number", NUMBER),
        new AttributesDescriptor("keyword", KEYWORD),
        new AttributesDescriptor("token", TOKEN),
        new AttributesDescriptor("rule", RULE),
        new AttributesDescriptor("attribute", ATTRIBUTE),
        new AttributesDescriptor("external", EXTERNAL),
        new AttributesDescriptor("parenthesis", PARENTHS),
        new AttributesDescriptor("braces", BRACES),
        new AttributesDescriptor("brackets", BRACKETS),
        new AttributesDescriptor("angles", ANGLES),
        new AttributesDescriptor("operation sign", OP_SIGN),
        new AttributesDescriptor("pin marker", PIN),
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
           "  <a>classHeader</a>=\"header.txt\"\n" +
           "  <a>parserClass</a>=\"org.MyParser\"\n" +
           "  <a>pin</a>(\".*_list(?:_\\d.*)?\")=1\n" +
           "  <a>COMMA</a>=\",\"\n" +
           "  <a>LEFT_PAREN</a>=\"(\"\n" +
           "  <a>RIGHT_PAREN</a>=\")\"\n" +
           "}\n" +
           "// Grammar rules\n" +
           "<r>root</r> ::= <r>header</r> <r>content</r>\n" +
           "<r>header</r> ::= <t>declare</t> <r>reference</r>\n" +
           "<k>external</k> <r>reference</r> ::= <e>parseReference</e>\n" +
           "<k>private</k> <k>meta</k> <r>comma_list</r> ::= '(' <<<e>p</e>>> (',' <<<e>p</e>>>) * ')'\n" +
           "<k>private</k> <r>content</r> ::= <p><t>as</t></p> <<<r>comma_list</r> <r>element</r>>> {<a>pin</a>=1}\n" +
           "<r>element</r> ::= <r>reference</r> [ {'+' | '-'} <r>reference</r> <t>only</t>?] {<a>recoverUntil</a>=\"<r>element_recover</r>\"}\n" +
           "<k>private</k> <r>element_recover</r> ::= !(',' | ')')\n" +
           "\n";
  }

  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    @NonNls
    final Map<String, TextAttributesKey> map = new THashMap<String, TextAttributesKey>();
    map.put("r", RULE);
    map.put("a", ATTRIBUTE);
    map.put("t", TOKEN);
    map.put("k", KEYWORD);
    map.put("e", EXTERNAL);
    map.put("p", PIN);
    return map;
  }

}