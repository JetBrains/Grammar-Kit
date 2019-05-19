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

package org.intellij.grammar.livePreview;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
public class LivePreviewParserDefinition implements ParserDefinition {
  public static final IElementType COMMENT = new IElementType("COMMENT", LivePreviewLanguage.BASE_INSTANCE);
  public static final IElementType STRING = new IElementType("STRING", LivePreviewLanguage.BASE_INSTANCE);
  public static final IElementType NUMBER = new IElementType("NUMBER", LivePreviewLanguage.BASE_INSTANCE);
  public static final IElementType KEYWORD = new IElementType("KEYWORD", LivePreviewLanguage.BASE_INSTANCE);

  private static final TokenSet ourWhiteSpaceTokens = TokenSet.create(TokenType.WHITE_SPACE);
  private static final TokenSet ourCommentTokens = TokenSet.create(COMMENT);
  private static final TokenSet ourStringLiteralElements = TokenSet.create(STRING);

  private final LivePreviewLanguage myLanguage;
  private final IFileElementType myFileElementType;

  public LivePreviewParserDefinition(LivePreviewLanguage language) {
    myLanguage = language;
    myFileElementType = new IFileElementType(myLanguage); // todo do not register
  }

  public LivePreviewLanguage getLanguage() {
    return myLanguage;
  }

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new LivePreviewLexer(project, myLanguage);
  }

  @Override
  public PsiParser createParser(Project project) {
    return new LivePreviewParser(project, myLanguage);
  }

  @Override
  public IFileElementType getFileNodeType() {
    return myFileElementType;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens() {
    return ourWhiteSpaceTokens;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return ourCommentTokens;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return ourStringLiteralElements;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return new ASTWrapperPsiElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new PsiFileBase(viewProvider, myLanguage) {
      @NotNull
      @Override
      public FileType getFileType() {
        return LivePreviewFileType.INSTANCE;
      }
    };
  }

  @Override
  public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }
}
