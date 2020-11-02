/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ArrayUtil;
import org.intellij.grammar.analysis.BnfFirstNextAnalyzer;
import org.intellij.grammar.generator.BnfConstants;
import org.intellij.grammar.generator.ExpressionHelper;
import org.intellij.grammar.generator.ParserGeneratorUtil;
import org.intellij.grammar.generator.RuleGraphHelper;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfExpression;
import org.intellij.grammar.psi.BnfFile;
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
      final BnfRule rule = (BnfRule)element;
      BnfFirstNextAnalyzer analyzer = new BnfFirstNextAnalyzer();
      Set<String> first = analyzer.asStrings(analyzer.calcFirst(rule));
      Set<String> next = analyzer.asStrings(analyzer.calcNext(rule).keySet());

      StringBuilder docBuilder = new StringBuilder();
      String[] firstS = first.toArray(ArrayUtil.EMPTY_STRING_ARRAY);
      Arrays.sort(firstS);
      docBuilder.append("<h1>Starts with:</h1>");
      docBuilder.append("<code>").append(StringUtil.escapeXmlEntities(StringUtil.join(firstS, " | "))).append("</code>");

      String[] nextS = next.toArray(ArrayUtil.EMPTY_STRING_ARRAY);
      Arrays.sort(nextS);
      docBuilder.append("<br><h1>Followed by:</h1>");
      docBuilder.append("<code>").append(StringUtil.escapeXmlEntities(StringUtil.join(nextS, " | "))).append("</code>");

      BnfFile file = (BnfFile)rule.getContainingFile();
      String recover = file.findAttributeValue(rule, KnownAttribute.RECOVER_WHILE, null);
      if (BnfConstants.RECOVER_AUTO.equals(recover)) {
        docBuilder.append("<br><h1>#auto recovery predicate:</h1>");
        docBuilder.append("<code>");
        docBuilder.append("private ").append(rule.getName()).append("_recover ::= !(");
        boolean f = true;
        for (String s : nextS) {
          if (s.startsWith("-") || s.startsWith("<")) continue;
          if (file.getRule(s) != null) continue;
          if (f) f = false;
          else docBuilder.append(" | ");
          docBuilder.append(StringUtil.escapeXmlEntities(s));
        }
        docBuilder.append(")");
        docBuilder.append("</code>");
      }
      dumpPriorityTable(docBuilder, rule, file);
      dumpContents(docBuilder, rule, file);
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

  private static void dumpContents(StringBuilder docBuilder, BnfRule rule, BnfFile file) {
    Map<PsiElement, RuleGraphHelper.Cardinality> map = RuleGraphHelper.getCached(file).getFor(rule);
    Collection<BnfRule> sortedPublicRules = ParserGeneratorUtil.getSortedPublicRules(map.keySet());
    Collection<BnfExpression> sortedTokens = ParserGeneratorUtil.getSortedTokens(map.keySet());
    Collection<LeafPsiElement> sortedExternalRules = ParserGeneratorUtil.getSortedExternalRules(map.keySet());
    if (sortedPublicRules.isEmpty() && sortedTokens.isEmpty()) {
      docBuilder.append("\n<br><h1>Contains no public rules and no tokens</h1>");
    }
    else {
      if (sortedPublicRules.size() > 0) {
        printElements(map, sortedPublicRules, docBuilder.append("\n<br><h1>Contains public rules:</h1>"));
      }
      else {
        docBuilder.append("<h2>Contains no public rules</h2>");
      }
      if (sortedTokens.size() > 0) {
        printElements(map, sortedTokens, docBuilder.append("\n<br><h1>Contains tokens:</h1>"));
      }
      else {
        docBuilder.append("<h2>Contains no tokens</h2>");
      }
    }
    if (!sortedExternalRules.isEmpty()) {
      printElements(map, sortedExternalRules, docBuilder.append("\n<br><h1>Contains external rules:</h1>"));
    }
  }

  private static void dumpPriorityTable(StringBuilder docBuilder, BnfRule rule, BnfFile file) {
    ExpressionHelper.ExpressionInfo expressionInfo = ExpressionHelper.getCached(file).getExpressionInfo(rule);
    if (expressionInfo == null) return;
    final ExpressionHelper.OperatorInfo ruleOperator = expressionInfo.operatorMap.get(rule);
    final int priority = expressionInfo.getPriority(rule);

    docBuilder.append("\n<br><h1>Priority table:");
    appendColored(docBuilder, String.format(" %s (%s)", ruleOperator != null ? ruleOperator.type : "priority group", priority));
    docBuilder.append("</h1>");
    expressionInfo.dumpPriorityTable(docBuilder.append("<code><pre>"), (sb, operatorInfo) -> {
      if (ruleOperator == operatorInfo ||
          ruleOperator == null && Comparing.equal(expressionInfo.getPriority(operatorInfo.rule), priority)) {
        appendColored(sb, operatorInfo);
      }
      else {
        sb.append(operatorInfo);
      }

    }).append("</pre></code>");
  }

  private static void appendColored(StringBuilder sb, Object o) {
    sb.append("<font").append(" color=\"#").append(ColorUtil.toHex(JBColor.BLUE)).append("\">");
    sb.append(o);
    sb.append("</font>");
  }

  public static void printElements(Map<PsiElement, RuleGraphHelper.Cardinality> map,
                                   Collection<? extends PsiElement> collection,
                                   StringBuilder sb) {
    for (PsiElement r : collection) {
      String text = r instanceof PsiNamedElement ? ((PsiNamedElement)r).getName() : r.getText();
      sb.append(" ");
      sb.append(StringUtil.escapeXmlEntities(StringUtil.notNullize(text, "?")));
      sb.append(RuleGraphHelper.getCardinalityText(map.get(r)));
    }
  }
}
