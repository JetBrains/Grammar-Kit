/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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

  private static final TokenSet ourWhiteSpaceTokens = TokenSet.WHITE_SPACE;
  private static final TokenSet ourCommentTokens = TokenSet.create(COMMENT);
  private static final TokenSet ourStringLiteralElements = TokenSet.create(STRING);

  private final LivePreviewLanguage myLanguage;
  private final IFileElementType myFileElementType;

  public LivePreviewParserDefinition(LivePreviewLanguage language) {
    myLanguage = language;
    myFileElementType = new IFileElementType(myLanguage); // todo do not register
  }

  @Override
  public @NotNull Lexer createLexer(Project project) {
    return new LivePreviewLexer(project, myLanguage);
  }

  @Override
  public @NotNull PsiParser createParser(Project project) {
    return new LivePreviewParser(project, myLanguage);
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return myFileElementType;
  }

  @Override
  public @NotNull TokenSet getWhitespaceTokens() {
    return ourWhiteSpaceTokens;
  }

  @Override
  public @NotNull TokenSet getCommentTokens() {
    return ourCommentTokens;
  }

  @Override
  public @NotNull TokenSet getStringLiteralElements() {
    return ourStringLiteralElements;
  }

  @Override
  public @NotNull PsiElement createElement(ASTNode node) {
    return new ASTWrapperPsiElement(node);
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new PsiFileBase(viewProvider, myLanguage) {
      @Override
      public @NotNull FileType getFileType() {
        return LivePreviewFileType.INSTANCE;
      }
    };
  }

  @Override
  public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }
}
