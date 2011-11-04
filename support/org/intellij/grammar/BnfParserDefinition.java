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
package org.intellij.grammar;

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
import org.intellij.grammar.parser.BnfLexer;
import org.intellij.grammar.parser.GrammarParser;
import org.intellij.grammar.psi.BnfTokenType;
import org.intellij.grammar.psi.BnfTypes;
import org.intellij.grammar.psi.impl.BnfFileImpl;
import org.jetbrains.annotations.NotNull;

/**
 * User: gregory
 * Date: 13.07.11
 * Time: 22:43
 */
public class BnfParserDefinition implements ParserDefinition {

  public static final IFileElementType BNF_FILE_ELEMENT_TYPE = new IFileElementType("BNF_FILE", BnfLanguage.INSTANCE);
  public static final TokenSet WS = TokenSet.create(TokenType.WHITE_SPACE);
  public static final IElementType BNF_LINE_COMMENT = new BnfTokenType("BNF_LINE_COMMENT");
  public static final IElementType BNF_BLOCK_COMMENT = new BnfTokenType("BNF_BLOCK_COMMENT");
  public static final TokenSet COMMENTS = TokenSet.create(BNF_LINE_COMMENT, BNF_BLOCK_COMMENT);
  public static final TokenSet LITERALS = TokenSet.create(BnfTypes.BNF_STRING);

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new BnfLexer();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new GrammarParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return BNF_FILE_ELEMENT_TYPE;
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
    return BnfTypes.Factory.createElement(astNode);
  }

  @Override
  public PsiFile createFile(FileViewProvider fileViewProvider) {
    return new BnfFileImpl(fileViewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
    return SpaceRequirements.MAY;
  }
}
