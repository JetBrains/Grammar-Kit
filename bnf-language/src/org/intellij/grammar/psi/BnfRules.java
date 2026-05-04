/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.JBIterable;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.generator.CommonRendererUtils;
import org.intellij.grammar.generator.NameFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BnfRules {

  private BnfRules() { }

  public static boolean isPrivate(@Nullable BnfRule node) {
    return hasModifier(node, "private");
  }

  public static boolean isExternal(@Nullable BnfRule node) {
    return hasModifier(node, "external");
  }

  public static boolean isMeta(@Nullable BnfRule node) {
    return hasModifier(node, "meta");
  }

  public static boolean isLeft(@Nullable BnfRule node) {
    return hasModifier(node, "left");
  }

  public static boolean isInner(@Nullable BnfRule node) {
    return hasModifier(node, "inner");
  }

  public static boolean isFake(@Nullable BnfRule node) {
    return hasModifier(node, "fake");
  }

  public static boolean isUpper(@Nullable BnfRule node) {
    return hasModifier(node, "upper");
  }

  public static @Nullable PsiElement firstNotTrivial(@NotNull BnfRule rule) {
    for (PsiElement tree = rule.getExpression(); tree != null; tree = PsiTreeUtil.getChildOfType(tree, BnfExpression.class)) {
      if (BnfAst.getTrivialNodeChild(tree) == null) {
        return tree;
      }
    }
    return null;
  }

  public static @Nullable BnfRule of(@Nullable BnfExpression expr) {
    return PsiTreeUtil.getParentOfType(expr, BnfRule.class);
  }

  private static boolean hasModifier(@Nullable BnfRule rule, @NotNull String s) {
    if (rule == null) return false;
    for (BnfModifier modifier : rule.getModifierList()) {
      if (s.equals(modifier.getText())) return true;
    }
    return false;
  }

  public static @NotNull BnfRule getSynonymTargetOrSelf(@NotNull BnfRule rule) {
    String attr = BnfAttributes.getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
    if (attr != null) {
      BnfRule realRule = ((BnfFile)rule.getContainingFile()).getRule(attr);
      if (realRule != null && shouldGeneratePsi(realRule, false)) return realRule;
    }
    return rule;
  }

  public static boolean shouldGeneratePsi(@NotNull BnfRule rule, boolean psiClasses) {
    BnfFile containingFile = (BnfFile)rule.getContainingFile();
    BnfRule grammarRoot = containingFile.getRules().get(0);
    if (grammarRoot == rule) return false;
    if (isPrivate(rule) || isExternal(rule)) return false;
    String attr = BnfAttributes.getAttribute(rule, KnownAttribute.ELEMENT_TYPE);
    if (!psiClasses) return !"".equals(attr);
    BnfRule thatRule = containingFile.getRule(attr);
    return thatRule == null || thatRule == grammarRoot || isPrivate(thatRule) || isExternal(thatRule);
  }

  public static @NotNull JBIterable<BnfRule> getSuperRules(@NotNull BnfFile file, @Nullable BnfRule rule) {
    JBIterable<Object> result = JBIterable.generate(rule, new JBIterable.SFun<Object, Object>() {
      Set<BnfRule> visited;

      @Override
      public Object fun(Object o) {
        if (o == ObjectUtils.NULL) return null;
        BnfRule cur = (BnfRule)o;
        if (visited == null) visited = new HashSet<>();
        if (!visited.add(cur)) return ObjectUtils.NULL;
        BnfRule next = getSynonymTargetOrSelf(cur);
        if (next != cur) return next;
        if (cur != rule) return null; // do not search for elementType any further
        String attr = BnfAttributes.getAttribute(cur, KnownAttribute.EXTENDS);
        //noinspection StringEquality
        BnfRule ext = attr != KnownAttribute.EXTENDS.getDefaultValue() ? file.getRule(attr) : null;
        return ext == null && attr != null ? null : ext;
      }
    }).map(o -> o == ObjectUtils.NULL ? null : o);
    return (JBIterable<BnfRule>)(JBIterable<?>)result;
  }

  public static @Nullable BnfRule getEffectiveSuperRule(@NotNull BnfFile file, @Nullable BnfRule rule) {
    return getSuperRules(file, rule).last();
  }

  public static @NotNull List<String> getSuperInterfaceNames(@NotNull BnfFile file, @NotNull BnfRule rule, @NotNull NameFormat format) {
    List<String> strings = new ArrayList<>();
    BnfRule topSuper = getEffectiveSuperRule(file, rule);
    List<String> topRuleImplements;
    String topRuleClass;
    if (topSuper != null && topSuper != rule) {
      topRuleImplements = BnfAttributes.getAttribute(topSuper, KnownAttribute.IMPLEMENTS).asStrings();
      topRuleClass = BnfAttributes.getAttribute(topSuper, KnownAttribute.PSI_PACKAGE) + "." + CommonRendererUtils.getRulePsiClassName(topSuper, format);
      if (!StringUtil.isEmpty(topRuleClass)) strings.add(topRuleClass);
    }
    else {
      topRuleImplements = Collections.emptyList();
      topRuleClass = null;
    }
    List<String> rootImplements = BnfAttributes.getRootAttribute(file, KnownAttribute.IMPLEMENTS).asStrings();
    List<String> ruleImplements = BnfAttributes.getAttribute(rule, KnownAttribute.IMPLEMENTS).asStrings();
    for (String className : ruleImplements) {
      if (className == null) continue;
      BnfRule superIntfRule = file.getRule(className);
      if (superIntfRule != null) {
        strings.add(BnfAttributes.getAttribute(superIntfRule, KnownAttribute.PSI_PACKAGE) + "." + CommonRendererUtils.getRulePsiClassName(superIntfRule, format));
      }
      else if (!topRuleImplements.contains(className) &&
               (topRuleClass == null || !rootImplements.contains(className))) {
        if (strings.size() == 1 && topSuper == null) {
          strings.add(0, className);
        }
        else {
          strings.add(className);
        }
      }
    }
    return strings;
  }

  public static @NotNull Set<String> getRuleClasses(@NotNull BnfRule rule) {
    Set<String> result = new LinkedHashSet<>();
    BnfFile file = (BnfFile)rule.getContainingFile();
    BnfRule topSuper = getEffectiveSuperRule(file, rule);
    String superClassName = topSuper == null ? BnfAttributes.getRootAttribute(file, KnownAttribute.EXTENDS) :
                            topSuper == rule ? BnfAttributes.getAttribute(rule, KnownAttribute.EXTENDS) :
                            BnfAttributes.getAttribute(topSuper, KnownAttribute.PSI_PACKAGE) + "." +
                            CommonRendererUtils.getRulePsiClassName(topSuper, NameFormat.forPsiClass(file));
    String implSuper = StringUtil.notNullize(BnfAttributes.getAttribute(rule, KnownAttribute.MIXIN), superClassName);
    Couple<String> names = CommonRendererUtils.getQualifiedRuleClassName(rule);
    result.add(names.first);
    result.add(names.second);
    result.add(superClassName);
    result.add(implSuper);
    result.addAll(getSuperInterfaceNames(file, rule, NameFormat.forPsiClass(file)));
    return result;
  }
}
