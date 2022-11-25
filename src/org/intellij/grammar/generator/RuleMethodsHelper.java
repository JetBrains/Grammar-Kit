/*
 * Copyright 2011-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.RuleGraphHelper.SealedHierarchyGraph;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static org.intellij.grammar.generator.ParserGeneratorUtil.*;
import static org.intellij.grammar.psi.BnfTypes.BNF_REFERENCE_OR_TOKEN;
import static org.intellij.grammar.psi.BnfTypes.BNF_STRING;

/**
* @author gregsh
*/
public class RuleMethodsHelper {
  private final RuleGraphHelper myGraphHelper;
  private final ExpressionHelper myExpressionHelper;
  private final Map<String, String> mySimpleTokens;
  private final GenOptions G;
  private final SealedHierarchyGraph mySealedRuleGraph;

  private final Map<BnfRule, Pair<Map<String, MethodInfo>, Collection<MethodInfo>>> myMethods;

  public RuleMethodsHelper(RuleGraphHelper ruleGraphHelper,
                           ExpressionHelper expressionHelper,
                           Map<String, String> simpleTokens,
                           GenOptions genOptions,
                           SealedHierarchyGraph sealedRuleGraph) {
    myGraphHelper = ruleGraphHelper;
    myExpressionHelper = expressionHelper;
    mySimpleTokens = Collections.unmodifiableMap(simpleTokens);
    G = genOptions;
    mySealedRuleGraph = sealedRuleGraph;

    myMethods = new LinkedHashMap<>();
  }

  public void buildMaps(Collection<BnfRule> sortedPsiRules) {
    Map<String, String> tokensReversed = RuleGraphHelper.computeTokens(myGraphHelper.getFile()).asMap();
    for (BnfRule rule : sortedPsiRules) {
      calcMethods(rule, tokensReversed);
    }
    for (BnfRule r0 : myGraphHelper.getRuleExtendsMap().keySet()) {
      if (!myMethods.containsKey(r0)) continue;
      Map<String, MethodInfo> p0 = myMethods.get(r0).first;
      for (BnfRule r : myGraphHelper.getRuleExtendsMap().get(r0)) {
        if (r0 == r) continue;
        if (!myMethods.containsKey(r)) continue;
        Map<String, MethodInfo> p = myMethods.get(r).first;
        for (String name : p.keySet()) {
          MethodInfo m0 = p0.get(name);
          if (m0 == null) continue;
          MethodInfo m = p.get(name);
          if (m0.cardinality != m.cardinality) continue;
          m.name = ""; // suppress super method duplication
        }
      }
    }
  }

  public @NotNull Collection<MethodInfo> getFor(@NotNull BnfRule rule, boolean isForInterface) {
    if (isForInterface) return myMethods.get(rule).second;
    return getIncludingAllMethodsOfSealedSuperRulesFor(rule);
  }

  @NotNull
  private List<MethodInfo> getIncludingAllMethodsOfSealedSuperRulesFor(@NotNull BnfRule rule) {
    return recurseUpSealedHierarchy(rule, new HashSet<>())
      .flatMap(it -> myMethods.get(it).second.stream())
      .toList();
  }

  public @NotNull Stream<BnfRule> recurseUpSealedHierarchy(@NotNull BnfRule rule, @NotNull HashSet<BnfRule> visited) {
    if (!visited.add(rule)) return Stream.empty();
    return Stream.concat(
      Stream.of(rule),
      mySealedRuleGraph.getSealedSuperRulesOf(rule).flatMap(superRule -> recurseUpSealedHierarchy(superRule, visited))
    );
  }

  public @Nullable MethodInfo getMethodInfo(@NotNull BnfRule rule, String name) {
    return myMethods.get(rule).first.get(name);
  }

  public @Nullable Collection<String> getMethodNames(@NotNull BnfRule rule) {
    return myMethods.get(rule).first.keySet();
  }

