/*
 * Copyright 2011-2013 Gregory Shrago
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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfRule;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author gregsh
 */
public class BnfRuleGraphTest extends BasePlatformTestCase {

  public void testSOEInPrivateMeta() { doTest("private r ::= A <<r>> external A::=", "A+"); }
  public void testSOEInPrivate() { doTest("private r ::= A r external A::=", "A+"); }

  private void doTest(String text, String... expected) {
    BnfFile f = (BnfFile)myFixture.configureByText("a.bnf", text);
    List<BnfRule> rules = f.getRules();
    assertFalse(rules.isEmpty());
    RuleGraphHelper helper = new RuleGraphHelper(f);
    Map<PsiElement,RuleGraphHelper.Cardinality> map = helper.getFor(rules.get(0));
    Collection<BnfRule> sortedPublicRules = ParserGeneratorUtil.getSortedPublicRules(map.keySet());
    Collection<BnfExpression> sortedTokens = ParserGeneratorUtil.getSortedTokens(map.keySet());
    Collection<LeafPsiElement> sortedExternalRules = ParserGeneratorUtil.getSortedExternalRules(map.keySet());
    StringBuilder sb = new StringBuilder();
    BnfDocumentationProvider.printElements(map, sortedPublicRules, sb);
    BnfDocumentationProvider.printElements(map, sortedTokens, sb);
    BnfDocumentationProvider.printElements(map, sortedExternalRules, sb);
    String output = sb.toString().trim().replace(' ', '\n');
    assertEquals(StringUtil.join(expected, "\n"), output);
  }
}
