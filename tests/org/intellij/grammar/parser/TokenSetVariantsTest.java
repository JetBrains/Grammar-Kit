/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import org.intellij.grammar.expression.ExpressionParserDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static org.intellij.grammar.expression.ExpressionTypes.ID;
import static org.intellij.grammar.expression.ExpressionTypes.NUMBER;
import static org.intellij.grammar.expression.ExpressionTypes.STRING;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.TRUE_CONDITION;
import static org.intellij.grammar.parser.GeneratedParserUtilBase._NONE_;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.adapt_builder_;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.consumeToken;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.enter_section_;
import static org.intellij.grammar.parser.GeneratedParserUtilBase.exit_section_;

/**
 * Contract of {@link GeneratedParserUtilBase#consumeToken(PsiBuilder, TokenSet)}: it consumes one token of the set, and
 * registers every token of the set as a separate expected variant so that the error message names them.
 *
 * <p>Registering the set as a single variant instead renders the {@code IElementType[]} through {@code toString}, which
 * produces an unreadable and unstable {@code [Lcom...IElementType;@<hash>} (GO-20621).
 */
public class TokenSetVariantsTest extends ParsingTestCase {

  private static final TokenSet TOKENS = TokenSet.create(ID, NUMBER, STRING);

  public TokenSetVariantsTest() {
    super("", "expr", new TokenSetParserDefinition());
  }

  public void testExpectedVariantsNameEveryTokenOfTheSet() {
    // "&" lexes to a token outside TOKENS, so the whole set is reported as expected, sorted and joined with "or".
    assertEquals("id, number or string expected, got '&'", errorMessage("&"));
  }

  public void testExpectedVariantsAreNotARawArray() {
    assertFalse(errorMessage("&").contains("IElementType"));
  }

  public void testNoErrorWhenSetMatches() {
    assertEmpty(errors(parse("abc")));
  }

  public void testAnyTokenOfTheSetMatches() {
    assertEmpty(errors(parse("42")));
    assertEmpty(errors(parse("'text'")));
  }

  private @NotNull String errorMessage(@NotNull String text) {
    Collection<PsiErrorElement> errors = errors(parse(text));
    assertSize(1, errors);
    return errors.iterator().next().getErrorDescription();
  }

  private @NotNull PsiFile parse(@NotNull String text) {
    return createPsiFile("a", text);
  }

  private static @NotNull Collection<PsiErrorElement> errors(@NotNull PsiFile file) {
    return PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class);
  }

  @Override
  protected boolean checkAllPsiRoots() {
    return false;
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  /**
   * Reuses the expression lexer and token types, and swaps in a parser whose whole grammar is one token-set consume.
   */
  private static class TokenSetParserDefinition extends ExpressionParserDefinition {
    @Override
    public @NotNull PsiParser createParser(Project project) {
      return new PsiParser() {
        @Override
        public @NotNull ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
          builder = adapt_builder_(root, builder, this, null);
          PsiBuilder.Marker marker = enter_section_(builder, 0, _NONE_, null);
          boolean result = consumeToken(builder, TOKENS);
          exit_section_(builder, 0, marker, root, result, true, TRUE_CONDITION);
          return builder.getTreeBuilt();
        }
      };
    }
  }
}