  protected void calcMethods(BnfRule rule, Map<String, String> tokensReversed) {
    List<MethodInfo> result = new ArrayList<>();

    Map<PsiElement, RuleGraphHelper.Cardinality> cardMap = myGraphHelper.getFor(rule);

    for (PsiElement element : cardMap.keySet()) {
      RuleGraphHelper.Cardinality c = myExpressionHelper.fixCardinality(rule, element, cardMap.get(element));
      String pathName = getRuleOrTokenNameForPsi(element, c);
      if (pathName == null) continue;
      if (element instanceof BnfRule) {
        BnfRule resultType = (BnfRule)element;
        if (!Rule.isPrivate(rule) && !mySealedRuleGraph.isValidSealedRule(rule)) {
          result.add(new MethodInfo(MethodType.RULE, pathName, pathName, resultType, c));
        }
      }
      else {
        result.add(new MethodInfo(MethodType.TOKEN, pathName, pathName, null, c));
      }
    }
    Collections.sort(result);

    BnfAttr attr = findAttribute(rule, KnownAttribute.GENERATE_TOKEN_ACCESSORS);
    boolean generateTokens = attr == null ? G.generateTokenAccessors :
                             Boolean.TRUE.equals(getAttributeValue(attr.getExpression()));
    boolean generateTokensSet = attr != null || G.generateTokenAccessorsSet;
    Map<String, MethodInfo> basicMethods = new LinkedHashMap<>();

    for (MethodInfo methodInfo : result) {
      basicMethods.put(methodInfo.name, methodInfo);
      if (methodInfo.type == MethodType.TOKEN) {
        boolean registered = tokensReversed.containsKey(methodInfo.name);
        String pattern = tokensReversed.get(methodInfo.name);
        // only regexp and lowercase tokens accessors are generated by default
        if (!(generateTokens || !generateTokensSet && registered && (pattern == null || isRegexpToken(pattern)))) {
          methodInfo.name = ""; // disable token
        }
      }
    }

    KnownAttribute.ListValue methods = getAttribute(rule, KnownAttribute.METHODS);
    for (Pair<String, String> pair : methods) {
      if (StringUtil.isEmpty(pair.first)) continue;
      MethodInfo methodInfo = basicMethods.get(pair.first);
      if (methodInfo != null) {
        methodInfo.name = ""; // suppress or user method override
      }
      if (StringUtil.isNotEmpty(pair.second)) {
        MethodInfo basicInfo = basicMethods.get(pair.second);
        if (basicInfo != null && (basicInfo.name.equals(pair.second) || basicInfo.name.isEmpty())) {
          basicInfo.name = pair.first; // simple rename, fix order anyway
          result.remove(basicInfo);
          result.add(basicInfo);
        }
        else {
          result.add(new MethodInfo(MethodType.USER, pair.first, pair.second, null, null));
        }
      }
      else if (methodInfo ==  null) {
        result.add(new MethodInfo(MethodType.MIXIN, pair.first, null, null, null));
      }
    }
    myMethods.put(rule, Pair.create(basicMethods, result));
  }

  private @Nullable String getRuleOrTokenNameForPsi(@NotNull PsiElement tree, @NotNull RuleGraphHelper.Cardinality type) {
    String result;

    if (!(tree instanceof BnfRule)) {
      if (type.many()) return null; // do not generate token lists

      IElementType effectiveType = getEffectiveType(tree);
      if (effectiveType == BNF_STRING) {
        result = mySimpleTokens.get(GrammarUtil.unquote(tree.getText()));
      }
      else if (effectiveType == BNF_REFERENCE_OR_TOKEN) {
        result = tree.getText();
      }
      else {
        result = null;
      }
    }
    else {
      BnfRule asRule = (BnfRule)tree;
      result = asRule.getName();
      if (StringUtil.isEmpty(getElementType(asRule, G.generateElementCase))) return null;
    }
    return result;
  }

  public enum MethodType { RULE, TOKEN, USER, MIXIN }
  public static class MethodInfo implements Comparable<MethodInfo> {
    final MethodType type;
    final String originalName;
    final String path;
    final BnfRule rule;
    final RuleGraphHelper.Cardinality cardinality;

    String name;

    private MethodInfo(MethodType type, String name, String path, BnfRule rule, RuleGraphHelper.Cardinality cardinality) {
      this.type = type;
      this.name = originalName = name;
      this.path = path;
      this.rule = rule;
      this.cardinality = cardinality;
    }

    @Override
    public int compareTo(@NotNull MethodInfo o) {
      if (type != o.type) return type.compareTo(o.type);
      return name.compareTo(o.name);
    }

    public @NotNull String generateGetterName() {
      boolean many = cardinality.many();

      boolean renamed = !Objects.equals(name, originalName);
      String getterNameBody = getGetterName(name);
      return getterNameBody + (many && !renamed ? "List" : "");
    }

    @Override
    public String toString() {
      return "MethodInfo{" +
             "type=" + type +
             ", name='" + name + '\'' +
             ", path='" + path + '\'' +
             ", rule=" + rule +
             ", cardinality=" + cardinality +
             '}';
    }
  }
}
