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

package org.intellij.jflex.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.jflex.editor.JFlexBraceMatcher;
import org.intellij.jflex.psi.JFlexTypes;

/**
 * @author gregsh
 */
public class JFlexParserUtil extends GeneratedParserUtilBase {
  public static PsiBuilder adapt_builder_(IElementType root, PsiBuilder builder, PsiParser parser, TokenSet[] extendsSets) {
    PsiBuilder result = GeneratedParserUtilBase.adapt_builder_(root, builder, parser, extendsSets);
    ErrorState.get(result).braces = new JFlexBraceMatcher().getPairs();
    ErrorState.get(result).altMode = true;
    return result;
  }


  public static boolean anything(PsiBuilder builder, int level, GeneratedParserUtilBase.Parser condition) {
    parseAsTree(GeneratedParserUtilBase.ErrorState.get(builder), builder, level + 1, DUMMY_BLOCK, true, TOKEN_ADVANCER, condition);
    return true;
  }

  public static boolean anything2(PsiBuilder builder, int level, GeneratedParserUtilBase.Parser condition) {
    parseAsTree(GeneratedParserUtilBase.ErrorState.get(builder), builder, level + 1, DUMMY_BLOCK, false, TOKEN_ADVANCER, condition);
    return true;
  }

  public static boolean is_percent(PsiBuilder builder, int level) {
    IElementType tokenType = builder.getTokenType();
    return tokenType != null && tokenType.toString().startsWith("%");
  }

  public static boolean is_new_line(PsiBuilder builder, int level) {
    if (builder.eof()) return true;
    for (int i=-1; ; i--) {
      IElementType type = builder.rawLookup(i);
      if (type == TokenType.WHITE_SPACE) continue;
      if (type == JFlexTypes.FLEX_LINE_COMMENT || type == JFlexTypes.FLEX_BLOCK_COMMENT) continue;
      if (type == JFlexParserDefinition.FLEX_NEWLINE || type == null) return true;
      if (builder.getOriginalText().charAt(builder.rawTokenTypeStart(i+1)-1) == '\n') return true;
      addVariant(builder, "<new-line>");
      return false;
    }
  }

}
