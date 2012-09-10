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

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.ParserGenerator;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfFile;
import org.intellij.grammar.psi.BnfReferenceOrToken;
import org.intellij.grammar.psi.BnfRule;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author gregsh
 */
public class BnfDocumentationProvider implements DocumentationProvider {

  @Nullable
  public String getQuickNavigateInfo(final PsiElement element, PsiElement originalElement) {
    return null;
  }

  @Nullable
  public List<String> getUrlFor(final PsiElement element, final PsiElement originalElement) {
    return null;
  }

  @Nullable
  public String generateDoc(final PsiElement element, final PsiElement originalElement) {
    if (element instanceof BnfRule) {
      BnfRule rule = (BnfRule)element;
      BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
      Set<String> first = analyzer.asStrings(analyzer.calcFirst(rule));
      Set<String> next = analyzer.asStrings(analyzer.calcNext(rule).keySet());

      StringBuilder docBuilder = new StringBuilder();
      String[] firstS = first.toArray(new String[first.size()]);
      Arrays.sort(firstS);
      docBuilder.append("<h1>Starts with:</h1>").append(StringUtil.escapeXml(StringUtil.join(firstS, " | ")));

      String[] nextS = next.toArray(new String[next.size()]);
      Arrays.sort(nextS);
      docBuilder.append("<br><h1>Followed by:</h1>").append(StringUtil.escapeXml(StringUtil.join(nextS, " | ")));

      if (rule.getModifierList().isEmpty()) {
        Map<PsiElement,RuleGraphHelper.Cardinality> map = RuleGraphHelper.getCached((BnfFile)rule.getContainingFile()).getFor(rule);
        Collection<BnfRule> sortedPublicRules = ParserGeneratorUtil.getSortedPublicRules(map.keySet());
        if (sortedPublicRules.size() > 0) {
          docBuilder.append("<br><h1>Contains nodes:</h1>");
          for (BnfRule r : sortedPublicRules) {
            docBuilder.append(" ").append(r.getName()).append(RuleGraphHelper.getCardinalityText(map.get(r)));
          }
        }
        else {
          docBuilder.append("<h1>Contains no nodes</h1>");
        }
        Collection<BnfReferenceOrToken> sortedSimpleTokens = ParserGeneratorUtil.getSortedSimpleTokens(map.keySet(), null);
        if (sortedSimpleTokens.size() > 0) {
          docBuilder.append("<br><h1>Contains tokens:</h1>");
          for (BnfReferenceOrToken r : sortedSimpleTokens) {
            docBuilder.append(" ").append(r.getText()).append(RuleGraphHelper.getCardinalityText(map.get(r)));
          }
        }
        else {
          docBuilder.append("<h1>Contains no tokens</h1>");
        }
      }
      return docBuilder.toString();
    }
    else if (element instanceof BnfAttr) {
      KnownAttribute attribute = KnownAttribute.getAttribute(((BnfAttr)element).getName());
      if (attribute != null) return attribute.getDescription();
    }
    return null;
  }

  @Nullable
  public PsiElement getDocumentationElementForLookupItem(final PsiManager psiManager, final Object obj5ect, final PsiElement element) {
    return null;
  }

  @Nullable
  public PsiElement getDocumentationElementForLink(final PsiManager psiManager, final String link, final PsiElement context) {
    return null;
  }
}
