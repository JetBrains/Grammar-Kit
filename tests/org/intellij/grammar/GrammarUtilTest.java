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

import java.util.ArrayList;
import java.util.List;

public class GrammarUtilTest extends BasePlatformTestCase {

  public void testPrevSiblingDescendsIntoDummyBlock() {
    PsiFile file = configureChunkedGrammar(GeneratedParserUtilBase.MAX_CHILDREN_IN_TREE);
    PsiElement trailing = assertInstanceOf(file.getLastChild(), PsiWhiteSpace.class);
    // The parser builds the platform class; nothing instantiates GeneratedParserUtilBase.DummyBlock
    assertInstanceOf(trailing.getPrevSibling(), DummyBlockType.DummyBlock.class);

    PsiElement prev = GrammarUtil.getDummyAwarePrevSibling(trailing);

    assertEquals("r" + (GeneratedParserUtilBase.MAX_CHILDREN_IN_TREE - 1), assertInstanceOf(prev, BnfRule.class).getName());
  }

  public void testPrevSiblingCrossesChunkBoundaries() {
    int ruleCount = 2 * GeneratedParserUtilBase.MAX_CHILDREN_IN_TREE * GeneratedParserUtilBase.MAX_CHILDREN_IN_TREE;
    PsiFile file = configureChunkedGrammar(ruleCount);
    PsiElement trailing = assertInstanceOf(file.getLastChild(), PsiWhiteSpace.class);
    // Enough rules that chunks nest, so the walk below crosses both a plain and a nested chunk start
    assertInstanceOf(trailing.getPrevSibling().getLastChild(), DummyBlockType.DummyBlock.class);

    List<String> visited = new ArrayList<>();
    for (PsiElement cur = GrammarUtil.getDummyAwarePrevSibling(trailing); cur != null;
         cur = GrammarUtil.getDummyAwarePrevSibling(cur)) {
      if (cur instanceof BnfRule) visited.add(((BnfRule)cur).getName());
    }

    List<String> expected = new ArrayList<>();
    for (int i = ruleCount - 1; i >= 0; i--) expected.add("r" + i);
    assertEquals(expected, visited);
  }

  /** Configures a grammar of {@code ruleCount} rules, chunked into groups of {@link GeneratedParserUtilBase#MAX_CHILDREN_IN_TREE}. */
  private PsiFile configureChunkedGrammar(int ruleCount) {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < ruleCount; i++) {
      text.append("r").append(i).append(" ::= a;\n");
    }
    return myFixture.configureByText("a.bnf", text.toString());
  }
}
