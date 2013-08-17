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
    return !builder.eof() && builder.getOriginalText().charAt(builder.getCurrentOffset()) == '%';
  }

  public static boolean is_new_line(PsiBuilder builder, int level) {
    for (int i=-1; ; i++) {
      IElementType type = builder.rawLookup(i);
      if (type == TokenType.WHITE_SPACE) continue;
      return type == JFlexTypes.FLEX_NEWLINE || type == null;
    }
  }


  public static boolean consumeToken(PsiBuilder builder_, IElementType token) {
    if (ErrorState.get(builder_).completionState != null) {
      return GeneratedParserUtilBase.consumeToken(builder_, token);
    }
    else return GeneratedParserUtilBase.consumeTokenFast(builder_, token);
  }

  public static boolean nextTokenIs(PsiBuilder builder_, IElementType token) {
    if (ErrorState.get(builder_).completionState != null) {
      return GeneratedParserUtilBase.nextTokenIs(builder_, token);
    }
    else return GeneratedParserUtilBase.nextTokenIsFast(builder_, token);
  }

  public static boolean replaceVariants(PsiBuilder builder_, int variantCount, String frameName) {
    if (ErrorState.get(builder_).completionState != null) {
      return GeneratedParserUtilBase.replaceVariants(builder_, variantCount, frameName);
    }
    return true;
  }
}
