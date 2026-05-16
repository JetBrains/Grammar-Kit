/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.grammar.generator;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.BnfAttr;
import org.intellij.grammar.psi.BnfRule;
import org.intellij.grammar.psi.BnfRules;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.grammar.generator.ParserGeneratorUtil.isRegexpToken;
import static org.intellij.grammar.psi.BnfAst.computeTokens;
import static org.intellij.grammar.psi.BnfAst.getEffectiveType;
import static org.intellij.grammar.psi.BnfAttributes.*;
import static org.intellij.grammar.psi.BnfTypes.BNF_REFERENCE_OR_TOKEN;
import static org.intellij.grammar.psi.BnfTypes.BNF_STRING;

/**
 * @author gregsh
 */
class RuleMethodsHelper {
  private final RuleGraphHelper myGraphHelper;
  private final ExpressionHelper myExpressionHelper;
  private final Map<String, String> mySimpleTokens;
  private final GenOptions G;

  private final Map<BnfRule, MethodSet> myMethods;

  RuleMethodsHelper(@NotNull RuleGraphHelper ruleGraphHelper,
                    @NotNull ExpressionHelper expressionHelper,
                    @NotNull Map<String, String> simpleTokens,
                    @NotNull GenOptions genOptions) {
    myGraphHelper = ruleGraphHelper;
    myExpressionHelper = expressionHelper;
    mySimpleTokens = Collections.unmodifiableMap(simpleTokens);
    G = genOptions;

    myMethods = new LinkedHashMap<>();
  }

  public void buildMaps(@NotNull Collection<BnfRule> sortedPsiRules) {
    Map<String, String> tokensReversed = computeTokens(myGraphHelper.getFile()).asMap();
    for (BnfRule rule : sortedPsiRules) {
      calcMethods(rule, tokensReversed);
    }
    for (BnfRule r0 : myGraphHelper.getRuleExtendsMap().keySet()) {
      MethodSet s0 = myMethods.get(r0);
      if (s0 == null) continue;
      Map<String, RuleMethodInfo> p0 = s0.basicByName();
      for (BnfRule r : myGraphHelper.getRuleExtendsMap().get(r0)) {
        if (r0 == r) continue;
        MethodSet s = myMethods.get(r);
        if (s == null) continue;
        Map<String, RuleMethodInfo> p = s.basicByName();
        for (String name : p.keySet()) {
          RuleMethodInfo m0 = p0.get(name);
          if (m0 == null) continue;
          RuleMethodInfo m = p.get(name);
          if (m0.cardinality != m.cardinality) continue;
          // suppress super method duplication
          RuleMethodInfo suppressed = m.withName("");
          p.put(name, suppressed);
          List<RuleMethodInfo> ordered = s.ordered();
          ordered.set(ordered.indexOf(m), suppressed);
        }
      }
    }
  }

  public @NotNull Collection<RuleMethodInfo> getFor(@NotNull BnfRule rule) {
    return myMethods.get(rule).ordered();
  }

  public @Nullable RuleMethodsHelper.RuleMethodInfo getMethodInfo(@NotNull BnfRule rule, String name) {
    return myMethods.get(rule).basicByName().get(name);
  }

  public @Nullable Collection<String> getMethodNames(@NotNull BnfRule rule) {
    return myMethods.get(rule).basicByName().keySet();
  }

