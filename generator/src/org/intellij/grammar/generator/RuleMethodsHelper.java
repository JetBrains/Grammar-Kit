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

  public RuleMethodsHelper(RuleGraphHelper ruleGraphHelper,
                           ExpressionHelper expressionHelper,
                           Map<String, String> simpleTokens,
                           GenOptions genOptions) {
    myGraphHelper = ruleGraphHelper;
    myExpressionHelper = expressionHelper;
    mySimpleTokens = Collections.unmodifiableMap(simpleTokens);
    G = genOptions;

    myMethods = new LinkedHashMap<>();
  }

  public void buildMaps(Collection<BnfRule> sortedPsiRules) {
    Map<String, String> tokensReversed = computeTokens(myGraphHelper.getFile()).asMap();
    for (BnfRule rule : sortedPsiRules) {
      calcMethods(rule, tokensReversed);
    }
    for (BnfRule r0 : myGraphHelper.getRuleExtendsMap().keySet()) {
      MethodSet s0 = myMethods.get(r0);
      if (s0 == null) continue;
      Map<String, MethodInfo> p0 = s0.basicByName();
      for (BnfRule r : myGraphHelper.getRuleExtendsMap().get(r0)) {
        if (r0 == r) continue;
        MethodSet s = myMethods.get(r);
        if (s == null) continue;
        Map<String, MethodInfo> p = s.basicByName();
        for (String name : p.keySet()) {
          MethodInfo m0 = p0.get(name);
          if (m0 == null) continue;
          MethodInfo m = p.get(name);
          if (m0.cardinality != m.cardinality) continue;
          // suppress super method duplication
          MethodInfo suppressed = m.withName("");
          p.put(name, suppressed);
          List<MethodInfo> ordered = s.ordered();
          ordered.set(ordered.indexOf(m), suppressed);
        }
      }
    }
  }

  public @NotNull Collection<MethodInfo> getFor(@NotNull BnfRule rule) {
    return myMethods.get(rule).ordered();
  }

  public @Nullable MethodInfo getMethodInfo(@NotNull BnfRule rule, String name) {
    return myMethods.get(rule).basicByName().get(name);
  }

  public @Nullable Collection<String> getMethodNames(@NotNull BnfRule rule) {
    return myMethods.get(rule).basicByName().keySet();
  }

  private void calcMethods(@NotNull BnfRule rule, @NotNull Map<String, String> tokensReversed) {
    List<MethodInfo> result = new ArrayList<>();

    Map<PsiElement, RuleGraphHelper.Cardinality> cardMap = myGraphHelper.getFor(rule);

    for (PsiElement element : cardMap.keySet()) {
      RuleGraphHelper.Cardinality c = myExpressionHelper.fixCardinality(rule, element, cardMap.get(element));
      String pathName = getRuleOrTokenNameForPsi(element, c);
      if (pathName == null) continue;
      if (element instanceof BnfRule resultType) {
        if (!BnfRules.isPrivate(rule)) {
          result.add(new MethodInfo(MethodType.RULE, pathName, pathName, pathName, resultType, c));
        }
      }
      else {
        result.add(new MethodInfo(MethodType.TOKEN, pathName, pathName, pathName, null, c));
      }
    }
    Collections.sort(result);

    BnfAttr attr = findAttribute(rule, KnownAttribute.GENERATE_TOKEN_ACCESSORS);
    boolean generateTokens = attr == null ? G.generateTokenAccessors :
                             Boolean.TRUE.equals(getAttributeValue(attr.getExpression()));
    boolean generateTokensSet = attr != null || G.generateTokenAccessorsSet;
    Map<String, MethodInfo> basicMethods = new LinkedHashMap<>();

    for (ListIterator<MethodInfo> it = result.listIterator(); it.hasNext(); ) {
      MethodInfo methodInfo = it.next();
      basicMethods.put(methodInfo.name, methodInfo);
      if (methodInfo.type == MethodType.TOKEN) {
        boolean registered = tokensReversed.containsKey(methodInfo.name);
        String pattern = tokensReversed.get(methodInfo.name);
        // only regexp and lowercase tokens accessors are generated by default
        if (!(generateTokens || !generateTokensSet && registered && (pattern == null || isRegexpToken(pattern)))) {
          // disable token
          MethodInfo disabled = methodInfo.withName("");
          it.set(disabled);
          basicMethods.put(methodInfo.originalName, disabled);
        }
      }
    }

    KnownAttribute.ListValue methods = getAttribute(rule, KnownAttribute.METHODS);
    for (Pair<String, String> pair : methods) {
      if (StringUtil.isEmpty(pair.first)) continue;
      MethodInfo methodInfo = basicMethods.get(pair.first);
      if (methodInfo != null) {
        // suppress or user method override
        MethodInfo disabled = methodInfo.withName("");
        basicMethods.put(pair.first, disabled);
        result.set(result.indexOf(methodInfo), disabled);
      }
      if (StringUtil.isNotEmpty(pair.second)) {
        MethodInfo basicInfo = basicMethods.get(pair.second);
        if (basicInfo != null && (basicInfo.name.equals(pair.second) || basicInfo.name.isEmpty())) {
          // simple rename, fix order anyway
          MethodInfo renamed = basicInfo.withName(pair.first);
          basicMethods.put(pair.second, renamed);
          result.remove(basicInfo);
          result.add(renamed);
        }
        else {
          result.add(new MethodInfo(MethodType.USER, pair.first, pair.first, pair.second, null, null));
        }
      }
      else if (methodInfo == null) {
        result.add(new MethodInfo(MethodType.MIXIN, pair.first, pair.first, null, null, null));
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
   * @param basicByName maps the original accessor name to its {@link MethodInfo} for the rule's RULE/TOKEN children only
   *                    (used for super-method deduplication and path-based lookups)
   * @param ordered is the full emission-ordered list including USER and MIXIN methods.
   */
  private record MethodSet(
    @NotNull Map<String, MethodInfo> basicByName,
    @NotNull List<MethodInfo> ordered
  ) { }

  public enum MethodType {RULE, TOKEN, USER, MIXIN}

  public record MethodInfo(
    @NotNull MethodType type,
    @NotNull String name,
    @NotNull String originalName,
    @Nullable String path,
    @Nullable BnfRule rule,
    @Nullable RuleGraphHelper.Cardinality cardinality
  ) implements Comparable<MethodInfo> {

    @NotNull MethodInfo withName(@NotNull String newName) {
      return new MethodInfo(type, newName, originalName, path, rule, cardinality);
    }

    @Override
    public int compareTo(@NotNull MethodInfo o) {
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
