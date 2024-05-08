/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.jflex.JFlexLanguage;
import org.intellij.jflex.psi.JFlexTokenSets;
import org.intellij.jflex.psi.impl.JFlexFileImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author gregsh
 */
final class JFlexParserDefinition implements ParserDefinition {

  private static final IFileElementType FILE_NODE_TYPE = new IFileElementType("JFLEX_FILE", JFlexLanguage.INSTANCE);

  @Override
  public @NotNull Lexer createLexer(Project project) {
    return new JFlexLexer();
  }

  @Override
  public @NotNull PsiParser createParser(Project project) {
    return new JFlexParser();
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return FILE_NODE_TYPE;
  }

  @Override
  public @NotNull TokenSet getWhitespaceTokens() {
    return JFlexTokenSets.WS;
  }

  @Override
  public @NotNull TokenSet getCommentTokens() {
    return JFlexTokenSets.COMMENTS;
  }

  @Override
  public @NotNull TokenSet getStringLiteralElements() {
    return JFlexTokenSets.LITERALS;
  }

  @Override
  public @NotNull PsiElement createElement(ASTNode astNode) {
    throw new UnsupportedOperationException(astNode.getElementType().toString());
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
    return new JFlexFileImpl(fileViewProvider);
  }

  @Override
  public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
    return SpaceRequirements.MAY;
  }
}
