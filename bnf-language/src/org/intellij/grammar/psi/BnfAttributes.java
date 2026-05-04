/*
 * Copyright 2011-2025 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.grammar.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.grammar.KnownAttribute;
import org.intellij.grammar.psi.impl.GrammarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BnfAttributes {

  private BnfAttributes() {
  }

  public static <T> T getRootAttribute(@NotNull PsiElement node, @NotNull KnownAttribute<T> attribute) {
    return getRootAttribute(node, attribute, null);
  }

  public static <T> T getRootAttribute(@NotNull PsiElement node, @NotNull KnownAttribute<T> attribute, @Nullable String match) {
    return ((BnfFile)node.getContainingFile()).findAttributeValue(null, attribute, match);
  }

  public static <T> T getAttribute(@NotNull BnfRule rule, @NotNull KnownAttribute<T> attribute) {
    return getAttribute(rule, attribute, null);
  }

  public static <T> T getAttribute(@NotNull BnfRule rule, @NotNull KnownAttribute<T> attribute, @Nullable String match) {
    return ((BnfFile)rule.getContainingFile()).findAttributeValue(rule, attribute, match);
  }

  public static @Nullable <T> BnfAttr findAttribute(@NotNull BnfRule rule, @NotNull KnownAttribute<T> attribute) {
    return ((BnfFile)rule.getContainingFile()).findAttribute(rule, attribute, null);
  }

  public static @Nullable Object getAttributeValue(@Nullable BnfExpression value) {
    if (value == null) return null;
    if (value instanceof BnfReferenceOrToken) {
      return getTokenValue((BnfReferenceOrToken)value);
    }
    else if (value instanceof BnfLiteralExpression) {
      return getLiteralValue((BnfLiteralExpression)value);
    }
    else if (value instanceof BnfValueList) {
      KnownAttribute.ListValue pairs = new KnownAttribute.ListValue();
      for (BnfListEntry o : ((BnfValueList)value).getListEntryList()) {
        PsiElement id = o.getId();
        pairs.add(Pair.create(id == null ? null : id.getText(), getLiteralValue(o.getLiteralExpression())));
      }
      return pairs;
    }
    return null;
  }

  public static @Nullable String getLiteralValue(@Nullable BnfStringLiteralExpression child) {
    return getLiteralValue((BnfLiteralExpression)child);
  }

  public static @Nullable <T> T getLiteralValue(@Nullable BnfLiteralExpression child) {
    if (child == null) return null;
    PsiElement literal = PsiTreeUtil.getDeepestFirst(child);
    String text = child.getText();
    IElementType elementType = literal.getNode().getElementType();
    if (elementType == BnfTypes.BNF_NUMBER) return (T)Integer.valueOf(text);
    if (elementType == BnfTypes.BNF_STRING) {
      String unquoted = GrammarUtil.unquote(text);
      String result = text.charAt(0) == '"' ? unquoted.replaceAll("\\\\([\"'])", "$1") : unquoted;
      return (T)result;
    }
    return null;
  }

  private static Object getTokenValue(BnfReferenceOrToken child) {
    String text = child.getText();
    if (text.equals("true")) return true;
    if (text.equals("false")) return false;
    return GrammarUtil.getIdText(child);
  }

  public static boolean useSyntaxApi(@NotNull BnfRule rule) {
    return "syntax".equals(getAttribute(rule, KnownAttribute.GENERATE).asMap().get("parser-api"));
  }

  public static boolean useSyntaxApi(@NotNull BnfFile file) {
    return "syntax".equals(getRootAttribute(file, KnownAttribute.GENERATE).asMap().get("parser-api"));
  }
}
