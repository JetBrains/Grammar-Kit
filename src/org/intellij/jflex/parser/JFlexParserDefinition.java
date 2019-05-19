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

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.jflex.JFlexLanguage;
import org.intellij.jflex.psi.JFlexTokenType;
import org.intellij.jflex.psi.JFlexTypes;
import org.intellij.jflex.psi.impl.JFlexFileImpl;
import org.jetbrains.annotations.NotNull;

import static org.intellij.jflex.psi.JFlexTypes.*;

/**
 * @author gregsh
 */
public class JFlexParserDefinition implements ParserDefinition {

  public static final IFileElementType FILE_NODE_TYPE = new IFileElementType("JFLEX_FILE", JFlexLanguage.INSTANCE);
  public static final IElementType FLEX_NEWLINE = new JFlexTokenType("newline");
  public static final TokenSet WS = TokenSet.create(TokenType.WHITE_SPACE, FLEX_NEWLINE);
  public static final TokenSet COMMENTS = TokenSet.create(FLEX_LINE_COMMENT, FLEX_BLOCK_COMMENT);
  public static final TokenSet LITERALS = TokenSet.create(JFlexTypes.FLEX_STRING);
  public static final TokenSet CHAR_CLASS_OPERATORS = TokenSet.create(FLEX_AMPAMP, FLEX_BARBAR, FLEX_DASHDASH, FLEX_HAT, FLEX_TILDETILDE);
  public static final TokenSet PATTERN_OPERATORS = TokenSet.create(FLEX_BAR, FLEX_BANG, FLEX_DOLLAR, FLEX_PLUS, FLEX_QUESTION, FLEX_STAR, FLEX_TILDE);

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new JFlexLexer();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new JFlexParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return FILE_NODE_TYPE;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens() {
    return WS;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return LITERALS;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode astNode) {
    throw new UnsupportedOperationException(astNode.getElementType().toString());
  }

  @Override
  public PsiFile createFile(FileViewProvider fileViewProvider) {
    return new JFlexFileImpl(fileViewProvider);
  }

  @Override
  public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
    return SpaceRequirements.MAY;
  }
  
}
