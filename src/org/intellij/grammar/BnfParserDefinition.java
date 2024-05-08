/*
 * Copyright 2011-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.grammar.parser.BnfLexer;
import org.intellij.grammar.parser.GrammarParser;
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

  @Override
  public @NotNull Lexer createLexer(Project project) {
    return new BnfLexer();
  }

  @Override
  public @NotNull PsiParser createParser(Project project) {
    return new GrammarParser();
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return BNF_FILE_ELEMENT_TYPE;
  }

  @Override
  public @NotNull TokenSet getWhitespaceTokens() {
    return BnfTokenSets.WS;
  }

  @Override
  public @NotNull TokenSet getCommentTokens() {
    return BnfTokenSets.COMMENTS;
  }

  @Override
  public @NotNull TokenSet getStringLiteralElements() {
    return BnfTokenSets.LITERALS;
  }

  @Override
  public @NotNull PsiElement createElement(ASTNode astNode) {
    throw new UnsupportedOperationException(astNode.getElementType().toString());
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
    return new BnfFileImpl(fileViewProvider);
  }

  @Override
  public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode node1, ASTNode node2) {
    IElementType t1 = node1.getElementType();
    if (t1 == BnfTypes.BNF_LINE_COMMENT) return SpaceRequirements.MUST_LINE_BREAK;
    return SpaceRequirements.MAY;
  }
}
