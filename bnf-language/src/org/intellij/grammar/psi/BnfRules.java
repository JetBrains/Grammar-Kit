/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
