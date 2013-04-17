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

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Getter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.popup.AbstractPopup;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.*;
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
      final BnfRule rule = (BnfRule)element;
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

      final String prefix = docBuilder.toString();
      if (!ParserGeneratorUtil.Rule.isMeta(rule)) {
        updateDocPopup(rule, new Getter<String>() {
          @Override
          public String get() {
            AccessToken accessToken = ApplicationManager.getApplication().acquireReadActionLock();
            try {
              return getUpdatedDocumentation(rule, prefix);
            }
            finally {
              accessToken.finish();
            }
          }
        });
      }
      return prefix;
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

  private static String getUpdatedDocumentation(BnfRule rule, String prefix) {
    StringBuilder docBuilder = new StringBuilder(prefix);
    BnfFile file = (BnfFile)rule.getContainingFile();
    Map<PsiElement, RuleGraphHelper.Cardinality> map = RuleGraphHelper.getCached(file).getFor(rule);
    Collection<BnfRule> sortedPublicRules = ParserGeneratorUtil.getSortedPublicRules(map.keySet());
    Collection<BnfExpression> sortedTokens = ParserGeneratorUtil.getSortedTokens(map.keySet());
    Collection<LeafPsiElement> sortedExternalRules = ParserGeneratorUtil.getSortedExternalRules(map.keySet());
    if (sortedPublicRules.isEmpty() && sortedTokens.isEmpty()) {
      docBuilder.append("<br><h1>Contains no public rules and no tokens</h1>");
    }
    else {
      if (sortedPublicRules.size() > 0) {
        docBuilder.append("<br><h1>Contains public rules:</h1>");
        for (BnfRule r : sortedPublicRules) {
          docBuilder.append(" ").append(r.getName()).append(RuleGraphHelper.getCardinalityText(map.get(r)));
        }
      }
      else {
        docBuilder.append("<h2>Contains no public rules</h2>");
      }
      if (sortedTokens.size() > 0) {
        docBuilder.append("<h1>Contains tokens:</h1>");
        for (BnfExpression r : sortedTokens) {
          docBuilder.append(" ").append(r.getText()).append(RuleGraphHelper.getCardinalityText(map.get(r)));
        }
      }
      else {
        docBuilder.append("<h2>Contains no tokens</h2>");
      }
    }
    if (!sortedExternalRules.isEmpty()) {
      docBuilder.append("<br><h1>Contains external rules:</h1>");
      for (LeafPsiElement r : sortedExternalRules) {
        docBuilder.append(" ").append(r.getText()).append(RuleGraphHelper.getCardinalityText(map.get(r)));
      }
    }
    return docBuilder.toString();
  }

  public static void updateDocPopup(final PsiElement element, final Getter<String> docGetter) {
    final Project project = element.getProject();
    ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
      @Override
      public void run() {
        final String documentation = docGetter.get();
        if (StringUtil.isEmpty(documentation)) return;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
          @Override
          public void run() {
            DocumentationManager documentationManager = DocumentationManager.getInstance(project);
            DocumentationComponent component;
            JBPopup hint = documentationManager.getDocInfoHint();
            if (hint != null) {
              component = (DocumentationComponent)((AbstractPopup)hint).getComponent();
            }
            else if (documentationManager.hasActiveDockedDocWindow()) {
              ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.DOCUMENTATION);
              Content selectedContent = toolWindow == null? null : toolWindow.getContentManager().getSelectedContent();
              component = selectedContent == null ? null : (DocumentationComponent)selectedContent.getComponent();
            }
            else {
              component = null;
            }
            PsiElement docElement = component == null? null : component.getElement();
            if (docElement == element) {
              component.setText(documentation, element, false);
            }
          }
        });
      }
    });
  }
}
