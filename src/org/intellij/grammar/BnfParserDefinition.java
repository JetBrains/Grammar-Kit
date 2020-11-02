/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
  public static final IElementType BNF_LINE_COMMENT = BnfTypes.BNF_LINE_COMMENT;
  public static final IElementType BNF_BLOCK_COMMENT = BnfTypes.BNF_BLOCK_COMMENT;
  public static final TokenSet COMMENTS = TokenSet.create(BNF_LINE_COMMENT, BNF_BLOCK_COMMENT);
  public static final TokenSet LITERALS = TokenSet.create(BnfTypes.BNF_STRING);
  public static final TokenSet PARENS_L = TokenSet.create(
    BnfTypes.BNF_LEFT_PAREN, BnfTypes.BNF_LEFT_BRACE, BnfTypes.BNF_LEFT_BRACKET, BnfTypes.BNF_EXTERNAL_START);
  public static final TokenSet PARENS_R = TokenSet.create(
    BnfTypes.BNF_RIGHT_PAREN, BnfTypes.BNF_RIGHT_BRACE, BnfTypes.BNF_RIGHT_BRACKET, BnfTypes.BNF_EXTERNAL_END);
  public static final TokenSet PARENS = TokenSet.andSet(PARENS_L, PARENS_R);
  public static final TokenSet OPERATORS = TokenSet.create(
    BnfTypes.BNF_OP_AND, BnfTypes.BNF_OP_EQ, BnfTypes.BNF_OP_NOT, BnfTypes.BNF_OP_ONEMORE, BnfTypes.BNF_OP_OPT,
    BnfTypes.BNF_OP_OR, BnfTypes.BNF_OP_ZEROMORE);
  public static final TokenSet ATOMS = TokenSet.create(BnfTypes.BNF_ID, BnfTypes.BNF_NUMBER, BnfTypes.BNF_STRING);

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
    throw new UnsupportedOperationException(astNode.getElementType().toString());
  }

  @Override
  public PsiFile createFile(FileViewProvider fileViewProvider) {
    return new BnfFileImpl(fileViewProvider);
  }

  @Override
  public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode node1, ASTNode node2) {
    IElementType t1 = node1.getElementType();
    IElementType t2 = node2.getElementType();
    if (t1 == BnfTypes.BNF_LINE_COMMENT) return SpaceRequirements.MUST_LINE_BREAK;
    //if (ATOMS.contains(t1) && ATOMS.contains(t2)) return SpaceRequirements.MUST;
    return SpaceRequirements.MAY;
  }
}
