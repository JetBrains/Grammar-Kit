/*
 * Copyright 2000-2011 Gregory Shrago
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
package org.intellij.grammar;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.parser.BnfLexer;
import org.intellij.grammar.psi.BnfTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 14.07.11
 * Time: 13:25
 */
public class BnfSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  @NotNull
  @Override
  public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
    return new MyHighlighter();
  }

  private class MyHighlighter extends SyntaxHighlighterBase {
    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
      return new BnfLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
      if (iElementType == TokenType.BAD_CHARACTER) {
        return pack(SyntaxHighlighterColors.INVALID_STRING_ESCAPE);
      }
      else if (iElementType == BnfParserDefinition.BNF_LINE_COMMENT || iElementType == BnfParserDefinition.BNF_BLOCK_COMMENT) {
        return pack(SyntaxHighlighterColors.LINE_COMMENT);
      }
      else if (iElementType == BnfTypes.BNF_STRING) {
        return pack(SyntaxHighlighterColors.STRING);
      }
      else if (iElementType == BnfTypes.BNF_NUMBER) {
        return pack(SyntaxHighlighterColors.NUMBER);
      }
      return EMPTY;
    }
  }
}
