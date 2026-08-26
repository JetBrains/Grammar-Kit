/*
 * Copyright 2011-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.psi.DummyBlockType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;

public class GrammarUtilTest extends BasePlatformTestCase {

  public void testPrevSiblingDescendsIntoDummyBlock() {
    PsiFile file = configureChunkedGrammar();
    PsiElement trailing = file.getLastChild();
    assertInstanceOf(trailing, PsiWhiteSpace.class);
    // The parser builds the platform class; nothing instantiates GeneratedParserUtilBase.DummyBlock
    assertInstanceOf(trailing.getPrevSibling(), DummyBlockType.DummyBlock.class);

    PsiElement prev = GrammarUtil.getDummyAwarePrevSibling(trailing);

    assertEquals("r" + (GeneratedParserUtilBase.MAX_CHILDREN_IN_TREE - 1), assertInstanceOf(prev, BnfRule.class).getName());
  }

  /** Configures a grammar of exactly one chunk, so every top-level rule sits inside a {@code DUMMY_BLOCK}. */
  private PsiFile configureChunkedGrammar() {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < GeneratedParserUtilBase.MAX_CHILDREN_IN_TREE; i++) {
      text.append("r").append(i).append(" ::= a;\n");
    }
    return myFixture.configureByText("a.bnf", text.toString());
  }
}