  private void calcMethods(@NotNull BnfRule rule, @NotNull Map<String, String> tokensReversed) {
    List<RuleMethodInfo> result = new ArrayList<>();

    Map<PsiElement, RuleGraphHelper.Cardinality> cardMap = myGraphHelper.getFor(rule);

    for (PsiElement element : cardMap.keySet()) {
      RuleGraphHelper.Cardinality c = myExpressionHelper.fixCardinality(rule, element, cardMap.get(element));
      String pathName = getRuleOrTokenNameForPsi(element, c);
      if (pathName == null) continue;
      if (element instanceof BnfRule resultType) {
        if (!BnfRules.isPrivate(rule)) {
          result.add(new RuleMethodInfo(MethodType.RULE, pathName, pathName, pathName, resultType, c));
        }
      }
      else {
        result.add(new RuleMethodInfo(MethodType.TOKEN, pathName, pathName, pathName, null, c));
      }
    }
    Collections.sort(result);

    BnfAttr attr = findAttribute(rule, KnownAttribute.GENERATE_TOKEN_ACCESSORS);
    boolean generateTokens = attr == null ? G.generateTokenAccessors :
                             Boolean.TRUE.equals(getAttributeValue(attr.getExpression()));
    boolean generateTokensSet = attr != null || G.generateTokenAccessorsSet;
    Map<String, RuleMethodInfo> basicMethods = new LinkedHashMap<>();

    for (ListIterator<RuleMethodInfo> it = result.listIterator(); it.hasNext(); ) {
      RuleMethodInfo ruleMethodInfo = it.next();
      basicMethods.put(ruleMethodInfo.name, ruleMethodInfo);
      if (ruleMethodInfo.type == MethodType.TOKEN) {
        boolean registered = tokensReversed.containsKey(ruleMethodInfo.name);
        String pattern = tokensReversed.get(ruleMethodInfo.name);
        // only regexp and lowercase tokens accessors are generated by default
        if (!(generateTokens || !generateTokensSet && registered && (pattern == null || isRegexpToken(pattern)))) {
          // disable token
          RuleMethodInfo disabled = ruleMethodInfo.withName("");
          it.set(disabled);
          basicMethods.put(ruleMethodInfo.originalName, disabled);
        }
      }
    }

    KnownAttribute.ListValue methods = getAttribute(rule, KnownAttribute.METHODS);
    for (Pair<String, String> pair : methods) {
      if (StringUtil.isEmpty(pair.first)) continue;
      RuleMethodInfo ruleMethodInfo = basicMethods.get(pair.first);
      if (ruleMethodInfo != null) {
        // suppress or user method override
        RuleMethodInfo disabled = ruleMethodInfo.withName("");
        basicMethods.put(pair.first, disabled);
        result.set(result.indexOf(ruleMethodInfo), disabled);
      }
      if (StringUtil.isNotEmpty(pair.second)) {
        RuleMethodInfo basicInfo = basicMethods.get(pair.second);
        if (basicInfo != null && (basicInfo.name.equals(pair.second) || basicInfo.name.isEmpty())) {
          // simple rename, fix order anyway
          RuleMethodInfo renamed = basicInfo.withName(pair.first);
          basicMethods.put(pair.second, renamed);
          result.remove(basicInfo);
          result.add(renamed);
        }
        else {
          result.add(new RuleMethodInfo(MethodType.USER, pair.first, pair.first, pair.second, null, null));
        }
      }
      else if (ruleMethodInfo == null) {
        result.add(new RuleMethodInfo(MethodType.MIXIN, pair.first, pair.first, null, null, null));
      }
    }
    myMethods.put(rule, new MethodSet(basicMethods, result));
  }

  private @Nullable String getRuleOrTokenNameForPsi(@NotNull PsiElement tree, @NotNull RuleGraphHelper.Cardinality type) {
    String result;

    if (!(tree instanceof BnfRule asRule)) {
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
      result = asRule.getName();
      if (StringUtil.isEmpty(CommonRendererUtils.getElementType(asRule, G.generateElementCase))) return null;
    }
    return result;
  }

  /**
   * Per-rule PSI accessor set
   *
   * @param basicByName maps the original accessor name to its {@link RuleMethodInfo} for the rule's RULE/TOKEN children only
   *                    (used for super-method deduplication and path-based lookups)
   * @param ordered is the full emission-ordered list including USER and MIXIN methods.
   */
  private record MethodSet(
    @NotNull Map<String, RuleMethodInfo> basicByName,
    @NotNull List<RuleMethodInfo> ordered
  ) { }

  public enum MethodType {RULE, TOKEN, USER, MIXIN}

  public record RuleMethodInfo(
    @NotNull MethodType type,
    @NotNull String name,
    @NotNull String originalName,
    @Nullable String path,
    @Nullable BnfRule rule,
    @Nullable RuleGraphHelper.Cardinality cardinality
  ) implements Comparable<RuleMethodInfo> {

    @NotNull RuleMethodsHelper.RuleMethodInfo withName(@NotNull String newName) {
      return new RuleMethodInfo(type, newName, originalName, path, rule, cardinality);
    }

    @Override
    public int compareTo(@NotNull RuleMethodsHelper.RuleMethodInfo o) {
      if (type != o.type) return type.compareTo(o.type);
      return name.compareTo(o.name);
    }

    public @NotNull String generateGetterName() {
      boolean many = cardinality.many();

      boolean renamed = !Objects.equals(name, originalName);
      String getterNameBody = CommonRendererUtils.getGetterName(name);
      return getterNameBody + (many && !renamed ? "List" : "");
    }

    @Override
    public String toString() {
      return "RuleMethodInfo{" +
             "type=" + type +
             ", name='" + name + '\'' +
             ", path='" + path + '\'' +
             ", rule=" + rule +
             ", cardinality=" + cardinality +
             '}';
    }
  }
}
